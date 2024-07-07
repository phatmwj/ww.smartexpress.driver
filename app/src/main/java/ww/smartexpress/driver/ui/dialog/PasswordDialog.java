package ww.smartexpress.driver.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;
import androidx.fragment.app.DialogFragment;

import lombok.Setter;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.databinding.DialogConfirmPasswordBinding;

public class PasswordDialog extends DialogFragment implements View.OnClickListener {

    @Setter
    private PasswordListener listener;
    public interface PasswordListener{
        void confirm(String password);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnPassConfirm:
                listener.confirm(password.get());
                break;
            default:
                break;
        }
    }

    public PasswordDialog(){}

    public void setIsVisibilityPassword(){
        isVisibility.set(!isVisibility.get());
    }
    public ObservableField<String> password = new ObservableField<>();
    public ObservableField<Boolean> isVisibility = new ObservableField<>(false);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DialogConfirmPasswordBinding binding = DialogConfirmPasswordBinding.inflate(inflater);
        isVisibility.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if(!isVisibility.get()){
                    binding.edtPw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }else {
                    binding.edtPw.setTransformationMethod(null);;
                }
            }
        });
        binding.setPassDialog(this);
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {

        super.onAttach(context);
        if (listener == null) {
            try {
                // Instantiate the mListener so we can send events to the host
                listener = (PasswordListener) context;
            } catch (ClassCastException e) {
                // The activity doesn't implement the interface, throw exception
                throw new ClassCastException(context.toString()
                        + " must implement PasswordListener");
            }
        }

    }


    public void confirmPassword(){
        listener.confirm(password.get());
    }
}
