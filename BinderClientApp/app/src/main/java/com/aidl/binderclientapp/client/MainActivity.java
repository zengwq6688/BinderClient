package com.aidl.binderclientapp.client;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.aidl.binderclientapp.Book;
import com.aidl.binderclientapp.IBookManager;
import com.aidl.binderclientapp.R;
import com.aidl.binderclientapp.Stub;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView test;
    IBookManager iBookManager;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("ServiceConnection", "onServiceConnected");
            Log.e("ServiceConnection", "service---->" + service);

            iBookManager = Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("ServiceConnection", "onServiceDisconnected");
            iBookManager=null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("onCreate", "client process" + Process.myPid());
        setContentView(R.layout.activity_main);
        test = findViewById(R.id.testAdd);
        bindService();
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != iBookManager) {
                    try {
                        Book book = new Book();
                        book.setName("测试");
                        iBookManager.addBook(book);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void bindService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.aidl.binderserverapp", "com.aidl.binderserverapp.RemoteService"));
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

}
