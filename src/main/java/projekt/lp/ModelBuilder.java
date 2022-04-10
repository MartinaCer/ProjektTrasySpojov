package projekt.lp;

import projekt.bean.FFDataWrapper;

import java.io.*;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ModelBuilder {

    private FFDataWrapper ffDataWrapper;
    private Map<Integer, Map<Integer, Integer>> distMatrix;

    private int globalOffsetSeconds;

    public ModelBuilder addData(FFDataWrapper dataWrapper, Map<Integer, Map<Integer, Integer>> mv) {
        this.ffDataWrapper = dataWrapper;
        this.distMatrix = mv;
        return this;
    }

    private void calculatePossibleConnections() {
        AtomicInteger i = new AtomicInteger();
        ffDataWrapper.getToNodes().forEach(toNode -> {
            ffDataWrapper.getFromNodes().forEach(fromNode -> {
                int travelBetweenDistInSec = distMatrix.get(toNode.getNodeId()).get(fromNode.getNodeId());
                travelBetweenDistInSec += globalOffsetSeconds;

                if (toNode.getTime().plusSeconds(travelBetweenDistInSec).isBefore(fromNode.getTime())) {
                    toNode.getAllPossibleConnection().add(fromNode);
                    i.getAndIncrement();
                }

            });

            //toNode.getAllPossibleConnection().sort((o1, o2) -> o1.getTime().compareTo(o2.getTime()));
        });
        System.out.println(i);
    }

    public void createModel() {
        calculatePossibleConnections();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("filename.txt"), "utf-8"))) {
            writer.write("Maximize\n");
            writer.write("obj: ");

            AtomicInteger pc = new AtomicInteger();

            ffDataWrapper.getToNodes().forEach(toNodes -> {
                toNodes.getAllPossibleConnection().forEach(fromNode -> {
                    try {
                        writer.write("x" + toNodes.getId() + "_" + fromNode.getId());
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
            ffDataWrapper.getToNodes().forEach(toNodes -> {
                try {
                    writer.write("c" + i + ":");
                    i.getAndIncrement();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                toNodes.getAllPossibleConnection().forEach(fromNode -> {
                    try {
                        writer.write("x" + toNodes.getId() + "_" + fromNode.getId());
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
            });



            writer.write("\n\n");
            writer.write("Binaries\n");

            ffDataWrapper.getToNodes().forEach(toNodes -> {
                toNodes.getAllPossibleConnection().forEach(fromNode -> {
                    try {
                        writer.write("x" + toNodes.getId() + "_" + fromNode.getId() + " ");
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

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
