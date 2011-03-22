package com.jiehoo.jpm.ui;

import com.jiehoo.jpm.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 *
 */
public class ExportDialog extends JDialog {
    public ExportDialog() {
        setModal(true);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        JPanel panel = new JPanel();
        panel.add(new JLabel(Utils.resource.getString("label_outputPath")));
        final JTextField field = new JTextField();
        field.setColumns(18);
        panel.add(field);
        JButton button = new JButton(Utils.resource.getString("button_browse"));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File file = UIManager.chooseDirectory();
                if (file != null) {
                    field.setText(file.getAbsolutePath());
                }
            }
        });
        panel.add(button);
        getContentPane().add(panel);

        panel = new JPanel();
        panel.add(new JLabel(Utils.resource.getString("label_resize")));
        final JSlider percent = new JSlider(JSlider.HORIZONTAL, 0, 100, 30);
        percent.setMajorTickSpacing(20);
        percent.setPaintTicks(true);
        percent.setPaintLabels(true);
        panel.add(percent);
        panel.add(new JLabel(Utils.resource.getString("label_percent")));
        getContentPane().add(panel);

        panel = new JPanel();
        button = new JButton(Utils.resource.getString("button_export"));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainPanel mainPanel = (MainPanel) UIManager.getComponent(UIManager.MAIN_PANEL);
                mainPanel.exportPictures(field.getText(), percent.getValue());
                setVisible(false);
            }
        });
        panel.add(button);
        button = new JButton(Utils.resource.getString("button_cancel"));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        panel.add(button);
        getContentPane().add(panel);

        setTitle(Utils.resource.getString("title_export"));
        pack();
        setResizable(false);
        setLocationRelativeTo((MainFrame) UIManager.getComponent(UIManager.MAIN_FRAME));
    }
}
