package io.intercom.android.sdk.identity;

import androidx.datastore.preferences.core.MutablePreferences;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;

@DebugMetadata(
   f = "AppConfigStore.kt",
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.identity.AppConfigStore$setAppConfig$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\n",
   d2 = {"<anonymous>", "", "prefs", "Landroidx/datastore/preferences/core/MutablePreferences;"}
)
final class AppConfigStore$setAppConfig$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   Object L$0;
   // $VF: synthetic field
   final AppConfig $config;

   AppConfigStore$setAppConfig$1$1(AppConfig var1, Continuation var2) {
      super(2, var2);
      this.$config = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            MutablePreferences var2 = (MutablePreferences)this.L$0;
            AppConfig var3 = this.$config;
            int var4 = 0;
            var2.set(AppConfigStore.access$getKEY_APP_NAME$cp(), var3.getName());
            var2.set(AppConfigStore.access$getKEY_APP_LOCALE$cp(), var3.getLocale());
            var2.set(AppConfigStore.access$getKEY_HELP_CENTER_LOCALE$cp(), var3.getHelpCenterLocale());
            var2.set(AppConfigStore.access$getKEY_LAUNCHER_LOGO_URL$cp(), var3.getLauncherLogoUrl());
            var2.set(AppConfigStore.access$getKEY_TEAM_GREETING$cp(), var3.getTeamGreeting());
            var2.set(AppConfigStore.access$getKEY_PRIMARY_COLOR$cp(), Boxing.boxInt(var3.getPrimaryColor()));
            var2.set(AppConfigStore.access$getKEY_SECONDARY_COLOR$cp(), Boxing.boxInt(var3.getSecondaryColor()));
            var2.set(AppConfigStore.access$getKEY_RATE_LIMIT_COUNT$cp(), Boxing.boxInt(var3.getRateLimitCount()));
            var2.set(AppConfigStore.access$getKEY_USER_UPDATE_CACHE_MAX_AGE_MS$cp(), Boxing.boxLong(var3.getUserUpdateCacheMaxAgeMs()));
            var2.set(AppConfigStore.access$getKEY_RATE_LIMIT_PERIOD_MS$cp(), Boxing.boxLong(var3.getRateLimitPeriodMs()));
            var2.set(AppConfigStore.access$getKEY_NEW_SESSION_THRESHOLD_MS$cp(), Boxing.boxLong(var3.getNewSessionThresholdMs()));
            var2.set(AppConfigStore.access$getKEY_SOFT_RESET_TIMEOUT_MS$cp(), Boxing.boxLong(var3.getSoftResetTimeoutMs()));
            var2.set(AppConfigStore.access$getKEY_INBOUND_MESSAGES$cp(), Boxing.boxBoolean(var3.isInboundMessages()));
            var2.set(AppConfigStore.access$getKEY_AUDIO_ENABLED$cp(), Boxing.boxBoolean(var3.isAudioEnabled()));
            var2.set(AppConfigStore.access$getKEY_METRICS_ENABLED$cp(), Boxing.boxBoolean(var3.isMetricsEnabled()));
            var2.set(AppConfigStore.access$getKEY_RECEIVED_FROM_SERVER$cp(), Boxing.boxBoolean(var3.isReceivedFromServer()));
            var2.set(AppConfigStore.access$getKEY_BACKGROUND_REQUESTS_ENABLED$cp(), Boxing.boxBoolean(var3.isBackgroundRequestsEnabled()));
            var2.set(AppConfigStore.access$getKEY_PRIMARY_COLOR_RENDER_DARK_TEXT$cp(), Boxing.boxBoolean(var3.isPrimaryColorRenderDarkText()));
            var2.set(AppConfigStore.access$getKEY_SECONDARY_COLOR_RENDER_DARK_TEXT$cp(), Boxing.boxBoolean(var3.isSecondaryColorRenderDarkText()));
            var2.set(AppConfigStore.access$getKEY_HELP_CENTER_URL$cp(), var3.getHelpCenterUrl());
            var2.set(AppConfigStore.access$getKEY_HELP_CENTER_URLS$cp(), var3.getHelpCenterUrls());
            var2.set(AppConfigStore.access$getKEY_FEATURES$cp(), var3.getFeatures());
            var2.set(AppConfigStore.access$getKEY_ACCESS_TEAMMATE_ENABLED$cp(), Boxing.boxBoolean(var3.isAccessToTeammateEnabled()));
            var2.set(AppConfigStore.access$getKEY_HELP_CENTER_REQUIRE_SEARCH$cp(), Boxing.boxBoolean(var3.isHelpCenterRequireSearchEnabled()));
            var2.set(
               AppConfigStore.access$getKEY_PREVENT_MULTIPLE_INBOUND_CONVERSATIONS$cp(), Boxing.boxBoolean(var3.isPreventMultipleInboundConversationsEnabled())
            );
            var2.set(AppConfigStore.access$getKEY_HAS_OPEN_CONVERSATIONS$cp(), Boxing.boxBoolean(var3.getHasOpenConversations()));
            var2.set(AppConfigStore.access$getKEY_HUB_CONFIG$cp(), AppConfigStore.access$getGSON$cp().toJson(var3.getConfigModules()));
            var2.set(AppConfigStore.access$getKEY_ATTACHMENT_SETTINGS$cp(), AppConfigStore.access$getGSON$cp().toJson(var3.getAttachmentSettings()));
            var2.set(AppConfigStore.access$getKEY_ARTICLE_AUTO_REACTION_ENABLED$cp(), Boxing.boxBoolean(var3.getArticleAutoReactionEnabled()));
            var2.set(AppConfigStore.access$getKEY_IDENTITY_VERIFICATION_ENABLED$cp(), Boxing.boxBoolean(var3.isIdentityVerificationEnabled()));
            var2.set(AppConfigStore.access$getKEY_CRASH_REPORTING_ENABLED$cp(), Boxing.boxBoolean(var3.isCrashReportingEnabled()));
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      AppConfigStore$setAppConfig$1$1 var3 = new AppConfigStore$setAppConfig$1$1(this.$config, var2);
      var3.L$0 = var1;
      return var3;
   }

   public final Object invoke(MutablePreferences var1, Continuation var2) {
      return ((AppConfigStore$setAppConfig$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((MutablePreferences)var1, (Continuation)var2);
   }
}
