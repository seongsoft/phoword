package com.seongsoft.phoword.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.seongsoft.phoword.R;

/**
 * Created by BeINone on 2016-09-28.
 */

public class RemoveVocaDialogFragment extends DialogFragment {

    private int mNumVocas;
    private RemoveVocaDialogListener mListener;

    public static RemoveVocaDialogFragment newInstance(int numVocas,
                                                       RemoveVocaDialogListener listener) {
        RemoveVocaDialogFragment fragment = new RemoveVocaDialogFragment();
        fragment.mNumVocas = numVocas;
        fragment.mListener = listener;

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_remove_title)
                .setMessage(mNumVocas + "개의 단어장이 삭제됩니다.")
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onConfirm();
                        dialog.dismiss();
                    }
                })
                .create();
    }

    public interface RemoveVocaDialogListener {
        void onConfirm();
    }

}
