package de.hhu.ausgabenverwaltung.adapters.database;

import static org.assertj.core.api.Assertions.assertThat;

import de.hhu.ausgabenverwaltung.adapters.database.implementation.GruppenRepositoryImp;
import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Sql("classpath:/db/migration/test.sql")
@SpringBootTest(properties = "spring.flyway.enabled = false")
public class GruppenRepositoryImpTests {

  @Autowired
  GruppenRepositoryImp gruppenRepository;

  @Test
  @DisplayName("FindbyId wird getestet.")
  void findbyIdTest(){
    //Arrange
    Gruppe gruppe1 = Gruppe.createGruppe("gruppe1", Set.of());
    UUID id = gruppenRepository.save(gruppe1);
    //Act
    Gruppe byId = gruppenRepository.findById(id);
    //Assert
    assertThat(byId).isEqualTo(gruppe1);
  }

  @Test
  @DisplayName("Alle Gruppen eines Users werden zurückgegeben")
  void gruppenVonUserTest() {
    //Arrange
    User userA = new User("githubname1");
    User userB = new User("githubname2");
    Gruppe gruppe1 = Gruppe.createGruppe("gruppe1", Set.of(userA));
    Gruppe gruppe2 = Gruppe.createGruppe("gruppe2", Set.of(userB));

    gruppenRepository.save(gruppe1);
    gruppenRepository.save(gruppe2);

    // Act
    List<Gruppe> gruppenVonA = gruppenRepository.getGruppenvonUser(userA);

    // Assert
    assertThat(gruppenVonA).contains(gruppe1);
  }

  @Test
  @DisplayName("Filtern nach offen Gruppen von User")
  void gruppenFiltern() {
    //Arrange
    User user = new User("githubname");

    Gruppe gruppe1 = Gruppe.createGruppe("gruppe1", Set.of(user));
    Gruppe gruppe2 = Gruppe.createGruppe("gruppe2", Set.of(user));
    gruppe2.schliessen();
    gruppenRepository.save(gruppe1);
    gruppenRepository.save(gruppe2);

    // Act
    List<Gruppe> offeneGruppen = gruppenRepository.getOffeneGruppenVonUser(user);

    // Assert
    assertThat(offeneGruppen).contains(gruppe1);
  }

  @Test
  @DisplayName("Filtern nach geschlossene Gruppen von User")
  void gruppenFiltern2() {
    //Arrange
    User user = new User("githubname");

    Gruppe gruppe1 = Gruppe.createGruppe("gruppe1", Set.of(user));
    Gruppe gruppe2 = Gruppe.createGruppe("gruppe2", Set.of(user));
    gruppe2.schliessen();
    gruppenRepository.save(gruppe1);
    gruppenRepository.save(gruppe2);

    // Act
    List<Gruppe> geschlosseneGruppen = gruppenRepository.getGeschlosseneGruppenVonUser(user);

    // Assert
    assertThat(geschlosseneGruppen).contains(gruppe2);
  }

  @Test
  @DisplayName("Ausgabe wird gespeichert")
  void ausgabeinGruppe() {
    //Arrange
    User userA = new User("githubname1");
    User userB = new User("githubname2");
    Gruppe gruppe1 = Gruppe.createGruppe("gruppe1", Set.of(userA,userB));
    Ausgabe ausgabe = new Ausgabe("test", "test", new BigDecimal("10.33"), userA, Set.of(userA, userB));
    gruppe1.addAusgabe(ausgabe);
    UUID gruppenid = gruppenRepository.save(gruppe1);

    // Act
    Gruppe byId = gruppenRepository.findById(gruppenid);

    // Assert
    assertThat(byId.getAusgaben()).contains(ausgabe);
  }

}

