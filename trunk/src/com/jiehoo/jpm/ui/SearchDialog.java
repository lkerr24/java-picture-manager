package com.jiehoo.jpm.ui;

import com.jiehoo.jpm.Utils;
import com.jiehoo.jpm.core.Tag;
import com.jiehoo.jpm.core.Workspace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 */
public class SearchDialog extends JDialog {
    private ArrayList<Integer> ranks = new ArrayList<Integer>();
    private ArrayList<Integer> tagids = new ArrayList<Integer>();
    private ArrayList<JCheckBox> rankButtons = new ArrayList<JCheckBox>();
    private ArrayList<TagButton> tagButtons = new ArrayList<TagButton>();

    public SearchDialog() {
        getContentPane().setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.add(new JLabel(Utils.resource.getString("label_rank")));
        ItemListener itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    ranks.add(Integer.parseInt(((JCheckBox) e.getSource()).getText()));
                } else {
                    ranks.remove(new Integer(((JCheckBox) e.getSource()).getText()));
                }
            }
        };
        for (int i = 0; i < 6; i++) {
            JCheckBox checkbox = new JCheckBox(i + "");
            panel.add(checkbox);
            rankButtons.add(checkbox);
            checkbox.addItemListener(itemListener);
        }
        getContentPane().add(panel, "North");

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TagButton tagButton = (TagButton) e.getSource();
                if (tagButton.getModel().isSelected()) {
                    tagids.add(tagButton.getTag().getID());
                } else {
                    tagids.remove(new Integer(tagButton.getTag().getID()));
                }
                ((MainPanel) UIManager.getComponent(UIManager.MAIN_PANEL)).applyTag(tagButton.getTag().getID(), !tagButton.getModel().isSelected());
            }
        };
        JPanel tagsPanel = new JPanel();
        tagsPanel.setLayout(new GridLayout(0, 10, 5, 5));
        HashMap<Integer, Tag> tags = Workspace.getInstance().getTags();
        for (Tag tag : tags.values()) {
            TagButton button = new TagButton(tag);
            button.addActionListener(actionListener);
            tagButtons.add(button);
            tagsPanel.add(button);
        }
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(tagsPanel);
        getContentPane().add(scrollPane, "Center");

        panel = new JPanel();
        JButton search = new JButton(Utils.resource.getString("button_search"));
        search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainPanel mainPanel = (MainPanel) UIManager.getComponent(UIManager.MAIN_PANEL);
                mainPanel.searchPictures(ranks, tagids);
                setVisible(false);
            }
        });
        panel.add(search);
        JButton cancel = new JButton(Utils.resource.getString("button_cancel"));
        panel.add(cancel);
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        JButton reset = new JButton(Utils.resource.getString("button_reset"));
        panel.add(reset);
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (JCheckBox button : rankButtons) {
                    button.setSelected(false);
                }
                for (TagButton button : tagButtons) {
                    button.setSelected(false);
                }
            }
        });
        getContentPane().add(panel, "South");

        pack();
        setResizable(false);
        setLocationRelativeTo((MainFrame) UIManager.getComponent(UIManager.MAIN_FRAME));

    }
}
