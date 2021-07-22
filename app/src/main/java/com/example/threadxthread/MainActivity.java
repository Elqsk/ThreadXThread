package com.example.threadxthread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    PrinterThread printerThread;

    ArrayList<String> oddData;
    ArrayList<String> evenData;

    ArrayAdapter<String> oddAdapter;
    ArrayAdapter<String> evenAdapter;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView oddListView = findViewById(R.id.lab3_list_odd);
        ListView evenListView = findViewById(R.id.lab3_list_even);

        oddData = new ArrayList<>();
        evenData = new ArrayList<>();

        oddAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, oddData);
        evenAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, evenData);

        oddListView.setAdapter(oddAdapter);
        evenListView.setAdapter(evenAdapter);

        handler = new Handler(Looper.getMainLooper());

        printerThread = new PrinterThread();
        printerThread.start();

        GeneratorThread generatorThread = new GeneratorThread();
        generatorThread.start();
    }

    class PrinterThread extends Thread {
        Handler oneHandler;

        @Override
        public void run() {
            super.run();

            Looper.prepare();

            Log.d("kkang", "Thread 1 / Looper.prepare()");
            Log.d("kkang", " ");

            oneHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);

                    SystemClock.sleep(1000);

                    Log.d("kkang", "Thread 1 / Handler oneHandler / SystemClock.sleep(1000)");

                    int data = msg.arg1;

                    Log.d("kkang", "Thread 1 / Handler oneHandler / data: " + data);
                    Log.d("kkang", " ");

                    if (msg.what == 0) {
                        handler.post(() -> {
                            evenData.add("even: " + data);
                            evenAdapter.notifyDataSetChanged();

                            Log.d("kkang", "Thread 1 / Handler oneHandler / ★ even: " + data);
                        });
                    } else if (msg.what == 1) {
                        handler.post(() -> {
                            oddData.add("odd: " + data);
                            oddAdapter.notifyDataSetChanged();

                            Log.d("kkang", "Thread 1 / Handler oneHandler / ★ odd: " + data);
                        });
                    }
                    Log.d("kkang", " ");
                }
            };
            Log.d("kkang", " ");
        }
    }

    class GeneratorThread extends Thread {

        @Override
        public void run() {
            super.run();

            Random random = new Random();

            Log.d("kkang", "Thread 2 / new Random()");

            for (int i = 0; i < 10; i ++) {
                SystemClock.sleep(1000);

                Log.d("kkang", "Thread 2 / int i: " + i);

                int data = random.nextInt(10);

                Log.d("kkang", "Thread 2 / int data: " + data);

                Message msg = new Message();

                Log.d("kkang", "Thread 2 / new Message()");

                if (data % 2 == 0) {
                    msg.what = 0;

                    Log.d("kkang", "Thread 2 / data % 2 == 0, msg.what = 0");
                } else {
                    msg.what = 1;

                    Log.d("kkang", "Thread 2 / data % 2 ≠≠ 0, msg.what = 1");
                }
                msg.arg1 = data;
                msg.arg2 = i;

                Log.d("kkang", "Thread 2 / msg.arg1 = data: " + msg.arg1);
                Log.d("kkang", "Thread 2 / msg.arg2 = i: " + i);

                printerThread.oneHandler.sendMessage(msg);

                Log.d("kkang", " ");
            }
            Log.d("kkang", "Thread 2 stopped");
            Log.d("kkang", " ");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("kkang", "onDestroy()");

        printerThread.oneHandler.getLooper().quit();
    }
}