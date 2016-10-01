package com.seongsoft.phoword.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.seongsoft.phoword.manager.DatabaseManager;
import com.seongsoft.phoword.utility.ParseDictionary;
import com.seongsoft.phoword.R;
import com.seongsoft.phoword.component.WordSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BeINone on 2016-09-28.
 */

public class SettingsFragment extends Fragment {

    private DatabaseManager mDBManager;

    private List<String> mWords;
    private List<String> mMeanings;
    private List<String> mPronounces;
    private List<String> mAudios;
    private List<String> mExamples;
    private List<String> mExampleMeanings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        TextView scoreResultTV = (TextView) v.findViewById(R.id.tv_score_result);
        scoreResultTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        final TextView wordSyncTV = (TextView) v.findViewById(R.id.tv_word_sync);
        wordSyncTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncWord();
            }
        });

        return v;
    }

    private void syncWord() {
        if (isNetworkConnected()) {
            RecognizeAsyncTask recognizeAsyncTask = new RecognizeAsyncTask();
            recognizeAsyncTask.execute();
        } else{
            Toast.makeText(getActivity(), "네트워크가 연결되어 있지 않습니다.", Toast.LENGTH_SHORT)
                    .show();
        }
    }
    private class RecognizeAsyncTask extends AsyncTask<Void, Void, Void> {
        public ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("단어가 많으면 시간이 조금 걸릴수도 있습니다...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<WordSet> result = mDBManager.selectNoSyncWord();
            for (int i = 0; i < result.size(); i++) {
                String word = result.get(i).getWord();
                if (word.length() == 1)      //알파벳 하나만 인식 될 때 거름
                    break;
                ParseDictionary parseDictionary = new ParseDictionary(getContext());
                if (parseDictionary.getParsingData(word.toLowerCase())) {
                    mWords.add(parseDictionary.word);
                    mMeanings.add(parseDictionary.meaning.toString());
                    mPronounces.add(parseDictionary.pron);
                    mAudios.add(parseDictionary.audioPath);
                    mExamples.add(parseDictionary.example.toString());
                    mExampleMeanings.add(parseDictionary.example_mean.toString());
                }
            }
            return null;
        }
    }
    protected boolean isNetworkConnected() {
        ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        // wifi 또는 모바일 네트워크 어느 하나라도 연결이 되어있다면,
        if (wifi.isConnected() || mobile.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

}
