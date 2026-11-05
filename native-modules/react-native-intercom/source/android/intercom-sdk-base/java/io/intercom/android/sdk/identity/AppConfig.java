package io.intercom.android.sdk.identity;

import androidx.annotation.ColorInt;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.nexus.NexusConfig;
import io.intercom.android.sdk.models.AttachmentSettings;
import io.intercom.android.sdk.models.ConfigModules;
import io.intercom.android.sdk.models.ConversationStateSyncSettings;
import io.intercom.android.sdk.models.HomeConfig;
import io.intercom.android.sdk.models.OpenConfig;
import io.intercom.android.sdk.models.Space;
import io.intercom.android.sdk.models.Space$Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u000b\n\u0002\u0010\"\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b)\n\u0002\u0018\u0002\n\u0002\b)\b\u0081\b\u0018\u00002\u00020\u0001B«\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0005\u0012\b\b\u0001\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\t\u0012\u0006\u0010\u000b\u001a\u00020\t\u0012\u0006\u0010\f\u001a\u00020\u0005\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u000e\u0012\u0006\u0010\u0010\u001a\u00020\u000e\u0012\u0006\u0010\u0011\u001a\u00020\u000e\u0012\u0006\u0010\u0012\u001a\u00020\t\u0012\u0006\u0010\u0013\u001a\u00020\t\u0012\u0006\u0010\u0014\u001a\u00020\u0003\u0012\u0006\u0010\u0015\u001a\u00020\u0003\u0012\u0006\u0010\u0016\u001a\u00020\t\u0012\u0006\u0010\u0017\u001a\u00020\t\u0012\u0006\u0010\u0018\u001a\u00020\u0003\u0012\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00030\u001a\u0012\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00030\u001a\u0012\u0006\u0010\u001c\u001a\u00020\u0003\u0012\u0006\u0010\u001d\u001a\u00020\u0003\u0012\u0006\u0010\u001e\u001a\u00020\t\u0012\u0006\u0010\u001f\u001a\u00020\t\u0012\u0006\u0010 \u001a\u00020\t\u0012\u0006\u0010!\u001a\u00020\t\u0012\u0006\u0010\"\u001a\u00020\t\u0012\b\u0010#\u001a\u0004\u0018\u00010$\u0012\u0006\u0010%\u001a\u00020&\u0012\u0006\u0010'\u001a\u00020(\u0012\u0006\u0010)\u001a\u00020\t\u0012\u0006\u0010*\u001a\u00020+\u0012\u0006\u0010,\u001a\u00020\t¢\u0006\u0004\b-\u0010.J\u000e\u0010P\u001a\u00020\t2\u0006\u0010Q\u001a\u00020\u0003J\u0006\u0010R\u001a\u00020\tJ\u000e\u0010S\u001a\u00020\t2\u0006\u0010T\u001a\u00020UJ\u0010\u0010V\u001a\u0004\u0018\u00010\u00032\u0006\u0010T\u001a\u00020UJ\t\u0010W\u001a\u00020\u0003HÆ\u0003J\t\u0010X\u001a\u00020\u0005HÆ\u0003J\t\u0010Y\u001a\u00020\u0005HÆ\u0003J\t\u0010Z\u001a\u00020\u0005HÆ\u0003J\t\u0010[\u001a\u00020\tHÆ\u0003J\t\u0010\\\u001a\u00020\tHÆ\u0003J\t\u0010]\u001a\u00020\tHÆ\u0003J\t\u0010^\u001a\u00020\u0005HÆ\u0003J\t\u0010_\u001a\u00020\u000eHÆ\u0003J\t\u0010`\u001a\u00020\u000eHÆ\u0003J\t\u0010a\u001a\u00020\u000eHÆ\u0003J\t\u0010b\u001a\u00020\u000eHÆ\u0003J\t\u0010c\u001a\u00020\tHÆ\u0003J\t\u0010d\u001a\u00020\tHÆ\u0003J\t\u0010e\u001a\u00020\u0003HÆ\u0003J\t\u0010f\u001a\u00020\u0003HÆ\u0003J\t\u0010g\u001a\u00020\tHÆ\u0003J\t\u0010h\u001a\u00020\tHÆ\u0003J\t\u0010i\u001a\u00020\u0003HÆ\u0003J\u000f\u0010j\u001a\b\u0012\u0004\u0012\u00020\u00030\u001aHÆ\u0003J\u000f\u0010k\u001a\b\u0012\u0004\u0012\u00020\u00030\u001aHÆ\u0003J\t\u0010l\u001a\u00020\u0003HÆ\u0003J\t\u0010m\u001a\u00020\u0003HÆ\u0003J\t\u0010n\u001a\u00020\tHÆ\u0003J\t\u0010o\u001a\u00020\tHÆ\u0003J\t\u0010p\u001a\u00020\tHÆ\u0003J\t\u0010q\u001a\u00020\tHÆ\u0003J\t\u0010r\u001a\u00020\tHÆ\u0003J\u000b\u0010s\u001a\u0004\u0018\u00010$HÆ\u0003J\t\u0010t\u001a\u00020&HÆ\u0003J\t\u0010u\u001a\u00020(HÆ\u0003J\t\u0010v\u001a\u00020\tHÆ\u0003J\t\u0010w\u001a\u00020+HÆ\u0003J\t\u0010x\u001a\u00020\tHÆ\u0003Jë\u0002\u0010y\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u00052\b\b\u0003\u0010\u0006\u001a\u00020\u00052\b\b\u0003\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\t2\b\b\u0002\u0010\u000b\u001a\u00020\t2\b\b\u0002\u0010\f\u001a\u00020\u00052\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u000e2\b\b\u0002\u0010\u0010\u001a\u00020\u000e2\b\b\u0002\u0010\u0011\u001a\u00020\u000e2\b\b\u0002\u0010\u0012\u001a\u00020\t2\b\b\u0002\u0010\u0013\u001a\u00020\t2\b\b\u0002\u0010\u0014\u001a\u00020\u00032\b\b\u0002\u0010\u0015\u001a\u00020\u00032\b\b\u0002\u0010\u0016\u001a\u00020\t2\b\b\u0002\u0010\u0017\u001a\u00020\t2\b\b\u0002\u0010\u0018\u001a\u00020\u00032\u000e\b\u0002\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00030\u001a2\u000e\b\u0002\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00030\u001a2\b\b\u0002\u0010\u001c\u001a\u00020\u00032\b\b\u0002\u0010\u001d\u001a\u00020\u00032\b\b\u0002\u0010\u001e\u001a\u00020\t2\b\b\u0002\u0010\u001f\u001a\u00020\t2\b\b\u0002\u0010 \u001a\u00020\t2\b\b\u0002\u0010!\u001a\u00020\t2\b\b\u0002\u0010\"\u001a\u00020\t2\n\b\u0002\u0010#\u001a\u0004\u0018\u00010$2\b\b\u0002\u0010%\u001a\u00020&2\b\b\u0002\u0010'\u001a\u00020(2\b\b\u0002\u0010)\u001a\u00020\t2\b\b\u0002\u0010*\u001a\u00020+2\b\b\u0002\u0010,\u001a\u00020\tHÇ\u0001J\u0013\u0010z\u001a\u00020\t2\b\u0010{\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010|\u001a\u00020\u0005H×\u0001J\t\u0010}\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b/\u00100R\u001c\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b1\u00102\u001a\u0004\b3\u00104R\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b5\u00104R\u0011\u0010\u0007\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b6\u00104R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\b\u00107R\u0011\u0010\n\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u00107R\u0011\u0010\u000b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u00107R\u0011\u0010\f\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b8\u00104R\u0011\u0010\r\u001a\u00020\u000e¢\u0006\b\n\u0000\u001a\u0004\b9\u0010:R\u0011\u0010\u000f\u001a\u00020\u000e¢\u0006\b\n\u0000\u001a\u0004\b;\u0010:R\u0011\u0010\u0010\u001a\u00020\u000e¢\u0006\b\n\u0000\u001a\u0004\b<\u0010:R\u0011\u0010\u0011\u001a\u00020\u000e¢\u0006\b\n\u0000\u001a\u0004\b=\u0010:R\u0011\u0010\u0012\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u00107R\u0011\u0010\u0013\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u00107R\u0011\u0010\u0014\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b>\u00100R\u0011\u0010\u0015\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b?\u00100R\u0011\u0010\u0016\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u00107R\u0011\u0010\u0017\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u00107R\u0011\u0010\u0018\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b@\u00100R\u0017\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00030\u001a¢\u0006\b\n\u0000\u001a\u0004\bA\u0010BR\u0017\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00030\u001a¢\u0006\b\n\u0000\u001a\u0004\bC\u0010BR\u0011\u0010\u001c\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\bD\u00100R\u0011\u0010\u001d\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\bE\u00100R\u0011\u0010\u001e\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u00107R\u0011\u0010\u001f\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u00107R\u0011\u0010 \u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b \u00107R\u0011\u0010!\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b!\u00107R\u0011\u0010\"\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\bF\u00107R\u0013\u0010#\u001a\u0004\u0018\u00010$¢\u0006\b\n\u0000\u001a\u0004\bG\u0010HR\u0011\u0010%\u001a\u00020&¢\u0006\b\n\u0000\u001a\u0004\bI\u0010JR\u0011\u0010'\u001a\u00020(¢\u0006\b\n\u0000\u001a\u0004\bK\u0010LR\u0011\u0010)\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\bM\u00107R\u0011\u0010*\u001a\u00020+¢\u0006\b\n\u0000\u001a\u0004\bN\u0010OR\u0011\u0010,\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b,\u00107¨\u0006~",
   d2 = {
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "",
         "name",
         "",
         "primaryColor",
         "",
         "secondaryColor",
         "secondaryColorDark",
         "isPrimaryColorRenderDarkText",
         "",
         "isSecondaryColorRenderDarkText",
         "isInboundMessages",
         "rateLimitCount",
         "rateLimitPeriodMs",
         "",
         "userUpdateCacheMaxAgeMs",
         "newSessionThresholdMs",
         "softResetTimeoutMs",
         "isMetricsEnabled",
         "isAudioEnabled",
         "locale",
         "helpCenterLocale",
         "isReceivedFromServer",
         "isBackgroundRequestsEnabled",
         "helpCenterUrl",
         "helpCenterUrls",
         "",
         "features",
         "launcherLogoUrl",
         "teamGreeting",
         "isIdentityVerificationEnabled",
         "isAccessToTeammateEnabled",
         "isHelpCenterRequireSearchEnabled",
         "isPreventMultipleInboundConversationsEnabled",
         "hasOpenConversations",
         "configModules",
         "Lio/intercom/android/sdk/models/ConfigModules;",
         "realTimeConfig",
         "Lio/intercom/android/nexus/NexusConfig;",
         "attachmentSettings",
         "Lio/intercom/android/sdk/models/AttachmentSettings;",
         "articleAutoReactionEnabled",
         "conversationStateSyncSettings",
         "Lio/intercom/android/sdk/models/ConversationStateSyncSettings;",
         "isCrashReportingEnabled",
         "<init>",
         "(Ljava/lang/String;IIIZZZIJJJJZZLjava/lang/String;Ljava/lang/String;ZZLjava/lang/String;Ljava/util/Set;Ljava/util/Set;Ljava/lang/String;Ljava/lang/String;ZZZZZLio/intercom/android/sdk/models/ConfigModules;Lio/intercom/android/nexus/NexusConfig;Lio/intercom/android/sdk/models/AttachmentSettings;ZLio/intercom/android/sdk/models/ConversationStateSyncSettings;Z)V",
         "getName",
         "()Ljava/lang/String;",
         "getPrimaryColor$annotations",
         "()V",
         "getPrimaryColor",
         "()I",
         "getSecondaryColor",
         "getSecondaryColorDark",
         "()Z",
         "getRateLimitCount",
         "getRateLimitPeriodMs",
         "()J",
         "getUserUpdateCacheMaxAgeMs",
         "getNewSessionThresholdMs",
         "getSoftResetTimeoutMs",
         "getLocale",
         "getHelpCenterLocale",
         "getHelpCenterUrl",
         "getHelpCenterUrls",
         "()Ljava/util/Set;",
         "getFeatures",
         "getLauncherLogoUrl",
         "getTeamGreeting",
         "getHasOpenConversations",
         "getConfigModules",
         "()Lio/intercom/android/sdk/models/ConfigModules;",
         "getRealTimeConfig",
         "()Lio/intercom/android/nexus/NexusConfig;",
         "getAttachmentSettings",
         "()Lio/intercom/android/sdk/models/AttachmentSettings;",
         "getArticleAutoReactionEnabled",
         "getConversationStateSyncSettings",
         "()Lio/intercom/android/sdk/models/ConversationStateSyncSettings;",
         "hasFeature",
         "feature",
         "isBackgroundRequestsDisabled",
         "isSpaceEnabled",
         "space",
         "Lio/intercom/android/sdk/models/Space$Type;",
         "getSpaceLabelIfExists",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "component8",
         "component9",
         "component10",
         "component11",
         "component12",
         "component13",
         "component14",
         "component15",
         "component16",
         "component17",
         "component18",
         "component19",
         "component20",
         "component21",
         "component22",
         "component23",
         "component24",
         "component25",
         "component26",
         "component27",
         "component28",
         "component29",
         "component30",
         "component31",
         "component32",
         "component33",
         "component34",
         "copy",
         "equals",
         "other",
         "hashCode",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nAppConfig.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AppConfig.kt\nio/intercom/android/sdk/identity/AppConfig\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,110:1\n1761#2,3:111\n1#3:114\n*S KotlinDebug\n*F\n+ 1 AppConfig.kt\nio/intercom/android/sdk/identity/AppConfig\n*L\n60#1:111,3\n*E\n"
)
public final class AppConfig {
   @NotNull
   private final String name;
   /** @deprecated */
   private final int primaryColor;
   private final int secondaryColor;
   private final int secondaryColorDark;
   private final boolean isPrimaryColorRenderDarkText;
   private final boolean isSecondaryColorRenderDarkText;
   private final boolean isInboundMessages;
   private final int rateLimitCount;
   private final long rateLimitPeriodMs;
   private final long userUpdateCacheMaxAgeMs;
   private final long newSessionThresholdMs;
   private final long softResetTimeoutMs;
   private final boolean isMetricsEnabled;
   private final boolean isAudioEnabled;
   @NotNull
   private final String locale;
   @NotNull
   private final String helpCenterLocale;
   private final boolean isReceivedFromServer;
   private final boolean isBackgroundRequestsEnabled;
   @NotNull
   private final String helpCenterUrl;
   @NotNull
   private final Set helpCenterUrls;
   @NotNull
   private final Set features;
   @NotNull
   private final String launcherLogoUrl;
   @NotNull
   private final String teamGreeting;
   private final boolean isIdentityVerificationEnabled;
   private final boolean isAccessToTeammateEnabled;
   private final boolean isHelpCenterRequireSearchEnabled;
   private final boolean isPreventMultipleInboundConversationsEnabled;
   private final boolean hasOpenConversations;
   @Nullable
   private final ConfigModules configModules;
   @NotNull
   private final NexusConfig realTimeConfig;
   @NotNull
   private final AttachmentSettings attachmentSettings;
   private final boolean articleAutoReactionEnabled;
   @NotNull
   private final ConversationStateSyncSettings conversationStateSyncSettings;
   private final boolean isCrashReportingEnabled;
   public static final int $stable;

