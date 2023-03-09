package de.hhu.ausgabenverwaltung.api.models;

import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import de.hhu.ausgabenverwaltung.domain.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.stream.Collectors;

public record AuslagenModel(@NotNull String grund, @NotNull String glaeubiger, @Positive int cent,
                            @NotNull @NotEmpty Set<String> schuldner) {

    public static AuslagenModel fromAusgabe(Ausgabe ausgabe) {
        return new AuslagenModel(ausgabe.name(),
            ausgabe.bezahltVon().githubHandle(),
            ausgabe.betrag().multiply(BigDecimal.TEN).intValue(),
            ausgabe.beteiligte().stream().map(User::githubHandle).collect(Collectors.toSet()));
    }

    public Ausgabe toAusgabe() {
        return new Ausgabe(grund,
            "",
            new BigDecimal(cent).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP),
            new User(glaeubiger),
            schuldner.stream().map(User::new).collect(Collectors.toList()));
    }

}
