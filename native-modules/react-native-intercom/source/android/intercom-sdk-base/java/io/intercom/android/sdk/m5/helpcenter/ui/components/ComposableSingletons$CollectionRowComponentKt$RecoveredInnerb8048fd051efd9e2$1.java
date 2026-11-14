package io.intercom.android.sdk.m5.helpcenter.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionRowData;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nCollectionRowComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CollectionRowComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/ComposableSingletons$CollectionRowComponentKt$lambda-3$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,107:1\n1047#2,6:108\n*S KotlinDebug\n*F\n+ 1 CollectionRowComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/ComposableSingletons$CollectionRowComponentKt$lambda-3$1\n*L\n102#1:108,6\n*E\n"
)
final class ComposableSingletons$CollectionRowComponentKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$CollectionRowComponentKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$CollectionRowComponentKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C101@3740L2,92@3359L398:CollectionRowComponent.kt#ngglre");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2013190203,
               var2,
               -1,
               "io.intercom.android.sdk.m5.helpcenter.ui.components.ComposableSingletons$CollectionRowComponentKt.lambda-3.<anonymous> (CollectionRowComponent.kt:92)"
            );
         }

         CollectionRowData var10000 = new CollectionRowData("", "Lorem Ipsum Dolor Sit", 0, "", 3, 2);
         var1.startReplaceGroup(410278631);
         ComposerKt.sourceInformation(var1, "CC(remember):CollectionRowComponent.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         kotlin.jvm.functions.Function1 var12;
         if (var7 == Composer.Companion.getEmpty()) {
            CollectionRowData var10 = var10000;
            int var9 = 0;
            var12 = ((kotlin.jvm.functions.Function1) (recoveredArgument_874_0) -> ComposableSingletons$CollectionRowComponentKt$RecoveredInnerb8048fd051efd9e2$1.invoke$lambda$1$lambda$0((java.lang.String)recoveredArgument_874_0));
            var10000 = var10;
            Object var11 = var12;
            var4.updateRememberedValue(var11);
            var12 = (Function1)var11;
         } else {
            var12 = (Function1)var7;
         }

         Function1 var3 = var12;
         var1.endReplaceGroup();
         CollectionRowComponentKt.CollectionRowComponent(var10000, var3, null, var1, 48, 4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
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
