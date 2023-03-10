package de.hhu.ausgabenverwaltung.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hhu.ausgabenverwaltung.api.models.GruppeModel;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import de.hhu.ausgabenverwaltung.service.GruppenService;
import java.util.List;
import java.util.Set;
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

    Gruppe gruppe = Gruppe.gruppeErstellen("gruppenName", Set.of(new User("user1")));

    when(service.gruppeErstellen(anySet(), any())).thenReturn(gruppe);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/gruppen")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("Gruppen von User werden ausgegeben")
  void test_2() throws Exception {
    User user1 = new User("user1");
    Gruppe gruppe = Gruppe.gruppeErstellen("gruppenName", Set.of(user1));

    when(service.gruppenVonUser(user1.githubHandle())).thenReturn(List.of(gruppe));

    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/user/{githubHandle}/gruppen", user1.githubHandle()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  @DisplayName("Gruppen-Info wird ausgegeben")
  void test_3() throws Exception {
    User user1 = new User("user1");
    Gruppe gruppe = Gruppe.gruppeErstellen("gruppenName", Set.of(user1));

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
    Gruppe gruppe = Gruppe.gruppeErstellen("gruppenName", Set.of(user1));

    doAnswer(invocation -> {
      gruppe.schliessen();
      return null;
    }).when(service).gruppeSchliessen(gruppe.getId());

    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/gruppen/{gruppenId}/schliessen", gruppe.getId()))
        .andExpect(status().isOk());
  }

}
