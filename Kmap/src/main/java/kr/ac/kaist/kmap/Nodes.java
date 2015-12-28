package kr.ac.kaist.kmap;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Woo on 2015. 12. 5..
 */
public class Nodes {
    private HashMap<String, String> node_id;

    public static void generateNodes(int from, int to) throws IOException {
        // loading vocab.kmap
        ArrayList<String> categories = loadVocab(from, to);

        // make map of instances


        // for each timeslot
        for(String fileName : App.typesFileList) {
            BufferedReader reader = new BufferedReader(new FileReader(new File(App.baseDir + fileName)));
            String inputLine = null;
            System.out.println("Start reading: " + fileName);
            while((inputLine = reader.readLine()) != null) {
                // ignore comment lines.
                if(inputLine.startsWith("#")) continue;

                // make matrix for type score
            }
            reader.close();
            System.out.println("Done!");
        }
    }

    private static ArrayList<String> loadVocab(int from, int to) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(App.vocabFile)));
        String inputLine;
        int i = 0;
        ArrayList<String> categories = new ArrayList<>();
        System.out.println("Start loading: " + App.vocabFile);
        while((inputLine = reader.readLine()) != null) {
            if(i >= to) break;

            categories.add(inputLine);

            i++;
        }
        reader.close();
        System.out.println("Done! size: " + categories.size());
        System.out.println();

        return categories;
    }

    public void setNodeIdMap() throws IOException {
        Category cat = null;
        int i = 0;
        BufferedWriter writer = null;

        cat = new Category();
        cat.setMap("category");

        i = 0;
        node_id = new HashMap<>();
        writer = new BufferedWriter(new FileWriter(new File(App.baseDir + "res/idToCategory.txt")));
        for(String category : cat.getKeySet()) {
            i++;
            node_id.put(category, Integer.toString(i));
            writer.write(i + " " + category); writer.newLine();
        }
        writer.close();
    }


    public String getId(String category) {
        return node_id.get(category);
    }
}
