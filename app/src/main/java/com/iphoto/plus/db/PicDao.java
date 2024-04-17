package com.iphoto.plus.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.iphoto.plus.bean.PicBean;

import java.util.List;

@Dao
public interface PicDao {
    @Query("SELECT * FROM pic_table")
    List<PicBean> getAll();

    @Query("SELECT * FROM pic_table WHERE status Like :status ORDER BY pic_name DESC " )
    List<PicBean> getStatus(int status);

    @Query("SELECT * FROM pic_table WHERE pid IN (:picIds)")
    List<PicBean> loadAllByIds(int[] picIds);

    @Query("SELECT * FROM pic_table WHERE pic_id Like :picId")
    PicBean findById(String picId);

    @Query("SELECT * FROM pic_table WHERE pic_name Like :name")
    PicBean findByName(String name);

    @Query("SELECT * FROM pic_table WHERE pic_name Like :name AND pic_id  Like :pidDate")
    PicBean findByNameAndDate(String name,String pidDate);

    @Insert
    void insertAll(PicBean... pics);

    @Insert
    void insert(PicBean  pic);

    @Update
    void update(PicBean  pic);

    @Delete
    void delete(PicBean pic);

    @Query("DELETE FROM pic_table")
    void delete();
}
    