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

    //作为指示标签的按钮
    private ImageView cursor;
    //标志指示标签的横坐标
    float cursorX = 0;
    //所有按钮的宽度的集合
    private int[] widthArgs;
    //所有按钮的集合
    private Button[] btnArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initView();

    }


    public void initView(){
        myviewpager = (ViewPager)this.findViewById(R.id.myviewpager);

        //ViewPager的setCurrentItem是跳转到ViewPager的指定页面，
        //但在使用这个方法的时候有个问题，跳转的时候有滑动效果，
        //当需要从当前页面跳转到其它页面时，跳转页面跨度过大、
        //或者ViewPager每个页面的视觉效果相差较大时，通过这种方式实现ViewPager跳转显得很不美观

        //解决办法:
        //我们可以去掉在使用ViewPager的setCurrentItem方法时的滑屏速度
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

        //重置所有按钮颜色
        resetButtonColor();
        //把第一个按钮的颜色设置为红色
        btn_first.setTextColor(Color.RED);
        //为什么不直接cursor.setWidth()和cursor.setX()
        //因为Android系统绘制原理是只有全部遍历测量之后才会布局，
        //只有在整个布局绘制完毕后，视图才能得到自身的高和宽。
        //所以在正常情况下，在OnCreate()方法中直接获取控件的宽度和高度取得值是0。
        //而我们此处设置指示器的大小和位置都需要用到第一个按钮的大小作为参考值，
        //所以可以通过post将一个runnable投递到消息队列的尾部，然后等待UI线程Looper调用此runnable的时候，view也已经初始化好了。这个时候就能成功获取控件的宽高
        btn_first.post(new Runnable(){
            @Override
            public void run() {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)cursor.getLayoutParams();
                //减去边距*2，以对齐标题栏文字
                lp.width = btn_first.getWidth()-btn_first.getPaddingLeft()*2;
                cursor.setLayoutParams(lp);
                cursor.setX(btn_first.getPaddingLeft());
            }
        });
    }

    //把事件的内部类定义出来
    private class InnerOnCheckedChangeListener implements OnCheckedChangeListener{
        //单选按钮选中事件方法
        //buttonView表示谁的状态被改变
        //isChecked上面的参数代表的状态是否选中
        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
            switch (buttonView.getId()) {
                case R.id.btn_first:
                    //单选按钮通过参数isChecked去得到当前到底是选中还是未选中
                    if(isChecked){
                        myviewpager.setCurrentItem(0);
                        cursorAnim(0);

                    }

                    break;
                case R.id.btn_second:
                    //单选按钮通过参数isChecked去得到当前到底是选中还是未选中
                    if(isChecked){
                        myviewpager.setCurrentItem(1);
                        cursorAnim(1);
                    }

                    break;
                case R.id.btn_four:
                    //单选按钮通过参数isChecked去得到当前到底是选中还是未选中
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
        //btn_third.setBackgroundColor(Color.parseColor("#DCDCDC"));
        btn_four.setBackgroundColor(Color.parseColor("#DCDCDC"));

        btn_first.setTextColor(Color.BLACK);
        btn_second.setTextColor(Color.BLACK);
        //btn_third.setTextColor(Color.BLACK);
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
        //每次滑动首先重置所有按钮的颜色
        resetButtonColor();

        //将滑动到的当前按钮颜色设置为红色
        btnArgs[arg0].setTextColor(Color.RED);

        cursorAnim(arg0);

        //把当前页面的单选按钮设置为选中状态
        ((CompoundButton) btnArgs[arg0]).setChecked(true);


    }

    //指示器的跳转，传入当前所处的页面的下标
    public void cursorAnim(int curItem){
        //每次调用，就将指示器的横坐标设置0，即开始的位置
        cursorX = 0;
        //再根据当前的curItem来设置指示器的宽度
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)cursor.getLayoutParams();
        //首先获得当前按钮的宽度，再减去按钮左右边距距，以对齐标题栏文本
        lp.width = widthArgs[curItem]-btnArgs[0].getPaddingLeft()*2;
        //通过指示标签对象，将标签设置到父容器中
        cursor.setLayoutParams(lp);
        //循环获取当前页之前的所有页面的宽度
        for(int i=0; i<curItem; i++){
            cursorX = cursorX + btnArgs[i].getWidth();
        }
        //再加上当前页面的左边距，即为指示器当前应处的位置
        cursor.setX(cursorX+btnArgs[curItem].getPaddingLeft());
    }

    /**
     * 通过文件名获取资源id 例子：getResId("icon", R.drawable.class);
     *
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
