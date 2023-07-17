package com.example.myapplication;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Locale;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
        String f = " -1";
        System.out.println(f);
        f = f.replaceAll("[^\\d-]", "");
        System.out.println(f);
    }



    public void setData(String string){
        int light = 0;
        for (int i = string.indexOf("light=") + "light=".length();string.charAt(i)!=',';i++){
            light *= 10;
            light += Integer.parseInt(String.valueOf(string.charAt(i)));
        }

    }
}