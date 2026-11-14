package io.intercom.android.sdk;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.VisibleForTesting;
import androidx.compose.runtime.internal.StabilityInferred;
import com.intercom.twig.Twig;
import io.intercom.android.nexus.NexusClient;
import io.intercom.android.sdk.activities.IntercomCarouselActivity;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.api.DeDuper;
import io.intercom.android.sdk.api.UserUpdateRequest;
import io.intercom.android.sdk.api.UserUpdater;
import io.intercom.android.sdk.errorreporting.ErrorReporter;
import io.intercom.android.sdk.helpcenter.api.CollectionContentRequestCallback;
import io.intercom.android.sdk.helpcenter.api.CollectionRequestCallback;
import io.intercom.android.sdk.helpcenter.api.HelpCenterApiWrapper;
import io.intercom.android.sdk.helpcenter.api.SearchRequestCallback;
import io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity;
import io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity$ArticleActivityArguments;
import io.intercom.android.sdk.helpcenter.utils.HelpCenterUrlUtilsKt;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.identity.Registration;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.identity.UserJwtRefresher;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.ConversationScreenOpenerKt;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.m5.data.UnreadCountTracker;
import io.intercom.android.sdk.m5.navigation.IntercomRootActivityLauncher;
import io.intercom.android.sdk.m5.push.IntercomPushClientHandler;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.models.HostAppState;
import io.intercom.android.sdk.overlay.LauncherOpenBehaviour;
import io.intercom.android.sdk.overlay.LauncherOpenBehaviour$LauncherType;
import io.intercom.android.sdk.overlay.OverlayPresenter;
import io.intercom.android.sdk.survey.ui.IntercomSurveyActivity;
import io.intercom.android.sdk.ui.theme.ThemeManager;
import io.intercom.android.sdk.ui.theme.ThemeMode;
import io.intercom.android.sdk.user.UserUpdaterHelper;
import io.intercom.android.sdk.utilities.ActivityFinisher;
import io.intercom.android.sdk.utilities.AttributeSanitiser;
import io.intercom.android.sdk.utilities.ValidatorUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ReplaceWith;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u008c\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\b\u0015\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\b\u0007\u0018\u0000 \u0091\u00012\u00020\u0001:\b\u008e\u0001\u008f\u0001\u0090\u0001\u0091\u0001B©\u0001\b\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u0010\u0012\u0006\u0010\u0011\u001a\u00020\u0012\u0012\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\u0005\u0012\u0006\u0010\u0015\u001a\u00020\u0016\u0012\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u0005\u0012\u0006\u0010\u0019\u001a\u00020\u001a\u0012\u0006\u0010\u001b\u001a\u00020\u001c\u0012\u0006\u0010\u001d\u001a\u00020\u001e\u0012\u0006\u0010\u001f\u001a\u00020 \u0012\u0006\u0010!\u001a\u00020\"\u0012\u0006\u0010#\u001a\u00020$¢\u0006\u0004\b%\u0010&J\b\u0010'\u001a\u00020(H\u0002J\u0012\u0010)\u001a\u00020*2\b\b\u0002\u0010+\u001a\u00020,H\u0007J\u000e\u0010-\u001a\u00020*2\u0006\u0010.\u001a\u00020/J\b\u00100\u001a\u00020*H\u0007J\u0010\u00101\u001a\u00020*2\b\b\u0002\u00102\u001a\u000203J\u0012\u00104\u001a\u00020*2\b\u00105\u001a\u0004\u0018\u000106H\u0007J\u0018\u00107\u001a\u00020*2\u0006\u00105\u001a\u0002062\b\b\u0002\u00102\u001a\u000203J\u0018\u00108\u001a\u00020*2\u0006\u00105\u001a\u0002062\u0006\u00102\u001a\u000203H\u0002J\u0006\u00109\u001a\u00020(J\b\u0010:\u001a\u0004\u0018\u000106J\b\u0010;\u001a\u00020*H\u0002J\u000e\u0010<\u001a\u00020*2\u0006\u0010=\u001a\u00020>J\u000e\u0010?\u001a\u00020*2\u0006\u0010@\u001a\u00020>J\u000e\u0010A\u001a\u00020*2\u0006\u0010B\u001a\u00020CJ\u0006\u0010D\u001a\u00020*J\u0010\u0010E\u001a\u00020*2\b\u0010F\u001a\u0004\u0018\u00010>J\u001e\u0010G\u001a\u00020*2\f\u0010H\u001a\b\u0012\u0004\u0012\u00020J0I2\b\b\u0002\u00102\u001a\u000203J\u0012\u0010K\u001a\u00020*2\b\u0010L\u001a\u0004\u0018\u00010MH\u0007J\u0018\u0010K\u001a\u00020*2\u0006\u0010L\u001a\u00020M2\b\b\u0002\u00102\u001a\u000203J&\u0010N\u001a\u00020*2\u0014\u0010L\u001a\u0010\u0012\u0004\u0012\u00020>\u0012\u0006\u0012\u0004\u0018\u00010\u00010O2\u0006\u00102\u001a\u000203H\u0002J\u000e\u0010P\u001a\u00020*2\u0006\u0010Q\u001a\u00020>J$\u0010P\u001a\u00020*2\b\u0010Q\u001a\u0004\u0018\u00010>2\u0012\u0010R\u001a\u000e\u0012\u0004\u0012\u00020>\u0012\u0002\b\u0003\u0018\u00010OJ$\u0010S\u001a\u00020*2\b\u0010Q\u001a\u0004\u0018\u00010>2\u0010\u0010T\u001a\f\u0012\u0004\u0012\u00020>\u0012\u0002\b\u00030OH\u0002J\b\u0010U\u001a\u00020*H\u0007J\u0006\u0010V\u001a\u00020*J\u0010\u0010V\u001a\u00020*2\b\u0010W\u001a\u0004\u0018\u00010>J\b\u0010X\u001a\u00020(H\u0002J\u0010\u0010Y\u001a\u00020*2\u0006\u0010Z\u001a\u00020>H\u0002J\b\u0010[\u001a\u00020*H\u0007J\b\u0010\\\u001a\u00020*H\u0007J\u001a\u0010]\u001a\u00020*2\u0010\u0010^\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010>\u0018\u00010IH\u0007J\u0012\u0010_\u001a\u00020*2\b\u0010`\u001a\u0004\u0018\u00010>H\u0007J\u0010\u0010a\u001a\u00020*2\u0006\u0010b\u001a\u00020>H\u0007J\u000e\u0010c\u001a\u00020*2\u0006\u0010d\u001a\u00020eJ\u0014\u0010f\u001a\u00020*2\f\u0010g\u001a\b\u0012\u0004\u0012\u00020h0IJ\u0010\u0010i\u001a\u00020*2\b\u0010j\u001a\u0004\u0018\u00010kJ\u0010\u0010l\u001a\u00020*2\b\u0010j\u001a\u0004\u0018\u00010kJ\u0010\u0010m\u001a\u00020*2\b\u0010n\u001a\u0004\u0018\u00010oJ\u0006\u0010p\u001a\u00020*J\b\u0010q\u001a\u00020*H\u0007J%\u0010r\u001a\u00020*2\u0006\u0010@\u001a\u00020>2\u0006\u0010s\u001a\u00020>2\u0006\u0010t\u001a\u00020>H\u0000¢\u0006\u0002\buJ\u0006\u0010v\u001a\u00020*J\u000e\u0010z\u001a\u00020*2\u0006\u0010{\u001a\u00020|J\u0010\u0010}\u001a\u00020*2\b\u0010{\u001a\u0004\u0018\u00010|J\u0010\u0010~\u001a\u00020*2\u0006\u0010\u007f\u001a\u00020>H\u0007J\u0013\u0010\u0080\u0001\u001a\u00020*2\n\u0010\u0081\u0001\u001a\u0005\u0018\u00010\u0082\u0001J\u001e\u0010\u0083\u0001\u001a\u00020*2\t\u0010\u0084\u0001\u001a\u0004\u0018\u00010>2\n\u0010\u0085\u0001\u001a\u0005\u0018\u00010\u0086\u0001J\u001e\u0010\u0087\u0001\u001a\u00020*2\t\u0010\u0088\u0001\u001a\u0004\u0018\u00010>2\n\u0010\u0089\u0001\u001a\u0005\u0018\u00010\u008a\u0001J\u001d\u0010\u008b\u0001\u001a\u00020*2\t\u0010\u008c\u0001\u001a\u0004\u0018\u00010>2\t\u0010\u008d\u0001\u001a\u0004\u0018\u00010>R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u001aX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u001cX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u001eX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020 X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\"X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020$X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010w\u001a\u00020e8F¢\u0006\u0006\u001a\u0004\bx\u0010y¨\u0006\u0092\u0001",
   d2 = {
         "Lio/intercom/android/sdk/Intercom;",
         "",
         "superDeDuper",
         "Lio/intercom/android/sdk/api/DeDuper;",
         "apiProvider",
         "Lio/intercom/android/sdk/Provider;",
         "Lio/intercom/android/sdk/api/Api;",
         "nexusClientProvider",
         "Lio/intercom/android/nexus/NexusClient;",
         "unreadCountTracker",
         "Lio/intercom/android/sdk/m5/data/UnreadCountTracker;",
         "metricTracker",
         "Lio/intercom/android/sdk/metrics/MetricTracker;",
         "errorReporter",
         "Lio/intercom/android/sdk/errorreporting/ErrorReporter;",
         "context",
         "Landroid/app/Application;",
         "overlayPresenter",
         "Lio/intercom/android/sdk/overlay/OverlayPresenter;",
         "appConfigProvider",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "userIdentity",
         "Lio/intercom/android/sdk/identity/UserIdentity;",
         "userUpdater",
         "Lio/intercom/android/sdk/api/UserUpdater;",
         "resetManager",
         "Lio/intercom/android/sdk/ResetManager;",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "activityFinisher",
         "Lio/intercom/android/sdk/utilities/ActivityFinisher;",
         "dataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "userUpdaterHelper",
         "Lio/intercom/android/sdk/user/UserUpdaterHelper;",
         "userJwtRefresher",
         "Lio/intercom/android/sdk/identity/UserJwtRefresher;",
         "<init>",
         "(Lio/intercom/android/sdk/api/DeDuper;Lio/intercom/android/sdk/Provider;Lio/intercom/android/sdk/Provider;Lio/intercom/android/sdk/m5/data/UnreadCountTracker;Lio/intercom/android/sdk/metrics/MetricTracker;Lio/intercom/android/sdk/errorreporting/ErrorReporter;Landroid/app/Application;Lio/intercom/android/sdk/overlay/OverlayPresenter;Lio/intercom/android/sdk/Provider;Lio/intercom/android/sdk/identity/UserIdentity;Lio/intercom/android/sdk/Provider;Lio/intercom/android/sdk/ResetManager;Lcom/intercom/twig/Twig;Lio/intercom/android/sdk/utilities/ActivityFinisher;Lio/intercom/android/sdk/m5/data/IntercomDataLayer;Lio/intercom/android/sdk/user/UserUpdaterHelper;Lio/intercom/android/sdk/identity/UserJwtRefresher;)V",
         "shouldDisableErrorReporting",
         "",
         "present",
         "",
         "space",
         "Lio/intercom/android/sdk/IntercomSpace;",
         "presentContent",
         "content",
         "Lio/intercom/android/sdk/IntercomContent;",
         "registerUnidentifiedUser",
         "loginUnidentifiedUser",
         "intercomStatusCallback",
         "Lio/intercom/android/sdk/IntercomStatusCallback;",
         "registerIdentifiedUser",
         "userRegistration",
         "Lio/intercom/android/sdk/identity/Registration;",
         "loginIdentifiedUser",
         "registerNewIdentifiedUser",
         "isUserLoggedIn",
         "fetchLoggedInUserAttributes",
         "softRegister",
         "setUserHash",
         "userHash",
         "",
         "setUserJwt",
         "jwt",
         "setUserJwtProvider",
         "provider",
         "Lio/intercom/android/sdk/IntercomUserJwtProvider;",
         "removeUserJwtProvider",
         "setMessengerEnvironment",
         "environment",
         "setAuthTokens",
         "authTokens",
         "",
         "Lio/intercom/android/sdk/AuthToken;",
         "updateUser",
         "userAttributes",
         "Lio/intercom/android/sdk/UserAttributes;",
         "performUpdate",
         "",
         "logEvent",
         "name",
         "metaData",
         "logEventWithValidation",
         "metadata",
         "displayMessenger",
         "displayMessageComposer",
         "initialMessage",
         "noUserRegistered",
         "logErrorAndOpenInbox",
         "error",
         "displayConversationsList",
         "displayHelpCenter",
         "displayHelpCenterCollections",
         "collectionIds",
         "displayCarousel",
         "carouselId",
         "displaySurvey",
         "surveyId",
         "setBottomPadding",
         "bottomPadding",
         "",
         "suppressProactiveContent",
         "contentTypes",
         "Lio/intercom/android/sdk/Intercom$ContentType;",
         "setInAppMessageVisibility",
         "visibility",
         "Lio/intercom/android/sdk/Intercom$Visibility;",
         "setLauncherVisibility",
         "setThemeMode",
         "themeMode",
         "Lio/intercom/android/sdk/ui/theme/ThemeMode;",
         "hideIntercom",
         "reset",
         "applyRefreshedUserJwt",
         "fingerprint",
         "replacing",
         "applyRefreshedUserJwt$intercom_sdk_base_release",
         "logout",
         "unreadConversationCount",
         "getUnreadConversationCount",
         "()I",
         "addUnreadConversationCountListener",
         "listener",
         "Lio/intercom/android/sdk/UnreadConversationCountListener;",
         "removeUnreadConversationCountListener",
         "displayArticle",
         "articleId",
         "fetchHelpCenterCollections",
         "collectionRequestCallback",
         "Lio/intercom/android/sdk/helpcenter/api/CollectionRequestCallback;",
         "fetchHelpCenterCollection",
         "collectionId",
         "collectionContentRequestCallback",
         "Lio/intercom/android/sdk/helpcenter/api/CollectionContentRequestCallback;",
         "searchHelpCenter",
         "searchTerm",
         "searchRequestCallback",
         "Lio/intercom/android/sdk/helpcenter/api/SearchRequestCallback;",
         "changeWorkspace",
         "apiKey",
         "appId",
         "Visibility",
         "ContentType",
         "LogLevel",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nIntercom.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Intercom.kt\nio/intercom/android/sdk/Intercom\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,1311:1\n1#2:1312\n774#3:1313\n865#3,2:1314\n*S KotlinDebug\n*F\n+ 1 Intercom.kt\nio/intercom/android/sdk/Intercom\n*L\n538#1:1313\n538#1:1314,2\n*E\n"
)
public final class Intercom {
   @NotNull
   public static final Intercom$Companion Companion = new Intercom$Companion(null);
   @NotNull
   private final DeDuper superDeDuper;
   @NotNull
   private final Provider apiProvider;
   @NotNull
   private final Provider nexusClientProvider;
   @NotNull
   private final UnreadCountTracker unreadCountTracker;
   @NotNull
   private final MetricTracker metricTracker;
   @NotNull
   private final Application context;
   @NotNull
   private final OverlayPresenter overlayPresenter;
   @NotNull
   private final Provider appConfigProvider;
   @NotNull
   private final UserIdentity userIdentity;
   @NotNull
   private final Provider userUpdater;
   @NotNull
   private final ResetManager resetManager;
   @NotNull
   private final Twig twig;
   @NotNull
   private final ActivityFinisher activityFinisher;
   @NotNull
   private final IntercomDataLayer dataLayer;
   @NotNull
   private final UserUpdaterHelper userUpdaterHelper;
   @NotNull
   private final UserJwtRefresher userJwtRefresher;
   public static final int $stable;

