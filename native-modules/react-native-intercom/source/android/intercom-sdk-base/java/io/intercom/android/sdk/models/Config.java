package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import io.intercom.android.nexus.NexusConfig;
import io.intercom.android.nexus.NexusConfig$Builder;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\\\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b(\n\u0002\u0018\u0002\n\u0002\b*\b\u0081\b\u0018\u0000 ~2\u00020\u0001:\u0001~Bå\u0002\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\u0003\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\u000b\u0012\b\b\u0002\u0010\r\u001a\u00020\u000b\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000b\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u000b\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u000b\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u000b\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0013\u0012\b\b\u0002\u0010\u0014\u001a\u00020\u0015\u0012\b\b\u0002\u0010\u0016\u001a\u00020\u0013\u0012\b\b\u0002\u0010\u0017\u001a\u00020\u0013\u0012\b\b\u0002\u0010\u0018\u001a\u00020\u0013\u0012\b\b\u0002\u0010\u0019\u001a\u00020\u001a\u0012\b\b\u0002\u0010\u001b\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00030\u001d\u0012\u000e\b\u0002\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00030\u001d\u0012\b\b\u0002\u0010\u001f\u001a\u00020\u000b\u0012\b\b\u0002\u0010 \u001a\u00020\u000b\u0012\b\b\u0002\u0010!\u001a\u00020\u000b\u0012\b\b\u0002\u0010\"\u001a\u00020\u000b\u0012\b\b\u0002\u0010#\u001a\u00020\u000b\u0012\n\b\u0002\u0010$\u001a\u0004\u0018\u00010%\u0012\b\b\u0002\u0010&\u001a\u00020'\u0012\b\b\u0002\u0010(\u001a\u00020\u000b\u0012\b\b\u0002\u0010)\u001a\u00020*\u0012\n\b\u0002\u0010+\u001a\u0004\u0018\u00010,¢\u0006\u0004\b-\u0010.J\t\u0010X\u001a\u00020\u0003HÆ\u0003J\u000b\u0010Y\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010Z\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010[\u001a\u00020\u0003HÆ\u0003J\t\u0010\\\u001a\u00020\u0003HÆ\u0003J\t\u0010]\u001a\u00020\u0003HÆ\u0003J\t\u0010^\u001a\u00020\u0003HÆ\u0003J\t\u0010_\u001a\u00020\u000bHÆ\u0003J\t\u0010`\u001a\u00020\u000bHÆ\u0003J\t\u0010a\u001a\u00020\u000bHÆ\u0003J\t\u0010b\u001a\u00020\u000bHÆ\u0003J\t\u0010c\u001a\u00020\u000bHÆ\u0003J\t\u0010d\u001a\u00020\u000bHÆ\u0003J\t\u0010e\u001a\u00020\u000bHÆ\u0003J\t\u0010f\u001a\u00020\u0013HÂ\u0003J\t\u0010g\u001a\u00020\u0015HÆ\u0003J\t\u0010h\u001a\u00020\u0013HÂ\u0003J\t\u0010i\u001a\u00020\u0013HÂ\u0003J\t\u0010j\u001a\u00020\u0013HÂ\u0003J\t\u0010k\u001a\u00020\u001aHÂ\u0003J\t\u0010l\u001a\u00020\u0003HÆ\u0003J\u000f\u0010m\u001a\b\u0012\u0004\u0012\u00020\u00030\u001dHÆ\u0003J\u000f\u0010n\u001a\b\u0012\u0004\u0012\u00020\u00030\u001dHÆ\u0003J\t\u0010o\u001a\u00020\u000bHÆ\u0003J\t\u0010p\u001a\u00020\u000bHÆ\u0003J\t\u0010q\u001a\u00020\u000bHÆ\u0003J\t\u0010r\u001a\u00020\u000bHÆ\u0003J\t\u0010s\u001a\u00020\u000bHÆ\u0003J\u000b\u0010t\u001a\u0004\u0018\u00010%HÆ\u0003J\t\u0010u\u001a\u00020'HÆ\u0003J\t\u0010v\u001a\u00020\u000bHÆ\u0003J\t\u0010w\u001a\u00020*HÆ\u0003J\u000b\u0010x\u001a\u0004\u0018\u00010,HÆ\u0003Jç\u0002\u0010y\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u000b2\b\b\u0002\u0010\r\u001a\u00020\u000b2\b\b\u0002\u0010\u000e\u001a\u00020\u000b2\b\b\u0002\u0010\u000f\u001a\u00020\u000b2\b\b\u0002\u0010\u0010\u001a\u00020\u000b2\b\b\u0002\u0010\u0011\u001a\u00020\u000b2\b\b\u0002\u0010\u0012\u001a\u00020\u00132\b\b\u0002\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u00132\b\b\u0002\u0010\u0017\u001a\u00020\u00132\b\b\u0002\u0010\u0018\u001a\u00020\u00132\b\b\u0002\u0010\u0019\u001a\u00020\u001a2\b\b\u0002\u0010\u001b\u001a\u00020\u00032\u000e\b\u0002\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00030\u001d2\u000e\b\u0002\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00030\u001d2\b\b\u0002\u0010\u001f\u001a\u00020\u000b2\b\b\u0002\u0010 \u001a\u00020\u000b2\b\b\u0002\u0010!\u001a\u00020\u000b2\b\b\u0002\u0010\"\u001a\u00020\u000b2\b\b\u0002\u0010#\u001a\u00020\u000b2\n\b\u0002\u0010$\u001a\u0004\u0018\u00010%2\b\b\u0002\u0010&\u001a\u00020'2\b\b\u0002\u0010(\u001a\u00020\u000b2\b\b\u0002\u0010)\u001a\u00020*2\n\b\u0002\u0010+\u001a\u0004\u0018\u00010,HÇ\u0001J\u0013\u0010z\u001a\u00020\u000b2\b\u0010{\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010|\u001a\u00020\u0015H×\u0001J\t\u0010}\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b/\u00100R\u0018\u0010\u0004\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b1\u00100R\u0018\u0010\u0005\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b2\u00100R\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b3\u00100R\u0016\u0010\u0007\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b4\u00100R\u0016\u0010\b\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b5\u00100R\u0016\u0010\t\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b6\u00100R\u0016\u0010\n\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u00107R\u0016\u0010\f\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u00107R\u0016\u0010\r\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u00107R\u0016\u0010\u000e\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u00107R\u0016\u0010\u000f\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u00107R\u0016\u0010\u0010\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u00107R\u0016\u0010\u0011\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u00107R\u0010\u0010\u0012\u001a\u00020\u00138\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0014\u001a\u00020\u00158\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b8\u00109R\u0010\u0010\u0016\u001a\u00020\u00138\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0017\u001a\u00020\u00138\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0018\u001a\u00020\u00138\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0019\u001a\u00020\u001a8\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u001b\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b:\u00100R\u001c\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00030\u001d8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b;\u0010<R\u001c\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00030\u001d8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b=\u0010<R\u0016\u0010\u001f\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b>\u00107R\u0016\u0010 \u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b?\u00107R\u0016\u0010!\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b@\u00107R\u0016\u0010\"\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\bA\u00107R\u0016\u0010#\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\bB\u00107R\u0018\u0010$\u001a\u0004\u0018\u00010%8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\bC\u0010DR\u0016\u0010&\u001a\u00020'8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\bE\u0010FR\u0016\u0010(\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b(\u00107R\u0016\u0010)\u001a\u00020*8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\bG\u0010HR\u0018\u0010+\u001a\u0004\u0018\u00010,8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\bI\u0010JR\u0011\u0010K\u001a\u00020\u00138F¢\u0006\u0006\u001a\u0004\bL\u0010MR\u0011\u0010N\u001a\u00020\u00138F¢\u0006\u0006\u001a\u0004\bO\u0010MR\u0011\u0010P\u001a\u00020\u00138F¢\u0006\u0006\u001a\u0004\bQ\u0010MR\u0011\u0010R\u001a\u00020\u00138F¢\u0006\u0006\u001a\u0004\bS\u0010MR\u0011\u0010T\u001a\u00020U8F¢\u0006\u0006\u001a\u0004\bV\u0010W¨\u0006\u007f",
   d2 = {
         "Lio/intercom/android/sdk/models/Config;",
         "",
         "name",
         "",
         "primaryColor",
         "secondaryColor",
         "locale",
         "helpCenterLocale",
         "launcherLogoUrl",
         "teamGreeting",
         "isFirstRequest",
         "",
         "isInboundMessages",
         "isAudioEnabled",
         "isMetricsEnabled",
         "isBackgroundRequestsEnabled",
         "isPrimaryColorRenderDarkText",
         "isSecondaryColorRenderDarkText",
         "_rateLimitPeriodSeconds",
         "",
         "rateLimitCount",
         "",
         "_userUpdateCacheMaxAgeSeconds",
         "_softResetTimeoutSeconds",
         "_newSessionThresholdSeconds",
         "_realTimeConfigBuilder",
         "Lio/intercom/android/nexus/NexusConfig$Builder;",
         "helpCenterUrl",
         "helpCenterUrls",
         "",
         "features",
         "identityVerificationEnabled",
         "accessToTeammateEnabled",
         "helpCenterRequireSearch",
         "preventMultipleInboundConversationsEnabled",
         "hasOpenConversations",
         "modules",
         "Lio/intercom/android/sdk/models/ConfigModules;",
         "attachmentSettings",
         "Lio/intercom/android/sdk/models/AttachmentSettings;",
         "isArticleAutoReactionEnabled",
         "conversationStateSyncSettings",
         "Lio/intercom/android/sdk/models/ConversationStateSyncSettings;",
         "crashReportingConfig",
         "Lio/intercom/android/sdk/models/CrashReportingConfig;",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZZZZZZJIJJJLio/intercom/android/nexus/NexusConfig$Builder;Ljava/lang/String;Ljava/util/Set;Ljava/util/Set;ZZZZZLio/intercom/android/sdk/models/ConfigModules;Lio/intercom/android/sdk/models/AttachmentSettings;ZLio/intercom/android/sdk/models/ConversationStateSyncSettings;Lio/intercom/android/sdk/models/CrashReportingConfig;)V",
         "getName",
         "()Ljava/lang/String;",
         "getPrimaryColor",
         "getSecondaryColor",
         "getLocale",
         "getHelpCenterLocale",
         "getLauncherLogoUrl",
         "getTeamGreeting",
         "()Z",
         "getRateLimitCount",
         "()I",
         "getHelpCenterUrl",
         "getHelpCenterUrls",
         "()Ljava/util/Set;",
         "getFeatures",
         "getIdentityVerificationEnabled",
         "getAccessToTeammateEnabled",
         "getHelpCenterRequireSearch",
         "getPreventMultipleInboundConversationsEnabled",
         "getHasOpenConversations",
         "getModules",
         "()Lio/intercom/android/sdk/models/ConfigModules;",
         "getAttachmentSettings",
         "()Lio/intercom/android/sdk/models/AttachmentSettings;",
         "getConversationStateSyncSettings",
         "()Lio/intercom/android/sdk/models/ConversationStateSyncSettings;",
         "getCrashReportingConfig",
         "()Lio/intercom/android/sdk/models/CrashReportingConfig;",
         "rateLimitPeriod",
         "getRateLimitPeriod",
         "()J",
         "userUpdateCacheMaxAge",
         "getUserUpdateCacheMaxAge",
         "softResetTimeout",
         "getSoftResetTimeout",
         "newSessionThreshold",
         "getNewSessionThreshold",
         "realTimeConfig",
         "Lio/intercom/android/nexus/NexusConfig;",
         "getRealTimeConfig",
         "()Lio/intercom/android/nexus/NexusConfig;",
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
         "copy",
         "equals",
         "other",
         "hashCode",
         "toString",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class Config {
   @NotNull
   public static final Config$Companion Companion = new Config$Companion(null);
   @NotNull
   private final String name;
   @SerializedName("base_color")
   @Nullable
   private final String primaryColor;
   @SerializedName("secondary_color")
   @Nullable
   private final String secondaryColor;
   @NotNull
   private final String locale;
   @SerializedName("help_center_locale")
   @NotNull
   private final String helpCenterLocale;
   @SerializedName("launcher_logo_url")
   @NotNull
   private final String launcherLogoUrl;
   @SerializedName("team_greeting")
   @NotNull
   private final String teamGreeting;
   @SerializedName("is_first_request")
   private final boolean isFirstRequest;
   @SerializedName("inbound_messages")
   private final boolean isInboundMessages;
   @SerializedName("audio_enabled")
   private final boolean isAudioEnabled;
   @SerializedName("metrics_enabled")
   private final boolean isMetricsEnabled;
   @SerializedName("background_requests_enabled")
   private final boolean isBackgroundRequestsEnabled;
   @SerializedName("primary_color_render_dark_text")
   private final boolean isPrimaryColorRenderDarkText;
   @SerializedName("secondary_color_render_dark_text")
   private final boolean isSecondaryColorRenderDarkText;
   @SerializedName("local_rate_limit_period")
   private final long _rateLimitPeriodSeconds;
   @SerializedName("local_rate_limit")
   private final int rateLimitCount;
   @SerializedName("user_update_dup_cache_max_age")
   private final long _userUpdateCacheMaxAgeSeconds;
   @SerializedName("soft_reset_timeout")
   private final long _softResetTimeoutSeconds;
   @SerializedName("new_session_threshold")
   private final long _newSessionThresholdSeconds;
   @SerializedName("real_time_config")
   @NotNull
   private final NexusConfig$Builder _realTimeConfigBuilder;
   @SerializedName("help_center_url")
   @NotNull
   private final String helpCenterUrl;
   @SerializedName("help_center_urls")
   @NotNull
   private final Set helpCenterUrls;
   @SerializedName("features")
   @NotNull
   private final Set features;
   @SerializedName("identity_verification_enabled")
   private final boolean identityVerificationEnabled;
   @SerializedName("access_to_teammate_enabled")
   private final boolean accessToTeammateEnabled;
   @SerializedName("help_center_require_search")
   private final boolean helpCenterRequireSearch;
   @SerializedName("prevent_multiple_inbound_conversations_enabled")
   private final boolean preventMultipleInboundConversationsEnabled;
   @SerializedName("has_open_conversations")
   private final boolean hasOpenConversations;
   @SerializedName("modules")
   @Nullable
   private final ConfigModules modules;
   @SerializedName("attachment_settings")
   @NotNull
   private final AttachmentSettings attachmentSettings;
   @SerializedName("article_auto_reaction_enabled")
   private final boolean isArticleAutoReactionEnabled;
   @SerializedName("conversation_state_sync_settings")
   @NotNull
   private final ConversationStateSyncSettings conversationStateSyncSettings;
   @SerializedName("crash_reporting_config")
   @Nullable
   private final CrashReportingConfig crashReportingConfig;
   public static final int $stable;

   static { $stable = 8; }
   public static final int DEFAULT_RATE_LIMIT_COUNT = 100;
   public static final long DEFAULT_RATE_LIMIT_PERIOD_SECONDS = 60L;
   public static final long DEFAULT_CACHE_MAX_AGE_SECONDS = 300L;
   public static final long DEFAULT_SESSION_TIMEOUT_SECONDS = 20L;
   public static final long DEFAULT_SOFT_RESET_TIMEOUT_SECONDS = 1L;
   @NotNull
   private static final Config NULL = new Config(
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      false,
      false,
      false,
      false,
      false,
      false,
      false,
      0L,
      0,
      0L,
      0L,
      0L,
      null,
      null,
      null,
      null,
      false,
      false,
      false,
      false,
      false,
      null,
      null,
      false,
      null,
      null,
      -1,
      1,
      null
   );

   public Config(
      @NotNull String var1,
      @Nullable String var2,
      @Nullable String var3,
      @NotNull String var4,
      @NotNull String var5,
      @NotNull String var6,
      @NotNull String var7,
      boolean var8,
      boolean var9,
      boolean var10,
      boolean var11,
      boolean var12,
      boolean var13,
      boolean var14,
      long var15,
      int var17,
      long var18,
      long var20,
      long var22,
      @NotNull NexusConfig$Builder var24,
      @NotNull String var25,
      @NotNull Set var26,
      @NotNull Set var27,
      boolean var28,
      boolean var29,
      boolean var30,
      boolean var31,
      boolean var32,
      @Nullable ConfigModules var33,
      @NotNull AttachmentSettings var34,
      boolean var35,
      @NotNull ConversationStateSyncSettings var36,
      @Nullable CrashReportingConfig var37
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "name");
      Intrinsics.checkNotNullParameter(var4, "locale");
      Intrinsics.checkNotNullParameter(var5, "helpCenterLocale");
      Intrinsics.checkNotNullParameter(var6, "launcherLogoUrl");
      Intrinsics.checkNotNullParameter(var7, "teamGreeting");
      Intrinsics.checkNotNullParameter(var24, "_realTimeConfigBuilder");
      Intrinsics.checkNotNullParameter(var25, "helpCenterUrl");
      Intrinsics.checkNotNullParameter(var26, "helpCenterUrls");
      Intrinsics.checkNotNullParameter(var27, "features");
      Intrinsics.checkNotNullParameter(var34, "attachmentSettings");
      Intrinsics.checkNotNullParameter(var36, "conversationStateSyncSettings");
      this.name = var1;
      this.primaryColor = var2;
      this.secondaryColor = var3;
      this.locale = var4;
      this.helpCenterLocale = var5;
      this.launcherLogoUrl = var6;
      this.teamGreeting = var7;
      this.isFirstRequest = var8;
      this.isInboundMessages = var9;
      this.isAudioEnabled = var10;
      this.isMetricsEnabled = var11;
      this.isBackgroundRequestsEnabled = var12;
      this.isPrimaryColorRenderDarkText = var13;
      this.isSecondaryColorRenderDarkText = var14;
      this._rateLimitPeriodSeconds = var15;
      this.rateLimitCount = var17;
      this._userUpdateCacheMaxAgeSeconds = var18;
      this._softResetTimeoutSeconds = var20;
      this._newSessionThresholdSeconds = var22;
      this._realTimeConfigBuilder = var24;
      this.helpCenterUrl = var25;
      this.helpCenterUrls = var26;
      this.features = var27;
      this.identityVerificationEnabled = var28;
      this.accessToTeammateEnabled = var29;
      this.helpCenterRequireSearch = var30;
      this.preventMultipleInboundConversationsEnabled = var31;
      this.hasOpenConversations = var32;
      this.modules = var33;
      this.attachmentSettings = var34;
      this.isArticleAutoReactionEnabled = var35;
      this.conversationStateSyncSettings = var36;
      this.crashReportingConfig = var37;
   }

   // $VF: synthetic method
   public Config(
      String var1,
      String var2,
      String var3,
      String var4,
      String var5,
      String var6,
      String var7,
      boolean var8,
      boolean var9,
      boolean var10,
      boolean var11,
      boolean var12,
      boolean var13,
      boolean var14,
      long var15,
      int var17,
      long var18,
      long var20,
      long var22,
      NexusConfig$Builder var24,
      String var25,
      Set var26,
      Set var27,
      boolean var28,
      boolean var29,
      boolean var30,
      boolean var31,
      boolean var32,
      ConfigModules var33,
      AttachmentSettings var34,
      boolean var35,
      ConversationStateSyncSettings var36,
      CrashReportingConfig var37,
      int var38,
      int var39,
      DefaultConstructorMarker var40
   ) {
      this((((var38 & 1) != 0) ? (String) ("") : var1), (((var38 & 2) != 0) ? (String) (null) : var2), (((var38 & 4) != 0) ? (String) (null) : var3), (((var38 & 8) != 0) ? (String) ("") : var4), (((var38 & 16) != 0) ? (String) ("") : var5), (((var38 & 32) != 0) ? (String) ("") : var6), (((var38 & 64) != 0) ? (String) ("") : var7), (((var38 & 128) != 0) ? (boolean) (false) : var8), (((var38 & 256) != 0) ? (boolean) (false) : var9), (((var38 & 512) != 0) ? (boolean) (true) : var10), (((var38 & 1024) != 0) ? (boolean) (true) : var11), (((var38 & 2048) != 0) ? (boolean) (true) : var12), (((var38 & 4096) != 0) ? (boolean) (false) : var13), (((var38 & 8192) != 0) ? (boolean) (false) : var14), (((var38 & 16384) != 0) ? (long) (60L) : var15), (((var38 & 32768) != 0) ? (int) (100) : var17), (((var38 & 65536) != 0) ? (long) (300L) : var18), (((var38 & 131072) != 0) ? (long) (1L) : var20), (((var38 & 262144) != 0) ? (long) (20L) : var22), (((var38 & 524288) != 0) ? (NexusConfig$Builder) (new NexusConfig$Builder()) : var24), (((var38 & 1048576) != 0) ? (String) ("") : var25), (((var38 & 2097152) != 0) ? (Set) (SetsKt.emptySet()) : var26), (((var38 & 4194304) != 0) ? (Set) (SetsKt.emptySet()) : var27), (((var38 & 8388608) != 0) ? (boolean) (false) : var28), (((var38 & 16777216) != 0) ? (boolean) (true) : var29), (((var38 & 33554432) != 0) ? (boolean) (false) : var30), (((var38 & 67108864) != 0) ? (boolean) (false) : var31), (((var38 & 134217728) != 0) ? (boolean) (false) : var32), (((var38 & 268435456) != 0) ? (ConfigModules) (null) : var33), (((var38 & 536870912) != 0) ? (AttachmentSettings) (AttachmentSettings.Companion.getDEFAULT()) : var34), (((var38 & 1073741824) != 0) ? (boolean) (true) : var35), (((var38 & -2147483648) != 0) ? (ConversationStateSyncSettings) (ConversationStateSyncSettings.Companion.getDEFAULT()) : var36), (((var39 & 1) != 0) ? (CrashReportingConfig) (new CrashReportingConfig(false, 1, null)) : var37));
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   @Nullable
   public final String getPrimaryColor() {
      return this.primaryColor;
   }

   @Nullable
   public final String getSecondaryColor() {
      return this.secondaryColor;
   }

   @NotNull
   public final String getLocale() {
      return this.locale;
   }

   @NotNull
   public final String getHelpCenterLocale() {
      return this.helpCenterLocale;
   }

   @NotNull
   public final String getLauncherLogoUrl() {
      return this.launcherLogoUrl;
   }

   @NotNull
   public final String getTeamGreeting() {
      return this.teamGreeting;
   }

   public final boolean isFirstRequest() {
      return this.isFirstRequest;
   }

   public final boolean isInboundMessages() {
      return this.isInboundMessages;
   }

   public final boolean isAudioEnabled() {
      return this.isAudioEnabled;
   }

   public final boolean isMetricsEnabled() {
      return this.isMetricsEnabled;
   }

   public final boolean isBackgroundRequestsEnabled() {
      return this.isBackgroundRequestsEnabled;
   }

   public final boolean isPrimaryColorRenderDarkText() {
      return this.isPrimaryColorRenderDarkText;
   }

   public final boolean isSecondaryColorRenderDarkText() {
      return this.isSecondaryColorRenderDarkText;
   }

   public final int getRateLimitCount() {
      return this.rateLimitCount;
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

   public final boolean getIdentityVerificationEnabled() {
      return this.identityVerificationEnabled;
   }

   public final boolean getAccessToTeammateEnabled() {
      return this.accessToTeammateEnabled;
   }

   public final boolean getHelpCenterRequireSearch() {
      return this.helpCenterRequireSearch;
   }

   public final boolean getPreventMultipleInboundConversationsEnabled() {
      return this.preventMultipleInboundConversationsEnabled;
   }

   public final boolean getHasOpenConversations() {
      return this.hasOpenConversations;
   }

   @Nullable
   public final ConfigModules getModules() {
      return this.modules;
   }

   @NotNull
   public final AttachmentSettings getAttachmentSettings() {
      return this.attachmentSettings;
   }

   public final boolean isArticleAutoReactionEnabled() {
      return this.isArticleAutoReactionEnabled;
   }

   @NotNull
   public final ConversationStateSyncSettings getConversationStateSyncSettings() {
      return this.conversationStateSyncSettings;
   }

   @Nullable
   public final CrashReportingConfig getCrashReportingConfig() {
      return this.crashReportingConfig;
   }

   public final long getRateLimitPeriod() {
      return TimeUnit.SECONDS.toMillis(this._rateLimitPeriodSeconds);
   }

   public final long getUserUpdateCacheMaxAge() {
      return TimeUnit.SECONDS.toMillis(this._userUpdateCacheMaxAgeSeconds);
   }

   public final long getSoftResetTimeout() {
      return TimeUnit.SECONDS.toMillis(this._softResetTimeoutSeconds);
   }

   public final long getNewSessionThreshold() {
      return TimeUnit.SECONDS.toMillis(this._newSessionThresholdSeconds);
   }

   @NotNull
   public final NexusConfig getRealTimeConfig() {
      NexusConfig var10000 = this._realTimeConfigBuilder.build();
      Intrinsics.checkNotNullExpressionValue(var10000, "build(...)");
      return var10000;
   }

   @NotNull
   public final String component1() {
      return this.name;
   }

   @Nullable
   public final String component2() {
      return this.primaryColor;
   }

   @Nullable
   public final String component3() {
      return this.secondaryColor;
   }

   @NotNull
   public final String component4() {
      return this.locale;
   }

   @NotNull
   public final String component5() {
      return this.helpCenterLocale;
   }

   @NotNull
   public final String component6() {
      return this.launcherLogoUrl;
   }

   @NotNull
   public final String component7() {
      return this.teamGreeting;
   }

   public final boolean component8() {
      return this.isFirstRequest;
   }

   public final boolean component9() {
      return this.isInboundMessages;
   }

   public final boolean component10() {
      return this.isAudioEnabled;
   }

   public final boolean component11() {
      return this.isMetricsEnabled;
   }

   public final boolean component12() {
      return this.isBackgroundRequestsEnabled;
   }

   public final boolean component13() {
      return this.isPrimaryColorRenderDarkText;
   }

   public final boolean component14() {
      return this.isSecondaryColorRenderDarkText;
   }

   private final long component15() {
      return this._rateLimitPeriodSeconds;
   }

   public final int component16() {
      return this.rateLimitCount;
   }

   private final long component17() {
      return this._userUpdateCacheMaxAgeSeconds;
   }

   private final long component18() {
      return this._softResetTimeoutSeconds;
   }

   private final long component19() {
      return this._newSessionThresholdSeconds;
   }

   private final NexusConfig$Builder component20() {
      return this._realTimeConfigBuilder;
   }

   @NotNull
   public final String component21() {
      return this.helpCenterUrl;
   }

   @NotNull
   public final Set component22() {
      return this.helpCenterUrls;
   }

   @NotNull
   public final Set component23() {
      return this.features;
   }

   public final boolean component24() {
      return this.identityVerificationEnabled;
   }

   public final boolean component25() {
      return this.accessToTeammateEnabled;
   }

   public final boolean component26() {
      return this.helpCenterRequireSearch;
   }

   public final boolean component27() {
      return this.preventMultipleInboundConversationsEnabled;
   }

   public final boolean component28() {
      return this.hasOpenConversations;
   }

   @Nullable
   public final ConfigModules component29() {
      return this.modules;
   }

   @NotNull
   public final AttachmentSettings component30() {
      return this.attachmentSettings;
   }

   public final boolean component31() {
      return this.isArticleAutoReactionEnabled;
   }

   @NotNull
   public final ConversationStateSyncSettings component32() {
      return this.conversationStateSyncSettings;
   }

   @Nullable
   public final CrashReportingConfig component33() {
      return this.crashReportingConfig;
   }

   @NotNull
   public final Config copy(
      @NotNull String var1,
      @Nullable String var2,
      @Nullable String var3,
      @NotNull String var4,
      @NotNull String var5,
      @NotNull String var6,
      @NotNull String var7,
      boolean var8,
      boolean var9,
      boolean var10,
      boolean var11,
      boolean var12,
      boolean var13,
      boolean var14,
      long var15,
      int var17,
      long var18,
      long var20,
      long var22,
      @NotNull NexusConfig$Builder var24,
      @NotNull String var25,
      @NotNull Set var26,
      @NotNull Set var27,
      boolean var28,
      boolean var29,
      boolean var30,
      boolean var31,
      boolean var32,
      @Nullable ConfigModules var33,
      @NotNull AttachmentSettings var34,
      boolean var35,
      @NotNull ConversationStateSyncSettings var36,
      @Nullable CrashReportingConfig var37
   ) {
      Intrinsics.checkNotNullParameter(var1, "name");
      Intrinsics.checkNotNullParameter(var4, "locale");
      Intrinsics.checkNotNullParameter(var5, "helpCenterLocale");
      Intrinsics.checkNotNullParameter(var6, "launcherLogoUrl");
      Intrinsics.checkNotNullParameter(var7, "teamGreeting");
      Intrinsics.checkNotNullParameter(var24, "_realTimeConfigBuilder");
      Intrinsics.checkNotNullParameter(var25, "helpCenterUrl");
      Intrinsics.checkNotNullParameter(var26, "helpCenterUrls");
      Intrinsics.checkNotNullParameter(var27, "features");
      Intrinsics.checkNotNullParameter(var34, "attachmentSettings");
      Intrinsics.checkNotNullParameter(var36, "conversationStateSyncSettings");
      return new Config(
         var1,
         var2,
         var3,
         var4,
         var5,
         var6,
         var7,
         var8,
         var9,
         var10,
         var11,
         var12,
         var13,
         var14,
         var15,
         var17,
         var18,
         var20,
         var22,
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
         var37
      );
   }

   // $VF: synthetic method
   public static Config copy$default(
      Config var0,
      String var1,
      String var2,
      String var3,
      String var4,
      String var5,
      String var6,
      String var7,
      boolean var8,
      boolean var9,
      boolean var10,
      boolean var11,
      boolean var12,
      boolean var13,
      boolean var14,
      long var15,
      int var17,
      long var18,
      long var20,
      long var22,
      NexusConfig$Builder var24,
      String var25,
      Set var26,
      Set var27,
      boolean var28,
      boolean var29,
      boolean var30,
      boolean var31,
      boolean var32,
      ConfigModules var33,
      AttachmentSettings var34,
      boolean var35,
      ConversationStateSyncSettings var36,
      CrashReportingConfig var37,
      int var38,
      int var39,
      Object var40
   ) {
      if ((var38 & 1) != 0) {
         var1 = var0.name;
      }

      if ((var38 & 2) != 0) {
         var2 = var0.primaryColor;
      }

      if ((var38 & 4) != 0) {
         var3 = var0.secondaryColor;
      }

      if ((var38 & 8) != 0) {
         var4 = var0.locale;
      }

      if ((var38 & 16) != 0) {
         var5 = var0.helpCenterLocale;
      }

      if ((var38 & 32) != 0) {
         var6 = var0.launcherLogoUrl;
      }

      if ((var38 & 64) != 0) {
         var7 = var0.teamGreeting;
      }

      if ((var38 & 128) != 0) {
         var8 = var0.isFirstRequest;
      }

      if ((var38 & 256) != 0) {
         var9 = var0.isInboundMessages;
      }

      if ((var38 & 512) != 0) {
         var10 = var0.isAudioEnabled;
      }

      if ((var38 & 1024) != 0) {
         var11 = var0.isMetricsEnabled;
      }

      if ((var38 & 2048) != 0) {
         var12 = var0.isBackgroundRequestsEnabled;
      }

      if ((var38 & 4096) != 0) {
         var13 = var0.isPrimaryColorRenderDarkText;
      }

      if ((var38 & 8192) != 0) {
         var14 = var0.isSecondaryColorRenderDarkText;
      }

      if ((var38 & 16384) != 0) {
         var15 = var0._rateLimitPeriodSeconds;
      }

      if ((var38 & 32768) != 0) {
         var17 = var0.rateLimitCount;
      }

      if ((var38 & 65536) != 0) {
         var18 = var0._userUpdateCacheMaxAgeSeconds;
      }

      if ((var38 & 131072) != 0) {
         var20 = var0._softResetTimeoutSeconds;
      }

      if ((var38 & 262144) != 0) {
         var22 = var0._newSessionThresholdSeconds;
      }

      if ((var38 & 524288) != 0) {
         var24 = var0._realTimeConfigBuilder;
      }

      if ((var38 & 1048576) != 0) {
         var25 = var0.helpCenterUrl;
      }

      if ((var38 & 2097152) != 0) {
         var26 = var0.helpCenterUrls;
      }

      if ((var38 & 4194304) != 0) {
         var27 = var0.features;
      }

      if ((var38 & 8388608) != 0) {
         var28 = var0.identityVerificationEnabled;
      }

      if ((var38 & 16777216) != 0) {
         var29 = var0.accessToTeammateEnabled;
      }

      if ((var38 & 33554432) != 0) {
         var30 = var0.helpCenterRequireSearch;
      }

      if ((var38 & 67108864) != 0) {
         var31 = var0.preventMultipleInboundConversationsEnabled;
      }

      if ((var38 & 134217728) != 0) {
         var32 = var0.hasOpenConversations;
      }

      if ((var38 & 268435456) != 0) {
         var33 = var0.modules;
      }

      if ((var38 & 536870912) != 0) {
         var34 = var0.attachmentSettings;
      }

      if ((var38 & 1073741824) != 0) {
         var35 = var0.isArticleAutoReactionEnabled;
      }

      if ((var38 & -2147483648) != 0) {
         var36 = var0.conversationStateSyncSettings;
      }

      if ((var39 & 1) != 0) {
         var37 = var0.crashReportingConfig;
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
         var10,
         var11,
         var12,
         var13,
         var14,
         var15,
         var17,
         var18,
         var20,
         var22,
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
         var37
      );
   }

   @NotNull
   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("Config(name=")
         .append(this.name)
         .append(", primaryColor=")
         .append(this.primaryColor)
         .append(", secondaryColor=")
         .append(this.secondaryColor)
         .append(", locale=")
         .append(this.locale)
         .append(", helpCenterLocale=")
         .append(this.helpCenterLocale)
         .append(", launcherLogoUrl=")
         .append(this.launcherLogoUrl)
         .append(", teamGreeting=")
         .append(this.teamGreeting)
         .append(", isFirstRequest=")
         .append(this.isFirstRequest)
         .append(", isInboundMessages=")
         .append(this.isInboundMessages)
         .append(", isAudioEnabled=")
         .append(this.isAudioEnabled)
         .append(", isMetricsEnabled=")
         .append(this.isMetricsEnabled)
         .append(", isBackgroundRequestsEnabled=");
      var1.append(this.isBackgroundRequestsEnabled)
         .append(", isPrimaryColorRenderDarkText=")
         .append(this.isPrimaryColorRenderDarkText)
         .append(", isSecondaryColorRenderDarkText=")
         .append(this.isSecondaryColorRenderDarkText)
         .append(", _rateLimitPeriodSeconds=")
         .append(this._rateLimitPeriodSeconds)
         .append(", rateLimitCount=")
         .append(this.rateLimitCount)
         .append(", _userUpdateCacheMaxAgeSeconds=")
         .append(this._userUpdateCacheMaxAgeSeconds)
         .append(", _softResetTimeoutSeconds=")
         .append(this._softResetTimeoutSeconds)
         .append(", _newSessionThresholdSeconds=")
         .append(this._newSessionThresholdSeconds)
         .append(", _realTimeConfigBuilder=")
         .append(this._realTimeConfigBuilder)
         .append(", helpCenterUrl=")
         .append(this.helpCenterUrl)
         .append(", helpCenterUrls=")
         .append(this.helpCenterUrls)
         .append(", features=")
         .append(this.features);
      var1.append(", identityVerificationEnabled=")
         .append(this.identityVerificationEnabled)
         .append(", accessToTeammateEnabled=")
         .append(this.accessToTeammateEnabled)
         .append(", helpCenterRequireSearch=")
         .append(this.helpCenterRequireSearch)
         .append(", preventMultipleInboundConversationsEnabled=")
         .append(this.preventMultipleInboundConversationsEnabled)
         .append(", hasOpenConversations=")
         .append(this.hasOpenConversations)
         .append(", modules=")
         .append(this.modules)
         .append(", attachmentSettings=")
         .append(this.attachmentSettings)
         .append(", isArticleAutoReactionEnabled=")
         .append(this.isArticleAutoReactionEnabled)
         .append(", conversationStateSyncSettings=")
         .append(this.conversationStateSyncSettings)
         .append(", crashReportingConfig=")
         .append(this.crashReportingConfig)
         .append(')');
      return var1.toString();
   }

   @Override
   public int hashCode() {
      int var1 = this.name.hashCode();
      var1 = var1 * 31 + (this.primaryColor == null ? 0 : this.primaryColor.hashCode());
      var1 = var1 * 31 + (this.secondaryColor == null ? 0 : this.secondaryColor.hashCode());
      var1 = var1 * 31 + this.locale.hashCode();
      var1 = var1 * 31 + this.helpCenterLocale.hashCode();
      var1 = var1 * 31 + this.launcherLogoUrl.hashCode();
      var1 = var1 * 31 + this.teamGreeting.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.isFirstRequest);
      var1 = var1 * 31 + Boolean.hashCode(this.isInboundMessages);
      var1 = var1 * 31 + Boolean.hashCode(this.isAudioEnabled);
      var1 = var1 * 31 + Boolean.hashCode(this.isMetricsEnabled);
      var1 = var1 * 31 + Boolean.hashCode(this.isBackgroundRequestsEnabled);
      var1 = var1 * 31 + Boolean.hashCode(this.isPrimaryColorRenderDarkText);
      var1 = var1 * 31 + Boolean.hashCode(this.isSecondaryColorRenderDarkText);
      var1 = var1 * 31 + Long.hashCode(this._rateLimitPeriodSeconds);
      var1 = var1 * 31 + Integer.hashCode(this.rateLimitCount);
      var1 = var1 * 31 + Long.hashCode(this._userUpdateCacheMaxAgeSeconds);
      var1 = var1 * 31 + Long.hashCode(this._softResetTimeoutSeconds);
      var1 = var1 * 31 + Long.hashCode(this._newSessionThresholdSeconds);
      var1 = var1 * 31 + this._realTimeConfigBuilder.hashCode();
      var1 = var1 * 31 + this.helpCenterUrl.hashCode();
      var1 = var1 * 31 + this.helpCenterUrls.hashCode();
      var1 = var1 * 31 + this.features.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.identityVerificationEnabled);
      var1 = var1 * 31 + Boolean.hashCode(this.accessToTeammateEnabled);
      var1 = var1 * 31 + Boolean.hashCode(this.helpCenterRequireSearch);
      var1 = var1 * 31 + Boolean.hashCode(this.preventMultipleInboundConversationsEnabled);
      var1 = var1 * 31 + Boolean.hashCode(this.hasOpenConversations);
      var1 = var1 * 31 + (this.modules == null ? 0 : this.modules.hashCode());
      var1 = var1 * 31 + this.attachmentSettings.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.isArticleAutoReactionEnabled);
      var1 = var1 * 31 + this.conversationStateSyncSettings.hashCode();
      return var1 * 31 + (this.crashReportingConfig == null ? 0 : this.crashReportingConfig.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Config)) {
         return false;
      } else {
         Config var2 = (Config)var1;
         if (!Intrinsics.areEqual(this.name, var2.name)) {
            return false;
         } else if (!Intrinsics.areEqual(this.primaryColor, var2.primaryColor)) {
            return false;
         } else if (!Intrinsics.areEqual(this.secondaryColor, var2.secondaryColor)) {
            return false;
         } else if (!Intrinsics.areEqual(this.locale, var2.locale)) {
            return false;
         } else if (!Intrinsics.areEqual(this.helpCenterLocale, var2.helpCenterLocale)) {
            return false;
         } else if (!Intrinsics.areEqual(this.launcherLogoUrl, var2.launcherLogoUrl)) {
            return false;
         } else if (!Intrinsics.areEqual(this.teamGreeting, var2.teamGreeting)) {
            return false;
         } else if (this.isFirstRequest != var2.isFirstRequest) {
            return false;
         } else if (this.isInboundMessages != var2.isInboundMessages) {
            return false;
         } else if (this.isAudioEnabled != var2.isAudioEnabled) {
            return false;
         } else if (this.isMetricsEnabled != var2.isMetricsEnabled) {
            return false;
         } else if (this.isBackgroundRequestsEnabled != var2.isBackgroundRequestsEnabled) {
            return false;
         } else if (this.isPrimaryColorRenderDarkText != var2.isPrimaryColorRenderDarkText) {
            return false;
         } else if (this.isSecondaryColorRenderDarkText != var2.isSecondaryColorRenderDarkText) {
            return false;
         } else if (this._rateLimitPeriodSeconds != var2._rateLimitPeriodSeconds) {
            return false;
         } else if (this.rateLimitCount != var2.rateLimitCount) {
            return false;
         } else if (this._userUpdateCacheMaxAgeSeconds != var2._userUpdateCacheMaxAgeSeconds) {
            return false;
         } else if (this._softResetTimeoutSeconds != var2._softResetTimeoutSeconds) {
            return false;
         } else if (this._newSessionThresholdSeconds != var2._newSessionThresholdSeconds) {
            return false;
         } else if (!Intrinsics.areEqual(this._realTimeConfigBuilder, var2._realTimeConfigBuilder)) {
            return false;
         } else if (!Intrinsics.areEqual(this.helpCenterUrl, var2.helpCenterUrl)) {
            return false;
         } else if (!Intrinsics.areEqual(this.helpCenterUrls, var2.helpCenterUrls)) {
            return false;
         } else if (!Intrinsics.areEqual(this.features, var2.features)) {
            return false;
         } else if (this.identityVerificationEnabled != var2.identityVerificationEnabled) {
            return false;
         } else if (this.accessToTeammateEnabled != var2.accessToTeammateEnabled) {
            return false;
         } else if (this.helpCenterRequireSearch != var2.helpCenterRequireSearch) {
            return false;
         } else if (this.preventMultipleInboundConversationsEnabled != var2.preventMultipleInboundConversationsEnabled) {
            return false;
         } else if (this.hasOpenConversations != var2.hasOpenConversations) {
            return false;
         } else if (!Intrinsics.areEqual(this.modules, var2.modules)) {
            return false;
         } else if (!Intrinsics.areEqual(this.attachmentSettings, var2.attachmentSettings)) {
            return false;
         } else if (this.isArticleAutoReactionEnabled != var2.isArticleAutoReactionEnabled) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.conversationStateSyncSettings, var2.conversationStateSyncSettings)
               ? false
               : Intrinsics.areEqual(this.crashReportingConfig, var2.crashReportingConfig);
         }
      }
   }

   public Config() {
      this(
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         0L,
         0,
         0L,
         0L,
         0L,
         null,
         null,
         null,
         null,
         false,
         false,
         false,
         false,
         false,
         null,
         null,
         false,
         null,
         null,
         -1,
         1,
         null
      );
   }

   // $VF: synthetic method
   public static final Config access$getNULL$cp() {
      return NULL;
   }
}
