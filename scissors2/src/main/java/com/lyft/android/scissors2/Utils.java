package com.lyft.android.scissors2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class Utils {

  private final static ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
  private static final String TAG = "scissors.Utils";

  public static void checkArg(boolean expression, String msg) {
    if (!expression) {
      throw new IllegalArgumentException(msg);
    }
  }

  public static void checkNotNull(Object object, String msg) {
    if (object == null) {
      throw new NullPointerException(msg);
    }
  }

  public static Bitmap asBitmap(Drawable drawable, int minWidth, int minHeight) {
    final Rect tmpRect = new Rect();
    drawable.copyBounds(tmpRect);
    if (tmpRect.isEmpty()) {
      tmpRect.set(0, 0, Math.max(minWidth, drawable.getIntrinsicWidth()), Math.max(minHeight, drawable.getIntrinsicHeight()));
      drawable.setBounds(tmpRect);
    }
    Bitmap bitmap = Bitmap.createBitmap(tmpRect.width(), tmpRect.height(), Bitmap.Config.ARGB_8888);
    drawable.draw(new Canvas(bitmap));
    return bitmap;
  }

  public static Future<Void> flushToFile(final Bitmap bitmap, final Bitmap.CompressFormat format, final int quality, final File file) {

    return EXECUTOR_SERVICE.submit(() -> {
      OutputStream outputStream = null;

      try {
        file.getParentFile().mkdirs();
        outputStream = new FileOutputStream(file);
        bitmap.compress(format, quality, outputStream);
        outputStream.flush();
      } catch (final Throwable throwable) {
        if (BuildConfig.DEBUG) {
          Log.e(TAG, "Error attempting to save bitmap.", throwable);
        }
      } finally {
        closeQuietly(outputStream);
      }
    }, null);
  }

  public static Future<Void> flushToStream(final Bitmap bitmap, final Bitmap.CompressFormat format, final int quality, final OutputStream outputStream, final boolean closeWhenDone) {

    return EXECUTOR_SERVICE.submit(() -> {
      try {
        bitmap.compress(format, quality, outputStream);
        outputStream.flush();
      } catch (final Throwable throwable) {
        if (BuildConfig.DEBUG) {
          Log.e(TAG, "Error attempting to save bitmap.", throwable);
        }
      } finally {
        if (closeWhenDone) {
          closeQuietly(outputStream);
        }
      }
    }, null);
  }

  private static void closeQuietly(@Nullable OutputStream outputStream) {
    try {
      if (outputStream != null) {
        outputStream.close();
      }
    } catch (Exception e) {
      Log.e(TAG, "Error attempting to close stream.", e);
    }
  }
}
