package com.jiehoo.jpm.core;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.HashSet;

public class ImageInfoConverter implements Converter {
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext context) {
        ImageInfo image = (ImageInfo) o;
        if (image.getRank() > 0)
            writer.addAttribute("rank", image.getRank() + "");
        writer.addAttribute("size", image.getSize() + "");
        if (!image.getDate().equals(ImageInfo.UNKNOWN_DATE))
            writer.addAttribute("date", image.getDate());
        if (!image.getCamera().equals(ImageInfo.UNKNOWN))
            writer.addAttribute("camera", image.getCamera());
        writer.addAttribute("path", image.getPath());
        if (image.getCompressionBPP().equals(ImageInfo.UNKNOWN))
            writer.addAttribute("compressionBPP", image.getCompressionBPP() + "");
        if (!image.getExposureTime().equals(ImageInfo.UNKNOWN))
            writer.addAttribute("exposureTime", image.getExposureTime());
        if (!image.getAperture().equals(ImageInfo.UNKNOWN))
            writer.addAttribute("aperture", image.getAperture());
        if (!image.getResolution().equals(ImageInfo.UNKNOWN_RESOLUTION))
            writer.addAttribute("resolution", image.getResolution());
        if (image.getTags().size() > 0)
            writer.addAttribute("tags", getTags(image.getTags()));
    }

    private String getTags(HashSet<Integer> tags) {
        StringBuilder buffer = new StringBuilder();
        for (Integer tag : tags) {
            buffer.append(tag).append(",");
        }
        if (buffer.length() > 0) {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        return buffer.toString();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        ImageInfo image = new ImageInfo();
        image.setRank(Integer.parseInt(getAttribute(reader, "rank", "0")));
        image.setSize(Long.parseLong(getAttribute(reader, "size", "0")));
        image.setDate(getAttribute(reader, "date", ImageInfo.UNKNOWN_DATE));
        image.setCamera(getAttribute(reader, "camera", ImageInfo.UNKNOWN));
        image.setCompressionBPP(getAttribute(reader, "compressionBPP", ImageInfo.UNKNOWN + ""));
        image.setPath(reader.getAttribute("path"));
        image.setExposureTime(getAttribute(reader, "exposureTime", ImageInfo.UNKNOWN));
        image.setAperture(getAttribute(reader, "aperture", ImageInfo.UNKNOWN));
        image.setResolution(getAttribute(reader, "resolution", ImageInfo.UNKNOWN_RESOLUTION));
        String ts = getAttribute(reader, "tags", "");
        if (!ts.equals("")) {
            String[] tags = ts.split(",");
            for (String tag : tags) {
                image.addTag(Integer.parseInt(tag));
            }
        }
        return image;
    }

    private String getAttribute(HierarchicalStreamReader reader, String name, String defaultValue) {
        String value = reader.getAttribute(name);
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    public boolean canConvert(Class aClass) {
        return aClass.equals(ImageInfo.class);
    }
}
