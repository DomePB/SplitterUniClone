package de.hhu.ausgabenverwaltung.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.Transaktion;
import de.hhu.ausgabenverwaltung.domain.User;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GruppenServiceTest {
	@Test
	@DisplayName("Anzahl der Mitglieder beträgt 1, wenn die Gruppe zuerst erstellt wird")
	void erstelleGruppeTest(){
		//Arrange
		User user = new User("githubname", "Jens");
		GruppenService gruppenService = new GruppenService();

		//Act
		Gruppe gruppe = gruppenService.gruppeErstellen(user, "gruppenName");

		//Assert
		assertThat(gruppe.getMitglieder().size()).isEqualTo(1);
	}

	@Test
	@DisplayName("Wird eine Gruppe korrekt geschlossen?")
	void gruppeSchliessenTest() {
		//Arrange
		GruppenService gruppenService = new GruppenService();
		Gruppe gruppe =
				new Gruppe("gruppe1", new ArrayList<>(), new ArrayList<>(), new HashSet<>(), true);

		//Act
		gruppenService.gruppeSchliessen(gruppe);

		//Assert
		assertThat(gruppe.istOffen()).isFalse();
	}

	@Test
	@DisplayName("Alle Gruppen eines Users werden zurückgegeben")
	void gruppenVonUserTest() {
		//Arrange
		GruppenService gruppenService = new GruppenService();
		User userA = new User("githubname1", "Alpha");
		User userB = new User("githubname2", "Beta");
		Gruppe gruppe1 = gruppenService.gruppeErstellen(userA, "Gruppe 1");
		Gruppe gruppe2 = gruppenService.gruppeErstellen(userB, "Gruppe 2");

		// Act
		List<Gruppe> gruppenVonA = gruppenService.gruppenVonUser(userA);

		// Assert
		assertThat(gruppenVonA).containsExactly(gruppe1);
	}

	@Test
	@DisplayName("Alle Schulden einer Gruppe korrekt berechnet")
	void schuldenEinerGruppe() {
		//Arrange
		GruppenService gruppenService = new GruppenService();
		User user1 = new User("githubname1", "Jens");
		User user2 = new User("githubname2", "Bob");
		Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Essen", new BigDecimal(10), user1,
				new ArrayList<>(List.of(user1, user2)));
		Ausgabe ausgabe2 = new Ausgabe("ausgabe1", "Kino", new BigDecimal(30), user2,
				new ArrayList<>(List.of(user1, user2)));
		Gruppe gruppe = new Gruppe("gruppe", new ArrayList<>(List.of(ausgabe1,ausgabe2)), new ArrayList<>(List.of(user1, user2)), new HashSet<>(),true);

		//Act
		var listVonSchuldigen =  gruppenService.alleSchuldenBerechnen(gruppe);

		//Assert
		assertThat(listVonSchuldigen).containsEntry(user1, new HashMap<>(Map.of(user2, new BigDecimal(5))));
		assertThat(listVonSchuldigen).containsEntry(user2, new HashMap<>(Map.of(user1, new BigDecimal(15))));
	}

	@Test
	@DisplayName("Salden werden korrekt berechnet")
	void berechneSalden() {
		//Arrange
		GruppenService gruppenService = new GruppenService();
		User userA = new User("githubname1", "Jens");
		User userB = new User("githubname2", "Bob");
		User userC = new User("githubname3", "Peter");
		User userD = new User("githubname4", "Heinz");

		Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal(8),userA,new ArrayList<>(List.of(userB)));
		Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Ausgabe2", new BigDecimal(5),userA,new ArrayList<>(List.of(userC)));
		Ausgabe ausgabe4 = new Ausgabe("ausgabe4", "Ausgabe2", new BigDecimal(3),userB,new ArrayList<>(List.of(userA)));
		Ausgabe ausgabe5 = new Ausgabe("ausgabe5", "Ausgabe2", new BigDecimal(7),userB,new ArrayList<>(List.of(userC)));
		Ausgabe ausgabe6 = new Ausgabe("ausgabe6", "Ausgabe2", new BigDecimal(11),userB,new ArrayList<>(List.of(userD)));

		Ausgabe ausgabe7 = new Ausgabe("ausgabe7", "Ausgabe2", new BigDecimal(10),userC,new ArrayList<>(List.of(userA)));
		Ausgabe ausgabe8 = new Ausgabe("ausgabe8", "Ausgabe2", new BigDecimal(1),userC,new ArrayList<>(List.of(userB)));
		Ausgabe ausgabe9 = new Ausgabe("ausgabe9", "Ausgabe2", new BigDecimal(6),userC,new ArrayList<>(List.of(userD)));

		Ausgabe ausgabe10 = new Ausgabe("ausgabe10", "Ausgabe2", new BigDecimal(2),userD,new ArrayList<>(List.of(userA)));
		Ausgabe ausgabe11 = new Ausgabe("ausgabe11", "Ausgabe2", new BigDecimal(5),userD,new ArrayList<>(List.of(userB)));
		Ausgabe ausgabe12 = new Ausgabe("ausgabe12", "Ausgabe2", new BigDecimal(4),userD,new ArrayList<>(List.of(userC)));

		Gruppe gruppe = new Gruppe("gruppe", new ArrayList<>(List.of(ausgabe1,ausgabe2,ausgabe4,ausgabe5,ausgabe6,ausgabe7,ausgabe8,ausgabe9,ausgabe10,ausgabe11,ausgabe12)), new ArrayList<>(List.of(userA, userB, userC, userD)), new HashSet<>(),true);

		//Act
		var alleSalden =  gruppenService.berechneSalden(gruppe);

		// Assert
		assertThat(alleSalden).containsEntry(userA, new BigDecimal(2));
		assertThat(alleSalden).containsEntry(userB, new BigDecimal(-7));
		assertThat(alleSalden).containsEntry(userC, new BigDecimal(-1));
		assertThat(alleSalden).containsEntry(userD, new BigDecimal(6));
	}

	@Test
	@DisplayName("Bei ausgleichenden Zahlungen sollen diese vorramgig berechnet werden, um Transaktionen minimal zu halten.")
	void berechneTransaktionen(){
		//Arrange

		GruppenService gruppenservice = mock(GruppenService.class);

		User userA = new User("githubname1", "Jens");
		User userB = new User("githubname2", "Bob");
		Gruppe gruppe = new Gruppe("gruppe", new ArrayList<>(), new ArrayList<>(List.of(userA, userB)), new HashSet<>(),true);
		when(gruppenservice.berechneSalden(gruppe)).thenReturn(new HashMap<>(Map.of(userA, new BigDecimal(5), userB, new BigDecimal(-5))));
		when(gruppenservice.berechneTransaktionen(gruppe)).thenCallRealMethod();
		//Act
		var alleTransaktionen = gruppenservice.berechneTransaktionen(gruppe);
		//Assert
		assertThat(alleTransaktionen).isEqualTo(new HashSet<>(Set.of(new Transaktion(userA,userB ,new BigDecimal(5)))));
	}

	@Test
	@DisplayName("UserA bezahlt mehr als B bekommt")
	void berechneTransaktionen2(){
		//Arrange

		GruppenService gruppenservice = mock(GruppenService.class);

		User userA = new User("githubname1", "Jens");
		User userB = new User("githubname2", "Bob");
		Gruppe gruppe = new Gruppe("gruppe", new ArrayList<>(), new ArrayList<>(List.of(userA, userB)), new HashSet<>(),true);
		when(gruppenservice.berechneSalden(gruppe)).thenReturn(new HashMap<>(Map.of(userA, new BigDecimal(5), userB, new BigDecimal(-3))));
		when(gruppenservice.berechneTransaktionen(gruppe)).thenCallRealMethod();
		//Act
		var alleTransaktionen = gruppenservice.berechneTransaktionen(gruppe);
		//Assert
		assertThat(alleTransaktionen).isEqualTo(new HashSet<>(Set.of(new Transaktion(userA,userB ,new BigDecimal(3)))));
	}
	@Test
	@DisplayName("UserB bezahlt mehr als A bekommt")
	void berechneTransaktionen3(){
		//Arrange

		GruppenService gruppenservice = mock(GruppenService.class);

		User userA = new User("githubname1", "Jens");
		User userB = new User("githubname2", "Bob");
		Gruppe gruppe = new Gruppe("gruppe", new ArrayList<>(), new ArrayList<>(List.of(userA, userB)), new HashSet<>(),true);
		when(gruppenservice.berechneSalden(gruppe)).thenReturn(new HashMap<>(Map.of(userA, new BigDecimal(-5), userB, new BigDecimal(6))));
		when(gruppenservice.berechneTransaktionen(gruppe)).thenCallRealMethod();
		//Act
		var alleTransaktionen = gruppenservice.berechneTransaktionen(gruppe);
		//Assert
		assertThat(alleTransaktionen).isEqualTo(new HashSet<>(Set.of(new Transaktion(userB,userA ,new BigDecimal(5)))));
	}
	@Test
	@DisplayName("UserB bezahlt weniger als A bekommt")
	void berechneTransaktionen4(){
		//Arrange

		GruppenService gruppenservice = mock(GruppenService.class);

		User userA = new User("githubname1", "Jens");
		User userB = new User("githubname2", "Bob");
		Gruppe gruppe = new Gruppe("gruppe", new ArrayList<>(), new ArrayList<>(List.of(userA, userB)), new HashSet<>(),true);
		when(gruppenservice.berechneSalden(gruppe)).thenReturn(new HashMap<>(Map.of(userA, new BigDecimal(-5), userB, new BigDecimal(3))));
		when(gruppenservice.berechneTransaktionen(gruppe)).thenCallRealMethod();
		//Act
		var alleTransaktionen = gruppenservice.berechneTransaktionen(gruppe);
		//Assert
		assertThat(alleTransaktionen).isEqualTo(new HashSet<>(Set.of(new Transaktion(userB,userA ,new BigDecimal(3)))));
	}
	@Test
	@DisplayName("UserA bezahlt weniger als B bekommt")
	void berechneTransaktionen5(){
		//Arrange

		GruppenService gruppenservice = mock(GruppenService.class);

		User userA = new User("githubname1", "Jens");
		User userB = new User("githubname2", "Bob");
		Gruppe gruppe = new Gruppe("gruppe", new ArrayList<>(), new ArrayList<>(List.of(userA, userB)), new HashSet<>(),true);
		when(gruppenservice.berechneSalden(gruppe)).thenReturn(new HashMap<>(Map.of(userA, new BigDecimal(3), userB, new BigDecimal(-5))));
		when(gruppenservice.berechneTransaktionen(gruppe)).thenCallRealMethod();
		//Act
		var alleTransaktionen = gruppenservice.berechneTransaktionen(gruppe);
		//Assert
		assertThat(alleTransaktionen).isEqualTo(new HashSet<>(Set.of(new Transaktion(userA,userB ,new BigDecimal(3)))));
	}

	@Test
	@DisplayName("Szenario 1: Summieren von Auslagen")
	void szenario1(){
		//Arrange
		GruppenService gruppenservice = new GruppenService();
		User userA = new User("githubname1", "A");
		User userB = new User("githubname2", "B");

		Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal(10),userA,new ArrayList<>(List.of(userA, userB)));
		Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Ausgabe2", new BigDecimal(20),userA,new ArrayList<>(List.of(userA, userB)));

		Gruppe gruppe = new Gruppe("gruppe", new ArrayList<>(List.of(ausgabe1,ausgabe2)), new ArrayList<>(List.of(userA, userB)), new HashSet<>(),true);
		//Act
		var alleTransaktionen = gruppenservice.berechneTransaktionen(gruppe);
		//Assert
		assertThat(alleTransaktionen).isEqualTo(new HashSet<>(Set.of(new Transaktion(userB,userA ,new BigDecimal(15)))));
	}
	@Test
	@DisplayName("Szenario 2: Ausgleich")
	void szenario2(){
		//Arrange
		GruppenService gruppenservice = new GruppenService();
		User userA = new User("githubname1", "A");
		User userB = new User("githubname2", "B");

		Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal(10),userA,new ArrayList<>(List.of(userA, userB)));
		Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Ausgabe2", new BigDecimal(20),userB,new ArrayList<>(List.of(userA, userB)));

		Gruppe gruppe = new Gruppe("gruppe", new ArrayList<>(List.of(ausgabe1,ausgabe2)), new ArrayList<>(List.of(userA, userB)), new HashSet<>(),true);
		//Act
		var alleTransaktionen = gruppenservice.berechneTransaktionen(gruppe);
		//Assert
		assertThat(alleTransaktionen).isEqualTo(new HashSet<>(Set.of(new Transaktion(userA,userB ,new BigDecimal(5)))));
	}
	@Test
	@DisplayName("Szenario 3: Zahlung ohne eigene Beteiligung")
	void szenario3(){
		//Arrange
		GruppenService gruppenservice = new GruppenService();
		User userA = new User("githubname1", "A");
		User userB = new User("githubname2", "B");

		Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal(10),userA,new ArrayList<>(List.of(userB)));
		Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Ausgabe2", new BigDecimal(20),userA,new ArrayList<>(List.of(userA, userB)));

		Gruppe gruppe = new Gruppe("gruppe", new ArrayList<>(List.of(ausgabe1,ausgabe2)), new ArrayList<>(List.of(userA, userB)), new HashSet<>(),true);
		//Act
		var alleTransaktionen = gruppenservice.berechneTransaktionen(gruppe);
		//Assert
		assertThat(alleTransaktionen).isEqualTo(new HashSet<>(Set.of(new Transaktion(userB,userA ,new BigDecimal(20)))));
	}

	@Test
	@DisplayName("Szenario 4: Ringausgleich")
	void szenario4(){
		//Arrange
		GruppenService gruppenservice = new GruppenService();
		User userA = new User("githubname1", "A");
		User userB = new User("githubname2", "B");
		User userC = new User("githubname3", "C");

		Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal(10),userA,new ArrayList<>(List.of(userA, userB)));
		Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Ausgabe2", new BigDecimal(10),userB,new ArrayList<>(List.of(userB, userC)));
		Ausgabe ausgabe3 = new Ausgabe("ausgabe3", "Ausgabe3", new BigDecimal(10),userC,new ArrayList<>(List.of(userA, userC)));

		Gruppe gruppe = new Gruppe("gruppe", new ArrayList<>(List.of(ausgabe1,ausgabe2,ausgabe3)), new ArrayList<>(List.of(userA, userB, userC)), new HashSet<>(),true);
		//Act
		var alleTransaktionen = gruppenservice.berechneTransaktionen(gruppe);
		//Assert
		assertThat(alleTransaktionen).isEmpty();
	}

	@Test
	@DisplayName("Szenario 5: ABC Beispiel aus der Einführung")
	void szenario5(){
		//Arrange
		GruppenService gruppenservice = new GruppenService();
		User anton = new User("githubname1", "Anton");
		User berta = new User("githubname2", "Berta");
		User christian = new User("githubname3", "Christian");

		Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal(60),anton,new ArrayList<>(List.of(anton,berta,christian)));
		Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Ausgabe2", new BigDecimal(30),berta,new ArrayList<>(List.of(anton,berta,christian)));
		Ausgabe ausgabe3 = new Ausgabe("ausgabe3", "Ausgabe3", new BigDecimal(100),christian,new ArrayList<>(List.of(berta, christian)));

		Gruppe gruppe = new Gruppe("gruppe", new ArrayList<>(List.of(ausgabe1,ausgabe2,ausgabe3)), new ArrayList<>(List.of(anton,berta,christian)), new HashSet<>(),true);
		//Act
		var alleTransaktionen = gruppenservice.berechneTransaktionen(gruppe);
		//Assert
		assertThat(alleTransaktionen).containsExactly(new Transaktion(berta,anton,new BigDecimal(30)),
				new Transaktion(berta,christian,new BigDecimal(20)));
	}


	@Test
	@DisplayName("Szenario 6: Beispiel aus der Aufgabenstellung")
	void szenario6(){
		//Arrange
		GruppenService gruppenservice = new GruppenService();

		User userA = new User("githubname1", "A");
		User userB = new User("githubname2", "B");
		User userC = new User("githubname3", "C");
		User userD = new User("githubname4", "D");
		User userE = new User("githubname5", "E");
		User userF = new User("githubname6", "F");

		Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Hotelzimmer", new BigDecimal("564"),userA,new ArrayList<>(List.of(userA,userB,userC,userD,userE,userF)));
		Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Benzin(Hinweg)", new BigDecimal("38.58"),userB,new ArrayList<>(List.of(userA,userB)));
		Ausgabe ausgabe3 = new Ausgabe("ausgabe3", "Bezin(Rückweg)", new BigDecimal("38.58"),userB,new ArrayList<>(List.of(userB,userA,userD)));
		Ausgabe ausgabe4 = new Ausgabe("ausgabe4", "Bezin", new BigDecimal("82.11"),userC,new ArrayList<>(List.of(userC,userE,userF)));
		Ausgabe ausgabe5 = new Ausgabe("ausgabe5", "Städtetour", new BigDecimal("96"),userD,new ArrayList<>(List.of(userA,userB,userC,userD,userE,userF)));
		Ausgabe ausgabe6 = new Ausgabe("ausgabe6", "Theatervorstellung", new BigDecimal("95.37"),userF,new ArrayList<>(List.of(userB,userE,userF)));

		Gruppe gruppe = new Gruppe("gruppe", new ArrayList<>(List.of(ausgabe1,ausgabe2,ausgabe3,ausgabe4,ausgabe5,ausgabe6)),
				new ArrayList<>(List.of(userA,userB,userC,userD,userE,userF)), new HashSet<>(),true);

		//Act
		var alleTransaktionen = gruppenservice.berechneTransaktionen(gruppe);

		//Assert
		assertThat(alleTransaktionen).containsExactlyInAnyOrder(new Transaktion(userB,userA,new BigDecimal("96.78")),
				new Transaktion(userC,userA,new BigDecimal("55.26")),
				new Transaktion(userD,userA,new BigDecimal("26.86")),
				new Transaktion(userE,userA,new BigDecimal("169.16")),
				new Transaktion(userF,userA,new BigDecimal("73.79")));
	}

	@Test
	@Disabled
	@DisplayName("Szenario 7: Minimierung")
	void szenario7(){
		//Arrange
		GruppenService gruppenservice = new GruppenService();

		User userA = new User("githubname1", "A");
		User userB = new User("githubname2", "B");
		User userC = new User("githubname3", "C");
		User userD = new User("githubname4", "D");
		User userE = new User("githubname5", "E");
		User userF = new User("githubname6", "F");
		User userG = new User("githubname7", "G");

		Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Ausgabe1", new BigDecimal("20"),userD,new ArrayList<>(List.of(userD,userF)));
		Ausgabe ausgabe2 = new Ausgabe("ausgabe2", "Ausgabe2", new BigDecimal("10"),userG,new ArrayList<>(List.of(userB)));
		Ausgabe ausgabe3 = new Ausgabe("ausgabe3", "Ausgabe3", new BigDecimal("75"),userE,new ArrayList<>(List.of(userA,userC,userE)));
		Ausgabe ausgabe4 = new Ausgabe("ausgabe4", "Ausgabe4", new BigDecimal("50"),userF,new ArrayList<>(List.of(userA,userF)));
		Ausgabe ausgabe5 = new Ausgabe("ausgabe5", "Ausgabe5", new BigDecimal("40"),userE,new ArrayList<>(List.of(userD)));
		Ausgabe ausgabe6 = new Ausgabe("ausgabe6", "Ausgabe6", new BigDecimal("40"),userF,new ArrayList<>(List.of(userB,userF)));
		Ausgabe ausgabe7 = new Ausgabe("ausgabe7", "Ausgabe7", new BigDecimal("5"),userF,new ArrayList<>(List.of(userC)));
		Ausgabe ausgabe8 = new Ausgabe("ausgabe8", "Ausgabe8", new BigDecimal("30"),userG,new ArrayList<>(List.of(userA)));

		Gruppe gruppe = new Gruppe("gruppe", new ArrayList<>(List.of(ausgabe1,ausgabe2,ausgabe3,ausgabe4,ausgabe5,ausgabe6,ausgabe7,ausgabe8)),
				new ArrayList<>(List.of(userA,userB,userC,userD,userE,userF,userG)), new HashSet<>(),true);

		//Act
		var alleTransaktionen = gruppenservice.berechneTransaktionen(gruppe);

		//Assert
		assertThat(alleTransaktionen).containsExactlyInAnyOrder(new Transaktion(userA,userF,new BigDecimal("40")),
				new Transaktion(userA,userG,new BigDecimal("40")),
				new Transaktion(userB,userE,new BigDecimal("30")),
				new Transaktion(userC,userE,new BigDecimal("30")),
				new Transaktion(userD,userE,new BigDecimal("30")));
	}

	@Test
	@DisplayName("User Transaktionen werden richtig angezeigt")
	void transaktionenFilternProUser(){

		//Arrange
		GruppenService gruppenservice = mock(GruppenService.class);

		User userA = new User("githubname1", "A");
		User userB = new User("githubname2", "B");
		User userC = new User("githubname3", "C");


		Transaktion transaktion1 = new Transaktion(userA, userB, new BigDecimal("40"));
		Transaktion transaktion2 = new Transaktion(userB, userA, new BigDecimal("10"));
		Transaktion transaktion3 = new Transaktion(userC, userB, new BigDecimal("20"));


		Gruppe gruppe1 = new Gruppe("gruppe1", new ArrayList<>(),
				new ArrayList<>(List.of(userA, userB)), new HashSet<>(Set.of(transaktion1)), true);
		Gruppe gruppe2 = new Gruppe("gruppe2", new ArrayList<>(),
				new ArrayList<>(List.of(userA, userB, userC)),
				new HashSet<>(Set.of(transaktion2, transaktion3)), true);

		when(gruppenservice.getGruppen()).thenReturn(List.of(gruppe1, gruppe2));
		when(gruppenservice.getBeteiligteTransaktionen(userA)).thenCallRealMethod();
		when(gruppenservice.berechneTransaktionen(gruppe1)).thenReturn(gruppe1.getTransaktionen());
		when(gruppenservice.berechneTransaktionen(gruppe2)).thenReturn(gruppe2.getTransaktionen());

		//Act
		var userTransaktionen = gruppenservice.getBeteiligteTransaktionen(userA);

		//Assert
		assertThat(userTransaktionen.size()).isEqualTo(2);
		assertThat(userTransaktionen).containsEntry(gruppe1, Set.of(transaktion1));
		assertThat(userTransaktionen).containsEntry(gruppe2, Set.of(transaktion2));
	}
}
