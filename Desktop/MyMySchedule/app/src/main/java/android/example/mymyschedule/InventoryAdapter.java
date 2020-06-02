package android.example.mymyschedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

public class InventoryAdapter extends RealmBaseAdapter<Inventory> {
    public InventoryAdapter(@Nullable OrderedRealmCollection<Inventory> data) {
        super(data);
    }

    private static class ViewHolder{
        TextView product_name;
        TextView product_inventory_count;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    android.R.layout.simple_list_item_2, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.product_name = convertView.findViewById(android.R.id.text1);
            viewHolder.product_inventory_count = convertView.findViewById(android.R.id.text2);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Inventory inventory = adapterData.get(position);
        viewHolder.product_name.setText(inventory.getProduct_name());
        viewHolder.product_inventory_count.setText(inventory.getProduct_inventory_count());
        return convertView;
    }
}
