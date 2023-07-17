package com.example.myapplication.ui.home;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.ui.view.ClockView;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    ClockView clockViewC,clockViewL;
    public TextView textViewH,textViewR,textViewC,textViewY,textViewW,textViewCs;
    static String TAG = "csss";
    MainActivity mainActivity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mainActivity = (MainActivity) getActivity();
        initView();
        updateViewData();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * 初始化一些控件
     */
    void initView(){
        clockViewC = binding.homeClockView;
        clockViewL = binding.homeClockViewToo;
        textViewH = binding.homeTextViewH;
        textViewR = binding.homeTextViewR;
        textViewC = binding.homeTextViewC;
        textViewY =binding.homeTextViewY;
        textViewW = binding.homeTextViewW;
        textViewCs = binding.homeCs;

        int width = requireActivity().getWindowManager().getCurrentWindowMetrics().getBounds().width();
        ViewGroup.LayoutParams layoutParams = clockViewC.getLayoutParams();
        layoutParams.width = width/2-24;
        layoutParams.height = width/2-24;
        clockViewC.setLayoutParams(layoutParams);
        layoutParams = clockViewL.getLayoutParams();
        layoutParams.width = width/2-24;
        layoutParams.height = width/2-24;
        clockViewL.setLayoutParams(layoutParams);
        clockViewC.setRange(200);
        clockViewC.setUnit("℃");
        clockViewL.setRange(2000);
        clockViewL.setUnit("Lx");
        clockViewC.setCompleteDegree(0.0f);
        clockViewL.setCompleteDegree(0.0f);

        Drawable drawable = ContextCompat.getDrawable(requireContext(),R.mipmap.u16);
        if (drawable != null) {
            drawable.setBounds(0,0,128,128);
            textViewH.setCompoundDrawables(drawable,null,null,null);
            //textViewH.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.within_safety_not));
        }
        drawable = ContextCompat.getDrawable(requireContext(),R.mipmap.u14);
        if (drawable != null) {
            drawable.setBounds(0,0,128,128);
            textViewC.setCompoundDrawables(drawable,null,null,null);
        }
        drawable = ContextCompat.getDrawable(requireContext(),R.mipmap.u19);
        if (drawable != null) {
            drawable.setBounds(0,0,128,128);
            textViewY.setCompoundDrawables(drawable,null,null,null);
        }
        drawable = ContextCompat.getDrawable(requireContext(),R.mipmap.u25);
        if (drawable != null) {
            drawable.setBounds(0,0,128,128);
            textViewR.setCompoundDrawables(drawable,null,null,null);
        }
    }

    @SuppressLint("SetTextI18n")
    public void updateViewData(){
        if (mainActivity.WiFiFlag){
            textViewW.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.within_safety_indicators));
            textViewW.setText("WiFi-已连接");
        }else {
            textViewW.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.within_safety_not));
            textViewW.setText("WiFi-未连接");
        }
        clockViewL.setCompleteDegree(mainActivity.light*10);
        clockViewC.setCompleteDegree(mainActivity.temp);
        if (mainActivity.MQ_4Flag){
            textViewC.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.within_safety_not));
            textViewC.setText("CH4检测：异常  ");
        }else {
            textViewC.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.within_safety_indicators));
            textViewC.setText("CH4检测：正常  ");
        }
        if (mainActivity.MQ_2Flag){
            textViewY.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.within_safety_not));
            textViewY.setText("烟雾检测：异常  ");
        }else {
            textViewY.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.within_safety_indicators));
            textViewY.setText("烟雾检测：正常  ");
        }
        if (mainActivity.FireFlag){
            textViewH.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.within_safety_not));
            textViewH.setText("火焰检测：异常  ");
        }else {
            textViewH.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.within_safety_indicators));
            textViewH.setText("火焰检测：正常  ");
        }
        if (mainActivity.peopleFlag){
            textViewR.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.within_safety_not));
            textViewR.setText("人体检测：异常  ");
        }else {
            textViewR.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.within_safety_indicators));
            textViewR.setText("人体检测：正常  ");
        }
    }
}