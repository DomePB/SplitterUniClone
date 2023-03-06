package de.hhu.ausgabenverwaltung.database.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("User")
public record UserDto(@Id String githubHandle) {
}
