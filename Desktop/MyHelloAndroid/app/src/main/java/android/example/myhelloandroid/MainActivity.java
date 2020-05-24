package android.example.myhelloandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickCat(View view){
        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText("猫(This is a Cat)");
    }

    public void onClickDeer(View view){
        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText("鹿(This is a Deer)");
    }

    public void onClickElephant(View view){
        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText("象(This is a Elephant)");
    }

    public void onClickGiraffe(View view){
        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText("麒麟(This is a Giraffe)");
    }

    public void onClickLion(View view){
        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText("獅子(This is a Lion)");
    }

    public void onClickTiger(View view){
        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText("虎(This is a Tiger)");
    }
}
