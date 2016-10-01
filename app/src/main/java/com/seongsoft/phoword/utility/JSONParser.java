package com.seongsoft.phoword.utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by BeINone on 2016-09-23.
 */

public class JSONParser {

    public static ArrayList<String> parseFromJSON(String data, String name) {   // db에서 json과 json 배열에서 가져올 요소를 인자로 넘긴다.
        ArrayList<String> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(data);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                list.add(jsonObject.getString(name));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static String parseFromList(ArrayList<String> list, String value) {        // value는 json 저장될 값의 이름 ex "예문" "뜻"
        JSONArray jsonArray = new JSONArray();
        for(int i = 0; i < list.size(); i++){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(value, list.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }

        return jsonArray.toString();
    }

}
