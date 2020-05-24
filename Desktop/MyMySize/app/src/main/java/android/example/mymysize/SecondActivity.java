package android.example.mymysize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        TextView textView = (TextView) findViewById(R.id.resultNumber);

        Intent intent1 = getIntent();
        int n1 = intent1.getIntExtra("NUMBER1", 0);
        int n2 = intent1.getIntExtra("NUMBER2", 0);
        int cal = intent1.getIntExtra("operation", 0);

        switch (cal){
            case 0:
                 textView.setText(String.valueOf(n1+n2));
                break;
            case 1:
                textView.setText(String.valueOf(n1-n2));
                break;
            case 2:
                textView.setText(String.valueOf(n1*n2));
                break;
            case 3:
                textView.setText(String.valueOf(n1/n2));
                break;
            default:
                break;
        }
    }

    public void onBuckButtonTapped(View view){
        finish();
    }
}
