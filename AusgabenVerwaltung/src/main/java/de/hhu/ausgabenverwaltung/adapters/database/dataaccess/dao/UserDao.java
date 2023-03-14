package de.hhu.ausgabenverwaltung.adapters.database.dataaccess.dao;

import de.hhu.ausgabenverwaltung.adapters.database.dataaccess.dto.UserDto;
import org.springframework.data.repository.CrudRepository;

public interface UserDao extends CrudRepository<UserDto, String> {

}
