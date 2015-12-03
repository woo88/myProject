package kr.ac.kaist.kmap;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App {
    private static final String baseDir = "/home/woo88/dbpedia/2015-04/en/";
    private static final String filename_categories = "article-categories_en.nt";
    private static final String filename_redirects = "redirects_en.nt";
    private static final String filename_infobox_properties = "infobox-properties_en.nt";

    public static void main(String[] args) throws IOException {
        System.out.println("Hello World!!!");

        Category cat = new Category(baseDir + filename_categories);
        System.out.println(cat.getInstanceSet("Climate_forcing"));

        Redirect red = new Redirect(baseDir + filename_redirects);
        System.out.println(red.getRedirect("United_States"));

        Infobox ib = new Infobox(baseDir + filename_infobox_properties);
        System.out.println(ib.getInfobox("United_States"));
    }
}
