package com.ndk.so.generator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

  public static final String TAG = "chenby";

  private static int REQ_PERMISSION_CODE = 1001;

  private static final String[] PERMISSIONS = { Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE};

  // Used to load the 'native-lib' library on application startup.
  static {
    System.loadLibrary("my-test");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Example of a call to a native method
    checkAndRequestPermissions();
  }

  private void init() {
    TextView tv = findViewById(R.id.sample_text);
    if (ApkUtils.getVersionCode(this, getPackageName()) < 2.0) {
      tv.setText("不是最新的版本号 开始更新 ");
      new ApkUpdateTask().execute();
    } else {
      tv.setText(" 最新版本号 无需更新");
    }
  }

  /**
   * 权限检测以及申请
   */
  private void checkAndRequestPermissions() {
    // Manifest.permission.WRITE_EXTERNAL_STORAGE 和  Manifest.permission.READ_PHONE_STATE是必须权限，允许这两个权限才会显示广告。

    if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        && hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      init();
    } else {
      ActivityCompat.requestPermissions(this, PERMISSIONS, REQ_PERMISSION_CODE);
    }

  }


  /**
   * 权限判断
   * @param permissionName
   * @return
   */
  private boolean hasPermission(String permissionName) {
    return ActivityCompat.checkSelfPermission(this, permissionName)
        == PackageManager.PERMISSION_GRANTED;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    if (requestCode == REQ_PERMISSION_CODE) {
      checkAndRequestPermissions();
    }

    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  public void onOpen(View view) {
    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
    startActivity(intent);
  }

  class ApkUpdateTask extends AsyncTask<Void, Void, Boolean> {


    @Override
    protected Boolean doInBackground(Void... params) {

      Log.d(TAG,"开始下载 。。。");

      //File patchFile = DownLoadUtils.download(Contants.URL_PATCH_DOWNLOAD) ;
      Log.d(TAG,"下载完成 。。。");

      String oldfile = ApkUtils.getSourceApkPath(MainActivity.this, getPackageName());

      String newFile = Contants.NEW_APK_PATH;


      String patchFileString = Contants.PATCH_FILE_PATH;

      File patchFile = new File(patchFileString);
      if(!patchFile.exists()) {
        return false;
      }

      Log.d(TAG,"开始合并");
      int ret = BsPatch.patch(oldfile, newFile,patchFileString);
      Log.d(TAG,"开始完成");

      if (ret == 0) {
        return true;
      } else {
        return false;
      }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
      if (aBoolean) {
        Log.d(TAG,"合并成功 开始安装新apk");
        ApkUtils.installApk(MainActivity.this, Contants.NEW_APK_PATH);
      }
    }
  }

}
