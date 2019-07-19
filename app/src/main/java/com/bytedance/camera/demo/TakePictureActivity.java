package com.bytedance.camera.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bytedance.camera.demo.utils.Utils;

import java.io.File;

import static com.bytedance.camera.demo.utils.Utils.rotateImage;

public class TakePictureActivity extends AppCompatActivity {

    private ImageView imageView;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 101;
    private File imageFile;
    String[] permissions = new String[] {

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        imageView = findViewById(R.id.img);
        findViewById(R.id.btn_picture).setOnClickListener(v -> {
            if (Utils.isPermissionsReady(TakePictureActivity.this,permissions)) {
                takePicture();
            } else {
                //todo 在这里申请相机、存储的权限
                Utils.reuqestPermissions(TakePictureActivity.this,permissions,REQUEST_EXTERNAL_STORAGE);
                //Utils.reuqestPermissions(TakePictureActivity.this,permissions,REQUEST_IMAGE_CAPTURE);

                Log.d("TakePictureActivity","d");
            }
        });

    }

    private void takePicture() {
        //todo 打开相机
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFile  = Utils.getOutputMediaFile(Utils.MEDIA_TYPE_IMAGE);
        if (imageFile != null){
            Uri fileUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ) {
                fileUri = FileProvider.getUriForFile(this,"com.bytedance.camera.demo", imageFile);
            }
            else {
                fileUri = Uri.fromFile(imageFile);
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
        //todo 处理返回数据
           setPic();
        }
    }

    private void setPic() {

        //todo 根据缩放比例读取文件，生成Bitmap
        int width = imageView.getWidth();
        int height = imageView.getHeight();
        BitmapFactory.Options bOptions = new BitmapFactory.Options();
        bOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bOptions);
        //todo 根据imageView裁剪
        int photoW = bOptions.outWidth;
        int photoH = bOptions.outHeight;
        int scaleFactor = Math.min(photoH / height, photoW / width);

        bOptions.inJustDecodeBounds = false;
        bOptions.inSampleSize = scaleFactor;
        bOptions.inPurgeable = true;

        //todo 根据缩放比例读取文件，生成Bitmap

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bOptions);

        //todo 如果存在预览方向改变，进行图片旋转
        bitmap  = rotateImage(bitmap, imageFile.getAbsolutePath());

        //todo 显示图片
        imageView.setImageBitmap(bitmap);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                //todo 判断权限是否已经授予
                if (Utils.isPermissionsReady(TakePictureActivity.this,permissions)){
                    takePicture();
                }
                break;
            }
        }
    }
}
