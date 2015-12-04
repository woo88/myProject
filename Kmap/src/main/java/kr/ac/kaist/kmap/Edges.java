package kr.ac.kaist.kmap;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;

/**
 * Created by Woo on 2015. 12. 4..
 */
public class Edges {
    private static String filename;
    private static Map<String, Set<String>> pageLinkMap = new DefaultHashMap<>(HashSet.class);
    private Map<String, Integer> edgeSizeMap= new HashMap<>();


    public Edges(String filename) {
        this.filename = filename;
    }

    public void putEdges(String key, Map<String, String> label_id) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
        String inputLine;
        Map<String, Set<String>> instance_id;
        ArrayList<Map<String, Object>> edges;
        HashMap resultMap;

        resultMap = new HashMap();
        App.putTimeSlot(resultMap);

        System.out.println("start reading page-links");

        while((inputLine = reader.readLine()) != null) {
            // Ignore comment lines.
            if(inputLine.startsWith("#")) {
                continue;
            }
            setPageLinkMap(inputLine);
        }
        reader.close();
//        System.out.println("\tnumber of instances including interlanguage information: " + map.size());
        System.out.println("\tfinish reading page-links");

        instance_id = setCategoryIdMap(label_id);
        edges = setEdgeSizeMap(instance_id);

        resultMap.put(key, edges);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File("edges.json"), resultMap);
    }

    private Map<String, Set<String>> setCategoryIdMap(Map<String, String> label_id) throws IOException {
        Category cat;
        Map<String, Set<String>> categoryIdMap = new DefaultHashMap<>(HashMap.class);

        cat = new Category();
        cat.setFileName(App.baseDir + App.filename_categories);
        cat.setMap("instance");

        for(String instance : cat.getKeySet()) {
            for(String category : cat.getValueSet(instance)) {
                categoryIdMap.get(instance).add(label_id.get(category));
            }
        }

        return categoryIdMap;
    }

    private ArrayList<Map<String, Object>> setEdgeSizeMap(Map<String, Set<String>> instance_id) throws IOException {
        String s; // category ID for FROM plus category ID for TO

        for(String from : pageLinkMap.keySet()) {
            Set<String> fromCategorySet = instance_id.get(from);
            for(String to : pageLinkMap.get(from)) {
                Set<String> toCategorySet = instance_id.get(to);
                for(String fromCategory : fromCategorySet) {
                    for(String toCategory : toCategorySet) {
                        // skip if FROM and TO are the same category
                        if(Objects.equals(fromCategory, toCategory)) continue;

                        s = fromCategory + "-" + toCategory;
                        Integer count = edgeSizeMap.get(s);
                        if(count == null) {
                            edgeSizeMap.put(s, 1);
                        } else {
                            edgeSizeMap.put(s, count + 1);
                        }
                    }
                }
            }
        }

        //

        ArrayList<Map<String, Object>> edges = new ArrayList<>();
        String[] strArr;
        int share_point;
        for(String from_to : edgeSizeMap.keySet()) {
            Map<String, Object> edge = new HashMap<>();
            Map<String, Integer> variables = new HashMap<>();

            variables.put("page-links", edgeSizeMap.get(from_to));
            variables.put("share-same-instance", 0);

            strArr = from_to.split("-", 2);
            share_point = 0;
            int edgeSize = edgeSizeMap.get(from_to) + share_point;

            edge.put("id", "0");
            edge.put("source", strArr[0]);
            edge.put("target", strArr[1]);
            edge.put("value", edgeSize);
            edge.put("variables", variables);
            edges.add(edge);
        }
        return edges;
    }

    private Set<String> getCategoryIdSet(String instance) {
        return null;
    }

    private static void setPageLinkMap(String s) {
        String[] strArr = s.split(" ", 4);
        String from = strArr[0];
        String to = strArr[2];
        String txt; // for checking prefix
        int idx; // index of the prefix

        txt = "/File:";
        idx = to.indexOf(txt);
        if(idx > -1) { // skip if the resource is a file
            return;
        } else { // remove prefix
            txt = "/resource/";
            idx = to.indexOf(txt);
            if(idx > -1) {
                to = to.substring(idx + txt.length(), to.length()-1);
            } else {
                System.out.println("[ERRO] there is no prefix /resource/");
                System.out.println(to);
            }
        }

        // remove prefix
        txt = "/resource/";
        idx = from.indexOf(txt);
        if(idx > -1) {
            from = from.substring(idx + txt.length(), from.length()-1);
        } else {
            System.out.println("[ERRO] there is no prefix /resource/");
            System.out.println(from);
        }

        pageLinkMap.get(from).add(to);
    }

    private static Set<String> getPageLinkMap(String s) {
        return pageLinkMap.get(s);
    }

    public static void main(String[] args) {
        setPageLinkMap("<http://dbpedia.org/resource/AcademyAwards>" +
                " <http://dbpedia.org/ontology/wikiPageWikiLink>" +
                " <http://dbpedia.org/resource/Academy_Awards>" +
                " .");
        setPageLinkMap("<http://dbpedia.org/resource/AcademicElitism>" +
                " <http://dbpedia.org/ontology/wikiPageWikiLink>" +
                " <http://dbpedia.org/resource/Ivory_tower>" +
                " .");
        setPageLinkMap("<http://dbpedia.org/resource/Albedo>" +
                " <http://dbpedia.org/ontology/wikiPageWikiLink>" +
                " <http://dbpedia.org/resource/File:Albedo-e_hg.svg>" +
                " .");
        setPageLinkMap("<http://dbpedia.org/resource/Albedo>" +
                " <http://dbpedia.org/ontology/wikiPageWikiLink>" +
                " <http://dbpedia.org/resource/Latin>" +
                " .");
        setPageLinkMap("<http://dbpedia.org/resource/Albedo>" +
                " <http://dbpedia.org/ontology/wikiPageWikiLink>" +
                " <http://dbpedia.org/resource/Diffuse_reflection>" +
                " .");

        System.out.println(getPageLinkMap("AcademyAwards"));
        System.out.println(getPageLinkMap("AcademicElitism"));
        System.out.println(getPageLinkMap("Albedo"));


    }
}
