/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekt.dijkstra;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Ja
 */
public class Graf {

    private final Map<Integer, Vrchol> vrcholy;

    public Graf() {
        vrcholy = new HashMap<>();
    }

    public void pridajVrchol(Vrchol vrchol) {
        vrcholy.put(vrchol.getKod(), vrchol);
    }

    public Map<Integer, Vrchol> getVrcholy() {
        return vrcholy;
    }

    public void vypocitajVzdialenostiOdVrchola(Vrchol zaciatok) {
        inicializujVzdialenosti();
        zaciatok.setVzdialenost(0);
        Set<Vrchol> prejdeneVrcholy = new HashSet<>();
        Set<Vrchol> neprejdeneVrcholy = new HashSet<>();
        neprejdeneVrcholy.add(zaciatok);

        while (!neprejdeneVrcholy.isEmpty()) {
            Vrchol aktualnyVrchol = najdiVrcholNajmensiaVzdialenost(neprejdeneVrcholy);
            neprejdeneVrcholy.remove(aktualnyVrchol);
            for (Map.Entry<Vrchol, Integer> sused : aktualnyVrchol.getSusedneVrcholy().entrySet()) {
                Vrchol vrchol = sused.getKey();
                int vzdialenost = sused.getValue();
                if (!prejdeneVrcholy.contains(vrchol)) {
                    vyratajNajmensiuVzdialenost(vrchol, vzdialenost, aktualnyVrchol);
                    neprejdeneVrcholy.add(vrchol);
                }
            }
            prejdeneVrcholy.add(aktualnyVrchol);
        }
    }

    private Vrchol najdiVrcholNajmensiaVzdialenost(Set<Vrchol> neprejdeneVrcholy) {
        Vrchol najdeny = null;
        int najdenaVzdialenost = Integer.MAX_VALUE;
        for (Vrchol vrchol : neprejdeneVrcholy) {
            int vzdialenost = vrchol.getVzdialenost();
            if (vzdialenost < najdenaVzdialenost) {
                najdenaVzdialenost = vzdialenost;
                najdeny = vrchol;
            }
        }
        return najdeny;
    }

    private void vyratajNajmensiuVzdialenost(Vrchol prehladavany, int vzdialenost, Vrchol zaciatok) {
        int zaciatocnaVzdialenost = zaciatok.getVzdialenost();
        if (zaciatocnaVzdialenost + vzdialenost < prehladavany.getVzdialenost()) {
            prehladavany.setVzdialenost(zaciatocnaVzdialenost + vzdialenost);
            LinkedList<Vrchol> najkratsiaTrasa = new LinkedList<>(zaciatok.getNajkratsiaTrasa());
            najkratsiaTrasa.add(zaciatok);
            prehladavany.setNajkratsiaTrasa(najkratsiaTrasa);
        }
    }

    private void inicializujVzdialenosti() {
        for (Vrchol vrchol : vrcholy.values()) {
            vrchol.setVzdialenost(Integer.MAX_VALUE);
        }
    }

}
