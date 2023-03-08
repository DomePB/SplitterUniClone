package de.hhu.ausgabenverwaltung.api.models;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import java.util.List;
import java.util.stream.Collectors;

public record GruppeModel(String name, List<String> personen) {

    GruppeModel fromGruppe(Gruppe gruppe) {
        return new GruppeModel(gruppe.getName(),
                gruppe.getMitglieder().stream().map(User::githubHandle)
                        .collect(Collectors.toList()));
    }

}
