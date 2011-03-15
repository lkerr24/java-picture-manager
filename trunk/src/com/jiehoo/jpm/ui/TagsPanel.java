package com.jiehoo.jpm.ui;

import com.jiehoo.jpm.core.Tag;
import com.jiehoo.jpm.core.Workspace;
import com.jiehoo.jpm.Utils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class TagsPanel extends JPanel {
	private JPanel tagsPanel;

	public TagsPanel() {
        JPanel panel=new JPanel();
        panel.add(new JLabel(Utils.resource.getString("label_newTag")));
		final JTextField newTagField = new JTextField();
        newTagField.setColumns(20);
		setLayout(new BorderLayout());
        panel.add(newTagField);
        JButton button=new JButton(Utils.resource.getString("button_newTag"));
        panel.add(button);
		add(panel, "South");
        ActionListener actionListener=new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Tag tag = Workspace.getInstance().createTag(
						newTagField.getText());
				addTag(tag.getID(), tag.getName());
				tagsPanel.updateUI();
				newTagField.setText("");
				((MainFrame)UIManager.getComponent(UIManager.MAIN_FRAME)).saveWorkspace();
			}
		};

		newTagField.addActionListener(actionListener);
        button.addActionListener(actionListener);
        
		tagsPanel = new JPanel();
		tagsPanel.setLayout(new GridLayout(0, 10, 5, 5));
		add(tagsPanel, "Center");
		HashMap<Integer, Tag> tags = Workspace.getInstance().getTags();
		for (Tag tag : tags.values()) {
			addTag(tag.getID(), tag.getName());
		}
	}

	public void addTag(final int tagID, String name) {
		TagButton button = new TagButton(tagID, name);
		tagsPanel.add(button);
	}

	class TagButton extends JToggleButton {
		public TagButton(final int tagID, String name) {
			super(name);
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					((MainFrame)UIManager.getComponent(UIManager.MAIN_FRAME)).applyTag(tagID);
				}
			});
            setBorder(new BevelBorder(BevelBorder.RAISED));
		}
	}

}
