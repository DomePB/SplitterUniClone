package de.hhu.ausgabenverwaltung.adapters.database.dataaccess.dto;

import de.hhu.ausgabenverwaltung.domain.User;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("ausgabe")
public record AusgabeDto(@Id UUID id,
                         String name,
                         String beschreibung,
                         BigDecimal betrag,
                         String bezahltVon,
                         @MappedCollection(idColumn = "ausgabeid")
                         Set<BeteiligtDto> beteiligt) {

  public static AusgabeDto fromAusgabe(de.hhu.ausgabenverwaltung.domain.Ausgabe ausgabe) {
    return new AusgabeDto(UUID.randomUUID(), ausgabe.name(),
        ausgabe.beschreibung(),
        ausgabe.betrag(),
        ausgabe.bezahltVon().githubHandle(),
        ausgabe.beteiligte().stream().map(User::githubHandle).map(BeteiligtDto::new)
            .collect(Collectors.toSet()));
  }

  public de.hhu.ausgabenverwaltung.domain.Ausgabe toAusgabe() {
    return new de.hhu.ausgabenverwaltung.domain.Ausgabe(name,
        beschreibung,
        betrag,
        new User(bezahltVon),
        beteiligt.stream().map(BeteiligtDto::githubhandle).map(User::new)
            .collect(Collectors.toSet()));
  }

}
