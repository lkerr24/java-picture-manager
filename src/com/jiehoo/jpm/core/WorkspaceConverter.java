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
        writer.addAttribute("outputPath", worksppace.getOutputPath());
        writer.addAttribute("tagIndex", worksppace.getTagIndex() + "");
        writer.addAttribute("pathIndex", worksppace.getPathIndex() + "");
        writer.startNode("paths");
        for (Path path : worksppace.getPaths().values()) {
            writer.startNode("path");
            context.convertAnother(path);
            writer.endNode();
        }
        writer.endNode();
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
        Workspace workspace = new Workspace(reader.getAttribute("outputPath"));
        Workspace.setInstance(workspace);
        workspace.setPathIndex(Integer.parseInt(reader.getAttribute("pathIndex")));
        workspace.setTagIndex(Integer.parseInt(reader.getAttribute("tagIndex")));
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
        if (node.equals("paths")) {
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                Path path = (Path) context.convertAnother(workspace, Path.class);
                workspace.getPaths().put(path.getID(), path);
                reader.moveUp();
            }
        } else if (node.equals("tags")) {
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                Tag tag = (Tag) context.convertAnother(workspace, Tag.class);
                workspace.getTags().put(tag.getID(), tag);
                reader.moveUp();
            }
        } else if (node.equals("images")) {
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                ImageInfo image = (ImageInfo) context.convertAnother(workspace, ImageInfo.class);
                workspace.getImageMap().put(new File(image.getParentPath() + image.getPath()), image);
                reader.moveUp();
            }
        }
        reader.moveUp();
    }

    public boolean canConvert(Class aClass) {
        return aClass.equals(Workspace.class);
    }
}
