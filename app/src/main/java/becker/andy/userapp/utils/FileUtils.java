package becker.andy.userapp.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    private static final String TAG = "FileUtils";
    private static final String TEMP_DIR_PATH = Environment.getExternalStorageDirectory().getPath();
    private static final String TEMP_DIR_PATH_LOCAL = Environment.getDataDirectory().getPath();


    public static String getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            if(isExternalStorageDocument(contentUri)){
                Log.d(TAG, "getFilePathFromURI: storage");
                File copyFile = new File(TEMP_DIR_PATH + File.separator + fileName);
                copy(context, contentUri, copyFile);
                return copyFile.getAbsolutePath();
            }else {
                Log.d(TAG, "getFilePathFromURI: local");
                File copyFile = new File(TEMP_DIR_PATH_LOCAL+ File.separator + fileName);
                copy(context, contentUri, copyFile);
                return copyFile.getAbsolutePath();
            }

        }
        return null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copyStream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean isExternalStorageDocument(Uri uri) {
        Log.d(TAG, "isExternalStorageDocument: "+uri.getAuthority());
        Log.d(TAG, "isExternalStorageDocument: "+TEMP_DIR_PATH_LOCAL);
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
}
