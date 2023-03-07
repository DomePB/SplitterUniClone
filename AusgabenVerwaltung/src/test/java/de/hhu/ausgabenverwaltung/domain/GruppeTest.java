package de.hhu.ausgabenverwaltung.domain;

import org.junit.jupiter.api.Disabled;
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
        User user = new User("githubname");
        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<>(), new HashSet<>(), true,UUID.randomUUID());


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
        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<>(List.of(user)), new HashSet<>(), true,UUID.randomUUID());
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
        Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe", new BigDecimal("10"), user, List.of(user));
        List<Ausgabe> ausgabe = List.of(ausgabe1);
        Gruppe gruppe = new Gruppe("gruppeName", ausgabe, new ArrayList<>(List.of(user)), new HashSet<>(), true,UUID.randomUUID());

        //Act
        gruppe.addMitglieder(user);

        //Assert
        assertThat(gruppe.getMitglieder().size()).isOne();

    }

    @Test
    @DisplayName("Sender = Empfaenger")
    void isTransaktionValid1() {
        //Arrange
        User user1 = new User("githubname");
        Transaktion transaktion = new Transaktion(user1, user1, new BigDecimal("100.00"));
        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<>(List.of(user1)), new HashSet<>(), true,UUID.randomUUID());
        //Act
        boolean isValid = gruppe.isTransaktionValid(transaktion);
        //Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("sender Und Empänger Hatten Schon Eine Transaktion In Der Selben Gruppe")
    void isTransaktionValid2() {

        //Arrange
        User user1 = new User("githubname1");
        User user2 = new User("githubname2");
        Transaktion transaktion1 = new Transaktion(user1, user2, new BigDecimal("100"));
        Transaktion transaktion2 = new Transaktion(user1, user2, new BigDecimal("50"));

        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<>(List.of(user1, user2)),
                new HashSet<>(Set.of(transaktion1)), true,UUID.randomUUID());
        //Act
        boolean isValid = gruppe.isTransaktionValid(transaktion2);

        //Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Alle Schulden einer Gruppe korrekt berechnet")
    void schuldenEinerGruppe() {
        //Arrange
        User user1 = new User("githubname1");
        User user2 = new User("githubname2");
        Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Essen", new BigDecimal(10), user1,
                new ArrayList<>(List.of(user1, user2)));
        Ausgabe ausgabe2 = new Ausgabe("ausgabe1", "Kino", new BigDecimal(30), user2,
                new ArrayList<>(List.of(user1, user2)));
        Gruppe gruppe = Gruppe.gruppeErstellen("gruppe",user1);
        gruppe.addMitglieder(user2);
        gruppe.setAusgaben((List.of(ausgabe1, ausgabe2)));

        //Act
        var listVonSchuldigen = gruppe.alleSchuldenBerechnen();

        //Assert
        assertThat(listVonSchuldigen).containsEntry(user1, new HashMap<>(Map.of(user2, new BigDecimal(5))));
        assertThat(listVonSchuldigen).containsEntry(user2, new HashMap<>(Map.of(user1, new BigDecimal(15))));
    }

    @Test
    @DisplayName("Personen einer Transaktion müssen in einer Gruppe sein")
    void isTransaktionValid3() {
        //Arrange
        User user1 = new User("githubname1");
        User user2 = new User("githubname2");
        Transaktion transaktion1 = new Transaktion(user1, user2, new BigDecimal("100"));

        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<>(List.of(user1)),
                new HashSet<>(), true,UUID.randomUUID());

        //Act
        boolean isValid = gruppe.isTransaktionValid(transaktion1);

        //Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Gültige Transaktion wird hinzugefügt")
    void transaktionHinzufuegenTest() {
        // Arrange
        User user1 = new User("githubname1");
        User user2 = new User("githubname2");
        Transaktion transaktion1 = new Transaktion(user1, user2, new BigDecimal("100"));

        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<>(List.of(user1, user2)),
                new HashSet<>(), true,UUID.randomUUID());

        // Act
        gruppe.transaktionHinzufuegen(transaktion1);

        // Assert
        assertThat(gruppe.getTransaktionen().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Ausgabe wird hinzugefügt")
    void ausgabeHinzufuegen() {
        // Arrange
        User user1 = new User("githubname1");
        User user2 = new User("githubname2");
        Ausgabe ausgabe = new Ausgabe("", "", new BigDecimal("10"), user1, new ArrayList<>(List.of(user1, user2)));

        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<>(List.of(user1, user2)),
                new HashSet<>(), true,UUID.randomUUID());

        // Act
        gruppe.ausgabeHinzufuegen(ausgabe);

        // Assert
        assertThat(gruppe.getAusgaben().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("die summer von Ausgabe soll korrekt sein")
    void ausgabensummantKorrekt() {
        //Arrange
        User user1 = new User("githubname1");
        Ausgabe ausgabe1 = new Ausgabe("", "", new BigDecimal("10"), user1, new ArrayList<>(List.of(user1)));
        Ausgabe ausgabe2 = new Ausgabe("", "", new BigDecimal("20"), user1, new ArrayList<>(List.of(user1)));

        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(List.of(ausgabe1, ausgabe2)), new ArrayList<>(List.of(user1)),
                new HashSet<>(), true,UUID.randomUUID());
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
        Ausgabe ausgabe1 = new Ausgabe("", "", new BigDecimal("10"), user1, new ArrayList<>(List.of(user1, user2)));
        Ausgabe ausgabe2 = new Ausgabe("", "", new BigDecimal("20"), user1, new ArrayList<>(List.of(user1)));

        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(List.of(ausgabe1, ausgabe2)), new ArrayList<>(List.of(user1, user2)),
                new HashSet<>(), true,UUID.randomUUID());
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
        User user1 = new User("githubname1");
        User user2 = new User("githubname2");
        Ausgabe ausgabe1 = new Ausgabe("", "", new BigDecimal("10"), user1, new ArrayList<>(List.of(user1, user2)));
        Ausgabe ausgabe2 = new Ausgabe("", "", new BigDecimal("20"), user1, new ArrayList<>(List.of(user1)));

        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(List.of(ausgabe1, ausgabe2)), new ArrayList<>(List.of(user1, user2)),
                new HashSet<>(), true,UUID.randomUUID());
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
        Ausgabe ausgabe1 = new Ausgabe("", "", new BigDecimal("10"), user1, new ArrayList<>(List.of(user1, user2)));


        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<>(List.of(user1, user2)),
                new HashSet<>(), false,UUID.randomUUID());
        //Act
        gruppe.ausgabeHinzufuegen(ausgabe1);
        //Assert
        assertThat(gruppe.getAusgaben().size()).isEqualTo(0);

    }

    @Test
    @DisplayName("keine Transaktionen hinzufuegen bei geschlossener Gruppe")
    void keineTransaktionen() {
        //Arrange
        User user1 = new User("githubname1");
        User user2 = new User("githubname2");
        Transaktion t = new Transaktion(user1, user2, new BigDecimal(10));

        Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<>(List.of(user1, user2)),
                new HashSet<>(), false,UUID.randomUUID());
        //Act
        gruppe.transaktionHinzufuegen(t);
        //Assert
        assertThat(gruppe.getTransaktionen().size()).isEqualTo(0);

    }

    @Test
    @DisplayName("Salden werden korrekt berechnet")
    void berechneSalden() {
        //Arrange
        User userA = new User("githubname1");
        User userB = new User("githubname2");
        User userC = new User("githubname3");
        User userD = new User("githubname4");

        Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal(8), userA, new ArrayList<>(List.of(userB)));
        Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Ausgabe2", new BigDecimal(5), userA, new ArrayList<>(List.of(userC)));
        Ausgabe ausgabe4 = new Ausgabe("ausgabe4", "Ausgabe2", new BigDecimal(3), userB, new ArrayList<>(List.of(userA)));
        Ausgabe ausgabe5 = new Ausgabe("ausgabe5", "Ausgabe2", new BigDecimal(7), userB, new ArrayList<>(List.of(userC)));
        Ausgabe ausgabe6 = new Ausgabe("ausgabe6", "Ausgabe2", new BigDecimal(11), userB, new ArrayList<>(List.of(userD)));

        Ausgabe ausgabe7 = new Ausgabe("ausgabe7", "Ausgabe2", new BigDecimal(10), userC, new ArrayList<>(List.of(userA)));
        Ausgabe ausgabe8 = new Ausgabe("ausgabe8", "Ausgabe2", new BigDecimal(1), userC, new ArrayList<>(List.of(userB)));
        Ausgabe ausgabe9 = new Ausgabe("ausgabe9", "Ausgabe2", new BigDecimal(6), userC, new ArrayList<>(List.of(userD)));

        Ausgabe ausgabe10 = new Ausgabe("ausgabe10", "Ausgabe2", new BigDecimal(2), userD, new ArrayList<>(List.of(userA)));
        Ausgabe ausgabe11 = new Ausgabe("ausgabe11", "Ausgabe2", new BigDecimal(5), userD, new ArrayList<>(List.of(userB)));
        Ausgabe ausgabe12 = new Ausgabe("ausgabe12", "Ausgabe2", new BigDecimal(4), userD, new ArrayList<>(List.of(userC)));

        Gruppe gruppe =Gruppe.gruppeErstellen("gruppe",userA);
        gruppe.setAusgaben(new ArrayList<>(List.of(ausgabe1, ausgabe2, ausgabe4, ausgabe5, ausgabe6, ausgabe7, ausgabe8, ausgabe9, ausgabe10, ausgabe11, ausgabe12)));
        gruppe.setMitglieder( new ArrayList<>(List.of(userA, userB, userC, userD)));
        //Act
        var schulden= gruppe.alleSchuldenBerechnen();
        var alleSalden = gruppe.berechneSalden(schulden);

        // Assert
        assertThat(alleSalden).containsEntry(userA, new BigDecimal(2));
        assertThat(alleSalden).containsEntry(userB, new BigDecimal(-7));
        assertThat(alleSalden).containsEntry(userC, new BigDecimal(-1));
        assertThat(alleSalden).containsEntry(userD, new BigDecimal(6));
    }

    @Test
    @DisplayName("Bei ausgleichenden Zahlungen sollen diese vorramgig berechnet werden, um Transaktionen minimal zu halten.")
    void berechneTransaktionen() {
        //Arrange
        User userA = new User("githubname1");
        User userB = new User("githubname2");
        Gruppe gruppe = Gruppe.gruppeErstellen("gruppe",userA);
        gruppe.addMitglieder(userB);

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
        Gruppe gruppe =Gruppe.gruppeErstellen("gruppe",userA);
        gruppe.addMitglieder(userB);

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
        Gruppe gruppe = Gruppe.gruppeErstellen("gruppe",userA);
        gruppe.addMitglieder(userB);

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
        Gruppe gruppe = Gruppe.gruppeErstellen("gruppe", userA);
        gruppe.addMitglieder(userB);
        //Act
        var alleTransaktionen = gruppe.berechneTransaktionen(new HashMap<>(Map.of(userA, new BigDecimal(-5), userB, new BigDecimal(3))));
        //Assert
        assertThat(alleTransaktionen).isEqualTo(new HashSet<>(Set.of(new Transaktion(userB, userA, new BigDecimal(3)))));
    }

    @Test
    @DisplayName("UserA bezahlt weniger als B bekommt")
    void berechneTransaktionen5() {
        //Arrange
        User userA = new User("A");
        User userB = new User("B");
        Gruppe gruppe = Gruppe.gruppeErstellen("gruppe", userA);
        gruppe.addMitglieder(userB);
        //Act
        var alleTransaktionen = gruppe.berechneTransaktionen(new HashMap<>(Map.of(userA, new BigDecimal(3), userB, new BigDecimal(-5))));
        //Assert
        assertThat(alleTransaktionen).isEqualTo(new HashSet<>(Set.of(new Transaktion(userA, userB, new BigDecimal(3)))));
    }

    @Test
    @DisplayName("Szenario 1: Summieren von Auslagen")
    void szenario1() {
        //Arrange
        User userA = new User("A");
        User userB = new User("B");

        Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal(10), userA, new ArrayList<>(List.of(userA, userB)));
        Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Ausgabe2", new BigDecimal(20), userA, new ArrayList<>(List.of(userA, userB)));

        Gruppe gruppe = Gruppe.gruppeErstellen("gruppe",userA);
        gruppe.addMitglieder(userB);
        gruppe.setAusgaben(new ArrayList<>(List.of(ausgabe1, ausgabe2)));
        //Act
        var alleTransaktionen = gruppe.berechneTransaktionen(gruppe.berechneSalden(gruppe.alleSchuldenBerechnen()));
        //Assert
        assertThat(alleTransaktionen).isEqualTo(new HashSet<>(Set.of(new Transaktion(userB, userA, new BigDecimal(15)))));
    }

    @Test
    @DisplayName("Szenario 2: Ausgleich")
    void szenario2() {
        //Arrange
        User userA = new User("A");
        User userB = new User("B");

        Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal(10), userA, new ArrayList<>(List.of(userA, userB)));
        Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Ausgabe2", new BigDecimal(20), userB, new ArrayList<>(List.of(userA, userB)));

        Gruppe gruppe = Gruppe.gruppeErstellen("gruppe", userA);
        gruppe.addMitglieder(userB);
        gruppe.setAusgaben(new ArrayList<>(List.of(ausgabe1, ausgabe2)));
        //Act
        var alleTransaktionen = gruppe.berechneTransaktionen(gruppe.berechneSalden(gruppe.alleSchuldenBerechnen()));
        //Assert
        assertThat(alleTransaktionen).isEqualTo(new HashSet<>(Set.of(new Transaktion(userA, userB, new BigDecimal(5)))));
    }

    @Test
    @DisplayName("Szenario 3: Zahlung ohne eigene Beteiligung")
    void szenario3() {
        //Arrange
        User userA = new User("A");
        User userB = new User("B");

        Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal(10), userA, new ArrayList<>(List.of(userB)));
        Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Ausgabe2", new BigDecimal(20), userA, new ArrayList<>(List.of(userA, userB)));

        Gruppe gruppe = Gruppe.gruppeErstellen("gruppe", userA);
        gruppe.addMitglieder(userB);
        gruppe.setAusgaben(new ArrayList<>(List.of(ausgabe1, ausgabe2)));
        //Act
        var alleTransaktionen = gruppe.berechneTransaktionen(gruppe.berechneSalden(gruppe.alleSchuldenBerechnen()));
        //Assert
        assertThat(alleTransaktionen).isEqualTo(new HashSet<>(Set.of(new Transaktion(userB, userA, new BigDecimal(20)))));
    }

    @Test
    @DisplayName("Szenario 4: Ringausgleich")
    void szenario4() {
        //Arrange
        User userA = new User("A");
        User userB = new User("B");
        User userC = new User("C");

        Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal(10), userA, new ArrayList<>(List.of(userA, userB)));
        Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Ausgabe2", new BigDecimal(10), userB, new ArrayList<>(List.of(userB, userC)));
        Ausgabe ausgabe3 = new Ausgabe("ausgabe3", "Ausgabe3", new BigDecimal(10), userC, new ArrayList<>(List.of(userA, userC)));

        Gruppe gruppe = Gruppe.gruppeErstellen("gruppe", userA);
        gruppe.addMitglieder(userB);
        gruppe.addMitglieder(userC);
        gruppe.setAusgaben(new ArrayList<>(List.of(ausgabe1, ausgabe2, ausgabe3)));

        //Act
        var alleTransaktionen = gruppe.berechneTransaktionen(gruppe.berechneSalden(gruppe.alleSchuldenBerechnen()));
        //Assert
        assertThat(alleTransaktionen).isEmpty();
    }

    @Test
    @DisplayName("Szenario 5: ABC Beispiel aus der Einführung")
    void szenario5() {
        //Arrange
        User anton = new User("Anton");
        User berta = new User("Berta");
        User christian = new User("Christian");

        Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal(60), anton, new ArrayList<>(List.of(anton, berta, christian)));
        Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Ausgabe2", new BigDecimal(30), berta, new ArrayList<>(List.of(anton, berta, christian)));
        Ausgabe ausgabe3 = new Ausgabe("ausgabe3", "Ausgabe3", new BigDecimal(100), christian, new ArrayList<>(List.of(berta, christian)));

        Gruppe gruppe =Gruppe.gruppeErstellen("gruppe", anton);
        gruppe.addMitglieder(berta);
        gruppe.addMitglieder(christian);
        gruppe.setAusgaben(new ArrayList<>(List.of(ausgabe1, ausgabe2, ausgabe3)));
        //Act
        var alleTransaktionen = gruppe.berechneTransaktionen(gruppe.berechneSalden(gruppe.alleSchuldenBerechnen()));
        //Assert
        assertThat(alleTransaktionen).containsExactlyInAnyOrder(new Transaktion(berta, anton, new BigDecimal(30)),
                new Transaktion(berta, christian, new BigDecimal(20)));
    }

    @Test
    @DisplayName("Szenario 6: Beispiel aus der Aufgabenstellung")
    void szenario6() {
        //Arrange
        User userA = new User("A");
        User userB = new User("B");
        User userC = new User("C");
        User userD = new User("D");
        User userE = new User("E");
        User userF = new User("F");

        Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Hotelzimmer", new BigDecimal("564"), userA, new ArrayList<>(List.of(userA, userB, userC, userD, userE, userF)));
        Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Benzin(Hinweg)", new BigDecimal("38.58"), userB, new ArrayList<>(List.of(userA, userB)));
        Ausgabe ausgabe3 = new Ausgabe("ausgabe3", "Bezin(Rückweg)", new BigDecimal("38.58"), userB, new ArrayList<>(List.of(userB, userA, userD)));
        Ausgabe ausgabe4 = new Ausgabe("ausgabe4", "Bezin", new BigDecimal("82.11"), userC, new ArrayList<>(List.of(userC, userE, userF)));
        Ausgabe ausgabe5 = new Ausgabe("ausgabe5", "Städtetour", new BigDecimal("96"), userD, new ArrayList<>(List.of(userA, userB, userC, userD, userE, userF)));
        Ausgabe ausgabe6 = new Ausgabe("ausgabe6", "Theatervorstellung", new BigDecimal("95.37"), userF, new ArrayList<>(List.of(userB, userE, userF)));

        Gruppe gruppe = Gruppe.gruppeErstellen("gruppe",userA);
        gruppe.setMitglieder(new ArrayList<>(List.of(userA, userB, userC, userD, userE, userF)));
        gruppe.setAusgaben(new ArrayList<>(List.of(ausgabe1, ausgabe2, ausgabe3, ausgabe4, ausgabe5, ausgabe6)));
        //Act
        var alleTransaktionen = gruppe.berechneTransaktionen(gruppe.berechneSalden(gruppe.alleSchuldenBerechnen()));

        //Assert
        assertThat(alleTransaktionen).containsExactlyInAnyOrder(new Transaktion(userB, userA, new BigDecimal("96.78")),
                new Transaktion(userC, userA, new BigDecimal("55.26")),
                new Transaktion(userD, userA, new BigDecimal("26.86")),
                new Transaktion(userE, userA, new BigDecimal("169.16")),
                new Transaktion(userF, userA, new BigDecimal("73.79")));
    }

    @Test
    @Disabled
    @DisplayName("Szenario 7: Minimierung")
    void szenario7() {
        //Arrange
        User userA = new User("A");
        User userB = new User("B");
        User userC = new User("C");
        User userD = new User("D");
        User userE = new User("E");
        User userF = new User("F");
        User userG = new User("G");

        Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal("20"), userD, new ArrayList<>(List.of(userD, userF)));
        Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Ausgabe2", new BigDecimal("10"), userG, new ArrayList<>(List.of(userB)));
        Ausgabe ausgabe3 = new Ausgabe("ausgabe3", "Ausgabe3", new BigDecimal("75"), userE, new ArrayList<>(List.of(userA, userC, userE)));
        Ausgabe ausgabe4 = new Ausgabe("ausgabe4", "Ausgabe4", new BigDecimal("50"), userF, new ArrayList<>(List.of(userA, userF)));
        Ausgabe ausgabe5 = new Ausgabe("ausgabe5", "Ausgabe5", new BigDecimal("40"), userE, new ArrayList<>(List.of(userD)));
        Ausgabe ausgabe6 = new Ausgabe("ausgabe6", "Ausgabe6", new BigDecimal("40"), userF, new ArrayList<>(List.of(userB, userF)));
        Ausgabe ausgabe7 = new Ausgabe("ausgabe7", "Ausgabe7", new BigDecimal("5"), userF, new ArrayList<>(List.of(userC)));
        Ausgabe ausgabe8 = new Ausgabe("ausgabe8", "Ausgabe8", new BigDecimal("30"), userG, new ArrayList<>(List.of(userA)));

        Gruppe gruppe = Gruppe.gruppeErstellen("gruppe", userA);
        gruppe.setMitglieder((List.of(userA, userB, userC, userD, userE, userF, userG)));
        gruppe.setAusgaben((List.of(ausgabe1, ausgabe2, ausgabe3, ausgabe4, ausgabe5, ausgabe6, ausgabe7, ausgabe8)));
        //Act
        var alleTransaktionen = gruppe.berechneTransaktionen(gruppe.berechneSalden(gruppe.alleSchuldenBerechnen()));

        //Assert
        assertThat(alleTransaktionen).containsExactlyInAnyOrder(new Transaktion(userA, userF, new BigDecimal("40")),
                new Transaktion(userA, userG, new BigDecimal("40")),
                new Transaktion(userB, userE, new BigDecimal("30")),
                new Transaktion(userC, userE, new BigDecimal("30")),
                new Transaktion(userD, userE, new BigDecimal("30")));
    }

}
