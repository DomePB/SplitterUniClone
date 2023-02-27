package de.hhu.ausgabenverwaltung.service;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import org.springframework.stereotype.Service;

@Service
public class GruppenService {


	public void personHinzufuegen (User user, Gruppe gruppe){
		gruppe.addMitglieger(user);
	}

}