   static { $stable = 8; }

   public AppConfig(
      @NotNull String var1,
      @ColorInt int var2,
      @ColorInt int var3,
      @ColorInt int var4,
      boolean var5,
      boolean var6,
      boolean var7,
      int var8,
      long var9,
      long var11,
      long var13,
      long var15,
      boolean var17,
      boolean var18,
      @NotNull String var19,
      @NotNull String var20,
      boolean var21,
      boolean var22,
      @NotNull String var23,
      @NotNull Set var24,
      @NotNull Set var25,
      @NotNull String var26,
      @NotNull String var27,
      boolean var28,
      boolean var29,
      boolean var30,
      boolean var31,
      boolean var32,
      @Nullable ConfigModules var33,
      @NotNull NexusConfig var34,
      @NotNull AttachmentSettings var35,
      boolean var36,
      @NotNull ConversationStateSyncSettings var37,
      boolean var38
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "name");
      Intrinsics.checkNotNullParameter(var19, "locale");
      Intrinsics.checkNotNullParameter(var20, "helpCenterLocale");
      Intrinsics.checkNotNullParameter(var23, "helpCenterUrl");
      Intrinsics.checkNotNullParameter(var24, "helpCenterUrls");
      Intrinsics.checkNotNullParameter(var25, "features");
      Intrinsics.checkNotNullParameter(var26, "launcherLogoUrl");
      Intrinsics.checkNotNullParameter(var27, "teamGreeting");
      Intrinsics.checkNotNullParameter(var34, "realTimeConfig");
      Intrinsics.checkNotNullParameter(var35, "attachmentSettings");
      Intrinsics.checkNotNullParameter(var37, "conversationStateSyncSettings");
      this.name = var1;
      this.primaryColor = var2;
      this.secondaryColor = var3;
      this.secondaryColorDark = var4;
      this.isPrimaryColorRenderDarkText = var5;
      this.isSecondaryColorRenderDarkText = var6;
      this.isInboundMessages = var7;
      this.rateLimitCount = var8;
      this.rateLimitPeriodMs = var9;
      this.userUpdateCacheMaxAgeMs = var11;
      this.newSessionThresholdMs = var13;
      this.softResetTimeoutMs = var15;
      this.isMetricsEnabled = var17;
      this.isAudioEnabled = var18;
      this.locale = var19;
      this.helpCenterLocale = var20;
      this.isReceivedFromServer = var21;
      this.isBackgroundRequestsEnabled = var22;
      this.helpCenterUrl = var23;
      this.helpCenterUrls = var24;
      this.features = var25;
      this.launcherLogoUrl = var26;
      this.teamGreeting = var27;
      this.isIdentityVerificationEnabled = var28;
      this.isAccessToTeammateEnabled = var29;
      this.isHelpCenterRequireSearchEnabled = var30;
      this.isPreventMultipleInboundConversationsEnabled = var31;
      this.hasOpenConversations = var32;
      this.configModules = var33;
      this.realTimeConfig = var34;
      this.attachmentSettings = var35;
      this.articleAutoReactionEnabled = var36;
      this.conversationStateSyncSettings = var37;
      this.isCrashReportingEnabled = var38;
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   /** @deprecated */
   public final int getPrimaryColor() {
      return this.primaryColor;
   }

