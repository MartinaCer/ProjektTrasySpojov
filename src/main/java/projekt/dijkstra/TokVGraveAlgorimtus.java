package projekt.dijkstra;

import lombok.Data;
import projekt.bean.FFDataWrapper;

public class TokVGraveAlgorimtus {

    private FFDataWrapper ffDataWrapper;

    public TokVGraveAlgorimtus addData(FFDataWrapper dataWrapper) {
        this.ffDataWrapper = dataWrapper;
        return this;
    }

    public void process() {
    }
}
