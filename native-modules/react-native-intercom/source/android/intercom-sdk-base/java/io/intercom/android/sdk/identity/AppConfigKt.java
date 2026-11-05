package io.intercom.android.sdk.identity;

import androidx.annotation.ColorInt;
import io.intercom.android.nexus.NexusConfig;
import io.intercom.android.sdk.models.AttachmentSettings;
import io.intercom.android.sdk.models.AttachmentSettingsKt;
import io.intercom.android.sdk.models.Config;
import io.intercom.android.sdk.models.ConfigModules;
import io.intercom.android.sdk.models.CrashReportingConfig;
import io.intercom.android.sdk.utilities.ColorUtils;
import java.util.Set;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001a\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\u001a\u0016\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\b\b\u0001\u0010\u0003\u001a\u00020\u0004H\u0000\u001a\u001a\u0010\u0005\u001a\u00020\u00042\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0003\u001a\u00020\u0004H\u0002¨\u0006\b",
   d2 = {
         "getAppConfig",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "Lio/intercom/android/sdk/models/Config;",
         "defaultColor",
         "",
         "getConfigColor",
         "color",
         "",
         "intercom-sdk-base_release"
   }
)
public final class AppConfigKt {
   @NotNull
   public static final AppConfig getAppConfig(@NotNull Config var0, @ColorInt int var1) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      int var2 = getConfigColor(var0.getPrimaryColor(), var1);
      int var3 = getConfigColor(var0.getSecondaryColor(), var1);
      String var10002 = var0.getName();
      int var10005 = ColorUtils.darkenColor(var3);
      boolean var10006 = var0.isPrimaryColorRenderDarkText();
      boolean var10007 = var0.isSecondaryColorRenderDarkText();
      boolean var10008 = var0.isInboundMessages();
      int var10009 = var0.getRateLimitCount();
      long var10010 = var0.getRateLimitPeriod();
      long var10011 = var0.getUserUpdateCacheMaxAge();
      long var10012 = var0.getNewSessionThreshold();
      long var10013 = var0.getSoftResetTimeout();
      boolean var10014 = var0.isMetricsEnabled();
      boolean var10015 = var0.isAudioEnabled();
      String var10016 = var0.getLocale();
      String var10017 = var0.getHelpCenterLocale();
      boolean var10019 = var0.isBackgroundRequestsEnabled();
      String var10020 = var0.getHelpCenterUrl();
      Set var10021 = var0.getHelpCenterUrls();
      Set var10022 = var0.getFeatures();
      String var10023 = var0.getLauncherLogoUrl();
      String var10024 = var0.getTeamGreeting();
      boolean var10025 = var0.getIdentityVerificationEnabled();
      boolean var10026 = var0.getAccessToTeammateEnabled();
      boolean var10027 = var0.getHelpCenterRequireSearch();
      boolean var10028 = var0.getPreventMultipleInboundConversationsEnabled();
      boolean var10029 = var0.getHasOpenConversations();
      ConfigModules var10030 = var0.getModules();
      NexusConfig var10031 = var0.getRealTimeConfig();
      AttachmentSettings var10032 = AttachmentSettingsKt.sanitized(var0.getAttachmentSettings());
      CrashReportingConfig var10035 = var0.getCrashReportingConfig();
      return new AppConfig(
         var10002,
         var2,
         var3,
         var10005,
         var10006,
         var10007,
         var10008,
         var10009,
         var10010,
         var10011,
         var10012,
         var10013,
         var10014,
         var10015,
         var10016,
         var10017,
         true,
         var10019,
         var10020,
         var10021,
         var10022,
         var10023,
         var10024,
         var10025,
         var10026,
         var10027,
         var10028,
         var10029,
         var10030,
         var10031,
         var10032,
         var0.isArticleAutoReactionEnabled(),
         var0.getConversationStateSyncSettings(),
         var10035 != null ? var10035.getEnabled() : true
      );
   }

   private static final int getConfigColor(String var0, int var1) {
      return var0 == null ? var1 : ColorUtils.parseColor(var0);
   }
}
