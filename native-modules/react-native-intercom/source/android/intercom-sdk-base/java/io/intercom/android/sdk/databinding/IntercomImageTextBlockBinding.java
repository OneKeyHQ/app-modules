package io.intercom.android.sdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;

public final class IntercomImageTextBlockBinding implements ViewBinding {
   @NonNull
   private final LinearLayout rootView;
   @NonNull
   public final FrameLayout imageHolder;
   @NonNull
   public final TextView text;
   @NonNull
   public final TextView title;

   private IntercomImageTextBlockBinding(@NonNull LinearLayout var1, @NonNull FrameLayout var2, @NonNull TextView var3, @NonNull TextView var4) {
      this.rootView = var1;
      this.imageHolder = var2;
      this.text = var3;
      this.title = var4;
   }

   @NonNull
   public LinearLayout getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomImageTextBlockBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomImageTextBlockBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_image_text_block, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomImageTextBlockBinding bind(@NonNull View var0) {
      int var1 = id.image_holder;
      FrameLayout var2 = (FrameLayout)ViewBindings.findChildViewById(var0, var1);
      if (var2 != null) {
         var1 = id.text;
         TextView var3 = (TextView)ViewBindings.findChildViewById(var0, var1);
         if (var3 != null) {
            var1 = id.title;
            TextView var4 = (TextView)ViewBindings.findChildViewById(var0, var1);
            if (var4 != null) {
               return new IntercomImageTextBlockBinding((LinearLayout)var0, var2, var3, var4);
            }
         }
      }

      String var5 = var0.getResources().getResourceName(var1);
      throw new NullPointerException("Missing required view with ID: ".concat(var5));
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
