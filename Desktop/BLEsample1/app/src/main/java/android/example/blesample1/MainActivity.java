package android.example.blesample1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Instrumentation;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private BluetoothAdapter bluetoothAdapter;
    private String mDeviceAddress = "";
    private BluetoothGatt bluetoothGatt = null;
    private Button mButton_Connect;
    private Button mButton_Disconnect;
    private Button startButton;
    private Button mButton_ReadChara1;
    private Button mButton_ReadChara2;
    private Button   mButton_WriteHello;
    private Button   mButton_WriteWorld;

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
                    }
                });
                return;
            }
            if (BluetoothProfile.STATE_DISCONNECTED == newState) {
                bluetoothGatt.connect();
                runOnUiThread(new Runnable() {
                    public void run() {
                        mButton_ReadChara1.setEnabled(false);
                        mButton_ReadChara2.setEnabled(false);
                    }
                });
                return;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (BluetoothGatt.GATT_SUCCESS != status) {
                return;
            }

            for (BluetoothGattService service : gatt.getServices()) {
                if ((null == service) || (null == service.getUuid())) {
                    continue;
                }
                if (service.getUuid() != null) {    // プライベートサービス
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mButton_ReadChara1.setEnabled(true);
                            mButton_ReadChara2.setEnabled(true);
                        }
                    });
                    continue;
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (BluetoothGatt.GATT_SUCCESS != status) {
                return;
            }
            if (characteristic.getUuid() != null) {
                byte[] byteChara = characteristic.getValue();
                ByteBuffer bb = ByteBuffer.wrap(byteChara);
                final String strChara = String.valueOf(bb.getShort());
                runOnUiThread(new Runnable() {
                    public void run() {
                        ((TextView) findViewById(R.id.textview_readchara1)).setText(strChara);
                    }
                });
                return;
            }
            if (characteristic.getUuid() != null) {
                final String strChara = characteristic.getStringValue(0);
                runOnUiThread(new Runnable() {
                    public void run() {
                        ((TextView) findViewById(R.id.textview_readchara2)).setText(strChara);
                    }
                });
                return;
            }
        }

        @Override
        public void onCharacteristicWrite( BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status ) {
            if( BluetoothGatt.GATT_SUCCESS != status ) {
                return;
            }
            if(characteristic.getUuid() != null) {
                runOnUiThread( new Runnable() {
                    public void run() {
                        mButton_WriteHello.setEnabled( true );
                        mButton_WriteWorld.setEnabled( true );
                    }
                } );
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
        mButton_ReadChara1 = (Button) findViewById(R.id.button_readchara1);
        mButton_ReadChara1.setOnClickListener(this);
        mButton_ReadChara2 = (Button) findViewById(R.id.button_readchara2);
        mButton_ReadChara2.setOnClickListener(this);
        mButton_WriteHello = (Button)findViewById( R.id.button_writehello );
        mButton_WriteHello.setOnClickListener( this );
        mButton_WriteWorld = (Button)findViewById( R.id.button_writeworld );
        mButton_WriteWorld.setOnClickListener( this );

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
        mButton_WriteHello.setEnabled( false );
        mButton_WriteWorld.setEnabled( false );
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
                } else {
                    strDeviceName = "";
                    mDeviceAddress = "";
                }
                ((TextView) findViewById(R.id.Device_Name)).setText(strDeviceName);
                ((TextView) findViewById(R.id.Device_Address)).setText(mDeviceAddress);
                ((TextView) findViewById(R.id.textview_readchara1)).setText("");
                ((TextView) findViewById(R.id.textview_readchara2)).setText("");
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        BluetoothGattCharacteristic characteristic = null;
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
        if (mButton_ReadChara1.getId() == v.getId()) {
            readCharacteristic(characteristic.getUuid(), characteristic.getUuid());
            return;
        }
        if (mButton_ReadChara2.getId() == v.getId()) {
            readCharacteristic(characteristic.getUuid(), characteristic.getUuid());
            return;
        }
        if( mButton_WriteHello.getId() == v.getId() )
        {
            mButton_WriteHello.setEnabled( false );    // 書き込みボタンの無効化（連打対策）
            mButton_WriteWorld.setEnabled( false );    // 書き込みボタンの無効化（連打対策）
            writeCharacteristic( characteristic.getUuid(), characteristic.getUuid(), "Hello" );
            return;
        }
        if( mButton_WriteWorld.getId() == v.getId() )
        {
            mButton_WriteHello.setEnabled( false );    // 書き込みボタンの無効化（連打対策）
            mButton_WriteWorld.setEnabled( false );    // 書き込みボタンの無効化（連打対策）
            writeCharacteristic( characteristic.getUuid(), characteristic.getUuid(), "World" );
            return;
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
        mButton_ReadChara1.setEnabled(false);
        mButton_ReadChara2.setEnabled(false);
        mButton_WriteHello.setEnabled( false );
        mButton_WriteWorld.setEnabled( false );
    }

    private void readCharacteristic(UUID uuid_service, UUID uuid_characteristic) {
        if (bluetoothGatt == null) {
            return;
        }
        BluetoothGattCharacteristic blecharacter = bluetoothGatt.getService(uuid_service).getCharacteristic(uuid_characteristic);
        bluetoothGatt.readCharacteristic(blecharacter);
    }

    private void writeCharacteristic( UUID uuid_service, UUID uuid_characteristic, String string ) {
        if( null == bluetoothGatt )
        {
            return;
        }
        BluetoothGattCharacteristic blechar = bluetoothGatt.getService( uuid_service ).getCharacteristic( uuid_characteristic );
        blechar.setValue( string );
        bluetoothGatt.writeCharacteristic( blechar );
    }
}