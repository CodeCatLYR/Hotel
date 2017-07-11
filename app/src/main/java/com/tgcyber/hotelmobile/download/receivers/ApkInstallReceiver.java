package com.tgcyber.hotelmobile.download.receivers;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile.download.FileDownloadManager;
import com.tgcyber.hotelmobile.download.SpUtils;


public class ApkInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogCat.i("DownloadManager", "onReceive"+intent.getAction());
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long downloadApkId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            long id = SpUtils.getInstance(context).getLong("download"+downloadApkId, -1L);
            LogCat.i("DownloadManager", "ACTION_DOWNLOAD_COMPLETE:"+downloadApkId+" saveId="+id);
            if (downloadApkId == id) {
                installApk(context, downloadApkId);
            }
        }
    }

    private static void installApk(Context context, long downloadApkId) {

        Intent install = new Intent(Intent.ACTION_VIEW);
        DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadFileUri = dManager.getUriForDownloadedFile(downloadApkId);
        if (downloadFileUri != null) {
            LogCat.i("DownloadManager", downloadFileUri.toString());
            install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(install);
        } else {
            FileDownloadManager.getInstance(context).getDm().remove(downloadApkId);
            LogCat.i("DownloadManager", "下载失败");
        }
    }
}
