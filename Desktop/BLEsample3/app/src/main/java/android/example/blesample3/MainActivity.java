package android.example.blesample3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private BluetoothAdapter bluetoothAdapter;
    private String mUuid = "";
    private String mDeviceAddress = "";
    private BluetoothGatt bluetoothGatt = null;
    private Button mButton_Connect;
    private Button mButton_Disconnect;
    private Button startButton;
    private Button findButton;

    private int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ENABLEBLUETOOTH = 1; // Bluetooth機能の有効化要求時の識別コード
    private static final int REQUEST_CONNECTDEVICE = 2; // デバイス接続要求時の識別コード

    private final BluetoothGattCallback mGattcallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (BluetoothGatt.GATT_SUCCESS != status) {
                return;
            }
            if (BluetoothProfile.STATE_CONNECTED == newState) {    // 接続完了
                runOnUiThread(new Runnable() {
                    public void run() {
                        mButton_Disconnect.setEnabled(true);
                        findButton.setEnabled(true);
                    }
                });
                return;
            }
            if (BluetoothProfile.STATE_DISCONNECTED == newState) {
                bluetoothGatt.connect();
                return;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton_Connect = (Button) findViewById(R.id.button_connect);
        mButton_Connect.setOnClickListener(this);
        mButton_Disconnect = (Button) findViewById(R.id.button_disconnect);
        mButton_Disconnect.setOnClickListener(this);
        startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(this);
        findButton = (Button)findViewById(R.id.find_uuid_button);
        findButton.setOnClickListener(this);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothManager == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestBluetoothFeature();
        mButton_Connect.setEnabled(false);
        mButton_Disconnect.setEnabled(false);
        if (!mDeviceAddress.equals("")) {
            mButton_Connect.setEnabled(true);
        }
        mButton_Connect.callOnClick();
    }

    private void requestBluetoothFeature() {
        if (bluetoothAdapter.isEnabled()) {
            return;
        }
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLEBLUETOOTH);
    }

    @Override
    protected void onPause() {
        super.onPause();
        disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != bluetoothGatt) {
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLEBLUETOOTH: // Bluetooth有効化要求
                if (Activity.RESULT_CANCELED == resultCode) {
                    Log.d("Error", "Bluetooth does not turn on");
                    finish();    // アプリ終了宣言
                    return;
                }
                break;
            case REQUEST_CONNECTDEVICE: // デバイス接続要求
                String strDeviceName;
                if (Activity.RESULT_OK == resultCode) {
                    strDeviceName = data.getStringExtra("Name");
                    mDeviceAddress = data.getStringExtra("Address");
                    mUuid = data.getStringExtra("Uuid");
                } else {
                    strDeviceName = "";
                    mDeviceAddress = "";
                }
                ((TextView) findViewById(R.id.Device_Name)).setText(strDeviceName);
                ((TextView) findViewById(R.id.Device_Address)).setText(mDeviceAddress);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        BluetoothGattCharacteristic characteristic;
        if (mButton_Connect.getId() == v.getId()) {
            mButton_Connect.setEnabled(false);    // 接続ボタンの無効化（連打対策）
            connect();            // 接続
            return;
        }
        if (mButton_Disconnect.getId() == v.getId()) {
            mButton_Disconnect.setEnabled(false);    // 切断ボタンの無効化（連打対策）
            disconnect();            // 切断
            return;
        }
        if (startButton.getId() == v.getId()) {
            Intent intent = new Intent(this, ScanActivity.class);
            startActivity(intent);
        }
        if (findButton.getId() == v.getId()){
            Intent intent = new Intent(this, showPHP.class);
            intent.putExtra("Uuid", mUuid);
            startActivity(intent);
        }
    }

    private void connect() {
        if (mDeviceAddress.equals("")) {    // DeviceAddressが空の場合は処理しない
            return;
        }
        if (null != bluetoothGatt) {    // mBluetoothGattがnullでないなら接続済みか、接続中。
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(mDeviceAddress);
        bluetoothGatt = device.connectGatt(this, false, mGattcallback);
    }

    private void disconnect() {
        if (null == bluetoothGatt) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;
        mButton_Connect.setEnabled(true);
        mButton_Disconnect.setEnabled(false);
    }
}