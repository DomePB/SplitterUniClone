package de.hhu.ausgabenverwaltung.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Gruppe {

    private final UUID id;
    private String name;
    private List<Ausgabe> ausgaben;
    private List<User> mitglieder;
    boolean offen;

    public Gruppe(String name, List<Ausgabe> ausgaben, List<User> mitglieder,
                  boolean offen, UUID id) {
        this.name = name;
        this.mitglieder = mitglieder;
        this.ausgaben = ausgaben;
        this.offen = offen;
        this.id = id;
    }

    public static Gruppe gruppeErstellen(String name,User user){
        return new Gruppe(name,new ArrayList<>(),new ArrayList<>(List.of(user)), true,UUID.randomUUID());
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public List<Ausgabe> getAusgaben() {
        return ausgaben;
    }

    public List<User> getMitglieder() {
        return mitglieder;
    }


    public void addMitglieder(User user) {
        if (!offen || mitglieder.contains(user) || !ausgaben.isEmpty()) {
            return;
        }
        mitglieder.add(user);
    }

    public void deleteMitglieder(User user) {
        if (!offen) {
            return;
        }
        mitglieder.remove(user);
    }



    public void ausgabeHinzufuegen(Ausgabe ausgabe) {
        if (!offen) {
            return;
        }
        ausgaben.add(ausgabe);
    }

    public BigDecimal summeVonUser(User user) {
        return ausgaben.stream()
                .filter(ausgabe -> ausgabe.bezahltVon().equals(user))
                .map(Ausgabe::betrag)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public HashMap<User, BigDecimal> mussBezahlenVonUser(User user) {
        HashMap<User, BigDecimal> schuldner = new HashMap<>();
        for (Ausgabe ausgabe : ausgaben) {
            if (ausgabe.bezahltVon().equals(user)) {
                for (User useri : ausgabe.beteiligte()
                ) {
                    if (useri.equals(user)) {
                        continue;
                    }
                    BigDecimal userSumme = schuldner.getOrDefault(useri, BigDecimal.ZERO);
                    userSumme = userSumme.add(
                            ausgabe.betrag().divide(new BigDecimal(ausgabe.beteiligte().size()),2,
                                    RoundingMode.HALF_UP));
                    schuldner.put(useri, userSumme);

                }
            }
        }
        return schuldner;
    }


    public void schliessen() {
        berechneTransaktionen(berechneSalden(alleSchuldenBerechnen()));
        offen = false;
    }

    public boolean istOffen() {
        return offen;
    }

    public HashMap<User, HashMap<User, BigDecimal>> alleSchuldenBerechnen() { // in Gruppe
        HashMap<User, HashMap<User, BigDecimal>> schulden = new HashMap<>();
        for (User mitglied : mitglieder) {
            schulden.put(mitglied, mussBezahlenVonUser(mitglied));
        }
        return schulden;
    }

    public HashMap<User, BigDecimal> berechneSalden(HashMap<User, HashMap<User, BigDecimal>> alleSchulden) {
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


    public Set<Transaktion> berechneTransaktionen(HashMap<User, BigDecimal> salden) {
        Set<Transaktion> transaktionen = new HashSet<>();
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

        return transaktionen;
    }

    public boolean checkMitglied(String githubHandle){
        if(mitglieder.contains(new User(githubHandle))){
            return true;
        }
        return false;
    }





    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Gruppe gruppe = (Gruppe) o;
        return name.equals(gruppe.name) && ausgaben.equals(gruppe.ausgaben) &&
                mitglieder.equals(gruppe.mitglieder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ausgaben, mitglieder);
    }
}
