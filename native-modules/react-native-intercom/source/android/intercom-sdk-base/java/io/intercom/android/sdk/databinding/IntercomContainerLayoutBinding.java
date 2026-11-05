package io.intercom.android.sdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;

public final class IntercomContainerLayoutBinding implements ViewBinding {
   @NonNull
   private final FrameLayout rootView;
   @NonNull
   public final LinearLayout cellLayout;
   @NonNull
   public final ProgressBar progressBar;

   private IntercomContainerLayoutBinding(@NonNull FrameLayout var1, @NonNull LinearLayout var2, @NonNull ProgressBar var3) {
      this.rootView = var1;
      this.cellLayout = var2;
      this.progressBar = var3;
   }

   @NonNull
   public FrameLayout getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomContainerLayoutBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomContainerLayoutBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_container_layout, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomContainerLayoutBinding bind(@NonNull View var0) {
      int var1 = id.cellLayout;
      LinearLayout var2 = (LinearLayout)ViewBindings.findChildViewById(var0, var1);
      if (var2 != null) {
         var1 = id.progressBar;
         ProgressBar var3 = (ProgressBar)ViewBindings.findChildViewById(var0, var1);
         if (var3 != null) {
            return new IntercomContainerLayoutBinding((FrameLayout)var0, var2, var3);
         }
      }

      String var4 = var0.getResources().getResourceName(var1);
      throw new NullPointerException("Missing required view with ID: ".concat(var4));
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
