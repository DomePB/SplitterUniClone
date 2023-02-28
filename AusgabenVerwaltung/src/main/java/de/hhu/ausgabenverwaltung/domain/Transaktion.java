package de.hhu.ausgabenverwaltung.domain;

import java.math.BigDecimal;

public record Transaktion(User sender, User empfaenger, BigDecimal betrag) {

}
