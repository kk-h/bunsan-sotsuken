package android.example.blesample2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class RealmActivity extends AppCompatActivity {
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realm);

        textView1 = (TextView)findViewById(R.id.text_uuid);
        textView2 = (TextView)findViewById(R.id.text_url);
        textView3 = (TextView)findViewById(R.id.text_location);

        Realm.init(this);
        Realm realm = Realm.getDefaultInstance();

        RealmQuery<Beacon_Model> query = realm.where(Beacon_Model.class);
        RealmResults<Beacon_Model> result1 = query.findAll();
        if(result1.get(0).getUuid() == null){
            makeDetabase(realm);
        }

        Intent intent = getIntent();
        String uuid = intent.getStringExtra("Uuid");

        query.equalTo("uuid", uuid);
        RealmResults<Beacon_Model> result2 = query.findAll();
        if (result2.get(0).getUuid() != uuid){
            Toast.makeText(this, "Connected Beacon isn't in a List", Toast.LENGTH_SHORT);
            finish();
        }
        textView1.setText(result2.get(0).getUuid());
        textView2.setText(result2.get(0).getUrl());
        textView3.setText(result2.get(0).getLocation());
    }

    private void makeDetabase(Realm realm){
        realm.beginTransaction();
        Beacon_Model beacon_model = realm.createObject(Beacon_Model.class);
        beacon_model.setUuid("ox1698");
        beacon_model.setUrl("google.co.jp");
        beacon_model.setLocation("〒739-0046 広島県東広島市鏡山１丁目３−２");
        realm.commitTransaction();
    }
}
