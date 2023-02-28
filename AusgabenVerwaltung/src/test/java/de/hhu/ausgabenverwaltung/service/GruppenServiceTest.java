package de.hhu.ausgabenverwaltung.service;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GruppenServiceTest {

	@Test
	@DisplayName("Anzahl der Mitglieder beträgt 1, wenn die Gruppe zuerst erstellt wird")
	void erstelle_gruppe_test_1(){
		//Arrange
		User user = new User("githubname", "Jens");
		GruppenService gruppenService = new GruppenService();
		//Act
		Gruppe gruppe = gruppenService.gruppeErstellen(user, "gruppenName");

		//Assert
		assertThat(gruppe.getMitglieder().size()).isEqualTo(1);

	}

}
