package kr.ac.kaist.kmap;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by woo on 2015-12-03.
 */
public class Type {
    private Set<String> set = new HashSet<>();

    public Type(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
        String inputLine;

        System.out.println("start reading instance-types");

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

            set.add(s);
        }
        reader.close();
        System.out.println("\tnumber of instances including type information: " + set.size());
        System.out.println("\tfinish reading instance-types");
    }

    public Set<String> getSet() {
        return set;
    }

    public Set<String> getIntersection(Set<String> s) {
        s.retainAll(set);
        return s;
    }

    public static void convertInsToCat(ArrayList<String> typesFileList) {

    }
}
