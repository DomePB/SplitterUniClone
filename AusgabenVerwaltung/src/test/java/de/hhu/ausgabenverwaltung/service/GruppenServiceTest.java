package de.hhu.ausgabenverwaltung.service;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GruppenServiceTest {

	@Test
	@DisplayName("person fügt Mitglieder hin ")
	void personHinzufuegen(){
		//Arrange
		User user = new User("githubname", "Jens");
		Gruppe gruppe = new Gruppe("gruppeName",new ArrayList<>(),new ArrayList<User>());
		GruppenService gruppenService = new GruppenService();

		//Act
		gruppenService.personHinzufuegen(user,gruppe);

		//Assert
		assertThat(gruppe.getMitglieder().size()).isEqualTo(1);

	}

}
