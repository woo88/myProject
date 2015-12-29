package kr.ac.kaist.kmap;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Created by Woo on 2015. 12. 5..
 */
public class Nodes {
    private HashMap<String, String> node_id;

    public static void generateNodes(ArrayList<String> fileList) throws IOException {
        // count numbers for each file
        for (String fileName : fileList) {
            String output = fileName + ".occ";

            if(App.checkFile(output)) continue;

            TreeMap<String, Integer> frequencyData = new TreeMap<String, Integer>( );
            WordCounter.readWordFile(frequencyData, fileName);
            WordCounter.writeAllCounts(frequencyData, output);
        }

        TreeMap<String, ArrayList> nodeData = new TreeMap<>();
        readVocabFile(nodeData);

        System.out.println("nodeData test: " + nodeData.get("!!!_albums").get(0));
    }

    private static void readVocabFile(TreeMap<String, ArrayList> nodeData) {
        Scanner vocabFile;
        String vocab;
        ArrayList data = new ArrayList();
        data.add("3.9");

        System.out.println("Start reading: " + App.vocabFile);
        try {
            vocabFile = new Scanner(new FileReader(App.vocabFile));
        } catch (FileNotFoundException e) {
            System.err.println(e);
            return;
        }

        while (vocabFile.hasNext()) {
            vocab = vocabFile.next();

            nodeData.put(vocab, data);
        }
        System.out.println("Done! size: " + nodeData.size());
        System.out.println();
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
