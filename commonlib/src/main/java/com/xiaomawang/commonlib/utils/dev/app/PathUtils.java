package com.xiaomawang.commonlib.utils.dev.app;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import com.xiaomawang.commonlib.utils.dev.DevUtils;


/**
 * detail: 路径相关工具类
 * Created by Blankj
 * Update to Ttt
 */
public final class PathUtils {

    private PathUtils() {
    }

    /**
     * 获取 Android 系统根目录 - path: /system
     * @return 系统根目录
     */
    public static String getRootPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    /**
     * 获取 data 目录 - path: /data
     * @return data 目录
     */
    public static String getDataPath() {
        return Environment.getDataDirectory().getAbsolutePath();
    }

    /**
     * 获取缓存目录 - path: data/cache
     * @return 缓存目录
     */
    public static String getIntDownloadCachePath() {
        return Environment.getDownloadCacheDirectory().getAbsolutePath();
    }

    /**
     * 获取此应用的缓存目录 - path: /data/data/package/cache
     * @return 此应用的缓存目录
     */
    public static String getAppIntCachePath() {
        return DevUtils.getContext().getCacheDir().getAbsolutePath();
    }

    /**
     * 获取此应用的文件目录 - path: /data/data/package/files
     * @return 此应用的文件目录
     */
    public static String getAppIntFilesPath() {
        return DevUtils.getContext().getFilesDir().getAbsolutePath();
    }

    /**
     * 获取此应用的数据库文件目录 - path: /data/data/package/databases/name
     * @param name 数据库文件名
     * @return 数据库文件目录
     */
    public static String getAppIntDbPath(String name) {
        return DevUtils.getContext().getDatabasePath(name).getAbsolutePath();
    }

