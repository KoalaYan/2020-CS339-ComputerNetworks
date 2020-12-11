package com.example.nfctest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.content.Intent;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private TextView myStepCount;
    private TextView myHeartRate;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private NfcUtils nfcUtils;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;
    private BTUtils btUtils;

    private static final int MESSAGE_READ = 0;

    private static Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case MESSAGE_READ:
                    byte[] buffer = (byte[])msg.obj;//buffer的大小和裏面數據的多少沒有關系
                    for(int i=0; i<buffer.length; i++){
                        if(buffer[i] != 0){
                            System.out.println(buffer[i]);
                        }
                    }
                    break;
            }
        }
    };

    ConnectedThread mConnectedThread;

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

        /*
        if (mNfcAdapter == null) {
            myStepCount.setText("NFC is not available on this device.");
        } else {
            myStepCount.setText("oooooooops");
            myHeartRate.setText("oooooooops");
        }
        */
        /*
        if (mBluetoothAdapter == null) {
            myStepCount.setText("Failed to obtain bluetooth adapter");
        } else {
            myStepCount.setText("Bluetooth adapter is obtained!");
        }
        */

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


        /*
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
                myStepCount.setText(str[0] + " (steps)");
                myHeartRate.setText(str[1] + " (bpm)");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
         */
    }

    void manageConnectedSocket(BluetoothSocket socket){
        //System.out.println("From manageConnectedSocket:"+socket);
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("0c312388-5d09-4f44-b670-5461605f0b1e"));
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();//這個操作需要幾秒鐘，不是立即能見效的
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket);
        }

        /** Will cancel an in-progress connection, clean up all internal resources, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    //Message msg = new Message();
                    //msg.what = MESSAGE_READ;
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }


}

