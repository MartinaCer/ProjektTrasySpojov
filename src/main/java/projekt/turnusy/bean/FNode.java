package projekt.turnusy.bean;

import lombok.*;
import projekt.turnusy.enums.NodeType;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class FNode {
    private static int newId = 0;

    // unique id of node in graph
    private int nodeId;

    private NodeType nodeType;

    private int stationId;
    private String stationName;

    private int distanceInKm;

    private LocalTime time;

    // linka
    private String line;
    // spoj?
    private String vehicle;

    private int ULID;

    private List<FEdge> possibleEdgesGoIn = new ArrayList<>();
    private List<FEdge> possibleEdgesGoOut = new ArrayList<>();

    private List<FEdge> edgesGoIn = new ArrayList<>();
    private List<FEdge> edgesGoOut = new ArrayList<>();

    // node that belongs to same line (departure - arrival)
    private FNode siblingNode;


    public static int generateId() {
        return newId++;
    }
}
