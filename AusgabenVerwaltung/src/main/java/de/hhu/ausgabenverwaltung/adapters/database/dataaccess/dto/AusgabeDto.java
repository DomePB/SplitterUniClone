package de.hhu.ausgabenverwaltung.adapters.database.dataaccess.dto;

import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import de.hhu.ausgabenverwaltung.domain.User;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("AUSGABE")
public record AusgabeDto(String name,
                         String beschreibung,
                         BigDecimal betrag,
                         String bezahltVon,
                         @MappedCollection(idColumn = "ausgabeId", keyColumn = "githubHandle")
                         List<String> beteiligte) {

  public static AusgabeDto fromAusgabe(Ausgabe ausgabe) {
    return new AusgabeDto(ausgabe.name(),
        ausgabe.beschreibung(),
        ausgabe.betrag(),
        ausgabe.bezahltVon().githubHandle(),
        ausgabe.beteiligte().stream().map(User::githubHandle).collect(Collectors.toList()));
  }

  public Ausgabe toAusgabe() {
    return new Ausgabe(name,
        beschreibung,
        betrag,
        new User(bezahltVon),
        beteiligte.stream().map(User::new).collect(Collectors.toList()));
  }

}
