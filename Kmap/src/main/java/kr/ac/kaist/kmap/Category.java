package kr.ac.kaist.kmap;

import java.io.*;
import java.util.*;

/**
 * Created by woo on 2015-12-01.
 */
public class Category {
    private Map<String, Set<String>> map = null;
    private static String filename = null;
    private static String targetFileName = null;

    public void setMap(String s) throws IOException {
        int keyIdx;
        int valueIdx;

        if(Objects.equals(filename, null)) {
            filename = App.baseDir + App.categoriesFileList.get(2);
        }


        if(Objects.equals(s, "category")) {
            targetFileName = "categoryToInstances.json";
            keyIdx = 2;
            valueIdx = 0;
        } else if(Objects.equals(s, "instance")) {
            targetFileName = "instanceToCategories.json";
            keyIdx = 0;
            valueIdx = 2;
        } else {
            return;
        }

        if(App.isFile(targetFileName)) {
           map = App.readJson(targetFileName);
        } else {
            readData(keyIdx, valueIdx, s);
        }
    }

    private void readData(int keyIdx, int valueIdx, String s) throws IOException {
        BufferedReader reader = null;
        String inputLine = null;

        System.out.println("start reading " + filename);

        reader = new BufferedReader(new FileReader(new File(filename)));
        map = new DefaultHashMap<>(HashSet.class);
        while((inputLine = reader.readLine()) != null) {
            // Ignore comment lines.
            if(inputLine.startsWith("#")) {
                System.out.println("\tskip the line: " + inputLine);
                continue;
            }

            String[] strArr = inputLine.split(" ", 4);
            String k = RemovePrefix(strArr[keyIdx]);
            String v = RemovePrefix(strArr[valueIdx]);
            map.get(k).add(v);
        }
        reader.close();
        System.out.println("\tnumber of " + s + ": " + map.size());
        System.out.println("\tfinished");

        App.writeJson(map, targetFileName);
    }

    private boolean checkExistingFile(String s) {
        if(Objects.equals(s, "category")) {

        } else if(Objects.equals(s, "instance")) {

        } else {

        }
        return false;
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

    public Integer getValueSetSize(String key) {
        return map.get(key).size();
    }

    public void setFileName(String filename) {
        Category.filename = filename;
    }

    public static void writeInsToCat(String baseDir, ArrayList<String> categoriesFileList) throws IOException {
        // loading vocab.kmap
        ArrayList<String> vocabList = loadVocab();

        for(String fileName : categoriesFileList) {
            String[] strArr = fileName.split("/");
            String output = "output/" + strArr[0] + strArr[2];
            if(App.checkFile(output)) {
                System.out.println("File exists: " + output);
                continue;
            }

            BufferedReader reader = new BufferedReader(new FileReader(new File(baseDir + fileName)));
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(output)));
            String inputLine = null;
            System.out.println("Start reading: " + fileName);
            while ((inputLine = reader.readLine()) != null) {
                // ignore comment lines.
                if(inputLine.startsWith("#")) continue;
            }
            System.out.println("Done");
            System.out.println();
            reader.close();
            writer.close();
        }
    }

    private static ArrayList<String> loadVocab() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(App.vocabFile)));
        String inputLine;
        ArrayList<String> categories = new ArrayList<>();
        System.out.println("Start loading: " + App.vocabFile);
        while((inputLine = reader.readLine()) != null) {
            categories.add(inputLine);
        }
        reader.close();
        System.out.println("Done! size: " + categories.size());
        System.out.println();

        return categories;
    }

    public static void convertInsToCat(ArrayList<String> categoriesFileList) {

    }
}
