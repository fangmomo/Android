package com.nightonke.saver.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.internal.MDButton;
import com.balysv.materialripple.MaterialRippleLayout;
import com.dd.CircularProgressButton;
import com.github.johnpersano.supertoasts.SuperToast;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nightonke.saver.BuildConfig;
import com.nightonke.saver.R;
import com.nightonke.saver.model.Logo;
import com.nightonke.saver.model.RecordManager;
import com.nightonke.saver.model.SettingManager;
import com.nightonke.saver.model.User;
import com.nightonke.saver.ui.RiseNumberTextView;
import com.nightonke.saver.util.CoCoinUtil;
import com.nightonke.saver.util.EmailValidator;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountBookSettingActivity extends AppCompatActivity
    implements
        View.OnClickListener
        {

    private final int UPDATE_LOGO = 0;
    private final int UPDATE_ACCOUNT_BOOK_NAME = 7;
    private final int UPDATE_SHOW_PICTURE = 9;
    private final int UPDATE_LOGO_ID = 11;

    private Context mContext;

    private MaterialIconView back;

    private File logoFile;
    private CircleImageView logo;
    private Bitmap logoBitmap;

    private MaterialEditText registerUserName;
    private MaterialEditText registerUserEmail;
    private MaterialEditText registerPassword;
    private MaterialEditText loginUserName;
    private MaterialEditText loginPassword;

    private MaterialRippleLayout profileLayout;
    private MaterialIconView userNameIcon;
    private MaterialIconView userEmailIcon;
    private TextView userName;
    private TextView userEmail;
    private TextView loginButton;
    private RiseNumberTextView expense;
    private TextView expenseTV;
    private RiseNumberTextView records;
    private TextView recordsTV;

    private MaterialRippleLayout accountBookNameLayout;
    private TextView accountBookNameTV;
    private TextView accountBookName;


    private Tencent mTencent;
    public static String APP_ID = "101534861";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());
        mContext = this;
        setContentView(R.layout.activity_account_book_setting);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.statusBarColor));
        } else{
            // do something for phones running an SDK before lollipop
            View statusBarView = findViewById(R.id.status_bar_view);
            statusBarView.getLayoutParams().height = CoCoinUtil.getStatusBarHeight();
        }

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        SuperToast.cancelAllSuperToasts();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
    }

// Load logo from local/////////////////////////////////////////////////////////////////////////////
    private void loadLogo() {
        User user = BmobUser.getCurrentUser(CoCoinApplication.getAppContext(), User.class);
        if (user != null) {
            try {
                File logoFile = new File(CoCoinApplication.getAppContext().getFilesDir() + CoCoinUtil.LOGO_NAME);
                if (!logoFile.exists()) {
                    // the local logo file is missed
                    // try to get from the server
                    BmobQuery<Logo> bmobQuery = new BmobQuery();
                    bmobQuery.addWhereEqualTo("objectId", user.getLogoObjectId());
                    bmobQuery.findObjects(CoCoinApplication.getAppContext()
                            , new FindListener<Logo>() {
                                @Override
                                public void onSuccess(List<Logo> object) {
                                    // there has been an old logo in the server/////////////////////////////////////////////////////////
                                    String url = object.get(0).getFile().getFileUrl(CoCoinApplication.getAppContext());
                                    if (BuildConfig.DEBUG) Log.d("CoCoin", "Logo in server: " + url);
                                    Ion.with(CoCoinApplication.getAppContext()).load(url)
                                            .write(new File(CoCoinApplication.getAppContext().getFilesDir()
                                                    + CoCoinUtil.LOGO_NAME))
                                            .setCallback(new FutureCallback<File>() {
                                                @Override
                                                public void onCompleted(Exception e, File file) {
                                                    logo.setImageBitmap(BitmapFactory.decodeFile(
                                                            CoCoinApplication.getAppContext().getFilesDir()
                                                                    + CoCoinUtil.LOGO_NAME));
                                                }
                                            });
                                }
                                @Override
                                public void onError(int code, String msg) {
                                    // the picture is lost
                                    if (BuildConfig.DEBUG) Log.d("CoCoin", "Can't find the old logo in server.");
                                }
                            });
                } else {
                    // the user logo is in the storage
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(logoFile));
                    logo.setImageBitmap(b);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            // use the default logo
            logo.setImageResource(R.drawable.default_user_logo);
        }
    }

// After select a picture///////////////////////////////////////////////////////////////////////////
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, new BaseUiListener());

        if(requestCode == Constants.REQUEST_API) {
            if(resultCode == Constants.REQUEST_LOGIN) {
                Tencent.handleResultData(data, new BaseUiListener());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


// User log out/////////////////////////////////////////////////////////////////////////////////////
    private void userLogout() {
        updateViews();
    }


// Change account book name/////////////////////////////////////////////////////////////////////////
    private void changeAccountBookName() {
        new MaterialDialog.Builder(this)
                .theme(Theme.LIGHT)
                .typeface(CoCoinUtil.GetTypeface(), CoCoinUtil.GetTypeface())
                .title(R.string.set_account_book_dialog_title)
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(1, 16)
                .positiveText(R.string.submit)
                .input(SettingManager.getInstance().getAccountBookName()
                        , null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // local change
                        SettingManager.getInstance().setAccountBookName(input.toString());
                        SettingManager.getInstance().setTodayViewTitleShouldChange(true);
                        SettingManager.getInstance().setMainViewTitleShouldChange(true);
                        accountBookName.setText(input.toString());
                    }
                }).show();
    }

