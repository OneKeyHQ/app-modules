package io.intercom.android.sdk.carousel;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;
import androidx.core.os.BundleCompat;
import androidx.fragment.app.Fragment;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.blocks.CarouselImageClickListener;
import io.intercom.android.sdk.blocks.ContentLayoutManager;
import io.intercom.android.sdk.blocks.UploadingImageCache;
import io.intercom.android.sdk.blocks.ViewHolderGenerator;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.models.carousel.CarouselScreen;
import io.intercom.android.sdk.models.carousel.ScreenAction;
import io.intercom.android.sdk.utilities.FontUtils;
import io.intercom.android.sdk.views.ContentAwareScrollView;
import java.util.List;

public class CarouselScreenFragment extends Fragment {
   public static final int CAROUSEL_ANIMATION_MS = 400;
   public static final int CAROUSEL_ANIMATION_DELAY_MS = 300;
   private static final String KEY_CAROUSEL_SCREEN = "carousel_screen";
   private boolean tickShouldAnimate = true;
   private LinearLayout actionLayout;
   private CarouselScreen carouselScreen = CarouselScreen.NULL;
   private CarouselListener listener;
   private PermissionManager permissionManager;

   public static CarouselScreenFragment newInstance(CarouselScreen var0) {
      CarouselScreenFragment var1 = new CarouselScreenFragment();
      Bundle var2 = new Bundle();
      var2.putParcelable("carousel_screen", var0);
      var1.setArguments(var2);
      return var1;
   }

   @Override
   public void onAttach(Context var1) {
      super.onAttach(var1);

      try {
         this.listener = (CarouselListener)var1;
      } catch (ClassCastException var3) {
         throw new ClassCastException(var1 + " must implement CarouselListener");
      }
   }

   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater var1, @Nullable ViewGroup var2, @Nullable Bundle var3) {
      Bundle var4 = this.getArguments();
      if (var4 != null) {
         this.carouselScreen = (CarouselScreen)BundleCompat.getParcelable(var4, "carousel_screen", CarouselScreen.class);
      }

      Injector var5 = Injector.get();
      Api var6 = var5.getApi();
      Provider var7 = var5.getAppConfigProvider();
      ViewHolderGenerator var8 = new ViewHolderGenerator(
         new UploadingImageCache(), var6, var7, "", new CarouselImageClickListener(var6), null, var5.getGson(), var5.getMetricTracker()
      );
      ContentLayoutManager var9 = new ContentLayoutManager(this.getContext(), var8.getCarouselHolder());
      this.permissionManager = new PermissionManager(this.getActivity());
      View var10 = var1.inflate(layout.intercom_fragment_carousel_screen, var2, false);
      LinearLayout var11 = (LinearLayout)var10.findViewById(id.intercom_carousel_fragment_root);
      this.actionLayout = (LinearLayout)var10.findViewById(id.intercom_carousel_action_layout);
      FrameLayout var12 = (FrameLayout)var10.findViewById(id.intercom_carousel_content_container);
      View var13 = var10.findViewById(id.intercom_carousel_gradient);
      ContentAwareScrollView var14 = (ContentAwareScrollView)var10.findViewById(id.intercom_carousel_scroll_view);
      int var15 = Color.parseColor(this.carouselScreen.getBackgroundColor());
      this.applyBottomGradient(var13, var14, var15);
      var11.setBackgroundColor(var15);
      this.displayScreenActions();
      var9.renderCarouselScreen(this.carouselScreen, var12);
      return var10;
   }

   @Override
   public void onResume() {
      super.onResume();
      this.displayScreenActions();
   }

   private void displayScreenActions() {
      this.actionLayout.removeAllViews();
      List var1 = this.carouselScreen.getActions();
      ScreenAction var2 = this.carouselScreen.getPermissionAction();

      for (java.lang.Object recoveredEntryElement_0_108 : var1) {
         ScreenAction var4 = (ScreenAction) recoveredEntryElement_0_108;
         TextView var5 = this.createButton(var4);
         if (ScreenAction.NULL.equals(var2) || !this.permissionManager.permissionsGranted(var2.getValidPermissions(this.permissionManager))) {
            this.actionLayout.addView(var5);
         } else if ("skip".equals(var4.getType())) {
            this.actionLayout.addView(this.buttonWithTickIcon(var5, Color.parseColor(var4.getTextColor())));
         } else if (var4.isPermissionAction()) {
         }
      }
   }

   private TextView createButton(ScreenAction var1) {
      LayoutInflater var2 = LayoutInflater.from(this.getContext());
      TextView var3 = (TextView)var2.inflate(layout.intercom_carousel_action_button, this.actionLayout, false);
      FontUtils.setRobotoMediumTypeface(var3);
      var3.setText(var1.getTitle());
      var3.getBackground().setColorFilter(Color.parseColor(var1.getBackgroundColor()), Mode.SRC_IN);
      var3.setTextColor(Color.parseColor(var1.getTextColor()));
      var3.setOnClickListener(new ActionOnClickListener(var1, this.listener));
      var3.setContentDescription(var1.getTitle() + ", button");
      return var3;
   }

   private FrameLayout buttonWithTickIcon(TextView var1, @ColorInt int var2) {
      var1.setText("");
      LayoutInflater var3 = LayoutInflater.from(this.getContext());
      FrameLayout var4 = new FrameLayout(var1.getContext());
      var4.setLayoutParams(var1.getLayoutParams());
      ImageView var5 = (ImageView)var3.inflate(layout.intercom_carousel_action_button_tick, var4, false);
      var5.getDrawable().setColorFilter(var2, Mode.SRC_IN);
      var4.addView(var1);
      var4.addView(var5);
      if (this.tickShouldAnimate) {
         this.animateTickIcon(var5);
      }

      return var4;
   }

   private void animateTickIcon(ImageView var1) {
      var1.clearAnimation();
      var1.setAlpha(0.0F);
      var1.setScaleX(0.0F);
      var1.setScaleY(0.0F);
      var1.setRotation(-360.0F);
      var1.animate().alpha(1.0F).rotationBy(360.0F).scaleX(1.0F).scaleY(1.0F).setStartDelay(300L).setDuration(400L).start();
      this.tickShouldAnimate = false;
   }

   private void applyBottomGradient(View var1, ContentAwareScrollView var2, @ColorInt int var3) {
      int var4 = ColorUtils.setAlphaComponent(var3, 0);
      int[] var5 = new int[]{var3, var4};
      GradientDrawable var6 = new GradientDrawable(Orientation.BOTTOM_TOP, var5);
      var1.setBackground(var6);
      var2.setListener(new CarouselScreenFragment$1(this, var1, var2));
      var1.getViewTreeObserver().addOnGlobalLayoutListener(new CarouselScreenFragment$2(this, var2, var1));
   }
}
