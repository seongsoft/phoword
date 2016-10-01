package com.seongsoft.phoword.tesseract;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.util.Arrays;
import java.util.List;

/**
 * Created by BeINone on 2016-09-22.
 */

public class TessCore {

    static final String TAG = "DBG_" + TessCore.class.getName();

    private Context mCtx;

    public TessCore(Context context) {
        this.mCtx = context;
    }

    public List<String> detectText(Bitmap bitmap) {           //비트맵 이미지를 인자로 받게 된다.
        Log.d(TAG, "Initialization of TessBaseApi");
        TessDataManager.initTessTrainedData(mCtx);      //DataManager클래스의 initTessTrainedData 메소드를 통해 트레이닝 데이터를 읽는다.
        TessBaseAPI tessBaseAPI = new TessBaseAPI();        //이제 tesseract OCR라이브러리를 사용하기 위해 참조변수를 선언했다.
        String path = TessDataManager.getTesseractFolder();     //path에는 tesseract라는 이름의 디렉토리의 경로가 저장된다.
        Log.d(TAG, "Tess folder: " + path);
        tessBaseAPI.setDebug(true);
        tessBaseAPI.init(path, "eng");
        // 추천 글자들
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        // 비추천 글자들
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "!@#$%^&*()_+=-[]}{;:'\"\\|~`,./<>?");
        tessBaseAPI.setPageSegMode(TessBaseAPI.OEM_TESSERACT_CUBE_COMBINED);
        Log.d(TAG, "Ended initialization of TessEngine");
        Log.d(TAG, "Running inspection on bitmap");
        tessBaseAPI.setImage(bitmap);           //전달 받은 비트맵 이미지를 넣는다.
        String inspection = tessBaseAPI.getUTF8Text();      //UTF8 포맷에 맟주어 인식된 글자를 저장한다.
        Log.d(TAG, "=====Got data=====" + inspection);
        List<String> recogText = Arrays.asList(inspection.split("\n|\\u0020"));    //이 부분 참 힘들게 했다.
        tessBaseAPI.end();
        return recogText;              // 글자를 반환한다.
    }

}