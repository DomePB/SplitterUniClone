package de.hhu.ausgabenverwaltung.service;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ApiService {

  private final GruppenRepository gruppen;

  public ApiService(GruppenRepository gruppen) {
    this.gruppen = gruppen;
  }

  public void gruppeHinzufuegen(Gruppe gruppe) {
    gruppen.add(gruppe);
  }

  public List<Gruppe> gruppenVonUser(String githubHandle) {
    return gruppen.vonUser(new User(githubHandle));
  }

}
