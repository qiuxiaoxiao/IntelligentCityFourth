package com.jiangda.qiucheng.birdspreliminary;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.Arrays;

public class SendActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton imageButton1;
    private ImageButton imageButton2;
    private ImageButton imageButton3;
    private ImageButton imageButton4;
    private ImageButton imageButton5;
    private ImageButton imageButton6;
    private ImageButton imageButton7;
    private ImageButton imageButton8;
    private TextView device_name;
    private TextView device_Id;
    private TextView launch_nums;
    private TextView device_status;
    private TextView shell_nums;
    private TextView storage;
    private boolean isClick = true;
    private Button titleback;
    private Button device_refresh;
    int launchNums = 0;
    int shellNums = 0;

    /**
     * 蓝牙
     */
    private BluetoothService mBluetoothService; //自定义蓝牙服务类
    private BluetoothAdapter mBluetoothAdapter;
    private String mConnectedDeviceName = null; //连接设备的名称

    //默认是1,因为程序启动时首先会连接一个蓝牙
    private int current_pos = 1;
    //hanlder消息标识 message.what
    public static final int MESSAGE_STATE_CHANGE = 1; // 状态改变
    public static final int MESSAGE_READ = 2;          // 读取数据
    public static final int MESSAGE_WRITE = 3;         // 给硬件传数据，暂不需要，看具体需求
    public static final int MESSAGE_DEVICE_NAME = 4;  // 设备名字
    public static final int MESSAGE_TOAST = 5;         // Toast

    //传感器 ,这里默认同时需要和三个硬件连接，分别设置id 1,2,3进行区分，demo中实际只用到 MAGIKARE_SENSOR_DOWN = 1
    //可以根据情况自行添加删除
    public static final int MAGIKARE_SENSOR_DOWN = 1;
    public static float[] m_receive_data_down; //传感器的数据 ,demo中我们只需要这一个，因为只有一个硬件设备，
    public static byte[] m_receive_data_down1; //传感器的数据 ,demo中我们只需要这一个，因为只有一个硬件设备
