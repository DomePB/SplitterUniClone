package de.hhu.ausgabenverwaltung.adapters.controller.api;

import de.hhu.ausgabenverwaltung.adapters.controller.api.models.AusgleichModel;
import de.hhu.ausgabenverwaltung.adapters.controller.api.models.AuslagenModel;
import de.hhu.ausgabenverwaltung.adapters.controller.api.models.GruppeModel;
import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.Transaktion;
import de.hhu.ausgabenverwaltung.service.GruppenService;
import de.hhu.ausgabenverwaltung.domain.User;
import de.hhu.ausgabenverwaltung.application.service.GruppenService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final GruppenService gruppenService;

    public ApiController(GruppenService gruppenService) {
        this.gruppenService = gruppenService;
    }

    @GetMapping("/")
    @ResponseBody
    public String hello() {
        return "Hello, API!";
    }

    @PostMapping("/gruppen")
    public ResponseEntity<UUID> gruppeErstellen(@Valid @RequestBody GruppeModel gruppeModel) {
        try {
            Gruppe gruppe =
                    gruppenService.gruppeErstellen(gruppeModel.personen(), gruppeModel.name());
            return new ResponseEntity<>(gruppe.getId(), HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST); //not sure if correct status
        }

    }

    @GetMapping("/user/{githubHandle}/gruppen")
    public ResponseEntity<List<GruppeModel>> gruppenAnzeigen(@PathVariable String githubHandle) {
        List<Gruppe> gruppen = gruppenService.gruppenVonUser(githubHandle);
        List<GruppeModel> gruppeModels =
            gruppen.stream().map(GruppeModel::fromGruppe).collect(Collectors.toList());

        return new ResponseEntity<>(gruppeModels, HttpStatus.OK);
    }

    @GetMapping("/gruppen/{gruppenId}")
    public ResponseEntity<GruppeModel> gruppenInfo(@PathVariable String gruppenId) {
        try {
            Gruppe gruppe = gruppenService.findById(UUID.fromString(gruppenId));
            GruppeModel gruppeModel = GruppeModel.fromGruppe(gruppe);
            return new ResponseEntity<>(gruppeModel, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/gruppen/{gruppenId}/schliessen")
    public ResponseEntity<String> gruppeSchliessen(@PathVariable String gruppenId) {
        try {
            gruppenService.gruppeSchliessen(UUID.fromString(gruppenId));
            return new ResponseEntity<>("Gruppe geschlossen", HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("Gruppe nicht gefunden", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/gruppen/{gruppenId}/auslagen")
    public ResponseEntity<String> auslageEintragen(@PathVariable String gruppenId,
                                                   @Valid @RequestBody
                                                   AuslagenModel auslagenModel) {
        Ausgabe ausgabe = auslagenModel.toAusgabe();

        try {
            UUID id = UUID.fromString(gruppenId);

            if (!gruppenService.istOffen(id)) {
                return new ResponseEntity<>("Die Gruppe ist bereits geschlossen",
                    HttpStatus.CONFLICT);
            }

            gruppenService.addAusgabe(id, ausgabe);
        } catch (Exception ex) {
            return new ResponseEntity<>("Die Gruppe ist nicht vorhanden", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("Die Ausgabe wurde korrekt eingetragen", HttpStatus.CREATED);
    }

    @GetMapping("/gruppen/{gruppenId}/ausgleich")
    public ResponseEntity<List<AusgleichModel>> ausgleichBerechnen(@PathVariable String gruppenId) {
        try {
            UUID id = UUID.fromString(gruppenId);
            Set<Transaktion> transaktionen = gruppenService.berechneTransaktionen(id);
            List<AusgleichModel> ausgleichZahlungen =
                transaktionen.stream().map(AusgleichModel::fromTransaktion)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(ausgleichZahlungen, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

}
