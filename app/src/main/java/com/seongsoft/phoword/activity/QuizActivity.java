package com.seongsoft.phoword.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.seongsoft.phoword.manager.DatabaseManager;
import com.seongsoft.phoword.R;
import com.seongsoft.phoword.component.WordSet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dsm_025 on 2016-06-27.
 */
public class QuizActivity extends AppCompatActivity {

    private DatabaseManager mDBManager;
    private List<WordSet> mWordSets;
    private TextView mQuestionTextView;
    private EditText mInputEditText;
    private ImageView mResultImageView;
    private ImageButton mNextImageButton;

    private int mWordCount;
    private int mNumCorrect;
    private int mNumWrong;

    ImageButton mSoundButton;

    int numWords;
    String vocaName;
    String[] type;
    String[] words;

    String question;
    String answer;

    String mean_question;
    List<String> mean_anaswer;
    List<String> word_question;
    String word_answer;
    String sound_question;
    String sound_answer;
    String example_qustion;
    String example_realAnswer;
    String example_answer;
    String current_quiz;
    int cnt = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Intent intent = getIntent();
        vocaName = intent.getExtras().getString(MainActivity.EXTRA_VOCA_NAME);
        numWords = intent.getExtras().getInt(MainActivity.EXTRA_NUM_WORDS);
        ArrayList<String> quizType = intent.getExtras().getStringArrayList(MainActivity.EXTRA_QUIZ_TYPES);
        type = quizType.toArray(new String [quizType.size()]);
        mDBManager = new DatabaseManager(getApplicationContext());

        createQuiz();
        randQuiz();

        mResultImageView = (ImageView) findViewById(R.id.quiz_result_iv);

        mQuestionTextView = (TextView) findViewById(R.id.quiz_question_tv);
        question();

        mInputEditText = (EditText) findViewById(R.id.quiz_input_et);

