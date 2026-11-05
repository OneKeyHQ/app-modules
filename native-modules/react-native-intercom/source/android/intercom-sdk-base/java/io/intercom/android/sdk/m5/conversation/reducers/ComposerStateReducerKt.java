package io.intercom.android.sdk.m5.conversation.reducers;

import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState;
import io.intercom.android.sdk.m5.conversation.states.ComposerState;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.InputTypeState;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBadgeState;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBadgeState$None;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBadgeState$PoweredByBadgeState;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBadgeState$PrivacyNoticeBadgeState;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton$GifInsert;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton$MediaInsert;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton$VoiceNotesInsert;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$ConversationEnded;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$Hidden;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$Reactions;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$ConversationEnded$ConversationEndedCta;
import io.intercom.android.sdk.models.ComposerSuggestions;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.ConversationEndedButton;
import io.intercom.android.sdk.models.MessageStyle;
import io.intercom.android.sdk.models.OpenMessengerResponse;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.PoweredBy;
import io.intercom.android.sdk.models.PrivacyNotice;
import io.intercom.android.sdk.models.ReactionReply;
import io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData;
import io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData$Cta;
import io.intercom.android.sdk.ui.common.StringProvider;
import io.intercom.android.sdk.ui.common.StringProvider.ActualString;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import io.intercom.android.sdk.utilities.extensions.AppConfigExtensionsKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000B\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a \u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0000\u001a\u0016\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\u0006\u0010\u0004\u001a\u00020\u0005H\u0002\u001a\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0002\u001a\u00020\u0003H\u0002\u001aF\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\u0010\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0012\u001a\u00020\u00072\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\n0\t2\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\tH\u0002\u001a\u001c\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00150\t2\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\tH\u0002¨\u0006\u0017",
   d2 = {
         "reduceComposerState",
         "Lio/intercom/android/sdk/m5/conversation/states/BottomBarUiState;",
         "clientState",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "config",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "isNewConversation",
         "",
         "reduceTextInputButtons",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/BottomBarUiState$BottomBarButton;",
         "reduceBottomBadge",
         "Lio/intercom/android/sdk/m5/conversation/states/BottomBarUiState$BottomBadgeState;",
         "reduceTextInput",
         "Lio/intercom/android/sdk/m5/conversation/states/ComposerState;",
         "currentState",
         "placeholder",
         "",
         "isDisabled",
         "buttons",
         "mediaUploadItems",
         "Lio/intercom/android/sdk/m5/conversation/states/MediaUploadItem;",
         "sortMediaItemsWithFailedFirst",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nComposerStateReducer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ComposerStateReducer.kt\nio/intercom/android/sdk/m5/conversation/reducers/ComposerStateReducerKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,204:1\n1761#2,3:205\n3307#2,10:209\n1#3:208\n*S KotlinDebug\n*F\n+ 1 ComposerStateReducer.kt\nio/intercom/android/sdk/m5/conversation/reducers/ComposerStateReducerKt\n*L\n103#1:205,3\n201#1:209,10\n*E\n"
)
public final class ComposerStateReducerKt {
   @NotNull
   public static final BottomBarUiState reduceComposerState(@NotNull ConversationClientState var0, @NotNull AppConfig var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var0, "clientState");
      Intrinsics.checkNotNullParameter(var1, "config");
      Conversation var3 = var0.getConversation();
      ComposerState var22;
      if (var3 == null) {
         boolean var21;
         label133: {
            OpenMessengerResponse var10000 = var0.getOpenMessengerResponse();
            if (var10000 != null) {
               ComposerSuggestions var20 = var10000.getComposerSuggestions();
               if (var20 != null) {
                  var21 = var20.isComposerDisabled();
                  break label133;
               }
            }

            var21 = false;
         }

         if (var21) {
            var22 = new io.intercom.android.sdk.m5.conversation.states.ComposerState$Hidden(false, 1, null);
         } else {
            ComposerState var23;
            String var32;
            label127: {
               var23 = var0.getComposerState();
               OpenMessengerResponse var10002 = var0.getOpenMessengerResponse();
               if (var10002 != null) {
                  io.intercom.android.sdk.models.ComposerState var31 = var10002.getComposerState();
                  if (var31 != null) {
                     var32 = var31.getPlaceholder();
                     break label127;
                  }
               }

               var32 = null;
            }

            var22 = reduceTextInput(var23, var2, var32, false, reduceTextInputButtons(var1), var0.getMediaUploadItems());
         }
      } else {
         boolean var5;
         io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData$Cta var26;
         label117: {
            var5 = AppConfigExtensionsKt.canStartNewConversation(var1) && !var3.getInboundConversationsDisabled();
            OpenMessengerResponse var24 = var0.getOpenMessengerResponse();
            if (var24 != null) {
               io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData var25 = var24.getNewConversationData();
               if (var25 != null) {
                  var26 = var25.getCta();
                  break label117;
               }
            }

            var26 = null;
         }

         io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData$Cta var6 = var26;
         io.intercom.android.sdk.m5.conversation.states.ComposerState$ConversationEnded$ConversationEndedCta var27;
         if (var5 && var6 != null) {
            StringProvider var33 = new ActualString(var6.getText());
            Integer var10003 = var6.getIcon().getIcon();
            var27 = new io.intercom.android.sdk.m5.conversation.states.ComposerState$ConversationEnded$ConversationEndedCta(var33, var10003 != null ? var10003 : drawable.intercom_send_message_icon, null, 4, null);
         } else {
            var27 = null;
         }

         io.intercom.android.sdk.m5.conversation.states.ComposerState$ConversationEnded$ConversationEndedCta var7 = var27;
         boolean var8 = !ReactionReply.isNull(var3.lastPart().getReactionReply());
         if (var3.getPreventEndUserReplies()) {
            io.intercom.android.sdk.m5.conversation.states.ComposerState$ConversationEnded var29;
            label105: {
               io.intercom.android.sdk.m5.conversation.states.ComposerState$ConversationEnded var9 = new io.intercom.android.sdk.m5.conversation.states.ComposerState$ConversationEnded(new StringRes(string.intercom_conversation_has_ended, null, 2, null), var7);
               ConversationEndedButton var28 = var3.getConversationEndedButton();
               if (var28 != null) {
                  ConversationEndedButton var13 = var28;
                  int var14 = 0;
                  if (ComposerStateReducerKt$WhenMappings.$EnumSwitchMapping$0[var13.getReason().ordinal()] == 1) {
                     io.intercom.android.sdk.m5.conversation.states.ComposerState$ConversationEnded$ConversationEndedCta var15 = var13.getMetadata() == null
                        ? null
                        : new io.intercom.android.sdk.m5.conversation.states.ComposerState$ConversationEnded$ConversationEndedCta(
                           new ActualString(var13.getCtaTitle()), drawable.intercom_send_message_icon, var13.getMetadata().getMergedIntoConversationId()
                        );
                     var29 = var9.copy(new ActualString(var13.getMessage()), var15);
                  } else {
                     var29 = io.intercom.android.sdk.m5.conversation.states.ComposerState$ConversationEnded.copy$default(var9, new ActualString(var13.getMessage()), null, 2, null);
                  }

                  if (var29 != null) {
                     break label105;
                  }
               }

               var29 = var9;
            }

            var22 = var29;
         } else if (var3.getComposerState().isVisible() && !var8) {
            var22 = reduceTextInput(
               var0.getComposerState(),
               var2,
               var3.getComposerState().getPlaceholder(),
               var3.getComposerState().isDisabled(),
               reduceTextInputButtons(var1),
               var0.getMediaUploadItems()
            );
         } else if (var8) {
            ReactionReply var34 = var3.lastPart().getReactionReply();
            Intrinsics.checkNotNullExpressionValue(var34, "getReactionReply(...)");
            String var35 = var3.lastPart().getId();
            Intrinsics.checkNotNullExpressionValue(var35, "getId(...)");
            var22 = new io.intercom.android.sdk.m5.conversation.states.ComposerState$Reactions(var34, var35, var3.getId());
         } else {
            var var17 = var3.parts();
            int var10 = 0;
            boolean var30;
            if (var17 instanceof Collection && ((Collection)var17).isEmpty()) {
               var30 = false;
            } else {
               label148: {
                  for (Object var12 : var17) {
                     Part var18 = (Part)var12;
                     int var19 = 0;
                     if (var18.getMessageStyle() == MessageStyle.ATTRIBUTE_COLLECTOR) {
                        var30 = true;
                        break label148;
                     }
                  }

                  var30 = false;
               }
            }

            boolean var16 = !var30;
            var22 = new io.intercom.android.sdk.m5.conversation.states.ComposerState$Hidden(var16);
         }
      }

