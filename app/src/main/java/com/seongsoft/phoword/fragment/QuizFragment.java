package com.seongsoft.phoword.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seongsoft.phoword.R;
import com.seongsoft.phoword.activity.MainActivity;
import com.seongsoft.phoword.adapter.QuizAdapter;
import com.seongsoft.phoword.component.Vocabulary;
import com.seongsoft.phoword.dialog.QuizNumWordsDialogFragment;
import com.seongsoft.phoword.dialog.QuizTypesDialogFragment;
import com.seongsoft.phoword.manager.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BeINone on 2016-09-27.
 */

public class QuizFragment extends Fragment {

    private static final String TAG_NUM_WORDS = "num_words";
    private static final String TAG_QUIZ_TYPE = "quiz_type";

    private DatabaseManager mDBManager;
    private QuizAdapter mAdapter;

    private static final String[] mQuizTypeItems = {"뜻", "단어", "발음", "예문"};

    private Vocabulary mVocabulary;
    private int mNumWords;

    public int mNumWordCheckedPosition;
    public boolean[] mQuizTypeCheckedItems = {true, false, false, false};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_quiz, container, false);

        ((MainActivity) getActivity()).hideSearchFAB();
        ((MainActivity) getActivity()).showStartQuizFAB();

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview_quiz);
        recyclerView.setHasFixedSize(true);

        mDBManager = new DatabaseManager(getContext());

        List<Vocabulary> vocabularies = new ArrayList<>();

        Vocabulary allWordsVoca = new Vocabulary(getString(R.string.all_words));
        vocabularies.add(allWordsVoca);

        Vocabulary favoritesVoca = new Vocabulary(getString(R.string.favorites));
        if (mDBManager.selectFavoriteWords().size() > 0) vocabularies.add(favoritesVoca);

        for (Vocabulary vocabulary : mDBManager.selectAllVocas()) {
            if (!mDBManager.isEmptyVoca(vocabulary)) vocabularies.add(vocabulary);
        }

        final TextView numWordSubTV = (TextView) v.findViewById(R.id.tv_num_words_sub);

        mAdapter = new QuizAdapter(getContext(), (ArrayList<Vocabulary>) vocabularies,
                new QuizAdapter.OnQuizVocaClickListener() {
                    @Override
                    public void onQuizVocaClick(Vocabulary vocabulary) {
                        mVocabulary = vocabulary;

                        if (vocabulary.getName().equals(getString(R.string.all_words))) {
                            mNumWords = mDBManager.selectAllWords().size();
                        } else if (vocabulary.getName().equals(getString(R.string.favorites))) {
                            mNumWords = mDBManager.selectFavoriteWords().size();
                        } else {
                            mNumWords = mDBManager.selectWordsInVoca(vocabulary).size();
                        }
                        numWordSubTV.setText(String.valueOf(mNumWords));
                        mNumWordCheckedPosition = mNumWords - 1;
                    }
                });
        recyclerView.setAdapter(mAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 단어수
        View numWordClickLayout = v.findViewById(R.id.layout_click_num_word);
        numWordClickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuizNumWordsDialogFragment.newInstance(mNumWords, mVocabulary,
                        new QuizNumWordsDialogFragment.QuizNumWordsDialogListener() {
                            @Override
                            public void onConfirm(int numWords) {
                                mNumWords = numWords;
                                numWordSubTV.setText(String.valueOf(numWords));
                            }
                        })
                        .show(getChildFragmentManager(), TAG_NUM_WORDS);
            }
        });

        final TextView quizTypeSubTV = (TextView) v.findViewById(R.id.tv_quiz_type_sub);

        // 퀴즈유형
        View quizTypeClickLayout = v.findViewById(R.id.layout_quiz_type_click);
        quizTypeClickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuizTypesDialogFragment.newInstance(mQuizTypeItems, mQuizTypeCheckedItems,
                        new QuizTypesDialogFragment.QuizTypesDialogListener() {
                            @Override
                            public void onConfirm(boolean[] checkedItems) {
                                mQuizTypeCheckedItems = checkedItems;
                                quizTypeSubTV.setText(null);

                                for (int index = 0; index < mQuizTypeItems.length; index++) {
                                    if (mQuizTypeCheckedItems[index]) {
                                        if (quizTypeSubTV.getText().toString().equals("")) {
                                            quizTypeSubTV.setText(mQuizTypeItems[index]);
                                        } else {
                                            quizTypeSubTV.append(", " + mQuizTypeItems[index]);
                                        }
                                    }
                                }
                            }
                        })
                        .show(getChildFragmentManager(), TAG_QUIZ_TYPE);
            }
        });

        for (int index = 0; index < mQuizTypeItems.length; index++) {
            if (mQuizTypeCheckedItems[index]) {
                if (quizTypeSubTV.getText() == "") {
                    quizTypeSubTV.setText(mQuizTypeItems[index]);
                } else {
                    quizTypeSubTV.append(", " + mQuizTypeItems[index]);
                }
            }
        }

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ((MainActivity) getActivity()).hideStartQuizFAB();
        ((MainActivity) getActivity()).showSearchFAB();
    }

    public Vocabulary getSelectedVocabulary() {
        return mVocabulary;
    }

    public int getNumWords() {
        return mNumWords;
    }

    public ArrayList<String> getQuizTypes() {
        List<String> quizTypes = new ArrayList<>();

        for (int index = 0; index < mQuizTypeItems.length; index++) {
            if (mQuizTypeCheckedItems[index]) quizTypes.add(mQuizTypeItems[index]);
        }

        return (ArrayList<String>) quizTypes;
    }

}
