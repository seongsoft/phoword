package com.seongsoft.phoword.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.seongsoft.phoword.R;
import com.seongsoft.phoword.fragment.WordInVocaFragment;

/**
 * Created by BeINone on 2016-09-26.
 */

public class WordInVocaActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_word_in_voca);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container_word_in_voca, new WordInVocaFragment())
                .commit();
    }

}
