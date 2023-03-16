package de.hhu.ausgabenverwaltung.adapters.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hhu.ausgabenverwaltung.adapters.controller.api.ApiController;
import de.hhu.ausgabenverwaltung.adapters.controller.api.models.AuslagenModel;
import de.hhu.ausgabenverwaltung.adapters.controller.api.models.GruppeModel;
import de.hhu.ausgabenverwaltung.application.service.GruppenService;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = ApiController.class)
public class ApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    GruppenService service;

    private static <T> Set<T> anySet() {
        return any();
    }

    @Test
    @DisplayName("Gruppe wird erstellt")
    void test_1() throws Exception {
        GruppeModel gruppeModel = new GruppeModel(null, "gruppenName", Set.of("A"), false, null);
        String json = new ObjectMapper().writeValueAsString(gruppeModel);

        Gruppe gruppe = Gruppe.createGruppe("gruppenName", Set.of(new User("user1")));

        when(service.createGruppe(anySet(), any())).thenReturn(gruppe);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/gruppen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Gruppen von User werden ausgegeben")
    void test_2() throws Exception {
        User user1 = new User("user1");
      Gruppe gruppe = Gruppe.createGruppe("gruppenName", Set.of(user1), UUID.randomUUID());

        when(service.getGruppenVonUser(user1.githubHandle())).thenReturn(List.of(gruppe));

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/user/{githubHandle}/gruppen", user1.githubHandle()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Gruppen-Info wird ausgegeben")
    void test_3() throws Exception {
      User user1 = new User("user1");
      Gruppe gruppe = Gruppe.createGruppe("gruppenName", Set.of(user1), UUID.randomUUID());

        when(service.findById(gruppe.getId())).thenReturn(gruppe);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/gruppen/{gruppenId}", gruppe.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Gruppe wird geschlossen")
    void test_4() throws Exception {
      User user1 = new User("user1");
      Gruppe gruppe = Gruppe.createGruppe("gruppenName", Set.of(user1), UUID.randomUUID());

        doAnswer(invocation -> {
            gruppe.schliessen();
            return null;
        }).when(service).closeGruppe(gruppe.getId());

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/gruppen/{gruppenId}/schliessen", gruppe.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Ausgabe in der Gruppe wird ausgelegt")
    void test_5() throws Exception {
      User user1 = new User("user1");
      User user2 = new User("user2");
      AuslagenModel auslagenModel = new AuslagenModel("grund", "user1", 100, Set.of("user2"));
      String json = new ObjectMapper().writeValueAsString(auslagenModel);
      Gruppe gruppe = Gruppe.createGruppe("gruppenName", Set.of(user1, user2), UUID.randomUUID());

      when(service.istOffen(gruppe.getId())).thenReturn(true);
      doNothing().when(service).addAusgabe(any(), any());
      mockMvc.perform(
          MockMvcRequestBuilders.post("/api/gruppen/{gruppenId}/auslagen", gruppe.getId())
              .contentType(MediaType.APPLICATION_JSON)
              .content(json)).andExpect(status().isCreated());
    }

  @Test
  @DisplayName("Ausgleich Konflikt wenn eine Gruppen bereits geschlossen ist")
  void test_6() throws Exception {
    User user1 = new User("user1");
    User user2 = new User("user2");
    AuslagenModel auslagenModel = new AuslagenModel("grund", "user1", 100, Set.of("user2"));
    String json = new ObjectMapper().writeValueAsString(auslagenModel);
    Gruppe gruppe = Gruppe.createGruppe("gruppenName", Set.of(user1, user2));
    when(service.istOffen(gruppe.getId())).thenThrow(new Exception(""));
    doNothing().when(service).addAusgabe(any(), any());
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/gruppen/{gruppenId}/auslagen", gruppe.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Json-Auslagen ist fehlerhaft")
  void test_7() throws Exception {
    User user1 = new User("user1");
    User user2 = new User("user2");
    Gruppe gruppe = Gruppe.createGruppe("gruppenName", Set.of(user1, user2), UUID.randomUUID());

    when(service.istOffen(gruppe.getId())).thenReturn(true);
    doNothing().when(service).addAusgabe(any(), any());
    mockMvc.perform(
        MockMvcRequestBuilders.post("/api/gruppen/{gruppenId}/auslagen", gruppe.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content("")).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Ausgleichen Test")
  void test_8() throws Exception {
    Gruppe gruppe = Gruppe.createGruppe("gruppenName", Set.of(), UUID.randomUUID());
    when(service.berechneTransaktionen(gruppe.getId())).thenReturn(Set.of());
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/gruppen/{gruppenId}/ausgleich", gruppe.getId()))
                    .andExpect(status().isOk());
  }

    @Test
    @DisplayName("Ausgleichen Test")
    void test_9() throws Exception {
        when(service.berechneTransaktionen(any())).thenThrow(new Exception(""));
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/gruppen/{gruppenId}/ausgleich", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
