package android.example.blecommunicator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int REQUEST_ENABLEBLUETOOTH = 1;// Bluetooth機能の有効化要求時の識別コード
    private static final int REQUEST_CONNECTDEVICE = 2; // デバイス接続要求時の識別コード
    private BluetoothAdapter mBluetoothAdapter;// BluetoothAdapter : Bluetooth処理で必要
    private String mDeviceAddress = "";    // デバイスアドレス
    private BluetoothGatt mBluetoothGatt = null;    // Gattサービスの検索、キャラスタリスティックの読み書き
    private Button mButton_Connect;    // 接続ボタン
    private Button mButton_Disconnect;    // 切断ボタン
    private static final UUID UUID_SERVICE_PRIVATE         = UUID.fromString( "FF6B1160-8FE6-11E7-ABC4-CEC278B6B50A" );
    private static final UUID UUID_CHARACTERISTIC_PRIVATE1 = UUID.fromString( "FF6B1426-8FE6-11E7-ABC4-CEC278B6B50A" );
    private static final UUID UUID_CHARACTERISTIC_PRIVATE2 = UUID.fromString( "FF6B1548-8FE6-11E7-ABC4-CEC278B6B50A" );
    private Button   mButton_ReadChara1;    // キャラクタリスティック１の読み込みボタン
    private Button   mButton_ReadChara2;    // キャラクタリスティック２の読み込みボタン
    private static final UUID UUID_NOTIFY                  = UUID.fromString( "00002902-0000-1000-8000-00805f9b34fb" );
    private CheckBox mCheckBox_NotifyChara1;    // キャラクタリスティック１の変更通知ON/OFFチェックボックス
    private Button   mButton_WriteHello;        // キャラクタリスティック２への「Hello」書き込みボタン
    private Button   mButton_WriteWorld;        // キャラクタリスティック２への「World」書き込みボタン

    private final BluetoothGattCallback mGattcallback = new BluetoothGattCallback() {// 接続状態変更（connectGatt()の結果として呼ばれる。）
        @Override
        public void onConnectionStateChange( BluetoothGatt gatt, int status, int newState ) {
            if( BluetoothGatt.GATT_SUCCESS != status ) {
                return;
            }
            if( BluetoothProfile.STATE_CONNECTED == newState ) {    // 接続完了
                runOnUiThread( new Runnable() {
                    public void run() {// GUIアイテムの有効無効の設定
                        mButton_Disconnect.setEnabled( true );// 切断ボタンを有効にする
                    }
                } );
                return;
            }
            if( BluetoothProfile.STATE_DISCONNECTED == newState ) {    // 切断完了（接続可能範囲から外れて切断された）
                mBluetoothGatt.connect();// 接続可能範囲に入ったら自動接続するために、mBluetoothGatt.connect()を呼び出す。
                runOnUiThread(new Runnable() {
                    public void run() {
                        mButton_ReadChara1.setEnabled( false );
                        mButton_ReadChara2.setEnabled( false );
                        mCheckBox_NotifyChara1.setEnabled( false );
                        mButton_WriteHello.setEnabled( false );
                        mButton_WriteWorld.setEnabled( false );
                    }
                });
                return;
            }
        }

        @Override
        public void onServicesDiscovered( BluetoothGatt gatt, int status ){// サービス検索が完了したときの処理（mBluetoothGatt.discoverServices()の結果として呼ばれる。）
            if( BluetoothGatt.GATT_SUCCESS != status ) {
                return;
            }
            for( BluetoothGattService service : gatt.getServices() ) {// 発見されたサービスのループ
                if( ( null == service ) || ( null == service.getUuid() ) ) {// サービスごとに個別の処理
                    continue;
                }
                if( UUID_SERVICE_PRIVATE.equals( service.getUuid() ) ) {    // プライベートサービス
                    runOnUiThread( new Runnable() {
                        public void run() {// GUIアイテムの有効無効の設定
                            mButton_ReadChara1.setEnabled( true );
                            mButton_ReadChara2.setEnabled( true );
                            mCheckBox_NotifyChara1.setEnabled( true );
                            mButton_WriteHello.setEnabled( true );
                            mButton_WriteWorld.setEnabled( true );
                        }
                    } );
                    continue;
                }
            }
        }


        @Override
        public void onCharacteristicRead( BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status ) {// キャラクタリスティックが読み込まれたときの処理
            if( BluetoothGatt.GATT_SUCCESS != status ) {
                return;
            }
            // キャラクタリスティックごとに個別の処理
            if( UUID_CHARACTERISTIC_PRIVATE1.equals( characteristic.getUuid() ) ) {    // キャラクタリスティック１：データサイズは、2バイト（数値を想定。0～65,535）
                byte[]       byteChara = characteristic.getValue();
                ByteBuffer bb        = ByteBuffer.wrap( byteChara );
                final String strChara  = String.valueOf( bb.getShort() );
                runOnUiThread( new Runnable() {
                    public void run() {
                        ( (TextView)findViewById( R.id.textview_readchara1 ) ).setText( strChara );// GUIアイテムへの反映
                    }
                } );
                return;
            }
            if( UUID_CHARACTERISTIC_PRIVATE2.equals( characteristic.getUuid() ) ) {    // キャラクタリスティック２：データサイズは、8バイト（文字列を想定。半角文字8文字）
                final String strChara = characteristic.getStringValue( 0 );
                runOnUiThread( new Runnable() {
                    public void run() {
                        ( (TextView)findViewById( R.id.textview_readchara2 ) ).setText( strChara );// GUIアイテムへの反映
                    }
                } );
                return;
            }
        }

        @Override
        public void onCharacteristicChanged( BluetoothGatt gatt, BluetoothGattCharacteristic characteristic ) {// キャラクタリスティック変更が通知されたときの処理
            // キャラクタリスティックごとに個別の処理
            if( UUID_CHARACTERISTIC_PRIVATE1.equals( characteristic.getUuid() ) ) {    // キャラクタリスティック１：データサイズは、2バイト（数値を想定。0～65,535）
                byte[]       byteChara = characteristic.getValue();
                ByteBuffer   bb        = ByteBuffer.wrap( byteChara );
                final String strChara  = String.valueOf( bb.getShort() );
                runOnUiThread( new Runnable() {
                    public void run()
                    {
                        ( (TextView)findViewById( R.id.textview_notifychara1 ) ).setText( strChara ); // GUIアイテムへの反映
                    }
                } );
                return;
            }
        }
        @Override
        public void onCharacteristicWrite( BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status ) {
            if (BluetoothGatt.GATT_SUCCESS != status) {
                return;
            }
            // キャラクタリスティックごとに個別の処理
            if (UUID_CHARACTERISTIC_PRIVATE2.equals(characteristic.getUuid())) {    // キャラクタリスティック２：データサイズは、8バイト（文字列を想定。半角文字8文字）
                runOnUiThread(new Runnable() {
                    public void run() {
                        // GUIアイテムの有効無効の設定
                        // 書き込みボタンを有効にする
                        mButton_WriteHello.setEnabled(true);
                        mButton_WriteWorld.setEnabled(true);
                    }
                });
                return;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton_Connect = (Button)findViewById( R.id.button_connect );
        mButton_Connect.setOnClickListener( this );
        mButton_Disconnect = (Button)findViewById( R.id.button_disconnect );
        mButton_Disconnect.setOnClickListener( this );

        mButton_ReadChara1 = (Button)findViewById( R.id.button_readchara1 );
        mButton_ReadChara1.setOnClickListener( this );
        mButton_ReadChara2 = (Button)findViewById( R.id.button_readchara2 );
        mButton_ReadChara2.setOnClickListener( this );

        mCheckBox_NotifyChara1 = (CheckBox)findViewById( R.id.checkbox_notifychara1 );
        mCheckBox_NotifyChara1.setOnClickListener( this );

        mButton_WriteHello = (Button)findViewById( R.id.button_writehello );
        mButton_WriteHello.setOnClickListener( this );
        mButton_WriteWorld = (Button)findViewById( R.id.button_writeworld );
        mButton_WriteWorld.setOnClickListener( this );

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_is_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if(mBluetoothAdapter == null){
            Toast.makeText( this, R.string.bluetooth_is_not_supported, Toast.LENGTH_SHORT ).show();
            finish();    // アプリ終了宣言
            return;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        requestBluetoothFeature();// Android端末のBluetooth機能の有効化要求
        // GUIアイテムの有効無効の設定
        mButton_Connect.setEnabled( false );
        mButton_Disconnect.setEnabled( false );
        mButton_ReadChara1.setEnabled( false );
        mButton_ReadChara2.setEnabled( false );
        mCheckBox_NotifyChara1.setChecked( false );
        mCheckBox_NotifyChara1.setEnabled( false );
        mButton_WriteHello.setEnabled( false );
        mButton_WriteWorld.setEnabled( false );

        if( !mDeviceAddress.equals( "" ) ) {
            mButton_Connect.setEnabled( true );// デバイスアドレスが空でなければ、接続ボタンを有効にする。
        }
        mButton_Connect.callOnClick();// 接続ボタンを押す
    }

    @Override
    protected void onPause() {// 別のアクティビティ（か別のアプリ）に移行したことで、バックグラウンドに追いやられた時
        super.onPause();
        disconnect();// 切断
    }

    @Override
    protected void onDestroy() {// アクティビティの終了直前
        super.onDestroy();
        if( null != mBluetoothGatt ) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    public void requestBluetoothFeature(){
        if(mBluetoothAdapter.isEnabled()){
            return;
        }
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLEBLUETOOTH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case REQUEST_ENABLEBLUETOOTH:
                if(Activity.RESULT_CANCELED == resultCode){
                    // 有効にされなかった
                    Toast.makeText( this, R.string.bluetooth_is_not_working, Toast.LENGTH_SHORT ).show();
                    finish();    // アプリ終了宣言
                    return;
                }
                break;
            case REQUEST_CONNECTDEVICE: // デバイス接続要求
                String strDeviceName;
                if( Activity.RESULT_OK == resultCode ) {
                    // デバイスリストアクティビティからの情報の取得
                    strDeviceName = data.getStringExtra( DeviceListActivity.EXTRAS_DEVICE_NAME );
                    mDeviceAddress = data.getStringExtra( DeviceListActivity.EXTRAS_DEVICE_ADDRESS );
                } else {
                    strDeviceName = "";
                    mDeviceAddress = "";
                }
                ( (TextView)findViewById( R.id.textview_devicename ) ).setText( strDeviceName );
                ( (TextView)findViewById( R.id.textview_deviceaddress ) ).setText( mDeviceAddress );
                ( (TextView)findViewById( R.id.textview_readchara1 ) ).setText( "" );
                ( (TextView)findViewById( R.id.textview_readchara2 ) ).setText( "" );
                ( (TextView)findViewById( R.id.textview_notifychara1 ) ).setText( "" );
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menuitem_search:
                Intent devicelistactivityIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(devicelistactivityIntent, REQUEST_CONNECTDEVICE);
                return true;
        }
        return false;
    }

    @Override
    public void onClick( View v ) {
        if( mButton_Connect.getId() == v.getId() ) {
            mButton_Connect.setEnabled( false );    // 接続ボタンの無効化（連打対策）
            connect();            // 接続
            return;
        }
        if( mButton_Disconnect.getId() == v.getId() ) {
            mButton_Disconnect.setEnabled( false );    // 切断ボタンの無効化（連打対策）
            disconnect();            // 切断
            return;
        }
        if( mButton_ReadChara1.getId() == v.getId() ) {
            readCharacteristic( UUID_SERVICE_PRIVATE, UUID_CHARACTERISTIC_PRIVATE1 );
            return;
        }
        if( mButton_ReadChara2.getId() == v.getId() ) {
            readCharacteristic( UUID_SERVICE_PRIVATE, UUID_CHARACTERISTIC_PRIVATE2 );
            return;
        }
        if( mCheckBox_NotifyChara1.getId() == v.getId() ) {
            setCharacteristicNotification( UUID_SERVICE_PRIVATE, UUID_CHARACTERISTIC_PRIVATE1, mCheckBox_NotifyChara1.isChecked() );
            return;
        }
        if( mButton_WriteHello.getId() == v.getId() ) {
            mButton_WriteHello.setEnabled( false );    // 書き込みボタンの無効化（連打対策）
            mButton_WriteWorld.setEnabled( false );    // 書き込みボタンの無効化（連打対策）
            writeCharacteristic( UUID_SERVICE_PRIVATE, UUID_CHARACTERISTIC_PRIVATE2, "Hello" );
            return;
        }
        if( mButton_WriteWorld.getId() == v.getId() ) {
            mButton_WriteHello.setEnabled( false );    // 書き込みボタンの無効化（連打対策）
            mButton_WriteWorld.setEnabled( false );    // 書き込みボタンの無効化（連打対策）
            writeCharacteristic( UUID_SERVICE_PRIVATE, UUID_CHARACTERISTIC_PRIVATE2, "World" );
            return;
        }
    }

    private void connect(){// 接続
        if( mDeviceAddress.equals( "" ) ) {    // DeviceAddressが空の場合は処理しない
            return;
        }
        if( null != mBluetoothGatt ) {    // mBluetoothGattがnullでないなら接続済みか、接続中。
            return;
        }
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice( mDeviceAddress );// 接続
        mBluetoothGatt = device.connectGatt( this, false, mGattcallback );
    }

    private void disconnect() {// 切断
        if( null == mBluetoothGatt ) {
            return;
        }
        // 切断
        //   mBluetoothGatt.disconnect()ではなく、mBluetoothGatt.close()しオブジェクトを解放する。
        //   理由：「ユーザーの意思による切断」と「接続範囲から外れた切断」を区別するため。
        //   ①「ユーザーの意思による切断」は、mBluetoothGattオブジェクトを解放する。再接続は、オブジェクト構築から。
        //   ②「接続可能範囲から外れた切断」は、内部処理でmBluetoothGatt.disconnect()処理が実施される。
        //     切断時のコールバックでmBluetoothGatt.connect()を呼んでおくと、接続可能範囲に入ったら自動接続する。
        mBluetoothGatt.close();
        mBluetoothGatt = null;// GUIアイテムの有効無効の設定
        mButton_Connect.setEnabled( true );// 接続ボタンのみ有効にする
        mButton_Disconnect.setEnabled( false );
        mButton_ReadChara1.setEnabled( false );
        mButton_ReadChara2.setEnabled( false );
        mCheckBox_NotifyChara1.setChecked( false );
        mCheckBox_NotifyChara1.setEnabled( false );
        mButton_WriteHello.setEnabled( false );
        mButton_WriteWorld.setEnabled( false );
    }

    private void readCharacteristic( UUID uuid_service, UUID uuid_characteristic ) {
        if( null == mBluetoothGatt ) {
            return;
        }
        BluetoothGattCharacteristic blechar = mBluetoothGatt.getService(uuid_service).getCharacteristic(uuid_characteristic);
        mBluetoothGatt.readCharacteristic(blechar);
    }

    private void setCharacteristicNotification( UUID uuid_service, UUID uuid_characteristic, boolean enable ) {
        if( null == mBluetoothGatt ) {
            return;
        }
        BluetoothGattCharacteristic blechar = mBluetoothGatt.getService( uuid_service ).getCharacteristic( uuid_characteristic );
        mBluetoothGatt.setCharacteristicNotification( blechar, enable );
        BluetoothGattDescriptor descriptor = blechar.getDescriptor( UUID_NOTIFY );
        descriptor.setValue( BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE );
        mBluetoothGatt.writeDescriptor( descriptor );
    }

    private void writeCharacteristic( UUID uuid_service, UUID uuid_characteristic, String string ) {
        if( null == mBluetoothGatt ) {
            return;
        }
        BluetoothGattCharacteristic blechar = mBluetoothGatt.getService( uuid_service ).getCharacteristic( uuid_characteristic );
        blechar.setValue( string );
        mBluetoothGatt.writeCharacteristic( blechar );
    }
}