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

    static int lastFileNumber;

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
        TreeMap<String, Integer> vocabData;
        String inputfile;

        // loading vocab.kmap
        inputfile = App.vocabFile;
        vocabData = new TreeMap<>();
        loadVocab(inputfile, vocabData);

        for(String fileName : categoriesFileList) {
            String[] strArr = fileName.split("/");
            String output = "output/" + strArr[0] + "/" + strArr[2];
            if(App.checkFile(output)) continue;

            // make directory
            String mkFolder = "output/" + strArr[0];
            File desti = new File(mkFolder);
            if(!desti.exists()) {
                desti.mkdirs();
                System.out.println("Directory is created: " + mkFolder);
            }

            BufferedReader reader = new BufferedReader(new FileReader(new File(baseDir + fileName)));
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(output)));
//            Map<String, ArrayList<String>> map = new DefaultHashMap<>(ArrayList.class);
            int lineNumber = 0;
            int totalLineNumber = 0;
            String inputLine = null;
            String prevIns = "";
            boolean notFirstLine = false;
            System.out.println("Start reading: " + fileName);
            while ((inputLine = reader.readLine()) != null) {
                // ignore comment lines.
                if(inputLine.startsWith("#")) continue;

                // tokenize
                strArr = inputLine.split(" ");
                String ins = App.removePrefix(strArr[0], "/resource/");
                String cat = App.removePrefix(strArr[2], "/Category:");

                if (Objects.equals(ins, prevIns)) {
                    writer.write(" " + vocabData.get(cat));
                } else {
                    if (notFirstLine) writer.newLine();
                    writer.write(ins + " " + vocabData.get(cat));
                }
                notFirstLine = true;
                prevIns = ins;

//                map.get(ins).add(cat);
                if (lineNumber >= 500000) {
                    totalLineNumber += lineNumber;
                    lineNumber = 0;
                    System.out.print(totalLineNumber + ", ");
                }
                lineNumber++;
            }
            System.out.println("Done");
            System.out.println("File is created: " + output);
            System.out.println();
            reader.close();
            writer.close();
        }
    }

    private static void loadVocab(String inputfile, TreeMap<String, Integer> vocabData) throws IOException {
        BufferedReader reader;
        String inputLine;
        Integer id;

        reader = new BufferedReader(new FileReader(new File(inputfile)));
        id = 0;

        System.out.println("Start loading: " + inputfile);
        while((inputLine = reader.readLine()) != null) {
            vocabData.put(inputLine, id);
            id++;
        }
        reader.close();
        System.out.println("Done!");
        System.out.println();
    }

    public static void convertInsToCat(String baseDir, ArrayList<String> categoriesFileList) throws IOException {
        String[] strArr;
        String output;
        BufferedReader reader;
        BufferedWriter writer;
        String inputLine;

        for (String inputfile : categoriesFileList) {
            strArr = inputfile.split("/");
            output = "output/" + strArr[0] + "/" + strArr[2] + "2";

            if(App.checkFile(output)) continue;

            reader = new BufferedReader(new FileReader(new File(baseDir + inputfile)));
            writer = new BufferedWriter(new FileWriter(new File(output)));

            System.out.println("Start reading: " + inputfile);
            while ((inputLine = reader.readLine()) != null) {
                strArr = inputLine.split(" ", 2);
                writer.write(strArr[1]); writer.newLine();
            }
            writer.close();
            reader.close();
            System.out.println("File is created: " + output);
            System.out.println();
        }
    }

    public static Map<String, String> getInsToCat(String timeslot) throws IOException {
        BufferedReader reader = null;
        String input = null;
        int lineNumber;
        double totalLineNumber;

        for (String catFile : App.categoriesFileList) {
            String[] strArr = catFile.split("/");

            if (Objects.equals(timeslot, strArr[0])) {
                input = "output/" + strArr[0] + "/" + strArr[2];
                break;
            } else {
                continue;
            }
        }

        String inputLine = null;
        lineNumber = 0;
        totalLineNumber = 0;
        reader = new BufferedReader(new FileReader(new File(input)));
        Map<String, String> map = new HashMap<>();
        System.out.println("Start loading: " + input);
        while ((inputLine = reader.readLine()) != null) {
            // check progress
//            if (lineNumber >= 1000000) {
//                totalLineNumber += lineNumber;
//                lineNumber = 0;
//                System.out.print(totalLineNumber + ", ");
//            }
//            lineNumber++;

            String[] strArr = inputLine.split(" ", 2);

            map.put(strArr[0], strArr[1]);
        }
        reader.close();
        System.out.println("Done");
//        System.out.println("Albedo has 6 categories: " + map.get("Albedo"));
        System.out.println();
        return map;
    }

    public static void writeOverlapsData(String input, String output) {
        TreeMap<String, Integer> overlapsData;

        // preprocessing for counting occurrences of overlap
        writeOverlapsTemp(input, output + ".tmp");

        for (int i = 0; i <= lastFileNumber; i++) {
            // count occurrences of overlap
            overlapsData = new TreeMap<>();
            WordCounter.readWordFile(overlapsData, output + ".tmp" + i);

            // delete temp file
            App.fileDelete(output + ".tmp" + i);

            // write overlaps data into file
            WordCounter.writeAllCounts(overlapsData, output + i);
        }
    }

    private static void writeOverlapsTemp(String input, String output) {
        BufferedReader reader;
        String inputLine;
        String[] strArr;
        String tmp1;
        String tmp2;
        StringJoiner joiner;
        BufferedWriter writer;
        int lineNumber;
        int totalLineNumber;
        int tokenNumber;
        int limitTokenNumber;
//        int tmpFileNumber;

        lastFileNumber = 0;

        if (App.checkFile(output + lastFileNumber)) return;

        try {
            reader = new BufferedReader(new FileReader(new File(input)));
        } catch (FileNotFoundException e) {
            System.err.println(e);
            return;
        }

        try {
            writer = new BufferedWriter(new FileWriter(new File(output + lastFileNumber)));
        } catch (IOException e) {
            System.err.println(e);
            return;
        }

        System.out.println("Start reading: " + input);
        lineNumber = 0;
        totalLineNumber = 0;
        tokenNumber = 0;
        limitTokenNumber = 5000000;
        try {
            while ((inputLine = reader.readLine()) != null) {
                // check progress
                if (lineNumber >= 500000) {
                    totalLineNumber += lineNumber;
                    lineNumber = 0;
                    System.out.print(totalLineNumber + ", ");
                }
                lineNumber++;

                // check token number (file size)
                if (tokenNumber > limitTokenNumber) {
                    tokenNumber = 0;
                    writer.close();
                    System.out.println("File is created: " + output + lastFileNumber);
                    lastFileNumber++;
                    writer = new BufferedWriter(new FileWriter(new File(output + lastFileNumber)));
                }

                strArr = inputLine.split(" ");

                // make overlaps data (combination)
                joiner = new StringJoiner(" ");
                for (int i = 1; i < strArr.length-1; i++) {
                    for (int j = i+1; j < strArr.length; j++) {
                        tmp1 = strArr[i] + "/" + strArr[j];
                        tmp2 = strArr[j] + "/" + strArr[i];
                        joiner.add(tmp1);
                        joiner.add(tmp2);
                        tokenNumber++;
                    }
                }

                //white overlaps to file
                writer.write(joiner.toString()); writer.newLine();
            }
        } catch (IOException e) {
            System.err.println(e);
            return;
        }

        try {
            writer.close();
        } catch (IOException e) {
            System.err.println(e);
            return;
        }

        System.out.println("File is created: " + output + lastFileNumber);

        try {
            reader.close();
        } catch (IOException e) {
            System.err.println(e);
            return;
        }

        System.out.println("Done! last temp file number: " + lastFileNumber);
        System.out.println();
    }

    public static void writeOverlapsData2(String input, String output) throws IOException {
        String inputfile;
        String outputfile;

        // preprocessing for counting occurrences of overlap
        inputfile = input;
        outputfile = output + ".tmp";
        writeOverlapsTemp2(inputfile, outputfile);

        // count occurrences of overlap
        inputfile = outputfile;
        outputfile = output;
        WordCounter.readWordFile(inputfile, outputfile);
    }

    private static void writeOverlapsTemp2(String input, String output) throws IOException {
        BufferedReader reader;
        String inputLine;
        String[] strArr;
        String tmp1;
        String tmp2;
        StringJoiner joiner;
        BufferedWriter writer;
        int lineNumber;
        double totalLineNumber;

        if (App.checkFile(output)) return;

        reader = new BufferedReader(new FileReader(new File(input)));
        writer = new BufferedWriter(new FileWriter(new File(output)));
        lineNumber = 0;
        totalLineNumber = 0;

        System.out.println("Start reading: " + input);
        while ((inputLine = reader.readLine()) != null) {
            // check progress
            if (lineNumber >= 1000000) {
                totalLineNumber += lineNumber;
                lineNumber = 0;
                System.out.print(totalLineNumber + ", ");
            }
            lineNumber++;

            strArr = inputLine.split(" ");

            // make overlaps data (combination)
            joiner = new StringJoiner(" ");
            for (int i = 1; i < strArr.length-1; i++) {
                for (int j = i+1; j < strArr.length; j++) {
                    tmp1 = strArr[i] + "/" + strArr[j];
                    tmp2 = strArr[j] + "/" + strArr[i];
                    joiner.add(tmp1);
                    joiner.add(tmp2);
                }
            }
            //white overlaps to file
            writer.write(joiner.toString()); writer.newLine();
        }
        writer.close();
        reader.close();
        System.out.println("Done!");
        System.out.println("File is created: " + output);
        System.out.println();
    }

    public static void main(String[] args) {
        String inputLine;
        String[] strArr;
        String tmp1;
        String tmp2;

        inputLine = "ins1 cat1 cat2 cat3 cat4 cat5";
        strArr = inputLine.split(" ");

        for (int i = 1; i < strArr.length-1; i++) {
            for (int j = i+1; j < strArr.length; j++) {
                tmp1 = strArr[i] + "/" + strArr[j];
                tmp2 = strArr[j] + "/" + strArr[i];
                System.out.println(tmp1);
                System.out.println(tmp2);
            }
        }
    }
}
