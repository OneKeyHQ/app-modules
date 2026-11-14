package io.intercom.android.sdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;

public final class IntercomWebviewCardInputBinding implements ViewBinding {
   @NonNull
   private final FrameLayout rootView;
   @NonNull
   public final AutoCompleteTextView input;

   private IntercomWebviewCardInputBinding(@NonNull FrameLayout var1, @NonNull AutoCompleteTextView var2) {
      this.rootView = var1;
      this.input = var2;
   }

   @NonNull
   public FrameLayout getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomWebviewCardInputBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomWebviewCardInputBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_webview_card_input, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomWebviewCardInputBinding bind(@NonNull View var0) {
      int var1 = id.input;
      AutoCompleteTextView var2 = (AutoCompleteTextView)ViewBindings.findChildViewById(var0, var1);
      if (var2 == null) {
         String var3 = var0.getResources().getResourceName(var1);
         throw new NullPointerException("Missing required view with ID: ".concat(var3));
      } else {
         return new IntercomWebviewCardInputBinding((FrameLayout)var0, var2);
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
