package de.hhu.ausgabenverwaltung.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class GruppeTest {

	@Test
	@DisplayName("person fügt Mitglieder hin ")
	void personHinzufuegen() {
		//Arrange
		User user = new User("githubname", "Jens");
		Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<User>());

		//Act
		gruppe.addMitglieger(user);

		//Assert
		assertThat(gruppe.getMitglieder().size()).isEqualTo(1);

	}
}
