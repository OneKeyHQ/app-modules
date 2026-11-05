package io.intercom.android.sdk.m5.conversation.data;

import android.os.Build.VERSION;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.intercom.android.nexus.EventData;
import io.intercom.android.nexus.NexusClient;
import io.intercom.android.nexus.NexusEvent;
import io.intercom.android.nexus.NexusEventType;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.m5.components.avatar.AvatarShape;
import io.intercom.android.sdk.m5.components.avatar.AvatarShape$Companion;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Avatar$Builder;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000*\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\u001a\u0016\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0000\u001a\f\u0010\u0005\u001a\u00020\u0002*\u00020\u0006H\u0002\u001a\u001c\u0010\u0007\u001a\u00020\b*\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\bH\u0002¨\u0006\r",
   d2 = {
         "nexusEventAsFlow",
         "Lkotlinx/coroutines/flow/Flow;",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent;",
         "nexusClient",
         "Lio/intercom/android/nexus/NexusClient;",
         "asNexusData",
         "Lio/intercom/android/nexus/NexusEvent;",
         "safeGetOrDefault",
         "",
         "Lio/intercom/android/nexus/EventData;",
         "key",
         "",
         "default",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nNexusEventAsFlow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NexusEventAsFlow.kt\nio/intercom/android/sdk/m5/conversation/data/NexusEventAsFlowKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,184:1\n1563#2:185\n1634#2,3:186\n*S KotlinDebug\n*F\n+ 1 NexusEventAsFlow.kt\nio/intercom/android/sdk/m5/conversation/data/NexusEventAsFlowKt\n*L\n149#1:185\n149#1:186,3\n*E\n"
)
public final class NexusEventAsFlowKt {
   @NotNull
   public static final Flow nexusEventAsFlow(@NotNull NexusClient var0) {
      Intrinsics.checkNotNullParameter(var0, "nexusClient");
      return FlowKt.callbackFlow(new NexusEventAsFlowKt$nexusEventAsFlow$1(var0, null));
   }

