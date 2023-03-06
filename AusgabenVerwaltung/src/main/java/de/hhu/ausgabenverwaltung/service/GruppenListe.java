package de.hhu.ausgabenverwaltung.service;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

public class GruppenListe implements GruppenRepository {

    List<Gruppe> gruppen;

    public GruppenListe() {
        this.gruppen = new ArrayList<>();
    }

    public GruppenListe(List<Gruppe> gruppen) {
        this.gruppen = gruppen;
    }

    @Override
    public List<Gruppe> vonUser(User user) {
        List<Gruppe> gruppenVonUser = new ArrayList<>();

        for (Gruppe gruppe : gruppen) {
            if (gruppe.getMitglieder().contains(user)) {
                gruppenVonUser.add(gruppe);
            }
        }
        return gruppenVonUser;
    }

    @Override
    public List<Gruppe> offenVonUser(User user) {
        List<Gruppe> offeneGruppen = new ArrayList<>();

        for (Gruppe gruppe : vonUser(user)) {
            if (gruppe.istOffen()) {
                offeneGruppen.add(gruppe);
            }
        }
        return offeneGruppen;
    }

    @Override
    public List<Gruppe> geschlossenVonUser(User user) {
        List<Gruppe> offeneGruppen = new ArrayList<>();

        for (Gruppe gruppe : vonUser(user)) {
            if (!gruppe.istOffen()) {
                offeneGruppen.add(gruppe);
            }
        }
        return offeneGruppen;
    }

    @Override
    public List<Gruppe> findAll() {
        return gruppen;
    }

    @Override
    public Gruppe findById(Long id) throws Exception {
        for (Gruppe gruppe : gruppen) {
            if (gruppe.getId().equals(id)) {
                return gruppe;
            }
        }
        throw new Exception("Gruppe existiert nicht");
    }
}
