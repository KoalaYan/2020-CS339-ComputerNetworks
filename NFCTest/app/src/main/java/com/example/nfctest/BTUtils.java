package com.example.nfctest;

import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BTUtils extends Thread{
    // NFC
    //public static NfcAdapter mNfcAdapter;
    //public static IntentFilter[] mIntentFilter = null;
    //public static PendingIntent mPendingIntent = null;
    //public static String[][] mTechList = null;

    public static BluetoothAdapter mBluetoothAdapter;
    public static BluetoothDevice mBluetoothDevice;
    public static BluetoothSocket mBluetoothSocket;
    //private static final UUID MyUUID = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
    private static UUID MyUUID;


    // 构造函数
    public BTUtils(Activity activity) {
        mBluetoothAdapter = BTCheck(activity);
        //BTInit(activity);
    }

    // 检查Bluetooth是否打开并初始化
    public static BluetoothAdapter BTCheck(Activity activity) {
        //else {
        //    if (!tmpAdapter.isEnabled()) {
        //        Intent setNfc = new Intent(Settings.ACTION_NFC_SETTINGS);
        //        activity.startActivity(setNfc);
        //    }
        //}
        return BluetoothAdapter.getDefaultAdapter();
    }

    private static byte subString2byte(String ori_str, int begin, int end) {
        byte num = 0;
        char[] str = ori_str.toCharArray();
        for (int i = begin; i < end; i++) {
            if (str[i] >= '0' && str[i] <= '9') {
                if (i == begin) {
                    num += (str[i] - '0') * 16;
                } else {
                    num += str[i] - '0';
                }
            }
            else if (str[i] >= 'A' && str[i] <= 'F') {
                if (i == begin) {
                    num += (str[i] - 'A' + 10) * 16;
                } else {
                    num += (str[i] - 'A' + 10);
                }
            }
        }
        return num;
    }

    private static byte[] string2mac(String str) {
        byte[] bMacAddress = new byte[6];
        int count = 0;
        for (int i = 0; i < 6; i++) {
            bMacAddress[i] = subString2byte(str, 2*i + count, 2*i + 2 + count);
            count++;
        }
        return bMacAddress;
    }


    // 读取蓝牙数据
    public static String connectBluetooth(Intent intent) throws UnsupportedEncodingException {
        //String [] strList = new String[]{"", ""};
        Parcelable[] rawArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        String str = null;
        String str2 = null;
        String UUIDString = null;

        if (rawArray != null) {
            //NdefMessage[] msgs = new NdefMessage[rawArray.length];
            NdefMessage mNdefMsg = (NdefMessage) rawArray[0];
            //for (int i = 0; i < rawArray.length; i++) {
            //    msgs[i] = (NdefMessage) rawArray[i];
            //    Log.i("Parsed NDEF message ", "NDEF record: " + msgs[i]);
            //}
            //NdefRecord[] records_list = msgs[0].getRecords();
            NdefRecord mNdefRecord = mNdefMsg.getRecords()[0];
            NdefRecord mNdefRecord_forUUID = mNdefMsg.getRecords()[1];
            //Log.i("Parsed NDEF record ", "First record: " + records_list[0]);

            if (mNdefRecord == null || mNdefRecord_forUUID == null) {
                return null;
            }

            // 预处理Mac地址的字符串
            str = new String(mNdefRecord.getPayload());
            int len = str.length();
            str = str.substring(3, len);

            //byte[] bMacAddress = string2mac(str);

            //str =  new String(bMacAddress);


            //byte[] payload = mNdefRecord.getPayload();

            try {
                //byte[] bMacAddress = new byte[6];
                // 生成Mac地址
                byte[] bMacAddress = string2mac(str);

                //str =  new String(bMacAddress);

                // 得到UUID
                UUIDString = new String(mNdefRecord_forUUID.getPayload());
                int len2 = UUIDString.length();
                UUIDString = UUIDString.substring(3, len2);
                MyUUID = UUID.fromString(UUIDString);


                mBluetoothAdapter.enable();             // 开启蓝牙
                //mBluetoothAdapter.startDiscovery();     // 开始搜索
                mBluetoothDevice = null;
                mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(bMacAddress);      // 利用Mac地址查找设备

                //ParcelUuid[] pUIied = bDev.getUuids();
                //Log.e("Bluetooth test", "remote UUID " + pUIid[0]);


                // 创建Socket
                mBluetoothSocket = null;
                try {
                    mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(MyUUID);
                    //str2 = "Socket Create succeed";
                } catch (IOException e) {
                    //str2 = "Socket Create failed";
                }
                //if (mBluetoothSocket == null) {
                //    str2 = "Socket NULL!";
                //} else {
                //    str2 = "Socket CREATE!";
                //}

                mBluetoothAdapter.cancelDiscovery();                    // 关闭蓝牙搜索
                try {
                    mBluetoothSocket.connect();
                    str2 = "Connect success";
                } catch (IOException connectException) {
                    try {
                        mBluetoothSocket.close();
                        str2 = "Close success";
                    } catch (IOException closeException) {
                        str2 = "Close failed";
                    }
                }


                //if (mBluetoothSocket.isConnected()) {
                //    str2 = "Socket connected!";
                //} else {
                //    str2 = "Socket not connected...";
                //}

                try {
                    Method m = mBluetoothDevice.getClass().getMethod("createBond", (Class[]) null);
                    m.invoke(mBluetoothDevice, (Object[]) null);

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                mBluetoothSocket.connect();


            } catch (IOException | NoSuchMethodException e) {
                e.printStackTrace();
            }


        }
        //return UUIDString;
        return str2;
    }



    // 读取NFC ID
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
}
