package android.example.blesample2;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Beacon_Model extends RealmObject {
    private String uuid;
    private String location;
    private String url;

    public String getUuid() {
        return uuid;
    }

    public String getLocation() {
        return location;
    }

    public String getUrl() {
        return url;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
