package io.intercom.android.sdk.m5.conversation;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.helpcenter.articles.ArticleMetadata;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.conversation.data.ConversationRepository;
import io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository;
import io.intercom.android.sdk.m5.conversation.data.MediaUploadStateRepository;
import io.intercom.android.sdk.m5.conversation.data.MediaUploadStateRepository$AddResult;
import io.intercom.android.sdk.m5.conversation.metrics.MetricData;
import io.intercom.android.sdk.m5.conversation.metrics.MetricData$ComposerInputClicked;
import io.intercom.android.sdk.m5.conversation.metrics.MetricData$ConversationsInMoreMenuClicked;
import io.intercom.android.sdk.m5.conversation.metrics.MetricData$HelpInMoreMenuClicked;
import io.intercom.android.sdk.m5.conversation.metrics.MetricData$MoreMenuClicked;
import io.intercom.android.sdk.m5.conversation.metrics.MetricData$NewConversationInMoreMenuClicked;
import io.intercom.android.sdk.m5.conversation.metrics.MetricData$PoweredByClicked;
import io.intercom.android.sdk.m5.conversation.metrics.MetricData$TicketsInMoreMenuClicked;
import io.intercom.android.sdk.m5.conversation.reducers.ConversationReducer;
import io.intercom.android.sdk.m5.conversation.reducers.InitialStateReducerKt;
import io.intercom.android.sdk.m5.conversation.states.BottomSheetState;
import io.intercom.android.sdk.m5.conversation.states.BottomSheetState$GifSearch;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.FloatingIndicatorState;
import io.intercom.android.sdk.m5.conversation.states.FloatingIndicatorState$JumpToBottomIndicator;
import io.intercom.android.sdk.m5.conversation.states.JumpToBottomButtonState;
import io.intercom.android.sdk.m5.conversation.states.LaunchMode;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem;
import io.intercom.android.sdk.m5.conversation.states.NetworkState$Connected;
import io.intercom.android.sdk.m5.conversation.states.NetworkState$Reconnected;
import io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData;
import io.intercom.android.sdk.m5.conversation.states.ReplySuggestion;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Idle;
import io.intercom.android.sdk.m5.conversation.streaming.FinStreamingStateHolder;
import io.intercom.android.sdk.m5.conversation.streaming.StreamingMessageState$Streaming;
import io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposerInputType;
import io.intercom.android.sdk.m5.conversation.usecase.AppendPartToConversationUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.AudioRecordingUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.ChangeInputUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.ConversationScrolledState;
import io.intercom.android.sdk.m5.conversation.usecase.FallbackPollingUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.GetNetworkState;
import io.intercom.android.sdk.m5.conversation.usecase.HandleFinStreamingDeltaUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.LoadGifUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.MarkUserContentAsSeenByAdmin;
import io.intercom.android.sdk.m5.conversation.usecase.OpenConversationUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.PushNotificationsBannerUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.RecordOpenedInteractionUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.RefreshConversationUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.RefreshUnreadConversationsCountUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.SendGifUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.SendMediaUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.SendMessageUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.SendQuickReplyUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.SendSuggestionUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.SettleStreamingPartUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.ShowAdminIsTypingUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.SoundEffectsUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.SubmitAttributeUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.TrackLastReceivedPartsUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.UpdateFloatingIndicatorUseCase;
import io.intercom.android.sdk.m5.conversation.usecase.VoiceTranscriptionUseCase;
import io.intercom.android.sdk.m5.conversation.utils.SoundPlayer;
import io.intercom.android.sdk.m5.conversation.utils.audio.AudioRecordingManager;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.m5.upload.data.MediaData;
import io.intercom.android.sdk.m5.upload.data.MediaData$Gif;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.models.Attribute;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.ReplyOption;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import io.intercom.android.sdk.utilities.connectivity.NetworkConnectivityMonitor;
import io.intercom.android.sdk.utilities.extensions.ContextExtensionsKt;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.Job.DefaultImpls;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.flow.MutableSharedFlow;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.SharedFlow;
import kotlinx.coroutines.flow.SharedFlowKt;
import kotlinx.coroutines.flow.SharingStarted;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.flow.StateFlowKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000Ú\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000e\b\u0001\u0018\u0000 Ì\u00012\u00020\u0001:\u0002Ì\u0001B«\u0003\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0013\u0012\b\b\u0002\u0010\u0014\u001a\u00020\u0015\u0012\b\b\u0002\u0010\u0016\u001a\u00020\u0017\u0012\b\b\u0002\u0010\u0018\u001a\u00020\u0019\u0012\b\b\u0002\u0010\u001a\u001a\u00020\u001b\u0012\b\b\u0002\u0010\u001c\u001a\u00020\u001d\u0012\b\b\u0002\u0010\u001e\u001a\u00020\u001f\u0012\b\b\u0002\u0010 \u001a\u00020!\u0012\b\b\u0002\u0010\"\u001a\u00020#\u0012\b\b\u0002\u0010$\u001a\u00020%\u0012\b\b\u0002\u0010&\u001a\u00020'\u0012\b\b\u0002\u0010(\u001a\u00020)\u0012\b\b\u0002\u0010*\u001a\u00020+\u0012\b\b\u0002\u0010,\u001a\u00020-\u0012\b\b\u0002\u0010.\u001a\u00020/\u0012\b\b\u0002\u00100\u001a\u000201\u0012\b\b\u0002\u00102\u001a\u000203\u0012\b\b\u0002\u00104\u001a\u000205\u0012\b\b\u0002\u00106\u001a\u000207\u0012\b\b\u0002\u00108\u001a\u000209\u0012\b\b\u0002\u0010:\u001a\u00020;\u0012\b\b\u0002\u0010<\u001a\u00020=\u0012\b\b\u0002\u0010>\u001a\u00020?\u0012\b\b\u0002\u0010@\u001a\u00020A\u0012\b\b\u0002\u0010B\u001a\u00020C\u0012\b\b\u0002\u0010D\u001a\u00020E\u0012\b\b\u0002\u0010F\u001a\u00020G\u0012\b\b\u0002\u0010H\u001a\u00020I\u0012\b\b\u0002\u0010J\u001a\u00020K\u0012\b\b\u0002\u0010L\u001a\u00020M\u0012\b\b\u0002\u0010N\u001a\u00020O\u0012\b\b\u0002\u0010P\u001a\u00020Q\u0012\b\b\u0002\u0010R\u001a\u00020S¢\u0006\u0004\bT\u0010UJ\b\u0010|\u001a\u0004\u0018\u00010\u0003J\u000f\u0010}\u001a\u00020~2\u0007\u0010\u007f\u001a\u00030\u0080\u0001J\u0010\u0010\u0081\u0001\u001a\u00020~2\u0007\u0010\u007f\u001a\u00030\u0080\u0001J\u0011\u0010\u0082\u0001\u001a\u00020~2\b\u0010\u0083\u0001\u001a\u00030\u0084\u0001J\u0011\u0010\u0085\u0001\u001a\u00020~2\b\u0010\u0086\u0001\u001a\u00030\u0087\u0001J*\u0010\u0088\u0001\u001a\u00020~2\u0007\u0010\u0089\u0001\u001a\u00020\u00032\u000f\u0010\u008a\u0001\u001a\n\u0012\u0005\u0012\u00030\u008c\u00010\u008b\u00012\u0007\u0010\u008d\u0001\u001a\u00020oJ\u0011\u0010\u008e\u0001\u001a\u00020~2\b\u0010\u008f\u0001\u001a\u00030\u0090\u0001J\u0011\u0010\u0091\u0001\u001a\u00020~2\b\u0010\u0092\u0001\u001a\u00030\u0093\u0001J\u0011\u0010\u0094\u0001\u001a\u00020~2\b\u0010\u0095\u0001\u001a\u00030\u0096\u0001J\u0012\u0010\u0097\u0001\u001a\u00020~2\u0007\u0010\u0098\u0001\u001a\u00020\u0003H\u0007J\u0011\u0010\u0099\u0001\u001a\u00020~2\b\u0010\u009a\u0001\u001a\u00030\u009b\u0001J\t\u0010\u009c\u0001\u001a\u00020\u0003H\u0002J\u0013\u0010\u009d\u0001\u001a\u00020~2\b\u0010\u009e\u0001\u001a\u00030\u009f\u0001H\u0002J\u0007\u0010 \u0001\u001a\u00020~J\u0010\u0010¡\u0001\u001a\u00020~2\u0007\u0010¢\u0001\u001a\u00020\u0003J\u0007\u0010£\u0001\u001a\u00020~J\u001a\u0010¤\u0001\u001a\u00020~2\b\u0010¥\u0001\u001a\u00030¦\u00012\u0007\u0010§\u0001\u001a\u00020\u0003J\u0013\u0010¨\u0001\u001a\u00020~2\b\u0010\u009e\u0001\u001a\u00030©\u0001H\u0002J\u0007\u0010ª\u0001\u001a\u00020~J\u0011\u0010«\u0001\u001a\u00020~2\b\u0010¬\u0001\u001a\u00030\u00ad\u0001J\u0011\u0010®\u0001\u001a\u00020~2\b\u0010\u009e\u0001\u001a\u00030¯\u0001J$\u0010°\u0001\u001a\u00020~2\u000f\u0010±\u0001\u001a\n\u0012\u0005\u0012\u00030©\u00010\u008b\u00012\n\b\u0002\u0010²\u0001\u001a\u00030\u0087\u0001J\u0011\u0010³\u0001\u001a\u00020~2\b\u0010´\u0001\u001a\u00030\u008c\u0001J\u0011\u0010µ\u0001\u001a\u00020~2\b\u0010´\u0001\u001a\u00030\u008c\u0001J\u0011\u0010¶\u0001\u001a\u00020~2\b\u0010´\u0001\u001a\u00030\u008c\u0001J\u0007\u0010·\u0001\u001a\u00020~J\u0011\u0010¸\u0001\u001a\u00020~2\b\u0010¹\u0001\u001a\u00030º\u0001J\u0011\u0010»\u0001\u001a\u00020~2\b\u0010¹\u0001\u001a\u00030º\u0001J\u0011\u0010¼\u0001\u001a\u00020~2\b\u0010¹\u0001\u001a\u00030º\u0001J\u0011\u0010½\u0001\u001a\u00020~2\b\u0010¾\u0001\u001a\u00030¿\u0001J\u0007\u0010À\u0001\u001a\u00020~J\u0007\u0010Á\u0001\u001a\u00020~J\t\u0010Â\u0001\u001a\u00020~H\u0002J\t\u0010Ã\u0001\u001a\u00020~H\u0014J\u0007\u0010Ä\u0001\u001a\u00020~J\u0007\u0010Å\u0001\u001a\u00020~J\u0019\u0010Æ\u0001\u001a\u00020~2\u0007\u0010Ç\u0001\u001a\u00020o2\u0007\u0010\u008d\u0001\u001a\u00020oJ\u0007\u0010È\u0001\u001a\u00020~J\u0007\u0010É\u0001\u001a\u00020~J\u0007\u0010Ê\u0001\u001a\u00020~J\u0007\u0010Ë\u0001\u001a\u00020~R\u0010\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u001dX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020!X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020#X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020%X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010&\u001a\u00020'X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020)X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010*\u001a\u00020+X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010,\u001a\u00020-X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010.\u001a\u00020/X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u00100\u001a\u000201X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u00102\u001a\u000203X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u00104\u001a\u000205X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u00108\u001a\u000209X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010:\u001a\u00020;X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010<\u001a\u00020=X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010>\u001a\u00020?X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010@\u001a\u00020AX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010B\u001a\u00020CX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010D\u001a\u00020EX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010F\u001a\u00020GX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010H\u001a\u00020IX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010J\u001a\u00020KX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010L\u001a\u00020MX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010N\u001a\u00020OX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010P\u001a\u00020QX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010R\u001a\u00020SX\u0082\u0004¢\u0006\u0002\n\u0000R\u0018\u0010V\u001a\n W*\u0004\u0018\u00010\u00030\u0003X\u0082\u0004¢\u0006\u0004\n\u0002\u0010XR\u0014\u0010Y\u001a\b\u0012\u0004\u0012\u00020[0ZX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\\\u001a\b\u0012\u0004\u0012\u00020^0]¢\u0006\b\n\u0000\u001a\u0004\b_\u0010`R\u0014\u0010a\u001a\b\u0012\u0004\u0012\u00020b0ZX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010c\u001a\b\u0012\u0004\u0012\u00020e0dX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010f\u001a\b\u0012\u0004\u0012\u00020e0g¢\u0006\b\n\u0000\u001a\u0004\bh\u0010iR\u0014\u0010j\u001a\b\u0012\u0004\u0012\u00020k0dX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010l\u001a\b\u0012\u0004\u0012\u00020k0g¢\u0006\b\n\u0000\u001a\u0004\bm\u0010iR\u0014\u0010n\u001a\b\u0012\u0004\u0012\u00020o0ZX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010p\u001a\u0004\u0018\u00010qX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010r\u001a\u0004\u0018\u00010qX\u0082\u000e¢\u0006\u0002\n\u0000R\u0017\u0010s\u001a\b\u0012\u0004\u0012\u00020t0]8F¢\u0006\u0006\u001a\u0004\bu\u0010`R\u000e\u0010v\u001a\u00020wX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010x\u001a\b\u0012\u0004\u0012\u00020y0]8F¢\u0006\u0006\u001a\u0004\bz\u0010`R\u0017\u0010{\u001a\b\u0012\u0004\u0012\u00020o0]¢\u0006\b\n\u0000\u001a\u0004\b{\u0010`¨\u0006Í\u0001",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/ConversationViewModel;",
         "Landroidx/lifecycle/ViewModel;",
         "initialConversationId",
         "",
         "launchMode",
         "Lio/intercom/android/sdk/m5/conversation/states/LaunchMode;",
         "articleMetadata",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleMetadata;",
         "initialMessage",
         "networkConnectivityMonitor",
         "Lio/intercom/android/sdk/utilities/connectivity/NetworkConnectivityMonitor;",
         "soundPlayer",
         "Lio/intercom/android/sdk/m5/conversation/utils/SoundPlayer;",
         "nexusCoroutineScope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "audioAmplitudeCoroutineScope",
         "conversationRepository",
         "Lio/intercom/android/sdk/m5/conversation/data/ConversationRepository;",
         "conversationReducer",
         "Lio/intercom/android/sdk/m5/conversation/reducers/ConversationReducer;",
         "trackLastReceivedPartsUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/TrackLastReceivedPartsUseCase;",
         "recordOpenedInteractionUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/RecordOpenedInteractionUseCase;",
         "soundEffectsUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/SoundEffectsUseCase;",
         "sendSuggestionUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/SendSuggestionUseCase;",
         "refreshUnreadConversationsCountUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/RefreshUnreadConversationsCountUseCase;",
         "refreshConversationUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/RefreshConversationUseCase;",
         "appendPartToConversationUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/AppendPartToConversationUseCase;",
         "openConversationUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/OpenConversationUseCase;",
         "sendMessageUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/SendMessageUseCase;",
         "sendQuickReplyUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/SendQuickReplyUseCase;",
         "loadGifUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/LoadGifUseCase;",
         "changeInputUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/ChangeInputUseCase;",
         "sendGifUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/SendGifUseCase;",
         "mediaUploadRepository",
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository;",
         "mediaUploadStateRepository",
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadStateRepository;",
         "sendMediaUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/SendMediaUseCase;",
         "updateFloatingIndicatorUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/UpdateFloatingIndicatorUseCase;",
         "getNetworkState",
         "Lio/intercom/android/sdk/m5/conversation/usecase/GetNetworkState;",
         "adminIsTypingUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/ShowAdminIsTypingUseCase;",
         "submitAttributeUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/SubmitAttributeUseCase;",
         "fallbackPollingUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/FallbackPollingUseCase;",
         "pushNotificationsBannerUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/PushNotificationsBannerUseCase;",
         "audioRecordingManager",
         "Lio/intercom/android/sdk/m5/conversation/utils/audio/AudioRecordingManager;",
         "audioRecordingUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/AudioRecordingUseCase;",
         "voiceTranscriptionUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/VoiceTranscriptionUseCase;",
         "markUserContentAsSeenByAdmin",
         "Lio/intercom/android/sdk/m5/conversation/usecase/MarkUserContentAsSeenByAdmin;",
         "dispatcher",
         "Lkotlinx/coroutines/CoroutineDispatcher;",
         "metricTracker",
         "Lio/intercom/android/sdk/metrics/MetricTracker;",
         "intercomDataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "handleFinStreamingDeltaUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/HandleFinStreamingDeltaUseCase;",
         "settleStreamingPartUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/SettleStreamingPartUseCase;",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "<init>",
         "(Ljava/lang/String;Lio/intercom/android/sdk/m5/conversation/states/LaunchMode;Lio/intercom/android/sdk/helpcenter/articles/ArticleMetadata;Ljava/lang/String;Lio/intercom/android/sdk/utilities/connectivity/NetworkConnectivityMonitor;Lio/intercom/android/sdk/m5/conversation/utils/SoundPlayer;Lkotlinx/coroutines/CoroutineScope;Lkotlinx/coroutines/CoroutineScope;Lio/intercom/android/sdk/m5/conversation/data/ConversationRepository;Lio/intercom/android/sdk/m5/conversation/reducers/ConversationReducer;Lio/intercom/android/sdk/m5/conversation/usecase/TrackLastReceivedPartsUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/RecordOpenedInteractionUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/SoundEffectsUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/SendSuggestionUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/RefreshUnreadConversationsCountUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/RefreshConversationUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/AppendPartToConversationUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/OpenConversationUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/SendMessageUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/SendQuickReplyUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/LoadGifUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/ChangeInputUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/SendGifUseCase;Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository;Lio/intercom/android/sdk/m5/conversation/data/MediaUploadStateRepository;Lio/intercom/android/sdk/m5/conversation/usecase/SendMediaUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/UpdateFloatingIndicatorUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/GetNetworkState;Lio/intercom/android/sdk/m5/conversation/usecase/ShowAdminIsTypingUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/SubmitAttributeUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/FallbackPollingUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/PushNotificationsBannerUseCase;Lio/intercom/android/sdk/m5/conversation/utils/audio/AudioRecordingManager;Lio/intercom/android/sdk/m5/conversation/usecase/AudioRecordingUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/VoiceTranscriptionUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/MarkUserContentAsSeenByAdmin;Lkotlinx/coroutines/CoroutineDispatcher;Lio/intercom/android/sdk/metrics/MetricTracker;Lio/intercom/android/sdk/m5/data/IntercomDataLayer;Lio/intercom/android/sdk/m5/conversation/usecase/HandleFinStreamingDeltaUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/SettleStreamingPartUseCase;Lcom/intercom/twig/Twig;)V",
         "decodedInitialMessage",
         "kotlin.jvm.PlatformType",
         "Ljava/lang/String;",
         "clientState",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "uiState",
         "Lkotlinx/coroutines/flow/StateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationUiState;",
         "getUiState",
         "()Lkotlinx/coroutines/flow/StateFlow;",
         "gifQueryStateFlow",
         "Lio/intercom/android/sdk/m5/conversation/SearchQuery;",
         "_uiEffect",
         "Lkotlinx/coroutines/flow/MutableSharedFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationUiEffect;",
         "uiEffect",
         "Lkotlinx/coroutines/flow/SharedFlow;",
         "getUiEffect",
         "()Lkotlinx/coroutines/flow/SharedFlow;",
         "_composerUiEffect",
         "Lio/intercom/android/sdk/m5/conversation/states/ComposerUiEffect;",
         "composerUiEffect",
         "getComposerUiEffect",
         "resumedState",
         "",
         "adminIsTypingJob",
         "Lkotlinx/coroutines/Job;",
         "voiceRecordingTimeoutJob",
         "amplitudeLevel",
         "",
         "getAmplitudeLevel",
         "finStreamingStateHolder",
         "Lio/intercom/android/sdk/m5/conversation/streaming/FinStreamingStateHolder;",
         "streamingMessageState",
         "Lio/intercom/android/sdk/m5/conversation/streaming/StreamingMessageState;",
         "getStreamingMessageState",
         "isFinStreamingActive",
         "getConversationId",
         "onResume",
         "",
         "context",
         "Landroid/content/Context;",
         "onPause",
         "onConversationScrolled",
         "conversationScrolledState",
         "Lio/intercom/android/sdk/m5/conversation/usecase/ConversationScrolledState;",
         "onJumpToBottomButtonClicked",
         "lastSeenItemIndex",
         "",
         "sendMessage",
         "messageText",
         "mediaUploadItems",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/MediaUploadItem;",
         "arePushNotificationsEnabled",
         "onSuggestionClick",
         "suggestion",
         "Lio/intercom/android/sdk/m5/conversation/states/ReplySuggestion;",
         "onReplyOptionClicked",
         "replyOption",
         "Lio/intercom/android/sdk/models/ReplyOption;",
         "onInputChange",
         "inputType",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposerInputType;",
         "trackClickedInput",
         "input",
         "trackMetric",
         "metricData",
         "Lio/intercom/android/sdk/m5/conversation/metrics/MetricData;",
         "getConversationContext",
         "sendGif",
         "mediaData",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Gif;",
         "loadGifs",
         "onGifSearchQueryChange",
         "searchQuery",
         "onRetryClick",
         "onSubmitAttribute",
         "attribute",
         "Lio/intercom/android/sdk/models/Attribute;",
         "partId",
         "sendMedia",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;",
         "showMediaItemsLimitDialog",
         "updateBottomSheet",
         "bottomSheetState",
         "Lio/intercom/android/sdk/m5/conversation/states/BottomSheetState;",
         "sendMediaData",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData;",
         "uploadMediaItems",
         "mediaItems",
         "transparencyBackgroundColor",
         "deleteMediaUploadItem",
         "uploadItem",
         "retryMediaItemUpload",
         "onFailedMediaUploadItem",
         "clearMediaUploadItems",
         "onRetryMessageClicked",
         "part",
         "Lio/intercom/android/sdk/models/Part;",
         "onFailedMessageOptionsClicked",
         "deleteFailedMessage",
         "onRetryMediaClicked",
         "failedMediaUploadData",
         "Lio/intercom/android/sdk/m5/conversation/states/PendingMessage$FailedMediaUploadData;",
         "onTyping",
         "onNetworkMessageDismissed",
         "loadConversation",
         "onCleared",
         "onPrivacyNoticeDismissed",
         "onReportAiAnswer",
         "updatePushNotificationsBanner",
         "dismissed",
         "startVoiceRecording",
         "stopVoiceRecordingAndTranscribe",
         "cancelVoiceRecording",
         "onTranscribedTextConsumed",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nConversationViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationViewModel.kt\nio/intercom/android/sdk/m5/conversation/ConversationViewModel\n+ 2 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n+ 3 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n+ 4 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n+ 5 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n+ 6 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,864:1\n49#2:865\n51#2:869\n46#3:866\n51#3:868\n105#4:867\n230#5,5:870\n230#5,5:875\n230#5,5:880\n230#5,5:885\n230#5,5:890\n230#5,3:895\n233#5,2:899\n230#5,5:901\n230#5,5:906\n230#5,5:911\n1#6:898\n*S KotlinDebug\n*F\n+ 1 ConversationViewModel.kt\nio/intercom/android/sdk/m5/conversation/ConversationViewModel\n*L\n221#1:865\n221#1:869\n221#1:866\n221#1:868\n221#1:867\n455#1:870,5\n466#1:875,5\n586#1:880,5\n599#1:885,5\n636#1:890,5\n706#1:895,3\n706#1:899,2\n733#1:901,5\n757#1:906,5\n817#1:911,5\n*E\n"
)
public final class ConversationViewModel extends ViewModel {
   @NotNull
   public static final ConversationViewModel$Companion Companion = new ConversationViewModel$Companion(null);
   @Nullable
   private final String initialConversationId;
   @NotNull
   private final LaunchMode launchMode;
   @Nullable
   private final ArticleMetadata articleMetadata;
   @NotNull
   private final NetworkConnectivityMonitor networkConnectivityMonitor;
   @NotNull
   private final SoundPlayer soundPlayer;
   @NotNull
   private final CoroutineScope nexusCoroutineScope;
   @NotNull
   private final CoroutineScope audioAmplitudeCoroutineScope;
   @NotNull
   private final ConversationRepository conversationRepository;
   @NotNull
   private final ConversationReducer conversationReducer;
   @NotNull
   private final TrackLastReceivedPartsUseCase trackLastReceivedPartsUseCase;
   @NotNull
   private final RecordOpenedInteractionUseCase recordOpenedInteractionUseCase;
   @NotNull
   private final SoundEffectsUseCase soundEffectsUseCase;
   @NotNull
   private final SendSuggestionUseCase sendSuggestionUseCase;
   @NotNull
   private final RefreshUnreadConversationsCountUseCase refreshUnreadConversationsCountUseCase;
   @NotNull
   private final RefreshConversationUseCase refreshConversationUseCase;
   @NotNull
   private final AppendPartToConversationUseCase appendPartToConversationUseCase;
   @NotNull
   private final OpenConversationUseCase openConversationUseCase;
   @NotNull
   private final SendMessageUseCase sendMessageUseCase;
   @NotNull
   private final SendQuickReplyUseCase sendQuickReplyUseCase;
   @NotNull
   private final LoadGifUseCase loadGifUseCase;
   @NotNull
   private final ChangeInputUseCase changeInputUseCase;
   @NotNull
   private final SendGifUseCase sendGifUseCase;
   @NotNull
   private final MediaUploadRepository mediaUploadRepository;
   @NotNull
   private final MediaUploadStateRepository mediaUploadStateRepository;
   @NotNull
   private final SendMediaUseCase sendMediaUseCase;
   @NotNull
   private final UpdateFloatingIndicatorUseCase updateFloatingIndicatorUseCase;
   @NotNull
   private final ShowAdminIsTypingUseCase adminIsTypingUseCase;
   @NotNull
   private final SubmitAttributeUseCase submitAttributeUseCase;
   @NotNull
   private final FallbackPollingUseCase fallbackPollingUseCase;
   @NotNull
   private final PushNotificationsBannerUseCase pushNotificationsBannerUseCase;
   @NotNull
   private final AudioRecordingManager audioRecordingManager;
   @NotNull
   private final AudioRecordingUseCase audioRecordingUseCase;
   @NotNull
   private final VoiceTranscriptionUseCase voiceTranscriptionUseCase;
   @NotNull
   private final MarkUserContentAsSeenByAdmin markUserContentAsSeenByAdmin;
   @NotNull
   private final CoroutineDispatcher dispatcher;
   @NotNull
   private final MetricTracker metricTracker;
   @NotNull
   private final IntercomDataLayer intercomDataLayer;
   @NotNull
   private final HandleFinStreamingDeltaUseCase handleFinStreamingDeltaUseCase;
   @NotNull
   private final SettleStreamingPartUseCase settleStreamingPartUseCase;
   @NotNull
   private final Twig twig;
   private final String decodedInitialMessage;
   @NotNull
   private final MutableStateFlow clientState;
   @NotNull
   private final StateFlow uiState;
   @NotNull
   private final MutableStateFlow gifQueryStateFlow;
   @NotNull
   private final MutableSharedFlow _uiEffect;
   @NotNull
   private final SharedFlow uiEffect;
   @NotNull
   private final MutableSharedFlow _composerUiEffect;
   @NotNull
   private final SharedFlow composerUiEffect;
   @NotNull
   private final MutableStateFlow resumedState;
   @Nullable
   private Job adminIsTypingJob;
   @Nullable
   private Job voiceRecordingTimeoutJob;
   @NotNull
   private final FinStreamingStateHolder finStreamingStateHolder;
   @NotNull
   private final StateFlow isFinStreamingActive;
   public static final int $stable;

