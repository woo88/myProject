package kr.ac.kaist.kmap;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App {
    private static final String baseDir = "/home/woo88/dbpedia/2015-04/en/";
    private static final String filename_categries = "article-categories_en.nt";
    private static final String filename_redirects = "redirects_en.nt";

    public static void main(String[] args) throws IOException {
        System.out.println("Hello World!!!");

//        Initializer init = new Initializer();
//        init.ReadRedirect(baseDir + filename_redirects);

        Redirect red = new Redirect(baseDir + filename_redirects);
        System.out.println(red.getRedirect("United_States"));
    }
}
