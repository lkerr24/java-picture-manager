package com.jiehoo.jpm.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DuplicateItem {
    private String id;
    private List<File> paths = new ArrayList<File>();

    public List<File> getPaths() {
        return paths;
    }

    public void setPaths(List<File> paths) {
        this.paths = paths;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
