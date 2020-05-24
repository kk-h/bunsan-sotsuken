package android.example.mysize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class HeightActivity extends AppCompatActivity {
    public static final String HEIGHT = "HEIGHT";
    private TextView mHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_height);

        mHeight = (TextView) findViewById(R.id.height);

        SharedPreferences pref
                = PreferenceManager.getDefaultSharedPreferences(this);
        int height = pref.getInt(HEIGHT, 160);
        mHeight.setText(String.valueOf(height));

        /// スピナーの処理
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // 一覧から１つを選んだ時の処理
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,
                                       long id) {
                // 選ばれた項目を取得してTextViewに表示
                Spinner spinner = (Spinner) parent;
                String item = (String) spinner.getSelectedItem();
                if (!item.isEmpty()) {
                    mHeight.setText(item);
                }
            }

            // 一覧で何も選択されなかった時の処理
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /// シークバーの処理
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(height);
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    // シークバーの値が変更された
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress,
                                                  boolean fromUser) {
                        // 変更された値をintから文字列に変換してTextViewに表示
                        String value = String.valueOf(progress);
                        mHeight.setText(value);
                    }

                    // シークバーのスライド開始
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    // シークバーのスライド終了
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

        /// ラジオボタンの処理
        RadioGroup radio = (RadioGroup) findViewById(R.id.radioGroup);
        radio.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    // ラジオグループのチェック状態が変更された時に呼び出されます
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // 選択されたラジオボタンのリソースIDより、ラジオボタンを取得する
                        RadioButton radioButton =
                                (RadioButton) findViewById(checkedId);
                        String value = radioButton.getText().toString();
                        mHeight.setText(value);
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(HEIGHT, Integer.parseInt(mHeight.getText().toString()));
        editor.commit();
    }
}

