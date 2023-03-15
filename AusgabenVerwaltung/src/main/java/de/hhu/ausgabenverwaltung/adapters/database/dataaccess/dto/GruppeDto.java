package de.hhu.ausgabenverwaltung.adapters.database.dataaccess.dto;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("GRUPPE")
public record GruppeDto(@Id UUID id,
                        String name,
                        boolean offen,
                        @MappedCollection(idColumn = "GRUPPENID", keyColumn = "ID")
                        List<AusgabeDto> ausgabe,
                        @MappedCollection(idColumn = "GRUPPENID")
                        Set<Mitglied> mitglied
) {
  public static GruppeDto fromGruppe(Gruppe gruppe) {
    Set<Mitglied> mitgliederGruppe =
        gruppe.getMitglieder().stream().map(User::githubHandle).map(Mitglied::new).collect(Collectors.toSet());
    return new GruppeDto(gruppe.getId(),
        gruppe.getName(),
        gruppe.istOffen(),
        gruppe.getAusgaben().stream().map(AusgabeDto::fromAusgabe).collect(Collectors.toList()),
        mitgliederGruppe);
  }

  public Gruppe toGruppe() {
    Set<User> mitgliederGruppe = mitglied.stream().map(Mitglied::githubhandle).map(User::new).collect(Collectors.toSet());
    return new Gruppe(name,
        ausgabe.stream().map(AusgabeDto::toAusgabe).collect(Collectors.toList()),
        mitgliederGruppe,
        offen,
       id);
  }
}
