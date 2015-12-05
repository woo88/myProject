package kr.ac.kaist.kmap;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by woo on 2015-12-05.
 */
public class Disk {
    private static HashSet<String> keySet = new HashSet();

    public static void main(String[] args) {
//        makeDirectory("page_links");
        storeInDisk("1", "2", "page_links");
    }

    public static void storeInDisk(String k, String v, String dir) {
        File dest;
        File[] fileList;
        String key;

        dest = new File(dir);
        fileList = dest.listFiles();
        for(File file : fileList) {
            key = file.getName();
        }
    }

    protected static void makeDirectory(String dir) {
        String mkFolder;
        File dest;

        mkFolder = dir;
        dest = new File(mkFolder);

        if(!dest.exists()) {
            dest.mkdirs();
        } else {
            File[] destroy = dest.listFiles();
            for(File des : destroy) {
                des.delete();
            }
        }
        System.out.println("Directory name: " + mkFolder);
    }

    public static HashSet<String> getKeySet() {
        return keySet;
    }
}
