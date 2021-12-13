package projekt.dijkstra;

import projekt.bean.FFDataWrapper;
import projekt.bean.Node;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class TokVGrafeAlgorimtus {

    private FFDataWrapper ffDataWrapper;
    private Map<Integer, Map<Integer, Integer>> distMatrix;

    private int globalOffsetSeconds;

    public TokVGrafeAlgorimtus addData(FFDataWrapper dataWrapper, Map<Integer, Map<Integer, Integer>> mv) {
        this.ffDataWrapper = dataWrapper;
        this.distMatrix = mv;
        return this;
    }

    public void process(int globalOffsetSeconds) {
        this.globalOffsetSeconds = globalOffsetSeconds;

        calculatePossibleConnections();
        alg();

    }

    private boolean al(Set<Node> set, Node n) {


        for (int i = 0; i < n.getAllPossibleConnection().size(); i++) {
            Node noderFrom = n.getAllPossibleConnection().get(i);
            if (!set.contains(noderFrom)) {
                set.add(noderFrom);

                if (noderFrom.getConnectedTo() == null || al(set, noderFrom.getConnectedTo())) {
                    noderFrom.setConnectedTo(n);
                    return true;
                }

            }

        }
        return false;
    }

    private void alg() {
        ffDataWrapper.getToNodes().forEach(n -> {
            n.setConnectedTo(null);
        });

        int pocetPrepojeni = 0;

        for (int i = 0; i < ffDataWrapper.getToNodes().size(); i++) {
            Set<Node> set = new TreeSet<Node>((o1, o2) -> Integer.compare(o1.getId(), o2.getId()));

            if (al(set, ffDataWrapper.getToNodes().get(i))) {
                pocetPrepojeni++;
            }

        }

        System.out.println("Celkovy pocet spojov: " + ffDataWrapper.getToNodes().size());
        System.out.println("Pocet prepojeni: " + pocetPrepojeni);
        System.out.println("Pocet turnusov: " + (ffDataWrapper.getToNodes().size() - pocetPrepojeni));
    }

    private void calculatePossibleConnections() {
        ffDataWrapper.getToNodes().forEach(toNode -> {
            ffDataWrapper.getFromNodes().forEach(fromNode -> {
                int travelBetweenDistInSec = distMatrix.get(toNode.getNodeId()).get(fromNode.getNodeId());
                travelBetweenDistInSec += globalOffsetSeconds;

                if (toNode.getTime().plusSeconds(travelBetweenDistInSec).isBefore(fromNode.getTime())) {
                    toNode.getAllPossibleConnection().add(fromNode);
                }

            });

            //toNode.getAllPossibleConnection().sort((o1, o2) -> o1.getTime().compareTo(o2.getTime()));

        });
    }
}
