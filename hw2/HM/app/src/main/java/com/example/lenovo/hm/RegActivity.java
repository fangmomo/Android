package com.example.lenovo.hm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.lenovo.hm.Entity.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
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
import retrofit2.adapter.rxjava.Result;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class RegActivity extends AppCompatActivity implements Validator.ValidationListener{
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

    @BindView(R.id.editText)
    @Email(messageResId = R.string.errorMessage)
    @Order(3)
    EditText em;

    boolean isReg = false;

    private Validator validator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().hide();
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
            );
        }
        validator = new Validator(this);
        validator.setValidationListener((Validator.ValidationListener) this);

        un= (EditText) findViewById(R.id.editText4);
        psd = (EditText) findViewById(R.id.editText6);
        em = (EditText) findViewById(R.id.editText);

        Button bt = (Button)findViewById(R.id.button4);
        Button bt2 = (Button)findViewById(R.id.button2);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegActivity.this, daohan.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onValidationSucceeded() {
        Runnable networkTask = new Runnable() {
            @Override
            public void run() {

                String  username= un.getText().toString();
                String pasd = psd.getText().toString();
                String email = em.getText().toString();
                // TODO
                // 在这里进行 http request.网络请求相关操作
                // 演示 @FormUrlEncoded 和 @Field
                Gson gson = new GsonBuilder()
                        //配置你的Gson
                        .setDateFormat("yyyy-MM-dd hh:mm:ss")
                        .create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://10.0.2.2:4567/")
                        //可以接收自定义的Gson，当然也可以不传
                        .addConverterFactory(GsonConverterFactory.create(gson)).build();

                BlogService service = retrofit.create(BlogService.class);
                User user = new User();
                user.psd=pasd;
                user.username=username;
                user.email=email;

                Call<Result<User>> call = service.createBlog(user);
                call.enqueue(new Callback<Result<User>>() {
                    @Override
                    public void onResponse(Call<Result<User>> call, Response<Result<User>> response) {
                        // 已经转换为想要的类型了
                        Result<User> result = response.body();
                        isReg = true;
                        System.out.println(result.toString());
                    }
                    @Override
                    public void onFailure(Call<Result<User>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        };
        new Thread(networkTask).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(isReg) {
            Intent intent = new Intent(RegActivity.this, LogginActivity.class);
            startActivity(intent);
        }else
            return;

    }

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
        @POST("blog")
        Call<Result<User>> createBlog(@Body User user);
    }
}
