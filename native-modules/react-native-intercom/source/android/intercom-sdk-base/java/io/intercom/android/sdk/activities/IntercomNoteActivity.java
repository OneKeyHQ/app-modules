package io.intercom.android.sdk.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.core.graphics.Insets;
import androidx.core.os.BundleCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsCompat.Type;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.color;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.blocks.LightboxOpeningImageClickListener;
import io.intercom.android.sdk.blocks.LinkOpeningButtonClickListener;
import io.intercom.android.sdk.blocks.UploadingImageCache;
import io.intercom.android.sdk.blocks.ViewHolderGenerator;
import io.intercom.android.sdk.blocks.lib.BlocksViewHolder;
import io.intercom.android.sdk.conversation.ReactionInputView;
import io.intercom.android.sdk.conversation.ReactionListener;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.ConversationScreenOpenerKt;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.metrics.MetricTracker$ReactionLocation;
import io.intercom.android.sdk.models.LastParticipatingAdmin;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.ReactionReply;
import io.intercom.android.sdk.utilities.BlockUtils;
import io.intercom.android.sdk.utilities.Phrase;
import io.intercom.android.sdk.utilities.TimeFormatter;
import io.intercom.android.sdk.views.ActiveStatePresenter;
import io.intercom.android.sdk.views.ContentAwareScrollView;
import io.intercom.android.sdk.views.IntercomToolbar;

public class IntercomNoteActivity extends IntercomBaseActivity implements OnClickListener {
   private static final String PARCEL_PART = "parcel_part";
   private static final String PARCEL_CONVERSATION_ID = "parcel_conversation_id";
   private static final String LAST_PARTICIPANT = "last_participant";
   private static final String COMPOSER_IS_VISIBLE = "composer_is_visible";
   private final FullScreenInAppPresenter presenter = new FullScreenInAppPresenter();
   Part part;
   private LastParticipatingAdmin lastParticipatingAdmin;
   private boolean composerIsVisible = true;
   String conversationId;
   View composerLayout;
   private LinearLayout noteLayout;
   ReactionInputView reactionComposer;
   MetricTracker metricTracker;

