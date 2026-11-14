package io.intercom.android.sdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import io.intercom.android.sdk.R.layout;

public final class IntercomBlocksCarouselLayoutBinding implements ViewBinding {
   @NonNull
   private final LinearLayout rootView;

   private IntercomBlocksCarouselLayoutBinding(@NonNull LinearLayout var1) {
      this.rootView = var1;
   }

   @NonNull
   public LinearLayout getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomBlocksCarouselLayoutBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomBlocksCarouselLayoutBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_blocks_carousel_layout, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomBlocksCarouselLayoutBinding bind(@NonNull View var0) {
      if (var0 == null) {
         throw new NullPointerException("rootView");
      } else {
         return new IntercomBlocksCarouselLayoutBinding((LinearLayout)var0);
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
