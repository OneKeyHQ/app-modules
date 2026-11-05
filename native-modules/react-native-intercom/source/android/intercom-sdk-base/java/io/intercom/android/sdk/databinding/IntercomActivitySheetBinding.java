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
import io.intercom.android.sdk.views.IntercomToolbar;

public final class IntercomActivitySheetBinding implements ViewBinding {
   @NonNull
   private final LinearLayout rootView;
   @NonNull
   public final IntercomToolbar intercomToolbar;
   @NonNull
   public final LinearLayout sheetRoot;
   @NonNull
   public final FrameLayout sheetView;

   private IntercomActivitySheetBinding(@NonNull LinearLayout var1, @NonNull IntercomToolbar var2, @NonNull LinearLayout var3, @NonNull FrameLayout var4) {
      this.rootView = var1;
      this.intercomToolbar = var2;
      this.sheetRoot = var3;
      this.sheetView = var4;
   }

   @NonNull
   public LinearLayout getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomActivitySheetBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomActivitySheetBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_activity_sheet, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomActivitySheetBinding bind(@NonNull View var0) {
      int var1 = id.intercom_toolbar;
      IntercomToolbar var2 = (IntercomToolbar)ViewBindings.findChildViewById(var0, var1);
      if (var2 != null) {
         LinearLayout var3 = (LinearLayout)var0;
         var1 = id.sheet_view;
         FrameLayout var4 = (FrameLayout)ViewBindings.findChildViewById(var0, var1);
         if (var4 != null) {
            return new IntercomActivitySheetBinding((LinearLayout)var0, var2, var3, var4);
         }
      }

      String var5 = var0.getResources().getResourceName(var1);
      throw new NullPointerException("Missing required view with ID: ".concat(var5));
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