   static { $stable = 8; }
   @JvmField
   @NotNull
   public static final Intercom$Visibility VISIBLE = Intercom$Visibility.VISIBLE;
   @JvmField
   @NotNull
   public static final Intercom$Visibility GONE = Intercom$Visibility.GONE;
   @NotNull
   public static final String PUSH_RECEIVER = "intercom_sdk";
   private static final Twig TWIG = LumberMill.getLogger();
   @SuppressLint("StaticFieldLeak")
   @Nullable
   private static Intercom instance;

   @VisibleForTesting
   public Intercom(
      @NotNull DeDuper var1,
      @NotNull Provider var2,
      @NotNull Provider var3,
      @NotNull UnreadCountTracker var4,
      @NotNull MetricTracker var5,
      @NotNull ErrorReporter var6,
      @NotNull Application var7,
      @NotNull OverlayPresenter var8,
      @NotNull Provider var9,
      @NotNull UserIdentity var10,
      @NotNull Provider var11,
      @NotNull ResetManager var12,
      @NotNull Twig var13,
      @NotNull ActivityFinisher var14,
      @NotNull IntercomDataLayer var15,
      @NotNull UserUpdaterHelper var16,
      @NotNull UserJwtRefresher var17
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "superDeDuper");
      Intrinsics.checkNotNullParameter(var2, "apiProvider");
      Intrinsics.checkNotNullParameter(var3, "nexusClientProvider");
      Intrinsics.checkNotNullParameter(var4, "unreadCountTracker");
      Intrinsics.checkNotNullParameter(var5, "metricTracker");
      Intrinsics.checkNotNullParameter(var6, "errorReporter");
      Intrinsics.checkNotNullParameter(var7, "context");
      Intrinsics.checkNotNullParameter(var8, "overlayPresenter");
      Intrinsics.checkNotNullParameter(var9, "appConfigProvider");
      Intrinsics.checkNotNullParameter(var10, "userIdentity");
      Intrinsics.checkNotNullParameter(var11, "userUpdater");
      Intrinsics.checkNotNullParameter(var12, "resetManager");
      Intrinsics.checkNotNullParameter(var13, "twig");
      Intrinsics.checkNotNullParameter(var14, "activityFinisher");
      Intrinsics.checkNotNullParameter(var15, "dataLayer");
      Intrinsics.checkNotNullParameter(var16, "userUpdaterHelper");
      Intrinsics.checkNotNullParameter(var17, "userJwtRefresher");
      this.superDeDuper = var1;
      this.apiProvider = var2;
      this.nexusClientProvider = var3;
      this.unreadCountTracker = var4;
      this.metricTracker = var5;
      this.context = var7;
      this.overlayPresenter = var8;
      this.appConfigProvider = var9;
      this.userIdentity = var10;
      this.userUpdater = var11;
      this.resetManager = var12;
      this.twig = var13;
      this.activityFinisher = var14;
      this.dataLayer = var15;
      this.userUpdaterHelper = var16;
      this.userJwtRefresher = var17;
      IntercomPushManager$IntercomPushIntegrationType var10000 = IntercomPushManager.getInstalledModuleType();
      if ((var10000 == null ? -1 : Intercom$WhenMappings.$EnumSwitchMapping$0[var10000.ordinal()]) == 1) {
         this.twig.i("Enabling FCM for cloud messaging");
         IntercomPushClientHandler.Companion.setUpNotificationChannels(this.context);
         IntercomPushManager.initializeFcmService(this.context);
      } else {
         this.twig.internal("No FCM integration detected");
      }