   /** @deprecated */
   // $VF: synthetic method
   @Deprecated(message = "Use IntercomTheme.colors.action instead")
   public static void getPrimaryColor$annotations() {
   }

   public final int getSecondaryColor() {
      return this.secondaryColor;
   }

   public final int getSecondaryColorDark() {
      return this.secondaryColorDark;
   }

   public final boolean isPrimaryColorRenderDarkText() {
      return this.isPrimaryColorRenderDarkText;
   }

   public final boolean isSecondaryColorRenderDarkText() {
      return this.isSecondaryColorRenderDarkText;
   }

   public final boolean isInboundMessages() {
      return this.isInboundMessages;
   }

   public final int getRateLimitCount() {
      return this.rateLimitCount;
   }

   public final long getRateLimitPeriodMs() {
      return this.rateLimitPeriodMs;
   }

   public final long getUserUpdateCacheMaxAgeMs() {
      return this.userUpdateCacheMaxAgeMs;
   }

   public final long getNewSessionThresholdMs() {
      return this.newSessionThresholdMs;
   }

   public final long getSoftResetTimeoutMs() {
      return this.softResetTimeoutMs;
   }

   public final boolean isMetricsEnabled() {
      return this.isMetricsEnabled;
   }

   public final boolean isAudioEnabled() {
      return this.isAudioEnabled;
   }

