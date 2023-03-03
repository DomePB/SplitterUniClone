package de.hhu.ausgabenverwaltung.service;

import de.hhu.ausgabenverwaltung.domain.Gruppe;
import de.hhu.ausgabenverwaltung.domain.Transaktion;
import de.hhu.ausgabenverwaltung.domain.User;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class GruppenService {
    private final List<Gruppe> gruppen = new ArrayList<>();

    public List<Gruppe> getGruppen() {
        return gruppen;
    }

    public Gruppe gruppeErstellen(User ersteller, String name) {
        Gruppe gruppe = new Gruppe(name, new ArrayList<>(), new ArrayList<>(List.of(ersteller)),
                new HashSet<>(), true);
        gruppen.add(gruppe);
        return gruppe;
    }

    public HashMap<User, HashMap<User, BigDecimal>> alleSchuldenBerechnen(Gruppe gruppe) {
        HashMap<User, HashMap<User, BigDecimal>> schulden = new HashMap<>();
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
                zielSumme = zielSumme.add(userSchulden.getValue());
                schuldenSumme.put(zielUser, zielSumme);

                // Addiere Einnahmen des aktuellen User
                summe = summe.subtract(userSchulden.getValue());
            }
            schuldenSumme.put(aktuellerUser, summe);
        }
        return schuldenSumme;
    }

    public Map<Gruppe, Set<Transaktion>> getBeteiligteTransaktionen(User user) {
        Map<Gruppe, Set<Transaktion>> userTransaktinen = new HashMap<>();
        for (Gruppe gruppe : getGruppen()) {
            Set<Transaktion> temp = new HashSet<>();
            if (gruppe.getMitglieder().contains(user)) {
                Set<Transaktion> groupeTransaktionen = berechneTransaktionen(gruppe);
                for (Transaktion t : groupeTransaktionen) {
                    if (t.sender().equals(user) || t.empfaenger().equals(user)) {
                        temp.add(t);
                    }
                }
                userTransaktinen.put(gruppe, temp);
            }
        }
        return userTransaktinen;
    }

    public Set<Transaktion> berechneTransaktionen(Gruppe gruppe) {
        Set<Transaktion> transaktionen = new HashSet<>();
        var salden = berechneSalden(gruppe);

        for (var empfaengerEntry : salden.entrySet()) {

            for (var senderEntry : salden.entrySet()) {
                if (empfaengerEntry.getKey().equals(senderEntry.getKey())) {
                    continue;
                }

                // Sind Empfänger und Sender gleich?
                if (empfaengerEntry.getValue().add(senderEntry.getValue())
                        .equals(BigDecimal.ZERO)) {
                    if (empfaengerEntry.getValue().compareTo(BigDecimal.ZERO) < 0) {
                        transaktionen.add(
                                new Transaktion(senderEntry.getKey(), empfaengerEntry.getKey(),
                                        empfaengerEntry.getValue().multiply(new BigDecimal(-1))));
                    } else if (empfaengerEntry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                        transaktionen.add(
                                new Transaktion(empfaengerEntry.getKey(), senderEntry.getKey(),
                                        senderEntry.getValue().multiply(new BigDecimal(-1))));
                    }

                    salden.put(empfaengerEntry.getKey(), BigDecimal.ZERO);
                    salden.put(senderEntry.getKey(), BigDecimal.ZERO);
                }
            }
        }

        for (var senderEntry : salden.entrySet()) {
            // Negative Salden werden ignoriert
            if (senderEntry.getValue().compareTo(BigDecimal.ZERO) < 0) {
                continue;
            }

            for (var empfaengerEntry : salden.entrySet()) {
                // Null-Salden werden ignoriert
                if (senderEntry.getValue().equals(BigDecimal.ZERO)) {
                    break;
                }

                // Nicht mit dem aktuellen User ausgleichen
                if (senderEntry.getKey().equals(empfaengerEntry.getKey())) {
                    continue;
                }

                // Ignoriere positive Salden
                if (empfaengerEntry.getValue().compareTo(BigDecimal.ZERO) >= 0) {
                    continue;
                }

                // Fälle behandeln
                if (senderEntry.getValue().add(empfaengerEntry.getValue())
                        .compareTo(BigDecimal.ZERO) < 0) {
                    // Schulden von sender sind kleiner als Einnahmen von empfaenger
                    transaktionen.add(
                            new Transaktion(senderEntry.getKey(), empfaengerEntry.getKey(),
                                    senderEntry.getValue()));
                    salden.put(empfaengerEntry.getKey(),
                            senderEntry.getValue().add(empfaengerEntry.getValue()));
                    salden.put(senderEntry.getKey(), BigDecimal.ZERO);
                } else {
                    // Schulden von sender sind größer als Einnahmen von empfaenger
                    transaktionen.add(
                            new Transaktion(senderEntry.getKey(), empfaengerEntry.getKey(),
                                    empfaengerEntry.getValue().multiply(new BigDecimal(-1))));
                    salden.put(senderEntry.getKey(),
                            senderEntry.getValue().add(empfaengerEntry.getValue()));
                    salden.put(empfaengerEntry.getKey(), BigDecimal.ZERO);
                }
            }
        }
        //gruppe.setTransaktionen(transaktionen);
        return transaktionen;
    }

}
