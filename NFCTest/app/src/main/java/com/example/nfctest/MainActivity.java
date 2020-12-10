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
            // 注册回调来设置 NDEF 消息。这样做可以使Activity处于前台时，
            // NFC 数据推送处于激活状态。
            //mNfcAdapter.setNdefPushMessageCallback(this, this);
            // 注册回调来监听消息发送成功
            //mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
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
                myText.setText("FFFFFFFuck");
            }
            else {
                //myText.setText("YYYYYYes");
                myText.setText(str);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /*
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //当该Activity接收到NFC标签时，运行该方法
        //调用工具方法，读取NFC数据
        try {
            String str = NfcUtils.readNFCFromTag(intent);
            //myText.setText(str);
            processExtraData();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void processExtraData() {
        myText.setText("123");
    }

     */

}