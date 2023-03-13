package de.hhu.ausgabenverwaltung.adapters.database.implementation;

import de.hhu.ausgabenverwaltung.application.repo.GruppenRepository;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.Transaktion;
import de.hhu.ausgabenverwaltung.domain.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class GruppenRepositoryImp implements GruppenRepository {

  Map<UUID, Gruppe> gruppen;

  public GruppenRepositoryImp() {
    this.gruppen = new HashMap<>();
  }

  @Override
  public void save(Gruppe gruppe) {
  gruppen.put(gruppe.getId(), gruppe);
  }

  @Override
  public List<Gruppe> getGruppenvonUser(User user) {
    List<Gruppe> gruppenVonUser = new ArrayList<>();

    for (Gruppe gruppe : gruppen.values()) {
      if (gruppe.getMitglieder().contains(user)) {
        gruppenVonUser.add(gruppe);
      }
    }
    return gruppenVonUser;
  }

  @Override
  public List<Gruppe> getOffeneGruppenVonUser(User user) {
    List<Gruppe> offeneGruppen = new ArrayList<>();

    for (Gruppe gruppe : getGruppenvonUser(user)) {
      if (gruppe.istOffen()) {
        offeneGruppen.add(gruppe);
      }
    }
    return offeneGruppen;
  }

  @Override
  public List<Gruppe> getGeschlosseneGruppenVonUser(User user) {
    List<Gruppe> offeneGruppen = new ArrayList<>();

    for (Gruppe gruppe : getGruppenvonUser(user)) {
      if (!gruppe.istOffen()) {
        offeneGruppen.add(gruppe);
      }
    }
    return offeneGruppen;
  }

  @Override
  public List<Gruppe> findAll() {
    return (gruppen.values().stream().toList());
  }

  @Override
  public Gruppe findById(UUID id) throws NoSuchElementException {
      if (gruppen.containsKey(id)){
        return gruppen.get(id);
      }
    throw new NoSuchElementException("Gruppe existiert nicht");
  }

  public Map<Gruppe, Set<Transaktion>> getBeteiligteTransaktionen(User user) {
    Map<Gruppe, Set<Transaktion>> userTransaktinen = new HashMap<>();
    for (Gruppe gruppe : gruppen.values()) {
      Set<Transaktion> temp = new HashSet<>();
      if (gruppe.getMitglieder().contains(user)) {
        Set<Transaktion> groupeTransaktionen =
            gruppe.berechneTransaktionen(
                gruppe.berechneSalden(gruppe.alleSchuldenBerechnen()));
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
