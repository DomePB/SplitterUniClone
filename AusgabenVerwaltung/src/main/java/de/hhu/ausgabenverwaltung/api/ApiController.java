package de.hhu.ausgabenverwaltung.api;

import de.hhu.ausgabenverwaltung.service.GruppenService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    private final GruppenService service;


    public ApiController(GruppenService service) {
        this.service = service;
    }

}
