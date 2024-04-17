package com.iphoto.plus.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.iphoto.plus.bean.CategoryResultBean;
import com.iphoto.plus.bean.PicBean;

@Database(entities = {PicBean.class, CategoryResultBean.class}, version = 9)
public abstract class AppDatabase extends RoomDatabase {
        public abstract PicDao picDao();
        public abstract CategoryDao categoryDao();
    }