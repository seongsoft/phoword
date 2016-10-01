package com.seongsoft.phoword.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.seongsoft.phoword.dialog.AddWordDialogFragment;
import com.seongsoft.phoword.tesseract.DataPath;
import com.seongsoft.phoword.manager.DatabaseManager;
import com.seongsoft.phoword.utility.JSONParser;
import com.seongsoft.phoword.fragment.QuizFragment;
import com.seongsoft.phoword.R;
import com.seongsoft.phoword.fragment.SettingsFragment;
import com.seongsoft.phoword.component.Vocabulary;
import com.seongsoft.phoword.fragment.VocabularyFragment;
import com.seongsoft.phoword.fragment.WordFragment;
import com.seongsoft.phoword.component.WordSet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.seongsoft.phoword.activity.AddWordToVocaActivity.EXTRA_VOCA;

public class MainActivity extends AppCompatActivity
        implements AddWordDialogFragment.MyOnWordAddedListener {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_CROP_FROM_CAMERA = 2;
    public static final int REQUEST_IMAGE_ALBUM = 3;
    public static final int REQUEST_ADD_WORD_TO_VOCA = 4;
    public static final int REQUEST_ADD_RECOGNIZED_WORD = 5;
    public static final int REQUEST_QUIZ = 6;

    public static final String TAG_ADD_WORD_DIALOG = "ADD_WORD_DIALOG";
    public static final String TAG_ADD_LIST_DIALOG = "ADD_LIST_DIALOG";
    public static final String TAG_SORT_DIALOG = "SORT_DIALOG";
    public static final String EXTRA_WORD = "word_sets";
    public static final String EXTRA_LIST = "word_list";
    public static final String EXTRA_TYPE = "view_type";
    public static final String EXTRA_RECOG_WORD = "recognized_word_sets";
    public static final String EXTRA_NUM_WORDS = "num_words";
    public static final String EXTRA_VOCA_NAME = "vocabulary";
    public static final String EXTRA_QUIZ_TYPES = "quiz_types";

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 2;
    private static final int MY_PERMISSIONS_REQUEST_FLASHLIGHT = 3;
    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 4;

    private static final int TAB_VOCABULARY = 0;
    private static final int TAB_WORD = 1;
    private static final int TAB_QUIZ = 2;
    private static final int TAB_SETTINGS = 3;

    private FloatingActionMenu mFAM;
    private com.github.clans.fab.FloatingActionButton mSearchFAB;
    private com.github.clans.fab.FloatingActionButton mStartQuizFAB;
    public Toolbar mToolbar;
    public BottomBar mBottomBar;
    private DatabaseManager mDBManager;

    private WordFragment mWordFragment;
    private VocabularyFragment mVocaFragment;
    private QuizFragment mQuizFragment;
    private SettingsFragment mSettingsFragment;

    private int mPrevSelectedTab = TAB_WORD;

    private boolean doubleBackToExitPressedOnce;

    protected String _path;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("단어장");
        setSupportActionBar(mToolbar);

//        deleteDatabase("words.db");

        mDBManager = new DatabaseManager(this);

        mWordFragment = WordFragment.newInstance(this, mDBManager.selectAllWords());
        mVocaFragment = VocabularyFragment.newInstance(this);
        mQuizFragment = new QuizFragment();
        mSettingsFragment = new SettingsFragment();

        mFAM = (FloatingActionMenu) findViewById(R.id.fam);

        mSearchFAB = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_search);
        mSearchFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mStartQuizFAB = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_start_quiz);
        mStartQuizFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra(EXTRA_VOCA_NAME, mQuizFragment.getSelectedVocabulary().getName());
                intent.putExtra(EXTRA_NUM_WORDS, mQuizFragment.getNumWords());
                intent.putStringArrayListExtra(EXTRA_QUIZ_TYPES, mQuizFragment.getQuizTypes());
                startActivityForResult(intent, REQUEST_QUIZ);
            }
        });
        mStartQuizFAB.hide(true);

        mBottomBar = (BottomBar) findViewById(R.id.bottombar);

        // 중앙 BottomBarTab 비활성화
        mBottomBar.getTabWithId(R.id.tab_blank).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_vocabulary) {
                    mToolbar.setTitle("단어장");
                    mPrevSelectedTab = TAB_VOCABULARY;
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, mVocaFragment)
                            .commit();
                }

                if (tabId == R.id.tab_account) {
                    mToolbar.setTitle("단어");
                    mPrevSelectedTab = TAB_WORD;
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, mWordFragment)
                            .commit();
                }

                if (tabId == R.id.tab_quiz) {
                    if (mDBManager.selectAllWords().isEmpty()) {
                        Toast.makeText(MainActivity.this, "저장된 단어가 존재하지 않습니다.",
                                Toast.LENGTH_SHORT)
                                .show();
//                        mBottomBar.getCurrentTab().setSelected(false);
//                        mBottomBar.selectTabAtPosition(mPrevSelectedTab);
                        return;
                    }
                    mPrevSelectedTab = TAB_QUIZ;
                    mToolbar.setTitle("퀴즈");
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, mQuizFragment)
                            .commit();
                }

                if (tabId == R.id.tab_settings) {
//                    mToolbar.setTitle("설정");
//                    mPrevSelectedTab = TAB_SETTINGS;
//                    getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.container, mSettingsFragment)
//                            .commit();
//                    mBottomBar.getCurrentTab().setSelected(false);
//                    mBottomBar.selectTabAtPosition(mPrevSelectedTab);
                    Toast.makeText(MainActivity.this, "준비 중입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        // label margin 없애보기
//        fam.setClosedOnTouchOutside(true);

        com.github.clans.fab.FloatingActionButton cameraFAB = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_camera);
        cameraFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraActivity();
                mFAM.close(false);
            }
        });

        com.github.clans.fab.FloatingActionButton galleryFAB = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_gallery);
        galleryFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, REQUEST_IMAGE_ALBUM);
                mFAM.close(false);
            }
        });

        com.github.clans.fab.FloatingActionButton textFAB = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_text);
        textFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AddWordDialogFragment dialogFragment = AddWordDialogFragment.newInstance(MainActivity.this);
