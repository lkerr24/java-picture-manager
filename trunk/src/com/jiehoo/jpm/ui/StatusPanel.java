package com.jiehoo.jpm.ui;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class StatusPanel extends JPanel {
    private JLabel totalLabel;
    private JLabel selectedLabel;
    private JLabel fileLabel;
    private JLabel resolutionLabel;
    private static final int WIDTH = 120;
    private static final int HEIGHT = 20;

    public StatusPanel() {
        UIManager.setComponent(UIManager.STATUS_PANEL, this);
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        totalLabel = new JLabel();
        totalLabel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));
        add(totalLabel);
        selectedLabel = new JLabel();
        selectedLabel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        selectedLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));
        add(selectedLabel);
        fileLabel = new JLabel();
        fileLabel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        fileLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));
        add(fileLabel);
        resolutionLabel = new JLabel();
        resolutionLabel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        add(resolutionLabel);
        add(Box.createHorizontalGlue());
    }

    public void setStatus(String total, String selected, String file, String resolution) {
        totalLabel.setText(total);
        setSelected(selected, file, resolution);
    }

    public void setSelected(String selected, String file, String resolution) {
        selectedLabel.setText(selected);
        fileLabel.setText(file);
        resolutionLabel.setText(resolution);
        updateUI();
    }
}
