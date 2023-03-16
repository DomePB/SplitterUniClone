package de.hhu.ausgabenverwaltung.adapters.database.dataaccess.dto;

import de.hhu.ausgabenverwaltung.domain.User;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

public record Ausgabe(@Id UUID id,
                      String name,
                      String beschreibung,
                      BigDecimal betrag,
                      String bezahltVon,
                      @MappedCollection(idColumn = "ausgabeid")
                      Set<Beteiligt> beteiligt) {

  public static Ausgabe fromAusgabe(de.hhu.ausgabenverwaltung.domain.Ausgabe ausgabe) {
    return new Ausgabe(UUID.randomUUID(), ausgabe.name(),
        ausgabe.beschreibung(),
        ausgabe.betrag(),
        ausgabe.bezahltVon().githubHandle(),
        ausgabe.beteiligte().stream().map(User::githubHandle).map(Beteiligt::new)
            .collect(Collectors.toSet()));
  }

  public de.hhu.ausgabenverwaltung.domain.Ausgabe toAusgabe() {
    return new de.hhu.ausgabenverwaltung.domain.Ausgabe(name,
        beschreibung,
        betrag,
        new User(bezahltVon),
        beteiligt.stream().map(Beteiligt::githubhandle).map(User::new).collect(Collectors.toSet()));
  }

}
