package de.hhu.ausgabenverwaltung.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hhu.ausgabenverwaltung.api.models.GruppeModel;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import de.hhu.ausgabenverwaltung.service.GruppenService;
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

}
