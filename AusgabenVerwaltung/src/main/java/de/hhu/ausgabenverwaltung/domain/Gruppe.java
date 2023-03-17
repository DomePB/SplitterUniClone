package de.hhu.ausgabenverwaltung.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
              ausgabe.betrag().divide(new BigDecimal(ausgabe.beteiligte().size()), 2,
                  RoundingMode.HALF_UP));
          schuldner.put(useri, userSumme);

        }
      }
    }
    return schuldner;
  }

  public void schliessen() {
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

  public HashMap<User, BigDecimal> berechneSalden(
      // -Betrag bekommt der Nutzer noch, +Betrag muss der Nutzer noch zahlen
      HashMap<User, HashMap<User, BigDecimal>> alleSchulden) {
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

  public Set<Transaktion> berechneTransaktionen(Map<User, BigDecimal> salden) {
    Set<Transaktion> transaktionen = new HashSet<>();

    // Aufteilen in positive und negative Salden
    Map<User, BigDecimal> posSalden = salden.entrySet().stream()
        .filter(entry -> entry.getValue().compareTo(BigDecimal.ZERO) > 0)
        .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()),
            HashMap::putAll);

    Map<User, BigDecimal> negSalden = salden.entrySet().stream()
        .filter(entry -> entry.getValue().compareTo(BigDecimal.ZERO) < 0)
        .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()),
            HashMap::putAll);

    // Schleife, die so lange läuft, bis alle Salden ausgeglichen sind
    while (!posSalden.isEmpty() && !negSalden.isEmpty()) {
      // Suche den Benutzer mit dem höchsten positiven Saldo
      User maxPositiveUser = null;
      BigDecimal maxPositiveSalden = BigDecimal.ZERO;
      for (var entry : posSalden.entrySet()) {
        if (entry.getValue().compareTo(maxPositiveSalden) > 0) {
          maxPositiveUser = entry.getKey();
          maxPositiveSalden = entry.getValue();
        }
      }

      // Suche den Benutzer mit dem höchsten negativen Saldo
      User maxNegativeUser = null;
      BigDecimal maxNegativeSalden = BigDecimal.valueOf(Integer.MAX_VALUE);
      for (var entry : negSalden.entrySet()) {
        if (entry.getValue().compareTo(maxNegativeSalden) < 0) {
          maxNegativeUser = entry.getKey();
          maxNegativeSalden = entry.getValue();
        }
      }

      // Berechnung der Transaktion und Anpassung der Salden
      BigDecimal transactionAmount = maxPositiveSalden.min(maxNegativeSalden.abs());
      transaktionen.add(new Transaktion(maxPositiveUser, maxNegativeUser, transactionAmount));
      posSalden.put(maxPositiveUser, maxPositiveSalden.subtract(transactionAmount));
      negSalden.put(maxNegativeUser, maxNegativeSalden.add(transactionAmount));

      // Entfernung der Benutzer mit ausgeglichenen Salden
      if (posSalden.get(maxPositiveUser).compareTo(BigDecimal.ZERO) == 0) {
        posSalden.remove(maxPositiveUser);
      }

      if (negSalden.get(maxNegativeUser).compareTo(BigDecimal.ZERO) == 0) {
        negSalden.remove(maxNegativeUser);
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
