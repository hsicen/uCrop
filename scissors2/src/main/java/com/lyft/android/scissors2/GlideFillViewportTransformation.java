package com.lyft.android.scissors2;

import android.graphics.Bitmap;
import android.graphics.Rect;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.nio.charset.Charset;
import java.security.MessageDigest;

class GlideFillViewportTransformation extends BitmapTransformation {

  private static final String ID = "com.lyft.android.scissors.GlideFillViewportTransformation";
  private static final byte[] ID_BYTES = ID.getBytes(Charset.defaultCharset());

  private final int viewportWidth;
  private final int viewportHeight;

  public GlideFillViewportTransformation(int viewportWidth, int viewportHeight) {
    this.viewportWidth = viewportWidth;
    this.viewportHeight = viewportHeight;
  }

  public static BitmapTransformation createUsing(int viewportWidth, int viewportHeight) {
    return new GlideFillViewportTransformation(viewportWidth, viewportHeight);
  }

  @Override
  protected Bitmap transform(@NonNull BitmapPool bitmapPool, Bitmap source, int outWidth, int outHeight) {
    int sourceWidth = source.getWidth();
    int sourceHeight = source.getHeight();

    Rect target = CropViewExtensions.computeTargetSize(sourceWidth, sourceHeight, viewportWidth, viewportHeight);

    int targetWidth = target.width();
    int targetHeight = target.height();

    return Bitmap.createScaledBitmap(
      source,
      targetWidth,
      targetHeight,
      true);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof GlideFillViewportTransformation) {
      GlideFillViewportTransformation other = (GlideFillViewportTransformation) obj;
      return other.viewportWidth == viewportWidth && other.viewportHeight == viewportHeight;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = viewportWidth * 31 + viewportHeight;
    return hash * 17 + ID.hashCode();
  }

  @Override
  public void updateDiskCacheKey(MessageDigest messageDigest) {
    messageDigest.update(ID_BYTES);
  }
}
