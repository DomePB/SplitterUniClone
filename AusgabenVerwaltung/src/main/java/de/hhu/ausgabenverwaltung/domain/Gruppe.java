package de.hhu.ausgabenverwaltung.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Gruppe {

    String name;
    List<Ausgabe> ausgaben;
    List<User> mitglieder;



    Set<Transaktion> transaktionen;

    public Gruppe(String name, List<Ausgabe> ausgaben, List<User> mitglieder, Set<Transaktion> transaktionen ) {
        this.name = name;
        this.mitglieder = mitglieder;
        this.ausgaben = ausgaben;
        this.transaktionen=transaktionen;
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

    public void addMitglieder(User user){
        mitglieder.add(user);
    }

    public void deleteMitglieder(User user){mitglieder.remove(0);}

    public boolean isTransaktionValid(Transaktion transaktion){
        if (!mitglieder.contains(transaktion.sender()) || !mitglieder.contains(transaktion.empfaenger())) {
            return false;
        }

        for (Transaktion t:transaktionen) {
            if (t.empfaenger().equals(transaktion.empfaenger()) && t.sender().equals(transaktion.sender()) ||
                    t.empfaenger().equals(transaktion.sender()) && t.sender().equals(transaktion.empfaenger()))
            {
                return false;
            }

        }
        return true;
    }

    public void transaktionHinzufuegen(Transaktion transaktion) {
        if (isTransaktionValid(transaktion)) {
            transaktionen.add(transaktion);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gruppe gruppe = (Gruppe) o;
        return name.equals(gruppe.name) && ausgaben.equals(gruppe.ausgaben) && mitglieder.equals(gruppe.mitglieder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ausgaben, mitglieder);
    }
}
