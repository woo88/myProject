package kr.ac.kaist.kmap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by woo on 2015-12-01.
 */
public class Category {
    // "category": [instances]
    private Map<String, Set<String>> map = new DefaultHashMap<>(HashSet.class);

    public Category(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
        String inputLine;

        System.out.println("start reading categories");

//        int i = 0;
        while((inputLine = reader.readLine()) != null) {
            // Ignore comment lines.
            if(inputLine.startsWith("#"))
                continue;

            String[] strArr = inputLine.split(" ", 4);
            String s = RemovePrefix(strArr[0]);
//            String p = strArr[1];
            String o = RemovePrefix(strArr[2]);

            map.get(o).add(s);

//            i++;
//            if(i > 2000000)
//                break;
        }

        reader.close();
        System.out.println("\tnumber of categories: " + map.size());
        System.out.println("\tfinish reading categories");
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

    public Set<String> getInstanceSet(String key) {
        return map.get(key);
    }

    public Set<String> getCategories() {
        return map.keySet();
    }

    public Integer getCategorySize(String category) {
        return map.get(category).size();
    }
}
