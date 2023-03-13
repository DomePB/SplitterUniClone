package de.hhu.ausgabenverwaltung.adapters.controller.web.forms;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public record AusgabeForm(
    @NotEmpty String ausgabeName,
    String ausgabeBeschreibung,
    @NotNull @Positive BigDecimal ausgabeBetrag,
    @NotEmpty String bezahltVon,
    @NotEmpty List<String> beteiligte) {
  public static AusgabeForm defaultAusgabe() {
    return new AusgabeForm("",
        "",
        BigDecimal.ZERO,
        "",
        new ArrayList<>());
  }
}
