package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.helpcenter.articles.ArticleMetadata;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.m5.conversation.data.ConversationRepository;
import io.intercom.android.sdk.m5.conversation.states.BottomSheetState;
import io.intercom.android.sdk.m5.conversation.states.ComposerState;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.CurrentlyTypingState;
import io.intercom.android.sdk.m5.conversation.states.LaunchMode;
import io.intercom.android.sdk.m5.conversation.states.NetworkState;
import io.intercom.android.sdk.models.Attribute;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.ConversationKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.MutableStateFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J,\u0010\u0006\u001a\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0086B¢\u0006\u0002\u0010\u000fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/SubmitAttributeUseCase;",
         "",
         "conversationRepository",
         "Lio/intercom/android/sdk/m5/conversation/data/ConversationRepository;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/data/ConversationRepository;)V",
         "invoke",
         "",
         "clientStateFlow",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "attribute",
         "Lio/intercom/android/sdk/models/Attribute;",
         "partId",
         "",
         "(Lkotlinx/coroutines/flow/MutableStateFlow;Lio/intercom/android/sdk/models/Attribute;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nSubmitAttributeUseCase.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SubmitAttributeUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/SubmitAttributeUseCase\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,66:1\n230#2,3:67\n233#2,2:73\n230#2,3:75\n233#2,2:84\n230#2,3:86\n233#2,2:92\n827#3:70\n855#3,2:71\n827#3:78\n855#3,2:79\n827#3:81\n855#3,2:82\n827#3:89\n855#3,2:90\n*S KotlinDebug\n*F\n+ 1 SubmitAttributeUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/SubmitAttributeUseCase\n*L\n20#1:67,3\n20#1:73,2\n37#1:75,3\n37#1:84,2\n54#1:86,3\n54#1:92,2\n23#1:70\n23#1:71,2\n39#1:78\n39#1:79,2\n42#1:81\n42#1:82,2\n57#1:89\n57#1:90,2\n*E\n"
)
public final class SubmitAttributeUseCase {
   @NotNull
   private final ConversationRepository conversationRepository;
   public static final int $stable;

   static { $stable = 8; }

