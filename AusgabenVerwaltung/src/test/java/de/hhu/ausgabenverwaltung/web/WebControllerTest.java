package de.hhu.ausgabenverwaltung.web;

import static com.tngtech.archunit.ArchConfiguration.get;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.config.http.MatcherType.mvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.Transaktion;
import de.hhu.ausgabenverwaltung.domain.User;
import de.hhu.ausgabenverwaltung.helper.WithMockOAuth2User;
import de.hhu.ausgabenverwaltung.service.GruppenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = WebController.class)
class WebControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    GruppenService service;

    @Test
    @WithMockOAuth2User(login = "JoeSchmoe")
    @DisplayName("Gruppe erstellen, prüft ob das redirect korrekt ist.")
    void gruppeErstellenTest() throws Exception {
        User user = new User("githubHandle");
        Gruppe gruppe = Gruppe.gruppeErstellen("gruppename",user);
        when(service.gruppeErstellen(user,"")).thenReturn(gruppe);

        mockMvc.perform(
                post("/").with(csrf()).param("gruppenName", "gruppenName"))
                .andExpect(view().name("redirect:/"))
                .andExpect(status().isFound());
    }

    @Test
    @WithMockOAuth2User(login = "JoeSchmoe")
    @DisplayName("Die Index-Seite ist für authentifizierte User erreichbar")
    void test_3() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("user", new User("JoeSchmoe")));
    }

    @Test
    @WithMockOAuth2User(login = "JoeSchmoe")
    @DisplayName("Exception werfen, wenn Gruppe nicht gefundnen wird.")
    void test_4() throws Exception{
        when(service.findById(any())).thenThrow(new Exception("Gruppe existiert nicht"));
        final UUID uuid = UUID.randomUUID();
        mockMvc.perform(MockMvcRequestBuilders.get("/gruppe").with(csrf()).param("id", uuid.toString()))
                        .andExpect(status().isNotFound());
    }

}

