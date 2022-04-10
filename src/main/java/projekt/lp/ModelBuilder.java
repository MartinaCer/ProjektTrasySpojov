package projekt.lp;

import projekt.bean.FFDataWrapper;
import projekt.turnusy.bean.FDWrapper;
import projekt.turnusy.bean.FEdge;
import projekt.turnusy.bean.FNode;

import java.io.*;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ModelBuilder {

    private FDWrapper data;
    private Map<Integer, Map<Integer, Integer>> distMatrix;

    private int globalOffsetSeconds;

    public ModelBuilder addData(FDWrapper dataWrapper, Map<Integer, Map<Integer, Integer>> mv) {
        this.data = dataWrapper;
        this.distMatrix = mv;
        return this;
    }

    public void calculatePossibleConnections() {
        data.getArrivals().forEach(arrival -> {
            data.getDepartures().forEach(departure -> {
                int travelBetweenDistInSec = data.getDistances().get(arrival.getStationId()).get(departure.getStationId());

                if (arrival.getTime().getHour() * 3600 + arrival.getTime().getMinute() * 60 + arrival.getTime().getSecond() + travelBetweenDistInSec + data.getGlobalOffset() <
                        departure.getTime().getHour() * 3600 + departure.getTime().getMinute() * 60 + departure.getTime().getSecond()) {

//                if (arrival.getTime().plusSeconds(travelBetweenDistInSec + data.getGlobalOffset()).isBefore(departure.getTime())) {
                    FEdge edge = FEdge.builder()
                            .nodeFrom(arrival)
                            .nodeTo(departure)
                            .price(travelBetweenDistInSec / 60)
                            .flow(0)
                            .build();

                    arrival.getPossibleEdgesGoOut().add(edge);
                    departure.getPossibleEdgesGoIn().add(edge);
                    data.getAllEdges().add(edge);

                }
            });
        });

        FNode bb = data.getArrivals().stream().max((a, b) -> a.getTime().compareTo(b.getTime())).get();

        int as = 1;
    }

    public void createModel() {
        calculatePossibleConnections();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("filename.txt"), "utf-8"))) {
            writer.write("Maximize\n");
            writer.write("obj: ");

            AtomicInteger pc = new AtomicInteger();

            data.getArrivals().forEach(toNodes -> {
                toNodes.getPossibleEdgesGoOut().forEach(fromNode -> {
                    try {
                        writer.write("x" + fromNode.getNodeFrom().getULID() + "_" + fromNode.getNodeTo().getULID());
                        if (pc.get() > 50) {
                            writer.write("\n +");
                            pc.set(0);
                        } else {
                            writer.write("+");
                        }
                        pc.getAndIncrement();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });

            writer.write("\n\n");
            writer.write("Subject To\n");

            AtomicInteger i = new AtomicInteger();
            data.getArrivals().forEach(toNodes -> {
                if (toNodes.getPossibleEdgesGoOut().size() != 0) {


                    try {
                        writer.write("c" + i + ":");
                        i.getAndIncrement();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    toNodes.getPossibleEdgesGoOut().forEach(fromNode -> {
                        try {
                            writer.write("x" + fromNode.getNodeFrom().getULID() + "_" + fromNode.getNodeTo().getULID());
                            if (pc.get() > 50) {
                                writer.write("\n +");
                                pc.set(0);
                            } else {
                                writer.write("+");
                            }
                            pc.getAndIncrement();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    try {
                        writer.write(" <= 1\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            data.getDepartures().forEach(fromNode -> {
                if (fromNode.getPossibleEdgesGoIn().size() != 0) {
                    System.out.println("aa + " + fromNode.getULID() + " " + i);

                    try {
                        writer.write("c" + i + ":");
                        i.getAndIncrement();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    fromNode.getPossibleEdgesGoIn().forEach(toNodes -> {
                        try {
                            writer.write("x" + toNodes.getNodeFrom().getULID() + "_" + toNodes.getNodeTo().getULID());
                            if (pc.get() > 50) {
                                writer.write("\n +");
                                pc.set(0);
                            } else {
                                writer.write("+");
                            }
                            pc.getAndIncrement();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    try {
                        writer.write(" <= 1\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


            writer.write("\n\n");
            writer.write("Binaries\n");

            data.getArrivals().forEach(toNodes -> {
                toNodes.getPossibleEdgesGoOut().forEach(fromNode -> {
                    try {
                        writer.write("x" + fromNode.getNodeFrom().getULID() + "_" + fromNode.getNodeTo().getULID() + " ");
                        if (pc.get() > 50) {
                            writer.write("\n");
                            pc.set(0);
                        }
                        pc.getAndIncrement();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });

            writer.write("\nEND");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
