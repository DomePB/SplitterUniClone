package de.hhu.ausgabenverwaltung.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
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

    @Mock
    GruppenService gruppenServiceMock;

    @Test
    @DisplayName("Anzahl der Mitglieder beträgt 1, wenn die Gruppe zuerst erstellt wird")
    void erstelleGruppeTest() {
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
}

