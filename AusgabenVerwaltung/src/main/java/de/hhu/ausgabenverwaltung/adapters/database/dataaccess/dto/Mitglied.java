package de.hhu.ausgabenverwaltung.adapters.database.dataaccess.dto;

import org.springframework.data.relational.core.mapping.Table;

@Table("mitglied")
public record Mitglied(String githubhandle) {
}
