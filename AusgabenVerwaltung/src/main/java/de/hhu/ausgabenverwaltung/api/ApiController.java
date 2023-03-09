package de.hhu.ausgabenverwaltung.api;

import de.hhu.ausgabenverwaltung.api.models.AusgleichModel;
import de.hhu.ausgabenverwaltung.api.models.AuslagenModel;
import de.hhu.ausgabenverwaltung.api.models.GruppeModel;
import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.service.GruppenService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    private final GruppenService service;

    public ApiController(GruppenService service) {
        this.service = service;
    }

    @GetMapping("/api")
    @ResponseBody
    public String hello() {
        return "Hello, API!";
    }

    @PostMapping("/api/gruppen")
    @ResponseBody
    public String gruppeErstellen(@RequestBody GruppeModel gruppeModel) {
        Gruppe gruppe = gruppeModel.toGruppe();

        return "Gruppe erstellen";
    }

    @GetMapping("/api/user/{githubHandle}/gruppen")
    @ResponseBody
    public String gruppenAnzeigen(@PathVariable String githubHandle) {
        return "Gruppen anzeigen";
    }

    @GetMapping("/api/gruppen/{gruppenId}")
    @ResponseBody
    public String gruppenInfo(@PathVariable UUID gruppenId) {
        return "Gruppen-Info";
    }

    @PostMapping("/api/gruppen/{gruppenId}/schliesen")
    @ResponseBody
    public String gruppeSchliessen(@PathVariable UUID gruppenId) {
        return "Gruppe schliessen";
    }

    @PostMapping("/api/gruppen/{gruppenId}/auslagen")
    @ResponseBody
    public String auslageEintragen(@RequestBody AuslagenModel auslagenModel) {
        Ausgabe ausgabe = auslagenModel.toAusgabe();

        return "Auslage eintragen";
    }

    @GetMapping("/api/gruppen/{gruppenId}/ausgleich")
    @ResponseBody
    public String ausgleichBerechnen(@PathVariable UUID gruppenId) {
        List<AusgleichModel> ausgleiche = new ArrayList<>();
        return "Ausgleich Berechnen";
    }

}
