package android.example.blesample3;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;

public class getPHP extends AsyncTask<String, Void, JSONObject>{
    private Activity mActivity;
    private String UUID;

    public getPHP(Activity activity, String uuid){
        mActivity = activity;
        UUID = uuid;
    }

    @Override
    protected JSONObject doInBackground(String...params) {
        HttpURLConnection httpURLConnection = null;
        JSONObject jsonObject = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(params[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream stream = httpURLConnection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line = "";
            while ((line = reader.readLine()) != null)
                stringBuilder.append(line);
            stream.close();

            jsonObject = new JSONObject(stringBuilder.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public void onPostExecute(JSONObject json) {
        StringBuilder builder = new StringBuilder();
        try {
            JSONArray array = json.getJSONArray("profile");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (UUID == obj.getString("Uuid")) {
                    builder.append(obj.getString("Uuid") + "\n");
                    builder.append(obj.getString("Url") + "\n");
                    builder.append(obj.getString("location") + "\n");
                    ((TextView) mActivity.findViewById(R.id.textview)).setText(builder.toString());
                }
                if(i == array.length() - 1){
                    ((TextView)mActivity.findViewById(R.id.textview)).setText("There is no beacon you want");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

