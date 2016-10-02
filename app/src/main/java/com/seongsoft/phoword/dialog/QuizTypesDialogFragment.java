package com.seongsoft.phoword.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
                    }
                })
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();

        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkEmpty() == 0) {
                        Toast.makeText(getContext(), "1개 이상 선택해주세요.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        mListener.onConfirm(mCheckedItems);
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    private int checkEmpty() {
        int count = 0;
        for (int index = 0; index < mCheckedItems.length; index++) {
            if (mCheckedItems[index]) count++;
        }
        return count;
    }

    public interface QuizTypesDialogListener {
        void onConfirm(boolean[] checkedItems);
    }

}
