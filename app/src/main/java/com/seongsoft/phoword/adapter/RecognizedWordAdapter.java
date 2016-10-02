package com.seongsoft.phoword.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.seongsoft.phoword.R;
import com.seongsoft.phoword.component.WordSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BeINone on 2016-09-27.
 */

public class RecognizedWordAdapter extends RecyclerView.Adapter<RecognizedWordAdapter.WordInVocaVH> {

    private Context mContext;
    private List<WordSet> mWordSets;
    private List<CheckBox> mCheckBoxes;

    private List<Boolean> mIsSelected;

    private int mCount;

    public RecognizedWordAdapter(Context context, ArrayList<WordSet> wordSets) {
        mContext = context;
        mWordSets = wordSets;
        mCheckBoxes = new ArrayList<>();

        mIsSelected = new ArrayList<>();
    }

    @Override
    public WordInVocaVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_word_in_voca, parent, false);

        return new WordInVocaVH(view);
    }

    @Override
    public void onBindViewHolder(final WordInVocaVH holder, int position) {
        final WordSet wordSet = mWordSets.get(position);
        final CheckBox checkBox = holder.mCheckBox;

        if (mCheckBoxes.size() <= position) mCheckBoxes.add(position, checkBox);
        if (mIsSelected.size() <= position) mIsSelected.add(position, false);

        holder.mWordTV.setText(mWordSets.get(position).getWord());
        if (wordSet.getMeaning().size() > 1) {
            holder.mMeaningTV.setText(
                    wordSet.getMeaning().get(0) + ", " + wordSet.getMeaning().get(1));
        } else if (wordSet.getMeaning().size() == 1) {
            holder.mMeaningTV.setText(
                    wordSet.getMeaning().get(0));
        }

        holder.mClickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsSelected.get(holder.getAdapterPosition()))
                    mCount--;
                else
                    mCount++;

                checkBox.setChecked(!checkBox.isChecked());
                mIsSelected.set(holder.getAdapterPosition(), checkBox.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWordSets.size();
    }

    public ArrayList<WordSet> getSelectedWordSets() {
        List<WordSet> selectedWordSets = new ArrayList<>();
        for (int index = 0; index < mIsSelected.size(); index++) {
            if (mIsSelected.get(index)) selectedWordSets.add(mWordSets.get(index));
        }

        return (ArrayList<WordSet>) selectedWordSets;
    }

    public void selectAll() {
        for (CheckBox checkBox : mCheckBoxes) {
            checkBox.setChecked(true);
        }
        for (int index = 0; index < mIsSelected.size(); index++) {
            mIsSelected.set(index, true);
        }
        mCount = mWordSets.size();
    }

    public void deselectAll() {
        for (CheckBox checkBox : mCheckBoxes) {
            checkBox.setChecked(false);
        }
        for (int index = 0; index < mIsSelected.size(); index++) {
            mIsSelected.set(index, false);
        }
        mCount = 0;
    }

    public int getNumSelectedWords() {
        return mCount;
    }

    public class WordInVocaVH extends RecyclerView.ViewHolder {

        private CheckBox mCheckBox;
        private TextView mWordTV;
        private TextView mMeaningTV;
        private View mClickLayout;

        public WordInVocaVH(View itemView) {
            super(itemView);

            mCheckBox = (CheckBox) itemView.findViewById(R.id.cb_word);
            mWordTV = (TextView) itemView.findViewById(R.id.tv_word);
            mMeaningTV = (TextView) itemView.findViewById(R.id.tv_meaning);
            mClickLayout = itemView.findViewById(R.id.layout_click);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mWordTV.getText() + "'";
        }

    }

}
