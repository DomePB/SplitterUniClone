package de.hhu.ausgabenverwaltung.service;

import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.Transaktion;
import de.hhu.ausgabenverwaltung.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
	@DisplayName("Alle Schulden einer Gruppe korrekt berechnet")
	void schuldenEinerGruppe(){
		//Arrange
		GruppenService gruppenService = new GruppenService();
		User user1 = new User("githubname1", "Jens");
		User user2 = new User("githubname2", "Bob");
		Ausgabe ausgabe1 = new Ausgabe("ausgabe1", "Essen", new BigDecimal(10),user1,new ArrayList<>(List.of(user1,user2)));
		Ausgabe ausgabe2 = new Ausgabe("ausgabe1", "Kino", new BigDecimal(30),user2,new ArrayList<>(List.of(user1,user2)));
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
		assertThat(alleSalden).containsEntry(userA, new BigDecimal(-2));
		assertThat(alleSalden).containsEntry(userB, new BigDecimal(7));
		assertThat(alleSalden).containsEntry(userC, new BigDecimal(1));
		assertThat(alleSalden).containsEntry(userD, new BigDecimal(-6));
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
}
