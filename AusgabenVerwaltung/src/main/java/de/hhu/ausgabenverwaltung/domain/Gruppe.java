package de.hhu.ausgabenverwaltung.domain;

import java.util.List;
import java.util.Objects;

public class Gruppe {

    String name;
    List<Ausgabe> ausgaben;
    List<User> mitglieder;

    public Gruppe(String name, List<Ausgabe> ausgaben, List<User> mitglieder) {
        this.name = name;
        this.mitglieder = mitglieder;
        this.ausgaben = ausgaben;
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


    public void addMitglieger(User user){
        mitglieder.add(user);
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
