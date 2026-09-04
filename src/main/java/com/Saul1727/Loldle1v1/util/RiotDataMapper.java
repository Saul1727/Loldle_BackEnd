package com.Saul1727.Loldle1v1.util;

import com.Saul1727.Loldle1v1.models.enums.Range_Type;
import com.Saul1727.Loldle1v1.models.enums.Region;
import com.Saul1727.Loldle1v1.models.enums.Resource;
import com.Saul1727.Loldle1v1.models.enums.Species;

// Traduce los strings que vienen de las APIs de Riot a nuestros enums. Riot no siempre
// usa los mismos nombres que definimos aquí, así que primero se intenta un match directo
// y luego un par de alias conocidos; si no hay forma de mapearlo, se deja sin asignar
// en vez de inventarse un valor.
public final class RiotDataMapper {

    private RiotDataMapper() {
    }

    public static Resource mapResource(String partype) {
        if (partype == null || partype.isBlank()) {
            return null;
        }
        return switch (normalize(partype)) {
            case "NONE" -> Resource.MANALESS;
            case "FURY" -> Resource.RAGE;
            case "HEALTH" -> Resource.HEALTH_COSTS;
            default -> tryValueOf(Resource.class, partype);
        };
    }

    public static Region mapRegion(String factionSlug) {
        if (factionSlug == null || factionSlug.isBlank()) {
            return Region.UNKNOWN;
        }
        Region mapped = switch (normalize(factionSlug)) {
            case "THE_VOID" -> Region.VOID;
            // La Universe API usa el slug "mount-targon", nuestro enum lo llama TARGON.
            case "MOUNT_TARGON" -> Region.TARGON;
            // "unaffiliated" (Jax y otros) no tiene un valor propio en el enum.
            case "UNAFFILIATED" -> Region.UNKNOWN;
            default -> tryValueOf(Region.class, factionSlug);
        };
        return mapped != null ? mapped : Region.UNKNOWN;
    }

    public static Species mapSpecies(String raceName) {
        if (raceName == null || raceName.isBlank()) {
            return Species.UNKNOWN;
        }
        Species mapped = switch (normalize(raceName)) {
            case "VASTAYA" -> Species.VASTAYAN;
            case "VOID", "VOIDBORN" -> Species.VOID_BEING;
            default -> tryValueOf(Species.class, raceName);
        };
        return mapped != null ? mapped : Species.UNKNOWN;
    }

    public static Range_Type mapRangeType(double attackRange) {
        return attackRange >= 300 ? Range_Type.RANGE : Range_Type.MELEE;
    }

    private static String normalize(String value) {
        return value.trim().toUpperCase().replace('-', '_').replace(' ', '_');
    }

    private static <E extends Enum<E>> E tryValueOf(Class<E> enumType, String rawValue) {
        try {
            return Enum.valueOf(enumType, normalize(rawValue));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
