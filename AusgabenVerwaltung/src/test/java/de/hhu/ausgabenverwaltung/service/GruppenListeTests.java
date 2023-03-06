package de.hhu.ausgabenverwaltung.service;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GruppenListeTests {
    @Test
    @DisplayName("Alle Gruppen eines Users werden zurückgegeben")
    void gruppenVonUserTest() {
        //Arrange
        User userA = new User("githubname1", "Alpha");
        User userB = new User("githubname2", "Beta");
        Gruppe gruppe1 = new Gruppe("gruppe1", new ArrayList<>(), new ArrayList<>(List.of(userA)), new HashSet<>(), true);
        Gruppe gruppe2 = new Gruppe("gruppe2", new ArrayList<>(), new ArrayList<>(List.of(userB)), new HashSet<>(), true);
        GruppenListe gruppenListe = new GruppenListe(List.of(gruppe1, gruppe2));
        // Act
        List<Gruppe> gruppenVonA = gruppenListe.vonUser(userA);
        // Assert
        assertThat(gruppenVonA).containsExactly(gruppe1);
    }

    @Test
    @DisplayName("Filtern nach offen und geschlossene Gruppen von User")
    void gruppenFiltern() {
        //Arrange
        User user = new User("githubname", "user");

        Gruppe gruppe1 = new Gruppe("gruppe1", new ArrayList<>(), List.of(user), new HashSet<>(), true);
        Gruppe gruppe2 = new Gruppe("gruppe2", new ArrayList<>(), List.of(user), new HashSet<>(), false);
        GruppenListe gruppenListe = new GruppenListe(List.of(gruppe1, gruppe2));
        // Act
        List<Gruppe> offeneGruppen = gruppenListe.offenVonUser(user);
        List<Gruppe> geschlosseneGruppen = gruppenListe.geschlossenVonUser(user);
        // Assert
        assertThat(offeneGruppen).containsExactly(gruppe1);
        assertThat(geschlosseneGruppen).containsExactly(gruppe2);
    }
}
