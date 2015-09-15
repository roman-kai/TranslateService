package org.cloa.test.translateservice;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class YandexTranslate {
    private String key;


    public YandexTranslate(String key){
        this.key = key;
    }

    public String translate(String s){
        String res = null;
        try {
            JSONObject jsonObject = callAPI(s);
            if(jsonObject!= null)
                res = jsonObject.getJSONArray("text").getString(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    private JSONObject callAPI(String str) throws UnsupportedEncodingException{
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://translate.yandex.net/api/v1.5/tr.json/translate?");
        urlString.append("key=").append(key);
        urlString.append("&lang=").append("en-ru");
        urlString.append("&text=").append(URLEncoder.encode(str, "UTF-8"));
        HttpURLConnection urlConnection = null;
        URL url = null;
        JSONObject object = null;

        try
        {
            url = new URL(urlString.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.connect();
            InputStream inStream = null;
            inStream = urlConnection.getInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
            String temp, response = "";
            while ((temp = bReader.readLine()) != null)
                response += temp;
            bReader.close();
            inStream.close();
            urlConnection.disconnect();
            object = (JSONObject) new JSONTokener(response).nextValue();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return (object);

    }
}