   public SubmitAttributeUseCase(@NotNull ConversationRepository var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "conversationRepository");
      this.conversationRepository = var1;
   }

   @Nullable
   public final Object invoke(@NotNull MutableStateFlow var1, @NotNull Attribute var2, @NotNull String var3, @NotNull Continuation var4) {
      SubmitAttributeUseCase$invoke$1 var36;
      label106: {
         if (var4 instanceof SubmitAttributeUseCase$invoke$1) {
            var36 = (SubmitAttributeUseCase$invoke$1)var4;
            if ((var36.label & -2147483648) != 0) {
               var36.label -= Integer.MIN_VALUE;
               break label106;
            }
         }

         var36 = new SubmitAttributeUseCase$invoke$1(this, var4);
      }

      Object var35 = var36.result;
      Object var37 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (var36.label) {
         case 0:
            ResultKt.throwOnFailure(var35);
            Conversation var83 = ((ConversationClientState)var1.getValue()).getConversation();
            if (var83 == null) {
               return Unit.INSTANCE;
            }

            Conversation var5 = var83;
            MutableStateFlow var6 = var1;
            int var7 = 0;

            Object var8;
            ConversationClientState var42;
            do {
               var8 = var6.getValue();
               var42 = (ConversationClientState)var8;
               int var10 = 0;
               List var11 = CollectionsKt.plus(((ConversationClientState)var42).getLoadingAttributeIdentifiers(), var2.getIdentifier());
               Iterable var12 = ((ConversationClientState)var42).getFailedAttributeIdentifiers();
               int var13 = 0;
               Iterable var14 = var12;
               ArrayList var15 = new ArrayList();
               int var16 = 0;

               for (Object var18 : var14) {
                  String var19 = (String)var18;
                  int var20 = 0;
                  if (!Intrinsics.areEqual(var19, var2.getIdentifier())) {
                     var15.add(var18);
                  }
               }

               List var21 = (List)var15;
               var42 = ConversationClientState.copy$default(
                  (ConversationClientState)var42,
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
                  var21,
                  var11,
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
                  8385535,
                  null
               );
            } while (!var6.compareAndSet(var8, var42));

            ConversationRepository var84 = this.conversationRepository;
            String var10001 = var5.getId();
            String var10003 = var2.getIdentifier();
            String var10004 = var2.getValue();
            if (var10004 == null) {
               var10004 = "";
            }

            String var10005 = var2.getType();
            var36.L$0 = var1;
            var36.L$1 = var2;
            var36.label = 1;
            var10000 = var84.submitForm(var10001, var3, var10003, var10004, var10005, var36);
            if (var10000 == var37) {
               return var37;
            }
            break;
         case 1:
            var2 = (Attribute)var36.L$1;
            var1 = (MutableStateFlow)var36.L$0;
            ResultKt.throwOnFailure(var35);
            var10000 = var35;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      NetworkResponse var38 = (NetworkResponse)var10000;
      NetworkResponse var39 = var38;
      if (var39 instanceof NetworkResponse$Success) {
         MutableStateFlow var40 = var1;
         int var43 = 0;

         Object var24;
         Object var45;
         do {
            var45 = var40.getValue();
            ConversationClientState var47 = (ConversationClientState)var45;
            int var50 = 0;
            Iterable var52 = var47.getFailedAttributeIdentifiers();
            int var55 = 0;
            Iterable var59 = var52;
            ArrayList var62 = new ArrayList();
            int var65 = 0;

            for (Object var71 : var59) {
               String var74 = (String)var71;
               int var77 = 0;
               if (!Intrinsics.areEqual(var74, var2.getIdentifier())) {
                  var62.add(var71);
               }
            }

            List var22 = (List)var62;
            Iterable $this$filterNot$ivx = var47.getLoadingAttributeIdentifiers();
            int $i$f$filterNotx = 0;
            Iterable var63 = $this$filterNot$ivx;
            var destination$iv$ivx = new ArrayList();
            int $i$f$filterNotTox = 0;

            for (Object var75 : var63) {
               String var78 = (String)var75;
               int var23 = 0;
               if (!Intrinsics.areEqual(var78, var2.getIdentifier())) {
                  destination$iv$ivx.add(var75);
               }
            }

            List recoveredLoadingIdentifiers = (List)destination$iv$ivx;
            Conversation var57 = ConversationKt.withOrderedComposerState((Conversation)((NetworkResponse$Success)var38).getBody(), var47.getConversation());
            var24 = ConversationClientState.copy$default(
               var47,
               null,
               var57,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               var22,
               recoveredLoadingIdentifiers,
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
               8385533,
               null
            );
         } while (!var40.compareAndSet(var45, var24));
      } else {
         if (!(var39 instanceof NetworkResponse$ClientError)
            && !(var39 instanceof NetworkResponse$NetworkError)
            && !(var39 instanceof NetworkResponse$ServerError)) {
            throw new NoWhenBranchMatchedException();
         }

         MutableStateFlow var41 = var1;
         int var44 = 0;

         Object var46;
         ConversationClientState var49;
         do {
            var46 = var41.getValue();
            var49 = (ConversationClientState)var46;
            int var51 = 0;
            List var10011 = CollectionsKt.plus(((ConversationClientState)var49).getFailedAttributeIdentifiers(), var2.getIdentifier());
            Iterable var54 = ((ConversationClientState)var49).getLoadingAttributeIdentifiers();
            List var58 = var10011;
            Object var61 = null;
            Object var64 = null;
            Object var67 = null;
            Object var70 = null;
            Object var73 = null;
            Object var76 = null;
            Object var79 = null;
            Object var80 = null;
            Object var81 = null;
            Object var82 = null;
            ConversationClientState var25 = (ConversationClientState)var49;
            int var26 = 0;
            Iterable var27 = var54;
            ArrayList var28 = new ArrayList();
            int var29 = 0;

            for (Object var31 : var27) {
               String var32 = (String)var31;
               int var33 = 0;
               if (!Intrinsics.areEqual(var32, var2.getIdentifier())) {
                  var28.add(var31);
               }
            }

            List var34 = (List)var28;
            var49 = ConversationClientState.copy$default(
               var25,
               (Map)var82,
               (Conversation)var81,
               (String)var80,
               (CurrentlyTypingState)var79,
               (ComposerState)var76,
               (BottomSheetState)var73,
               (LaunchMode)var70,
               (NetworkResponse)var67,
               (ArticleMetadata)var64,
               (NetworkState)var61,
               var58,
               var34,
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
               8385535,
               null
            );
         } while (!var41.compareAndSet(var46, var49));
      }

      return Unit.INSTANCE;
   }
}
