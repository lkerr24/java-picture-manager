package com.jiehoo.jpm.ui;

import com.jiehoo.jpm.Utils;
import com.jiehoo.jpm.core.Workspace;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.io.File;

public class NavigatePanel extends JScrollPane {
    private JTree tree;
    private DefaultMutableTreeNode topNode;

    public NavigatePanel() {
        topNode=new DefaultMutableTreeNode(Utils.resource                .getString("workspace"));
        tree=new JTree(topNode);
        getViewport().add(tree);
        tree.setExpandsSelectedPaths(true);
        for (String path : Workspace.getInstance().getPaths()) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(path);
            topNode.add(node);
        }
        tree.expandPath(new TreePath(topNode));
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
                        .getPath().getLastPathComponent();
                if (node != topNode) {
                    String path = getPath(node);
                    File file = new File(path);
                    if (file.isDirectory()) {
                        MainPanel mainPanel = (MainPanel) UIManager.getComponent(UIManager.MAINPANEL);
                        mainPanel.reset();
                        File[] files = file.listFiles(Utils.fileTreeFilter);
                        if (files != null) {
                            for (File f : files) {
                                if (f.isDirectory()&&f.getName().equalsIgnoreCase("thumbnails"))
                                {
                                    continue;
                                }
                                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(
                                        f.getName());
                                node.add(childNode);
                                mainPanel.addPicture(new Picture(f));
                                mainPanel.updateUI();
                            }
                            ((DefaultTreeModel) tree.getModel())
                                    .nodeStructureChanged(node);
                            tree.expandPath(e.getPath());
                        }
                    } else {
                        ((MainPanel)UIManager.getComponent(UIManager.MAINPANEL)).viewPicture(file);
                    }
                }
            }
        });
    }

    public String getSelectedPicture() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
        return getPath(node);
    }

    private String getPath(DefaultMutableTreeNode node) {
        StringBuilder buffer = new StringBuilder();
        buffer.insert(0, (String) node.getUserObject());
        while (node.getParent() != topNode) {
            node = (DefaultMutableTreeNode) node.getParent();
            buffer.insert(0, "\\");
            buffer.insert(0, node.getUserObject());
        }
        return buffer.toString();
    }

    public void addNode(String dir) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(dir);
        topNode.add(node);
        ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(topNode);
    }
}
