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

    private TextView myText;
    private NfcAdapter mNfcAdapter;
    //private IntentFilter[] mIntentFilter = null;
    private PendingIntent mPendingIntent;
    //private String[][] mTechList = null;
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
        myText = (TextView) findViewById(R.id.mycount);

        //mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            myText.setText("NFC is not available on this device.");
        } else {
            myText.setText("oooooooops");
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
            String str = NfcUtils.readNFCFromTag(intent);
            if(str.length() == 0) {
                myText.setText("Length of String is 0. Failed...");
            }
            else {
                myText.setText(str);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}