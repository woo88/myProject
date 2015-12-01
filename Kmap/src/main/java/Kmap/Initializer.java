package Kmap;

import java.io.*;

/**
 * Created by Woo on 2015. 12. 1..
 */
public class Initializer {
    private static final String baseDir = "/home/woo/dev/dbpedia2015-04/en/";
    private static final String filename_categries = "article-categories_en.nt";
    private static final String filename_redirects = "redirects_en.nt";

    public void Initialize() throws IOException {
        Initializer init = new Initializer();
        init.ReadNT(baseDir + filename_categries);
        init.ReadNT(baseDir + filename_redirects);
    }

    private void ReadNT(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
        String inputLine;

        while((inputLine = reader.readLine()) != null) {
            // Ignore comment lines.
            if(inputLine.startsWith("#"))
                continue;

            String[] strArr = inputLine.split(" ", 4);
            String s = strArr[0];
            String p = strArr[1];
            String o = strArr[2];
        }

        reader.close();
    }
}
