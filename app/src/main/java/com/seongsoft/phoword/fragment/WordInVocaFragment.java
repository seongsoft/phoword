package com.seongsoft.phoword.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seongsoft.phoword.R;
import com.seongsoft.phoword.adapter.WordAdapter;
import com.seongsoft.phoword.component.MyRecyclerView;
import com.seongsoft.phoword.component.Vocabulary;
import com.seongsoft.phoword.component.WordSet;
import com.seongsoft.phoword.decoration.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BeINone on 2016-09-26.
 */

public class WordInVocaFragment extends Fragment {

    private static final String ARGS_WORDS = "wordsets";
    private static final String ARGS_VOCA = "voca";

    public static WordInVocaFragment newInstance(Vocabulary vocabulary,
                                                 ArrayList<WordSet> wordSets) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARGS_WORDS, wordSets);
        args.putParcelable(ARGS_VOCA, vocabulary);

        WordInVocaFragment fragment = new WordInVocaFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_word_in_voca, container, false);

        List<WordSet> wordSets = getArguments().getParcelableArrayList(ARGS_WORDS);
        Vocabulary vocabulary = getArguments().getParcelable(ARGS_VOCA);

        MyRecyclerView recyclerView = (MyRecyclerView) v.findViewById(R.id.recyclerview_word_in_voca);
        recyclerView.setHasFixedSize(true);

        View emptyView = v.findViewById(R.id.view_empty_word);
        recyclerView.setEmptyView(emptyView);

        WordAdapter adapter = new WordAdapter(getContext(), (ArrayList<WordSet>) wordSets,
                vocabulary, WordAdapter.TYPE_WORD_IN_VOCA);
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));

        return v;
    }

}
