package de.hhu.ausgabenverwaltung.web;

import static org.springframework.http.HttpStatus.I_AM_A_TEAPOT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.Transaktion;
import de.hhu.ausgabenverwaltung.domain.User;
import de.hhu.ausgabenverwaltung.service.GruppenService;
import de.hhu.ausgabenverwaltung.web.models.AusgabeForm;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
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
    public String index(Model model, @AuthenticationPrincipal OAuth2User token) {
        Map<Gruppe, Set<Transaktion>> beteiligteTransaktionen =
                service.getBeteiligteTransaktionen(token.getAttribute("login"));
        model.addAttribute("beteiligteTransaktionen", beteiligteTransaktionen);
        model.addAttribute("user", token.getAttribute("login"));
        model.addAttribute("offeneGruppen", service.offenVonUser(token.getAttribute("login")));
        model.addAttribute("geschlosseneGruppen", service.geschlossenVonUser(token.getAttribute("login")));
        return "start";
    }

    @PostMapping("/")
    public String gruppeErstellen(String gruppenName, @AuthenticationPrincipal OAuth2User token) {
        String githubHandle = token.getAttribute("login");
        try {
            service.gruppeErstellen(githubHandle, gruppenName);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,e.toString());
        }
        return "redirect:/";
    }

    @GetMapping("/gruppe")
    public String gruppenUebersicht(@RequestParam UUID id, Model model,@AuthenticationPrincipal OAuth2User token) {
        User user = new User(token.getAttribute("login"));
        Gruppe gruppe;
        HashMap<User, BigDecimal> salden;
        Set<Transaktion> transaktionen;
        try {
            gruppe = service.findById(id);
            if(!service.checkMitglied(id,token.getAttribute("login"))){
                throw new ResponseStatusException(UNAUTHORIZED, "Nicht Authoriziert auf die Gruppe zuzugreifen");
            }
            salden = service.berechneSalden(id);
            transaktionen = service.berechneTransaktionen(id);
        } catch (Exception e) {
            throw new ResponseStatusException(NOT_FOUND, "Gruppe nicht gefunden");
        }
        model.addAttribute("transaktionen", transaktionen);
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
            attrs.addAttribute("id", gruppe.getId());
            Ausgabe ausgabe1 = new Ausgabe(ausgabe.ausgabeName(), ausgabe.ausgabeBeschreibung(),
                    ausgabe.ausgabeBetrag(), new User(ausgabe.bezahltVon()),
                    ausgabe.beteiligte().stream().map(User::new).collect(Collectors.toList()));
            service.addAusgabe(id, ausgabe1);
            return "redirect:/gruppe";
        } catch (Exception e) {
            return "redirect:/gruppe";
        }
    }

    @PostMapping("/gruppe/mitglieder")
    public String userHinzufuegen(@RequestParam UUID id,
                                  @RequestParam(name = "mitgliedName") String name,
                                  RedirectAttributes attrs) {
        try {
            Gruppe gruppe = service.findById(id);
            service.addMitglied(id, name);
            attrs.addAttribute("id", gruppe.getId());
            return "redirect:/gruppe";
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,e.toString());
        }
    }

    @PostMapping("/gruppe/schliessen")
    public String gruppeSchliessen(@RequestParam UUID id, RedirectAttributes attrs) {
        attrs.addAttribute("id", id);

        try {
            Gruppe gruppe = service.findById(id);
            service.gruppeSchliessen(id);

            return "redirect:/gruppe";
        } catch (Exception e) {
            return "redirect:/gruppe";
        }
    }

}