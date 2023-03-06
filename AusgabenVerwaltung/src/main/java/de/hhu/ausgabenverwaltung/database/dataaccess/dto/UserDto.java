package de.hhu.ausgabenverwaltung.database.dataaccess.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


public record UserDto(String githubHandle) {
}
