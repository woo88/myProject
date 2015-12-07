package kr.ac.kaist.kmap;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Woo on 2015. 12. 5..
 */
public class Nodes {
    private HashMap<String, String> node_id;

    public void setNodeIdMap() throws IOException {
        Category cat;
        int i;

        cat = new Category();
        cat.setMap("category");

        i = 0;
        node_id = new HashMap<>();
        for(String category : cat.getKeySet()) {
            i++;
            node_id.put(category, Integer.toString(i));
        }
    }


    public String getId(String category) {
        return node_id.get(category);
    }
}
