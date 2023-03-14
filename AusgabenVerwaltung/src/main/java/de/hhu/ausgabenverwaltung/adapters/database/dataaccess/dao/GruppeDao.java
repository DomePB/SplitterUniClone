package de.hhu.ausgabenverwaltung.adapters.database.dataaccess.dao;

import de.hhu.ausgabenverwaltung.adapters.database.dataaccess.dto.GruppeDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GruppeDao extends CrudRepository<GruppeDto, UUID> {

  List<GruppeDto> findBygithubHandle(String githubHandle);

  List<GruppeDto> findBygithubHandleAndOffenFalse(String githubHandle);

  List<GruppeDto> findBygithubHandleAndOffenTrue(String githubHandle);

}