   @NotNull
   public final String getLocale() {
      return this.locale;
   }

   @NotNull
   public final String getHelpCenterLocale() {
      return this.helpCenterLocale;
   }

   public final boolean isReceivedFromServer() {
      return this.isReceivedFromServer;
   }

   public final boolean isBackgroundRequestsEnabled() {
      return this.isBackgroundRequestsEnabled;
   }

   @NotNull
   public final String getHelpCenterUrl() {
      return this.helpCenterUrl;
   }

   @NotNull
   public final Set getHelpCenterUrls() {
      return this.helpCenterUrls;
   }

   @NotNull
   public final Set getFeatures() {
      return this.features;
   }

   @NotNull
   public final String getLauncherLogoUrl() {
      return this.launcherLogoUrl;
   }

   @NotNull
   public final String getTeamGreeting() {
      return this.teamGreeting;
   }

   public final boolean isIdentityVerificationEnabled() {
      return this.isIdentityVerificationEnabled;
   }

   public final boolean isAccessToTeammateEnabled() {
      return this.isAccessToTeammateEnabled;
   }

   public final boolean isHelpCenterRequireSearchEnabled() {
      return this.isHelpCenterRequireSearchEnabled;
   }

   public final boolean isPreventMultipleInboundConversationsEnabled() {
      return this.isPreventMultipleInboundConversationsEnabled;
   }

