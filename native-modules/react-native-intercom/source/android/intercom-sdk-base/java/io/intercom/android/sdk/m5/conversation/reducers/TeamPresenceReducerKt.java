package io.intercom.android.sdk.m5.conversation.reducers;

import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.states.ExpandedTeamPresenceState;
import io.intercom.android.sdk.m5.conversation.states.TeamPresenceState;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.AvatarDetails;
import io.intercom.android.sdk.models.AvatarType;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.Header;
import io.intercom.android.sdk.models.OpenMessengerResponse;
import io.intercom.android.sdk.models.Avatar$Builder;
import io.intercom.android.sdk.models.Header$Expanded;
import io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0014\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u001c\u0010\u0000\u001a\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u0000¨\u0006\u0006",
   d2 = {
         "reduceTeamPresenceState",
         "Lio/intercom/android/sdk/m5/conversation/states/TeamPresenceState;",
         "conversation",
         "Lio/intercom/android/sdk/models/Conversation;",
         "openMessengerResponse",
         "Lio/intercom/android/sdk/models/OpenMessengerResponse;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTeamPresenceReducer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TeamPresenceReducer.kt\nio/intercom/android/sdk/m5/conversation/reducers/TeamPresenceReducerKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,39:1\n1563#2:40\n1634#2,3:41\n*S KotlinDebug\n*F\n+ 1 TeamPresenceReducer.kt\nio/intercom/android/sdk/m5/conversation/reducers/TeamPresenceReducerKt\n*L\n24#1:40\n24#1:41,3\n*E\n"
)
public final class TeamPresenceReducerKt {
   @NotNull
   public static final TeamPresenceState reduceTeamPresenceState(@Nullable Conversation var0, @Nullable OpenMessengerResponse var1) {
      io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData var2;
      io.intercom.android.sdk.models.Header$Expanded var31;
      label105: {
         var2 = var1 != null ? var1.getNewConversationData() : null;
         if (var0 != null) {
            Header var10000 = var0.getHeader();
            if (var10000 != null) {
               var31 = var10000.getExpanded();
               if (var31 != null) {
                  break label105;
               }
            }
         }

         if (var2 != null) {
            Header var32 = var2.getHeader();
            if (var32 != null) {
               var31 = var32.getExpanded();
               break label105;
            }
         }

         var31 = null;
      }

      io.intercom.android.sdk.models.Header$Expanded var3 = var31;
      ExpandedTeamPresenceState var35;
      if (var3 != null) {
         Header var33;
         label89: {
            if (var0 != null) {
               var33 = var0.getHeader();
               if (var33 != null) {
                  break label89;
               }
            }

            var33 = var2 != null ? var2.getHeader() : null;
         }

         Header var4;
         io.intercom.android.sdk.models.Header$Expanded var5;
         String var34;
         AvatarType var37;
         List var10001;
         label84: {
            var4 = var33;
            var5 = var3;
            int var6 = 0;
            var34 = var5.getTitle();
            var10001 = var5.getBody();
            AvatarDetails var10002 = var5.getAvatarDetails();
            if (var10002 != null) {
               var37 = var10002.getAvatarType();
               if (var37 != null) {
                  break label84;
               }
            }

            var37 = AvatarType.UNKNOWN;
         }

         List var41;
         label79: {
            AvatarDetails var10003 = var5.getAvatarDetails();
            if (var10003 != null) {
               List var40 = var10003.getAvatars();
               if (var40 != null) {
                  Iterable var7 = var40;
                  AvatarType var8 = var37;
                  List var9 = var10001;
                  String var10 = var34;
                  int var11 = 0;
                  Iterable var12 = var7;
                  ArrayList var13 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var7, 10));
                  int var14 = 0;

                  for (Object var16 : var12) {
                     io.intercom.android.sdk.models.Avatar$Builder var17 = (io.intercom.android.sdk.models.Avatar$Builder)var16;
                     Collection var18 = var13;
                     int var19 = 0;
                     Avatar var38 = var17.build();
                     Intrinsics.checkNotNullExpressionValue(var38, "build(...)");
                     var18.add(new AvatarWrapper(var38, false));
                  }

                  List var30 = (List)var13;
                  var34 = var10;
                  var10001 = var9;
                  var37 = var8;
                  var41 = var30;
                  break label79;
               }
            }

            var41 = CollectionsKt.emptyList();
         }

         List var10004 = var5.getFooter();
         List var10005 = var5.getSocialAccounts();
         boolean var20 = var4 != null ? var4.getDisplayActiveIndicator() : false;
         List var21 = var10005;
         List var22 = var10004;
         List var23 = var41;
         AvatarType var24 = var37;
         List var25 = var10001;
         String var26 = var34;
         var35 = new ExpandedTeamPresenceState(var26, var25, var24, var23, var22, var21, var20);
      } else {
         var35 = ExpandedTeamPresenceState.Companion.getDefault();
      }

      String var36;
      label108: {
         if (var0 != null) {
            var36 = var0.getTeamIntro();
            if (var36 != null) {
               break label108;
            }
         }

         var36 = var2 != null ? var2.getTeamIntro() : null;
         if (var36 == null) {
            var36 = "";
         }
      }

      String var39;
      label109: {
         if (var0 != null) {
            var39 = var0.getSpecialNotice();
            if (var39 != null) {
               break label109;
            }
         }

         var39 = var2 != null ? var2.getSpecialNotice() : null;
         if (var39 == null) {
            var39 = "";
         }
      }

      String var27 = var39;
      String var28 = var36;
      ExpandedTeamPresenceState var29 = var35;
      return new TeamPresenceState(var29, var28, var27);
   }
}
