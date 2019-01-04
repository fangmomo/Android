package com.nightonke.saver.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nightonke.saver.R;
import com.nightonke.saver.activity.CoCoinApplication;
import com.nightonke.saver.model.Feedback;
import com.nightonke.saver.util.CoCoinUtil;

import cn.bmob.v3.listener.SaveListener;
public class HelpFeedbackFragment extends Fragment {

    private boolean chineseIsDoubleCount = true;
    private TextView title;
    private EditText input;
    private TextView help;
    private TextView number;
    private TextView send;

    private int min = 1;
    private int max = 400;
    private boolean exceed = false;

    public static HelpFeedbackFragment newInstance() {
        HelpFeedbackFragment fragment = new HelpFeedbackFragment();
        return fragment;
    }

    private Activity activity;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            activity = (Activity)context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_feedback_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        title = (TextView)view.findViewById(R.id.title);
        title.setTypeface(CoCoinUtil.getInstance().typefaceLatoLight);
        input = (EditText)view.findViewById(R.id.edittext);
        input.setTypeface(CoCoinUtil.getInstance().typefaceLatoLight);
        help = (TextView)view.findViewById(R.id.helper);
        help.setTypeface(CoCoinUtil.getInstance().typefaceLatoLight);
        number = (TextView)view.findViewById(R.id.number);
        number.setTypeface(CoCoinUtil.getInstance().typefaceLatoLight);
        send = (TextView)view.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exceed) {
                    new MaterialDialog.Builder(mContext)
                            .title(R.string.help_feedback_dialog_title)
                            .content(R.string.help_feedback_dialog_content)
                            .positiveText(R.string.ok_1)
                            .show();
                } else {
                    //CoCoinUtil.getInstance().showToast(CoCoinApplication.getAppContext(), CoCoinApplication.getAppContext().getResources().getString(R.string.help_feedback_sent));
                    Feedback feedback = new Feedback();
                    feedback.setContent(input.getText().toString());

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("18801292026", null, feedback.getContent(), null, null);
                    Toast.makeText(mContext,"发送成功",Toast.LENGTH_SHORT).show();
                }
            }
        });

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setNumberText();
                try {
                    ((OnTextChangeListener)activity)
                            .onTextChange(input.getText().toString(), exceed);
                } catch (ClassCastException cce){
                    cce.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        send.requestFocus();

        setNumberText();
    }

    private void setNumberText() {
        int count = -1;
        if (chineseIsDoubleCount) {
            count = CoCoinUtil.getInstance().textCounter(input.getText().toString());
        } else {
            count =input.getText().toString().length();
        }
        number.setText(count + "/" + min + "-" + max);
        if (min <= count && count <= max) {
            number.setTextColor(ContextCompat.getColor(mContext, R.color.my_blue));
            exceed = false;
        } else {
            number.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            exceed = true;
        }
    }

    public interface OnTextChangeListener {
        void onTextChange(String text, boolean exceed);
    }

}
