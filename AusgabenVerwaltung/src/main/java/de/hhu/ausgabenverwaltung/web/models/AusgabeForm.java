package de.hhu.ausgabenverwaltung.web.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public record AusgabeForm (
     String ausgabeName,
     String ausgabeBeschreibung,
    BigDecimal ausgabeBetrag,
   String bezahltVon,
   List<String> beteiligte){
    public static AusgabeForm defaultAusgabe(){
        return new AusgabeForm("",
       "",
        BigDecimal.ZERO,
        "",
        new ArrayList<>());
    }
}
