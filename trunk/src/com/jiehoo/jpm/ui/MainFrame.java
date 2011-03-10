package com.jiehoo.jpm.ui;

import com.jiehoo.jpm.Utils;
import com.jiehoo.jpm.core.Workspace;
import org.apache.log4j.Logger;
import org.jr.swing.JDynamicButton;
import org.jr.swing.MenuItemProperty;
import org.jr.swing.MenuProperty;
import org.jr.swing.util.SwingUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class MainFrame extends JFrame {
    private static Logger logger = Logger.getLogger(MainFrame.class);
    private Preferences preference;
    String frameTitle;

    Container contentPane;
    JToolBar toolbar;
    JMenuBar menubar;
    JSplitPane splitPane;
    NavigatePanel navigatePanel;
    MainPanel mainPanel;

    JDynamicButton addPathButton;
    JDynamicButton scanButton;
    JDynamicButton checkDuplicateButton;
    JDynamicButton viewSlidesButton;
    JDynamicButton exportPicturesButton;
    ArrayList<JDynamicButton> toolbarButtons = new ArrayList<JDynamicButton>();

    JMenuItem addPathMenuItem;
    JMenuItem scanMenuItem;
    JMenuItem checkDuplicateMenuItem;
    JMenuItem viewSlidesMenuItem;
    JMenuItem exportPicturesMenuItem;

    public MainFrame() {
        init();
    }

    private void init() {
        preference = Utils.GetPreferences();
        initFrame();
        contentPane = this.getContentPane();
        navigatePanel = new NavigatePanel();
        mainPanel = new MainPanel();
        UIManager.setComponent(UIManager.MAINPANEL, mainPanel);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, navigatePanel,
                mainPanel);
        splitPane.setResizeWeight(0.1);

        createMenubar();
        createToolbar();
        setJMenuBar(menubar);
        contentPane.add(toolbar, "North");
        contentPane.add(splitPane, "Center");
        initAction();
    }

    private void initFrame() {
        frameTitle = Utils.resource.getString("main_title");
        setTitle(frameTitle);
        setIconImage(getImageIcon(Utils.resource.getString("logo")).getImage());
        setSize(preference.getInt("mainframe_width", 640), preference.getInt(
                "mainframe_height", 480));
        setLocation(preference.getInt("mainframe_location_x", 0), preference
                .getInt("mainframe_location_y", 0));
        if (preference.getBoolean("mainframe_maximize", true)) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        UIManager.setComponent(UIManager.MAIN_FRAME, this);
    }

    private void initAction() {
        AddPathAction addPathAction = new AddPathAction();
        addPathMenuItem.addActionListener(addPathAction);
        addPathButton.addActionListener(addPathAction);
    }

    private void createMenubar() {
        menubar = new JMenuBar();
        JMenu menu = createTopMenu("memu_file");
        // menu.setMnemonic(KeyEvent.VK_F);
        addPathMenuItem = SwingUtil.createMenuItem(new MenuItemProperty(
                Utils.resource.getString("menuitem_addPath")), menu);
        menu = createTopMenu("memu_operation");
        scanMenuItem = SwingUtil.createMenuItem(new MenuItemProperty(Utils.resource
                .getString("menuitem_scan")), menu);
        checkDuplicateMenuItem = SwingUtil.createMenuItem(new MenuItemProperty(
                Utils.resource.getString("menuitem_checkDuplicate")), menu);
        viewSlidesMenuItem = SwingUtil.createMenuItem(new MenuItemProperty(
                Utils.resource.getString("menuitem_viewSlides")), menu);
        exportPicturesMenuItem = SwingUtil.createMenuItem(new MenuItemProperty(
                Utils.resource.getString("menuitem_exportPictures")), menu);
    }

    private JMenu createTopMenu(String key) {
        JMenu menu = SwingUtil.createMenu(new MenuProperty(Utils.resource
                .getString(key)), null);
        menubar.add(menu);
        return menu;
    }

    private void createToolbar() {
        toolbar = new JToolBar();
        addPathButton = createToolbarButton("button_addPath");
        scanButton = createToolbarButton("button_scan");
        checkDuplicateButton = createToolbarButton("button_checkDuplicate");
        viewSlidesButton = createToolbarButton("button_viewSlides");
        exportPicturesButton = createToolbarButton("button_exportPictures");
    }

    private JDynamicButton createToolbarButton(String key) {
        JDynamicButton button;
        String property = Utils.resource.getString(key);
        String[] ps = property.split("\\*");
        button = new JDynamicButton(ps[0], getImageIcon(ps[2]));
        button.setToolTipText(ps[1]);
        button.setVerticalTextPosition(AbstractButton.BOTTOM);
        button.setHorizontalTextPosition(AbstractButton.CENTER);
        button.setMaximumSize(new Dimension(88, 88));
        button.setMinimumSize(new Dimension(88, 88));
        toolbar.add(button);
        return button;
    }

    public ImageIcon getImageIcon(String name) {
        return new ImageIcon(MainFrame.class.getResource(Utils.resource
                .getString("image_path")
                + name));
    }


    public void saveWorkspace() {
        try {
            Workspace.getInstance().save();
        } catch (IOException e) {
            UIManager.reportError("Can't save workspace file.", e);
        }
    }

    public void applyTag(int tagID) {
        String file = navigatePanel.getSelectedPicture();
        Workspace.getInstance().applyTag(file, tagID);
        saveWorkspace();
    }

    private void addPath() {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int returnVal = fileChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            boolean isValidNewPath = Workspace.getInstance().addPath(
                    file.getAbsolutePath());
            if (isValidNewPath) {
                navigatePanel.addNode(file.getAbsolutePath());
                saveWorkspace();
                Thread scanThread = new Thread() {
                    public void run() {
                        try {
                            Workspace.getInstance().scan(false);
                        } catch (IOException e) {
                            UIManager.reportError("Scan workspace error.", e);
                        }
                        saveWorkspace();
                    }
                };
                scanThread.start();
            }
        }
    }


    class AddPathAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            addPath();
        }
    }
}
