package de.hhu.ausgabenverwaltung.adapters.database.implementation;

import de.hhu.ausgabenverwaltung.application.repo.GruppenRepository;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
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

    return gruppen.values().stream()
            .filter(gruppe -> gruppe.getMitglieder().contains(user))
            .collect(Collectors.toList());
    }


  @Override
  public List<Gruppe> getOffeneGruppenVonUser(User user) {
    List<Gruppe> offeneGruppen = new ArrayList<>();

    return getGruppenvonUser(user).stream()
            .filter(Gruppe::istOffen)
            .collect(Collectors.toList());
  }

  @Override
  public List<Gruppe> getGeschlosseneGruppenVonUser(User user) {
    List<Gruppe> offeneGruppen = new ArrayList<>();
    return getGruppenvonUser(user).stream()
            .filter(gruppe -> !gruppe.istOffen())
            .collect(Collectors.toList());
  }

  @Override
  public List<Gruppe> findAll() {
    return (gruppen.values().stream().toList());
  }

  @Override
  public Gruppe findById(UUID id) throws NoSuchElementException {
    return gruppen.values().stream()
            .filter(gruppe -> gruppe.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Gruppe existiert nicht"));
  }

}
