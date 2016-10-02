package com.seongsoft.phoword.dialog;

import android.content.ContextWrapper;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.seongsoft.phoword.R;
import com.seongsoft.phoword.component.WordSet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by BeINone on 2016-09-24.
 */

public class WordInfoDialogFragment extends DialogFragment {

    private static final String ARGS_KEY = "wordset";

    public static WordInfoDialogFragment newInstance(WordSet wordSet) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_KEY, wordSet);

        WordInfoDialogFragment fragment = new WordInfoDialogFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_word_info, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        TextView wordTV = (TextView) v.findViewById(R.id.tv_word);
        TextView pronounceTV = (TextView) v.findViewById(R.id.tv_pronounce);
        ImageButton audioIB = (ImageButton) v.findViewById(R.id.ib_audio);
        TextView meaningTV = (TextView) v.findViewById(R.id.tv_meaning);
        TextView exampleTV = (TextView) v.findViewById(R.id.tv_example);

        final WordSet wordSet = getArguments().getParcelable(ARGS_KEY);

        if (wordSet != null) {
            if (wordSet.getWord() != null && !wordSet.getWord().equals("null")) {
                wordTV.setText(wordSet.getWord());
            }
            if (wordSet.getPronounce() != null && !wordSet.getPronounce().equals("null")) {
                pronounceTV.setText(wordSet.getPronounce());
            }
            if (wordSet.getAudio() != null  && !wordSet.getAudio().equals("null")) {
                audioIB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playAudio(wordSet.getAudio());
                    }
                });
            }
            if (wordSet.getMeaning() != null && !wordSet.getMeaning().get(0).equals("null")) {
                for (int index = 0; index < wordSet.getMeaning().size(); index++) {
                    meaningTV.append(index + 1 + ". " + wordSet.getMeaning().get(index) + "\n");
                }
            }
            if (wordSet.getExample() != null && !wordSet.getExample().get(0).equals("null")) {
                for (int index = 0; index < wordSet.getExample().size(); index++) {
                    exampleTV.append(wordSet.getExample().get(index) + "\n");
                    exampleTV.append(wordSet.getExampleMeaning().get(index) + "\n\n");
                }
            }
        }

        return v;
    }

    //params 줄 때 소문자로 바꿔서 보내줄 것.
    public void playAudio(String path) {     //오디오 재생 메소드
        ContextWrapper contextWrapper = new ContextWrapper(getContext());

        try {
            File Mytemp = File.createTempFile("sample", "mp3", contextWrapper.getCacheDir());  //Make a file to play audio file
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

}
