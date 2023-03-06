package de.hhu.ausgabenverwaltung.service.input;

import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import de.hhu.ausgabenverwaltung.domain.Transaktion;
import de.hhu.ausgabenverwaltung.domain.User;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record GruppeInput(UUID id,
                          String name,
                          List<Ausgabe> ausgaben,
                          List<User> mitglieder,
                          boolean offen,
                          Set<Transaktion> transaktionen) {
}
