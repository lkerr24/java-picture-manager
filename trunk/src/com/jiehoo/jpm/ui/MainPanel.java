package com.jiehoo.jpm.ui;

import com.jiehoo.jpm.core.ImageInfo;
import com.jiehoo.jpm.core.Workspace;

import javax.swing.*;
import java.awt.*;
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
        picturesPanel.setLayout(new GridLayout(0, 8, 10, 10));
        for (Picture picture : pictures) {
            picturesPanel.add(picture);
        }
        TagsPanel tagsPanel = new TagsPanel();
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(tagsPanel);
        setLeftComponent(scrollPane);
        scrollPane = new JScrollPane();
        scrollPane.getViewport().add(picturesPanel);
        cardPanel.setLayout(cardLayout);
        cardPanel.add(scrollPane, PICTURES_VIEW);
        cardPanel.add(picturePanel, PICTURE_VIEW);
        currentCard = PICTURES_VIEW;
        setRightComponent(cardPanel);
    }

    public void viewPictures(File[] files) {
        cardLayout.show(cardPanel, PICTURES_VIEW);
        currentCard = PICTURES_VIEW;
        for (File f : files) {
            Picture picture = new Picture(f);
            pictures.add(picture);
            picturesPanel.add(picture);
        }
        updateUI();
    }

    public void reset() {
        pictures.clear();
        picturesPanel.removeAll();
    }

    public void viewPicture(File file) {
        cardLayout.show(cardPanel, PICTURE_VIEW);
        currentCard = PICTURE_VIEW;
        Picture picture = new Picture(file, picturePanel.getWidth(), picturePanel.getHeight());
        picturePanel.removeAll();
        picturePanel.add(picture);
        updateUI();
    }


    public void applyTag(int tagID, boolean remove) {
        if (currentCard.equals(PICTURES_VIEW)) {
            for (Picture picture : pictures) {
                if (picture.isSelected() && picture.isPicture()) {
                    Workspace.getInstance().applyTag(picture.getPicturePath(), tagID, remove);
                }
            }
        } else {
            Picture picture = (Picture) picturePanel.getComponent(0);
            if (picture.isSelected() && picture.isPicture()) {
                Workspace.getInstance().applyTag(picture.getPicturePath(), tagID, remove);
            }
        }
        UIManager.saveWorkspace();
    }

    public void applyRank(int rank) {
        if (currentCard.equals(PICTURES_VIEW)) {
            for (Picture picture : pictures) {
                if (picture.isSelected() && picture.isPicture()) {
                    Workspace.getInstance().applyRank(picture.getPicturePath(), rank);
                }
            }
        } else {
            Picture picture = (Picture) picturePanel.getComponent(0);
            if (picture.isSelected() && picture.isPicture()) {
                Workspace.getInstance().applyRank(picture.getPicturePath(), rank);
            }
        }
        UIManager.saveWorkspace();
    }

    public void searchPictures(List<Integer> ranks, List<Integer> tags) {
        reset();
        cardLayout.show(cardPanel, PICTURES_VIEW);
        currentCard = PICTURES_VIEW;
        List<Map.Entry<String, ImageInfo>> images = Workspace.getInstance().getImages(ranks, tags);
        for (Map.Entry<String, ImageInfo> image : images) {
            Picture picture = new Picture(new File(image.getKey()));
            pictures.add(picture);
            picturesPanel.add(picture);
        }
        updateUI();
    }
}
