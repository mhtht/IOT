package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.data.DataEntered;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.ui.dashboard.DashboardFragment;
import com.example.myapplication.ui.home.HomeFragment;
import com.example.myapplication.util.UserDatagramProtocol;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    static byte[] ESP_IP = {(byte)192,(byte)168,(byte)3,(byte)1};
    static int PORT = 1234;
    static final String TAG = "myApp_main";
    public boolean WiFiFlag = false;
    public int light,temp;
    public boolean MQ_4Flag, MQ_2Flag, FireFlag, peopleFlag;
    public boolean ledFlag,fanFlag,curtainFlag;

    public LinkedList<DataEntered> list;

    UserDatagramProtocol datagramProtocol;
    ThreadPoolExecutor threadPool;

    HomeFragment fragmentHome;
    public DashboardFragment dashboardFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        threadPool = new ThreadPoolExecutor(
                8,
                16,
                4,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        list = new LinkedList<>();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        fragmentHome = (HomeFragment) getFragment(HomeFragment.class);
        dashboardFragment = (DashboardFragment)getFragment(DashboardFragment.class);
        Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {

                Log.d(TAG, "handleMessage: "+msg.what);
                switch (msg.what){
                    case 0:
                        WiFiFlag = false;
                        break;
                    case 1:
                        WiFiFlag = true;
                        break;
                    case 3:
                        if (!msg.obj.toString().equals("")){
                            readData(msg.obj.toString());
                            for (DataEntered data:list){
                                examineDataEntered(data);
                                threadPool.execute(()->controlModule());
                                DashboardFragment dashboardFragment1 = (DashboardFragment)getFragment(DashboardFragment.class);
                                if (dashboardFragment1!=null){
                                    dashboardFragment1.setSwitch(fanFlag,curtainFlag,ledFlag);
                                }
                            }
                        }
                        threadPool.execute(()->datagramProtocol.getData());
                        break;
                }
                fragmentHome.updateViewData();
                super.handleMessage(msg);
            }
        };
        datagramProtocol =new UserDatagramProtocol(ESP_IP, PORT,handler);
        threadPool.execute(()->datagramProtocol.udpLink());
    }
    public void setSendData(byte[] bytes){
        threadPool.execute(()-> datagramProtocol.sendDataGo(bytes));
    }
    public Fragment getFragment(Class<?> clazz) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() > 0) {
            NavHostFragment navHostFragment = (NavHostFragment) fragments.get(0);
            List<Fragment> childfragments = navHostFragment.getChildFragmentManager().getFragments();
            if(childfragments.size() > 0){
                for (int j = 0; j < childfragments.size(); j++) {
                    Fragment fragment = childfragments.get(j);
                    if(fragment.getClass().isAssignableFrom(clazz)){
                        Log.i(TAG, "HomeFragment: " + fragment);
                        return fragment;
                    }
                }
            }
        }
        return null;
    }
    /**
     * 读取传入数据并解析
     * @param string esp传入数据
     */
    void readData(String string){
        TextView testText = fragmentHome.textViewCs;
        HashMap<String, Integer> dataHashMap = new HashMap<>();
        String[] arrOfStr;
        arrOfStr = string.split(",");
        if (arrOfStr.length<=1){
            return;
        }
        for (String a : arrOfStr) {
            String[] arrOfStr2 = a.split("=");
            int integer;
            try {
                integer = Integer.parseInt(arrOfStr2[1]);
                dataHashMap.put(arrOfStr2[0],integer);
            }catch (NumberFormatException e){
                String sTemp = "\n数据传入："+arrOfStr2[0]+"错误内容为:\""+arrOfStr2[1]+"\"尝试修正:";
                testText.append(sTemp);
                arrOfStr2[1] = arrOfStr2[1].replaceAll("[^\\d-]", "");
                try {
                    integer = Integer.parseInt(arrOfStr2[1]);
                    dataHashMap.put(arrOfStr2[0],integer);
                    sTemp = "修正成功";
                    testText.append(sTemp);
                }catch (NumberFormatException re)
                {
                    sTemp = "修正失败,数值为："+arrOfStr2[1];
                    testText.append(sTemp);
                    Log.e(TAG, "setData: ", re);
                }
                Log.e(TAG, "setData: ", e);
            }
        }
        if (dataHashMap.containsKey("light")){
            Integer integer = dataHashMap.get("light");
            if(integer!= null){
                light = integer;
            }
            else{
                testText.append("\nlight传入数据为空");
            }
        }
        if (dataHashMap.containsKey("temp")){
            Integer integer = dataHashMap.get("temp");
            if(integer!= null){
                temp = integer;
            }
            else{
                testText.append("\ntemp传入数据为空");
            }
        }

        if (dataHashMap.containsKey("MQ_4")){
            Integer integer = dataHashMap.get("MQ_4");
            if(integer!= null){
                MQ_4Flag = integer <= 0;
            }
            else{
                testText.append("\nMQ_4传入数据为空");
            }
        }
        if (dataHashMap.containsKey("MQ_2")){
            Integer integer = dataHashMap.get("MQ_2");
            if(integer!= null){
                MQ_2Flag = integer <=0;
            }
            else{
                testText.append("\nMQ_2传入数据为空");
            }
        }
        if (dataHashMap.containsKey("Fire")){
            Integer integer = dataHashMap.get("Fire");
            if(integer!= null){
                FireFlag = integer<=0;
            }
            else{
                testText.append("\nFire传入数据为空");
            }
        }
        if (dataHashMap.containsKey("people")){
            Integer integer = dataHashMap.get("people");
            if(integer!= null){
                peopleFlag = integer <= 0;
            }
            else{
                testText.append("\npeople传入数据为空");
            }
        }
    }

    void examineDataEntered(DataEntered dataEntered){
        boolean todoFlag = false;
        if (dataEntered.intFlag){
            if (dataEntered.greaterThanFlag){
                if (dataEntered.in.equals(MainActivity2.stringsIf[0])){
                    if (temp > dataEntered.number){
                        todoFlag = true;
                    }
                }else if (dataEntered.in.equals(MainActivity2.stringsIf[1])){
                    if (light > dataEntered.number){
                        todoFlag = true;
                    }
                }else {
                    Log.e(TAG, "examineDataEntered:奇怪的数据 "+dataEntered.in);
                }
            }else {
                if (dataEntered.in.equals(MainActivity2.stringsIf[0])){
                    if (temp < dataEntered.number){
                        todoFlag = true;
                    }
                }else if (dataEntered.in.equals(MainActivity2.stringsIf[1])){
                    if (light < dataEntered.number){
                        todoFlag = true;
                    }
                }else {
                    Log.e(TAG, "examineDataEntered:奇怪的数据 "+dataEntered.in);
                }
            }
        }else {
            if (dataEntered.in.equals(MainActivity2.stringsIf[2])){
                if (FireFlag==dataEntered.TriggerAlarmFlag){
                    todoFlag =true;
                }
            }else if (dataEntered.in.equals(MainActivity2.stringsIf[3])){
                if (MQ_2Flag==dataEntered.TriggerAlarmFlag){
                    todoFlag =true;
                }
            }else if (dataEntered.in.equals(MainActivity2.stringsIf[4])){
                if (MQ_4Flag==dataEntered.TriggerAlarmFlag){
                    todoFlag =true;
                }
            }else if (dataEntered.in.equals(MainActivity2.stringsIf[5])){
                if (peopleFlag==dataEntered.TriggerAlarmFlag){
                    todoFlag =true;
                }
            }else {
                Log.e(TAG, "examineDataEntered:奇怪的数据 "+dataEntered.in);
            }
        }
        if (todoFlag){
            if (dataEntered.outTodo.equals(MainActivity2.stringsDo[0])){
                ledFlag = dataEntered.offOn;
            } else if (dataEntered.outTodo.equals(MainActivity2.stringsDo[1])){
                fanFlag = dataEntered.offOn;
            }else if (dataEntered.outTodo.equals(MainActivity2.stringsDo[2])){
                curtainFlag = dataEntered.offOn;
            }
        }
    }

    void controlModule(){
        byte[] bytes = new byte[2];
        bytes [0] = 0x04;
        if (ledFlag){
            bytes[1] = 0x21;
        }else {
            bytes[1] = 0x20;
        }
        setSendData(bytes);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Log.e(TAG, "controlModule: ", e);
        }

        bytes [0] = 0x02;
        if (fanFlag){
            bytes[1] = 0x21;
        }else {
            bytes[1] = 0x20;
        }
        setSendData(bytes);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Log.e(TAG, "controlModule: ", e);
        }

        bytes [0] = 0x03;
        if (curtainFlag){
            bytes[1] = 0x21;
        }else {
            bytes[1] = 0x20;
        }
        setSendData(bytes);
    }
}