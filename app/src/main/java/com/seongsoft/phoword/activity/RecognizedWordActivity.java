package com.seongsoft.phoword.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.seongsoft.phoword.R;
import com.seongsoft.phoword.adapter.RecognizedWordAdapter;
import com.seongsoft.phoword.component.MyRecyclerView;
import com.seongsoft.phoword.component.WordSet;
import com.seongsoft.phoword.decoration.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BeINone on 2016-09-22.
 */

public class RecognizedWordActivity extends AppCompatActivity {

    public static final String EXTRA_WORDS = "word_sets";

    private RecognizedWordAdapter mAdapter;
    private List<WordSet> mWordSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognized_word);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_recognized_word);
        setSupportActionBar(toolbar);

        mWordSets = getIntent().getParcelableArrayListExtra(MainActivity.EXTRA_RECOG_WORD);

        MyRecyclerView recyclerView = (MyRecyclerView) findViewById(R.id.recyclerview_recognized_word);
        recyclerView.setHasFixedSize(true);

        View emptyView = findViewById(R.id.view_empty_recognized_word);
        recyclerView.setEmptyView(emptyView);

        mAdapter = new RecognizedWordAdapter(this, (ArrayList<WordSet>) mWordSets);
        recyclerView.setAdapter(mAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*   리스트에 구분선을 넣음   */
        recyclerView.addItemDecoration(new DividerItemDecoration(this));

        View addLayout = findViewById(R.id.layout_add);
        addLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(
                        EXTRA_WORDS, mAdapter.getSelectedWordSets());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recognized_word, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_all) {
            if (mAdapter.getNumSelectedWords() == mWordSets.size()) {
                mAdapter.deselectAll();
            } else {
                mAdapter.selectAll();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
