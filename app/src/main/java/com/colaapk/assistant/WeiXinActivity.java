package com.colaapk.assistant;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeiXinActivity extends AppCompatActivity {

    public static final String USERS = "users";
    public static final String ID = "ID";
    private EditText mNum;
    private EditText mId;
    private Button mConmmit;
    private SharedPreferences mSharedPreferences;
    private OkHttpClient mOkHttpClient;
    private TextView mDownload;
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wei_xin);
        initView();

        if (mSharedPreferences != null) {
            mId.setText(mSharedPreferences.getString(ID, "").toString());
        }
    }

    private void initView() {
        mToolBar = (Toolbar) findViewById(R.id.wx_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeiXinActivity.this.finish();
            }
        });
        mId = (EditText) findViewById(R.id.id);
        mNum = (EditText) findViewById(R.id.num);
        mConmmit = (Button) findViewById(R.id.bt_wx);
        mDownload = (TextView) findViewById(R.id.download);
        mDownload.setMovementMethod(LinkMovementMethod.getInstance());
        mSharedPreferences = getSharedPreferences(USERS, MODE_PRIVATE);
    }

    public void btWXClick(View view) {
        String id = mId.getText().toString();
        String num = mNum.getText().toString();
        if (mSharedPreferences != null && !TextUtils.isEmpty(id)) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(ID, id);
            editor.apply();
        } else {
            Toast.makeText(this, "用户ID不能为空！！！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.isEmpty(num)) {
            closeKeyboard();
            if (Integer.parseInt(num) >= 99998) {
                num = 99998 + "";
                Toast.makeText(this, "修改的数据过大，已经强制修改为99998", Toast.LENGTH_SHORT).show();
            }
            mConmmit.setClickable(false);
            mConmmit.setText("正在提交数据");
            String getUrl = "http://weixin.droi.com/health/phone/index.php/SendWechat/send?accountId=" + id + "&jibuNuber=" + num;
            GetAsynHttpUtil.newGetAsynHttpUtil().get(getUrl, new GetHttpListener() {
                @Override
                public void onError() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mConmmit.setClickable(true);
                            mConmmit.setText("修改步数");
                            ToastUtils.showToast(WeiXinActivity.this, "数据提交失败，请重新提交");
                        }
                    });
                }

                @Override
                public void onSuccess(final String json) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            ResultInfo info = gson.fromJson(json, ResultInfo.class);
                            mConmmit.setClickable(true);
                            mConmmit.setText("修改步数 ");
                            ToastUtils.showToast(WeiXinActivity.this, info.getMesssage());
                        }
                    });
                }
            });
        } else {
            Toast.makeText(this, "请输入有效步数！！！", Toast.LENGTH_SHORT).show();
            return;
        }


    }

    private void closeKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
