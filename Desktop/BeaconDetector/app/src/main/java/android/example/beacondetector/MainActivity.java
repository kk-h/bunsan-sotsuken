package android.example.beacondetector;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    private BeaconManager beaconManager;
    final Region mRegion = new Region("iBeacon", null, null, null);
    private static final String IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    int count = 0;
    ArrayList<Identifier> uuids = new ArrayList<Identifier>();
    ArrayList<Identifier> majors = new ArrayList<Identifier>();
    ArrayList<Identifier> minors = new ArrayList<Identifier>();
    ArrayList<Integer> rssis = new ArrayList<Integer>();
    ArrayList<Integer> txPowers = new ArrayList<Integer>();
    ArrayList<Double> distances = new ArrayList<Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beaconManager = BeaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON_FORMAT));

    }

    @Override
    protected void onResume() {
        super.onResume();
        beaconManager.bind(this);//サービス開始
    }

    @Override
    protected void onPause() {
        super.onPause();
        beaconManager.unbind(this);//サービス終了
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {//領域への入場を検知
                Log.d("iBeacon", "Enter Region");
            }

            @Override
            public void didExitRegion(Region region) {//領域からの退場を検知
                Log.d("iBeacon", "Exit Region");
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {//領域への入退場のステータス変化を検知
                Log.d("MyActivity", "Determine State" + i);
            }
        });
        try {//Beacon情報の監視を開始
            beaconManager.startMonitoringBeaconsInRegion(mRegion);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

                //検出したBeaconの情報を全てlog出力
                for (Beacon beacon : beacons) {
                    Log.d("MyActivity", "UUID:" + beacon.getId1() + ", major:"
                            + beacon.getId2() + ", minor:" + beacon.getId3() + ", RSSI:"
                            + beacon.getRssi() + ", TxPower:" + beacon.getTxPower()
                            + ", Distance:" + beacon.getDistance());

                    uuids.add(beacon.getId1());
                    majors.add(beacon.getId2());
                    minors.add(beacon.getId3());
                    rssis.add(beacon.getRssi());
                    txPowers.add(beacon.getTxPower());
                    distances.add(beacon.getDistance());
                }
                count = beacons.size();
                Log.d("Activity", "total:" + count + "台");
            }
        });
    }

    public void clicked(View view) {
        TextView countView = (TextView) findViewById(R.id.result);
        countView.setText(String.valueOf(count));

        ListView beaconList = (ListView) findViewById(R.id.beacon_list);
        ArrayList<BeaconListItems> listItems = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            BeaconListItems beaconItem = new BeaconListItems(getUuids().get(i), getMajors().get(i),
                    getMinors().get(i), getRssis().get(i), getTxPowers().get(i), getDistances().get(i));
            listItems.add(beaconItem);
        }
        BeaconListAdapter beaconAdapter = new BeaconListAdapter(this, R.layout.beacon_view, listItems);
        beaconList.setAdapter(beaconAdapter);

    }
}
