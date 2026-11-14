package io.intercom.android.sdk.m5.conversation.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build.VERSION;
import androidx.activity.compose.ActivityResultRegistryKt;
import androidx.activity.compose.ManagedActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.WindowInsets;
import androidx.compose.foundation.layout.WindowInsetsPaddingKt;
import androidx.compose.foundation.layout.WindowInsetsPadding_androidKt;
import androidx.compose.foundation.layout.WindowInsets_androidKt;
import androidx.compose.foundation.lazy.LazyListItemInfo;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.foundation.lazy.LazyListStateKt;
import androidx.compose.material3.ModalBottomSheetKt;
import androidx.compose.material3.ScaffoldKt;
import androidx.compose.material3.SheetState;
import androidx.compose.material3.SnackbarDuration;
import androidx.compose.material3.SnackbarHostState;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.DisposableEffectResult;
import androidx.compose.runtime.DisposableEffectScope;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.runtime.saveable.RememberSaveableKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.focus.FocusManager;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.platform.SoftwareKeyboardController;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.unit.Density;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.LayoutDirection;
import androidx.core.content.ContextCompat;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.TicketType;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.ConversationViewModel;
import io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadError;
import io.intercom.android.sdk.m5.conversation.metrics.MetricData;
import io.intercom.android.sdk.m5.conversation.states.AttributeData;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState;
import io.intercom.android.sdk.m5.conversation.states.ExpandedTeamPresenceState;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem;
import io.intercom.android.sdk.m5.conversation.states.NetworkState;
import io.intercom.android.sdk.m5.conversation.states.ReplySuggestion;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState;
import io.intercom.android.sdk.m5.conversation.states.BottomSheetState$InlineSources;
import io.intercom.android.sdk.m5.conversation.states.BottomSheetState$MediaInput;
import io.intercom.android.sdk.m5.conversation.states.BottomSheetState$TeammatePresence;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content;
import io.intercom.android.sdk.m5.conversation.states.NetworkState$Connected;
import io.intercom.android.sdk.m5.conversation.states.NetworkState$Disconnected;
import io.intercom.android.sdk.m5.conversation.states.NetworkState$Reconnected;
import io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Idle;
import io.intercom.android.sdk.m5.conversation.ui.components.DeleteFailedMessageDialogKt;
import io.intercom.android.sdk.m5.conversation.ui.components.UploadErrorDialogKt;
import io.intercom.android.sdk.m5.conversation.ui.components.UploadSizeLimitDialogKt;
import io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposerInputType;
import io.intercom.android.sdk.m5.conversation.ui.components.composer.MessageComposerKt;
import io.intercom.android.sdk.m5.conversation.usecase.ConversationScrolledState;
import io.intercom.android.sdk.m5.conversation.utils.KeyboardState;
import io.intercom.android.sdk.m5.conversation.utils.KeyboardStateKt;
import io.intercom.android.sdk.m5.conversation.utils.URIExtensionsKt;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewArgs;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$Companion;
import io.intercom.android.sdk.m5.preview.ui.PreviewMediaContract;
import io.intercom.android.sdk.m5.upload.data.MediaData$Gif;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.AvatarType;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.ReplyOption;
import io.intercom.android.sdk.models.Header$Expanded$Body;
import io.intercom.android.sdk.models.Header$Expanded$Style;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.Phrase;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KFunction;
import kotlin.reflect.KProperty;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.SharedFlow;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.flow.StateFlowKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u009c\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\u001a§\u0001\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u0014\b\u0002\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\u000b2\u0014\b\u0002\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00010\u000b2\u0014\b\u0002\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00010\u000b2\u0014\b\u0002\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\u000bH\u0001¢\u0006\u0002\u0010\u0012\u001a4\u0010\u0013\u001a\u00020\u00012\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0082@¢\u0006\u0002\u0010\u001b\u001a½\u0007\u0010\u001c\u001a\u00020\u00012\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u001d\u001a\u00020\u001e2\b\b\u0002\u0010\u0016\u001a\u00020\u00172\u0014\b\u0002\u0010\u001f\u001a\u000e\u0012\u0004\u0012\u00020 \u0012\u0004\u0012\u00020\u00010\u000b2\u0014\b\u0002\u0010!\u001a\u000e\u0012\u0004\u0012\u00020\"\u0012\u0004\u0012\u00020\u00010\u000b2 \b\u0002\u0010#\u001a\u001a\u0012\u0004\u0012\u00020\f\u0012\n\u0012\b\u0012\u0004\u0012\u00020&0%\u0012\u0004\u0012\u00020\u00010$2\u0014\b\u0002\u0010'\u001a\u000e\u0012\u0004\u0012\u00020(\u0012\u0004\u0012\u00020\u00010\u000b2\u0014\b\u0002\u0010)\u001a\u000e\u0012\u0004\u0012\u00020*\u0012\u0004\u0012\u00020\u00010\u000b2\u0014\b\u0002\u0010+\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\u000b2\u000e\b\u0002\u0010,\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u001a\b\u0002\u0010-\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020.0%\u0012\u0004\u0012\u00020\u00010\u000b2\u000e\b\u0002\u0010/\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u000e\b\u0002\u00100\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u000e\b\u0002\u00101\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u000e\b\u0002\u00102\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u0014\b\u0002\u00103\u001a\u000e\u0012\u0004\u0012\u000204\u0012\u0004\u0012\u00020\u00010\u000b2\u0014\b\u0002\u00105\u001a\u000e\u0012\u0004\u0012\u000204\u0012\u0004\u0012\u00020\u00010\u000b2\u0014\b\u0002\u00106\u001a\u000e\u0012\u0004\u0012\u000207\u0012\u0004\u0012\u00020\u00010\u000b2\u000e\b\u0002\u00108\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u0014\b\u0002\u00109\u001a\u000e\u0012\u0004\u0012\u00020:\u0012\u0004\u0012\u00020\u00010\u000b2\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u0014\b\u0002\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\u000b2\u0014\b\u0002\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00010\u000b2\u0014\b\u0002\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00010\u000b2\u0014\b\u0002\u0010;\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\u000b2\u0014\b\u0002\u0010<\u001a\u000e\u0012\u0004\u0012\u00020=\u0012\u0004\u0012\u00020\u00010\u000b2\u0012\u0010>\u001a\u000e\u0012\u0004\u0012\u00020?\u0012\u0004\u0012\u00020\u00010\u000b2\u0012\u0010@\u001a\u000e\u0012\u0004\u0012\u00020A\u0012\u0004\u0012\u00020\u00010\u000b2\u0014\b\u0002\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\u000b2\b\b\u0002\u0010B\u001a\u00020C2\u000e\b\u0002\u0010D\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072(\b\u0002\u0010E\u001a\"\u0012\n\u0012\b\u0012\u0004\u0012\u00020F0%\u0012\n\u0012\b\u0012\u0004\u0012\u00020G0%\u0012\u0004\u0012\u00020\u0001\u0018\u00010$2\u000e\b\u0002\u0010H\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u0010\b\u0002\u0010I\u001a\n\u0012\u0004\u0012\u00020K\u0018\u00010J28\b\u0002\u0010L\u001a2\u0012\u0013\u0012\u00110M¢\u0006\f\bN\u0012\b\bO\u0012\u0004\b\b(P\u0012\u0013\u0012\u00110M¢\u0006\f\bN\u0012\b\bO\u0012\u0004\b\b(Q\u0012\u0004\u0012\u00020\u00010$2\b\b\u0002\u0010R\u001a\u00020S2\u000e\b\u0002\u0010T\u001a\b\u0012\u0004\u0012\u00020V0U2\u000e\b\u0002\u0010W\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u000e\b\u0002\u0010X\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u000e\b\u0002\u0010Y\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u000e\b\u0002\u0010Z\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u0014\b\u0002\u0010[\u001a\u000e\u0012\u0004\u0012\u00020&\u0012\u0004\u0012\u00020\u00010\u000b2\u0014\b\u0002\u0010\\\u001a\u000e\u0012\u0004\u0012\u00020&\u0012\u0004\u0012\u00020\u00010\u000b2\u000e\b\u0002\u0010]\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u000e\b\u0002\u0010^\u001a\b\u0012\u0004\u0012\u00020_0U2\u000e\b\u0002\u0010`\u001a\b\u0012\u0004\u0012\u00020M0UH\u0001¢\u0006\u0002\u0010a\u001a\"\u0010b\u001a\u00020M*\b\u0012\u0004\u0012\u00020c0%2\u0006\u0010d\u001a\u00020V2\u0006\u0010e\u001a\u00020AH\u0002\u001a\u0019\u0010j\u001a\u00020k*\u00020k2\u0006\u0010l\u001a\u00020mH\u0003¢\u0006\u0002\u0010n\u001a\r\u0010o\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010p\u001a\r\u0010q\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010p\"\u0010\u0010f\u001a\u00020gX\u0082\u0004¢\u0006\u0004\n\u0002\u0010h\"\u0010\u0010i\u001a\u00020gX\u0082\u0004¢\u0006\u0004\n\u0002\u0010h¨\u0006r²\u0006\u0016\u0010s\u001a\u000e\u0012\u0004\u0012\u00020M\u0012\u0004\u0012\u00020\f0tX\u008a\u008e\u0002²\u0006\f\u0010u\u001a\u0004\u0018\u00010&X\u008a\u008e\u0002²\u0006\f\u0010v\u001a\u0004\u0018\u000104X\u008a\u008e\u0002²\u0006\f\u0010w\u001a\u0004\u0018\u00010xX\u008a\u008e\u0002²\u0006\n\u0010y\u001a\u00020zX\u008a\u0084\u0002",
   d2 = {
         "ConversationScreen",
         "",
         "conversationViewModel",
         "Lio/intercom/android/sdk/m5/conversation/ConversationViewModel;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "onBackClick",
         "Lkotlin/Function0;",
         "onNewConversationClicked",
         "navigateToTicketDetail",
         "openTicket",
         "Lkotlin/Function1;",
         "",
         "onCreateTicket",
         "Lio/intercom/android/sdk/blocks/lib/models/TicketType;",
         "onMenuClicked",
         "Lio/intercom/android/sdk/m5/conversation/states/HeaderMenuItem;",
         "navigateToAnotherConversation",
         "(Lio/intercom/android/sdk/m5/conversation/ConversationViewModel;Landroidx/compose/ui/Modifier;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "showNetworkMessage",
         "networkState",
         "Lio/intercom/android/sdk/m5/conversation/states/NetworkState;",
         "snackbarHostState",
         "Landroidx/compose/material3/SnackbarHostState;",
         "context",
         "Landroid/content/Context;",
         "onDismiss",
         "(Lio/intercom/android/sdk/m5/conversation/states/NetworkState;Landroidx/compose/material3/SnackbarHostState;Landroid/content/Context;Lkotlin/jvm/functions/Function0;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "ConversationScreenContent",
         "uiState",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationUiState;",
         "onSuggestionClick",
         "Lio/intercom/android/sdk/m5/conversation/states/ReplySuggestion;",
         "onReplyClicked",
         "Lio/intercom/android/sdk/models/ReplyOption;",
         "onSendMessage",
         "Lkotlin/Function2;",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/MediaUploadItem;",
         "onInputChange",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposerInputType;",
         "onGifClick",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "onGifSearchQueryChange",
         "onGifInputSelected",
         "onMediaItemsSelected",
         "Landroid/net/Uri;",
         "onMediaInputSelected",
         "onMediaItemsLimitReached",
         "onTitleClicked",
         "onRetryClick",
         "onRetryMessageClicked",
         "Lio/intercom/android/sdk/models/Part;",
         "onFailedMessageOptionsClicked",
         "onRetryMediaClicked",
         "Lio/intercom/android/sdk/m5/conversation/states/PendingMessage$FailedMediaUploadData;",
         "onTyping",
         "onSubmitAttribute",
         "Lio/intercom/android/sdk/m5/conversation/states/AttributeData;",
         "trackClickedInput",
         "trackMetric",
         "Lio/intercom/android/sdk/m5/conversation/metrics/MetricData;",
         "onConversationScrolled",
         "Lio/intercom/android/sdk/m5/conversation/usecase/ConversationScrolledState;",
         "onJumpToBottomButtonClicked",
         "",
         "lazyListState",
         "Landroidx/compose/foundation/lazy/LazyListState;",
         "onPrivacyNoticeDismissed",
         "onInlineSourcesClick",
         "Lio/intercom/android/sdk/models/InlineSource;",
         "Lio/intercom/android/sdk/models/Source;",
         "onReportAiAnswer",
         "composerUiEffect",
         "Lkotlinx/coroutines/flow/SharedFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ComposerUiEffect;",
         "updatePushNotificationsBanner",
         "",
         "Lkotlin/ParameterName;",
         "name",
         "dismissed",
         "arePushNotificationsEnabled",
         "voiceTranscriptionState",
         "Lio/intercom/android/sdk/m5/conversation/states/VoiceTranscriptionState;",
         "amplitudeLevel",
         "Lkotlinx/coroutines/flow/StateFlow;",
         "",
         "onStartVoiceRecording",
         "onStopVoiceRecording",
         "onCancelVoiceRecording",
         "onTranscribedTextConsumed",
         "onDeleteMediaUploadItem",
         "onFailedMediaUploadItem",
         "onClearMediaUploadItems",
         "streamingMessageState",
         "Lio/intercom/android/sdk/m5/conversation/streaming/StreamingMessageState;",
         "isFinStreamingActive",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/conversation/states/ConversationUiState;Landroidx/compose/material3/SnackbarHostState;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function2;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/foundation/lazy/LazyListState;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function2;Lkotlin/jvm/functions/Function0;Lkotlinx/coroutines/flow/SharedFlow;Lkotlin/jvm/functions/Function2;Lio/intercom/android/sdk/m5/conversation/states/VoiceTranscriptionState;Lkotlinx/coroutines/flow/StateFlow;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlinx/coroutines/flow/StateFlow;Lkotlinx/coroutines/flow/StateFlow;Landroidx/compose/runtime/Composer;IIIIIII)V",
         "isLazyListScrolled",
         "Landroidx/compose/foundation/lazy/LazyListItemInfo;",
         "scrollOffset",
         "itemsCount",
         "JumpToBottomNewMessagesTopOffset",
         "Landroidx/compose/ui/unit/Dp;",
         "F",
         "LazyListJumpToBottomScrollOffset",
         "getPaddingValuesForComposer",
         "Landroidx/compose/foundation/layout/PaddingValues;",
         "bottomBarUiState",
         "Lio/intercom/android/sdk/m5/conversation/states/BottomBarUiState;",
         "(Landroidx/compose/foundation/layout/PaddingValues;Lio/intercom/android/sdk/m5/conversation/states/BottomBarUiState;Landroidx/compose/runtime/Composer;I)Landroidx/compose/foundation/layout/PaddingValues;",
         "ConversationScreenContentPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "NewConversationScreenContentPreview",
         "intercom-sdk-base_release",
         "showUploadSizeLimitDialog",
         "Lkotlin/Pair;",
         "uploadErrorDialogState",
         "deleteMessageDialogPart",
         "selectedGif",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Gif;",
         "keyboardAsState",
         "Lio/intercom/android/sdk/m5/conversation/utils/KeyboardState;"
   }
)
@SourceDebugExtension(
   "SMAP\nConversationScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationScreen.kt\nio/intercom/android/sdk/m5/conversation/ui/ConversationScreenKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 4 Effects.kt\nandroidx/compose/runtime/EffectsKt\n+ 5 Effects.kt\nandroidx/compose/runtime/EffectsKt$rememberCoroutineScope$1\n+ 6 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 7 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 8 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 9 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 10 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 11 Dp.kt\nandroidx/compose/ui/unit/Dp\n+ 12 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 13 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n+ 14 Effects.kt\nandroidx/compose/runtime/DisposableEffectScope\n*L\n1#1,1034:1\n1047#2,6:1035\n1047#2,6:1041\n1047#2,6:1047\n1047#2,6:1053\n1047#2,6:1059\n1047#2,6:1065\n1047#2,6:1071\n1047#2,6:1078\n1047#2,6:1084\n1047#2,6:1090\n1047#2,6:1096\n1047#2,6:1102\n1047#2,6:1108\n1047#2,6:1114\n1047#2,6:1120\n1047#2,6:1126\n1047#2,6:1132\n1047#2,6:1138\n1047#2,6:1144\n1047#2,6:1150\n1047#2,6:1156\n1047#2,6:1162\n1047#2,6:1168\n1047#2,6:1174\n1047#2,6:1180\n1047#2,6:1186\n1047#2,6:1192\n1047#2,6:1198\n1047#2,6:1204\n1047#2,6:1210\n1047#2,6:1216\n1047#2,6:1222\n1047#2,6:1228\n1047#2,6:1234\n1047#2,6:1240\n1047#2,6:1246\n1047#2,6:1252\n1047#2,6:1258\n1047#2,6:1264\n1047#2,6:1270\n1047#2,6:1276\n1047#2,6:1282\n1047#2,6:1288\n1047#2,6:1294\n1047#2,6:1300\n1047#2,6:1306\n1047#2,6:1312\n1047#2,6:1318\n1047#2,6:1324\n1047#2,6:1330\n1047#2,6:1336\n1047#2,6:1342\n1047#2,6:1348\n1047#2,6:1354\n1047#2,6:1360\n1047#2,6:1366\n1047#2,6:1372\n1047#2,6:1378\n1047#2,6:1384\n1047#2,6:1390\n1047#2,6:1396\n1047#2,6:1402\n1047#2,6:1408\n1047#2,6:1414\n1047#2,6:1420\n1047#2,6:1426\n1047#2,6:1432\n1047#2,6:1438\n1047#2,6:1444\n1047#2,6:1450\n1047#2,6:1456\n1047#2,6:1462\n1047#2,6:1468\n1047#2,6:1474\n1047#2,6:1480\n1047#2,6:1486\n1047#2,6:1492\n1047#2,6:1498\n1047#2,6:1504\n1047#2,6:1510\n1047#2,6:1516\n1047#2,6:1522\n1047#2,6:1528\n1047#2,6:1534\n1047#2,6:1540\n1047#2,6:1546\n1047#2,6:1552\n1047#2,6:1558\n1047#2,6:1564\n1047#2,6:1570\n1047#2,6:1576\n1047#2,3:1590\n1050#2,3:1594\n1047#2,6:1599\n1047#2,6:1605\n1047#2,6:1614\n1047#2,6:1620\n75#3:1077\n75#3:1582\n75#3:1597\n75#3:1598\n75#3:1611\n75#3:1613\n75#3:1659\n75#3:1671\n615#4:1583\n612#4,6:1584\n613#5:1593\n1#6:1612\n1#6:1702\n70#7:1626\n66#7,10:1627\n77#7:1663\n81#8,6:1637\n88#8,6:1652\n96#8:1662\n402#9,9:1643\n411#9:1658\n412#9,2:1660\n967#10,7:1664\n1617#10,9:1692\n1869#10:1701\n1870#10:1703\n1626#10:1704\n52#11:1672\n118#12:1673\n157#12:1674\n118#12:1706\n118#12:1707\n85#13:1675\n117#13,2:1676\n85#13:1678\n117#13,2:1679\n85#13:1681\n117#13,2:1682\n85#13:1684\n117#13,2:1685\n85#13:1705\n68#14,5:1687\n*S KotlinDebug\n*F\n+ 1 ConversationScreen.kt\nio/intercom/android/sdk/m5/conversation/ui/ConversationScreenKt\n*L\n147#1:1035,6\n148#1:1041,6\n149#1:1047,6\n150#1:1053,6\n151#1:1059,6\n152#1:1065,6\n153#1:1071,6\n156#1:1078,6\n159#1:1084,6\n160#1:1090,6\n163#1:1096,6\n165#1:1102,6\n169#1:1108,6\n191#1:1114,6\n220#1:1120,6\n235#1:1126,6\n246#1:1132,6\n249#1:1138,6\n252#1:1144,6\n264#1:1150,6\n267#1:1156,6\n276#1:1162,6\n403#1:1168,6\n404#1:1174,6\n405#1:1180,6\n406#1:1186,6\n407#1:1192,6\n408#1:1198,6\n409#1:1204,6\n289#1:1210,6\n292#1:1216,6\n295#1:1222,6\n298#1:1228,6\n301#1:1234,6\n321#1:1240,6\n340#1:1246,6\n324#1:1252,6\n331#1:1258,6\n334#1:1264,6\n337#1:1270,6\n344#1:1276,6\n348#1:1282,6\n351#1:1288,6\n354#1:1294,6\n357#1:1300,6\n360#1:1306,6\n367#1:1312,6\n370#1:1318,6\n373#1:1324,6\n376#1:1330,6\n379#1:1336,6\n383#1:1342,6\n386#1:1348,6\n391#1:1354,6\n395#1:1360,6\n451#1:1366,6\n452#1:1372,6\n453#1:1378,6\n454#1:1384,6\n455#1:1390,6\n456#1:1396,6\n457#1:1402,6\n458#1:1408,6\n459#1:1414,6\n460#1:1420,6\n461#1:1426,6\n462#1:1432,6\n463#1:1438,6\n464#1:1444,6\n466#1:1450,6\n467#1:1456,6\n468#1:1462,6\n469#1:1468,6\n470#1:1474,6\n471#1:1480,6\n472#1:1486,6\n473#1:1492,6\n474#1:1498,6\n475#1:1504,6\n476#1:1510,6\n479#1:1516,6\n481#1:1522,6\n483#1:1528,6\n485#1:1534,6\n488#1:1540,6\n489#1:1546,6\n490#1:1552,6\n491#1:1558,6\n492#1:1564,6\n493#1:1570,6\n494#1:1576,6\n499#1:1590,3\n499#1:1594,3\n505#1:1599,6\n509#1:1605,6\n551#1:1614,6\n576#1:1620,6\n155#1:1077\n498#1:1582\n500#1:1597\n502#1:1598\n546#1:1611\n547#1:1613\n644#1:1659\n894#1:1671\n499#1:1583\n499#1:1584,6\n499#1:1593\n325#1:1702\n643#1:1626\n643#1:1627,10\n643#1:1663\n643#1:1637,6\n643#1:1652,6\n643#1:1662\n643#1:1643,9\n643#1:1658\n643#1:1660,2\n878#1:1664,7\n325#1:1692,9\n325#1:1701\n325#1:1703\n325#1:1704\n900#1:1672\n900#1:1673\n900#1:1674\n888#1:1706\n889#1:1707\n159#1:1675\n159#1:1676,2\n160#1:1678\n160#1:1679,2\n163#1:1681\n163#1:1682,2\n165#1:1684\n165#1:1685,2\n501#1:1705\n221#1:1687,5\n*E\n"
)
public final class ConversationScreenKt {
   private static final float JumpToBottomNewMessagesTopOffset;
   private static final float LazyListJumpToBottomScrollOffset;

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ConversationScreen(
      @NotNull ConversationViewModel var0,
      @Nullable Modifier var1,
      @Nullable Function0 var2,
      @Nullable Function0 var3,
      @Nullable Function0 var4,
      @Nullable Function1 var5,
      @Nullable Function1 var6,
      @Nullable Function1 var7,
      @Nullable Function1 var8,
      @Nullable Composer var9,
      int var10,
      int var11
   ) {
      Intrinsics.checkNotNullParameter(var0, "conversationViewModel");
      Composer var81 = var9.startRestartGroup(-1550678767);
      ComposerKt.sourceInformation(
         var81,
         "C(ConversationScreen)P(!2,4,7,3,8,5,6)146@8197L2,147@8244L2,148@8289L2,149@8328L2,150@8375L2,151@8425L2,152@8483L2,154@8522L7,155@8577L32,157@8727L6,158@8821L32,158@8804L49,159@8888L63,162@8987L40,164@9085L24,164@9068L41,168@9252L260,166@9147L376,178@9564L67,190@10000L1073,190@9979L1094,219@11341L69,219@11318L92,272@12940L16,402@18030L42,403@18105L54,404@18194L43,405@18275L48,406@18359L44,407@18439L46,408@18521L44,288@13594L67,291@13688L70,294@13784L144,297@13954L63,300@14040L636,320@14711L72,339@15482L56,323@14816L275,330@15124L92,333@15253L73,336@15353L98,343@15598L60,347@15753L71,350@15866L79,353@15977L69,356@16067L56,359@16153L88,366@16441L67,369@16532L61,372@16628L120,375@16788L113,378@16943L57,382@17076L72,385@17181L180,390@17390L64,394@17563L157,284@13424L5300:ConversationScreen.kt#fnibcj"
      );
      int var12 = var10;
      if ((var11 & 1) != 0) {
         var12 |= 6;
      } else if ((var10 & 6) == 0) {
         var12 |= var81.changedInstance(var0) ? 4 : 2;
      }

      if ((var11 & 2) != 0) {
         var12 |= 48;
      } else if ((var10 & 48) == 0) {
         var12 |= var81.changed(var1) ? 32 : 16;
      }

      if ((var11 & 4) != 0) {
         var12 |= 384;
      } else if ((var10 & 384) == 0) {
         var12 |= var81.changedInstance(var2) ? 256 : 128;
      }

      if ((var11 & 8) != 0) {
         var12 |= 3072;
      } else if ((var10 & 3072) == 0) {
         var12 |= var81.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var11 & 16) != 0) {
         var12 |= 24576;
      } else if ((var10 & 24576) == 0) {
         var12 |= var81.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var11 & 32) != 0) {
         var12 |= 196608;
      } else if ((var10 & 196608) == 0) {
         var12 |= var81.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var11 & 64) != 0) {
         var12 |= 1572864;
      } else if ((var10 & 1572864) == 0) {
         var12 |= var81.changedInstance(var6) ? 1048576 : 524288;
      }

      if ((var11 & 128) != 0) {
         var12 |= 12582912;
      } else if ((var10 & 12582912) == 0) {
         var12 |= var81.changedInstance(var7) ? 8388608 : 4194304;
      }

      if ((var11 & 256) != 0) {
         var12 |= 100663296;
      } else if ((var10 & 100663296) == 0) {
         var12 |= var81.changedInstance(var8) ? 67108864 : 33554432;
      }

      if ((var12 & 38347923) == 38347922 && var81.getSkipping()) {
         var81.skipToGroupEnd();
      } else {
         if ((var11 & 2) != 0) {
            var1 = Modifier.Companion;
         }

         if ((var11 & 4) != 0) {
            var81.startReplaceGroup(1223568402);
            ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
            Composer var14 = var81;
            boolean var15 = false;
            int var16 = 0;
            Object var17 = var14.rememberedValue();
            int var18 = 0;
            Object var10000;
            if (var17 == Composer.Companion.getEmpty()) {
               int var19 = 0;
               Object var136 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreen$lambda$1$lambda$0());
               var14.updateRememberedValue(var136);
               var10000 = var136;
            } else {
               var10000 = var17;
            }

            Function0 var13 = (Function0)var10000;
            var81.endReplaceGroup();
            var2 = var13;
         }

         if ((var11 & 8) != 0) {
            var81.startReplaceGroup(1223569906);
            ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
            Composer var90 = var81;
            boolean var98 = false;
            int var107 = 0;
            Object var116 = var90.rememberedValue();
            int var126 = 0;
            Object var1016;
            if (var116 == Composer.Companion.getEmpty()) {
               int var137 = 0;
               Object var138 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreen$lambda$3$lambda$2());
               var90.updateRememberedValue(var138);
               var1016 = var138;
            } else {
               var1016 = var116;
            }

            Function0 var82 = (Function0)var1016;
            var81.endReplaceGroup();
            var3 = var82;
         }

         if ((var11 & 16) != 0) {
            var81.startReplaceGroup(1223571346);
            ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
            Composer var91 = var81;
            boolean var99 = false;
            int var108 = 0;
            Object var117 = var91.rememberedValue();
            int var127 = 0;
            Object var1017;
            if (var117 == Composer.Companion.getEmpty()) {
               int var139 = 0;
               Object var140 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreen$lambda$5$lambda$4());
               var91.updateRememberedValue(var140);
               var1017 = var140;
            } else {
               var1017 = var117;
            }

            Function0 var83 = (Function0)var1017;
            var81.endReplaceGroup();
            var4 = var83;
         }

         if ((var11 & 32) != 0) {
            var81.startReplaceGroup(1223572594);
            ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
            Composer var92 = var81;
            boolean var100 = false;
            int var109 = 0;
            Object var118 = var92.rememberedValue();
            int var128 = 0;
            Object var1018;
            if (var118 == Composer.Companion.getEmpty()) {
               int var141 = 0;
               Object var142 = ((kotlin.jvm.functions.Function1) (recoveredArgument_329_0) -> ConversationScreenKt.ConversationScreen$lambda$7$lambda$6((java.lang.String)recoveredArgument_329_0));
               var92.updateRememberedValue(var142);
               var1018 = var142;
            } else {
               var1018 = var118;
            }

            Function1 var84 = (Function1)var1018;
            var81.endReplaceGroup();
            var5 = var84;
         }

         if ((var11 & 64) != 0) {
            var81.startReplaceGroup(1223574098);
            ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
            Composer var93 = var81;
            boolean var101 = false;
            int var110 = 0;
            Object var119 = var93.rememberedValue();
            int var129 = 0;
            Object var1019;
            if (var119 == Composer.Companion.getEmpty()) {
               int var143 = 0;
               Object var144 = ((kotlin.jvm.functions.Function1) (recoveredArgument_332_0) -> ConversationScreenKt.ConversationScreen$lambda$9$lambda$8((io.intercom.android.sdk.blocks.lib.models.TicketType)recoveredArgument_332_0));
               var93.updateRememberedValue(var144);
               var1019 = var144;
            } else {
               var1019 = var119;
            }

            Function1 var85 = (Function1)var1019;
            var81.endReplaceGroup();
            var6 = var85;
         }

         if ((var11 & 128) != 0) {
            var81.startReplaceGroup(1223575698);
            ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
            Composer var94 = var81;
            boolean var102 = false;
            int var111 = 0;
            Object var120 = var94.rememberedValue();
            int var130 = 0;
            Object var1020;
            if (var120 == Composer.Companion.getEmpty()) {
               int var145 = 0;
               Object var146 = ((kotlin.jvm.functions.Function1) (recoveredArgument_333_0) -> ConversationScreenKt.ConversationScreen$lambda$11$lambda$10((io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem)recoveredArgument_333_0));
               var94.updateRememberedValue(var146);
               var1020 = var146;
            } else {
               var1020 = var120;
            }

            Function1 var86 = (Function1)var1020;
            var81.endReplaceGroup();
            var7 = var86;
         }

         if ((var11 & 256) != 0) {
            var81.startReplaceGroup(1223577554);
            ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
            Composer var95 = var81;
            boolean var103 = false;
            int var112 = 0;
            Object var121 = var95.rememberedValue();
            int var131 = 0;
            Object var1021;
            if (var121 == Composer.Companion.getEmpty()) {
               int var147 = 0;
               Object var148 = ((kotlin.jvm.functions.Function1) (recoveredArgument_334_0) -> ConversationScreenKt.ConversationScreen$lambda$13$lambda$12((java.lang.String)recoveredArgument_334_0));
               var95.updateRememberedValue(var148);
               var1021 = var148;
            } else {
               var1021 = var121;
            }

            Function1 var87 = (Function1)var1021;
            var81.endReplaceGroup();
            var8 = var87;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1550678767, var12, -1, "io.intercom.android.sdk.m5.conversation.ui.ConversationScreen (ConversationScreen.kt:153)");
         }

         CompositionLocal var96 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var104 = 0;
         int var113 = 0;
         ComposerKt.sourceInformationMarkerStart(var81, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var122 = var81.consume(var96);
         ComposerKt.sourceInformationMarkerEnd(var81);
         Context var88 = (Context)var122;
         var81.startReplaceGroup(1223580592);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var114 = var81;
         boolean var123 = false;
         int var132 = 0;
         Object var149 = var114.rememberedValue();
         int var20 = 0;
         Object var1022;
         if (var149 == Composer.Companion.getEmpty()) {
            int var21 = 0;
            Object var159 = new SnackbarHostState();
            var114.updateRememberedValue(var159);
            var1022 = var159;
         } else {
            var1022 = var149;
         }

         SnackbarHostState var105 = (SnackbarHostState)var1022;
         var81.endReplaceGroup();
         SnackbarHostState var97 = var105;
         int var106 = ColorKt.recovered$81a7e7de546cbf77(IntercomTheme.INSTANCE.getColors(var81, IntercomTheme.$stable).recovered$baf23fcaed2b2227());
         Object[] var1023 = new Object[0];
         var81.startReplaceGroup(1223588400);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var133 = var81;
         boolean var150 = false;
         int $i$f$cachex = 0;
         Object it$ivx = var133.rememberedValue();
         int var22/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var10001;
         if (it$ivx == Composer.Companion.getEmpty()) {
            Object[] var44 = var1023;
            int var23 = 0;
            Function0 var45 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreen$lambda$16$lambda$15());
            var1023 = var44;
            Object var176 = var45;
            var133.updateRememberedValue(var176);
            var10001 = var176;
         } else {
            var10001 = it$ivx;
         }

         Function0 var124 = (Function0)var10001;
         var81.endReplaceGroup();
         MutableState var115 = (MutableState)RememberSaveableKt.rememberSaveable(var1023, var124, var81, 48);
         var81.startReplaceGroup(1223590575);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var151 = var81;
         boolean var155 = false;
         int $i$f$cachexx = 0;
         Object it$ivxx = var151.rememberedValue();
         int var177/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var1024;
         if (it$ivxx == Composer.Companion.getEmpty()) {
            int var24 = 0;
            Object var185 = SnapshotStateKt.mutableStateOf$default(null, null, 2, null);
            var151.updateRememberedValue(var185);
            var1024 = var185;
         } else {
            var1024 = it$ivxx;
         }

         MutableState var134 = (MutableState)var1024;
         var81.endReplaceGroup();
         MutableState var125 = var134;
         var81.startReplaceGroup(1223593720);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var156 = var81;
         boolean var162 = false;
         int $i$f$cachexxx = 0;
         Object it$ivxxx = var156.rememberedValue();
         int var186/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
         Object var1025;
         if (it$ivxxx == Composer.Companion.getEmpty()) {
            int var25 = 0;
            Object var196 = SnapshotStateKt.mutableStateOf$default(null, null, 2, null);
            var156.updateRememberedValue(var196);
            var1025 = var196;
         } else {
            var1025 = it$ivxxx;
         }

         MutableState var152 = (MutableState)var1025;
         var81.endReplaceGroup();
         MutableState var135 = var152;
         Object[] var1026 = new Object[0];
         var81.startReplaceGroup(1223596840);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var163 = var81;
         boolean var168 = false;
         int $i$f$cachexxxx = 0;
         Object it$ivxxxx = var163.rememberedValue();
         int var197/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
         Object var1045;
         if (it$ivxxxx == Composer.Companion.getEmpty()) {
            Object[] var539 = var1026;
            int var26 = 0;
            Function0 var571 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreen$lambda$26$lambda$25());
            var1026 = var539;
            Object var206 = var571;
            var163.updateRememberedValue(var206);
            var1045 = var206;
         } else {
            var1045 = it$ivxxxx;
         }

         Function0 var157 = (Function0)var1045;
         var81.endReplaceGroup();
         MutableState var153 = (MutableState)RememberSaveableKt.rememberSaveable(var1026, var157, var81, 48);
         ActivityResultContract var1027 = new PreviewMediaContract();
         var81.startReplaceGroup(1223602420);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var169 = var81;
         boolean var180 = var81.changed(var153) | var81.changedInstance(var0);
         int $i$f$cachexxxxx = 0;
         Object it$ivxxxxx = var169.rememberedValue();
         int var207/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxx */ = 0;
         Object var1046;
         if (!var180 && it$ivxxxxx != Composer.Companion.getEmpty()) {
            var1046 = it$ivxxxxx;
         } else {
            ActivityResultContract var540 = var1027;
            int var27 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_293_0 = var0;
            final androidx.compose.runtime.MutableState recoveredCapture_293_1 = var153;
            Function1 var572 = ((kotlin.jvm.functions.Function1) (recoveredArgument_293_0) -> ConversationScreenKt.ConversationScreen$lambda$31$lambda$30(recoveredCapture_293_0, recoveredCapture_293_1, (java.util.List)recoveredArgument_293_0));
            var1027 = var540;
            Object var28 = var572;
            var169.updateRememberedValue(var28);
            var1046 = var28;
         }

         Function1 var164 = (Function1)var1046;
         var81.endReplaceGroup();
         ManagedActivityResultLauncher var158 = ActivityResultRegistryKt.rememberLauncherForActivityResult(var1027, var164, var81, 0);
         LazyListState var165 = LazyListStateKt.rememberLazyListState(Integer.MAX_VALUE, 0, var81, 6, 2);
         Object var1028 = null;
         var81.startReplaceGroup(1223627169);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var181 = var81;
         boolean var189 = var81.changedInstance(var0) | var81.changed(var115) | var81.changedInstance(var88);
         int $i$f$cachexxxxxx = 0;
         Object it$ivxxxxxx = var181.rememberedValue();
         int var216/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxx */ = 0;
         Object var1047;
         if (!var189 && it$ivxxxxxx != Composer.Companion.getEmpty()) {
            var1047 = it$ivxxxxxx;
         } else {
            Object var541 = null;
            int var224 = 0;
            Function2 var573 = new ConversationScreenKt$ConversationScreen$8$1(var0, var88, var97, var115, var125, var135, null);
            var1028 = var541;
            Object var29 = var573;
            var181.updateRememberedValue(var29);
            var1047 = var29;
         }

         Function2 var170 = (Function2)var1047;
         var81.endReplaceGroup();
         EffectsKt.LaunchedEffect(var1028, var170, var81, 6);
         Object var1029 = null;
         var81.startReplaceGroup(1223669077);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var182 = var81;
         boolean invalid$ivx = false;
         int $i$f$cachexxxxxxx = 0;
         Object it$ivxxxxxxx = var182.rememberedValue();
         int var217/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxx */ = 0;
         Object var1048;
         if (it$ivxxxxxxx == Composer.Companion.getEmpty()) {
            Object var542 = null;
            int var225 = 0;
            Function1 var574 = ((kotlin.jvm.functions.Function1) (recoveredArgument_294_0) -> ConversationScreenKt.ConversationScreen$lambda$35$lambda$34((androidx.compose.runtime.DisposableEffectScope)recoveredArgument_294_0));
            var1029 = var542;
            Object var226 = var574;
            var182.updateRememberedValue(var226);
            var1048 = var226;
         } else {
            var1048 = it$ivxxxxxxx;
         }

         Function1 var171 = (Function1)var1048;
         var81.endReplaceGroup();
         EffectsKt.DisposableEffect(var1029, var171, var81, 54);
         var81.startReplaceGroup(1223671891);
         ComposerKt.sourceInformation(var81, "232@11700L53,234@11810L72,231@11657L236");
         if ((Boolean)ConversationScreen$lambda$17(var115).getFirst()) {
            String var172 = Phrase.from(var88, string.intercom_file_too_big)
               .put("limit", (CharSequence)ConversationScreen$lambda$17(var115).getSecond())
               .format()
               .toString();
            String var1030 = StringResources_androidKt.stringResource(string.intercom_failed_to_send, var81, 0);
            String var1049 = var172;
            var81.startReplaceGroup(1223684088);
            ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
            Composer var191 = var81;
            boolean var201 = var81.changed(var115);
            int $i$f$cachexxxxxxxx = 0;
            Object it$ivxxxxxxxx = var191.rememberedValue();
            int var227/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxx */ = 0;
            Object var10002;
            if (!var201 && it$ivxxxxxxxx != Composer.Companion.getEmpty()) {
               var10002 = it$ivxxxxxxxx;
            } else {
               String var575 = var172;
               String var543 = var1030;
               int var236 = 0;
               final androidx.compose.runtime.MutableState recoveredCapture_295_0 = var115;
               Function0 var46 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreen$lambda$37$lambda$36(recoveredCapture_295_0));
               var1030 = var543;
               var1049 = var575;
               Object var30 = var46;
               var191.updateRememberedValue(var30);
               var10002 = var30;
            }

            Function0 var183 = (Function0)var10002;
            var81.endReplaceGroup();
            UploadSizeLimitDialogKt.UploadSizeLimitDialog(var1030, var1049, var183, var81, 0, 0);
         }

         var81.endReplaceGroup();
         MediaUploadItem var173 = ConversationScreen$lambda$20(var125);
         var81.startReplaceGroup(1223688500);
         ComposerKt.sourceInformation(var81, "");
         if (var173 == null) {
            Object var1032 = null;
         } else {
            MediaUploadItem var192 = var173;
            MediaUploadItem var202 = var192;
            int var211 = 0;
            io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadError var219 = var202.getUploadError();
            var81.startReplaceGroup(1223690850);
            ComposerKt.sourceInformation(var81, "245@12114L94,248@12237L95,251@12362L69,243@12038L408");
            if (var219 != null) {
               io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadError var1031 = var219;
               var81.startReplaceGroup(-79450099);
               ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var228 = var81;
               boolean var237 = var81.changedInstance(var0) | var81.changedInstance(var202);
               int $i$f$cachexxxxxxxx = 0;
               Object it$ivxxxxxxxx = var228.rememberedValue();
               int var32/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxx */ = 0;
               Object var1051;
               if (!var237 && it$ivxxxxxxxx != Composer.Companion.getEmpty()) {
                  var1051 = it$ivxxxxxxxx;
               } else {
                  io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadError var33 = var219;
                  int var34 = 0;
                  final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_296_0 = var0;
                  final io.intercom.android.sdk.m5.conversation.states.MediaUploadItem recoveredCapture_296_1 = var202;
                  Function0 var1050 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreen$lambda$44$lambda$39$lambda$38(recoveredCapture_296_0, recoveredCapture_296_1));
                  var1031 = var33;
                  Object var35 = var1050;
                  var228.updateRememberedValue(var35);
                  var1051 = var35;
               }

               Function0 var36 = (Function0)var1051;
               var81.endReplaceGroup();
               Function0 var1052 = var36;
               var81.startReplaceGroup(-79446162);
               ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var229 = var81;
               boolean var238 = var81.changedInstance(var0) | var81.changedInstance(var202);
               int $i$f$cachexxxxxxxxx = 0;
               Object it$ivxxxxxxxxx = var229.rememberedValue();
               int var266/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxx */ = 0;
               Object var1057;
               if (!var238 && it$ivxxxxxxxxx != Composer.Companion.getEmpty()) {
                  var1057 = it$ivxxxxxxxxx;
               } else {
                  Function0 var37 = var36;
                  io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadError var276 = var1031;
                  int var287 = 0;
                  final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_297_0 = var0;
                  final io.intercom.android.sdk.m5.conversation.states.MediaUploadItem recoveredCapture_297_1 = var202;
                  Function0 var38 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreen$lambda$44$lambda$41$lambda$40(recoveredCapture_297_0, recoveredCapture_297_1));
                  var1031 = var276;
                  var1052 = var37;
                  Object var299 = var38;
                  var229.updateRememberedValue(var299);
                  var1057 = var299;
               }

               Function0 var310 = (Function0)var1057;
               var81.endReplaceGroup();
               Function0 var1058 = var310;
               var81.startReplaceGroup(-79442188);
               ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var230 = var81;
               boolean var239 = false;
               int $i$f$cachexxxxxxxxxx = 0;
               Object it$ivxxxxxxxxxx = var230.rememberedValue();
               int var267/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxx */ = 0;
               Object var10003;
               if (it$ivxxxxxxxxxx == Composer.Companion.getEmpty()) {
                  Function0 var376 = var310;
                  Function0 var344 = var1052;
                  io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadError var277 = var1031;
                  int var288 = 0;
                  final androidx.compose.runtime.MutableState recoveredCapture_298_0 = var125;
                  Function0 var39 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreen$lambda$44$lambda$43$lambda$42(recoveredCapture_298_0));
                  var1031 = var277;
                  var1052 = var344;
                  var1058 = var376;
                  Object var300 = var39;
                  var230.updateRememberedValue(var300);
                  var10003 = var300;
               } else {
                  var10003 = it$ivxxxxxxxxxx;
               }

               Function0 var311 = (Function0)var10003;
               var81.endReplaceGroup();
               UploadErrorDialogKt.UploadErrorDialog(var1031, var1052, var1058, var311, var81, 3072, 0);
            } else {
               ConversationScreen$lambda$21(var125, null);
            }

            var81.endReplaceGroup();
         }

         var81.endReplaceGroup();
         Part var174 = ConversationScreen$lambda$23(var135);
         var81.startReplaceGroup(1223710704);
         ComposerKt.sourceInformation(var81, "*263@12708L79,266@12813L62,262@12658L228");
         if (var174 == null) {
            Object var1035 = null;
         } else {
            Part var193 = var174;
            Part var203 = var193;
            int var212 = 0;
            var81.startReplaceGroup(-79431106);
            ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
            Composer var220 = var81;
            boolean var231 = var81.changedInstance(var0) | var81.changedInstance(var203);
            int $i$f$cachexxxxxxxx = 0;
            Object it$ivxxxxxxxx = var220.rememberedValue();
            int var258/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxx */ = 0;
            Object var1033;
            if (!var231 && it$ivxxxxxxxx != Composer.Companion.getEmpty()) {
               var1033 = it$ivxxxxxxxx;
            } else {
               int var268 = 0;
               final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_299_0 = var0;
               final io.intercom.android.sdk.models.Part recoveredCapture_299_1 = var203;
               Object var278 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreen$lambda$49$lambda$46$lambda$45(recoveredCapture_299_0, recoveredCapture_299_1));
               var220.updateRememberedValue(var278);
               var1033 = var278;
            }

            Function0 var289 = (Function0)var1033;
            var81.endReplaceGroup();
            Function0 var1034 = var289;
            var81.startReplaceGroup(-79427763);
            ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
            Composer var221 = var81;
            boolean var232 = false;
            int $i$f$cachexxxxxxxxx = 0;
            Object it$ivxxxxxxxxx = var221.rememberedValue();
            int var259/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxx */ = 0;
            Object var1054;
            if (it$ivxxxxxxxxx == Composer.Companion.getEmpty()) {
               Function0 var301 = var289;
               int var269 = 0;
               final androidx.compose.runtime.MutableState recoveredCapture_300_0 = var135;
               Function0 var1053 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreen$lambda$49$lambda$48$lambda$47(recoveredCapture_300_0));
               var1034 = var301;
               Object var279 = var1053;
               var221.updateRememberedValue(var279);
               var1054 = var279;
            } else {
               var1054 = it$ivxxxxxxxxx;
            }

            Function0 var290 = (Function0)var1054;
            var81.endReplaceGroup();
            DeleteFailedMessageDialogKt.DeleteFailedMessageDialog(var1034, var290, var81, 48, 0);
         }

         var81.endReplaceGroup();
         State var175 = SnapshotStateKt.collectAsState(var0.getUiState(), null, var81, 0, 1);
         ConversationUiState var184 = (ConversationUiState)var175.getValue();
         var81.startReplaceGroup(1223722608);
         ComposerKt.sourceInformation(var81, "275@13118L295,275@13069L344");
         if (var184 instanceof io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content) {
            NetworkState var1036 = ((io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content)var184).getNetworkState();
            var81.startReplaceGroup(1223726167);
            ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
            Composer var204 = var81;
            boolean invalid$ivxx = var81.changedInstance(var184) | var81.changedInstance(var88) | var81.changedInstance(var0);
            int $i$f$cachexxxxxxxx = 0;
            Object it$ivxxxxxxxx = var204.rememberedValue();
            int var242/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxx */ = 0;
            Object var1055;
            if (!invalid$ivxx && it$ivxxxxxxxx != Composer.Companion.getEmpty()) {
               var1055 = it$ivxxxxxxxx;
            } else {
               NetworkState var544 = var1036;
               int var251 = 0;
               Function2 var576 = new ConversationScreenKt$ConversationScreen$13$1(var184, var97, var88, var0, null);
               var1036 = var544;
               Object var260 = var576;
               var204.updateRememberedValue(var260);
               var1055 = var260;
            }

            Function2 var194 = (Function2)var1055;
            var81.endReplaceGroup();
            EffectsKt.LaunchedEffect(var1036, var194, var81, 0);
         }

         var81.endReplaceGroup();
         SharedFlow var195 = var0.getComposerUiEffect();
         ConversationUiState var214 = var184;
         VoiceTranscriptionState var205 = var214 instanceof io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content ? ((io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content)var184).getVoiceTranscriptionState() : io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Idle.INSTANCE;
         StateFlow var215 = var0.getAmplitudeLevel();
         var81.startReplaceGroup(1223883098);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var243 = var81;
         boolean var252 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxx = 0;
         Object it$ivxxxxxxxx = var243.rememberedValue();
         int var280/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxx */ = 0;
         Object var1037;
         if (!var252 && it$ivxxxxxxxx != Composer.Companion.getEmpty()) {
            var1037 = it$ivxxxxxxxx;
         } else {
            int var291 = 0;
            Object var302 = new ConversationScreenKt$ConversationScreen$14$1(var0);
            var243.updateRememberedValue(var302);
            var1037 = var302;
         }

         KFunction var234 = (KFunction)var1037;
         var81.endReplaceGroup();
         KFunction var223 = var234;
         var81.startReplaceGroup(1223885510);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var253 = var81;
         boolean var262 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxx = 0;
         Object it$ivxxxxxxxxx = var253.rememberedValue();
         int var292/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxx */ = 0;
         Object var1038;
         if (!var262 && it$ivxxxxxxxxx != Composer.Companion.getEmpty()) {
            var1038 = it$ivxxxxxxxxx;
         } else {
            int var303 = 0;
            Object var312 = new ConversationScreenKt$ConversationScreen$15$1(var0);
            var253.updateRememberedValue(var312);
            var1038 = var312;
         }

         KFunction var244 = (KFunction)var1038;
         var81.endReplaceGroup();
         KFunction var235 = var244;
         var81.startReplaceGroup(1223888347);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var263 = var81;
         boolean var272 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxx = var263.rememberedValue();
         int var304/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxx */ = 0;
         Object var1039;
         if (!var272 && it$ivxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var1039 = it$ivxxxxxxxxxx;
         } else {
            int var313 = 0;
            Object var345 = new ConversationScreenKt$ConversationScreen$16$1(var0);
            var263.updateRememberedValue(var345);
            var1039 = var345;
         }

         KFunction var254 = (KFunction)var1039;
         var81.endReplaceGroup();
         KFunction var245 = var254;
         var81.startReplaceGroup(1223890944);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var273 = var81;
         boolean var283 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxx = var273.rememberedValue();
         int var314/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxx */ = 0;
         Object var1040;
         if (!var283 && it$ivxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var1040 = it$ivxxxxxxxxxxx;
         } else {
            int var346 = 0;
            Object var377 = new ConversationScreenKt$ConversationScreen$17$1(var0);
            var273.updateRememberedValue(var377);
            var1040 = var377;
         }

         KFunction var264 = (KFunction)var1040;
         var81.endReplaceGroup();
         KFunction var255 = var264;
         var81.startReplaceGroup(1223893628);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var284 = var81;
         boolean var295 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxx = var284.rememberedValue();
         int var347/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxx */ = 0;
         Object var1041;
         if (!var295 && it$ivxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var1041 = it$ivxxxxxxxxxxxx;
         } else {
            int var378 = 0;
            Object var407 = new ConversationScreenKt$ConversationScreen$18$1(var0);
            var284.updateRememberedValue(var407);
            var1041 = var407;
         }

         KFunction var274 = (KFunction)var1041;
         var81.endReplaceGroup();
         KFunction var265 = var274;
         var81.startReplaceGroup(1223896190);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var296 = var81;
         boolean var307 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxx = var296.rememberedValue();
         int var379/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxx */ = 0;
         Object var1042;
         if (!var307 && it$ivxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var1042 = it$ivxxxxxxxxxxxxx;
         } else {
            int var408 = 0;
            Object var40 = new ConversationScreenKt$ConversationScreen$19$1(var0);
            var296.updateRememberedValue(var40);
            var1042 = var40;
         }

         KFunction var285 = (KFunction)var1042;
         var81.endReplaceGroup();
         KFunction var275 = var285;
         var81.startReplaceGroup(1223898812);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var308 = var81;
         boolean var317 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxx = var308.rememberedValue();
         int var409/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxx */ = 0;
         Object var1043;
         if (!var317 && it$ivxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var1043 = it$ivxxxxxxxxxxxxxx;
         } else {
            int var436 = 0;
            Object var41 = new ConversationScreenKt$ConversationScreen$20$1(var0);
            var308.updateRememberedValue(var41);
            var1043 = var41;
         }

         KFunction var297 = (KFunction)var1043;
         var81.endReplaceGroup();
         KFunction var286 = var297;
         StateFlow var298 = var0.getStreamingMessageState();
         StateFlow var309 = var0.isFinStreamingActive();
         Modifier var1044 = var1;
         ConversationUiState var1056 = var184;
         SnackbarHostState var1059 = var97;
         var81.startReplaceGroup(1223741171);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var350 = var81;
         boolean var381 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxx = var350.rememberedValue();
         int var463/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxx */ = 0;
         Object var1060;
         if (!var381 && it$ivxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var1060 = it$ivxxxxxxxxxxxxxxx;
         } else {
            SnackbarHostState var603 = var97;
            ConversationUiState var577 = var184;
            Modifier var545 = var1;
            int var42 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_301_0 = var0;
            Function1 var47 = ((kotlin.jvm.functions.Function1) (recoveredArgument_301_0) -> ConversationScreenKt.ConversationScreen$lambda$59$lambda$58(recoveredCapture_301_0, (io.intercom.android.sdk.m5.conversation.states.ReplySuggestion)recoveredArgument_301_0));
            var1044 = var545;
            var1056 = var577;
            var1059 = var603;
            Object var43 = var47;
            var350.updateRememberedValue(var43);
            var1060 = var43;
         }

         Function1 var318 = (Function1)var1060;
         var81.endReplaceGroup();
         Function1 var1061 = var318;
         var81.startReplaceGroup(1223744182);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var351 = var81;
         boolean var382 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxx = var351.rememberedValue();
         int var464/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxx */ = 0;
         Object var10004;
         if (!var382 && it$ivxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10004 = it$ivxxxxxxxxxxxxxxxx;
         } else {
            Function1 var629 = var318;
            SnackbarHostState var604 = var1059;
            ConversationUiState var578 = var1056;
            Modifier var546 = var1044;
            int var489 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_302_0 = var0;
            Function1 var48 = ((kotlin.jvm.functions.Function1) (recoveredArgument_302_0) -> ConversationScreenKt.ConversationScreen$lambda$61$lambda$60(recoveredCapture_302_0, (io.intercom.android.sdk.models.ReplyOption)recoveredArgument_302_0));
            var1044 = var546;
            var1056 = var578;
            var1059 = var604;
            var1061 = var629;
            Object var514 = var48;
            var351.updateRememberedValue(var514);
            var10004 = var514;
         }

         Function1 var319 = (Function1)var10004;
         var81.endReplaceGroup();
         Function1 var1062 = var319;
         var81.startReplaceGroup(1223747328);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var352 = var81;
         boolean var383 = var81.changedInstance(var0) | var81.changedInstance(var88);
         int $i$f$cachexxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxx = var352.rememberedValue();
         int var465/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxx */ = 0;
         Object var10005;
         if (!var383 && it$ivxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10005 = it$ivxxxxxxxxxxxxxxxxx;
         } else {
            Function1 var654 = var319;
            Function1 var630 = var1061;
            SnackbarHostState var605 = var1059;
            ConversationUiState var579 = var1056;
            Modifier var547 = var1044;
            int var490 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_303_0 = var0;
            final android.content.Context recoveredCapture_303_1 = var88;
            Function2 var49 = ((kotlin.jvm.functions.Function2) (recoveredArgument_303_0, recoveredArgument_303_1) -> ConversationScreenKt.ConversationScreen$lambda$63$lambda$62(recoveredCapture_303_0, recoveredCapture_303_1, (java.lang.String)recoveredArgument_303_0, (java.util.List)recoveredArgument_303_1));
            var1044 = var547;
            var1056 = var579;
            var1059 = var605;
            var1061 = var630;
            var1062 = var654;
            Object var515 = var49;
            var352.updateRememberedValue(var515);
            var10005 = var515;
         }

         Function2 var320 = (Function2)var10005;
         var81.endReplaceGroup();
         Function2 var1063 = var320;
         var81.startReplaceGroup(1223752687);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var353 = var81;
         boolean var384 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxx = var353.rememberedValue();
         int var466/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10006;
         if (!var384 && it$ivxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10006 = it$ivxxxxxxxxxxxxxxxxxx;
         } else {
            Function2 var678 = var320;
            Function1 var655 = var1062;
            Function1 var631 = var1061;
            SnackbarHostState var606 = var1059;
            ConversationUiState var580 = var1056;
            Modifier var548 = var1044;
            int var491 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_304_0 = var0;
            Function1 var50 = ((kotlin.jvm.functions.Function1) (recoveredArgument_304_0) -> ConversationScreenKt.ConversationScreen$lambda$65$lambda$64(recoveredCapture_304_0, (io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposerInputType)recoveredArgument_304_0));
            var1044 = var548;
            var1056 = var580;
            var1059 = var606;
            var1061 = var631;
            var1062 = var655;
            var1063 = var678;
            Object var516 = var50;
            var353.updateRememberedValue(var516);
            var10006 = var516;
         }

         Function1 var321 = (Function1)var10006;
         var81.endReplaceGroup();
         Function1 var1064 = var321;
         var81.startReplaceGroup(1223756012);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var354 = var81;
         boolean var385 = var81.changed(var153) | var81.changedInstance(var158);
         int $i$f$cachexxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxx = var354.rememberedValue();
         int var467/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10007;
         if (!var385 && it$ivxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10007 = it$ivxxxxxxxxxxxxxxxxxxx;
         } else {
            Function1 var701 = var321;
            Function2 var679 = var1063;
            Function1 var656 = var1062;
            Function1 var632 = var1061;
            SnackbarHostState var607 = var1059;
            ConversationUiState var581 = var1056;
            Modifier var549 = var1044;
            int var492 = 0;
            final androidx.activity.compose.ManagedActivityResultLauncher recoveredCapture_305_0 = var158;
            final androidx.compose.runtime.MutableState recoveredCapture_305_1 = var153;
            Function1 var51 = ((kotlin.jvm.functions.Function1) (recoveredArgument_305_0) -> ConversationScreenKt.ConversationScreen$lambda$67$lambda$66(recoveredCapture_305_0, recoveredCapture_305_1, (io.intercom.android.sdk.blocks.lib.models.Block)recoveredArgument_305_0));
            var1044 = var549;
            var1056 = var581;
            var1059 = var607;
            var1061 = var632;
            var1062 = var656;
            var1063 = var679;
            var1064 = var701;
            Object var517 = var51;
            var354.updateRememberedValue(var517);
            var10007 = var517;
         }

         Function1 var322 = (Function1)var10007;
         var81.endReplaceGroup();
         Function1 var1065 = var322;
         var81.startReplaceGroup(1223776920);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var355 = var81;
         boolean var386 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxxx = var355.rememberedValue();
         int var468/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10008;
         if (!var386 && it$ivxxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10008 = it$ivxxxxxxxxxxxxxxxxxxxx;
         } else {
            Function1 var723 = var322;
            Function1 var702 = var1064;
            Function2 var680 = var1063;
            Function1 var657 = var1062;
            Function1 var633 = var1061;
            SnackbarHostState var608 = var1059;
            ConversationUiState var582 = var1056;
            Modifier var550 = var1044;
            int var493 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_306_0 = var0;
            Function1 var52 = ((kotlin.jvm.functions.Function1) (recoveredArgument_306_0) -> ConversationScreenKt.ConversationScreen$lambda$69$lambda$68(recoveredCapture_306_0, (java.lang.String)recoveredArgument_306_0));
            var1044 = var550;
            var1056 = var582;
            var1059 = var608;
            var1061 = var633;
            var1062 = var657;
            var1063 = var680;
            var1064 = var702;
            var1065 = var723;
            Object var518 = var52;
            var355.updateRememberedValue(var518);
            var10008 = var518;
         }

         Function1 var323 = (Function1)var10008;
         var81.endReplaceGroup();
         Function1 var1066 = var323;
         var81.startReplaceGroup(1223801576);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var356 = var81;
         boolean var387 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxxxx = var356.rememberedValue();
         int var469/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10009;
         if (!var387 && it$ivxxxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10009 = it$ivxxxxxxxxxxxxxxxxxxxxx;
         } else {
            Function1 var744 = var323;
            Function1 var724 = var1065;
            Function1 var703 = var1064;
            Function2 var681 = var1063;
            Function1 var658 = var1062;
            Function1 var634 = var1061;
            SnackbarHostState var609 = var1059;
            ConversationUiState var583 = var1056;
            Modifier var551 = var1044;
            int var494 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_307_0 = var0;
            Function0 var53 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreen$lambda$71$lambda$70(recoveredCapture_307_0));
            var1044 = var551;
            var1056 = var583;
            var1059 = var609;
            var1061 = var634;
            var1062 = var658;
            var1063 = var681;
            var1064 = var703;
            var1065 = var724;
            var1066 = var744;
            Object var519 = var53;
            var356.updateRememberedValue(var519);
            var10009 = var519;
         }

         Function0 var324 = (Function0)var10009;
         var81.endReplaceGroup();
         Function0 var1067 = var324;
         var81.startReplaceGroup(1223780483);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var357 = var81;
         boolean var388 = var81.changedInstance(var88) | var81.changedInstance(var0) | var81.changed(var106);
         int $i$f$cachexxxxxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxxxxx = var357.rememberedValue();
         int var470/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10010;
         if (!var388 && it$ivxxxxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10010 = it$ivxxxxxxxxxxxxxxxxxxxxxx;
         } else {
            Function0 var764 = var324;
            Function1 var745 = var1066;
            Function1 var725 = var1065;
            Function1 var704 = var1064;
            Function2 var682 = var1063;
            Function1 var659 = var1062;
            Function1 var635 = var1061;
            SnackbarHostState var610 = var1059;
            ConversationUiState var584 = var1056;
            Modifier var552 = var1044;
            int var495 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_309_0 = var0;
            final int recoveredCapture_309_1 = var106;
            final android.content.Context recoveredCapture_309_2 = var88;
            Function1 var54 = ((kotlin.jvm.functions.Function1) (recoveredArgument_309_0) -> ConversationScreenKt.ConversationScreen$lambda$74$lambda$73(recoveredCapture_309_0, recoveredCapture_309_1, recoveredCapture_309_2, (java.util.List)recoveredArgument_309_0));
            var1044 = var552;
            var1056 = var584;
            var1059 = var610;
            var1061 = var635;
            var1062 = var659;
            var1063 = var682;
            var1064 = var704;
            var1065 = var725;
            var1066 = var745;
            var1067 = var764;
            Object var520 = var54;
            var357.updateRememberedValue(var520);
            var10010 = var520;
         }

         Function1 var325 = (Function1)var10010;
         var81.endReplaceGroup();
         Function1 var1068 = var325;
         var81.startReplaceGroup(1223790156);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var358 = var81;
         boolean var389 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxxxxxx = var358.rememberedValue();
         int var471/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10011;
         if (!var389 && it$ivxxxxxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10011 = it$ivxxxxxxxxxxxxxxxxxxxxxxx;
         } else {
            Function1 var783 = var325;
            Function0 var765 = var1067;
            Function1 var746 = var1066;
            Function1 var726 = var1065;
            Function1 var705 = var1064;
            Function2 var683 = var1063;
            Function1 var660 = var1062;
            Function1 var636 = var1061;
            SnackbarHostState var611 = var1059;
            ConversationUiState var585 = var1056;
            Modifier var553 = var1044;
            int var496 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_310_0 = var0;
            Function0 var55 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreen$lambda$76$lambda$75(recoveredCapture_310_0));
            var1044 = var553;
            var1056 = var585;
            var1059 = var611;
            var1061 = var636;
            var1062 = var660;
            var1063 = var683;
            var1064 = var705;
            var1065 = var726;
            var1066 = var746;
            var1067 = var765;
            var1068 = var783;
            Object var521 = var55;
            var358.updateRememberedValue(var521);
            var10011 = var521;
         }

         Function0 var326 = (Function0)var10011;
         var81.endReplaceGroup();
         Function0 var1069 = var326;
         var81.startReplaceGroup(1223794265);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var359 = var81;
         boolean var390 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxxxxxxx = var359.rememberedValue();
         int var472/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10012;
         if (!var390 && it$ivxxxxxxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10012 = it$ivxxxxxxxxxxxxxxxxxxxxxxxx;
         } else {
            Function0 var801 = var326;
            Function1 var784 = var1068;
            Function0 var766 = var1067;
            Function1 var747 = var1066;
            Function1 var727 = var1065;
            Function1 var706 = var1064;
            Function2 var684 = var1063;
            Function1 var661 = var1062;
            Function1 var637 = var1061;
            SnackbarHostState var612 = var1059;
            ConversationUiState var586 = var1056;
            Modifier var554 = var1044;
            int var497 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_311_0 = var0;
            Function0 var56 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreen$lambda$78$lambda$77(recoveredCapture_311_0));
            var1044 = var554;
            var1056 = var586;
            var1059 = var612;
            var1061 = var637;
            var1062 = var661;
            var1063 = var684;
            var1064 = var706;
            var1065 = var727;
            var1066 = var747;
            var1067 = var766;
            var1068 = var784;
            var1069 = var801;
            Object var522 = var56;
            var359.updateRememberedValue(var522);
            var10012 = var522;
         }

         Function0 var327 = (Function0)var10012;
         var81.endReplaceGroup();
         Function0 var1070 = var327;
         var81.startReplaceGroup(1223797490);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var360 = var81;
         boolean var391 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxxxxxxxx = var360.rememberedValue();
         int var473/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10013;
         if (!var391 && it$ivxxxxxxxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10013 = it$ivxxxxxxxxxxxxxxxxxxxxxxxxx;
         } else {
            Function0 var818 = var327;
            Function0 var802 = var1069;
            Function1 var785 = var1068;
            Function0 var767 = var1067;
            Function1 var748 = var1066;
            Function1 var728 = var1065;
            Function1 var707 = var1064;
            Function2 var685 = var1063;
            Function1 var662 = var1062;
            Function1 var638 = var1061;
            SnackbarHostState var613 = var1059;
            ConversationUiState var587 = var1056;
            Modifier var555 = var1044;
            int var498 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_312_0 = var0;
            Function0 var57 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreen$lambda$80$lambda$79(recoveredCapture_312_0));
            var1044 = var555;
            var1056 = var587;
            var1059 = var613;
            var1061 = var638;
            var1062 = var662;
            var1063 = var685;
            var1064 = var707;
            var1065 = var728;
            var1066 = var748;
            var1067 = var767;
            var1068 = var785;
            var1069 = var802;
            var1070 = var818;
            Object var523 = var57;
            var360.updateRememberedValue(var523);
            var10013 = var523;
         }

         Function0 var328 = (Function0)var10013;
         var81.endReplaceGroup();
         Function0 var1071 = var328;
         Function0 var10014 = var2;
         var81.startReplaceGroup(1223805292);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var361 = var81;
         boolean var392 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxxxxxxxxx = var361.rememberedValue();
         int var474/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10015;
         if (!var392 && it$ivxxxxxxxxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10015 = it$ivxxxxxxxxxxxxxxxxxxxxxxxxxx;
         } else {
            Function0 var58 = var2;
            Function0 var834 = var328;
            Function0 var819 = var1070;
            Function0 var803 = var1069;
            Function1 var786 = var1068;
            Function0 var768 = var1067;
            Function1 var749 = var1066;
            Function1 var729 = var1065;
            Function1 var708 = var1064;
            Function2 var686 = var1063;
            Function1 var663 = var1062;
            Function1 var639 = var1061;
            SnackbarHostState var614 = var1059;
            ConversationUiState var588 = var1056;
            Modifier var556 = var1044;
            int var499 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_313_0 = var0;
            Function0 var59 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreen$lambda$82$lambda$81(recoveredCapture_313_0));
            var1044 = var556;
            var1056 = var588;
            var1059 = var614;
            var1061 = var639;
            var1062 = var663;
            var1063 = var686;
            var1064 = var708;
            var1065 = var729;
            var1066 = var749;
            var1067 = var768;
            var1068 = var786;
            var1069 = var803;
            var1070 = var819;
            var1071 = var834;
            var10014 = var58;
            Object var524 = var59;
            var361.updateRememberedValue(var524);
            var10015 = var524;
         }

         Function0 var329 = (Function0)var10015;
         var81.endReplaceGroup();
         Function0 var1072 = var329;
         Function0 var10016 = var3;
         var81.startReplaceGroup(1223810263);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var362 = var81;
         boolean var393 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxx = var362.rememberedValue();
         int var475/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10017;
         if (!var393 && it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10017 = it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxx;
         } else {
            Function0 var60 = var3;
            Function0 var863 = var329;
            Function0 var849 = var10014;
            Function0 var835 = var1071;
            Function0 var820 = var1070;
            Function0 var804 = var1069;
            Function1 var787 = var1068;
            Function0 var769 = var1067;
            Function1 var750 = var1066;
            Function1 var730 = var1065;
            Function1 var709 = var1064;
            Function2 var687 = var1063;
            Function1 var664 = var1062;
            Function1 var640 = var1061;
            SnackbarHostState var615 = var1059;
            ConversationUiState var589 = var1056;
            Modifier var557 = var1044;
            int var500 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_314_0 = var0;
            Function1 var61 = ((kotlin.jvm.functions.Function1) (recoveredArgument_314_0) -> ConversationScreenKt.ConversationScreen$lambda$84$lambda$83(recoveredCapture_314_0, (io.intercom.android.sdk.models.Part)recoveredArgument_314_0));
            var1044 = var557;
            var1056 = var589;
            var1059 = var615;
            var1061 = var640;
            var1062 = var664;
            var1063 = var687;
            var1064 = var709;
            var1065 = var730;
            var1066 = var750;
            var1067 = var769;
            var1068 = var787;
            var1069 = var804;
            var1070 = var820;
            var1071 = var835;
            var10014 = var849;
            var1072 = var863;
            var10016 = var60;
            Object var525 = var61;
            var362.updateRememberedValue(var525);
            var10017 = var525;
         }

         Function1 var330 = (Function1)var10017;
         var81.endReplaceGroup();
         Function1 var1073 = var330;
         var81.startReplaceGroup(1223813887);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var363 = var81;
         boolean var394 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxx = var363.rememberedValue();
         int var476/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10018;
         if (!var394 && it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10018 = it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
         } else {
            Function1 var890 = var330;
            Function0 var877 = var10016;
            Function0 var864 = var1072;
            Function0 var850 = var10014;
            Function0 var836 = var1071;
            Function0 var821 = var1070;
            Function0 var805 = var1069;
            Function1 var788 = var1068;
            Function0 var770 = var1067;
            Function1 var751 = var1066;
            Function1 var731 = var1065;
            Function1 var710 = var1064;
            Function2 var688 = var1063;
            Function1 var665 = var1062;
            Function1 var641 = var1061;
            SnackbarHostState var616 = var1059;
            ConversationUiState var590 = var1056;
            Modifier var558 = var1044;
            int var501 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_315_0 = var0;
            Function1 var62 = ((kotlin.jvm.functions.Function1) (recoveredArgument_315_0) -> ConversationScreenKt.ConversationScreen$lambda$86$lambda$85(recoveredCapture_315_0, (io.intercom.android.sdk.models.Part)recoveredArgument_315_0));
            var1044 = var558;
            var1056 = var590;
            var1059 = var616;
            var1061 = var641;
            var1062 = var665;
            var1063 = var688;
            var1064 = var710;
            var1065 = var731;
            var1066 = var751;
            var1067 = var770;
            var1068 = var788;
            var1069 = var805;
            var1070 = var821;
            var1071 = var836;
            var10014 = var850;
            var1072 = var864;
            var10016 = var877;
            var1073 = var890;
            Object var526 = var62;
            var363.updateRememberedValue(var526);
            var10018 = var526;
         }

         Function1 var331 = (Function1)var10018;
         var81.endReplaceGroup();
         Function1 var1074 = var331;
         var81.startReplaceGroup(1223817429);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var364 = var81;
         boolean var395 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = var364.rememberedValue();
         int var477/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10019;
         if (!var395 && it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10019 = it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
         } else {
            Function1 var903 = var331;
            Function1 var891 = var1073;
            Function0 var878 = var10016;
            Function0 var865 = var1072;
            Function0 var851 = var10014;
            Function0 var837 = var1071;
            Function0 var822 = var1070;
            Function0 var806 = var1069;
            Function1 var789 = var1068;
            Function0 var771 = var1067;
            Function1 var752 = var1066;
            Function1 var732 = var1065;
            Function1 var711 = var1064;
            Function2 var689 = var1063;
            Function1 var666 = var1062;
            Function1 var642 = var1061;
            SnackbarHostState var617 = var1059;
            ConversationUiState var591 = var1056;
            Modifier var559 = var1044;
            int var502 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_317_0 = var0;
            Function1 var63 = ((kotlin.jvm.functions.Function1) (recoveredArgument_317_0) -> ConversationScreenKt.ConversationScreen$lambda$88$lambda$87(recoveredCapture_317_0, (io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData)recoveredArgument_317_0));
            var1044 = var559;
            var1056 = var591;
            var1059 = var617;
            var1061 = var642;
            var1062 = var666;
            var1063 = var689;
            var1064 = var711;
            var1065 = var732;
            var1066 = var752;
            var1067 = var771;
            var1068 = var789;
            var1069 = var806;
            var1070 = var822;
            var1071 = var837;
            var10014 = var851;
            var1072 = var865;
            var10016 = var878;
            var1073 = var891;
            var1074 = var903;
            Object var527 = var63;
            var364.updateRememberedValue(var527);
            var10019 = var527;
         }

         Function1 var332 = (Function1)var10019;
         var81.endReplaceGroup();
         Function1 var1075 = var332;
         var81.startReplaceGroup(1223820296);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var365 = var81;
         boolean var396 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = var365.rememberedValue();
         int var478/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10020;
         if (!var396 && it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10020 = it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
         } else {
            Function1 var915 = var332;
            Function1 var904 = var1074;
            Function1 var892 = var1073;
            Function0 var879 = var10016;
            Function0 var866 = var1072;
            Function0 var852 = var10014;
            Function0 var838 = var1071;
            Function0 var823 = var1070;
            Function0 var807 = var1069;
            Function1 var790 = var1068;
            Function0 var772 = var1067;
            Function1 var753 = var1066;
            Function1 var733 = var1065;
            Function1 var712 = var1064;
            Function2 var690 = var1063;
            Function1 var667 = var1062;
            Function1 var643 = var1061;
            SnackbarHostState var618 = var1059;
            ConversationUiState var592 = var1056;
            Modifier var560 = var1044;
            int var503 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_318_0 = var0;
            Function0 var64 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreen$lambda$90$lambda$89(recoveredCapture_318_0));
            var1044 = var560;
            var1056 = var592;
            var1059 = var618;
            var1061 = var643;
            var1062 = var667;
            var1063 = var690;
            var1064 = var712;
            var1065 = var733;
            var1066 = var753;
            var1067 = var772;
            var1068 = var790;
            var1069 = var807;
            var1070 = var823;
            var1071 = var838;
            var10014 = var852;
            var1072 = var866;
            var10016 = var879;
            var1073 = var892;
            var1074 = var904;
            var1075 = var915;
            Object var528 = var64;
            var365.updateRememberedValue(var528);
            var10020 = var528;
         }

         Function0 var333 = (Function0)var10020;
         var81.endReplaceGroup();
         Function0 var1076 = var333;
         var81.startReplaceGroup(1223823080);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var366 = var81;
         boolean var397 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = var366.rememberedValue();
         int var479/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10021;
         if (!var397 && it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10021 = it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
         } else {
            Function0 var926 = var333;
            Function1 var916 = var1075;
            Function1 var905 = var1074;
            Function1 var893 = var1073;
            Function0 var880 = var10016;
            Function0 var867 = var1072;
            Function0 var853 = var10014;
            Function0 var839 = var1071;
            Function0 var824 = var1070;
            Function0 var808 = var1069;
            Function1 var791 = var1068;
            Function0 var773 = var1067;
            Function1 var754 = var1066;
            Function1 var734 = var1065;
            Function1 var713 = var1064;
            Function2 var691 = var1063;
            Function1 var668 = var1062;
            Function1 var644 = var1061;
            SnackbarHostState var619 = var1059;
            ConversationUiState var593 = var1056;
            Modifier var561 = var1044;
            int var504 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_319_0 = var0;
            Function1 var65 = ((kotlin.jvm.functions.Function1) (recoveredArgument_319_0) -> ConversationScreenKt.ConversationScreen$lambda$92$lambda$91(recoveredCapture_319_0, (io.intercom.android.sdk.m5.conversation.states.AttributeData)recoveredArgument_319_0));
            var1044 = var561;
            var1056 = var593;
            var1059 = var619;
            var1061 = var644;
            var1062 = var668;
            var1063 = var691;
            var1064 = var713;
            var1065 = var734;
            var1066 = var754;
            var1067 = var773;
            var1068 = var791;
            var1069 = var808;
            var1070 = var824;
            var1071 = var839;
            var10014 = var853;
            var1072 = var867;
            var10016 = var880;
            var1073 = var893;
            var1074 = var905;
            var1075 = var916;
            var1076 = var926;
            Object var529 = var65;
            var366.updateRememberedValue(var529);
            var10021 = var529;
         }

         Function1 var334 = (Function1)var10021;
         var81.endReplaceGroup();
         Function1 var1077 = var334;
         Function0 var10022 = var4;
         Function1 var10023 = var5;
         Function1 var10024 = var7;
         Function1 var10025 = var6;
         var81.startReplaceGroup(1223832275);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var367 = var81;
         boolean var398 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = var367.rememberedValue();
         int var480/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10026;
         if (!var398 && it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10026 = it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
         } else {
            Function1 var69 = var6;
            Function1 var68 = var7;
            Function1 var67 = var5;
            Function0 var66 = var4;
            Function1 var936 = var334;
            Function0 var927 = var1076;
            Function1 var917 = var1075;
            Function1 var906 = var1074;
            Function1 var894 = var1073;
            Function0 var881 = var10016;
            Function0 var868 = var1072;
            Function0 var854 = var10014;
            Function0 var840 = var1071;
            Function0 var825 = var1070;
            Function0 var809 = var1069;
            Function1 var792 = var1068;
            Function0 var774 = var1067;
            Function1 var755 = var1066;
            Function1 var735 = var1065;
            Function1 var714 = var1064;
            Function2 var692 = var1063;
            Function1 var669 = var1062;
            Function1 var645 = var1061;
            SnackbarHostState var620 = var1059;
            ConversationUiState var594 = var1056;
            Modifier var562 = var1044;
            int var505 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_320_0 = var0;
            Function1 var70 = ((kotlin.jvm.functions.Function1) (recoveredArgument_320_0) -> ConversationScreenKt.ConversationScreen$lambda$94$lambda$93(recoveredCapture_320_0, (java.lang.String)recoveredArgument_320_0));
            var1044 = var562;
            var1056 = var594;
            var1059 = var620;
            var1061 = var645;
            var1062 = var669;
            var1063 = var692;
            var1064 = var714;
            var1065 = var735;
            var1066 = var755;
            var1067 = var774;
            var1068 = var792;
            var1069 = var809;
            var1070 = var825;
            var1071 = var840;
            var10014 = var854;
            var1072 = var868;
            var10016 = var881;
            var1073 = var894;
            var1074 = var906;
            var1075 = var917;
            var1076 = var927;
            var1077 = var936;
            var10022 = var66;
            var10023 = var67;
            var10024 = var68;
            var10025 = var69;
            Object var530 = var70;
            var367.updateRememberedValue(var530);
            var10026 = var530;
         }

         Function1 var335 = (Function1)var10026;
         var81.endReplaceGroup();
         Function1 var1078 = var335;
         var81.startReplaceGroup(1223835181);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var368 = var81;
         boolean var399 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = var368.rememberedValue();
         int var481/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10027;
         if (!var399 && it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10027 = it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
         } else {
            Function1 var977 = var335;
            Function1 var969 = var10025;
            Function1 var961 = var10024;
            Function1 var953 = var10023;
            Function0 var945 = var10022;
            Function1 var937 = var1077;
            Function0 var928 = var1076;
            Function1 var918 = var1075;
            Function1 var907 = var1074;
            Function1 var895 = var1073;
            Function0 var882 = var10016;
            Function0 var869 = var1072;
            Function0 var855 = var10014;
            Function0 var841 = var1071;
            Function0 var826 = var1070;
            Function0 var810 = var1069;
            Function1 var793 = var1068;
            Function0 var775 = var1067;
            Function1 var756 = var1066;
            Function1 var736 = var1065;
            Function1 var715 = var1064;
            Function2 var693 = var1063;
            Function1 var670 = var1062;
            Function1 var646 = var1061;
            SnackbarHostState var621 = var1059;
            ConversationUiState var595 = var1056;
            Modifier var563 = var1044;
            int var506 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_321_0 = var0;
            Function1 var71 = ((kotlin.jvm.functions.Function1) (recoveredArgument_321_0) -> ConversationScreenKt.ConversationScreen$lambda$96$lambda$95(recoveredCapture_321_0, (io.intercom.android.sdk.m5.conversation.metrics.MetricData)recoveredArgument_321_0));
            var1044 = var563;
            var1056 = var595;
            var1059 = var621;
            var1061 = var646;
            var1062 = var670;
            var1063 = var693;
            var1064 = var715;
            var1065 = var736;
            var1066 = var756;
            var1067 = var775;
            var1068 = var793;
            var1069 = var810;
            var1070 = var826;
            var1071 = var841;
            var10014 = var855;
            var1072 = var869;
            var10016 = var882;
            var1073 = var895;
            var1074 = var907;
            var1075 = var918;
            var1076 = var928;
            var1077 = var937;
            var10022 = var945;
            var10023 = var953;
            var10024 = var961;
            var10025 = var969;
            var1078 = var977;
            Object var531 = var71;
            var368.updateRememberedValue(var531);
            var10027 = var531;
         }

         Function1 var336 = (Function1)var10027;
         var81.endReplaceGroup();
         Function1 var1079 = var336;
         var81.startReplaceGroup(1223838312);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var369 = var81;
         boolean var400 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = var369.rememberedValue();
         int var482/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10028;
         if (!var400 && it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10028 = it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
         } else {
            Function1 var985 = var336;
            Function1 var978 = var1078;
            Function1 var970 = var10025;
            Function1 var962 = var10024;
            Function1 var954 = var10023;
            Function0 var946 = var10022;
            Function1 var938 = var1077;
            Function0 var929 = var1076;
            Function1 var919 = var1075;
            Function1 var908 = var1074;
            Function1 var896 = var1073;
            Function0 var883 = var10016;
            Function0 var870 = var1072;
            Function0 var856 = var10014;
            Function0 var842 = var1071;
            Function0 var827 = var1070;
            Function0 var811 = var1069;
            Function1 var794 = var1068;
            Function0 var776 = var1067;
            Function1 var757 = var1066;
            Function1 var737 = var1065;
            Function1 var716 = var1064;
            Function2 var694 = var1063;
            Function1 var671 = var1062;
            Function1 var647 = var1061;
            SnackbarHostState var622 = var1059;
            ConversationUiState var596 = var1056;
            Modifier var564 = var1044;
            int var507 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_322_0 = var0;
            Function1 var72 = ((kotlin.jvm.functions.Function1) (recoveredArgument_322_0) -> ConversationScreenKt.ConversationScreen$lambda$98$lambda$97(recoveredCapture_322_0, (io.intercom.android.sdk.m5.conversation.usecase.ConversationScrolledState)recoveredArgument_322_0));
            var1044 = var564;
            var1056 = var596;
            var1059 = var622;
            var1061 = var647;
            var1062 = var671;
            var1063 = var694;
            var1064 = var716;
            var1065 = var737;
            var1066 = var757;
            var1067 = var776;
            var1068 = var794;
            var1069 = var811;
            var1070 = var827;
            var1071 = var842;
            var10014 = var856;
            var1072 = var870;
            var10016 = var883;
            var1073 = var896;
            var1074 = var908;
            var1075 = var919;
            var1076 = var929;
            var1077 = var938;
            var10022 = var946;
            var10023 = var954;
            var10024 = var962;
            var10025 = var970;
            var1078 = var978;
            var1079 = var985;
            Object var532 = var72;
            var369.updateRememberedValue(var532);
            var10028 = var532;
         }

         Function1 var337 = (Function1)var10028;
         var81.endReplaceGroup();
         Function1 var1080 = var337;
         var81.startReplaceGroup(1223843425);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var370 = var81;
         boolean var401 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = var370.rememberedValue();
         int var483/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10029;
         if (!var401 && it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10029 = it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
         } else {
            Function1 var992 = var337;
            Function1 var986 = var1079;
            Function1 var979 = var1078;
            Function1 var971 = var10025;
            Function1 var963 = var10024;
            Function1 var955 = var10023;
            Function0 var947 = var10022;
            Function1 var939 = var1077;
            Function0 var930 = var1076;
            Function1 var920 = var1075;
            Function1 var909 = var1074;
            Function1 var897 = var1073;
            Function0 var884 = var10016;
            Function0 var871 = var1072;
            Function0 var857 = var10014;
            Function0 var843 = var1071;
            Function0 var828 = var1070;
            Function0 var812 = var1069;
            Function1 var795 = var1068;
            Function0 var777 = var1067;
            Function1 var758 = var1066;
            Function1 var738 = var1065;
            Function1 var717 = var1064;
            Function2 var695 = var1063;
            Function1 var672 = var1062;
            Function1 var648 = var1061;
            SnackbarHostState var623 = var1059;
            ConversationUiState var597 = var1056;
            Modifier var565 = var1044;
            int var508 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_324_0 = var0;
            Function1 var73 = ((kotlin.jvm.functions.Function1) (recoveredArgument_324_0) -> ConversationScreenKt.ConversationScreen$lambda$100$lambda$99(recoveredCapture_324_0, ((java.lang.Integer)recoveredArgument_324_0).intValue()));
            var1044 = var565;
            var1056 = var597;
            var1059 = var623;
            var1061 = var648;
            var1062 = var672;
            var1063 = var695;
            var1064 = var717;
            var1065 = var738;
            var1066 = var758;
            var1067 = var777;
            var1068 = var795;
            var1069 = var812;
            var1070 = var828;
            var1071 = var843;
            var10014 = var857;
            var1072 = var871;
            var10016 = var884;
            var1073 = var897;
            var1074 = var909;
            var1075 = var920;
            var1076 = var930;
            var1077 = var939;
            var10022 = var947;
            var10023 = var955;
            var10024 = var963;
            var10025 = var971;
            var1078 = var979;
            var1079 = var986;
            var1080 = var992;
            Object var533 = var73;
            var370.updateRememberedValue(var533);
            var10029 = var533;
         }

         Function1 var338 = (Function1)var10029;
         var81.endReplaceGroup();
         Function1 var1081 = var338;
         var81.startReplaceGroup(1223848329);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var371 = var81;
         boolean var402 = (var12 & 234881024) == 67108864;
         int $i$f$cachexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = var371.rememberedValue();
         int var484/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10030;
         if (!var402 && it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10030 = it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
         } else {
            Function1 var998 = var338;
            Function1 var993 = var1080;
            Function1 var987 = var1079;
            Function1 var980 = var1078;
            Function1 var972 = var10025;
            Function1 var964 = var10024;
            Function1 var956 = var10023;
            Function0 var948 = var10022;
            Function1 var940 = var1077;
            Function0 var931 = var1076;
            Function1 var921 = var1075;
            Function1 var910 = var1074;
            Function1 var898 = var1073;
            Function0 var885 = var10016;
            Function0 var872 = var1072;
            Function0 var858 = var10014;
            Function0 var844 = var1071;
            Function0 var829 = var1070;
            Function0 var813 = var1069;
            Function1 var796 = var1068;
            Function0 var778 = var1067;
            Function1 var759 = var1066;
            Function1 var739 = var1065;
            Function1 var718 = var1064;
            Function2 var696 = var1063;
            Function1 var673 = var1062;
            Function1 var649 = var1061;
            SnackbarHostState var624 = var1059;
            ConversationUiState var598 = var1056;
            Modifier var566 = var1044;
            int var509 = 0;
            final kotlin.jvm.functions.Function1 recoveredCapture_325_0 = var8;
            Function1 var74 = ((kotlin.jvm.functions.Function1) (recoveredArgument_325_0) -> ConversationScreenKt.ConversationScreen$lambda$102$lambda$101(recoveredCapture_325_0, (java.lang.String)recoveredArgument_325_0));
            var1044 = var566;
            var1056 = var598;
            var1059 = var624;
            var1061 = var649;
            var1062 = var673;
            var1063 = var696;
            var1064 = var718;
            var1065 = var739;
            var1066 = var759;
            var1067 = var778;
            var1068 = var796;
            var1069 = var813;
            var1070 = var829;
            var1071 = var844;
            var10014 = var858;
            var1072 = var872;
            var10016 = var885;
            var1073 = var898;
            var1074 = var910;
            var1075 = var921;
            var1076 = var931;
            var1077 = var940;
            var10022 = var948;
            var10023 = var956;
            var10024 = var964;
            var10025 = var972;
            var1078 = var980;
            var1079 = var987;
            var1080 = var993;
            var1081 = var998;
            Object var534 = var74;
            var371.updateRememberedValue(var534);
            var10030 = var534;
         }

         Function1 var339 = (Function1)var10030;
         var81.endReplaceGroup();
         Function1 var1082 = var339;
         LazyListState var10031 = var165;
         var81.startReplaceGroup(1223852600);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var372 = var81;
         boolean var403 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = var372.rememberedValue();
         int var485/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10032;
         if (!var403 && it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10032 = it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
         } else {
            LazyListState var75 = var165;
            Function1 var1003 = var339;
            Function1 var999 = var1081;
            Function1 var994 = var1080;
            Function1 var988 = var1079;
            Function1 var981 = var1078;
            Function1 var973 = var10025;
            Function1 var965 = var10024;
            Function1 var957 = var10023;
            Function0 var949 = var10022;
            Function1 var941 = var1077;
            Function0 var932 = var1076;
            Function1 var922 = var1075;
            Function1 var911 = var1074;
            Function1 var899 = var1073;
            Function0 var886 = var10016;
            Function0 var873 = var1072;
            Function0 var859 = var10014;
            Function0 var845 = var1071;
            Function0 var830 = var1070;
            Function0 var814 = var1069;
            Function1 var797 = var1068;
            Function0 var779 = var1067;
            Function1 var760 = var1066;
            Function1 var740 = var1065;
            Function1 var719 = var1064;
            Function2 var697 = var1063;
            Function1 var674 = var1062;
            Function1 var650 = var1061;
            SnackbarHostState var625 = var1059;
            ConversationUiState var599 = var1056;
            Modifier var567 = var1044;
            int var510 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_326_0 = var0;
            Function0 var76 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreen$lambda$104$lambda$103(recoveredCapture_326_0));
            var1044 = var567;
            var1056 = var599;
            var1059 = var625;
            var1061 = var650;
            var1062 = var674;
            var1063 = var697;
            var1064 = var719;
            var1065 = var740;
            var1066 = var760;
            var1067 = var779;
            var1068 = var797;
            var1069 = var814;
            var1070 = var830;
            var1071 = var845;
            var10014 = var859;
            var1072 = var873;
            var10016 = var886;
            var1073 = var899;
            var1074 = var911;
            var1075 = var922;
            var1076 = var932;
            var1077 = var941;
            var10022 = var949;
            var10023 = var957;
            var10024 = var965;
            var10025 = var973;
            var1078 = var981;
            var1079 = var988;
            var1080 = var994;
            var1081 = var999;
            var1082 = var1003;
            var10031 = var75;
            Object var535 = var76;
            var372.updateRememberedValue(var535);
            var10032 = var535;
         }

         Function0 var340 = (Function0)var10032;
         var81.endReplaceGroup();
         Function0 var1083 = var340;
         var81.startReplaceGroup(1223856068);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var373 = var81;
         boolean var404 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = var373.rememberedValue();
         int var486/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10033;
         if (!var404 && it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10033 = it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
         } else {
            Function0 var1010 = var340;
            LazyListState var1007 = var10031;
            Function1 var1004 = var1082;
            Function1 var1000 = var1081;
            Function1 var995 = var1080;
            Function1 var989 = var1079;
            Function1 var982 = var1078;
            Function1 var974 = var10025;
            Function1 var966 = var10024;
            Function1 var958 = var10023;
            Function0 var950 = var10022;
            Function1 var942 = var1077;
            Function0 var933 = var1076;
            Function1 var923 = var1075;
            Function1 var912 = var1074;
            Function1 var900 = var1073;
            Function0 var887 = var10016;
            Function0 var874 = var1072;
            Function0 var860 = var10014;
            Function0 var846 = var1071;
            Function0 var831 = var1070;
            Function0 var815 = var1069;
            Function1 var798 = var1068;
            Function0 var780 = var1067;
            Function1 var761 = var1066;
            Function1 var741 = var1065;
            Function1 var720 = var1064;
            Function2 var698 = var1063;
            Function1 var675 = var1062;
            Function1 var651 = var1061;
            SnackbarHostState var626 = var1059;
            ConversationUiState var600 = var1056;
            Modifier var568 = var1044;
            int var511 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_327_0 = var0;
            Function2 var77 = ((kotlin.jvm.functions.Function2) (recoveredArgument_327_0, recoveredArgument_327_1) -> ConversationScreenKt.ConversationScreen$lambda$106$lambda$105(recoveredCapture_327_0, (java.util.List)recoveredArgument_327_0, (java.util.List)recoveredArgument_327_1));
            var1044 = var568;
            var1056 = var600;
            var1059 = var626;
            var1061 = var651;
            var1062 = var675;
            var1063 = var698;
            var1064 = var720;
            var1065 = var741;
            var1066 = var761;
            var1067 = var780;
            var1068 = var798;
            var1069 = var815;
            var1070 = var831;
            var1071 = var846;
            var10014 = var860;
            var1072 = var874;
            var10016 = var887;
            var1073 = var900;
            var1074 = var912;
            var1075 = var923;
            var1076 = var933;
            var1077 = var942;
            var10022 = var950;
            var10023 = var958;
            var10024 = var966;
            var10025 = var974;
            var1078 = var982;
            var1079 = var989;
            var1080 = var995;
            var1081 = var1000;
            var1082 = var1004;
            var10031 = var1007;
            var1083 = var1010;
            Object var536 = var77;
            var373.updateRememberedValue(var536);
            var10033 = var536;
         }

         Function2 var341 = (Function2)var10033;
         var81.endReplaceGroup();
         Function2 var1084 = var341;
         var81.startReplaceGroup(1223862640);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var374 = var81;
         boolean var405 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = var374.rememberedValue();
         int var487/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10034;
         if (!var405 && it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10034 = it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
         } else {
            Function2 var1013 = var341;
            Function0 var1011 = var1083;
            LazyListState var1008 = var10031;
            Function1 var1005 = var1082;
            Function1 var1001 = var1081;
            Function1 var996 = var1080;
            Function1 var990 = var1079;
            Function1 var983 = var1078;
            Function1 var975 = var10025;
            Function1 var967 = var10024;
            Function1 var959 = var10023;
            Function0 var951 = var10022;
            Function1 var943 = var1077;
            Function0 var934 = var1076;
            Function1 var924 = var1075;
            Function1 var913 = var1074;
            Function1 var901 = var1073;
            Function0 var888 = var10016;
            Function0 var875 = var1072;
            Function0 var861 = var10014;
            Function0 var847 = var1071;
            Function0 var832 = var1070;
            Function0 var816 = var1069;
            Function1 var799 = var1068;
            Function0 var781 = var1067;
            Function1 var762 = var1066;
            Function1 var742 = var1065;
            Function1 var721 = var1064;
            Function2 var699 = var1063;
            Function1 var676 = var1062;
            Function1 var652 = var1061;
            SnackbarHostState var627 = var1059;
            ConversationUiState var601 = var1056;
            Modifier var569 = var1044;
            int var512 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_328_0 = var0;
            Function0 var78 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreen$lambda$108$lambda$107(recoveredCapture_328_0));
            var1044 = var569;
            var1056 = var601;
            var1059 = var627;
            var1061 = var652;
            var1062 = var676;
            var1063 = var699;
            var1064 = var721;
            var1065 = var742;
            var1066 = var762;
            var1067 = var781;
            var1068 = var799;
            var1069 = var816;
            var1070 = var832;
            var1071 = var847;
            var10014 = var861;
            var1072 = var875;
            var10016 = var888;
            var1073 = var901;
            var1074 = var913;
            var1075 = var924;
            var1076 = var934;
            var1077 = var943;
            var10022 = var951;
            var10023 = var959;
            var10024 = var967;
            var10025 = var975;
            var1078 = var983;
            var1079 = var990;
            var1080 = var996;
            var1081 = var1001;
            var1082 = var1005;
            var10031 = var1008;
            var1083 = var1011;
            var1084 = var1013;
            Object var537 = var78;
            var374.updateRememberedValue(var537);
            var10034 = var537;
         }

         Function0 var342 = (Function0)var10034;
         var81.endReplaceGroup();
         Function0 var1085 = var342;
         SharedFlow var10035 = var195;
         var81.startReplaceGroup(1223868269);
         ComposerKt.sourceInformation(var81, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var375 = var81;
         boolean var406 = var81.changedInstance(var0);
         int $i$f$cachexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = 0;
         Object it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx = var375.rememberedValue();
         int var488/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx */ = 0;
         Object var10036;
         if (!var406 && it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
            var10036 = it$ivxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
         } else {
            SharedFlow var79 = var195;
            Function0 var1015 = var342;
            Function2 var1014 = var1084;
            Function0 var1012 = var1083;
            LazyListState var1009 = var10031;
            Function1 var1006 = var1082;
            Function1 var1002 = var1081;
            Function1 var997 = var1080;
            Function1 var991 = var1079;
            Function1 var984 = var1078;
            Function1 var976 = var10025;
            Function1 var968 = var10024;
            Function1 var960 = var10023;
            Function0 var952 = var10022;
            Function1 var944 = var1077;
            Function0 var935 = var1076;
            Function1 var925 = var1075;
            Function1 var914 = var1074;
            Function1 var902 = var1073;
            Function0 var889 = var10016;
            Function0 var876 = var1072;
            Function0 var862 = var10014;
            Function0 var848 = var1071;
            Function0 var833 = var1070;
            Function0 var817 = var1069;
            Function1 var800 = var1068;
            Function0 var782 = var1067;
            Function1 var763 = var1066;
            Function1 var743 = var1065;
            Function1 var722 = var1064;
            Function2 var700 = var1063;
            Function1 var677 = var1062;
            Function1 var653 = var1061;
            SnackbarHostState var628 = var1059;
            ConversationUiState var602 = var1056;
            Modifier var570 = var1044;
            int var513 = 0;
            final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_330_0 = var0;
            Function2 var80 = ((kotlin.jvm.functions.Function2) (recoveredArgument_330_0, recoveredArgument_330_1) -> ConversationScreenKt.ConversationScreen$lambda$110$lambda$109(recoveredCapture_330_0, ((java.lang.Boolean)recoveredArgument_330_0).booleanValue(), ((java.lang.Boolean)recoveredArgument_330_1).booleanValue()));
            var1044 = var570;
            var1056 = var602;
            var1059 = var628;
            var1061 = var653;
            var1062 = var677;
            var1063 = var700;
            var1064 = var722;
            var1065 = var743;
            var1066 = var763;
            var1067 = var782;
            var1068 = var800;
            var1069 = var817;
            var1070 = var833;
            var1071 = var848;
            var10014 = var862;
            var1072 = var876;
            var10016 = var889;
            var1073 = var902;
            var1074 = var914;
            var1075 = var925;
            var1076 = var935;
            var1077 = var944;
            var10022 = var952;
            var10023 = var960;
            var10024 = var968;
            var10025 = var976;
            var1078 = var984;
            var1079 = var991;
            var1080 = var997;
            var1081 = var1002;
            var1082 = var1006;
            var10031 = var1009;
            var1083 = var1012;
            var1084 = var1014;
            var1085 = var1015;
            var10035 = var79;
            Object var538 = var80;
            var375.updateRememberedValue(var538);
            var10036 = var538;
         }

         Function2 var343 = (Function2)var10036;
         var81.endReplaceGroup();
         ConversationScreenContent(
            var1044,
            var1056,
            var1059,
            var1061,
            var1062,
            var1063,
            var1064,
            var1065,
            var1066,
            var1067,
            var1068,
            var1069,
            var1070,
            var1071,
            var10014,
            var1072,
            var10016,
            var1073,
            var1074,
            var1075,
            var1076,
            var1077,
            var10022,
            var10023,
            var10024,
            var10025,
            var1078,
            var1079,
            var1080,
            var1081,
            var1082,
            var10031,
            var1083,
            var1084,
            var1085,
            var10035,
            var343,
            var205,
            var215,
            (Function0)var223,
            (Function0)var235,
            (Function0)var245,
            (Function0)var255,
            (Function1)var265,
            (Function1)var275,
            (Function0)var286,
            var298,
            var309,
            var81,
            384 | 14 & var12 >> 3,
            57344 & var12 << 6 | 3670016 & var12 << 9,
            896 & var12 >> 6 | 7168 & var12 >> 6 | 57344 & var12 >> 9 | 458752 & var12 >> 3,
            0,
            0,
            0,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var89 = var81.endRestartGroup();
      if (var89 != null) {
         final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_331_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_331_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredCapture_331_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredCapture_331_3 = var3;
         final kotlin.jvm.functions.Function0 recoveredCapture_331_4 = var4;
         final kotlin.jvm.functions.Function1 recoveredCapture_331_5 = var5;
         final kotlin.jvm.functions.Function1 recoveredCapture_331_6 = var6;
         final kotlin.jvm.functions.Function1 recoveredCapture_331_7 = var7;
         final kotlin.jvm.functions.Function1 recoveredCapture_331_8 = var8;
         final int recoveredCapture_331_9 = var10;
         final int recoveredCapture_331_10 = var11;
         var89.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_331_0, recoveredArgument_331_1) -> ConversationScreenKt.ConversationScreen$lambda$111(recoveredCapture_331_0, recoveredCapture_331_1, recoveredCapture_331_2, recoveredCapture_331_3, recoveredCapture_331_4, recoveredCapture_331_5, recoveredCapture_331_6, recoveredCapture_331_7, recoveredCapture_331_8, recoveredCapture_331_9, recoveredCapture_331_10, (androidx.compose.runtime.Composer)recoveredArgument_331_0, ((java.lang.Integer)recoveredArgument_331_1).intValue())));
      }
   }

   private static final Object showNetworkMessage(NetworkState var0, SnackbarHostState var1, Context var2, Function0 var3, Continuation var4) {
      ConversationScreenKt$showNetworkMessage$1 var11;
      label41: {
         if (var4 instanceof ConversationScreenKt$showNetworkMessage$1) {
            var11 = (ConversationScreenKt$showNetworkMessage$1)var4;
            if ((var11.label & -2147483648) != 0) {
               var11.label -= Integer.MIN_VALUE;
               break label41;
            }
         }

         var11 = new ConversationScreenKt$showNetworkMessage$1(var4);
      }

      Object var10 = var11.result;
      Object var12 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (var11.label) {
         case 0:
            ResultKt.throwOnFailure(var10);
            NetworkState var5 = var0;
            if (Intrinsics.areEqual(var5, io.intercom.android.sdk.m5.conversation.states.NetworkState$Connected.INSTANCE)) {
               return Unit.INSTANCE;
            }

            if (Intrinsics.areEqual(var5, io.intercom.android.sdk.m5.conversation.states.NetworkState$Disconnected.INSTANCE)) {
               String var13 = var2.getString(string.intercom_no_network_connection);
               Intrinsics.checkNotNullExpressionValue(var13, "getString(...)");
               SnackbarDuration var10004 = SnackbarDuration.Indefinite;
               var11.label = 1;
               if (SnackbarHostState.showSnackbar$default(var1, var13, null, false, var10004, var11, 6, null) == var12) {
                  return var12;
               }

               return Unit.INSTANCE;
            }

            if (!Intrinsics.areEqual(var5, io.intercom.android.sdk.m5.conversation.states.NetworkState$Reconnected.INSTANCE)) {
               throw new NoWhenBranchMatchedException();
            }

            String var10000 = var2.getString(string.intercom_connected);
            Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
            String var6 = var10000;
            SnackbarDuration var7 = SnackbarDuration.Long;
            String var9 = var2.getString(string.intercom_dismiss);
            Intrinsics.checkNotNullExpressionValue(var9, "getString(...)");
            String var15 = var9.toUpperCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue(var15, "toUpperCase(...)");
            String var8 = var15;
            var11.L$0 = var3;
            var11.label = 2;
            if (SnackbarHostState.showSnackbar$default(var1, var6, var8, false, var7, var11, 4, null) == var12) {
               return var12;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var10);
            return Unit.INSTANCE;
         case 2:
            var3 = (Function0)var11.L$0;
            ResultKt.throwOnFailure(var10);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      int var14 = 0;
      var3.invoke();
      return Unit.INSTANCE;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ConversationScreenContent(
      @Nullable Modifier var0,
      @NotNull ConversationUiState var1,
      @Nullable SnackbarHostState var2,
      @Nullable Function1 var3,
      @Nullable Function1 var4,
      @Nullable Function2 var5,
      @Nullable Function1 var6,
      @Nullable Function1 var7,
      @Nullable Function1 var8,
      @Nullable Function0 var9,
      @Nullable Function1 var10,
      @Nullable Function0 var11,
      @Nullable Function0 var12,
      @Nullable Function0 var13,
      @Nullable Function0 var14,
      @Nullable Function0 var15,
      @NotNull Function0 var16,
      @Nullable Function1 var17,
      @Nullable Function1 var18,
      @Nullable Function1 var19,
      @Nullable Function0 var20,
      @Nullable Function1 var21,
      @Nullable Function0 var22,
      @Nullable Function1 var23,
      @Nullable Function1 var24,
      @Nullable Function1 var25,
      @Nullable Function1 var26,
      @Nullable Function1 var27,
      @NotNull Function1 var28,
      @NotNull Function1 var29,
      @Nullable Function1 var30,
      @Nullable LazyListState var31,
      @Nullable Function0 var32,
      @Nullable Function2 var33,
      @Nullable Function0 var34,
      @Nullable SharedFlow var35,
      @Nullable Function2 var36,
      @Nullable VoiceTranscriptionState var37,
      @Nullable StateFlow var38,
      @Nullable Function0 var39,
      @Nullable Function0 var40,
      @Nullable Function0 var41,
      @Nullable Function0 var42,
      @Nullable Function1 var43,
      @Nullable Function1 var44,
      @Nullable Function0 var45,
      @Nullable StateFlow var46,
      @Nullable StateFlow var47,
      @Nullable Composer var48,
      int var49,
      int var50,
      int var51,
      int var52,
      int var53,
      int var54,
      int var55
   ) {
      Intrinsics.checkNotNullParameter(var1, "uiState");
      Intrinsics.checkNotNullParameter(var16, "onNewConversationClicked");
      Intrinsics.checkNotNullParameter(var28, "onConversationScrolled");
      Intrinsics.checkNotNullParameter(var29, "onJumpToBottomButtonClicked");
      Composer var109 = var48.startRestartGroup(-1201222277);
      ComposerKt.sourceInformation(
         var109,
         "C(ConversationScreenContent)P(4,45,41,36,27,32,19,15,17,16,23,21,22,37,7,29,25,31,14,30,39,35,6,40,24,11,43,44,10,20,5,3,26,18,28,1,46,47!1,33,34,8,38,12,13,9,42)450@19896L32,451@19981L2,452@20029L2,453@20094L11,454@20156L2,455@20194L2,456@20245L2,457@20286L2,458@20338L2,459@20381L2,460@20428L2,461@20465L2,462@20499L2,463@20534L2,465@20624L2,466@20680L2,467@20758L2,468@20789L2,469@20842L2,470@20887L2,471@20926L2,472@20976L2,473@21023L2,474@21069L2,475@21113L2,478@21284L2,479@21323L23,480@21391L2,482@21510L2,484@21678L11,487@21877L2,488@21920L2,489@21965L2,490@22013L2,491@22074L2,492@22135L2,493@22181L2,497@22400L7,498@22433L24,499@22519L7,500@22554L17,501@22613L7,504@22753L25,504@22736L42,508@22931L177,506@22826L282,517@23145L82,*545@23871L7,546@23967L7,550@24161L833,550@24105L889,642@28165L12888:ConversationScreen.kt#fnibcj"
      );
      int var56 = var49;
      int var57 = var50;
      int var58 = var51;
      int var59 = var52;
      int var60 = var53;
      if ((var54 & 1) != 0) {
         var56 |= 6;
      } else if ((var49 & 6) == 0) {
         var56 |= var109.changed(var0) ? 4 : 2;
      }

      if ((var54 & 2) != 0) {
         var56 |= 48;
      } else if ((var49 & 48) == 0) {
         var56 |= ((var49 & 64) == 0 ? var109.changed(var1) : var109.changedInstance(var1)) ? 32 : 16;
      }

      if ((var54 & 4) != 0) {
         var56 |= 384;
      } else if ((var49 & 384) == 0) {
         var56 |= var109.changed(var2) ? 256 : 128;
      }

      if ((var54 & 8) != 0) {
         var56 |= 3072;
      } else if ((var49 & 3072) == 0) {
         var56 |= var109.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var54 & 16) != 0) {
         var56 |= 24576;
      } else if ((var49 & 24576) == 0) {
         var56 |= var109.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var54 & 32) != 0) {
         var56 |= 196608;
      } else if ((var49 & 196608) == 0) {
         var56 |= var109.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var54 & 64) != 0) {
         var56 |= 1572864;
      } else if ((var49 & 1572864) == 0) {
         var56 |= var109.changedInstance(var6) ? 1048576 : 524288;
      }

      if ((var54 & 128) != 0) {
         var56 |= 12582912;
      } else if ((var49 & 12582912) == 0) {
         var56 |= var109.changedInstance(var7) ? 8388608 : 4194304;
      }

      if ((var54 & 256) != 0) {
         var56 |= 100663296;
      } else if ((var49 & 100663296) == 0) {
         var56 |= var109.changedInstance(var8) ? 67108864 : 33554432;
      }

      if ((var54 & 512) != 0) {
         var56 |= 805306368;
      } else if ((var49 & 805306368) == 0) {
         var56 |= var109.changedInstance(var9) ? 536870912 : 268435456;
      }

      if ((var54 & 1024) != 0) {
         var57 |= 6;
      } else if ((var50 & 6) == 0) {
         var57 |= var109.changedInstance(var10) ? 4 : 2;
      }

      if ((var54 & 2048) != 0) {
         var57 |= 48;
      } else if ((var50 & 48) == 0) {
         var57 |= var109.changedInstance(var11) ? 32 : 16;
      }

      if ((var54 & 4096) != 0) {
         var57 |= 384;
      } else if ((var50 & 384) == 0) {
         var57 |= var109.changedInstance(var12) ? 256 : 128;
      }

      if ((var54 & 8192) != 0) {
         var57 |= 3072;
      } else if ((var50 & 3072) == 0) {
         var57 |= var109.changedInstance(var13) ? 2048 : 1024;
      }

      if ((var54 & 16384) != 0) {
         var57 |= 24576;
      } else if ((var50 & 24576) == 0) {
         var57 |= var109.changedInstance(var14) ? 16384 : 8192;
      }

      if ((var54 & 32768) != 0) {
         var57 |= 196608;
      } else if ((var50 & 196608) == 0) {
         var57 |= var109.changedInstance(var15) ? 131072 : 65536;
      }

      if ((var54 & 65536) != 0) {
         var57 |= 1572864;
      } else if ((var50 & 1572864) == 0) {
         var57 |= var109.changedInstance(var16) ? 1048576 : 524288;
      }

      if ((var54 & 131072) != 0) {
         var57 |= 12582912;
      } else if ((var50 & 12582912) == 0) {
         var57 |= var109.changedInstance(var17) ? 8388608 : 4194304;
      }

      if ((var54 & 262144) != 0) {
         var57 |= 100663296;
      } else if ((var50 & 100663296) == 0) {
         var57 |= var109.changedInstance(var18) ? 67108864 : 33554432;
      }

      if ((var54 & 524288) != 0) {
         var57 |= 805306368;
      } else if ((var50 & 805306368) == 0) {
         var57 |= var109.changedInstance(var19) ? 536870912 : 268435456;
      }

      if ((var54 & 1048576) != 0) {
         var58 |= 6;
      } else if ((var51 & 6) == 0) {
         var58 |= var109.changedInstance(var20) ? 4 : 2;
      }

      if ((var54 & 2097152) != 0) {
         var58 |= 48;
      } else if ((var51 & 48) == 0) {
         var58 |= var109.changedInstance(var21) ? 32 : 16;
      }

      if ((var54 & 4194304) != 0) {
         var58 |= 384;
      } else if ((var51 & 384) == 0) {
         var58 |= var109.changedInstance(var22) ? 256 : 128;
      }

      if ((var54 & 16777216) != 0) {
         var58 |= 24576;
      } else if ((var51 & 24576) == 0) {
         var58 |= var109.changedInstance(var24) ? 16384 : 8192;
      }

      if ((var54 & 33554432) != 0) {
         var58 |= 196608;
      } else if ((var51 & 196608) == 0) {
         var58 |= var109.changedInstance(var25) ? 131072 : 65536;
      }

      if ((var54 & 67108864) != 0) {
         var58 |= 1572864;
      } else if ((var51 & 1572864) == 0) {
         var58 |= var109.changedInstance(var26) ? 1048576 : 524288;
      }

      if ((var54 & 134217728) != 0) {
         var58 |= 12582912;
      } else if ((var51 & 12582912) == 0) {
         var58 |= var109.changedInstance(var27) ? 8388608 : 4194304;
      }

      if ((var54 & 268435456) != 0) {
         var58 |= 100663296;
      } else if ((var51 & 100663296) == 0) {
         var58 |= var109.changedInstance(var28) ? 67108864 : 33554432;
      }

      if ((var54 & 536870912) != 0) {
         var58 |= 805306368;
      } else if ((var51 & 805306368) == 0) {
         var58 |= var109.changedInstance(var29) ? 536870912 : 268435456;
      }

      if ((var54 & 1073741824) != 0) {
         var59 |= 6;
      } else if ((var52 & 6) == 0) {
         var59 |= var109.changedInstance(var30) ? 4 : 2;
      }

      if ((var52 & 48) == 0) {
         var59 |= (var55 & 1) == 0 && var109.changed(var31) ? 32 : 16;
      }

      if ((var55 & 2) != 0) {
         var59 |= 384;
      } else if ((var52 & 384) == 0) {
         var59 |= var109.changedInstance(var32) ? 256 : 128;
      }

      if ((var55 & 4) != 0) {
         var59 |= 3072;
      } else if ((var52 & 3072) == 0) {
         var59 |= var109.changedInstance(var33) ? 2048 : 1024;
      }

      if ((var55 & 8) != 0) {
         var59 |= 24576;
      } else if ((var52 & 24576) == 0) {
         var59 |= var109.changedInstance(var34) ? 16384 : 8192;
      }

      if ((var55 & 16) != 0) {
         var59 |= 196608;
      } else if ((var52 & 196608) == 0) {
         var59 |= var109.changedInstance(var35) ? 131072 : 65536;
      }

      if ((var55 & 32) != 0) {
         var59 |= 1572864;
      } else if ((var52 & 1572864) == 0) {
         var59 |= var109.changedInstance(var36) ? 1048576 : 524288;
      }

      if ((var52 & 100663296) == 0) {
         var59 |= (var55 & 128) == 0 && var109.changedInstance(var38) ? 67108864 : 33554432;
      }

      if ((var55 & 256) != 0) {
         var59 |= 805306368;
      } else if ((var52 & 805306368) == 0) {
         var59 |= var109.changedInstance(var39) ? 536870912 : 268435456;
      }

      if ((var55 & 512) != 0) {
         var60 |= 6;
      } else if ((var53 & 6) == 0) {
         var60 |= var109.changedInstance(var40) ? 4 : 2;
      }

      if ((var55 & 1024) != 0) {
         var60 |= 48;
      } else if ((var53 & 48) == 0) {
         var60 |= var109.changedInstance(var41) ? 32 : 16;
      }

      if ((var55 & 2048) != 0) {
         var60 |= 384;
      } else if ((var53 & 384) == 0) {
         var60 |= var109.changedInstance(var42) ? 256 : 128;
      }

      if ((var55 & 4096) != 0) {
         var60 |= 3072;
      } else if ((var53 & 3072) == 0) {
         var60 |= var109.changedInstance(var43) ? 2048 : 1024;
      }

      if ((var55 & 8192) != 0) {
         var60 |= 24576;
      } else if ((var53 & 24576) == 0) {
         var60 |= var109.changedInstance(var44) ? 16384 : 8192;
      }

      if ((var55 & 16384) != 0) {
         var60 |= 196608;
      } else if ((var53 & 196608) == 0) {
         var60 |= var109.changedInstance(var45) ? 131072 : 65536;
      }

      if ((var53 & 1572864) == 0) {
         var60 |= (var55 & 32768) == 0 && var109.changedInstance(var46) ? 1048576 : 524288;
      }

      if ((var53 & 12582912) == 0) {
         var60 |= (var55 & 65536) == 0 && var109.changedInstance(var47) ? 8388608 : 4194304;
      }

      if ((var56 & 306783379) == 306783378
         && (var57 & 306783379) == 306783378
         && (var58 & 306782355) == 306782354
         && (var59 & 302589075) == 302589074
         && (var60 & 4793491) == 4793490
         && var109.getSkipping()) {
         var109.skipToGroupEnd();
      } else {
         var109.startDefaults();
         if ((var49 & 1) != 0 && !var109.getDefaultsInvalid()) {
            var109.skipToGroupEnd();
            if ((var55 & 1) != 0) {
               var59 &= -113;
            }

            if ((var55 & 128) != 0) {
               var59 &= -234881025;
            }

            if ((var55 & 32768) != 0) {
               var60 &= -3670017;
            }

            if ((var55 & 65536) != 0) {
               int var111 = var60 & -29360129;
            }
         } else {
            if ((var54 & 1) != 0) {
               var0 = Modifier.Companion;
            }

            if ((var54 & 4) != 0) {
               var109.startReplaceGroup(1612376009);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var62 = var109;
               boolean var63 = false;
               int var64 = 0;
               Object var65 = var62.rememberedValue();
               int var66 = 0;
               Object var10000;
               if (var65 == Composer.Companion.getEmpty()) {
                  int var67 = 0;
                  Object var340 = new SnackbarHostState();
                  var62.updateRememberedValue(var340);
                  var10000 = var340;
               } else {
                  var10000 = var65;
               }

               SnackbarHostState var61 = (SnackbarHostState)var10000;
               var109.endReplaceGroup();
               var2 = var61;
            }

            if ((var54 & 8) != 0) {
               var109.startReplaceGroup(1612378699);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var149 = var109;
               boolean var186 = false;
               int var223 = 0;
               Object var262 = var149.rememberedValue();
               int var301 = 0;
               Object var475;
               if (var262 == Composer.Companion.getEmpty()) {
                  int var341 = 0;
                  Object var342 = ((kotlin.jvm.functions.Function1) (recoveredArgument_336_0) -> ConversationScreenKt.ConversationScreenContent$lambda$115$lambda$114((io.intercom.android.sdk.m5.conversation.states.ReplySuggestion)recoveredArgument_336_0));
                  var149.updateRememberedValue(var342);
                  var475 = var342;
               } else {
                  var475 = var262;
               }

               Function1 var112 = (Function1)var475;
               var109.endReplaceGroup();
               var3 = var112;
            }

            if ((var54 & 16) != 0) {
               var109.startReplaceGroup(1612380235);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var150 = var109;
               boolean var187 = false;
               int var224 = 0;
               Object var263 = var150.rememberedValue();
               int var302 = 0;
               Object var476;
               if (var263 == Composer.Companion.getEmpty()) {
                  int var343 = 0;
                  Object var344 = ((kotlin.jvm.functions.Function1) (recoveredArgument_337_0) -> ConversationScreenKt.ConversationScreenContent$lambda$117$lambda$116((io.intercom.android.sdk.models.ReplyOption)recoveredArgument_337_0));
                  var150.updateRememberedValue(var344);
                  var476 = var344;
               } else {
                  var476 = var263;
               }

               Function1 var113 = (Function1)var476;
               var109.endReplaceGroup();
               var4 = var113;
            }

            if ((var54 & 32) != 0) {
               var109.startReplaceGroup(1612382324);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var151 = var109;
               boolean var188 = false;
               int var225 = 0;
               Object var264 = var151.rememberedValue();
               int var303 = 0;
               Object var477;
               if (var264 == Composer.Companion.getEmpty()) {
                  int var345 = 0;
                  Object var346 = ((kotlin.jvm.functions.Function2) (recoveredArgument_338_0, recoveredArgument_338_1) -> ConversationScreenKt.ConversationScreenContent$lambda$119$lambda$118((java.lang.String)recoveredArgument_338_0, (java.util.List)recoveredArgument_338_1));
                  var151.updateRememberedValue(var346);
                  var477 = var346;
               } else {
                  var477 = var264;
               }

               Function2 var114 = (Function2)var477;
               var109.endReplaceGroup();
               var5 = var114;
            }

            if ((var54 & 64) != 0) {
               var109.startReplaceGroup(1612384299);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var152 = var109;
               boolean var189 = false;
               int var226 = 0;
               Object var265 = var152.rememberedValue();
               int var304 = 0;
               Object var478;
               if (var265 == Composer.Companion.getEmpty()) {
                  int var347 = 0;
                  Object var348 = ((kotlin.jvm.functions.Function1) (recoveredArgument_339_0) -> ConversationScreenKt.ConversationScreenContent$lambda$121$lambda$120((io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposerInputType)recoveredArgument_339_0));
                  var152.updateRememberedValue(var348);
                  var478 = var348;
               } else {
                  var478 = var265;
               }

               Function1 var115 = (Function1)var478;
               var109.endReplaceGroup();
               var6 = var115;
            }

            if ((var54 & 128) != 0) {
               var109.startReplaceGroup(1612385515);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var153 = var109;
               boolean var190 = false;
               int var227 = 0;
               Object var266 = var153.rememberedValue();
               int var305 = 0;
               Object var479;
               if (var266 == Composer.Companion.getEmpty()) {
                  int var349 = 0;
                  Object var350 = ((kotlin.jvm.functions.Function1) (recoveredArgument_340_0) -> ConversationScreenKt.ConversationScreenContent$lambda$123$lambda$122((io.intercom.android.sdk.blocks.lib.models.Block)recoveredArgument_340_0));
                  var153.updateRememberedValue(var350);
                  var479 = var350;
               } else {
                  var479 = var266;
               }

               Function1 var116 = (Function1)var479;
               var109.endReplaceGroup();
               var7 = var116;
            }

            if ((var54 & 256) != 0) {
               var109.startReplaceGroup(1612387147);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var154 = var109;
               boolean var191 = false;
               int var228 = 0;
               Object var267 = var154.rememberedValue();
               int var306 = 0;
               Object var480;
               if (var267 == Composer.Companion.getEmpty()) {
                  int var351 = 0;
                  Object var352 = ((kotlin.jvm.functions.Function1) (recoveredArgument_341_0) -> ConversationScreenKt.ConversationScreenContent$lambda$125$lambda$124((java.lang.String)recoveredArgument_341_0));
                  var154.updateRememberedValue(var352);
                  var480 = var352;
               } else {
                  var480 = var267;
               }

               Function1 var117 = (Function1)var480;
               var109.endReplaceGroup();
               var8 = var117;
            }

            if ((var54 & 512) != 0) {
               var109.startReplaceGroup(1612388459);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var155 = var109;
               boolean var192 = false;
               int var229 = 0;
               Object var268 = var155.rememberedValue();
               int var307 = 0;
               Object var481;
               if (var268 == Composer.Companion.getEmpty()) {
                  int var353 = 0;
                  Object var354 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreenContent$lambda$127$lambda$126());
                  var155.updateRememberedValue(var354);
                  var481 = var354;
               } else {
                  var481 = var268;
               }

               Function0 var118 = (Function0)var481;
               var109.endReplaceGroup();
               var9 = var118;
            }

            if ((var54 & 1024) != 0) {
               var109.startReplaceGroup(1612390123);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var156 = var109;
               boolean var193 = false;
               int var230 = 0;
               Object var269 = var156.rememberedValue();
               int var308 = 0;
               Object var482;
               if (var269 == Composer.Companion.getEmpty()) {
                  int var355 = 0;
                  Object var356 = ((kotlin.jvm.functions.Function1) (recoveredArgument_343_0) -> ConversationScreenKt.ConversationScreenContent$lambda$129$lambda$128((java.util.List)recoveredArgument_343_0));
                  var156.updateRememberedValue(var356);
                  var482 = var356;
               } else {
                  var482 = var269;
               }

               Function1 var119 = (Function1)var482;
               var109.endReplaceGroup();
               var10 = var119;
            }

            if ((var54 & 2048) != 0) {
               var109.startReplaceGroup(1612391499);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var157 = var109;
               boolean var194 = false;
               int var231 = 0;
               Object var270 = var157.rememberedValue();
               int var309 = 0;
               Object var483;
               if (var270 == Composer.Companion.getEmpty()) {
                  int var357 = 0;
                  Object var358 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreenContent$lambda$131$lambda$130());
                  var157.updateRememberedValue(var358);
                  var483 = var358;
               } else {
                  var483 = var270;
               }

               Function0 var120 = (Function0)var483;
               var109.endReplaceGroup();
               var11 = var120;
            }

            if ((var54 & 4096) != 0) {
               var109.startReplaceGroup(1612393003);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var158 = var109;
               boolean var195 = false;
               int var232 = 0;
               Object var271 = var158.rememberedValue();
               int var310 = 0;
               Object var484;
               if (var271 == Composer.Companion.getEmpty()) {
                  int var359 = 0;
                  Object var360 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreenContent$lambda$133$lambda$132());
                  var158.updateRememberedValue(var360);
                  var484 = var360;
               } else {
                  var484 = var271;
               }

               Function0 var121 = (Function0)var484;
               var109.endReplaceGroup();
               var12 = var121;
            }

            if ((var54 & 8192) != 0) {
               var109.startReplaceGroup(1612394187);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var159 = var109;
               boolean var196 = false;
               int var233 = 0;
               Object var272 = var159.rememberedValue();
               int var311 = 0;
               Object var485;
               if (var272 == Composer.Companion.getEmpty()) {
                  int var361 = 0;
                  Object var362 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreenContent$lambda$135$lambda$134());
                  var159.updateRememberedValue(var362);
                  var485 = var362;
               } else {
                  var485 = var272;
               }

               Function0 var122 = (Function0)var485;
               var109.endReplaceGroup();
               var13 = var122;
            }

            if ((var54 & 16384) != 0) {
               var109.startReplaceGroup(1612395275);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var160 = var109;
               boolean var197 = false;
               int var234 = 0;
               Object var273 = var160.rememberedValue();
               int var312 = 0;
               Object var486;
               if (var273 == Composer.Companion.getEmpty()) {
                  int var363 = 0;
                  Object var364 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreenContent$lambda$137$lambda$136());
                  var160.updateRememberedValue(var364);
                  var486 = var364;
               } else {
                  var486 = var273;
               }

               Function0 var123 = (Function0)var486;
               var109.endReplaceGroup();
               var14 = var123;
            }

            if ((var54 & 32768) != 0) {
               var109.startReplaceGroup(1612396395);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var161 = var109;
               boolean var198 = false;
               int var235 = 0;
               Object var274 = var161.rememberedValue();
               int var313 = 0;
               Object var487;
               if (var274 == Composer.Companion.getEmpty()) {
                  int var365 = 0;
                  Object var366 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreenContent$lambda$139$lambda$138());
                  var161.updateRememberedValue(var366);
                  var487 = var366;
               } else {
                  var487 = var274;
               }

               Function0 var124 = (Function0)var487;
               var109.endReplaceGroup();
               var15 = var124;
            }

            if ((var54 & 131072) != 0) {
               var109.startReplaceGroup(1612399275);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var162 = var109;
               boolean var199 = false;
               int var236 = 0;
               Object var275 = var162.rememberedValue();
               int var314 = 0;
               Object var488;
               if (var275 == Composer.Companion.getEmpty()) {
                  int var367 = 0;
                  Object var368 = ((kotlin.jvm.functions.Function1) (recoveredArgument_349_0) -> ConversationScreenKt.ConversationScreenContent$lambda$141$lambda$140((io.intercom.android.sdk.models.Part)recoveredArgument_349_0));
                  var162.updateRememberedValue(var368);
                  var488 = var368;
               } else {
                  var488 = var275;
               }

               Function1 var125 = (Function1)var488;
               var109.endReplaceGroup();
               var17 = var125;
            }

            if ((var54 & 262144) != 0) {
               var109.startReplaceGroup(1612401067);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var163 = var109;
               boolean var200 = false;
               int var237 = 0;
               Object var276 = var163.rememberedValue();
               int var315 = 0;
               Object var489;
               if (var276 == Composer.Companion.getEmpty()) {
                  int var369 = 0;
                  Object var370 = ((kotlin.jvm.functions.Function1) (recoveredArgument_350_0) -> ConversationScreenKt.ConversationScreenContent$lambda$143$lambda$142((io.intercom.android.sdk.models.Part)recoveredArgument_350_0));
                  var163.updateRememberedValue(var370);
                  var489 = var370;
               } else {
                  var489 = var276;
               }

               Function1 var126 = (Function1)var489;
               var109.endReplaceGroup();
               var18 = var126;
            }

            if ((var54 & 524288) != 0) {
               var109.startReplaceGroup(1612403563);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var164 = var109;
               boolean var201 = false;
               int var238 = 0;
               Object var277 = var164.rememberedValue();
               int var316 = 0;
               Object var490;
               if (var277 == Composer.Companion.getEmpty()) {
                  int var371 = 0;
                  Object var372 = ((kotlin.jvm.functions.Function1) (recoveredArgument_351_0) -> ConversationScreenKt.ConversationScreenContent$lambda$145$lambda$144((io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData)recoveredArgument_351_0));
                  var164.updateRememberedValue(var372);
                  var490 = var372;
               } else {
                  var490 = var277;
               }

               Function1 var127 = (Function1)var490;
               var109.endReplaceGroup();
               var19 = var127;
            }

            if ((var54 & 1048576) != 0) {
               var109.startReplaceGroup(1612404555);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var165 = var109;
               boolean var202 = false;
               int var239 = 0;
               Object var278 = var165.rememberedValue();
               int var317 = 0;
               Object var491;
               if (var278 == Composer.Companion.getEmpty()) {
                  int var373 = 0;
                  Object var374 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreenContent$lambda$147$lambda$146());
                  var165.updateRememberedValue(var374);
                  var491 = var374;
               } else {
                  var491 = var278;
               }

               Function0 var128 = (Function0)var491;
               var109.endReplaceGroup();
               var20 = var128;
            }

            if ((var54 & 2097152) != 0) {
               var109.startReplaceGroup(1612406251);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var166 = var109;
               boolean var203 = false;
               int var240 = 0;
               Object var279 = var166.rememberedValue();
               int var318 = 0;
               Object var492;
               if (var279 == Composer.Companion.getEmpty()) {
                  int var375 = 0;
                  Object var376 = ((kotlin.jvm.functions.Function1) (recoveredArgument_353_0) -> ConversationScreenKt.ConversationScreenContent$lambda$149$lambda$148((io.intercom.android.sdk.m5.conversation.states.AttributeData)recoveredArgument_353_0));
                  var166.updateRememberedValue(var376);
                  var492 = var376;
               } else {
                  var492 = var279;
               }

               Function1 var129 = (Function1)var492;
               var109.endReplaceGroup();
               var21 = var129;
            }

            if ((var54 & 4194304) != 0) {
               var109.startReplaceGroup(1612407691);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var167 = var109;
               boolean var204 = false;
               int var241 = 0;
               Object var280 = var167.rememberedValue();
               int var319 = 0;
               Object var493;
               if (var280 == Composer.Companion.getEmpty()) {
                  int var377 = 0;
                  Object var378 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreenContent$lambda$151$lambda$150());
                  var167.updateRememberedValue(var378);
                  var493 = var378;
               } else {
                  var493 = var280;
               }

               Function0 var130 = (Function0)var493;
               var109.endReplaceGroup();
               var22 = var130;
            }

            if ((var54 & 8388608) != 0) {
               var109.startReplaceGroup(1612408939);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var168 = var109;
               boolean var205 = false;
               int var242 = 0;
               Object var281 = var168.rememberedValue();
               int var320 = 0;
               Object var494;
               if (var281 == Composer.Companion.getEmpty()) {
                  int var379 = 0;
                  Object var380 = ((kotlin.jvm.functions.Function1) (recoveredArgument_355_0) -> ConversationScreenKt.ConversationScreenContent$lambda$153$lambda$152((java.lang.String)recoveredArgument_355_0));
                  var168.updateRememberedValue(var380);
                  var494 = var380;
               } else {
                  var494 = var281;
               }

               Function1 var131 = (Function1)var494;
               var109.endReplaceGroup();
               var23 = var131;
            }

            if ((var54 & 16777216) != 0) {
               var109.startReplaceGroup(1612410539);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var169 = var109;
               boolean var206 = false;
               int var243 = 0;
               Object var282 = var169.rememberedValue();
               int var321 = 0;
               Object var495;
               if (var282 == Composer.Companion.getEmpty()) {
                  int var381 = 0;
                  Object var382 = ((kotlin.jvm.functions.Function1) (recoveredArgument_356_0) -> ConversationScreenKt.ConversationScreenContent$lambda$155$lambda$154((io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem)recoveredArgument_356_0));
                  var169.updateRememberedValue(var382);
                  var495 = var382;
               } else {
                  var495 = var282;
               }

               Function1 var132 = (Function1)var495;
               var109.endReplaceGroup();
               var24 = var132;
            }

            if ((var54 & 33554432) != 0) {
               var109.startReplaceGroup(1612412043);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var170 = var109;
               boolean var207 = false;
               int var244 = 0;
               Object var283 = var170.rememberedValue();
               int var322 = 0;
               Object var496;
               if (var283 == Composer.Companion.getEmpty()) {
                  int var383 = 0;
                  Object var384 = ((kotlin.jvm.functions.Function1) (recoveredArgument_357_0) -> ConversationScreenKt.ConversationScreenContent$lambda$157$lambda$156((io.intercom.android.sdk.blocks.lib.models.TicketType)recoveredArgument_357_0));
                  var170.updateRememberedValue(var384);
                  var496 = var384;
               } else {
                  var496 = var283;
               }

               Function1 var133 = (Function1)var496;
               var109.endReplaceGroup();
               var25 = var133;
            }

            if ((var54 & 67108864) != 0) {
               var109.startReplaceGroup(1612413515);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var171 = var109;
               boolean var208 = false;
               int var245 = 0;
               Object var284 = var171.rememberedValue();
               int var323 = 0;
               Object var497;
               if (var284 == Composer.Companion.getEmpty()) {
                  int var385 = 0;
                  Object var386 = ((kotlin.jvm.functions.Function1) (recoveredArgument_358_0) -> ConversationScreenKt.ConversationScreenContent$lambda$159$lambda$158((java.lang.String)recoveredArgument_358_0));
                  var171.updateRememberedValue(var386);
                  var497 = var386;
               } else {
                  var497 = var284;
               }

               Function1 var134 = (Function1)var497;
               var109.endReplaceGroup();
               var26 = var134;
            }

            if ((var54 & 134217728) != 0) {
               var109.startReplaceGroup(1612414923);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var172 = var109;
               boolean var209 = false;
               int var246 = 0;
               Object var285 = var172.rememberedValue();
               int var324 = 0;
               Object var498;
               if (var285 == Composer.Companion.getEmpty()) {
                  int var387 = 0;
                  Object var388 = ((kotlin.jvm.functions.Function1) (recoveredArgument_359_0) -> ConversationScreenKt.ConversationScreenContent$lambda$161$lambda$160((io.intercom.android.sdk.m5.conversation.metrics.MetricData)recoveredArgument_359_0));
                  var172.updateRememberedValue(var388);
                  var498 = var388;
               } else {
                  var498 = var285;
               }

               Function1 var135 = (Function1)var498;
               var109.endReplaceGroup();
               var27 = var135;
            }

            if ((var54 & 1073741824) != 0) {
               var109.startReplaceGroup(1612420395);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var173 = var109;
               boolean var210 = false;
               int var247 = 0;
               Object var286 = var173.rememberedValue();
               int var325 = 0;
               Object var499;
               if (var286 == Composer.Companion.getEmpty()) {
                  int var389 = 0;
                  Object var390 = ((kotlin.jvm.functions.Function1) (recoveredArgument_360_0) -> ConversationScreenKt.ConversationScreenContent$lambda$163$lambda$162((java.lang.String)recoveredArgument_360_0));
                  var173.updateRememberedValue(var390);
                  var499 = var390;
               } else {
                  var499 = var286;
               }

               Function1 var136 = (Function1)var499;
               var109.endReplaceGroup();
               var30 = var136;
            }

            if ((var55 & 1) != 0) {
               var31 = LazyListStateKt.rememberLazyListState(0, 0, var109, 0, 3);
               var59 &= -113;
            }

            if ((var55 & 2) != 0) {
               var109.startReplaceGroup(1612423819);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var174 = var109;
               boolean var211 = false;
               int var248 = 0;
               Object var287 = var174.rememberedValue();
               int var326 = 0;
               Object var500;
               if (var287 == Composer.Companion.getEmpty()) {
                  int var391 = 0;
                  Object var392 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreenContent$lambda$165$lambda$164());
                  var174.updateRememberedValue(var392);
                  var500 = var392;
               } else {
                  var500 = var287;
               }

               Function0 var137 = (Function0)var500;
               var109.endReplaceGroup();
               var32 = var137;
            }

            if ((var55 & 4) != 0) {
               var33 = null;
            }

            if ((var55 & 8) != 0) {
               var109.startReplaceGroup(1612427627);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var175 = var109;
               boolean var212 = false;
               int var249 = 0;
               Object var288 = var175.rememberedValue();
               int var327 = 0;
               Object var501;
               if (var288 == Composer.Companion.getEmpty()) {
                  int var393 = 0;
                  Object var394 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreenContent$lambda$167$lambda$166());
                  var175.updateRememberedValue(var394);
                  var501 = var394;
               } else {
                  var501 = var288;
               }

               Function0 var138 = (Function0)var501;
               var109.endReplaceGroup();
               var34 = var138;
            }

            if ((var55 & 16) != 0) {
               var35 = null;
            }

            if ((var55 & 32) != 0) {
               var109.startReplaceGroup(1612433012);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var176 = var109;
               boolean var213 = false;
               int var250 = 0;
               Object var289 = var176.rememberedValue();
               int var328 = 0;
               Object var502;
               if (var289 == Composer.Companion.getEmpty()) {
                  int var395 = 0;
                  Object var396 = ((kotlin.jvm.functions.Function2) (recoveredArgument_363_0, recoveredArgument_363_1) -> ConversationScreenKt.ConversationScreenContent$lambda$169$lambda$168(((java.lang.Boolean)recoveredArgument_363_0).booleanValue(), ((java.lang.Boolean)recoveredArgument_363_1).booleanValue()));
                  var176.updateRememberedValue(var396);
                  var502 = var396;
               } else {
                  var502 = var289;
               }

               Function2 var139 = (Function2)var502;
               var109.endReplaceGroup();
               var36 = var139;
            }

            if ((var55 & 64) != 0) {
               var37 = io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Idle.INSTANCE;
            }

            if ((var55 & 128) != 0) {
               var38 = StateFlowKt.MutableStateFlow(0.0F);
               var59 &= -234881025;
            }

            if ((var55 & 256) != 0) {
               var109.startReplaceGroup(1612439371);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var177 = var109;
               boolean var214 = false;
               int var251 = 0;
               Object var290 = var177.rememberedValue();
               int var329 = 0;
               Object var503;
               if (var290 == Composer.Companion.getEmpty()) {
                  int var397 = 0;
                  Object var398 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreenContent$lambda$171$lambda$170());
                  var177.updateRememberedValue(var398);
                  var503 = var398;
               } else {
                  var503 = var290;
               }

               Function0 var140 = (Function0)var503;
               var109.endReplaceGroup();
               var39 = var140;
            }

            if ((var55 & 512) != 0) {
               var109.startReplaceGroup(1612440747);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var178 = var109;
               boolean var215 = false;
               int var252 = 0;
               Object var291 = var178.rememberedValue();
               int var330 = 0;
               Object var504;
               if (var291 == Composer.Companion.getEmpty()) {
                  int var399 = 0;
                  Object var400 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreenContent$lambda$173$lambda$172());
                  var178.updateRememberedValue(var400);
                  var504 = var400;
               } else {
                  var504 = var291;
               }

               Function0 var141 = (Function0)var504;
               var109.endReplaceGroup();
               var40 = var141;
            }

            if ((var55 & 1024) != 0) {
               var109.startReplaceGroup(1612442187);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var179 = var109;
               boolean var216 = false;
               int var253 = 0;
               Object var292 = var179.rememberedValue();
               int var331 = 0;
               Object var505;
               if (var292 == Composer.Companion.getEmpty()) {
                  int var401 = 0;
                  Object var402 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreenContent$lambda$175$lambda$174());
                  var179.updateRememberedValue(var402);
                  var505 = var402;
               } else {
                  var505 = var292;
               }

               Function0 var142 = (Function0)var505;
               var109.endReplaceGroup();
               var41 = var142;
            }

            if ((var55 & 2048) != 0) {
               var109.startReplaceGroup(1612443723);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var180 = var109;
               boolean var217 = false;
               int var254 = 0;
               Object var293 = var180.rememberedValue();
               int var332 = 0;
               Object var506;
               if (var293 == Composer.Companion.getEmpty()) {
                  int var403 = 0;
                  Object var404 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreenContent$lambda$177$lambda$176());
                  var180.updateRememberedValue(var404);
                  var506 = var404;
               } else {
                  var506 = var293;
               }

               Function0 var143 = (Function0)var506;
               var109.endReplaceGroup();
               var42 = var143;
            }

            if ((var55 & 4096) != 0) {
               var109.startReplaceGroup(1612445675);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var181 = var109;
               boolean var218 = false;
               int var255 = 0;
               Object var294 = var181.rememberedValue();
               int var333 = 0;
               Object var507;
               if (var294 == Composer.Companion.getEmpty()) {
                  int var405 = 0;
                  Object var406 = ((kotlin.jvm.functions.Function1) (recoveredArgument_368_0) -> ConversationScreenKt.ConversationScreenContent$lambda$179$lambda$178((io.intercom.android.sdk.m5.conversation.states.MediaUploadItem)recoveredArgument_368_0));
                  var181.updateRememberedValue(var406);
                  var507 = var406;
               } else {
                  var507 = var294;
               }

               Function1 var144 = (Function1)var507;
               var109.endReplaceGroup();
               var43 = var144;
            }

            if ((var55 & 8192) != 0) {
               var109.startReplaceGroup(1612447627);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var182 = var109;
               boolean var219 = false;
               int var256 = 0;
               Object var295 = var182.rememberedValue();
               int var334 = 0;
               Object var508;
               if (var295 == Composer.Companion.getEmpty()) {
                  int var407 = 0;
                  Object var408 = ((kotlin.jvm.functions.Function1) (recoveredArgument_369_0) -> ConversationScreenKt.ConversationScreenContent$lambda$181$lambda$180((io.intercom.android.sdk.m5.conversation.states.MediaUploadItem)recoveredArgument_369_0));
                  var182.updateRememberedValue(var408);
                  var508 = var408;
               } else {
                  var508 = var295;
               }

               Function1 var145 = (Function1)var508;
               var109.endReplaceGroup();
               var44 = var145;
            }

            if ((var55 & 16384) != 0) {
               var109.startReplaceGroup(1612449099);
               ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
               Composer var183 = var109;
               boolean var220 = false;
               int var257 = 0;
               Object var296 = var183.rememberedValue();
               int var335 = 0;
               Object var509;
               if (var296 == Composer.Companion.getEmpty()) {
                  int var409 = 0;
                  Object var410 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreenContent$lambda$183$lambda$182());
                  var183.updateRememberedValue(var410);
                  var509 = var410;
               } else {
                  var509 = var296;
               }

               Function0 var146 = (Function0)var509;
               var109.endReplaceGroup();
               var45 = var146;
            }

            if ((var55 & 32768) != 0) {
               var46 = StateFlowKt.MutableStateFlow(io.intercom.android.sdk.m5.conversation.streaming.StreamingMessageState$Idle.INSTANCE);
               var60 &= -3670017;
            }

            if ((var55 & 65536) != 0) {
               var47 = StateFlowKt.MutableStateFlow(false);
               int var110 = var60 & -29360129;
            }
         }

         var109.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1201222277, var56, var57, "io.intercom.android.sdk.m5.conversation.ui.ConversationScreenContent (ConversationScreen.kt:496)"
            );
         }

         CompositionLocal var184 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var221 = 0;
         int var258 = 0;
         ComposerKt.sourceInformationMarkerStart(var109, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var297 = var109.consume(var184);
         ComposerKt.sourceInformationMarkerEnd(var109);
         Context var147 = (Context)var297;
         boolean var259 = false;
         int var298 = 0;
         ComposerKt.sourceInformationMarkerStart(var109, 773894976, "CC(rememberCoroutineScope)N(getContext)616@28039L68:Effects.kt#9igjgp");
         Composer var336 = var109;
         ComposerKt.sourceInformationMarkerStart(var109, 683736516, "CC(remember):Effects.kt#9igjgp");
         Composer var411 = var109;
         boolean var68 = false;
         int var69 = 0;
         Object var70 = var411.rememberedValue();
         int var71 = 0;
         Object var510;
         if (var70 == Composer.Companion.getEmpty()) {
            int var72 = 0;
            int var73 = 0;
            Object var441 = EffectsKt.createCompositionCoroutineScope(EmptyCoroutineContext.INSTANCE, var336);
            var411.updateRememberedValue(var441);
            var510 = var441;
         } else {
            var510 = var70;
         }

         CoroutineScope var74 = (CoroutineScope)var510;
         ComposerKt.sourceInformationMarkerEnd(var109);
         CoroutineScope var75 = var74;
         ComposerKt.sourceInformationMarkerEnd(var109);
         CoroutineScope var185 = var75;
         CompositionLocal var260 = CompositionLocalsKt.getLocalSoftwareKeyboardController();
         int $changed$ivx = 0;
         int $i$f$getCurrentx = 0;
         ComposerKt.sourceInformationMarkerStart(var109, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var412 = var109.consume(var260);
         ComposerKt.sourceInformationMarkerEnd(var109);
         SoftwareKeyboardController var222 = (SoftwareKeyboardController)var412;
         State var261 = KeyboardStateKt.keyboardAsState(var109, 0);
         CompositionLocal var338 = CompositionLocalsKt.getLocalFocusManager();
         int $changed$ivxx = 0;
         int $i$f$getCurrentxx = 0;
         ComposerKt.sourceInformationMarkerStart(var109, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var420 = var109.consume(var338);
         ComposerKt.sourceInformationMarkerEnd(var109);
         FocusManager var300 = (FocusManager)var420;
         Object[] var511 = new Object[0];
         var109.startReplaceGroup(1612467426);
         ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var417 = var109;
         boolean var421 = false;
         int $i$f$cachex = 0;
         Object var429 = var417.rememberedValue();
         int var435 = 0;
         Object var10001;
         if (var429 == Composer.Companion.getEmpty()) {
            Object[] var105 = var511;
            int var442 = 0;
            Function0 var106 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreenContent$lambda$186$lambda$185());
            var511 = var105;
            Object var443 = var106;
            var417.updateRememberedValue(var443);
            var10001 = var443;
         } else {
            var10001 = var429;
         }

         Function0 var414 = (Function0)var10001;
         var109.endReplaceGroup();
         MutableState var339 = (MutableState)RememberSaveableKt.rememberSaveable(var511, var414, var109, 48);
         ActivityResultContract var512 = new RequestPermission();
         var109.startReplaceGroup(1612473274);
         ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var422 = var109;
         boolean var425 = (var59 & 3670016) == 1048576;
         int $i$f$cachexx = 0;
         Object it$ivx = var422.rememberedValue();
         int var444/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var515;
         if (!var425 && it$ivx != Composer.Companion.getEmpty()) {
            var515 = it$ivx;
         } else {
            ActivityResultContract var471 = var512;
            int var450 = 0;
            final kotlin.jvm.functions.Function2 recoveredCapture_372_0 = var36;
            Function1 var473 = ((kotlin.jvm.functions.Function1) (recoveredArgument_372_0) -> ConversationScreenKt.ConversationScreenContent$lambda$188$lambda$187(recoveredCapture_372_0, ((java.lang.Boolean)recoveredArgument_372_0).booleanValue()));
            var512 = var471;
            Object var455 = var473;
            var422.updateRememberedValue(var455);
            var515 = var455;
         }

         Function1 var418 = (Function1)var515;
         var109.endReplaceGroup();
         ManagedActivityResultLauncher var415 = ActivityResultRegistryKt.rememberLauncherForActivityResult(var512, var418, var109, 0);
         SheetState var419 = ModalBottomSheetKt.rememberModalBottomSheetState(true, null, var109, 6, 2);
         CompositionLocal var426 = CompositionLocalsKt.getLocalDensity();
         int $changed$ivxxx = 0;
         int $i$f$getCurrentxxx = 0;
         ComposerKt.sourceInformationMarkerStart(var109, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var445 = var109.consume(var426);
         ComposerKt.sourceInformationMarkerEnd(var109);
         Object var427 = var445;
         Density var432 = (Density)var427;
         int var438 = 0;
         float var423 = var432.recovered$245d7c913f591981(LazyListJumpToBottomScrollOffset);
         CompositionLocal var433 = AndroidCompositionLocals_androidKt.getLocalConfiguration();
         int $changed$ivxxxx = 0;
         int $i$f$getCurrentxxxx = 0;
         ComposerKt.sourceInformationMarkerStart(var109, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var451 = var109.consume(var433);
         ComposerKt.sourceInformationMarkerEnd(var109);
         Configuration var428 = (Configuration)var451;
         boolean var434 = var428.fontScale > 1.0;
         boolean var440 = var428.orientation == 2;
         LazyListState var513 = var31;
         Boolean var516 = var434;
         Boolean var10002 = var440;
         var109.startReplaceGroup(1612513290);
         ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var452 = var109;
         boolean var456 = ((var59 & 112 ^ 48) > 32 && var109.changed(var31) || (var59 & 48) == 32)
            | var109.changed(var423)
            | (var58 & 234881024) == 67108864
            | var109.changed(var440)
            | var109.changed(var434);
         int $i$f$cachexxx = 0;
         Object it$ivxx = var452.rememberedValue();
         int var78/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var10003;
         if (!var456 && it$ivxx != Composer.Companion.getEmpty()) {
            var10003 = it$ivxx;
         } else {
            Boolean var107 = var10002;
            Boolean var474 = var516;
            LazyListState var472 = var31;
            int var79 = 0;
            Function2 var108 = new ConversationScreenKt$ConversationScreenContent$37$1(var31, var423, var28, var440, var434, null);
            var513 = var472;
            var516 = var474;
            var10002 = var107;
            Object var80 = var108;
            var452.updateRememberedValue(var80);
            var10003 = var80;
         }

         Function2 var447 = (Function2)var10003;
         var109.endReplaceGroup();
         EffectsKt.LaunchedEffect(var513, var516, var10002, var447, var109, 14 & var59 >> 3);
         var109.startReplaceGroup(1612542464);
         ComposerKt.sourceInformation(var109, "579@25329L6,575@25192L49,582@25491L2662,573@25037L3116");
         if ((Boolean)var339.getValue()) {
            Modifier var448 = WindowInsetsPadding_androidKt.statusBarsPadding(Modifier.Companion);
            long var453 = IntercomTheme.INSTANCE.getColors(var109, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
            var109.startReplaceGroup(1612545498);
            ComposerKt.sourceInformation(var109, "CC(remember):ConversationScreen.kt#9igjgp");
            Composer var460 = var109;
            boolean var462 = var109.changedInstance(var185) | var109.changed(var339);
            int $i$f$cachexxxx = 0;
            Object it$ivxxx = var460.rememberedValue();
            int var81/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
            Object var514;
            if (!var462 && it$ivxxx != Composer.Companion.getEmpty()) {
               var514 = it$ivxxx;
            } else {
               int var82 = 0;
               final kotlinx.coroutines.CoroutineScope recoveredCapture_373_0 = var185;
               final androidx.compose.runtime.MutableState recoveredCapture_373_1 = var339;
               Object var83 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt.ConversationScreenContent$lambda$192$lambda$191(recoveredCapture_373_0, recoveredCapture_373_1));
               var460.updateRememberedValue(var83);
               var514 = var83;
            }

            Function0 var458 = (Function0)var514;
            var109.endReplaceGroup();
            ModalBottomSheetKt.recovered$1910bd45b3835662(
               var458,
               var448,
               var419,
               0.0F,
               false,
               null,
               var453,
               0L,
               0.0F,
               0L,
               null,
               ConversationScreenKt$ConversationScreenContent$39.INSTANCE,
               null,
               ComposableLambdaKt.rememberComposableLambda(
                  1670416802, true, new ConversationScreenKt$ConversationScreenContent$40(var1, var185, var339, var10, var26, var7, var8), var109, 54
               ),
               var109,
               0,
               3072,
               6072
            );
         }

         var109.endReplaceGroup();
         byte var459 = 0;
         int var461 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var109, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Modifier var449 = Modifier.Companion;
         Alignment var454 = Alignment.Companion.getTopStart();
         boolean var457 = false;
         MeasurePolicy var463 = BoxKt.maybeCachedBoxMeasurePolicy(var454, var457);
         Modifier var465 = var449;
         MeasurePolicy var467 = var463;
         Composer var468 = var109;
         int var469 = 112 & var459 << 3;
         int var470 = 0;
         ComposerKt.sourceInformationMarkerStart(var468, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var84 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var468, 0));
         CompositionLocalMap var85 = var468.getCurrentCompositionLocalMap();
         Modifier var86 = ComposedModifierKt.materializeModifier(var468, var465);
         Function0 var87 = ComposeUiNode.Companion.getConstructor();
         Composer var88 = var468;
         int var89 = 6 | 896 & var469 << 6;
         int var90 = 0;
         ComposerKt.sourceInformationMarkerStart(var88, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var88.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var88.startReusableNode();
         if (var88.getInserting()) {
            var88.createNode(var87);
         } else {
            var88.useNode();
         }

         Composer var91 = Updater.recovered$676aa17b44fd6280(var88);
         int var92 = 0;
         Updater.recovered$89682b199a142880(var91, var467, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var91, var85, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var91, var84, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var91, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var91, var86, ComposeUiNode.Companion.getSetModifier());
         int var93 = 14 & var89 >> 6;
         Composer var94 = var88;
         int var95 = 0;
         ComposerKt.sourceInformationMarkerStart(var94, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var96 = 6 | 112 & var459 >> 6;
         Composer var97 = var94;
         BoxScope var98 = BoxScopeInstance.INSTANCE;
         int var99 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var97,
            230066103,
            "C643@28206L7,646@28306L6,647@28374L13,657@28794L1758,691@30578L2847,648@28417L354,741@33449L7587,644@28222L12825:ConversationScreen.kt#fnibcj"
         );
         CompositionLocal var100 = CompositionLocalsKt.getLocalDensity();
         int $changed$ivxxxxx = 0;
         int $i$f$getCurrentxxxxx = 0;
         ComposerKt.sourceInformationMarkerStart(var97, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var103 = var97.consume(var100);
         ComposerKt.sourceInformationMarkerEnd(var97);
         Density var104 = (Density)var103;
         ScaffoldKt.recovered$857cf99fc8161460(
            WindowInsetsPaddingKt.windowInsetsPadding(
               BackgroundKt.recovered$42a3174180b3fb7a(
                  var0, IntercomTheme.INSTANCE.getColors(var97, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(), null, 2, null
               ),
               WindowInsets_androidKt.getDisplayCutout(WindowInsets.Companion, var97, 6)
            ),
            ComposableLambdaKt.rememberComposableLambda(
               -10937487,
               true,
               new ConversationScreenKt$ConversationScreenContent$41$1(
                  var1, var14, var13, var222, var185, var261, var339, var22, var24, var27, var415, var147, var36
               ),
               var97,
               54
            ),
            ComposableLambdaKt.rememberComposableLambda(
               -1816921840,
               true,
               new ConversationScreenKt$ConversationScreenContent$41$2(
                  var1,
                  var300,
                  var222,
                  var5,
                  var185,
                  var31,
                  var6,
                  var9,
                  var339,
                  var16,
                  var12,
                  var11,
                  var261,
                  var27,
                  var30,
                  var32,
                  var20,
                  var35,
                  var38,
                  var39,
                  var40,
                  var41,
                  var42,
                  var43,
                  var44,
                  var45
               ),
               var97,
               54
            ),
            ComposableLambdaKt.rememberComposableLambda(672061103, true, new ConversationScreenKt$ConversationScreenContent$41$3(var2), var97, 54),
            null,
            0,
            0L,
            0L,
            null,
            ComposableLambdaKt.rememberComposableLambda(
               -617229178,
               true,
               new ConversationScreenKt$ConversationScreenContent$41$4(
                  var1,
                  var15,
                  var46,
                  var47,
                  var31,
                  var3,
                  var4,
                  var17,
                  var18,
                  var19,
                  var21,
                  var22,
                  var25,
                  var30,
                  var33,
                  var222,
                  var185,
                  var261,
                  var339,
                  var34,
                  var104,
                  var29
               ),
               var97,
               54
            ),
            var97,
            805309872,
            496
         );
         ComposerKt.sourceInformationMarkerEnd(var97);
         ComposerKt.sourceInformationMarkerEnd(var94);
         var88.endNode();
         ComposerKt.sourceInformationMarkerEnd(var88);
         ComposerKt.sourceInformationMarkerEnd(var468);
         ComposerKt.sourceInformationMarkerEnd(var109);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var148 = var109.endRestartGroup();
      if (var148 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_374_0 = var0;
         final io.intercom.android.sdk.m5.conversation.states.ConversationUiState recoveredCapture_374_1 = var1;
         final androidx.compose.material3.SnackbarHostState recoveredCapture_374_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredCapture_374_3 = var3;
         final kotlin.jvm.functions.Function1 recoveredCapture_374_4 = var4;
         final kotlin.jvm.functions.Function2 recoveredCapture_374_5 = var5;
         final kotlin.jvm.functions.Function1 recoveredCapture_374_6 = var6;
         final kotlin.jvm.functions.Function1 recoveredCapture_374_7 = var7;
         final kotlin.jvm.functions.Function1 recoveredCapture_374_8 = var8;
         final kotlin.jvm.functions.Function0 recoveredCapture_374_9 = var9;
         final kotlin.jvm.functions.Function1 recoveredCapture_374_10 = var10;
         final kotlin.jvm.functions.Function0 recoveredCapture_374_11 = var11;
         final kotlin.jvm.functions.Function0 recoveredCapture_374_12 = var12;
         final kotlin.jvm.functions.Function0 recoveredCapture_374_13 = var13;
         final kotlin.jvm.functions.Function0 recoveredCapture_374_14 = var14;
         final kotlin.jvm.functions.Function0 recoveredCapture_374_15 = var15;
         final kotlin.jvm.functions.Function0 recoveredCapture_374_16 = var16;
         final kotlin.jvm.functions.Function1 recoveredCapture_374_17 = var17;
         final kotlin.jvm.functions.Function1 recoveredCapture_374_18 = var18;
         final kotlin.jvm.functions.Function1 recoveredCapture_374_19 = var19;
         final kotlin.jvm.functions.Function0 recoveredCapture_374_20 = var20;
         final kotlin.jvm.functions.Function1 recoveredCapture_374_21 = var21;
         final kotlin.jvm.functions.Function0 recoveredCapture_374_22 = var22;
         final kotlin.jvm.functions.Function1 recoveredCapture_374_23 = var23;
         final kotlin.jvm.functions.Function1 recoveredCapture_374_24 = var24;
         final kotlin.jvm.functions.Function1 recoveredCapture_374_25 = var25;
         final kotlin.jvm.functions.Function1 recoveredCapture_374_26 = var26;
         final kotlin.jvm.functions.Function1 recoveredCapture_374_27 = var27;
         final kotlin.jvm.functions.Function1 recoveredCapture_374_28 = var28;
         final kotlin.jvm.functions.Function1 recoveredCapture_374_29 = var29;
         final kotlin.jvm.functions.Function1 recoveredCapture_374_30 = var30;
         final androidx.compose.foundation.lazy.LazyListState recoveredCapture_374_31 = var31;
         final kotlin.jvm.functions.Function0 recoveredCapture_374_32 = var32;
         final kotlin.jvm.functions.Function2 recoveredCapture_374_33 = var33;
         final kotlin.jvm.functions.Function0 recoveredCapture_374_34 = var34;
         final kotlinx.coroutines.flow.SharedFlow recoveredCapture_374_35 = var35;
         final kotlin.jvm.functions.Function2 recoveredCapture_374_36 = var36;
         final io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState recoveredCapture_374_37 = var37;
         final kotlinx.coroutines.flow.StateFlow recoveredCapture_374_38 = var38;
         final kotlin.jvm.functions.Function0 recoveredCapture_374_39 = var39;
         final kotlin.jvm.functions.Function0 recoveredCapture_374_40 = var40;
         final kotlin.jvm.functions.Function0 recoveredCapture_374_41 = var41;
         final kotlin.jvm.functions.Function0 recoveredCapture_374_42 = var42;
         final kotlin.jvm.functions.Function1 recoveredCapture_374_43 = var43;
         final kotlin.jvm.functions.Function1 recoveredCapture_374_44 = var44;
         final kotlin.jvm.functions.Function0 recoveredCapture_374_45 = var45;
         final kotlinx.coroutines.flow.StateFlow recoveredCapture_374_46 = var46;
         final kotlinx.coroutines.flow.StateFlow recoveredCapture_374_47 = var47;
         final int recoveredCapture_374_48 = var49;
         final int recoveredCapture_374_49 = var50;
         final int recoveredCapture_374_50 = var51;
         final int recoveredCapture_374_51 = var52;
         final int recoveredCapture_374_52 = var53;
         final int recoveredCapture_374_53 = var54;
         final int recoveredCapture_374_54 = var55;
         var148.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_374_0, recoveredArgument_374_1) -> ConversationScreenKt.ConversationScreenContent$lambda$194(recoveredCapture_374_0, recoveredCapture_374_1, recoveredCapture_374_2, recoveredCapture_374_3, recoveredCapture_374_4, recoveredCapture_374_5, recoveredCapture_374_6, recoveredCapture_374_7, recoveredCapture_374_8, recoveredCapture_374_9, recoveredCapture_374_10, recoveredCapture_374_11, recoveredCapture_374_12, recoveredCapture_374_13, recoveredCapture_374_14, recoveredCapture_374_15, recoveredCapture_374_16, recoveredCapture_374_17, recoveredCapture_374_18, recoveredCapture_374_19, recoveredCapture_374_20, recoveredCapture_374_21, recoveredCapture_374_22, recoveredCapture_374_23, recoveredCapture_374_24, recoveredCapture_374_25, recoveredCapture_374_26, recoveredCapture_374_27, recoveredCapture_374_28, recoveredCapture_374_29, recoveredCapture_374_30, recoveredCapture_374_31, recoveredCapture_374_32, recoveredCapture_374_33, recoveredCapture_374_34, recoveredCapture_374_35, recoveredCapture_374_36, recoveredCapture_374_37, recoveredCapture_374_38, recoveredCapture_374_39, recoveredCapture_374_40, recoveredCapture_374_41, recoveredCapture_374_42, recoveredCapture_374_43, recoveredCapture_374_44, recoveredCapture_374_45, recoveredCapture_374_46, recoveredCapture_374_47, recoveredCapture_374_48, recoveredCapture_374_49, recoveredCapture_374_50, recoveredCapture_374_51, recoveredCapture_374_52, recoveredCapture_374_53, recoveredCapture_374_54, (androidx.compose.runtime.Composer)recoveredArgument_374_0, ((java.lang.Integer)recoveredArgument_374_1).intValue())));
      }
   }

   private static final boolean isLazyListScrolled(List var0, float var1, int var2) {
      LazyListItemInfo var10000 = (LazyListItemInfo)CollectionsKt.lastOrNull(var0);
      int var3 = var10000 != null ? var10000.getIndex() : 0;
      int var4 = 0;
      Iterable var6 = CollectionsKt.reversed(var0);
      int var7 = 0;
      ArrayList var8 = new ArrayList();

      for (Object var10 : var6) {
         LazyListItemInfo var11 = (LazyListItemInfo)var10;
         int var12 = 0;
         boolean var13 = var4 <= var1;
         var4 += var11.getSize();
         if (!var13) {
            break;
         }

         var8.add(var10);
      }

      List var5 = var8;
      return var2 - var3 > var5.size();
   }

   @Composable
   private static final PaddingValues getPaddingValuesForComposer(PaddingValues var0, BottomBarUiState var1, Composer var2, int var3) {
      var2.startReplaceGroup(-849083091);
      ComposerKt.sourceInformation(var2, "C(getPaddingValuesForComposer)893@41999L7:ConversationScreen.kt#fnibcj");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(-849083091, var3, -1, "io.intercom.android.sdk.m5.conversation.ui.getPaddingValuesForComposer (ConversationScreen.kt:892)");
      }

      PaddingValues var18;
      if (var1.getComposerState() instanceof io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput) {
         CompositionLocal var6 = CompositionLocalsKt.getLocalLayoutDirection();
         int var7 = 0;
         int var8 = 0;
         ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var9 = var2.consume(var6);
         ComposerKt.sourceInformationMarkerEnd(var2);
         LayoutDirection var5 = (LayoutDirection)var9;
         float var10000 = PaddingKt.calculateStartPadding(var0, var5);
         float var10001 = var0.recovered$00be2fd8b9cc26a4();
         float var10002 = PaddingKt.calculateEndPadding(var0, var5);
         float var10 = var0.recovered$7be19aa93c4abc66();
         float var12 = MessageComposerKt.getComposerHalfSize();
         int var15 = 0;
         float var11 = Dp.recovered$95b3d103663bf060(var10 - var12);
         int var13 = 0;
         int var16 = 0;
         float var14 = Dp.recovered$95b3d103663bf060(var13);
         int var17 = 0;
         var18 = PaddingKt.recovered$e24264c71fc15ac4(var10000, var10001, var10002, Dp.recovered$95b3d103663bf060(RangesKt.coerceAtLeast(var11, var14)));
      } else {
         var18 = var0;
      }

      PaddingValues var4 = var18;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var2.endReplaceGroup();
      return var4;
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ConversationScreenContentPreview(Composer var0, int var1) {
      Composer var4 = var0.startRestartGroup(-1340943046);
      ComposerKt.sourceInformation(var4, "C(ConversationScreenContentPreview)927@43195L2096,927@43181L2110:ConversationScreen.kt#fnibcj");
      if (var1 == 0 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1340943046, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.ConversationScreenContentPreview (ConversationScreen.kt:907)"
            );
         }

         io.intercom.android.sdk.models.Header$Expanded$Style var3 = io.intercom.android.sdk.models.Header$Expanded$Style.CAPTION;
         List var10003 = CollectionsKt.listOf(new io.intercom.android.sdk.models.Header$Expanded$Body(var3, "Active 15m ago", null));
         AvatarType var10004 = AvatarType.LAYERED_BUBBLES;
         AvatarWrapper[] var5 = new AvatarWrapper[3];
         Avatar var10009 = Avatar.create("", "M");
         Intrinsics.checkNotNullExpressionValue(var10009, "create(...)");
         var5[0] = new AvatarWrapper(var10009, false, 2, null);
         Avatar var6 = Avatar.create("", "S");
         Intrinsics.checkNotNullExpressionValue(var6, "create(...)");
         var5[1] = new AvatarWrapper(var6, false, 2, null);
         Avatar var7 = Avatar.create("", "D");
         Intrinsics.checkNotNullExpressionValue(var7, "create(...)");
         var5[2] = new AvatarWrapper(var7, false, 2, null);
         ExpandedTeamPresenceState var2 = new ExpandedTeamPresenceState(
            "Banana", var10003, var10004, CollectionsKt.listOf(var5), CollectionsKt.emptyList(), CollectionsKt.emptyList(), false
         );
         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(1448885348, true, new ConversationScreenKt$ConversationScreenContentPreview$1(var2), var4, 54),
            var4,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var4.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_375_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_375_0, recoveredArgument_375_1) -> ConversationScreenKt.ConversationScreenContentPreview$lambda$196(recoveredCapture_375_0, (androidx.compose.runtime.Composer)recoveredArgument_375_0, ((java.lang.Integer)recoveredArgument_375_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void NewConversationScreenContentPreview(Composer var0, int var1) {
      Composer var4 = var0.startRestartGroup(-1946511650);
      ComposerKt.sourceInformation(var4, "C(NewConversationScreenContentPreview)987@45954L2351,987@45940L2365:ConversationScreen.kt#fnibcj");
      if (var1 == 0 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1946511650, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.NewConversationScreenContentPreview (ConversationScreen.kt:969)"
            );
         }

         io.intercom.android.sdk.models.Header$Expanded$Style var3 = io.intercom.android.sdk.models.Header$Expanded$Style.CAPTION;
         List var10003 = CollectionsKt.listOf(new io.intercom.android.sdk.models.Header$Expanded$Body(var3, "Active 15m ago", null));
         AvatarType var10004 = AvatarType.LAYERED_BUBBLES;
         Avatar var10007 = Avatar.create("", "S");
         Intrinsics.checkNotNullExpressionValue(var10007, "create(...)");
         ExpandedTeamPresenceState var2 = new ExpandedTeamPresenceState(
            "Banana",
            var10003,
            var10004,
            CollectionsKt.listOf(new AvatarWrapper(var10007, false, 2, null)),
            CollectionsKt.emptyList(),
            CollectionsKt.emptyList(),
            false
         );
         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(-2080970892, true, new ConversationScreenKt$NewConversationScreenContentPreview$1(var2), var4, 54),
            var4,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var4.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_376_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_376_0, recoveredArgument_376_1) -> ConversationScreenKt.NewConversationScreenContentPreview$lambda$197(recoveredCapture_376_0, (androidx.compose.runtime.Composer)recoveredArgument_376_0, ((java.lang.Integer)recoveredArgument_376_1).intValue())));
      }
   }

   private static final Unit ConversationScreen$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$3$lambda$2() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$5$lambda$4() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$7$lambda$6(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$9$lambda$8(TicketType var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$11$lambda$10(HeaderMenuItem var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$13$lambda$12(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final MutableState ConversationScreen$lambda$16$lambda$15() {
      return SnapshotStateKt.mutableStateOf$default(TuplesKt.to(false, "0"), null, 2, null);
   }

   private static final Pair ConversationScreen$lambda$17(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Pair)var1.getValue();
   }

   private static final void ConversationScreen$lambda$18(MutableState var0, Pair var1) {
      MutableState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setValue(var1);
   }

   private static final MediaUploadItem ConversationScreen$lambda$20(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (MediaUploadItem)var1.getValue();
   }

   private static final void ConversationScreen$lambda$21(MutableState var0, MediaUploadItem var1) {
      MutableState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setValue(var1);
   }

   private static final Part ConversationScreen$lambda$23(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Part)var1.getValue();
   }

   private static final void ConversationScreen$lambda$24(MutableState var0, Part var1) {
      MutableState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setValue(var1);
   }

   private static final MutableState ConversationScreen$lambda$26$lambda$25() {
      return SnapshotStateKt.mutableStateOf$default(null, null, 2, null);
   }

   private static final io.intercom.android.sdk.m5.upload.data.MediaData$Gif ConversationScreen$lambda$27(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (io.intercom.android.sdk.m5.upload.data.MediaData$Gif)var1.getValue();
   }

   private static final void ConversationScreen$lambda$28(MutableState var0, io.intercom.android.sdk.m5.upload.data.MediaData$Gif var1) {
      MutableState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setValue(var1);
   }

   private static final Unit ConversationScreen$lambda$31$lambda$30(ConversationViewModel var0, MutableState var1, List var2) {
      Intrinsics.checkNotNullParameter(var2, "it");
      if ((Uri)CollectionsKt.firstOrNull(var2) != null) {
         int var4 = 0;
         if (ConversationScreen$lambda$27(var1) != null) {
            io.intercom.android.sdk.m5.upload.data.MediaData$Gif var10001 = ConversationScreen$lambda$27(var1);
            Intrinsics.checkNotNull(var10001);
            var0.sendMediaData(var10001);
            ConversationScreen$lambda$28(var1, null);
         }
      }

      return Unit.INSTANCE;
   }

   private static final boolean ConversationScreen$arePushNotificationsEnabled(Context var0) {
      return VERSION.SDK_INT >= 33 ? ContextCompat.checkSelfPermission(var0, "android.permission.POST_NOTIFICATIONS") == 0 : true;
   }

   private static final DisposableEffectResult ConversationScreen$lambda$35$lambda$34(DisposableEffectScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$DisposableEffect");
      int var2 = 0;
      return new ConversationScreenKt$ConversationScreen$lambda$35$lambda$34$RecoveredInnere3b0c44298fc1c14$inlined$onDispose$1();
   }

   private static final Unit ConversationScreen$lambda$37$lambda$36(MutableState var0) {
      ConversationScreen$lambda$18(var0, TuplesKt.to(false, "0"));
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$44$lambda$39$lambda$38(ConversationViewModel var0, MediaUploadItem var1) {
      var0.retryMediaItemUpload(var1);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$44$lambda$41$lambda$40(ConversationViewModel var0, MediaUploadItem var1) {
      var0.deleteMediaUploadItem(var1);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$44$lambda$43$lambda$42(MutableState var0) {
      ConversationScreen$lambda$21(var0, null);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$49$lambda$46$lambda$45(ConversationViewModel var0, Part var1) {
      var0.deleteFailedMessage(var1);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$49$lambda$48$lambda$47(MutableState var0) {
      ConversationScreen$lambda$24(var0, null);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$59$lambda$58(ConversationViewModel var0, ReplySuggestion var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0.onSuggestionClick(var1);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$61$lambda$60(ConversationViewModel var0, ReplyOption var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0.onReplyOptionClicked(var1);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$63$lambda$62(ConversationViewModel var0, Context var1, String var2, List var3) {
      Intrinsics.checkNotNullParameter(var2, "message");
      Intrinsics.checkNotNullParameter(var3, "mediaUploadItems");
      var0.sendMessage(var2, var3, ConversationScreen$arePushNotificationsEnabled(var1));
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$65$lambda$64(ConversationViewModel var0, ComposerInputType var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0.onInputChange(var1);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$67$lambda$66(ManagedActivityResultLauncher var0, MutableState var1, Block var2) {
      Intrinsics.checkNotNullParameter(var2, "it");
      int var3 = var2.getWidth();
      int var4 = var2.getHeight();
      String var10001 = var2.getAttribution();
      Intrinsics.checkNotNullExpressionValue(var10001, "getAttribution(...)");
      String var5 = var10001;
      String var7 = var2.getUrl();
      Intrinsics.checkNotNullExpressionValue(var7, "getUrl(...)");
      String var6 = var7;
      ConversationScreen$lambda$28(var1, new io.intercom.android.sdk.m5.upload.data.MediaData$Gif(var3, var4, var6, var5));
      io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$Companion var10003 = IntercomPreviewFile.Companion;
      String var10004 = var2.getUrl();
      Intrinsics.checkNotNullExpressionValue(var10004, "getUrl(...)");
      var0.launch(new IntercomPreviewArgs(CollectionsKt.listOfNotNull(var10003.network(var10004, "image/gif")), null, null, true, 0, null, 54, null));
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$69$lambda$68(ConversationViewModel var0, String var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0.onGifSearchQueryChange(var1);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$71$lambda$70(ConversationViewModel var0) {
      var0.loadGifs();
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$74$lambda$73(ConversationViewModel var0, int var1, Context var2, List var3) {
      Intrinsics.checkNotNullParameter(var3, "uris");
      Iterable var5 = var3;
      int var6 = 0;
      Iterable var7 = var5;
      ArrayList var8 = new ArrayList();
      int var9 = 0;
      Iterable var10 = var7;
      int var11 = 0;

      for (Object var13 : var10) {
         Object var14 = var13;
         int var15 = 0;
         Uri var16 = (Uri)var14;
         int var17 = 0;
         io.intercom.android.sdk.m5.upload.data.MediaData$Media var10000 = URIExtensionsKt.getMediaData$default(var16, var2, false, 2, null);
         if (var10000 != null) {
            Object var18 = var10000;
            int var19 = 0;
            var8.add(var18);
         }
      }

      List var4 = (List)var8;
      var0.uploadMediaItems(var4, var1);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$76$lambda$75(ConversationViewModel var0) {
      var0.updateBottomSheet(io.intercom.android.sdk.m5.conversation.states.BottomSheetState$MediaInput.INSTANCE);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$78$lambda$77(ConversationViewModel var0) {
      var0.showMediaItemsLimitDialog();
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$80$lambda$79(ConversationViewModel var0) {
      var0.updateBottomSheet(io.intercom.android.sdk.m5.conversation.states.BottomSheetState$TeammatePresence.INSTANCE);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$82$lambda$81(ConversationViewModel var0) {
      var0.onRetryClick();
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$84$lambda$83(ConversationViewModel var0, Part var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0.onRetryMessageClicked(var1);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$86$lambda$85(ConversationViewModel var0, Part var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0.onFailedMessageOptionsClicked(var1);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$88$lambda$87(ConversationViewModel var0, io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0.onRetryMediaClicked(var1);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$90$lambda$89(ConversationViewModel var0) {
      var0.onTyping();
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$92$lambda$91(ConversationViewModel var0, AttributeData var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0.onSubmitAttribute(var1.getAttribute(), var1.getPartId());
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$94$lambda$93(ConversationViewModel var0, String var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0.trackClickedInput(var1);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$96$lambda$95(ConversationViewModel var0, MetricData var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0.trackMetric(var1);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$98$lambda$97(ConversationViewModel var0, ConversationScrolledState var1) {
      Intrinsics.checkNotNullParameter(var1, "conversationScrollState");
      var0.onConversationScrolled(var1);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$100$lambda$99(ConversationViewModel var0, int var1) {
      var0.onJumpToBottomButtonClicked(var1);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$102$lambda$101(Function1 var0, String var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0.invoke(var1);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$104$lambda$103(ConversationViewModel var0) {
      var0.onPrivacyNoticeDismissed();
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$106$lambda$105(ConversationViewModel var0, List var1, List var2) {
      Intrinsics.checkNotNullParameter(var1, "selectedSources");
      Intrinsics.checkNotNullParameter(var2, "sources");
      var0.updateBottomSheet(new io.intercom.android.sdk.m5.conversation.states.BottomSheetState$InlineSources(var1, var2));
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$108$lambda$107(ConversationViewModel var0) {
      var0.onReportAiAnswer();
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$110$lambda$109(ConversationViewModel var0, boolean var1, boolean var2) {
      var0.updatePushNotificationsBanner(var1, var2);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreen$lambda$111(
      ConversationViewModel var0,
      Modifier var1,
      Function0 var2,
      Function0 var3,
      Function0 var4,
      Function1 var5,
      Function1 var6,
      Function1 var7,
      Function1 var8,
      int var9,
      int var10,
      Composer var11,
      int var12
   ) {
      ConversationScreen(var0, var1, var2, var3, var4, var5, var6, var7, var8, var11, RecomposeScopeImplKt.updateChangedFlags(var9 | 1), var10);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$115$lambda$114(ReplySuggestion var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$117$lambda$116(ReplyOption var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$119$lambda$118(String var0, List var1) {
      Intrinsics.checkNotNullParameter(var0, "<unused var>");
      Intrinsics.checkNotNullParameter(var1, "<unused var>");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$121$lambda$120(ComposerInputType var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$123$lambda$122(Block var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$125$lambda$124(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$127$lambda$126() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$129$lambda$128(List var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$131$lambda$130() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$133$lambda$132() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$135$lambda$134() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$137$lambda$136() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$139$lambda$138() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$141$lambda$140(Part var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$143$lambda$142(Part var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$145$lambda$144(io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$147$lambda$146() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$149$lambda$148(AttributeData var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$151$lambda$150() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$153$lambda$152(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$155$lambda$154(HeaderMenuItem var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$157$lambda$156(TicketType var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$159$lambda$158(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$161$lambda$160(MetricData var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$163$lambda$162(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$165$lambda$164() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$167$lambda$166() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$169$lambda$168(boolean var0, boolean var1) {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$171$lambda$170() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$173$lambda$172() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$175$lambda$174() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$177$lambda$176() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$179$lambda$178(MediaUploadItem var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$181$lambda$180(MediaUploadItem var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$183$lambda$182() {
      return Unit.INSTANCE;
   }

   private static final KeyboardState ConversationScreenContent$lambda$184(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (KeyboardState)var1.getValue();
   }

   private static final MutableState ConversationScreenContent$lambda$186$lambda$185() {
      return SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
   }

   private static final Unit ConversationScreenContent$lambda$188$lambda$187(Function2 var0, boolean var1) {
      if (var1) {
         var0.invoke(true, true);
      } else {
         var0.invoke(true, false);
      }

      return Unit.INSTANCE;
   }

   private static final void ConversationScreenContent$hideBottomSheet(CoroutineScope var0, MutableState var1) {
      BuildersKt.launch$default(var0, null, null, new ConversationScreenKt$ConversationScreenContent$hideBottomSheet$1(var1, null), 3, null);
   }

   private static final void ConversationScreenContent$hideKeyboardAndShowBottomSheet(
      SoftwareKeyboardController var0, CoroutineScope var1, MutableState var2, State var3
   ) {
      if (var0 != null) {
         var0.hide();
      }

      BuildersKt.launch$default(
         var1, null, null, new ConversationScreenKt$ConversationScreenContent$hideKeyboardAndShowBottomSheet$1(var2, var3, null), 3, null
      );
   }

   private static final void ConversationScreenContent$showBottomSheet(CoroutineScope var0, MutableState var1) {
      BuildersKt.launch$default(var0, null, null, new ConversationScreenKt$ConversationScreenContent$showBottomSheet$1(var1, null), 3, null);
   }

   private static final Unit ConversationScreenContent$lambda$192$lambda$191(CoroutineScope var0, MutableState var1) {
      ConversationScreenContent$hideBottomSheet(var0, var1);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContent$lambda$194(
      Modifier var0,
      ConversationUiState var1,
      SnackbarHostState var2,
      Function1 var3,
      Function1 var4,
      Function2 var5,
      Function1 var6,
      Function1 var7,
      Function1 var8,
      Function0 var9,
      Function1 var10,
      Function0 var11,
      Function0 var12,
      Function0 var13,
      Function0 var14,
      Function0 var15,
      Function0 var16,
      Function1 var17,
      Function1 var18,
      Function1 var19,
      Function0 var20,
      Function1 var21,
      Function0 var22,
      Function1 var23,
      Function1 var24,
      Function1 var25,
      Function1 var26,
      Function1 var27,
      Function1 var28,
      Function1 var29,
      Function1 var30,
      LazyListState var31,
      Function0 var32,
      Function2 var33,
      Function0 var34,
      SharedFlow var35,
      Function2 var36,
      VoiceTranscriptionState var37,
      StateFlow var38,
      Function0 var39,
      Function0 var40,
      Function0 var41,
      Function0 var42,
      Function1 var43,
      Function1 var44,
      Function0 var45,
      StateFlow var46,
      StateFlow var47,
      int var48,
      int var49,
      int var50,
      int var51,
      int var52,
      int var53,
      int var54,
      Composer var55,
      int var56
   ) {
      ConversationScreenContent(
         var0,
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
         var16,
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
         var38,
         var39,
         var40,
         var41,
         var42,
         var43,
         var44,
         var45,
         var46,
         var47,
         var55,
         RecomposeScopeImplKt.updateChangedFlags(var48 | 1),
         RecomposeScopeImplKt.updateChangedFlags(var49),
         RecomposeScopeImplKt.updateChangedFlags(var50),
         RecomposeScopeImplKt.updateChangedFlags(var51),
         RecomposeScopeImplKt.updateChangedFlags(var52),
         var53,
         var54
      );
      return Unit.INSTANCE;
   }

   private static final Unit ConversationScreenContentPreview$lambda$196(int var0, Composer var1, int var2) {
      ConversationScreenContentPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit NewConversationScreenContentPreview$lambda$197(int var0, Composer var1, int var2) {
      NewConversationScreenContentPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final Object access$showNetworkMessage(NetworkState var0, SnackbarHostState var1, Context var2, Function0 var3, Continuation var4) {
      return showNetworkMessage(var0, var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static final void access$ConversationScreenContent$hideBottomSheet(CoroutineScope var0, MutableState var1) {
      ConversationScreenContent$hideBottomSheet(var0, var1);
   }

   // $VF: synthetic method
   public static final void access$ConversationScreen$lambda$18(MutableState var0, Pair var1) {
      ConversationScreen$lambda$18(var0, var1);
   }

   // $VF: synthetic method
   public static final void access$ConversationScreen$lambda$21(MutableState var0, MediaUploadItem var1) {
      ConversationScreen$lambda$21(var0, var1);
   }

   // $VF: synthetic method
   public static final void access$ConversationScreen$lambda$24(MutableState var0, Part var1) {
      ConversationScreen$lambda$24(var0, var1);
   }

   // $VF: synthetic method
   public static final KeyboardState access$ConversationScreenContent$lambda$184(State var0) {
      return ConversationScreenContent$lambda$184(var0);
   }

   // $VF: synthetic method
   public static final boolean access$isLazyListScrolled(List var0, float var1, int var2) {
      return isLazyListScrolled(var0, var1, var2);
   }

   // $VF: synthetic method
   public static final void access$ConversationScreenContent$hideKeyboardAndShowBottomSheet(
      SoftwareKeyboardController var0, CoroutineScope var1, MutableState var2, State var3
   ) {
      ConversationScreenContent$hideKeyboardAndShowBottomSheet(var0, var1, var2, var3);
   }

   // $VF: synthetic method
   public static final void access$ConversationScreenContent$showBottomSheet(CoroutineScope var0, MutableState var1) {
      ConversationScreenContent$showBottomSheet(var0, var1);
   }

   // $VF: synthetic method
   public static final float access$getJumpToBottomNewMessagesTopOffset$p() {
      return JumpToBottomNewMessagesTopOffset;
   }

   // $VF: synthetic method
   public static final PaddingValues access$getPaddingValuesForComposer(PaddingValues var0, BottomBarUiState var1, Composer var2, int var3) {
      return getPaddingValuesForComposer(var0, var1, var2, var3);
   }

   static {
      int var0 = 180;
      int var1 = 0;
      JumpToBottomNewMessagesTopOffset = Dp.recovered$95b3d103663bf060(var0);
      int $this$dp$ivx = 40;
      int $i$f$getDpx = 0;
      LazyListJumpToBottomScrollOffset = Dp.recovered$95b3d103663bf060($this$dp$ivx);
   }
}
