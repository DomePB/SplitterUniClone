package de.hhu.ausgabenverwaltung.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
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

    @Mock
    GruppenService gruppenServiceMock;

    @Test
    @DisplayName("Anzahl der Mitglieder beträgt 1, wenn die Gruppe zuerst erstellt wird")
    void erstelleGruppeTest() {
        //Arrange
        User user = new User("githubname");
        //Act
        Gruppe gruppe = gruppenService.gruppeErstellen(user, "gruppenName");

        //Assert
        assertThat(gruppe.getMitglieder().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Wird eine Gruppe korrekt geschlossen?")
    void gruppeSchliessenTest() {
        //Arrange
        Gruppe gruppe =
                Gruppe.gruppeErstellen("gruppe1", new User("test"));

        //Act
        gruppenService.gruppeSchliessen(gruppe);

        //Assert
        assertThat(gruppe.istOffen()).isFalse();
    }
}

