package android.example.myjanken;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LastConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_confirmation);

        Intent intent = getIntent();
        int curry_c = intent.getIntExtra("CURRY_COUNT", 0);
        int fish_c = intent.getIntExtra("FISH_COUNT", 0);
        int pasta_c = intent.getIntExtra("PASTA_COUNT", 0);
        int pizza_c = intent.getIntExtra("PIZZA_COUNT", 0);
        int salad_c = intent.getIntExtra("SALAD_COUNT", 0);
        int soba_c = intent.getIntExtra("SOBA_COUNT", 0);
        int steak_c = intent.getIntExtra("STEAK_COUNT", 0);
        int sashimi_c = intent.getIntExtra("SASHIMI_COUNT", 0);
        String p_number = intent.getStringExtra("PHONE_NUMBER");
        String m_number = intent.getStringExtra("EMAIL_ADDRESS");
        String a_number = intent.getStringExtra("ADDRESS");

        TextView textView1 = (TextView)findViewById(R.id.curry_count);
        TextView textView2 = (TextView)findViewById(R.id.fish_count);
        TextView textView3 = (TextView)findViewById(R.id.pasta_count);
        TextView textView4 = (TextView)findViewById(R.id.pizza_count);
        TextView textView5 = (TextView)findViewById(R.id.salad_count);
        TextView textView6 = (TextView)findViewById(R.id.soba_count);
        TextView textView7 = (TextView)findViewById(R.id.steak_count);
        TextView textView8 = (TextView)findViewById(R.id.sashimi_count);
        TextView textView9 = (TextView)findViewById(R.id.phone_number);
        TextView textView10 = (TextView)findViewById(R.id.mail_address);
        TextView textView11 = (TextView)findViewById(R.id.your_address);

        textView1.setText(String.valueOf(curry_c));
        textView2.setText(String.valueOf(fish_c));
        textView3.setText(String.valueOf(pasta_c));
        textView4.setText(String.valueOf(pizza_c));
        textView5.setText(String.valueOf(salad_c));
        textView6.setText(String.valueOf(soba_c));
        textView7.setText(String.valueOf(steak_c));
        textView8.setText(String.valueOf(sashimi_c));
        textView9.setText(p_number);
        textView10.setText(m_number);
        textView11.setText(a_number);
    }

    public void onLastConfirmationButtonTapped(View view){
        Intent intent = new Intent(this, FinishActivity.class);
        startActivity(intent);
    }

    public void onBuckButtonTapped(View view){
        finish();
    }
}
