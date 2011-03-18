package com.jiehoo.jpm;

import org.apache.log4j.Logger;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImageManager {
    private static Logger logger = Logger.getLogger(ImageManager.class);
    private static final int MAX_ENTRIES = 500;
    private static Map<File, byte[]> thumbnailsCache = new LinkedHashMap<File, byte[]>(MAX_ENTRIES, .75F, true) {
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_ENTRIES;
        }
    };

    public static BufferedImage getImage(File file, int width, int height) {
        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(file);
        } catch (IOException e) {
            throw new JPMException("Can't read image:" + file.getAbsolutePath(), e);
        }
        double originalWidth = originalImage.getWidth();
        double originalHeight = originalImage.getHeight();
        double rateWidth = width / originalWidth;
        double rateHeight = height / originalHeight;
        double rate = Math.min(rateWidth, rateHeight);
        int targetWidth = (int) (originalWidth * rate);
        int targetHeight = (int) (originalHeight * rate);
        return resizeImage(originalImage, targetWidth, targetHeight);
    }

    public static void resizeImage(File file, int width, int height, String output) {
        try {
            ImageIO.write(getImage(file, width, height), getFormat(output), new File(output));
        } catch (IOException e) {
            throw new JPMException("Can't read image:" + file.getAbsolutePath() + " or write to:" + output, e);
        }
    }

    public static void resizeImage(File file, float percent, String output) {
        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(file);
            int width = (int) (originalImage.getWidth() * percent);
            int height = (int) (originalImage.getHeight() * percent);
            ImageIO.write(resizeImage(originalImage, width, height), getFormat(output), new File(output));
        } catch (IOException e) {
            throw new JPMException("Can't read image:" + file.getAbsolutePath() + " or write to:" + output, e);
        }
    }

    private static String getFormat(String output) {
        return output.substring(output.lastIndexOf(".") + 1);
    }

    private static int getImageType(BufferedImage image) {
        return image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image
                .getType();
    }

    private static BufferedImage resizeImage(Image originalImage, int width, int height, int type) {
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        return resizeImage(originalImage, width, height, getImageType(originalImage));
    }

    public static BufferedImage getThumbnails(File file, int width, int height) {
        return resizeImage(new ImageIcon(getThumbnails(file)).getImage(), width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public static byte[] getThumbnails(File file) {
        if (thumbnailsCache.containsKey(file)) {
            return thumbnailsCache.get(file);
        }
        byte[] data = null;
        IImageMetadata metadata;
        try {
            metadata = Sanselan.getMetadata(file);
        } catch (Exception e) {
            throw new JPMException("Can't read thumbnail:" + file.getAbsolutePath(), e);
        }
        if (metadata instanceof JpegImageMetadata) {
            JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            ArrayList dirs = jpegMetadata.getExif().getDirectories();
            for (Object dir : dirs) {
                TiffImageMetadata.Directory tiffdir = (TiffImageMetadata.Directory) dir;
                data = null;
                if (tiffdir.getJpegImageData() != null) {
                    data = tiffdir.getJpegImageData().data;
                    if (data != null) {
                        break;
                    }
                }
            }
        }
        if (data == null) {
            BufferedImage image = getImage(file, Constants.THUMBNAILS_WIDTH, Constants.THUMBNAILS_HEIGHT);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(image, getFormat(file.getName()), baos);
                File dir = new File(file.getParent(), Constants.THUMBNAILS_DIRECTORY);
                boolean canOuput = true;
                if (!dir.exists()) {
                    if (!dir.mkdir()) {
                        canOuput = false;
                        logger.warn("Can't create thumbnails directory:" + dir);
                    }
                }
                if (canOuput) {
                    ImageIO.write(image, getFormat(file.getName()), new File(dir, file.getName()));
                }
            } catch (Exception e) {
                throw new JPMException("Can't create thumbnail:" + file.getAbsolutePath(), e);
            }
            data = baos.toByteArray();
        }
        thumbnailsCache.put(file, data);
        return data;
    }

    public static File getImageFile(String resourceKey) {
        return new File(ImageManager.class.getResource("ui/" + Utils.resource.getString("image_path")).getFile(), Utils.resource.getString(resourceKey));
    }
}
