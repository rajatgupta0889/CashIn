package com.mantralabsglobal.cashin.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.mantralabsglobal.cashin.businessobjects.AndroidImage;

/**
 * Created by pk on 6/28/2015.
 */
public class CameraUtils {

    public static void createBlackAndWhite(final String filePath, final CameraUtils.Listener listener) {

        Runnable runnable = new Runnable(){

            @Override
            public void run() {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ALPHA_8;
                Bitmap src = BitmapFactory.decodeFile(filePath, options);

                src = toGrayscale(src);

                AndroidImage androidImage = new AndroidImage(src);
                androidImage = applyThreshold(androidImage,120);
                listener.onComplete(androidImage.getImage());
            }
        };

        new Thread(runnable).start();
    }

    public static AndroidImage applyThreshold(AndroidImage imageIn, int threshold) {

        // The Resulting image
        AndroidImage imageOut;

        // Initiate the Output image
        imageOut = new AndroidImage(imageIn.getImage());

        // Do Threshold process
        for(int y=0; y<imageIn.getHeight(); y++){
            for(int x=0; x<imageIn.getWidth(); x++){

                if(imageOut.getRComponent(x,y) < threshold){
                    imageOut.setPixelColor(x, y, 0,0,0);
                }
                else{
                    imageOut.setPixelColor(x, y, 255,255,255);
                }
            }
        }

        // Return final image
        return imageOut;
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public interface Listener {
        public void onComplete(Bitmap bmp);
    }
}
