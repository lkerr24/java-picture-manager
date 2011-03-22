package com.jiehoo.jpm;

import java.io.File;
import java.io.FileFilter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class Utils {
    public static ResourceBundle resource = ResourceBundle.getBundle("resources", getLocale());

    public static Locale getLocale() {
        Locale locale = Locale.getDefault();
        if (!locale.equals(Locale.getDefault())) {
            Locale.setDefault(locale);
        }
        return locale;
    }

    public static Preferences GetPreferences() {
        return Preferences.userRoot().node("jpm");
    }

    public static FileFilter dirFilter = new FileFilter() {
        public boolean accept(File dir) {
            return (!dir.getName().startsWith(".")) && dir.isDirectory();
        }
    };

    public static FileFilter fileTreeFilter = new FileFilter() {
        public boolean accept(File dir) {
            return (!dir.getName().startsWith(".")) && (dir.isDirectory() && !dir.getName().equalsIgnoreCase(Constants.THUMBNAILS_DIRECTORY)) || (dir.isFile() && dir.getName().toLowerCase().endsWith(".jpg"));
        }
    };
}
