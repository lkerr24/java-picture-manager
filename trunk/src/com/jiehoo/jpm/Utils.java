package com.jiehoo.jpm;

import java.io.File;
import java.io.FileFilter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import com.jiehoo.jpm.core.DirectoryFilter;

public class Utils {
    public static ResourceBundle resource = ResourceBundle.getBundle("resources", getLocale());

    public static Locale getLocale() {
		Locale locale = Locale.getDefault();
		if (!locale.equals(Locale.getDefault())) {
			Locale.setDefault(locale);
		}
		return locale;
	}
	
	public static Preferences GetPreferences()
	{
		return Preferences.userRoot().node("jpm");
	}

	public static FileFilter dirFilter = new DirectoryFilter();
	
	public static FileFilter fileTreeFilter = new FileFilter() {
		public boolean accept(File dir) {
			if ((dir.isDirectory()&&!dir.getName().equalsIgnoreCase("thumbnails"))||(dir.isFile()&&dir.getName().toLowerCase().endsWith(".jpg"))){
				return true;
			}
			return false;
		}
	};
}
