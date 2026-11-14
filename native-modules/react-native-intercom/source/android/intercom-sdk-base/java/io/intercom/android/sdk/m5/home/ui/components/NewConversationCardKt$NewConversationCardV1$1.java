package io.intercom.android.sdk.m5.home.ui.components;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.components.BotAndHumansFacePileKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarIconKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeNewConversationData;
import io.intercom.android.sdk.m5.home.data.IconType;
import io.intercom.android.sdk.models.Avatar;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nNewConversationCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NewConversationCard.kt\nio/intercom/android/sdk/m5/home/ui/components/NewConversationCardKt$NewConversationCardV1$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,248:1\n118#2:249\n118#2:254\n1563#3:250\n1634#3,3:251\n*S KotlinDebug\n*F\n+ 1 NewConversationCard.kt\nio/intercom/android/sdk/m5/home/ui/components/NewConversationCardKt$NewConversationCardV1$1\n*L\n74#1:249\n84#1:254\n80#1:250\n80#1:251,3\n*E\n"
)
final class NewConversationCardKt$NewConversationCardV1$1 implements Function2 {
   // $VF: synthetic field
   final HomeCards$HomeNewConversationData $newConversationData;
   // $VF: synthetic field
   final AvatarWrapper $botAvatar;
   // $VF: synthetic field
   final List $adminAvatars;

   NewConversationCardKt$NewConversationCardV1$1(HomeCards$HomeNewConversationData var1, AvatarWrapper var2, List var3) {
      this.$newConversationData = var1;
      this.$botAvatar = var2;
      this.$adminAvatars = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C:NewConversationCard.kt#utrtal");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1023934521, var2, -1, "io.intercom.android.sdk.m5.home.ui.components.NewConversationCardV1.<anonymous> (NewConversationCard.kt:70)"
            );
         }

         IconType var10000 = this.$newConversationData.getAction().getIcon();
         switch (var10000 == null ? -1 : NewConversationCardKt$NewConversationCardV1$1$WhenMappings.$EnumSwitchMapping$0[var10000.ordinal()]) {
            case 1:
               var1.startReplaceGroup(1718791131);
               ComposerKt.sourceInformation(var1, "73@3012L70");
               if (this.$botAvatar != null) {
                  Modifier var18 = Modifier.Companion;
                  int var15 = 32;
                  int var17 = 0;
                  AvatarIconKt.recovered$f5ec3c81e3146789(
                     SizeKt.recovered$111ef94c93e78c04(var18, Dp.recovered$95b3d103663bf060(var15)), this.$botAvatar, null, false, 0L, null, var1, 6, 60
                  );
               }

               var1.endReplaceGroup();
               break;
            case 2:
               var1.startReplaceGroup(1719018020);
               ComposerKt.sourceInformation(var1, "80@3307L243");
               if (this.$botAvatar != null) {
                  Iterable var4 = this.$adminAvatars;
                  int var5 = 0;
                  Iterable var6 = var4;
                  ArrayList var7 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var4, 10));
                  int var8 = 0;

                  for (Object var10 : var6) {
                     AvatarWrapper var11 = (AvatarWrapper)var10;
                     Collection var13 = var7;
                     int var12 = 0;
                     var13.add(var11.getAvatar());
                  }

                  List var3 = (List)var7;
                  Avatar var10001 = this.$botAvatar.getAvatar();
                  Pair var10002 = BotAndHumansFacePileKt.humanAvatarPairForHome(var3);
                  int var14 = 36;
                  var5 = 0;
                  BotAndHumansFacePileKt.recovered$2d180ffbba3167ff(null, var10001, var10002, Dp.recovered$95b3d103663bf060(var14), null, var1, 3072, 17);
               }

               var1.endReplaceGroup();
               break;
            default:
               var1.startReplaceGroup(-83081034);
               var1.endReplaceGroup();
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
