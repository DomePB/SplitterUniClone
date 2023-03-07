package de.hhu.ausgabenverwaltung.service;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.Transaktion;
import de.hhu.ausgabenverwaltung.domain.User;

import java.math.BigDecimal;
import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class GruppenService {
    private final GruppenRepository gruppen;

    public GruppenService(GruppenRepository gruppen){
        this.gruppen =  gruppen;
    }

    public List<Gruppe> getGruppen() {
        return gruppen.findAll();
    }


    public Gruppe gruppeErstellen(User ersteller, String name) {
        Gruppe gruppe = new Gruppe(name, new ArrayList<>(), new ArrayList<>(List.of(ersteller)),
                new HashSet<>(), true,UUID.randomUUID());
        gruppen.findAll().add(gruppe);
        return gruppe;
    } //Application Service

    public void gruppeSchliessen(Gruppe gruppe) {
        gruppe.schliessen();
    }

    public List<Gruppe> geschlossenVonUser(User user) {
        return gruppen.geschlossenVonUser(user);
    } //Application service

    public List<Gruppe> offenVonUser(User user) {
        return gruppen.offenVonUser(user);
    } //Application service

    public Gruppe findById(UUID gruppenId) throws Exception { //Application Service
        return gruppen.findById(gruppenId);
    }
}
