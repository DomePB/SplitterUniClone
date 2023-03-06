package de.hhu.ausgabenverwaltung.database;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import de.hhu.ausgabenverwaltung.service.GruppenRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class GruppenRepositoryImp implements GruppenRepository {

    List<Gruppe> gruppen;

    public GruppenRepositoryImp() {
        this.gruppen = new ArrayList<>();
    }

    public GruppenRepositoryImp(List<Gruppe> gruppen) {
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
    public Gruppe findById(UUID id) throws Exception {
        for (Gruppe gruppe : gruppen) {
            if (gruppe.getId().equals(id)) {
                return gruppe;
            }
        }
        throw new Exception("Gruppe existiert nicht");
    }
}
