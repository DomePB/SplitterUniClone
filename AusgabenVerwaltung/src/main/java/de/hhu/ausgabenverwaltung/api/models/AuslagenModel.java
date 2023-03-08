package de.hhu.ausgabenverwaltung.api.models;

import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import de.hhu.ausgabenverwaltung.domain.User;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public record AuslagenModel(String grund, String glaeubiger, int cent, List<String> schuldner) {

    AuslagenModel fromAusgabe(Ausgabe ausgabe) {
        return new AuslagenModel(ausgabe.name(),
                ausgabe.bezahltVon().githubHandle(),
                ausgabe.betrag().multiply(BigDecimal.TEN).intValue(),
                ausgabe.beteiligte().stream().map(User::githubHandle).collect(Collectors.toList()));
    }

}
