package com.seongsoft.phoword.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.seongsoft.phoword.R;
import com.seongsoft.phoword.component.Vocabulary;
import com.seongsoft.phoword.manager.DatabaseManager;

/**
 * Created by BeINone on 2016-09-27.
 */

public class QuizNumWordsDialogFragment extends DialogFragment {

    private int mNumWords;
    private Vocabulary mVocabulary;
    private QuizNumWordsDialogListener mListener;

    private AlertDialog mDialog;
    private EditText mEditText;

    private int mLimitNumWords;

    public static QuizNumWordsDialogFragment newInstance(int numWords, Vocabulary vocabulary,
                                                         QuizNumWordsDialogListener listener) {
        QuizNumWordsDialogFragment fragment = new QuizNumWordsDialogFragment();
        fragment.mNumWords = numWords;
        fragment.mVocabulary = vocabulary;
        fragment.mListener = listener;

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_quiz_num_words, null);

        mEditText = (EditText) dialogView.findViewById(R.id.et_num_words);
        mEditText.setText(String.valueOf(mNumWords));
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    mDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                    return true;
                }

                return false;
            }
        });
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) hideKeyboard(v);
            }
        });

        mDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.num_words)
                .setView(dialogView)
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

        return mDialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        DatabaseManager dbManager = new DatabaseManager(getContext());

        if (mVocabulary.getName().equals(getString(R.string.all_words))) {
            mLimitNumWords = dbManager.selectAllWords().size();
        } else if (mVocabulary.getName().equals(getString(R.string.favorites))) {
            mLimitNumWords = dbManager.selectFavoriteWords().size();
        } else {
            mLimitNumWords = dbManager.selectWordsInVoca(mVocabulary).size();
        }

        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int numWords = Integer.valueOf(mEditText.getText().toString());
                    if (numWords > mNumWords) {
                        Toast.makeText(getContext(), mLimitNumWords + "개 이하로 입력해주세요.",
                                Toast.LENGTH_SHORT).show();
                    } else if (numWords < 1) {
                        Toast.makeText(getContext(), "단어수는 1개 이상이어야 합니다.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        mListener.onConfirm(numWords);
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    public interface QuizNumWordsDialogListener {
        void onConfirm(int numWords);
    }

    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
