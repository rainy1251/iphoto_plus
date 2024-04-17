package com.iphoto.plus.mvp.presenter;

import android.os.Environment;
import android.text.TextUtils;

import androidx.exifinterface.media.ExifInterface;

import com.iphoto.plus.base.BasePresenter;
import com.iphoto.plus.base.CommonObserver;
import com.iphoto.plus.bean.OriginalPicBean;
import com.iphoto.plus.bean.PicBean;
import com.iphoto.plus.mvp.contract.NoticeContract;
import com.iphoto.plus.mvp.model.NoticeModel;
import com.iphoto.plus.net.RxScheduler;
import com.iphoto.plus.util.Contents;
import com.iphoto.plus.util.FilesUtil;
import com.iphoto.plus.util.ModifyExif;
import com.iphoto.plus.util.UiUtils;
import com.iphoto.plus.view.AppDB;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NoticePresenter extends BasePresenter<NoticeContract.View> implements NoticeContract.Presenter {

    private final NoticeModel model;
    private int syncNum = 0;

    public NoticePresenter(NoticeContract.View view) {
        super(view);
        model = new NoticeModel();
    }

    @Override
    public void getOriginalPic(int pageIndex, String albumId, String photographerId) {
        if (pageIndex == -1) {
            getView().getOriginalPic(null);
        } else {

            int offset = pageIndex * Contents.offsetNum;
            model.getOriginalPic(offset, albumId, photographerId)
                    .compose(RxScheduler.Obs_io_main())
                    .to(getView().bindAutoDispose())//解决内存泄漏
                    .subscribe(new CommonObserver<OriginalPicBean>() {
                        @Override
                        public void onNext(@NotNull OriginalPicBean bean) {
                            getView().getOriginalPic(bean);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            super.onError(e);
                            getView().getError();
                        }
                    });
        }
    }

    @Override
    public void syncData(ArrayList<OriginalPicBean.ResultBean> result) {
        Flowable.fromIterable(result).parallel(4)
                .runOn(Schedulers.io())
                .doOnCancel(new Action() {
                    @Override
                    public void run() throws Throwable {
                    }
                })
                .map(new Function<OriginalPicBean.ResultBean, Integer>() {
                    @Override
                    public Integer apply(@NonNull OriginalPicBean.ResultBean bean) throws Throwable {
//TODO
                        if (!TextUtils.isEmpty(bean.getName())) {

                            PicBean picBean = new PicBean(bean.getName(), bean.getSize(), bean.getUrl(), bean.getLocalId(), 100, 1);

                            AppDB.getInstance().picDao().insert(picBean);
                            syncNum++;
                        }
                        return syncNum;
                    }

                })
                .sequential()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Throwable {
                        getView().syncComplete();
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        getView().syncNum(integer);
                    }

                });
    }

    @Override
    public void syncSD(String albumId) {
        String absolutePath = UiUtils.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        File fileAll = new File(absolutePath+ File.separator + "YAOPAI", albumId);

        if (!fileAll.exists()) {
            getView().syncSDComplete();
            return;
        }
        File[] files = fileAll.listFiles();
        if (files == null) {
            getView().syncSDComplete();
            return;
        }

        if (files.length == 0) {
            getView().syncSDComplete();
            return;
        }
        for (File file : files) {
            String id = ModifyExif.getExif(file.getAbsolutePath()).getAttribute(ExifInterface.TAG_COPYRIGHT);
            PicBean picBean = new PicBean(file.getName(), (int) file.length()
                    , file.getAbsolutePath(), id, 0, 0);
            PicBean byId = AppDB.getInstance().picDao().findById(id);
            if (byId == null) {
                AppDB.getInstance().picDao().insert(picBean);
            } else {
                FilesUtil.deleteFile(FilesUtil.getPicPath(albumId,file.getName()));
            }

        }
        getView().syncSDComplete();
    }
}
