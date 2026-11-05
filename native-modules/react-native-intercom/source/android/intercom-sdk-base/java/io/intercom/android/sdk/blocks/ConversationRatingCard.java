package io.intercom.android.sdk.blocks;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff.Mode;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import coil.request.ImageRequest;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.dimen;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.api.ErrorObject;
import io.intercom.android.sdk.api.ErrorStringExtractorKt;
import io.intercom.android.sdk.blocks.lib.interfaces.ConversationRatingBlock;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;
import io.intercom.android.sdk.blocks.lib.models.ConversationRating;
import io.intercom.android.sdk.blocks.lib.models.ConversationRatingOption;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.components.IntercomPrimaryButton;
import io.intercom.android.sdk.utilities.ColorUtils;
import io.intercom.android.sdk.utilities.IntercomCoilKt;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;
import java.util.ArrayList;
import java.util.List;

public class ConversationRatingCard implements ConversationRatingBlock {
   private static final float SELECTED_RATING_SCALE = 1.2F;
   private static final float DESELECTED_RATING_SCALE = 1.0F;
   private static final float RATING_HORIZONTAL_PADDING = 17.0F;
   private static final float RATING_VERTICAL_PADDING = 14.0F;
   private static final float DIALOG_HORIZONTAL_MARGIN = 16.0F;
   private static final long ANIMATION_DURATION_MS = 200L;
   private static final String ERROR_TYPE_RATING_WINDOW_CLOSED = "rating_window_closed";
   private static final String ERROR_TYPE_RATING_LOCKED = "rating_locked";
   private final Api api;
   private final String conversationId;
   private final Provider appConfigProvider;
   private final ColorFilter deselectedFilter;
   private final List ratingViews;
   private LinearLayout rootLayout;
   private IntercomPrimaryButton tellUsMoreButton;
   private ConversationRating conversationRating;
   private boolean submissionInProgress;
   private final OnClickListener ratingClickListener = new ConversationRatingCard$1(this);

   ConversationRatingCard(Api var1, String var2, Provider var3) {
      this.api = var1;
      this.conversationId = var2;
      this.appConfigProvider = var3;
      this.ratingViews = new ArrayList();
      this.deselectedFilter = ColorUtils.newGreyscaleFilter();
   }

   @Override
   public View addConversationRatingBlock(ConversationRating var1, BlockMetadata var2, ViewGroup var3) {
      return this.createConversationRatingBlock(var1, var3);
   }

   private View createConversationRatingBlock(ConversationRating var1, ViewGroup var2) {
      Context var3 = var2.getContext();
      this.ratingViews.clear();
      this.conversationRating = var1;
      this.submissionInProgress = false;
      LayoutInflater var4 = LayoutInflater.from(var3);
      this.rootLayout = (LinearLayout)var4.inflate(layout.intercom_conversation_rating_block, var2, false);
      this.tellUsMoreButton = (IntercomPrimaryButton)this.rootLayout.findViewById(id.intercom_rating_tell_us_more_button);
      this.tellUsMoreButton.setText(var3.getString(string.intercom_tell_us_more));
      this.updateViewVisibility();
      this.setupRatingsLayout();
      this.updateSelectedRating();
      this.updateInteractionEnabled();
      return this.rootLayout;
   }

   private void updateInteractionEnabled() {
      boolean var1 = !this.submissionInProgress && !this.conversationRating.isBlockRating();

      for (java.lang.Object recoveredEntryElement_0_97 : this.ratingViews) {
         ImageView var3 = (ImageView) recoveredEntryElement_0_97;
         var3.setEnabled(var1);
      }

      if (this.tellUsMoreButton != null) {
         if (var1) {
            this.tellUsMoreButton.setOnClick((kotlin.jvm.functions.Function0<kotlin.Unit>) () -> {
               this.showRemarkDialog(this.rootLayout.getContext());
               return null;
            });
         } else {
            this.tellUsMoreButton.setOnClick((kotlin.jvm.functions.Function0<kotlin.Unit>) () -> null);
         }
      }
   }

   private static boolean isLockedRatingFailure(ErrorObject var0) {
      if (var0.getStatusCode() != 403) {
         return false;
      }

      String var1 = ErrorStringExtractorKt.extractErrorCode(var0);
      return "rating_window_closed".equals(var1) || "rating_locked".equals(var1);
   }

