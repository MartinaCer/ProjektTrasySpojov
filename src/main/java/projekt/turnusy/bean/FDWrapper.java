package projekt.turnusy.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FDWrapper {
    private FNode sink;
    private FNode source;

    private List<FNode> arrivals = new ArrayList<>();
    private List<FNode> departures = new ArrayList<>();

    private List<FEdge> allEdges = new ArrayList<>();
    private List<FNode> allNodes = new ArrayList<>();

    private Map<Integer, Map<Integer, Integer>> distances;

    private int globalOffset;
}
