# Assistant
春雨改步器 v.0.1

**核心：**

1. 修改位置为sd卡的/.Pedometer/.cypedometer/xxxx-xx-xx

2. 权限设置：  
android.permission.READ_EXTERNAL_STORAGE
    android.permission.WRITE_EXTERNAL_STORAGE
    android.permission.KILL_BACKGROUND_PROCESSES

3. Android 6.0及以后版本的动态权限设置

4. 修改步数之前，春雨计步器必须关闭（包括Activity和Service）,这里使用了killBackgroundProcesses（packagename）;
5.  修改之后，自动启动春雨计步器
getPackageManager().getLaunchIntentForPackage（packagename），注意判段是否为null;
