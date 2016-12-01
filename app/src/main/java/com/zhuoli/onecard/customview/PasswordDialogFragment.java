package com.zhuoli.onecard.customview;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.zhuoli.onecard.R;

public class PasswordDialogFragment extends android.support.v4.app.DialogFragment {
	private EditText mPassword;

	private PasswordDialogListener mListener;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getActivity() instanceof PasswordDialogListener) {
			mListener = (PasswordDialogListener) getActivity();
		} else {
			throw new RuntimeException("宿主类型错误");
		}
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_setting_other, null);
		mPassword = (EditText) view.findViewById(R.id.id_txt_password);
		builder.setView(view)
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								mListener.onPositiveButtonClick(mPassword.getText().toString());
							}
						})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mListener.onNegativeButtonClick();
					}
				});
		return builder.create();
	}

	public interface PasswordDialogListener {
		void onPositiveButtonClick(String password);

		void onNegativeButtonClick();
	}
}  