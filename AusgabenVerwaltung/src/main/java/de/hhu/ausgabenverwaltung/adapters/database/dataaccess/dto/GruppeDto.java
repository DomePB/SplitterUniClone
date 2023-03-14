package de.hhu.ausgabenverwaltung.adapters.database.dataaccess.dto;

import de.hhu.ausgabenverwaltung.domain.Ausgabe;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.UUID;

@Table("GRUPPE")
public record GruppeDto (@Id UUID uuid,
                         String name,
                         boolean offen,
                         List<Ausgabe> ausgabe,
                         List<String> mitglieder
){
}
