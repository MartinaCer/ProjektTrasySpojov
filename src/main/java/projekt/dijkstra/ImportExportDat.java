/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekt.dijkstra;

import projekt.bean.FFDataWrapper;
import projekt.bean.Node;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import projekt.dto.PocetSpojovDto;

/**
 *
 * @author Ja
 */
public final class ImportExportDat {

    private ImportExportDat() {
    }

    public static void nacitajData(Graf graf) {
        try {
            InputStream is = ImportExportDat.class.getResourceAsStream("/useky.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String riadok;
            while ((riadok = br.readLine()) != null) {
                String[] usek = riadok.split(";");
                int kodZaciatku = Integer.valueOf(usek[0]);
                String nazovZaciatku = usek[1];
                int kodKonca = Integer.valueOf(usek[2]);
                String nazovKonca = usek[3];
                int vzdialenost = Integer.valueOf(usek[4]);
                Vrchol zaciatok = graf.getVrcholy().computeIfAbsent(kodZaciatku, key -> new Vrchol(kodZaciatku, nazovZaciatku));
                Vrchol koniec = graf.getVrcholy().computeIfAbsent(kodKonca, key -> new Vrchol(kodKonca, nazovKonca));
                zaciatok.pridajSuseda(koniec, vzdialenost);
            }
            br.close();
            is.close();
        } catch (IOException ex) {
        }
    }

    public static void nacitajZaciatkyAKonce(List<Integer> zaciatky, List<Integer> konce, String nazovSuboru) {
        try {
            InputStream is = ImportExportDat.class.getResourceAsStream(nazovSuboru);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String riadok;
            while ((riadok = br.readLine()) != null) {
                String[] data = riadok.split(";");
                if (data.length == 1) {
                    zaciatky.add(Integer.valueOf(data[0]));
                } else {
                    if (!data[0].isEmpty()) {
                        zaciatky.add(Integer.valueOf(data[0]));
                    }
                    konce.add(Integer.valueOf(data[1]));
                }
            }
            br.close();
            is.close();
        } catch (IOException ex) {
        }
    }

    public static FFDataWrapper nacitajSpoje(String nazovSuboru) {
        FFDataWrapper ffDataWrapper = FFDataWrapper.builder()
                .fromNodes(new ArrayList<>())
                .toNodes(new ArrayList<>())
                .build();

        try {
            InputStream is = ImportExportDat.class.getResourceAsStream(nazovSuboru);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String riadok;

            int id = 0;

            while ((riadok = br.readLine()) != null) {
                String[] data = riadok.split(";");

                String fTime = data[4].length() == 8 ? data[4] : "0" + data[4];
                String tTime = data[7].length() == 8 ? data[7] : "0" + data[7];

                Node fromNode = Node.builder()
                        .id(id)
                        .nodeId(Integer.parseInt(data[2]))
                        .nodeName(data[3])
                        .line(data[0])
                        .connection(data[1])
                        .km(Integer.parseInt(data[8]))
                        .time(LocalTime.parse(fTime))
                        .allPossibleConnection(new ArrayList<>())
                        .build();

                Node toNode = Node.builder()
                        .id(id)
                        .nodeId(Integer.parseInt(data[5]))
                        .nodeName(data[6])
                        .line(data[0])
                        .connection(data[1])
                        .km(Integer.parseInt(data[8]))
                        .time(LocalTime.parse(tTime))
                        .allPossibleConnection(new ArrayList<>())
                        .build();

                ffDataWrapper.getFromNodes().add(fromNode);

                ffDataWrapper.getToNodes().add(toNode);

                id++;

            }
            br.close();
            is.close();
        } catch (IOException ex) {
        }

        return ffDataWrapper;
    }

    public static void zapisMaticeVzdialenostiDoCsv(Map<Integer, Map<Integer, Integer>> vzdialenosti, String nazovSuboru) {
        try (PrintWriter writer = new PrintWriter(nazovSuboru)) {
            StringBuilder sb = new StringBuilder();
            for (Integer value : vzdialenosti.entrySet().iterator().next().getValue().keySet()) {
                sb.append(";");
                sb.append(value);
            }
            sb.append("\n");
            for (Map.Entry<Integer, Map<Integer, Integer>> entry : vzdialenosti.entrySet()) {
                sb.append(entry.getKey());
                for (Integer integer : entry.getValue().values()) {
                    sb.append(";");
                    sb.append(integer);
                }
                sb.append("\n");
            }
            writer.write(sb.toString());
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void zapisSpojeSekundyDoCsv(Map<Integer, PocetSpojovDto> spojeSekundy, String nazovSuboru) {
        try (PrintWriter writer = new PrintWriter(nazovSuboru)) {
            StringBuilder sb = new StringBuilder();
            sb.append("pocet sekund;spoje celkovo;pocet prepojeni;pocet turnusov\n");
            for (Map.Entry<Integer, PocetSpojovDto> entry : spojeSekundy.entrySet()) {
                PocetSpojovDto dto = entry.getValue();
                sb.append(entry.getKey());
                sb.append(";");
                sb.append(dto.getSpojeCelkovo());
                sb.append(";");
                sb.append(dto.getPocetPrepojeni());
                sb.append(";");
                sb.append(dto.getSpojeCelkovo() - dto.getPocetPrepojeni());
                sb.append("\n");
            }
            writer.write(sb.toString());
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
