package android.example.myscheduler;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MySchedulerApplication extends Application {
    @Override
    public void onCreate(){
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
