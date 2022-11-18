package me.hsicen.uCrop;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.squareup.picasso.Transformation;

class PicassoFillViewportTransformation implements Transformation {

  private final int viewportWidth;
  private final int viewportHeight;

  public PicassoFillViewportTransformation(int viewportWidth, int viewportHeight) {
    this.viewportWidth = viewportWidth;
    this.viewportHeight = viewportHeight;
  }

  public static Transformation createUsing(int viewportWidth, int viewportHeight) {
    return new PicassoFillViewportTransformation(viewportWidth, viewportHeight);
  }

  @Override
  public Bitmap transform(Bitmap source) {
    int sourceWidth = source.getWidth();
    int sourceHeight = source.getHeight();

    Rect target = CropViewExtensions.computeTargetSize(sourceWidth, sourceHeight, viewportWidth, viewportHeight);
    final Bitmap result = Bitmap.createScaledBitmap(source, target.width(), target.height(), true);

    if (result != source) {
      source.recycle();
    }

    return result;
  }

  @Override
  public String key() {
    return viewportWidth + "x" + viewportHeight;
  }
}
