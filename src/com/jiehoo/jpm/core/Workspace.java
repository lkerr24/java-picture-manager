package com.jiehoo.jpm.core;

import com.jiehoo.jpm.Constants;
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


    private String rootPath;
    private int tagIndex;
    private HashMap<Integer, Tag> tags = new HashMap<Integer, Tag>();
    private HashMap<File, ImageInfo> imageMap = new HashMap<File, ImageInfo>();
    private static TagSortByUsedTimes usedTimesSorter = new TagSortByUsedTimes();
    private static TagSortByLastUsedTime lastUsedTimeSorter = new TagSortByLastUsedTime();
    private static Workspace instance;
    private static XStream xstream = new XStream(new DomDriver("UTF-8"));
    private static Comparator<File> duplicateComparator = new Comparator<File>() {
        public int compare(File o1, File o2) {
            ImageInfo i1 = Workspace.getInstance().getImage(o1);
            ImageInfo i2 = Workspace.getInstance().getImage(o1);
            int value = (int) (i2.getSize() - i1.getSize());
            if (value == 0) {
                return o1.getAbsolutePath().length() - o2.getAbsolutePath().length();
            } else {
                return value;
            }
        }
    };

    static {
        xstream.alias("Workspace", Workspace.class);
        xstream.alias("image", ImageInfo.class);
        xstream.alias("tag", Tag.class);
        xstream.useAttributeFor(Tag.class, "ID");
        xstream.useAttributeFor(Tag.class, "name");
        xstream.useAttributeFor(Tag.class, "lastUsedTime");
        xstream.useAttributeFor(Tag.class, "usedTimes");
        xstream.registerConverter(new WorkspaceConverter());
        xstream.registerConverter(new ImageInfoConverter());
    }

    protected void setTagIndex(int tagIndex) {
        this.tagIndex = tagIndex;
    }

    public String getRootPath() {
        return rootPath;
    }

    public int getTagIndex() {
        return tagIndex;
    }

    public void deletePicture(File file) {
        imageMap.remove(file);
        if (!file.delete()) {
            logger.warn("Can't delete picture:" + file);
        }
    }

    protected static void setInstance(Workspace instance) {
        Workspace.instance = instance;
    }

    public HashMap<Integer, Tag> getTags() {
        return tags;
    }

    protected Workspace(String rootPath) {
        this.rootPath = rootPath;
    }

    public List<Entry<File, ImageInfo>> getImages(List<Integer> ranks, List<Integer> tags) {
        List<Entry<File, ImageInfo>> images = new ArrayList<Entry<File, ImageInfo>>();
        boolean anyRank = false;
        if (ranks.size() == 0) {
            anyRank = true;
        }
        for (Entry<File, ImageInfo> entry : imageMap.entrySet()) {

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

    public void applyTag(File picture, int tagID, boolean remove) {
        ImageInfo image = imageMap.get(picture);
        Tag tag = tags.get(tagID);
        if (remove) {
            image.removeTag(tagID);
        } else {
            image.addTag(tagID);
        }
        tag.use();
    }

    public void applyRank(File picture, int rank) {
        ImageInfo image = imageMap.get(picture);
        image.setRank(rank);
    }

    private List<Tag> getOtherTags(HashSet<Integer> alreadyUsedTags) {
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

    public List<Tag> getLastUsedTags(HashSet<Integer> alreadyUsedTags, int maxResult) {
        List<Tag> result = getOtherTags(alreadyUsedTags);
        Collections.sort(result, lastUsedTimeSorter);
        return result.subList(0, maxResult > result.size() ? result.size() : maxResult);
    }

    public List<Tag> getOftenUsedTags(HashSet<Integer> alreadyUsedTags, int maxResult) {
        List<Tag> result = getOtherTags(alreadyUsedTags);
        Collections.sort(result, usedTimesSorter);
        return result.subList(0, maxResult > result.size() ? result.size() : maxResult);
    }

    public static Workspace getInstance() {
        return instance;
    }

    public static void init() throws IOException {
        if (instance == null) {
            Preferences preference = Utils.GetPreferences();
            String output = preference.get(Constants.LAST_WORKSPACE, System.getProperty("user.home"));
            preference.put(Constants.LAST_WORKSPACE, output);
            File file = new File(output, FILE_NAME);
            if (!file.exists()) {
                instance = new Workspace(output);
                instance.save();
            } else {
                instance = (Workspace) xstream.fromXML(new FileReader(file));
                instance.rootPath = file.getParent();
            }
        }
    }

    public void save() throws IOException {
        PrintStream writer = new PrintStream(new FileOutputStream(new File(
                rootPath, FILE_NAME)), false, "UTF8");
        //writer.print('\ufeff');
        xstream.toXML(instance, writer);
    }


    public HashMap<File, ImageInfo> getImageMap() {
        return imageMap;
    }

    public void setImageMap(HashMap<File, ImageInfo> imageMap) {
        this.imageMap = imageMap;
    }

    public ImageInfo getImage(File path) {
        return imageMap.get(path);
    }

    public void scan(boolean forceUpdate) throws IOException {
        scan(new File(rootPath), forceUpdate);
    }

    private void scan(File dir, boolean forceUpdate) throws IOException {
        logger.info("Scan directory:" + dir.getAbsolutePath());
        if (!dir.exists() || dir.getName().equalsIgnoreCase(Constants.THUMBNAILS_DIRECTORY)) {
            return;
        }
        File[] files = dir.listFiles(Utils.fileFilter);
        if (files != null) {
            for (File file : files) {
                if (forceUpdate || !imageMap.containsKey(file)) {
                    ImageInfo image = new ImageInfo();
                    image.setPath(file.getAbsolutePath().substring(rootPath.length() + 1));
                    image.extractImageInfo(file);
                    imageMap.put(file, image);
                } else {
                    logger.debug("Scanned image:" + file.getAbsolutePath());
                }
            }
        }
        File[] dirs = dir.listFiles(Utils.dirFilter);
        if (dirs != null) {
            for (File d : dirs) {
                scan(d, forceUpdate);
            }
        }
    }

    public List<DuplicateItem> getDuplicates() {
        HashMap<String, ArrayList<File>> fileIDMap = new HashMap<String, ArrayList<File>>();
        for (Entry<File, ImageInfo> stringImageInfoEntry : imageMap.entrySet()) {
            File path = stringImageInfoEntry.getKey();
            ImageInfo image = stringImageInfoEntry.getValue();
            String ID = image.getID();
            if (fileIDMap.containsKey(ID)) {
                ArrayList<File> list = fileIDMap.get(ID);
                list.add(path);
                logger.debug("Find duplicate item:" + ID + ", count:" + list.size());
            } else {
                ArrayList<File> list = new ArrayList<File>();
                list.add(path);
                fileIDMap.put(ID, list);
            }
        }
        for (Entry<String, ArrayList<File>> fileIDEntry : fileIDMap.entrySet()) {
            if (fileIDEntry.getValue().size() > 1) {
                Collections.sort(fileIDEntry.getValue(), duplicateComparator);
            }
        }
        List<DuplicateItem> result = new ArrayList<DuplicateItem>();
        for (Map.Entry<String, ArrayList<File>> stringArrayListEntry : fileIDMap
                .entrySet()) {
            ArrayList<File> list = stringArrayListEntry.getValue();
            if (list.size() > 1) {
                DuplicateItem item = new DuplicateItem();
                item.setId(stringArrayListEntry.getKey());
                item.setPaths(stringArrayListEntry.getValue());
                result.add(item);
            }
        }
        return result;
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
