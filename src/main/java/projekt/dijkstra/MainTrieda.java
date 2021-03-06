/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekt.dijkstra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import projekt.dto.PocetSpojovDto;

/**
 *
 * @author Ja
 */
public class MainTrieda {

    public static void main(String[] args) {
        Graf graf = new Graf();
        ImportExportDat.nacitajData(graf);
        ImportExportDat.zapisMaticeVzdialenostiDoCsv(vypocitajZaciatokKoniecZoSuboru("/zGaraze.csv", graf), "zGarazeVysledky.csv");
        ImportExportDat.zapisMaticeVzdialenostiDoCsv(vypocitajZaciatokKoniecZoSuboru("/doGaraze.csv", graf), "doGarazeVysledky.csv");
        Map<Integer, Map<Integer, Integer>> mv = vypocitajZaciatokKoniecZoSuboru("/konceZaciatkySpojov.csv", graf);
        ImportExportDat.zapisMaticeVzdialenostiDoCsv(mv, "konceZaciatkySpojovVysledky.csv");

        TokVGrafeAlgorimtus tokVGrafeAlgorimtus = new TokVGrafeAlgorimtus();
        tokVGrafeAlgorimtus.addData(ImportExportDat.nacitajSpoje("/spoje.csv"), mv);
        Map<Integer, PocetSpojovDto> spojeSekundy = new HashMap<>();
        for (int i = 0; i < 1800; i += 30) {
            spojeSekundy.put(i, tokVGrafeAlgorimtus.process(i));
        }
        ImportExportDat.zapisSpojeSekundyDoCsv(spojeSekundy, "spojePocetSekund.csv");
    }

    private static Map<Integer, Map<Integer, Integer>> vypocitajZaciatokKoniecZoSuboru(String nazovSuboru, Graf graf) {
        List<Integer> zaciatky = new ArrayList<>();
        List<Integer> konce = new ArrayList<>();
        ImportExportDat.nacitajZaciatkyAKonce(zaciatky, konce, nazovSuboru);
        //prvy kluc je kod zaciatocnej zastavky, druhy kluc konecnej zastavky, hodnota je vzdialenost
        Map<Integer, Map<Integer, Integer>> vzdialenosti = new HashMap<>();
        for (Integer zaciatok : zaciatky) {
            graf.vypocitajVzdialenostiOdVrchola(graf.getVrcholy().get(zaciatok));
            Map<Integer, Integer> mapaKoncov = new HashMap<>();
            for (Integer koniec : konce) {
                mapaKoncov.put(koniec, graf.getVrcholy().get(koniec).getVzdialenost());
            }
            vzdialenosti.put(zaciatok, mapaKoncov);
        }
        return vzdialenosti;
    }

    private static void menuVypocitajVzdialenost(Graf graf) {
        Scanner sc = new Scanner(System.in);
        String vstup;
        Vrchol zaciatok;
        Vrchol koniec;
        System.out.println("Program na v??po??et najkrat??ej trasy medzi dvoma zast??vkami ur??en??ch unik??tnym k??dom. Pre ukon??enie stla?? 0.");
        while (true) {
            System.out.println("Zadaj k??d za??iato??nej zast??vky:");
            vstup = sc.nextLine();
            if (vstup.equals("0")) {
                break;
            }
            int kodZaciatku = Integer.valueOf(vstup);
            zaciatok = graf.getVrcholy().get(kodZaciatku);
            while (zaciatok == null) {
                System.out.println("Zast??vka s dan??m k??dom neexistuje, sk??s znova:");
                vstup = sc.nextLine();
                if (vstup.equals("0")) {
                    break;
                }
                kodZaciatku = Integer.valueOf(vstup);
                zaciatok = graf.getVrcholy().get(kodZaciatku);
            }
            System.out.println("Zadaj k??d kone??nej zast??vky:");
            vstup = sc.nextLine();
            if (vstup.equals("0")) {
                break;
            }
            int kodKonca = Integer.valueOf(vstup);
            koniec = graf.getVrcholy().get(kodKonca);
            while (koniec == null) {
                System.out.println("Zast??vka s dan??m k??dom neexistuje, sk??s znova:");
                vstup = sc.nextLine();
                if (vstup.equals("0")) {
                    break;
                }
                kodKonca = Integer.valueOf(vstup);
                koniec = graf.getVrcholy().get(kodKonca);
            }
            graf.vypocitajVzdialenostiOdVrchola(zaciatok);
            System.out.println("Najlep??ia trasa " + zaciatok.getNazov() + " -> " + koniec.getNazov() + ":");
            System.out.println("Trvanie trasy: " + koniec.getVzdialenost() + " sek??nd");
            System.out.print("Trasa: ");
            for (Vrchol vrchol : koniec.getNajkratsiaTrasa()) {
                System.out.print(vrchol.getNazov() + " -> ");
            }
            System.out.println(koniec.getNazov());
            System.out.println("------------------------------------");
            System.out.println("Nov?? trasa? 0 - nie, in?? znak - ??no");
            vstup = sc.nextLine();
            if (vstup.equals("0")) {
                break;
            } else {
                System.out.println("------------------------------------");
            }
        }
    }
}
