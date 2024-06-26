package com.iphoto.plus.bean;

import java.io.Serializable;
import java.util.List;

public class CategoryBean implements Serializable {


    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean implements Serializable {
        private String albumId;
        private int count;
        private String id;
        private String name;

        public ResultBean(String name) {
            this.name = name;
        }

        public ResultBean() {
        }

        public ResultBean(String id, String albumId, int count,  String name) {
            this.albumId = albumId;
            this.count = count;
            this.id = id;
            this.name = name;
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
    }
}
