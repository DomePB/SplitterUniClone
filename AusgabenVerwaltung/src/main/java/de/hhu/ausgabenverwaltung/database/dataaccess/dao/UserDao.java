package de.hhu.ausgabenverwaltung.database.dataaccess.dao;

import de.hhu.ausgabenverwaltung.database.dataaccess.dto.UserDto;
import org.springframework.data.repository.CrudRepository;

public interface UserDao extends CrudRepository<UserDto,String>{

}
