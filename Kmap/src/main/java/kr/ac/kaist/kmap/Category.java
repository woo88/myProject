package kr.ac.kaist.kmap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by woo on 2015-12-01.
 */
public class Category {
    // "category": [instances]
    private Map<String, Set<String>> map = new DefaultHashMap<>(HashSet.class);
    private String filename = App.baseDir + App.filename_categories;

    public void setMap(String s) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
        String inputLine;
        String k;
        String v;

        System.out.println("start reading " + App.filename_categories);

//        int i = 0;
        while((inputLine = reader.readLine()) != null) {
            // Ignore comment lines.
            if(inputLine.startsWith("#"))
                continue;

            String[] strArr = inputLine.split(" ", 4);
            if(Objects.equals(s, "category")) {
                k = RemovePrefix(strArr[2]);
                v = RemovePrefix(strArr[0]);
            } else if(Objects.equals(s, "instance")) {
                k = RemovePrefix(strArr[0]);
                v = RemovePrefix(strArr[2]);
            } else {
                return;
            }
            map.get(k).add(v);

//            i++;
//            if(i > 2000000)
//                break;
        }

        reader.close();
        System.out.println("\tnumber of " + s + ": " + map.size());
        System.out.println("\tfinished");
    }

    /**
     * <http://dbpedia.org/resource/
     * <http://dbpedia.org/resource/Category:
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

    public Set<String> getValueSet(String key) {
        return map.get(key);
    }

    public Set<String> getKeySet() {
        return map.keySet();
    }

    public Integer getValueSetSize(String category) {
        return map.get(category).size();
    }

    public void setFileName(String filename) {
        this.filename = filename;
    }
}
