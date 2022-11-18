package me.hsicen.uCrop;

import android.annotation.SuppressLint;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;

/**
 * A {@link BitmapLoader} with transformation for {@link Glide} image library.
 *
 * @see GlideBitmapLoader#createUsing(CropView)
 * @see GlideBitmapLoader#createUsing(CropView, RequestManager)
 */
public class GlideBitmapLoader implements BitmapLoader {

  private final RequestManager requestManager;
  private final BitmapTransformation transformation;

  public GlideBitmapLoader(@NonNull RequestManager requestManager, @NonNull BitmapTransformation transformation) {
    this.requestManager = requestManager;
    this.transformation = transformation;
  }

  public static BitmapLoader createUsing(@NonNull CropView cropView) {
    return createUsing(cropView, Glide.with(cropView.getContext()));
  }

  public static BitmapLoader createUsing(@NonNull CropView cropView, @NonNull RequestManager requestManager) {
    return new GlideBitmapLoader(requestManager,
      GlideFillViewportTransformation.createUsing(cropView.getViewportWidth(), cropView.getViewportHeight()));
  }

  @SuppressLint("CheckResult")
  @Override
  public void load(@Nullable Object model, @NonNull ImageView imageView) {
    RequestOptions requestOptions = new RequestOptions();
    requestOptions.skipMemoryCache(true)
      .diskCacheStrategy(DiskCacheStrategy.DATA)
      .transform(transformation);

    requestManager.asBitmap()
      .load(model)
      .apply(requestOptions);
  }
}
