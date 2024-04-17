package com.iphoto.plus.view;

import androidx.room.Room;

import com.iphoto.plus.db.AppDatabase;
import com.iphoto.plus.util.UiUtils;

public class AppDB {

    private volatile static AppDatabase db;

    private AppDB() {
    }

    public static AppDatabase getInstance() {

        if (db == null) {

            synchronized ( AppDB.class) {

                if (db == null) {

                    db = Room.databaseBuilder(UiUtils.getContext(),
                            AppDatabase.class, "database-name")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();

                }

            }

        }

        return db;

    }
}