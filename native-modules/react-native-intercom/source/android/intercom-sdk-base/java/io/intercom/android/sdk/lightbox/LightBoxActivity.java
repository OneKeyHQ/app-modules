package io.intercom.android.sdk.lightbox;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.view.WindowInsetsCompat.Type;
import coil.request.ImageRequest;
import coil.request.ImageRequest.Builder;
import io.intercom.android.sdk.R.color;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.activities.IntercomActivity;
import io.intercom.android.sdk.utilities.IntercomCoilKt;

public class LightBoxActivity extends AppCompatActivity implements LightBoxListener, IntercomActivity {
   private static final String EXTRA_IMAGE_URL = "extra_image_url";
   private static final String EXTRA_ACTIVITY_FULLSCREEN = "extra_activity_fullscreen";
   private static final int ANIMATION_TIME_MS = 300;
   public static final String TRANSITION_KEY = "lightbox_image";
   private String imageUrl = "";
   ViewGroup rootView;

   @Override
   protected void onCreate(@Nullable Bundle var1) {
      Bundle var2 = this.getIntent().getExtras();
      boolean var3 = false;
      if (var2 != null) {
         this.imageUrl = var2.getString("extra_image_url", "");
         var3 = var2.getBoolean("extra_activity_fullscreen", false);
      }

      EdgeToEdge.enable(this);
      super.onCreate(var1);
      this.getOnBackPressedDispatcher().addCallback(new LightBoxActivity$1(this, true));
      this.setContentView(layout.intercom_activity_lightbox);
      if (var3) {
         new WindowInsetsControllerCompat(this.getWindow(), this.getWindow().getDecorView()).hide(Type.systemBars());
      }

      this.rootView = (ViewGroup)this.findViewById(id.root_view);
      LightBoxImageView var4 = (LightBoxImageView)this.findViewById(id.full_image);
      var4.setTransitionName("lightbox_image");
      this.loadImage(var4);
      this.reloadAfterTransition(var4);
      var4.setLightBoxListener(this);
      this.fadeIn();
   }

   private void reloadAfterTransition(LightBoxImageView var1) {
      Transition var2 = this.getWindow().getSharedElementEnterTransition();
      var2.addListener(new LightBoxActivity$2(this, var1));
   }

   private void loadImage(ImageView var1) {
      ImageRequest var2 = new Builder(var1.getContext()).data(this.imageUrl).crossfade(true).error(drawable.intercom_image_load_failed).target(var1).build();
      IntercomCoilKt.loadIntercomImage(var1.getContext(), var2);
   }

   private void fadeIn() {
      int var1 = ContextCompat.getColor(this, color.intercom_full_transparent_full_black);
      int var2 = ContextCompat.getColor(this, color.intercom_transparent_black_lightbox);
      ValueAnimator var3 = ValueAnimator.ofObject(new ArgbEvaluator(), var1, var2);
      var3.setDuration(300L);
      var3.addUpdateListener(new LightBoxActivity$3(this));
      var3.start();
   }

   private void fadeOut() {
      int var1 = ContextCompat.getColor(this, color.intercom_transparent_black_lightbox);
      int var2 = ContextCompat.getColor(this, color.intercom_full_transparent_full_black);
      ValueAnimator var3 = ValueAnimator.ofObject(new ArgbEvaluator(), var1, var2);
      var3.setDuration(300L);
      var3.addUpdateListener(new LightBoxActivity$4(this));
      var3.start();
   }

   public static Intent imageIntent(Context var0, String var1, boolean var2) {
      return new Intent(var0, LightBoxActivity.class).putExtra("extra_image_url", var1).putExtra("extra_activity_fullscreen", var2);
   }

   @Override
   public void closeLightBox() {
      this.fadeOut();
      this.supportFinishAfterTransition();
   }

   // $VF: synthetic method
   static void access$000(LightBoxActivity var0, ImageView var1) {
      var0.loadImage(var1);
   }
}
