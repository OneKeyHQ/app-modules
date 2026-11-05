package io.intercom.android.sdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import io.intercom.android.sdk.R.layout;

public final class IntercomProgressBarBinding implements ViewBinding {
   @NonNull
   private final ProgressBar rootView;
   @NonNull
   public final ProgressBar progressBar;

   private IntercomProgressBarBinding(@NonNull ProgressBar var1, @NonNull ProgressBar var2) {
      this.rootView = var1;
      this.progressBar = var2;
   }

   @NonNull
   public ProgressBar getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomProgressBarBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomProgressBarBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_progress_bar, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomProgressBarBinding bind(@NonNull View var0) {
      if (var0 == null) {
         throw new NullPointerException("rootView");
      }

      ProgressBar var1 = (ProgressBar)var0;
      return new IntercomProgressBarBinding((ProgressBar)var0, var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
