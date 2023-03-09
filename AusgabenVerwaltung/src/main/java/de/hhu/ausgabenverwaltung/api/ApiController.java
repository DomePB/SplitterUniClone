package de.hhu.ausgabenverwaltung.api;

import de.hhu.ausgabenverwaltung.api.models.AusgleichModel;
import de.hhu.ausgabenverwaltung.api.models.AuslagenModel;
import de.hhu.ausgabenverwaltung.api.models.GruppeModel;
import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.Transaktion;
import de.hhu.ausgabenverwaltung.service.ApiService;
import de.hhu.ausgabenverwaltung.service.GruppenService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @Deprecated
    private final ApiService service; // TODO: entfernen

    private final GruppenService gruppenService;

    public ApiController(ApiService service, GruppenService gruppenService) {
        this.service = service;
        this.gruppenService = gruppenService;
    }

    @GetMapping("/api")
    @ResponseBody
    public String hello() {
        return "Hello, API!";
    }

    @GetMapping("/api/test")
    @ResponseBody
    public String hello2() {
        return "Hello, API!";
    }

    @PostMapping("/api/gruppen")
    public ResponseEntity<UUID> gruppeErstellen(@RequestBody GruppeModel gruppeModel) {
        Gruppe gruppe = gruppeModel.toGruppe();
        service.gruppeHinzufuegen(gruppe);

        return new ResponseEntity<>(gruppe.getId(), HttpStatus.CREATED);
    }

    @GetMapping("/api/user/{githubHandle}/gruppen")
    public ResponseEntity<List<GruppeModel>> gruppenAnzeigen(@PathVariable String githubHandle) {
        List<Gruppe> gruppen = service.gruppenVonUser(githubHandle); // TODO: Falsche methode
        List<GruppeModel> gruppeModels =
            gruppen.stream().map(GruppeModel::fromGruppe).collect(Collectors.toList());

        return new ResponseEntity<>(gruppeModels, HttpStatus.OK);
    }

    @GetMapping("/api/gruppen/{gruppenId}")
    public ResponseEntity<GruppeModel> gruppenInfo(@PathVariable UUID gruppenId) {
        try {
            Gruppe gruppe = service.findById(gruppenId);
            GruppeModel gruppeModel = GruppeModel.fromGruppe(gruppe);
            return new ResponseEntity<>(gruppeModel, HttpStatus.OK);

        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/api/gruppen/{gruppenId}/schliessen")
    public ResponseEntity<String> gruppeSchliessen(@PathVariable UUID gruppenId) {
        try {
            service.gruppeSchliessen(gruppenId);
            return new ResponseEntity<>("Gruppe geschlossen", HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("Gruppe nicht gefunden", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/api/gruppen/{gruppenId}/auslagen")
    public ResponseEntity<String> auslageEintragen(@PathVariable UUID gruppenId,
                                                   @RequestBody AuslagenModel auslagenModel) {
        Ausgabe ausgabe = auslagenModel.toAusgabe();

        try {
            if (!gruppenService.istOffen(gruppenId)) {
                return new ResponseEntity<>("Die Gruppe ist bereits geschlossen",
                    HttpStatus.CONFLICT);
            }

            gruppenService.addAusgabe(gruppenId, ausgabe);
        } catch (Exception ex) {
            return new ResponseEntity<>("Die Gruppe ist nicht vorhanden", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("Die Ausgabe wurde korrekt eingetragen", HttpStatus.CREATED);
    }

    @GetMapping("/api/gruppen/{gruppenId}/ausgleich")
    public ResponseEntity<List<AusgleichModel>> ausgleichBerechnen(@PathVariable UUID gruppenId) {
        try {
            Gruppe gruppe = gruppenService.findById(gruppenId);
            Set<Transaktion> transaktionen = gruppe.getTransaktionen();
            List<AusgleichModel> ausgleichZahlungen =
                transaktionen.stream().map(AusgleichModel::fromTransaktion)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(ausgleichZahlungen, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

}
