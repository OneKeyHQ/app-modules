package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.blocks.lib.models.BlockAttachment;
import io.intercom.android.sdk.blocks.lib.models.BlockAttachment$Companion;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository;
import io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadError;
import io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadError$ClientError;
import io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadError$NetworkError;
import io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadError$ServerError;
import io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadError$SizeLimitExceeded;
import io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadError$UnexpectedError;
import io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadResult;
import io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadResult$Failure;
import io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadResult$Success;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiEffect$ShowUploadSizeLimitDialog;
import io.intercom.android.sdk.m5.conversation.states.PendingMessage;
import io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Other;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.Part$Builder;
import io.intercom.android.sdk.models.Part$MessageState;
import io.intercom.android.sdk.models.Participant$Builder;
import io.intercom.android.sdk.utilities.commons.TimeProvider;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.MutableSharedFlow;
import kotlinx.coroutines.flow.MutableStateFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ<\u0010\f\u001a\u00020\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u00122\u0006\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u0017H\u0086B¢\u0006\u0002\u0010\u0018R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/SendMediaUseCase;",
         "",
         "sendMessageUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/SendMessageUseCase;",
         "mediaUploadRepository",
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository;",
         "userIdentity",
         "Lio/intercom/android/sdk/identity/UserIdentity;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/usecase/SendMessageUseCase;Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository;Lio/intercom/android/sdk/identity/UserIdentity;)V",
         "getSendMessageUseCase",
         "()Lio/intercom/android/sdk/m5/conversation/usecase/SendMessageUseCase;",
         "invoke",
         "",
         "clientState",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "uiEffect",
         "Lkotlinx/coroutines/flow/MutableSharedFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationUiEffect;",
         "mediaData",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;",
         "uuid",
         "",
         "(Lkotlinx/coroutines/flow/MutableStateFlow;Lkotlinx/coroutines/flow/MutableSharedFlow;Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nSendMediaUseCase.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SendMediaUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/SendMediaUseCase\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,153:1\n230#2,5:154\n230#2,3:159\n233#2,2:169\n230#2,5:171\n563#3:162\n548#3,6:163\n*S KotlinDebug\n*F\n+ 1 SendMediaUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/SendMediaUseCase\n*L\n77#1:154,5\n111#1:159,3\n111#1:169,2\n138#1:171,5\n113#1:162\n113#1:163,6\n*E\n"
)
public final class SendMediaUseCase {
   @NotNull
   private final SendMessageUseCase sendMessageUseCase;
   @NotNull
   private final MediaUploadRepository mediaUploadRepository;
   @NotNull
   private final UserIdentity userIdentity;
   public static final int $stable;

   static { $stable = 8; }

