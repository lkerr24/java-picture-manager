package com.jiehoo.jpm.ui;

import com.jiehoo.jpm.ImageManager;
import com.jiehoo.jpm.core.ImageInfo;
import com.jiehoo.jpm.core.Workspace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainPanel extends JSplitPane {
    private static final String PICTURES_VIEW = "PICTURES_VIEW";
    private static final String PICTURE_VIEW = "PICTURE_VIEW";
    private List<Picture> pictures = new ArrayList<Picture>();
    private JPanel picturesPanel = new JPanel();
    private JPanel picturePanel = new JPanel();
    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel = new JPanel();
    private String currentCard;

    public MainPanel() {
        super(JSplitPane.VERTICAL_SPLIT);
        UIManager.setComponent(UIManager.MAIN_PANEL, this);
        this.setResizeWeight(0.1);
        TagsPanel tagsPanel = new TagsPanel();
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(tagsPanel);
        setLeftComponent(scrollPane);
        scrollPane = new JScrollPane();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(picturesPanel);
        panel.add(Box.createVerticalGlue());
        scrollPane.getViewport().add(panel);
        cardPanel.setLayout(cardLayout);
        cardPanel.add(scrollPane, PICTURES_VIEW);
        cardPanel.add(picturePanel, PICTURE_VIEW);
        currentCard = PICTURES_VIEW;
        setRightComponent(cardPanel);
        picturesPanel.addMouseListener(new MouseListener() {
            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                picturesPanel.requestFocusInWindow();
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
            }
        });
        picturesPanel.getActionMap().put("selectAll", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                for (Picture picture : pictures) {
                    picture.setSelect(true);
                }
                picturesPanel.updateUI();
            }
        });
        InputMap inputMap = picturesPanel.getInputMap();
        inputMap.put(KeyStroke.getKeyStroke("control A"), "selectAll");
    }

    public void viewPictures(File[] files) {
        cardLayout.show(cardPanel, PICTURES_VIEW);
        currentCard = PICTURES_VIEW;
        int columns = getWidth() / 200;
        if (columns == 0) {
            columns = 1;
        }
        picturesPanel.setLayout(new GridLayout(0, columns, 10, 10));
        for (File f : files) {
            Picture picture = new Picture(f);
            pictures.add(picture);
            picturesPanel.add(picture);
        }
        updateUI();
    }

    public void clearSelect() {
        for (Picture picture : pictures) {
            picture.setSelect(false);
        }
    }

    public void reset() {
        pictures.clear();
        picturesPanel.removeAll();
    }

    public void viewPicture(File file) {
        cardLayout.show(cardPanel, PICTURE_VIEW);
        currentCard = PICTURE_VIEW;
        Picture picture = new Picture(file, picturePanel.getWidth(), picturePanel.getHeight() - 30);
        picturePanel.removeAll();
        picturePanel.add(picture);
        picture.setSelect(true);
        updateUI();
    }


    public void applyTag(int tagID, boolean remove) {
        List<Picture> pictures = getSelectedPictures(true);
        for (Picture picture : pictures) {
            Workspace.getInstance().applyTag(picture.getPicture(), tagID, remove);
        }
        UIManager.saveWorkspace();
    }

    public void applyRank(int rank) {
        List<Picture> pictures = getSelectedPictures(true);
        for (Picture picture : pictures) {
            Workspace.getInstance().applyRank(picture.getPicture(), rank);
        }
        UIManager.saveWorkspace();
    }

    public void searchPictures(List<Integer> ranks, List<Integer> tags) {
        reset();
        cardLayout.show(cardPanel, PICTURES_VIEW);
        currentCard = PICTURES_VIEW;
        List<Map.Entry<File, ImageInfo>> images = Workspace.getInstance().getImages(ranks, tags);
        for (Map.Entry<File, ImageInfo> image : images) {
            Picture picture = new Picture(image.getKey());
            pictures.add(picture);
            picturesPanel.add(picture);
        }
        updateUI();
    }

    public void exportPictures(String path, int percent) {
        List<Picture> pictures = getSelectedPictures(true);
        for (Picture picture : pictures) {
            ImageManager.resizeImage(picture.getPicture(), ((float) percent) / 100, new File(path, picture.getName()));
        }
    }

    public List<Picture> getSelectedPictures(boolean excludeDirectory) {
        List<Picture> result = new ArrayList<Picture>();
        if (currentCard.equals(PICTURES_VIEW)) {
            for (Picture picture : pictures) {
                if (picture.isSelected() && (!excludeDirectory || picture.isPicture())) {
                    result.add(picture);
                }
            }
        } else {
            Picture picture = (Picture) picturePanel.getComponent(0);
            if (picture.isSelected() && (!excludeDirectory || picture.isPicture())) {
                result.add(picture);
            }
        }
        return result;
    }

    public boolean hasSelectedPictures() {
        if (currentCard.equals(PICTURES_VIEW)) {
            for (Picture picture : pictures) {
                if (picture.isSelected() && picture.isPicture()) {
                    return true;
                }
            }
        } else {
            Picture picture = (Picture) picturePanel.getComponent(0);
            if (picture.isSelected() && picture.isPicture()) {
                return true;
            }
        }
        return false;
    }
}
