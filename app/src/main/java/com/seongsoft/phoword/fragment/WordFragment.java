package com.seongsoft.phoword.fragment;

import android.content.Context;
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
import com.seongsoft.phoword.component.WordSet;
import com.seongsoft.phoword.decoration.DividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by BeINone on 2016-09-22.
 */

public class WordFragment extends Fragment {

    private static final String ARGS_KEY = "wordsets";

    private WordAdapter mAdapter;

    public static WordFragment newInstance(Context context, ArrayList<WordSet> wordSets) {
        WordFragment fragment = new WordFragment();
        fragment.mAdapter = new WordAdapter(context, wordSets, WordAdapter.TYPE_WORD);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_word, container, false);

        MyRecyclerView recyclerView = (MyRecyclerView) v.findViewById(R.id.recyclerview_word);
        recyclerView.setHasFixedSize(true);

        View emptyView = v.findViewById(R.id.view_empty_word);
        recyclerView.setEmptyView(emptyView);

        recyclerView.setAdapter(mAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));

        return v;
    }

    public WordAdapter getAdapter() {
        return mAdapter;
    }

}
