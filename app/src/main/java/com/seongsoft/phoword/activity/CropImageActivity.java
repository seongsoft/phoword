package com.seongsoft.phoword.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.seongsoft.phoword.component.CropImage;
import com.seongsoft.phoword.tesseract.DataPath;
import com.seongsoft.phoword.utility.ParseDictionary;
import com.seongsoft.phoword.R;
import com.seongsoft.phoword.tesseract.TessCore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BeINone on 2016-09-22.
 */

public class CropImageActivity extends AppCompatActivity {

    public static final String EXTRA_WORD = "words";
    public static final String EXTRA_MEANING = "meanings";
    public static final String EXTRA_PRONOUNCE = "pronounces";
    public static final String EXTRA_AUDIO = "audios";
    public static final String EXTRA_EXAMPLE = "examples";
    public static final String EXTRA_EXAMPLE_MEANING = "example_meanings";
    private static final String TAG = "CropImageActivity";

    private TessCore mTessCore = new TessCore(this);
    private CropImage mCropImage;
    private Bitmap mBitmap;

    private RecognizeAsyncTask mAsyncTask;

    private List<String> mWords;
    private List<String> mMeanings;
    private List<String> mPronounces;
    private List<String> mAudios;
    private List<String> mExamples;
    private List<String> mExampleMeanings;

    private int dgree = 0;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_crop_image);

        mCropImage = (CropImage) this.findViewById(R.id.crop_image_view);
        mCropImage.setCornerDrawable(80, 80);
        View view = getLayoutInflater().inflate(R.layout.activity_main, null);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        Log.d(TAG, "PhotoPath : " + DataPath.PHOTO_PATH);
        mCropImage.setImagePath(DataPath.PHOTO_PATH);

        mWords = new ArrayList<>();
        mMeanings = new ArrayList<>();
        mPronounces = new ArrayList<>();
        mAudios = new ArrayList<>();
        mExamples = new ArrayList<>();
        mExampleMeanings = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_crop, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.save_btn:
                mAsyncTask = new RecognizeAsyncTask();
                mAsyncTask.execute();
                return true;
            case R.id.rotate_btn:
                mCropImage.rotateImage(90);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mAsyncTask != null) mAsyncTask.progressDialog.dismiss();
    }

    private class RecognizeAsyncTask extends AsyncTask<Void, Void, Void> {

        public ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(CropImageActivity.this);
            progressDialog.setMessage("잠시만 기다려주세요...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            mBitmap = mCropImage.crop(CropImageActivity.this);
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<String> result = mTessCore.detectText(mBitmap);

            for (String word : result) {
                if(word.length() <= 3 || word.equals("")){
                    continue;
                }
                ParseDictionary parseDictionary = new ParseDictionary(CropImageActivity.this);
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

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            Intent intent = getIntent();
            intent.putStringArrayListExtra(EXTRA_WORD, (ArrayList<String>) mWords);
            intent.putStringArrayListExtra(EXTRA_MEANING, (ArrayList<String>) mMeanings);
            intent.putStringArrayListExtra(EXTRA_PRONOUNCE, (ArrayList<String>) mPronounces);
            intent.putStringArrayListExtra(EXTRA_AUDIO, (ArrayList<String>) mAudios);
            intent.putStringArrayListExtra(EXTRA_EXAMPLE, (ArrayList<String>) mExamples);
            intent.putStringArrayListExtra(EXTRA_EXAMPLE_MEANING, (ArrayList<String>) mExampleMeanings);
            setResult(-1, intent);
            finish();
        }

    }

}