//                dialogFragment.show(getSupportFragmentManager(), TAG_ADD_WORD_DIALOG);
                mFAM.close(false);
            }
        });

        verifyPermissions(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Intent intent;

            switch (requestCode) {
                case REQUEST_IMAGE_ALBUM:
                    DataPath.PHOTO_URI = data.getData();
                    DataPath.PHOTO_PATH = getPathFromUri(DataPath.PHOTO_URI);

                case REQUEST_IMAGE_CAPTURE:
                    intent = new Intent(MainActivity.this, CropImageActivity.class);
                    startActivityForResult(intent, REQUEST_CROP_FROM_CAMERA);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;

                case REQUEST_CROP_FROM_CAMERA:
                    List<WordSet> recogWordSets = new ArrayList<>();

                    List<String> words = data.getStringArrayListExtra(CropImageActivity.EXTRA_WORD);
                    List<String> meanings = data.getStringArrayListExtra(CropImageActivity.EXTRA_MEANING);
                    List<String> pronounces = data.getStringArrayListExtra(CropImageActivity.EXTRA_PRONOUNCE);
                    List<String> audios = data.getStringArrayListExtra(CropImageActivity.EXTRA_AUDIO);
                    List<String> examples = data.getStringArrayListExtra(CropImageActivity.EXTRA_EXAMPLE);
                    List<String> exampleMeanings = data.getStringArrayListExtra(CropImageActivity.EXTRA_EXAMPLE_MEANING);

                    for (int index = 0; index < words.size(); index++) {
                        String word = words.get(index);
                        List<String> meaning = JSONParser.parseFromJSON(meanings.get(index), "뜻");
                        String pronounce = pronounces.get(index);
                        String audio = audios.get(index);
                        List<String> example = JSONParser.parseFromJSON(examples.get(index), "예문");
                        List<String> exampleMeaning = JSONParser.parseFromJSON(exampleMeanings.get(index), "해석");
                        WordSet wordSet
                                = new WordSet(word, (ArrayList<String>) meaning, pronounce, audio,
                                (ArrayList<String>) example, (ArrayList<String>) exampleMeaning,
                                System.currentTimeMillis());
                        recogWordSets.add(wordSet);
                    }

                    intent = new Intent(this, RecognizedWordActivity.class);
                    intent.putParcelableArrayListExtra(EXTRA_RECOG_WORD, (ArrayList<WordSet>) recogWordSets);
                    startActivityForResult(intent, REQUEST_ADD_RECOGNIZED_WORD);
                    break;

                case REQUEST_ADD_RECOGNIZED_WORD:
                    List<WordSet> wordSets = data.getParcelableArrayListExtra(RecognizedWordActivity.EXTRA_WORDS);

                    if (wordSets != null) {
                        for (WordSet wordSet : wordSets) {
                            try {
                                String word = wordSet.getWord();
                                String meaning = JSONParser.parseFromList(
                                        wordSet.getMeaning(), "뜻");
                                String pronounce = wordSet.getPronounce();
                                String audio = wordSet.getAudio();
                                String example = JSONParser.parseFromList(
                                        wordSet.getExample(), "예문");
                                String exampleMeaning = JSONParser.parseFromList(
                                        wordSet.getExampleMeaning(), "해석");

                                mDBManager.insertWord(word, meaning, pronounce, audio, example,
                                        exampleMeaning, System.currentTimeMillis());
                                mWordFragment.getAdapter().add(wordSet);
                            } catch (SQLiteConstraintException ce) {
                                /*   예외처리 필요   */
                                ce.printStackTrace();
                            }
                        }
                        Toast.makeText(this, wordSets.size() + "개의 단어가 추가되었습니다.", Toast.LENGTH_SHORT).show();

                        for (int i = 0; i < mWordFragment.getAdapter().mWordSets.size(); i++)
                            Log.i("wordsets in adapter", mWordFragment.getAdapter().mWordSets.get(i).getWord());
                    }
                    break;

                case REQUEST_ADD_WORD_TO_VOCA:
                    List<WordSet> selectedWordSets = data.getParcelableArrayListExtra(AddWordToVocaActivity.EXTRA_WORDS);
                    Vocabulary vocabulary = data.getParcelableExtra(EXTRA_VOCA);
                    for (int index = 0; index < selectedWordSets.size(); index++) {
                        mDBManager.insertVoca(vocabulary, selectedWordSets.get(index));
                    }
                    mWordFragment.getAdapter().quitChoiceMode();
                    mWordFragment.getAdapter().mActionMode.finish();
                    Toast.makeText(this,
                            selectedWordSets.size() + "개의 단어가 '"
                                    + vocabulary.getName() + "'에 추가되었습니다.",
                            Toast.LENGTH_SHORT)
                            .show();
                    break;

                case REQUEST_QUIZ:

                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        doubleBackToExit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onWordAdded(WordSet wordSet) {
        try {
            /*   단어와 뜻을 DB에 저장   */
//            mDBManager.insertWord(wordSet);

            /*   페이지에 단어 추가   */
            mWordFragment.getAdapter().add(wordSet);
        } catch (SQLiteConstraintException e) {
            Toast.makeText(this, R.string.word_duplication, Toast.LENGTH_SHORT).show();
        }
    }

    public String getPathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex("_data"));
        cursor.close();

        return path;
    }

    public void showSearchFAB() {
        mSearchFAB.show(true);
    }

    public void hideSearchFAB() {
        mSearchFAB.hide(true);
    }

    public void showStartQuizFAB() {
        mStartQuizFAB.show(true);
    }

    public void hideStartQuizFAB() {
        mStartQuizFAB.hide(true);
    }

    private void doubleBackToExit() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();

        if (backStackEntryCount == 0) {

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.double_back_to_exit, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

        } else {
            super.onBackPressed();
        }
    }

    private void startCameraActivity() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (intent.resolveActivity(getPackageManager()) != null) {
            File file = null;
            try {
                file = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (file != null) {
                mImageUri = Uri.fromFile(file);
                DataPath.PHOTO_PATH = file.getPath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(       // directory로 지정된 폴더로 임시파일이 생성된다.
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        _path = image.getAbsolutePath(); //현재 파일의 절대경로를 반환함

        return image;
    }

    private void verifyPermissions(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.FLASHLIGHT)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.FLASHLIGHT)) {

            } else {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.FLASHLIGHT},
                        MY_PERMISSIONS_REQUEST_FLASHLIGHT);
            }
        }

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.INTERNET)) {

            } else {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.INTERNET},
                        MY_PERMISSIONS_REQUEST_INTERNET);
            }
        }
    }

}
