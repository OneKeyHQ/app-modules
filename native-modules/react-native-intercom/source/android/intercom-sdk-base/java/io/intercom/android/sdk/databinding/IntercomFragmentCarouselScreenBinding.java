package io.intercom.android.sdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.views.ContentAwareScrollView;

public final class IntercomFragmentCarouselScreenBinding implements ViewBinding {
   @NonNull
   private final LinearLayout rootView;
   @NonNull
   public final LinearLayout intercomCarouselActionLayout;
   @NonNull
   public final FrameLayout intercomCarouselContentContainer;
   @NonNull
   public final LinearLayout intercomCarouselFragmentRoot;
   @NonNull
   public final View intercomCarouselGradient;
   @NonNull
   public final ContentAwareScrollView intercomCarouselScrollView;

   private IntercomFragmentCarouselScreenBinding(
      @NonNull LinearLayout var1,
      @NonNull LinearLayout var2,
      @NonNull FrameLayout var3,
      @NonNull LinearLayout var4,
      @NonNull View var5,
      @NonNull ContentAwareScrollView var6
   ) {
      this.rootView = var1;
      this.intercomCarouselActionLayout = var2;
      this.intercomCarouselContentContainer = var3;
      this.intercomCarouselFragmentRoot = var4;
      this.intercomCarouselGradient = var5;
      this.intercomCarouselScrollView = var6;
   }

   @NonNull
   public LinearLayout getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomFragmentCarouselScreenBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomFragmentCarouselScreenBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_fragment_carousel_screen, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomFragmentCarouselScreenBinding bind(@NonNull View var0) {
      int var1 = id.intercom_carousel_action_layout;
      LinearLayout var2 = (LinearLayout)ViewBindings.findChildViewById(var0, var1);
      if (var2 != null) {
         var1 = id.intercom_carousel_content_container;
         FrameLayout var3 = (FrameLayout)ViewBindings.findChildViewById(var0, var1);
         if (var3 != null) {
            LinearLayout var4 = (LinearLayout)var0;
            var1 = id.intercom_carousel_gradient;
            View var5 = ViewBindings.findChildViewById(var0, var1);
            if (var5 != null) {
               var1 = id.intercom_carousel_scroll_view;
               ContentAwareScrollView var6 = (ContentAwareScrollView)ViewBindings.findChildViewById(var0, var1);
               if (var6 != null) {
                  return new IntercomFragmentCarouselScreenBinding((LinearLayout)var0, var2, var3, var4, var5, var6);
               }
            }
         }
      }

      String var7 = var0.getResources().getResourceName(var1);
      throw new NullPointerException("Missing required view with ID: ".concat(var7));
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