// Update some views when login/////////////////////////////////////////////////////////////////////
    private void updateViews() {
        setIconEnable(userNameIcon, SettingManager.getInstance().getLoggenOn());
        setIconEnable(userEmailIcon, SettingManager.getInstance().getLoggenOn());
        if (SettingManager.getInstance().getLoggenOn()) {
            userName.setText(SettingManager.getInstance().getUserName());
            userEmail.setText(SettingManager.getInstance().getUserEmail());
            loginButton.setText(mContext.getResources().getText(R.string.logout_button));
            loginButton.setBackgroundResource(R.drawable.button_logout);
        } else {
            userName.setText("");
            userEmail.setText("");
            loginButton.setText(mContext.getResources().getText(R.string.login_button));
            loginButton.setBackgroundResource(R.drawable.button_login);
        }
    }

// Init the setting activity////////////////////////////////////////////////////////////////////////
    private void init() {
        back = (MaterialIconView)findViewById(R.id.icon_left);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        logo = (CircleImageView)findViewById(R.id.profile_image);
        logo.setOnClickListener(this);
        profileLayout = (MaterialRippleLayout)findViewById(R.id.profile_layout);
        userNameIcon = (MaterialIconView)findViewById(R.id.user_name_icon);
        userEmailIcon = (MaterialIconView)findViewById(R.id.user_email_icon);
        userName = (TextView)findViewById(R.id.user_name);
        userName.setTypeface(CoCoinUtil.typefaceLatoLight);
        userEmail = (TextView)findViewById(R.id.user_email);
        userEmail.setTypeface(CoCoinUtil.typefaceLatoLight);
        loginButton = (TextView)findViewById(R.id.login_button);
        loginButton.setTypeface(CoCoinUtil.typefaceLatoLight);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!SettingManager.getInstance().getLoggenOn())
                    mTencent.login(AccountBookSettingActivity.this,"all", new BaseUiListener());
                else{
                    SettingManager.getInstance().setUserName("");
                    SettingManager.getInstance().setUserEmail("");
                    SettingManager.getInstance().setLoggenOn(false);
                }
            }
        });
        expense = (RiseNumberTextView)findViewById(R.id.expense);
        expense.setTypeface(CoCoinUtil.typefaceLatoLight);
        records = (RiseNumberTextView)findViewById(R.id.records);
        records.setTypeface(CoCoinUtil.typefaceLatoLight);
        expenseTV = (TextView)findViewById(R.id.expense_text);
        expenseTV.setTypeface(CoCoinUtil.GetTypeface());
        recordsTV = (TextView)findViewById(R.id.records_text);
        recordsTV.setTypeface(CoCoinUtil.GetTypeface());

        expense.withNumber(RecordManager.SUM).setDuration(1500).start();
        records.withNumber(RecordManager.RECORDS.size()).setDuration(1500).start();

        accountBookNameLayout = (MaterialRippleLayout)findViewById(R.id.account_book_name_layout);
        accountBookNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAccountBookName();
            }
        });
        accountBookName = (TextView)findViewById(R.id.account_book_name);
        accountBookName.setTypeface(CoCoinUtil.GetTypeface());
        accountBookName.setText(SettingManager.getInstance().getAccountBookName());
        accountBookNameTV = (TextView)findViewById(R.id.account_book_name_text);
        accountBookNameTV.setTypeface(CoCoinUtil.GetTypeface());


        boolean loggenOn = SettingManager.getInstance().getLoggenOn();
        if (loggenOn) {
            // is logged on, set the user name and email
            userName.setText(SettingManager.getInstance().getUserName());
            userEmail.setText(SettingManager.getInstance().getUserEmail());
            loginButton.setText(mContext.getResources().getText(R.string.logout_button));
            loginButton.setBackgroundResource(R.drawable.button_logout);
        } else {
            userName.setText("");
            userEmail.setText("");
            loginButton.setText(getResourceString(R.string.login_button));
        }
        setIconEnable(userNameIcon, loggenOn);
        setIconEnable(userEmailIcon, loggenOn);
        loadLogo();
    }

    private void setIconEnable(MaterialIconView icon, boolean enable) {
        if (enable) icon.setColor(mContext.getResources().getColor(R.color.my_blue));
        else icon.setColor(mContext.getResources().getColor(R.color.my_gray));
    }
// Get string///////////////////////////////////////////////////////////////////////////////////////
    private String getResourceString(int resourceId) {
        return CoCoinApplication.getAppContext().getResources().getString(resourceId);
    }

// activity finish//////////////////////////////////////////////////////////////////////////////////
    public static String openidString = null;
    @Override
    public void finish() {

        SuperToast.cancelAllSuperToasts();

        super.finish();
    }
            private class BaseUiListener implements IUiListener {
                @Override
                public void onComplete(Object response) {
                    try {
                        System.out.println(((JSONObject) response).toString());
                        openidString = ((JSONObject) response).getString("openid");
                        mTencent.setOpenId(openidString);
                        mTencent.setAccessToken(((JSONObject) response).getString("access_token"),((JSONObject) response).getString("expires_in"));
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    QQToken qqToken = mTencent.getQQToken();
                    UserInfo info = new UserInfo(getApplicationContext(), qqToken);

                    info.getUserInfo(new IUiListener() {
                        @Override
                        public void onComplete(Object o) {
                            //用户信息获取到了
                            try {
                                System.out.println(((JSONObject) o).toString());
                                SettingManager.getInstance().setUserName(((JSONObject) o).getString("nickname"));
                                SettingManager.getInstance().setUserEmail("1149160879@qq.com");
                                SettingManager.getInstance().setLoggenOn(true);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            updateViews();
                        }
                        @Override
                        public void onError(UiError uiError) {
                        }
                        @Override
                        public void onCancel() {
                        }
                    });
                }

                @Override
                public void onError(UiError uiError) {
                }

                @Override
                public void onCancel() {
                }
            }
}
