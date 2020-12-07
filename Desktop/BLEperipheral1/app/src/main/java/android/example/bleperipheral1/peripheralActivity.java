package android.example.bleperipheral1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class peripheralActivity extends AppCompatActivity{
    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    BluetoothLeAdvertiser bluetoothLeAdvertiser;
    BluetoothGattCharacteristic psdiCharacteristic;
    BluetoothGattCharacteristic bluetoothCharacteristic1;
    BluetoothGattCharacteristic bluetoothCharacteristic2;
    BluetoothGattCharacteristic notifyCharacteristic;
    BluetoothGattService psdiService;
    BluetoothGattService bluetoothGattService;
    BluetoothGattServer bluetoothGattServer;
    boolean connected = false;
    BluetoothDevice bluetoothdevice;

    private String TAG = "Debug :";

    private static final UUID UUID_LIFF_PSDI_SERVICE = UUID.fromString("e625601e-9e55-4597-a598-76018a0d293d");
    private static final UUID UUID_LIFF_PSDI = UUID.fromString("26e2b12b-85f0-4f3f-9fdd-91d114270e6e");
    private static final String UUID_LIFF_SERVICE_STR = "a9d158bb-9007-4fe3-b5d2-d3696a3eb067";
    private static final UUID UUID_LIFF_SERVICE = UUID.fromString(UUID_LIFF_SERVICE_STR);
    private static final UUID UUID_LIFF_WRITE = UUID.fromString("52dc2801-7e98-4fc2-908a-66161b5959b0");
    private static final UUID UUID_LIFF_READ = UUID.fromString("52dc2802-7e98-4fc2-908a-66161b5959b0");
    private static final UUID UUID_LIFF_NOTIFY = UUID.fromString("52dc2803-7e98-4fc2-908a-66161b5959b0");
    private static final UUID UUID_LIFF_DESC = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private static final int UUID_LIFF_VALUE_SIZE = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peripheral);

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter != null) {
            prepareBle();
        }
    }

    private void prepareBle() {
        bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (bluetoothLeAdvertiser == null) {
            Log.e(TAG, "cannot use peripheral mode");
            return;
        }

        bluetoothGattServer = bluetoothManager.openGattServer(this, gattServerCallback);
        psdiService = new BluetoothGattService(UUID_LIFF_PSDI_SERVICE, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        psdiCharacteristic = new BluetoothGattCharacteristic(UUID_LIFF_PSDI, BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ);
        psdiService.addCharacteristic(psdiCharacteristic);
        bluetoothGattServer.addService(psdiService);

        try {
            Thread.sleep(200);
        } catch (Exception ex) {
        }

        bluetoothGattService = new BluetoothGattService(UUID_LIFF_SERVICE, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        bluetoothCharacteristic2 = new BluetoothGattCharacteristic(UUID_LIFF_READ, BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ);
        bluetoothGattService.addCharacteristic(bluetoothCharacteristic2);
        bluetoothGattServer.addService(bluetoothGattService);

        try {
            Thread.sleep(200);
        } catch (Exception ex) {
        }

        startBleAdvertising();
    }

    private void startBleAdvertising() {
        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        builder.setIncludeTxPowerLevel(true);
        builder.addServiceUuid(ParcelUuid.fromString(UUID_LIFF_SERVICE_STR));

        AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();
        settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        settingsBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
        settingsBuilder.setTimeout(0);
        settingsBuilder.setConnectable(true);

        AdvertiseData.Builder respBuilder = new AdvertiseData.Builder();
        respBuilder.setIncludeDeviceName(true);

        bluetoothLeAdvertiser.startAdvertising(settingsBuilder.build(), builder.build(), respBuilder.build(), new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.d("bleperi", "onStartSuccess");
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.d("bleperi", "onStartFailure");
            }
        });
    }

    private BluetoothGattServerCallback gattServerCallback = new BluetoothGattServerCallback() {
        private byte[] psdiValue = new byte[8];
        private byte[] notifyDescValue = new byte[2];
        private byte[] charValue = new byte[UUID_LIFF_VALUE_SIZE];

        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            Log.d("bleperi", "onMtuChanged(" + mtu + ")");
        }

        @Override
        public void onConnectionStateChange(android.bluetooth.BluetoothDevice device, int status, int newState) {
            Log.d("bleperi", "onConnectionStateChange");

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                bluetoothdevice = device;
                connected = true;
                Log.d("bleperi", "STATE_CONNECTED:" + device.toString());
            } else {
                connected = false;
                Log.d("bleperi", "Unknown STATE:" + newState);
            }
        }

        public void onCharacteristicReadRequest(android.bluetooth.BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            Log.d("bleperi", "onCharacteristicReadRequest");

            if( characteristic.getUuid().compareTo(UUID_LIFF_PSDI) == 0) {
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, psdiValue);
            }else if( characteristic.getUuid().compareTo(UUID_LIFF_READ) == 0){
                if( offset > charValue.length ) {
                    bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, offset, null);
                }else {
                    byte[] value = new byte[charValue.length - offset];
                    System.arraycopy(charValue, offset, value, 0, value.length);
                    bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
                }
            }else{
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, offset, null );
            }
        }

        public void onCharacteristicWriteRequest(android.bluetooth.BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            Log.d("bleperi", "onCharacteristicWriteRequest");

            if( characteristic.getUuid().compareTo(UUID_LIFF_WRITE) == 0 ){
                if(offset < charValue.length ) {
                    int len = value.length;
                    if( (offset + len ) > charValue.length)
                        len = charValue.length - offset;
                    System.arraycopy(value, 0, charValue, offset, len);
                    bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
                }else {
                    bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, offset, null);
                }

                if( (notifyDescValue[0] & 0x01) != 0x00 ) {
                    if (offset == 0 && value[0] == (byte) 0xff) {
                        notifyCharacteristic.setValue(charValue);
                        bluetoothGattServer.notifyCharacteristicChanged(bluetoothdevice, notifyCharacteristic, false);
                    }
                }
            }else{
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, offset, null);
            }
        }

        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            Log.d("bleperi", "onDescriptorReadRequest");

            if( descriptor.getUuid().compareTo(UUID_LIFF_DESC) == 0 ) {
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, notifyDescValue);
            }
        }

        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            Log.d("bleperi", "onDescriptorWriteRequest");

            if( descriptor.getUuid().compareTo(UUID_LIFF_DESC) == 0 ) {
                notifyDescValue[0] = value[0];
                notifyDescValue[1] = value[1];

                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
            }
        }
    };

    public static String toHexString(byte[] data) {
        StringBuffer sb = new StringBuffer();
        for (byte b : data) {
            String s = Integer.toHexString(0xff & b);
            if (s.length() == 1)
                sb.append("0");
            sb.append(s);
        }
        return sb.toString();
    }
}

