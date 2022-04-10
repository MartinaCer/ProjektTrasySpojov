package projekt.turnusy.bean;

import lombok.*;

@Getter
@Setter
@Builder
public class FEdge {
    private FNode nodeFrom;
    private FNode nodeTo;

    // 1/0
    private int flow;

    // time in minutes
    private int price;


    public FNode getOppositeNode(FNode node) {
        if (nodeFrom == node) {
            return nodeTo;
        } else if (nodeTo == node) {
            return nodeFrom;
        }
        return null; // xD
    }

    @Override
    public String toString() {
        return "FEdge{" +
                "nodeFrom=" + nodeFrom.getNodeId() +
                "nodeFrom=" + nodeFrom.getNodeType() +
                ", nodeTo=" + nodeTo.getNodeId() +
                ", nodeTo=" + nodeTo.getNodeType() +
                ", flow=" + flow +
                ", price=" + price +
                '}';
    }
}
