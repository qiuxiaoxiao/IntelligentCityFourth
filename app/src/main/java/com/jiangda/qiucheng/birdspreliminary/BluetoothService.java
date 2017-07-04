package com.jiangda.qiucheng.birdspreliminary;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

/**
 * Created by qiucheng on 2017/7/3.
 */

public class BluetoothService {
    private static final String TAG = "BluetoothService";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Context context;
    //蓝牙适配器
    private BluetoothAdapter mAdapter;
    private Handler mHandler;

    //当前传感器设备的个数，即要开启的线程个数，用于设置线程数组的大小
    //这里默认为1，因为我们目前只需要和一个传感器连接， 比如：你要连接两个硬件设备，那就设置值为2，这样就会开启两个线程，分别去执行想要操作
    public static final int  SENSEOR_NUM=1;

    private AcceptThread mAcceptThread;// 请求连接的监听进程
    private ConnectThread mConnectThread;// 连接一个设备的进程
    public ConnectedThread[] mConnectedThread=new ConnectedThread[SENSEOR_NUM];// 已经连接之后的管理进程

    private int mState;// 当前状态
    // 指明连接状态的常量
    public static final int STATE_NONE = 0;         //没有连接
    public static final int STATE_LISTEN = 1;       //等待连接
    public static final int STATE_CONNECTING = 2;  //正在连接
    public static final int STATE_CONNECTED = 3;   //已经连接

    public BluetoothService(Context context, Handler mHandler) {
        this.context = context;
        this.mHandler = mHandler;
        mAdapter = BluetoothAdapter.getDefaultAdapter();//获取蓝牙适配器
        mState = STATE_NONE ; //当前连接状态：未连接
    }

    // 参数 index 是 硬件设备的id ，随便设的，目的在于当 同时连接多个硬件设备的时候，根据此id进行区分
    public synchronized void connect(BluetoothDevice device, int index) {

        //连接一个蓝牙时，将该设备 的蓝牙连接线程关闭，如果有的话
        //demo  就只有一个硬件设备，默认该设备id 取值index=1;
        if (mConnectedThread[index-1] != null) {
            mConnectedThread[index-1].cancel();
            mConnectedThread[index-1]=null;
        }
        mConnectThread=new ConnectThread(device,index);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private int index;

        public ConnectThread(BluetoothDevice device, int index) {
            mmDevice = device;
            this.index = index;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);// Get a BluetoothSocket for a connection with the given BluetoothDevice
            } catch (IOException e) {
            }
            mmSocket = tmp;
        }

        public void run() {

            setName("ConnectThread");
            //当连接成功，取消蓝牙适配器搜索蓝牙设备的操作，因为搜索操作非常耗时
            mAdapter.cancelDiscovery();// Always cancel discovery because it will slow down a connection

            try {
                mmSocket.connect();// This is a blocking call and will only return on a successful connection or an exception
            } catch (IOException e) {
                connectionFailed(this.index);
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                BluetoothService.this.start();// 引用来说明要调用的是外部类的方法 run
                return;
            }

            synchronized (BluetoothService.this) {// Reset the ConnectThread because we're done
                mConnectThread = null;
            }
            connected(mmSocket, mmDevice, index);// Start the connected thread
        }



        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class ConnectedThread extends Thread{
        private BluetoothSocket mmSocket;
        private InputStream mmInStream;
        private OutputStream mmOutStream;
        private int index;
        private Queue<Byte> queueBuffer = new LinkedList<Byte>();
        private byte[] packBuffer = new byte[9];

        //构造方法
        public ConnectedThread(BluetoothSocket socket,int index) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            this.index=index;
            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

//        public void SendBleMessage(byte[] data) throws IOException {
//            mmOutStream.write(data);
//        }

        // 数组大小看你的数据需求，这里存的是你处理蓝牙传输来的字节数据之后实际要用到的数据
        private float [] fData=new float[31];

        @Override
        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] tempInputBuffer = new byte[1024];
            int bytes;
//            while(true){
            while(mState == STATE_CONNECTED){
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(tempInputBuffer);
                    mHandler.obtainMessage(SendActivity.MESSAGE_READ, bytes, -1, tempInputBuffer).sendToTarget();
//                    Log.i(TAG,ConvertUtils.getInstance().bytesToHexString(tempInputBuffer));
                    Log.i(TAG, Utils.byteConvert2String(tempInputBuffer ,0, tempInputBuffer.length));

                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost(this.index);
                    e.printStackTrace();
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(SendActivity.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "发送数据出现异常", e);
            }
        }


        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {}
        }
    }
    //连接失败
    private void connectionFailed(int index) {
        setState(STATE_LISTEN);
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(SendActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "未能连接设备"+index);
        bundle.putInt("device_id",index);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }
    // 连接丢失
    private void connectionLost(int index) {
        setState(STATE_LISTEN);
        Message msg = mHandler.obtainMessage(SendActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "设备丢失"+index);
        bundle.putInt("device_id",index);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }


    //用于 蓝牙连接的Activity onResume()方法
    public synchronized void start() {
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(STATE_LISTEN);
    }

    public synchronized void connected(BluetoothSocket socket,BluetoothDevice device,int index) {
        Log.d("MAGIKARE","连接到线程"+index);
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        // Cancel the accept thread because we only want to connect to one device
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread[index-1] = new ConnectedThread(socket,index);
        setState(STATE_CONNECTED);
        mConnectedThread[index-1].start();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(SendActivity.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString("device_name", device.getName()+" "+index);

        msg.setData(bundle);
        mHandler.sendMessage(msg);


    }

    private synchronized void setState(int state) {
        mState = state;
        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(SendActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;
        //private int index;
        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            // this.index=index;
            // Create a new listening server socket
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord("BluetoothData", MY_UUID);
            }
            catch (IOException e) {}
            mmServerSocket = tmp;
        }

        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {

                }
            }).start();

        }

        public void cancel() {

            try {
                if(mmServerSocket!=null) {
                    mmServerSocket.close();
                }
            }
            catch (IOException e) {}
        }
    }
    public synchronized int getState() {
        return mState;
    }

//    public synchronized void sendBleMessage(byte[] data) throws IOException {
//        mConnectedThread[0].SendBleMessage(data);
//    }


    public synchronized void stop() {
        if (mConnectedThread != null) {
            for(int i=0;i<mConnectedThread.length;i++)
            {
                mConnectedThread[i].cancel();
            }
            mConnectedThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        setState(STATE_NONE);
    }



    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread[0];
        }
        // Perform the write unsynchronized
        r.write(out);
    }
}
