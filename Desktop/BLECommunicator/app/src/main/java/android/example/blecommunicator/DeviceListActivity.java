package android.example.blecommunicator;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DeviceListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    static class DeviceListAdapter extends BaseAdapter{
        private ArrayList<BluetoothDevice> mDeviceList;
        private LayoutInflater mInflater;

        public DeviceListAdapter(Activity activity){
            super();
            mDeviceList = new ArrayList<BluetoothDevice>();
            mInflater = activity.getLayoutInflater();
        }

        // リストへの追加
        public void addDevice(BluetoothDevice device){
            if(!mDeviceList.contains(device)){ // 加えられていなければ加える
                mDeviceList.add(device);
                notifyDataSetChanged(); // ListViewの更新
            }
        }

        public void clear(){
            mDeviceList.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount(){
            return mDeviceList.size();
        }

        @Override
        public Object getItem(int position){
            return mDeviceList.get(position);
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        static class ViewHolder{
            TextView deviceName;
            TextView deviceAddress;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            ViewHolder viewHolder;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.listitem_device, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView)convertView.findViewById(R.id.textview_deviceaddress);
                viewHolder.deviceName = (TextView)convertView.findViewById(R.id.textview_devicename);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            BluetoothDevice device = mDeviceList.get(position);
            String deviceName = device.getName();
            if(deviceName == null&&0<deviceName.length()){
                viewHolder.deviceName.setText(deviceName);
            }else{
                viewHolder.deviceName.setText(R.string.unknown_device);
            }
            viewHolder.deviceAddress.setText(device.getAddress());
            return convertView;
        }
    }

    private static final int REQUEST_ENABLEBLUETOOTH = 1; // Bluetooth機能の有効化要求時の識別コード
    private static final long SCAN_PERIOD = 10000;    // スキャン時間。単位はミリ秒。
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private BluetoothAdapter mBluetoothAdapter; // BluetoothAdapter : Bluetooth処理で必要
    private DeviceListAdapter mDeviceListAdapter; // リストビューの内容
    private Handler mHandler; // UIスレッド操作ハンドラ : 「一定時間後にスキャンをやめる処理」で必要
    private boolean mScanning = false; // スキャン中かどうかのフラグ

    private ScanCallback mLeScanCallback = new ScanCallback() {// スキャンに成功（アドバタイジングは一定間隔で常に発行されているため、本関数は一定間隔で呼ばれ続ける）
        @Override
        public void onScanResult( int callbackType, final ScanResult result ) {
            super.onScanResult( callbackType, result );
            runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    mDeviceListAdapter.addDevice( result.getDevice() );
                }
            } );
        }
        // スキャンに失敗
        @Override
        public void onScanFailed( int errorCode ) {
            super.onScanFailed( errorCode );
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        setResult(Activity.RESULT_CANCELED);
        mDeviceListAdapter = new DeviceListAdapter( this ); // ビューアダプターの初期化
        ListView listView = (ListView)findViewById( R.id.devicelist );    // リストビューの取得
        listView.setAdapter( mDeviceListAdapter );    // リストビューにビューアダプターをセット
        listView.setOnItemClickListener(this); // クリックリスナーオブジェクトのセット

        // UIスレッド操作ハンドラの作成（「一定時間後にスキャンをやめる処理」で使用する）
        mHandler = new Handler();

        // Bluetoothアダプタの取得
        BluetoothManager bluetoothManager = (BluetoothManager)getSystemService( Context.BLUETOOTH_SERVICE );
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if( null == mBluetoothAdapter ) {    // デバイス（＝スマホ）がBluetoothをサポートしていない
            Toast.makeText( this, R.string.bluetooth_is_not_supported, Toast.LENGTH_SHORT ).show();
            finish();    // アプリ終了宣言
            return;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        requestBluetoothFeature();
        startScan();
    }

    @Override
    protected void onPause(){
        super.onPause();
        stopScan();
    }

    private void requestBluetoothFeature(){
        if(mBluetoothAdapter.isEnabled()){
            return;
        }
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); //有効化要求
        startActivityForResult(enableBtIntent, REQUEST_ENABLEBLUETOOTH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case REQUEST_ENABLEBLUETOOTH:
                if(Activity.RESULT_CANCELED == resultCode){
                    Toast.makeText( this, R.string.bluetooth_is_not_working, Toast.LENGTH_SHORT ).show();
                    finish();    // アプリ終了宣言
                    return;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startScan(){
        mDeviceListAdapter.clear(); //リストビューを空に
        final android.bluetooth.le.BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();//スキャナーの取得。Runnableでも使えるようにfinalに
        if(scanner == null){
            return;
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScanning = false;
                scanner.stopScan(mLeScanCallback);
                invalidateOptionsMenu();
            }
        }, SCAN_PERIOD);
        mScanning = true;
        scanner.startScan(mLeScanCallback);
        invalidateOptionsMenu();
    }

    private void stopScan(){
        mHandler.removeCallbacksAndMessages(null);//ハンドラーの削除
        android.bluetooth.le.BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
        if(scanner == null){
            return;
        }
        mScanning = false;
        scanner.stopScan(mLeScanCallback);
        invalidateOptionsMenu();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device = (BluetoothDevice)mDeviceListAdapter.getItem(position);
        if(device == null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(EXTRAS_DEVICE_ADDRESS, device.getAddress());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_device_list, menu);
        if(!mScanning){
            menu.findItem(R.id.menuitem_stop).setVisible(false);
            menu.findItem(R.id.menuitem_scan).setVisible(true);
            menu.findItem(R.id.menuitem_progress).setActionView(null);
        }else{
            menu.findItem(R.id.menuitem_stop).setVisible(true);
            menu.findItem(R.id.menuitem_scan).setVisible(false);
            menu.findItem(R.id.menuitem_progress).setActionView(R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menuitem_scan:
                startScan();
                break;
            case R.id.menuitem_stop:
                stopScan();
                break;
        }
        return true;
    }
}