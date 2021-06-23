package cn.linked.baselib.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.linked.baselib.R;

public class MessageDialog extends Dialog {

    private int activeButtonNum = 0;

    private TextView titleTextView;
    private TextView messageTextView;
    private TextView activeButton1;
    private TextView activeButton2;
    private TextView activeButton3;

    public MessageDialog(@NonNull Context context) {
        super(context);
        init();
    }
    public MessageDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }
    protected MessageDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        setContentView(R.layout.layout_message_dialog);
        titleTextView = findViewById(R.id.titleText);
        messageTextView = findViewById(R.id.messageText);
        activeButton1 = findViewById(R.id.activeButton1);
        activeButton2 = findViewById(R.id.activeButton2);
        activeButton3 = findViewById(R.id.activeButton3);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.dimAmount = 0.1f;
        getWindow().setAttributes(params);
        getWindow().setWindowAnimations(R.style.MessageDialogInOutAnim);
    }

    @Override
    public void cancel() {
        super.cancel();
    }

    public MessageDialog setTitle(@NonNull String title) {
        titleTextView.setText(title);
        return this;
    }

    public MessageDialog setMessage(@NonNull String message) {
        messageTextView.setText(message);
        return this;
    }

    public MessageDialog setMessageTextCenter(boolean textCenter) {
        if(textCenter) {
            messageTextView.setGravity(Gravity.CENTER);
        }else {
            messageTextView.setGravity(Gravity.CENTER_VERTICAL);
        }
        return this;
    }

    public MessageDialog setButton(@NonNull String text, @NonNull View.OnClickListener listener) {
        if(activeButtonNum < 3) {
            if(activeButtonNum == 0) {
                setCanceledOnTouchOutside(false);
                setCancelable(false);
            }
            TextView button = null;
            if(activeButtonNum == 0) {
                button = activeButton1;
            }else if(activeButtonNum == 1) {
                button = activeButton2;
            }else {
                button = activeButton3;
            }
            button.setText(text);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(listener);
            activeButtonNum++;
        }
        return this;
    }

}
