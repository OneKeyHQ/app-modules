package io.intercom.android.sdk.tickets.create.model;

import android.content.Context;
import android.webkit.MimeTypeMap;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.blocks.lib.models.TicketTypeV2;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.survey.QuestionState;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$UploadFileQuestionModel;
import io.intercom.android.sdk.survey.ui.models.Answer;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError$FileLimitExceeded;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Error;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$None;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Success;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem;
import io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData;
import io.intercom.android.sdk.tickets.create.data.TicketAttributeRequest;
import io.intercom.android.sdk.tickets.create.data.TicketRepository;
import io.intercom.android.sdk.tickets.create.reducers.ConditionalAttributesReducerKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
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
   d1 = "\u0000À\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\b\u0001\u0018\u0000 M2\u00020\u0001:\u0003MNOB]\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\u0010\u000e\u001a\u0004\u0018\u00010\u000f\u0012\u0006\u0010\u0010\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0013¢\u0006\u0004\b\u0014\u0010\u0015J\n\u0010(\u001a\u0004\u0018\u00010\u0018H\u0002J\u0018\u0010)\u001a\u00020*2\u000e\b\u0002\u0010+\u001a\b\u0012\u0004\u0012\u00020-0,H\u0002J\u0012\u0010.\u001a\u00020*2\n\b\u0002\u0010/\u001a\u0004\u0018\u000100J\u000e\u00101\u001a\u00020*2\u0006\u00102\u001a\u00020\u000fJ\u001e\u00103\u001a\u0002042\u0006\u00105\u001a\u0002062\f\u00107\u001a\b\u0012\u0004\u0012\u00020\u000f08H\u0002J\u0010\u00109\u001a\u0002042\u0006\u00105\u001a\u000206H\u0002J \u0010:\u001a\u0002042\u0006\u0010;\u001a\u00020<2\u0006\u0010=\u001a\u00020>2\u0006\u0010?\u001a\u00020>H\u0002J\u001c\u0010@\u001a\u00020*2\u0012\u0010A\u001a\u000e\u0012\u0004\u0012\u00020\u001e\u0012\u0004\u0012\u00020*0BH\u0002J\u000e\u0010C\u001a\b\u0012\u0004\u0012\u00020-0,H\u0002J\u000e\u0010D\u001a\u00020*2\u0006\u0010E\u001a\u00020FJ\u000e\u0010G\u001a\u00020*2\u0006\u0010E\u001a\u00020FJ\b\u0010H\u001a\u00020*H\u0002J\u0016\u0010I\u001a\u00020*2\f\u0010J\u001a\b\u0012\u0004\u0012\u00020<0,H\u0002J\u000e\u0010K\u001a\u00020*2\u0006\u0010L\u001a\u00020FR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00180\u0017X\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00180\u001a¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u001eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020!0 X\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\"\u001a\b\u0012\u0004\u0012\u00020!0#¢\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u0010\u0010&\u001a\u0004\u0018\u00010'X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006P",
   d2 = {
         "Lio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel;",
         "Landroidx/lifecycle/ViewModel;",
         "launchedFrom",
         "Lio/intercom/android/sdk/tickets/create/model/CreateTicketLaunchedFrom;",
         "ticketRepository",
         "Lio/intercom/android/sdk/tickets/create/data/TicketRepository;",
         "userIdentity",
         "Lio/intercom/android/sdk/identity/UserIdentity;",
         "config",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "dispatcher",
         "Lkotlinx/coroutines/CoroutineDispatcher;",
         "metricTracker",
         "Lio/intercom/android/sdk/metrics/MetricTracker;",
         "conversationId",
         "",
         "ticketTypeId",
         "",
         "applicationContext",
         "Landroid/content/Context;",
         "<init>",
         "(Lio/intercom/android/sdk/tickets/create/model/CreateTicketLaunchedFrom;Lio/intercom/android/sdk/tickets/create/data/TicketRepository;Lio/intercom/android/sdk/identity/UserIdentity;Lio/intercom/android/sdk/identity/AppConfig;Lkotlinx/coroutines/CoroutineDispatcher;Lio/intercom/android/sdk/metrics/MetricTracker;Ljava/lang/String;JLandroid/content/Context;)V",
         "_uiState",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$CreateTicketFormUiState;",
         "uiState",
         "Lkotlinx/coroutines/flow/StateFlow;",
         "getUiState",
         "()Lkotlinx/coroutines/flow/StateFlow;",
         "lastKnownContentState",
         "Lio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$CreateTicketFormUiState$Content;",
         "_effect",
         "Lkotlinx/coroutines/flow/MutableSharedFlow;",
         "Lio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$TicketSideEffect;",
         "effect",
         "Lkotlinx/coroutines/flow/SharedFlow;",
         "getEffect",
         "()Lkotlinx/coroutines/flow/SharedFlow;",
         "currentTicketType",
         "Lio/intercom/android/sdk/blocks/lib/models/TicketTypeV2;",
         "getLastKnownContent",
         "fetchTicketType",
         "",
         "attributes",
         "",
         "Lio/intercom/android/sdk/tickets/create/data/TicketAttributeRequest;",
         "createTicket",
         "compositionAwareScope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "onAnswerUpdated",
         "questionID",
         "isUnsupportedFileType",
         "",
         "data",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;",
         "supportedFileType",
         "",
         "isFileSizeExceeded",
         "canRetryFileLimitExceededError",
         "mediaItem",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$MediaItem;",
         "index",
         "",
         "maxSelection",
         "withState",
         "operation",
         "Lkotlin/Function1;",
         "getAttributeRequest",
         "onRetryFileClicked",
         "fileClickData",
         "Lio/intercom/android/sdk/survey/ui/questiontype/AnswerClickData;",
         "onDeleteFileClicked",
         "updateCtaState",
         "compressAndUploadFileAttachments",
         "mediaItems",
         "onAnswerClicked",
         "answerClickData",
         "Companion",
         "CreateTicketFormUiState",
         "TicketSideEffect",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nCreateTicketViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CreateTicketViewModel.kt\nio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,499:1\n774#2:500\n865#2,2:501\n1869#2,2:503\n230#2,2:505\n230#2,2:507\n230#2,2:509\n774#2:511\n865#2,2:512\n1761#2,3:514\n*S KotlinDebug\n*F\n+ 1 CreateTicketViewModel.kt\nio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel\n*L\n294#1:500\n294#1:501,2\n294#1:503,2\n310#1:505,2\n312#1:507,2\n322#1:509,2\n332#1:511\n332#1:512,2\n337#1:514,3\n*E\n"
)
public final class CreateTicketViewModel extends ViewModel {
   @NotNull
   public static final CreateTicketViewModel$Companion Companion = new CreateTicketViewModel$Companion(null);
   @NotNull
   private final CreateTicketLaunchedFrom launchedFrom;
   @NotNull
   private final TicketRepository ticketRepository;
   @NotNull
   private final UserIdentity userIdentity;
   @NotNull
   private final AppConfig config;
   @NotNull
   private final CoroutineDispatcher dispatcher;
   @NotNull
   private final MetricTracker metricTracker;
   @Nullable
   private final String conversationId;
   private final long ticketTypeId;
   @NotNull
   private final Context applicationContext;
   @NotNull
   private final MutableStateFlow _uiState;
   @NotNull
   private final StateFlow uiState;
   @Nullable
   private CreateTicketViewModel$CreateTicketFormUiState$Content lastKnownContentState;
   @NotNull
   private final MutableSharedFlow _effect;
   @NotNull
   private final SharedFlow effect;
   @Nullable
   private TicketTypeV2 currentTicketType;
   public static final int $stable;

