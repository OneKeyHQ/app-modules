package io.intercom.android.sdk.m5.data;

import android.app.Activity;
import androidx.annotation.ColorInt;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.nexus.NexusConfig;
import io.intercom.android.sdk.Intercom$Visibility;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.identity.AppConfigKt;
import io.intercom.android.sdk.identity.AppConfigStore;
import io.intercom.android.sdk.models.AppLifecycleState;
import io.intercom.android.sdk.models.Config;
import io.intercom.android.sdk.models.ConfigModules;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.CustomizationModel;
import io.intercom.android.sdk.models.DeliveryOption;
import io.intercom.android.sdk.models.HostAppState;
import io.intercom.android.sdk.models.OpenMessengerResponse;
import io.intercom.android.sdk.models.OverlayState;
import io.intercom.android.sdk.models.TeamPresence;
import io.intercom.android.sdk.models.Ticket;
import io.intercom.android.sdk.models.carousel.Carousel;
import io.intercom.android.sdk.survey.model.SurveyData;
import io.intercom.android.sdk.ui.theme.ThemeManager;
import io.intercom.android.sdk.ui.theme.ThemeMode;
import io.intercom.android.sdk.utilities.ActivityUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.channels.BufferOverflow;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.flow.MutableSharedFlow;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.SharedFlow;
import kotlinx.coroutines.flow.SharedFlowKt;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.flow.StateFlowKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000Ú\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0002\b\f\b\u0001\u0018\u0000 \u008e\u00012\u00020\u0001:\u0002\u008e\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0014\u0010B\u001a\u00020/2\f\u0010C\u001a\b\u0012\u0004\u0012\u00020\u000b0\nJ\u000e\u0010D\u001a\u00020/2\u0006\u0010\u0012\u001a\u00020\u0011J\u000e\u0010E\u001a\u00020/2\u0006\u0010\u0016\u001a\u00020\u0015J\u000e\u0010F\u001a\u00020/2\u0006\u0010\u001a\u001a\u00020\u0019J\u0006\u0010G\u001a\u00020/J\u000e\u0010H\u001a\u00020/2\u0006\u0010I\u001a\u00020JJ\u0006\u0010K\u001a\u00020/J\u000e\u0010L\u001a\u00020/2\u0006\u0010M\u001a\u00020NJ\u000e\u0010O\u001a\u00020/2\u0006\u0010P\u001a\u00020QJ\u000e\u0010R\u001a\u00020/2\u0006\u0010P\u001a\u00020QJ\u0016\u0010S\u001a\u00020/2\u0006\u0010T\u001a\u0002092\u0006\u0010U\u001a\u000209J\u0014\u0010V\u001a\u00020/2\f\u0010)\u001a\b\u0012\u0004\u0012\u00020(0'J\u000e\u0010W\u001a\u00020/2\u0006\u0010>\u001a\u00020XJ\u0006\u0010Y\u001a\u00020/J\u000e\u0010Z\u001a\u00020/2\u0006\u0010[\u001a\u000209J\u000e\u0010\\\u001a\u00020/2\u0006\u0010]\u001a\u00020(J\u0016\u0010^\u001a\u00020/2\u0006\u0010]\u001a\u00020(2\u0006\u0010_\u001a\u00020(J\u000e\u0010`\u001a\u00020/2\u0006\u0010a\u001a\u00020\u000bJ\u000e\u0010b\u001a\u00020/2\u0006\u0010c\u001a\u00020(J\u0014\u0010d\u001a\u00020/2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\nJ\u000e\u0010e\u001a\u00020/2\u0006\u0010f\u001a\u00020gJ\u000e\u0010h\u001a\u00020/2\u0006\u0010f\u001a\u00020gJ\u000e\u0010i\u001a\u00020/2\u0006\u0010f\u001a\u00020gJ\u000e\u0010j\u001a\u00020/2\u0006\u0010k\u001a\u00020lJ\u0006\u0010m\u001a\u00020/J\u0017\u0010n\u001a\u0004\u0018\u00010l2\b\b\u0002\u0010o\u001a\u00020p¢\u0006\u0002\u0010qJ\u000e\u0010r\u001a\u00020/2\u0006\u0010s\u001a\u00020lJ\u000e\u0010t\u001a\u0002092\u0006\u0010s\u001a\u00020lJ\u0006\u0010u\u001a\u000209J\b\u0010v\u001a\u0004\u0018\u00010(J\u0006\u0010w\u001a\u00020(J\u0006\u0010x\u001a\u00020/J\u0006\u0010y\u001a\u00020/J\u0006\u0010z\u001a\u00020/J\u0016\u0010{\u001a\u00020/2\u0006\u0010|\u001a\u00020-H\u0086@¢\u0006\u0002\u0010}J\u0010\u0010~\u001a\u00020/2\u0006\u0010\u007f\u001a\u000207H\u0002J&\u0010\u0080\u0001\u001a\u00020/2\u0007\u0010\u0081\u0001\u001a\u00020\u00052\u0014\u0010\u0082\u0001\u001a\u000f\u0012\u0004\u0012\u000207\u0012\u0004\u0012\u00020/0\u0083\u0001J&\u0010\u0084\u0001\u001a\u00020/2\u0007\u0010\u0081\u0001\u001a\u00020\u00052\u0014\u0010\u0085\u0001\u001a\u000f\u0012\u0004\u0012\u00020\u001d\u0012\u0004\u0012\u00020/0\u0083\u0001J,\u0010\u0086\u0001\u001a\u00020/2\u0007\u0010\u0081\u0001\u001a\u00020\u00052\u001a\u0010\u0087\u0001\u001a\u0015\u0012\n\u0012\b\u0012\u0004\u0012\u00020(0'\u0012\u0004\u0012\u00020/0\u0083\u0001J\u001b\u0010{\u001a\u00020/2\t\b\u0002\u0010\u0081\u0001\u001a\u00020\u00052\u0006\u00104\u001a\u00020-H\u0007J&\u0010\u0088\u0001\u001a\u00020/2\u0007\u0010\u0081\u0001\u001a\u00020\u00052\u0014\u0010\u0089\u0001\u001a\u000f\u0012\u0004\u0012\u00020-\u0012\u0004\u0012\u00020/0\u0083\u0001J\u0010\u0010\u008a\u0001\u001a\u00020/2\u0007\u0010\u008b\u0001\u001a\u00020;J\u0007\u0010\u008c\u0001\u001a\u00020/J\t\u0010\u008d\u0001\u001a\u0004\u0018\u00010;R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\n0\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u001d\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\n0\r¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00110\r¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000fR\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00150\r¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u000fR\u0014\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00190\r¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u000fR\u0014\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u001d0\r¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u000fR\u0014\u0010 \u001a\b\u0012\u0004\u0012\u00020!0\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\"\u001a\b\u0012\u0004\u0012\u00020!0\r¢\u0006\b\n\u0000\u001a\u0004\b#\u0010\u000fR\u000e\u0010$\u001a\u00020%X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010&\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020(0'0\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u001d\u0010)\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020(0'0\r¢\u0006\b\n\u0000\u001a\u0004\b*\u0010\u000fR\u0014\u0010+\u001a\b\u0012\u0004\u0012\u00020-0,X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010.\u001a\b\u0012\u0004\u0012\u00020/0,X\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u00100\u001a\b\u0012\u0004\u0012\u00020/01¢\u0006\b\n\u0000\u001a\u0004\b2\u00103R\u0017\u00104\u001a\b\u0012\u0004\u0012\u00020-01¢\u0006\b\n\u0000\u001a\u0004\b5\u00103R\u0014\u00106\u001a\b\u0012\u0004\u0012\u0002070\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u00108\u001a\b\u0012\u0004\u0012\u0002090\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010:\u001a\u0004\u0018\u00010;X\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010<\u001a\b\u0012\u0004\u0012\u00020=0\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u0017\u0010>\u001a\b\u0012\u0004\u0012\u0002070\r¢\u0006\b\n\u0000\u001a\u0004\b?\u0010\u000fR\u0017\u0010@\u001a\b\u0012\u0004\u0012\u0002090\r¢\u0006\b\n\u0000\u001a\u0004\bA\u0010\u000f¨\u0006\u008f\u0001",
   d2 = {
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "",
         "appConfigStore",
         "Lio/intercom/android/sdk/identity/AppConfigStore;",
         "applicationScope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "<init>",
         "(Lio/intercom/android/sdk/identity/AppConfigStore;Lkotlinx/coroutines/CoroutineScope;)V",
         "_conversations",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "",
         "Lio/intercom/android/sdk/models/Conversation;",
         "conversations",
         "Lkotlinx/coroutines/flow/StateFlow;",
         "getConversations",
         "()Lkotlinx/coroutines/flow/StateFlow;",
         "_teamPresence",
         "Lio/intercom/android/sdk/models/TeamPresence;",
         "teamPresence",
         "getTeamPresence",
         "_ticket",
         "Lio/intercom/android/sdk/models/Ticket;",
         "ticket",
         "getTicket",
         "_surveyData",
         "Lio/intercom/android/sdk/survey/model/SurveyData;",
         "surveyData",
         "getSurveyData",
         "_overlayState",
         "Lio/intercom/android/sdk/models/OverlayState;",
         "overlayState",
         "getOverlayState",
         "_hostAppState",
         "Lio/intercom/android/sdk/models/HostAppState;",
         "hostAppState",
         "getHostAppState",
         "sessionStartAttemptIds",
         "Ljava/util/concurrent/atomic/AtomicLong;",
         "_unreadConversationIds",
         "",
         "",
         "unreadConversationIds",
         "getUnreadConversationIds",
         "_event",
         "Lkotlinx/coroutines/flow/MutableSharedFlow;",
         "Lio/intercom/android/sdk/m5/data/IntercomEvent;",
         "_userJwtChanges",
         "",
         "userJwtChanges",
         "Lkotlinx/coroutines/flow/SharedFlow;",
         "getUserJwtChanges",
         "()Lkotlinx/coroutines/flow/SharedFlow;",
         "event",
         "getEvent",
         "_config",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "_pushNotificationsBannerDismissed",
         "",
         "openResponse",
         "Lio/intercom/android/sdk/models/OpenMessengerResponse;",
         "homeCards",
         "Lio/intercom/android/sdk/m5/home/data/HomeCards;",
         "config",
         "getConfig",
         "pushNotificationsBannerDismissed",
         "getPushNotificationsBannerDismissed",
         "addConversations",
         "newConversations",
         "updateTeamPresence",
         "updateTicket",
         "updateSurveyData",
         "clearSurveyData",
         "updateCarousel",
         "carousel",
         "Lio/intercom/android/sdk/models/carousel/Carousel;",
         "clearCarousel",
         "updateBottomPadding",
         "bottomPadding",
         "",
         "updateLauncherVisibility",
         "visibility",
         "Lio/intercom/android/sdk/Intercom$Visibility;",
         "updateInAppNotificationsVisibility",
         "updateContentVisibility",
         "carouselEnabled",
         "surveyEnabled",
         "updateUnreadConversationIds",
         "updateConfig",
         "Lio/intercom/android/sdk/models/Config;",
         "resetConfig",
         "updatePushNotificationsBannerDismissed",
         "dismissed",
         "markConversationAsRead",
         "conversationId",
         "updateConversationTitle",
         "title",
         "fetchConversationSuccess",
         "conversation",
         "markConversationPartAsDismissed",
         "partId",
         "updateOverlayConversations",
         "activityReadyForViewAttachment",
         "activity",
         "Landroid/app/Activity;",
         "activityPaused",
         "activityStopped",
         "appEnteredBackground",
         "timestamp",
         "",
         "appEnteredForeground",
         "claimSessionStart",
         "claim",
         "Lio/intercom/android/sdk/m5/data/SessionStartClaim;",
         "(Lio/intercom/android/sdk/m5/data/SessionStartClaim;)Ljava/lang/Long;",
         "sessionStartSucceeded",
         "attemptId",
         "sessionStartFailed",
         "requestRetryIfSessionStartInFlight",
         "getSessionId",
         "generateSessionId",
         "hardReset",
         "clearUserData",
         "userJwtChanged",
         "emitEvent",
         "intercomEvent",
         "(Lio/intercom/android/sdk/m5/data/IntercomEvent;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "updateAppConfig",
         "newAppConfig",
         "configUpdates",
         "coroutineScope",
         "onNewAppConfig",
         "Lkotlin/Function1;",
         "overlayStateUpdates",
         "onNewOverlyState",
         "unreadConversationIdsUpdates",
         "onNewUnreadConversationsIdsState",
         "listenToEvents",
         "onNewEvent",
         "updateOpenResponse",
         "response",
         "clearOpenResponse",
         "getOpenResponse",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nIntercomDataLayer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomDataLayer.kt\nio/intercom/android/sdk/m5/data/IntercomDataLayer\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,560:1\n230#2,3:561\n233#2,2:573\n230#2,5:575\n230#2,5:580\n230#2,5:585\n230#2,5:590\n230#2,5:595\n230#2,5:600\n230#2,5:605\n230#2,5:610\n230#2,5:615\n230#2,5:620\n230#2,5:625\n230#2,5:630\n230#2,5:635\n230#2,5:640\n230#2,3:645\n233#2,2:651\n230#2,3:653\n233#2,2:659\n230#2,3:661\n233#2,2:668\n230#2,3:673\n233#2,2:680\n230#2,3:682\n233#2,2:688\n230#2,5:690\n230#2,3:695\n233#2,2:704\n230#2,3:706\n233#2,2:718\n230#2,5:720\n230#2,5:725\n230#2,5:730\n230#2,5:735\n230#2,5:740\n230#2,5:745\n230#2,5:750\n230#2,5:755\n230#2,5:760\n230#2,5:765\n230#2,5:770\n230#2,5:775\n230#2,5:780\n230#2,5:785\n230#2,5:790\n230#2,5:795\n230#2,5:800\n1068#3:564\n1669#3,8:565\n827#3:648\n855#3,2:649\n827#3:656\n855#3,2:657\n1563#3:664\n1634#3,3:665\n1761#3,3:670\n1563#3:676\n1634#3,3:677\n827#3:685\n855#3,2:686\n827#3:698\n855#3:699\n1761#3,3:700\n856#3:703\n827#3:709\n855#3:710\n1761#3,3:711\n856#3:714\n827#3:715\n855#3,2:716\n*S KotlinDebug\n*F\n+ 1 IntercomDataLayer.kt\nio/intercom/android/sdk/m5/data/IntercomDataLayer\n*L\n99#1:561,3\n99#1:573,2\n107#1:575,5\n111#1:580,5\n115#1:585,5\n116#1:590,5\n124#1:595,5\n125#1:600,5\n133#1:605,5\n141#1:610,5\n149#1:615,5\n157#1:620,5\n165#1:625,5\n175#1:630,5\n184#1:635,5\n202#1:640,5\n207#1:645,3\n207#1:651,2\n213#1:653,3\n213#1:659,2\n220#1:661,3\n220#1:668,2\n233#1:673,3\n233#1:680,2\n250#1:682,3\n250#1:688,2\n254#1:690,5\n263#1:695,3\n263#1:704,2\n277#1:706,3\n277#1:718,2\n293#1:720,5\n302#1:725,5\n313#1:730,5\n321#1:735,5\n327#1:740,5\n336#1:745,5\n347#1:750,5\n371#1:755,5\n387#1:760,5\n401#1:765,5\n414#1:770,5\n426#1:775,5\n427#1:780,5\n428#1:785,5\n429#1:790,5\n430#1:795,5\n438#1:800,5\n101#1:564\n102#1:565,8\n209#1:648\n209#1:649,2\n214#1:656\n214#1:657,2\n221#1:664\n221#1:665,3\n232#1:670,3\n235#1:676\n235#1:677,3\n251#1:685\n251#1:686,2\n266#1:698\n266#1:699\n268#1:700,3\n266#1:703\n280#1:709\n280#1:710\n282#1:711,3\n280#1:714\n283#1:715\n283#1:716,2\n*E\n"
)
public final class IntercomDataLayer {
   @NotNull
   public static final IntercomDataLayer$Companion Companion = new IntercomDataLayer$Companion(null);
   @NotNull
   private final AppConfigStore appConfigStore;
   @NotNull
   private final CoroutineScope applicationScope;
   @NotNull
   private final MutableStateFlow _conversations;
   @NotNull
   private final StateFlow conversations;
   @NotNull
   private final MutableStateFlow _teamPresence;
   @NotNull
   private final StateFlow teamPresence;
   @NotNull
   private final MutableStateFlow _ticket;
   @NotNull
   private final StateFlow ticket;
   @NotNull
   private final MutableStateFlow _surveyData;
   @NotNull
   private final StateFlow surveyData;
   @NotNull
   private final MutableStateFlow _overlayState;
   @NotNull
   private final StateFlow overlayState;
   @NotNull
   private final MutableStateFlow _hostAppState;
   @NotNull
   private final StateFlow hostAppState;
   @NotNull
   private final AtomicLong sessionStartAttemptIds;
   @NotNull
   private final MutableStateFlow _unreadConversationIds;
   @NotNull
   private final StateFlow unreadConversationIds;
   @NotNull
   private final MutableSharedFlow _event;
   @NotNull
   private final MutableSharedFlow _userJwtChanges;
   @NotNull
   private final SharedFlow userJwtChanges;
   @NotNull
   private final SharedFlow event;
   @NotNull
   private final MutableStateFlow _config;
   @NotNull
   private final MutableStateFlow _pushNotificationsBannerDismissed;
   @Nullable
   private OpenMessengerResponse openResponse;
   @NotNull
   private List homeCards;
   @NotNull
   private final StateFlow config;
   @NotNull
   private final StateFlow pushNotificationsBannerDismissed;
   public static final int $stable;

   static { $stable = 8; }
   @ColorInt
   private static final int DEFAULT_COLOR = -15758352;

   public IntercomDataLayer(@NotNull AppConfigStore var1, @NotNull CoroutineScope var2) {
      Intrinsics.checkNotNullParameter(var1, "appConfigStore");
      Intrinsics.checkNotNullParameter(var2, "applicationScope");
      super();
      this.appConfigStore = var1;
      this.applicationScope = var2;
      this._conversations = StateFlowKt.MutableStateFlow(CollectionsKt.emptyList());
      this.conversations = FlowKt.asStateFlow(this._conversations);
      this._teamPresence = StateFlowKt.MutableStateFlow(TeamPresence.NULL);
      this.teamPresence = FlowKt.asStateFlow(this._teamPresence);
      this._ticket = StateFlowKt.MutableStateFlow(Ticket.Companion.getNULL());
      this.ticket = FlowKt.asStateFlow(this._ticket);
      this._surveyData = StateFlowKt.MutableStateFlow(SurveyData.Companion.getNULL());
      this.surveyData = FlowKt.asStateFlow(this._surveyData);
      this._overlayState = StateFlowKt.MutableStateFlow(OverlayState.NULL);
      this.overlayState = FlowKt.asStateFlow(this._overlayState);
      this._hostAppState = StateFlowKt.MutableStateFlow(HostAppState.NULL);
      this.hostAppState = FlowKt.asStateFlow(this._hostAppState);
      this.sessionStartAttemptIds = new AtomicLong();
      this._unreadConversationIds = StateFlowKt.MutableStateFlow(SetsKt.emptySet());
      this.unreadConversationIds = FlowKt.asStateFlow(this._unreadConversationIds);
      this._event = SharedFlowKt.MutableSharedFlow$default(0, 0, null, 7, null);
      this._userJwtChanges = SharedFlowKt.MutableSharedFlow$default(0, 1, BufferOverflow.DROP_OLDEST, 1, null);
      this.userJwtChanges = FlowKt.asSharedFlow(this._userJwtChanges);
      this.event = this._event;
      this.homeCards = CollectionsKt.emptyList();
      this._config = StateFlowKt.MutableStateFlow(this.appConfigStore.getAppConfig(-15758352));
      this._pushNotificationsBannerDismissed = StateFlowKt.MutableStateFlow(this.appConfigStore.getPushNotificationsBannerDismissed());
      this.config = FlowKt.asStateFlow(this._config);
      this.pushNotificationsBannerDismissed = FlowKt.asStateFlow(this._pushNotificationsBannerDismissed);
   }

   @NotNull
   public final StateFlow getConversations() {
      return this.conversations;
   }

   @NotNull
   public final StateFlow getTeamPresence() {
      return this.teamPresence;
   }

   @NotNull
   public final StateFlow getTicket() {
      return this.ticket;
   }

   @NotNull
   public final StateFlow getSurveyData() {
      return this.surveyData;
   }

   @NotNull
   public final StateFlow getOverlayState() {
      return this.overlayState;
   }

   @NotNull
   public final StateFlow getHostAppState() {
      return this.hostAppState;
   }

   @NotNull
   public final StateFlow getUnreadConversationIds() {
      return this.unreadConversationIds;
   }

   @NotNull
   public final SharedFlow getUserJwtChanges() {
      return this.userJwtChanges;
   }

   @NotNull
   public final SharedFlow getEvent() {
      return this.event;
   }

   @NotNull
   public final StateFlow getConfig() {
      return this.config;
   }

   @NotNull
   public final StateFlow getPushNotificationsBannerDismissed() {
      return this.pushNotificationsBannerDismissed;
   }

   public final void addConversations(@NotNull List var1) {
      Intrinsics.checkNotNullParameter(var1, "newConversations");
      MutableStateFlow var2 = this._conversations;
      int var3 = 0;

      Object var4;
      Object var15;
      do {
         var4 = var2.getValue();
         List var5 = (List)var4;
         int var6 = 0;
         Iterable var7 = CollectionsKt.plus(var1, var5);
         int var8 = 0;
         Iterable var16 = CollectionsKt.sortedWith(
            var7, new IntercomDataLayer$addConversations$lambda$2$RecoveredInnere3b0c44298fc1c14$inlined$sortedByDescending$1()
         );
         int var17 = 0;
         HashSet var9 = new HashSet();
         ArrayList var10 = new ArrayList();

         for (Object var12 : var16) {
            Conversation var13 = (Conversation)var12;
            int var14 = 0;
            Object var18 = var13.getId();
            if (var9.add(var18)) {
               var10.add(var12);
            }
         }

         var15 = var10;
      } while (!var2.compareAndSet(var4, var15));
   }

   public final void updateTeamPresence(@NotNull TeamPresence var1) {
      Intrinsics.checkNotNullParameter(var1, "teamPresence");
      MutableStateFlow var2 = this._teamPresence;
      int var3 = 0;

      Object var4;
      Object var7;
      do {
         var4 = var2.getValue();
         TeamPresence var5 = (TeamPresence)var4;
         int var6 = 0;
         var7 = var1;
      } while (!var2.compareAndSet(var4, var7));
   }

   public final void updateTicket(@NotNull Ticket var1) {
      Intrinsics.checkNotNullParameter(var1, "ticket");
      MutableStateFlow var2 = this._ticket;
      int var3 = 0;

      Object var4;
      Object var7;
      do {
         var4 = var2.getValue();
         Ticket var5 = (Ticket)var4;
         int var6 = 0;
         var7 = var1;
      } while (!var2.compareAndSet(var4, var7));
   }

   public final void updateSurveyData(@NotNull SurveyData var1) {
      Intrinsics.checkNotNullParameter(var1, "surveyData");
      MutableStateFlow var2 = this._surveyData;
      int var3 = 0;

      Object var4;
      Object var10;
      do {
         var4 = var2.getValue();
         SurveyData var5 = (SurveyData)var4;
         int var6 = 0;
         var10 = var1;
      } while (!var2.compareAndSet(var4, var10));

      MutableStateFlow $this$update$ivx = this._overlayState;
      int $i$f$updatex = 0;

      Object prevValue$ivx;
      Object nextValue$ivx;
      do {
         prevValue$ivx = $this$update$ivx.getValue();
         OverlayState var11 = (OverlayState)prevValue$ivx;
         int var13 = 0;
         nextValue$ivx = OverlayState.copy$default(
            var11,
            Intrinsics.areEqual(var11.getSurveyData(), SurveyData.Companion.getNULL()) ? var1 : var11.getSurveyData(),
            null,
            0,
            null,
            null,
            false,
            false,
            null,
            null,
            null,
            null,
            2046,
            null
         );
      } while (!$this$update$ivx.compareAndSet(prevValue$ivx, nextValue$ivx));
   }

   public final void clearSurveyData() {
      MutableStateFlow var1 = this._surveyData;
      int var2 = 0;

      Object var3;
      Object var9;
      do {
         var3 = var1.getValue();
         SurveyData var4 = (SurveyData)var3;
         int var5 = 0;
         var9 = SurveyData.Companion.getNULL();
      } while (!var1.compareAndSet(var3, var9));

      MutableStateFlow $this$update$ivx = this._overlayState;
      int $i$f$updatex = 0;

      Object prevValue$ivx;
      Object nextValue$ivx;
      do {
         prevValue$ivx = $this$update$ivx.getValue();
         OverlayState var10 = (OverlayState)prevValue$ivx;
         int var12 = 0;
         nextValue$ivx = OverlayState.copy$default(var10, SurveyData.Companion.getNULL(), null, 0, null, null, false, false, null, null, null, null, 2046, null);
      } while (!$this$update$ivx.compareAndSet(prevValue$ivx, nextValue$ivx));
   }

   public final void updateCarousel(@NotNull Carousel var1) {
      Intrinsics.checkNotNullParameter(var1, "carousel");
      MutableStateFlow var2 = this._overlayState;
      int var3 = 0;

      Object var4;
      Object var7;
      do {
         var4 = var2.getValue();
         OverlayState var5 = (OverlayState)var4;
         int var6 = 0;
         var7 = OverlayState.copy$default(
            var5,
            null,
            Intrinsics.areEqual(var5.getCarousel(), Carousel.NULL) ? var1 : var5.getCarousel(),
            0,
            null,
            null,
            false,
            false,
            null,
            null,
            null,
            null,
            2045,
            null
         );
      } while (!var2.compareAndSet(var4, var7));
   }

   public final void clearCarousel() {
      MutableStateFlow var1 = this._overlayState;
      int var2 = 0;

      Object var3;
      Object var6;
      do {
         var3 = var1.getValue();
         OverlayState var4 = (OverlayState)var3;
         int var5 = 0;
         Carousel var10002 = Carousel.NULL;
         Intrinsics.checkNotNullExpressionValue(Carousel.NULL, "NULL");
         var6 = OverlayState.copy$default(var4, null, var10002, 0, null, null, false, false, null, null, null, null, 2045, null);
      } while (!var1.compareAndSet(var3, var6));
   }

   public final void updateBottomPadding(int var1) {
      MutableStateFlow var2 = this._overlayState;
      int var3 = 0;

      Object var4;
      Object var7;
      do {
         var4 = var2.getValue();
         OverlayState var5 = (OverlayState)var4;
         int var6 = 0;
         var7 = OverlayState.copy$default(var5, null, null, var1, null, null, false, false, null, null, null, null, 2043, null);
      } while (!var2.compareAndSet(var4, var7));
   }

   public final void updateLauncherVisibility(@NotNull io.intercom.android.sdk.Intercom$Visibility var1) {
      Intrinsics.checkNotNullParameter(var1, "visibility");
      MutableStateFlow var2 = this._overlayState;
      int var3 = 0;

      Object var4;
      Object var7;
      do {
         var4 = var2.getValue();
         OverlayState var5 = (OverlayState)var4;
         int var6 = 0;
         var7 = OverlayState.copy$default(var5, null, null, 0, var1, null, false, false, null, null, null, null, 2039, null);
      } while (!var2.compareAndSet(var4, var7));
   }

   public final void updateInAppNotificationsVisibility(@NotNull io.intercom.android.sdk.Intercom$Visibility var1) {
      Intrinsics.checkNotNullParameter(var1, "visibility");
      MutableStateFlow var2 = this._overlayState;
      int var3 = 0;

      Object var4;
      Object var7;
      do {
         var4 = var2.getValue();
         OverlayState var5 = (OverlayState)var4;
         int var6 = 0;
         var7 = OverlayState.copy$default(var5, null, null, 0, null, var1, false, false, null, null, null, null, 2031, null);
      } while (!var2.compareAndSet(var4, var7));
   }

   public final void updateContentVisibility(boolean var1, boolean var2) {
      MutableStateFlow var3 = this._overlayState;
      int var4 = 0;

      Object var5;
      Object var8;
      do {
         var5 = var3.getValue();
         OverlayState var6 = (OverlayState)var5;
         int var7 = 0;
         var8 = OverlayState.copy$default(var6, null, null, 0, null, null, var1, var2, null, null, null, null, 1951, null);
      } while (!var3.compareAndSet(var5, var8));
   }

   public final void updateUnreadConversationIds(@NotNull Set var1) {
      Intrinsics.checkNotNullParameter(var1, "unreadConversationIds");
      MutableStateFlow var2 = this._unreadConversationIds;
      int var3 = 0;

      Object var4;
      Object var7;
      do {
         var4 = var2.getValue();
         Set var5 = (Set)var4;
         int var6 = 0;
         var7 = var1;
      } while (!var2.compareAndSet(var4, var7));
   }

   public final void updateConfig(@NotNull Config var1) {
      Intrinsics.checkNotNullParameter(var1, "config");
      if (!Intrinsics.areEqual(var1, Config.Companion.getNULL())) {
         this.updateAppConfig(AppConfigKt.getAppConfig(var1, ((AppConfig)this._config.getValue()).getPrimaryColor()));
      }
   }

   public final void resetConfig() {
      AppConfig var1 = (AppConfig)this.config.getValue();
      NexusConfig var2 = new NexusConfig();
      this.updateAppConfig(
         AppConfig.copy$default(
            var1,
            null,
            0,
            0,
            0,
            false,
            false,
            false,
            0,
            0L,
            0L,
            0L,
            0L,
            false,
            false,
            null,
            null,
            false,
            false,
            null,
            null,
            null,
            null,
            "",
            false,
            false,
            false,
            false,
            false,
            null,
            var2,
            null,
            false,
            null,
            false,
            -541065217,
            3,
            null
         )
      );
   }

   public final void updatePushNotificationsBannerDismissed(boolean var1) {
      this.appConfigStore.setPushNotificationsBannerDismissed(var1);
      MutableStateFlow var2 = this._pushNotificationsBannerDismissed;
      int var3 = 0;

      Object var4;
      Object var7;
      do {
         var4 = var2.getValue();
         boolean var5 = (Boolean)var4;
         int var6 = 0;
         var7 = var1;
      } while (!var2.compareAndSet(var4, var7));
   }

   public final void markConversationAsRead(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "conversationId");
      List var2 = ((OverlayState)this.overlayState.getValue()).getConversations();
      MutableStateFlow var3 = this._overlayState;
      int var4 = 0;

      Object var5;
      Object var26;
      do {
         var5 = var3.getValue();
         OverlayState var6 = (OverlayState)var5;
         int var7 = 0;
         Iterable var8 = var2;
         boolean var9 = false;
         boolean var10 = false;
         Object var11 = null;
         Object var12 = null;
         byte var13 = 0;
         Object var14 = null;
         Object var15 = null;
         OverlayState var16 = var6;
         int var17 = 0;
         Iterable var18 = var8;
         ArrayList var19 = new ArrayList();
         int var20 = 0;

         for (Object var22 : var18) {
            Conversation itx = (Conversation)var22;
            int var24 = 0;
            if (!Intrinsics.areEqual(itx.getId(), var1)) {
               var19.add(var22);
            }
         }

         List var25 = (List)var19;
         var26 = OverlayState.copy$default(
            var16, (SurveyData)var15, (Carousel)var14, var13, (io.intercom.android.sdk.Intercom$Visibility)var12, (io.intercom.android.sdk.Intercom$Visibility)var11, var10, var9, var25, null, null, null, 1919, null
         );
      } while (!var3.compareAndSet(var5, var26));

      Set var27 = (Set)this.unreadConversationIds.getValue();
      MutableStateFlow $this$update$ivx = this._unreadConversationIds;
      int $i$f$updatex = 0;

      Object prevValue$ivx;
      Object nextValue$ivx;
      do {
         prevValue$ivx = $this$update$ivx.getValue();
         Set var31 = (Set)prevValue$ivx;
         int var32 = 0;
         Iterable var33 = var27;
         int var34 = 0;
         Iterable var35 = var33;
         ArrayList var36 = new ArrayList();
         int var37 = 0;

         for (Object var39 : var35) {
            String itx = (String)var39;
            int var41 = 0;
            if (!Intrinsics.areEqual(itx, var1)) {
               var36.add(var39);
            }
         }

         nextValue$ivx = CollectionsKt.toSet((Iterable & List)var36);
      } while (!$this$update$ivx.compareAndSet(prevValue$ivx, nextValue$ivx));
   }

   public final void updateConversationTitle(@NotNull String var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "conversationId");
      Intrinsics.checkNotNullParameter(var2, "title");
      MutableStateFlow var3 = this._conversations;
      int var4 = 0;

      Object var5;
      Object var18;
      do {
         var5 = var3.getValue();
         List var6 = (List)var5;
         int var7 = 0;
         Iterable var8 = var6;
         int var9 = 0;
         Iterable var10 = var8;
         ArrayList var11 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var8, 10));
         int var12 = 0;

         for (Object var14 : var10) {
            Conversation var15 = (Conversation)var14;
            Collection var16 = var11;
            int var17 = 0;
            var16.add(
               Intrinsics.areEqual(var15.getId(), var1)
                  ? Conversation.copy$default(
                     var15,
                     null,
                     var2,
                     false,
                     null,
                     null,
                     null,
                     null,
                     null,
                     false,
                     false,
                     null,
                     null,
                     false,
                     null,
                     null,
                     null,
                     null,
                     null,
                     null,
                     null,
                     null,
                     2097149,
                     null
                  )
                  : var15
            );
         }

         var18 = (List)var11;
      } while (!var3.compareAndSet(var5, var18));

      List var28 = ((OverlayState)this.overlayState.getValue()).getConversations();
      var var29 = var28;
      int var31 = 0;
      boolean var10000;
      if (var29 instanceof Collection && ((Collection)var29).isEmpty()) {
         var10000 = false;
      } else {
         Iterator var33 = var29.iterator();

         while (true) {
            if (!var33.hasNext()) {
               var10000 = false;
               break;
            }

            Object var35 = var33.next();
            Conversation var37 = (Conversation)var35;
            int var39 = 0;
            if (Intrinsics.areEqual(var37.getId(), var1)) {
               var10000 = true;
               break;
            }
         }
      }

      if (var10000) {
         MutableStateFlow $this$update$ivx = this._overlayState;
         int $i$f$updatex = 0;

         Object nextValue$ivx;
         Object prevValue$ivx;
         do {
            prevValue$ivx = $this$update$ivx.getValue();
            OverlayState var36 = (OverlayState)prevValue$ivx;
            int var38 = 0;
            Iterable var40 = var28;
            boolean var41 = false;
            boolean var42 = false;
            Object var43 = null;
            Object var44 = null;
            byte var45 = 0;
            Object var46 = null;
            Object var47 = null;
            OverlayState var48 = var36;
            int var49 = 0;
            Iterable var19 = var40;
            ArrayList var20 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var40, 10));
            int var21 = 0;

            for (Object var23 : var19) {
               Conversation var24 = (Conversation)var23;
               Collection var25 = var20;
               int var26 = 0;
               var25.add(
                  Intrinsics.areEqual(var24.getId(), var1)
                     ? Conversation.copy$default(
                        var24,
                        null,
                        var2,
                        false,
                        null,
                        null,
                        null,
                        null,
                        null,
                        false,
                        false,
                        null,
                        null,
                        false,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        2097149,
                        null
                     )
                     : var24
               );
            }

            List var50 = (List)var20;
            nextValue$ivx = OverlayState.copy$default(
               var48, (SurveyData)var47, (Carousel)var46, var45, (io.intercom.android.sdk.Intercom$Visibility)var44, (io.intercom.android.sdk.Intercom$Visibility)var43, var42, var41, var50, null, null, null, 1919, null
            );
         } while (!$this$update$ivx.compareAndSet(prevValue$ivx, nextValue$ivx));
      }
   }

   public final void fetchConversationSuccess(@NotNull Conversation var1) {
      Intrinsics.checkNotNullParameter(var1, "conversation");
      Set var2 = (Set)this.unreadConversationIds.getValue();
      if (var1.isRead()) {
         MutableStateFlow var3 = this._unreadConversationIds;
         int var4 = 0;

         Object var5;
         Object var17;
         do {
            var5 = var3.getValue();
            Set var6 = (Set)var5;
            int var7 = 0;
            Iterable var8 = var2;
            int var9 = 0;
            Iterable var10 = var8;
            ArrayList var11 = new ArrayList();
            int var12 = 0;

            for (Object var14 : var10) {
               String itx = (String)var14;
               int var16 = 0;
               if (!Intrinsics.areEqual(itx, var1.getId())) {
                  var11.add(var14);
               }
            }

            var17 = CollectionsKt.toSet((Iterable & List)var11);
         } while (!var3.compareAndSet(var5, var17));
      } else {
         MutableStateFlow var18 = this._unreadConversationIds;
         int var19 = 0;

         Object var20;
         Object var23;
         do {
            var20 = var18.getValue();
            Set var21 = (Set)var20;
            int var22 = 0;
            var23 = SetsKt.plus(var2, var1.getId());
         } while (!var18.compareAndSet(var20, var23));
      }
   }

   public final void markConversationPartAsDismissed(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "partId");
      List var2 = ((OverlayState)this.overlayState.getValue()).getConversations();
      Set var3 = SetsKt.plus(((OverlayState)this.overlayState.getValue()).getDismissedPartIds(), var1);
      MutableStateFlow var4 = this._overlayState;
      int var5 = 0;

      Object var6;
      Object var33;
      do {
         var6 = var4.getValue();
         OverlayState var7 = (OverlayState)var6;
         int var8 = 0;
         Iterable var9 = var2;
         boolean var10 = false;
         boolean var11 = false;
         Object var12 = null;
         Object var13 = null;
         byte var14 = 0;
         Object var15 = null;
         Object var16 = null;
         OverlayState var17 = var7;
         int var18 = 0;
         Iterable var19 = var9;
         ArrayList var20 = new ArrayList();
         int var21 = 0;

         for (Object var23 : var19) {
            Conversation var24 = (Conversation)var23;
            int var25 = 0;
            var var26 = var3;
            int var27 = 0;
            boolean var10000;
            if (var26 instanceof Collection && ((Collection)var26).isEmpty()) {
               var10000 = false;
            } else {
               Iterator var28 = var26.iterator();

               while (true) {
                  if (!var28.hasNext()) {
                     var10000 = false;
                     break;
                  }

                  Object var29 = var28.next();
                  String itx = (String)var29;
                  int var31 = 0;
                  if (Intrinsics.areEqual(itx, var24.lastPart().getId())) {
                     var10000 = true;
                     break;
                  }
               }
            }

            if (!var10000) {
               var20.add(var23);
            }
         }

         List var32 = (List)var20;
         var33 = OverlayState.copy$default(
            var17, (SurveyData)var16, (Carousel)var15, var14, (io.intercom.android.sdk.Intercom$Visibility)var13, (io.intercom.android.sdk.Intercom$Visibility)var12, var11, var10, var32, var3, null, null, 1663, null
         );
      } while (!var4.compareAndSet(var6, var33));
   }

   public final void updateOverlayConversations(@NotNull List var1) {
      Intrinsics.checkNotNullParameter(var1, "conversations");
      Set var2 = ((OverlayState)this.overlayState.getValue()).getDismissedPartIds();
      MutableStateFlow var3 = this._overlayState;
      int var4 = 0;

      Object var5;
      Object var32;
      do {
         var5 = var3.getValue();
         OverlayState var6 = (OverlayState)var5;
         int var7 = 0;
         Iterable var8 = var1;
         boolean var9 = false;
         boolean var10 = false;
         Object var11 = null;
         Object var12 = null;
         byte var13 = 0;
         Object var14 = null;
         Object var15 = null;
         OverlayState var16 = var6;
         int var17 = 0;
         Iterable var18 = var8;
         ArrayList var19 = new ArrayList();
         int var20 = 0;

         for (Object var22 : var18) {
            Conversation var23 = (Conversation)var22;
            int var24 = 0;
            var var25 = var2;
            int var26 = 0;
            boolean var10000;
            if (var25 instanceof Collection && ((Collection)var25).isEmpty()) {
               var10000 = false;
            } else {
               Iterator var27 = var25.iterator();

               while (true) {
                  if (!var27.hasNext()) {
                     var10000 = false;
                     break;
                  }

                  Object var28 = var27.next();
                  String itx = (String)var28;
                  int var30 = 0;
                  if (Intrinsics.areEqual(itx, var23.lastPart().getId())) {
                     var10000 = true;
                     break;
                  }
               }
            }

            if (!var10000) {
               var19.add(var22);
            }
         }

         List var31 = (List)var19;
         Iterable var33 = var31;
         boolean var34 = var9;
         boolean var35 = var10;
         Object var36 = var11;
         Object var37 = var12;
         byte var38 = var13;
         Object var39 = var14;
         Object var40 = var15;
         OverlayState var41 = var16;
         int $i$f$filterNotx = 0;
         Iterable var43 = var33;
         var destination$iv$ivx = new ArrayList();
         int $i$f$filterNotTox = 0;

         for (Object var47 : var43) {
            Conversation var48 = (Conversation)var47;
            int var49 = 0;
            if (var48.lastPart().getDeliveryOption() != DeliveryOption.BADGE) {
               destination$iv$ivx.add(var47);
            }
         }

         List var50 = (List)destination$iv$ivx;
         var32 = OverlayState.copy$default(
            var41, (SurveyData)var40, (Carousel)var39, var38, (io.intercom.android.sdk.Intercom$Visibility)var37, (io.intercom.android.sdk.Intercom$Visibility)var36, var35, var34, var50, null, null, null, 1919, null
         );
      } while (!var3.compareAndSet(var5, var32));
   }

   public final void activityReadyForViewAttachment(@NotNull Activity var1) {
      Intrinsics.checkNotNullParameter(var1, "activity");
      Activity var2 = ActivityUtils.isHostActivity(var1) ? var1 : null;
      MutableStateFlow var3 = this._overlayState;
      int var4 = 0;

      Object var5;
      Object var8;
      do {
         var5 = var3.getValue();
         OverlayState var6 = (OverlayState)var5;
         int var7 = 0;
         var8 = OverlayState.copy$default(var6, null, null, 0, null, null, false, false, null, null, var2, null, 511, null);
      } while (!var3.compareAndSet(var5, var8));
   }

   public final void activityPaused(@NotNull Activity var1) {
      Intrinsics.checkNotNullParameter(var1, "activity");
      MutableStateFlow var2 = this._overlayState;
      int var3 = 0;

      Object var4;
      Object var7;
      do {
         var4 = var2.getValue();
         OverlayState var5 = (OverlayState)var4;
         int var6 = 0;
         var7 = OverlayState.copy$default(var5, null, null, 0, null, null, false, false, null, null, null, var1, 511, null);
      } while (!var2.compareAndSet(var4, var7));
   }

   public final void activityStopped(@NotNull Activity var1) {
      Intrinsics.checkNotNullParameter(var1, "activity");
      Activity var2 = Intrinsics.areEqual(var1, ((OverlayState)this.overlayState.getValue()).getPausedHostActivity())
         ? null
         : ((OverlayState)this.overlayState.getValue()).getPausedHostActivity();
      MutableStateFlow var3 = this._overlayState;
      int var4 = 0;

      Object var5;
      Object var8;
      do {
         var5 = var3.getValue();
         OverlayState var6 = (OverlayState)var5;
         int var7 = 0;
         var8 = OverlayState.copy$default(var6, null, null, 0, null, null, false, false, null, null, null, var2, 1023, null);
      } while (!var3.compareAndSet(var5, var8));
   }

   public final void appEnteredBackground(long var1) {
      MutableStateFlow var3 = this._overlayState;
      int var4 = 0;

      Object var5;
      Object var12;
      do {
         var5 = var3.getValue();
         OverlayState var6 = (OverlayState)var5;
         int var7 = 0;
         var12 = OverlayState.copy$default(var6, null, null, 0, null, null, false, false, null, null, null, null, 511, null);
      } while (!var3.compareAndSet(var5, var12));

      MutableStateFlow $this$update$ivx = this._hostAppState;
      int $i$f$updatex = 0;

      Object nextValue$ivx;
      Object prevValue$ivx;
      do {
         prevValue$ivx = $this$update$ivx.getValue();
         HostAppState var13 = (HostAppState)prevValue$ivx;
         int var14 = 0;
         nextValue$ivx = HostAppState.copy$default(var13, AppLifecycleState.BACKGROUND, false, var1, null, false, false, 58, null);
      } while (!$this$update$ivx.compareAndSet(prevValue$ivx, nextValue$ivx));
   }

   public final void appEnteredForeground() {
      MutableStateFlow var1 = this._hostAppState;
      int var2 = 0;

      Object var3;
      Object var6;
      do {
         var3 = var1.getValue();
         HostAppState var4 = (HostAppState)var3;
         int var5 = 0;
         var6 = HostAppState.copy$default(var4, AppLifecycleState.FOREGROUND, false, 0L, null, false, false, 60, null);
      } while (!var1.compareAndSet(var3, var6));
   }

   @Nullable
   public final Long claimSessionStart(@NotNull SessionStartClaim var1) {
      Intrinsics.checkNotNullParameter(var1, "claim");
      Object var2 = null;
      boolean var3 = false;
      MutableStateFlow var4 = this._hostAppState;
      int var5 = 0;

      Object var6;
      Object var13;
      Long var14;
      do {
         var6 = var4.getValue();
         HostAppState var7 = (HostAppState)var6;
         int var8 = 0;
         boolean var9 = var7.getSessionStartAttemptId() == null;
         boolean var10000;
         switch (var1) {
            case IF_IDLE:
               var10000 = var9;
               break;
            case NEW_VISIT:
               var10000 = true;
               break;
            case IF_OWED:
               var10000 = var9 && var7.getSessionStartOwed();
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }

         boolean var10 = var10000;
         HostAppState var15;
         if (!var10) {
            var14 = null;
            var15 = var7;
         } else {
            long var11 = this.sessionStartAttemptIds.incrementAndGet();
            var14 = var11;
            var3 = var7.getSessionStartOwed() && var1 != SessionStartClaim.NEW_VISIT;
            var15 = HostAppState.copy$default(var7, null, false, 0L, var11, false, false, 23, null);
         }

         var13 = var15;
      } while (!var4.compareAndSet(var6, var13));

      if (var14 != null && !var3) {
         this.generateSessionId();
      }

      return var14;
   }

   // $VF: synthetic method
   public static Long claimSessionStart$default(IntercomDataLayer var0, SessionStartClaim var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = SessionStartClaim.IF_IDLE;
      }

      return var0.claimSessionStart(var1);
   }

   public final void sessionStartSucceeded(long var1) {
      MutableStateFlow var3 = this._hostAppState;
      int var4 = 0;

      Object var5;
      Object var10;
      do {
         var5 = var3.getValue();
         HostAppState var6 = (HostAppState)var5;
         int var7 = 0;
         Long var10000 = var6.getSessionStartAttemptId();
         long var8 = var1;
         var10 = var10000 != null && var10000 == var8 ? HostAppState.copy$default(var6, null, true, 0L, null, false, false, 5, null) : var6;
      } while (!var3.compareAndSet(var5, var10));
   }

   public final boolean sessionStartFailed(long var1) {
      boolean var3 = false;
      MutableStateFlow var4 = this._hostAppState;
      int var5 = 0;

      Object var6;
      Object var11;
      boolean var12;
      do {
         var6 = var4.getValue();
         HostAppState var7 = (HostAppState)var6;
         int var8 = 0;
         Long var10000 = var7.getSessionStartAttemptId();
         long var9 = var1;
         HostAppState var13;
         if (var10000 != null && var10000 == var9) {
            var12 = var7.getSessionStartRetryRequested();
            var13 = HostAppState.copy$default(var7, null, false, 0L, null, true, false, 7, null);
         } else {
            var12 = false;
            var13 = var7;
         }

         var11 = var13;
      } while (!var4.compareAndSet(var6, var11));

      return var12;
   }

   public final boolean requestRetryIfSessionStartInFlight() {
      boolean var1 = false;
      MutableStateFlow var2 = this._hostAppState;
      int var3 = 0;

      Object var4;
      boolean var7;
      Object var8;
      do {
         var4 = var2.getValue();
         HostAppState var5 = (HostAppState)var4;
         int var6 = 0;
         var7 = var5.getSessionStartAttemptId() != null;
         var8 = var7 ? HostAppState.copy$default(var5, null, false, 0L, null, false, true, 31, null) : var5;
      } while (!var2.compareAndSet(var4, var8));

      return var7;
   }

   @Nullable
   public final String getSessionId() {
      return this.appConfigStore.getSessionId();
   }

   @NotNull
   public final String generateSessionId() {
      return this.appConfigStore.generateSessionId();
   }

   public final void hardReset() {
      this.resetConfig();
      MutableStateFlow var1 = this._hostAppState;
      int var2 = 0;

      Object var3;
      Object var6;
      do {
         var3 = var1.getValue();
         HostAppState var4 = (HostAppState)var3;
         int var5 = 0;
         var6 = HostAppState.copy$default(var4, null, false, 0L, null, false, false, 5, null);
      } while (!var1.compareAndSet(var3, var6));
   }

   public final void clearUserData() {
      MutableStateFlow var1 = this._conversations;
      int var2 = 0;

      Object var3;
      Object var21;
      do {
         var3 = var1.getValue();
         List var4 = (List)var3;
         int var5 = 0;
         var21 = CollectionsKt.emptyList();
      } while (!var1.compareAndSet(var3, var21));

      MutableStateFlow $this$update$ivx = this._teamPresence;
      int $i$f$updatex = 0;

      Object prevValue$ivx;
      Object nextValue$ivx;
      do {
         prevValue$ivx = $this$update$ivx.getValue();
         TeamPresence var22 = (TeamPresence)prevValue$ivx;
         int var32 = 0;
         nextValue$ivx = TeamPresence.NULL;
      } while (!$this$update$ivx.compareAndSet(prevValue$ivx, nextValue$ivx));

      MutableStateFlow $this$update$ivxx = this._ticket;
      int $i$f$updatexx = 0;

      Object prevValue$ivxx;
      Object nextValue$ivxx;
      do {
         prevValue$ivxx = $this$update$ivxx.getValue();
         Ticket var24 = (Ticket)prevValue$ivxx;
         int var33 = 0;
         nextValue$ivxx = Ticket.Companion.getNULL();
      } while (!$this$update$ivxx.compareAndSet(prevValue$ivxx, nextValue$ivxx));

      MutableStateFlow $this$update$ivxxx = this._surveyData;
      int $i$f$updatexxx = 0;

      Object prevValue$ivxxx;
      Object nextValue$ivxxx;
      do {
         prevValue$ivxxx = $this$update$ivxxx.getValue();
         SurveyData var26 = (SurveyData)prevValue$ivxxx;
         int var34 = 0;
         nextValue$ivxxx = SurveyData.Companion.getNULL();
      } while (!$this$update$ivxxx.compareAndSet(prevValue$ivxxx, nextValue$ivxxx));

      MutableStateFlow $this$update$ivxxxx = this._overlayState;
      int $i$f$updatexxxx = 0;

      Object prevValue$ivxxxx;
      Object nextValue$ivxxxx;
      do {
         prevValue$ivxxxx = $this$update$ivxxxx.getValue();
         OverlayState var28 = (OverlayState)prevValue$ivxxxx;
         int var35 = 0;
         SurveyData var10001 = SurveyData.Companion.getNULL();
         Carousel var10002 = Carousel.NULL;
         Intrinsics.checkNotNullExpressionValue(Carousel.NULL, "NULL");
         nextValue$ivxxxx = OverlayState.copy$default(
            var28, var10001, var10002, 0, null, null, false, false, CollectionsKt.emptyList(), SetsKt.emptySet(), null, null, 1660, null
         );
      } while (!$this$update$ivxxxx.compareAndSet(prevValue$ivxxxx, nextValue$ivxxxx));

      MutableStateFlow $this$update$ivxxxxx = this._unreadConversationIds;
      int $i$f$updatexxxxx = 0;

      Object prevValue$ivxxxxx;
      Object nextValue$ivxxxxx;
      do {
         prevValue$ivxxxxx = $this$update$ivxxxxx.getValue();
         Set var30 = (Set)prevValue$ivxxxxx;
         int var36 = 0;
         nextValue$ivxxxxx = SetsKt.emptySet();
      } while (!$this$update$ivxxxxx.compareAndSet(prevValue$ivxxxxx, nextValue$ivxxxxx));

      this.openResponse = null;
      this.homeCards = CollectionsKt.emptyList();
   }

   public final void userJwtChanged() {
      this._userJwtChanges.tryEmit(Unit.INSTANCE);
   }

   @Nullable
   public final Object emitEvent(@NotNull IntercomEvent var1, @NotNull Continuation var2) {
      Object var10000 = this._event.emit(var1, var2);
      return var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var10000 : Unit.INSTANCE;
   }

   private final void updateAppConfig(AppConfig var1) {
      if (!Intrinsics.areEqual(var1, this._config.getValue())) {
         this.appConfigStore.setAppConfig(var1);
         this._config.setValue(var1);
         ConfigModules var10000 = var1.getConfigModules();
         if (var10000 != null) {
            CustomizationModel var6 = var10000.getCustomization();
            if (var6 != null) {
               String var7 = var6.getThemeMode();
               if (var7 != null) {
                  ThemeMode var9;
                  label28: {
                     String var2 = var7;
                     int var3 = 0;
                     String var8 = var2.toLowerCase(Locale.ROOT);
                     Intrinsics.checkNotNullExpressionValue(var8, "toLowerCase(...)");
                     String var4 = var8;
                     switch (var4.hashCode()) {
                        case -887328209:
                           if (var4.equals("system")) {
                              var9 = ThemeMode.SYSTEM;
                              break label28;
                           }
                           break;
                        case 3075958:
                           if (var4.equals("dark")) {
                              var9 = ThemeMode.DARK;
                              break label28;
                           }
                           break;
                        case 102970646:
                           if (var4.equals("light")) {
                              var9 = ThemeMode.LIGHT;
                              break label28;
                           }
                     }

                     var9 = ThemeMode.LIGHT;
                  }

                  ThemeMode var5 = var9;
                  ThemeManager.INSTANCE.updateServerTheme(var5);
               }
            }
         }
      }
   }

   public final void configUpdates(@NotNull CoroutineScope var1, @NotNull Function1 var2) {
      Intrinsics.checkNotNullParameter(var1, "coroutineScope");
      Intrinsics.checkNotNullParameter(var2, "onNewAppConfig");
      BuildersKt.launch$default(var1, null, null, new IntercomDataLayer$configUpdates$1(this, var2, null), 3, null);
   }

   public final void overlayStateUpdates(@NotNull CoroutineScope var1, @NotNull Function1 var2) {
      Intrinsics.checkNotNullParameter(var1, "coroutineScope");
      Intrinsics.checkNotNullParameter(var2, "onNewOverlyState");
      BuildersKt.launch$default(var1, null, null, new IntercomDataLayer$overlayStateUpdates$1(this, var2, null), 3, null);
   }

   public final void unreadConversationIdsUpdates(@NotNull CoroutineScope var1, @NotNull Function1 var2) {
      Intrinsics.checkNotNullParameter(var1, "coroutineScope");
      Intrinsics.checkNotNullParameter(var2, "onNewUnreadConversationsIdsState");
      BuildersKt.launch$default(var1, null, null, new IntercomDataLayer$unreadConversationIdsUpdates$1(this, var2, null), 3, null);
   }

   @JvmOverloads
   public final void emitEvent(@NotNull CoroutineScope var1, @NotNull IntercomEvent var2) {
      Intrinsics.checkNotNullParameter(var1, "coroutineScope");
      Intrinsics.checkNotNullParameter(var2, "event");
      BuildersKt.launch$default(var1, null, null, new IntercomDataLayer$emitEvent$2(this, var2, null), 3, null);
   }

   // $VF: synthetic method
   public static void emitEvent$default(IntercomDataLayer var0, CoroutineScope var1, IntercomEvent var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.applicationScope;
      }

      var0.emitEvent(var1, var2);
   }

   public final void listenToEvents(@NotNull CoroutineScope var1, @NotNull Function1 var2) {
      Intrinsics.checkNotNullParameter(var1, "coroutineScope");
      Intrinsics.checkNotNullParameter(var2, "onNewEvent");
      BuildersKt.launch$default(var1, null, null, new IntercomDataLayer$listenToEvents$1(this, var2, null), 3, null);
   }

   public final void updateOpenResponse(@NotNull OpenMessengerResponse var1) {
      Intrinsics.checkNotNullParameter(var1, "response");
      this.openResponse = var1;
   }

   public final void clearOpenResponse() {
      this.openResponse = null;
   }

   @Nullable
   public final OpenMessengerResponse getOpenResponse() {
      return this.openResponse;
   }

   @JvmOverloads
   public final void emitEvent(@NotNull IntercomEvent var1) {
      Intrinsics.checkNotNullParameter(var1, "event");
      emitEvent$default(this, null, var1, 1, null);
   }
}
