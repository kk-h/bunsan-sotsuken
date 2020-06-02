package android.example.mymyschedule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class SearchActivity extends AppCompatActivity {
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        String keyword = intent.getStringExtra("KEYWORD");

        TextView namet = (TextView) findViewById(R.id.name);
        TextView numbert = (TextView) findViewById(R.id.number);
        TextView inventoryt = (TextView) findViewById(R.id.inven);
        TextView deliveryt = (TextView) findViewById(R.id.delivery);
        TextView detailt = (TextView) findViewById(R.id.detail);

        realm = Realm.getDefaultInstance();
        RealmQuery<Inventory> query = realm.where(Inventory.class).equalTo("product_name", keyword);
        RealmResults<Inventory> result = query.findAll();

        String name = result.get(0).getProduct_name();
        String number = result.get(0).getProduct_number();
        String inventory = result.get(0).getProduct_inventory_count();
        String delivery = result.get(0).getDelivery();
        String detail = result.get(0).getDetail();

        namet.setText(name);
        numbert.setText(number);
        inventoryt.setText(inventory);
        deliveryt.setText(delivery);
        detailt.setText(detail);
    }
}