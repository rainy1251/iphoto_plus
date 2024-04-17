package com.iphoto.plus.components.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iphoto.plus.R;
import com.iphoto.plus.adapter.PicAdapter;
import com.iphoto.plus.base.BaseMvpFragment;
import com.iphoto.plus.bean.CategoryBean;
import com.iphoto.plus.bean.CategoryResultBean;
import com.iphoto.plus.bean.PicBean;
import com.iphoto.plus.components.activity.ChangeCardActivity;
import com.iphoto.plus.components.activity.SettingActivity;
import com.iphoto.plus.databinding.FragmentLiveBinding;
import com.iphoto.plus.event.ChangeCardEvent;
import com.iphoto.plus.mvp.contract.LiveContract;
import com.iphoto.plus.mvp.presenter.LivePresenter;
import com.iphoto.plus.ptp.Camera;
import com.iphoto.plus.ptp.PtpConstants;
import com.iphoto.plus.ptp.model.LiveViewData;
import com.iphoto.plus.util.Contents;
import com.iphoto.plus.util.FilesUtil;
import com.iphoto.plus.util.MyToast;
import com.iphoto.plus.util.SPUtils;
import com.iphoto.plus.util.TrafficInfo;
import com.iphoto.plus.view.AppDB;
import com.iphoto.plus.view.CommonDialog;
import com.iphoto.plus.view.GroupPop;
import com.iphoto.plus.view.LargePicDialog;
import com.iphoto.plus.view.ModePop;
import com.iphoto.plus.view.PxPop;
import com.iphoto.plus.view.QrCodeDialog;
import com.iphoto.plus.view.ScanningDialog;
import com.iphoto.plus.view.SessionActivity;
import com.iphoto.plus.view.SessionView;
import com.king.zxing.util.CodeUtils;
import com.qiniu.android.utils.AsyncRun;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LiveFragment extends BaseMvpFragment<LivePresenter, FragmentLiveBinding>
        implements Camera.StorageInfoListener, LiveContract.View, SessionView, View.OnClickListener
        , CommonDialog.OnConfirmListener {

    private List<PicBean> dataList;
    private final List<Integer> picIds = new ArrayList<>();
    private PicAdapter mAdapter;
    private boolean isCreate = true;
    private LivePresenter presenter;
    private ProgressBar pbBar;
    private TextView tvProgress;
    private ScanningDialog dialog;
    private String qiNiuToken;
    private int total;
    private int reUpLoadNum;
    private String albumId;
    private int localNum;
    private boolean autoScan;
    private boolean reUpLoadFlag = true;
    private TrafficInfo speed;
    private int groupCount;
    private String groupName = "全部";
    private boolean groupFlag;
    private String cameraName = "";
    private boolean isSd = true;

    @Override
    protected void initView() {
        ((SessionActivity) Objects.requireNonNull(getActivity())).setSessionView(this);
        presenter = new LivePresenter(this);
        presenter.initToolbar(viewBinding);
        presenter.initRecycleView(getActivity(), viewBinding);
        initRefreshLayout(viewBinding.srlView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        autoScan = SPUtils.getBooleanDefTrue(Contents.AUTO);
        Intent intent = (Objects.requireNonNull(getActivity())).getIntent();

        String title = intent.getStringExtra(Contents.Title);
        albumId = intent.getStringExtra(Contents.AlbumId);
        qiNiuToken = intent.getStringExtra(Contents.QiNiuToken);
        total = intent.getIntExtra(Contents.Total, 0);

        viewBinding.includeLive.tvToolbarTitle.setText(title);
        viewBinding.tvId.setText("相册码：" + albumId);
        viewBinding.tvSuccess.setText(total + "张");
        testNetSpeed();

        presenter.queryUploadedPics();

        initListener();
       presenter.getCategoryList(albumId);
    }

    private void initListener() {
        viewBinding.includeLive.ivBack.setOnClickListener(this);
        // viewBinding.rgCard.setOnCheckedChangeListener(this);
        viewBinding.includeLive.ivSet.setOnClickListener(this);
        viewBinding.tvGroup.setOnClickListener(this);
        viewBinding.tvMode.setOnClickListener(this);
        viewBinding.tvPx.setOnClickListener(this);
        viewBinding.tvCardOne.setOnClickListener(this);
        viewBinding.tvCardTwo.setOnClickListener(this);
        viewBinding.llReLoad.setOnClickListener(this);
        viewBinding.includeLive.ivRightRight.setOnClickListener(this);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            String mode = SPUtils.getModeString(Contents.UPLOAD_MODE);
            if ((dataList.get(position).getStatus() == 0 || dataList.get(position).getStatus() == 4) && mode.equals(Contents.HAND_UPLOAD)) {
                presenter.handUploadPic(dataList.get(position), albumId);
            } else {
                LargePicDialog dialog = new LargePicDialog(getActivity(), dataList.get(position).getPicPath());
                dialog.show();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_live;
    }

    protected Camera camera() {
        if (getActivity() == null) {
            return null;
        }
        return ((SessionActivity) getActivity()).getCamera();
    }

    //    String storageLabel = "";
    @Override
    public void onStorageFound(int handle, String label) {

    }

    int storageId;

    boolean inStart;

    @Override
    public void onStart() {
        super.onStart();
        inStart = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        inStart = false;
        if (presenter.getScanDispose() != null) {
            presenter.getScanDispose().dispose();
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

    @Override
    public void onAllStoragesFound(String storageIds) {
        if (!inStart || camera() == null) {
            return;
        }
        AsyncRun.runInMain(() -> viewBinding.tvCard.setText(storageIds));

        if (cameraName.contains("Canon")) {

            if (isSd) {
                if (storageIds.contains("131072")) {
                    storageId = 131072;
                } else {
                    storageId = 131073;
                }

            } else {
                if (storageIds.contains("65536")) {
                    storageId = 65536;
                } else {
                    storageId = 65537;
                }
            }
        } else {
            if (isSd) {
                if (storageIds.contains("131073")) {
                    storageId = 131073;
                } else {
                    storageId = 131072;
                }

            } else {
                if (storageIds.contains("65537")) {
                    storageId = 65537;
                } else {
                    storageId = 65536;
                }
            }

        }
        camera().retrieveImageHandles(this, storageId, PtpConstants.ObjectFormat.EXIF_JPEG);

    }

    //获取照片总数
    @SuppressLint("SetTextI18n")
    @Override
    public void onImageHandlesRetrieved(int[] handles) {
        AsyncRun.runInMain(() -> {

            presenter.setModePx(viewBinding, mAdapter);

            localNum = handles.length;
            viewBinding.tvTotal.setText(localNum + "张");
            if (!isCreate) {
                return;
            }
            if (!inStart) {
                return;
            }
            picIds.clear();
            if (autoScan) {
                presenter.getScanPicIds(handles, camera());
            }
            isCreate = false;
        });
    }

    @Override
    public void scanIdComplete(List<Integer> ids) {
        if (ids.size() > 0) {
            picIds.addAll(ids);
            showDialog(ids);
        } else {
            MyToast.show("扫描完成，没有找到新照片");

        }
    }

    /**
     * 手动上传进度更新
     */

    @SuppressLint({"NotifyDataSetChanged", "DefaultLocale"})
    @Override
    public void getUploadHandNext(PicBean bean) {
        if (bean.getStatus() == 1) {
            AppDB.getInstance().picDao().update(bean);
            total++;
            groupCount++;
            viewBinding.tvSuccess.setText(String.format("%d张", total));
            viewBinding.tvGroup.setText(String.format("%s (%d)▼", groupName, groupCount));
        } else if (bean.getStatus() == -1) {
            bean.setStatus(4);
            AppDB.getInstance().picDao().update(bean);
            reUpLoadNum++;
            viewBinding.tvReLoad.setText(reUpLoadNum + "张");
            mAdapter.remove(bean);
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 手动模式下，按快门增加照片
     */
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void getUploadHandAdd(PicBean bean) {
        if (bean != null) {
            dataList.add(0, bean);
            mAdapter.notifyDataSetChanged();
        }
    }
//TODO
    //TOdo
    @SuppressLint("DefaultLocale")
    @Override
    public void setCategoryList(CategoryBean beans) {
        if (beans.getResult().size() == 0) {
            viewBinding.tvGroup.setVisibility(View.GONE);
            return;
        }
        groupCount = total;
        boolean flag = SPUtils.getBoolean("groupFlag");
        if (!flag) {
            viewBinding.tvGroup.setText(String.format("%s (%d)▼", "选择标签", total));
            for (int i = 0; i < beans.getResult().size(); i++) {
                CategoryBean.ResultBean resultBean = beans.getResult().get(i);
                CategoryResultBean bean = new CategoryResultBean(resultBean.getId(), resultBean.getAlbumId(), resultBean.getCount(), resultBean.getName(), false);
                AppDB.getInstance().categoryDao().insert(bean);
            }
            SPUtils.save("groupFlag",true);

        }
    }

    @Override
    public void getReUpload(List<PicBean> reUploadList) {
        dataList.addAll(0, reUploadList);
        mAdapter.notifyItemRangeChanged(0, reUploadList.size());
        presenter.reUpLoadPic(reUploadList, albumId);//上传全部上传失败的照片
    }


    private void showDialog(List<Integer> ids) {
        String title = "扫描有新照片";
        String content = "共扫描" + ids.size() + "张，是否上传";
        CommonDialog dialog = new CommonDialog(getActivity(), title, content, 1);
        dialog.setOnConfirmListener(this);
        dialog.show();
    }

    @Override
    public void enableUi(boolean enabled) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void cameraStarted(Camera camera) {
        if (camera != null) {
            isCreate = true;
            cameraName = camera.getDeviceName();
            viewBinding.tvConnect.setText("已连接");
            viewBinding.tvConnect.setTextColor(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.color_success));
            viewBinding.tvCamara.setText("相机型号：" + cameraName);
            camera.retrieveStorages(this);
        } else {
            viewBinding.tvCamara.setText("未连接");
        }

    }

    @Override
    public void cameraStopped(Camera camera) {
        if (camera == null) {
            viewBinding.tvCamara.setText("未连接");
            viewBinding.tvConnect.setText("未连接");
            isCreate = false;
            viewBinding.tvConnect.setTextColor(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.color_error));
        }

    }

    @Override
    public void cameraNoFound() {

        presenter.setModePx(viewBinding, mAdapter);
        viewBinding.tvConnect.setText("未连接");
    }

    @Override
    public void propertyChanged(int property, int value) {

    }

    @Override
    public void propertyDescChanged(int property, int[] values) {

    }


    @Override
    public void setCaptureBtnText(String text) {

    }

    @Override
    public void focusStarted() {

    }

    @Override
    public void focusEnded(boolean hasFocused) {

    }

    @Override
    public void liveViewStarted() {

    }

    @Override
    public void liveViewStopped() {

    }

    @Override
    public void liveViewData(LiveViewData data) {

    }

    @Override
    public void capturedPictureReceived(int objectHandle, String filename, Bitmap thumbnail, Bitmap bitmap) {
        Log.i("rain", filename + "===filename");

    }

    //增加照片

    @SuppressLint("SetTextI18n")
    @Override
    public void objectAdded(int handle, int format) {
        if (camera() == null) {
            return;
        }
        if (format == PtpConstants.ObjectFormat.EXIF_JPEG) {
            localNum++;
            viewBinding.tvTotal.setText(localNum + "张");
            String uploadMode = SPUtils.getModeString(Contents.UPLOAD_MODE);
            if (uploadMode.equals(Contents.HAND_UPLOAD)) {
                presenter.addHandBean(handle, camera(), albumId, qiNiuToken, uploadMode);
            } else {
                presenter.upLoadSingle(handle, camera(), albumId, qiNiuToken);
            }
        }
    }

    /**
     * 新增标星照片
     */
    @Override
    public void rateAdded(int handle) {
        if (camera() == null) {
            return;
        }
        localNum++;
        PicBean byId = AppDB.getInstance().picDao().findById(String.valueOf(handle));

        if (byId == null) {

            presenter.upLoadSingle(handle, camera(), albumId, qiNiuToken);
        }
    }


    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_back:
                (Objects.requireNonNull(getActivity())).finish();
                break;
            case R.id.iv_set:
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);

                break;
            case R.id.iv_right_right:
                createQrCode(albumId);
                break;
            case R.id.tv_mode:
                ModePop modePop = ModePop.newInstance(getActivity(), viewBinding.tvMode);
                modePop.setCheckModeListener(modeType -> viewBinding.tvMode.setText(modeType + "▼"));
                break;
            case R.id.tv_px:
                PxPop pxPop = PxPop.newInstance(getActivity(), viewBinding.tvPx);
                pxPop.setCheckPxListener(pxType -> viewBinding.tvPx.setText(pxType));
                break;
            case R.id.tv_group:

                List<CategoryResultBean> trueValues = AppDB.getInstance().categoryDao().findTrueValues(true);
                if (trueValues.size() > 0) {
                    //TODO
                    //TODO
                    GroupPop groupPop = GroupPop.newInstance(getActivity(), viewBinding.tvGroup, trueValues);
                    groupPop.setCheckGroupListener(bean -> {
                        if (!bean.getName().contains("无标签")) {
                            SPUtils.save(Contents.categoryId, String.valueOf(bean.getId()));
                            groupName = bean.getName().replaceAll("\\s", "");
                            groupCount = bean.getCount();
                            viewBinding.tvGroup.setText(String.format("%s (%d)▼", bean.getName().replaceAll("\\s", ""), bean.getCount()));
                        } else {
                            SPUtils.save(Contents.categoryId, "");
                            groupName = "选择标签";
                            groupCount = total;
                            viewBinding.tvGroup.setText(String.format("%s (%d)▼", "选择标签", total));
                        }

                    });
                } else {
                    List<CategoryResultBean> all = AppDB.getInstance().categoryDao().getAll();
                    GroupPop groupPop = GroupPop.newInstance(getActivity(), viewBinding.tvGroup, all);
                    groupPop.setCheckGroupListener(bean -> {
                        if (!bean.getName().contains("无标签")) {
                            SPUtils.save(Contents.categoryId, String.valueOf(bean.getId()));
                            groupName = bean.getName().replaceAll("\\s", "");
                            groupCount = bean.getCount();
                            viewBinding.tvGroup.setText(String.format("%s (%d)▼", bean.getName().replaceAll("\\s", ""), bean.getCount()));
                        } else {
                            SPUtils.save(Contents.categoryId, "");
                            groupName = "选择标签";
                            groupCount = total;
                            viewBinding.tvGroup.setText(String.format("%s (%d)▼", "选择标签", total));
                        }

                    });
                }

                break;
            case R.id.ll_reLoad:
                List<PicBean> reUpload = AppDB.getInstance().picDao().getStatus(4);
                int imageIdFromSD = FilesUtil.getImageIdFromSD(albumId);
                if (reUpload.size() > 0 && imageIdFromSD > 0) {
                    if (reUpLoadFlag) {
                        CommonDialog commonDialog = new CommonDialog(getActivity(), "确认重新上传", "上传失败的照片将会重新上传", 5);
                        commonDialog.setOnConfirmListener(this);
                        commonDialog.show();
                    } else {
                        MyToast.show("正在重新上传");
                    }

                } else {
                    viewBinding.tvReLoad.setText(0 + "张");
                    MyToast.show("没有可重新上传的照片");
                }


                break;
            case R.id.tv_card_one:
                if (isSd) {
                    return;
                }
                viewBinding.tvCardOne.setTextColor(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.ip_blue));
                viewBinding.tvCardOne.setBackgroundResource(R.drawable.selector_checked);
                viewBinding.tvCardTwo.setBackgroundResource(R.drawable.selector_unchecked);
                viewBinding.tvCardTwo.setTextColor(getActivity().getResources().getColor(R.color.black));
                isSd = true;
                EventBus.getDefault().post(new ChangeCardEvent(true));
                Intent changeOne = new Intent(getActivity(), ChangeCardActivity.class);
                startActivity(changeOne);
                break;
            case R.id.tv_card_two:
                if (!isSd) {
                    return;
                }
                viewBinding.tvCardOne.setTextColor(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.black));
                viewBinding.tvCardTwo.setTextColor(getActivity().getResources().getColor(R.color.ip_blue));
                isSd = false;
                viewBinding.tvCardOne.setBackgroundResource(R.drawable.selector_unchecked);
                viewBinding.tvCardTwo.setBackgroundResource(R.drawable.selector_checked);
                EventBus.getDefault().post(new ChangeCardEvent(true));
                Intent intentChange = new Intent(getActivity(), ChangeCardActivity.class);
                startActivity(intentChange);
                break;
        }
    }

    @Override
    public void getRecycleViewData(List<PicBean> dataList, PicAdapter adapter) {
        this.dataList = dataList;
        this.mAdapter = adapter;
    }

    /**
     * 确认扫描
     */
    @Override
    public void confirm(int type) {
        if (type == 1) {
            dialog = new ScanningDialog(getActivity());
            pbBar = dialog.findViewById(R.id.pb_bar);
            tvProgress = dialog.findViewById(R.id.tv_progress);
            pbBar.setMax(picIds.size());
            dialog.show();
            presenter.scanningPic(picIds, camera(), albumId);
        } else if (type == 5) {
            reUpLoadFlag = false;
            presenter.queryReUploadPics();
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void getScanProgress(int progress) {
        if (pbBar == null) {
            return;
        }
        pbBar.setProgress(progress);
        tvProgress.setText(progress + "/" + picIds.size());
    }

    /**
     * 相机扫描完成
     */
    @Override
    public void getScanComplete() {
        dialog.dismiss();
        dataList.clear();
        presenter.queryUploadedPics();
    }


    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void getUploadNext(PicBean bean) {

        if (bean.getStatus() == 1) {
            AppDB.getInstance().picDao().update(bean);
            total++;
            groupCount++;
            viewBinding.tvSuccess.setText(total + "张");
            viewBinding.tvGroup.setText(groupName + " (" + groupCount + ")▼");
        } else if (bean.getStatus() == -1) {
            bean.setStatus(4);
            AppDB.getInstance().picDao().update(bean);
            reUpLoadNum++;
            viewBinding.tvReLoad.setText(reUpLoadNum + "张");
            mAdapter.remove(bean);
        }
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void getReUploadNext(PicBean bean) {
        if (bean.getStatus() == 1) {
            AppDB.getInstance().picDao().update(bean);
            total++;
            groupCount++;
            reUpLoadNum--;
            viewBinding.tvReLoad.setText(reUpLoadNum + "张");
            viewBinding.tvSuccess.setText(total + "张");
            viewBinding.tvGroup.setText(groupName + " (" + groupCount + ")▼");
        }
        if (bean.getStatus() == -1) {
            bean.setStatus(4);
            AppDB.getInstance().picDao().update(bean);
            mAdapter.remove(bean);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void getReUploadComplete() {
        reUpLoadFlag = true;
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void getUploadSingleNext(PicBean bean) {
        if (bean.getStatus() == 1) {
            AppDB.getInstance().picDao().update(bean);
            total++;
            if (viewBinding != null) {
                groupCount++;
                viewBinding.tvSuccess.setText(total + "张");
                viewBinding.tvGroup.setText(groupName + " (" + groupCount + ")▼");
            }
        } else if (bean.getStatus() == -1) {
            bean.setStatus(4);
            AppDB.getInstance().picDao().update(bean);
            reUpLoadNum++;
            viewBinding.tvReLoad.setText(reUpLoadNum + "张");
            mAdapter.remove(bean);
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 快门照片添加到列表
     */
    @Override
    public void getUploadSingleAdd(PicBean bean) {
        AsyncRun.runInMain(() -> {
            if (bean != null) {

                dataList.add(0, bean);
                mAdapter.notifyItemRangeChanged(0, 10);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void getAllPics(Map<String, List<PicBean>> map) {
        if (map.containsKey("net")) {
            List<PicBean> pics1 = map.get("net");
            if (pics1 != null && pics1.size() > 0) {
                dataList.addAll(pics1);
                mAdapter.notifyDataSetChanged(); //获取同步后的照片
            }
        } else if (map.containsKey("local")) {
            List<PicBean> pics2 = map.get("local");
            if (pics2 != null && pics2.size() > 0) {
                dataList.addAll(0, pics2);
                mAdapter.notifyDataSetChanged();
                presenter.upLoadPic(pics2, albumId);//上传全部未上传的照片
            }
        }

    }

    /**
     * 开启网速监测
     */
    private void testNetSpeed() {
        try {
            Handler mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        if (viewBinding != null) {
                            viewBinding.tvSpeed.setText(String.format("网速：%s", (msg.obj).toString()));
                        }
                    }
                    super.handleMessage(msg);
                }

            };
            speed = new TrafficInfo(getActivity(), mHandler, TrafficInfo.getUid());
            speed.startCalculateNetSpeed();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void createQrCode(String id) {
        Bitmap qrCode = CodeUtils.createQRCode(Contents.ACTIVITY_URL + id, 600);
        QrCodeDialog dialog = new QrCodeDialog(getActivity(), qrCode);
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter.getDispose() != null) {
            presenter.getDispose().dispose();
        }
        if (speed != null) {
            speed.stopCalculateNetSpeed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter.getDispose() != null) {
            presenter.getDispose().dispose();
        }
        SPUtils.save(Contents.categoryId, "");
        isCreate = true;
        SPUtils.save(Contents.SDCARD, false);
        presenter.deleteDB();

    }

}
