package de.hhu.ausgabenverwaltung.api.models;

import de.hhu.ausgabenverwaltung.domain.Transaktion;
import java.math.BigDecimal;

public record AusgleichModel(String von, String an, int cents) {

    AusgleichModel fromTransaktion(Transaktion transaktion) {
        return new AusgleichModel(transaktion.sender().githubHandle(),
                transaktion.empfaenger().githubHandle(),
                transaktion.betrag().multiply(BigDecimal.TEN).intValue());
    }

}
