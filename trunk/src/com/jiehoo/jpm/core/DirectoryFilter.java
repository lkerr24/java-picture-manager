package com.jiehoo.jpm.core;

import java.io.File;
import java.io.FileFilter;

public class DirectoryFilter implements FileFilter {

	public boolean accept(File pathname) {
		if (pathname.isDirectory())
		{
			return true;
		}
		else
		{
			return false;
		}
	}

}