   @Override
   protected void onCreate(Bundle var1) {
      EdgeToEdge.enable(this, SystemBarStyle.dark(0), SystemBarStyle.dark(0));
      super.onCreate(var1);
      this.getOnBackPressedDispatcher().addCallback(new IntercomNoteActivity$1(this, true));
      this.setContentView(layout.intercom_activity_note);
      ViewCompat.setOnApplyWindowInsetsListener(this.findViewById(id.note_root), (var0, var1x) -> {
         Insets var2 = var1x.getInsets(Type.systemBars() | Type.displayCutout());
         Insets var3 = var1x.getInsets(Type.ime());
         var0.setPadding(var2.left, var2.top, var2.right, Math.max(var2.bottom, var3.bottom));
         return WindowInsetsCompat.CONSUMED;
      });
      this.noteLayout = (LinearLayout)this.findViewById(id.note_layout);
      this.noteLayout.setVisibility(4);
      this.reactionComposer = (ReactionInputView)this.findViewById(id.reaction_input_view);
      IntercomToolbar var2 = (IntercomToolbar)this.findViewById(id.intercom_toolbar);
      var2.setListener(new InAppToolbarListener(this));
      this.part = new Part();
      this.lastParticipatingAdmin = LastParticipatingAdmin.NULL;
      Injector var3 = Injector.get();
      Api var4 = var3.getApi();
      this.metricTracker = var3.getMetricTracker();
      Provider var5 = var3.getAppConfigProvider();
      TimeFormatter var6 = new TimeFormatter(this, var3.getTimeProvider());
      Bundle var7 = this.getIntent().getExtras();
      if (var7 != null) {
         var7.setClassLoader(Part.class.getClassLoader());
         if (var7.containsKey("parcel_part")) {
            this.part = (Part)BundleCompat.getParcelable(var7, "parcel_part", Part.class);
            String var8 = "";
            if (this.part != null && this.part.getParticipant() != null) {
               var8 = this.part.getParticipant().getForename();
            }

            String var9 = ((AppConfig)var5.get()).getName();
            CharSequence var10 = Phrase.from(this, string.intercom_teammate_from_company).put("name", var8).put("company", var9).format();
            var2.setTitle(var10);
         }

         if (var7.containsKey("parcel_conversation_id")) {
            this.conversationId = var7.getString("parcel_conversation_id");
            var3.getDataLayer().markConversationAsRead(this.conversationId);
            var4.markConversationAsRead(this.conversationId);
         }

         if (var7.containsKey("last_participant")) {
            this.lastParticipatingAdmin = (LastParticipatingAdmin)BundleCompat.getParcelable(var7, "last_participant", LastParticipatingAdmin.class);
            if (!LastParticipatingAdmin.isNull(this.lastParticipatingAdmin)) {
               var2.setSubtitle(var6.getAdminActiveStatus(this.lastParticipatingAdmin, var5));
            }
         }

         if (var7.containsKey("composer_is_visible")) {
            this.composerIsVisible = var7.getBoolean("composer_is_visible", true);
         }
      }

      var2.setUpNoteToolbar(this.part.getParticipant(), this.lastParticipatingAdmin.isActive(), new ActiveStatePresenter(), (AppConfig)var5.get());
      ViewHolderGenerator var13 = new ViewHolderGenerator(
         new UploadingImageCache(),
         var4,
         var5,
         this.conversationId,
         new LightboxOpeningImageClickListener(var4),
         new LinkOpeningButtonClickListener(var4),
         var3.getGson(),
         this.metricTracker
      );
      BlocksViewHolder var14 = var13.getNoteHolder();
      this.composerLayout = this.findViewById(id.note_composer_container);
      ContentAwareScrollView var15 = (ContentAwareScrollView)this.findViewById(id.note_view);
      LinearLayout var11 = ViewHolderGenerator.createPartsLayout(var14, this.part, this);
      var15.addView(BlockUtils.getBlockView(var15, var11, this));
      if (!ReactionReply.isNull(this.part.getReactionReply())) {
         ReactionListener var12 = new ConversationReactionListener(
            MetricTracker$ReactionLocation.IN_APP, this.part.getId(), this.conversationId, Injector.get().getApi(), this.metricTracker
         );
         var15.setListener(new IntercomNoteActivity$2(this, var15, var12));
         this.reactionComposer.preloadReactionImages(this.part.getReactionReply());
      }

      View var16 = this.getWindow().getDecorView();
      var16.setAlpha(0.0F);
      var16.animate().alpha(1.0F).setDuration(200L).setListener(new IntercomNoteActivity$3(this)).start();
   }

   private void applyAccessibilityStyling(EditText var1) {
      int var2 = this.getResources().getColor(color.intercom_accessibility_grey);
      var1.setTextColor(var2);
      var1.setHintTextColor(var2);
   }

   void animateContent() {
      this.noteLayout.setScaleX(0.9F);
      this.noteLayout.setScaleY(0.9F);
      this.noteLayout.setAlpha(0.0F);
      this.noteLayout.setVisibility(0);
      this.noteLayout.animate().scaleX(1.0F).scaleY(1.0F).alpha(1.0F).setDuration(200L).start();
   }

   public static Intent buildNoteIntent(Context var0, Part var1, String var2, LastParticipatingAdmin var3, boolean var4) {
      Intent var5 = new Intent(var0, IntercomNoteActivity.class)
         .putExtra("parcel_part", var1)
         .putExtra("last_participant", var3)
         .putExtra("composer_is_visible", var4);
      if (!TextUtils.isEmpty(var2)) {
         var5.putExtra("parcel_conversation_id", var2);
      }

      var5.setExtrasClassLoader(Part.class.getClassLoader());
      return var5;
   }

   @Override
   public void onClick(View var1) {
      if (var1.getId() == id.note_touch_target) {
         this.presenter.closeWindow(this);
         this.metricTracker.closedInAppFromFull(this.conversationId, this.part.getId());
         this.startActivity(ConversationScreenOpenerKt.getConversationIntent(this, this.conversationId));
      }
   }

   // $VF: synthetic method
   static FullScreenInAppPresenter access$000(IntercomNoteActivity var0) {
      return var0.presenter;
   }

   // $VF: synthetic method
   static boolean access$100(IntercomNoteActivity var0) {
      return var0.composerIsVisible;
   }

   // $VF: synthetic method
   static void access$200(IntercomNoteActivity var0, EditText var1) {
      var0.applyAccessibilityStyling(var1);
   }
}
