package com.iphoto.plus.mvp.presenter;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.iphoto.plus.R;
import com.iphoto.plus.adapter.PicAdapter;
import com.iphoto.plus.base.BasePresenter;
import com.iphoto.plus.base.CommonObserver;
import com.iphoto.plus.bean.CategoryBean;
import com.iphoto.plus.bean.PicBean;
import com.iphoto.plus.databinding.FragmentLiveBinding;
import com.iphoto.plus.mvp.contract.LiveContract;
import com.iphoto.plus.net.RetrofitClient;
import com.iphoto.plus.net.RxScheduler;
import com.iphoto.plus.net.qiniu.UpLoadImageSubscribe;
import com.iphoto.plus.net.qiniu.UploadTokenSubscribe;
import com.iphoto.plus.ptp.Camera;
import com.iphoto.plus.util.Contents;
import com.iphoto.plus.util.FilesUtil;
import com.iphoto.plus.util.SPUtils;
import com.iphoto.plus.view.AppDB;
import com.iphoto.plus.view.SpaceItemDecoration;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LivePresenter extends BasePresenter<LiveContract.View> implements LiveContract.Presenter {

    private int progress = 0;

    public LivePresenter(LiveContract.View view) {
        super(view);
    }

    @Override
    public void initToolbar(FragmentLiveBinding binding) {
        binding.includeLive.ivIcon.setVisibility(View.GONE);
        binding.includeLive.ivBack.setVisibility(View.VISIBLE);
        binding.includeLive.tvToolbarTitle.setVisibility(View.VISIBLE);
        binding.includeLive.ivSet.setVisibility(View.VISIBLE);
        binding.includeLive.ivRightRight.setVisibility(View.VISIBLE);
//        binding.includeLive.ivRightRight.setText("分享");
    }

    @Override
    public void initRecycleView(Context context, FragmentLiveBinding binding) {
        List<PicBean> dataList = new ArrayList<>();
        PicAdapter mAdapter = new PicAdapter(context, R.layout.item_pic, dataList);
        GridLayoutManager manager = new GridLayoutManager(context, 3);
        binding.rvView.setLayoutManager(manager);
        binding.rvView.setAdapter(mAdapter);
        binding.rvView.addItemDecoration(new SpaceItemDecoration(10, 20));
        mAdapter.setEmptyView(R.layout.empty_layout);
        getView().getRecycleViewData(dataList, mAdapter);
    }

    @Override
    public void setModePx(FragmentLiveBinding viewBinding, PicAdapter adapter) {
        String px = SPUtils.getPxString(Contents.PHOTO_PX);
        String mode = SPUtils.getModeString(Contents.UPLOAD_MODE);
        if (px.equals(Contents.Standard)) {
            viewBinding.tvPx.setText("标清▼");
        } else if (px.equals(Contents.Original)) {
            viewBinding.tvPx.setText("原图▼");
        }

        switch (mode) {
            case Contents.DIRECT_UPLOAD:
                adapter.setHandType(false);
                viewBinding.tvMode.setText("边拍边传▼");
                break;
            case Contents.RATING_UPLOAD:
                adapter.setHandType(false);
                viewBinding.tvMode.setText("标记上传▼");
                break;
            case Contents.HAND_UPLOAD:
                viewBinding.tvMode.setText("点选上传▼");
                adapter.setHandType(true);
                break;
        }

    }

    /**
     * 查询扫描照片的ID
     */

    @Override
    public void getScanPicIds(int[] ids, Camera camera) {

        List<Integer> picIds = new ArrayList<>();
        List<Integer> picIdList = new ArrayList<>();
        for (int id : ids) {
            picIdList.add(id);
        }

//        Flowable.fromIterable(picIdList).parallel(2)
//                .runOn(Schedulers.io())
//                .doOnCancel(() -> {
//                })
//                .map(integer -> {
//
//                    PicBean byPid = AppDB.getInstance().picDao().findById(String.valueOf(integer));
//                    if (byPid == null) {
//                        picIds.add(integer);
//                    }
//                    return "1";
//
//                })
//                .sequential()
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnComplete(() -> getView().scanIdComplete(picIds))
//                .subscribe(s -> {
//                });
        Observable.fromIterable(picIdList)
                .map(integer -> {
                    PicBean byPid = AppDB.getInstance().picDao().findById(String.valueOf(integer));
                    if (byPid == null) {
                        picIds.add(integer);
                    }
                    return "1";

                })
                .compose(RxScheduler.Obs_io_main())
                .to(getView().bindAutoDispose())
                .subscribe(new CommonObserver<String>() {
                    @Override
                    public void onNext(String s) {
                    }
                    @Override
                    public void onComplete() {
                        getView().scanIdComplete(picIds);
                    }
                });

    }

    /**
     * 扫描照片
     */
    @Override
    public void scanningPic(List<Integer> picIds, Camera camera, String albumId) {
        progress = 0;
        String uploadMode = SPUtils.getModeString(Contents.UPLOAD_MODE);
        String photoPx = SPUtils.getPxString(Contents.PHOTO_PX);
        Observable.fromIterable(picIds)
                .concatMap((Function<Integer, ObservableSource<String>>) id -> Observable.create((ObservableOnSubscribe<String>) emitter ->
                        camera.retrieveImage((objectHandle, byteBuffer, length, info) -> {

                            String filePath ;
                            if (info!=null) {
                                filePath =FilesUtil.getFileFromBytes(albumId, uploadMode,
                                        byteBuffer.array(), length, info.filename, objectHandle, photoPx);
                            }else{
                                filePath =FilesUtil.getFileFromBytes(albumId, uploadMode,
                                        byteBuffer.array(), length,String.valueOf(objectHandle), objectHandle, photoPx);
                            }
                            emitter.onNext(filePath);
                            emitter.onComplete();
                        }, id)))
                .compose(RxScheduler.Obs_io_main())
                .to(getView().bindAutoDispose())
                .subscribe(new CommonObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        progress++;
                        getView().getScanProgress(progress);
                    }
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mScanDisposable = d;
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        getView().getScanComplete();
                    }
                });
    }

    private Disposable mDisposable;
    private Disposable mScanDisposable;
    /**
     * 批量上传
     */

    @Override
    public void upLoadPic(List<PicBean> pathList, String albumId) {

        String mode = SPUtils.getModeString(Contents.UPLOAD_MODE);
        if (mode.equals(Contents.HAND_UPLOAD)) {
            return;
        }
        Observable.fromIterable(pathList)
                .concatMap((Function<PicBean, ObservableSource<PicBean>>) picBean -> Observable.create(new UploadTokenSubscribe(picBean, albumId)))
                .concatMap((Function<PicBean, ObservableSource<PicBean>>) picBean -> Observable.create(new UpLoadImageSubscribe(picBean)))
                .compose(RxScheduler.Obs_io_main())
                .to(getView().bindAutoDispose())
                .subscribe(new CommonObserver<PicBean>() {
                    @Override
                    public void onNext(@NotNull PicBean bean) {
                        if(getView()!=null){
                            getView().getUploadNext(bean);
                        }
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        super.onSubscribe(d);
                        mDisposable = d;
                    }
                });
    }
    /**
     * 重新上传
     */

    @Override
    public void reUpLoadPic(List<PicBean> pathList, String albumId) {

        Observable.fromIterable(pathList)
                .concatMap((Function<PicBean, ObservableSource<PicBean>>) picBean -> Observable.create(new UploadTokenSubscribe(picBean, albumId)))
                .concatMap((Function<PicBean, ObservableSource<PicBean>>) picBean -> Observable.create(new UpLoadImageSubscribe(picBean)))
                .compose(RxScheduler.Obs_io_main())
                .to(getView().bindAutoDispose())
                .subscribe(new CommonObserver<PicBean>() {
                    @Override
                    public void onNext(@NotNull PicBean bean) {
                        if(getView()!=null){
                            getView().getReUploadNext(bean);
                        }
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        if(getView()!=null){
                            getView().getReUploadComplete();
                        }
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        super.onSubscribe(d);
                        mDisposable = d;
                    }
                });
    }

    /**
     * 边拍边传
     */
    @Override
    public void upLoadSingle(int picId, Camera camera, String albumId, String qiNiuToken) {

        String uploadMode = SPUtils.getModeString(Contents.UPLOAD_MODE);
        String photoPx = SPUtils.getPxString(Contents.PHOTO_PX);

        Observable.create((ObservableOnSubscribe<PicBean>) emitter
                -> camera.retrieveImage((objectHandle, byteBuffer, length, info)
                -> {
                    if (info!=null){
                        FilesUtil.getFileFromBytes(albumId, uploadMode,
                                byteBuffer.array(), length, info.filename, objectHandle, photoPx);
                    }else{
                        FilesUtil.getFileFromBytes(albumId, uploadMode,
                                byteBuffer.array(), length, String.valueOf(objectHandle), objectHandle, photoPx);
                    }

            PicBean byId = AppDB.getInstance().picDao().findById(String.valueOf(picId));
            getView().getUploadSingleAdd(byId);
            emitter.onNext(byId);
            emitter.onComplete();
        }, picId))
                .flatMap((Function<PicBean, ObservableSource<PicBean>>) picBean
                        -> Observable.create(new UploadTokenSubscribe(picBean, albumId)))
                .flatMap((Function<PicBean, ObservableSource<PicBean>>) picBean
                        -> Observable.create(new UpLoadImageSubscribe(picBean)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<PicBean>() {
                    @Override
                    public void onNext(PicBean bean) {
                        getView().getUploadSingleNext(bean);
                    }
                });

    }

    /**
     * 手动上传时，动态获取照片
     */
    @Override
    public void addHandBean(int picId, Camera camera, String albumId, String qiNiuToken, String mode) {
        String photoPx = SPUtils.getPxString(Contents.PHOTO_PX);
        Observable.create((ObservableOnSubscribe<PicBean>) emitter -> camera.retrieveImage((objectHandle, byteBuffer, length, info) -> {
            FilesUtil.getFileFromBytes(albumId, mode,
                    byteBuffer.array(), length, info.filename, objectHandle,  photoPx);
            PicBean byId = AppDB.getInstance().picDao().findById(String.valueOf(picId));
            emitter.onNext(byId);
            emitter.onComplete();
        }, picId)).compose(RxScheduler.Obs_io_main())
                .to(getView().bindAutoDispose())
                .subscribe(new CommonObserver<PicBean>() {
                    @Override
                    public void onNext(PicBean bean) {
                        getView().getUploadHandAdd(bean);
                    }
                });

    }

    @Override
    public void deleteDB() {
        Flowable.create((FlowableOnSubscribe<Boolean>) emitter -> {
            AppDB.getInstance().picDao().delete();
//            AppDB.getInstance().categoryDao().delete();
            emitter.onNext(true);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

    }

    /**
     * 查询已上传的照片
     */
    @Override
    public void queryUploadedPics() {

        Flowable<Map<String, List<PicBean>>> f1 = Flowable.create(emitter -> {
            List<PicBean> all = AppDB.getInstance().picDao().getStatus(1);
            Map<String, List<PicBean>> map = new HashMap<>();
            map.put("net", all);
            emitter.onNext(map);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);

        Flowable<Map<String, List<PicBean>>> f2 = Flowable.create(emitter -> {
            List<PicBean> all2 = AppDB.getInstance().picDao().getStatus(0);
            Map<String, List<PicBean>> map2 = new HashMap<>();
            map2.put("local", all2);
            emitter.onNext(map2);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);

        Flowable.concat(f1, f2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(map -> getView().getAllPics(map));
    }

    /**
     * 重新上传
     */
    @Override
    public void queryReUploadPics() {
        Flowable.create((FlowableOnSubscribe< List<PicBean> >) emitter -> {
            List<PicBean> reUpload= AppDB.getInstance().picDao().getStatus(4);
            emitter.onNext(reUpload);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reUploadList -> getView().getReUpload(reUploadList));
    }

    /**
     * 手动上传0
     */
    @Override 
    public void handUploadPic(PicBean bean, String albumId) {

        Observable.create((ObservableOnSubscribe<PicBean>) emitter -> {
            emitter.onNext(bean);
            emitter.onComplete();
        })
                .flatMap((Function<PicBean, ObservableSource<PicBean>>) bean1 -> Observable.create(new UploadTokenSubscribe(bean1, albumId)))
                .flatMap((Function<PicBean, ObservableSource<PicBean>>) picBean -> Observable.create(new UpLoadImageSubscribe(picBean)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<PicBean>() {
                    @Override
                    public void onNext(PicBean bean) {
                        getView().getUploadHandNext(bean);
                    }
                });
    }

    @Override
    public Disposable getDispose() {
        return mDisposable;
    }

    @Override
    public Disposable getScanDispose() {
        return mScanDisposable;
    }

    @Override
    public void getCategoryList(String albumId) {
        RetrofitClient.getServer().getCategoryList(albumId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<CategoryBean>() {
                    @Override
                    public void onNext(CategoryBean bean) {
                        getView().setCategoryList(bean);
                    }
                });
    }

}
