package uk.co.emcreations.energycoop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Site {
    GRAIG_FATHA("Graig Fatha Wind Farm", "graigfatha", "gf-wattage"),
    KIRK_HILL("Kirk Hill Wind Farm", "kirkhill", "kh-wattage"),
    DERRIL_WATER("Derril Water Solar Farm", "derrilwater", "dw-wattage");

    private final String name;
    private final String id;
    private final String ownershipKey;
}
