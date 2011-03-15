package com.jiehoo.jpm;

import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImageManager {

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

    private static BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height,
                getImageType(originalImage));
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    public static byte[] getThumbnails(File file) {
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
                        return data;
                    }
                }
            }
        }
        return data;
    }

    public static File getImageFile(String resourceKey) {
        return new File(ImageManager.class.getResource(Utils.resource.getString("image_path")).getFile() + "/ui", Utils.resource.getString(resourceKey));
    }
}
