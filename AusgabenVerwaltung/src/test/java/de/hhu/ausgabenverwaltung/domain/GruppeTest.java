package de.hhu.ausgabenverwaltung.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class GruppeTest {

    @Test
    @DisplayName("Person zur Gruppe hinzufuegen")
    void personHinzufuegen() {
        //Arrange
        User user = new User("githubname", "Jens");
        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<>(), new HashSet<>(), true);


        //Act
        gruppe.addMitglieder(user);

        //Assert
        assertThat(gruppe.getMitglieder().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Person aus Gruppe entfernen")
    void personEntfernen() {
        //Arrange
        User user = new User("githubname", "Jens");
        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<>(List.of(user)), new HashSet<>(), true);
        //Act
        gruppe.deleteMitglieder(user);
        //Assert
        assertThat(gruppe.getMitglieder().isEmpty()).isTrue();

    }

    @Test
    @DisplayName("User und Ausgabe sind schon drin")
    void addMitglieder() {
        //Arrange
        User user = new User("githubname", "Jens");
        Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe", new BigDecimal("10"), user, List.of(user));
        List<Ausgabe> ausgabe = List.of(ausgabe1);
        Gruppe gruppe = new Gruppe("gruppeName", ausgabe, new ArrayList<>(List.of(user)), new HashSet<>(), true);

        //Act
        gruppe.addMitglieder(user);

        //Assert
        assertThat(gruppe.getMitglieder().size()).isOne();

    }

    @Test
    @DisplayName("Sender = Empfaenger")
    void isTransaktionValid1() {
        //Arrange
        User user1 = new User("githubname", "Jens");
        Transaktion transaktion = new Transaktion(user1, user1, new BigDecimal("100.00"));
        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<>(List.of(user1)), new HashSet<>(), true);
        //Act
        boolean isValid = gruppe.isTransaktionValid(transaktion);
        //Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("sender Und Empänger Hatten Schon Eine Transaktion In Der Selben Gruppe")
    void isTransaktionValid2() {

        //Arrange
        User user1 = new User("githubname1", "Jens");
        User user2 = new User("githubname2", "Bob");
        Transaktion transaktion1 = new Transaktion(user1, user2, new BigDecimal("100"));
        Transaktion transaktion2 = new Transaktion(user1, user2, new BigDecimal("50"));

        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<>(List.of(user1, user2)),
                new HashSet<>(Set.of(transaktion1)), true);
        //Act
        boolean isValid = gruppe.isTransaktionValid(transaktion2);

        //Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Personen einer Transaktion müssen in einer Gruppe sein")
    void isTransaktionValid3() {
        //Arrange
        User user1 = new User("githubname1", "Jens");
        User user2 = new User("githubname2", "Bob");
        Transaktion transaktion1 = new Transaktion(user1, user2, new BigDecimal("100"));

        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<>(List.of(user1)),
                new HashSet<>(), true);

        //Act
        boolean isValid = gruppe.isTransaktionValid(transaktion1);

        //Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Gültige Transaktion wird hinzugefügt")
    void transaktionHinzufuegenTest() {
        // Arrange
        User user1 = new User("githubname1", "Jens");
        User user2 = new User("githubname2", "Bob");
        Transaktion transaktion1 = new Transaktion(user1, user2, new BigDecimal("100"));

        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<>(List.of(user1, user2)),
                new HashSet<>(), true);

        // Act
        gruppe.transaktionHinzufuegen(transaktion1);

        // Assert
        assertThat(gruppe.getTransaktionen().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Ausgabe wird hinzugefügt")
    void ausgabeHinzufuegen() {
        // Arrange
        User user1 = new User("githubname1", "Jens");
        User user2 = new User("githubname2", "Bob");
        Ausgabe ausgabe = new Ausgabe("", "", new BigDecimal("10"), user1, new ArrayList<>(List.of(user1, user2)));

        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<>(List.of(user1, user2)),
                new HashSet<>(), true);

        // Act
        gruppe.ausgabeHinzufuegen(ausgabe);

        // Assert
        assertThat(gruppe.getAusgaben().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("die summer von Ausgabe soll korrekt sein")
    void ausgabensummantKorrekt() {
        //Arrange
        User user1 = new User("githubname1", "Jens");
        Ausgabe ausgabe1 = new Ausgabe("", "", new BigDecimal("10"), user1, new ArrayList<>(List.of(user1)));
        Ausgabe ausgabe2 = new Ausgabe("", "", new BigDecimal("20"), user1, new ArrayList<>(List.of(user1)));

        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(List.of(ausgabe1, ausgabe2)), new ArrayList<>(List.of(user1)),
                new HashSet<>(), true);
        //Act
        BigDecimal summe = gruppe.summeVonUser(user1);
        //Assert
        assertThat(summe).isEqualTo(new BigDecimal(30));
    }

    @Test
    @DisplayName("Die Summe von noch zu bezahlenden Sachen einer Person")
    void ausgabenBeiteiligtKorrekt() {
        //Arrange
        User user1 = new User("githubname1", "Jens");
        User user2 = new User("githubname2", "Bob");
        Ausgabe ausgabe1 = new Ausgabe("", "", new BigDecimal("10"), user1, new ArrayList<>(List.of(user1, user2)));
        Ausgabe ausgabe2 = new Ausgabe("", "", new BigDecimal("20"), user1, new ArrayList<>(List.of(user1)));

        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(List.of(ausgabe1, ausgabe2)), new ArrayList<>(List.of(user1, user2)),
                new HashSet<>(), true);
        HashMap<User, BigDecimal> gruppeMussBezahlenVon;
        //Act
        gruppeMussBezahlenVon = gruppe.mussBezahlenVonUser(user1);
        //Assert
        assertThat(gruppeMussBezahlenVon).containsEntry(user2, new BigDecimal(5));
    }

    @Test
    @DisplayName("User kann nicht an sich selber Geld schulden")
    void keinEigenerSchuldner() {
        //Arrange
        User user1 = new User("githubname1", "Jens");
        User user2 = new User("githubname2", "Bob");
        Ausgabe ausgabe1 = new Ausgabe("", "", new BigDecimal("10"), user1, new ArrayList<>(List.of(user1, user2)));
        Ausgabe ausgabe2 = new Ausgabe("", "", new BigDecimal("20"), user1, new ArrayList<>(List.of(user1)));

        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(List.of(ausgabe1, ausgabe2)), new ArrayList<>(List.of(user1, user2)),
                new HashSet<>(), true);
        HashMap<User, BigDecimal> gruppeMussBezahlenVon;
        //Act
        gruppeMussBezahlenVon = gruppe.mussBezahlenVonUser(user1);
        //Assert
        assertThat(gruppeMussBezahlenVon).doesNotContainKey(user1);
    }

    @Test
    @DisplayName("keine Ausgaben hinzufuegen bei geschlossener Gruppe")
    void keineAusgabe() {
        //Arrange
        User user1 = new User("githubname1", "Jens");
        User user2 = new User("githubname2", "Bob");
        Ausgabe ausgabe1 = new Ausgabe("", "", new BigDecimal("10"), user1, new ArrayList<>(List.of(user1, user2)));


        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<>(List.of(user1, user2)),
                new HashSet<>(), false);
        //Act
        gruppe.ausgabeHinzufuegen(ausgabe1);
        //Assert
        assertThat(gruppe.getAusgaben().size()).isEqualTo(0);

    }

    @Test
    @DisplayName("keine Transaktionen hinzufuegen bei geschlossener Gruppe")
    void keineTransaktionen() {
        //Arrange
        User user1 = new User("githubname1", "Jens");
        User user2 = new User("githubname2", "Bob");
        Transaktion t = new Transaktion(user1, user2, new BigDecimal(10));

        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<>(List.of(user1, user2)),
                new HashSet<>(), false);
        //Act
        gruppe.transaktionHinzufuegen(t);
        //Assert
        assertThat(gruppe.getTransaktionen().size()).isEqualTo(0);

    }


}
