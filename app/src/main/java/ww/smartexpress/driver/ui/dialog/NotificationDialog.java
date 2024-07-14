package ww.smartexpress.driver.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.fragment.app.DialogFragment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.databinding.DialogConfirmPasswordBinding;
import ww.smartexpress.driver.databinding.DialogNotificationBinding;

@NoArgsConstructor
@AllArgsConstructor
public class NotificationDialog extends DialogFragment implements View.OnClickListener {

    @Getter
    @Setter
    private String image;
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private String msg;
    private NotificationListener listener;
    public interface NotificationListener{
        void onCLick();
    }

    @Override
    public void onClick(View view) {
        listener.onCLick();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DialogNotificationBinding binding = DialogNotificationBinding.inflate(inflater);
        binding.setNoti(this);
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.getWindow().setLayout(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.setCanceledOnTouchOutside(true);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setBackgroundDrawable(new ColorDrawable(0x00000000));
        layoutParams.gravity = Gravity.TOP;
        layoutParams.dimAmount = 0.0f;
        window.setAttributes(layoutParams);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.TOP);
        dialog.setCanceledOnTouchOutside(false);
//        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

//        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
//        window.addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        dialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 10000); // Thời gian tính bằng mili giây (ở đây là 5 giây)
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {

        super.onAttach(context);
        if (listener == null) {
            try {
                // Instantiate the mListener so we can send events to the host
                listener = (NotificationListener) context;
            } catch (ClassCastException e) {
                // The activity doesn't implement the interface, throw exception
                throw new ClassCastException(context.toString()
                        + " must implement NotificationListener");
            }
        }

    }
}
