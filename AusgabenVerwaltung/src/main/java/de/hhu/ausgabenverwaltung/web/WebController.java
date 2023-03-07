package de.hhu.ausgabenverwaltung.web;

import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import de.hhu.ausgabenverwaltung.service.GruppenService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("offeneGruppen", service.offenVonUser(user));
        model.addAttribute("geschlosseneGruppen", service.geschlossenVonUser(user));
        return "start";
    }

    @PostMapping("/")
    public String gruppeErstellen(@RequestParam(name = "gruppenName") String gruppenName, OAuth2AuthenticationToken token) {
        User user = new User(token.getPrincipal().getAttribute("login"));
        service.gruppeErstellen(user, gruppenName);
        return "redirect:/";
    }

    @GetMapping("/gruppe")
    public String gruppenUebersicht(@RequestParam UUID id, Model model) {
        try {
            Gruppe gruppe = service.findById(id);
            model.addAttribute("gruppe", gruppe);
            return "gruppen-uebersicht";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @PostMapping("/gruppe/ausgaben")
    public String ausgabeHinzufuegen(@RequestParam UUID id,
                                     @RequestParam(name = "ausgabeName") String name,
                                     RedirectAttributes attrs) {
        try {
            Gruppe gruppe = service.findById(id);
            gruppe.ausgabeHinzufuegen(new Ausgabe(name, "", BigDecimal.ONE, new User("guthub"),
                    List.of()));
            attrs.addAttribute("id", gruppe.getId());

            return "redirect:/gruppe";
        } catch (Exception e) {
            return "redirect:/gruppe";
        }
    }
}
