package com.iphoto.plus.mvp.contract;

import android.content.Context;

import com.iphoto.plus.adapter.PicAdapter;
import com.iphoto.plus.base.BaseView;
import com.iphoto.plus.bean.CategoryBean;
import com.iphoto.plus.bean.PicBean;
import com.iphoto.plus.databinding.FragmentLiveBinding;
import com.iphoto.plus.ptp.Camera;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.disposables.Disposable;


public interface LiveContract {
    interface Model {
    }

    interface View extends BaseView {
        void getRecycleViewData(List<PicBean> dataList, PicAdapter adapter);
        void getScanProgress(int progress);
        void getScanComplete();
        void getUploadNext(PicBean bean);
        void getReUploadNext(PicBean bean);
        void getReUploadComplete();

        void getUploadSingleNext(PicBean bean);
        void getUploadSingleAdd(PicBean picId);

        void getAllPics(Map<String, List<PicBean>> map);
        void scanIdComplete(List<Integer> picIds);

        void getUploadHandNext(PicBean bean);

        void getUploadHandAdd(PicBean bean);
        void setCategoryList(CategoryBean bean);
        void getReUpload(List<PicBean> reUploadList);
    }

    interface Presenter {
        void initToolbar(FragmentLiveBinding binding);
        void initRecycleView(Context context, FragmentLiveBinding binding);
        void setModePx(FragmentLiveBinding binding,PicAdapter adapter);
        void scanningPic(List<Integer> picIds, Camera camera,String activity);
        void upLoadPic(List<PicBean> pathList, String token);
        void reUpLoadPic(List<PicBean> pathList, String token);
        void upLoadSingle(int picId, Camera camera,String albumId,String qiNiuToken);
        void addHandBean(int picId, Camera camera,String albumId,String qiNiuToken,String mode);
        void deleteDB();
        void queryUploadedPics();
        void queryReUploadPics();
        void getScanPicIds(int[] handles,Camera camera);
        void handUploadPic(PicBean bean,String albumId);
        Disposable getDispose();
        Disposable getScanDispose();
        void getCategoryList(String albumId);

    }

}
