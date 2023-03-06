package de.hhu.ausgabenverwaltung.web;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import de.hhu.ausgabenverwaltung.service.GruppenListe;
import de.hhu.ausgabenverwaltung.service.GruppenService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WebController {

    private final GruppenService service;

    public WebController(GruppenService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index(Model model, OAuth2AuthenticationToken token) {
        User user = new User(token.getPrincipal().getAttribute("login"));
        //service.gruppeErstellen(user,"Testgruppe");
        //service.gruppeErstellen(new User("test", "test"), "gruppe2");
        model.addAttribute("user", user);
        model.addAttribute("offeneGruppen", service.getGruppenListe().offenVonUser(user));
        model.addAttribute("geschlosseneGruppen", service.getGruppenListe().geschlossenVonUser(user));
        return "start";
    }

    @PostMapping("/")
    public String gruppeErstellen(@RequestParam(name = "gruppenName") String gruppenName, OAuth2AuthenticationToken token) {
        User user = new User(token.getPrincipal().getAttribute("login"));
        service.gruppeErstellen(user, gruppenName);
        return "redirect:/";
    }

    @GetMapping("/gruppe")
    public String gruppenUebersicht(@RequestParam Long id, Model model) {
        try {
            Gruppe gruppe = service.getGruppenListe().findById(id);
            model.addAttribute("gruppe", gruppe);
            return "gruppen-uebersicht";
        } catch (Exception e) {
            return "redirect:/";
        }
    }
}
