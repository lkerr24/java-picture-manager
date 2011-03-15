package com.jiehoo.jpm.ui;

import com.jiehoo.jpm.ImageManager;
import com.jiehoo.jpm.JPMException;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 *
 */
public class Picture extends JLabel {
    private static Logger logger = Logger.getLogger(Picture.class);
    private static BufferedImage folderImage;
    private static BufferedImage errorImage;
    private static int width = 100;
    private static int height = 100;
    private boolean selected = false;
    private TitledBorder selectedBorder;
    private TitledBorder unselectedBorder;
    private File file;
    private boolean isPicture;

    private static MouseListener mouseListener = new MouseListener() {
        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
            Picture picture = (Picture) e.getSource();
            if (!picture.isPicture && e.getClickCount() == 2) {
                e.consume();
                ((NavigatePanel) UIManager.getComponent(UIManager.NAVIGATE_PANEL)).selectChild(picture.getName());
            }
            picture.changeSelected();
            picture.setBorder();
            TagsPanel tagsPanel = (TagsPanel) UIManager.getComponent(UIManager.TAGS_PANEL);
            tagsPanel.reset();

        }
    };

    static {
        folderImage = ImageManager.getImage(ImageManager.getImageFile("image_folder"), width, height);
        errorImage = ImageManager.getImage(ImageManager.getImageFile("image_error"), width, height);
    }

    public Picture(File file) {
        this.file = file;
        if (file.isDirectory()) {
            setIcon(new ImageIcon(folderImage));
        } else {
            isPicture = true;
            try {
                setIcon(new ImageIcon(ImageManager.getThumbnails(file)));
            } catch (Exception e) {
                logger.warn("Can't read image:" + file, e);
                setIcon(new ImageIcon(errorImage));
            }
        }
        init();
    }

    public Picture(File file, int width, int height) {
        this.file = file;
        isPicture = true;
        try {
            setIcon(new ImageIcon(ImageManager.getImage(file, width, height)));
        } catch (JPMException e) {
            logger.warn("Can't read image:" + file, e);
            setIcon(new ImageIcon(errorImage));
        }
        init();
    }

    private void setBorder() {
        if (selected) {
            if (selectedBorder == null) {
                selectedBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.blue, 2), file.getName());
                selectedBorder.setTitleJustification(TitledBorder.CENTER);
            }
            setBorder(selectedBorder);
        } else {
            if (unselectedBorder == null) {
                unselectedBorder = BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.black, 2), file.getName());
                unselectedBorder.setTitleJustification(TitledBorder.CENTER);
            }
            setBorder(unselectedBorder);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isPicture() {
        return isPicture;
    }

    public String getPicturePath() {
        return file.getAbsolutePath();
    }

    private void changeSelected() {
        selected = !selected;
    }

    public String getName() {
        return file.getName();
    }

    private void init() {
        setVerticalTextPosition(JLabel.BOTTOM);
        setHorizontalTextPosition(JLabel.CENTER);
        setBorder();
        addMouseListener(mouseListener);
    }
}
