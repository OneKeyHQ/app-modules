package io.intercom.android.sdk.m5.conversation.data;

import io.intercom.android.nexus.NexusClient;
import io.intercom.android.sdk.AblyManager;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.identity.AppConfig;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.flow.SharedFlow;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0018\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u001e\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0000¨\u0006\u0007",
   d2 = {
         "combinedEventAsFlow",
         "Lkotlinx/coroutines/flow/Flow;",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent;",
         "nexusClient",
         "Lio/intercom/android/nexus/NexusClient;",
         "ablyManager",
         "Lio/intercom/android/sdk/AblyManager;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nCombinedEventAsFlow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CombinedEventAsFlow.kt\nio/intercom/android/sdk/m5/conversation/data/CombinedEventAsFlowKt\n+ 2 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n+ 3 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n+ 4 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n*L\n1#1,65:1\n17#2:66\n19#2:70\n49#2:71\n51#2:75\n17#2:76\n19#2:80\n46#3:67\n51#3:69\n46#3:72\n51#3:74\n46#3:77\n51#3:79\n105#4:68\n105#4:73\n105#4:78\n*S KotlinDebug\n*F\n+ 1 CombinedEventAsFlow.kt\nio/intercom/android/sdk/m5/conversation/data/CombinedEventAsFlowKt\n*L\n32#1:66\n32#1:70\n37#1:71\n37#1:75\n56#1:76\n56#1:80\n32#1:67\n32#1:69\n37#1:72\n37#1:74\n56#1:77\n56#1:79\n32#1:68\n37#1:73\n56#1:78\n*E\n"
)
public final class CombinedEventAsFlowKt {
   @NotNull
   public static final Flow combinedEventAsFlow(@NotNull NexusClient var0, @NotNull AblyManager var1) {
      Intrinsics.checkNotNullParameter(var0, "nexusClient");
      Intrinsics.checkNotNullParameter(var1, "ablyManager");
      Flow var2 = NexusEventAsFlowKt.nexusEventAsFlow(var0);
      boolean var3 = ((AppConfig)Injector.get().getAppConfigProvider().get()).hasFeature("ably-realtime-disabled");
      if (var3) {
         return var2;
      }

      SharedFlow var4 = var1.getSharedEventFlow();
      Flow var6 = var4;
      int var7 = 0;
      Flow var8 = var6;
      int var9 = 0;
      int var10 = 0;
      var6 = new CombinedEventAsFlowKt$combinedEventAsFlow$RecoveredInnere3b0c44298fc1c14$inlined$filter$1(var8);
      var7 = 0;
      var8 = var6;
      var9 = 0;
      var10 = 0;
      Flow var5 = new CombinedEventAsFlowKt$combinedEventAsFlow$RecoveredInnere3b0c44298fc1c14$inlined$map$1(var8);
      Flow var15 = var2;
      int $i$f$filterx = 0;
      Flow $this$unsafeTransform$iv$ivx = var15;
      var10 = 0;
      int $i$f$unsafeFlowx = 0;
      var6 = new CombinedEventAsFlowKt$combinedEventAsFlow$RecoveredInnere3b0c44298fc1c14$inlined$filter$2($this$unsafeTransform$iv$ivx, var1);
      Flow[] var16 = new Flow[]{var5, var6};
      return FlowKt.merge(var16);
   }
}
