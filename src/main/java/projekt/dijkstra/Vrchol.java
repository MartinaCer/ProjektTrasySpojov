/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekt.dijkstra;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Ja
 */
public class Vrchol {

    private final int kod;
    private final String nazov;
    private final Map<Vrchol, Integer> susedneVrcholy;
    private List<Vrchol> najkratsiaTrasa;
    private int vzdialenost;

    public Vrchol(int kod, String nazov) {
        this.kod = kod;
        this.nazov = nazov;
        this.susedneVrcholy = new HashMap<>();
        this.najkratsiaTrasa = new LinkedList<>();
        this.vzdialenost = Integer.MAX_VALUE;
    }

    public void pridajSuseda(Vrchol vrchol, Integer cena) {
        susedneVrcholy.put(vrchol, cena);
    }

    public int getKod() {
        return kod;
    }

    public String getNazov() {
        return nazov;
    }

    public Map<Vrchol, Integer> getSusedneVrcholy() {
        return susedneVrcholy;
    }

    public List<Vrchol> getNajkratsiaTrasa() {
        return najkratsiaTrasa;
    }

    public void setNajkratsiaTrasa(List<Vrchol> najkratsiaTrasa) {
        this.najkratsiaTrasa = najkratsiaTrasa;
    }

    public int getVzdialenost() {
        return vzdialenost;
    }

    public void setVzdialenost(int vzdialenost) {
        this.vzdialenost = vzdialenost;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + this.kod;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vrchol other = (Vrchol) obj;
        return this.kod == other.kod;
    }

}
