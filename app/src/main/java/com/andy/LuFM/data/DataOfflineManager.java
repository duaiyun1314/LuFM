package com.andy.LuFM.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import com.andy.LuFM.event.EventType;
import com.andy.LuFM.event.IEventHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * handle local data (eg. copy asset to external)
 */
public class DataOfflineManager {
    public static void loadOfflineData(Context context, IEventHandler handler) {
        String dbDir = "data/data/com.andy.LuFM/databases";
        File dbDirParent = new File(dbDir);
        if (!dbDirParent.exists()) {
            dbDirParent.mkdir();
        }
        try {
            AssetManager assetManager = context.getAssets();
            String[] filedirs = assetManager.list("offline");
            for (String fileName : filedirs) {
                InputStream inputStream = assetManager.open("offline/" + fileName);
                ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                while (true) {
                    ZipEntry entry = zipInputStream.getNextEntry();
                    if (entry == null) {
                        break;
                    }
                    moveDataToDB(zipInputStream, entry.getName(), dbDir);
                }
            }
            handler.OnEvent(null, EventType.LOAD_OFFLINE_DATA_SUCCEED, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void moveDataToDB(ZipInputStream zipInputStream, String name, String dbDir) {
        if (zipInputStream == null || TextUtils.isEmpty(name) || TextUtils.isEmpty(dbDir)) {
            return;
        }
        Log.i("Sync", "name:" + name);
        String fileName = dbDir + "/" + name;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                return;
            }
            byte[] buffer = new byte[1024];
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            int len;
            while ((len = zipInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, len);

            }
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
