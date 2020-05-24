package android.example.mymysize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    int n1=0;
    int n2=0;
    int cal = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RadioGroup radio = (RadioGroup)findViewById(R.id.radioGroup);
        radio.check(R.id.plus);
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.plus:
                        cal = 0;
                        break;
                    case R.id.minus:
                        cal = 1;
                        break;
                    case R.id.multiply:
                        cal = 2;
                        break;
                    case R.id.divide:
                        cal = 3;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void onCalcurateButtonTapped(View view){
        EditText number1 = (EditText)findViewById(R.id.number1);
        EditText number2 = (EditText)findViewById(R.id.number2);
        n1 = Integer.parseInt(number1.getText().toString());
        n2 = Integer.parseInt(number2.getText().toString());

        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra("NUMBER1", n1);
        intent.putExtra("NUMBER2", n2);
        intent.putExtra("operation", cal);
        startActivity(intent);
    }
}
