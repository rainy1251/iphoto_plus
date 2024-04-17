package com.iphoto.plus.components.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.iphoto.plus.R;
import com.iphoto.plus.components.activity.LiveActivity;
import com.iphoto.plus.components.activity.NoticeActivity;
import com.iphoto.plus.adapter.ActivityListAdapter;
import com.iphoto.plus.base.BaseMvpFragment;
import com.iphoto.plus.bean.AlbumListBean;
import com.iphoto.plus.databinding.FragmentActivityTabBinding;
import com.iphoto.plus.event.LoginSuccessEvent;
import com.iphoto.plus.mvp.contract.AlbumTabContract;
import com.iphoto.plus.mvp.presenter.AlbumTabPresenter;
import com.iphoto.plus.util.Contents;
import com.iphoto.plus.util.MyToast;
import com.iphoto.plus.view.SpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class AlbumTabFragment extends BaseMvpFragment<AlbumTabPresenter, FragmentActivityTabBinding>
        implements AlbumTabContract.View {

    private List<AlbumListBean.DataBean.ListBean> dataList;
    private ActivityListAdapter mAdapter;
    private AlbumTabPresenter presenter;
    private int pageIndex = 1;
    private int mType;

    public AlbumTabFragment(int type) {
        this.mType = type;
    }

    public AlbumTabFragment() {

    }

    @Override
    protected void initView() {
        presenter = new AlbumTabPresenter(this);

        initRefreshLayout(viewBinding.srlView);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initData() {

        presenter.getList(pageIndex,mType);
        dataList = new ArrayList<>();
        mAdapter = new ActivityListAdapter(getActivity(), R.layout.item_list_tab, dataList);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        viewBinding.rvView.setLayoutManager(manager);
        viewBinding.rvView.setAdapter(mAdapter);
        viewBinding.rvView.addItemDecoration(new SpaceItemDecoration(0, 10));
        mAdapter.setEmptyView(R.layout.empty_layout);

        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(getActivity(), LiveActivity.class);
            intent.putExtra(Contents.Title, dataList.get(position).getName());
            intent.putExtra(Contents.AlbumId, dataList.get(position).getCode());
            startActivity(intent);

        });


    }

    @Override
    public void loadMore() {
        super.loadMore();
        pageIndex++;
        presenter.getList(pageIndex,mType);
    }

    @Override
    public void refresh() {
        super.refresh();
        pageIndex = 1;
        dataList.clear();
        presenter.getList(pageIndex,mType);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginEvent(LoginSuccessEvent event) {
        pageIndex = 1;
        dataList.clear();
        mAdapter.notifyDataSetChanged();
        if (event.isLogin()) {
            presenter.getList(pageIndex, mType);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_activity_tab;
    }

    @Override
    public void onSuccess(AlbumListBean bean) {
        if (bean.getCode() == 0) {
            List<AlbumListBean.DataBean.ListBean> result = bean.getData().getList();
            if ( result.size() < 10) {
                MyToast.show("没有更多了");
            }
            dataList.addAll(result);
            mAdapter.setDiffNewData(result);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}