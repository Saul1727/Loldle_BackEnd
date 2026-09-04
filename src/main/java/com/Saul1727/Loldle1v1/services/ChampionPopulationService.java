package com.Saul1727.Loldle1v1.services;

import com.Saul1727.Loldle1v1.models.Champion;
import com.Saul1727.Loldle1v1.models.dtos.DDragonChampionData;
import com.Saul1727.Loldle1v1.models.dtos.DDragonResponseDto;
import com.Saul1727.Loldle1v1.models.dtos.UniverseChampionDto;
import com.Saul1727.Loldle1v1.models.enums.Species;
import com.Saul1727.Loldle1v1.repository.ChampionRepository;
import com.Saul1727.Loldle1v1.util.RiotDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// Rellena la tabla de campeones a partir de la API pública de Riot (DDragon) y de la
// Universe API para lo que DDragon no trae (año de salida, región, especie).
//
// Se ejecuta solo al arrancar y solo si la tabla está vacía, así que reiniciar la app
// no vuelve a golpear la API de Riot cada vez. Si algo falla a mitad, se loguea y la
// app sigue arrancando igual: esto es un job de datos, no algo crítico para servir peticiones.
//
// OJO: la Universe API no es oficial ni está documentada por Riot (es la que usan varias
// webs de la comunidad). Su endpoint de índice (champions/index.json) devuelve 403 porque
// necesita listar el bucket de S3 y ese permiso está deshabilitado, pero la página de cada
// campeón por separado (champions/{slug}/index.json) sigue funcionando, así que pedimos
// cada campeón individualmente en vez de depender del índice.
@Service
public class ChampionPopulationService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ChampionPopulationService.class);

    private final RestClient restClient;
    private final ChampionRepository championRepository;
    private final boolean autoPopulateEnabled;
    private final String ddragonBaseUrl;
    private final String universeBaseUrl;

    public ChampionPopulationService(RestClient riotRestClient,
                                      ChampionRepository championRepository,
                                      @Value("${app.champions.auto-populate:true}") boolean autoPopulateEnabled,
                                      @Value("${riot.ddragon.base-url:https://ddragon.leagueoflegends.com}") String ddragonBaseUrl,
                                      @Value("${riot.universe.base-url:https://universe-meeps.leagueoflegends.com/v1/en_us}") String universeBaseUrl) {
        this.restClient = riotRestClient;
        this.championRepository = championRepository;
        this.autoPopulateEnabled = autoPopulateEnabled;
        this.ddragonBaseUrl = ddragonBaseUrl;
        this.universeBaseUrl = universeBaseUrl;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!autoPopulateEnabled) {
            return;
        }
        if (championRepository.count() > 0) {
            log.info("Ya hay campeones en la base de datos, se omite la población automática");
            return;
        }
        try {
            populate();
        } catch (Exception e) {
            log.error("No se pudo poblar la tabla de campeones desde la API de Riot", e);
        }
    }

    public void populate() {
        log.info("Poblando campeones desde la API de Riot...");

        // slug de Universe (id de DDragon en minúsculas) -> nombre del campeón, para
        // poder pedir cada página de Universe y saber a qué Champion aplicar la respuesta.
        Map<String, String> championNameBySlug = new HashMap<>();
        Map<String, Champion> champions = fetchFromDDragon(championNameBySlug);
        enrichWithUniverseData(champions, championNameBySlug);

        championRepository.saveAll(champions.values());
        log.info("Población completada: {} campeones guardados", champions.size());
    }

    private Map<String, Champion> fetchFromDDragon(Map<String, String> championNameBySlug) {
        String[] versions = restClient.get()
                .uri(ddragonBaseUrl + "/api/versions.json")
                .retrieve()
                .body(String[].class);

        String latestVersion = (versions != null && versions.length > 0) ? versions[0] : "15.1.1";

        DDragonResponseDto response = restClient.get()
                .uri(ddragonBaseUrl + "/cdn/{version}/data/en_US/champion.json", latestVersion)
                .retrieve()
                .body(DDragonResponseDto.class);

        Map<String, Champion> champions = new HashMap<>();
        if (response == null || response.data == null) {
            return champions;
        }

        for (DDragonChampionData data : response.data.values()) {
            Champion champion = new Champion();
            champion.setName(data.name);
            champion.setResource(RiotDataMapper.mapResource(data.partype));
            if (data.stats != null) {
                champion.setRangeType(RiotDataMapper.mapRangeType(data.stats.attackrange));
            }
            champions.put(data.name, champion);

            if (data.id != null && !data.id.isBlank()) {
                championNameBySlug.put(data.id.toLowerCase(Locale.ROOT), data.name);
            }
        }
        return champions;
    }

    private void enrichWithUniverseData(Map<String, Champion> champions, Map<String, String> championNameBySlug) {
        for (Map.Entry<String, String> entry : championNameBySlug.entrySet()) {
            String slug = entry.getKey();
            String championName = entry.getValue();
            try {
                UniverseChampionDto detail = restClient.get()
                        .uri(universeBaseUrl + "/champions/{slug}/index.json", slug)
                        .retrieve()
                        .body(UniverseChampionDto.class);
                applyUniverseData(champions.get(championName), detail);
            } catch (Exception e) {
                log.debug("No se pudo obtener info de Universe para {}", slug, e);
            }
            sleepBriefly();
        }
    }

    private void applyUniverseData(Champion champion, UniverseChampionDto detail) {
        if (champion == null || detail == null || detail.champion == null) {
            return;
        }
        UniverseChampionDto.ChampionDetails info = detail.champion;

        if (info.releaseDate != null) {
            champion.setYear(extractYear(info.releaseDate));
        }
        champion.setRegion(RiotDataMapper.mapRegion(info.associatedFactionSlug));

        // Riot solo lista una raza cuando el campeón NO es humano; una lista vacía
        // significa "humano" por convención, no "desconocido".
        if (info.races == null || info.races.isEmpty()) {
            champion.setSpecies(Species.HUMAN);
        } else {
            champion.setSpecies(RiotDataMapper.mapSpecies(info.races.get(0).name));
        }
    }

    private int extractYear(String releaseDate) {
        try {
            return Instant.parse(releaseDate).atZone(ZoneOffset.UTC).getYear();
        } catch (Exception e) {
            return 0;
        }
    }

    private void sleepBriefly() {
        try {
            Thread.sleep(80);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
