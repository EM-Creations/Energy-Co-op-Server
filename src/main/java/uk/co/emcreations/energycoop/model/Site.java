package uk.co.emcreations.energycoop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Site {
    GRAIG_FATHA("Graig Fatha Wind Farm", "graigfatha"),
    KIRK_HILL("Kirk Hill Wind Farm", "kirkhill"),
    DERRIL_WATER("Derril Water Solar Farm", "derrilwater");

    private final String name;
    private final String id;
}
