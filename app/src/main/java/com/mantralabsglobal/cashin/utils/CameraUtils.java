package com.mantralabsglobal.cashin.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

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

                int width = src.getWidth();
                int height = src.getHeight();
                // create output bitmap
                Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
                // color information
                int A, R, G, B;
                int pixel;

                // scan through all pixels
                for (int x = 0; x < width; ++x) {
                    for (int y = 0; y < height; ++y) {
                        // get pixel color
                        pixel = src.getPixel(x, y);
                        A = Color.alpha(pixel);
                        R = Color.red(pixel);
                        G = Color.green(pixel);
                        B = Color.blue(pixel);
                        int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);

                        // use 128 as threshold, above -> white, below -> black
                        if (gray > 110)
                            gray = 255;
                        else
                            gray = 0;
                        // set new pixel color to output bitmap
                        bmOut.setPixel(x, y, Color.argb(A, gray, gray, gray));
                    }
                }
                listener.onComplete(bmOut);
            }
        };

        new Thread(runnable).start();
    }

    public interface Listener {
        public void onComplete(Bitmap bmp);
    }
}
