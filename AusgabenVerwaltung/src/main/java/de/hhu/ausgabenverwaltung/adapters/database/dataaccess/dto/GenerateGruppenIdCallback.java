package de.hhu.ausgabenverwaltung.adapters.database.dataaccess.dto;

import java.util.UUID;
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

@Component
public class GenerateGruppenIdCallback implements BeforeConvertCallback<GruppeDto> {
  @Override
  public GruppeDto onBeforeConvert(GruppeDto aggregate) {
    if (aggregate.id() == null) {
      return new GruppeDto(UUID.randomUUID(),
          aggregate.name(),
          aggregate.offen(),
          aggregate.ausgabe(),
          aggregate.mitglied());
    }

    return aggregate;
  }
}
