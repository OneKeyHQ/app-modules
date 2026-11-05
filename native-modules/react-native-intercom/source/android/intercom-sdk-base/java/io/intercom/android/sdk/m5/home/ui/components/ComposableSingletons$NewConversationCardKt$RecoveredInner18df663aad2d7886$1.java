package io.intercom.android.sdk.m5.home.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.home.data.Action;
import io.intercom.android.sdk.m5.home.data.HomeCardType;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeNewConversationData;
import io.intercom.android.sdk.m5.home.data.IconType;
import io.intercom.android.sdk.models.Avatar;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nNewConversationCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NewConversationCard.kt\nio/intercom/android/sdk/m5/home/ui/components/ComposableSingletons$NewConversationCardKt$lambda-4$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,248:1\n1047#2,6:249\n*S KotlinDebug\n*F\n+ 1 NewConversationCard.kt\nio/intercom/android/sdk/m5/home/ui/components/ComposableSingletons$NewConversationCardKt$lambda-4$1\n*L\n245#1:249,6\n*E\n"
)
final class ComposableSingletons$NewConversationCardKt$RecoveredInner18df663aad2d7886$1 implements Function2 {
   public static final ComposableSingletons$NewConversationCardKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$NewConversationCardKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C244@9173L2,222@8214L961:NewConversationCard.kt#utrtal");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               633163645,
               var2,
               -1,
               "io.intercom.android.sdk.m5.home.ui.components.ComposableSingletons$NewConversationCardKt.lambda-4.<anonymous> (NewConversationCard.kt:222)"
            );
         }

         Action var3 = new Action("Ask a question", IconType.FIN, null);
         HomeCardType var4 = HomeCardType.NEW_CONVERSATION;
         List var5 = CollectionsKt.emptyList();
         HomeCards$HomeNewConversationData var10000 = new HomeCards$HomeNewConversationData("Get in touch", var4, var3, false, var5, true, true, null);
         AvatarWrapper[] var14 = new AvatarWrapper[4];
         Avatar var10005 = Avatar.create("", "VR");
         Intrinsics.checkNotNullExpressionValue(var10005, "create(...)");
         var14[0] = new AvatarWrapper(var10005, false, 2, null);
         var10005 = Avatar.create("", "PR");
         Intrinsics.checkNotNullExpressionValue(var10005, "create(...)");
         var14[1] = new AvatarWrapper(var10005, false, 2, null);
         var10005 = Avatar.create("", "SK");
         Intrinsics.checkNotNullExpressionValue(var10005, "create(...)");
         var14[2] = new AvatarWrapper(var10005, false, 2, null);
         var10005 = Avatar.create("", "LD");
         Intrinsics.checkNotNullExpressionValue(var10005, "create(...)");
         var14[3] = new AvatarWrapper(var10005, false, 2, null);
         List var10001 = CollectionsKt.listOf(var14);
         Avatar var10004 = Avatar.create("", "B");
         Intrinsics.checkNotNullExpressionValue(var10004, "create(...)");
         AvatarWrapper var10002 = new AvatarWrapper(var10004, false, 2, null);
         var1.startReplaceGroup(1732386197);
         ComposerKt.sourceInformation(var1, "CC(remember):NewConversationCard.kt#9igjgp");
         Composer var16 = var1;
         boolean var17 = false;
         int var6 = 0;
         Object var7 = var16.rememberedValue();
         int var8 = 0;
         Object var10003;
         if (var7 == Composer.Companion.getEmpty()) {
            AvatarWrapper var12 = var10002;
            List var11 = var10001;
            HomeCards$HomeNewConversationData var10 = var10000;
            int var9 = 0;
            Function0 var13 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$NewConversationCardKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            var10001 = var11;
            var10002 = var12;
            Object var18 = var13;
            var16.updateRememberedValue(var18);
            var10003 = var18;
         } else {
            var10003 = var7;
         }

         Function0 var15 = (Function0)var10003;
         var1.endReplaceGroup();
         NewConversationCardKt.NewConversationCard(var10000, var10001, var10002, var15, var1, 3072, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