   public SendMediaUseCase(@NotNull SendMessageUseCase var1, @NotNull MediaUploadRepository var2, @NotNull UserIdentity var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "sendMessageUseCase");
      Intrinsics.checkNotNullParameter(var2, "mediaUploadRepository");
      Intrinsics.checkNotNullParameter(var3, "userIdentity");
      this.sendMessageUseCase = var1;
      this.mediaUploadRepository = var2;
      this.userIdentity = var3;
   }

   // $VF: synthetic method
   public SendMediaUseCase(SendMessageUseCase var1, MediaUploadRepository var2, UserIdentity var3, int var4, DefaultConstructorMarker var5) {
      this(var1, var2, (((var4 & 4) != 0) ? (UserIdentity) (Injector.get().getUserIdentity()) : var3));
   }

   @NotNull
   public final SendMessageUseCase getSendMessageUseCase() {
      return this.sendMessageUseCase;
   }

   @Nullable
   public final Object invoke(
      @NotNull MutableStateFlow var1, @NotNull MutableSharedFlow var2, @NotNull MediaData$Media var3, @NotNull String var4, @NotNull Continuation var5
   ) {
      SendMediaUseCase recoveredThis = this;
      SendMediaUseCase$invoke$1 var27;
      label105: {
         if (var5 instanceof SendMediaUseCase$invoke$1) {
            var27 = (SendMediaUseCase$invoke$1)var5;
            if ((var27.label & -2147483648) != 0) {
               var27.label -= Integer.MIN_VALUE;
               break label105;
            }
         }

         var27 = new SendMediaUseCase$invoke$1(recoveredThis, var5);
      }

      Object var26 = var27.result;
      Object var28 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (var27.label) {
         case 0:
            ResultKt.throwOnFailure(var26);
            MediaData$Media var7 = var3;
            Block$Builder var58;
            if (var7 instanceof MediaData$Media$Image) {
               var58 = new Block$Builder()
                  .withLocalUri(((MediaData$Media$Image)var3).getUri())
                  .withWidth(((MediaData$Media$Image)var3).getWidth())
                  .withHeight(((MediaData$Media$Image)var3).getHeight())
                  .withType("IMAGE");
            } else {
               if (!(var7 instanceof MediaData$Media$Video) && !(var7 instanceof MediaData$Media$Other)) {
                  throw new NoWhenBranchMatchedException();
               }

               BlockAttachment$Companion var59 = BlockAttachment.Companion;
               String var10001 = var3.getFileName();
               String var10002 = var3.getUri().toString();
               Intrinsics.checkNotNullExpressionValue(var10002, "toString(...)");
               BlockAttachment var8 = BlockAttachment$Companion.create$default(var59, var10001, var10002, var3.getMimeType(), null, 0L, 0L, 56, null);
               var58 = new Block$Builder().withAttachments(CollectionsKt.listOf(var8)).withType("LOCAL_ATTACHMENT");
            }

            Block$Builder var6 = var58;
            Part var30 = new Part$Builder()
               .withBlocks(CollectionsKt.listOf(var6))
               .withCreatedAt(TimeUnit.MILLISECONDS.toSeconds(TimeProvider.SYSTEM.currentTimeMillis()))
               .withParticipantIsAdmin(false)
               .withClientAssignedUuid(var4)
               .build();
            Part var9 = var30;
            int var10 = 0;
            var9.setParticipant(new Participant$Builder().withId(recoveredThis.userIdentity.getIntercomId()).build());
            var9.setMessageState(Part$MessageState.SENDING);
            Part var29 = var30;
            MutableStateFlow var31 = var1;
            int var33 = 0;

            Object var17;
            Object var35;
            do {
               var35 = var31.getValue();
               ConversationClientState var11 = (ConversationClientState)var35;
               int var12 = 0;
               LinkedHashMap var13 = new LinkedHashMap();
               LinkedHashMap var14 = var13;
               ConversationClientState var15 = var11;
               int var16 = 0;
               var14.putAll(var11.getPendingMessages());
               Intrinsics.checkNotNull(var29);
               var14.put(var4, new PendingMessage(var29, false, false, null, 4, null));
               var17 = ConversationClientState.copy$default(
                  var15,
                  var13,
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
            } while (!var31.compareAndSet(var35, var17));

            MediaUploadRepository var60 = recoveredThis.mediaUploadRepository;
            var27.L$0 = recoveredThis;
            var27.L$1 = var1;
            var27.L$2 = var2;
            var27.L$3 = var3;
            var27.L$4 = var4;
            var27.label = 1;
            var10000 = MediaUploadRepository.uploadSingleMediaItem$default(var60, var3, 0, var27, 2, null);
            if (var10000 == var28) {
               return var28;
            }
            break;
         case 1:
            var4 = (String)var27.L$4;
            var3 = (MediaData$Media)var27.L$3;
            var2 = (MutableSharedFlow)var27.L$2;
            var1 = (MutableStateFlow)var27.L$1;
            recoveredThis = (SendMediaUseCase)var27.L$0;
            ResultKt.throwOnFailure(var26);
            var10000 = var26;
            break;
         case 2:
            ResultKt.throwOnFailure(var26);
            return Unit.INSTANCE;
         case 3:
            ResultKt.throwOnFailure(var26);
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      MediaUploadRepository$UploadResult var32 = (MediaUploadRepository$UploadResult)var10000;
      MediaUploadRepository$UploadResult var34 = var32;
      if (var34 instanceof MediaUploadRepository$UploadResult$Success) {
         SendMessageUseCase var61 = recoveredThis.sendMessageUseCase;
         List var63 = CollectionsKt.listOf(((MediaUploadRepository$UploadResult$Success)var32).getBlock());
         var27.L$0 = null;
         var27.L$1 = null;
         var27.L$2 = null;
         var27.L$3 = null;
         var27.L$4 = null;
         var27.label = 2;
         return var61.invoke(var1, var63, var4, var27) == var28 ? var28 : Unit.INSTANCE;
      }

      if (!(var34 instanceof MediaUploadRepository$UploadResult$Failure)) {
         throw new NoWhenBranchMatchedException();
      }

      MediaUploadRepository$UploadError var36 = ((MediaUploadRepository$UploadResult$Failure)var32).getError();
      if (var36 instanceof MediaUploadRepository$UploadError$SizeLimitExceeded) {
         MutableStateFlow var37 = var1;
         int var39 = 0;

         Object var41;
         ConversationClientState var44;
         do {
            var41 = var37.getValue();
            var44 = (ConversationClientState)var41;
            int var46 = 0;
            Map var48 = ((ConversationClientState)var44).getPendingMessages();
            ConversationClientState var50 = (ConversationClientState)var44;
            int var18 = 0;
            Map var19 = var48;
            Map var20 = new LinkedHashMap();
            int var21 = 0;

            for (java.lang.Object recoveredEntryElement_0_291 : var19.entrySet()) {
               Entry var23 = (Entry) recoveredEntryElement_0_291;
               Entry var24 = var23;
               int var25 = 0;
               if (!Intrinsics.areEqual(var4, var24.getKey())) {
                  var20.put(var23.getKey(), var23.getValue());
               }
            }

            var44 = ConversationClientState.copy$default(
               var50,
               var20,
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
         } while (!var37.compareAndSet(var41, var44));

         ConversationUiEffect$ShowUploadSizeLimitDialog var62 = new ConversationUiEffect$ShowUploadSizeLimitDialog(
            ((MediaUploadRepository$UploadError$SizeLimitExceeded)((MediaUploadRepository$UploadResult$Failure)var32).getError()).getLimitMB()
         );
         var27.L$0 = null;
         var27.L$1 = null;
         var27.L$2 = null;
         var27.L$3 = null;
         var27.L$4 = null;
         var27.label = 3;
         return var2.emit(var62, var27) == var28 ? var28 : Unit.INSTANCE;
      } else {
         if (!(var36 instanceof MediaUploadRepository$UploadError$ClientError)
            && !(var36 instanceof MediaUploadRepository$UploadError$NetworkError)
            && !(var36 instanceof MediaUploadRepository$UploadError$ServerError)
            && !(var36 instanceof MediaUploadRepository$UploadError$UnexpectedError)) {
            throw new NoWhenBranchMatchedException();
         }

         Map var38 = ((ConversationClientState)var1.getValue()).getPendingMessages();
         PendingMessage var40 = (PendingMessage)var38.get(var4);
         if (var40 != null) {
            PendingMessage var42 = PendingMessage.copy$default(var40, null, false, false, new PendingMessage$FailedMediaUploadData(var4, var3), 7, null);
            MutableStateFlow var45 = var1;
            int var47 = 0;

            Object var49;
            Object var57;
            do {
               var49 = var45.getValue();
               ConversationClientState var51 = (ConversationClientState)var49;
               int var52 = 0;
               LinkedHashMap var53 = new LinkedHashMap();
               LinkedHashMap var54 = var53;
               ConversationClientState var55 = var51;
               int var56 = 0;
               var54.putAll(var51.getPendingMessages());
               var54.put(var4, var42);
               var57 = ConversationClientState.copy$default(
                  var55,
                  var53,
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
            } while (!var45.compareAndSet(var49, var57));
         }

         return Unit.INSTANCE;
      }
   }

   // $VF: synthetic method
   public static Object invoke$default(
      SendMediaUseCase var0, MutableStateFlow var1, MutableSharedFlow var2, MediaData$Media var3, String var4, Continuation var5, int var6, Object var7
   ) {
      if ((var6 & 8) != 0) {
         var4 = UUID.randomUUID().toString();
      }

      return var0.invoke(var1, var2, var3, var4, var5);
   }
}
