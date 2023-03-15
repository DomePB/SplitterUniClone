package de.hhu.ausgabenverwaltung.adapters.database.implementation;

import de.hhu.ausgabenverwaltung.adapters.database.dataaccess.dao.GruppeDao;
import de.hhu.ausgabenverwaltung.adapters.database.dataaccess.dto.GruppeDto;
import de.hhu.ausgabenverwaltung.application.repo.GruppenRepository;
import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class GruppenRepositoryImp implements GruppenRepository {

  private final GruppeDao gruppeDao;
  public GruppenRepositoryImp(GruppeDao gruppeDao) {
    this.gruppeDao = gruppeDao;
  }

  @Override
  public void save(Gruppe gruppe) {
  //  GruppeDto gruppeDto = GruppeDto.fromGruppe(gruppe);
   // gruppeDao.save(gruppeDto);
    gruppeDao.insertGruppe(gruppe.getId(),gruppe.getName(),gruppe.istOffen());
    gruppeDao.insertMITGLIED(gruppe.getId(),gruppe.getMitglieder().iterator().next().githubHandle());
  }

  @Override
  public List<Gruppe> getGruppenvonUser(User user) {
    List<UUID> gruppenvonUser = gruppeDao.getGruppenvonUser(user.githubHandle());
    return gruppenvonUser.stream().map(gruppeDao::findById).map(Optional::get).map(GruppeDto::toGruppe).toList();
    }

  @Override
  public List<Gruppe> getOffeneGruppenVonUser(User user) {
    return gruppeDao.getOffeneGruppenVonUser(user.githubHandle()).stream().map(GruppeDto::toGruppe)
        .collect(Collectors.toList());

  }

  @Override
  public List<Gruppe> getGeschlosseneGruppenVonUser(User user) {
    return gruppeDao.getGeschlosseneGruppenVonUser(user.githubHandle()).stream()
        .map(GruppeDto::toGruppe).collect(Collectors.toList());

  }

  @Override
  public Gruppe findById(UUID id) throws NoSuchElementException {
    return gruppeDao.findById(id).orElseThrow(NoSuchElementException::new).toGruppe();

  }

}
