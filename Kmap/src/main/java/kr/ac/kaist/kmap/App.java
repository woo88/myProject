package kr.ac.kaist.kmap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;

/**
 * Hello world!
 *
 */
public class App {
//    protected static final String baseDir = "/home/woo88/dbpedia/2015-04/en/";
//    protected static final String baseDir = "/home/woo88/dbpedia/";
//    protected static final String filename_categories = "article-categories_en.nt";
//    protected static final String filename_categories = "article_categories_en.nt";
    private static final String filename_redirects = "redirects_en.nt";
    private static final String filename_infobox = "infobox-properties_en.nt";
    private static final String filename_types = "instance-types_en.nt";
    private static final String filename_page_length = "page-length_en.nt";
    private static final String filename_interlanguage = "interlanguage-links_en.nt";
    private static final String filename_freebase = "freebase-links_en.nt";
    private static final String filename_nytimes = "nytimes_links.nt";
    private static final String filename_yago_links = "yago_links.nt";
    private static final String filename_yago_types = "yago_types.nt";
//    protected static final String filename_page_links = "page-links_en.nt";
    protected static final String filename_page_links = "page_links_en.nt";
//    private static final String TIME_SLOT = "2015-04";
    private static final String TIME_SLOT = "2014";

    public static String vocabFile = null;
    public static String baseDir = null;
    public static ArrayList<String> categoriesFileList = new ArrayList<>();
    public static ArrayList<String> typesFileList = new ArrayList<>();
    public static ArrayList<String> redirectsFileList = new ArrayList<>();
    public static ArrayList<String> infoboxFileList = new ArrayList<>();

//    private static HashMap resultMap = new HashMap();

    public static void main(String[] args) throws IOException {
        vocabFile = "output/vocab.kmap";
        baseDir = "/home/woo88/dbpedia/";
        categoriesFileList.add("3.9/en/article_categories_en.nt");
        categoriesFileList.add("2014/en/article_categories_en.nt");
        categoriesFileList.add("2015-04/en/article-categories_en.nt");
        typesFileList.add("3.9/en/instance_types_en.nt");
        typesFileList.add("2014/en/instance_types_en.nt");
        typesFileList.add("2015-04/en/instance-types_en.nt");
        redirectsFileList.add("3.9/en/redirects_en.nt");
        redirectsFileList.add("2014/en/redirects_en.nt");
        redirectsFileList.add("2015-04/en/redirects_en.nt");
        infoboxFileList.add("3.9/en/raw_infobox_properties_en.nt");
        infoboxFileList.add("2014/en/infobox_properties_en.nt");
        infoboxFileList.add("2015-04/en/infobox-properties_en.nt");

        // id, timeslot, types, instances, redirects

        // if there is no vocab.kmap
        if(!checkFile("output/vocab.kmap")) {
            SortedSet<String> allCategories = null;
            allCategories = readAllCategories();

            // write vocabularies to file
            writeVocab(allCategories);
        }

        // preprocessing
        Category.writeInsToCat(baseDir, categoriesFileList);
        Category.convertInsToCat(categoriesFileList);
        Type.convertInsToCat(typesFileList);
        Redirect.convertInsToCat(redirectsFileList);
        Infobox.convertInsToCat(infoboxFileList);

//        Nodes.generateNodes(0, 10);

//        GenerateNodes();
//        GenerateEdges();

//        resultMap.put("timeslot", TIME_SLOT);
//        resultMap.put("nodes", nodes);
//        resultMap.put("edges", "test");

//        StringBuffer sbuf = new StringBuffer();
//        ObjectMapper mapper = new ObjectMapper();

//        mapper.writerWithDefaultPrettyPrinter().writeValue(new File("output2.json"), resultMap);
    }

    public static boolean checkFile(String s) {
        File f = new File(s);
        if(f.isFile()) {
            System.out.println("File exists: " + s);
            System.out.println("File size: " + f.length());
            System.out.println();
            return true;
        }
        return false;
    }

