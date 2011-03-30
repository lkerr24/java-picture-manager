package com.jiehoo.jpm.core;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.io.File;
import java.util.HashMap;

public class WorkspaceConverter implements Converter {
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext context) {
        Workspace worksppace = (Workspace) o;
        writer.startNode("tags");
        for (Tag tag : worksppace.getTags().values()) {
            writer.startNode("tag");
            context.convertAnother(tag);
            writer.endNode();
        }
        writer.endNode();
        writer.startNode("images");
        for (ImageInfo image : worksppace.getImageMap().values()) {
            writer.startNode("image");
            context.convertAnother(image);
            writer.endNode();
        }
        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Workspace workspace = new Workspace("");
        Workspace.setInstance(workspace);
        while (reader.hasMoreChildren()) {
            parseCollection(workspace, reader, context);
        }
        HashMap<File, ImageInfo> imageMap = new HashMap<File, ImageInfo>();
        for (ImageInfo image : workspace.getImageMap().values()) {
            imageMap.put(new File(image.getAbsolutePath()), image);
        }
        workspace.setImageMap(imageMap);
        return workspace;
    }

    private void parseCollection(Workspace workspace, HierarchicalStreamReader reader, UnmarshallingContext context) {
        reader.moveDown();
        String node = reader.getNodeName();
        if (node.equals("tags")) {
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                Tag tag = (Tag) context.convertAnother(workspace, Tag.class);
                workspace.getTags().put(tag.getID(), tag);
                reader.moveUp();
                if (workspace.getTagIndex() < tag.getID()) {
                    workspace.setTagIndex(tag.getID());
                }
            }
        } else if (node.equals("images")) {
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                ImageInfo image = (ImageInfo) context.convertAnother(workspace, ImageInfo.class);
                workspace.getImageMap().put(new File(image.getPath()), image);
                reader.moveUp();
            }
        }
        reader.moveUp();
    }

    public boolean canConvert(Class aClass) {
        return aClass.equals(Workspace.class);
    }
}
