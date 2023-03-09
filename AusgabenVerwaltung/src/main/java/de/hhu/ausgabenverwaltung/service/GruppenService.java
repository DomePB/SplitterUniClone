package de.hhu.ausgabenverwaltung.service;

import de.hhu.ausgabenverwaltung.domain.Ausgabe;
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


    public Gruppe gruppeErstellen(String ersteller, String name) {
        Gruppe gruppe = Gruppe.gruppeErstellen(name,new User(ersteller));
        gruppen.findAll().add(gruppe);
        return gruppe;
    } //Application Service

    public void gruppeSchliessen(UUID gruppenId) throws Exception {
        Gruppe gruppe = findById(gruppenId);
        gruppe.schliessen();
    }

    public List<Gruppe> geschlossenVonUser(String githubHandle) {
        return gruppen.geschlossenVonUser(new User(githubHandle));
    } //Application service

    public List<Gruppe> offenVonUser(String githubHandle) {
        return gruppen.offenVonUser(new User(githubHandle));
    } //Application service

    public Gruppe findById(UUID gruppenId) throws Exception { //Application Service
        return gruppen.findById(gruppenId);
    }
    public HashMap<User, BigDecimal> berechneSalden(UUID gruppenId) throws Exception {
        Gruppe gruppe = findById(gruppenId);
        return gruppe.berechneSalden(gruppe.alleSchuldenBerechnen());
    }
    public void addMitglied(UUID gruppenId, String githubHandle) throws Exception {
        Gruppe gruppe = findById(gruppenId);
        gruppe.addMitglieder(new User(githubHandle));
    }
    public void addAusgabe(UUID gruppenId, Ausgabe ausgabe) throws Exception {
        Gruppe gruppe = findById(gruppenId);
        gruppe.ausgabeHinzufuegen(ausgabe);
        gruppe.berechneTransaktionen(berechneSalden(gruppenId));
    }

    public boolean checkMitglied(UUID gruppenId, String githubHandle) throws Exception {
        Gruppe gruppe = findById(gruppenId);
        return gruppe.checkMitglied(githubHandle);
    }

}
