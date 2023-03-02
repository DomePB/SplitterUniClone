package de.hhu.ausgabenverwaltung.service;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.Transaktion;
import de.hhu.ausgabenverwaltung.domain.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class GruppenService {
	private final List<Gruppe> gruppen = new ArrayList<>();


	public Gruppe gruppeErstellen(User ersteller, String name){
		 Gruppe gruppe = new Gruppe(name, new ArrayList<>(), new ArrayList<>(List.of(ersteller)), new HashSet<>(),true);
		gruppen.add(gruppe);
		 return gruppe;
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
		var salden = berechneSalden(gruppe);

		for(var saldenEntry: salden.entrySet()){
			for (var zielEntry: salden.entrySet()) {
				if ( saldenEntry.getKey()==zielEntry.getKey()){
					continue;
				}
				if (saldenEntry.getValue().add(zielEntry.getValue()).equals(BigDecimal.ZERO)){
					if (saldenEntry.getValue().compareTo(BigDecimal.ZERO) > 0){

						transaktionen.add(new Transaktion(saldenEntry.getKey(), zielEntry.getKey(), saldenEntry.getValue()));
					}
					else if(saldenEntry.getValue().compareTo(BigDecimal.ZERO) < 0) {
						transaktionen.add(new Transaktion(zielEntry.getKey(), saldenEntry.getKey(), zielEntry.getValue()));
					}
					salden.put(saldenEntry.getKey(), BigDecimal.ZERO);
					salden.put(zielEntry.getKey(), BigDecimal.ZERO);
				}
			}
		}
		for(var saldenEntry: salden.entrySet()){

			if (saldenEntry.getValue().equals(BigDecimal.ZERO)){
				continue;
			}

			for (var zielEntry: salden.entrySet()) {
				if (saldenEntry.getKey() == zielEntry.getKey()) {
					continue;
				}
				if (saldenEntry.getValue().compareTo(BigDecimal.ZERO) > 0 && zielEntry.getValue().compareTo(BigDecimal.ZERO) < 0){
					if (saldenEntry.getValue().compareTo(zielEntry.getValue().multiply(new BigDecimal(-1)))>0){
						BigDecimal bigDecimalBetrag = zielEntry.getValue().multiply(new BigDecimal(-1));
						transaktionen.add(new Transaktion(saldenEntry.getKey(), zielEntry.getKey(),bigDecimalBetrag));
						salden.put(saldenEntry.getKey(), saldenEntry.getValue().add(zielEntry.getValue()));
						salden.put(zielEntry.getKey(), zielEntry.getValue().add(bigDecimalBetrag));
					}else{ //Zielentry<Salden
						BigDecimal bigDecimalBetrag = saldenEntry.getValue().multiply(new BigDecimal(1));
						transaktionen.add(new Transaktion(saldenEntry.getKey(), zielEntry.getKey(),bigDecimalBetrag));
						salden.put(saldenEntry.getKey(), saldenEntry.getValue().add(zielEntry.getValue()));
						salden.put(zielEntry.getKey(), zielEntry.getValue().add(bigDecimalBetrag));
					}


				}if (saldenEntry.getValue().compareTo(BigDecimal.ZERO) < 0 && zielEntry.getValue().compareTo(BigDecimal.ZERO) > 0){
					if (saldenEntry.getValue().compareTo(zielEntry.getValue())>0){
					BigDecimal bigDecimalBetrag = saldenEntry.getValue().multiply(new BigDecimal(-1));
					transaktionen.add(new Transaktion(zielEntry.getKey(), saldenEntry.getKey(),bigDecimalBetrag));
					salden.put(zielEntry.getKey(), zielEntry.getValue().add(saldenEntry.getValue()));
					salden.put(saldenEntry.getKey(), saldenEntry.getValue().add(bigDecimalBetrag));
					}
				}

			}
		}
		return transaktionen;
	}

}
