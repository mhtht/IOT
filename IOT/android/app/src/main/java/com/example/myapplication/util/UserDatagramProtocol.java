package com.example.myapplication.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author LinM
 * 工具类用于UDP数据传输
 */
public class UserDatagramProtocol {

    private static final String TAG = "UDP";
    byte[] espIp;
    int port;
    int receivePort = 4321;
    InetAddress serverAddress;
    DatagramSocket receiveSocket;
    DatagramSocket sendSocket;
    DatagramPacket outPacket;
    boolean udpLink = false;
    static byte[] sendCmd = new byte[]{1,2};

    Handler handler;

    byte[] udpData = {(byte) 0xBB,(byte) 0x01,(byte) 0x01,(byte) 0x01};

    ThreadPoolExecutor threadPool;

    /**
     * 构造函数传入IP和端口
     */
    public UserDatagramProtocol(byte[] espIp, int port, Handler handler){
        this.espIp = Arrays.copyOf(espIp,4);
        this.port = port;
        this.handler = handler;

    }

    public void udpLink(){
        try {
            sendSocket = new DatagramSocket();
            Log.d("carMiniUdp", "udpLink: 构造DatagramSocket对象(成功)");
        } catch (SocketException e) {
            Log.d("carMiniUdp", "udpLink: 构造DatagramSocket对象(失败)");
            e.printStackTrace();
            Message msg = Message.obtain();
            msg.what = 0;
            msg.obj = "监听失败：构造DatagramSocket对象(失败)";
            handler.sendMessage(msg);
            return;
        }
        try {
            Log.d("carMiniUdp", "udpLink: "+Arrays.toString(espIp));
            serverAddress = InetAddress.getByAddress(espIp);
            Log.d("carMiniUdp", "udpLink: 根据InetAddress对象、发送端口号、发送数据 来创建" +
                    "发送的DatagramPacket数据包对象(成功)");
        } catch (UnknownHostException e) {
            Log.d("carMiniUdp", "udpLink: 根据InetAddress对象、发送端口号、发送数据 来创建" +
                    "发送的DatagramPacket数据包对象(失败)");
            e.printStackTrace();
            Message msg = Message.obtain();
            msg.what = 0;
            msg.obj = "监听失败：创建发送的DatagramPacket数据包对象(失败)";
            handler.sendMessage(msg);
            return;
        }
        udpLink = true;
        Message msg = Message.obtain();
        msg.what = 1;
        msg.obj = "监听启动";
        handler.sendMessage(msg);
        getData();
    }

    public void udpClose() {
        sendSocket.close();
        udpLink = false;
        Message msg = Message.obtain();
        msg.what = 0;
        msg.obj = "监听关闭";
        handler.sendMessage(msg);
    }

    public void sendDataGo(byte[] data){
        byte[] sendData= new byte[data.length+3];
        int i = 0;
        long checkNum = 0;
        sendData[i++] = 0x55;
        for (byte by:data){
            sendData[i++] = by;
            checkNum += by & 0xFF;
        }
        sendData[i++] = (byte)(checkNum & 0xFF);
        sendData[i] = (byte) 0xBB;
        outPacket = new DatagramPacket(sendData, sendData.length, this.serverAddress, this.port);
        try {
            sendSocket.send(outPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Log.d(TAG, "sendDataGo: 发送成功"+ Arrays.toString(sendData));
    }


    public void getData(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Log.d(TAG, "getData: cs1");
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(()->{
            while (true) {
                sendDataGo(sendCmd);
                Log.d(TAG, "getData: cs");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "getData: 强制退出线程", e);
                    return;
                }
            }
        });
        receiveData();
        executor.shutdownNow();
    }

    @SuppressLint("DefaultLocale")
    public void receiveData(){
        try {
            receiveSocket = new DatagramSocket(receivePort);

        } catch (SocketException e) {
            e.printStackTrace();
        }
        String str;
        byte[] inBuf= new byte[1024];
        DatagramPacket inPacket=new DatagramPacket(inBuf,inBuf.length);
        try {
            receiveSocket.receive(inPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        str = new String(inPacket.getData(),0,inPacket.getLength());
        Log.d("carMiniUdp", "receiveData: "+str);
        Message msg = Message.obtain();
        msg.what = 3;
        msg.obj = str;
        handler.sendMessage(msg);
        receiveSocket.close();
    }

}
