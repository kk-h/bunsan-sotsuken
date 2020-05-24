package android.example.myjanken;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SignActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
    }

    public void onConfirmationButtonTapped(View view) {
        EditText phone = (EditText) findViewById(R.id.phone_text);
        EditText email = (EditText) findViewById(R.id.mail_text);
        EditText address = (EditText) findViewById(R.id.address_text);

        String yourphone = phone.getText().toString();
        String youremail = email.getText().toString();
        String youraddress = address.getText().toString();

        Intent intent = new Intent(this, LastConfirmationActivity.class);
        Intent intent2 = getIntent();
        int curry_c = intent2.getIntExtra("CURRY_COUNT", 0);
        int fish_c = intent2.getIntExtra("FISH_COUNT", 0);
        int pasta_c = intent2.getIntExtra("PASTA_COUNT", 0);
        int pizza_c = intent2.getIntExtra("PIZZA_COUNT", 0);
        int salad_c = intent2.getIntExtra("SALAD_COUNT", 0);
        int soba_c = intent2.getIntExtra("SOBA_COUNT", 0);
        int steak_c = intent2.getIntExtra("STEAK_COUNT", 0);
        int sashimi_c = intent2.getIntExtra("SASHIMI_COUNT", 0);

        String p_number = intent.getStringExtra("PHONE_NUMBER");
        String m_number = intent.getStringExtra("EMAIL_ADDRESS");
        String a_number = intent.getStringExtra("ADDRESS");

        intent.putExtra("CURRY_COUNT", curry_c);
        intent.putExtra("FISH_COUNT", fish_c);
        intent.putExtra("PASTA_COUNT", pasta_c);
        intent.putExtra("PIZZA_COUNT", pizza_c);
        intent.putExtra("SALAD_COUNT", salad_c);
        intent.putExtra("SOBA_COUNT", soba_c);
        intent.putExtra("STEAK_COUNT", steak_c);
        intent.putExtra("SASHIMI_COUNT", sashimi_c);
        intent.putExtra("PHONE_NUMBER", yourphone);
        intent.putExtra("EMAIL_ADDRESS", youremail);
        intent.putExtra("ADDRESS", youraddress);
        if (!TextUtils.isEmpty(yourphone) && !TextUtils.isEmpty(youremail) && !TextUtils.isEmpty(youraddress)) {
            startActivity(intent);
        } else {
            TextView textView = (TextView) findViewById(R.id.write_text);
            textView.setText("空欄があります");
        }
    }

    public void onBuckButtonTapped(View view){
        finish();
    }
}
