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
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BTUtils extends Thread {

    // 蓝牙连接
    public static BluetoothAdapter mBluetoothAdapter;
    public static BluetoothDevice mBluetoothDevice;
    public static BluetoothSocket mBluetoothSocket;
    private static UUID MyUUID;

    // 蓝牙数据传输
    public static final int MESSAGE_READ = 0;
    private static InputStream mInStream;


    // 构造函数
    public BTUtils(Activity activity) {
        mBluetoothAdapter = BTCheck(activity);
    }

    // 检查Bluetooth是否打开并初始化
    public static BluetoothAdapter BTCheck(Activity activity) {

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
            } else if (str[i] >= 'A' && str[i] <= 'F') {
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
            bMacAddress[i] = subString2byte(str, 2 * i + count, 2 * i + 2 + count);
            count++;
        }
        return bMacAddress;
    }


    // 连接蓝牙并读取数据
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String connectBluetooth(Intent intent, String[] strList, TextView tv) throws IOException {

        String messageStr = null;
        String resultStr = null;
        String UUIDString = null;
        String MACString = null;
        String tv_str = "enter bluetooth\n";
        tv.setText(tv_str);

        // 若数组长度不为2，说明存在数据错误，返回错误信息
        if (strList.length != 2) {
            messageStr = "Error size";
            return null;
        }

        String[] tmpArr = strList[1].split("\\+");        // 字符串分割
        MACString = tmpArr[0];                                  // 获得MAC地址字符串
        UUIDString = tmpArr[1];                                 // 获得UUID字符串

        // 生成Mac地址
        byte[] bMacAddress = string2mac(MACString);

        // 生成UUID
        MyUUID = UUID.fromString(UUIDString);

        mBluetoothAdapter.enable();             // 开启蓝牙
        mBluetoothDevice = null;
        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(bMacAddress);      // 利用Mac地址查找设备

        // 创建Socket
        mBluetoothSocket = null;
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(MyUUID);
            //messageStr = "Socket Create succeed";
        } catch (IOException e) {
            //messageStr = "Socket Create failed";
        }

        mBluetoothAdapter.cancelDiscovery();                    // 关闭蓝牙搜索

        // 建立Socket连接
        try {
            mBluetoothSocket.connect();
            messageStr = "Connect success";
        } catch (IOException connectException) {
            try {
                mBluetoothSocket.close();
                messageStr = "Fail to connect socket, close success";
                return null;
            } catch (IOException closeException) {
                messageStr = "Fail to connect socket, close failed";
                return null;
            }
        }

        tv_str += "socket connected successfully\n";
        tv.setText(tv_str);

        // 读取蓝牙数据
        InputStream tmpIn = null;
        try {
            tmpIn = mBluetoothSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            messageStr = "Error occurred in bt data reading";
            return messageStr;
            }
        mInStream = tmpIn;

        byte[] mBuffer = new byte[1024];
        int byteNum;


        // 持续监听InputStream直到异常抛出（Server的蓝牙关闭）
        while (true) {
            try {
                byteNum = mInStream.read(mBuffer);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            } finally {
                resultStr = "123214";
                break;
            }
        }

        // 将buffer转化成String结果返回
        try {
            resultStr = new String(mBuffer);
            //resultStr = new String("123");
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred when transferring buffer";
        }


        // 关闭Socket
        try {
            mBluetoothSocket.close();
            if (!mBluetoothSocket.isConnected()) {
                messageStr = "Close success";
            } else {
                messageStr = "Close failed";
                return null;
            }
        } catch (IOException e) {
            messageStr = "Error occurred when closing socket";
            return null;
        }

        return resultStr;
    }
}