   static { $stable = 8; }

   public CreateTicketViewModel(
      @NotNull CreateTicketLaunchedFrom var1,
      @NotNull TicketRepository var2,
      @NotNull UserIdentity var3,
      @NotNull AppConfig var4,
      @NotNull CoroutineDispatcher var5,
      @NotNull MetricTracker var6,
      @Nullable String var7,
      long var8,
      @NotNull Context var10
   ) {
      Intrinsics.checkNotNullParameter(var1, "launchedFrom");
      Intrinsics.checkNotNullParameter(var2, "ticketRepository");
      Intrinsics.checkNotNullParameter(var3, "userIdentity");
      Intrinsics.checkNotNullParameter(var4, "config");
      Intrinsics.checkNotNullParameter(var5, "dispatcher");
      Intrinsics.checkNotNullParameter(var6, "metricTracker");
      Intrinsics.checkNotNullParameter(var10, "applicationContext");
      super();
      this.launchedFrom = var1;
      this.ticketRepository = var2;
      this.userIdentity = var3;
      this.config = var4;
      this.dispatcher = var5;
      this.metricTracker = var6;
      this.conversationId = var7;
      this.ticketTypeId = var8;
      this.applicationContext = var10;
      this._uiState = StateFlowKt.MutableStateFlow(CreateTicketViewModel$CreateTicketFormUiState$Initial.INSTANCE);
      this.uiState = FlowKt.asStateFlow(this._uiState);
      this._effect = SharedFlowKt.MutableSharedFlow$default(0, 0, null, 7, null);
      this.effect = FlowKt.asSharedFlow(this._effect);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new CreateTicketViewModel$1(this, null), 2, null);
      MetricTracker var10000 = this.metricTracker;
      Long var10001 = this.ticketTypeId;
      String var10002 = this.conversationId;
      if (this.conversationId == null) {
         var10002 = "";
      }

