package projekt.dijkstra;

import projekt.bean.FFDataWrapper;
import projekt.bean.Node;

import java.util.Map;

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

    private void alg() {
        ffDataWrapper.getToNodes().forEach(n -> {
            n.setConnectedTo(null);
        });

        int pocetPrepojeni = 0;

        for (Node toNode : ffDataWrapper.getToNodes()) {
//            boolean visited[] = new boolean[toNode.getAllPossibleConnection().size()];
//            for(int i = 0; i < toNode.getAllPossibleConnection().size(); ++i)
//                visited[i] = false;

            for (Node fromNode : toNode.getAllPossibleConnection()) {
                if (fromNode.getConnectedTo() != null)
                    continue;
                else {
                    fromNode.setConnectedTo(toNode);
                    pocetPrepojeni++;
                    break;
                }
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

            toNode.getAllPossibleConnection().sort((o1, o2) -> o1.getTime().compareTo(o2.getTime()));

        });
    }
}
