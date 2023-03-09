package de.hhu.ausgabenverwaltung.api.models;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record GruppeModel(String name, List<String> personen) {

    public static GruppeModel fromGruppe(Gruppe gruppe) {
        return new GruppeModel(gruppe.getName(),
            gruppe.getMitglieder().stream().map(User::githubHandle)
                .collect(Collectors.toList()));
    }

    public Gruppe toGruppe() {
        return new Gruppe(name,
            new ArrayList<>(),
            personen.stream().map(User::new).collect(Collectors.toList()),
            new HashSet<>(),
            true,
            UUID.fromString(name));
    }

}
