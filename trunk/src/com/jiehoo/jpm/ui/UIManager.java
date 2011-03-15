package com.jiehoo.jpm.ui;

import com.jiehoo.jpm.core.Workspace;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 *
 */
public class UIManager {
    private static Logger logger = Logger.getLogger(UIManager.class);
    private static Map<String, Object> components = new HashMap<String, Object>();
    public static final String MAIN_FRAME = "MAIN_FRAME";
    public static final String MAIN_PANEL = "MAIN_PANEL";
    public static final String TAGS_PANEL = "TAGS_PANEL";

    public static void setComponent(String name, Object componenet) {
        components.put(name, componenet);
    }

    public static Object getComponent(String name) {
        return components.get(name);
    }

    public static void reportError(String message, Exception e) {
        JOptionPane.showMessageDialog((Component) getComponent(MAIN_FRAME), message
                + (e != null ? " ->" + e.getMessage() : ""));
        logger.error(message, e);
    }


    public static void saveWorkspace() {
        try {
            Workspace.getInstance().save();
        } catch (IOException e) {
            reportError("Can't save workspace file.", e);
        }
    }


}
