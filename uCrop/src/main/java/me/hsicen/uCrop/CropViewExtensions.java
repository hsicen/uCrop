package me.hsicen.uCrop;

import static me.hsicen.uCrop.CropView.Extensions.LoaderType;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Rect;

class CropViewExtensions {

  final static boolean HAS_PICASSO = canHasClass("com.squareup.picasso.Picasso");
  final static boolean HAS_GLIDE = canHasClass("com.bumptech.glide.Glide");
  final static boolean HAS_UIL = canHasClass("com.nostra13.universalimageloader.core.ImageLoader");

  static void pickUsing(Activity activity, int requestCode) {
    activity.startActivityForResult(
      createChooserIntent(),
      requestCode);
  }

  static void pickUsing(Fragment fragment, int requestCode) {
    fragment.startActivityForResult(
      createChooserIntent(),
      requestCode);
  }

  private static Intent createChooserIntent() {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    intent.addCategory(Intent.CATEGORY_OPENABLE);

    return Intent.createChooser(intent, null);
  }

  static BitmapLoader resolveBitmapLoader(CropView cropView, LoaderType loaderType) {
    switch (loaderType) {
      case PICASSO:
        return PicassoBitmapLoader.createUsing(cropView);
      case GLIDE:
        return GlideBitmapLoader.createUsing(cropView);
      case UIL:
        return UILBitmapLoader.createUsing(cropView);
      case CLASS_LOOKUP:
        break;
      default:
        throw new IllegalStateException("Unsupported type of loader = " + loaderType);
    }

    if (HAS_PICASSO) {
      return PicassoBitmapLoader.createUsing(cropView);
    }
    if (HAS_GLIDE) {
      return GlideBitmapLoader.createUsing(cropView);
    }
    if (HAS_UIL) {
      return UILBitmapLoader.createUsing(cropView);
    }
    throw new IllegalStateException("You must provide a BitmapLoader.");
  }

  static boolean canHasClass(String className) {
    try {
      Class.forName(className);
      return true;
    } catch (ClassNotFoundException e) {
    }
    return false;
  }

  static Rect computeTargetSize(int sourceWidth, int sourceHeight, int viewportWidth, int viewportHeight) {

    if (sourceWidth == viewportWidth && sourceHeight == viewportHeight) {
      return new Rect(0, 0, viewportWidth, viewportHeight); // Fail fast for when source matches exactly on viewport
    }

    float scale;
    if (sourceWidth * viewportHeight > viewportWidth * sourceHeight) {
      scale = (float) viewportHeight / (float) sourceHeight;
    } else {
      scale = (float) viewportWidth / (float) sourceWidth;
    }
    final int recommendedWidth = (int) ((sourceWidth * scale) + 0.5f);
    final int recommendedHeight = (int) ((sourceHeight * scale) + 0.5f);
    return new Rect(0, 0, recommendedWidth, recommendedHeight);
  }
}