      var10000.viewedCreateTicketForm(var10001, var10002, this.launchedFrom.getFrom());
      fetchTicketType$default(this, null, 1, null);
   }

   // $VF: synthetic method
   public CreateTicketViewModel(
      CreateTicketLaunchedFrom var1,
      TicketRepository var2,
      UserIdentity var3,
      AppConfig var4,
      CoroutineDispatcher var5,
      MetricTracker var6,
      String var7,
      long var8,
      Context var10,
      int var11,
      DefaultConstructorMarker var12
   ) {
      this(var1, (((var11 & 2) != 0) ? (TicketRepository) (new TicketRepository(null, null, null, null, null, 31, null)) : var2), (((var11 & 4) != 0) ? (UserIdentity) (Injector.get().getUserIdentity()) : var3), (((var11 & 8) != 0) ? (AppConfig) ((AppConfig)Injector.get().getAppConfigProvider().get()) : var4), (((var11 & 16) != 0) ? (CoroutineDispatcher) (Dispatchers.getIO()) : var5), (((var11 & 32) != 0) ? (MetricTracker) (Injector.get().getMetricTracker()) : var6), var7, var8, (((var11 & 256) != 0) ? (Context) (Injector.get().getApplication()) : var10));
   }

   @NotNull
   public final StateFlow getUiState() {
      return this.uiState;
   }

   @NotNull
   public final SharedFlow getEffect() {
      return this.effect;
   }

   private final CreateTicketViewModel$CreateTicketFormUiState getLastKnownContent() {
      return this._uiState.getValue() instanceof CreateTicketViewModel$CreateTicketFormUiState$Content
         ? (CreateTicketViewModel$CreateTicketFormUiState)this._uiState.getValue()
         : this.lastKnownContentState;
   }

   private final void fetchTicketType(List var1) {
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new CreateTicketViewModel$fetchTicketType$1(this, var1, null), 2, null
      );
   }

   // $VF: synthetic method
   static void fetchTicketType$default(CreateTicketViewModel var0, List var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = CollectionsKt.emptyList();
      }

      var0.fetchTicketType(var1);
   }

   public final void createTicket(@Nullable CoroutineScope var1) {
      MetricTracker var10000 = this.metricTracker;
      Long var10001 = this.ticketTypeId;
      String var10002 = this.conversationId;
      if (this.conversationId == null) {
         var10002 = "";
      }

      var10000.submittedCreateTicketForm(var10001, var10002, this.launchedFrom.getFrom());
      final io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel recoveredExpressionCapture_1558_0 = this;
      final kotlinx.coroutines.CoroutineScope recoveredExpressionCapture_1558_1 = var1;
      this.withState(((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1558_0) -> io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel.createTicket$lambda$0(recoveredExpressionCapture_1558_0, recoveredExpressionCapture_1558_1, (io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content) ((io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content)recoveredExpressionArgument_1558_0))));
   }

   // $VF: synthetic method
   public static void createTicket$default(CreateTicketViewModel var0, CoroutineScope var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = null;
      }

      var0.createTicket(var1);
   }

   public final void onAnswerUpdated(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "questionID");
      final java.lang.String recoveredExpressionCapture_1560_0 = var1;
      final io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel recoveredExpressionCapture_1560_1 = this;
      this.withState(((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1560_0) -> io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel.onAnswerUpdated$lambda$3(recoveredExpressionCapture_1560_0, recoveredExpressionCapture_1560_1, (io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content) ((io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content)recoveredExpressionArgument_1560_0))));
   }

   private final boolean isUnsupportedFileType(MediaData$Media var1, Set var2) {
      String var3 = MimeTypeMap.getSingleton().getExtensionFromMimeType(var1.getMimeType());
      return !CollectionsKt.contains(var2, var3);
   }

   private final boolean isFileSizeExceeded(MediaData$Media var1) {
      return var1.getSize() > this.config.getAttachmentSettings().getUploadSizeLimit();
   }

   private final boolean canRetryFileLimitExceededError(Answer$MediaAnswer$MediaItem var1, int var2, int var3) {
      if (var1.getUploadStatus() instanceof Answer$MediaAnswer$FileUploadStatus$Error) {
         Answer$MediaAnswer$FileUploadStatus var10000 = var1.getUploadStatus();
         Intrinsics.checkNotNull(
            var10000, "null cannot be cast to non-null type io.intercom.android.sdk.survey.ui.models.Answer.MediaAnswer.FileUploadStatus.Error"
         );
         if (((Answer$MediaAnswer$FileUploadStatus$Error)var10000).getError() instanceof Answer$MediaAnswer$FileUploadError$FileLimitExceeded && var2 < var3) {
            return true;
         }
      }

      return false;
   }

   private final void withState(Function1 var1) {
      if (this._uiState.getValue() instanceof CreateTicketViewModel$CreateTicketFormUiState$Content) {
         Object var10001 = this._uiState.getValue();
         Intrinsics.checkNotNull(
            var10001, "null cannot be cast to non-null type io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel.CreateTicketFormUiState.Content"
         );
         var1.invoke((CreateTicketViewModel$CreateTicketFormUiState$Content)var10001);
      }
   }

   private final List getAttributeRequest() {
      List var1 = new ArrayList();
      final java.util.List recoveredExpressionCapture_1559_0 = var1;
      this.withState(((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1559_0) -> io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel.getAttributeRequest$lambda$6(recoveredExpressionCapture_1559_0, (io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content) ((io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content)recoveredExpressionArgument_1559_0))));
      return var1;
   }

   public final void onRetryFileClicked(@NotNull AnswerClickData var1) {
      Intrinsics.checkNotNullParameter(var1, "fileClickData");
      final io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel recoveredExpressionCapture_1563_0 = this;
      final io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData recoveredExpressionCapture_1563_1 = var1;
      this.withState(((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1563_0) -> io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel.onRetryFileClicked$lambda$9(recoveredExpressionCapture_1563_0, recoveredExpressionCapture_1563_1, (io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content) ((io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content)recoveredExpressionArgument_1563_0))));
   }

   public final void onDeleteFileClicked(@NotNull AnswerClickData var1) {
      Intrinsics.checkNotNullParameter(var1, "fileClickData");
      final io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData recoveredExpressionCapture_1562_0 = var1;
      final io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel recoveredExpressionCapture_1562_1 = this;
      this.withState(((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1562_0) -> io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel.onDeleteFileClicked$lambda$11(recoveredExpressionCapture_1562_0, recoveredExpressionCapture_1562_1, (io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content) ((io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content)recoveredExpressionArgument_1562_0))));
   }

   private final void updateCtaState() {
      final io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel recoveredExpressionCapture_1564_0 = this;
      this.withState(((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1564_0) -> io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel.updateCtaState$lambda$14(recoveredExpressionCapture_1564_0, (io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content) ((io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content)recoveredExpressionArgument_1564_0))));
   }

   private final void compressAndUploadFileAttachments(List var1) {
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new CreateTicketViewModel$compressAndUploadFileAttachments$1(var1, this, null), 2, null
      );
   }

   public final void onAnswerClicked(@NotNull AnswerClickData var1) {
      Intrinsics.checkNotNullParameter(var1, "answerClickData");
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), null, null, new CreateTicketViewModel$onAnswerClicked$1(this, var1, null), 3, null);
   }

   private static final Unit createTicket$lambda$0(CreateTicketViewModel var0, CoroutineScope var1, CreateTicketViewModel$CreateTicketFormUiState$Content var2) {
      Intrinsics.checkNotNullParameter(var2, "content");
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(var0), var0.dispatcher, null, new CreateTicketViewModel$createTicket$1$1(var2, var0, var1, null), 2, null
      );
      return Unit.INSTANCE;
   }

   private static final Unit onAnswerUpdated$lambda$3$lambda$2$lambda$1(CreateTicketViewModel var0, CreateTicketViewModel$CreateTicketFormUiState$Content var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0._uiState.setValue(CreateTicketViewModel$CreateTicketFormUiState$Content.copy$default(var1, null, null, true, false, 11, null));
      return Unit.INSTANCE;
   }

   private static final Unit onAnswerUpdated$lambda$3(String var0, CreateTicketViewModel var1, CreateTicketViewModel$CreateTicketFormUiState$Content var2) {
      Intrinsics.checkNotNullParameter(var2, "content");
      List var3 = ConditionalAttributesReducerKt.reduceAttributeList(var0, var1.currentTicketType, var2);
      int var4 = 0;
      if (!var3.isEmpty()) {
         final io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel recoveredExpressionCapture_1561_0 = var1;
         var1.withState(((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1561_0) -> io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel.onAnswerUpdated$lambda$3$lambda$2$lambda$1(recoveredExpressionCapture_1561_0, (io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content) ((io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content)recoveredExpressionArgument_1561_0))));
         var1.fetchTicketType(var3);
      }

      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(var1), var1.dispatcher, null, new CreateTicketViewModel$onAnswerUpdated$1$2(var2, var1, null), 2, null
      );
      return Unit.INSTANCE;
   }

   private static final Unit getAttributeRequest$lambda$6(List var0, CreateTicketViewModel$CreateTicketFormUiState$Content var1) {
      Intrinsics.checkNotNullParameter(var1, "content");
      Iterable var2 = var1.getQuestions();
      int var3 = 0;
      Iterable var4 = var2;
      ArrayList var5 = new ArrayList();
      int var6 = 0;

      for (Object var8 : var4) {
         QuestionState var9 = (QuestionState)var8;
         int var10 = 0;
         if (!Intrinsics.areEqual(var9.getQuestionModel().getId(), "-1")) {
            var5.add(var8);
         }
      }

      var2 = (Iterable & List)var5;
      var3 = 0;

      for (Object var14 : var2) {
         QuestionState var15 = (QuestionState)var14;
         int var16 = 0;
         Object var17 = CreateTicketViewModelKt.access$formatAnswerForServer(var15);
         if (!Intrinsics.areEqual(var17, Unit.INSTANCE)) {
            var0.add(new TicketAttributeRequest(var15.getQuestionModel().getId(), var17));
         }
      }

      return Unit.INSTANCE;
   }

   private static final Unit onRetryFileClicked$lambda$9(
      CreateTicketViewModel var0, AnswerClickData var1, CreateTicketViewModel$CreateTicketFormUiState$Content var2
   ) {
      Intrinsics.checkNotNullParameter(var2, "content");
      Iterable var4 = var2.getQuestions();
      int var5 = 0;

      for (Object var7 : var4) {
         QuestionState var8 = (QuestionState)var7;
         int var9 = 0;
         if (Intrinsics.areEqual(var8.getQuestionModel().getId(), var1.getQuestionId())) {
            QuestionState var3 = (QuestionState)var7;
            Answer var10000 = var3.getAnswer();
            Intrinsics.checkNotNull(var10000, "null cannot be cast to non-null type io.intercom.android.sdk.survey.ui.models.Answer.MediaAnswer");
            Answer$MediaAnswer var12 = (Answer$MediaAnswer)var10000;
            Iterable var14 = var12.getMediaItems();
            boolean var15 = false;

            for (Object element$ivx : var14) {
               Answer$MediaAnswer$MediaItem itx = (Answer$MediaAnswer$MediaItem)element$ivx;
               int var11 = 0;
               if (Intrinsics.areEqual(itx, var1.getClickedItem())) {
                  Answer$MediaAnswer$MediaItem var13 = (Answer$MediaAnswer$MediaItem)element$ivx;
                  var13.setUploadStatus(Answer$MediaAnswer$FileUploadStatus$None.INSTANCE);
                  var3.setAnswer(new Answer$MediaAnswer(var12.getMediaItems()));
                  var0.onAnswerUpdated(var1.getQuestionId());
                  return Unit.INSTANCE;
               }
            }

            throw new NoSuchElementException("Collection contains no element matching the predicate.");
         }
      }

      throw new NoSuchElementException("Collection contains no element matching the predicate.");
   }

   private static final Unit onDeleteFileClicked$lambda$11(
      AnswerClickData var0, CreateTicketViewModel var1, CreateTicketViewModel$CreateTicketFormUiState$Content var2
   ) {
      Intrinsics.checkNotNullParameter(var2, "content");
      Iterable var4 = var2.getQuestions();
      int var5 = 0;

      for (Object var7 : var4) {
         QuestionState var8 = (QuestionState)var7;
         int var9 = 0;
         if (Intrinsics.areEqual(var8.getQuestionModel().getId(), var0.getQuestionId())) {
            QuestionState var3 = (QuestionState)var7;
            Answer var10000 = var3.getAnswer();
            Intrinsics.checkNotNull(var10000, "null cannot be cast to non-null type io.intercom.android.sdk.survey.ui.models.Answer.MediaAnswer");
            Answer$MediaAnswer var10 = (Answer$MediaAnswer)var10000;
            var3.setAnswer(new Answer$MediaAnswer(CollectionsKt.minus(var10.getMediaItems(), var0.getClickedItem())));
            var1.onAnswerUpdated(var0.getQuestionId());
            return Unit.INSTANCE;
         }
      }

      throw new NoSuchElementException("Collection contains no element matching the predicate.");
   }

   private static final Unit updateCtaState$lambda$14(CreateTicketViewModel var0, CreateTicketViewModel$CreateTicketFormUiState$Content var1) {
      Intrinsics.checkNotNullParameter(var1, "content");
      Iterable var3 = var1.getQuestions();
      int var4 = 0;
      Iterable var5 = var3;
      ArrayList var6 = new ArrayList();
      int var7 = 0;

      for (Object var9 : var5) {
         QuestionState var10 = (QuestionState)var9;
         int var11 = 0;
         if (var10.getQuestionModel() instanceof SurveyData$Step$Question$UploadFileQuestionModel) {
            var6.add(var9);
         }
      }

      for (java.lang.Object recoveredEntryElement_0_513 : (List)var6) {
         QuestionState var13 = (QuestionState) recoveredEntryElement_0_513;
         var13.validate();
         if (var13.getAnswer() instanceof Answer$MediaAnswer) {
            Answer var10000 = var13.getAnswer();
            Intrinsics.checkNotNull(var10000, "null cannot be cast to non-null type io.intercom.android.sdk.survey.ui.models.Answer.MediaAnswer");
            Answer$MediaAnswer var14 = (Answer$MediaAnswer)var10000;
            var var15 = var14.getMediaItems();
            var7 = 0;
            boolean var21;
            if (var15 instanceof Collection && ((Collection)var15).isEmpty()) {
               var21 = false;
            } else {
               label59: {
                  for (Object var18 : var15) {
                     Answer$MediaAnswer$MediaItem var19 = (Answer$MediaAnswer$MediaItem)var18;
                     int var20 = 0;
                     if (!(var19.getUploadStatus() instanceof Answer$MediaAnswer$FileUploadStatus$Success)) {
                        var21 = true;
                        break label59;
                     }
                  }

                  var21 = false;
               }
            }

            if (var21) {
               var0._uiState.setValue(CreateTicketViewModel$CreateTicketFormUiState$Content.copy$default(var1, null, null, false, false, 7, null));
               return Unit.INSTANCE;
            }
         }
      }

      var0._uiState.setValue(CreateTicketViewModel$CreateTicketFormUiState$Content.copy$default(var1, null, null, false, true, 7, null));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final MutableStateFlow access$get_uiState$p(CreateTicketViewModel var0) {
      return var0._uiState;
   }

   // $VF: synthetic method
   public static final TicketRepository access$getTicketRepository$p(CreateTicketViewModel var0) {
      return var0.ticketRepository;
   }

   // $VF: synthetic method
   public static final long access$getTicketTypeId$p(CreateTicketViewModel var0) {
      return var0.ticketTypeId;
   }

   // $VF: synthetic method
   public static final void access$setCurrentTicketType$p(CreateTicketViewModel var0, TicketTypeV2 var1) {
      var0.currentTicketType = var1;
   }

   // $VF: synthetic method
   public static final UserIdentity access$getUserIdentity$p(CreateTicketViewModel var0) {
      return var0.userIdentity;
   }

   // $VF: synthetic method
   public static final AppConfig access$getConfig$p(CreateTicketViewModel var0) {
      return var0.config;
   }

   // $VF: synthetic method
   public static final CreateTicketViewModel$CreateTicketFormUiState access$getLastKnownContent(CreateTicketViewModel var0) {
      return var0.getLastKnownContent();
   }

   // $VF: synthetic method
   public static final void access$fetchTicketType(CreateTicketViewModel var0, List var1) {
      var0.fetchTicketType(var1);
   }

   // $VF: synthetic method
   public static final CoroutineDispatcher access$getDispatcher$p(CreateTicketViewModel var0) {
      return var0.dispatcher;
   }

   // $VF: synthetic method
   public static final Context access$getApplicationContext$p(CreateTicketViewModel var0) {
      return var0.applicationContext;
   }

   // $VF: synthetic method
   public static final boolean access$isFileSizeExceeded(CreateTicketViewModel var0, MediaData$Media var1) {
      return var0.isFileSizeExceeded(var1);
   }

   // $VF: synthetic method
   public static final void access$updateCtaState(CreateTicketViewModel var0) {
      var0.updateCtaState();
   }

   // $VF: synthetic method
   public static final MutableSharedFlow access$get_effect$p(CreateTicketViewModel var0) {
      return var0._effect;
   }

   // $VF: synthetic method
   public static final void access$withState(CreateTicketViewModel var0, Function1 var1) {
      var0.withState(var1);
   }

   // $VF: synthetic method
   public static final String access$getConversationId$p(CreateTicketViewModel var0) {
      return var0.conversationId;
   }

   // $VF: synthetic method
   public static final List access$getAttributeRequest(CreateTicketViewModel var0) {
      return var0.getAttributeRequest();
   }

   // $VF: synthetic method
   public static final boolean access$isUnsupportedFileType(CreateTicketViewModel var0, MediaData$Media var1, Set var2) {
      return var0.isUnsupportedFileType(var1, var2);
   }

   // $VF: synthetic method
   public static final boolean access$canRetryFileLimitExceededError(CreateTicketViewModel var0, Answer$MediaAnswer$MediaItem var1, int var2, int var3) {
      return var0.canRetryFileLimitExceededError(var1, var2, var3);
   }

   // $VF: synthetic method
   public static final void access$compressAndUploadFileAttachments(CreateTicketViewModel var0, List var1) {
      var0.compressAndUploadFileAttachments(var1);
   }

   // $VF: synthetic method
   public static final void access$setLastKnownContentState$p(CreateTicketViewModel var0, CreateTicketViewModel$CreateTicketFormUiState$Content var1) {
      var0.lastKnownContentState = var1;
   }
}
