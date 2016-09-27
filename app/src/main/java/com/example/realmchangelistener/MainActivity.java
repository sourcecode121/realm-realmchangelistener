package com.example.realmchangelistener;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.realmchangelistener.models.Task;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private Realm realm;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        final TextView textView = (TextView) findViewById(R.id.textView);

        realm = Realm.getDefaultInstance();

        RealmChangeListener realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object element) {
                Log.d("Realm", "Updated with new data");
                textView.setText(task.getTitle());
            }
        };

        realm.addChangeListener(realmChangeListener);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task t = realm.createObject(Task.class);
                t.setId(UUID.randomUUID().toString());
                t.setTitle("First task");
                t.setDescription("This is the first task");
            }
        });

        task = realm.where(Task.class).findFirst();
        textView.setText(task.getTitle());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        task = realm.where(Task.class).findFirst();
                        task.setTitle("New task");
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.removeAllChangeListeners();
        realm.close();
    }
}
