package com.jiehoo.jpm.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainPanel extends JSplitPane {
    private List<Picture> pictures = new ArrayList<Picture>();
    private JPanel picturesPanel = new JPanel();
    private TagsPanel tagsPanel;

    public MainPanel() {
        super(JSplitPane.VERTICAL_SPLIT);
        this.setResizeWeight(0.1);
        picturesPanel.setLayout(new GridLayout(0, 8, 10, 10));
        for (Picture picture : pictures) {
            picturesPanel.add(picture);
        }
        tagsPanel = new TagsPanel();
        JScrollPane scrollPane=new JScrollPane();
        scrollPane.getViewport().add(tagsPanel);
        setLeftComponent(scrollPane);
        setRightComponent(picturesPanel);
        picturesPanel.setMaximumSize(new Dimension(getWidth(), (int) (getHeight() * getResizeWeight())));
    }

    public void addPicture(Picture picture) {
        pictures.add(picture);
        picturesPanel.add(picture);
    }

    public void reset() {
        pictures.clear();
        picturesPanel.removeAll();
    }

    public void viewPicture(File file) {

    }
}
