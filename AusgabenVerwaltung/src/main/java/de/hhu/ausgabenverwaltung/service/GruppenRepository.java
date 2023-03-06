package de.hhu.ausgabenverwaltung.service;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;


import java.util.List;
import java.util.UUID;


public interface GruppenRepository {

    List<Gruppe> vonUser(User user);

    List<Gruppe> offenVonUser(User user);

    List<Gruppe> geschlossenVonUser(User user);

    List<Gruppe> findAll();

    Gruppe findById(UUID id) throws Exception;
}
