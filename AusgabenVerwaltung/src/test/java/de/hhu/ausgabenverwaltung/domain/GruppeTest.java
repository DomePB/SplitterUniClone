package de.hhu.ausgabenverwaltung.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class GruppeTest {

    @Test
    @DisplayName("Person zur Gruppe hinzufuegen")
    void personHinzufuegen() {
        //Arrange
        User user = new User("githubname");
      Gruppe gruppe =
          new Gruppe("gruppeName", new ArrayList<>(), new HashSet<>(), true, UUID.randomUUID());


        //Act
        gruppe.addMitglieder(user);

        //Assert
        assertThat(gruppe.getMitglieder().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Person aus Gruppe entfernen")
    void personEntfernen() {
        //Arrange
      User user = new User("githubname");
      Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new HashSet<>(Set.of(user)),
          true, UUID.randomUUID());
        //Act
        gruppe.deleteMitglieder(user);
        //Assert
        assertThat(gruppe.getMitglieder().isEmpty()).isTrue();

    }

    @Test
    @DisplayName("User und Ausgabe sind schon drin")
    void addMitglieder() {
        //Arrange
        User user = new User("githubname");
        Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe", new BigDecimal("10"), user, Set.of(user));
      List<Ausgabe> ausgabe = List.of(ausgabe1);
      Gruppe gruppe =
          new Gruppe("gruppeName", ausgabe, new HashSet<>(Set.of(user)), true, UUID.randomUUID());

        //Act
        gruppe.addMitglieder(user);

        //Assert
        assertThat(gruppe.getMitglieder().size()).isOne();

    }

    @Test
    @DisplayName("Alle Schulden einer Gruppe korrekt berechnet")
    void schuldenEinerGruppe() {
        //Arrange
        User user1 = new User("githubname1");
        User user2 = new User("githubname2");
        Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Essen", new BigDecimal(10), user1,
                new HashSet<>(Set.of(user1, user2)));
      Ausgabe ausgabe2 = new Ausgabe("ausgabe1", "Kino", new BigDecimal(30), user2,
          new HashSet<>(Set.of(user1, user2)));
      Gruppe gruppe = Gruppe.createGruppe("gruppe", Set.of(user1, user2));
        gruppe.addAusgabe(ausgabe1);
        gruppe.addAusgabe(ausgabe2);

        //Act
        var listVonSchuldigen = gruppe.alleSchuldenBerechnen();

        //Assert
        assertThat(listVonSchuldigen).containsEntry(user1, new HashMap<>(Map.of(user2, new BigDecimal("5.00"))));
        assertThat(listVonSchuldigen).containsEntry(user2, new HashMap<>(Map.of(user1, new BigDecimal("15.00"))));
    }

    @Test
    @DisplayName("Ausgabe wird hinzugefügt")
    void ausgabeHinzufuegen() {
        // Arrange
        User user1 = new User("githubname1");
        User user2 = new User("githubname2");
        Ausgabe ausgabe = new Ausgabe("", "", new BigDecimal("10"), user1, new HashSet<>(Set.of(user1, user2)));

      Gruppe gruppe =
          new Gruppe("gruppeName", new ArrayList<>(), new HashSet<>(Set.of(user1, user2)),
              true, UUID.randomUUID());

        // Act
        gruppe.addAusgabe(ausgabe);

        // Assert
        assertThat(gruppe.getAusgaben().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("die summer von Ausgabe soll korrekt sein")
    void ausgabensummantKorrekt() {
        //Arrange
        User user1 = new User("githubname1");
        Ausgabe ausgabe1 = new Ausgabe("", "", new BigDecimal("10"), user1, new HashSet<>(Set.of(user1)));
        Ausgabe ausgabe2 = new Ausgabe("", "", new BigDecimal("20"), user1, new HashSet<>(Set.of(user1)));

      Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(List.of(ausgabe1, ausgabe2)),
          new HashSet<>(Set.of(user1)),
          true, UUID.randomUUID());
        //Act
        BigDecimal summe = gruppe.summeVonUser(user1);
        //Assert
        assertThat(summe).isEqualTo(new BigDecimal(30));
    }

    @Test
    @DisplayName("Die Summe von noch zu bezahlenden Sachen einer Person")
    void ausgabenBeiteiligtKorrekt() {
        //Arrange
        User user1 = new User("githubname1");
        User user2 = new User("githubname2");
        Ausgabe ausgabe1 = new Ausgabe("", "", new BigDecimal("10"), user1, new HashSet<>(Set.of(user1, user2)));
        Ausgabe ausgabe2 = new Ausgabe("", "", new BigDecimal("20"), user1, new HashSet<>(Set.of(user1)));

      Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(List.of(ausgabe1, ausgabe2)),
          new HashSet<>(Set.of(user1, user2)),
          true, UUID.randomUUID());
      HashMap<User, BigDecimal> gruppeMussBezahlenVon;
        //Act
        gruppeMussBezahlenVon = gruppe.mussBezahlenVonUser(user1);
        //Assert
        assertThat(gruppeMussBezahlenVon).containsEntry(user2, new BigDecimal("5.00"));
    }

    @Test
    @DisplayName("User kann nicht an sich selber Geld schulden")
    void keinEigenerSchuldner() {
        //Arrange
        User user1 = new User("githubname1");
        User user2 = new User("githubname2");
        Ausgabe ausgabe1 = new Ausgabe("", "", new BigDecimal("10"), user1, new HashSet<>(Set.of(user1, user2)));
        Ausgabe ausgabe2 = new Ausgabe("", "", new BigDecimal("20"), user1, new HashSet<>(Set.of(user1)));

      Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(List.of(ausgabe1, ausgabe2)),
          new HashSet<>(Set.of(user1, user2)),
          true, UUID.randomUUID());
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
        User user1 = new User("githubname1");
        User user2 = new User("githubname2");
        Ausgabe ausgabe1 = new Ausgabe("", "", new BigDecimal("10"), user1, new HashSet<>(Set.of(user1, user2)));


      Gruppe gruppe =
          new Gruppe("gruppeName", new ArrayList<>(), new HashSet<>(Set.of(user1, user2)),
              false, UUID.randomUUID());
        //Act
        gruppe.addAusgabe(ausgabe1);
        //Assert
        assertThat(gruppe.getAusgaben().size()).isEqualTo(0);

    }

    @Test
    @DisplayName("Muss bezahlenVonUser bei 100/3")
    void berechneSalden_2() {
        User userA = new User("githubname1");
        User userB = new User("githubname2");
        User userC = new User("githubname3");
        Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal(100), userA, new HashSet<>(Set.of(userA,userB,userC)));

      Gruppe gruppe = Gruppe.createGruppe("test", Set.of(userA, userB, userC));
        gruppe.addAusgabe(ausgabe1);

        //Act
        HashMap<User, BigDecimal> mussBezahlen = gruppe.mussBezahlenVonUser(userA);
        //Assert
        assertThat(mussBezahlen).containsEntry(userB,new BigDecimal("33.33"));
    }

    @Test
    @DisplayName("Salden werden korrekt berechnet")
    void berechneSalden() {
        //Arrange
        User userA = new User("githubname1");
        User userB = new User("githubname2");
        User userC = new User("githubname3");
        User userD = new User("githubname4");

        Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal(8), userA, new HashSet<>(Set.of(userB)));
        Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Ausgabe2", new BigDecimal(5), userA, new HashSet<>(Set.of(userC)));
        Ausgabe ausgabe4 = new Ausgabe("ausgabe4", "Ausgabe2", new BigDecimal(3), userB, new HashSet<>(Set.of(userA)));
        Ausgabe ausgabe5 = new Ausgabe("ausgabe5", "Ausgabe2", new BigDecimal(7), userB, new HashSet<>(Set.of(userC)));
        Ausgabe ausgabe6 = new Ausgabe("ausgabe6", "Ausgabe2", new BigDecimal(11), userB, new HashSet<>(Set.of(userD)));

        Ausgabe ausgabe7 = new Ausgabe("ausgabe7", "Ausgabe2", new BigDecimal(10), userC, new HashSet<>(Set.of(userA)));
        Ausgabe ausgabe8 = new Ausgabe("ausgabe8", "Ausgabe2", new BigDecimal(1), userC, new HashSet<>(Set.of(userB)));
        Ausgabe ausgabe9 = new Ausgabe("ausgabe9", "Ausgabe2", new BigDecimal(6), userC, new HashSet<>(Set.of(userD)));

        Ausgabe ausgabe10 = new Ausgabe("ausgabe10", "Ausgabe2", new BigDecimal(2), userD, new HashSet<>(Set.of(userA)));
        Ausgabe ausgabe11 = new Ausgabe("ausgabe11", "Ausgabe2", new BigDecimal(5), userD, new HashSet<>(Set.of(userB)));
        Ausgabe ausgabe12 = new Ausgabe("ausgabe12", "Ausgabe2", new BigDecimal(4), userD, new HashSet<>(Set.of(userC)));

      Gruppe gruppe = Gruppe.createGruppe("gruppe", Set.of(userA, userB, userC, userD));
        gruppe.addAusgabe(ausgabe1);
        gruppe.addAusgabe(ausgabe2);
        gruppe.addAusgabe(ausgabe4);
        gruppe.addAusgabe(ausgabe5);
        gruppe.addAusgabe(ausgabe6);
        gruppe.addAusgabe(ausgabe7);
        gruppe.addAusgabe(ausgabe8);
        gruppe.addAusgabe(ausgabe9);
        gruppe.addAusgabe(ausgabe10);
        gruppe.addAusgabe(ausgabe11);
        gruppe.addAusgabe(ausgabe12);


        //Act
        var schulden= gruppe.alleSchuldenBerechnen();
        var alleSalden = gruppe.berechneSalden(schulden);

        // Assert
        assertThat(alleSalden).containsEntry(userA, new BigDecimal("2.00"));
        assertThat(alleSalden).containsEntry(userB, new BigDecimal("-7.00"));
        assertThat(alleSalden).containsEntry(userC, new BigDecimal("-1.00"));
        assertThat(alleSalden).containsEntry(userD, new BigDecimal("6.00"));
    }

    @Test
    @DisplayName("Bei ausgleichenden Zahlungen sollen diese vorramgig berechnet werden, um Transaktionen minimal zu halten.")
    void berechneTransaktionen() {
        //Arrange
        User userA = new User("githubname1");
      User userB = new User("githubname2");
      Gruppe gruppe = Gruppe.createGruppe("gruppe", Set.of(userA, userB));

        //Act
        var alleTransaktionen = gruppe.berechneTransaktionen(new HashMap<>(Map.of(userA, new BigDecimal(5), userB, new BigDecimal(-5))));
        //Assert
        assertThat(alleTransaktionen).isEqualTo(new HashSet<>(Set.of(new Transaktion(userA, userB, new BigDecimal(5)))));
    }

    @Test
    @DisplayName("UserA bezahlt mehr als B bekommt")
    void berechneTransaktionen2() {
        //Arrange
        User userA = new User("githubname1");
      User userB = new User("githubname2");
      Gruppe gruppe = Gruppe.createGruppe("gruppe", Set.of(userA, userB));

        //Act
        var alleTransaktionen = gruppe.berechneTransaktionen(new HashMap<>(Map.of(userA, new BigDecimal(5), userB, new BigDecimal(-3))));

        //Assert
        assertThat(alleTransaktionen).isEqualTo(new HashSet<>(Set.of(new Transaktion(userA, userB, new BigDecimal(3)))));
    }

    @Test
    @DisplayName("UserB bezahlt mehr als A bekommt")
    void berechneTransaktionen3() {
        //Arrange
        User userA = new User("githubname1");
      User userB = new User("githubname2");
      Gruppe gruppe = Gruppe.createGruppe("gruppe", Set.of(userA, userB));

        //Act
        var alleTransaktionen = gruppe.berechneTransaktionen(new HashMap<>(Map.of(userA, new BigDecimal(-5), userB, new BigDecimal(6))));

        //Assert
        assertThat(alleTransaktionen).isEqualTo(new HashSet<>(Set.of(new Transaktion(userB, userA, new BigDecimal(5)))));
    }

    @Test
    @DisplayName("UserB bezahlt weniger als A bekommt")
    void berechneTransaktionen4() {
      //Arrange
      User userA = new User("githubname1");
      User userB = new User("githubname2");
      Gruppe gruppe = Gruppe.createGruppe("gruppe", Set.of(userA, userB));

      //Act
      var alleTransaktionen = gruppe.berechneTransaktionen(
          new HashMap<>(Map.of(userA, new BigDecimal(-5), userB, new BigDecimal(3))));

      //Assert
      assertThat(alleTransaktionen).isEqualTo(
          new HashSet<>(Set.of(new Transaktion(userB, userA, new BigDecimal(3)))));
    }

    @Test
    @DisplayName("UserA bezahlt weniger als B bekommt")
    void berechneTransaktionen5() {
      //Arrange
      User userA = new User("A");
      User userB = new User("B");
      Gruppe gruppe = Gruppe.createGruppe("gruppe", Set.of(userA, userB));

      //Act
      var alleTransaktionen = gruppe.berechneTransaktionen(
          new HashMap<>(Map.of(userA, new BigDecimal(3), userB, new BigDecimal(-5))));

      //Assert
      assertThat(alleTransaktionen).isEqualTo(
          new HashSet<>(Set.of(new Transaktion(userA, userB, new BigDecimal(3)))));
    }



}
