package com.seongsoft.phoword.utility;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.seongsoft.phoword.manager.DatabaseManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by dsm_025 on 2016-08-16.
 */
public class ParseDictionary {

    private Elements mean;
    private String[] example_arr;
    private String[] example_mean_arr;
    private DatabaseManager mDBManager;
    private Context mContext;

    public String word;
    public JSONArray meaning;
    public String pron;
    public String audioPath;
    public JSONArray example;
    public JSONArray example_mean;

    public ParseDictionary(Context context) {
        mContext = context;
    }

    public boolean getParsingData(String data) {
        word = data.toLowerCase();
        String url = "http://dic.daum.net/search.do?q=" + data + "&dic=eng&search_first=Y";
//            String url = "http://search.daum.net/search?nil_suggest=btn&w=tot&DA=SBC&q=" +params[0] ;

        System.out.println("download: " + url);

        Document doc = null;

        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (doc != null) {
            //#mArticle > .search_cont > .card_word > .search_box >
            Element element = doc.select(".cleanword_type > .search_cleanword > .tit_cleansch > .txt_cleansch >span").first();

            try {
                if (!word.equals(element.text().replaceAll(" ", ""))) {
                    return false;
                }
            } catch (NullPointerException e) {
                return false;
            }

            mean = doc.select(".cleanword_type > .list_search > li > .txt_search");       //뜻 Parsing

            Element pronounce = doc.select(".cleanword_type > .wrap_listen > .desc_listen > .txt_pronounce").first();
            if (pronounce != null) pron = pronounce.text();

            Elements example = doc.select(".card_word > .cont_example > .list_example > li > .box_example > .txt_example > .txt_ex");
            Elements example_mean = doc.select(".card_word > .cont_example > .list_example > li > .box_example > .mean_example > .txt_ex");
            if (example != null) {
                example_arr = new String[example.size()];

                int cnt = 0;
                for (Element e : example) {
                    example_arr[cnt++] = e.text();
                }
            }
            if (example_mean != null) {
                example_mean_arr = new String[example_mean.size()];

                int cnt = 0;
                for (Element e : example_mean) {
                    example_mean_arr[cnt++] = e.text();
                }
            }

            try {
                int count;
                Element title = doc.select(".cleanword_type > .wrap_listen > .desc_listen > a").first();

                if (title != null) {
                    audioPath = title.attr("href");
                    //download audio file from url
                    URL req = null;
                    URLConnection connection = null;
                    req = new URL(audioPath);
                    connection = req.openConnection();
                    connection.connect();
                    int lengthOfFile = connection.getContentLength();
                    Log.d("length : ", lengthOfFile + "");
                    File audioForder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/PhoWord/");

                    if (!audioForder.exists()) {
                        audioForder.mkdir();
                        new File(audioForder.getPath() + ".nomedia");
                    }
                    audioPath = audioForder.getPath() + "/" + data + ".mp3";

                    InputStream input = new BufferedInputStream(req.openStream());
                    OutputStream output = new FileOutputStream(audioPath);

                    byte audio[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(audio)) != -1) {
                        total += count;
                        output.write(audio, 0, count);
                    }

                    output.flush();
                    output.close();
                    input.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mean == null) {
                return false;
            }

            createJson();
            insertDB();

            return true;
        }

        return false;
    }

    private void createJson() {
        meaning = new JSONArray();
        for (Element e : mean) {
            JSONObject object = new JSONObject();
            try {
                object.put("뜻", e.text());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            meaning.put(object);
        }
        example = new JSONArray();
        for (int i = 0; i < example_arr.length; i++) {
            JSONObject object = new JSONObject();
            try {
                object.put("예문", example_arr[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            example.put(object);
        }
        example_mean = new JSONArray();
        for (int i = 0; i < example_mean_arr.length; i++) {
            JSONObject object = new JSONObject();
            try {
                object.put("해석", example_mean_arr[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            example_mean.put(object);
        }
        System.out.println("JSON 결과 : " + example + example_mean);
    }

    private void insertDB() {
        mDBManager = new DatabaseManager(mContext);
        mDBManager.insertWord(word, meaning.toString(), pron, audioPath, example.toString(), example_mean.toString(), System.currentTimeMillis());
    }

}

//params 줄 때 소문자로 바꿔서 보내줄 것.
//밑의 내용은 알아서 잘 쓰시길
//    public void playAudio(String path) {     //오디오 재생 메소드
//        try {
//            File Mytemp = File.createTempFile("sample", "mp3", getCacheDir());  //Make a file to play audio file
//            Mytemp.deleteOnExit();
//            File file = new File(path);
//            int size = (int) file.length();
//            byte[] bytes = new byte[size];
//            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
//            buf.read(bytes, 0, bytes.length);
//            buf.close();
//
//            FileOutputStream fos = new FileOutputStream(Mytemp);
//            fos.write(bytes);
//            fos.close();
//
//            MediaPlayer mediaPlayer = new MediaPlayer();
//
//            FileInputStream MyFile = new FileInputStream(Mytemp);
//            mediaPlayer.setDataSource(MyFile.getFD());
//
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }