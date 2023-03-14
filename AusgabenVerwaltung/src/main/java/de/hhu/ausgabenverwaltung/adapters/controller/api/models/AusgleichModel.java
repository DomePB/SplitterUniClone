package de.hhu.ausgabenverwaltung.adapters.controller.api.models;

import de.hhu.ausgabenverwaltung.domain.Transaktion;
import de.hhu.ausgabenverwaltung.domain.User;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record AusgleichModel(String von, String an, int cents) {

  public static AusgleichModel fromTransaktion(Transaktion transaktion) {
    return new AusgleichModel(transaktion.sender().githubHandle(),
        transaktion.empfaenger().githubHandle(),
        transaktion.betrag().multiply(new BigDecimal(100)).intValue());
  }

  public Transaktion toTransaktion() {
    return new Transaktion(new User(von),
        new User(an),
        new BigDecimal(cents).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
  }

}
