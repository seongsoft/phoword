package com.seongsoft.phoword.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.seongsoft.phoword.adapter.AddWordToVocaAdapter;
import com.seongsoft.phoword.manager.DatabaseManager;
import com.seongsoft.phoword.decoration.GridSpacingItemDecoration;
import com.seongsoft.phoword.R;
import com.seongsoft.phoword.component.Vocabulary;
import com.seongsoft.phoword.adapter.WordAdapter;
import com.seongsoft.phoword.component.WordSet;

import java.util.ArrayList;
import java.util.List;

public class AddWordToVocaActivity extends AppCompatActivity
        implements AddWordToVocaAdapter.MyOnWordAddedToVocaListener {

    public static final String EXTRA_VOCA = "vocabulary";
    public static final String EXTRA_WORDS = "wordsets";

    private List<WordSet> mWordSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word_to_voca);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_word_to_voca);
//        setSupportActionBar(toolbar);

        mWordSets = getIntent().getParcelableArrayListExtra(WordAdapter.EXTRA_SELECTED_WORDS);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_add_word_to_voca);
        recyclerView.setHasFixedSize(true);

        DatabaseManager dbManager = new DatabaseManager(this);
        List<Vocabulary> vocabularies = dbManager.selectAllVocas();

        recyclerView.setAdapter(
                new AddWordToVocaAdapter(this, (ArrayList<Vocabulary>) vocabularies, this));

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);

        // 아이템 사이의 여백 추가
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 16, true));
    }

    @Override
    public void onWordAddedToVoca(Vocabulary vocabulary) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_VOCA, vocabulary);
        intent.putParcelableArrayListExtra(EXTRA_WORDS, (ArrayList<WordSet>) mWordSets);
        setResult(RESULT_OK, intent);
        finish();
    }

}
