package com.Saul1727.Loldle1v1.services;

import com.Saul1727.Loldle1v1.models.Champion;
import com.Saul1727.Loldle1v1.models.dtos.DDragonChampionData;
import com.Saul1727.Loldle1v1.models.dtos.DDragonResponseDto;
import com.Saul1727.Loldle1v1.models.dtos.UniverseChampionDto;
import com.Saul1727.Loldle1v1.models.dtos.UniverseIndexDto;
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
import java.util.Map;

// Rellena la tabla de campeones a partir de la API pública de Riot (DDragon) y de la
// Universe API para lo que DDragon no trae (año de salida, región, especie).
//
// Se ejecuta solo al arrancar y solo si la tabla está vacía, así que reiniciar la app
// no vuelve a golpear la API de Riot cada vez. Si algo falla a mitad, se loguea y la
// app sigue arrancando igual: esto es un job de datos, no algo crítico para servir peticiones.
//
// OJO: la URL de la Universe API no es oficial ni está documentada por Riot (es la que
// usan varias webs de la comunidad), así que si cambia o responde distinto a lo esperado
// se puede sobreescribir con la propiedad riot.universe.base-url sin tocar código.
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

        Map<String, Champion> champions = fetchFromDDragon();
        enrichWithUniverseData(champions);

        championRepository.saveAll(champions.values());
        log.info("Población completada: {} campeones guardados", champions.size());
    }

    private Map<String, Champion> fetchFromDDragon() {
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
        }
        return champions;
    }

    private void enrichWithUniverseData(Map<String, Champion> champions) {
        UniverseIndexDto index;
        try {
            index = restClient.get()
                    .uri(universeBaseUrl + "/champions/index.json")
                    .retrieve()
                    .body(UniverseIndexDto.class);
        } catch (Exception e) {
            log.warn("No se pudo consultar el índice de la Universe API, los campeones se guardan sin región/especie/año", e);
            return;
        }

        if (index == null || index.champions == null) {
            return;
        }

        for (UniverseIndexDto.Entry entry : index.champions) {
            try {
                UniverseChampionDto detail = restClient.get()
                        .uri(universeBaseUrl + "/champions/{slug}/index.json", entry.id)
                        .retrieve()
                        .body(UniverseChampionDto.class);
                applyUniverseData(champions, detail);
            } catch (Exception e) {
                log.debug("No se pudo obtener info de Universe para {}", entry.id, e);
            }

            sleepBriefly();
        }
    }

    private void applyUniverseData(Map<String, Champion> champions, UniverseChampionDto detail) {
        if (detail == null || detail.name == null) {
            return;
        }
        Champion champion = champions.get(detail.name);
        if (champion == null) {
            return;
        }

        if (detail.champion != null) {
            if (detail.champion.releaseDate != null) {
                champion.setYear(extractYear(detail.champion.releaseDate));
            }
            champion.setRegion(RiotDataMapper.mapRegion(detail.champion.associatedFactionSlug));
        }

        if (detail.races != null && !detail.races.isEmpty()) {
            champion.setSpecies(RiotDataMapper.mapSpecies(detail.races.get(0).name));
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
