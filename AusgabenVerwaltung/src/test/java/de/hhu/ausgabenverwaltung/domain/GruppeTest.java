package de.hhu.ausgabenverwaltung.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GruppeTest {

	@Test
	@DisplayName("Person zur Gruppe hinzufuegen ")
	void personHinzufuegen() {
		//Arrange
		User user = new User("githubname", "Jens");
		Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<User>(), new HashSet<Transaktion>());


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
		Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<>(List.of(user)), new HashSet<Transaktion>());
		//Act
		gruppe.deleteMitglieder(user);
		//Assert
		assertThat(gruppe.getMitglieder().isEmpty()).isTrue();

	}
	@Test
	@DisplayName("Absender und Empfänger sind gleich bei einer Transaktion in der gleichen Gruppe")
	void absenderEqualsEmpfänger(){
		//Arrange
		User user1= new User("githubname", "Jens");
		Transaktion transaktion = new Transaktion(user1,user1, new BigDecimal("100.00") );
		Gruppe gruppe = new Gruppe("gruppeName", new ArrayList<>(), new ArrayList<>(List.of(user1)), new HashSet<Transaktion>(Set.of(transaktion)));
		//Act
		boolean isValid = gruppe.isTransaktionValid(transaktion);
		//Assert
		assertThat(isValid).isFalse();
	}
}
