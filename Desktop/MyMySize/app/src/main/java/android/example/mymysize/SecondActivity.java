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
        TextView textView1 = (TextView)findViewById(R.id.resultNumber2);

        Intent intent1 = getIntent();
        int n1 = intent1.getIntExtra("NUMBER1", 0);
        int n2 = intent1.getIntExtra("NUMBER2", 0);
        int n3 = intent1.getIntExtra("NUMBER3", 0);
        int cal = intent1.getIntExtra("operation", 0);
        int ope = intent1.getIntExtra("operation2", 0);

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

        switch (ope){
            case 0:
                textView1.setText(String.valueOf(n3*n3));
                break;
            case 1:
                textView1.setText(String.valueOf(Math.sqrt((float)n3)));
                break;
            case 2:
                textView1.setText(String.valueOf(Math.sin((float)n3)));
                break;
            case 3:
                textView1.setText(String.valueOf(Math.cos((float)n3)));
                break;
            default:
                break;
        }
    }

    public void onBuckButtonTapped(View view){
        finish();
    }
}
