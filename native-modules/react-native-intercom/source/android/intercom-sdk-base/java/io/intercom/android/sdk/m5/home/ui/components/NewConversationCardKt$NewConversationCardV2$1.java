package io.intercom.android.sdk.m5.home.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.components.BotAndHumansFacePileKt;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Avatar$Builder;
import io.intercom.android.sdk.models.AvatarDetails;
import io.intercom.android.sdk.models.AvatarType;
import io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData$HomeCard;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nNewConversationCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NewConversationCard.kt\nio/intercom/android/sdk/m5/home/ui/components/NewConversationCardKt$NewConversationCardV2$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,248:1\n1563#2:249\n1634#2,3:250\n118#3:253\n*S KotlinDebug\n*F\n+ 1 NewConversationCard.kt\nio/intercom/android/sdk/m5/home/ui/components/NewConversationCardKt$NewConversationCardV2$1\n*L\n116#1:249\n116#1:250,3\n118#1:253\n*E\n"
)
final class NewConversationCardKt$NewConversationCardV2$1 implements Function2 {
   // $VF: synthetic field
   final OpenMessengerResponse$NewConversationData$HomeCard $homeCard;

   NewConversationCardKt$NewConversationCardV2$1(OpenMessengerResponse$NewConversationData$HomeCard var1) {
      this.$homeCard = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C112@4319L352:NewConversationCard.kt#utrtal");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               94824693, var2, -1, "io.intercom.android.sdk.m5.home.ui.components.NewConversationCardV2.<anonymous> (NewConversationCard.kt:110)"
            );
         }

         AvatarDetails var10000 = this.$homeCard.getAvatarDetails();
         if ((var10000 != null ? var10000.getAvatarType() : null) == AvatarType.FACEPILE) {
            Avatar var10001 = ((Avatar$Builder)this.$homeCard.getAvatarDetails().getAvatars().get(0)).build();
            Intrinsics.checkNotNullExpressionValue(var10001, "build(...)");
            Iterable var3 = CollectionsKt.drop(this.$homeCard.getAvatarDetails().getAvatars(), 1);
            Avatar var13 = var10001;
            Object var12 = null;
            int var4 = 0;
            Iterable var5 = var3;
            ArrayList var6 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var3, 10));
            int var7 = 0;

            for (Object var9 : var5) {
               Avatar$Builder var10 = (Avatar$Builder)var9;
               Collection var14 = var6;
               int var11 = 0;
               var14.add(var10.build());
            }

            List var17 = (List)var6;
            Pair var10002 = BotAndHumansFacePileKt.humanAvatarPairForHome(var17);
            int var15 = 36;
            var4 = 0;
            BotAndHumansFacePileKt.recovered$2d180ffbba3167ff((Modifier)var12, var13, var10002, Dp.recovered$95b3d103663bf060(var15), null, var1, 3072, 17);
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
