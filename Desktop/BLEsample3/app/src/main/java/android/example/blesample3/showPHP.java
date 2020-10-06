package android.example.blesample3;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class showPHP extends AppCompatActivity {
    private getPHP httpreq; // 追加

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_php);

        Intent intent = new Intent();
        String UUID = intent.getStringExtra("Uuid");

        httpreq = new getPHP(this, UUID);
        httpreq.execute("http://localhost:8080/read.php");

    }
}

