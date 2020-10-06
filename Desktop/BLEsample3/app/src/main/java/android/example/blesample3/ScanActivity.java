package android.example.blesample3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ScanActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    static class ScanAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mDeviceList;
        private LayoutInflater mInflator;

        public ScanAdapter(Activity activity) {
            super();
            mDeviceList = new ArrayList<BluetoothDevice>();
            mInflator = activity.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mDeviceList.contains(device)) {
                mDeviceList.add(device);
                notifyDataSetChanged();
            }
        }

        public void clear() {
            mDeviceList.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mDeviceList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDeviceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private static class ViewHolder {
            TextView deviceName;
            TextView deviceAddress;
            TextView deviceUuid;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflator.inflate(R.layout.activity_scan, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) convertView.findViewById(R.id.Device_Address);
                viewHolder.deviceName = (TextView) convertView.findViewById(R.id.Device_Name);
                viewHolder.deviceUuid = (TextView)convertView.findViewById(R.id.Device_Uuid);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            BluetoothDevice device = mDeviceList.get(position);
            String deviceName = device.getName();
            if (deviceName != null && 0 != deviceName.length()) {
                viewHolder.deviceName.setText(deviceName);
            } else {
                viewHolder.deviceName.setText("There is no device.");
            }
            viewHolder.deviceAddress.setText(device.getAddress());
            viewHolder.deviceUuid.setText(device.getUuids().toString());
            return convertView;
        }
    }

    private BluetoothAdapter bluetoothAdapter;
    private boolean mScanning = false;
    private Handler handler;
    private static final long SCAN_PERIOD = 10000;
    private ScanAdapter scanAdapter;
    private static final int REQUEST_ENABLEBLUETOOTH = 1;

    private ScanCallback leScanCallback = new ScanCallback() {
        public void onScanResult(int callbackType, final ScanResult result) {
            super.onScanResult(callbackType, result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    scanAdapter.addDevice(result.getDevice());
                    scanAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        setResult(Activity.RESULT_CANCELED);
        scanAdapter = new ScanAdapter(this);
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(scanAdapter);
        listView.setOnItemClickListener((AdapterView.OnItemClickListener) this);

        handler = new Handler();

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            Log.d("Error", "Device is not supported.");
            finish();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestBluetoothFeature();
        startScan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScan();
    }

    private void requestBluetoothFeature() {
        if (bluetoothAdapter.isEnabled()) {
            return;
        }
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLEBLUETOOTH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLEBLUETOOTH: // Bluetooth有効化要求
                if (Activity.RESULT_CANCELED == resultCode) {
                    Log.d("bluetooth", "Bluetooth does not turn on");
                    finish();
                    return;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startScan() {
        scanAdapter.clear();
        final android.bluetooth.le.BluetoothLeScanner scanner = bluetoothAdapter.getBluetoothLeScanner();
        if (null == scanner) {
            return;
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScanning = false;
                scanner.stopScan(leScanCallback);
                invalidateOptionsMenu();
                Toast.makeText(ScanActivity.this, R.string.noBLE, Toast.LENGTH_SHORT).show();
            }
        }, SCAN_PERIOD);

        mScanning = true;
        scanner.startScan(leScanCallback);
        invalidateOptionsMenu();
    }

    private void stopScan() {
        handler.removeCallbacksAndMessages(null);
        android.bluetooth.le.BluetoothLeScanner scanner = bluetoothAdapter.getBluetoothLeScanner();
        if (null == scanner) {
            return;
        }
        mScanning = false;
        scanner.stopScan(leScanCallback);
        invalidateOptionsMenu();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice bluetoothDevice = (BluetoothDevice) scanAdapter.getItem(position);
        if (bluetoothDevice == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("Name", bluetoothDevice.getName());
        intent.putExtra("Address", bluetoothDevice.getAddress());
        intent.putExtra("Uuid", bluetoothDevice.getUuids());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate( R.menu.activity_scan, menu );
        if( !mScanning ) {
            menu.findItem( R.id.menuitem_stop ).setVisible( false );
            menu.findItem( R.id.menuitem_scan ).setVisible( true );
            menu.findItem( R.id.menuitem_progress ).setActionView( null );
        }
        else {
            menu.findItem( R.id.menuitem_stop ).setVisible( true );
            menu.findItem( R.id.menuitem_scan ).setVisible( false );
            menu.findItem( R.id.menuitem_progress ).setActionView( R.layout.progress );
        }
        return true;
    }

    // オプションメニューのアイテム選択時の処理
    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch( item.getItemId() ) {
            case R.id.menuitem_scan:
                startScan();    // スキャンの開始
                break;
            case R.id.menuitem_stop:
                stopScan();    // スキャンの停止
                break;
        }
        return true;
    }
}


