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
  private final GruppenRepository gruppen;

  public GruppenService(GruppenRepository gruppen) {
    this.gruppen = gruppen;
  }

  public Gruppe gruppeErstellen(Set<String> mitglieder, String name) throws Exception {
    if (name.isEmpty()) {
      throw new InvalidNameException();
    }

    for (String mitglied : mitglieder) {
      nameIsValid(mitglied);
    }

    Gruppe gruppe = Gruppe.gruppeErstellen(name,
        mitglieder.stream().map(User::new).collect(Collectors.toSet()));
    gruppen.save(gruppe);

    return gruppe;
  }

  public Gruppe gruppeErstellen(String ersteller, String name) throws Exception {
    return gruppeErstellen(new HashSet<>(Set.of(ersteller)), name);
  }

  public void gruppeSchliessen(UUID gruppenId) throws Exception {
    Gruppe gruppe = findById(gruppenId);
    gruppe.schliessen();
  }

  public List<Gruppe> gruppenVonUser(String githubHandle) {
    return gruppen.vonUser(new User(githubHandle));
  }

  public List<Gruppe> geschlossenVonUser(String githubHandle) {
    return gruppen.geschlossenVonUser(new User(githubHandle));
  }

  public List<Gruppe> offenVonUser(String githubHandle) {
    return gruppen.offenVonUser(new User(githubHandle));
  }

  public Gruppe findById(UUID gruppenId) throws Exception { //Application Service
    return gruppen.findById(gruppenId);
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

  public Set<Transaktion> berechneTransaktionen(UUID gruppenId) throws Exception {
    Gruppe gruppe = findById(gruppenId);
    return gruppe.berechneTransaktionen(gruppe.berechneSalden(gruppe.alleSchuldenBerechnen()));
  }

  public Map<Gruppe, Set<Transaktion>> getBeteiligteTransaktionen(String githubHandle) {
    return gruppen.getBeteiligteTransaktionen(new User(githubHandle));
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
