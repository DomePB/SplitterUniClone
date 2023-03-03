package de.hhu.ausgabenverwaltung.web;

import de.hhu.ausgabenverwaltung.domain.User;
import de.hhu.ausgabenverwaltung.service.GruppenService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WebController {

    private final GruppenService service;

    public WebController(GruppenService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index(Model model)
    {
        service.gruppeErstellen(new User("test","test"),"Testgruppe");
        model.addAttribute("gruppenListe", service.getGruppen());
        return "index";
    }

}
