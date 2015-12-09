package kr.ac.kaist.gaussian_lda;

import java.io.File;

/**
 * Created by Woo on 2015. 12. 10..
 */
public class App {
    private static String inputCorpus= "data/word2vec_sentence.txt";

    public static void main( String[] args ) {
        File f = new File(inputCorpus);
        System.out.println(f.isFile());
    }
}