    private static void writeVocab(SortedSet<String> allCategories) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(vocabFile)));
        System.out.println("Start writing vocabulary of categories");
        for(String cat : allCategories) {
            writer.write(cat); writer.newLine();
        }
        writer.close();
        System.out.println("Done");
        System.out.println();
    }

    private static SortedSet<String> readAllCategories() throws IOException {
        String baseDir = "/home/woo88/dbpedia/";
        ArrayList fileList = new ArrayList();
        fileList.add("3.9/en/article_categories_en.nt");
        fileList.add("2014/en/article_categories_en.nt");
        fileList.add("2015-04/en/article-categories_en.nt");
        SortedSet<String> allCategories = new TreeSet();

        System.out.println("Start generating vocabulary of categories");
        for(Object filePath : fileList) {
            BufferedReader reader = new BufferedReader(new FileReader(new File(baseDir + filePath)));
            String inputLine;
            System.out.print(filePath + ", ");
            while((inputLine = reader.readLine()) != null) {
                // ignore comment lines.
                if(inputLine.startsWith("#")) continue;

                String[] strArr = inputLine.split(" ");
                String vocab = removePrefix(strArr[2], "/Category:");

                allCategories.add(vocab);
            }
            reader.close();
        }
        System.out.println();
        System.out.println("Vocabulary size: " + allCategories.size());
        System.out.println("Done");
        System.out.println();

        return allCategories;
    }

    protected static String removePrefix(String s, String prefix) {
        int idx = s.indexOf(prefix);
        if(idx > -1) {
            s = s.substring(idx + prefix.length(), s.length()-1);
        } else {
            System.out.println("[ERRO] there is no prefix " + prefix);
            System.out.println("\tin " + s);
        }
        return s;
    }

    private static void GenerateNodes() throws IOException {
        HashMap resultMap = new HashMap();
        putTimeSlot(resultMap);

        ArrayList<Map<String, Object>> nodes = new ArrayList<>();
        Category cat = new Category();
//        cat.setFileName(baseDir + filename_categories);
        cat.setMap("category");

        Redirect red = new Redirect(baseDir + filename_redirects);
        Infobox ib = new Infobox(baseDir + filename_infobox);
        Type type = new Type(baseDir + filename_types);
        PageLength pl = new PageLength(baseDir + filename_page_length);
        InterLanguage il = new InterLanguage(baseDir + filename_interlanguage);

        /**
         * variables
         *
         * "instances": 0,
         * "redirects": 0,
         * "infobox": 0,
         * "types": 0,
         * "page-length": 0,
         * "interlanguage": 0,
         * "freebase": 0,
         * "nytimes": 0,
         * "yago-intances": 0,
         * "yago-types": 0
         */

        int i = 0; // node ID
        for(String category : cat.getKeySet()) {
            Map<String, Integer> variables = new HashMap<>();
            int instances_point = cat.getValueSetSize(category);
            int redirect_point = 0;
            int infobox_point = 0;
            int type_point;
            int length_point = 0;
            int interlanguage_point = 0;

            for(String instance : cat.getValueSet(category)){
                redirect_point += red.getRedirect(instance);
                infobox_point += ib.getInfobox(instance);
                length_point += pl.getPageLength(instance);
                interlanguage_point += il.getValue(instance);
            }
            type_point = type.getIntersection(cat.getValueSet(category)).size();

            variables.put("instances", instances_point);
            variables.put("redirects", redirect_point);
            variables.put("infobox", infobox_point);
            variables.put("types", type_point);
            variables.put("page-length", length_point);
            variables.put("interlanguage", interlanguage_point);

            int node_size = 0;
            node_size += instances_point;
            node_size += redirect_point;
            node_size += infobox_point;
            node_size += type_point;
            node_size += length_point;
            node_size += interlanguage_point;

            //        HashMap node = new HashMap();
            Map<String, Object> node = new HashMap<>();
            i++; // node ID
            node.put("id", String.valueOf(i));
            node.put("label", category);
            node.put("value", node_size);
            node.put("variables", variables);

            nodes.add(node);
        }
        resultMap.put("nodes", nodes);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File("nodes.json"), resultMap);
    }

    protected static void putTimeSlot(HashMap resultMap) {
        resultMap.put("timeslot", TIME_SLOT);
        System.out.println("put timeslot information");
    }

    private static void GenerateEdges() throws IOException {
//        Edges e = new Edges(baseDir + filename_page_links);
//        e.putEdges("edges");

        Edges3 e = new Edges3();
        e.setBaseDir(baseDir);
        e.setInputFileName(filename_page_links);
        e.generateEdges();
    }

    public static void writeJson(Map<String, Set<String>> map, String s) {
        ObjectMapper mapper = new ObjectMapper();
        File f = new File(App.baseDir + "res/" + s);

        try {
            mapper.writeValue(f, map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isFile(String targetFileName) {
        File f = new File(baseDir + "res/" + targetFileName);

        if(f.isFile()) {
            return true;
        } else {
            return false;
        }
    }

    public static Map<String, Set<String>> readJson(String targetFileName) {
        Map<String, Set<String>> map = null;
        ObjectMapper mapper = new ObjectMapper();
        File f = new File(App.baseDir + "res/" + targetFileName);

        // read JSON from a file
        try {
            map = new DefaultHashMap<>(HashSet.class);
            map = mapper.readValue(
                    f,
                    new TypeReference<Map<String, Set<String>>>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}
