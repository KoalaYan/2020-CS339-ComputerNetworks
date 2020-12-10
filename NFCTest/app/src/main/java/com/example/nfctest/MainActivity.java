package com.example.nfctest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.content.Intent;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;


public class MainActivity extends AppCompatActivity {

    private TextView myStepCount;
    private TextView myHeartRate;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private NfcUtils nfcUtils;

    public void initData() {
        //nfc初始化设置
        nfcUtils = new NfcUtils(this);
        mNfcAdapter = NfcUtils.mNfcAdapter;
        mPendingIntent = NfcUtils.mPendingIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myStepCount = (TextView) findViewById(R.id.mycount);
        myHeartRate = (TextView) findViewById(R.id.myrate);
        
        if (mNfcAdapter == null) {
            myStepCount.setText("NFC is not available on this device.");
        } else {
            myStepCount.setText("oooooooops");
            myHeartRate.setText("oooooooops");
        }
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //当该Activity接收到NFC标签时，运行该方法
        //调用工具方法，读取NFC数据
        try {
            String[] str = NfcUtils.readNFCFromTag(intent);

            if(str[0].length() == 0 || str[1].length() == 0) {
                if (str[0].length() == 0) {
                    myStepCount.setText("Failed...");
                }
                if (str[1].length() == 0) {
                    myHeartRate.setText("Failed...");
                }
            }
            else {
                myStepCount.setText(str[0] + " steps");
                myHeartRate.setText(str[1] + " bpm");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}