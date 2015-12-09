package kr.ac.kaist.gaussian_lda;

import java.io.*;
import java.util.HashMap;

/**
 * Created by Woo on 2015. 12. 10..
 */
public class App {
    private static String inputCorpus= "data/word2vec_sentence.txt";
    private static String inputVectors = "data/vectors.txt";
    private static String outVectors = "data/lda_vectors";

    public static void main( String[] args ) throws IOException {
        HashMap wordToIdx;

        // convert word to index for vectors file
        wordToIdx = genVectors();

        // convert word to index for corpus file
        genCorpus(wordToIdx);
    }

    private static void genCorpus(HashMap wordToIdx) {

    }

    private static HashMap genVectors() throws IOException {
        String inputLine = null;
        HashMap wordToIdx = new HashMap();
        int i = 1;

        System.out.println("Reading vectors file");

        BufferedReader reader = new BufferedReader(new FileReader(new File(inputVectors)));
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outVectors)));
        while((inputLine = reader.readLine()) != null) {
            String[] strArr = inputLine.split(" ", 2);

            if(strArr[1].length() == 0) {
                System.out.print(strArr[0] + ", ");
            }

            wordToIdx.put(strArr[0], i);
            i++;
            writer.write(Integer.toString(i) + " " + strArr[1]); writer.newLine();
        }
        reader.close();
        writer.close();

        System.out.println();
        System.out.println("finished");
        System.out.println();

        return wordToIdx;
    }
}
