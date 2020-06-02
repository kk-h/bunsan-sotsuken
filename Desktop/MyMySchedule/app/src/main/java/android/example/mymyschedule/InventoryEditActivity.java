package android.example.mymyschedule;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import io.realm.Realm;
import io.realm.RealmResults;

public class InventoryEditActivity extends AppCompatActivity {
    private Realm realm;
    EditText nameEdit;
    EditText numberEdit;
    EditText countEdit;
    EditText deliveryEdit;
    EditText detailEdit;
    Button delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_edit);
        realm = Realm.getDefaultInstance();
        nameEdit = (EditText) findViewById(R.id.editText);
        numberEdit = (EditText) findViewById(R.id.editText2);
        countEdit = (EditText) findViewById(R.id.editText3);
        deliveryEdit = (EditText) findViewById(R.id.editText5);
        detailEdit = (EditText) findViewById(R.id.editText7);
        delete = (Button)findViewById(R.id.delete);

        long inventoryId = getIntent().getLongExtra("inventory_id", -1);
        if (inventoryId != -1) {
            RealmResults<Inventory> results = realm.where(Inventory.class)
                    .equalTo("id", inventoryId).findAll();
            Inventory inventory = results.first();
            nameEdit.setText(inventory.getProduct_name());
            numberEdit.setText(inventory.getProduct_number());
            countEdit.setText(inventory.getProduct_inventory_count());
            deliveryEdit.setText(inventory.getDelivery());
            detailEdit.setText(inventory.getDetail());
            delete.setVisibility(View.VISIBLE);
        }else{
            delete.setVisibility(View.INVISIBLE);
        }
    }

    public void onSaveButtonTapped(View view) {
        long inventoryId = getIntent().getLongExtra("inventory_id", -1);
        if (inventoryId != -1) {
            final RealmResults<Inventory> results = realm.where(Inventory.class).equalTo("id", inventoryId).findAll();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Inventory inventory = results.first();
                    inventory.setProduct_name(nameEdit.getText().toString());
                    inventory.setProduct_number(numberEdit.getText().toString());
                    inventory.setProduct_inventory_count(countEdit.getText().toString());
                    inventory.setDelivery(deliveryEdit.getText().toString());
                    inventory.setDetail(detailEdit.getText().toString());
                }
            });
            Snackbar.make(findViewById(android.R.id.content), "アップデートしました", Snackbar.LENGTH_LONG)
                    .setAction("戻る", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    })
                    .setActionTextColor(Color.BLUE)
                    .show();
        } else {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Number maxId = realm.where(Inventory.class).max("id");
                    long nextId = 0;
                    if (maxId != null) {
                        nextId = maxId.longValue() + 1;
                    }
                    Inventory inventory = realm.createObject(Inventory.class, new Long(nextId));
                    inventory.setProduct_name(nameEdit.getText().toString());
                    inventory.setProduct_number(numberEdit.getText().toString());
                    inventory.setProduct_inventory_count(countEdit.getText().toString());
                    inventory.setDelivery(deliveryEdit.getText().toString());
                    inventory.setDetail(detailEdit.getText().toString());
                }
            });
            finish();
        }
    }

    public void onDeleteButtonTapped(View view){
        final long inventoryId = getIntent().getLongExtra("inventory_id", -1);
        if (inventoryId != -1){
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Inventory inventory = realm.where(Inventory.class)
                            .equalTo("id", inventoryId).findFirst();
                    inventory.deleteFromRealm();
                }
            });
        }
    }
}

