package com.iphoto.plus.bean;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "category_table")
public class CategoryResultBean implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public long gid;
    @ColumnInfo(name = "category_id")
    public String id;
    public String albumId;
    public int count;
    public String name;
    @ColumnInfo(name = "is_check")
    public boolean check;

    public CategoryResultBean( String id, String albumId, int count, String name,boolean check) {
        this.id = id;
        this.albumId = albumId;
        this.count = count;
        this.name = name;
        this.check = check;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public boolean getCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}