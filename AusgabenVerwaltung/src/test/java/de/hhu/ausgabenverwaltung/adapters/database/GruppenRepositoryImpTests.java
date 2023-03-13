package de.hhu.ausgabenverwaltung.adapters.database;

import de.hhu.ausgabenverwaltung.adapters.database.implementation.GruppenRepositoryImp;
import static org.assertj.core.api.Assertions.assertThat;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import java.util.Set;


public class GruppenRepositoryImpTests {
    @Test
    @DisplayName("Alle Gruppen eines Users werden zurückgegeben")
    void gruppenVonUserTest() {
        //Arrange
        User userA = new User("githubname1");
        User userB = new User("githubname2");
        Gruppe gruppe1 = Gruppe.createGruppe("gruppe1", Set.of(userA));
        Gruppe gruppe2 = Gruppe.createGruppe("gruppe2", Set.of(userB));
        GruppenRepositoryImp gruppenRepositoryImp =
            new GruppenRepositoryImp();
        gruppenRepositoryImp.save(gruppe1);
        gruppenRepositoryImp.save(gruppe2);


        // Act
        List<Gruppe> gruppenVonA = gruppenRepositoryImp.getGruppenvonUser(userA);

        // Assert
        assertThat(gruppenVonA).containsExactly(gruppe1);
    }

    @Test
    @DisplayName("Filtern nach offen und geschlossene Gruppen von User")
    void gruppenFiltern() {
        //Arrange
        User user = new User("githubname");

        Gruppe gruppe1 = Gruppe.createGruppe("gruppe1", Set.of(user));
        Gruppe gruppe2 = Gruppe.createGruppe("gruppe2", Set.of(user));
        gruppe2.schliessen();
        GruppenRepositoryImp gruppenRepositoryImp =
            new GruppenRepositoryImp();
        gruppenRepositoryImp.save(gruppe1);
        gruppenRepositoryImp.save(gruppe2);

        // Act
        List<Gruppe> offeneGruppen = gruppenRepositoryImp.getOffeneGruppenVonUser(user);
        List<Gruppe> geschlosseneGruppen = gruppenRepositoryImp.getGeschlosseneGruppenVonUser(user);

        // Assert
        assertThat(offeneGruppen).containsExactly(gruppe1);
        assertThat(geschlosseneGruppen).containsExactly(gruppe2);
    }

//    @Test kommt alles in service am ende
//    @DisplayName("User Transaktionen werden richtig angezeigt")
//    void transaktionenFilternProUser() {
//
//        //Arrange
//        User userA = new User("githubname1");
//        User userB = new User("githubname2");
//        User userC = new User("githubname3");
//
//
//        Transaktion transaktion1 = new Transaktion(userA, userB, new BigDecimal("40"));
//        Transaktion transaktion2 = new Transaktion(userB, userA, new BigDecimal("10"));
//        Transaktion transaktion3 = new Transaktion(userC, userB, new BigDecimal("20"));
//
//
//        Gruppe gruppe1 = Gruppe.gruppeErstellen("gruppe1",userA);
//        gruppe1.addMitglieder(userB);
//        gruppe1.setTransaktionen(Set.of(transaktion1));
//        Gruppe gruppe2 = Gruppe.gruppeErstellen("gruppe2",
//                userA);
//        gruppe2.setMitglieder(List.of(userA, userB, userC));
//        gruppe2.setTransaktionen(Set.of(transaktion2, transaktion3));
//
//        GruppenRepository repo = new GruppenRepositoryImp(List.of(gruppe1,gruppe2));
//
//        //Act
//        var userTransaktionen = repo.getBeteiligteTransaktionen(userA);
//
//        //Assert
//        assertThat(userTransaktionen.size()).isEqualTo(2);
//        assertThat(userTransaktionen).containsEntry(gruppe1, Set.of(transaktion1));
//        assertThat(userTransaktionen).containsEntry(gruppe2, Set.of(transaktion2));
//    }
}

