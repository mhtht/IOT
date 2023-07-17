package com.example.myapplication.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.MainActivity;
import com.example.myapplication.MainActivity2;
import com.example.myapplication.R;
import com.example.myapplication.data.DataEntered;
import com.example.myapplication.databinding.FragmentDashboardBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedList;


public class DashboardFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private FragmentDashboardBinding binding;
    MainActivity mainActivity;

    SwitchCompat switchCompatFan,switchCompatLed, switchCompatCurtain;
    FloatingActionButton floatingActionButton;
    ListView listView;

    public LinkedList<DataEntered> list;

    @SuppressLint("StaticFieldLeak")
    public static DashboardFragment dashboardFragment1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initView();
        mainActivity = (MainActivity) getActivity();
        dashboardFragment1 = this;
        switchCompatLed.setOnCheckedChangeListener(this);
        switchCompatCurtain.setOnCheckedChangeListener(this);
        switchCompatFan.setOnCheckedChangeListener(this);
        switchCompatLed.setChecked(mainActivity.ledFlag);
        switchCompatCurtain.setChecked(mainActivity.curtainFlag);
        switchCompatFan.setChecked(mainActivity.fanFlag);
        list = mainActivity.list;
        floatingActionButton.setOnClickListener((view)->{
            Intent intent = new Intent();
            intent.setClass(getActivity(), MainActivity2.class);
            intent.putExtra("taskNum", list.size());
            startActivity(intent);
        });
        listView.setAdapter(new Item(mainActivity,this));

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        //setSwitch(mainActivity.fanFlag, mainActivity.curtainFlag, mainActivity.ledFlag);
        listView.setAdapter(new Item(mainActivity,this));
    }
    public void restartList(){
        listView.setAdapter(new Item(mainActivity,this));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    void initView(){
        switchCompatFan = binding.SwitchCompatFan;
        switchCompatCurtain = binding.SwitchCompatCurtain;
        switchCompatLed = binding.SwitchCompatLed;
        listView = binding.listView;
        floatingActionButton = binding.FloatingActionButton;
    }

    /**
     * 监听switch开关变化
     * @param compoundButton switch开关
     * @param b 开关状态
     */
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();
        byte[] bytes = new byte[2];
        if (id == R.id.SwitchCompat_fan){
            bytes [0] = 0x02;
            mainActivity.fanFlag = b;
        }else if (id == R.id.SwitchCompat_curtain){
            bytes [0] = 0x03;
            mainActivity.curtainFlag = b;
        } else if (id ==R.id.SwitchCompat_led) {
            bytes [0] = 0x04;
            mainActivity.ledFlag = b;
        }else {
            return;
        }
        if (b){
            bytes[1] = 0x21;
            mainActivity.setSendData(bytes);
        }else {
            bytes[1] = 0x20;
            mainActivity.setSendData(bytes);
        }
    }
    public void setSwitch(boolean fanFlag ,boolean curtainFlag,boolean ledFlag){
//        switchCompatFan.setChecked(mainActivity.fanFlag);
//        switchCompatCurtain.setChecked(mainActivity.curtainFlag);
//        switchCompatLed.setChecked(mainActivity.ledFlag);
    }
}



class Item extends BaseAdapter{
    MainActivity mainActivity;
    DashboardFragment dashboardFragment;
    int count = 0;

    Item(MainActivity mainActivity,DashboardFragment dashboardFragment){
        this.mainActivity = mainActivity;
        this.dashboardFragment = dashboardFragment;
    }
    @Override
    public int getCount() {
        return dashboardFragment.list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder") View view1 = View.inflate(mainActivity,R.layout.item,null);
        DataEntered dataEntered = dashboardFragment.list.get(i);
        String string = "如果"+dataEntered.in;
        ImageView imageViewIn = view1.findViewById(R.id.task_image_in);
        ImageView imageViewOut = view1.findViewById(R.id.task_image_out);
        TextView textViewName = view1.findViewById(R.id.task_name);
        TextView textViewTodo = view1.findViewById(R.id.task_todo);
        Button button = view1.findViewById(R.id.task_del);
        imageViewIn.setImageDrawable(dataEntered.drawableIn);
        imageViewOut.setImageDrawable(dataEntered.drawableOut);
        textViewName.setText(dataEntered.taskName);
        if (dataEntered.intFlag){
            if (dataEntered.greaterThanFlag){
                string+="大于";
            }else {
                string+="小于";
            }
            string += dataEntered.number;
        }else {
            if (dataEntered.TriggerAlarmFlag){
                string+="开启";
            }else {
                string+="关闭";
            }
        }
        string+= "则"+dataEntered.outTodo;
        if (dataEntered.offOn){
            string+="开启";
        }else {
            string+="关闭";
        }
        textViewTodo.setText(string);
        button.setOnClickListener(view2 -> {
            dashboardFragment.list.remove(i);
            dashboardFragment.restartList();
        });
        return view1;
    }
}