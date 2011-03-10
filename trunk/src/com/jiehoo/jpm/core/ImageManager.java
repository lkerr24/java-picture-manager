package com.jiehoo.jpm.core;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ImageManager {
    private static ImageReader reader;

    static {
        Iterator readers = ImageIO.getImageReadersByFormatName("jpg");
        reader = (ImageReader) readers.next();
        while (readers.hasNext()) {
            reader = (ImageReader) readers.next();
            if (reader.readerSupportsThumbnails()) {
                break;
            }
        }
    }

    public static BufferedImage getImage(File file, int width, int height) throws IOException {
        BufferedImage originalImage = ImageIO.read(file);
        double originalWidth = originalImage.getWidth();
        double originalHeight = originalImage.getHeight();
        double rateWidth = width / originalWidth;
        double rateHeight = height / originalHeight;
        double rate = Math.min(rateWidth, rateHeight);
        int targetWidth = (int) (originalWidth * rate);
        int targetHeight = (int) (originalHeight * rate);
        return ResizeImage(originalImage, targetWidth, targetHeight);
    }

    public static void ResizeImage(File file, int width, int height,
                                   String output) throws IOException {
        ImageIO.write(getImage(file, width, height), GetFormat(output), new File(output));
    }

    public static void ResizeImage(File file, float percent, String output) throws IOException {
        BufferedImage originalImage = ImageIO.read(file);
        int width = (int) (originalImage.getWidth() * percent);
        int height = (int) (originalImage.getHeight() * percent);
        ImageIO.write(ResizeImage(originalImage, width, height), GetFormat(output), new File(output));
    }

    private static String GetFormat(String output) {
        return output.substring(output.lastIndexOf(".") + 1);
    }

    private static int GetImageType(BufferedImage image) {
        return image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image
                .getType();
    }

    private static BufferedImage ResizeImage(BufferedImage originalImage,
                                             int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height,
                GetImageType(originalImage));
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    public static byte[] getThumbnails(File file) throws IOException, ImageReadException {
        byte[] data = null;
        IImageMetadata metadata = Sanselan.getMetadata(file);
        if (metadata instanceof JpegImageMetadata) {
            JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            ArrayList dirs = jpegMetadata.getExif().getDirectories();
            for (int i = 0; i < dirs.size(); i++) {
                TiffImageMetadata.Directory tiffdir = (TiffImageMetadata.Directory) dirs.get(i);
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

    public static BufferedImage getThumbnails2(File file) throws IOException {
        ImageInputStream iis = ImageIO.createImageInputStream(new
                FileInputStream(file.getAbsoluteFile()));
        reader.setInput(iis, true);
        return reader.readThumbnail(0, 0);
    }
}
