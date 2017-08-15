package com.colaapk.assistant;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    private static String CHUNYU_PACKAGENAME = "me.chunyu.Pedometer";
    private static String CHUNYU_CLASS_NAME = "me.chunyu.Pedometer.PedometerActivity";

    private EditText mNumber;//步数
    private File mFile;
    private Toolbar mToolbar;
    private ActivityManager mActivityManager;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        mNumber = (EditText) findViewById(R.id.et);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mFile = FileUtils.getExternalSDCardPath();
        new Thread(new Runnable() {
            @Override
            public void run() {
                checkUpdata();
            }
        }).start();
    }

    /*
    * 动态权限申请
    * */
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

    /*
    * 刷步
    * */
    public void btClick(View v) {
        //kill 春雨
        mActivityManager.killBackgroundProcesses(CHUNYU_PACKAGENAME);
        if (mFile != null) {
            String mun = mNumber.getText().toString();
            if (TextUtils.isEmpty(mun)) {
                ToastUtils.show(this, "请输入有效参数！！！");
                return;
            }
            if (Integer.parseInt(mun) >= 99999) {
                ToastUtils.show(this, "为了能领取到部分红包，步数将修改为99998");
                mun = 99998 + "";
            }
            final String trueNum = mun;
            closeKeyboard();
            //开始操作文件
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String data;
                    //获取源文件中的第一个随机数
                    String initString = FileUtils.getString(mFile);
                    String[] sourceStrArray = initString.split(",");
                    if (!TextUtils.isEmpty(sourceStrArray[0])) {
                        data = sourceStrArray[0] + "," + trueNum;
                    } else {
                        data = "339" + "," + trueNum;
                    }
                    Log.i("MainActivity", "data: " + data);

                    FileUtils.saveData(mFile, data);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.show(MainActivity.this, "修改成功！");
                            Intent intent = getPackageManager().getLaunchIntentForPackage(CHUNYU_PACKAGENAME);
                            //Intent intent = new Intent();
                            if (intent != null) {

                                intent.setClassName(CHUNYU_PACKAGENAME, CHUNYU_CLASS_NAME);
                                startActivity(intent);
                            } else {

                                ToastUtils.show(MainActivity.this, "你还没有安装春雨计步器,无法启动应用！！！");
                            }
                        }
                    });

                }
            }).start();
        } else {

            ToastUtils.show(MainActivity.this, "内存卡不存在或没有读写权限");
        }

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

    /*
    * 关闭键盘
    * */
    private void closeKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /*
    * 清除过期的文件
    * */
    public void btClear(View view) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            final File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ".Pedometer");
            mActivityManager.killBackgroundProcesses(CHUNYU_PACKAGENAME);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileUtils.deleteDirWihtFile(file);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.show(MainActivity.this, "春雨计步器历史刷步文件已经被清除了！！！");
                        }
                    });
                }
            }).start();
        }
    }

    private void checkUpdata() {
        String getUrl = "http://ouif47kta.bkt.clouddn.com/info/info";
        final PackageManager packageManager = getPackageManager();
        GetAsynHttpUtil.newGetAsynHttpUtil().get(getUrl, new GetHttpListener() {
            @Override
            public void onError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //ToastUtils.show(MainActivity.this,"获取更新信息失败！！！");
                        Log.i("MainActivity", "获取更新信息失败!!!");
                    }
                });
            }

            @Override
            public void onSuccess(String json) {
                Log.i("MainActivity", "json: " + json);
                Gson gson = new Gson();
                final UpdateInfo updateInfo = gson.fromJson(json, UpdateInfo.class);
                try {
                    PackageInfo info = packageManager.getPackageInfo(getPackageName(), 0);
                    String versionCode = String.valueOf(info.versionCode);
                    String versionName = info.versionName;
                    if (!TextUtils.equals(updateInfo.getVersionCode(), versionCode) || !TextUtils.equals(updateInfo.getVersionName(), versionName)) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //是否更新提示
                                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
                                dialog.setTitle("版本更新");
                                dialog.setMessage(updateInfo.getInstruction());
                                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ToastUtils.show(MainActivity.this, "为了能更好的使用本软件请及时升级为最新版本！！！");
                                    }
                                });
                                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //确定更新，显示更新进度
                                        mProgressDialog = new ProgressDialog(MainActivity.this);
                                        mProgressDialog.setTitle("更新说明");
                                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                        mProgressDialog.setMessage(updateInfo.getInstruction());
                                        mProgressDialog.setIndeterminate(false);
                                        mProgressDialog.setCancelable(false);
                                        mProgressDialog.show();
                                        String url = updateInfo.getUrl();
                                        DownloadUtil.get().download(url, "Assistant", new DownloadUtil.OnDownloadListener() {
                                            @Override
                                            public void onDownloadSuccess(final File file) {
                                                //下载完成进行安装
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mProgressDialog.dismiss();
                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        intent.setDataAndType(Uri.fromFile(file),
                                                                "application/vnd.android.package-archive");
                                                        MainActivity.this.startActivity(intent);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onDownloading(final int progress) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mProgressDialog.setProgress(progress);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onDownloadFailed() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mProgressDialog.dismiss();
                                                        ToastUtils.show(MainActivity.this, "下载失败");
                                                    }
                                                });

                                            }
                                        });
                                    }
                                });
                                dialog.show();
                            }
                        });

                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
