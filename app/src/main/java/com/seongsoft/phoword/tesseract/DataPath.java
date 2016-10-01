package com.seongsoft.phoword.tesseract;

import android.net.Uri;
import android.os.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BeINone on 2016-09-22.
 */

public class DataPath {

    // SD카드가 마운트 된 경로를 조사한다. 구한 경로의 /SimpleAndroidOCR 이라는 디텍토리를 생성한 것을 의미한다.
    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory().toString() + "/SimpleAndroidOCR" + "/ocr.jpg";
    public static String PHOTO_PATH;
    public static Uri PHOTO_URI;
    public static List<String> recog_text = new ArrayList<>();
//    public static List<WordSet> quiz_text = new ArrayList<WordSet>();

}
