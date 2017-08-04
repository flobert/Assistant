package com.colaapk.assistant;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    private static String CHUNYU_PACKAGE = "me.chunyu.Pedometer";
    private EditText mNumber;//步数
    private File mFile;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        mNumber = (EditText) findViewById(R.id.et);
        mFile = getInnerSDCardPath();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //Toast.makeText(this, Environment.getExternalStorageDirectory().getPath()+"111111111", Toast.LENGTH_LONG).show();
    }

    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btClick(View v) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        am.killBackgroundProcesses(CHUNYU_PACKAGE);
        if (mFile != null) {
            String mun = mNumber.getText().toString();
            if (TextUtils.isEmpty(mun)) {
                Toast.makeText(this, "请输入有效参数！！！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Integer.parseInt(mun) >= 99999) {
                Toast.makeText(this, "为了能领取到部分红包，步数将修改为99998", Toast.LENGTH_SHORT).show();
                mun = 99998 + "";
            }
            closeKeyboard();
            String data;
            String initString = FileUtils.getString(mFile);
            String[] sourceStrArray = initString.split(",");
            if (!TextUtils.isEmpty(sourceStrArray[0])) {
                data = sourceStrArray[0] + "," + mun;
            } else {
                data = "339" + "," + mun;
            }
            Log.i("MainActivity", "data: " + data);
            FileUtils.saveData(mFile, data);
            Toast.makeText(this, "修改成功！", Toast.LENGTH_SHORT).show();
            Intent intent = getPackageManager().getLaunchIntentForPackage(CHUNYU_PACKAGE);
            if (intent != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "你还没有安装春雨计步器,无法启动应用！！！", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "内存卡不存在或没有读写权限", Toast.LENGTH_SHORT).show();
        }


    }

    public File getInnerSDCardPath() {
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String d = sf.format(date);
        //Toast.makeText(this, d, Toast.LENGTH_SHORT).show();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/.Pedometer/.cypedometer/");
            if (!file.exists()) {
                file.mkdirs();
            }
            String path = file.getAbsolutePath() + File.separator + d;
            Log.i("MainActivity", "getInnerSDCardPath: " + path);
            File file1 = new File(path);
            try {
                file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file1;
        }
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_wx:
                startActivity(new Intent(MainActivity.this, WeiXinActivity.class));
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void closeKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
