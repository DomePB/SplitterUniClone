package de.hhu.ausgabenverwaltung.adapters.controller.web.forms;

import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import de.hhu.ausgabenverwaltung.domain.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record AusgabeForm(
    @NotEmpty String ausgabeName,
    String ausgabeBeschreibung,
    @NotNull @Positive BigDecimal ausgabeBetrag,
    @NotEmpty String bezahltVon,
    @NotEmpty List<String> beteiligte) {
  public static AusgabeForm defaultAusgabe() {
    return new AusgabeForm("Ausgabe",
        "",
        BigDecimal.ONE,
        "test",
        new ArrayList<>(List.of("test")));
  }

  public Ausgabe toAusgabe() {
    return new Ausgabe(ausgabeName,
        ausgabeBeschreibung,
        ausgabeBetrag,
        new User(bezahltVon),
        beteiligte.stream().map(User::new).collect(Collectors.toSet()));
  }
}
