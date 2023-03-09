package de.hhu.ausgabenverwaltung.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import de.hhu.ausgabenverwaltung.helper.WithMockOAuth2User;
import de.hhu.ausgabenverwaltung.service.GruppenService;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
    void test_1() throws Exception {
        User user = new User("githubHandle");
        Gruppe gruppe = Gruppe.gruppeErstellen("gruppename", user);
        when(service.gruppeErstellen("githubHandle", "")).thenReturn(gruppe);

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
                .andExpect(model().attribute("user", "JoeSchmoe"));
    }

    @Test
    @WithMockOAuth2User(login = "JoeSchmoe")
    @DisplayName("Exception werfen, wenn Gruppe nicht gefundnen wird.")
    void test_4() throws Exception {
        when(service.findById(any())).thenThrow(new Exception("Gruppe existiert nicht"));
        final UUID uuid = UUID.randomUUID();
        mockMvc.perform(MockMvcRequestBuilders.get("/gruppe").with(csrf()).param("id", uuid.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockOAuth2User(login = "JoeSchmoe")
    @DisplayName("Gruppenübersicht anzeigen, wenn die Gruppe existiert")
    void test_5() throws Exception {
        when(service.findById(any())).thenReturn(Gruppe.gruppeErstellen("gruppenName", new User("JoeSchmoe")));
        final UUID uuid = UUID.randomUUID();
        mockMvc.perform(MockMvcRequestBuilders.get("/gruppe").with(csrf()).param("id", uuid.toString()))
                .andExpect(view().name("gruppen-uebersicht"))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockOAuth2User(login = "JoeSchmoe")
    @DisplayName("Bei Ausgabe hinzufügen wird man zu gruppen-uebersicht redirected")
    void test_6() throws Exception {
        when(service.findById(any())).thenReturn(Gruppe.gruppeErstellen("gruppenName", new User("JoeSchmoe")));
        final UUID uuid = UUID.randomUUID();
        mockMvc.perform(MockMvcRequestBuilders.post("/gruppe/ausgaben").with(csrf()).param("id", uuid.toString()))
                .andExpect(status().isFound());

    }

    @Test
    @WithMockOAuth2User(login = "JoeSchmoe")
    @DisplayName("Bei Mitglieder hinzufügen wird man zu gruppen-uebersicht redirected")
    void test_7() throws Exception {
        when(service.findById(any())).thenReturn(Gruppe.gruppeErstellen("gruppenName", new User("JoeSchmoe")));
        final UUID uuid = UUID.randomUUID();
        mockMvc.perform(MockMvcRequestBuilders.post("/gruppe/mitglieder")
                        .with(csrf())
                        .param("id", uuid.toString())
                        .param("mitgliedName", "test"))
                .andExpect(status().isFound());
    }

    @Test
    @WithMockOAuth2User(login = "JoeSchmoe")
    @DisplayName("Gruppe schliessen, prüft ob das redirect korrekt ist.")
    void test_8() throws Exception {
        User user = new User("githubHandle");
        Gruppe gruppe = Gruppe.gruppeErstellen("gruppename", user);
        when(service.gruppeErstellen("githubHandle", "")).thenReturn(gruppe);

        mockMvc.perform(post("/gruppe/schliessen")
                        .with(csrf())
                        .param("id", gruppe.getId().toString()))
                .andExpect(view().name("redirect:/gruppe"))
                .andExpect(status().isFound());
    }

    @Test
    @WithMockOAuth2User(login = "JoeSchmoe")
    @DisplayName("Prüfe ob beim Gruppe schliessen man zu /gruppe redirected wird")
    void test_9() throws Exception {
        User user = new User("githubHandle");
        Gruppe gruppe = Gruppe.gruppeErstellen("gruppename", user);
        when(service.gruppeErstellen("githubHandle", "")).thenReturn(gruppe);

        mockMvc.perform(post("/gruppe/schliessen")
                        .with(csrf())
                        .param("id", gruppe.getId().toString()))
                .andExpect(view().name("redirect:/gruppe"))
                .andExpect(status().isFound());
    }

}

