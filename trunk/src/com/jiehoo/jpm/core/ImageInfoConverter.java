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
        if (!image.getCamera().equals(ImageInfo.UNKNOWN_CAMERA))
            writer.addAttribute("camera", image.getCamera());
        writer.addAttribute("path", image.getPath());
        if (image.getCompressionBPP().equals(ImageInfo.UNKNOWN_COMPRESSION_BPP))
            writer.addAttribute("compressionBPP", image.getCompressionBPP() + "");
        if (!image.getExposureTime().equals(ImageInfo.UNKNOWN_EXPOSURE_TIME))
            writer.addAttribute("exposureTime", image.getExposureTime());
        if (!image.getMaxAperture().equals(ImageInfo.UNKNOWN_MAX_APERTURE))
            writer.addAttribute("maxAperture", image.getMaxAperture());
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
        image.setCamera(getAttribute(reader, "camera", ImageInfo.UNKNOWN_CAMERA));
        image.setCompressionBPP(getAttribute(reader, "compressionBPP", ImageInfo.UNKNOWN_COMPRESSION_BPP + ""));
        image.setPath(reader.getAttribute("path"));
        image.setExposureTime(getAttribute(reader, "exposureTime", ImageInfo.UNKNOWN_EXPOSURE_TIME));
        image.setMaxAperture(getAttribute(reader, "maxAperture", ImageInfo.UNKNOWN_MAX_APERTURE));
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
