package com.example.nfctest;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;

public class NfcBase extends AppCompatActivity {
    //protected NfcAdapter mNfcAdapter;
    //private PendingIntent mPendingIntent;
    // Order: onCreate->onStart->onResume->onPause->onStop->onDestroy

    public void initData() {
        NfcUtils mNfcUtils = new NfcUtils(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 开启前台调度系统
        NfcUtils.mNfcAdapter.enableForegroundDispatch(this, NfcUtils.mPendingIntent, NfcUtils.mIntentFilter, NfcUtils.mTechList);
    }

    @Override
    public void onPause() {
        super.onPause();
        // 关闭前台调度系统
        NfcUtils.mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    // 当该Acticity接收到NFC标签时，运行该方法，读取NFC数据
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            String str = NfcUtils.readNFCFromTag(intent);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
