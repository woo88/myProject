package kr.ac.kaist.gaussian_lda;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Created by Woo on 2015. 12. 10..
 */
public class App {
    private static String vocabFile = "data/bow.txt";

//    private static String inputVectors = "data/vectors.txt";
    private static String inputVectors = "data/vectors.glove.5d.txt";
    private static String outVectors = "data/vectors.glove.5d.lda.txt";

    private static String inputCorpus= "data/word2vec_sentence.txt";
    private static String outCorpus = "data/corpus.train";

    public static void main( String[] args ) throws IOException {
        HashMap<String, String> wordToIdx;
        ArrayList<String> vocabList;

        // get word index from bow.txt
        vocabList = getVocabList();

        System.out.println("[Index] enabling: " + vocabList.indexOf("enabling"));

        // convert word to index for vectors file
        genVectors(vocabList);

        // convert word to index for corpus file
        genCorpus(vocabList);
    }

    private static ArrayList<String> getVocabList() throws IOException {
        String inputLine = null;
        ArrayList<String> vocabList = new ArrayList<>();

        System.out.println("Reading bow file");

        BufferedReader reader = new BufferedReader(new FileReader(new File(vocabFile)));
        while((inputLine = reader.readLine()) != null) {
            if(inputLine.trim().isEmpty()) continue;

            vocabList.add(inputLine.trim());
        }
        reader.close();

        return vocabList;
    }

    private static void genCorpus(ArrayList<String> vocabList) throws IOException {
        String inputLine = null;
        int i = 0;

        System.out.println("Reading corpus file");

        BufferedReader reader = new BufferedReader(new FileReader(new File(inputCorpus)));
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outCorpus)));
        while((inputLine = reader.readLine()) != null) {
            if(inputLine.trim().isEmpty()) continue;

            String[] strArr = inputLine.split(" ");

            StringJoiner joiner = new StringJoiner(" ");
            for(String word : strArr) {
                int idx = vocabList.indexOf(word);
                if(idx == -1) {
                    System.out.print(word + ", ");
                }

                joiner.add(Integer.toString(idx));
            }

            if(joiner.length() == 0) {
                System.out.println(i + ": " + inputLine);
                return;
            }

            writer.write(joiner.toString()); writer.newLine();
            i++;
        }
        reader.close();
        writer.close();

        System.out.println("finished");
    }

    private static void genVectors(ArrayList<String> vocabList) throws IOException {
        String inputLine = null;
        HashMap<String, String> wordToVec = new HashMap();

        System.out.println("Reading vectors file");

        BufferedReader reader = new BufferedReader(new FileReader(new File(inputVectors)));
        while((inputLine = reader.readLine()) != null) {
            String[] strArr = inputLine.split(" ", 2);

            if(strArr[1].length() == 0) {
                System.out.print(strArr[0] + ", ");
                return;
            }

            wordToVec.put(strArr[0], strArr[1]);
        }
        reader.close();

        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outVectors)));
        for(String word : vocabList) {
            writer.write(wordToVec.get(word)); writer.newLine();
        }
        writer.close();

        System.out.println();
        System.out.println("finished");
        System.out.println();
    }
}