    /**
     * 获取 Android 外置储存的根目录 - path: /storage/emulated/0
     * @return 外置储存根目录
     */
    public static String getExtStoragePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 获取闹钟铃声目录 - path: /storage/emulated/0/Alarms
     * @return 闹钟铃声目录
     */
    public static String getExtAlarmsPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS).getAbsolutePath();
    }

    /**
     * 获取相机拍摄的照片和视频的目录 - path: /storage/emulated/0/DCIM
     * @return 照片和视频目录
     */
    public static String getExtDcimPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    }

    /**
     * 获取文档目录 - path: /storage/emulated/0/Documents
     * @return 文档目录
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getExtDocumentsPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
    }

    /**
     * 获取下载目录 - path: /storage/emulated/0/Download
     * @return 下载目录
     */
    public static String getExtDownloadsPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    /**
     * 获取视频目录 - path: /storage/emulated/0/Movies
     * @return 视频目录
     */
    public static String getExtMoviesPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath();
    }

    /**
     * 获取音乐目录 - path: /storage/emulated/0/Music
     * @return 音乐目录
     */
    public static String getExtMusicPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
    }

    /**
     * 获取提示音目录 - path: /storage/emulated/0/Notifications
     * @return 提示音目录
     */
    public static String getExtNotificationsPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS).getAbsolutePath();
    }

    /**
     * 获取图片目录 - path: /storage/emulated/0/Pictures
     * @return 图片目录
     */
    public static String getExtPicturesPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    }

    /**
     * 获取 Podcasts 目录 - path: /storage/emulated/0/Podcasts
     * @return Podcasts 目录
     */
    public static String getExtPodcastsPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS).getAbsolutePath();
    }

    /**
     * 获取铃声目录 - path: /storage/emulated/0/Ringtones
     * @return 下载目录
     */
    public static String getExtRingtonesPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES).getAbsolutePath();
    }

    /**
     * 获取此应用在外置储存中的缓存目录 - path: /storage/emulated/0/Android/data/package/cache
     * @return 此应用在外置储存中的缓存目录
     */
    public static String getAppExtCachePath() {
        return DevUtils.getContext().getExternalCacheDir().getAbsolutePath();
    }

    /**
     * 获取此应用在外置储存中的文件目录 - path: /storage/emulated/0/Android/data/package/files
     * @return 此应用在外置储存中的文件目录
     */
    public static String getAppExtFilePath() {
        return DevUtils.getContext().getExternalFilesDir(null).getAbsolutePath();
    }

    /**
     * 获取此应用在外置储存中的闹钟铃声目录 - path: /storage/emulated/0/Android/data/package/files/Alarms
     * @return 此应用在外置储存中的闹钟铃声目录
     */
    public static String getAppExtAlarmsPath() {
        return DevUtils.getContext().getExternalFilesDir(Environment.DIRECTORY_ALARMS).getAbsolutePath();
    }

    /**
     * 获取此应用在外置储存中的相机目录 - path: /storage/emulated/0/Android/data/package/files/DCIM
     * @return 此应用在外置储存中的相机目录
     */
    public static String getAppExtDcimPath() {
        return DevUtils.getContext().getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath();
    }

    /**
     * 获取此应用在外置储存中的文档目录 - path: /storage/emulated/0/Android/data/package/files/Documents
     * @return 此应用在外置储存中的文档目录
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getAppExtDocumentsPath() {
        return DevUtils.getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
    }

    /**
     * 获取此应用在外置储存中的闹钟目录 - path: /storage/emulated/0/Android/data/package/files/Download
     * @return 此应用在外置储存中的闹钟目录
     */
    public static String getAppExtDownloadPath() {
        return DevUtils.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    /**
     * 获取此应用在外置储存中的视频目录 - path: /storage/emulated/0/Android/data/package/files/Movies
     * @return 此应用在外置储存中的视频目录
     */
    public static String getAppExtMoviesPath() {
        return DevUtils.getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath();
    }

    /**
     * 获取此应用在外置储存中的音乐目录 - path: /storage/emulated/0/Android/data/package/files/Music
     * @return 此应用在外置储存中的音乐目录
     */
    public static String getAppExtMusicPath() {
        return DevUtils.getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath();
    }

    /**
     * 获取此应用在外置储存中的提示音目录 - path: /storage/emulated/0/Android/data/package/files/Notifications
     * @return 此应用在外置储存中的提示音目录
     */
    public static String getAppExtNotificationsPath() {
        return DevUtils.getContext().getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS).getAbsolutePath();
    }

    /**
     * 获取此应用在外置储存中的图片目录 - path: /storage/emulated/0/Android/data/package/files/Pictures
     * @return 此应用在外置储存中的图片目录
     */
    public static String getAppExtPicturesPath() {
        return DevUtils.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    }

    /**
     * 获取此应用在外置储存中的 Podcasts 目录 - path: /storage/emulated/0/Android/data/package/files/Podcasts
     * @return 此应用在外置储存中的 Podcasts 目录
     */
    public static String getAppExtPodcastsPath() {
        return DevUtils.getContext().getExternalFilesDir(Environment.DIRECTORY_PODCASTS).getAbsolutePath();
    }

    /**
     * 获取此应用在外置储存中的铃声目录 - path: /storage/emulated/0/Android/data/package/files/Ringtones
     * @return 此应用在外置储存中的铃声目录
     */
    public static String getAppExtRingtonesPath() {
        return DevUtils.getContext().getExternalFilesDir(Environment.DIRECTORY_RINGTONES).getAbsolutePath();
    }

    /**
     * 获取此应用的 Obb 目录 - path: /storage/emulated/0/Android/obb/package
     * 一般用来存放游戏数据包
     * @return 此应用的 Obb 目录
     */
    public static String getObbPath() {
        return DevUtils.getContext().getObbDir().getAbsolutePath();
    }

    /**
     * 通过 Uri 获取 文件路径
     * @param context
     * @param uri
     * @return
     */
    public static String getFilePathByUri(Context context, Uri uri) {
        String path = null;
        // 以 file:// 开头的
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            path = uri.getPath();
            return path;
        }
        // 以 content:// 开头的，比如 content://media/extenral/images/media/17766
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
            return path;
        }
        // 4.4及之后的 是以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        path = Environment.getExternalStorageDirectory() + "/" + split[1];
                        return path;
                    }
                } else if (isDownloadsDocument(uri)) {
                    // DownloadsProvider
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                            Long.valueOf(id));
                    path = getDataColumn(context, contentUri, null, null);
                    return path;
                } else if (isMediaDocument(uri)) {
                    // MediaProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    path = getDataColumn(context, contentUri, selection, selectionArgs);
                    return path;
                }
            }
        }
        return null;
    }

    /**
     * 解决华为手机拿不到图片的问题
     * @param context
     * @return
     */
    public static String getHWImagePathFromURI(Context context, Uri uri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        String path = null;
        if (cursor != null) {
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = context.getContentResolver().query(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            if (cursor != null && cursor.getCount()>0) {
                cursor.moveToFirst();
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
            }
        }
        return path;
    }

    /**
     * 解决小米手机上获取图片路径为null的情况
     * @param intent
     * @return
     */
    public static String getXMImagePathFromURI(Context context, Intent intent) {
        Uri uri = geturi(context, intent);//解决方案
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);

        String path = null;
        if(cursor!=null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);// 图片在的路径
        }
        return path;
    }

    public static Uri geturi(Context context, Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");

                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[] { MediaStore.Images.ImageColumns._ID },
                        buff.toString(), null, null);

                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
