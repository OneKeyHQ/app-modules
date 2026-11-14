package io.intercom.android.sdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;

public final class IntercomToolbarBinding implements ViewBinding {
   @NonNull
   private final FrameLayout rootView;
   @NonNull
   public final FrameLayout intercomLeftItemLayout;
   @NonNull
   public final ImageView intercomToolbarAvatar;
   @NonNull
   public final View intercomToolbarAvatarActiveState;
   @NonNull
   public final ImageButton intercomToolbarClose;
   @NonNull
   public final View intercomToolbarDivider;
   @NonNull
   public final ImageButton intercomToolbarInbox;
   @NonNull
   public final TextView intercomToolbarSubtitle;
   @NonNull
   public final TextView intercomToolbarTitle;
   @NonNull
   public final LinearLayout intercomToolbarTitleContainer;
   @NonNull
   public final RelativeLayout toolbarContentContainer;
   @NonNull
   public final ProgressBar toolbarProgressBar;
   @NonNull
   public final ImageView wallpaperImage;

   private IntercomToolbarBinding(
      @NonNull FrameLayout var1,
      @NonNull FrameLayout var2,
      @NonNull ImageView var3,
      @NonNull View var4,
      @NonNull ImageButton var5,
      @NonNull View var6,
      @NonNull ImageButton var7,
      @NonNull TextView var8,
      @NonNull TextView var9,
      @NonNull LinearLayout var10,
      @NonNull RelativeLayout var11,
      @NonNull ProgressBar var12,
      @NonNull ImageView var13
   ) {
      this.rootView = var1;
      this.intercomLeftItemLayout = var2;
      this.intercomToolbarAvatar = var3;
      this.intercomToolbarAvatarActiveState = var4;
      this.intercomToolbarClose = var5;
      this.intercomToolbarDivider = var6;
      this.intercomToolbarInbox = var7;
      this.intercomToolbarSubtitle = var8;
      this.intercomToolbarTitle = var9;
      this.intercomToolbarTitleContainer = var10;
      this.toolbarContentContainer = var11;
      this.toolbarProgressBar = var12;
      this.wallpaperImage = var13;
   }

   @NonNull
   public FrameLayout getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomToolbarBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomToolbarBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_toolbar, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomToolbarBinding bind(@NonNull View var0) {
      int var1 = id.intercom_left_item_layout;
      FrameLayout var2 = (FrameLayout)ViewBindings.findChildViewById(var0, var1);
      if (var2 != null) {
         var1 = id.intercom_toolbar_avatar;
         ImageView var3 = (ImageView)ViewBindings.findChildViewById(var0, var1);
         if (var3 != null) {
            var1 = id.intercom_toolbar_avatar_active_state;
            View var4 = ViewBindings.findChildViewById(var0, var1);
            if (var4 != null) {
               var1 = id.intercom_toolbar_close;
               ImageButton var5 = (ImageButton)ViewBindings.findChildViewById(var0, var1);
               if (var5 != null) {
                  var1 = id.intercom_toolbar_divider;
                  View var6 = ViewBindings.findChildViewById(var0, var1);
                  if (var6 != null) {
                     var1 = id.intercom_toolbar_inbox;
                     ImageButton var7 = (ImageButton)ViewBindings.findChildViewById(var0, var1);
                     if (var7 != null) {
                        var1 = id.intercom_toolbar_subtitle;
                        TextView var8 = (TextView)ViewBindings.findChildViewById(var0, var1);
                        if (var8 != null) {
                           var1 = id.intercom_toolbar_title;
                           TextView var9 = (TextView)ViewBindings.findChildViewById(var0, var1);
                           if (var9 != null) {
                              var1 = id.intercom_toolbar_title_container;
                              LinearLayout var10 = (LinearLayout)ViewBindings.findChildViewById(var0, var1);
                              if (var10 != null) {
                                 var1 = id.toolbar_content_container;
                                 RelativeLayout var11 = (RelativeLayout)ViewBindings.findChildViewById(var0, var1);
                                 if (var11 != null) {
                                    var1 = id.toolbar_progress_bar;
                                    ProgressBar var12 = (ProgressBar)ViewBindings.findChildViewById(var0, var1);
                                    if (var12 != null) {
                                       var1 = id.wallpaper_image;
                                       ImageView var13 = (ImageView)ViewBindings.findChildViewById(var0, var1);
                                       if (var13 != null) {
                                          return new IntercomToolbarBinding(
                                             (FrameLayout)var0, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13
                                          );
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      String var14 = var0.getResources().getResourceName(var1);
      throw new NullPointerException("Missing required view with ID: ".concat(var14));
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
