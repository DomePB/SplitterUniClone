package de.hhu.ausgabenverwaltung.service;

import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

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
		List<HashMap<User, BigDecimal>> listVonSchuldigen =  gruppenService.alleSchuldenBerechnen(gruppe);
		//Assert
		assertThat(listVonSchuldigen).contains(new HashMap<>(Map.of(user1, new BigDecimal(15))), new HashMap<>(Map.of(user2, new BigDecimal(5))));

	}
}
