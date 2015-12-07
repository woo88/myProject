package kr.ac.kaist.kmap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by woo on 2015-12-07.
 */
public class Edges3 {
    private static String baseDir;
    private static String inputFileName;

    public static void setBaseDir(String baseDir) {
        Edges3.baseDir = baseDir;
    }

    public static void setInputFileName(String inputFileName) {
        Edges3.inputFileName = inputFileName;
    }

    public static void generateEdges() throws IOException {
        // setPageLinkToCategoryLink()
//        setPageLinkToCategoryLink();

        // external sort
//        sortCategoryLinks();

        // reducing
        countCategoryLinks();
    }

    private static void countCategoryLinks() throws IOException {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        String inputLine = null;
        String prevLine = null;
        int count = 1;

        reader = new BufferedReader(new FileReader(new File(baseDir + "res/sorted_category-links_en.txt")));
        writer = new BufferedWriter(new FileWriter(new File(baseDir + "res/result_category-links_en.txt")));
        prevLine = reader.readLine();
        while((inputLine = reader.readLine()) != null) {
            if(Objects.equals(inputLine, prevLine)) {
                count++;
            } else {
                writer.write(prevLine + "," + count); writer.newLine();
                count = 1;
            }
            prevLine = inputLine;
        }
        reader.close();
        writer.close();
    }

    private static void sortCategoryLinks() throws IOException {
        ExternalSort sort = new ExternalSort();
        boolean verbose = true;
        boolean distinct = false;
        int maxtmpfiles = 10;
        Charset cs = Charset.defaultCharset();
        String inputfile = null, outputfile = null;
        File tempFileStore = null;
        boolean usegzip = false;
        int headersize = 0;

        inputfile = baseDir + "res/category-links_en.txt";
        outputfile = baseDir + "res/sorted_category-links_en.txt";
        tempFileStore = new File(baseDir + "res/tmp/");

        Comparator<String> comparator = sort.defaultcomparator;
        List<File> l = sort.sortInBatch(new File(inputfile), comparator,
                maxtmpfiles, cs, tempFileStore, distinct, headersize,
                usegzip);
        if (verbose)
            System.out.println("created " + l.size() + " tmp files");
        sort.mergeSortedFiles(l, new File(outputfile), comparator, cs,
                distinct, false, usegzip);
    }

    private static void setPageLinkToCategoryLink() throws IOException {
        BufferedReader reader;
        String inputLine;
        BufferedWriter writer;

        // generate Map<"instance", Set<"category id">>
        Map<String, Set<String>> instanceToCategoryidSet = setInstanceToCategoryidSet();

        System.out.println("start reading " + inputFileName);
        reader = new BufferedReader(new FileReader(new File(baseDir + inputFileName)));
        writer = new BufferedWriter(new FileWriter(new File(baseDir + "res/category-links_en.txt")));
        while((inputLine = reader.readLine()) != null) {
            // ignore comment lines.
            if(inputLine.startsWith("#")) {
                System.out.println("\tskip this line: " + inputLine);
                continue;
            }

            String[] strArr = inputLine.split(" ", 4);
            String from = strArr[0];
            String to = strArr[2];
            String txt = "/File:";
            int idx = to.indexOf(txt);
            if(idx > -1) { // skip if the resource is a file
                continue;
            } else { // remove prefix
                to = removePrefix(to, "/resource/");
            }
            from = removePrefix(from, "/resource/");

            if(instanceToCategoryidSet.get(from) == null || instanceToCategoryidSet.get(to) == null) continue;

            for(String fromCategoryId : instanceToCategoryidSet.get(from)) {
                for(String toCategoryId : instanceToCategoryidSet.get(to)) {
                    // skip if FROM and TO are in the same category
                    if(Objects.equals(fromCategoryId, toCategoryId)) continue;

                    writer.write(fromCategoryId + "-" + toCategoryId); writer.newLine();
                }
            }
        }
        System.out.println("\tfinished");
        writer.close();
        reader.close();
    }

    private static Map<String, Set<String>> setInstanceToCategoryidSet() {
        Map<String, Set<String>> instanceToCategoryidSet = null;
        File f;
        ObjectMapper mapper;

        f = new File(baseDir + "res/instanceToCategoryidSet.json");
        if(f.isFile()) {
            System.out.println("start reading " + baseDir + "res/instanceToCategoryidSet.json");
            mapper = new ObjectMapper();
            // read JSON from a file
            try {
                instanceToCategoryidSet = mapper.readValue(
                        f,
                        new TypeReference<Map<String, Set<String>>>() {
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return instanceToCategoryidSet;
        }

        Category cat;
        Nodes nod;

        cat = new Category();
        try {
            cat.setMap("instance");
        } catch (IOException e) {
            e.printStackTrace();
        }

        nod = new Nodes();
        try {
            nod.setNodeIdMap();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("start generating Map<\"instance\", Set<\"category id\">>");
        instanceToCategoryidSet = new DefaultHashMap<>(HashSet.class);
        for(String instance : cat.getKeySet()) {
            for(String category : cat.getValueSet(instance)) {
                instanceToCategoryidSet.get(instance).add(nod.getId(category));
            }
        }

        mapper = new ObjectMapper();
        // write JSON to a file
        try {
            mapper.writeValue(f, instanceToCategoryidSet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\tMap size: " + instanceToCategoryidSet.size());
        System.out.println("\tfinished");
        return instanceToCategoryidSet;
    }

    private static String removePrefix(String s, String prefix) {
        int idx = s.indexOf(prefix);
        if(idx > -1) {
            s = s.substring(idx + prefix.length(), s.length()-1);
        } else {
            System.out.println("[ERRO] there is no prefix /resource/");
            System.out.println(s);
        }
        return s;
    }
}
