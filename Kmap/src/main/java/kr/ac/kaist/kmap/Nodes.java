package kr.ac.kaist.kmap;

import java.io.*;
import java.util.*;

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

        TreeMap<String, String> nodeData = new TreeMap<>();
        readVocabFile(nodeData);

        // add data for each data
        for (String tmp : fileList) {
            String input = tmp + ".occ";

            readDataFile(nodeData, input);
        }

        // write nodeData to nodes.kmap
        writeNodes(nodeData);

//        System.out.println("nodeData.get(!!!_albums) test: " + nodeData.get("!!!_albums"));
    }

    private static void writeNodes(TreeMap<String, String> nodeData) {
        BufferedWriter writer;
        String data39;
        int score39 = 0;
        String data2014;
        int score2014 = 0;
        String data2015;
        int score2015 = 0;

        System.out.println("Start writing: " + App.nodesFile);
        try {
            writer = new BufferedWriter(new FileWriter(new File(App.nodesFile)));
            writer.write("#node_name " +
                    "timeslot/node_size/#instancesHavingType/#instancesRedirected/infoboxLength");
            writer.newLine();
        } catch (IOException e) {
            System.err.println(e);
            return;
        }
        for(String node : nodeData.keySet( ))
        {
            String data = nodeData.get(node);
            String[] strArr = data.split(" ");

            score39 = Integer.parseInt(strArr[0]) + Integer.parseInt(strArr[1]) + Integer.parseInt(strArr[2]);
            data39 = "3.9/" + score39 + "/" + strArr[0] + "/" + strArr[1] + "/" + strArr[2];

            score2014 = Integer.parseInt(strArr[3]) + Integer.parseInt(strArr[4]) + Integer.parseInt(strArr[5]);
            data2014 = "2014/" + score2014 + "/" + strArr[3] + "/" + strArr[4] + "/" + strArr[5];

            score2015 = Integer.parseInt(strArr[6]) + Integer.parseInt(strArr[7]) + Integer.parseInt(strArr[8]);
            data2015 = "2015-04/" + score2015 + "/" + strArr[6] + "/" + strArr[7] + "/" + strArr[8];

            if ((score39 + score2014 + score2015) == 0) continue;

            try {
                writer.write(node + " " + data39 + " " + data2014 + " " + data2015);
                writer.newLine();
            } catch (IOException e) {
                System.err.println(e);
                return;
            }
        }

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done");
        System.out.println();
    }

    private static void readDataFile(TreeMap<String, String> nodeData, String input) {
        BufferedReader reader;
        String inputLine;
        TreeMap<String, String> varData = new TreeMap<>();

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

        System.out.println("Start adding data");
        for (String node : nodeData.keySet()) {
            String data = nodeData.get(node);
            if (varData.containsKey(node)) {
                data = data + " " + varData.get(node);
            } else {
                data = data + " 0";
            }

            nodeData.put(node, data.trim());
        }
        System.out.println("Done!");
        System.out.println();
    }

    private static void readVocabFile(TreeMap<String, String> nodeData) {
        Scanner vocabFile;
        String vocab;
//        ArrayList data = new ArrayList();
        String data = "";
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
