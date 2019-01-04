package com.nightonke.saver.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nightonke.saver.R;
import com.nightonke.saver.adapter.ButtonGridViewAdapter;
import com.nightonke.saver.adapter.EditMoneyRemarkFragmentAdapter;
import com.nightonke.saver.adapter.TagChooseFragmentAdapter;
import com.nightonke.saver.fragment.CoCoinFragmentManager;
import com.nightonke.saver.fragment.TagChooseFragment;
import com.nightonke.saver.model.CoCoinRecord;
import com.nightonke.saver.model.RecordManager;
import com.nightonke.saver.model.SettingManager;
import com.nightonke.saver.model.User;
import com.nightonke.saver.ui.CoCoinScrollableViewPager;
import com.nightonke.saver.ui.MyGridView;
import com.nightonke.saver.util.CoCoinUtil;
import com.squareup.leakcanary.LeakCanary;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobUser;

public class MainActivity extends AppCompatActivity
        implements
        TagChooseFragment.OnTagItemSelectedListener {

    private final int SETTING_TAG = 0;

    private Context mContext;


    private TextView toolBarTitle;


    private MyGridView myGridView;
    private ButtonGridViewAdapter myGridViewAdapter;


    private boolean isPassword = false;

    //private View guillotineMenu;

    private ViewPager tagViewPager;
    private CoCoinScrollableViewPager editViewPager;
    private FragmentPagerAdapter tagAdapter;
    private FragmentPagerAdapter editAdapter;

    private boolean isLoading;


    boolean doubleBackToExitPressedOnce = false;



    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.root)
    FrameLayout root;
    @InjectView(R.id.content_hamburger)
    View contentHamburger;

    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LeakCanary.install(this.getApplication());
        setContentView(R.layout.activity_main);

        mContext = this;

       RecordManager.getInstance(CoCoinApplication.getAppContext());
       CoCoinUtil.init(CoCoinApplication.getAppContext());


        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(listener, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(listener, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);


        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        Log.d("Saver", "Version number: " + currentapiVersion);

        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.statusBarColor));
        } else{
            // do something for phones running an SDK before lollipop
        }

        User user = BmobUser.getCurrentUser(CoCoinApplication.getAppContext(), User.class);
        if (user != null) {
            SettingManager.getInstance().setLoggenOn(true);
            SettingManager.getInstance().setUserName(user.getUsername());
            SettingManager.getInstance().setUserEmail(user.getEmail());

            // 允许用户使用应用
        } else {
            SettingManager.getInstance().setLoggenOn(false);
            //缓存用户对象为空时， 可打开用户注册界面…
        }


        toolBarTitle = (TextView)findViewById(R.id.guillotine_title);
        toolBarTitle.setTypeface(CoCoinUtil.typefaceLatoLight);
        toolBarTitle.setText(SettingManager.getInstance().getAccountBookName());

        editViewPager = (CoCoinScrollableViewPager) findViewById(R.id.edit_pager);
        editAdapter = new EditMoneyRemarkFragmentAdapter(getSupportFragmentManager(), CoCoinFragmentManager.MAIN_ACTIVITY_FRAGMENT);
        
        editViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 1) {
                    if (CoCoinFragmentManager.mainActivityEditRemarkFragment != null)
                        CoCoinFragmentManager.mainActivityEditRemarkFragment.editRequestFocus();
                } else {
                    if (CoCoinFragmentManager.mainActivityEditMoneyFragment != null)
                        CoCoinFragmentManager.mainActivityEditMoneyFragment.editRequestFocus();
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        editViewPager.setAdapter(editAdapter);

// tag viewpager////////////////////////////////////////////////////////////////////////////////////
        tagViewPager = (ViewPager)findViewById(R.id.viewpager);

        if (RecordManager.getInstance(mContext).TAGS.size() % 8 == 0)
            tagAdapter = new TagChooseFragmentAdapter(getSupportFragmentManager(), RecordManager.TAGS.size() / 8);
        else
            tagAdapter = new TagChooseFragmentAdapter(getSupportFragmentManager(), RecordManager.TAGS.size() / 8 + 1);
        tagViewPager.setAdapter(tagAdapter);

// button grid view/////////////////////////////////////////////////////////////////////////////////
        myGridView = (MyGridView)findViewById(R.id.gridview);
        myGridViewAdapter = new ButtonGridViewAdapter(this);
        myGridView.setAdapter(myGridViewAdapter);

        myGridView.setOnItemClickListener(gridViewClickListener);
        myGridView.setOnItemLongClickListener(gridViewLongClickListener);

        myGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        myGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        View lastChild = myGridView.getChildAt(myGridView.getChildCount() - 1);
                        myGridView.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.FILL_PARENT, lastChild.getBottom()));

                        //ViewGroup.LayoutParams params = transparentLy.getLayoutParams();
                        //params.height = myGridView.getMeasuredHeight();
                    }
                });

        ButterKnife.inject(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        toolbar.hideOverflowMenu();

        contentHamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoading = true;
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(mContext, AccountBookTodayViewActivity.class);
                        startActivityForResult(intent, SETTING_TAG);
                        isLoading = false;
                    }
                }, 1000);
            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        if (SettingManager.getInstance().getShowMainActivityGuide()) {
            boolean wrapInScrollView = true;
            new MaterialDialog.Builder(this)
                    .title(R.string.guide)
                    .typeface(CoCoinUtil.GetTypeface(), CoCoinUtil.GetTypeface())
                    .customView(R.layout.main_activity_guide, wrapInScrollView)
                    .positiveText(R.string.ok)
                    .show();
            SettingManager.getInstance().setShowMainActivityGuide(false);
        }
    }

    private AdapterView.OnItemLongClickListener gridViewLongClickListener
            = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (!isLoading) {
                buttonClickOperation(true, position);
            }
            return true;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SETTING_TAG:
                if (resultCode == RESULT_OK) {
                    if (data.getBooleanExtra("IS_CHANGED", false)) {
                        for (int i = 0; i < tagAdapter.getCount() && i < CoCoinFragmentManager.tagChooseFragments.size(); i++) {
                            if (CoCoinFragmentManager.tagChooseFragments.get(i) != null)
                                CoCoinFragmentManager.tagChooseFragments.get(i).updateTags();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }


    private AdapterView.OnItemClickListener gridViewClickListener
            = new AdapterView.OnItemClickListener() {
        ///here
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!isLoading) {
                buttonClickOperation(false, position);
            }
        }
    };

    private void buttonClickOperation(boolean longClick, int position) {
        if (editViewPager.getCurrentItem() == 1) return;

        if (!isPassword) {
            if (CoCoinFragmentManager.mainActivityEditMoneyFragment.getNumberText().toString().equals("0")
                    && !CoCoinUtil.ClickButtonCommit(position)) {
                if (CoCoinUtil.ClickButtonDelete(position)
                        || CoCoinUtil.ClickButtonIsZero(position)) {

                } else {
                    CoCoinFragmentManager.mainActivityEditMoneyFragment.setNumberText(CoCoinUtil.BUTTONS[position]);
                }
            } else {
                if (CoCoinUtil.ClickButtonDelete(position)) {
                    if (longClick) {
                        CoCoinFragmentManager.mainActivityEditMoneyFragment.setNumberText("0");
                        CoCoinFragmentManager.mainActivityEditMoneyFragment.setHelpText(
                                CoCoinUtil.FLOATINGLABELS[CoCoinFragmentManager.mainActivityEditMoneyFragment
                                        .getNumberText().toString().length()]);
                    } else {
                        CoCoinFragmentManager.mainActivityEditMoneyFragment.setNumberText(
                                CoCoinFragmentManager.mainActivityEditMoneyFragment.getNumberText().toString()
                                .substring(0, CoCoinFragmentManager.mainActivityEditMoneyFragment
                                        .getNumberText().toString().length() - 1));
                        if (CoCoinFragmentManager.mainActivityEditMoneyFragment
                                .getNumberText().toString().length() == 0) {
                            CoCoinFragmentManager.mainActivityEditMoneyFragment.setNumberText("0");
                            CoCoinFragmentManager.mainActivityEditMoneyFragment.setHelpText(" ");
                        }
                    }
                    //here
                } else if (CoCoinUtil.ClickButtonCommit(position)) {
                    commit();
                } else {
                    CoCoinFragmentManager.mainActivityEditMoneyFragment.setNumberText(
                            CoCoinFragmentManager.mainActivityEditMoneyFragment.getNumberText().toString()
                                    + CoCoinUtil.BUTTONS[position]);
                }
            }
            CoCoinFragmentManager.mainActivityEditMoneyFragment
                    .setHelpText(CoCoinUtil.FLOATINGLABELS[
                            CoCoinFragmentManager.mainActivityEditMoneyFragment.getNumberText().toString().length()]);
       //mima
        } else {
        }
    }

    private void commit() {
        if (CoCoinFragmentManager.mainActivityEditMoneyFragment.getTagId() == -1) {

        } else if (CoCoinFragmentManager.mainActivityEditMoneyFragment.getNumberText().toString().equals("0")) {

        } else  {
            Calendar calendar = Calendar.getInstance();
            CoCoinRecord coCoinRecord = new CoCoinRecord(
                    -1,
                    Float.valueOf(CoCoinFragmentManager.mainActivityEditMoneyFragment.getNumberText().toString()),
                    "RMB",
                    CoCoinFragmentManager.mainActivityEditMoneyFragment.getTagId(),
                    calendar);

            coCoinRecord.setRemark(CoCoinFragmentManager.mainActivityEditRemarkFragment.getRemark());
            long saveId = RecordManager.saveRecord(coCoinRecord);
            if (saveId == -1) {

            } else {
                CoCoinFragmentManager.mainActivityEditMoneyFragment.setTagImage(R.color.transparent);
                CoCoinFragmentManager.mainActivityEditMoneyFragment.setTagName("");
            }
            CoCoinFragmentManager.mainActivityEditMoneyFragment.setNumberText("0");
            CoCoinFragmentManager.mainActivityEditMoneyFragment.setHelpText(" ");
        }
    }

    @Override
    public void onBackPressed() {
        if (isPassword) {
            return;
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        doubleBackToExitPressedOnce = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onResume() {
        super.onResume();

        isLoading = false;
        System.gc();
    }

    @Override
    public void onDestroy() {

        if (sensorManager != null) {
            sensorManager.unregisterListener(listener);
        }

        super.onDestroy();

    }

    @Override
    public void onTagItemPicked(int position) {
        if (CoCoinFragmentManager.mainActivityEditMoneyFragment != null)
            CoCoinFragmentManager.mainActivityEditMoneyFragment.setTag(tagViewPager.getCurrentItem() * 8 + position + 2);
    }

    @Override
    public void onAnimationStart(int id) {
        // Todo add animation for changing tag
    }

    private static final float SHAKE_ACCELERATED_SPEED = 15;
    private SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

}
