package io.intercom.android.sdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.lightbox.LightBoxImageView;

public final class IntercomActivityLightboxBinding implements ViewBinding {
   @NonNull
   private final RelativeLayout rootView_;
   @NonNull
   public final LightBoxImageView fullImage;
   @NonNull
   public final RelativeLayout rootView;

   private IntercomActivityLightboxBinding(@NonNull RelativeLayout var1, @NonNull LightBoxImageView var2, @NonNull RelativeLayout var3) {
      this.rootView_ = var1;
      this.fullImage = var2;
      this.rootView = var3;
   }

   @NonNull
   public RelativeLayout getRoot() {
      return this.rootView_;
   }

   @NonNull
   public static IntercomActivityLightboxBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomActivityLightboxBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_activity_lightbox, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomActivityLightboxBinding bind(@NonNull View var0) {
      int var1 = id.full_image;
      LightBoxImageView var2 = (LightBoxImageView)ViewBindings.findChildViewById(var0, var1);
      if (var2 == null) {
         String var4 = var0.getResources().getResourceName(var1);
         throw new NullPointerException("Missing required view with ID: ".concat(var4));
      } else {
         RelativeLayout var3 = (RelativeLayout)var0;
         return new IntercomActivityLightboxBinding((RelativeLayout)var0, var2, var3);
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
