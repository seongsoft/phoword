package com.seongsoft.phoword.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seongsoft.phoword.R;
import com.seongsoft.phoword.component.Vocabulary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BeINone on 2016-09-27.
 */

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {

    private Context mContext;
    private List<Vocabulary> mVocabularies;
    private OnQuizVocaClickListener mListener;

    private CardView mPrevClickedCardView;

    private boolean isStart = true;

    public QuizAdapter(Context context, ArrayList<Vocabulary> vocabularies,
                       OnQuizVocaClickListener listener) {
        mContext = context;
        mVocabularies = vocabularies;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_quiz, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Vocabulary vocabulary = mVocabularies.get(position);
        final CardView cardView = holder.mCardView;

        holder.mTextView.setText(vocabulary.getName());

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPrevClickedCardView != null) {
                    mPrevClickedCardView.setCardBackgroundColor(Color.WHITE);
                }
                cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

                if (mPrevClickedCardView != cardView) {
                    mListener.onQuizVocaClick(vocabulary);
                    mPrevClickedCardView = cardView;
                }
            }
        });

        if (isStart) {
            if (mVocabularies.get(position).getName()
                    .equals(mContext.getString(R.string.all_words))) {
                cardView.performClick();
                isStart = false;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mVocabularies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView mCardView;
        private TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            mCardView = (CardView) itemView.findViewById(R.id.cv_quiz);
            mTextView = (TextView) itemView.findViewById(R.id.tv_quiz);
        }

    }

    public interface OnQuizVocaClickListener {
        void onQuizVocaClick(Vocabulary vocabulary);
    }

}
