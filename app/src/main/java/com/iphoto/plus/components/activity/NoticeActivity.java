package com.iphoto.plus.components.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;

import com.iphoto.plus.R;
import com.iphoto.plus.base.BaseMvpActivity;
import com.iphoto.plus.bean.OriginalPicBean;
import com.iphoto.plus.databinding.ActivityNoticeBinding;
import com.iphoto.plus.mvp.contract.NoticeContract;
import com.iphoto.plus.mvp.presenter.NoticePresenter;
import com.iphoto.plus.util.Contents;
import com.iphoto.plus.util.MyToast;
import com.iphoto.plus.util.PermissionUtils;
import com.iphoto.plus.util.SPUtils;
import com.iphoto.plus.view.AppDB;
import com.iphoto.plus.view.CommonDialog;

import java.util.ArrayList;

public class NoticeActivity extends BaseMvpActivity<NoticePresenter, ActivityNoticeBinding>
        implements NoticeContract.View, View.OnClickListener, CommonDialog.OnConfirmListener {

    private NoticePresenter presenter;
    private String photographerId;
    private String albumId;
    private int pageIndex = 0;
    private String title;
    private int total;
    private CommonDialog dialog;
    private String token;
    private int isEnter = -1;

    @Override
    public int getLayoutId() {
        return R.layout.activity_notice;
    }

    @Override
    public void initView() {

        viewBinding.includeNotice.ivIcon.setVisibility(View.VISIBLE);
        viewBinding.includeNotice.ivBack.setVisibility(View.VISIBLE);
        viewBinding.includeNotice.tvToolbarTitle.setVisibility(View.GONE);
        presenter = new NoticePresenter(this);
    }

    @Override
    protected void initData() {
        albumId = getIntent().getStringExtra(Contents.AlbumId);
        title = getIntent().getStringExtra(Contents.Title);
        photographerId = SPUtils.getString(Contents.Id);
        requestExternalRW();
        viewBinding.tvEnter.setOnClickListener(this);
        viewBinding.includeNotice.ivBack.setOnClickListener(this);
    }

    @Override
    public void getError() {
        if (viewBinding != null) {
            isEnter = 2;
            viewBinding.tvEnter.setText("网络异常,请重试");
        }

    }
    public void requestExternalRW() {
        if (PermissionUtils.isGrantExternalRW(this, 1)) {
            presenter.getOriginalPic(pageIndex, albumId, photographerId);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //检验是否获取权限，如果获取权限，外部存储会处于开放状态，会弹出一个toast提示获得授权
                String sdCard = Environment.getExternalStorageState();
                if (sdCard.equals(Environment.MEDIA_MOUNTED)) {
                    presenter.getOriginalPic(pageIndex, albumId, photographerId);
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void getOriginalPic(OriginalPicBean bean) {
        if (bean != null) {

            ArrayList<OriginalPicBean.ResultBean> result = (ArrayList<OriginalPicBean.ResultBean>) bean.getResult();
            total = bean.getTotal();
            presenter.syncData(result);
        } else {
            //同步SDCard
            presenter.syncSD(albumId);
        }

    }

    @Override
    public void syncComplete() {
        pageIndex++;
        if (pageIndex * Contents.offsetNum < total) {

            presenter.getOriginalPic(pageIndex, albumId, photographerId);
        } else {
            presenter.getOriginalPic(-1, albumId, photographerId);
        }
    }

    @Override
    public void syncSDComplete() {
        if (viewBinding != null) {
            isEnter = 1;
            viewBinding.tvEnter.setText("开始拍摄");
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void syncNum(int syncNum) {
        if (viewBinding == null) {
            return;
        }
        viewBinding.tvEnter.setText("数据同步中(" + syncNum + "/" + total + ")...");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_enter:
                switch (isEnter) {
                    case 1:
                        Intent intent = new Intent(this, LiveActivity.class);
                        intent.putExtra(Contents.Title, title);
                        intent.putExtra(Contents.AlbumId, albumId);
                        intent.putExtra(Contents.Total, total);
                        intent.putExtra(Contents.QiNiuToken, token);
                        startActivity(intent);
                        finish();
                        break;
                    case 2:
                        pageIndex = 0;
                        AppDB.getInstance().picDao().delete();
                        presenter.getOriginalPic(pageIndex,albumId, photographerId);
                        break;
                    default:
                        MyToast.show("正在同步，请稍后");
                        break;
                }
                break;
            case R.id.iv_back:

                showDialog();
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showDialog() {
        dialog = new CommonDialog(this, "提示", "确定要退出直播界面?", 0);
        dialog.setOnConfirmListener(this);
        dialog.show();
    }

    @Override
    public void confirm(int type) {
        AppDB.getInstance().picDao().delete();
        dialog.dismiss();
        finish();
    }
}