   private void setupRatingsLayout() {
      LinearLayout var1 = (LinearLayout)this.rootLayout.findViewById(id.intercom_rating_options_layout);
      Context var2 = this.rootLayout.getContext();
      int var3 = ScreenUtils.dpToPx(14.0F, var2);
      int var4 = ScreenUtils.dpToPx(17.0F, var2);

      for (int var5 = 0; var5 < this.conversationRating.getOptions().size(); var5++) {
         ConversationRatingOption var6 = (ConversationRatingOption)this.conversationRating.getOptions().get(var5);
         FrameLayout var7 = new FrameLayout(var2);
         var7.setClipChildren(false);
         var7.setClipToPadding(false);
         var7.setLayoutParams(new LayoutParams(0, -1, 1.0F));
         ImageView var8 = new ImageView(var2);
         int var9 = var2.getResources().getDimensionPixelSize(dimen.intercom_conversation_rating_size);
         android.widget.FrameLayout.LayoutParams var10 = new android.widget.FrameLayout.LayoutParams(var9, var9, 17);
         var10.setMargins(var4, var3, var4, var3);
         var8.setLayoutParams(var10);
         var8.setLongClickable(false);
         this.loadEmojiWithFallback(var2, var6.getUnicode(), var8);
         var8.setOnClickListener(this.ratingClickListener);
         var7.addView(var8);
         var1.addView(var7);
         this.ratingViews.add(var8);
      }
   }

   private void updateViewVisibility() {
      if (this.rootLayout != null) {
         boolean var1 = this.conversationRating.getRatingIndex() != -1;
         boolean var2 = !TextUtils.isEmpty(this.conversationRating.getRemark());
         boolean var3 = this.conversationRating.isBlockRating();
         boolean var4 = var3 && !var2;
         boolean var5 = var2;
         TextView var6 = (TextView)this.rootLayout.findViewById(id.rate_your_conversation_text_view);
         var6.setVisibility(!var5 && !var3 ? 0 : 8);
         TextView var7 = (TextView)this.rootLayout.findViewById(id.intercom_rating_expired_text_view);
         var7.setVisibility(var4 ? 0 : 8);
         LinearLayout var8 = (LinearLayout)this.rootLayout.findViewById(id.intercom_rating_options_layout);
         var8.setVisibility(!var5 && (!var3 || var1) ? 0 : 8);
         this.tellUsMoreButton.setVisibility(var1 && !var2 && !var3 ? 0 : 8);
         LinearLayout var9 = (LinearLayout)this.rootLayout.findViewById(id.intercom_you_rated_layout);
         var9.setVisibility(var5 ? 0 : 8);
         ConversationRatingOption var10 = this.findSelectedOption();
         if (var10 != null) {
            ImageView var11 = (ImageView)this.rootLayout.findViewById(id.intercom_you_rated_image_view);
            this.loadEmojiWithFallback(var11.getContext(), var10.getUnicode(), var11);
         }
      }
   }

   @Nullable
   private ConversationRatingOption findSelectedOption() {
      for (java.lang.Object recoveredEntryElement_0_174 : this.conversationRating.getOptions()) {
         ConversationRatingOption var2 = (ConversationRatingOption) recoveredEntryElement_0_174;
         if (var2.getIndex() == this.conversationRating.getRatingIndex()) {
            return var2;
         }
      }

      return null;
   }

   @VisibleForTesting
   void rateConversation(ConversationRating var1, ConversationRatingOption var2) {
      if (!var1.isBlockRating() && !this.submissionInProgress && !var1.getRatingIndex().equals(var2.getIndex())) {
         this.submissionInProgress = true;
         this.updateInteractionEnabled();
         this.api.rateConversation(this.conversationId, var2.getIndex(), new ConversationRatingCard$2(this, var1, var2));
      }
   }

   @VisibleForTesting
   void addRemarkToConversation(ConversationRating var1, String var2) {
      this.addRemarkToConversation(var1, var2, null);
   }

   private boolean addRemarkToConversation(ConversationRating var1, String var2, @Nullable ConversationRatingCard$RemarkSubmissionListener var3) {
      if (!var1.isBlockRating() && TextUtils.isEmpty(var1.getRemark()) && !this.submissionInProgress) {
         this.submissionInProgress = true;
         this.updateInteractionEnabled();
         this.api.addConversationRatingRemark(this.conversationId, var2, new ConversationRatingCard$3(this, var1, var2, var3));
         return true;
      } else {
         return false;
      }
   }

   private void updateSelectedRating() {
      if (this.conversationRating.getOptions().size() == this.ratingViews.size()) {
         int var1 = this.conversationRating.getRatingIndex();

         for (int var2 = 0; var2 < this.conversationRating.getOptions().size(); var2++) {
            ConversationRatingOption var3 = (ConversationRatingOption)this.conversationRating.getOptions().get(var2);
            ImageView var4 = (ImageView)this.ratingViews.get(var2);
            if (var1 == -1) {
               this.selectView(1.0F, var4);
            } else if (var1 == var3.getIndex()) {
               this.selectView(1.2F, var4);
            } else {
               this.deselectView(var4);
            }
         }
      }
   }

