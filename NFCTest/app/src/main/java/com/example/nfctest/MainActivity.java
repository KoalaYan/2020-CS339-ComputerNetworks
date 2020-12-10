package com.example.nfctest;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;


public class MainActivity extends AppCompatActivity {

    private TextView myText;
    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myText = (TextView) findViewById(R.id.mycount);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
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