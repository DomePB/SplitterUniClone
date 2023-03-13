package de.hhu.ausgabenverwaltung.application.repo;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.Transaktion;
import de.hhu.ausgabenverwaltung.domain.User;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public interface GruppenRepository {

  void save(Gruppe gruppe);

  List<Gruppe> getGruppenvonUser(User user);

  List<Gruppe> getOffeneGruppenVonUser(User user);

  List<Gruppe> getGeschlosseneGruppenVonUser(User user);

  List<Gruppe> findAll();

  Gruppe findById(UUID id) throws Exception;

  Map<Gruppe, Set<Transaktion>> getBeteiligteTransaktionen(User user);
}
