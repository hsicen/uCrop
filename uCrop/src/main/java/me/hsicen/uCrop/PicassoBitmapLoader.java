package me.hsicen.uCrop;

import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import java.io.File;

/**
 * A {@link BitmapLoader} with transformation for {@link Picasso} image library.
 *
 * @see PicassoBitmapLoader#createUsing(CropView)
 * @see PicassoBitmapLoader#createUsing(CropView, Picasso)
 */
public class PicassoBitmapLoader implements BitmapLoader {

  private final Picasso picasso;
  private final Transformation transformation;

  public PicassoBitmapLoader(Picasso picasso, Transformation transformation) {
    this.picasso = picasso;
    this.transformation = transformation;
  }

  public static BitmapLoader createUsing(CropView cropView) {
    return createUsing(cropView, Picasso.with(cropView.getContext()));
  }

  public static BitmapLoader createUsing(CropView cropView, Picasso picasso) {
    return new PicassoBitmapLoader(picasso, PicassoFillViewportTransformation.createUsing(cropView.getViewportWidth(), cropView.getViewportHeight()));
  }

  @Override
  public void load(@Nullable Object model, @NonNull ImageView imageView) {
    final RequestCreator requestCreator;

    if (model instanceof Uri || model == null) {
      requestCreator = picasso.load((Uri) model);
    } else if (model instanceof String) {
      requestCreator = picasso.load((String) model);
    } else if (model instanceof File) {
      requestCreator = picasso.load((File) model);
    } else if (model instanceof Integer) {
      requestCreator = picasso.load((Integer) model);
    } else {
      throw new IllegalArgumentException("Unsupported model " + model);
    }

    requestCreator.skipMemoryCache().transform(transformation).into(imageView);
  }
}
