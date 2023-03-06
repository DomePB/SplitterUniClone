package de.hhu.ausgabenverwaltung.database.dto;

import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import de.hhu.ausgabenverwaltung.domain.Transaktion;
import de.hhu.ausgabenverwaltung.domain.User;
import java.util.List;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("Gruppe")
public record GruppeDto(@Id Long id, String name, List<Ausgabe> ausgaben, List<User> mitglieder, boolean offen, Set<Transaktion> transaktion) {
}