   public final boolean getHasOpenConversations() {
      return this.hasOpenConversations;
   }

   @Nullable
   public final ConfigModules getConfigModules() {
      return this.configModules;
   }

   @NotNull
   public final NexusConfig getRealTimeConfig() {
      return this.realTimeConfig;
   }

   @NotNull
   public final AttachmentSettings getAttachmentSettings() {
      return this.attachmentSettings;
   }

   public final boolean getArticleAutoReactionEnabled() {
      return this.articleAutoReactionEnabled;
   }

   @NotNull
   public final ConversationStateSyncSettings getConversationStateSyncSettings() {
      return this.conversationStateSyncSettings;
   }

   public final boolean isCrashReportingEnabled() {
      return this.isCrashReportingEnabled;
   }

   public final boolean hasFeature(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "feature");
      return this.features.contains(var1);
   }

   public final boolean isBackgroundRequestsDisabled() {
      return !this.isBackgroundRequestsEnabled;
   }

   public final boolean isSpaceEnabled(@NotNull Space$Type var1) {
      Intrinsics.checkNotNullParameter(var1, "space");
      if (this.configModules != null) {
         HomeConfig var10000 = this.configModules.getHome();
         if (var10000 != null) {
            OpenConfig var8 = var10000.getOpenConfig();
            if (var8 != null) {
               List var9 = var8.getSpaces();
               if (var9 != null) {
                  var var2 = var9;
                  int var3 = 0;
                  boolean var10;
                  if (var2 instanceof Collection && ((Collection)var2).isEmpty()) {
                     var10 = false;
                  } else {
                     for (Object var5 : var2) {
                        Space var6 = (Space)var5;
                        int var7 = 0;
                        if (var6.getType() == var1) {
                           return true;
                        }
                     }

                     var10 = false;
                  }

                  return var10;
               }
            }
         }
      }

      return false;
   }

   @Nullable
   public final String getSpaceLabelIfExists(@NotNull Space$Type var1) {
      Intrinsics.checkNotNullParameter(var1, "space");
      if (this.configModules != null) {
         HomeConfig var10000 = this.configModules.getHome();
         if (var10000 != null) {
            OpenConfig var7 = var10000.getOpenConfig();
            if (var7 != null) {
               List var8 = var7.getSpaces();
               if (var8 != null) {
                  Object var9;
                  label30: {
                     for (Object var4 : var8) {
                        Space var5 = (Space)var4;
                        int var6 = 0;
                        if (var5.getType() == var1) {
                           var9 = var4;
                           break label30;
                        }
                     }

                     var9 = null;
                  }

                  Space var10 = (Space)var9;
                  if ((Space)var9 != null) {
                     return var10.getLabel();
                  }
               }
            }
         }
      }

      return null;
   }

   @NotNull
   public final String component1() {
      return this.name;
   }

   public final int component2() {
      return this.primaryColor;
   }

   public final int component3() {
      return this.secondaryColor;
   }

   public final int component4() {
      return this.secondaryColorDark;
   }

   public final boolean component5() {
      return this.isPrimaryColorRenderDarkText;
   }

   public final boolean component6() {
      return this.isSecondaryColorRenderDarkText;
   }

   public final boolean component7() {
      return this.isInboundMessages;
   }

   public final int component8() {
      return this.rateLimitCount;
   }

   public final long component9() {
      return this.rateLimitPeriodMs;
   }

   public final long component10() {
      return this.userUpdateCacheMaxAgeMs;
   }

   public final long component11() {
      return this.newSessionThresholdMs;
   }

   public final long component12() {
      return this.softResetTimeoutMs;
   }

   public final boolean component13() {
      return this.isMetricsEnabled;
   }

   public final boolean component14() {
      return this.isAudioEnabled;
   }

   @NotNull
   public final String component15() {
      return this.locale;
   }

   @NotNull
   public final String component16() {
      return this.helpCenterLocale;
   }

   public final boolean component17() {
      return this.isReceivedFromServer;
   }

   public final boolean component18() {
      return this.isBackgroundRequestsEnabled;
   }

   @NotNull
   public final String component19() {
      return this.helpCenterUrl;
   }

   @NotNull
   public final Set component20() {
      return this.helpCenterUrls;
   }

   @NotNull
   public final Set component21() {
      return this.features;
   }

   @NotNull
   public final String component22() {
      return this.launcherLogoUrl;
   }

   @NotNull
   public final String component23() {
      return this.teamGreeting;
   }

   public final boolean component24() {
      return this.isIdentityVerificationEnabled;
   }

   public final boolean component25() {
      return this.isAccessToTeammateEnabled;
   }

   public final boolean component26() {
      return this.isHelpCenterRequireSearchEnabled;
   }

   public final boolean component27() {
      return this.isPreventMultipleInboundConversationsEnabled;
   }

   public final boolean component28() {
      return this.hasOpenConversations;
   }

   @Nullable
   public final ConfigModules component29() {
      return this.configModules;
   }

   @NotNull
   public final NexusConfig component30() {
      return this.realTimeConfig;
   }

   @NotNull
   public final AttachmentSettings component31() {
      return this.attachmentSettings;
   }

   public final boolean component32() {
      return this.articleAutoReactionEnabled;
   }

   @NotNull
   public final ConversationStateSyncSettings component33() {
      return this.conversationStateSyncSettings;
   }

   public final boolean component34() {
      return this.isCrashReportingEnabled;
   }

   @NotNull
   public final AppConfig copy(
      @NotNull String var1,
      @ColorInt int var2,
      @ColorInt int var3,
      @ColorInt int var4,
      boolean var5,
      boolean var6,
      boolean var7,
      int var8,
      long var9,
      long var11,
      long var13,
      long var15,
      boolean var17,
      boolean var18,
      @NotNull String var19,
      @NotNull String var20,
      boolean var21,
      boolean var22,
      @NotNull String var23,
      @NotNull Set var24,
      @NotNull Set var25,
      @NotNull String var26,
      @NotNull String var27,
      boolean var28,
      boolean var29,
      boolean var30,
      boolean var31,
      boolean var32,
      @Nullable ConfigModules var33,
      @NotNull NexusConfig var34,
      @NotNull AttachmentSettings var35,
      boolean var36,
      @NotNull ConversationStateSyncSettings var37,
      boolean var38
   ) {
      Intrinsics.checkNotNullParameter(var1, "name");
      Intrinsics.checkNotNullParameter(var19, "locale");
      Intrinsics.checkNotNullParameter(var20, "helpCenterLocale");
      Intrinsics.checkNotNullParameter(var23, "helpCenterUrl");
      Intrinsics.checkNotNullParameter(var24, "helpCenterUrls");
      Intrinsics.checkNotNullParameter(var25, "features");
      Intrinsics.checkNotNullParameter(var26, "launcherLogoUrl");
      Intrinsics.checkNotNullParameter(var27, "teamGreeting");
      Intrinsics.checkNotNullParameter(var34, "realTimeConfig");
      Intrinsics.checkNotNullParameter(var35, "attachmentSettings");
      Intrinsics.checkNotNullParameter(var37, "conversationStateSyncSettings");
      return new AppConfig(
         var1,
         var2,
         var3,
         var4,
         var5,
         var6,
         var7,
         var8,
         var9,
         var11,
         var13,
         var15,
         var17,
         var18,
         var19,
         var20,
         var21,
         var22,
         var23,
         var24,
         var25,
         var26,
         var27,
         var28,
         var29,
         var30,
         var31,
         var32,
         var33,
         var34,
         var35,
         var36,
         var37,
         var38
      );
   }

   // $VF: synthetic method
   public static AppConfig copy$default(
      AppConfig var0,
      String var1,
      int var2,
      int var3,
      int var4,
      boolean var5,
      boolean var6,
      boolean var7,
      int var8,
      long var9,
      long var11,
      long var13,
      long var15,
      boolean var17,
      boolean var18,
      String var19,
      String var20,
      boolean var21,
      boolean var22,
      String var23,
      Set var24,
      Set var25,
      String var26,
      String var27,
      boolean var28,
      boolean var29,
      boolean var30,
      boolean var31,
      boolean var32,
      ConfigModules var33,
      NexusConfig var34,
      AttachmentSettings var35,
      boolean var36,
      ConversationStateSyncSettings var37,
      boolean var38,
      int var39,
      int var40,
      Object var41
   ) {
      if ((var39 & 1) != 0) {
         var1 = var0.name;
      }

      if ((var39 & 2) != 0) {
         var2 = var0.primaryColor;
      }

      if ((var39 & 4) != 0) {
         var3 = var0.secondaryColor;
      }

      if ((var39 & 8) != 0) {
         var4 = var0.secondaryColorDark;
      }

      if ((var39 & 16) != 0) {
         var5 = var0.isPrimaryColorRenderDarkText;
      }

      if ((var39 & 32) != 0) {
         var6 = var0.isSecondaryColorRenderDarkText;
      }

      if ((var39 & 64) != 0) {
         var7 = var0.isInboundMessages;
      }

      if ((var39 & 128) != 0) {
         var8 = var0.rateLimitCount;
      }

      if ((var39 & 256) != 0) {
         var9 = var0.rateLimitPeriodMs;
      }

      if ((var39 & 512) != 0) {
         var11 = var0.userUpdateCacheMaxAgeMs;
      }

      if ((var39 & 1024) != 0) {
         var13 = var0.newSessionThresholdMs;
      }

      if ((var39 & 2048) != 0) {
         var15 = var0.softResetTimeoutMs;
      }

      if ((var39 & 4096) != 0) {
         var17 = var0.isMetricsEnabled;
      }

      if ((var39 & 8192) != 0) {
         var18 = var0.isAudioEnabled;
      }

      if ((var39 & 16384) != 0) {
         var19 = var0.locale;
      }

      if ((var39 & 32768) != 0) {
         var20 = var0.helpCenterLocale;
      }

      if ((var39 & 65536) != 0) {
         var21 = var0.isReceivedFromServer;
      }

      if ((var39 & 131072) != 0) {
         var22 = var0.isBackgroundRequestsEnabled;
      }

      if ((var39 & 262144) != 0) {
         var23 = var0.helpCenterUrl;
      }

      if ((var39 & 524288) != 0) {
         var24 = var0.helpCenterUrls;
      }

      if ((var39 & 1048576) != 0) {
         var25 = var0.features;
      }

      if ((var39 & 2097152) != 0) {
         var26 = var0.launcherLogoUrl;
      }

      if ((var39 & 4194304) != 0) {
         var27 = var0.teamGreeting;
      }

      if ((var39 & 8388608) != 0) {
         var28 = var0.isIdentityVerificationEnabled;
      }

      if ((var39 & 16777216) != 0) {
         var29 = var0.isAccessToTeammateEnabled;
      }

      if ((var39 & 33554432) != 0) {
         var30 = var0.isHelpCenterRequireSearchEnabled;
      }

      if ((var39 & 67108864) != 0) {
         var31 = var0.isPreventMultipleInboundConversationsEnabled;
      }

      if ((var39 & 134217728) != 0) {
         var32 = var0.hasOpenConversations;
      }

      if ((var39 & 268435456) != 0) {
         var33 = var0.configModules;
      }

      if ((var39 & 536870912) != 0) {
         var34 = var0.realTimeConfig;
      }

      if ((var39 & 1073741824) != 0) {
         var35 = var0.attachmentSettings;
      }

      if ((var39 & -2147483648) != 0) {
         var36 = var0.articleAutoReactionEnabled;
      }

      if ((var40 & 1) != 0) {
         var37 = var0.conversationStateSyncSettings;
      }

      if ((var40 & 2) != 0) {
         var38 = var0.isCrashReportingEnabled;
      }

      return var0.copy(
         var1,
         var2,
         var3,
         var4,
         var5,
         var6,
         var7,
         var8,
         var9,
         var11,
         var13,
         var15,
         var17,
         var18,
         var19,
         var20,
         var21,
         var22,
         var23,
         var24,
         var25,
         var26,
         var27,
         var28,
         var29,
         var30,
         var31,
         var32,
         var33,
         var34,
         var35,
         var36,
         var37,
         var38
      );
   }

   @NotNull
   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("AppConfig(name=")
         .append(this.name)
         .append(", primaryColor=")
         .append(this.primaryColor)
         .append(", secondaryColor=")
         .append(this.secondaryColor)
         .append(", secondaryColorDark=")
         .append(this.secondaryColorDark)
         .append(", isPrimaryColorRenderDarkText=")
         .append(this.isPrimaryColorRenderDarkText)
         .append(", isSecondaryColorRenderDarkText=")
         .append(this.isSecondaryColorRenderDarkText)
         .append(", isInboundMessages=")
         .append(this.isInboundMessages)
         .append(", rateLimitCount=")
         .append(this.rateLimitCount)
         .append(", rateLimitPeriodMs=")
         .append(this.rateLimitPeriodMs)
         .append(", userUpdateCacheMaxAgeMs=")
         .append(this.userUpdateCacheMaxAgeMs)
         .append(", newSessionThresholdMs=")
         .append(this.newSessionThresholdMs)
         .append(", softResetTimeoutMs=");
      var1.append(this.softResetTimeoutMs)
         .append(", isMetricsEnabled=")
         .append(this.isMetricsEnabled)
         .append(", isAudioEnabled=")
         .append(this.isAudioEnabled)
         .append(", locale=")
         .append(this.locale)
         .append(", helpCenterLocale=")
         .append(this.helpCenterLocale)
         .append(", isReceivedFromServer=")
         .append(this.isReceivedFromServer)
         .append(", isBackgroundRequestsEnabled=")
         .append(this.isBackgroundRequestsEnabled)
         .append(", helpCenterUrl=")
         .append(this.helpCenterUrl)
         .append(", helpCenterUrls=")
         .append(this.helpCenterUrls)
         .append(", features=")
         .append(this.features)
         .append(", launcherLogoUrl=")
         .append(this.launcherLogoUrl)
         .append(", teamGreeting=")
         .append(this.teamGreeting);
      var1.append(", isIdentityVerificationEnabled=")
         .append(this.isIdentityVerificationEnabled)
         .append(", isAccessToTeammateEnabled=")
         .append(this.isAccessToTeammateEnabled)
         .append(", isHelpCenterRequireSearchEnabled=")
         .append(this.isHelpCenterRequireSearchEnabled)
         .append(", isPreventMultipleInboundConversationsEnabled=")
         .append(this.isPreventMultipleInboundConversationsEnabled)
         .append(", hasOpenConversations=")
         .append(this.hasOpenConversations)
         .append(", configModules=")
         .append(this.configModules)
         .append(", realTimeConfig=")
         .append(this.realTimeConfig)
         .append(", attachmentSettings=")
         .append(this.attachmentSettings)
         .append(", articleAutoReactionEnabled=")
         .append(this.articleAutoReactionEnabled)
         .append(", conversationStateSyncSettings=")
         .append(this.conversationStateSyncSettings)
         .append(", isCrashReportingEnabled=")
         .append(this.isCrashReportingEnabled)
         .append(')');
      return var1.toString();
   }

   @Override
   public int hashCode() {
      int var1 = this.name.hashCode();
      var1 = var1 * 31 + Integer.hashCode(this.primaryColor);
      var1 = var1 * 31 + Integer.hashCode(this.secondaryColor);
      var1 = var1 * 31 + Integer.hashCode(this.secondaryColorDark);
      var1 = var1 * 31 + Boolean.hashCode(this.isPrimaryColorRenderDarkText);
      var1 = var1 * 31 + Boolean.hashCode(this.isSecondaryColorRenderDarkText);
      var1 = var1 * 31 + Boolean.hashCode(this.isInboundMessages);
      var1 = var1 * 31 + Integer.hashCode(this.rateLimitCount);
      var1 = var1 * 31 + Long.hashCode(this.rateLimitPeriodMs);
      var1 = var1 * 31 + Long.hashCode(this.userUpdateCacheMaxAgeMs);
      var1 = var1 * 31 + Long.hashCode(this.newSessionThresholdMs);
      var1 = var1 * 31 + Long.hashCode(this.softResetTimeoutMs);
      var1 = var1 * 31 + Boolean.hashCode(this.isMetricsEnabled);
      var1 = var1 * 31 + Boolean.hashCode(this.isAudioEnabled);
      var1 = var1 * 31 + this.locale.hashCode();
      var1 = var1 * 31 + this.helpCenterLocale.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.isReceivedFromServer);
      var1 = var1 * 31 + Boolean.hashCode(this.isBackgroundRequestsEnabled);
      var1 = var1 * 31 + this.helpCenterUrl.hashCode();
      var1 = var1 * 31 + this.helpCenterUrls.hashCode();
      var1 = var1 * 31 + this.features.hashCode();
      var1 = var1 * 31 + this.launcherLogoUrl.hashCode();
      var1 = var1 * 31 + this.teamGreeting.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.isIdentityVerificationEnabled);
      var1 = var1 * 31 + Boolean.hashCode(this.isAccessToTeammateEnabled);
      var1 = var1 * 31 + Boolean.hashCode(this.isHelpCenterRequireSearchEnabled);
      var1 = var1 * 31 + Boolean.hashCode(this.isPreventMultipleInboundConversationsEnabled);
      var1 = var1 * 31 + Boolean.hashCode(this.hasOpenConversations);
      var1 = var1 * 31 + (this.configModules == null ? 0 : this.configModules.hashCode());
      var1 = var1 * 31 + this.realTimeConfig.hashCode();
      var1 = var1 * 31 + this.attachmentSettings.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.articleAutoReactionEnabled);
      var1 = var1 * 31 + this.conversationStateSyncSettings.hashCode();
      return var1 * 31 + Boolean.hashCode(this.isCrashReportingEnabled);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof AppConfig)) {
         return false;
      } else {
         AppConfig var2 = (AppConfig)var1;
         if (!Intrinsics.areEqual(this.name, var2.name)) {
            return false;
         } else if (this.primaryColor != var2.primaryColor) {
            return false;
         } else if (this.secondaryColor != var2.secondaryColor) {
            return false;
         } else if (this.secondaryColorDark != var2.secondaryColorDark) {
            return false;
         } else if (this.isPrimaryColorRenderDarkText != var2.isPrimaryColorRenderDarkText) {
            return false;
         } else if (this.isSecondaryColorRenderDarkText != var2.isSecondaryColorRenderDarkText) {
            return false;
         } else if (this.isInboundMessages != var2.isInboundMessages) {
            return false;
         } else if (this.rateLimitCount != var2.rateLimitCount) {
            return false;
         } else if (this.rateLimitPeriodMs != var2.rateLimitPeriodMs) {
            return false;
         } else if (this.userUpdateCacheMaxAgeMs != var2.userUpdateCacheMaxAgeMs) {
            return false;
         } else if (this.newSessionThresholdMs != var2.newSessionThresholdMs) {
            return false;
         } else if (this.softResetTimeoutMs != var2.softResetTimeoutMs) {
            return false;
         } else if (this.isMetricsEnabled != var2.isMetricsEnabled) {
            return false;
         } else if (this.isAudioEnabled != var2.isAudioEnabled) {
            return false;
         } else if (!Intrinsics.areEqual(this.locale, var2.locale)) {
            return false;
         } else if (!Intrinsics.areEqual(this.helpCenterLocale, var2.helpCenterLocale)) {
            return false;
         } else if (this.isReceivedFromServer != var2.isReceivedFromServer) {
            return false;
         } else if (this.isBackgroundRequestsEnabled != var2.isBackgroundRequestsEnabled) {
            return false;
         } else if (!Intrinsics.areEqual(this.helpCenterUrl, var2.helpCenterUrl)) {
            return false;
         } else if (!Intrinsics.areEqual(this.helpCenterUrls, var2.helpCenterUrls)) {
            return false;
         } else if (!Intrinsics.areEqual(this.features, var2.features)) {
            return false;
         } else if (!Intrinsics.areEqual(this.launcherLogoUrl, var2.launcherLogoUrl)) {
            return false;
         } else if (!Intrinsics.areEqual(this.teamGreeting, var2.teamGreeting)) {
            return false;
         } else if (this.isIdentityVerificationEnabled != var2.isIdentityVerificationEnabled) {
            return false;
         } else if (this.isAccessToTeammateEnabled != var2.isAccessToTeammateEnabled) {
            return false;
         } else if (this.isHelpCenterRequireSearchEnabled != var2.isHelpCenterRequireSearchEnabled) {
            return false;
         } else if (this.isPreventMultipleInboundConversationsEnabled != var2.isPreventMultipleInboundConversationsEnabled) {
            return false;
         } else if (this.hasOpenConversations != var2.hasOpenConversations) {
            return false;
         } else if (!Intrinsics.areEqual(this.configModules, var2.configModules)) {
            return false;
         } else if (!Intrinsics.areEqual(this.realTimeConfig, var2.realTimeConfig)) {
            return false;
         } else if (!Intrinsics.areEqual(this.attachmentSettings, var2.attachmentSettings)) {
            return false;
         } else if (this.articleAutoReactionEnabled != var2.articleAutoReactionEnabled) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.conversationStateSyncSettings, var2.conversationStateSyncSettings)
               ? false
               : this.isCrashReportingEnabled == var2.isCrashReportingEnabled;
         }
      }
   }
}
