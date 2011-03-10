package com.jiehoo.jpm.ui;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


/**
 *
 */
public class UIManager {
    private static Logger logger = Logger.getLogger(UIManager.class);
    private static Map<String, Object> components=new HashMap<String,Object>();
    public static final String MAIN_FRAME="MAIN_FRAME";
    public static final String MAINPANEL ="MAINPANEL";

    public static void setComponent(String name,Object componenet)
    {
         components.put(name,componenet);
    }

    public static Object getComponent(String name)
    {
        return components.get(name);
    }

    public static void reportError(String message, Exception e) {
        JOptionPane.showMessageDialog((Component)getComponent(MAIN_FRAME), message
                + (e != null ? " ->" + e.getMessage() : ""));
        logger.error(message, e);
    }

}
