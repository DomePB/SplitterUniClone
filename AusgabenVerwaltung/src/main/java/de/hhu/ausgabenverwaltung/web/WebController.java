package de.hhu.ausgabenverwaltung.web;

import de.hhu.ausgabenverwaltung.service.GruppenService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WebController {

    private final GruppenService service;

    public WebController(GruppenService service) {
        this.service = service;
    }

    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "Hello, World!";
    }

}
