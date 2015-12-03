package kr.ac.kaist.kmap;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by woo on 2015-12-03.
 */
public class PageLength {
    private static Map<String, Integer> map = new HashMap<>();

    public PageLength(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
        String inputLine;

        System.out.println("start reading page-length");

        while((inputLine = reader.readLine()) != null) {
            // Ignore comment lines.
            if(inputLine.startsWith("#")) {
                continue;
            }

            String[] strArr = inputLine.split(" ", 4);
            String s = strArr[0];

            // Remove prefix.
            String txt = "/resource/";
            int idx = s.indexOf(txt);
            if(idx > -1) {
                s = s.substring(idx + txt.length(), s.length()-1);
            } else {
                System.out.println("[ERRO] there is no prefix /resource/");
                System.out.println(s);
                break;
            }

            map.put(s, findNumber(strArr[2]));
        }
        reader.close();
//        System.out.println("\tnumber of instances including type information: " + map.size());
        System.out.println("\tfinish reading instance-types");
    }

    private static int findNumber(String s) {
        return Integer.parseInt(s.substring(1, s.indexOf("\"", 1)));
    }

    public static int getPageLength(String instance) {
        Integer i = map.get(instance);
        if(i == null) {
            return 0;
        } else {
            return i;
        }
    }

    public static void main(String[] args) {
        String s = "\"69\"^^<http://www.w3.org/2001/XMLSchema#nonNegativeInteger>";
        System.out.println(findNumber(s));
    }
}
