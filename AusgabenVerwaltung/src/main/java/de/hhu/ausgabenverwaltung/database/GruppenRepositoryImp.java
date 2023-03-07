package de.hhu.ausgabenverwaltung.database;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.Transaktion;
import de.hhu.ausgabenverwaltung.domain.User;
import de.hhu.ausgabenverwaltung.service.GruppenRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

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
    public Map<Gruppe, Set<Transaktion>> getBeteiligteTransaktionen(User user) {
        Map<Gruppe, Set<Transaktion>> userTransaktinen = new HashMap<>();
        for (Gruppe gruppe : gruppen) {
            Set<Transaktion> temp = new HashSet<>();
            if (gruppe.getMitglieder().contains(user)) {
                Set<Transaktion> groupeTransaktionen = gruppe.getTransaktionen();
                for (Transaktion t : groupeTransaktionen) {
                    if (t.sender().equals(user) || t.empfaenger().equals(user)) {
                        temp.add(t);
                    }
                }
                userTransaktinen.put(gruppe, temp);
            }
        }
        return userTransaktinen;
    }
}
