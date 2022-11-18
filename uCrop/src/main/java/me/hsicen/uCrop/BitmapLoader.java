package me.hsicen.uCrop;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Load extension delegates actual Bitmap loading to a BitmapLoader allowing it to use different implementations.
 *
 * @see PicassoBitmapLoader
 * @see GlideBitmapLoader
 */
public interface BitmapLoader {

  void load(@Nullable Object model, @NonNull ImageView view);
}
