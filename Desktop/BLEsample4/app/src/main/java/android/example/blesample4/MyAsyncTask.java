package android.example.blesample4;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyAsyncTask extends AsyncTask<String, Void, JSONArray> {
    private Activity mActivity;
    UUID muuid;

    public MyAsyncTask(Activity activity, UUID uuid) {
        mActivity = activity;
        muuid = uuid;
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        JSONArray array = null;
        Request request = new Request.Builder().url("https://test-mfapp.herokuapp.com/connect_db.php").get().build();
        OkHttpClient client = new OkHttpClient();

        try {
            Response response = client.newCall(request).execute();
            String jsonData = response.body().string();
            try {
                array = new JSONArray(jsonData);
                String id;
                String uuid;
                String location;
                String url;
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    id = object.getString("id");
                    uuid = object.getString("uuid");
                    location = object.getString("location");
                    url = object.getString("utl");
                    Log.i("gotJSON:", location);
                }
                return array;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("MYAPP", "unexpected JSON exception", e);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return array;
    }

    public void onPostExecute(JSONArray array){
    }
}
