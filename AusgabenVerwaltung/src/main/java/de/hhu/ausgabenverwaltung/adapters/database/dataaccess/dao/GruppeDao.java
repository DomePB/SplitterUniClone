package de.hhu.ausgabenverwaltung.adapters.database.dataaccess.dao;

import de.hhu.ausgabenverwaltung.adapters.database.dataaccess.dto.GruppeDto;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface GruppeDao extends CrudRepository<GruppeDto, Long> {

  Optional<GruppeDto> findById(Long Id);

  @Query("SELECT * FROM GRUPPE WHERE ID = :Id")
  GruppeDto find(@Param("Id") Long id);

}
