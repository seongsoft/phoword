package com.seongsoft.phoword.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.seongsoft.phoword.R;
import com.seongsoft.phoword.component.WordSet;

public class AddWordDialogFragment extends DialogFragment {

    private Context mContext;
    private AlertDialog mDialog;
    private EditText mWordInput;
    private EditText mMeaningInput;
    private MyOnWordAddedListener mListener;

    public static AddWordDialogFragment newInstance(MyOnWordAddedListener listener) {
        AddWordDialogFragment fragment = new AddWordDialogFragment();
        fragment.mListener = listener;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        mContext = getContext();

        /*   단어와 뜻을 입력할 수 있는 EditText를 포함하고 있는 xml 파일을 inflate   */
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_word, null);

        /*   다이얼로그를 생성하고 타이틀을 지정   */
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.dialog_add_word_title);

        /*   inflate된 EditText를 다이얼로그 안에 삽입   */
        builder.setView(dialogView);

        mWordInput = (EditText) dialogView.findViewById(R.id.edit_word);
        mMeaningInput = (EditText) dialogView.findViewById(R.id.edit_meaning);

        /*   키보드 띄우기   */
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mWordInput.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                mWordInput.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
                mWordInput.requestFocus();
            }
        }, 200);

        /*   취소 버튼과 추가 버튼을 생성   */
        builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //   추가 버튼을 누르면 입력받은 단어와 뜻을 전달

//                WordSet wordSet = new WordSet(mWordInput.getText().toString(), mMeaningInput.getText().toString(), System.currentTimeMillis());
//                mListener.onWordAdded(wordSet);
            }
        })

                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //   취소 버튼을 누르면 다이얼로그 창을 종료
                        dismiss();
                    }
                });

        mDialog = builder.create();

        /*   버튼 텍스트의 색을 변경   */
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(mContext, R.color.colorDisabledLight));
                mDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            }
        });

        return mDialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        mWordInput.addTextChangedListener(new TextWatcher() {

            Button positiveButton = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    positiveButton.setEnabled(false);
                    positiveButton.setTextColor(ContextCompat.getColor(mContext, R.color.colorDisabledLight));
                }
                else {
                    positiveButton.setEnabled(true);
                    positiveButton.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public interface MyOnWordAddedListener {
        void onWordAdded(WordSet wordSet);
    }

}
