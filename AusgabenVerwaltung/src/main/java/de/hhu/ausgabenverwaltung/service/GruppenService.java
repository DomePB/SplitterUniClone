package de.hhu.ausgabenverwaltung.service;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.Transaktion;
import de.hhu.ausgabenverwaltung.domain.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class GruppenService {

	public Gruppe gruppeErstellen(User ersteller, String name){
		return new Gruppe(name, new ArrayList<>(), new ArrayList<>(List.of(ersteller)), new HashSet<>(),true);
	}

	public HashMap<User, HashMap<User, BigDecimal>> alleSchuldenBerechnen(Gruppe gruppe){
		HashMap<User, HashMap<User,BigDecimal>> schulden = new HashMap<>();

		for (User mitglied : gruppe.getMitglieder()) {
			schulden.put(mitglied, gruppe.mussBezahlenVonUser(mitglied));
		}
		return schulden;

	}

	public HashMap<User, BigDecimal> berechneSalden(Gruppe gruppe) {
		var alleSchulden = alleSchuldenBerechnen(gruppe);
		HashMap<User, BigDecimal> schuldenSumme = new HashMap<>();

		// Iteriere durch alle Spalten der Tabelle
		for (var entry : alleSchulden.entrySet()) {
			User aktuellerUser = entry.getKey();
			BigDecimal summe = schuldenSumme.getOrDefault(aktuellerUser, BigDecimal.ZERO);

			// Iteriere durch alle Zeilen der Tabelle
			for (var userSchulden : entry.getValue().entrySet()) {
				// Subtrahiere Schulden des Ziel Users
				User zielUser = userSchulden.getKey();
				BigDecimal zielSumme = schuldenSumme.getOrDefault(zielUser, BigDecimal.ZERO);
				zielSumme = zielSumme.subtract(userSchulden.getValue());
				schuldenSumme.put(zielUser, zielSumme);

				// Addiere Einnahmen des aktuellen User
				summe = summe.add(userSchulden.getValue());
			}

			schuldenSumme.put(aktuellerUser, summe);
		}

		return schuldenSumme;
	}

	public Set<Transaktion> berechneTransaktionen(Gruppe gruppe) {
		Set<Transaktion> transaktionen = new HashSet<>();


		return transaktionen;
	}

}
