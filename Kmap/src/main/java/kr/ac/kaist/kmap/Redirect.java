package kr.ac.kaist.kmap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by woo on 2015-12-03.
 */
public class Redirect {
    /**
     * "instance name": number of redirects
     * e.g.,
     * "United_States": 169
     */
    private Map<String, Integer> map = new HashMap<>();

    public Redirect(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
        String inputLine;

        System.out.println("start reading redirects");

        while((inputLine = reader.readLine()) != null) {
            // Ignore comment lines.
            if(inputLine.startsWith("#")) {
                continue;
            }

            String[] strArr = inputLine.split(" ", 4);
            String o = strArr[2];

            // Remove prefix.
            String txt = "/resource/";
            int idx = o.indexOf(txt);
            if(idx > -1) {
                o = o.substring(idx + txt.length(), o.length()-1);
            } else {
                System.out.println("there is no prefix /resource/");
                System.out.println(o);
                break;
            }

            setRedirect(o);
        }
        reader.close();
        System.out.println("\tnumber of instances including redirects: " + map.size());
        System.out.println("\tfinish reading redirects");
    }

    private void setRedirect(String key) {
        Integer count = map.get(key);
        if(count == null) {
            map.put(key, 1);
        } else {
            map.put(key, count + 1);
        }
    }

    public Integer getRedirect(String key) {
        Integer i = map.get(key);
        if(i == null) {
            return 0;
        } else {
            return i;
        }
    }

    public static void convertInsToCat(ArrayList<String> redirectsFileList) {

    }
}
