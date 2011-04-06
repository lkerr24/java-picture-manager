package com.jiehoo.jpm.core;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.apache.log4j.Logger;

import java.util.HashSet;

public class ImageInfoConverter implements Converter {
    private static Logger logger = Logger.getLogger(ImageInfoConverter.class);

    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext context) {
        ImageInfo image = (ImageInfo) o;
        writeAttribute(writer, "path", image.getPath());
        if (image.getRank() > 0) writeAttribute(writer, "rank", image.getRank() + "");
        writeAttribute(writer, "size", image.getSize() + "");
        writeValidAttribute(writer, "date", image.getDate(), ImageInfo.UNKNOWN_DATE);
        writeValidAttribute(writer, "camera", image.getCamera(), ImageInfo.UNKNOWN);
        writeValidAttribute(writer, "compressionBPP", image.getCompressionBPP(), ImageInfo.UNKNOWN);
        writeValidAttribute(writer, "exposureTime", image.getExposureTime(), ImageInfo.UNKNOWN);
        writeValidAttribute(writer, "aperture", image.getAperture(), ImageInfo.UNKNOWN);
        writeValidAttribute(writer, "resolution", image.getResolution(), ImageInfo.UNKNOWN);
        if (image.getTags().size() > 0) writeAttribute(writer, "tags", getTags(image.getTags()));
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
        image.setPath(reader.getAttribute("path"));
        image.setRank(Integer.parseInt(getAttribute(reader, "rank", "0")));
        image.setSize(Long.parseLong(getAttribute(reader, "size", "0")));
        image.setDate(getAttribute(reader, "date", ImageInfo.UNKNOWN_DATE));
        image.setCamera(getAttribute(reader, "camera", ImageInfo.UNKNOWN));
        image.setCompressionBPP(getAttribute(reader, "compressionBPP", ImageInfo.UNKNOWN + ""));
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

    protected void writeAttribute(HierarchicalStreamWriter writer, String name, String value) {
        try {
            writer.addAttribute(name, value);
        } catch (Throwable e) {
            logger.error("Can't write attribute " + name + ", with value:" + value, e);
        }
    }

    protected void writeValidAttribute(HierarchicalStreamWriter writer, String name, String value,
                                       String invalidValue) {
        if (!value.equals(invalidValue))
            writer.addAttribute(name, value);
    }
}
