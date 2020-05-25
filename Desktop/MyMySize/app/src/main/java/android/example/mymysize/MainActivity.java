package android.example.mymysize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    int n1=0;
    int n2=0;
    int n3=0;
    int cal = 0;
    int ope = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RadioGroup radio = (RadioGroup)findViewById(R.id.radioGroup);
        RadioGroup radio2 = (RadioGroup)findViewById(R.id.radioGroup2);
        radio.check(R.id.plus);
        radio2.check(R.id.times);
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

        radio2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.times:
                        ope = 0;
                        break;
                    case R.id.route:
                        ope = 1;
                        break;
                    case R.id.sin:
                        ope = 2;
                        break;
                    case R.id.cos:
                        ope = 3;
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
        EditText number3 = (EditText)findViewById(R.id.number3);
        n1 = Integer.parseInt(number1.getText().toString());
        n2 = Integer.parseInt(number2.getText().toString());
        n3 = Integer.parseInt(number3.getText().toString());

        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra("NUMBER1", n1);
        intent.putExtra("NUMBER2", n2);
        intent.putExtra("NUMBER3", n3);
        intent.putExtra("operation", cal);
        intent.putExtra("operation2", ope);

        startActivity(intent);
    }
}
