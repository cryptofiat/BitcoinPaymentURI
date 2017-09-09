package eu.cryptoeuro.euro2paymenturi.model;

import java.util.stream.Stream;

public enum SignatureType {
    ETH;

    public static SignatureType findByName(String name) {
        return Stream.of(SignatureType.values())
                .filter(it -> it.name().equals(name))
                .findFirst()
                .orElse(null);
    }
}
