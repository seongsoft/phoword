package com.seongsoft.phoword.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.seongsoft.phoword.dialog.AddVocabularyDialogFragment;
import com.seongsoft.phoword.R;

/**
 * Created by BeINone on 2016-09-26.
 */

public class VocabularyFooterVH extends RecyclerView.ViewHolder {

    public ImageView mImageIV;

    public VocabularyFooterVH(View itemView,
                              final AddVocabularyDialogFragment.OnVocabularyAddedListener listener) {
        super(itemView);

        mImageIV = (ImageView) itemView.findViewById(R.id.iv_add_vocabulary);

        itemView.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddVocabularyDialogFragment.newInstance(listener);
            }
        });
    }

}
