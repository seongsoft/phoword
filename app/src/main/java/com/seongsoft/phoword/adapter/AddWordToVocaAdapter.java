package com.seongsoft.phoword.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seongsoft.phoword.R;
import com.seongsoft.phoword.component.Vocabulary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BeINone on 2016-09-26.
 */

public class AddWordToVocaAdapter extends RecyclerView.Adapter<VocabularyItemVH> {

    private Context mContext;
    private MyOnWordAddedToVocaListener mListener;

    private List<Vocabulary> mVocabularies;

    public AddWordToVocaAdapter(Context context, ArrayList<Vocabulary> vocabularies,
                                MyOnWordAddedToVocaListener listener) {
        mContext = context;
        mListener = listener;
        mVocabularies = vocabularies;
    }

    @Override
    public VocabularyItemVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_vocabulary, parent, false);

        return new VocabularyItemVH(view);
    }

    @Override
    public void onBindViewHolder(VocabularyItemVH holder, int position) {
        final Vocabulary vocabulary = mVocabularies.get(position);

        holder.mNameTV.setText(vocabulary.getName());
        holder.mImageIV.setBackgroundColor(vocabulary.getColor());
        holder.mImageIV.setImageResource(R.drawable.ic_vocabulary_big);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onWordAddedToVoca(vocabulary);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVocabularies.size();
    }

    public interface MyOnWordAddedToVocaListener {
        void onWordAddedToVoca(Vocabulary vocabulary);
    }

}