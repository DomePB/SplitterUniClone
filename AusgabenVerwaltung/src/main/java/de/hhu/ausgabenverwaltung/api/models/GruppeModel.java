package de.hhu.ausgabenverwaltung.api.models;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record GruppeModel(String gruppe, String name, List<String> personen, boolean geschlossen,
                          List<AuslagenModel> ausgaben) {

    public static GruppeModel fromGruppe(Gruppe gruppe) {
        return new GruppeModel(gruppe.getId().toString(),
            gruppe.getName(),
            gruppe.getMitglieder().stream().map(User::githubHandle)
                .collect(Collectors.toList()),
            !gruppe.istOffen(),
            gruppe.getAusgaben().stream().map(AuslagenModel::fromAusgabe)
                .collect(Collectors.toList()));
    }

    public Gruppe toGruppe() {
        return new Gruppe(name,
            ausgaben == null ? new ArrayList<>() :
                ausgaben.stream().map(AuslagenModel::toAusgabe).collect(Collectors.toList()),
            personen == null ? new ArrayList<>() :
                personen.stream().map(User::new).collect(Collectors.toList()),
            new HashSet<>(),
            !geschlossen,
            gruppe == null ? UUID.randomUUID() : UUID.fromString(gruppe));
    }

}
