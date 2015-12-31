package kr.ac.kaist.kmap;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by woo on 2015-12-03.
 */
public class Infobox {
    /**
     * "instance name": number of infobox-properties
     * e.g.,
     * "United_States": 102
     */
    private Map<String, Integer> map = new HashMap<>();

    public Infobox(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
        String inputLine;

        System.out.println("start reading infobx-properties");

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

            setInfobox(s);
        }
        reader.close();
        System.out.println("\tnumber of instances including infobox: " + map.size());
        System.out.println("\tfinish reading infobox-properties");
    }

    private void setInfobox(String key) {
        Integer count = map.get(key);
        if(count == null) {
            map.put(key, 1);
        } else {
            map.put(key, count + 1);
        }
    }

    public Integer getInfobox(String key) {
        Integer i = map.get(key);
        if(i == null) {
            return 0;
        } else {
            return i;
        }
    }

    public static void convertInsToCat(String baseDir, ArrayList<String> infoboxFileList) throws IOException {
        BufferedWriter writer;
        Map<String, String> insToCat;

        for(String fileName : infoboxFileList) {
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
            String ins = App.removePrefix(strArr[0], "/resource/");

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
