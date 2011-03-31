package com.jiehoo.jpm.core;

import com.jiehoo.jpm.Constants;
import org.apache.log4j.Logger;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.constants.TagInfo;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashSet;

public class ImageInfo {
    public static final String UNKNOWN = "NA";
    public static final String UNKNOWN_DATE = "1970:00:00 00:00:00";
    public static final String UNKNOWN_RESOLUTION = "NAxNA";
    public static final String UNKNOWN_ID =
            UNKNOWN_DATE + "_" + UNKNOWN + "_" + UNKNOWN + "_" + UNKNOWN + "_" + UNKNOWN;
    private static Logger logger = Logger.getLogger(ImageInfo.class);
    private int rank;
    private long size;
    private String path;
    private String camera;
    private String date;
    private String compressionBPP;
    private String exposureTime;
    private String aperture;
    private String resolution;
    private HashSet<Integer> tags = new HashSet<Integer>();
    private static final DecimalFormat format = new DecimalFormat("0.00");

    public String getAbsolutePath() {
        return Workspace.getInstance().getRootPath() + Constants.PATH_SEPERATOR + path;
    }

    public String getCompressionBPP() {
        return compressionBPP;
    }

    public void setCompressionBPP(String compressionBPP) {
        this.compressionBPP = compressionBPP;
    }

    public String getExposureTime() {
        return exposureTime;
    }

    public void setExposureTime(String exposureTime) {
        this.exposureTime = exposureTime;
    }

    public String getAperture() {
        return aperture;
    }

    public void setAperture(String aperture) {
        this.aperture = aperture;
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
        buffer.append("_").append(compressionBPP);
        buffer.append("_").append(exposureTime);
        buffer.append("_").append(aperture);
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

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void extractImageInfo(File file) throws IOException {
        logger.debug("Extract image info for:" + file);
        size = file.length();
        JpegImageMetadata metadata;
        try {
            metadata = (JpegImageMetadata) Sanselan.getMetadata(file);
            camera = (String) getPropertyValue(metadata, TiffConstants.EXIF_TAG_MODEL, UNKNOWN);
            date = (String) getPropertyValue(metadata, TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL, UNKNOWN_DATE);
            compressionBPP = getPropertyValue(metadata, TiffConstants.EXIF_TAG_COMPRESSED_BITS_PER_PIXEL,
                    UNKNOWN).toString();
            exposureTime =
                    getPropertyValue(metadata, TiffConstants.EXIF_TAG_EXPOSURE_TIME, UNKNOWN).toString();
            aperture = getPropertyValue(metadata, TiffConstants.EXIF_TAG_APERTURE_VALUE, UNKNOWN)
                    .toString();
            resolution = getPropertyValue(metadata, TiffConstants.EXIF_TAG_EXIF_IMAGE_WIDTH, UNKNOWN)
                    .toString() + "x" +
                    getPropertyValue(metadata, TiffConstants.EXIF_TAG_EXIF_IMAGE_LENGTH, UNKNOWN)
                            .toString();
        } catch (ImageReadException e) {
            logger.warn("Can't extract image information.", e);
        }
    }

    private Object getPropertyValue(JpegImageMetadata metadata, TagInfo property, Object defaultValue)
            throws ImageReadException {
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

    public String getDisplaySize() {
        double s = (double) size / 1024;
        if (s > 1000) {
            s = s / 1024;
            return format.format(s) + " MB";
        } else {
            return format.format(s) + " KB";
        }
    }

}
