package de.hhu.ausgabenverwaltung.service;

import de.hhu.ausgabenverwaltung.database.GruppenRepositoryImp;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GruppenRepositoryImpTests {
    @Test
    @DisplayName("Alle Gruppen eines Users werden zurückgegeben")
    void gruppenVonUserTest() {
        //Arrange
        User userA = new User("githubname1");
        User userB = new User("githubname2");
        Gruppe gruppe1 = Gruppe.gruppeErstellen("gruppe1",userA);
        Gruppe gruppe2 = Gruppe.gruppeErstellen("gruppe2",userB);
        GruppenRepositoryImp gruppenRepositoryImp = new GruppenRepositoryImp(List.of(gruppe1, gruppe2));
        // Act
        List<Gruppe> gruppenVonA = gruppenRepositoryImp.vonUser(userA);
        // Assert
        assertThat(gruppenVonA).containsExactly(gruppe1);
    }

    @Test
    @DisplayName("Filtern nach offen und geschlossene Gruppen von User")
    void gruppenFiltern() {
        //Arrange
        User user = new User("githubname");

        Gruppe gruppe1 = Gruppe.gruppeErstellen("gruppe1",user);
        Gruppe gruppe2 = Gruppe.gruppeErstellen("gruppe2", user);
        gruppe2.schliessen();
        GruppenRepositoryImp gruppenRepositoryImp = new GruppenRepositoryImp(List.of(gruppe1, gruppe2));
        // Act
        List<Gruppe> offeneGruppen = gruppenRepositoryImp.offenVonUser(user);
        List<Gruppe> geschlosseneGruppen = gruppenRepositoryImp.geschlossenVonUser(user);
        // Assert
        assertThat(offeneGruppen).containsExactly(gruppe1);
        assertThat(geschlosseneGruppen).containsExactly(gruppe2);
    }
}
