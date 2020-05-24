package android.example.myjanken;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    int curry_count = 0;
    int fish_count = 0;
    int pasta_count = 0;
    int pizza_count = 0;
    int salad_count = 0;
    int soba_count = 0;
    int steak_count = 0;
    int sashimi_count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onSelectedMenu(View view) {
        TextView orderText = (TextView) findViewById(R.id.order_item);
        int id = view.getId();
        switch(id){
            case R.id.curry:
                orderText.setText("カレーを注文リストに入れました");
                curry_count++;
                break;
            case R.id.fish_and_chips:
                orderText.setText("フィッシュアンドチップスを注文リストに入れました");
                fish_count++;
                break;
            case R.id.pasta:
                orderText.setText("パスタを注文リストに入れました");
                pasta_count++;
                break;
            case R.id.pizza:
                orderText.setText("ピザを注文リストに入れました");
                pizza_count++;
                break;
            case R.id.salad:
                orderText.setText("サラダを注文リストに入れました");
                salad_count++;
                break;
            case R.id.soba:
                orderText.setText("そばを注文リストに入れました");
                soba_count++;
                break;
            case R.id.steak:
                orderText.setText("ステーキを注文リストに入れました");
                steak_count++;
                break;
            case R.id.sashimi:
                orderText.setText("さしみを注文リストに入れました");
                sashimi_count++;
                break;
            default:
                break;
        }
    }

    public void onOrderButtonTapped(View view){
        Intent intent = new Intent(this, SignActivity.class);
        intent.putExtra("CURRY_COUNT", curry_count);
        intent.putExtra("FISH_COUNT", fish_count);
        intent.putExtra("PASTA_COUNT", pasta_count);
        intent.putExtra("PIZZA_COUNT", pizza_count);
        intent.putExtra("SALAD_COUNT", salad_count);
        intent.putExtra("SOBA_COUNT", soba_count);
        intent.putExtra("STEAK_COUNT", steak_count);
        intent.putExtra("SASHIMI_COUNT", sashimi_count);
        if(curry_count == 0 && fish_count == 0 && pasta_count == 0 && pizza_count == 0 &&
            salad_count == 0 && soba_count == 0 && steak_count == 0 && sashimi_count == 0) {
            TextView textView = (TextView)findViewById(R.id.order_item);
            textView.setText("商品を選択してください");
        }else {
            startActivity(intent);
        }
    }

    public void onClearButtonTapped(View view){
        curry_count = 0;
        fish_count = 0;
        pasta_count = 0;
        pizza_count = 0;
        salad_count = 0;
        soba_count = 0;
        steak_count = 0;
        sashimi_count = 0;
    }
}
