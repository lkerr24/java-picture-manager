package com.jiehoo.jpm.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainPanel extends JSplitPane {
    private static final String PICTURES_VIEW="PICTURES_VIEW";
    private static final String PICTURE_VIEW="PICTURE_VIEW";
    private List<Picture> pictures = new ArrayList<Picture>();
    private JPanel picturesPanel = new JPanel();
    private JPanel picturePanel = new JPanel();
    private CardLayout cardLayout=new CardLayout();
    private JPanel cardPanel=new JPanel();

    public MainPanel() {
        super(JSplitPane.VERTICAL_SPLIT);
        this.setResizeWeight(0.1);
        picturesPanel.setLayout(new GridLayout(0, 8, 10, 10));
        for (Picture picture : pictures) {
            picturesPanel.add(picture);
        }
        TagsPanel tagsPanel = new TagsPanel();
        JScrollPane scrollPane=new JScrollPane();
        scrollPane.getViewport().add(tagsPanel);
        setLeftComponent(scrollPane);
        cardPanel.setLayout(cardLayout);
        cardPanel.add(picturesPanel,PICTURES_VIEW);
        cardPanel.add(picturePanel,PICTURE_VIEW);
        picturePanel.setLayout(new ViewportLayout());
        scrollPane=new JScrollPane();
        scrollPane.getViewport().add(cardPanel);
        setRightComponent(scrollPane);
    }

    public void viewPictures(File[] files)
    {
         cardLayout.show(cardPanel,PICTURES_VIEW);
        for (File f : files) {
            Picture picture=new Picture(f);
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
        cardLayout.show(cardPanel,PICTURE_VIEW);
        Picture picture=new Picture(file,picturesPanel.getWidth(),(int)(getHeight()*(1-getResizeWeight()))-getDividerSize()-100);
        picturePanel.removeAll();
        picturePanel.add(picture);
        updateUI();
    }
}
