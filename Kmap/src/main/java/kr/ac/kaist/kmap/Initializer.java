package kr.ac.kaist.kmap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by woo on 2015-12-01.
 */
public class Initializer {
//    public static final String baseDir = "/home/woo/dev/dbpedia2015-04/en/";
    public static final String baseDir = "/home/woo88/dbpedia/2015-04/en/";
    public static final String filename_categries = "article-categories_en.nt";
    public static final String filename_redirects = "redirects_en.nt";

    public void Initialize() throws IOException {
        Initializer init = new Initializer();
        init.ReadNT(baseDir + filename_categries);
        init.ReadRedirect(baseDir + filename_redirects);
    }

    private void ReadNT(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
        String inputLine;

        // "category": [instances]
        Map<String, HashSet<String>> map = new DefaultHashMap<>(HashSet.class);

        while((inputLine = reader.readLine()) != null) {
            // Ignore comment lines.
            if(inputLine.startsWith("#"))
                continue;

            String[] strArr = inputLine.split(" ", 4);
            String s = RemovePrefix(strArr[0]);
//            String p = strArr[1];
            String o = RemovePrefix(strArr[2]);

            map.get(o).add(s);
        }

        reader.close();

        System.out.println(map.get("Climate_forcing"));
    }

    private void ReadRedirect(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
        String inputLine;

        Map<String, Integer> map = new HashMap<>();

        while((inputLine = reader.readLine()) != null) {
            // Ignore comment lines.
            if(inputLine.startsWith("#"))
                continue;

            String[] strArr = inputLine.split(" ", 4);
            String o = strArr[2];

            // Remove prefix.
            String txt = "/resource/";
            int idx = txt.indexOf(txt);
            if(idx > -1) {
                o = o.substring(idx + txt.length(), o.length()-1);
            } else {
                System.out.println("there is no prefix /resource/");
                System.out.println(o);
                break;
            }

            Integer count = map.get(o);
            if (count == null) {
                map.put(o, 1);
            }
            else {
                map.put(o, count + 1);
            }
        }
    }

    /**
     * <http://dbpedia.org/resource/
     * <http://dbpedia.org/resource/Category:
     * @param s
     * @return
     */
    public String RemovePrefix(String s) {
        String txt = "/Category:";
        int idx = s.indexOf(txt);
        if(idx > -1) {
            s = s.substring(idx + txt.length(), s.length()-1);
            return s;
        }

        txt = "/resource/";
        idx = s.indexOf(txt);
        if(idx > -1) {
            s = s.substring(idx + txt.length(), s.length()-1);
            return s;
        }

        return s;
    }
}
