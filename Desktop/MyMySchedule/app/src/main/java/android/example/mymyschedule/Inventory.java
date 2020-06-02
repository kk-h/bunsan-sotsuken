package android.example.mymyschedule;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Inventory extends RealmObject {
    @PrimaryKey
    private long id;
    private String product_name;
    private String product_number;
    private String product_inventory_count;
    private String delivery;
    private String detail;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_number() {
        return product_number;
    }

    public void setProduct_number(String product_number) {
        this.product_number = product_number;
    }

    public String getProduct_inventory_count() {
        return product_inventory_count;
    }

    public void setProduct_inventory_count(String product_inventory_count) {
        this.product_inventory_count = product_inventory_count;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}

