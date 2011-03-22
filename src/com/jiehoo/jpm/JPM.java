package com.jiehoo.jpm;

import com.jiehoo.jpm.core.Workspace;
import com.jiehoo.jpm.ui.MainFrame;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.util.prefs.Preferences;

public class JPM {
    public static Preferences preference;
    private static Logger logger = Logger.getLogger(JPM.class);

    public static void main(String[] args) {
        if (!canRun()) {
            System.exit(1);
        }
        init();
        if (isExistedInstance()) {
            int result = JOptionPane.showConfirmDialog(null, Utils.resource.getString("message_runningInstance"), Utils.resource.getString("title_runningInstance"), JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                logger.error("Have one instance is running, please close it before run this.");
                System.exit(1);
            }
        }
        preference.putBoolean("is_running", true);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                preference.putBoolean("is_running", false);
            }
        });
        String output = preference.get("output", null);
        if (output == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setDialogTitle(Utils.resource.getString("title_output"));

            int returnVal = fileChooser.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                output = fileChooser.getSelectedFile().getAbsolutePath();
                preference.put("output", output);
            }
        }
        try {
            Workspace.init();
        } catch (IOException e) {
            logger.error("Can't init workspace.", e);
            System.exit(1);
        }
        MainFrame frame = new MainFrame();
        frame.setVisible(true);
    }

    private static void init() {
        preference = Utils.GetPreferences();
    }

    private static boolean isExistedInstance() {
        return preference.getBoolean("is_running", false);
    }

    private static boolean canRun() {
        return isJREVersionCompatible();
    }

    private static boolean isJREVersionCompatible() {
        Class<?> testClass;
        try {
            testClass = Class.forName("java.lang.Object");
        } catch (ClassNotFoundException e) {
            logger.error("Can't load Object class.", e);
            return false;
        }

        Package testPackage = testClass.getPackage();
        if (testPackage == null) {
            System.err
                    .println("Pls use JRE compatible with SUN JDK 1.4 or later to run this program");
            return false;
        } else if (!testPackage.isCompatibleWith("1.5")) {
            System.err.println("JDK Vendor:"
                    + testPackage.getImplementationVendor() + ",JDK version:"
                    + testPackage.getSpecificationVersion());
            System.err
                    .println("Pls use JRE compatible with JDK 1.5 or later to run this program");
            return false;
        }
        return true;
    }

}
