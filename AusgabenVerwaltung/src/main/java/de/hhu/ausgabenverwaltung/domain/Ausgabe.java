package de.hhu.ausgabenverwaltung.domain;

import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Set;

public record Ausgabe(String name, String beschreibung,
                      @Min(value = 0, message = "Betrag muss positiv sein") BigDecimal betrag,
                      User bezahltVon,
                     Set<User> beteiligte) {

}
