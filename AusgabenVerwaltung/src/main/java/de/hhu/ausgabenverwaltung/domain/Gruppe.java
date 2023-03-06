package de.hhu.ausgabenverwaltung.domain;

import java.math.BigDecimal;
import java.util.*;

public class Gruppe {

    private Long id;
    private String name;
    private List<Ausgabe> ausgaben;
    private List<User> mitglieder;
    boolean offen;
    Set<Transaktion> transaktionen;

    public Gruppe(String name, List<Ausgabe> ausgaben, List<User> mitglieder,
                  Set<Transaktion> transaktionen, boolean offen) {
        this.name = name;
        this.mitglieder = mitglieder;
        this.ausgaben = ausgaben;
        this.transaktionen = transaktionen;
        this.offen = offen;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ausgabe> getAusgaben() {
        return ausgaben;
    }

    public void setAusgaben(List<Ausgabe> ausgaben) {
        this.ausgaben = ausgaben;
    }

    public List<User> getMitglieder() {
        return mitglieder;
    }

    public void setMitglieder(List<User> mitglieder) {
        this.mitglieder = mitglieder;
    }

    public Set<Transaktion> getTransaktionen() {
        return transaktionen;
    }

    public void setTransaktionen(Set<Transaktion> transaktionen) {
        this.transaktionen = transaktionen;
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

    public boolean isTransaktionValid(Transaktion transaktion) {
        if (transaktion.sender().equals(transaktion.empfaenger())) {
            return false;
        }
        if (!mitglieder.contains(transaktion.sender()) ||
                !mitglieder.contains(transaktion.empfaenger())) {
            return false;
        }

        for (Transaktion t : transaktionen) {
            if (t.empfaenger().equals(transaktion.empfaenger()) &&
                    t.sender().equals(transaktion.sender()) ||
                    t.empfaenger().equals(transaktion.sender()) &&
                            t.sender().equals(transaktion.empfaenger())) {
                return false;
            }
        }
        return true;
    }

    public void transaktionHinzufuegen(Transaktion transaktion) {
        if (!offen) {
            return;
        }
        if (isTransaktionValid(transaktion)) {
            transaktionen.add(transaktion);
        }
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
                            ausgabe.betrag().divide(new BigDecimal(ausgabe.beteiligte().size())));
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
