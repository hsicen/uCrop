package me.hsicen.uCropSample;

import static android.graphics.Bitmap.CompressFormat.JPEG;
import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.FragmentActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import dev.chrisbanes.insetter.Insetter;
import me.hsicen.uCrop.CropView;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends FragmentActivity {

  static final ButterKnife.Setter<View, Integer> VISIBILITY = (view, visibility, index) -> view.setVisibility(visibility);
  CropView cropView;
  List<View> buttons;
  private final AnimatorListener animatorListener = new AnimatorListener() {
    @Override
    public void onAnimationStart(Animator animation) {
      ButterKnife.apply(buttons, VISIBILITY, View.INVISIBLE);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
      ButterKnife.apply(buttons, VISIBILITY, View.VISIBLE);
    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }
  };
  View pickButton;
  CompositeSubscription subscriptions = new CompositeSubscription();
  private float[] ASPECT_RATIOS;
  private String[] ASPECT_LABELS;
  private int selectedRatio = 0;

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
  static void autoCancel(ObjectAnimator objectAnimator) {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
      objectAnimator.setAutoCancel(true);
    }
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // 导航栏处理
    setContentView(R.layout.activity_main);
    pickButton = findViewById(R.id.pick_fab);
    cropView = findViewById(R.id.crop_view);

    buttons = new ArrayList<>();
    buttons.add(findViewById(R.id.crop_fab));
    buttons.add(findViewById(R.id.ratio_fab));
    buttons.add(findViewById(R.id.pick_mini_fab));

    findViewById(R.id.crop_fab).setOnClickListener(v -> onCropClicked());
    findViewById(R.id.pick_fab).setOnClickListener(v -> onPickClicked());
    findViewById(R.id.pick_mini_fab).setOnClickListener(v -> onPickClicked());
    findViewById(R.id.ratio_fab).setOnClickListener(v -> onRatioClicked());
    findViewById(R.id.crop_view).setOnTouchListener((v, event) -> onTouchCropView(event));

    WindowInsetsControllerCompat wc = ViewCompat.getWindowInsetsController(findViewById(R.id.rootView));
    wc.hide(WindowInsetsCompat.Type.navigationBars());
    wc.hide(WindowInsetsCompat.Type.statusBars());
    wc.hide(WindowInsetsCompat.Type.captionBar());

    Insetter.builder()
      .padding(WindowInsetsCompat.Type.navigationBars())
      .padding(WindowInsetsCompat.Type.statusBars())
      .padding(WindowInsetsCompat.Type.captionBar())
      .applyToView(findViewById(R.id.rootView));

    // 设置显示比例
    cropView.post(() -> {
      float ration = (cropView.getWidth() * 1f) / cropView.getHeight();
      ASPECT_RATIOS = new float[]{0f, 1f, 6f / 4f, 16f / 9f, ration};
      ASPECT_LABELS = new String[]{"原始", "1:1", "6:4", "16:9", "全屏"};
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == RequestCodes.PICK_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
      Uri galleryPictureUri = data.getData();
      cropView.extensions().load(galleryPictureUri);
      updateButtons();
    }
  }

  public void onCropClicked() {
    final File croppedFile = new File(getCacheDir(), "cropped.jpg");

    Observable<Void> onSave = Observable.from(cropView.extensions().crop().quality(100).format(JPEG).into(croppedFile)).subscribeOn(io()).observeOn(mainThread());

    subscriptions.add(onSave.subscribe(nothing -> CropResultActivity.startUsing(croppedFile, MainActivity.this)));
  }

  public void onPickClicked() {
    cropView.extensions().pickUsing(this, RequestCodes.PICK_IMAGE_FROM_GALLERY);
  }

  public void onRatioClicked() {
    final float oldRatio = cropView.getImageRatio();
    selectedRatio = (selectedRatio + 1) % ASPECT_RATIOS.length;

    // Since the animation needs to interpolate to the native
    // ratio, we need to get that instead of using 0
    float newRatio = ASPECT_RATIOS[selectedRatio];
    if (Float.compare(0, newRatio) == 0) {
      newRatio = cropView.getImageRatio();
    }

    ObjectAnimator viewportRatioAnimator = ObjectAnimator.ofFloat(cropView, "viewportRatio", oldRatio, newRatio).setDuration(420);
    autoCancel(viewportRatioAnimator);
    viewportRatioAnimator.addListener(animatorListener);
    viewportRatioAnimator.start();

    Toast.makeText(this, ASPECT_LABELS[selectedRatio], Toast.LENGTH_SHORT).show();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    subscriptions.unsubscribe();
  }

  public boolean onTouchCropView(MotionEvent event) { // GitHub issue #4
    if (event.getPointerCount() > 1 || cropView.getImageBitmap() == null) {
      return true;
    }

    switch (event.getActionMasked()) {
      case MotionEvent.ACTION_DOWN:
      case MotionEvent.ACTION_MOVE:
        ButterKnife.apply(buttons, VISIBILITY, View.INVISIBLE);
        break;
      default:
        ButterKnife.apply(buttons, VISIBILITY, View.VISIBLE);
        break;
    }
    return true;
  }

  private void updateButtons() {
    ButterKnife.apply(buttons, VISIBILITY, View.VISIBLE);
    pickButton.setVisibility(View.GONE);
  }
}
