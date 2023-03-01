package de.hhu.ausgabenverwaltung.service;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Service
public class GruppenService {

	public Gruppe gruppeErstellen(User ersteller, String name){
		return new Gruppe(name, new ArrayList<>(), new ArrayList<>(List.of(ersteller)), new HashSet<>(),true);
	}

	public List<HashMap<User, BigDecimal>> alleSchuldenBerechnen(Gruppe gruppe){
		List<HashMap<User,BigDecimal>> schulden = new ArrayList<>();

		for (User mitglied : gruppe.getMitglieder()) {
			schulden.add(gruppe.mussBezahlenVonUser(mitglied));
		}
		return schulden;

	}

}
