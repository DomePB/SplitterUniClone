package de.hhu.ausgabenverwaltung.config.helper.security;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.hhu.ausgabenverwaltung.application.service.GruppenService;
import de.hhu.ausgabenverwaltung.config.WebSecurityConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@AutoConfigureMockMvc
@Import({WebSecurityConfiguration.class})
public class SecurityTests {


    @Autowired
    MockMvc mvc;

    @MockBean
    GruppenService gruppenService;


    @DisplayName("Anfrage auf index wird auf gitHub rediricted")
    @Test
    public void testRedirection() throws Exception {
        mvc.perform(get("/").with(csrf()).param("gruppenName", "gruppenName"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/oauth2/authorization/github"));
    }
}
