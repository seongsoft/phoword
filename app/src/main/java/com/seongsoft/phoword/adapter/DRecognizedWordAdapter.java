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

public class DRecognizedWordAdapter extends RecyclerView.Adapter<DRecognizedWordAdapter.ViewHolder> {

    private Context mContext;
    private List<WordSet> mWordSets;

    private List<CheckBox> mCheckBoxes;

    private int mCount;

    public DRecognizedWordAdapter(Context context, List<WordSet> wordSets) {
        mContext = context;
        mWordSets = wordSets;
        mCheckBoxes = new ArrayList<>();
    }

    @Override
    public DRecognizedWordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recognized_word, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final WordSet wordSet = mWordSets.get(position);
        final View clickLayout = holder.mClickLayout;
        final CheckBox checkBox = holder.mCheckBox;

        mCheckBoxes.add(checkBox);

        holder.mWordTV.setText(wordSet.getWord());
        if (wordSet.getMeaning().size() > 1) {
            holder.mMeaningTV.setText(
                    wordSet.getMeaning().get(0) + ", " + wordSet.getMeaning().get(1));
        } else if (wordSet.getMeaning().size() == 1) {
            holder.mMeaningTV.setText(
                    wordSet.getMeaning().get(0));
        }

        clickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wordSet.isSelected())
                    mCount--;
                else
                    mCount++;

                checkBox.setChecked(!checkBox.isChecked());
                wordSet.setSelected(checkBox.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWordSets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox mCheckBox;
        private TextView mWordTV;
        private TextView mMeaningTV;
        private View mClickLayout;

        public ViewHolder(View itemView) {
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

    public void selectAll() {
        for (CheckBox checkBox : mCheckBoxes) checkBox.setChecked(true);
        for (WordSet wordSet : mWordSets) wordSet.setSelected(true);
    }

    public void deselectAll() {
        for (CheckBox checkBox : mCheckBoxes) checkBox.setChecked(false);
        for (WordSet wordSet : mWordSets) wordSet.setSelected(false);
    }

    public List<WordSet> getSelectedWordSets() {
        List<WordSet> wordSets = new ArrayList<>();
        for (WordSet wordSet : mWordSets) {
            if (wordSet.isSelected())
                wordSets.add(wordSet);
        }

        return wordSets;
    }

}
