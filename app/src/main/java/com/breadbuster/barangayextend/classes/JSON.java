package com.breadbuster.barangayextend.classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSON {
    public static void getJSONData(String arrayName, String columnName, ArrayList<String> arrayList, JSONObject response){
        try {
            JSONArray jsonArray = response.getJSONArray(arrayName);

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                arrayList.add(jsonObject.getString(columnName));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
