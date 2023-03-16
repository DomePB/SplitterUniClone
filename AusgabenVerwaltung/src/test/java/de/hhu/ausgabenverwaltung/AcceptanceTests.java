package de.hhu.ausgabenverwaltung;

import static org.assertj.core.api.Assertions.assertThat;

import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.Transaktion;
import de.hhu.ausgabenverwaltung.domain.User;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AcceptanceTests {

    @Test
    @DisplayName("Szenario 1: Summieren von Auslagen")
    void szenario1() {
        //Arrange
        User userA = new User("A");
        User userB = new User("B");

        Ausgabe
                ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal(10), userA, new HashSet<>(Set.of(userA, userB)));
        Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Ausgabe2", new BigDecimal(20), userA, new HashSet<>(Set.of(userA, userB)));

        Gruppe gruppe = Gruppe.createGruppe("gruppe", Set.of(userA, userB));
        gruppe.addAusgabe(ausgabe1);
        gruppe.addAusgabe(ausgabe2);

        //Act
        var alleTransaktionen = gruppe.berechneTransaktionen(gruppe.berechneSalden(gruppe.alleSchuldenBerechnen()));
        //Assert
        assertThat(alleTransaktionen).isEqualTo(new HashSet<>(Set.of(new Transaktion(userB, userA, new BigDecimal("15.00")))));
    }

    @Test
    @DisplayName("Szenario 2: Ausgleich")
    void szenario2() {
        //Arrange
        User userA = new User("A");
        User userB = new User("B");

        Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal(10), userA, new HashSet<>(Set.of(userA, userB)));
        Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Ausgabe2", new BigDecimal(20), userB, new HashSet<>(Set.of(userA, userB)));

        Gruppe gruppe = Gruppe.createGruppe("gruppe", Set.of(userA, userB));
        gruppe.addAusgabe(ausgabe1);
        gruppe.addAusgabe(ausgabe2);

        //Act
        var alleTransaktionen = gruppe.berechneTransaktionen(gruppe.berechneSalden(gruppe.alleSchuldenBerechnen()));
        //Assert
        assertThat(alleTransaktionen).isEqualTo(new HashSet<>(Set.of(new Transaktion(userA, userB, new BigDecimal("5.00")))));
    }

    @Test
    @DisplayName("Szenario 3: Zahlung ohne eigene Beteiligung")
    void szenario3() {
        //Arrange
        User userA = new User("A");
        User userB = new User("B");

        Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal(10), userA, new HashSet<>(Set.of(userB)));
        Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Ausgabe2", new BigDecimal(20), userA, new HashSet<>(Set.of(userA, userB)));

        Gruppe gruppe = Gruppe.createGruppe("gruppe", Set.of(userA, userB));
        gruppe.addAusgabe(ausgabe1);
        gruppe.addAusgabe(ausgabe2);

        //Act
        var alleTransaktionen = gruppe.berechneTransaktionen(gruppe.berechneSalden(gruppe.alleSchuldenBerechnen()));
        //Assert
        assertThat(alleTransaktionen).isEqualTo(new HashSet<>(Set.of(new Transaktion(userB, userA, new BigDecimal("20.00")))));
    }

    @Test
    @DisplayName("Szenario 4: Ringausgleich")
    void szenario4() {
        //Arrange
        User userA = new User("A");
        User userB = new User("B");
        User userC = new User("C");

        Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal(10), userA, new HashSet<>(Set.of(userA, userB)));
        Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Ausgabe2", new BigDecimal(10), userB, new HashSet<>(Set.of(userB, userC)));
        Ausgabe ausgabe3 = new Ausgabe("ausgabe3", "Ausgabe3", new BigDecimal(10), userC, new HashSet<>(Set.of(userA, userC)));

        Gruppe gruppe = Gruppe.createGruppe("gruppe", Set.of(userA, userB, userC));
        gruppe.addAusgabe(ausgabe1);
        gruppe.addAusgabe(ausgabe2);
        gruppe.addAusgabe(ausgabe3);


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

        Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal(60), anton, new HashSet<>(Set.of(anton, berta, christian)));
        Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Ausgabe2", new BigDecimal(30), berta, new HashSet<>(Set.of(anton, berta, christian)));
        Ausgabe ausgabe3 = new Ausgabe("ausgabe3", "Ausgabe3", new BigDecimal(100), christian, new HashSet<>(Set.of(berta, christian)));

        Gruppe gruppe = Gruppe.createGruppe("gruppe", Set.of(anton, berta, christian));
        gruppe.addAusgabe(ausgabe1);
        gruppe.addAusgabe(ausgabe2);
        gruppe.addAusgabe(ausgabe3);

        //Act
        var alleTransaktionen = gruppe.berechneTransaktionen(gruppe.berechneSalden(gruppe.alleSchuldenBerechnen()));
        //Assert
        assertThat(alleTransaktionen).containsExactlyInAnyOrder(new Transaktion(berta, anton, new BigDecimal("30.00")),
                new Transaktion(berta, christian, new BigDecimal("20.00")));
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

        Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Hotelzimmer", new BigDecimal("564"), userA, new HashSet<>(Set.of(userA, userB, userC, userD, userE, userF)));
        Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Benzin(Hinweg)", new BigDecimal("38.58"), userB, new HashSet<>(Set.of(userA, userB)));
        Ausgabe ausgabe3 = new Ausgabe("ausgabe3", "Bezin(Rückweg)", new BigDecimal("38.58"), userB, new HashSet<>(Set.of(userB, userA, userD)));
        Ausgabe ausgabe4 = new Ausgabe("ausgabe4", "Bezin", new BigDecimal("82.11"), userC, new HashSet<>(Set.of(userC, userE, userF)));
        Ausgabe ausgabe5 = new Ausgabe("ausgabe5", "Städtetour", new BigDecimal("96"), userD, new HashSet<>(Set.of(userA, userB, userC, userD, userE, userF)));
        Ausgabe ausgabe6 = new Ausgabe("ausgabe6", "Theatervorstellung", new BigDecimal("95.37"), userF, new HashSet<>(Set.of(userB, userE, userF)));

        Gruppe gruppe =
                Gruppe.createGruppe("gruppe", Set.of(userA, userB, userC, userD, userE, userF));
        gruppe.addAusgabe(ausgabe1);
        gruppe.addAusgabe(ausgabe2);
        gruppe.addAusgabe(ausgabe3);
        gruppe.addAusgabe(ausgabe4);
        gruppe.addAusgabe(ausgabe5);
        gruppe.addAusgabe(ausgabe6);


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

        Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal("20"), userD, new HashSet<>(Set.of(userD, userF)));
        Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Ausgabe2", new BigDecimal("10"), userG, new HashSet<>(Set.of(userB)));
        Ausgabe ausgabe3 = new Ausgabe("ausgabe3", "Ausgabe3", new BigDecimal("75"), userE, new HashSet<>(Set.of(userA, userC, userE)));
        Ausgabe ausgabe4 = new Ausgabe("ausgabe4", "Ausgabe4", new BigDecimal("50"), userF, new HashSet<>(Set.of(userA, userF)));
        Ausgabe ausgabe5 = new Ausgabe("ausgabe5", "Ausgabe5", new BigDecimal("40"), userE, new HashSet<>(Set.of(userD)));
        Ausgabe ausgabe6 = new Ausgabe("ausgabe6", "Ausgabe6", new BigDecimal("40"), userF, new HashSet<>(Set.of(userB, userF)));
        Ausgabe ausgabe7 = new Ausgabe("ausgabe7", "Ausgabe7", new BigDecimal("5"), userF, new HashSet<>(Set.of(userC)));
        Ausgabe ausgabe8 = new Ausgabe("ausgabe8", "Ausgabe8", new BigDecimal("30"), userG, new HashSet<>(Set.of(userA)));

        Gruppe gruppe = Gruppe.createGruppe("gruppe",
                Set.of(userA, userB, userC, userD, userE, userF, userG));
        gruppe.addAusgabe(ausgabe1);
        gruppe.addAusgabe(ausgabe2);
        gruppe.addAusgabe(ausgabe3);
        gruppe.addAusgabe(ausgabe4);
        gruppe.addAusgabe(ausgabe5);
        gruppe.addAusgabe(ausgabe6);
        gruppe.addAusgabe(ausgabe7);
        gruppe.addAusgabe(ausgabe8);


        //Act
        var alleTransaktionen = gruppe.berechneTransaktionen(gruppe.berechneSalden(gruppe.alleSchuldenBerechnen()));

        //Assert
        assertThat(alleTransaktionen).containsExactlyInAnyOrder(new Transaktion(userA, userF, new BigDecimal("40.00")),
                new Transaktion(userA, userG, new BigDecimal("40.00")),
                new Transaktion(userB, userE, new BigDecimal("30.00")),
                new Transaktion(userC, userE, new BigDecimal("30.00")),
                new Transaktion(userD, userE, new BigDecimal("30.00")));
    }
}
