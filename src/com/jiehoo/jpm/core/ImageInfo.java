package com.jiehoo.jpm.core;

import org.apache.log4j.Logger;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.constants.TagInfo;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class ImageInfo {
    public static final String UNKNOWN_CAMERA = "NA";
    public static final String UNKNOWN_DATE = "1970:00:00 00:00:00";
    public static final int UNKNOWN_INTEROP_OFFSET = 0;
    public static final String UNKNOWN_EXPOSURE_TIME = "NA";
    public static final String UNKNOWN_MAX_APERTURE = "NA";
    public static final String UNKNOWN_ID = UNKNOWN_DATE + "_" + UNKNOWN_INTEROP_OFFSET + "_" + UNKNOWN_EXPOSURE_TIME + "_" + UNKNOWN_MAX_APERTURE + "_" + UNKNOWN_CAMERA;
    private static Logger logger = Logger.getLogger(ImageInfo.class);
    private int rank;
    private long size;
    private String path;
    private String camera;
    private String date;
    private int interopOffset;
    private String exposureTime;
    private String maxAperture;
    private HashSet<Integer> tags = new HashSet<Integer>();

    public String getAbsolutePath() {
        return Workspace.getInstance().getOutputPath() + "/" + path;
    }

    public int getInteropOffset() {
        return interopOffset;
    }

    public void setInteropOffset(int interopOffset) {
        this.interopOffset = interopOffset;
    }

    public String getExposureTime() {
        return exposureTime;
    }

    public void setExposureTime(String exposureTime) {
        this.exposureTime = exposureTime;
    }

    public String getMaxAperture() {
        return maxAperture;
    }

    public void setMaxAperture(String maxAperture) {
        this.maxAperture = maxAperture;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getCamera() {
        return camera;
    }

    public void setCamera(String camera) {
        this.camera = camera;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getID() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(date);
        buffer.append("_").append(interopOffset);
        buffer.append("_").append(exposureTime);
        buffer.append("_").append(maxAperture);
        buffer.append("_").append(camera);
        if (buffer.toString().equals(UNKNOWN_ID)) {
            buffer.append("_").append(size);
        }
        return buffer.toString();
    }

    public HashSet<Integer> getTags() {
        return tags;
    }

    public void setTags(HashSet<Integer> tags) {
        this.tags = tags;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void addTag(int tag) {
        tags.add(tag);
    }


    public void removeTag(int tag) {
        tags.remove(tag);
    }

    public void extractImageInfo(File file) throws IOException {
        logger.debug("Extract image info for:" + file);
        size = file.length();
        JpegImageMetadata metadata;
        try {
            metadata = (JpegImageMetadata) Sanselan.getMetadata(file);
            camera = (String) getPropertyValue(metadata, TiffConstants.EXIF_TAG_MODEL, UNKNOWN_CAMERA);
            date = (String) getPropertyValue(metadata, TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL, UNKNOWN_DATE);
            interopOffset = (Integer) getPropertyValue(metadata, TiffConstants.EXIF_TAG_INTEROP_OFFSET, UNKNOWN_INTEROP_OFFSET);
            exposureTime = getPropertyValue(metadata, TiffConstants.EXIF_TAG_EXPOSURE_TIME, UNKNOWN_EXPOSURE_TIME).toString();
            maxAperture = getPropertyValue(metadata, TiffConstants.EXIF_TAG_MAX_APERTURE_VALUE, UNKNOWN_MAX_APERTURE).toString();
        } catch (ImageReadException e) {
            logger.warn("Can't extract image information.", e);
        }
    }

    private Object getPropertyValue(JpegImageMetadata metadata, TagInfo property, Object defaultValue) throws ImageReadException {
        if (metadata == null) {
            return defaultValue;
        }
        TiffField field = metadata.findEXIFValue(property);
        if (field != null) {
            Object result = field.getValue();
            if (result != null && result instanceof String) {
                String s = (String) result;
                result = s.trim();
            }
            return result;
        } else {
            return defaultValue;
        }
    }

}
