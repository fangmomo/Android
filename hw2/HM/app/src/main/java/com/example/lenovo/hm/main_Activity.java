package com.example.lenovo.hm;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.example.lenovo.hm.adapter.MyFragmentPagerAdapter;
import com.example.lenovo.hm.fragment.homeFragment;
import com.example.lenovo.hm.fragment.personFragment;
import com.example.lenovo.hm.fragment.searchFragment;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class main_Activity  extends FragmentActivity implements  OnPageChangeListener {
    private ViewPager myviewpager;
    //fragment的集合，对应每个子页面
    private ArrayList<Fragment> fragments;
    //选项卡中的按钮
    private RadioButton btn_first;
    private RadioButton btn_second;
    private RadioButton btn_four;

    private ImageView cursor;
    float cursorX = 0;
    private int[] widthArgs;
    private Button[] btnArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initView();

    }


    public void initView(){
        myviewpager = (ViewPager)this.findViewById(R.id.myviewpager);

        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller( myviewpager.getContext());
            mScroller.set( myviewpager, scroller);
        }catch(NoSuchFieldException e){

        }catch (IllegalArgumentException e){

        }catch (IllegalAccessException e){

        }

        btn_first = (RadioButton)this.findViewById(R.id.btn_first);
        btn_second = (RadioButton)this.findViewById(R.id.btn_second);
        btn_four = (RadioButton)this.findViewById(R.id.btn_four);


        btnArgs = new Button[]{btn_first,btn_second,btn_four};

       cursor = (ImageView)this.findViewById(R.id.cursor_btn);

        cursor.setBackgroundColor(Color.RED);


        myviewpager.setOnPageChangeListener(this);
       /* btn_first.setOnClickListener(this);
        btn_second.setOnClickListener(this);
        btn_third.setOnClickListener(this);
        btn_four.setOnClickListener(this);  */

        btn_first.setOnCheckedChangeListener(new InnerOnCheckedChangeListener());
        btn_second.setOnCheckedChangeListener(new InnerOnCheckedChangeListener());
        btn_four.setOnCheckedChangeListener(new InnerOnCheckedChangeListener());


        fragments = new ArrayList<Fragment>();
        fragments.add(new homeFragment());
        fragments.add(new searchFragment());
        fragments.add(new personFragment());
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        myviewpager.setAdapter(adapter);

        resetButtonColor();
          btn_first.post(new Runnable(){
            @Override
            public void run() {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)cursor.getLayoutParams();
                lp.width = btn_first.getWidth()-btn_first.getPaddingLeft()*2;
                cursor.setLayoutParams(lp);
                cursor.setX(btn_first.getPaddingLeft());
            }
        });
    }

    private class InnerOnCheckedChangeListener implements OnCheckedChangeListener{
        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
            switch (buttonView.getId()) {
                case R.id.btn_first:
                    if(isChecked){
                        myviewpager.setCurrentItem(0);
                        cursorAnim(0);

                    }

                    break;
                case R.id.btn_second:
                    if(isChecked){
                        myviewpager.setCurrentItem(1);
                        cursorAnim(1);
                    }

                    break;
                case R.id.btn_four:
                    if(isChecked){
                        myviewpager.setCurrentItem(2);
                        cursorAnim(2);
                    }

                    break;

                default:
                    break;
            }

        }



    }

    //重置所有按钮的颜色
    public void resetButtonColor(){
        btn_first.setBackgroundColor(Color.parseColor("#DCDCDC"));
        btn_second.setBackgroundColor(Color.parseColor("#DCDCDC"));
        btn_four.setBackgroundColor(Color.parseColor("#DCDCDC"));

        btn_first.setTextColor(Color.BLACK);
        btn_second.setTextColor(Color.BLACK);
        btn_four.setTextColor(Color.BLACK);

    }


    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
        if(widthArgs==null){
            widthArgs = new int[]{btn_first.getWidth(),
                    btn_second.getWidth(),
                    //btn_third.getWidth(),
                    btn_four.getWidth()};
        }
        resetButtonColor();
        btnArgs[arg0].setTextColor(Color.RED);

        cursorAnim(arg0);
        ((CompoundButton) btnArgs[arg0]).setChecked(true);


    }

    public void cursorAnim(int curItem){
        cursorX = 0;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)cursor.getLayoutParams();
        lp.width = widthArgs[curItem]-btnArgs[0].getPaddingLeft()*2;
        cursor.setLayoutParams(lp);
        for(int i=0; i<curItem; i++){
            cursorX = cursorX + btnArgs[i].getWidth();
        }
        cursor.setX(cursorX+btnArgs[curItem].getPaddingLeft());
    }

    /**
     * 通过文件名获取资源id 例子：getResId("icon", R.drawable.class)
     * @param variableName
     * @param c
     * @return
     */
    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
