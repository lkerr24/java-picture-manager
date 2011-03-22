package com.jiehoo.jpm.ui;

import com.jiehoo.jpm.Utils;
import com.jiehoo.jpm.core.DuplicateItem;
import com.jiehoo.jpm.core.Workspace;
import org.jr.swing.JDynamicButton;
import org.jr.swing.MenuItemProperty;
import org.jr.swing.MenuProperty;
import org.jr.swing.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class MainFrame extends JFrame {
    private Preferences preference;
    String frameTitle;

    Container contentPane;
    JToolBar toolbar;
    JMenuBar menubar;
    JSplitPane splitPane;
    NavigatePanel navigatePanel;
    MainPanel mainPanel;

    JDynamicButton addPathButton;
    JDynamicButton searchButton;
    JDynamicButton checkDuplicateButton;
    JDynamicButton viewSlidesButton;
    JDynamicButton exportPicturesButton;
    ArrayList<JDynamicButton> toolbarButtons = new ArrayList<JDynamicButton>();

    JMenuItem addPathMenuItem;
    JMenuItem searchMenuItem;
    JMenuItem checkDuplicateMenuItem;
    JMenuItem viewSlidesMenuItem;
    JMenuItem exportPicturesMenuItem;

    SearchDialog searchDialog;
    ResolveDuplicateDialog resolveDuplicateDialog;
    ExportDialog exportDialog;

    public MainFrame() {
        init();
    }

    private void init() {
        preference = Utils.GetPreferences();
        initFrame();
        contentPane = this.getContentPane();
        navigatePanel = new NavigatePanel();
        mainPanel = new MainPanel();
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, navigatePanel,
                mainPanel);
        splitPane.setResizeWeight(0.1);

        createMenubar();
        createToolbar();
        setJMenuBar(menubar);
        contentPane.add(toolbar, "North");
        contentPane.add(splitPane, "Center");
        initAction();
        scan();
    }

    private void initFrame() {
        UIManager.setComponent(UIManager.MAIN_FRAME, this);
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
    }

    private void initAction() {
        AddPathAction addPathAction = new AddPathAction();
        addPathMenuItem.addActionListener(addPathAction);
        addPathButton.addActionListener(addPathAction);
        SearchAction searchAction = new SearchAction();
        searchMenuItem.addActionListener(searchAction);
        searchButton.addActionListener(searchAction);
        CheckDuplicateAction checkDuplicateAction = new CheckDuplicateAction();
        checkDuplicateMenuItem.addActionListener(checkDuplicateAction);
        checkDuplicateButton.addActionListener(checkDuplicateAction);
        ExportAction exportAction = new ExportAction();
        exportPicturesMenuItem.addActionListener(exportAction);
        exportPicturesButton.addActionListener(exportAction);
    }

    private void createMenubar() {
        menubar = new JMenuBar();
        JMenu menu = createTopMenu("memu_file");
        // menu.setMnemonic(KeyEvent.VK_F);
        addPathMenuItem = SwingUtil.createMenuItem(new MenuItemProperty(
                Utils.resource.getString("menuitem_addPath")), menu);
        menu = createTopMenu("memu_operation");
        searchMenuItem = SwingUtil.createMenuItem(new MenuItemProperty(Utils.resource
                .getString("menuitem_search")), menu);
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
        addPathButton = createToolbarButton("toolbarbutton_addPath");
        searchButton = createToolbarButton("toolbarbutton_search");
        checkDuplicateButton = createToolbarButton("toolbarbutton_deduplicate");
        viewSlidesButton = createToolbarButton("toolbarbutton_viewSlides");
        exportPicturesButton = createToolbarButton("toolbarbutton_exportPictures");
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

    private void scan() {
        Thread scanThread = new Thread() {
            public void run() {
                try {
                    Workspace.getInstance().scan(false);
                } catch (IOException e) {
                    UIManager.reportError("Scan workspace error.", e);
                }
                UIManager.saveWorkspace();
            }
        };
        scanThread.start();
    }

    private void addPath() {
        File file = UIManager.chooseDirectory();
        if (file != null) {
            boolean isValidNewPath = Workspace.getInstance().addPath(
                    file.getAbsolutePath());
            if (isValidNewPath) {
                navigatePanel.addNode(file.getAbsolutePath());
                UIManager.saveWorkspace();
                scan();
            }
        }
    }

    private void search() {
        if (searchDialog == null) {
            searchDialog = new SearchDialog();
        }
        searchDialog.setVisible(true);
    }

    private void checkDuplicate() {
        java.util.List<DuplicateItem> duplicates = Workspace.getInstance().getDuplicates();
        if (duplicates.size() == 0) {
            JOptionPane.showMessageDialog(this, Utils.resource.getString("message_noDuplicate"));
        } else {
            resolveDuplicateDialog = new ResolveDuplicateDialog(duplicates);
            resolveDuplicateDialog.setVisible(true);
        }
    }

    private void export() {
        if (exportDialog == null) {
            exportDialog = new ExportDialog();
        }
        if (mainPanel.hasSelectedPictures()) {
            exportDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, Utils.resource.getString("message_noSelectedPictures"));
        }
    }


    class AddPathAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            addPath();
        }
    }

    class SearchAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            search();
        }
    }

    class CheckDuplicateAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            checkDuplicate();
        }
    }

    class ExportAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            export();
        }
    }
}
