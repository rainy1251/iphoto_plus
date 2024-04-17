package com.iphoto.plus.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.iphoto.plus.bean.CategoryResultBean;

import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM category_table")
    List<CategoryResultBean> getAll();

    @Query("SELECT * FROM category_table WHERE category_id Like :id")
    CategoryResultBean findById(String id);

    @Query("SELECT * FROM category_table WHERE name Like :name ")
    CategoryResultBean findByName(String name);

    @Query("SELECT * FROM category_table WHERE is_check Like :check")
    List<CategoryResultBean> findTrueValues(boolean check);

    @Insert
    void insertAll(CategoryResultBean... pics);

    @Insert
    void insert(CategoryResultBean  pic);

    @Update
    void update(CategoryResultBean  pic);

    @Delete
    void delete(CategoryResultBean pic);

    @Query("DELETE FROM category_table")
    void delete();
}
    