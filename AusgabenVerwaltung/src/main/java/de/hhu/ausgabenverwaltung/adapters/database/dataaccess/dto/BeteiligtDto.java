package de.hhu.ausgabenverwaltung.adapters.database.dataaccess.dto;

import org.springframework.data.relational.core.mapping.Table;

@Table("beteiligt")
public record BeteiligtDto(String githubhandle) {
}
