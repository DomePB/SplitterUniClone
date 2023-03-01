package de.hhu.ausgabenverwaltung.service;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class GruppenService {


	public Gruppe gruppeErstellen(User ersteller, String name){
		return new Gruppe(name, new ArrayList<>(), new ArrayList<>(List.of(ersteller)), new HashSet<>(),true);
	}

}
