package com.example.nfctest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Intent;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private TextView myStepCount;
    private TextView myHeartRate;
    private TextView myMode;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private NfcUtils nfcUtils;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;
    private BTUtils btUtils;

    // 下拉菜单
    private Spinner mySpinner;
    ArrayAdapter<String> spinnerAdapter;
    String[] spinnerArray;


    //nfc初始化设置
    public void initData() {
        nfcUtils = new NfcUtils(this);
        mNfcAdapter = NfcUtils.mNfcAdapter;
        mPendingIntent = NfcUtils.mPendingIntent;

        btUtils = new BTUtils(this);
        mBluetoothAdapter = BTUtils.mBluetoothAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // NFC工具类初始化
        initData();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myStepCount = (TextView) findViewById(R.id.mycount);
        myHeartRate = (TextView) findViewById(R.id.myrate);
        myMode = (TextView) findViewById(R.id.mymode);
        mySpinner = (Spinner) findViewById(R.id.spinner);
        spinnerArray = new String[]{"History Record", "..."};
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spinnerArray);
        mySpinner.setAdapter(spinnerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //开启前台调度系统
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //关闭前台调度系统
        mNfcAdapter.disableForegroundDispatch(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //当该Activity接收到NFC标签时，运行该方法
        //调用工具方法，读取NFC数据

        /*
        try {
            String str = BTUtils.connectBluetooth(intent);

            myHeartRate.setText(str);

            if (str.length() == 0) {
                myHeartRate.setText("Empty string...");
            }

            mBluetoothDevice = BTUtils.mBluetoothDevice;
            if (mBluetoothDevice == null) {
                //myStepCount.setText("Failed in connecting device");
            } else {
                //myStepCount.setText("Successfully connect!");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

         */


        try {
            // 利用NFC获取Tag
            String[] str = NfcUtils.readNFCFromTag(intent);

            if (str != null) {
                //打印数据传输模式
                myMode.setText(str[0]);

                if (str[0].equals("nfc")) {
                    // 使用NFC进行数据传输，已完成，只需打印
                    int len = str.length;
                    if (len <= 1) {
                        myStepCount.setText("Empty message in NFC transmission");
                        myHeartRate.setText("Empty message in NFC transmission");
                    }
                    String[] tmpArr = str[len - 1].split("\\+");        // 字符串分割

                    if (len > 1) {
                        // 打印最新的步数和心率
                        myStepCount.setText(tmpArr[0]);
                        myHeartRate.setText(tmpArr[1]);

                        // 将历史记录放在下拉列表里
                        spinnerArray = Arrays.copyOfRange(str, 1, len);
                        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spinnerArray);
                        mySpinner.setAdapter(spinnerAdapter);
                    }

                } else {
                    //myStepCount.setText("Begin to use bluetooth");
                    // 使用蓝牙进行剩余数据的传输
                    String oriBTRes = null;
                    oriBTRes = BTUtils.connectBluetooth(intent, str, myStepCount);
                    if (oriBTRes != null) {
                        //myStepCount.setText(oriBTRes);
                        String[] recordList = stringSplit(oriBTRes, "\\;");
                        //myStepCount.setText(String.valueOf(recordList.length));
                        for (int i = 0; i < recordList.length; i++) {
                            String[] tmpList = stringSplit(recordList[i], "\\#");

                            if (i == recordList.length - 1) {
                                // 最新数据，打印
                                if (tmpList.length == 2) {
                                    myStepCount.setText(tmpList[0]);
                                    myHeartRate.setText(tmpList[1]);
                                    recordList[i] = tmpList[0] + " " + tmpList[1];
                                } else {
                                    //myStepCount.setText(String.valueOf(tmpList.length));
                                    myStepCount.setText("Error occurred in the latest message...");
                                    myHeartRate.setText("Error occurred in the latest message...");
                                }
                            }
                            // 将历史数据进行形式处理，去掉#
                            if (tmpList.length == 2) {
                                recordList[i] = tmpList[0] + " " + tmpList[1];
                            }
                        }
                        // 将历史记录放在下拉列表里
                        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recordList);
                        mySpinner.setAdapter(spinnerAdapter);
                    } else {
                        myStepCount.setText("Bluetooth connection returns null");
                    }
                }

            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        private String[] stringSplit(String str, String sp) {
        return str.split(sp);
    }

}

