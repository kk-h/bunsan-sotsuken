package jp.sacredsanctuary.bledemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import jp.sacredsanctuary.bledemo.R;
import jp.sacredsanctuary.bledemo.model.BluetoothDeviceData;
import jp.sacredsanctuary.bledemo.view.ViewHolder;

public class BluetoothDeviceListAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final String ClassName = BluetoothDeviceListAdapter.class.getSimpleName();
    private List<BluetoothDeviceData> mDeviceDataList;

    /**
     * Create a new BluetoothDeviceListAdapter.
     */
    public BluetoothDeviceListAdapter() {
        this.mDeviceDataList = new ArrayList<>();
    }

    public void setItems(List<BluetoothDeviceData> deviceDataList) {
        mDeviceDataList.clear();
        mDeviceDataList.addAll(deviceDataList);
        notifyItemRangeInserted(0, getItemCount());
    }

    public void clearItems() {
        mDeviceDataList.clear();
    }

    public List<BluetoothDeviceData> getAllItem() {
        return mDeviceDataList;
    }

    protected void onItemClicked(@NonNull BluetoothDeviceData data) {
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View inflate = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(inflate);
        inflate.setOnClickListener(v -> {
            final int position = holder.getAdapterPosition();
            onItemClicked(mDeviceDataList.get(position));
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getDeviceName().setText(mDeviceDataList.get(
                position).getBluetoothDevice().getName());
        holder.geDeviceHardwareAddress().setText(mDeviceDataList.get(
                position).getBluetoothDevice().getAddress());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return (mDeviceDataList != null) ? mDeviceDataList.size() : 0;
    }
}
