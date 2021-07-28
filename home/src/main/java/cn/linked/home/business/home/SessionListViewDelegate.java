package cn.linked.home.business.home;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cn.linked.baselib.BaseFragment;
import cn.linked.baselib.UIContext;
import cn.linked.baselib.ui.BaseViewDelegate;
import cn.linked.home.R;

public class SessionListViewDelegate extends BaseViewDelegate {

    private ViewGroup rootView;
    private SessionListViewModel model;

    private RecyclerView sessionList;
    private ChatSessionRecyclerAdapter sessionListAdapter;

    private ViewGroup homeInfoBarButton;
    private HomeViewDelegate parentViewDelegate;

    public SessionListViewDelegate(@NonNull UIContext uiContext, ViewGroup parent, SessionListViewModel model) {
        super(uiContext);
        this.model = model;
        rootView = (ViewGroup) uiContext.getLayoutInflater().inflate(R.layout.layout_home_session_list, parent, false);
        homeInfoBarButton = rootView.findViewById(R.id.homeInfoBar);
        sessionList = rootView.findViewById(R.id.sessionList);
        sessionListAdapter = new ChatSessionRecyclerAdapter(uiContext.getContext(), model.getChatSessionItemList());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getUiContext().getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        sessionList.setLayoutManager(linearLayoutManager);
        sessionList.setAdapter(sessionListAdapter);
        parentViewDelegate = ((HomeFragment) ((BaseFragment) getUiContext()).getParentFragment()).getHomeViewDelegate();
        parentViewDelegate.addChildViewDelegate(this);
    }

    @Override
    public void init() {
        addAndObserve(model.getAdapterHandler(), h -> {
            h.handle(sessionListAdapter);
        });
        homeInfoBarButton.setOnClickListener(view -> {
            parentViewDelegate.getViewPager().setCurrentItem(1, true);
        });
    }

    @Override
    public void destroy() {
        rootView = null;
        model = null;
        sessionList = null;
        sessionListAdapter = null;
        parentViewDelegate = null;
    }

    @Override
    public ViewGroup getRootView() {
        return rootView;
    }

}
