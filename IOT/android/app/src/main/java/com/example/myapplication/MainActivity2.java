package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.myapplication.data.DataEntered;
import com.example.myapplication.ui.dashboard.DashboardFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = "dataEnteredActivity";
    public static final String[] stringsIf = new String[]{"温度数值","亮度数值","火焰警报","烟雾警报","甲烷警报","人员警报"};
    public static final String[] stringsDo = new String[]{"电灯","风扇","窗帘"};
    Button buttonIf,buttonDo;
    EditText editText;

    FloatingActionButton floatingActionButton;

    DataEntered dataEntered;
    DashboardFragment dashboardFragment;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();
        dashboardFragment = DashboardFragment.dashboardFragment1;
        buttonIf = findViewById(R.id.button_if);
        buttonDo = findViewById(R.id.button_do);
        editText = findViewById(R.id.task_name_init);
        floatingActionButton = findViewById(R.id.main2_check);
        dataEntered = new DataEntered();
        buttonIf.setOnClickListener((view)-> popUpsIf());
        buttonDo.setOnClickListener((view) -> popUpsDo());
        floatingActionButton.setOnClickListener(view -> {
            if (dataEntered.in==null||dataEntered.outTodo==null){
                Toast.makeText(this,"请输入数据",Toast.LENGTH_SHORT).show();
                return;
            }
            String s =  editText.getText().toString();
            if (s.equals("")){
                if (dashboardFragment.list.size()==0){
                    s = "新建任务1";
                }else {
                    s = "新建任务"+ (dashboardFragment.list.getLast().taskNameMax+1);
                    dataEntered.taskNameMax = dashboardFragment.list.getLast().taskNameMax+1;
                }

            }else if (s.contains("新建任务")){
                Toast.makeText(this,"都取名了就别叫新建任务了",Toast.LENGTH_SHORT).show();
                return;
            }
            dataEntered.taskName = s;
            dashboardFragment.list.add(dataEntered);

            finish();
        });

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void changeButtonIf(){
        Drawable drawable = dataEntered.drawableIn;
        String s;
        s = dataEntered.in;
        if (dataEntered.intFlag){
            if (dataEntered.greaterThanFlag){
                s += "大于";
            }else {
                s += "小于";
            }
            s += String.valueOf(dataEntered.number);
        }else {
            if (dataEntered.TriggerAlarmFlag){
                s+="开启";
            }else {
                s+="关闭";
            }
        }
        buttonIf.setText(s);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        buttonIf.setCompoundDrawables(drawable,null,null,null);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void changeButtonDo(){
        Drawable drawable = dataEntered.drawableOut;
        String s;
        s = dataEntered.outTodo;
        if (dataEntered.offOn){
            s+="开启";
        }else {
            s+="关闭";
        }
        buttonDo.setText(s);
        drawable.setBounds(0, 0, 48, 48);
        buttonDo.setCompoundDrawables(drawable,null,null,null);
    }

    void popUpsIf(){
        AlertDialog alertDialogIf = new AlertDialog.Builder(this)
                .setIcon(R.drawable.baseline_add_24)
                .setTitle("添加的条件")
                .setItems(stringsIf, (dialogInterface, i) -> {
                    if(i<=1){
                        popUpNum(stringsIf[i]);
                    } else if (i<=5) {
                        popUpSwitch(stringsIf[i]);
                    }
                }).create();
        alertDialogIf.show();
    }

    /**
     * 设置弹窗输入亮度或温度的数值
     * @param s stringsIf中的值
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    void popUpNum(String s){
        Drawable drawable;
        if (s.equals(stringsIf[0])){
            drawable = getDrawable(R.drawable.baseline_device_thermostat_24);
        } else if (s.equals(stringsIf[1])){
            drawable = getDrawable(R.drawable.baseline_light_mode_24);
        }else {
            Log.e(TAG, "popUpNum: 输入了一个莫名其妙的参："+s);
            return;
        }
        View view = View.inflate(this, R.layout.input_int_dialog,null);
        EditText editText = view.findViewById(R.id.set_num);
        Button button = view.findViewById(R.id.check_button);
        RadioButton radioButtonBig = view.findViewById(R.id.big_button);
        AlertDialog alertDialogNum = new AlertDialog.Builder(this)
                .setIcon(drawable)
                .setTitle("请输入"+s)
                .setView(view)
                .create();
        alertDialogNum.show();
        button.setOnClickListener((v -> {
            String stringEditText = editText.getText().toString();
            if (!stringEditText.equals("")){
                dataEntered.drawableIn=drawable;
                dataEntered.in = s;
                dataEntered.intFlag=true;
                dataEntered.number = Integer.parseInt(stringEditText);
                dataEntered.greaterThanFlag = radioButtonBig.isChecked();
                changeButtonIf();
                alertDialogNum.dismiss();
            }else {
                Toast.makeText(this,"请输入数据",Toast.LENGTH_SHORT).show();
            }
        }));
    }

    /**
     * 设置弹窗输入报警
     * @param s stringsIf中的值
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    void popUpSwitch(String s){
        Drawable drawable;
        if (s.equals(stringsIf[2])){
            drawable = getDrawable(R.drawable.baseline_local_fire_department_24);
        }else if (s.equals(stringsIf[3])){
            drawable = getDrawable(R.drawable.baseline_follow_the_signs_24);
        }else if (s.equals(stringsIf[4])){
            drawable = getDrawable(R.drawable.baseline_fire_extinguisher_24);
        }else if (s.equals(stringsIf[5])){
            drawable = getDrawable(R.drawable.baseline_man_24);
        }else {
            Log.e(TAG, "popUpNum: 输入了一个莫名其妙的参："+s);
            return;
        }
        View view = View.inflate(this,R.layout.input_switch_dialog,null);
        Button button = view.findViewById(R.id.check_button_switch);
        RadioButton radioButtonBig = view.findViewById(R.id.alert_true);
        AlertDialog alertDialogSwitch = new AlertDialog.Builder(this)
                .setIcon(drawable)
                .setTitle("请选择"+s+"的状态")
                .setView(view)
                .create();
        alertDialogSwitch.show();
        button.setOnClickListener(v -> {
            dataEntered.drawableIn=drawable;
            dataEntered.in = s;
            dataEntered.intFlag=false;
            dataEntered.TriggerAlarmFlag = radioButtonBig.isChecked();
            changeButtonIf();
            alertDialogSwitch.dismiss();
        });
    }

    void popUpsDo(){
        AlertDialog alertDialogDo = new AlertDialog.Builder(this)
                .setIcon(R.drawable.baseline_add_24)
                .setTitle("添加的结果")
                .setItems(stringsDo, (dialogInterface, i) -> popUpSwitchOut(stringsDo[i])).create();
        alertDialogDo.show();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void popUpSwitchOut(String s){
        Drawable drawable;
        if (s.equals(stringsDo[0])){
            drawable = getDrawable(R.mipmap.u6);
        }else if (s.equals(stringsDo[1])){
            drawable = getDrawable(R.mipmap.u7);
        }else if (s.equals(stringsDo[2])){
            drawable = getDrawable(R.mipmap.u15);
        }else {
            Log.e(TAG, "popUpNum: 输入了一个莫名其妙的参："+s);
            return;
        }
        View view = View.inflate(this,R.layout.output_switch_dialog,null);
        Button button = view.findViewById(R.id.check_button_switch_output);
        RadioButton radioButton = view.findViewById(R.id.alert_true_out);
        AlertDialog alertDialogSwitch = new AlertDialog.Builder(this)
                .setIcon(drawable)
                .setTitle("请选择"+s+"的状态")
                .setView(view)
                .create();
        alertDialogSwitch.show();
        button.setOnClickListener(v -> {
            dataEntered.drawableOut = drawable;
            dataEntered.offOn = radioButton.isChecked();
            dataEntered.outTodo = s;
            changeButtonDo();
            alertDialogSwitch.dismiss();
        });
    }
}

