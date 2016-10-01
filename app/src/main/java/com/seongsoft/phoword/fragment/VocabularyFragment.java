package com.seongsoft.phoword.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seongsoft.phoword.activity.MainActivity;
import com.seongsoft.phoword.manager.DatabaseManager;
import com.seongsoft.phoword.decoration.GridSpacingItemDecoration;
import com.seongsoft.phoword.R;
import com.seongsoft.phoword.component.Vocabulary;
import com.seongsoft.phoword.adapter.VocabularyAdapter;

import java.util.List;

/**
 * Created by BeINone on 2016-09-21.
 */

public class VocabularyFragment extends Fragment {

    private VocabularyAdapter mAdapter;
    private boolean isNew;

    public static VocabularyFragment newInstance(Context context) {
        VocabularyFragment fragment = new VocabularyFragment();
        fragment.mAdapter = new VocabularyAdapter(context);
        fragment.isNew = true;

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_vocabulary, container, false);

        ((MainActivity) getActivity()).mToolbar.setTitle("단어장");

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview_vocabulary);
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(mAdapter);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        // 아이템 사이의 여백 추가
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 16, true));

        DatabaseManager dbManagaer = new DatabaseManager(getContext());

        if (isNew) {
            List<Vocabulary> vocabularies = dbManagaer.selectAllVocas();
            if (vocabularies != null) mAdapter.addAll(vocabularies);
        }
        isNew = false;

        return v;
    }

}
