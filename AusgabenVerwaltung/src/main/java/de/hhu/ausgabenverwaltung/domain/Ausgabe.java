package de.hhu.ausgabenverwaltung.domain;

import java.math.BigDecimal;
import java.util.List;

public record Ausgabe(String name, String beschreibung, BigDecimal betrag, User bezahltVon, List<User> beteiligte) {

}
