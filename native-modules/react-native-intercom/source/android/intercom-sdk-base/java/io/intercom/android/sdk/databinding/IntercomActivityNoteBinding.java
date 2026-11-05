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
import io.intercom.android.sdk.conversation.ReactionInputView;
import io.intercom.android.sdk.views.ContentAwareScrollView;
import io.intercom.android.sdk.views.IntercomToolbar;

public final class IntercomActivityNoteBinding implements ViewBinding {
   @NonNull
   private final FrameLayout rootView;
   @NonNull
   public final LinearLayout appBarLayout;
   @NonNull
   public final IntercomToolbar intercomToolbar;
   @NonNull
   public final FrameLayout noteComposerContainer;
   @NonNull
   public final LinearLayout noteLayout;
   @NonNull
   public final FrameLayout noteRoot;
   @NonNull
   public final View noteTouchTarget;
   @NonNull
   public final ContentAwareScrollView noteView;
   @NonNull
   public final ReactionInputView reactionInputView;

   private IntercomActivityNoteBinding(
      @NonNull FrameLayout var1,
      @NonNull LinearLayout var2,
      @NonNull IntercomToolbar var3,
      @NonNull FrameLayout var4,
      @NonNull LinearLayout var5,
      @NonNull FrameLayout var6,
      @NonNull View var7,
      @NonNull ContentAwareScrollView var8,
      @NonNull ReactionInputView var9
   ) {
      this.rootView = var1;
      this.appBarLayout = var2;
      this.intercomToolbar = var3;
      this.noteComposerContainer = var4;
      this.noteLayout = var5;
      this.noteRoot = var6;
      this.noteTouchTarget = var7;
      this.noteView = var8;
      this.reactionInputView = var9;
   }

   @NonNull
   public FrameLayout getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomActivityNoteBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomActivityNoteBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_activity_note, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomActivityNoteBinding bind(@NonNull View var0) {
      int var1 = id.app_bar_layout;
      LinearLayout var2 = (LinearLayout)ViewBindings.findChildViewById(var0, var1);
      if (var2 != null) {
         var1 = id.intercom_toolbar;
         IntercomToolbar var3 = (IntercomToolbar)ViewBindings.findChildViewById(var0, var1);
         if (var3 != null) {
            var1 = id.note_composer_container;
            FrameLayout var4 = (FrameLayout)ViewBindings.findChildViewById(var0, var1);
            if (var4 != null) {
               var1 = id.note_layout;
               LinearLayout var5 = (LinearLayout)ViewBindings.findChildViewById(var0, var1);
               if (var5 != null) {
                  FrameLayout var6 = (FrameLayout)var0;
                  var1 = id.note_touch_target;
                  View var7 = ViewBindings.findChildViewById(var0, var1);
                  if (var7 != null) {
                     var1 = id.note_view;
                     ContentAwareScrollView var8 = (ContentAwareScrollView)ViewBindings.findChildViewById(var0, var1);
                     if (var8 != null) {
                        var1 = id.reaction_input_view;
                        ReactionInputView var9 = (ReactionInputView)ViewBindings.findChildViewById(var0, var1);
                        if (var9 != null) {
                           return new IntercomActivityNoteBinding((FrameLayout)var0, var2, var3, var4, var5, var6, var7, var8, var9);
                        }
                     }
                  }
               }
            }
         }
      }

      String var10 = var0.getResources().getResourceName(var1);
      throw new NullPointerException("Missing required view with ID: ".concat(var10));
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
