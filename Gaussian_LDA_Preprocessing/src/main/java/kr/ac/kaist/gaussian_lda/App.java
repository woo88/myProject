package kr.ac.kaist.gaussian_lda;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Created by Woo on 2015. 12. 10..
 */
public class App {
    private static String inputVectors = "data/vectors.txt";
    private static String outVectors = "data/lda_vectors.txt";

    private static String inputCorpus= "data/word2vec_sentence.txt";
    private static String outCorpus = "data/lda_corpus.txt";

    public static void main( String[] args ) throws IOException {
        HashMap<String, String> wordToIdx;

        // convert word to index for vectors file
        wordToIdx = genVectors();

        // convert word to index for corpus file
        genCorpus(wordToIdx);
    }

    private static void genCorpus(HashMap<String, String> wordToIdx) throws IOException {
        String inputLine = null;
        int i = 0;

        System.out.println("Reading corpus file");

        BufferedReader reader = new BufferedReader(new FileReader(new File(inputCorpus)));
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outCorpus)));
        while((inputLine = reader.readLine()) != null) {

            String[] strArr = inputLine.split(" ");

            StringJoiner joiner = new StringJoiner(" ");
            for(String word : strArr) {
                String s = wordToIdx.get(word);
                if(Objects.equals(s, null)) continue;

                joiner.add(s);
            }
            writer.write(joiner.toString()); writer.newLine();

            if(i < 20) System.out.println(joiner.toString());
            if(Objects.equals(joiner.toString(), "null"))
                System.out.println(i + ": " + inputLine);
            i++;
        }
        reader.close();
        writer.close();

        System.out.println("finished");
    }

    private static HashMap genVectors() throws IOException {
        String inputLine = null;
        HashMap<String, String> wordToIdx = new HashMap();
        int i = 1;

        System.out.println("Reading vectors file");

        BufferedReader reader = new BufferedReader(new FileReader(new File(inputVectors)));
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outVectors)));
        while((inputLine = reader.readLine()) != null) {
            String[] strArr = inputLine.split(" ", 2);

            if(strArr[1].length() == 0) {
                System.out.print(strArr[0] + ", ");
            }

            wordToIdx.put(strArr[0], Integer.toString(i));
            writer.write(i + " " + strArr[1]); writer.newLine();
            i++;
        }
        reader.close();
        writer.close();

        System.out.println();
        System.out.println("finished");
        System.out.println();

        return wordToIdx;
    }
}
