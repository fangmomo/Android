package com.example.lenovo.hm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.lenovo.hm.utils.ResponseBodyPrinter;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class LogginActivity extends AppCompatActivity implements Validator.ValidationListener{
    @BindView(R.id.editText4)
    @NotEmpty(messageResId = R.string.errorMessage)
    @Order(1)
    EditText un;

    @BindView(R.id.button4)
    Button button;

    @BindView(R.id.editText6)
    @NotEmpty(messageResId = R.string.errorMessage)
    @Order(2)
    EditText psd;

    boolean loginistrue = false;
    int loggin = 0;

    private Validator validator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().hide();
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
            );
        }
        un = (EditText) findViewById(R.id.editText4);
        psd = (EditText) findViewById(R.id.editText6);

        validator = new Validator(this);
        validator.setValidationListener((Validator.ValidationListener) this);
        Button bt = (Button)findViewById(R.id.button4);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });
    }

    @Override
    public void onValidationSucceeded() {


        Runnable networkTask = new Runnable() {
            @Override
            public void run() {
                // TODO
                // 在这里进行 http request.网络请求相关操作
                // 演示 @FormUrlEncoded 和 @Field
                String email = un.getText().toString();
                String pasd = psd.getText().toString();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://172.29.114.57:4567/")
                        .build();
                BlogService service = retrofit.create(BlogService.class);
                Call<ResponseBody> call1 = service.testFormUrlEncoded1(email,pasd);
                Response<ResponseBody> response = null;
                try {
                    response = call1.execute();
                    if (response.isSuccessful()) {
                        System.out.println(response.body().string());
                        loginistrue = true;
                        loggin=1;
                    } else {
                        System.err.println("HttpCode:" + response.code());
                        System.err.println("Message:" + response.message());
                        System.err.println(response.errorBody().string());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(networkTask).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int i =0 ;
        while(true&&i<5) {
            if (loginistrue) {
                Intent intent = new Intent(LogginActivity.this, main_Activity.class);
                startActivity(intent);
                break;
            }
            try {
                Thread.sleep(1000);
                i++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
            return;
    }

    //校验失败
    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            }
        }
    }
    public interface BlogService {
        @POST("/form")
        @FormUrlEncoded
        Call<ResponseBody> testFormUrlEncoded1(@Field("username") String name, @Field("psd") String psd);
    }


}