      if (this.shouldDisableErrorReporting()) {
         var6.disableExceptionHandler();
      } else {
         var6.enableExceptionHandler();
      }
   }

   private final boolean shouldDisableErrorReporting() {
      return !((AppConfig)this.appConfigProvider.get()).isCrashReportingEnabled();
   }

   @JvmOverloads
   public final void present(@NotNull IntercomSpace var1) {
      Intrinsics.checkNotNullParameter(var1, "space");
      if (this.noUserRegistered()) {
         TWIG.e(
            "Intercom was presented but there was no user registered on this device.Please call registerUnidentifiedUser() or registerIdentifiedUser(Registration)."
         );
      } else {
         switch (var1) {
            case HelpCenter:
               HelpCenterUrlUtilsKt.openCollections(this.context, CollectionsKt.emptyList(), "api");
               break;
            case Home:
               IntercomRootActivityLauncher.INSTANCE.startHome(this.context);
               break;
            case Messages:
               IntercomRootActivityLauncher.INSTANCE.startMessages(this.context);
               break;
            case Tickets:
               IntercomRootActivityLauncher.INSTANCE.startTickets(this.context);
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }
      }
   }

   // $VF: synthetic method
   public static void present$default(Intercom var0, IntercomSpace var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = IntercomSpace.Home;
      }

      var0.present(var1);
   }

   public final void presentContent(@NotNull IntercomContent var1) {
      Intrinsics.checkNotNullParameter(var1, "content");
      IntercomContent var2 = var1;
      if (var2 instanceof IntercomContent$Article) {
         this.context
            .startActivity(
               IntercomArticleActivity.Companion
                  .buildIntent(
                     this.context, new IntercomArticleActivity$ArticleActivityArguments(((IntercomContent$Article)var1).getId(), "api", false, false, 12, null)
                  )
            );
      } else if (var2 instanceof IntercomContent$Carousel) {
         this.context.startActivity(IntercomCarouselActivity.buildIntent(this.context, ((IntercomContent$Carousel)var1).getId()));
      } else if (var2 instanceof IntercomContent$Survey) {
         this.context.startActivity(IntercomSurveyActivity.Companion.buildIntent(this.context, ((IntercomContent$Survey)var1).getId()));
      } else if (var2 instanceof IntercomContent$HelpCenterCollections) {
         HelpCenterUrlUtilsKt.openCollections(this.context, ((IntercomContent$HelpCenterCollections)var1).getIds(), "api");
      } else if (var2 instanceof IntercomContent$Conversation) {
         ConversationScreenOpenerKt.openComposer$default(this.context, "", true, null, ((IntercomContent$Conversation)var1).getId(), null, 40, null);
      } else {
         if (!(var2 instanceof IntercomContent$Ticket)) {
            throw new NoWhenBranchMatchedException();
         }

         IntercomRootActivityLauncher.INSTANCE.startTicketDetails(this.context, ((IntercomContent$Ticket)var1).getId(), "from_programmatic");
      }
   }

   /** @deprecated */
   @Deprecated(
      message = "Use loginUnidentifiedUser instead.",
      replaceWith = @ReplaceWith(expression = "loginUnidentifiedUser()", imports = {}),
      level = DeprecationLevel.WARNING
   )
   public final void registerUnidentifiedUser() {
      this.loginUnidentifiedUser(IntercomStatusCallbackKt.getNoStatusCallback());
   }

   public final void loginUnidentifiedUser(@NotNull IntercomStatusCallback var1) {
      Intrinsics.checkNotNullParameter(var1, "intercomStatusCallback");
      if (this.userIdentity.isUnidentified()) {
         this.twig.i("The unidentified user is already registered");
         var1.onSuccess();
      } else if (this.userIdentity.isIdentified()) {
         this.twig.e("The identified user is already registered");
         var1.onFailure(
            new IntercomError(
               3002,
               "Failed to register user. We already have a registered user. If you are attempting to register a new user, call logout() before this. If you are attempting to register an identified user call: registerIdentifiedUser(Registration)"
            )
         );
      } else {
         this.resetManager.hardReset();
         this.userIdentity.registerUnidentifiedUser();
         ((NexusClient)this.nexusClientProvider.get()).disconnect();
         boolean var2 = ((HostAppState)this.dataLayer.getHostAppState().getValue()).isBackgrounded();
         boolean var3 = !((HostAppState)this.dataLayer.getHostAppState().getValue()).getSessionStartedSinceLastBackgrounded();
         UserUpdateRequest var4 = UserUpdateRequest.create(var3, var2, true);
         UserUpdater var10000 = (UserUpdater)this.userUpdater.get();
         Intrinsics.checkNotNull(var4);
         var10000.updateUser(var4, var1);
      }
   }

   // $VF: synthetic method
   public static void loginUnidentifiedUser$default(Intercom var0, IntercomStatusCallback var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = IntercomStatusCallbackKt.getNoStatusCallback();
      }

      var0.loginUnidentifiedUser(var1);
   }

   /** @deprecated */
   @Deprecated(
      message = "Use loginIdentifiedUser with callback instead.",
      replaceWith = @ReplaceWith(expression = "loginIdentifiedUser(userRegistration)", imports = {}),
      level = DeprecationLevel.WARNING
   )
   public final void registerIdentifiedUser(@Nullable Registration var1) {
      if (var1 == null) {
         this.twig.e("The registration object passed is null. An example successful call is registerIdentifiedUser(Registration.create().withEmail(email));");
      } else {
         this.loginIdentifiedUser(var1, IntercomStatusCallbackKt.getNoStatusCallback());
      }
   }

   public final void loginIdentifiedUser(@NotNull Registration var1, @NotNull IntercomStatusCallback var2) {
      Intrinsics.checkNotNullParameter(var1, "userRegistration");
      Intrinsics.checkNotNullParameter(var2, "intercomStatusCallback");
      String var10000 = var1.getUserId();
      Intrinsics.checkNotNullExpressionValue(var10000, "getUserId(...)");
      if (var10000.length() == 0) {
         var10000 = var1.getEmail();
         Intrinsics.checkNotNullExpressionValue(var10000, "getEmail(...)");
         if (var10000.length() == 0) {
            this.twig.e("Failed to register user. You need to provide a user identifier, an email address, or both. ");
            var2.onFailure(new IntercomError(1007, "Failed to register user. You need to provide unique user identifier, an email address, or both."));
            return;
         }
      }

      if (!this.userIdentity.identityExists()) {
         if (this.userIdentity.isSoftReset() && this.userIdentity.softIdentityIsSameUser(var1)) {
            this.softRegister();
            var2.onSuccess();
         } else {
            this.registerNewIdentifiedUser(var1, var2);
         }
      } else {
         if (!Intrinsics.areEqual(var1.getEmail(), this.userIdentity.getEmail()) || !Intrinsics.areEqual(var1.getUserId(), this.userIdentity.getUserId())) {
            this.twig.i("The user is already registered with a different email or user id");
            this.registerNewIdentifiedUser(var1, var2);
         } else if (this.userIdentity.registrationHasAttributes(var1)) {
            Map var4;
            label45: {
               this.twig.i("We already have a registered user. Updating this user with the attributes provided.");
               UserAttributes var10001 = var1.getAttributes();
               if (var10001 != null) {
                  var4 = var10001.toMap();
                  if (var4 != null) {
                     break label45;
                  }
               }

               var4 = MapsKt.emptyMap();
            }

            this.performUpdate(var4, var2);
         } else {
            this.twig.i("The user is already registered with the same email and user id, skipping API call");
            var2.onSuccess();
         }
      }
   }

   // $VF: synthetic method
   public static void loginIdentifiedUser$default(Intercom var0, Registration var1, IntercomStatusCallback var2, int var3, Object var4) {
      if ((var3 & 2) != 0) {
         var2 = IntercomStatusCallbackKt.getNoStatusCallback();
      }

      var0.loginIdentifiedUser(var1, var2);
   }

   private final void registerNewIdentifiedUser(Registration var1, IntercomStatusCallback var2) {
      this.resetManager.hardReset();
      this.userIdentity.registerIdentifiedUser(var1);
      ((NexusClient)this.nexusClientProvider.get()).disconnect();
      boolean var3 = !((HostAppState)this.dataLayer.getHostAppState().getValue()).getSessionStartedSinceLastBackgrounded();
      boolean var4 = ((HostAppState)this.dataLayer.getHostAppState().getValue()).isBackgrounded();
      UserUpdateRequest var10000;
      if (var1.getAttributes() != null) {
         UserAttributes var10002 = var1.getAttributes();
         Intrinsics.checkNotNull(var10002);
         UserUpdateRequest var6 = UserUpdateRequest.create(var3, var4, var10002.toMap(), true);
         Intrinsics.checkNotNull(var6);
         var10000 = var6;
      } else {
         UserUpdateRequest var7 = UserUpdateRequest.create(var3, var4, true);
         Intrinsics.checkNotNull(var7);
         var10000 = var7;
      }

      UserUpdateRequest var5 = var10000;
      ((UserUpdater)this.userUpdater.get()).updateUser(var5, var2);
   }

   public final boolean isUserLoggedIn() {
      return this.userIdentity.identityExists();
   }

   @Nullable
   public final Registration fetchLoggedInUserAttributes() {
      UserIdentity var2 = this.userIdentity;
      UserIdentity var3 = var2;
      int var4 = 0;
      UserIdentity var1 = var3.identityExists() ? var2 : null;
      if (var1 == null) {
         return null;
      }

      var3 = var1;
      var4 = 0;
      Registration var5 = new Registration();
      CharSequence var6 = var3.getUserId();
      if (var6 != null && var6.length() != 0) {
         var5.withUserId(var3.getUserId());
      }

      var6 = var3.getEmail();
      if (var6 != null && var6.length() != 0) {
         var5.withEmail(var3.getEmail());
      }

      var6 = var3.getAnonymousId();
      if (var6 != null && var6.length() != 0) {
         var5.withUserId(var3.getAnonymousId());
      }

      return var5;
   }

   private final void softRegister() {
      ((NexusClient)this.nexusClientProvider.get()).connect(((AppConfig)this.appConfigProvider.get()).getRealTimeConfig(), true);
      this.userIdentity.softRestart();
      this.resetManager.clear();
      final io.intercom.android.sdk.Intercom recoveredCapture_30_0 = this;
      Runnable var1 = ((java.lang.Runnable) () -> Intercom.softRegister$lambda$2(recoveredCapture_30_0));
      if (Intrinsics.areEqual(Looper.myLooper(), Looper.getMainLooper())) {
         var1.run();
      } else {
         new Handler(Looper.getMainLooper()).post(var1);
      }
   }

   public final void setUserHash(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "userHash");
      if (var1.length() == 0) {
         this.twig.w("The user hash you sent us to verify was empty, we will not be able to authenticate your requests without a valid user hash.");
      } else if (Intrinsics.areEqual(this.userIdentity.getHmac(), var1)) {
         this.twig.i("The user hash set matches the existing user identity hash value");
      } else {
         if (this.userIdentity.softUserIdentityHmacDiffers(var1)) {
            this.resetManager.hardReset();
         }

         this.userIdentity.setUserHash(var1);
         this.userUpdaterHelper.retryOwedSessionStart();
      }
   }

   public final void setUserJwt(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "jwt");
      if (var1.length() == 0) {
         this.twig.w("The JWT you sent us to verify was empty, we will not be able to authenticate your requests without a valid JWT.");
      } else if (Intrinsics.areEqual(this.userIdentity.getJwt(), var1)) {
         this.twig.i("The JWT set matches the existing user identity JWT value");
      } else {
         if (this.userIdentity.softUserIdentityJwtDiffers(var1)) {
            this.resetManager.hardReset();
         }

         this.userIdentity.setJwt(var1);
         this.userJwtRefresher.onUserJwtSet(var1);
         this.dataLayer.userJwtChanged();
         this.userUpdaterHelper.retryOwedSessionStart();
      }
   }

   public final void setUserJwtProvider(@NotNull IntercomUserJwtProvider var1) {
      Intrinsics.checkNotNullParameter(var1, "provider");
      this.userJwtRefresher.setProvider(var1);
   }

   public final void removeUserJwtProvider() {
      this.userJwtRefresher.setProvider(null);
   }

   public final void setMessengerEnvironment(@Nullable String var1) {
      String var10001 = var1 != null ? StringsKt.trim(var1).toString() : null;
      if (var10001 == null) {
         var10001 = "";
      }

      this.userIdentity.setMessengerEnvironment(var10001);
   }

   public final void setAuthTokens(@NotNull List var1, @NotNull IntercomStatusCallback var2) {
      Intrinsics.checkNotNullParameter(var1, "authTokens");
      Intrinsics.checkNotNullParameter(var2, "intercomStatusCallback");
      Iterable var4 = var1;
      int var5 = 0;
      Iterable var6 = var4;
      ArrayList var7 = new ArrayList();
      int var8 = 0;

      for (Object var10 : var6) {
         AuthToken var11 = (AuthToken)var10;
         int var12 = 0;
         if (var11.getName().length() > 0 && var11.getToken().length() > 0) {
            var7.add(var10);
         }
      }

      List var3 = (List)var7;
      if (var3.isEmpty()) {
         this.twig.e("All auth tokens in the map have empty keys or values.");
         var2.onFailure(new IntercomError(3003, "ERROR - Failed to set Auth Tokens. All keys or values passed were empty."));
      } else {
         ((Api)this.apiProvider.get()).setAuthTokens(var3, var2);
      }
   }

   // $VF: synthetic method
   public static void setAuthTokens$default(Intercom var0, List var1, IntercomStatusCallback var2, int var3, Object var4) {
      if ((var3 & 2) != 0) {
         var2 = IntercomStatusCallbackKt.getNoStatusCallback();
      }

      var0.setAuthTokens(var1, var2);
   }

   /** @deprecated */
   @Deprecated(
      message = "Use updateUser with callback instead.",
      replaceWith = @ReplaceWith(expression = "updateUser(userAttributes, statusCallback)", imports = {}),
      level = DeprecationLevel.WARNING
   )
   public final void updateUser(@Nullable UserAttributes var1) {
      if (var1 == null) {
         LumberMill.getLogger().e("updateUser method failed: the UserAttributes object provided is null");
      } else {
         this.updateUser(var1, IntercomStatusCallbackKt.getNoStatusCallback());
      }
   }

   public final void updateUser(@NotNull UserAttributes var1, @NotNull IntercomStatusCallback var2) {
      Intrinsics.checkNotNullParameter(var1, "userAttributes");
      Intrinsics.checkNotNullParameter(var2, "intercomStatusCallback");
      Map var10001 = var1.toMap();
      Intrinsics.checkNotNullExpressionValue(var10001, "toMap(...)");
      this.performUpdate(var10001, var2);
   }

   // $VF: synthetic method
   public static void updateUser$default(Intercom var0, UserAttributes var1, IntercomStatusCallback var2, int var3, Object var4) {
      if ((var3 & 2) != 0) {
         var2 = IntercomStatusCallbackKt.getNoStatusCallback();
      }

      var0.updateUser(var1, var2);
   }

   private final void performUpdate(Map var1, IntercomStatusCallback var2) {
      if (this.userIdentity.isUnidentified()) {
         AttributeSanitiser.anonymousSanitisation(var1);
      }

      if (this.superDeDuper.shouldUpdateUser(var1)) {
         this.superDeDuper.update(var1);
         boolean var3 = ((HostAppState)this.dataLayer.getHostAppState().getValue()).isBackgrounded();
         UserUpdateRequest var4 = UserUpdateRequest.create(false, var3, var1, false);
         UserUpdater var10000 = (UserUpdater)this.userUpdater.get();
         Intrinsics.checkNotNull(var4);
         var10000.updateUser(var4, var2);
         this.twig.internal("dupe", "updated user");
      } else {
         var2.onSuccess();
         this.twig.internal("dupe", "dropped dupe");
      }
   }

   public final void logEvent(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "name");
      this.logEventWithValidation(var1, MapsKt.emptyMap());
   }

   public final void logEvent(@Nullable String var1, @Nullable Map var2) {
      if (var2 == null) {
         this.twig.i("The metadata provided is null, logging event with no metadata");
      } else if (var2.isEmpty()) {
         this.twig.i("The metadata provided is empty, logging event with no metadata");
      }

      Map var10002 = var2 != null ? MapsKt.toMap(var2) : null;
      if (var10002 == null) {
         var10002 = MapsKt.emptyMap();
      }

      this.logEventWithValidation(var1, var10002);
   }

   private final void logEventWithValidation(String var1, Map var2) {
      CharSequence var3 = var1;
      if (var3 == null || var3.length() == 0) {
         this.twig.e("The event name is null or empty. We can't log an event with this string.");
      } else {
         ((Api)this.apiProvider.get()).logEvent(var1, var2);
      }
   }

   /** @deprecated */
   @Deprecated(message = "Use present() instead.")
   public final void displayMessenger() {
      if (this.noUserRegistered()) {
         this.logErrorAndOpenInbox(
            "The messenger was opened but there was no user registered on this device.Please call registerUnidentifiedUser() or registerIdentifiedUser(Registration)."
         );
      } else {
         new LauncherOpenBehaviour().openMessenger(this.context);
      }
   }

   public final void displayMessageComposer() {
      this.displayMessageComposer("");
   }

   public final void displayMessageComposer(@Nullable String var1) {
      if (this.noUserRegistered()) {
         this.logErrorAndOpenInbox(
            "The messenger was opened but there was no user registered on this device. Please call registerUnidentifiedUser() or registerIdentifiedUser(Registration)."
         );
      } else if (!((AppConfig)this.appConfigProvider.get()).isReceivedFromServer()) {
         this.logErrorAndOpenInbox(
            "It appears your app has not received a successful response from Intercom. Please check if you are using the correct Android app ID and API Key from the Intercom settings."
         );
      } else if (!((AppConfig)this.appConfigProvider.get()).isInboundMessages()) {
         this.logErrorAndOpenInbox(
            "It appears your app is not on a plan that allows message composing. As a fallback we are calling present(IntercomSpace.Messages)"
         );
      } else {
         this.metricTracker.openedMessengerNewConversation(LauncherOpenBehaviour$LauncherType.CUSTOM);
         Context var10000 = this.context;
         String var10001 = var1;
         if (var1 == null) {
            var10001 = "";
         }

         ConversationScreenOpenerKt.openComposer$default(var10000, var10001, true, null, null, null, 56, null);
      }
   }

   private final boolean noUserRegistered() {
      return !this.userIdentity.identityExists() || this.userIdentity.isSoftReset();
   }

   private final void logErrorAndOpenInbox(String var1) {
      this.twig.e(var1);
      this.present(IntercomSpace.Messages);
   }

   /** @deprecated */
   @Deprecated(message = "Use present(IntercomSpace.Messages) instead.")
   public final void displayConversationsList() {
      this.metricTracker.openedMessengerConversationList(LauncherOpenBehaviour$LauncherType.CUSTOM);
      IntercomRootActivityLauncher.INSTANCE.startMessages(this.context);
   }

   /** @deprecated */
   @Deprecated(message = "Use present(IntercomSpace.HelpCenter) instead.")
   public final void displayHelpCenter() {
      this.displayHelpCenterCollections(CollectionsKt.emptyList());
   }

   /** @deprecated */
   @Deprecated(message = "Use presentContent(IntercomContent.HelpCenterCollections(ids)) instead.")
   public final void displayHelpCenterCollections(@Nullable List var1) {
      Context var10000 = this.context;
      List var10001 = var1 != null ? CollectionsKt.filterNotNull(var1) : null;
      if (var10001 == null) {
         var10001 = CollectionsKt.emptyList();
      }

      HelpCenterUrlUtilsKt.openCollections(var10000, var10001, "api");
   }

   /** @deprecated */
   @Deprecated(message = "Use presentContent(IntercomContent.Carousel(id)) instead.")
   public final void displayCarousel(@Nullable String var1) {
      this.context.startActivity(IntercomCarouselActivity.buildIntent(this.context, var1));
   }

   /** @deprecated */
   @Deprecated(message = "Use presentContent(IntercomContent.Survey(id)) instead.")
   public final void displaySurvey(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "surveyId");
      this.context.startActivity(IntercomSurveyActivity.Companion.buildIntent(this.context, var1));
   }

   public final void setBottomPadding(int var1) {
      this.dataLayer.updateBottomPadding(var1);
   }

   public final void suppressProactiveContent(@NotNull List var1) {
      Intrinsics.checkNotNullParameter(var1, "contentTypes");
      this.dataLayer.updateContentVisibility(!var1.contains(Intercom$ContentType.CAROUSEL), !var1.contains(Intercom$ContentType.SURVEY));
      if (!this.noUserRegistered()) {
         boolean var2 = ((HostAppState)this.dataLayer.getHostAppState().getValue()).isBackgrounded();
         UserUpdater var10000 = (UserUpdater)this.userUpdater.get();
         UserUpdateRequest var10001 = UserUpdateRequest.create(false, var2, true);
         Intrinsics.checkNotNullExpressionValue(var10001, "create(...)");
         var10000.updateUser(var10001, IntercomStatusCallbackKt.getNoStatusCallback());
      }
   }

   public final void setInAppMessageVisibility(@Nullable Intercom$Visibility var1) {
      if (var1 != null) {
         this.dataLayer.updateInAppNotificationsVisibility(var1);
      } else {
         this.twig.i("The visibility provided is null, visibility won't be updated");
      }
   }

   public final void setLauncherVisibility(@Nullable Intercom$Visibility var1) {
      if (var1 != null) {
         this.dataLayer.updateLauncherVisibility(var1);
      } else {
         this.twig.i("The visibility provided is null, visibility won't be updated");
      }
   }

   public final void setThemeMode(@Nullable ThemeMode var1) {
      if (var1 != null) {
         ThemeManager.INSTANCE.setSessionOverride(var1);
         this.twig.i("Theme mode set to: " + var1 + " (session only)");
      } else {
         ThemeManager.INSTANCE.clearSessionOverride();
         this.twig.i("Theme mode cleared, using server-provided theme");
      }
   }

   public final void hideIntercom() {
      this.activityFinisher.finishActivities();
   }

   /** @deprecated */
   @Deprecated(message = "call {@link #logout()} instead")
   public final void reset() {
      this.logout();
   }

   // $VF: Extended synchronized range to monitorexit
   public final void applyRefreshedUserJwt$intercom_sdk_base_release(@NotNull String var1, @NotNull String var2, @NotNull String var3) {
      Intrinsics.checkNotNullParameter(var1, "jwt");
      Intrinsics.checkNotNullParameter(var2, "fingerprint");
      Intrinsics.checkNotNullParameter(var3, "replacing");
      boolean var10000;
      synchronized (this.userIdentity) {
         int var6 = 0;
         boolean var7 = Intrinsics.areEqual(this.userIdentity.getFingerprint(), var2) && Intrinsics.areEqual(this.userIdentity.getJwt(), var3);
         boolean var8 = var7;
         int var9 = 0;
         if (var8) {
            this.userIdentity.setJwt(var1);
         }

         boolean var11 = var7;
         var10000 = var11;
      }

      boolean var4 = var10000;
      if (var4) {
         this.dataLayer.userJwtChanged();
         this.userUpdaterHelper.retryOwedSessionStart();
      }
   }

   public final void logout() {
      if (!this.userIdentity.isSoftReset()) {
         this.resetManager.softReset();
      }
   }

   public final int getUnreadConversationCount() {
      return ((Set)this.dataLayer.getUnreadConversationIds().getValue()).size();
   }

   public final void addUnreadConversationCountListener(@NotNull UnreadConversationCountListener var1) {
      Intrinsics.checkNotNullParameter(var1, "listener");
      UnreadCountTracker.addListener$default(this.unreadCountTracker, var1, null, 2, null);
   }

   public final void removeUnreadConversationCountListener(@Nullable UnreadConversationCountListener var1) {
      if (var1 != null) {
         UnreadConversationCountListener var2 = var1;
         UnreadCountTracker var3 = this.unreadCountTracker;
         UnreadConversationCountListener var4 = var2;
         int var5 = 0;
         int var6 = 0;
         var3.removeListener(var4);
      }
   }

   /** @deprecated */
   @Deprecated(message = "Use presentContent(IntercomContent.Article(id)) instead.")
   public final void displayArticle(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "articleId");
      Intent var2 = IntercomArticleActivity.Companion
         .buildIntent(this.context, new IntercomArticleActivity$ArticleActivityArguments(var1, "api", false, false, 12, null));
      this.context.startActivity(var2);
   }

   public final void fetchHelpCenterCollections(@Nullable CollectionRequestCallback var1) {
      HelpCenterApiWrapper var10000 = HelpCenterApiWrapper.INSTANCE;
      MetricTracker var10001 = this.metricTracker;
      Intrinsics.checkNotNull(var1);
      var10000.fetchHelpCenterCollections(var10001, var1);
   }

   public final void fetchHelpCenterCollection(@Nullable String var1, @Nullable CollectionContentRequestCallback var2) {
      HelpCenterApiWrapper var10000 = HelpCenterApiWrapper.INSTANCE;
      MetricTracker var10001 = this.metricTracker;
      Intrinsics.checkNotNull(var1);
      Intrinsics.checkNotNull(var2);
      var10000.fetchHelpCenterCollection(var10001, var1, var2);
   }

   public final void searchHelpCenter(@Nullable String var1, @Nullable SearchRequestCallback var2) {
      HelpCenterApiWrapper var10000 = HelpCenterApiWrapper.INSTANCE;
      MetricTracker var10001 = this.metricTracker;
      Intrinsics.checkNotNull(var1);
      Intrinsics.checkNotNull(var2);
      var10000.fetchHelpCenterResultsForSearchTerm(var10001, var1, var2);
   }

   public final void changeWorkspace(@Nullable String var1, @Nullable String var2) {
      if (var1 != null && var2 != null && ValidatorUtil.isValidAppIdParams(var1, var2)) {
         this.logout();
         Injector.get().changeWorkspace(var1, var2);
      } else {
         TWIG.e("Workspace has not been changed. Please make sure that you're passing in the correct app ID and API key");
      }
   }

   @JvmOverloads
   public final void present() {
      present$default(this, null, 1, null);
   }

   private static final void softRegister$lambda$2(Intercom var0) {
      var0.overlayPresenter.refreshStateBecauseUserIdentityIsNotInStoreYet();
   }

   @JvmStatic
   public static final synchronized void initialize(@Nullable Application var0, @Nullable String var1, @Nullable String var2) {
      Companion.initialize(var0, var1, var2);
   }

   /** @deprecated */
   @JvmStatic
   @Deprecated(message = "Use initialize() instead. SDK will not communicate with Intercom until a user registration is made.")
   public static final synchronized void registerForLaterInitialisation(@Nullable Application var0) {
      Companion.registerForLaterInitialisation(var0);
   }

   /** @deprecated */
   @JvmStatic
   @Deprecated(message = "Use initialize() instead. SDK will not communicate with Intercom until a user registration is made.")
   public static final void unregisterForLateInitialisation(@Nullable Application var0) {
      Companion.unregisterForLateInitialisation(var0);
   }

   @JvmStatic
   @NotNull
   public static final synchronized Intercom client() {
      return Companion.client();
   }

   @JvmStatic
   public static final void setLogLevel(@Intercom$LogLevel int var0) {
      Companion.setLogLevel(var0);
   }

   // $VF: synthetic method
   public static final Intercom access$getInstance$cp() {
      return instance;
   }

   // $VF: synthetic method
   public static final Twig access$getTWIG$cp() {
      return TWIG;
   }

   // $VF: synthetic method
   public static final void access$setInstance$cp(Intercom var0) {
      instance = var0;
   }
}
