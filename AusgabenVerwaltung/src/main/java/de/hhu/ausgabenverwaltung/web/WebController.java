package de.hhu.ausgabenverwaltung.web;

import de.hhu.ausgabenverwaltung.domain.User;
import de.hhu.ausgabenverwaltung.service.GruppenService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

@Controller
public class WebController {

    private final GruppenService service;

    public WebController(GruppenService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index(Model model, OAuth2AuthenticationToken token )
    {
        User user = new User( token.getPrincipal().getAttribute("login"), token.getName());
        service.gruppeErstellen(user,"Testgruppe");
        service.gruppeErstellen(new User("test", "test"), "gruppe2");
        model.addAttribute("user", user);
        model.addAttribute("gruppenListe", service.gruppenVonUser(user));
        return "index";
    }

}
