package com.zhenghui.zhqb.gift.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.Toast;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import id.zelory.compressor.Compressor;

import static com.zhenghui.zhqb.gift.util.Constant.CODE_807900;

public class QiNiuUtil {

    private static String ANDROID = "ANDROID";
    private static String IOS = "IOS";
    private String token = "";

    private Context context;

    private String data;
    private List<String> list;

    private SharedPreferences userInfoSp;
    private SharedPreferences appConfigSp;

    public QiNiuUtil(Context context, String data, List<String> list){
        this.data = data;
        this.list = list;
        this.context = context;

        init();
    }

    private void uploadSingle(final QiNiuCallBack callBack , String url){

        if(url.indexOf(ANDROID) == -1 || url.indexOf(IOS) == -1){

            Configuration config = new Configuration.Builder().build();
            UploadManager uploadManager = new UploadManager(config);
            String key = ANDROID + timestamp() + getImageWidthHeight(url) + ".jpg";
            uploadManager.put(url, key, token,
                    new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject res) {
                            //res包含hash、key等信息，具体字段取决于上传策略的设置
                            if(info.isOK())
                            {
                                callBack.onSuccess(key, info, res);

                            } else{
                                Log.i("QiNiu", "Upload Fail");
                                Log.i("QiNiu", "key="+key);
                                Log.i("QiNiu", "res="+res);
                                Log.i("QiNiu", "info="+info);
                            }
                        }
                    }, null);
        }



    }

//    private void uploadList(final QiNiuCallBack callBack){
//        Configuration config = new Configuration.Builder().zone(Zone.httpAutoZone).build();
//        UploadManager uploadManager = new UploadManager(config);
//
//        for(int i= 0;i<list.size();i++){
//            String key = ANDROID + timestamp() +  i + getPicSize(list.get(i)) + ".jpg";
//            uploadManager.put(list.get(i), key, token,
//                    new UpCompletionHandler() {
//                        @Override
//                        public void complete(String key, ResponseInfo info, JSONObject res) {
//                            //res包含hash、key等信息，具体字段取决于上传策略的设置
//                            if(info.isOK())
//                            {
//                                callBack.onSuccess(key, info, res);
//
//                            } else{
//                                Log.i("QiNiu", "Upload Fail");
//                                Log.i("QiNiu", "key="+key);
//                                Log.i("QiNiu", "res="+res);
//                                Log.i("QiNiu", "info="+info);
//                            }
//                        }
//                    }, null);
//
//        }
//
//    }

    private void init(){
        userInfoSp = context.getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        appConfigSp = context.getSharedPreferences("appConfig",Context.MODE_PRIVATE);
    }

    public void qiNiu(final QiNiuCallBack callBack, final boolean isSingle){

        JSONObject object = new JSONObject();
        try {
            object.put("token",userInfoSp.getString("token", null));
            object.put("systemCode",appConfigSp.getString("systemCode", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        new Xutil().post(CODE_807900, object.toString(), new Xutil.XUtils3CallBackPost() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject != null){
                        token = jsonObject.getString("uploadToken");
                    }

                   if (isSingle){
                       Compressor(callBack);
                   }else{
//                        System.out.println("luploadList(callBack);");
//                        uploadList(callBack);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTip(String tip) {
                Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error, boolean isOnCallback) {
                Toast.makeText(context, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    static String size = "";
    static String imageWidth = "";
    static String imageHeight = "";

    private static String getPicSize(String data){
        try {
            ExifInterface exifInterface = new ExifInterface(data);

            //拍摄日期
            String FDateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
            // 设备品牌
            String deviceName = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
            // 设备型号
            String deviceModel = exifInterface.getAttribute(ExifInterface.TAG_MODEL);

            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:

                case ExifInterface.ORIENTATION_ROTATE_270:
                    // 照片被旋转 宽高相反
                    imageWidth = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
                    imageHeight = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
                    break;

                default:
                    imageWidth = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
                    imageHeight = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);


                    break;

            }

            size = "_" + imageWidth + "_" + imageHeight;

            System.out.print(size = "_" + imageWidth + "_" + imageHeight);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return size;
    }

    public static String getImageWidthHeight(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        imageWidth = options.outWidth + "";
        imageHeight = options.outHeight + "";
        size = "_" + imageWidth + "_" + imageHeight;

        System.out.print("size = _" + imageWidth + "_" + imageHeight);
        return size;
    }

    private static String timestamp(){
        String time = System.currentTimeMillis()+"";

        return "_"+time;
    }

//    private void luBan(final QiNiuCallBack callBack){
//        File file = new File(data);
//
//        Luban.get(context)
//                .load(file) //传人要压缩的图片
//                .putGear(3)      //设定压缩档次，默认三挡
//                .setCompressListener(new OnCompressListener() { //设置回调
//
//                    @Override
//                    public void onStart() {
//                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
//                    }
//                    @Override
//                    public void onSuccess(File file) {
//                        // TODO 压缩成功后调用，返回压缩后的图片文件
////                        uploadSingle(callBack,data);
//                        uploadSingle(callBack,file.getAbsolutePath());
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        // TODO 当压缩过去出现问题时调用
//                        uploadSingle(callBack,data);
//                    }
//                }).launch();    //启动压缩
//    }

    private void Compressor(QiNiuCallBack callBack){
        File compressedImageFile = Compressor.getDefault(context).compressToFile(new File(data));
        uploadSingle(callBack,compressedImageFile.getAbsolutePath());

//        File compressedImage = new Compressor.Builder(context)
//                .setMaxWidth(Float.parseFloat(imageWidth))
//                .setMaxHeight(Float.parseFloat(imageHeight))
//                .setQuality(80)
//                .setCompressFormat(Bitmap.CompressFormat.JPEG)
//                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
//                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
//                .build()
//                .compressToFile(new File(data));
//
//        uploadSingle(callBack,compressedImage.getAbsolutePath());
    }

    public interface QiNiuCallBack {

        void onSuccess(String key, ResponseInfo info, JSONObject res);

    }

}
