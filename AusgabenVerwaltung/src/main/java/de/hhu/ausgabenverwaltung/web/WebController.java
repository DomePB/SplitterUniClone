package de.hhu.ausgabenverwaltung.web;

import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import de.hhu.ausgabenverwaltung.service.GruppenService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

    public String index(Model model,@AuthenticationPrincipal OAuth2User token) {
        User user = new User(token.getAttribute("login"));
        User user2= new User("myUser");
        Gruppe g = service.gruppeErstellen(new User("test"), "gruppe2");
        g.addMitglieder(user2);
        g.addMitglieder(new User(token.getAttribute("login")));
        g.ausgabeHinzufuegen(new Ausgabe("test","test",BigDecimal.TEN,user,List.of(user2)));

        model.addAttribute("user", user);
        model.addAttribute("offeneGruppen", service.offenVonUser(user));
        model.addAttribute("geschlosseneGruppen", service.geschlossenVonUser(user));
        return "start";
    }

    @PostMapping("/")
    public String gruppeErstellen(@RequestParam(name = "gruppenName") String gruppenName,@AuthenticationPrincipal OAuth2User token) {
        User user = new User(token.getAttribute("login"));
        service.gruppeErstellen(user, gruppenName);
        return "redirect:/";
    }

    @GetMapping("/gruppe")
    public String gruppenUebersicht(@RequestParam UUID id, Model model) {
        try {
            Gruppe gruppe = service.findById(id);
            HashMap<User, BigDecimal> salden =
                    gruppe.berechneSalden(gruppe.alleSchuldenBerechnen());
            model.addAttribute("gruppe", gruppe);
            model.addAttribute("salden",salden);

            return "gruppen-uebersicht";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @PostMapping("/gruppe/ausgaben")
    public String ausgabeHinzufuegen(@RequestParam UUID id, String ausgabeName, String ausgabeBeschreibung, BigDecimal ausgabeBetrag,
                                     String bezahltVon, ArrayList<String> beteiligte, RedirectAttributes attrs) {
        try {
            Gruppe gruppe = service.findById(id);
            List<User> users = beteiligte.stream().map(User::new).collect(Collectors.toList());
            gruppe.ausgabeHinzufuegen(new Ausgabe(ausgabeName, ausgabeBeschreibung, ausgabeBetrag,
                    new User(bezahltVon), users));
            attrs.addAttribute("id", gruppe.getId());

            return "redirect:/gruppe";
        } catch (Exception e) {
            return "redirect:/gruppe";
        }
    }
    @PostMapping("/gruppe/Mitglieder")
    public String userHinzufuegen(@RequestParam UUID id,
                                     @RequestParam(name = "MitgliedName") String name,
                                     RedirectAttributes attrs) {
        try {
            Gruppe gruppe = service.findById(id);
            gruppe.addMitglieder(new User(name));
            attrs.addAttribute("id", gruppe.getId());
            return "redirect:/gruppe";
        } catch (Exception e) {
            return "redirect:/gruppe";
        }
    }

}
