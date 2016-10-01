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

public class QuizTypesDialogFragment extends DialogFragment {

    private CharSequence[] mItems;
    private boolean[] mCheckedItems;
    private QuizTypesDialogListener mListener;

    public static QuizTypesDialogFragment newInstance(
            CharSequence[] items, boolean[] checkedItems, QuizTypesDialogListener listener) {
        QuizTypesDialogFragment fragment = new QuizTypesDialogFragment();
        fragment.mItems = items;
        fragment.mCheckedItems = checkedItems;
        fragment.mListener = listener;

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.quiz_type)
                .setMultiChoiceItems(mItems, mCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        mCheckedItems[which] = isChecked;
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onConfirm(mCheckedItems);
                        dialog.dismiss();
                    }
                })
                .create();
    }

    public interface QuizTypesDialogListener {
        void onConfirm(boolean[] checkedItems);
    }

}
