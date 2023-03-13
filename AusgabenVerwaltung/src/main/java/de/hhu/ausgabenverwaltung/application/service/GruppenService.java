package de.hhu.ausgabenverwaltung.application.service;

import de.hhu.ausgabenverwaltung.application.repo.GruppenRepository;
import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.Transaktion;
import de.hhu.ausgabenverwaltung.domain.User;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.naming.InvalidNameException;
import org.springframework.stereotype.Service;

@Service
public class GruppenService {
  private final GruppenRepository gruppenRepo;

  public GruppenService(GruppenRepository gruppenRepo) {
    this.gruppenRepo = gruppenRepo;
  }

  public Gruppe createGruppe(Set<String> mitglieder, String name) throws Exception {
    if (name.isEmpty()) {
      throw new InvalidNameException();
    }

    for (String mitglied : mitglieder) {
      nameIsValid(mitglied);
    }

    Gruppe gruppe = Gruppe.createGruppe(name,
        mitglieder.stream().map(User::new).collect(Collectors.toSet()));
    gruppenRepo.save(gruppe);

    return gruppe;
  }

  public Gruppe createGruppe(String ersteller, String name) throws Exception {//im Test schreiben, dass gruppenRepo.save von vreatGruppe aufgerufen wird
    return createGruppe(new HashSet<>(Set.of(ersteller)), name);
  }

  public void closeGruppe(UUID gruppenId) throws Exception {
    Gruppe gruppe = findById(gruppenId);
    gruppe.schliessen();
    gruppenRepo.save(gruppe);
  }

  public List<Gruppe> getGruppenVonUser(String githubHandle) {
    return gruppenRepo.getGruppenvonUser(new User(githubHandle));
  }

  public List<Gruppe> getGeschlossenGruppenVonUser(String githubHandle) {
    return gruppenRepo.getGeschlosseneGruppenVonUser(new User(githubHandle));
  }

  public List<Gruppe> getOffeneGruppenVonUser(String githubHandle) {
    return gruppenRepo.getOffeneGruppenVonUser(new User(githubHandle));
  }

  public Gruppe findById(UUID gruppenId) throws Exception { //Application Service
    return gruppenRepo.findById(gruppenId);
  }

  public boolean istOffen(UUID gruppenId) throws Exception {
    Gruppe gruppe = findById(gruppenId);
    return gruppe.istOffen();
  }

  public HashMap<User, BigDecimal> berechneSalden(UUID gruppenId) throws Exception {
    Gruppe gruppe = findById(gruppenId);
    return gruppe.berechneSalden(gruppe.alleSchuldenBerechnen());
  }

  public void addMitglied(UUID gruppenId, String githubHandle) throws Exception {
    Gruppe gruppe = findById(gruppenId);
    if (nameIsValid(githubHandle)) {
      gruppe.addMitglieder(new User(githubHandle));
    }
    gruppenRepo.save(gruppe);
  }

  public void addAusgabe(UUID gruppenId, Ausgabe ausgabe) throws Exception {
    Gruppe gruppe = findById(gruppenId);
    gruppe.addAusgabe(ausgabe);
    gruppe.berechneTransaktionen(berechneSalden(gruppenId));
    gruppenRepo.save(gruppe);
  }

  public boolean checkMitglied(UUID gruppenId, String githubHandle) throws Exception {
    Gruppe gruppe = findById(gruppenId);
    return gruppe.checkMitglied(githubHandle);
  }

  public Set<Transaktion> berechneTransaktionen(UUID gruppenId) throws Exception {
    Gruppe gruppe = findById(gruppenId);
    return gruppe.berechneTransaktionen(gruppe.berechneSalden(gruppe.alleSchuldenBerechnen()));
  }

  public Map<Gruppe, Set<Transaktion>> getBeteiligteTransaktionen(String githubHandle) {
    return gruppenRepo.getBeteiligteTransaktionen(new User(githubHandle));
  }

  public boolean nameIsValid(String githubHandle) throws Exception {
    String regex = "(^[a-zA-Z\\d](?:[a-zA-Z\\d]|-(?=[a-zA-Z\\d])){0,38}$)";
    if (githubHandle.matches(regex)) {
      return true;
    } else {
      throw new InvalidNameException("Invalid Github Name");
    }
  }

}
