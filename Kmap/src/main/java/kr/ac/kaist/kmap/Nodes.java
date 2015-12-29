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
        // add data for each data
        for (String tmp : fileList) {
            String input = tmp + ".occ";

            readDataFile(nodeData, input);
            System.out.println("\tnodeData.get(!!!_albums) test: " + nodeData.get("!!!_albums"));
        }
    }

    private static void readDataFile(TreeMap<String, ArrayList> nodeData, String input) {
        BufferedReader reader;
        String inputLine;
        TreeMap<String, String> varData = new TreeMap<>();
//        ArrayList dataList;

        System.out.println("Start reading: " + input);
        try {
            reader = new BufferedReader(new FileReader(new File(input)));
        } catch (FileNotFoundException e) {
            System.err.println(e);
            return;
        }

        try {
            while ((inputLine = reader.readLine()) != null) {
                String[] strArr = inputLine.split(" ");

                varData.put(strArr[0], strArr[1]);
            }
        } catch (IOException e) {
            System.err.println(e);
            return;
        }

        try {
            reader.close();
        } catch (IOException e) {
            System.err.println(e);
            return;
        }
        System.out.println("Done! size: " + varData.size());
//        System.out.println("\tvarData.get(!!!_albums) test: " + varData.get("!!!_albums"));

        System.out.println("Start adding data");
        for (String node : nodeData.keySet()) {
            ArrayList dataList = nodeData.get(node);
            if (varData.containsKey(node)) {
                dataList.add(varData.get(node));
            } else {
                dataList.add("0");
            }
            nodeData.put(node, dataList);
            dataList = null;
        }
        System.out.println("Done!");
        System.out.println();
    }

    private static void readVocabFile(TreeMap<String, ArrayList> nodeData) {
        Scanner vocabFile;
        String vocab;
        ArrayList data = new ArrayList();
//        data.add("3.9");

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