        mNextImageButton = (ImageButton) findViewById(R.id.quiz_next_ib);
        mNextImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });
    }
    private void createQuiz(){
        words = new String[numWords];
        List<WordSet> list = mDBManager.selectWordsforQuiz(vocaName, numWords);
        for(int i = 0; i < list.size(); i++){
            words[i] = list.get(i).getWord();
        }
        //DB에서 단어 불러오기 메소드 처리
    }
    private void randQuiz(){
        String word = words[cnt];
        int ran = (int) (Math.random() * type.length - 1);
        current_quiz = type[ran];
        WordSet wordSet = mDBManager.selectWord(words[cnt]);
        switch (type[ran]){
            case "뜻":
                mean_question =  wordSet.getWord();
                mean_anaswer = wordSet.getMeaning();
                break;
            case "단어":
                word_question = wordSet.getMeaning();
                word_answer = wordSet.getWord();
                break;
            case "발음":
                sound_question = wordSet.getAudio();
                sound_answer = wordSet.getWord();
                break;
            case "예문":
                List<String> list = wordSet.getExample();
                String q = list.get((int) (Math.random() * list.size() - 1));
                example_qustion = exampleQuiz_question(q, word);
                example_answer = q;
                example_realAnswer = word;
                break;
        }
    }
    private String exampleQuiz_question(String data, String word){
        String a;
        a = data.replace(word, "[       ]");
        return a;
    }
    private void question() {
        switch (current_quiz){
            case "뜻":
                mQuestionTextView.setText(mean_question);
                break;
            case "단어":
                String q = "";
                int i;
                for(i = 0; i < word_question.size()-1; i++) {
                    q += word_question.get(i) + ",";
                }
                q += word_question.get(i);
                mQuestionTextView.setText(q);
                break;
            case "발음":
                mSoundButton = (ImageButton)findViewById(R.id.sound);
                mSoundButton.setVisibility(View.VISIBLE);
                mSoundButton.bringToFront();
                mSoundButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playAudio(sound_question);
                    }
                });
                break;
            case "예문":
                mQuestionTextView.setText(example_qustion);
                break;
            }

    }

    public void playAudio(String path) {     //오디오 재생 메소드
        try {
            File Mytemp = File.createTempFile("sample", "mp3", getCacheDir());  //Make a file to play audio file
            Mytemp.deleteOnExit();
            File file = new File(path);
            int size = (int) file.length();
            byte[] bytes = new byte[size];
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();

            FileOutputStream fos = new FileOutputStream(Mytemp);
            fos.write(bytes);
            fos.close();

            MediaPlayer mediaPlayer = new MediaPlayer();

            FileInputStream MyFile = new FileInputStream(Mytemp);
            mediaPlayer.setDataSource(MyFile.getFD());

            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void nextQuestion() {
        checkAnswer();
        if (cnt == words.length - 1) {
            mQuestionTextView.setText(null);
            displayResult();
        }
        else{
            mInputEditText.setText(null);
            cnt++;
            randQuiz();
            question();
        }
    }

    private void checkAnswer() {
        String answer = mInputEditText.getText().toString();

        switch (current_quiz) {
            case "뜻":
                for(String s : mean_anaswer) {
                    if(s.equals(answer)) {
                        upCount();
                        Toast.makeText(getApplicationContext(), "맞췄습니다", Toast.LENGTH_SHORT).show();
                        mResultImageView.setImageDrawable(null);
                        return;
                    }
                }
                Toast.makeText(getApplicationContext(), "틀렸습니다", Toast.LENGTH_SHORT).show();

                downCount();
                break;
            case "단어":
                if(word_answer.equals(answer)) {
                    upCount();
                    Toast.makeText(getApplicationContext(), "맞췄습니다", Toast.LENGTH_SHORT).show();
                    break;
                }
                Toast.makeText(getApplicationContext(), "틀렸습니다", Toast.LENGTH_SHORT).show();
                downCount();
                break;
            case "발음":
                if(sound_answer.equals(answer)) {
                    upCount();
                    Toast.makeText(getApplicationContext(), "맞췄습니다", Toast.LENGTH_SHORT).show();
                    break;
                }
                downCount();
                Toast.makeText(getApplicationContext(), "틀렸습니다", Toast.LENGTH_SHORT).show();

                break;
            case "예문":
                if(example_realAnswer.equals(answer)) {
                    upCount();
                    Toast.makeText(getApplicationContext(), "맞췄습니다", Toast.LENGTH_SHORT).show();
                    break;
                }
                downCount();
                Toast.makeText(getApplicationContext(), "틀렸습니다", Toast.LENGTH_SHORT).show();
                break;
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mResultImageView.setImageDrawable(null);
    }
    public void upCount() {
        mDBManager.upCountQuizWord(words[cnt]);
        mNumCorrect++;
    }
    public void downCount() {
        mNumWrong++;
    }
    private void displayResult() {
        TextView correctPercentageTV = (TextView) findViewById(R.id.quiz_correct_percentage);
        TextView numCorrectTV = (TextView) findViewById(R.id.quiz_num_correct_tv);
        TextView numWrongTV = (TextView) findViewById(R.id.quiz_num_wrong_tv);

        int numQuestion = mNumCorrect + mNumWrong;
        int correctPercentage = (100 / numQuestion) * mNumCorrect;
        if(correctPercentage == 99)
            correctPercentage =100;

        correctPercentageTV.setText(String.valueOf(correctPercentage));
        numCorrectTV.setText(String.valueOf(mNumCorrect));
        numWrongTV.setText(String.valueOf(mNumWrong));

        View questionLayout = findViewById(R.id.quiz_question_layout);
        View resultLayout = findViewById(R.id.quiz_result_layout);

        questionLayout.setVisibility(View.INVISIBLE);
        resultLayout.setVisibility(View.VISIBLE);

        Button okButton = (Button) findViewById(R.id.quiz_ok_btn);
        mNextImageButton.setVisibility(View.INVISIBLE);
        okButton.setVisibility(View.VISIBLE);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_quiz);
//
//        txt = (TextView)findViewById(R.id.question);
//        wordSets =  DBManager.getInstance(this).selectAllWords();
//        txt.append(wordSets.get(0).getMeaning());
//        edit = (EditText)findViewById(R.id.answer);
//        checkButton = (Button)findViewById(R.id.check_button);
//        checkButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                checkAnswer();
//                if(wordSets.size() > listCnt + 1) {
//                    edit.setText("");
//                    txt.setText("문제 : " + wordSets.get(++listCnt).getMeaning());
//                }else{
//                    Intent i = getIntent();
//                    setResult(RESULT_OK);
//                    Toast.makeText(getApplicationContext(), "맞춘 개수 : " + trueCnt + " 틀린 개수" + falsetCnt , Toast.LENGTH_LONG).show();
//                    finish();
//                }
//            }
//        });
//    }
//
//    public void checkAnswer(){
//        String correct= wordSets.get(listCnt).getMeaning();
//        String answer = String.valueOf(edit.getText());
//        if(correct.equals(answer)){
//            Toast.makeText(getApplicationContext(), "정답입니다.", Toast.LENGTH_SHORT).show();
//            trueCnt++;
//        }else{
//            Toast.makeText(getApplicationContext(), "오답입니다.", Toast.LENGTH_SHORT).show();
//            falsetCnt++;
//        }
//    }

}

