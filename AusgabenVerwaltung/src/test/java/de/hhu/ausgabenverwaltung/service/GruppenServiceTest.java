package de.hhu.ausgabenverwaltung.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hhu.ausgabenverwaltung.application.repo.GruppenRepository;
import de.hhu.ausgabenverwaltung.application.service.GruppenService;
import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.Transaktion;
import de.hhu.ausgabenverwaltung.domain.User;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GruppenServiceTest {

    @Mock
    GruppenRepository repository;

    @InjectMocks
    GruppenService gruppenService;

    @Test
    @DisplayName("Anzahl der Mitglieder beträgt 1, wenn die Gruppe zuerst erstellt wird")
    void erstelleGruppeTest() throws Exception {
        //Arrange
        //Act
        Gruppe gruppe = gruppenService.gruppeErstellen("githubname", "gruppenName");

        //Assert
        assertThat(gruppe.getMitglieder().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Wird eine Gruppe korrekt geschlossen?")
    void gruppeSchliessenTest() throws Exception {
        //Arrange
        Gruppe gruppe = gruppenService.gruppeErstellen("test", "test");
        UUID id = gruppe.getId();
        when(repository.findById(id)).thenReturn(gruppe);
        //Act
        gruppenService.gruppeSchliessen(id);

        //Assert
        assertThat(gruppe.istOffen()).isFalse();
    }

    @Test
    @DisplayName("Werden die geschlossenen Gruppen von User richtig rausgegeben")
    void geschlossenVonUserTest() throws Exception {
        //Arrange
        Gruppe gruppe = gruppenService.gruppeErstellen("test", "testgruppe");
        gruppe.schliessen();
        when(repository.geschlossenVonUser(new User("test"))).thenReturn(List.of(gruppe));
        //Act
        List<Gruppe> geschlossenVonUser = gruppenService.geschlossenVonUser("test");

        //Assert
        assertThat(geschlossenVonUser).contains(gruppe);
    }

    @Test
    @DisplayName("Werden die offenen Gruppen von User richtig rausgegeben")
    void offenVonUserTest() throws Exception {
        //Arrange
        Gruppe gruppe = gruppenService.gruppeErstellen("test", "testgruppe");
        when(repository.offenVonUser(new User("test"))).thenReturn(List.of(gruppe));
        //Act
        List<Gruppe> offenVonUser = gruppenService.offenVonUser("test");
        //Assert
        assertThat(offenVonUser).contains(gruppe);
    }

    @Test
    @DisplayName("Die richtige Gruppe wird mit id gefunden")
    void findByIdTest() throws Exception {
        //Arrange
        Gruppe gruppe = gruppenService.gruppeErstellen("test", "testgruppe");
        UUID id = gruppe.getId();
        when(repository.findById(id)).thenReturn(gruppe);
        //Act
        Gruppe gesuchteGruppe = gruppenService.findById(id);
        //Assert
        assertThat(gesuchteGruppe).isEqualTo(gruppe);
    }

    @Test
    @DisplayName("Die Salden werden richtig zurueckgegeben")
    void berechneSaldenTest() throws Exception {
        //Arrange
        Gruppe gruppe = gruppenService.gruppeErstellen("test", "testgruppe");
        User test1 = new User("test1");
        User test2 = new User("test2");
        gruppe.addMitglieder(test1);
        gruppe.addMitglieder(test2);
        gruppe.ausgabeHinzufuegen(
                new Ausgabe("ausgabe1", "ausgabe2", BigDecimal.TEN, test1, List.of(test2)));
        UUID id = gruppe.getId();
        when(repository.findById(id)).thenReturn(gruppe);
        //Act
        HashMap<User, BigDecimal> salden = gruppenService.berechneSalden(id);
        //Assert
        assertThat(salden).isEqualTo(new HashMap<>(
                Map.of(test1, new BigDecimal("-10.00"), test2, new BigDecimal("10.00"),
                        new User("test"), BigDecimal.ZERO)));
    }

    @Test
    @DisplayName("Mitglied wird hinzugefuegt")
    void addMitgliedTest() throws Exception {
        //Arrange
        Gruppe gruppe = gruppenService.gruppeErstellen("test", "testgruppe");
        UUID id = gruppe.getId();
        when(repository.findById(id)).thenReturn(gruppe);
        //Act
        gruppenService.addMitglied(id, "test1");
        //Assert
        assertThat(gruppe.getMitglieder().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Ausgabe wird hinzugefuegt")
    void addAusgabe() throws Exception {
        //Arrange
        Gruppe gruppe = gruppenService.gruppeErstellen("test", "testgruppe");
        UUID id = gruppe.getId();
        User user = new User("test1");
        gruppe.addMitglieder(user);
        when(repository.findById(id)).thenReturn(gruppe);
        //Act
        gruppenService.addAusgabe(id,
                new Ausgabe("ausgabe1", "ausgabe2", BigDecimal.TEN, user, List.of(user)));
        //Assert
        assertThat(gruppe.getAusgaben().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Mitglieder Check")
    void checkMitglied() throws Exception {
        //Arrange
        Gruppe gruppe = gruppenService.gruppeErstellen("test", "testgruppe");
        UUID id = gruppe.getId();
        User user = new User("test1");
        gruppe.addMitglieder(user);
        when(repository.findById(id)).thenReturn(gruppe);
        //Act
        boolean checkMitglied = gruppenService.checkMitglied(id, "test1");
        //Assert
        assertThat(checkMitglied).isTrue();
    }

    @Test
    @DisplayName("Checkt ob GithubName korrekt ist")
    void isNameValid() throws Exception {
        //Act
        boolean nameisValid = gruppenService.nameIsValid("test");
        //Assert
        assertThat(nameisValid).isTrue();
    }

    @Test
    @DisplayName("Prueft ob Gruppe offen ist")
    void istOffen() throws Exception {
        //Arrange
        Gruppe gruppe = gruppenService.gruppeErstellen("test", "testgruppe");
        UUID id = gruppe.getId();
        when(repository.findById(id)).thenReturn(gruppe);
        //Act
        boolean istOffen = gruppenService.istOffen(id);
        //Assert
        assertThat(istOffen).isTrue();
    }
    @Test
    @DisplayName("berechneTransaktionen set")
    void berechneTransaktionen() throws Exception {
        //Arrange
        Gruppe gruppe = gruppenService.gruppeErstellen("test", "testgruppe");
        UUID id = gruppe.getId();
        when(repository.findById(id)).thenReturn(gruppe);
        //Act
        Set<Transaktion> transaktionen = gruppenService.berechneTransaktionen(id);
        //Assert
        assertThat(transaktionen.size()).isEqualTo(0);
    }
}
