package com.jiehoo.jpm.core;

import com.jiehoo.jpm.Utils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.prefs.Preferences;

public class Workspace {
    private static Logger logger = Logger.getLogger(Workspace.class);
    private static final String FILE_NAME = "jpm.xml";
    private String outputPath;
    private int tagIndex;
    private HashSet<String> paths = new HashSet<String>();
    private HashMap<Integer, Tag> tags = new HashMap<Integer, Tag>();
    private HashMap<String, ImageInfo> imageMap = new HashMap<String, ImageInfo>();
    private static TagSortByUsedTimes usedTimesSorter = new TagSortByUsedTimes();
    private static TagSortByLastUsedTime lastUsedTimeSorter = new TagSortByLastUsedTime();
    private static Workspace instance;
    private static XStream xstream = new XStream(new DomDriver());

    static {
        xstream.alias("Workspace", Workspace.class);
        xstream.alias("Image", ImageInfo.class);
        xstream.alias("Tag", Tag.class);
    }

    public HashMap<Integer, Tag> getTags() {
        return tags;
    }

    private static FilenameFilter fileFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".jpg");
        }
    };

    private Workspace(String outputPath) {
        this.outputPath = outputPath;
    }

    public List<Entry<String, ImageInfo>> getImages(List<Integer> ranks, List<Integer> tags) {
        List<Entry<String, ImageInfo>> images = new ArrayList<Entry<String, ImageInfo>>();
        boolean anyRank = false;
        if (ranks.size() == 0) {
            anyRank = true;
        }
        for (Entry<String, ImageInfo> entry : imageMap.entrySet()) {

            if ((anyRank || ranks.contains(entry.getValue().getRank())) && entry.getValue().getTags().containsAll(tags)) {
                images.add(entry);
            }
        }
        return images;
    }

    public Tag createTag(String name) {
        for (Tag tag : tags.values()) {
            if (tag.getName().compareToIgnoreCase(name) == 0) {
                return tag;
            }
        }
        tagIndex++;
        Tag tag = new Tag();
        tag.setID(tagIndex);
        tag.setName(name);
        tags.put(tagIndex, tag);
        return tag;
    }

    public void applyTag(String picture, int tagID, boolean remove) {
        ImageInfo image = imageMap.get(picture);
        Tag tag = tags.get(tagID);
        if (remove) {
            image.removeTag(tagID);
        } else {
            image.addTag(tagID);
        }
        tag.use();
    }

    public void applyRank(String picture, int rank) {
        ImageInfo image = imageMap.get(picture);
        image.setRank(rank);
    }

    private List<Tag> GetOtherTags(HashSet<Integer> alreadyUsedTags) {
        List<Tag> result = new ArrayList<Tag>();
        result.addAll(tags.values());
        for (int i = 0; i < result.size(); i++) {
            Tag tag = result.get(i);
            if (alreadyUsedTags.contains(tag.getID())) {
                result.remove(i);
                i--;
            }
        }
        return result;
    }

    public List<Tag> GetLastUsedTags(HashSet<Integer> alreadyUsedTags, int maxResult) {
        List<Tag> result = GetOtherTags(alreadyUsedTags);
        Collections.sort(result, lastUsedTimeSorter);
        return result.subList(0, maxResult > result.size() ? result.size() : maxResult);
    }

    public List<Tag> GetOftenUsedTags(HashSet<Integer> alreadyUsedTags, int maxResult) {
        List<Tag> result = GetOtherTags(alreadyUsedTags);
        Collections.sort(result, usedTimesSorter);
        return result.subList(0, maxResult > result.size() ? result.size() : maxResult);
    }

    public static Workspace getInstance() {
        return instance;
    }

    public static void init() throws IOException {
        if (instance == null) {
            Preferences preference = Utils.GetPreferences();
            String output = preference.get("output", System.getProperty("user.home"));
            preference.put("output", output);
            File file = new File(output, FILE_NAME);
            if (!file.exists()) {
                instance = new Workspace(output);
                instance.save();
            } else {
                instance = (Workspace) xstream.fromXML(new FileReader(file));
            }
        }
    }

    public void save() throws IOException {
        PrintStream writer = new PrintStream(new FileOutputStream(new File(
                outputPath, FILE_NAME)), false, "UTF8");
        //writer.print('\ufeff');
        xstream.toXML(instance, writer);
    }


    public HashMap<String, ImageInfo> getImageMap() {
        return imageMap;
    }

    public void setImageMap(HashMap<String, ImageInfo> imageMap) {
        this.imageMap = imageMap;
    }

    public boolean addPath(String path) {
        //TODO check path already included in the old path and sub path
        paths.add(path);
        return true;
    }

    public HashSet<String> getPaths() {
        return paths;
    }

    public void scan(boolean forceUpdate) throws IOException {
        for (String s : paths) {
            scan(new File(s), forceUpdate);
        }
    }

    private void scan(File dir, boolean forceUpdate) throws IOException {
        logger.info("Scan directory:" + dir.getAbsolutePath());
        File[] files = dir.listFiles(fileFilter);
        for (File file : files) {
            if (forceUpdate || !imageMap.containsKey(file.getAbsolutePath())) {
                ImageInfo image = new ImageInfo();
                image.extractImageInfo(file.getAbsolutePath());
                imageMap.put(file.getAbsolutePath(), image);
            } else {
                logger.debug("Scanned image:" + file.getAbsolutePath());
            }
        }
        File[] dirs = dir.listFiles(Utils.dirFilter);
        for (File d : dirs) {
            scan(d, forceUpdate);
        }
    }

    public void listDuplicate() {
        HashMap<String, ArrayList<String>> fileIDMap = new HashMap<String, ArrayList<String>>();
        for (Entry<String, ImageInfo> stringImageInfoEntry : imageMap.entrySet()) {
            String path = stringImageInfoEntry.getKey();
            ImageInfo image = stringImageInfoEntry.getValue();
            String ID = image.getID();
            if (fileIDMap.containsKey(ID)) {
                ArrayList<String> list = fileIDMap.get(ID);
                list.add(path);
            } else {
                ArrayList<String> list = new ArrayList<String>();
                list.add(path);
                fileIDMap.put(ID, list);
            }
        }
        for (Entry<String, ArrayList<String>> stringArrayListEntry : fileIDMap
                .entrySet()) {
            ArrayList<String> list = stringArrayListEntry.getValue();
            if (list.size() > 1) {
                System.out.print("Duplicate: ");
                for (String s : list) {
                    System.out.print(s + " <-> ");
                }
                System.out.println();
            }
        }
    }

    public void output() throws IOException {
        PrintStream writer = new PrintStream(new FileOutputStream(new File(
                outputPath, "jpm.csv")), false, "UTF8");
        writer.print('\ufeff');
        writer.print("File,Camera,Size,Date,ID\r\n");
        /*
           * writer.print(image.getFullPath()); writer.print(",");
           * writer.print(image.getCamera()); writer.print(",");
           * writer.print(image.getSize()); writer.print(",");
           * writer.print(image.getDate()); writer.print(",");
           * writer.print(image.getID()); writer.print("\r\n");
           */
        writer.close();
    }

    static class TagSortByUsedTimes implements Comparator<Tag> {

        public int compare(Tag o1, Tag o2) {
            return o2.getUsedTimes() - o1.getUsedTimes();
        }
    }

    static class TagSortByLastUsedTime implements Comparator<Tag> {

        public int compare(Tag o1, Tag o2) {
            return o2.getLastUsedTime().compareTo(o1.getLastUsedTime());
        }
    }

}
