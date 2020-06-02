package android.example.mymyschedule;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    private Realm realm;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, InventoryEditActivity.class));
            }
        });
        realm = Realm.getDefaultInstance();

        listView = (ListView)findViewById(R.id.listView);
        RealmResults<Inventory> inventories = realm.where(Inventory.class).findAll();
        InventoryAdapter adapter = new InventoryAdapter(inventories);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Inventory inventory = (Inventory)parent.getItemAtPosition(position);
                startActivity(new Intent(MainActivity.this, InventoryEditActivity.class)
                        .putExtra("inventory_id", inventory.getId()));
            }
        });
    }

    public void onSearchTapped(View view){
        TextView textView = (TextView)findViewById(R.id.searchText);
        String keyword = textView.getText().toString();
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("KEYWORD", keyword);
        startActivity(intent);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        realm.close();
    }
}
