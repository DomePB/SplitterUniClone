package de.hhu.ausgabenverwaltung.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Gruppe {

  private final UUID id;
  private final String name;
  private final List<Ausgabe> ausgaben;
  private final Set<User> mitglieder;
  boolean offen;

  public Gruppe(String name, List<Ausgabe> ausgaben, Set<User> mitglieder,
                boolean offen, UUID id) {
    this.name = name;
    this.mitglieder = mitglieder;
    this.ausgaben = ausgaben;
    this.offen = offen;
    this.id = id;
  }

  public static Gruppe createGruppe(String name, Set<User> mitglieder) {
    return new Gruppe(name, new ArrayList<>(), new HashSet<>(mitglieder), true,
        null);
  }

  public static Gruppe createGruppe(String name, Set<User> mitglieder, UUID id) {
    return new Gruppe(name, new ArrayList<>(), new HashSet<>(mitglieder), true,
        id);
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public List<Ausgabe> getAusgaben() {
    return Collections.unmodifiableList(ausgaben);
  }

  public Set<User> getMitglieder() {
    return Collections.unmodifiableSet(mitglieder);
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


  public void addAusgabe(Ausgabe ausgabe) {
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

  public void schliessen() {
    offen = false;
  }

  public boolean istOffen() {
    return offen;
  }

  public HashMap<User, BigDecimal> mussBezahlenVonUser(User vonUser) {
    HashMap<User, BigDecimal> schuldner = new HashMap<>();
    for (Ausgabe ausgabe : ausgaben) {
      // Beachte nur Ausgaben, die vom User bezahlt wurden
      if (!ausgabe.bezahltVon().equals(vonUser)) {
        continue;
      }

      // Betrachte die beteiligten
      for (User anUser : ausgabe.beteiligte()) {
        // Ignoriere User, die an sich selbst zahlen
        if (anUser.equals(vonUser)) {
          continue;
        }

        // Summe berechnen
        BigDecimal userSumme = schuldner.getOrDefault(anUser, BigDecimal.ZERO);
        userSumme = userSumme.add(ausgabe.betrag()
            .divide(new BigDecimal(ausgabe.beteiligte().size()), 2, RoundingMode.HALF_UP));
        schuldner.put(anUser, userSumme);
      }
    }
    return schuldner;
  }

  public HashMap<User, HashMap<User, BigDecimal>> alleSchuldenBerechnen() { // in Gruppe
    HashMap<User, HashMap<User, BigDecimal>> schulden = new HashMap<>();

    for (User mitglied : mitglieder) {
      schulden.put(mitglied, mussBezahlenVonUser(mitglied));
    }

    return schulden;
  }

  public HashMap<User, BigDecimal> berechneSalden(
      HashMap<User, HashMap<User, BigDecimal>> alleSchulden) {
    // -Betrag bekommt der Nutzer noch, +Betrag muss der Nutzer noch zahlen
    HashMap<User, BigDecimal> schuldenSumme = new HashMap<>();

    // Iteriere durch alle Spalten der Tabelle
    for (var entry : alleSchulden.entrySet()) {
      User aktuellerUser = entry.getKey();
      BigDecimal summe = schuldenSumme.getOrDefault(aktuellerUser, BigDecimal.ZERO);

      // Iteriere durch alle Zeilen der Tabelle
      for (var userSchulden : entry.getValue().entrySet()) {
        // Addieren Schulden des Ziel Users
        User zielUser = userSchulden.getKey();
        BigDecimal zielSumme = schuldenSumme.getOrDefault(zielUser, BigDecimal.ZERO);
        zielSumme = zielSumme.add(userSchulden.getValue());
        schuldenSumme.put(zielUser, zielSumme);

        // Subtrahiere Einnahmen des aktuellen User
        summe = summe.subtract(userSchulden.getValue());
      }
      schuldenSumme.put(aktuellerUser, summe);
    }
    return schuldenSumme;
  }

  public Set<Transaktion> berechneTransaktionen(HashMap<User, BigDecimal> salden) {
    Set<Transaktion> transaktionen = new HashSet<>();

    // "Perfekte" AusgleichsZahlungen berechnen
    for (var empfaengerEntry : salden.entrySet()) {
      for (var senderEntry : salden.entrySet()) {
        // Ignoriere Sender = Empfänger
        if (empfaengerEntry.getKey().equals(senderEntry.getKey())) {
          continue;
        }

        // Sind Empfänger und Sender gleich?
        if (empfaengerEntry.getValue().add(senderEntry.getValue())
            .compareTo(BigDecimal.ZERO) == 0) {
          if (empfaengerEntry.getValue().compareTo(BigDecimal.ZERO) < 0) {
            transaktionen.add(
                new Transaktion(senderEntry.getKey(), empfaengerEntry.getKey(),
                    empfaengerEntry.getValue().abs()));
          } else if (empfaengerEntry.getValue().compareTo(BigDecimal.ZERO) > 0) {
            transaktionen.add(
                new Transaktion(empfaengerEntry.getKey(), senderEntry.getKey(),
                    senderEntry.getValue().abs()));
          }

          salden.put(empfaengerEntry.getKey(), BigDecimal.ZERO);
          salden.put(senderEntry.getKey(), BigDecimal.ZERO);
        }
      }
    }

    // Alle Ausgleichs-Zahlungen berechnen
    for (var senderEntry : salden.entrySet()) {
      // Negative Salden werden ignoriert
      if (senderEntry.getValue().compareTo(BigDecimal.ZERO) < 0) {
        continue;
      }

      for (var empfaengerEntry : salden.entrySet()) {
        // Null-Salden werden ignoriert
        if (senderEntry.getValue().compareTo(BigDecimal.ZERO) == 0) {
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
                  empfaengerEntry.getValue().abs()));
          salden.put(senderEntry.getKey(),
              senderEntry.getValue().add(empfaengerEntry.getValue()));
          salden.put(empfaengerEntry.getKey(), BigDecimal.ZERO);
        }
      }
    }

    return transaktionen;
  }

  public boolean checkMitglied(String githubHandle) {
    return mitglieder.contains(new User(githubHandle));
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
    return name.equals(gruppe.name) && ausgaben.equals(gruppe.ausgaben)
        && mitglieder.equals(gruppe.mitglieder);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, ausgaben, mitglieder);
  }
}
