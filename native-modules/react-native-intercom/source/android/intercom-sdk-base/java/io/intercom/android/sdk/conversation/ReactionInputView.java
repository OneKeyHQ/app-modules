package io.intercom.android.sdk.conversation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorFilter;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View.OnTouchListener;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import coil.request.ImageRequest;
import coil.request.ImageRequest.Builder;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.R.dimen;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.models.Reaction;
import io.intercom.android.sdk.models.ReactionReply;
import io.intercom.android.sdk.utilities.ColorUtils;
import io.intercom.android.sdk.utilities.IntercomCoilKt;
import io.intercom.android.sdk.utilities.commons.DeviceUtils;
import java.util.ArrayList;
import java.util.List;

public class ReactionInputView extends LinearLayout {
   private static final float REACTION_SCALE = 2.5F;
   private static final int VIBRATION_DURATION_MS = 10;
   private final List reactionViews;
   private final Vibrator vibrator;
   private final ColorFilter deselectedFilter;
   private final Twig twig = LumberMill.getLogger();
   Integer highlightedViewIndex = null;
   int reactionsLoaded = 0;
   private ReactionReply reactionReply;
   private ReactionListener listener;
   private final OnTouchListener touchListener = new ReactionInputView$2(this);

   public ReactionInputView(Context var1) {
      this(var1, null, 0);
   }

   public ReactionInputView(Context var1, AttributeSet var2) {
      this(var1, var2, 0);
   }

   public ReactionInputView(Context var1, AttributeSet var2, int var3) {
      super(var1, var2, var3);
      this.reactionViews = new ArrayList();
      this.setVisibility(8);
      if (this.isInEditMode()) {
         this.vibrator = null;
      } else {
         this.vibrator = (Vibrator)var1.getSystemService("vibrator");
      }

      this.deselectedFilter = ColorUtils.newGreyscaleFilter();
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      this.setOnTouchListener(this.touchListener);
   }

   @Override
   protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      this.setOnTouchListener(null);
   }

   public void setUpReactions(ReactionReply var1, boolean var2, ReactionListener var3) {
      Context var4 = this.getContext();
      this.reactionReply = var1;
      this.listener = var3;
      this.resetView();
      List var5 = var1.getReactionSet();
      int var6 = var5.size();

      for (int var7 = 0; var7 < var5.size(); var7++) {
         FrameLayout var8 = new FrameLayout(var4);
         var8.setClipChildren(false);
         var8.setClipToPadding(false);
         var8.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1.0F));
         ImageView var9 = new ImageView(var4);
         int var10 = this.getResources().getDimensionPixelSize(dimen.intercom_reaction_size);
         var9.setLayoutParams(new FrameLayout.LayoutParams(var10, var10, 17));
         var9.setPivotY(this.getResources().getDimensionPixelSize(dimen.intercom_reaction_offset));
         var9.setPivotX(var10 / 2);
         this.reactionViews.add(var9);
         var8.addView(var9);
         Reaction var11 = (Reaction)var5.get(var7);
         ImageRequest var12 = new Builder(var4).target(new ReactionInputView$1(this, var9, var6, var2, var4)).data(var11.getImageUrl()).build();
         IntercomCoilKt.loadIntercomImage(var4, var12);
         Integer var13 = var1.getReactionIndex();
         if (var13 != null && !var13.equals(var11.getIndex())) {
            this.deselectViewAtIndex(var7);
         }

         this.addView(var8);
      }
   }

   public void preloadReactionImages(ReactionReply var1) {
      for (java.lang.Object recoveredEntryElement_0_105 : var1.getReactionSet()) {
         Reaction var3 = (Reaction) recoveredEntryElement_0_105;
         ImageRequest var4 = new Builder(this.getContext()).data(var3.getImageUrl()).build();
         IntercomCoilKt.loadIntercomImage(this.getContext(), var4);
      }
   }

   @SuppressLint("MissingPermission")
   void vibrateForSelection() {
      if (DeviceUtils.hasPermission(this.getContext(), "android.permission.VIBRATE")) {
         this.vibrator.vibrate(10L);
      }
   }

   void handleTouchUp() {
      if (this.highlightedViewIndex != null && this.highlightedViewIndex >= 0 && this.highlightedViewIndex < this.reactionReply.getReactionSet().size()) {
         Reaction var1 = (Reaction)this.reactionReply.getReactionSet().get(this.highlightedViewIndex);
         if (var1 != null && (this.reactionReply.getReactionIndex() == null || var1.getIndex() != this.reactionReply.getReactionIndex())) {
            this.reactionReply.setReactionIndex(var1.getIndex());
            if (this.listener != null) {
               this.listener.onReactionSelected(var1);
            }
         }
      }

      this.highlightSelectedReaction();
      this.highlightedViewIndex = null;
   }

   void highlightSelectedReaction() {
      for (int var1 = 0; var1 < this.getChildCount(); var1++) {
         Integer var2 = this.reactionReply.getReactionIndex();
         Reaction var3 = (Reaction)this.reactionReply.getReactionSet().get(var1);
         if (var2 != null && !var2.equals(var3.getIndex())) {
            this.deselectViewAtIndex(var1);
         } else {
            this.selectViewAtIndex(1.0F, var1);
         }
      }
   }

   Integer getCurrentSelectedIndex() {
      for (int var1 = 0; var1 < this.getChildCount(); var1++) {
         Integer var2 = this.reactionReply.getReactionIndex();
         Reaction var3 = (Reaction)this.reactionReply.getReactionSet().get(var1);
         if (var2 == null || var2.equals(var3.getIndex())) {
            return var1;
         }
      }

      return null;
   }

   void selectViewAtIndex(float var1, int var2) {
      ImageView var3 = (ImageView)this.reactionViews.get(var2);
      var3.animate().setInterpolator(new OvershootInterpolator(2.0F)).scaleX(var1).scaleY(var1).setDuration(200L).start();
      var3.clearColorFilter();
   }

   void deselectViewAtIndex(int var1) {
      ImageView var2 = (ImageView)this.reactionViews.get(var1);
      var2.animate().setInterpolator(new OvershootInterpolator(0.6F)).scaleX(1.0F).scaleY(1.0F).setDuration(200L).start();
      var2.setColorFilter(this.deselectedFilter);
   }

   private void resetView() {
      this.removeAllViews();
      this.reactionViews.clear();
   }
}
