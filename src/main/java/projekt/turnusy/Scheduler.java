package projekt.turnusy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import projekt.turnusy.bean.FDWrapper;
import projekt.turnusy.bean.FEdge;
import projekt.turnusy.bean.FNode;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Scheduler {
    private FDWrapper data;

    private static int MAX = 1000000;
    private static int MIN = -100000000;

    private int turnus = 0; //len pre test alg

    private void setSource(FNode node) {
        data.setSource(node);
    }

    private void setSink(FNode node) {
        data.setSink(node);
    }

    public void process(int globalOffsetSeconds) {
        data.setGlobalOffset(globalOffsetSeconds);
        calculatePossibleConnections();
        createConnectionsForSourceSink();
        calculateSourceSinkFlow();
        calculateConnections();
        minimizeEmptyTransit();

    }

    private void calculateConnections() {
        int pocetPrepojeni = 0;

        for (int i = 0; i < data.getArrivals().size(); i++) {
            Set<FNode> set = new TreeSet<FNode>((o1, o2) -> Integer.compare(o1.getNodeId(), o2.getNodeId()));

            if (alg(set, data.getArrivals().get(i))) {
                pocetPrepojeni++;
            }

        }

        System.out.println("Celkovy pocet spojov: " + data.getArrivals().size());
        System.out.println("Pocet prepojeni: " + pocetPrepojeni);
        System.out.println("Pocet turnusov: " + (data.getArrivals().size() - pocetPrepojeni));

        turnus = pocetPrepojeni;

        int o = data.getArrivals().stream().mapToInt(n -> n.getEdgesGoOut().size()).sum();
        System.out.println(o);

        o = data.getDepartures().stream().mapToInt(n -> n.getEdgesGoIn().size()).sum();
        System.out.println(o);

    }

    private boolean alg(Set<FNode> set, FNode arrival) {

        for (int i = 0; i < arrival.getPossibleEdgesGoOut().size(); i++) {
            FEdge edge = arrival.getPossibleEdgesGoOut().get(i);
            FNode departure = edge.getOppositeNode(arrival);

            if (!set.contains(departure)) {
                set.add(departure);

                if (departure.getEdgesGoIn().isEmpty()) {
                    edge.setFlow(1);
                    departure.getEdgesGoIn().add(edge);
                    if (!arrival.getEdgesGoOut().isEmpty()) {
                        arrival.getEdgesGoOut().get(0).setFlow(0);
                        arrival.getEdgesGoOut().clear();
                    }
                    arrival.getEdgesGoOut().add(edge);
                    return true;
                } else if (alg(set, departure.getEdgesGoIn().get(0).getOppositeNode(departure))) {

                    if (!arrival.getEdgesGoOut().isEmpty()) {
                        arrival.getEdgesGoOut().get(0).setFlow(0);
                        arrival.getEdgesGoOut().clear();
                    }
                    arrival.getEdgesGoOut().add(edge);

                    departure.getEdgesGoIn().get(0).setFlow(0);

                    departure.getEdgesGoIn().clear();

                    edge.setFlow(1);

                    departure.getEdgesGoIn().add(edge);

                    return true;
                }

            }

        }
        return false;
    }

    public void minimizeEmptyTransit() {
        int f = 0;

        for (FEdge edge : data.getAllEdges()) {
            if (edge.getFlow() > 0) {
                f += edge.getPrice();
            }
        }

        System.out.println("Sum of empty transits in seconds: " + f);

        Map<Integer, Map<Integer, FEdge>> edgesMap = new HashMap<>();
        data.getAllEdges().forEach(edge -> {
            if (!edgesMap.containsKey(edge.getNodeFrom().getNodeId())) {
                edgesMap.put(edge.getNodeFrom().getNodeId(), new HashMap<>());
            }

            edgesMap.get(edge.getNodeFrom().getNodeId()).put(edge.getNodeTo().getNodeId(), edge);
        });


//        FNode n0 = FNode.builder().nodeId(0).build();
//        FNode n1 = FNode.builder().nodeId(1).build();
//        FNode n2 = FNode.builder().nodeId(2).build();
//        FNode n3 = FNode.builder().nodeId(3).build();
//        FNode n4 = FNode.builder().nodeId(4).build();
//
//        edgesMap.put(0, new HashMap<>());
//        edgesMap.get(0).put(1, FEdge.builder().nodeFrom(n0).nodeTo(n1).flow(1).price(1).build());
//
//        edgesMap.put(1, new HashMap<>());
//        edgesMap.get(1).put(2, FEdge.builder().nodeFrom(n1).nodeTo(n2).flow(0).price(2).build());
//        edgesMap.get(1).put(3, FEdge.builder().nodeFrom(n1).nodeTo(n3).flow(1).price(3).build());
//
//        edgesMap.put(2, new HashMap<>());
//        edgesMap.get(2).put(4, FEdge.builder().nodeFrom(n2).nodeTo(n4).flow(0).price(2).build());
//
//        edgesMap.put(3, new HashMap<>());
//        edgesMap.get(3).put(4, FEdge.builder().nodeFrom(n2).nodeTo(n4).flow(1).price(3).build());

        while (true) {
            int D[][] = new int[data.getArrivals().size() * 2 + 2][data.getArrivals().size() * 2 + 2];
            int X[][] = new int[data.getArrivals().size() * 2 + 2][data.getArrivals().size() * 2 + 2];

//            int D[][] = new int[5][5];
//            int X[][] = new int[5][5];

            for (int i = 0; i < D.length; i++) {
                for (int j = 0; j < D.length; j++) {
                    if (edgesMap.containsKey(j) && edgesMap.get(j).containsKey(i) && edgesMap.get(j).get(i).getFlow() > 0) {
                        D[i][j] = -edgesMap.get(j).get(i).getPrice();
                        if (i == 0) {
                            System.out.println(D[i][j]);
                        }
                        X[i][j] = i;
                    } else if (edgesMap.containsKey(i) && edgesMap.get(i).containsKey(j) && edgesMap.get(i).get(j).getFlow() < 1 &&
                            (!edgesMap.containsKey(j) || !edgesMap.get(j).containsKey(i) || edgesMap.get(j).get(i).getFlow() == 0)) {
                        D[i][j] = edgesMap.get(i).get(j).getPrice();
                        X[i][j] = i;
                    } else {
                        D[i][j] = MAX;
                        X[i][j] = MAX;
                    }

                }
            }

//            int INF = 1000;
//
//            int D[][] = {
//                    {INF, INF, INF, INF, INF},
//                    {-1, INF, 2, INF, INF},
//                    {INF, -2, INF, INF, 2},
//                    {INF, -3, INF, INF, INF},
//                    {INF, INF, INF, -3, INF},
//            };
//            int X[][] = {
//                    {INF, INF, INF, INF, INF},
//                    {-1, INF, 1, INF, INF},
//                    {INF, 2, INF, INF, 2},
//                    {INF, 3, INF, INF, INF},
//                    {INF, INF, INF, 4, INF},
//            };

            boolean foundNegativeCycle = false;
            int j1 = -1;

            //floyd
            for (int k = 0; k < D.length && !foundNegativeCycle; k++) {
                for (int i = 0; i < D.length && !foundNegativeCycle; i++) {
                    for (int j = 0; j < D.length && !foundNegativeCycle; j++) {

                        int sum = D[i][k] + D[k][j];
                        if (sum > MAX)
                            sum = MAX;

                        if (sum < MIN) {
                            sum = MIN;
                        }

                        if (sum < D[i][j]) {//(D[i][k] != Integer.MAX_VALUE && D[k][j] != Integer.MAX_VALUE && D[i][k] + D[k][j] < D[i][j]) { //D[i][k] != Integer.MAX_VALUE && D[k][j] != Integer.MAX_VALUE &&
                            D[i][j] = sum;

                            X[i][j] = X[k][j];

                            if (i == j && D[i][j] < 0) {
                                foundNegativeCycle = true;
                                j1 = i;
                            }
                        }
                    }
                }
            }

            if (j1 != -1) {
                List<Integer> path = new ArrayList<>();
                int temp = j1;

                while (!path.contains(X[j1][temp])) { //path.size() < 2 || path.get(path.size() - 1) != X[j1][j1]) {
                    path.add(X[j1][temp]);
                    temp = X[j1][temp];
                }

                int ind = path.indexOf(X[j1][temp]);
                path = path.subList(ind, path.size());
                path.add(X[j1][temp]);

                System.out.println("Negative cycle: " + path);

                for (int i = 0; i < path.size() - 1; i++) {
                    if (edgesMap.containsKey(path.get(i)) && edgesMap.get(path.get(i)).containsKey(path.get(i + 1))) {
//                        System.out.println(edgesMap.get(path.get(i)).get(path.get(i + 1)));
                        if (edgesMap.get(path.get(i)).get(path.get(i + 1)).getFlow() == 0) {
                            edgesMap.get(path.get(i)).get(path.get(i + 1)).setFlow(1);
                        } else {
                            edgesMap.get(path.get(i)).get(path.get(i + 1)).setFlow(0);
                        }
//                        System.out.println(edgesMap.get(path.get(i)).get(path.get(i + 1)));
                    } else {
//                        System.out.println(edgesMap.get(path.get(i + 1)).get(path.get(i)));
                        if (edgesMap.get(path.get(i + 1)).get(path.get(i)).getFlow() == 0) {
                            edgesMap.get(path.get(i + 1)).get(path.get(i)).setFlow(1);
                        } else {
                            edgesMap.get(path.get(i + 1)).get(path.get(i)).setFlow(0);
                        }
//                        System.out.println(edgesMap.get(path.get(i + 1)).get(path.get(i)));
                    }
                }

            } else {
                break;
            }

            f = 0;

            for (FEdge edge : data.getAllEdges()) {
                if (edge.getFlow() > 0) {
                    f += edge.getPrice();
                }
            }

//            long p = data.getAllEdges().stream().filter(e -> e.getNodeFrom().getNodeType() != null && e.getNodeTo().getNodeType() != null && e.getFlow() > 0).count();
//            System.out.println(p);
//            if (p != turnus) {
//                System.out.println("nesedi pocet turnusov");
//                return;
//            }

            System.out.println("Sum of empty transits in seconds: " + f);
        }
    }


    private void calculateSourceSinkFlow() {
        data.getArrivals().forEach(arrival -> {
            if (!arrival.getEdgesGoOut().isEmpty()) {
                arrival.getPossibleEdgesGoIn().get(0).setFlow(1);
                arrival.getEdgesGoIn().addAll(arrival.getPossibleEdgesGoIn());
            }
        });

        data.getDepartures().forEach(depparture -> {
            if (!depparture.getEdgesGoIn().isEmpty()) {
                depparture.getPossibleEdgesGoOut().get(0).setFlow(1);
                depparture.getEdgesGoOut().addAll(depparture.getPossibleEdgesGoOut());
            }
        });
    }

    public void createConnectionsForSourceSink() {
        data.getDepartures().forEach(departure -> {

            FEdge edge = FEdge.builder()
                    .nodeFrom(departure)
                    .nodeTo(data.getSink())
                    .price(0)
                    .flow(0)
                    .build();

            departure.getPossibleEdgesGoOut().add(edge);
            data.getSink().getPossibleEdgesGoIn().add(edge);
            data.getAllEdges().add(edge);
        });

        data.getArrivals().forEach(arrival -> {

            FEdge edge = FEdge.builder()
                    .nodeFrom(data.getSource())
                    .nodeTo(arrival)
                    .price(0)
                    .flow(0)
                    .build();

            arrival.getPossibleEdgesGoIn().add(edge);
            data.getSource().getPossibleEdgesGoOut().add(edge);
            data.getAllEdges().add(edge);
        });
    }

    public void calculatePossibleConnections() {
        data.getArrivals().forEach(arrival -> {
            data.getDepartures().forEach(departure -> {
                int travelBetweenDistInSec = data.getDistances().get(arrival.getStationId()).get(departure.getStationId());

                if (arrival.getTime().plusSeconds(travelBetweenDistInSec + data.getGlobalOffset()).isBefore(departure.getTime())) {
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
    }
}
