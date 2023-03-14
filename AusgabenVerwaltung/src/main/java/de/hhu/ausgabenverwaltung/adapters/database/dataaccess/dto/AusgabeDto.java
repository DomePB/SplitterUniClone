package de.hhu.ausgabenverwaltung.adapters.database.dataaccess.dto;

import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.List;

@Table("AUSGABE")
public record AusgabeDto(String name,
                         String beschreibung,
                         BigDecimal betrag,
                         String bezahltVon,
                         List<String> beteiligte) {
}