   static { $stable = 8; }
   private static final long DEBOUNCE_DELAY_MS = 500L;

   public ConversationViewModel(
      @Nullable String var1,
      @NotNull LaunchMode var2,
      @Nullable ArticleMetadata var3,
      @NotNull String var4,
      @NotNull NetworkConnectivityMonitor var5,
      @NotNull SoundPlayer var6,
      @NotNull CoroutineScope var7,
      @NotNull CoroutineScope var8,
      @NotNull ConversationRepository var9,
      @NotNull ConversationReducer var10,
      @NotNull TrackLastReceivedPartsUseCase var11,
      @NotNull RecordOpenedInteractionUseCase var12,
      @NotNull SoundEffectsUseCase var13,
      @NotNull SendSuggestionUseCase var14,
      @NotNull RefreshUnreadConversationsCountUseCase var15,
      @NotNull RefreshConversationUseCase var16,
      @NotNull AppendPartToConversationUseCase var17,
      @NotNull OpenConversationUseCase var18,
      @NotNull SendMessageUseCase var19,
      @NotNull SendQuickReplyUseCase var20,
      @NotNull LoadGifUseCase var21,
      @NotNull ChangeInputUseCase var22,
      @NotNull SendGifUseCase var23,
      @NotNull MediaUploadRepository var24,
      @NotNull MediaUploadStateRepository var25,
      @NotNull SendMediaUseCase var26,
      @NotNull UpdateFloatingIndicatorUseCase var27,
      @NotNull GetNetworkState var28,
      @NotNull ShowAdminIsTypingUseCase var29,
      @NotNull SubmitAttributeUseCase var30,
      @NotNull FallbackPollingUseCase var31,
      @NotNull PushNotificationsBannerUseCase var32,
      @NotNull AudioRecordingManager var33,
      @NotNull AudioRecordingUseCase var34,
      @NotNull VoiceTranscriptionUseCase var35,
      @NotNull MarkUserContentAsSeenByAdmin var36,
      @NotNull CoroutineDispatcher var37,
      @NotNull MetricTracker var38,
      @NotNull IntercomDataLayer var39,
      @NotNull HandleFinStreamingDeltaUseCase var40,
      @NotNull SettleStreamingPartUseCase var41,
      @NotNull Twig var42
   ) {
      Intrinsics.checkNotNullParameter(var2, "launchMode");
      Intrinsics.checkNotNullParameter(var4, "initialMessage");
      Intrinsics.checkNotNullParameter(var5, "networkConnectivityMonitor");
      Intrinsics.checkNotNullParameter(var6, "soundPlayer");
      Intrinsics.checkNotNullParameter(var7, "nexusCoroutineScope");
      Intrinsics.checkNotNullParameter(var8, "audioAmplitudeCoroutineScope");
      Intrinsics.checkNotNullParameter(var9, "conversationRepository");
      Intrinsics.checkNotNullParameter(var10, "conversationReducer");
      Intrinsics.checkNotNullParameter(var11, "trackLastReceivedPartsUseCase");
      Intrinsics.checkNotNullParameter(var12, "recordOpenedInteractionUseCase");
      Intrinsics.checkNotNullParameter(var13, "soundEffectsUseCase");
      Intrinsics.checkNotNullParameter(var14, "sendSuggestionUseCase");
      Intrinsics.checkNotNullParameter(var15, "refreshUnreadConversationsCountUseCase");
      Intrinsics.checkNotNullParameter(var16, "refreshConversationUseCase");
      Intrinsics.checkNotNullParameter(var17, "appendPartToConversationUseCase");
      Intrinsics.checkNotNullParameter(var18, "openConversationUseCase");
      Intrinsics.checkNotNullParameter(var19, "sendMessageUseCase");
      Intrinsics.checkNotNullParameter(var20, "sendQuickReplyUseCase");
      Intrinsics.checkNotNullParameter(var21, "loadGifUseCase");
      Intrinsics.checkNotNullParameter(var22, "changeInputUseCase");
      Intrinsics.checkNotNullParameter(var23, "sendGifUseCase");
      Intrinsics.checkNotNullParameter(var24, "mediaUploadRepository");
      Intrinsics.checkNotNullParameter(var25, "mediaUploadStateRepository");
      Intrinsics.checkNotNullParameter(var26, "sendMediaUseCase");
      Intrinsics.checkNotNullParameter(var27, "updateFloatingIndicatorUseCase");
      Intrinsics.checkNotNullParameter(var28, "getNetworkState");
      Intrinsics.checkNotNullParameter(var29, "adminIsTypingUseCase");
      Intrinsics.checkNotNullParameter(var30, "submitAttributeUseCase");
      Intrinsics.checkNotNullParameter(var31, "fallbackPollingUseCase");
      Intrinsics.checkNotNullParameter(var32, "pushNotificationsBannerUseCase");
      Intrinsics.checkNotNullParameter(var33, "audioRecordingManager");
      Intrinsics.checkNotNullParameter(var34, "audioRecordingUseCase");
      Intrinsics.checkNotNullParameter(var35, "voiceTranscriptionUseCase");
      Intrinsics.checkNotNullParameter(var36, "markUserContentAsSeenByAdmin");
      Intrinsics.checkNotNullParameter(var37, "dispatcher");
      Intrinsics.checkNotNullParameter(var38, "metricTracker");
      Intrinsics.checkNotNullParameter(var39, "intercomDataLayer");
      Intrinsics.checkNotNullParameter(var40, "handleFinStreamingDeltaUseCase");
      Intrinsics.checkNotNullParameter(var41, "settleStreamingPartUseCase");
      Intrinsics.checkNotNullParameter(var42, "twig");
      super();
      this.initialConversationId = var1;
      this.launchMode = var2;
      this.articleMetadata = var3;
      this.networkConnectivityMonitor = var5;
      this.soundPlayer = var6;
      this.nexusCoroutineScope = var7;
      this.audioAmplitudeCoroutineScope = var8;
      this.conversationRepository = var9;
      this.conversationReducer = var10;
      this.trackLastReceivedPartsUseCase = var11;
      this.recordOpenedInteractionUseCase = var12;
      this.soundEffectsUseCase = var13;
      this.sendSuggestionUseCase = var14;
      this.refreshUnreadConversationsCountUseCase = var15;
      this.refreshConversationUseCase = var16;
      this.appendPartToConversationUseCase = var17;
      this.openConversationUseCase = var18;
      this.sendMessageUseCase = var19;
      this.sendQuickReplyUseCase = var20;
      this.loadGifUseCase = var21;
      this.changeInputUseCase = var22;
      this.sendGifUseCase = var23;
      this.mediaUploadRepository = var24;
      this.mediaUploadStateRepository = var25;
      this.sendMediaUseCase = var26;
      this.updateFloatingIndicatorUseCase = var27;
      this.adminIsTypingUseCase = var29;
      this.submitAttributeUseCase = var30;
      this.fallbackPollingUseCase = var31;
      this.pushNotificationsBannerUseCase = var32;
      this.audioRecordingManager = var33;
      this.audioRecordingUseCase = var34;
      this.voiceTranscriptionUseCase = var35;
      this.markUserContentAsSeenByAdmin = var36;
      this.dispatcher = var37;
      this.metricTracker = var38;
      this.intercomDataLayer = var39;
      this.handleFinStreamingDeltaUseCase = var40;
      this.settleStreamingPartUseCase = var41;
      this.twig = var42;
      this.decodedInitialMessage = Uri.decode(var4);
      String var43 = this.initialConversationId;
      String var10003 = this.decodedInitialMessage;
      Intrinsics.checkNotNullExpressionValue(this.decodedInitialMessage, "decodedInitialMessage");
      ComposerState$TextInput var44 = new ComposerState$TextInput(
         var10003, new StringRes(string.intercom_reply_to_conversation, null, 2, null), false, null, null, null, 60, null
      );
      ArticleMetadata var45 = this.articleMetadata;
      LaunchMode var46 = this.launchMode;
      this.clientState = StateFlowKt.MutableStateFlow(
         new ConversationClientState(
            null,
            null,
            var43,
            null,
            var44,
            null,
            var46,
            null,
            var45,
            null,
            null,
            null,
            null,
            null,
            0,
            0,
            null,
            null,
            false,
            false,
            null,
            null,
            null,
            8388267,
            null
         )
      );
      Flow var49 = this.clientState;
      ConversationReducer var50 = this.conversationReducer;
      int var51 = 0;
      Flow var52 = var49;
      int var47 = 0;
      int var48 = 0;
      this.uiState = FlowKt.stateIn(
         new ConversationViewModel$special$RecoveredInnere3b0c44298fc1c14$inlined$map$1(var52, var50),
         ViewModelKt.getViewModelScope(this),
         SharingStarted.Companion.WhileSubscribed$default(SharingStarted.Companion, 5000L, 0L, 2, null),
         InitialStateReducerKt.reduceInitialState(((ConversationClientState)this.clientState.getValue()).getLaunchMode())
      );
      this.gifQueryStateFlow = StateFlowKt.MutableStateFlow(SearchQuery$None.INSTANCE);
      this._uiEffect = SharedFlowKt.MutableSharedFlow$default(0, 0, null, 7, null);
      this.uiEffect = FlowKt.shareIn$default(this._uiEffect, ViewModelKt.getViewModelScope(this), SharingStarted.Companion.getEagerly(), 0, 4, null);
      this._composerUiEffect = SharedFlowKt.MutableSharedFlow$default(0, 0, null, 7, null);
      this.composerUiEffect = FlowKt.shareIn$default(
         this._composerUiEffect, ViewModelKt.getViewModelScope(this), SharingStarted.Companion.getEagerly(), 0, 4, null
      );
      this.resumedState = StateFlowKt.MutableStateFlow(false);
      this.finStreamingStateHolder = new FinStreamingStateHolder();
      this.isFinStreamingActive = FlowKt.stateIn(
         FlowKt.combine(this.finStreamingStateHolder.getState(), this.clientState, new ConversationViewModel$isFinStreamingActive$1(null)),
         ViewModelKt.getViewModelScope(this),
         SharingStarted.Companion.WhileSubscribed$default(SharingStarted.Companion, 0L, 0L, 3, null),
         this.finStreamingStateHolder.getState().getValue() instanceof StreamingMessageState$Streaming
      );
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$1(this, null), 2, null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$2(this, null), 2, null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$3(this, null), 2, null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$4(this, null), 2, null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$5(this, null), 2, null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$6(this, null), 2, null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$7(this, null), 2, null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$8(var28, this, null), 2, null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$9(this, null), 2, null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$10(this, null), 2, null);
      if (this.initialConversationId == null) {
         Injector.get().getDataLayer().clearOpenResponse();
      }

