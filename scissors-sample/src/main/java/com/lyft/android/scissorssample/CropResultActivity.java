package com.lyft.android.scissorssample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.FragmentActivity;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import dev.chrisbanes.insetter.Insetter;

public class CropResultActivity extends FragmentActivity {

  private static final String EXTRA_FILE_PATH = "EXTRA_FILE_PATH";

  ImageView resultView;

  static void startUsing(File croppedPath, Activity activity) {
    Intent intent = new Intent(activity, CropResultActivity.class);
    intent.putExtra(EXTRA_FILE_PATH, croppedPath.getPath());
    activity.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_crop_result);
    resultView = findViewById(R.id.result_image);

    WindowInsetsControllerCompat wc = ViewCompat.getWindowInsetsController(resultView);
    wc.hide(WindowInsetsCompat.Type.navigationBars());
    wc.hide(WindowInsetsCompat.Type.statusBars());
    wc.hide(WindowInsetsCompat.Type.captionBar());

    Insetter.builder()
      .padding(WindowInsetsCompat.Type.navigationBars())
      .padding(WindowInsetsCompat.Type.statusBars())
      .padding(WindowInsetsCompat.Type.captionBar())
      .applyToView(resultView);

    String filePath = getIntent().getStringExtra(EXTRA_FILE_PATH);
    File imageFile = new File(filePath);

    Picasso.with(this)
      .load(imageFile)
      .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
      .into(resultView);

    // Or Glide
    //Glide.with(this)
    //        .load(imageFile)
    //        .diskCacheStrategy(DiskCacheStrategy.NONE)
    //        .skipMemoryCache(true)
    //        .into(resultView);

    // Or Android-Universal-Image-Loader
    //DisplayImageOptions options = new DisplayImageOptions.Builder()
    //        .cacheInMemory(false)
    //        .cacheOnDisk(false)
    //        .build();
    //ImageLoader.getInstance().displayImage("file://" + filePath, resultView, options);
  }
}
