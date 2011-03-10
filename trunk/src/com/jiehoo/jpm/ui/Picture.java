package com.jiehoo.jpm.ui;

import com.jiehoo.jpm.Utils;
import com.jiehoo.jpm.core.ImageManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 *
 */
public class Picture extends JLabel {
    private static Logger logger = Logger.getLogger(MainFrame.class);
    private static BufferedImage folderImage;
    private static BufferedImage errorImage;
    private static int width = 256;
    private static int height = 256;

    static {
        try {
            folderImage = ImageManager.getImage(getImageFile("image_folder"), width, height);
            errorImage = ImageManager.getImage(getImageFile("image_error"), width, height);
        } catch (IOException e) {
            logger.error("Can't open image.", e);
        }
    }

    private static File getImageFile(String resourceKey) {
        return new File(Picture.class.getResource(Utils.resource.getString("image_path")).getFile(), Utils.resource.getString(resourceKey));
    }

    public Picture(File file) {
        setText(file.getName());
        if (file.isDirectory()) {
            setIcon(new ImageIcon(folderImage));
        } else {
            try {
                setIcon(new ImageIcon(ImageManager.getThumbnails(file)));
            } catch (Exception e) {
                logger.warn("Can't read image:"+file,e);
                setIcon(new ImageIcon(errorImage));
            }
        }
        setVerticalTextPosition(JLabel.BOTTOM);
        setHorizontalTextPosition(JLabel.CENTER);
    }
}
