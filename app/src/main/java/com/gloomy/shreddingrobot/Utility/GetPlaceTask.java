package com.gloomy.shreddingrobot.Utility;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.gloomy.shreddingrobot.MainActivity;
import com.gloomy.shreddingrobot.Widget.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rzhu on 2/26/2015.
 */
public class GetPlaceTask extends AsyncTask<String,Void,String> {

    final String GOOGLE_KEY = "AIzaSyCwEG2NbmRZ89bkxPAhWjGyk1nfGf9Ld2k";
    private final String TAG = "GetPlaceTask";
    String temp;
    Context context;

    public GetPlaceTask(Context context) {
        // Required by the semantics of AsyncTask
        super();
        // Set a Context for the background task
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        // make Call to the url
        temp = makeCall("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + params[0] + "," + params[1] + "&radius=100&key=" + GOOGLE_KEY);

        return temp;
    }

    @Override
    protected void onPreExecute() {
        // we can start a progress bar here
    }

    @Override
    protected void onPostExecute(String result) {
        if (temp == null) {
            // we have an error to the call
            // we can also stop the progress bar
        } else {
            // all things went right

            // parse Google places search result
            String name =  getName(temp);
            ((MainActivity) context).setTrackLocation(name);
            Logger.e("name: " + name);
        }
    }

    private String makeCall(String url) {

        // string buffers the url
        StringBuffer buffer_string = new StringBuffer(url);
        String replyString = "";

        // instanciate an HttpClient
        HttpClient httpclient = new DefaultHttpClient();
        // instanciate an HttpGet
        HttpGet httpget = new HttpGet(buffer_string.toString());

        try {
            // get the responce of the httpclient execution of the url
            HttpResponse response = httpclient.execute(httpget);
            InputStream is = response.getEntity().getContent();

            // buffer input stream the result
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(20);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            // the result as a string is ready for parsing
            replyString = new String(baf.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Logger.i(TAG, replyString);

        // trim the whitespaces
        return replyString.trim();
    }

    private String getName(final String response) {

        ArrayList temp = new ArrayList();
        try {

            // make an jsonObject in order to parse the response
            JSONObject jsonObject = new JSONObject(response);

            // make an jsonObject in order to parse the response
            if (jsonObject.has("results")) {

                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++) {

                    if (jsonArray.getJSONObject(i).has("name")) {
                        return jsonArray.getJSONObject(i).optString("name");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";

    }
}
