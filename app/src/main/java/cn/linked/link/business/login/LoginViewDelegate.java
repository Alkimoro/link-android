package cn.linked.link.business.login;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;

import cn.linked.baselib.LinkApplication;
import cn.linked.baselib.UIContext;
import cn.linked.baselib.entity.User;
import cn.linked.baselib.ui.BaseViewDelegate;
import cn.linked.baselib.ui.dialog.MessageDialog;
import cn.linked.link.R;
import cn.linked.router.api.Router;

public class LoginViewDelegate extends BaseViewDelegate {

    private LoginViewModel viewModel;

    private ViewGroup rootView;

    private EditText userIdEditText;
    private EditText passwordEditText;

    private View loginButtonView;
    private boolean loginButtonClickable = true;

    public LoginViewDelegate(@NonNull UIContext uiContext, ViewGroup parent, LoginViewModel viewModel) {
        super(uiContext);
        this.viewModel = viewModel;
        rootView = (ViewGroup) inflate(R.layout.layout_login, parent, false);
        userIdEditText = rootView.findViewById(R.id.user_id_edit_text);
        passwordEditText = rootView.findViewById(R.id.user_password_edit_text);
        loginButtonView = rootView.findViewById(R.id.login_button);
    }

    @Override
    public void init() {
        loginButtonView.setOnClickListener(view -> {
            if(loginButtonClickable) {
                loginButtonClickable = false;
                try {
                    this.viewModel.login(Long.parseLong(userIdEditText.getText().toString()), passwordEditText.getText().toString());
                }catch (Exception e) {
                    e.printStackTrace();
                    MessageDialog dialog = new MessageDialog(getUiContext().getContext());
                    dialog.setMessage("输入错误，请检查数据格式。").setButton("确定", v -> {
                        dialog.cancel();
                    }).show();
                    loginButtonClickable = true;
                }
            }
        });

        this.addAndObserve(viewModel.getLoginStateLiveData(), value -> {
            if(value instanceof User) {
                Intent intent=new Intent(LinkApplication.getInstance(), Router.route("app/mainActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getUiContext().getContext().startActivity(intent);
                if(getUiContext().getContext() instanceof Activity) {
                    ((Activity) getUiContext().getContext()).finish();
                }
                loginButtonClickable = false;
            }else {
                MessageDialog dialog = new MessageDialog(getUiContext().getContext());
                dialog.setMessage(value.toString()).setButton("确定", v -> {
                    dialog.cancel();
                }).show();
                loginButtonClickable = true;
            }
        });
    }

    @Override
    public void destroy() {
        rootView = null;
        userIdEditText = null;
        passwordEditText = null;
        loginButtonView = null;
    }

    @Override
    public ViewGroup getRootView() {
        return rootView;
    }

}
