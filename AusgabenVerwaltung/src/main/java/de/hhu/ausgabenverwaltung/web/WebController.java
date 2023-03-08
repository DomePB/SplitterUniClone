package de.hhu.ausgabenverwaltung.web;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import de.hhu.ausgabenverwaltung.service.GruppenService;
import de.hhu.ausgabenverwaltung.web.models.AusgabeForm;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
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
        //  User user2= new User("myUser");
        //   Gruppe g = service.gruppeErstellen(new User("test"), "gruppe2");
        // g.addMitglieder(user2);
        //  g.addMitglieder(new User(token.getAttribute("login")));
        //  g.ausgabeHinzufuegen(new Ausgabe("test","test",BigDecimal.TEN,user,List.of(user2)));

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
    public String gruppenUebersicht(@RequestParam UUID id, Model model,
                                    @AuthenticationPrincipal OAuth2User token) {
        User user = new User(token.getAttribute("login"));
        Gruppe gruppe;
        HashMap<User, BigDecimal> salden;
        try {
            gruppe = service.findById(id);
            salden =
                    service.berechneSalden(gruppe);
            service.berechneTransaktionen(gruppe);
        } catch (Exception e) {
            throw new ResponseStatusException(NOT_FOUND, "Gruppe nicht gefunden");
        }


        model.addAttribute("gruppe", gruppe);
        model.addAttribute("salden", salden);
        model.addAttribute("ausgabe", AusgabeForm.defaultAusgabe());
        model.addAttribute("user", user);

        return "gruppen-uebersicht";
    }

    @PostMapping("/gruppe/ausgaben")
    public String ausgabeHinzufuegen(@RequestParam UUID id, @Validated
    AusgabeForm ausgabe, RedirectAttributes attrs) {
        try {
            Gruppe gruppe = service.findById(id);
            //    List<User> users = beteiligte.stream().map(User::new).collect(Collectors.toList());
            //   gruppe.ausgabeHinzufuegen(new Ausgabe(ausgabeName, ausgabeBeschreibung, ausgabeBetrag,
            //      new User(bezahltVon), users));
            attrs.addAttribute("id", gruppe.getId());
            Ausgabe ausgabe1 = new Ausgabe(ausgabe.ausgabeName(), ausgabe.ausgabeBeschreibung(),
                    ausgabe.ausgabeBetrag(), new User(ausgabe.bezahltVon()),
                    ausgabe.beteiligte().stream().map(User::new).collect(Collectors.toList()));
            service.addAusgabe(gruppe, ausgabe1);
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
            service.addMitglied(gruppe, new User(name));
            attrs.addAttribute("id", gruppe.getId());
            return "redirect:/gruppe";
        } catch (Exception e) {
            return "redirect:/gruppe";
        }
    }

    @PostMapping("/gruppe/schliessen")
    public String ausgabeHinzufuegen(@RequestParam UUID id, RedirectAttributes attrs) {
        attrs.addAttribute("id", id);

        try {
            Gruppe gruppe = service.findById(id);
            service.gruppeSchliessen(gruppe);

            return "redirect:/gruppe";
        } catch (Exception e) {
            return "redirect:/gruppe";
        }
    }

}