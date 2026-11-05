package io.intercom.android.sdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import androidx.viewpager.widget.ViewPager;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;

public final class IntercomActivityCarouselBinding implements ViewBinding {
   @NonNull
   private final ConstraintLayout rootView;
   @NonNull
   public final ConstraintLayout intercomCarouselCloseContainer;
   @NonNull
   public final ConstraintLayout intercomCarouselRoot;
   @NonNull
   public final ImageButton intercomClose;
   @NonNull
   public final View intercomCloseBackground;
   @NonNull
   public final LinearLayout intercomPageNavigationLayout;
   @NonNull
   public final FrameLayout intercomStateContainer;
   @NonNull
   public final ViewPager intercomViewPager;

   private IntercomActivityCarouselBinding(
      @NonNull ConstraintLayout var1,
      @NonNull ConstraintLayout var2,
      @NonNull ConstraintLayout var3,
      @NonNull ImageButton var4,
      @NonNull View var5,
      @NonNull LinearLayout var6,
      @NonNull FrameLayout var7,
      @NonNull ViewPager var8
   ) {
      this.rootView = var1;
      this.intercomCarouselCloseContainer = var2;
      this.intercomCarouselRoot = var3;
      this.intercomClose = var4;
      this.intercomCloseBackground = var5;
      this.intercomPageNavigationLayout = var6;
      this.intercomStateContainer = var7;
      this.intercomViewPager = var8;
   }

   @NonNull
   public ConstraintLayout getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomActivityCarouselBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomActivityCarouselBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_activity_carousel, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomActivityCarouselBinding bind(@NonNull View var0) {
      int var1 = id.intercom_carousel_close_container;
      ConstraintLayout var2 = (ConstraintLayout)ViewBindings.findChildViewById(var0, var1);
      if (var2 != null) {
         ConstraintLayout var3 = (ConstraintLayout)var0;
         var1 = id.intercom_close;
         ImageButton var4 = (ImageButton)ViewBindings.findChildViewById(var0, var1);
         if (var4 != null) {
            var1 = id.intercom_close_background;
            View var5 = ViewBindings.findChildViewById(var0, var1);
            if (var5 != null) {
               var1 = id.intercom_page_navigation_layout;
               LinearLayout var6 = (LinearLayout)ViewBindings.findChildViewById(var0, var1);
               if (var6 != null) {
                  var1 = id.intercom_state_container;
                  FrameLayout var7 = (FrameLayout)ViewBindings.findChildViewById(var0, var1);
                  if (var7 != null) {
                     var1 = id.intercom_view_pager;
                     ViewPager var8 = (ViewPager)ViewBindings.findChildViewById(var0, var1);
                     if (var8 != null) {
                        return new IntercomActivityCarouselBinding((ConstraintLayout)var0, var2, var3, var4, var5, var6, var7, var8);
                     }
                  }
               }
            }
         }
      }

      String var9 = var0.getResources().getResourceName(var1);
      throw new NullPointerException("Missing required view with ID: ".concat(var9));
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