      ComposerState var4 = var22;
      return new BottomBarUiState(
         var4,
         var0.getCurrentlyTypingState(),
         new InputTypeState(
            var1.getAttachmentSettings().getMediaEnabled(),
            var1.getAttachmentSettings().getGifsEnabled(),
            var1.getAttachmentSettings().getCameraEnabled(),
            var1.getAttachmentSettings().getFilesEnabled(),
            var1.getAttachmentSettings().getTrustedFileExtensions(),
            var4 instanceof io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput ? RangesKt.coerceAtLeast(10 - ((io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput)var4).getMediaUploadItems().size(), 0) : 10
         ),
         reduceBottomBadge(var0)
      );
   }

   private static final List reduceTextInputButtons(AppConfig var0) {
      List var1 = CollectionsKt.createListBuilder();
      List var2 = var1;
      int var3 = 0;
      if (var0.getAttachmentSettings().getMediaEnabled() || var0.getAttachmentSettings().getCameraEnabled() || var0.getAttachmentSettings().getFilesEnabled()) {
         var2.add(io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton$MediaInsert.INSTANCE);
      }

      if (var0.getAttachmentSettings().getGifsEnabled()) {
         var2.add(io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton$GifInsert.INSTANCE);
      }

      if (var0.getAttachmentSettings().getVoiceNotesEnabled()) {
         var2.add(io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton$VoiceNotesInsert.INSTANCE);
      }

      return CollectionsKt.build(var1);
   }

   private static final io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBadgeState reduceBottomBadge(ConversationClientState var0) {
      PoweredBy var3;
      if (var0.getConversation() == null) {
         OpenMessengerResponse var10000 = var0.getOpenMessengerResponse();
         var3 = var10000 != null ? var10000.getPoweredBy() : null;
      } else {
         var3 = var0.getConversation().getPoweredBy();
      }

      PoweredBy var1 = var3;
      OpenMessengerResponse var4 = var0.getOpenMessengerResponse();
      PrivacyNotice var2 = var4 != null ? var4.getPrivacyNotice() : null;
      if (var2 != null && !var2.isDismissed() && !var0.hasUserSentAMessage$intercom_sdk_base_release() && !var0.getDismissedPrivacyNotice()) {
         return new io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBadgeState$PrivacyNoticeBadgeState(var2.getText());
      } else {
         return var1 != null ? new io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBadgeState$PoweredByBadgeState(var1) : io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBadgeState$None.INSTANCE;
      }
   }

   private static final ComposerState reduceTextInput(ComposerState var0, boolean var1, String var2, boolean var3, List var4, List var5) {
      ComposerState var12;
      if (var0 instanceof io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput) {
         List var6 = sortMediaItemsWithFailedFirst(var5);
         io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput var7 = (io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput)var0;
         StringProvider var10000;
         if (var2 != null) {
            String var10 = var2;
            int var11 = 0;
            var10000 = new ActualString(var10);
         } else {
            var10000 = new StringRes(string.intercom_message_placeholder, null, 2, null);
         }

         StringProvider var8 = var10000;
         String var9 = var1 ? ((io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput)var0).getInitialMessage() : "";
         var12 = io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput.copy$default(var7, var9, var8, var3, var4, var6, null, 32, null);
      } else {
         var12 = var0;
      }

      return var12;
   }

   private static final List sortMediaItemsWithFailedFirst(List var0) {
      Iterable var2 = var0;
      int var3 = 0;
      ArrayList var4 = new ArrayList();
      ArrayList var5 = new ArrayList();

      for (Object var7 : var2) {
         MediaUploadItem var8 = (MediaUploadItem)var7;
         int var9 = 0;
         if (var8.isError()) {
            var4.add(var7);
         } else {
            var5.add(var7);
         }
      }

      Pair var1 = new Pair(var4, var5);
      List var10 = (List)var1.component1();
      List var11 = (List)var1.component2();
      return CollectionsKt.plus(var10, var11);
   }
}
