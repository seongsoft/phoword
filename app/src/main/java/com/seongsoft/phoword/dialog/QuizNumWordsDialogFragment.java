package com.seongsoft.phoword.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.seongsoft.phoword.R;

/**
 * Created by BeINone on 2016-09-27.
 */

public class QuizNumWordsDialogFragment extends DialogFragment {

    private int mNumWords;
    private QuizNumWordsDialogListener mListener;

    public static QuizNumWordsDialogFragment newInstance(int numWords,
                                                         QuizNumWordsDialogListener listener) {
        QuizNumWordsDialogFragment fragment = new QuizNumWordsDialogFragment();
        fragment.mNumWords = numWords;
        fragment.mListener = listener;

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_quiz_num_words, null);
//        final EditText editText = (EditText) v.findViewById(R.id.et_num_words);
        final EditText editText = new EditText(getContext());
        editText.setText(String.valueOf(mNumWords));

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.num_words)
                .setView(editText)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int numWords = Integer.valueOf(editText.getText().toString());
                        if (numWords > mNumWords) {
                            Toast.makeText(getContext(), mNumWords + "개 이하로 입력해주세요.",
                                    Toast.LENGTH_SHORT).show();
                        } else if (numWords < 1) {
                            Toast.makeText(getContext(), "단어수는 1개 이상이어야 합니다.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            mListener.onConfirm(numWords);
                            dialog.dismiss();
                        }
                    }
                })
                .create();
    }

    public interface QuizNumWordsDialogListener {
        void onConfirm(int numWords);
    }

}
