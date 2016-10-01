package com.seongsoft.phoword.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.seongsoft.phoword.R;

/**
 * Created by BeINone on 2016-09-26.
 */

public class VocabularyItemVH extends RecyclerView.ViewHolder {

    public ImageView mImageIV;
    public TextView mNameTV;
    public CheckBox mCheckBox;

    public VocabularyItemVH(View itemView) {
        super(itemView);

        mImageIV = (ImageView) itemView.findViewById(R.id.iv_vocabulary);
        mNameTV = (TextView) itemView.findViewById(R.id.tv_vocabulary);
        mCheckBox = (CheckBox) itemView.findViewById(R.id.cb_vocabulary);
    }

}
