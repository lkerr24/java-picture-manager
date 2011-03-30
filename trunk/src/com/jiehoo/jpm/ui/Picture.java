package com.jiehoo.jpm.ui;

import com.jiehoo.jpm.Constants;
import com.jiehoo.jpm.ImageManager;
import com.jiehoo.jpm.JPMException;
import com.jiehoo.jpm.core.ImageInfo;
import com.jiehoo.jpm.core.Workspace;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class Picture extends JLabel {
    private static final int MAX_ENTRIES = 200;
    private static Map<File, Picture> cache = new LinkedHashMap<File, Picture>(MAX_ENTRIES, .75F, true) {
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_ENTRIES;
        }
    };
    private static Logger logger = Logger.getLogger(Picture.class);
    private static ImageIcon folderImage;
    private static ImageIcon errorImage;
    private static ImageIcon pictureImage;
    private boolean selected = false;
    private TitledBorder selectedBorder;
    private TitledBorder unselectedBorder;
    private File file;
    private boolean isPicture;
    private boolean loaded;

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
                return;
            } else if (picture.isPicture && e.getClickCount() == 2) {
                e.consume();
                ((NavigatePanel) UIManager.getComponent(UIManager.NAVIGATE_PANEL)).selectChild(picture.getName());
                ((MainPanel) UIManager.getComponent(UIManager.MAIN_PANEL)).viewPicture(picture.getPicture());
                return;
            } else if (e.isControlDown()) {
                picture.changeSelected();
                picture.setBorder();
            } else {
                MainPanel mainPanel = (MainPanel) UIManager.getComponent(UIManager.MAIN_PANEL);
                mainPanel.clearSelect();
                picture.changeSelected();
                picture.setBorder();
            }
            TagsPanel tagsPanel = (TagsPanel) UIManager.getComponent(UIManager.TAGS_PANEL);
            tagsPanel.reset();
        }
    };

    static {
        folderImage = new ImageIcon(ImageManager.getImageFromContainer("image_folder", Constants.THUMBNAILS_WIDTH, Constants.THUMBNAILS_HEIGHT));
        errorImage = new ImageIcon(ImageManager.getImageFromContainer("image_error", Constants.THUMBNAILS_WIDTH, Constants.THUMBNAILS_HEIGHT));
        pictureImage = new ImageIcon(ImageManager.getImageFromContainer("image_picture", Constants.THUMBNAILS_WIDTH, Constants.THUMBNAILS_HEIGHT));
    }

    public static Picture getPicture(File file) {
        Picture picture = cache.get(file);
        if (picture == null) {
            picture = new Picture(file);
            cache.put(file, picture);
        } else {
            picture.setSelect(false);
        }
        return picture;
    }

    private Picture(File file) {
        this.file = file;
        if (file.isDirectory()) {
            setIcon(folderImage);
        } else {
            isPicture = true;
            try {
                setIcon(pictureImage);
                setToolTipText(getDescription(file));
            } catch (Exception e) {
                logger.warn("Can't read image:" + file, e);
                setIcon(errorImage);
            }
        }
        init();
    }

    public Picture(File file, int width, int height) {
        this.file = file;
        isPicture = true;
        try {
            setIcon(new ImageIcon(ImageManager.getImage(file, width, height)));
            setToolTipText(getDescription(file));
        } catch (JPMException e) {
            logger.warn("Can't read image:" + file, e);
            setIcon(errorImage);
        }
        init();
    }

    private String getDescription(File path) {
        StringBuilder buffer = new StringBuilder();
        ImageInfo image = Workspace.getInstance().getImage(path);
        buffer.append("<html>");
        buffer.append("Path:").append(path).append("<br>");
        buffer.append("Size:").append(image.getSize()).append("<br>");
        buffer.append("Date:").append(image.getDate()).append("<br>");
        buffer.append("Rank:").append(image.getRank()).append("<br>");
        buffer.append("</html>");
        return buffer.toString();
    }

    public boolean preview() {
        if (isPicture && !loaded) {
            setIcon(new ImageIcon(ImageManager.getThumbnails(file)));
            loaded = true;
            return true;
        }
        return false;
    }

    private void setBorder() {
        if (selected) {
            if (selectedBorder == null) {
                selectedBorder = BorderFactory.createTitledBorder(UIManager.selectedBorder, file.getName());
                selectedBorder.setTitleJustification(TitledBorder.CENTER);
                selectedBorder.setTitlePosition(TitledBorder.BOTTOM);
            }
            setBorder(selectedBorder);
        } else {
            if (unselectedBorder == null) {
                unselectedBorder = BorderFactory.createTitledBorder(UIManager.notSelectedBorder, file.getName());
                unselectedBorder.setTitleJustification(TitledBorder.CENTER);
                unselectedBorder.setTitlePosition(TitledBorder.BOTTOM);
            }
            setBorder(unselectedBorder);
        }
    }

    public void setSelect(boolean selected) {
        this.selected = selected;
        setBorder();
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isPicture() {
        return isPicture;
    }

    public File getPicture() {
        return file;
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
