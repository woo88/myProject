package kr.ac.kaist.kmap;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class App {
    private static final String baseDir = "/home/woo88/dbpedia/2015-04/en/";
    private static final String filename_categories = "article-categories_en.nt";
    private static final String filename_redirects = "redirects_en.nt";
    private static final String filename_infobox_properties = "infobox-properties_en.nt";

    public static void main(String[] args) throws IOException {
        System.out.println("Hello World!!!");

        Category cat = new Category(baseDir + filename_categories);
//        System.out.println(cat.getInstanceSet("Climate_forcing"));

        Redirect red = new Redirect(baseDir + filename_redirects);
//        System.out.println(red.getRedirect("United_States"));

        Infobox ib = new Infobox(baseDir + filename_infobox_properties);
//        System.out.println(ib.getInfobox("United_States"));

        /**
         * "instances": 0,
         * "redirects": 0,
         * "infobox": 0,
         * "types": 0,
         * "page-length": 0,
         * "freebase": 0,
         * "nytimes": 0,
         * "yago-intances": 0,
         * "yago-types": 0
         */
        ArrayList<Map<String, Object>> nodes = new ArrayList<>();
        int i = 0;
        for(String category : cat.getCategories()) {
            i++;
            Map<String, Integer> variables = new HashMap<>();
            variables.put("instances", cat.getCategorySize(category));
            int redirect_point = 0;
            int infobox_point = 0;
            for(String instance : cat.getInstanceSet(category)){
                redirect_point += red.getRedirect(instance);
                infobox_point += ib.getInfobox(instance);
            }
            variables.put("redirects", redirect_point);
            variables.put("infobox", infobox_point);

            //        HashMap node = new HashMap();
            Map<String, Object> node = new HashMap<>();
            node.put("id", String.valueOf(i));
            node.put("label", category);
            node.put("value", 0);
            node.put("variables", variables);

            nodes.add(node);
        }

        HashMap map = new HashMap();
        map.put("timeslot", "2015-04");
        map.put("nodes", nodes);
        map.put("edges", "test");

//        StringBuffer sbuf = new StringBuffer();
        ObjectMapper mapper = new ObjectMapper();

        mapper.writerWithDefaultPrettyPrinter().writeValue(new File("output.json"), map);
    }
}
