package de.hhu.ausgabenverwaltung.service;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;

import java.util.ArrayList;
import java.util.List;

public class GruppenListe {

    List<Gruppe> gruppen = new ArrayList<>();

    public GruppenListe(){
        this.gruppen=new ArrayList<>();
    }

    public GruppenListe(List<Gruppe> gruppen) {
        this.gruppen = gruppen;
    }

    public GruppenListe vonUser(User user){
        List<Gruppe> gruppenVonUser = new ArrayList<>();

        for (Gruppe gruppe : gruppen) {
            if (gruppe.getMitglieder().contains(user)) {
                gruppenVonUser.add(gruppe);
            }
        }
        return new GruppenListe(gruppenVonUser);
    }

    public GruppenListe offen(){

        List<Gruppe> offeneGruppen = new ArrayList<>();

        for (Gruppe gruppe : gruppen) {
            if (gruppe.istOffen()) {
                offeneGruppen.add(gruppe);
            }
        }
        return new GruppenListe(offeneGruppen);
    }

    public GruppenListe geschlossen(){

        List<Gruppe> geschlosseneGruppen = new ArrayList<>();

        for (Gruppe gruppe : gruppen) {
            if (!gruppe.istOffen()) {
                geschlosseneGruppen.add(gruppe);
            }
        }
        return new GruppenListe(geschlosseneGruppen);
    }

    public List<Gruppe> getList(){
        return gruppen;
    }
}
