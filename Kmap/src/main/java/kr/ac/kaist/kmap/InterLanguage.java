package kr.ac.kaist.kmap;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by woo on 2015-12-03.
 */
public class InterLanguage {
    private static Map<String, Integer> map = new HashMap<>();

    public InterLanguage(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
        String inputLine;

        System.out.println("start reading interlanguage");

        while((inputLine = reader.readLine()) != null) {
            // Ignore comment lines.
            if(inputLine.startsWith("#")) {
                continue;
            }

            String[] strArr = inputLine.split(" ", 4);
            setValue(strArr[0]);
        }
        reader.close();
        System.out.println("\tnumber of instances including interlanguage information: " + map.size());
        System.out.println("\tfinish reading interlanguage");
    }

    private static void setValue(String s) {
        // Remove prefix.
        String txt = "/resource/";
        int idx = s.indexOf(txt);
        if(idx > -1) {
            s = s.substring(idx + txt.length(), s.length()-1);
        } else {
            System.out.println("[ERRO] there is no prefix /resource/");
            System.out.println(s);
        }

        Integer count = map.get(s);
        if(count == null) {
            map.put(s, 1);
        } else {
            map.put(s, count + 1);
        }
    }

    public static int getValue(String instance) {
        Integer i = map.get(instance);
        if(i == null) {
            return 0;
        } else {
            return i;
        }
    }

    public static void main(String[] args) {
        setValue("<http://dbpedia.org/resource/Jack_Bauer>");
        setValue("<http://dbpedia.org/resource/Jack_Bauer>");
        setValue("<http://dbpedia.org/resource/Jack_Bauer>");
        setValue("<http://dbpedia.org/resource/Vivian_Brown>");
        setValue("<http://dbpedia.org/resource/Vivian_Brown>");
        System.out.println(getValue("Jack_Bauer"));
        System.out.println(getValue("Vivian_Brown"));
        System.out.println(getValue("Null_Test"));
    }
}