   private static final ParsedNexusEvent asNexusData(NexusEvent var0) {
      NexusEventType var10000 = var0.getEventType();
      ParsedNexusEvent var25;
      switch (var10000 == null ? -1 : NexusEventAsFlowKt$WhenMappings.$EnumSwitchMapping$0[var10000.ordinal()]) {
         case 1:
            String var32 = var0.getEventData().optString("conversationId");
            Intrinsics.checkNotNullExpressionValue(var32, "optString(...)");
            String var35 = var0.getEventData().optString("createdByUserId");
            Intrinsics.checkNotNullExpressionValue(var35, "optString(...)");
            Avatar$Builder var37 = new Avatar$Builder().withImageUrl(var0.getEventData().optString("adminAvatar"));
            AvatarShape$Companion var40 = AvatarShape.Companion;
            String var10006 = var0.getEventData().optString("avatarShape");
            Intrinsics.checkNotNullExpressionValue(var10006, "optString(...)");
            Avatar var17 = var37.withShape(var40.fromName(var10006)).build();
            Intrinsics.checkNotNullExpressionValue(var17, "build(...)");
            EventData var41 = var0.getEventData();
            Intrinsics.checkNotNullExpressionValue(var41, "getEventData(...)");
            Object var42 = safeGetOrDefault(var41, "isBot", false);
            Intrinsics.checkNotNull(var42, "null cannot be cast to non-null type kotlin.Boolean");
            boolean var43 = (Boolean)var42;
            EventData var44 = var0.getEventData();
            Intrinsics.checkNotNullExpressionValue(var44, "getEventData(...)");
            Object var45 = safeGetOrDefault(var44, "showAvatar", true);
            Intrinsics.checkNotNull(var45, "null cannot be cast to non-null type kotlin.Boolean");
            boolean var46 = (Boolean)var45;
            String var10007 = var0.getEventData().optString("clientId");
            Intrinsics.checkNotNullExpressionValue(var10007, "optString(...)");
            var25 = new ParsedNexusEvent$ConversationNexusEvent$AdminIsTyping(var32, var35, var17, var43, var46, var10007);
            break;
         case 2:
            String var31 = var0.getEventData().optString("conversationId");
            Intrinsics.checkNotNullExpressionValue(var31, "optString(...)");
            String var34 = var0.getEventData().optString("createdByUserId");
            Intrinsics.checkNotNullExpressionValue(var34, "optString(...)");
            Avatar var36 = new Avatar$Builder().withImageUrl(var0.getEventData().optString("adminAvatar")).build();
            Intrinsics.checkNotNullExpressionValue(var36, "build(...)");
            EventData var38 = var0.getEventData();
            Intrinsics.checkNotNullExpressionValue(var38, "getEventData(...)");
            Object var39 = safeGetOrDefault(var38, "isBot", false);
            Intrinsics.checkNotNull(var39, "null cannot be cast to non-null type kotlin.Boolean");
            var25 = new ParsedNexusEvent$ConversationNexusEvent$UserContentSeenByAdmin(var31, var34, var36, (Boolean)var39);
            break;
         case 3:
            String var30 = var0.getEventData().optString("conversationId");
            Intrinsics.checkNotNullExpressionValue(var30, "optString(...)");
            String var33 = var0.getEventData().optString("createdByUserId");
            Intrinsics.checkNotNullExpressionValue(var33, "optString(...)");
            var25 = new ParsedNexusEvent$ConversationNexusEvent$NewComment(var30, var33, var0.getEventData().optString("ticketId"), null, 8, null);
            break;
         case 4:
            Object var26 = var0.getEventData().get("blocks");
            Intrinsics.checkNotNull(var26, "null cannot be cast to non-null type org.json.JSONArray");
            JSONArray var1 = (JSONArray)var26;

            List var3;
            try {
               String var28 = var1.toString();
               Intrinsics.checkNotNullExpressionValue(var28, "toString(...)");
               String var18 = var28;
               Type var29 = new NexusEventAsFlowKt$asNexusData$blocks$type$1().getType();
               Intrinsics.checkNotNullExpressionValue(var29, "getType(...)");
               Type var4 = var29;
               ArrayList var19 = (ArrayList)new Gson().fromJson(var18, var4);
               Intrinsics.checkNotNull(var19);
               Iterable var20 = var19;
               int var21 = 0;
               Iterable var22 = var20;
               ArrayList var23 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var20, 10));
               int var24 = 0;

               for (Object var12 : var22) {
                  Block$Builder var13 = (Block$Builder)var12;
                  Collection var15 = var23;
                  int var14 = 0;
                  var15.add(var13.build());
               }

               var3 = (List)var23;
            } catch (JsonSyntaxException var16) {
               List var5 = CollectionsKt.createListBuilder();
               List var6 = var5;
               int var7 = 0;
               int var8 = 0;

               for (int var9 = var1.length(); var8 < var9; var8++) {
                  var26 = var1.get(var8);
                  Intrinsics.checkNotNull(var26, "null cannot be cast to non-null type org.json.JSONObject");
                  JSONObject var10 = (JSONObject)var26;
                  Block var10001 = new Block$Builder().withType(var10.optString("type")).withText(var10.optString("text")).build();
                  Intrinsics.checkNotNullExpressionValue(var10001, "build(...)");
                  var6.add(var10001);
               }

               var3 = CollectionsKt.build(var5);
            }

            List var2 = var3;
            String var10002 = var0.getEventData().optString("conversationId");
            Intrinsics.checkNotNullExpressionValue(var10002, "optString(...)");
            String var10003 = var0.getEventData().optString("clientAssignedUuid");
            Intrinsics.checkNotNullExpressionValue(var10003, "optString(...)");
            String var10004 = var0.getEventData().optString("partType");
            Intrinsics.checkNotNullExpressionValue(var10004, "optString(...)");
            Object var10005 = var0.getEventData().get("tokenSequenceIndex");
            Intrinsics.checkNotNull(var10005, "null cannot be cast to non-null type kotlin.Int");
            var25 = new ParsedNexusEvent$ConversationNexusEvent$FinStreaming(var10002, var10003, var10004, (Integer)var10005, var2);
            break;
         default:
            var25 = ParsedNexusEvent$UnSupportedEvent.INSTANCE;
      }

      return var25;
   }

   private static final Object safeGetOrDefault(EventData var0, String var1, Object var2) {
      Object var10000;
      if (VERSION.SDK_INT >= 24) {
         Object var3 = var0.getOrDefault(var1, var2);
         Intrinsics.checkNotNull(var3);
         var10000 = var3;
      } else {
         var10000 = var0.get(var1);
         if (var10000 == null) {
            var10000 = var2;
         }
      }

      return var10000;
   }

   // $VF: synthetic method
   public static final ParsedNexusEvent access$asNexusData(NexusEvent var0) {
      return asNexusData(var0);
   }
}
