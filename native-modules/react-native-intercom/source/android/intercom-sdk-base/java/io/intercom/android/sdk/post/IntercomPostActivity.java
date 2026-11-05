package io.intercom.android.sdk.post;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.activity.compose.ComponentActivityKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.core.os.BundleCompat;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.activities.IntercomBaseActivity;
import io.intercom.android.sdk.m5.ConversationScreenOpenerKt;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.models.LastParticipatingAdmin;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.utilities.TimeFormatter;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0001\u0018\u0000 $2\u00020\u0001:\u0001$B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u0014J\b\u0010\u001a\u001a\u00020\u0017H\u0002J\b\u0010\u001b\u001a\u00020\u001cH\u0002J\b\u0010\u001d\u001a\u00020\u001eH\u0002J\b\u0010\u001f\u001a\u00020\u0017H\u0002J\b\u0010 \u001a\u00020!H\u0002J\b\u0010\"\u001a\u00020!H\u0002J\b\u0010#\u001a\u00020\u001eH\u0002R#\u0010\u0004\u001a\n \u0006*\u0004\u0018\u00010\u00050\u00058BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\t\u0010\n\u001a\u0004\b\u0007\u0010\bR?\u0010\u000b\u001a&\u0012\f\u0012\n \u0006*\u0004\u0018\u00010\r0\r \u0006*\u0012\u0012\f\u0012\n \u0006*\u0004\u0018\u00010\r0\r\u0018\u00010\f0\f8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0010\u0010\n\u001a\u0004\b\u000e\u0010\u000fR\u001b\u0010\u0011\u001a\u00020\u00128BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0015\u0010\n\u001a\u0004\b\u0013\u0010\u0014¨\u0006%",
   d2 = {
         "Lio/intercom/android/sdk/post/IntercomPostActivity;",
         "Lio/intercom/android/sdk/activities/IntercomBaseActivity;",
         "<init>",
         "()V",
         "injector",
         "Lio/intercom/android/sdk/Injector;",
         "kotlin.jvm.PlatformType",
         "getInjector",
         "()Lio/intercom/android/sdk/Injector;",
         "injector$delegate",
         "Lkotlin/Lazy;",
         "appConfigProvider",
         "Lio/intercom/android/sdk/Provider;",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "getAppConfigProvider",
         "()Lio/intercom/android/sdk/Provider;",
         "appConfigProvider$delegate",
         "timeFormatter",
         "Lio/intercom/android/sdk/utilities/TimeFormatter;",
         "getTimeFormatter",
         "()Lio/intercom/android/sdk/utilities/TimeFormatter;",
         "timeFormatter$delegate",
         "onCreate",
         "",
         "savedInstanceState",
         "Landroid/os/Bundle;",
         "openConversation",
         "getPart",
         "Lio/intercom/android/sdk/models/Part;",
         "getUserStatus",
         "",
         "sendPostAsRead",
         "isComposerVisible",
         "",
         "isPreview",
         "getConversationId",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class IntercomPostActivity extends IntercomBaseActivity {
   @NotNull
   public static final IntercomPostActivity$Companion Companion = new IntercomPostActivity$Companion(null);
   @NotNull
   private final Lazy injector$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> IntercomPostActivity.injector_delegate$lambda$0()));
   @NotNull
   private final Lazy appConfigProvider$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.post.IntercomPostActivity.appConfigProvider_delegate$lambda$1(this)));
   @NotNull
   private final Lazy timeFormatter$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.post.IntercomPostActivity.timeFormatter_delegate$lambda$2(this)));
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final String PARCEL_PART = "parcel_part";
   @NotNull
   private static final String PARCEL_CONVERSATION_ID = "parcel_conversation_id";
   @NotNull
   private static final String POST_PREVIEW = "is_post_preview";
   @NotNull
   private static final String LAST_PARTICIPANT = "last_participant";
   @NotNull
   private static final String COMPOSER_IS_VISIBLE = "composer_is_visible";

   private final Injector getInjector() {
      Lazy var1 = this.injector$delegate;
      return (Injector)var1.getValue();
   }

   private final Provider getAppConfigProvider() {
      Lazy var1 = this.appConfigProvider$delegate;
      return (Provider)var1.getValue();
   }

   private final TimeFormatter getTimeFormatter() {
      Lazy var1 = this.timeFormatter$delegate;
      return (TimeFormatter)var1.getValue();
   }

   @Override
   protected void onCreate(@Nullable Bundle var1) {
      EdgeToEdge.enable(this, SystemBarStyle.Companion.dark(0), SystemBarStyle.Companion.dark(0));
      super.onCreate(var1);
      if (!this.isPreview()) {
         Part var2 = this.getPart();
         this.getInjector().getMetricTracker().viewedInAppFromMessenger(this.getConversationId(), var2.getId(), var2.getMessageStyle());
      }

      this.getOnBackPressedDispatcher().addCallback(this, new IntercomPostActivity$onCreate$1(this));
      ComponentActivityKt.setContent$default(
         this, null, ComposableLambdaKt.composableLambdaInstance(309457934, true, new IntercomPostActivity$onCreate$2(this)), 1, null
      );
   }

   private final void openConversation() {
      Bundle var10000 = this.getIntent().getExtras();
      if (var10000 == null) {
         var10000 = new Bundle();
      }

      Bundle var1 = var10000;
      String var2 = var1.getString("parcel_conversation_id", "");
      this.getInjector().getMetricTracker().openedConversationFromFull(var2, this.getPart().getId());
      this.startActivity(ConversationScreenOpenerKt.getConversationIntent(this, var2));
      this.finish();
   }

   private final Part getPart() {
      Bundle var1 = this.getIntent().getExtras();
      if (var1 != null) {
         var1.setClassLoader(Part.class.getClassLoader());
         if (var1.containsKey("parcel_part")) {
            Part var2 = (Part)BundleCompat.getParcelable(var1, "parcel_part", Part.class);
            if (var2 == null) {
               var2 = Part.NULL;
               Intrinsics.checkNotNullExpressionValue(Part.NULL, "NULL");
            }

            return var2;
         }
      }

      Part var10000 = Part.NULL;
      Intrinsics.checkNotNullExpressionValue(Part.NULL, "NULL");
      return var10000;
   }

   private final String getUserStatus() {
      Bundle var1 = this.getIntent().getExtras();
      if (var1 != null && var1.containsKey("last_participant")) {
         LastParticipatingAdmin var2 = (LastParticipatingAdmin)BundleCompat.getParcelable(var1, "last_participant", LastParticipatingAdmin.class);
         if (var2 != null && !LastParticipatingAdmin.isNull(var2)) {
            return this.getTimeFormatter().getAdminActiveStatus(var2, this.getAppConfigProvider()).toString();
         }
      }

      return "";
   }

   private final void sendPostAsRead() {
      Bundle var1 = this.getIntent().getExtras();
      if (var1 != null && var1.containsKey("parcel_conversation_id")) {
         String var2 = var1.getString("parcel_conversation_id", "");
         IntercomDataLayer var10000 = this.getInjector().getDataLayer();
         Intrinsics.checkNotNull(var2);
         var10000.markConversationAsRead(var2);
         this.getInjector().getApi().markConversationAsRead(var2);
      }
   }

   private final boolean isComposerVisible() {
      Bundle var1 = this.getIntent().getExtras();
      return var1 != null ? var1.getBoolean("composer_is_visible", true) : true;
   }

   private final boolean isPreview() {
      Bundle var10000 = this.getIntent().getExtras();
      if (var10000 == null) {
         var10000 = new Bundle();
      }

      Bundle var1 = var10000;
      return var1.getBoolean("is_post_preview", false);
   }

   private final String getConversationId() {
      Bundle var10000 = this.getIntent().getExtras();
      if (var10000 == null) {
         var10000 = new Bundle();
      }

      Bundle var1 = var10000;
      String var2 = var1.getString("parcel_conversation_id", "");
      Intrinsics.checkNotNullExpressionValue(var2, "getString(...)");
      return var2;
   }

   private static final Injector injector_delegate$lambda$0() {
      return Injector.get();
   }

   private static final Provider appConfigProvider_delegate$lambda$1(IntercomPostActivity var0) {
      return var0.getInjector().getAppConfigProvider();
   }

   private static final TimeFormatter timeFormatter_delegate$lambda$2(IntercomPostActivity var0) {
      return new TimeFormatter(var0, var0.getInjector().getTimeProvider());
   }

   @JvmStatic
   @NotNull
   public static final Intent buildPostIntent(
      @NotNull Context var0, @NotNull Part var1, @NotNull String var2, @NotNull LastParticipatingAdmin var3, boolean var4, boolean var5
   ) {
      return Companion.buildPostIntent(var0, var1, var2, var3, var4, var5);
   }

   // $VF: synthetic method
   public static final boolean access$isPreview(IntercomPostActivity var0) {
      return var0.isPreview();
   }

   // $VF: synthetic method
   public static final Injector access$getInjector(IntercomPostActivity var0) {
      return var0.getInjector();
   }

   // $VF: synthetic method
   public static final String access$getConversationId(IntercomPostActivity var0) {
      return var0.getConversationId();
   }

   // $VF: synthetic method
   public static final Part access$getPart(IntercomPostActivity var0) {
      return var0.getPart();
   }

   // $VF: synthetic method
   public static final Provider access$getAppConfigProvider(IntercomPostActivity var0) {
      return var0.getAppConfigProvider();
   }

   // $VF: synthetic method
   public static final String access$getUserStatus(IntercomPostActivity var0) {
      return var0.getUserStatus();
   }

   // $VF: synthetic method
   public static final boolean access$isComposerVisible(IntercomPostActivity var0) {
      return var0.isComposerVisible();
   }

   // $VF: synthetic method
   public static final void access$openConversation(IntercomPostActivity var0) {
      var0.openConversation();
   }

   // $VF: synthetic method
   public static final void access$sendPostAsRead(IntercomPostActivity var0) {
      var0.sendPostAsRead();
   }
}
