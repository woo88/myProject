package kr.ac.kaist.kmap;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    public static void convertInsToCat(String baseDir, ArrayList<String> redirectsFileList) throws IOException {
        BufferedWriter writer;
        Map<String, String> insToCat;

        for(String fileName : redirectsFileList) {
            String[] strArr = fileName.split("/");
            String output = "output/" + strArr[0] + "/" + strArr[2];

            if(App.checkFile(output)) continue;

            writer = new BufferedWriter(new FileWriter(new File(output)));

            // get Map of instance to categories
            insToCat = Category.getInsToCat(strArr[0]);
            // convert
            convertInsToCatInner(baseDir, fileName, writer, insToCat);
            insToCat = null;

            writer.close();
            System.out.println("File is created: " + output);
            System.out.println();
        }
    }

    private static void convertInsToCatInner(String baseDir, String fileName,
                                                       BufferedWriter writer,
                                                       Map<String, String> insToCat) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(baseDir + fileName)));
        int lineNumber = 0;
        int totalLineNumber = 0;
        String inputLine = null;
        System.out.println("Start reading: " + baseDir + fileName);
        while ((inputLine = reader.readLine()) != null) {
            // check progress
            if (lineNumber >= 500000) {
                totalLineNumber += lineNumber;
                lineNumber = 0;
                System.out.print(totalLineNumber + ", ");
            }
            lineNumber++;

            // ignore comment lines.
            if(inputLine.startsWith("#")) continue;

            // tokenize
            String[] strArr = inputLine.split(" ");
            String ins = App.removePrefix(strArr[2], "/resource/");

            try {
                writer.write(insToCat.get(ins)); writer.newLine();
            } catch (NullPointerException e) {
                continue;
            }
        }
        reader.close();
        System.out.println("Done");
    }
}
