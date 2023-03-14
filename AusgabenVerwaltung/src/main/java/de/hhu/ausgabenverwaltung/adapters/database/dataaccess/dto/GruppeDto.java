package de.hhu.ausgabenverwaltung.adapters.database.dataaccess.dto;

import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Table("GRUPPE")
public record GruppeDto(@Id UUID uuid,
                        String name,
                        boolean offen,
                        List<Ausgabe> ausgaben,
                        Set<String> mitglieder
) {
    public Gruppe toGruppe() {
        Set<User> mitgliederGruppe = mitglieder.stream().map(User::new).collect(Collectors.toSet());
        return new Gruppe(name, ausgaben, mitgliederGruppe, offen, uuid);
    }

    public static GruppeDto fromGruppe(Gruppe gruppe) {
        Set<String> mitgliederGruppe = gruppe.getMitglieder().stream().map(User::githubHandle).collect(Collectors.toSet());
        return new GruppeDto(gruppe.getId(), gruppe.getName(), gruppe.istOffen(), gruppe.getAusgaben(), mitgliederGruppe);
    }
}
