package de.hhu.ausgabenverwaltung.adapters.database.dataaccess.dao;

import de.hhu.ausgabenverwaltung.adapters.database.dataaccess.dto.GruppeDto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GruppeDao extends CrudRepository<GruppeDto, UUID> {

  @Query("SELECT * FROM GRUPPE g JOIN MITGLIED m ON g.id = m.gruppenId WHERE m.githubHandle = :githubHandle")
  List<GruppeDto> getGruppenvonUser(@Param("githubHandle")String githubHandle);

  @Query("SELECT * FROM GRUPPE g JOIN MITGLIED m ON g.id = m.gruppenId WHERE m.githubHandle = :githubHandle AND g.offen IS TRUE")
  List<GruppeDto> getOffeneGruppenVonUser(@Param("githubHandle")String githubHandle);

  @Query("SELECT * FROM GRUPPE g JOIN MITGLIED m ON g.id = m.gruppenId WHERE m.githubHandle = :githubHandle AND g.offen IS FALSE")
  List<GruppeDto> getGeschlosseneGruppenVonUser(@Param("githubHandle")String githubHandle);
  @Modifying
  @Query("INSERT INTO GRUPPE(id,name,offen) VALUES (:gruppenId,:name,:offen)")
 void insertGruppe(@Param("gruppenId") UUID id, @Param("name")String name,@Param("offen") boolean offen);

  @Modifying
  @Query("INSERT INTO MITGLIED(gruppenId,githubHandle) VALUES (:id,:githubHandle)")
  void insertMITGLIED(@Param("id") UUID id, @Param("githubHandle") String githubHandle);
}