      this.loadConversation();
   }

   // $VF: synthetic method
   public ConversationViewModel(
      String var1,
      LaunchMode var2,
      ArticleMetadata var3,
      String var4,
      NetworkConnectivityMonitor var5,
      SoundPlayer var6,
      CoroutineScope var7,
      CoroutineScope var8,
      ConversationRepository var9,
      ConversationReducer var10,
      TrackLastReceivedPartsUseCase var11,
      RecordOpenedInteractionUseCase var12,
      SoundEffectsUseCase var13,
      SendSuggestionUseCase var14,
      RefreshUnreadConversationsCountUseCase var15,
      RefreshConversationUseCase var16,
      AppendPartToConversationUseCase var17,
      OpenConversationUseCase var18,
      SendMessageUseCase var19,
      SendQuickReplyUseCase var20,
      LoadGifUseCase var21,
      ChangeInputUseCase var22,
      SendGifUseCase var23,
      MediaUploadRepository var24,
      MediaUploadStateRepository var25,
      SendMediaUseCase var26,
      UpdateFloatingIndicatorUseCase var27,
      GetNetworkState var28,
      ShowAdminIsTypingUseCase var29,
      SubmitAttributeUseCase var30,
      FallbackPollingUseCase var31,
      PushNotificationsBannerUseCase var32,
      AudioRecordingManager var33,
      AudioRecordingUseCase var34,
      VoiceTranscriptionUseCase var35,
      MarkUserContentAsSeenByAdmin var36,
      CoroutineDispatcher var37,
      MetricTracker var38,
      IntercomDataLayer var39,
      HandleFinStreamingDeltaUseCase var40,
      SettleStreamingPartUseCase var41,
      Twig var42,
      int var43,
      int var44,
      DefaultConstructorMarker var45
   ) {
      if ((var43 & 1) != 0) {
         var1 = null;
      }

      if ((var43 & 4) != 0) {
         var3 = null;
      }

      if ((var43 & 16) != 0) {
         var5 = new NetworkConnectivityMonitor();
      }

      if ((var43 & 32) != 0) {
         var6 = new SoundPlayer();
      }

      if ((var43 & 64) != 0) {
         var7 = CoroutineScopeKt.CoroutineScope(Dispatchers.getIO());
      }

      if ((var43 & 128) != 0) {
         var8 = CoroutineScopeKt.CoroutineScope(Dispatchers.getIO());
      }

      if ((var43 & 256) != 0) {
         var9 = new ConversationRepository(null, null, null, null, null, null, var7, 63, null);
      }

      if ((var43 & 512) != 0) {
         var10 = new ConversationReducer(null, null, 3, null);
      }

      if ((var43 & 1024) != 0) {
         var11 = new TrackLastReceivedPartsUseCase(null, null, 3, null);
      }

      if ((var43 & 2048) != 0) {
         var12 = new RecordOpenedInteractionUseCase(var9);
      }

      if ((var43 & 4096) != 0) {
         var13 = new SoundEffectsUseCase(null, var6, 1, null);
      }

      if ((var43 & 8192) != 0) {
         var14 = new SendSuggestionUseCase(var9, var13, null, 4, null);
      }

      if ((var43 & 16384) != 0) {
         var15 = new RefreshUnreadConversationsCountUseCase(null, null, 3, null);
      }

      if ((var43 & 32768) != 0) {
         var16 = new RefreshConversationUseCase(var9, null, var11, var12, 2, null);
      }

      if ((var43 & 65536) != 0) {
         var17 = new AppendPartToConversationUseCase(var11, var12);
      }

      if ((var43 & 131072) != 0) {
         var18 = new OpenConversationUseCase(var16, var15, null, 4, null);
      }

      if ((var43 & 262144) != 0) {
         var19 = new SendMessageUseCase(var9, var16, var13, null, null, 24, null);
      }

      if ((var43 & 524288) != 0) {
         var20 = new SendQuickReplyUseCase(var9, var16, var13, null, 8, null);
      }

      if ((var43 & 1048576) != 0) {
         var21 = new LoadGifUseCase(var9);
      }

      if ((var43 & 2097152) != 0) {
         var22 = new ChangeInputUseCase();
      }

      if ((var43 & 4194304) != 0) {
         var23 = new SendGifUseCase(var19, var22);
      }

      if ((var43 & 8388608) != 0) {
         var24 = new MediaUploadRepository(var9, null, null, 6, null);
      }

      if ((var43 & 16777216) != 0) {
         var25 = new MediaUploadStateRepository(var24);
      }

      if ((var43 & 33554432) != 0) {
         var26 = new SendMediaUseCase(var19, var24, null, 4, null);
      }

      if ((var43 & 67108864) != 0) {
         var27 = new UpdateFloatingIndicatorUseCase();
      }

      if ((var43 & 134217728) != 0) {
         var28 = new GetNetworkState(var5);
      }

      if ((var43 & 268435456) != 0) {
         var29 = new ShowAdminIsTypingUseCase();
      }

      if ((var43 & 536870912) != 0) {
         var30 = new SubmitAttributeUseCase(var9);
      }

      if ((var43 & 1073741824) != 0) {
         var31 = new FallbackPollingUseCase(null, null, var16, 3, null);
      }

      if ((var43 & -2147483648) != 0) {
         var32 = new PushNotificationsBannerUseCase(null, null, 3, null);
      }

      if ((var44 & 1) != 0) {
         Application var10002 = Injector.get().getApplication();
         Intrinsics.checkNotNullExpressionValue(var10002, "getApplication(...)");
         var33 = new AudioRecordingManager(var10002, var8);
      }

      if ((var44 & 2) != 0) {
         var34 = new AudioRecordingUseCase(var33, null, 2, null);
      }

      if ((var44 & 4) != 0) {
         var35 = new VoiceTranscriptionUseCase(var34, var9);
      }

      if ((var44 & 8) != 0) {
         var36 = new MarkUserContentAsSeenByAdmin();
      }

      if ((var44 & 16) != 0) {
         var37 = Dispatchers.getIO();
      }

      if ((var44 & 32) != 0) {
         var38 = Injector.get().getMetricTracker();
      }

      if ((var44 & 64) != 0) {
         var39 = Injector.get().getDataLayer();
      }

      if ((var44 & 128) != 0) {
         var40 = new HandleFinStreamingDeltaUseCase(var39);
      }

      if ((var44 & 256) != 0) {
         var41 = new SettleStreamingPartUseCase();
      }

      if ((var44 & 512) != 0) {
         var42 = LumberMill.getLogger();
      }

      this(
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
         var42
      );
   }

   @NotNull
   public final StateFlow getUiState() {
      return this.uiState;
   }

   @NotNull
   public final SharedFlow getUiEffect() {
      return this.uiEffect;
   }

   @NotNull
   public final SharedFlow getComposerUiEffect() {
      return this.composerUiEffect;
   }

   @NotNull
   public final StateFlow getAmplitudeLevel() {
      return this.audioRecordingManager.getAmplitudeLevel();
   }

   @NotNull
   public final StateFlow getStreamingMessageState() {
      return this.finStreamingStateHolder.getState();
   }

   @NotNull
   public final StateFlow isFinStreamingActive() {
      return this.isFinStreamingActive;
   }

   @Nullable
   public final String getConversationId() {
      return ((ConversationClientState)this.clientState.getValue()).getConversationId();
   }

   public final void onResume(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      this.networkConnectivityMonitor.startListening(var1);
      this.soundPlayer.loadSounds(var1);
      this.resumedState.setValue(true);
   }

   public final void onPause(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      this.networkConnectivityMonitor.stopListening(var1);
      this.soundPlayer.release();
      this.resumedState.setValue(false);
      Activity var10000 = ContextExtensionsKt.findActivity(var1);
      if (var10000 != null ? !var10000.isChangingConfigurations() : true) {
         this.cancelVoiceRecording();
      }
   }

   public final void onConversationScrolled(@NotNull ConversationScrolledState var1) {
      Intrinsics.checkNotNullParameter(var1, "conversationScrolledState");
      MutableStateFlow var2 = this.clientState;
      int var3 = 0;

      Object var4;
      ConversationClientState var7;
      do {
         var4 = var2.getValue();
         var7 = (ConversationClientState)var4;
         int var6 = 0;
         var7 = ConversationClientState.copy$default(
            (ConversationClientState)var7,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            0,
            0,
            null,
            null,
            var1.getScrolled(),
            false,
            null,
            null,
            null,
            8126463,
            null
         );
      } while (!var2.compareAndSet(var4, var7));

      this.updateFloatingIndicatorUseCase.invoke(this.clientState, var1);
   }

   public final void onJumpToBottomButtonClicked(int var1) {
      FloatingIndicatorState var2 = ((ConversationClientState)this.clientState.getValue()).getFloatingIndicatorState();
      if (var2 instanceof FloatingIndicatorState$JumpToBottomIndicator) {
         MutableStateFlow var3 = this.clientState;
         int var4 = 0;

         Object var5;
         Object var8;
         do {
            var5 = var3.getValue();
            ConversationClientState var6 = (ConversationClientState)var5;
            int var7 = 0;
            var8 = ConversationClientState.copy$default(
               var6,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               0,
               0,
               ((FloatingIndicatorState$JumpToBottomIndicator)var2).copy(new JumpToBottomButtonState(0, var1)),
               null,
               false,
               false,
               null,
               null,
               null,
               8323071,
               null
            );
         } while (!var3.compareAndSet(var5, var8));
      }
   }

   public final void sendMessage(@NotNull String var1, @NotNull List var2, boolean var3) {
      Intrinsics.checkNotNullParameter(var1, "messageText");
      Intrinsics.checkNotNullParameter(var2, "mediaUploadItems");
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$sendMessage$1(this, var1, var2, null), 2, null
      );
      this.pushNotificationsBannerUseCase.invoke(this.clientState, var3);
   }

   public final void onSuggestionClick(@NotNull ReplySuggestion var1) {
      Intrinsics.checkNotNullParameter(var1, "suggestion");
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$onSuggestionClick$1(this, var1, null), 2, null
      );
   }

   public final void onReplyOptionClicked(@NotNull ReplyOption var1) {
      Intrinsics.checkNotNullParameter(var1, "replyOption");
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$onReplyOptionClicked$1(this, var1, null), 2, null
      );
   }

   public final void onInputChange(@NotNull ComposerInputType var1) {
      Intrinsics.checkNotNullParameter(var1, "inputType");
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$onInputChange$1(this, var1, null), 2, null
      );
   }

   /** @deprecated */
   @Deprecated(message = "Use trackMetric instead")
   public final void trackClickedInput(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "input");
      MetricTracker var10000 = this.metricTracker;
      String var10001 = ((ConversationClientState)this.clientState.getValue()).getConversationId();
      if (var10001 == null) {
         var10001 = "";
      }

      var10000.clickedInput(var10001, var1);
   }

   public final void trackMetric(@NotNull MetricData var1) {
      Intrinsics.checkNotNullParameter(var1, "metricData");
      MetricData var2 = var1;
      if (var2 instanceof MetricData$MoreMenuClicked) {
         this.metricTracker.moreMenuClicked(this.getConversationContext());
      } else if (var2 instanceof MetricData$ConversationsInMoreMenuClicked) {
         this.metricTracker
            .conversationsInMoreMenuClicked(((MetricData$ConversationsInMoreMenuClicked)var1).isMoreMenuContext() ? "more_menu" : this.getConversationContext());
      } else if (var2 instanceof MetricData$NewConversationInMoreMenuClicked) {
         this.metricTracker
            .newConversationInMoreMenuClicked(
               ((MetricData$NewConversationInMoreMenuClicked)var1).isMoreMenuContext() ? "more_menu" : this.getConversationContext()
            );
      } else if (var2 instanceof MetricData$TicketsInMoreMenuClicked) {
         this.metricTracker
            .ticketsInMoreMenuClicked(((MetricData$TicketsInMoreMenuClicked)var1).isMoreMenuContext() ? "more_menu" : this.getConversationContext());
      } else if (Intrinsics.areEqual(var2, MetricData$HelpInMoreMenuClicked.INSTANCE)) {
         this.metricTracker.helpInMoreMenuClicked(this.getConversationContext());
      } else if (Intrinsics.areEqual(var2, MetricData$PoweredByClicked.INSTANCE)) {
         this.metricTracker.clickedPoweredBy("conversation");
      } else {
         if (!(var2 instanceof MetricData$ComposerInputClicked)) {
            throw new NoWhenBranchMatchedException();
         }

         MetricTracker var10000 = this.metricTracker;
         String var10001 = ((ConversationClientState)this.clientState.getValue()).getConversationId();
         if (var10001 == null) {
            var10001 = "";
         }

         var10000.clickedInput(var10001, ((MetricData$ComposerInputClicked)var1).getInput());
      }
   }

   private final String getConversationContext() {
      return ((ConversationClientState)this.clientState.getValue()).getConversationId() == null ? "new_conversation" : "existing_conversation";
   }

   private final void sendGif(MediaData$Gif var1) {
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$sendGif$1(this, var1, null), 2, null);
   }

   public final void loadGifs() {
      MutableStateFlow var1 = this.clientState;
      int var2 = 0;

      Object var3;
      ConversationClientState var6;
      do {
         var3 = var1.getValue();
         var6 = (ConversationClientState)var3;
         int var5 = 0;
         var6 = ConversationClientState.copy$default(
            (ConversationClientState)var6,
            null,
            null,
            null,
            null,
            null,
            new BottomSheetState$GifSearch(CollectionsKt.emptyList()),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            0,
            0,
            null,
            null,
            false,
            false,
            null,
            null,
            null,
            8388575,
            null
         );
      } while (!var1.compareAndSet(var3, var6));

      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$loadGifs$2(this, null), 2, null);
   }

   public final void onGifSearchQueryChange(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "searchQuery");
      this.gifQueryStateFlow.setValue(new SearchQuery$Query(var1));
   }

   public final void onRetryClick() {
      MutableStateFlow var1 = this.clientState;
      int var2 = 0;

      Object var3;
      ConversationClientState var6;
      do {
         var3 = var1.getValue();
         var6 = (ConversationClientState)var3;
         int var5 = 0;
         var6 = ConversationClientState.copy$default(
            (ConversationClientState)var6,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            0,
            0,
            null,
            null,
            false,
            false,
            null,
            null,
            null,
            8388479,
            null
         );
      } while (!var1.compareAndSet(var3, var6));

      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$onRetryClick$2(this, null), 2, null);
   }

   public final void onSubmitAttribute(@NotNull Attribute var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "attribute");
      Intrinsics.checkNotNullParameter(var2, "partId");
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$onSubmitAttribute$1(this, var1, var2, null), 2, null
      );
   }

   private final void sendMedia(MediaData$Media var1) {
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$sendMedia$1(this, var1, null), 2, null);
   }

   public final void showMediaItemsLimitDialog() {
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$showMediaItemsLimitDialog$1(this, null), 2, null
      );
   }

   public final void updateBottomSheet(@NotNull BottomSheetState var1) {
      Intrinsics.checkNotNullParameter(var1, "bottomSheetState");
      MutableStateFlow var2 = this.clientState;
      int var3 = 0;

      Object var4;
      ConversationClientState var7;
      do {
         var4 = var2.getValue();
         var7 = (ConversationClientState)var4;
         int var6 = 0;
         var7 = ConversationClientState.copy$default(
            (ConversationClientState)var7,
            null,
            null,
            null,
            null,
            null,
            var1,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            0,
            0,
            null,
            null,
            false,
            false,
            null,
            null,
            null,
            8388575,
            null
         );
      } while (!var2.compareAndSet(var4, var7));
   }

   public final void sendMediaData(@NotNull MediaData var1) {
      Intrinsics.checkNotNullParameter(var1, "mediaData");
      MediaData var2 = var1;
      if (var2 instanceof MediaData$Media) {
         this.sendMedia((MediaData$Media)var1);
      } else {
         if (!(var2 instanceof MediaData$Gif)) {
            throw new NoWhenBranchMatchedException();
         }

         this.sendGif((MediaData$Gif)var1);
      }
   }

   public final void uploadMediaItems(@NotNull List var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "mediaItems");
      MediaUploadStateRepository$AddResult var3 = this.mediaUploadStateRepository.addAndStartUploading(var1, ViewModelKt.getViewModelScope(this), var2);
      if (var3.getHasLimitRejections()) {
         this.showMediaItemsLimitDialog();
      }
   }

   // $VF: synthetic method
   public static void uploadMediaItems$default(ConversationViewModel var0, List var1, int var2, int var3, Object var4) {
      if ((var3 & 2) != 0) {
         var2 = -1;
      }

      var0.uploadMediaItems(var1, var2);
   }

   public final void deleteMediaUploadItem(@NotNull MediaUploadItem var1) {
      Intrinsics.checkNotNullParameter(var1, "uploadItem");
      this.mediaUploadStateRepository.removeMediaItem(var1.getInstanceId());
   }

   public final void retryMediaItemUpload(@NotNull MediaUploadItem var1) {
      Intrinsics.checkNotNullParameter(var1, "uploadItem");
      this.mediaUploadStateRepository.retryUpload(var1.getInstanceId(), ViewModelKt.getViewModelScope(this));
   }

   public final void onFailedMediaUploadItem(@NotNull MediaUploadItem var1) {
      Intrinsics.checkNotNullParameter(var1, "uploadItem");
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$onFailedMediaUploadItem$1(this, var1, null), 2, null
      );
   }

   public final void clearMediaUploadItems() {
      this.mediaUploadStateRepository.cancelAllUploads();
   }

   public final void onRetryMessageClicked(@NotNull Part var1) {
      Intrinsics.checkNotNullParameter(var1, "part");
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$onRetryMessageClicked$1(this, var1, null), 2, null
      );
   }

   public final void onFailedMessageOptionsClicked(@NotNull Part var1) {
      Intrinsics.checkNotNullParameter(var1, "part");
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$onFailedMessageOptionsClicked$1(this, var1, null), 2, null
      );
   }

   public final void deleteFailedMessage(@NotNull Part var1) {
      Intrinsics.checkNotNullParameter(var1, "part");
      MutableStateFlow var2 = this.clientState;
      int var3 = 0;

      Object var4;
      ConversationClientState var11;
      do {
         var4 = var2.getValue();
         var11 = (ConversationClientState)var4;
         int var6 = 0;
         Map var7 = MapsKt.toMutableMap(((ConversationClientState)var11).getPendingMessages());
         Map var8 = var7;
         ConversationClientState var9 = (ConversationClientState)var11;
         int var10 = 0;
         var8.remove(var1.getUuid());
         var11 = ConversationClientState.copy$default(
            var9,
            var7,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            0,
            0,
            null,
            null,
            false,
            false,
            null,
            null,
            null,
            8388606,
            null
         );
      } while (!var2.compareAndSet(var4, var11));
   }

   public final void onRetryMediaClicked(@NotNull PendingMessage$FailedMediaUploadData var1) {
      Intrinsics.checkNotNullParameter(var1, "failedMediaUploadData");
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$onRetryMediaClicked$1(this, var1, null), 2, null
      );
   }

   public final void onTyping() {
      String var1 = ((ConversationClientState)this.clientState.getValue()).getConversationId();
      if (var1 != null) {
         this.conversationRepository.nexusEventsRepository().userTyping(var1);
      }
   }

   public final void onNetworkMessageDismissed() {
      if (Intrinsics.areEqual(((ConversationClientState)this.clientState.getValue()).getNetworkState(), NetworkState$Reconnected.INSTANCE)) {
         MutableStateFlow var1 = this.clientState;
         int var2 = 0;

         Object var3;
         ConversationClientState var6;
         do {
            var3 = var1.getValue();
            var6 = (ConversationClientState)var3;
            int var5 = 0;
            var6 = ConversationClientState.copy$default(
               (ConversationClientState)var6,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               NetworkState$Connected.INSTANCE,
               null,
               null,
               null,
               null,
               0,
               0,
               null,
               null,
               false,
               false,
               null,
               null,
               null,
               8388095,
               null
            );
         } while (!var1.compareAndSet(var3, var6));
      }
   }

   private final void loadConversation() {
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$loadConversation$1(this, null), 2, null);
   }

   @Override
   protected void onCleared() {
      super.onCleared();
      CoroutineScopeKt.cancel$default(this.nexusCoroutineScope, null, 1, null);
      CoroutineScopeKt.cancel$default(this.audioAmplitudeCoroutineScope, null, 1, null);
      this.audioRecordingManager.dispose();
      this.mediaUploadStateRepository.cancelAllUploads();
   }

   public final void onPrivacyNoticeDismissed() {
      MutableStateFlow var1 = this.clientState;
      int var2 = 0;

      Object var3;
      ConversationClientState var6;
      do {
         var3 = var1.getValue();
         var6 = (ConversationClientState)var3;
         int var5 = 0;
         var6 = ConversationClientState.copy$default(
            (ConversationClientState)var6,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            0,
            0,
            null,
            null,
            false,
            true,
            null,
            null,
            null,
            7864319,
            null
         );
      } while (!var1.compareAndSet(var3, var6));

      this.conversationRepository.dismissPrivacyPolicy();
   }

   public final void onReportAiAnswer() {
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$onReportAiAnswer$1(this, null), 2, null);
   }

   public final void updatePushNotificationsBanner(boolean var1, boolean var2) {
      this.intercomDataLayer.updatePushNotificationsBannerDismissed(var1);
      this.pushNotificationsBannerUseCase.invoke(this.clientState, var2);
   }

   public final void startVoiceRecording() {
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$startVoiceRecording$1(this, null), 2, null
      );
      if (this.voiceRecordingTimeoutJob != null) {
         DefaultImpls.cancel$default(this.voiceRecordingTimeoutJob, (CancellationException)null, 1, null);
      }

      this.voiceRecordingTimeoutJob = BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$startVoiceRecording$2(this, null), 2, null
      );
   }

   public final void stopVoiceRecordingAndTranscribe() {
      if (this.voiceRecordingTimeoutJob != null) {
         DefaultImpls.cancel$default(this.voiceRecordingTimeoutJob, (CancellationException)null, 1, null);
      }

      this.voiceRecordingTimeoutJob = null;
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$stopVoiceRecordingAndTranscribe$1(this, null), 2, null
      );
   }

   public final void cancelVoiceRecording() {
      if (this.voiceRecordingTimeoutJob != null) {
         DefaultImpls.cancel$default(this.voiceRecordingTimeoutJob, (CancellationException)null, 1, null);
      }

      this.voiceRecordingTimeoutJob = null;
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ConversationViewModel$cancelVoiceRecording$1(this, null), 2, null
      );
   }

   public final void onTranscribedTextConsumed() {
      MutableStateFlow var1 = this.clientState;
      int var2 = 0;

      Object var3;
      ConversationClientState var6;
      do {
         var3 = var1.getValue();
         var6 = (ConversationClientState)var3;
         int var5 = 0;
         var6 = ConversationClientState.copy$default(
            (ConversationClientState)var6,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            0,
            0,
            null,
            null,
            false,
            false,
            null,
            VoiceTranscriptionState$Idle.INSTANCE,
            null,
            6291455,
            null
         );
      } while (!var1.compareAndSet(var3, var6));
   }

   // $VF: synthetic method
   public static final SendMessageUseCase access$getSendMessageUseCase$p(ConversationViewModel var0) {
      return var0.sendMessageUseCase;
   }

   // $VF: synthetic method
   public static final MutableStateFlow access$getClientState$p(ConversationViewModel var0) {
      return var0.clientState;
   }

   // $VF: synthetic method
   public static final SendSuggestionUseCase access$getSendSuggestionUseCase$p(ConversationViewModel var0) {
      return var0.sendSuggestionUseCase;
   }

   // $VF: synthetic method
   public static final SendQuickReplyUseCase access$getSendQuickReplyUseCase$p(ConversationViewModel var0) {
      return var0.sendQuickReplyUseCase;
   }

   // $VF: synthetic method
   public static final ChangeInputUseCase access$getChangeInputUseCase$p(ConversationViewModel var0) {
      return var0.changeInputUseCase;
   }

   // $VF: synthetic method
   public static final SendGifUseCase access$getSendGifUseCase$p(ConversationViewModel var0) {
      return var0.sendGifUseCase;
   }

   // $VF: synthetic method
   public static final LoadGifUseCase access$getLoadGifUseCase$p(ConversationViewModel var0) {
      return var0.loadGifUseCase;
   }

   // $VF: synthetic method
   public static final OpenConversationUseCase access$getOpenConversationUseCase$p(ConversationViewModel var0) {
      return var0.openConversationUseCase;
   }

   // $VF: synthetic method
   public static final SubmitAttributeUseCase access$getSubmitAttributeUseCase$p(ConversationViewModel var0) {
      return var0.submitAttributeUseCase;
   }

   // $VF: synthetic method
   public static final SendMediaUseCase access$getSendMediaUseCase$p(ConversationViewModel var0) {
      return var0.sendMediaUseCase;
   }

   // $VF: synthetic method
   public static final MutableSharedFlow access$get_uiEffect$p(ConversationViewModel var0) {
      return var0._uiEffect;
   }

   // $VF: synthetic method
   public static final MutableSharedFlow access$get_composerUiEffect$p(ConversationViewModel var0) {
      return var0._composerUiEffect;
   }

   // $VF: synthetic method
   public static final VoiceTranscriptionUseCase access$getVoiceTranscriptionUseCase$p(ConversationViewModel var0) {
      return var0.voiceTranscriptionUseCase;
   }

   // $VF: synthetic method
   public static final IntercomDataLayer access$getIntercomDataLayer$p(ConversationViewModel var0) {
      return var0.intercomDataLayer;
   }

   // $VF: synthetic method
   public static final ConversationRepository access$getConversationRepository$p(ConversationViewModel var0) {
      return var0.conversationRepository;
   }

   // $VF: synthetic method
   public static final AppendPartToConversationUseCase access$getAppendPartToConversationUseCase$p(ConversationViewModel var0) {
      return var0.appendPartToConversationUseCase;
   }

   // $VF: synthetic method
   public static final SoundEffectsUseCase access$getSoundEffectsUseCase$p(ConversationViewModel var0) {
      return var0.soundEffectsUseCase;
   }

   // $VF: synthetic method
   public static final RefreshConversationUseCase access$getRefreshConversationUseCase$p(ConversationViewModel var0) {
      return var0.refreshConversationUseCase;
   }

   // $VF: synthetic method
   public static final Job access$getAdminIsTypingJob$p(ConversationViewModel var0) {
      return var0.adminIsTypingJob;
   }

   // $VF: synthetic method
   public static final void access$setAdminIsTypingJob$p(ConversationViewModel var0, Job var1) {
      var0.adminIsTypingJob = var1;
   }

   // $VF: synthetic method
   public static final ShowAdminIsTypingUseCase access$getAdminIsTypingUseCase$p(ConversationViewModel var0) {
      return var0.adminIsTypingUseCase;
   }

   // $VF: synthetic method
   public static final MarkUserContentAsSeenByAdmin access$getMarkUserContentAsSeenByAdmin$p(ConversationViewModel var0) {
      return var0.markUserContentAsSeenByAdmin;
   }

   // $VF: synthetic method
   public static final HandleFinStreamingDeltaUseCase access$getHandleFinStreamingDeltaUseCase$p(ConversationViewModel var0) {
      return var0.handleFinStreamingDeltaUseCase;
   }

   // $VF: synthetic method
   public static final FinStreamingStateHolder access$getFinStreamingStateHolder$p(ConversationViewModel var0) {
      return var0.finStreamingStateHolder;
   }

   // $VF: synthetic method
   public static final SettleStreamingPartUseCase access$getSettleStreamingPartUseCase$p(ConversationViewModel var0) {
      return var0.settleStreamingPartUseCase;
   }

   // $VF: synthetic method
   public static final FallbackPollingUseCase access$getFallbackPollingUseCase$p(ConversationViewModel var0) {
      return var0.fallbackPollingUseCase;
   }

   // $VF: synthetic method
   public static final MutableStateFlow access$getResumedState$p(ConversationViewModel var0) {
      return var0.resumedState;
   }

   // $VF: synthetic method
   public static final RefreshUnreadConversationsCountUseCase access$getRefreshUnreadConversationsCountUseCase$p(ConversationViewModel var0) {
      return var0.refreshUnreadConversationsCountUseCase;
   }

   // $VF: synthetic method
   public static final Twig access$getTwig$p(ConversationViewModel var0) {
      return var0.twig;
   }

   // $VF: synthetic method
   public static final MutableStateFlow access$getGifQueryStateFlow$p(ConversationViewModel var0) {
      return var0.gifQueryStateFlow;
   }

   // $VF: synthetic method
   public static final MediaUploadStateRepository access$getMediaUploadStateRepository$p(ConversationViewModel var0) {
      return var0.mediaUploadStateRepository;
   }
}
