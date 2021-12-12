package projekt.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Node {

    private int nodeId;
    private String nodeName;

    //Linka
    private String line;
    //Spoj
    private String connection;

    private LocalTime time;
    private int km;
}