   private void showRemarkDialog(Context var1) {
      Builder var2 = new Builder(var1);
      var2.setTitle(string.intercom_tell_us_more);
      LinearLayout var3 = new LinearLayout(var1);
      var3.setOrientation(1);
      LayoutParams var4 = new LayoutParams(-1, -2);
      int var5 = ScreenUtils.dpToPx(16.0F, var1);
      var4.setMargins(var5, 0, var5, 0);
      EditText var6 = new EditText(var1);
      if (var6.getBackground() != null) {
         var6.getBackground().mutate().setColorFilter(((AppConfig)this.appConfigProvider.get()).getPrimaryColor(), Mode.SRC_ATOP);
      }

      var6.setLayoutParams(var4);
      var3.addView(var6);
      TextView var7 = new TextView(var1);
      var7.setLayoutParams(var4);
      var7.setVisibility(8);
      var3.addView(var7);
      var2.setView(var3);
      var2.setPositiveButton(17039370, null);
      var2.setNegativeButton(17039360, new ConversationRatingCard$4(this));
      AlertDialog var8 = var2.create();
      var8.show();
      var6.setOnFocusChangeListener(new ConversationRatingCard$5(this, var8));
      android.widget.Button var9 = var8.getButton(-1);
      var9.setTextColor(((AppConfig)this.appConfigProvider.get()).getPrimaryColor());
      var9.setOnClickListener(new ConversationRatingCard$6(this, var7, var9, var6, var8));
      android.widget.Button var10 = var8.getButton(-2);
      var10.setTextColor(((AppConfig)this.appConfigProvider.get()).getPrimaryColor());
   }

   private static void showRemarkFailureFeedback(TextView var0, android.widget.Button var1, boolean var2) {
      var0.setText(var2 ? string.intercom_rating_window_closed : string.intercom_something_went_wrong_try_again);
      var0.setVisibility(0);
      if (!var2) {
         var1.setEnabled(true);
      }
   }

   private void selectView(float var1, ImageView var2) {
      var2.animate().setInterpolator(new OvershootInterpolator(2.0F)).scaleX(var1).scaleY(var1).setDuration(200L).start();
      var2.clearColorFilter();
   }

   private void deselectView(ImageView var1) {
      var1.animate().setInterpolator(new OvershootInterpolator(0.6F)).scaleX(1.0F).scaleY(1.0F).setDuration(200L).start();
      var1.setColorFilter(this.deselectedFilter);
   }

   private static String imageUrlForUnicode(String var0) {
      return "https://js.intercomcdn.com/images/stickers/" + var0 + ".png";
   }

   @DrawableRes
   @VisibleForTesting
   static int getEmojiDrawableForUnicode(String var0) {
      if (var0 == null) {
         return 0;
      }

      switch (var0) {
         case "1f620":
            return drawable.intercom_reaction_angry;
         case "1f615":
            return drawable.intercom_reaction_sad;
         case "1f610":
            return drawable.intercom_reaction_neutral;
         case "1f603":
            return drawable.intercom_reaction_happy;
         case "1f929":
            return drawable.intercom_reaction_star_struck;
         default:
            return 0;
      }
   }

   private void loadEmojiWithFallback(Context var1, String var2, ImageView var3) {
      String var4 = imageUrlForUnicode(var2);
      ImageRequest var5 = new coil.request.ImageRequest.Builder(var1).data(var4).target(var3).listener(new ConversationRatingCard$7(this, var2, var3)).build();
      IntercomCoilKt.loadIntercomImage(var1, var5);
   }

   // $VF: synthetic method
   static List access$000(ConversationRatingCard var0) {
      return var0.ratingViews;
   }

   // $VF: synthetic method
   static ConversationRating access$100(ConversationRatingCard var0) {
      return var0.conversationRating;
   }

   // $VF: synthetic method
   static boolean access$202(ConversationRatingCard var0, boolean var1) {
      return var0.submissionInProgress = var1;
   }

   // $VF: synthetic method
   static void access$300(ConversationRatingCard var0) {
      var0.updateInteractionEnabled();
   }

   // $VF: synthetic method
   static void access$400(ConversationRatingCard var0) {
      var0.updateViewVisibility();
   }

   // $VF: synthetic method
   static void access$500(ConversationRatingCard var0) {
      var0.updateSelectedRating();
   }

   // $VF: synthetic method
   static boolean access$600(ErrorObject var0) {
      return isLockedRatingFailure(var0);
   }

   // $VF: synthetic method
   static LinearLayout access$700(ConversationRatingCard var0) {
      return var0.rootLayout;
   }

   // $VF: synthetic method
   static void access$800(TextView var0, android.widget.Button var1, boolean var2) {
      showRemarkFailureFeedback(var0, var1, var2);
   }

   // $VF: synthetic method
   static boolean access$900(ConversationRatingCard var0, ConversationRating var1, String var2, ConversationRatingCard$RemarkSubmissionListener var3) {
      return var0.addRemarkToConversation(var1, var2, var3);
   }
}
