package de.hhu.ausgabenverwaltung.api.models;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record GruppeModel(String gruppe, @NotNull String name,
                          @NotNull @NotEmpty Set<String> personen, Boolean geschlossen,
                          List<AuslagenModel> ausgaben) {

    public static GruppeModel fromGruppe(Gruppe gruppe) {
        return new GruppeModel(gruppe.getId().toString(),
            gruppe.getName(),
            gruppe.getMitglieder().stream().map(User::githubHandle)
                .collect(Collectors.toSet()),
            !gruppe.istOffen(),
            gruppe.getAusgaben().stream().map(AuslagenModel::fromAusgabe)
                .collect(Collectors.toList()));
    }

}
