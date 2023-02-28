package de.hhu.ausgabenverwaltung.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GruppeTest {

	@Test
	@DisplayName("Person zur Gruppe hinzufuegen ")
	void personHinzufuegen() {
		//Arrange
		User user = new User("githubname", "Jens");
		Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<User>());

		//Act
		gruppe.addMitglieder(user);

		//Assert
		assertThat(gruppe.getMitglieder().size()).isEqualTo(1);

	}

	@Test
	@DisplayName("Person aus Gruppe entfernen")
	void personEntfernen(){
		//Arrange
		User user = new User("githubname", "Jens");
		Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<>(List.of(user)));
		//Act
		gruppe.deleteMitglieder(user);
		//Assert
		assertThat(gruppe.getMitglieder().isEmpty()).isTrue();

	}
}