//    public static char[] m_receive_data_down1; //传感器的数据 ,demo中我们只需要这一个，因为只有一个硬件设备
    public static byte[] m_send_data_down1; //传感器的数据 ,demo中我们只需要这一个，因为只有一个硬件设备
    private String readMessage;

    //电压
    int storage1=0;
    int storage2=0;
    String storageAll;



    //炮位状态
    String cannon0;
    String cannon1;
    String cannon2;
    String cannon3;
    String cannon4;
    String cannon5;
    String cannon6;
    String cannon7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        InitBluetooth();


        imageButton1 = (ImageButton) findViewById(R.id.imageBtn1);
        imageButton2 = (ImageButton) findViewById(R.id.imageBtn2);
        imageButton3 = (ImageButton) findViewById(R.id.imageBtn3);
        imageButton4 = (ImageButton) findViewById(R.id.imageBtn4);
        imageButton5 = (ImageButton) findViewById(R.id.imageBtn5);
        imageButton6 = (ImageButton) findViewById(R.id.imageBtn6);
        imageButton7 = (ImageButton) findViewById(R.id.imageBtn7);
        imageButton8 = (ImageButton) findViewById(R.id.imageBtn8);

        imageButton1.setOnClickListener(this);
        imageButton2.setOnClickListener(this);
        imageButton3.setOnClickListener(this);
        imageButton4.setOnClickListener(this);
        imageButton5.setOnClickListener(this);
        imageButton6.setOnClickListener(this);
        imageButton7.setOnClickListener(this);
        imageButton8.setOnClickListener(this);
        titleback = (Button) findViewById(R.id.title_back);
        titleback.setOnClickListener(this);
        device_refresh = (Button)findViewById(R.id.device_refresh);
        device_refresh.setOnClickListener(this);


        device_name = (TextView) findViewById(R.id.device_name);
        device_Id = (TextView) findViewById(R.id.device_Id);
        launch_nums = (TextView) findViewById(R.id.launch_nums);
        device_status = (TextView) findViewById(R.id.device_status);
        shell_nums = (TextView) findViewById(R.id.shell_nums);
        storage = (TextView) findViewById(R.id.storage);
    }

    @Override
    public void onClick(View v) {
        byte[] message = new byte[9];
        message[0] = 0x00;
        message[1] = 0x42;
        message[2] = 0x50;
        message[3] = 0x00;
        message[4] = 0x00;
        message[5] = (byte) 0xff;
        message[6] = (byte) 0xff;
        message[7] = (byte) 0xff;
        message[8] = 0x00;
        switch (v.getId()) {
            case R.id.device_refresh:
                message[5] = (byte) 0xaa;
                message[6] = (byte) 0xaa;
                message[7] = (byte) 0xaa;
                sendMessage(message);
                setUpAllTable();
                break;
            case R.id.imageBtn1:
                ImageClick(v);
                message[8] = 0x08;
                sendMessage(message);
                paowei();
                break;
            case R.id.imageBtn2:
                ImageClick(v);
                message[8] = 0x07;
                sendMessage(message);
                paowei();
                break;
            case R.id.imageBtn3:
                ImageClick(v);
                message[8] = 0x06;
                sendMessage(message);
                paowei();
                break;
            case R.id.imageBtn4:
                ImageClick(v);
                message[8] = 0x05;
                sendMessage(message);
                paowei();
                break;
            case R.id.imageBtn5:
                ImageClick(v);
                message[8] = 0x04;
                sendMessage(message);
                paowei();
                break;
            case R.id.imageBtn6:
                ImageClick(v);
                message[8] = 0x03;
                sendMessage(message);
                paowei();
                break;
            case R.id.imageBtn7:
                ImageClick(v);
                message[8] = 0x02;
                sendMessage(message);
                paowei();
                break;
            case R.id.imageBtn8:
                ImageClick(v);
                message[8] = 0x01;
                sendMessage(message);
                paowei();
                break;
            case R.id.title_back:
                Intent intent = new Intent(SendActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    //点击后切换背景
    private void ImageClick(View v) {
        if (isClick) {
            setShellLaunch();
            ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.credit_level));
        }
    }

    public void setShellLaunch(){
        shellNums--;
        String shell_Nums = Integer.toString(shellNums);
        shell_nums.setText(shell_Nums);

        launchNums++;
        String launch_Nums = Integer.toString(launchNums);
        launch_nums.setText(launch_Nums);
    }

    /**
     * 蓝牙
     */
    private void InitBluetooth(){
        //获取蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 1、判断设备是否支持蓝牙功能
        if (mBluetoothAdapter == null) {
            //设备不支持蓝牙功能
            Toast.makeText(this, "当前设备不支持蓝牙功能！", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2、打开设备的蓝牙功能
        if (!mBluetoothAdapter.isEnabled()) {
            boolean enable = mBluetoothAdapter.enable(); //返回值表示 是否成功打开了蓝牙设备
            if (enable) {
                Toast.makeText(this, "打开蓝牙功能成功！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "打开蓝牙功能失败，请到'系统设置'中手动开启蓝牙功能！", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // 3、创建自定义蓝牙服务对象
        if (mBluetoothService == null) {
            mBluetoothService = new BluetoothService(SendActivity.this, mHandler);
        }
        if (mBluetoothService != null) {
            //根据MAC地址远程获取一个蓝牙设备，这里固定了，实际开发中，需要动态设置参数（MAC地址）
            BluetoothDevice sensor_down = mBluetoothAdapter.getRemoteDevice("80:D6:D2:23:2A:A6");
            if (sensor_down != null) {
                //成功获取到远程蓝牙设备（传感器），这里默认只连接MAGIKARE_SENSOR_DOWN = 1这个设备
                mBluetoothService.connect(sensor_down, MAGIKARE_SENSOR_DOWN);
            }
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_WRITE://给硬件传数据
                    m_send_data_down1 = (byte[]) msg.obj;
                    Log.i("SendActivityWrite", Utils.byteConvert2String(m_send_data_down1,0,m_send_data_down1.length));
                    break;
                case MESSAGE_READ://读取数据
                    m_receive_data_down1 = (byte[]) msg.obj;

                    readMessage = msg.obj.toString();
                    readMessage = Utils.byteConvert2String(m_receive_data_down1, 0, msg.arg1);
                    Log.i("SendActivityRead", readMessage);
//                    setUpTable();
                    break;
                case MESSAGE_STATE_CHANGE://状态改变
//                    连接状态
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            break;
                        case BluetoothService.STATE_LISTEN:
                            break;
                        case BluetoothService.STATE_NONE:
                            break;
                    }
                    break;
                case MESSAGE_DEVICE_NAME://设备名字
                    mConnectedDeviceName = msg.getData().getString("device_name");
                    Toast.makeText(getApplicationContext(), "成功连接到设备" + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST://Toast
                    int index = msg.getData().getInt("device_id");
                    Toast.makeText(getApplicationContext(), msg.getData().getString("toast"), Toast.LENGTH_SHORT).show();
                    //重新连接硬件设备
                    if (mBluetoothService != null) {
                        switch (index) {
                            case MAGIKARE_SENSOR_DOWN:
                                //根据你的硬件的MAC地址写参数，每一个硬件设备都有一个MAC地址，此方法是根据MAC地址得到蓝牙设备
                                BluetoothDevice sensor_down = mBluetoothAdapter.getRemoteDevice("80:D6:D2:23:2A:A6");
                                if (sensor_down != null)
                                    mBluetoothService.connect(sensor_down, MAGIKARE_SENSOR_DOWN);
                                break;
                        }
                    }
                    break;
            }
            return false;
        }
    });

    private void toast(String str) {
        Toast.makeText(SendActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    public synchronized void onResume() {
        super.onResume();
        if (mBluetoothService != null) {
            if (mBluetoothService.getState() == BluetoothService.STATE_NONE) {
                mBluetoothService.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null) {
            mBluetoothService.stop();
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, "You are not connected to a device", Toast.LENGTH_SHORT).show();
            return;
        }
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            mBluetoothService.write(send);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A array of byte to send.
     */
    private void sendMessage(byte[] message) {
        if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, "You are not connected to a device", Toast.LENGTH_SHORT).show();
         }
        if (message.length > 0) {
            mBluetoothService.write(message);
        }
    }


    private void setUpTable() {
        if (readMessage != null && readMessage != "") {
            String[] read_array = readMessage.split(" ");
            for (int i = 0; i < read_array.length; i++) {
                Log.i("返回的数据", i + read_array[i]);
                if(i == 1){ //电压整数位
//                    storage1 = Integer.parseInt(read_array[1],16);
                    storage1 = m_receive_data_down1[1];
                    if (storage1 < 0){
                        storage1+= 256;
                    }
                }else if (i == 2){ //电压小数位
//                    storage2 = Integer.parseInt(read_array[2],16);
                    storage2 = m_receive_data_down1[2];
                    if (storage2 < 0){
                        storage2+= 256;
                    }
                    storageAll = Integer.toString(storage1) + "." + Integer.toString(storage2);
                    storage.setText(storageAll);
                }else if (i == 4){//炮位状态
//                    int cannon8 = Integer.parseInt(read_array[4],16);
                    int cannon8 = m_receive_data_down1[4];
                    if (cannon8<0){
                        cannon8 += 256;
                    }
                    String cannon8Status= Integer.toBinaryString(cannon8);
                    int x = cannon8Status.length();
                    for (int a=0;a<x;a++){
                        Log.i("ss",a + ":" + String.valueOf(cannon8Status.charAt(a)));
                        switch (a){
                            case 0:
                                cannon0 = String.valueOf(cannon8Status.charAt(0)) ;
                                if (cannon0.equals("1")){
                                    shellNums++;
                                    imageButton1.setImageDrawable(getResources().getDrawable(R.drawable.credit_level_filling));
                                }else if (cannon0.equals("0")){
                                    imageButton1.setImageDrawable(getResources().getDrawable(R.drawable.credit_level));
                                }
                                break;
                            case 1:
                                cannon1 = String.valueOf(cannon8Status.charAt(1));
                                if (cannon1.equals("1")){
                                    shellNums++;
                                    imageButton2.setImageDrawable(getResources().getDrawable(R.drawable.credit_level_filling));
                                }else if (cannon1.equals("0")){
                                    imageButton2.setImageDrawable(getResources().getDrawable(R.drawable.credit_level));
                                }
                                break;
                            case 2:
                                cannon2 = String.valueOf(cannon8Status.charAt(2));
                                if (cannon2.equals("1")){
                                    shellNums++;
                                    imageButton3.setImageDrawable(getResources().getDrawable(R.drawable.credit_level_filling));
                                }else if (cannon2.equals("0")){
                                    imageButton3.setImageDrawable(getResources().getDrawable(R.drawable.credit_level));
                                }
                                break;
                            case 3:
                                cannon3 = String.valueOf(cannon8Status.charAt(3));
                                if (cannon3.equals("1")){
                                    shellNums++;
                                    imageButton4.setImageDrawable(getResources().getDrawable(R.drawable.credit_level_filling));
                                }else if (cannon3.equals("0")){
                                    imageButton4.setImageDrawable(getResources().getDrawable(R.drawable.credit_level));
                                }
                                break;
                            case 4:
                                cannon4 = String.valueOf(cannon8Status.charAt(4));
                                if (cannon4.equals("1")){
                                    shellNums++;
                                    imageButton5.setImageDrawable(getResources().getDrawable(R.drawable.credit_level_filling));
                                }else if (cannon4.equals("0")){
                                    imageButton5.setImageDrawable(getResources().getDrawable(R.drawable.credit_level));
                                }
                                break;
                            case 5:
                                cannon5 = String.valueOf(cannon8Status.charAt(5));
                                if (cannon5.equals("1")){
                                    shellNums++;
                                    imageButton6.setImageDrawable(getResources().getDrawable(R.drawable.credit_level_filling));
                                }else if (cannon5.equals("0")){
                                    imageButton6.setImageDrawable(getResources().getDrawable(R.drawable.credit_level));
                                }
                                break;
                            case 6:
                                cannon6 = String.valueOf(cannon8Status.charAt(6));
                                if (cannon6.equals("1")){
                                    shellNums++;
                                    imageButton7.setImageDrawable(getResources().getDrawable(R.drawable.credit_level_filling));
                                }else if (cannon6.equals("0")){
                                    imageButton7.setImageDrawable(getResources().getDrawable(R.drawable.credit_level));
                                }
                                break;
                            case 7:
                                cannon7 = String.valueOf(cannon8Status.charAt(7));
                                if (cannon7.equals("1")){
                                    shellNums++;
                                    imageButton8.setImageDrawable(getResources().getDrawable(R.drawable.credit_level_filling));
                                }else if (cannon7.equals("0")){
                                    imageButton8.setImageDrawable(getResources().getDrawable(R.drawable.credit_level));
                                }
                                break;
                        }
                    }
                }else if (i == 7){
                    if (read_array[7].equals("00")){
                        Log.i("炮类型","煤气炮");
                        device_name.setText("煤气炮");
                    }else if (read_array[7].equals("01")){
                        Log.i("炮类型","钛雷炮");
                        device_name.setText("钛雷炮");
                    }else if (read_array[7].equals("02")){
                        Log.i("炮类型","二脚炮");
                        device_name.setText("二脚炮");
                    }
                }
            }
        }
    }


    private void paowei() {
        int cannon8 = m_receive_data_down1[4];
        if (cannon8 < 0) {
            cannon8 += 256;
        }
        String cannon8Status = Integer.toBinaryString(cannon8);
        int x = cannon8Status.length();
        for (int a = 0; a < x; a++) {
            Log.i("ss", a + ":" + String.valueOf(cannon8Status.charAt(a)));
            switch (a) {
                case 0:
                    cannon0 = String.valueOf(cannon8Status.charAt(0));
                    if (cannon0.equals("1")) {
                        shellNums++;
                        imageButton1.setImageDrawable(getResources().getDrawable(R.drawable.credit_level_filling));
                    } else if (cannon0.equals("0")) {
                        imageButton1.setImageDrawable(getResources().getDrawable(R.drawable.credit_level));
                    }
                    break;
                case 1:
                    cannon1 = String.valueOf(cannon8Status.charAt(1));
                    if (cannon1.equals("1")) {
                        shellNums++;
                        imageButton2.setImageDrawable(getResources().getDrawable(R.drawable.credit_level_filling));
                    } else if (cannon1.equals("0")) {
                        imageButton2.setImageDrawable(getResources().getDrawable(R.drawable.credit_level));
                    }
                    break;
                case 2:
                    cannon2 = String.valueOf(cannon8Status.charAt(2));
                    if (cannon2.equals("1")) {
                        shellNums++;
                        imageButton3.setImageDrawable(getResources().getDrawable(R.drawable.credit_level_filling));
                    } else if (cannon2.equals("0")) {
                        imageButton3.setImageDrawable(getResources().getDrawable(R.drawable.credit_level));
                    }
                    break;
                case 3:
                    cannon3 = String.valueOf(cannon8Status.charAt(3));
                    if (cannon3.equals("1")) {
                        shellNums++;
                        imageButton4.setImageDrawable(getResources().getDrawable(R.drawable.credit_level_filling));
                    } else if (cannon3.equals("0")) {
                        imageButton4.setImageDrawable(getResources().getDrawable(R.drawable.credit_level));
                    }
                    break;
                case 4:
                    cannon4 = String.valueOf(cannon8Status.charAt(4));
                    if (cannon4.equals("1")) {
                        shellNums++;
                        imageButton5.setImageDrawable(getResources().getDrawable(R.drawable.credit_level_filling));
                    } else if (cannon4.equals("0")) {
                        imageButton5.setImageDrawable(getResources().getDrawable(R.drawable.credit_level));
                    }
                    break;
                case 5:
                    cannon5 = String.valueOf(cannon8Status.charAt(5));
                    if (cannon5.equals("1")) {
                        shellNums++;
                        imageButton6.setImageDrawable(getResources().getDrawable(R.drawable.credit_level_filling));
                    } else if (cannon5.equals("0")) {
                        imageButton6.setImageDrawable(getResources().getDrawable(R.drawable.credit_level));
                    }
                    break;
                case 6:
                    cannon6 = String.valueOf(cannon8Status.charAt(6));
                    if (cannon6.equals("1")) {
                        shellNums++;
                        imageButton7.setImageDrawable(getResources().getDrawable(R.drawable.credit_level_filling));
                    } else if (cannon6.equals("0")) {
                        imageButton7.setImageDrawable(getResources().getDrawable(R.drawable.credit_level));
                    }
                    break;
                case 7:
                    cannon7 = String.valueOf(cannon8Status.charAt(7));
                    if (cannon7.equals("1")) {
                        shellNums++;
                        imageButton8.setImageDrawable(getResources().getDrawable(R.drawable.credit_level_filling));
                    } else if (cannon7.equals("0")) {
                        imageButton8.setImageDrawable(getResources().getDrawable(R.drawable.credit_level));
                    }
            }
        }
    }

    private void setUpAllTable(){
        launchNums = 0;
        shellNums = 0;
        setUpTable();

        String S_launchNums = Integer.toString(launchNums);
        String S_shellNums = Integer.toString(shellNums);

        device_Id.setText("6");
        device_status.setText("可控");
        launch_nums.setText(S_launchNums);
        shell_nums.setText(S_shellNums);
    }
}
