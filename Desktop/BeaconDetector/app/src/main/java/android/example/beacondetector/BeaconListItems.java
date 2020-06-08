package android.example.beacondetector;

import org.altbeacon.beacon.Identifier;

public class BeaconListItems {
    private Identifier mUuid = null;
    private Identifier mMajor = null;
    private Identifier mMinor = null;
    private Integer mRssi = null;
    private Integer mTxPower = null;
    private Double mDistance = null;

    public BeaconListItems() {};

 BeaconListItems(Identifier uuid, Identifier major, Identifier minor, Integer rssi, Integer txPower, Double distance) {
        mUuid = uuid;
        mMajor = major;
        mMinor = minor;
        mRssi = rssi;
        mTxPower = txPower;
        mDistance = distance;
    }

    public Identifier getUuids() {
        return mUuid;
    }

    public void setUuid(Identifier mUuid) {
        this.mUuid = mUuid;
    }

    public Identifier getMajor() {
        return mMajor;
    }

    public void setMajor(Identifier mMajor) {
        this.mMajor = mMajor;
    }

    public Identifier getMinor() {
        return mMinor;
    }

    public void setMinor(Identifier mMinor) {
        this.mMinor = mMinor;
    }

    public Integer getRssi() {
        return mRssi;
    }

    public void setRssi(Integer mRssi) {
        this.mRssi = mRssi;
    }

    public Integer getTxPower() {
        return mTxPower;
    }

    public void setTxPower(Integer mTxPower) {
        this.mTxPower = mTxPower;
    }

    public Double getDistance() {
        return mDistance;
    }

    public void setDistance(Double mDistance) {
        this.mDistance = mDistance;
    }

}

