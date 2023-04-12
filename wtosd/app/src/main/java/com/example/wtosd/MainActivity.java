package com.example.wtosd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class MainActivity extends Activity {

    private AssetManager assetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkWriteAndReadPermission();
        File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(fileDir, "first.mp3");
        String outfile = String.valueOf(file);
        String infile = "file:///android_asset/first.mp3";
        try {
             assetManager = AssetManager.class.newInstance();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        copyAsset(assetManager,"first.mp3",outfile);
        Uri uri = Uri.parse("file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
        sendBroadcast(intent);

    }
    private boolean copyAsset(AssetManager assetManager, String name, String toPath) {
        InputStream inputStream = null;
        try {
            inputStream = getResources().getAssets().open(name);
            OutputStream out = null;
            try {
                new File(toPath).createNewFile();
                out = new FileOutputStream(toPath);
                copyFile(inputStream, out);
                inputStream.close();
                inputStream = null;
                out.flush();
                out.close();
                out = null;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

        private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1){
            out.write(buffer,0,read);
            Log.d("cjy", "copyFile: ");
        }

    }


    private void checkWriteAndReadPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R ||
        Environment.isExternalStorageManager()) {
            Toast.makeText(this, "已获得所有权限", Toast.LENGTH_SHORT).show();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage("本程序需要你同意访问的所有文件权限")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                            startActivity(intent);
                        }
                    });
            builder.show();
            }
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permission : grantResults) {
            if (permission == PackageManager.PERMISSION_DENIED) {
                Log.d("cjy" , "onRequestPermissionsResult: " + permission);
                Toast.makeText(this, "申请权限失败", Toast.LENGTH_LONG).show();
                              break;
            }
        }
    }

}