package io.intercom.android.sdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import io.intercom.android.sdk.R.layout;

public final class IntercomFakeComposerBinding implements ViewBinding {
   @NonNull
   private final EditText rootView;
   @NonNull
   public final EditText composerInputView;

   private IntercomFakeComposerBinding(@NonNull EditText var1, @NonNull EditText var2) {
      this.rootView = var1;
      this.composerInputView = var2;
   }

   @NonNull
   public EditText getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomFakeComposerBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomFakeComposerBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_fake_composer, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomFakeComposerBinding bind(@NonNull View var0) {
      if (var0 == null) {
         throw new NullPointerException("rootView");
      }

      EditText var1 = (EditText)var0;
      return new IntercomFakeComposerBinding((EditText)var0, var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
