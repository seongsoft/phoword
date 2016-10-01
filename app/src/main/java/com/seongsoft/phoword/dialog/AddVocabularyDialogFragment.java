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

import com.seongsoft.phoword.manager.DatabaseManager;
import com.seongsoft.phoword.R;
import com.seongsoft.phoword.component.Vocabulary;

/**
 * Created by BeINone on 2016-09-22.
 */

public class AddVocabularyDialogFragment extends DialogFragment {

    private Context mContext;
    private AlertDialog mDialog;
    private EditText mEditText;
    private OnVocabularyAddedListener mListener;

    public static AddVocabularyDialogFragment newInstance(OnVocabularyAddedListener listener) {
        AddVocabularyDialogFragment fragment = new AddVocabularyDialogFragment();
        fragment.mListener = listener;

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        mContext = getContext();

        /*   단어와 뜻을 입력할 수 있는 EditText를 포함하고 있는 xml 파일을 inflate   */
        View dialogView
                = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_vocabulary, null);

        /*   다이얼로그를 생성하고 타이틀을 지정   */
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.dialog_add_voca_title);

        /*   inflate된 EditText를 다이얼로그 안에 삽입   */
        builder.setView(dialogView);

        mEditText = (EditText) dialogView.findViewById(R.id.et_add_voca);

        /*   키보드 띄우기, text 모두 선택   */
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mEditText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                mEditText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
                mEditText.selectAll();
            }
        }, 200);

        DatabaseManager dbManager = new DatabaseManager(mContext);
        if (!dbManager.vocaExists("새 단어장")) {
            mEditText.setText("새 단어장");
        } else {
            int index = 2;
            while (dbManager.vocaExists("새 단어장 (" + index + ")")) {
                index++;
            }

            mEditText.setText("새 단어장 (" + index + ")");
        }

        /*   취소 버튼과 추가 버튼을 생성   */
        builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //   추가 버튼을 누르면 입력받은 폴더 이름을 전달
                Vocabulary vocabulary = new Vocabulary(getContext(), mEditText.getText().toString());
                mListener.onVocabularyAdded(vocabulary);
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
                mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                mDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            }
        });

        return mDialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        mEditText.addTextChangedListener(new TextWatcher() {
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

    public interface OnVocabularyAddedListener {
        void onVocabularyAdded(Vocabulary vocabulary);
    }

}
