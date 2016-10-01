package com.seongsoft.phoword.listener;

import com.seongsoft.phoword.component.Vocabulary;
import com.seongsoft.phoword.component.WordSet;

import java.io.Serializable;

public interface MyOnActionItemFinishListener extends Serializable {

    int RESULT_OK = -1;
    int RESULT_CANCELED = 0;

    void onActionItemFinish(int requestCode, int resultCode);
    void onActionItemFinish(int requestCode, int resultCode, WordSet wordSet);
    void onActionItemFinish(int requestCode, int resultCode, Vocabulary vocabulary);

}
