package kr.ac.kaist.kmap;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;

/**
 * Created by Woo on 2015. 12. 4..
 */
public class Edges {
    private static String filename;
    private Map<String, Integer> edgeSizeMap;

    public Edges(String filename) {
        this.filename = filename;
    }

    public void putEdges(String key) throws IOException {
        HashMap<String, Set<String>> pageLinkMap;
        BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
        String inputLine;
        Map<String, Set<String>> instance_id;
        ArrayList<Map<String, Object>> edges;
        HashMap resultMap;

        resultMap = new HashMap();
        App.putTimeSlot(resultMap);

        System.out.println("start reading page-links");

        pageLinkMap = new DefaultHashMap<>(HashSet.class);
        while((inputLine = reader.readLine()) != null) {
            // Ignore comment lines.
            if(inputLine.startsWith("#")) {
                continue;
            }
            setPageLinkMap(pageLinkMap, inputLine);
        }
        reader.close();
//        System.out.println("\tnumber of instances including interlanguage information: " + map.size());
        System.out.println("\tfinish reading page-links");

        instance_id = setCategoryIdMap();
        edges = setEdgeSizeMap(pageLinkMap, instance_id);
        instance_id = null;

        resultMap.put(key, edges);
        edges = null;

        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File("edges.json"), resultMap);
    }

    private Map<String, Set<String>> setCategoryIdMap() throws IOException {
        Category cat;
        Nodes nod;
        Map<String, Set<String>> categoryIdMap;

        cat = new Category();
        cat.setFileName(App.baseDir + App.filename_categories);
        cat.setMap("instance");

        nod = new Nodes();
        nod.setNodeIdMap();

        categoryIdMap = new DefaultHashMap<>(HashMap.class);
        for(String instance : cat.getKeySet()) {
            for(String category : cat.getValueSet(instance)) {
                categoryIdMap.get(instance).add(nod.getId(category));
            }
        }

        return categoryIdMap;
    }

    private ArrayList<Map<String, Object>> setEdgeSizeMap(HashMap<String, Set<String>> pageLinkMap, Map<String, Set<String>> instance_id) throws IOException {
        String s; // category ID for FROM plus category ID for TO
        Set<String> fromCategorySet;
        Set<String> toCategorySet;

        edgeSizeMap= new HashMap<>();
        for(String from : pageLinkMap.keySet()) {
            fromCategorySet = instance_id.get(from);
            for(String to : pageLinkMap.get(from)) {
                toCategorySet = instance_id.get(to);
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
        pageLinkMap = null;

        //

        ArrayList<Map<String, Object>> edges = new ArrayList<>();
        String[] strArr;
        Map<String, Object> edge;
        Map<String, Integer> variables;
        int share_point;
        int edgeSize;
        for(String from_to : edgeSizeMap.keySet()) {
            edge = new HashMap<>();
            variables = new HashMap<>();

            variables.put("page-links", edgeSizeMap.get(from_to));
            variables.put("share-same-instance", 0);

            strArr = from_to.split("-", 2);
            share_point = 0;
            edgeSize = edgeSizeMap.get(from_to) + share_point;

            edge.put("id", "0");
            edge.put("source", strArr[0]);
            edge.put("target", strArr[1]);
            edge.put("value", edgeSize);
            edge.put("variables", variables);
            edges.add(edge);
        }
        edgeSizeMap = null;
        return edges;
    }

    private Set<String> getCategoryIdSet(String instance) {
        return null;
    }

    private static void setPageLinkMap(HashMap<String, Set<String>> pageLinkMap, String s) {
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

//    private static Set<String> getToSet(String s) {
//        return pageLinkMap.get(s);
//    }

    public static void main(String[] args) {
        HashMap<String, Set<String>> pageLinkMap;

        pageLinkMap = new DefaultHashMap<>(HashSet.class);
        setPageLinkMap(pageLinkMap, "<http://dbpedia.org/resource/AcademyAwards>" +
                " <http://dbpedia.org/ontology/wikiPageWikiLink>" +
                " <http://dbpedia.org/resource/Academy_Awards>" +
                " .");
        setPageLinkMap(pageLinkMap, "<http://dbpedia.org/resource/AcademicElitism>" +
                " <http://dbpedia.org/ontology/wikiPageWikiLink>" +
                " <http://dbpedia.org/resource/Ivory_tower>" +
                " .");
        setPageLinkMap(pageLinkMap, "<http://dbpedia.org/resource/Albedo>" +
                " <http://dbpedia.org/ontology/wikiPageWikiLink>" +
                " <http://dbpedia.org/resource/File:Albedo-e_hg.svg>" +
                " .");
        setPageLinkMap(pageLinkMap, "<http://dbpedia.org/resource/Albedo>" +
                " <http://dbpedia.org/ontology/wikiPageWikiLink>" +
                " <http://dbpedia.org/resource/Latin>" +
                " .");
        setPageLinkMap(pageLinkMap, "<http://dbpedia.org/resource/Albedo>" +
                " <http://dbpedia.org/ontology/wikiPageWikiLink>" +
                " <http://dbpedia.org/resource/Diffuse_reflection>" +
                " .");

        System.out.println(pageLinkMap.keySet());
        System.out.println(pageLinkMap.get("AcademyAwards"));
        System.out.println(pageLinkMap.get("AcademicElitism"));
        System.out.println(pageLinkMap.get("Albedo"));
    }
}
