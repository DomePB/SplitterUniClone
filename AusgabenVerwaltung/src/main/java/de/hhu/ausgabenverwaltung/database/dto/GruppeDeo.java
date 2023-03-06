package de.hhu.ausgabenverwaltung.database.dto;

import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface GruppeDeo extends CrudRepository<GruppeDto,Long> {

    Optional<GruppeDto> findById(Long Id);

    @Query("SELECT * FROM GRUPPE WHERE ID = :Id")
    GruppeDto find(@Param("Id")Long id);

}
