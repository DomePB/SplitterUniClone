package de.hhu.ausgabenverwaltung.api.models;

import java.util.List;

public record AuslagenModel(String grund, String glaeubiger, int cent, List<String> schuldner) {
}
