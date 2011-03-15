package com.jiehoo.jpm.core;

import java.util.Date;

public class Tag {
    private int ID;
    private String name;
    private Date lastUsedTime;
    private int usedTimes;

    public int getID() {
        return ID;
    }

    public void setID(int id) {
        ID = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(Date lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }

    public int getUsedTimes() {
        return usedTimes;
    }

    public void use() {
        lastUsedTime = new Date();
        usedTimes++;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tag) {
            Tag other = (Tag) obj;
            return ID == other.ID;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ID;
    }


}
