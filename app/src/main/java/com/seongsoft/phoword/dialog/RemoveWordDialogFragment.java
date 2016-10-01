package com.seongsoft.phoword.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.seongsoft.phoword.listener.MyOnActionItemFinishListener;
import com.seongsoft.phoword.R;
import com.seongsoft.phoword.adapter.WordAdapter;

public class RemoveWordDialogFragment extends DialogFragment {

    private Context mContext;
    private int mWordCount;
    private MyOnActionItemFinishListener mListener;

    public static RemoveWordDialogFragment newInstance(int wordCount, MyOnActionItemFinishListener listener) {
        RemoveWordDialogFragment fragment = new RemoveWordDialogFragment();
        fragment.mWordCount = wordCount;
        fragment.mListener = listener;

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        mContext = getContext();

        /*   다이얼로그를 생성하고 타이틀을 지정   */
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.dialog_remove_title);

        builder.setMessage(mWordCount + "개의 단어가 삭제됩니다.");

        /*   삭제 버튼과 취소 버튼을 생성   */
        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //   삭제 버튼을 누르면 체크된 단어들을 삭제
                mListener.onActionItemFinish(WordAdapter.REQUEST_REMOVE, MyOnActionItemFinishListener.RESULT_OK);
            }
        })

                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //   취소 버튼을 누르면 다이얼로그 창을 종료
                        mListener.onActionItemFinish(WordAdapter.REQUEST_REMOVE, MyOnActionItemFinishListener.RESULT_CANCELED);
                        dismiss();
                    }
                });

        final AlertDialog dialog = builder.create();

        /*   버튼 텍스트의 색을 변경   */
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            }
        });

        return dialog;
    }

}
