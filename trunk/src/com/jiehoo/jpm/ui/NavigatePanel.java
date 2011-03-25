package com.jiehoo.jpm.ui;

import com.jiehoo.jpm.ImageManager;
import com.jiehoo.jpm.Utils;
import com.jiehoo.jpm.core.Workspace;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class NavigatePanel extends JScrollPane {
    private JTree tree;
    private DefaultMutableTreeNode topNode;
    private Set<String> loadedNodes = new HashSet<String>();

    public NavigatePanel() {
        UIManager.setComponent(UIManager.NAVIGATE_PANEL, this);
        topNode = new MyMutableTreeNode(Workspace.getInstance().getRootPath(), true);
        tree = new JTree(topNode);
        getViewport().add(tree);
        tree.setExpandsSelectedPaths(true);
        tree.setCellRenderer(new MyTreeCellRenderer());
        tree.expandPath(new TreePath(topNode));
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TagsPanel tagsPanel = (TagsPanel) UIManager.getComponent(UIManager.TAGS_PANEL);
                tagsPanel.reset();
                selectNode((MyMutableTreeNode) e.getPath().getLastPathComponent());
            }
        });
        tree.addMouseListener(new MouseListener() {
            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                tree.requestFocusInWindow();
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
            }
        });
    }

    public void selectChild(String childName) {
        for (Enumeration e = ((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).children(); e.hasMoreElements();) {
            MyMutableTreeNode child = (MyMutableTreeNode) e.nextElement();
            if (child.getUserObject().equals(childName)) {
                selectNode(child);
                TreePath treePath = new TreePath(child.getPath());
                tree.setSelectionPath(treePath);
                tree.scrollPathToVisible(treePath);
                return;
            }
        }
    }

    public void selectNode(MyMutableTreeNode node) {
        String path = getPath(node);
        File file = new File(path);
        if (file.isDirectory()) {
            MainPanel mainPanel = (MainPanel) UIManager.getComponent(UIManager.MAIN_PANEL);
            mainPanel.reset();
            File[] files = file.listFiles(Utils.fileTreeFilter);
            if (files != null) {

                if (!loadedNodes.contains(path)) {
                    for (File f : files) {
                        if (f.isDirectory() && f.getName().equalsIgnoreCase("thumbnails")) {
                            continue;
                        }
                        MyMutableTreeNode childNode = new MyMutableTreeNode(f.getName());
                        childNode.detectType(f.getAbsolutePath());
                        node.add(childNode);
                    }
                    //((DefaultTreeModel) tree.getModel()).nodeStructureChanged(node);
                }
                mainPanel.viewPictures(files);
            }
        } else {
            ((MainPanel) UIManager.getComponent(UIManager.MAIN_PANEL)).viewPicture(file);
        }
        loadedNodes.add(path);
        tree.expandPath(new TreePath(node.getPath()));
    }

    private static String getPath(MyMutableTreeNode node) {
        StringBuilder buffer = new StringBuilder();
        buffer.insert(0, (String) node.getUserObject());
        while (node.getParent() != null) {
            node = (MyMutableTreeNode) node.getParent();
            buffer.insert(0, "\\");
            buffer.insert(0, node.getUserObject());
        }
        return buffer.toString();
    }

    public void addNode(String dir) {
        MyMutableTreeNode node = new MyMutableTreeNode(dir);
        node.detectType(dir);
        topNode.add(node);
        ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(topNode);
    }

    static class MyMutableTreeNode extends DefaultMutableTreeNode {
        boolean isRoot;
        boolean isDirectory;
        boolean containsPictures;
        boolean containsDirectories;
        boolean notExist;

        public MyMutableTreeNode(String name) {
            this(name, false);
        }

        public MyMutableTreeNode(String name, boolean isRoot) {
            super(name);
            this.isRoot = isRoot;
        }

        public void detectType(String path) {
            File file = new File(path);
            if (!file.exists()) {
                notExist = true;
            } else if (file.isDirectory()) {
                isDirectory = true;
                File[] files = file.listFiles(Utils.fileTreeFilter);
                if (files != null) {
                    for (File f : files) {
                        if (f.isDirectory()) {
                            containsDirectories = true;
                        } else {
                            containsPictures = true;
                        }
                    }
                }
            }
        }
    }

    static class MyTreeCellRenderer extends JLabel implements TreeCellRenderer {
        static Icon workspaceIcon;
        static Icon folderIcon;
        static Icon notExistFolderIcon;
        static Icon emptyFolderIcon;
        static Icon pictureFolderIcon;
        static Icon pictureIcon;
        static int width = 16;
        static int height = 16;

        static {
            workspaceIcon = new ImageIcon(ImageManager.getImageFromContainer("icon_workspace", width, height));
            notExistFolderIcon = new ImageIcon(ImageManager.getImageFromContainer("icon_notExistFolder", width, height));
            folderIcon = new ImageIcon(ImageManager.getImageFromContainer("icon_folder", width, height));
            emptyFolderIcon = new ImageIcon(ImageManager.getImageFromContainer("icon_emptyFolder", width, height));
            pictureFolderIcon = new ImageIcon(ImageManager.getImageFromContainer("icon_pictureFolder", width, height));
            pictureIcon = new ImageIcon(ImageManager.getImageFromContainer("icon_picture", width, height));
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            MyMutableTreeNode node = (MyMutableTreeNode) value;
            if (node.isRoot) {
                setIcon(workspaceIcon);
            } else if (node.notExist) {
                setIcon(notExistFolderIcon);
            } else if (node.isDirectory) {
                if (node.containsPictures) {
                    setIcon(pictureFolderIcon);
                } else if (node.containsDirectories) {
                    setIcon(folderIcon);
                } else {
                    setIcon(emptyFolderIcon);
                }
            } else {
                //setIcon(new ImageIcon(ImageManager.getThumbnails(new File(getPath(node)))));
                setIcon(pictureIcon);
            }
            setText((String) node.getUserObject());
            if (selected) {
                setForeground(Color.blue);
            } else {
                setForeground(null);
            }
            return this;
        }
    }
}
