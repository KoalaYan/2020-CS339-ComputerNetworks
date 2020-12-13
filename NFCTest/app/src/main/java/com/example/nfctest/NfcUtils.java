package com.example.nfctest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class NfcUtils {
    // NFC
    public static NfcAdapter mNfcAdapter;
    public static IntentFilter[] mIntentFilter = null;
    public static PendingIntent mPendingIntent = null;
    public static String[][] mTechList = null;


    // 构造函数
    public NfcUtils(Activity activity) {
        mNfcAdapter = NfcCheck(activity);
        NfcInit(activity);
    }

    // 检查NFC是否打开
    public static NfcAdapter NfcCheck(Activity activity) {
        NfcAdapter tmpAdapter = NfcAdapter.getDefaultAdapter(activity);
        if (tmpAdapter == null) {
            return null;
        } else {
            if (!tmpAdapter.isEnabled()) {
                Intent setNfc = new Intent(Settings.ACTION_NFC_SETTINGS);
                activity.startActivity(setNfc);
            }
        }
        return tmpAdapter;
    }

    // 初始化NFC设置
    public static void NfcInit(Activity activity) {
        mPendingIntent = PendingIntent.getActivity(activity, 0, new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter filter2 = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        try {
            filter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        mIntentFilter = new IntentFilter[]{filter, filter2};
        mTechList = null;
    }

    // 读取NFC数据
    public static String[] readNFCFromTag(Intent intent) throws UnsupportedEncodingException {
        String [] strList = null;
        Parcelable[] rawArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        if (rawArray != null) {
            NdefMessage mNdefMsg = (NdefMessage) rawArray[0];
            int len = mNdefMsg.getRecords().length;                 // 获取传输数据的size

            strList = new String[(len - 1) / 2 + 1];                    // 将"步数 + 心率"保存为一个字符串

            strList[0] = new String(mNdefMsg.getRecords()[0].getPayload());  // 获取标志位
            int tmpLen = strList[0].length();
            strList[0] = strList[0].substring(3, tmpLen);

            // 使用NFC进行数据传输
            for (int i = 0; i < (len - 1) / 2; i++) {
                String str1 = new String(mNdefMsg.getRecords()[2 * i + 1].getPayload());
                tmpLen = str1.length();
                str1 = str1.substring(3, tmpLen);      // 去除冗余
                str1 = str1 + "+";
                String str2 = new String(mNdefMsg.getRecords()[2 * i + 2].getPayload());
                tmpLen = str2.length();
                str2 = str2.substring(3, tmpLen);      // 去除冗余
                strList[i + 1] =str1 + str2;            // 组合成一个字符串进行传输
            }
        }
        return strList;
    }

    

    // 读取NFC ID
    /*
    public static String readNFCId(Intent intent) throws UnsupportedEncodingException {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        return ByteArrayToHexString(tag.getId());
    }

    private static String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

     */
}
