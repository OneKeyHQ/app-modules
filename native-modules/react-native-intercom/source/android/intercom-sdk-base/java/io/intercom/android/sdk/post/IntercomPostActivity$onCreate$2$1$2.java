package io.intercom.android.sdk.post;

import android.content.Context;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.utilities.Phrase;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomPostActivity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomPostActivity.kt\nio/intercom/android/sdk/post/IntercomPostActivity$onCreate$2$1$2\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,370:1\n75#2:371\n1047#3,6:372\n*S KotlinDebug\n*F\n+ 1 IntercomPostActivity.kt\nio/intercom/android/sdk/post/IntercomPostActivity$onCreate$2$1$2\n*L\n140#1:371\n151#1:372,6\n*E\n"
)
final class IntercomPostActivity$onCreate$2$1$2 implements Function2 {
   // $VF: synthetic field
   final Part $part;
   // $VF: synthetic field
   final IntercomPostActivity this$0;

   IntercomPostActivity$onCreate$2$1$2(Part var1, IntercomPostActivity var2) {
      this.$part = var1;
      this.this$0 = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C139@5727L7,150@6310L72,145@6038L371:IntercomPostActivity.kt#hv1x2k");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2005201696,
               var2,
               -1,
               "io.intercom.android.sdk.post.IntercomPostActivity.onCreate.<anonymous>.<anonymous>.<anonymous> (IntercomPostActivity.kt:139)"
            );
         }

         CompositionLocal var4 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var5 = 0;
         int var6 = 0;
         ComposerKt.sourceInformationMarkerStart(var1, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var7 = var1.consume(var4);
         ComposerKt.sourceInformationMarkerEnd(var1);
         Context var3 = (Context)var7;
         CharSequence var19 = Phrase.from(var3, string.intercom_teammate_from_company)
            .put("name", this.$part.getParticipant().getForename())
            .put("company", ((AppConfig)IntercomPostActivity.access$getAppConfigProvider(this.this$0).get()).getName())
            .format();
         Modifier var10000 = Modifier.Companion;
         Avatar var10001 = this.$part.getParticipant().getAvatar();
         Intrinsics.checkNotNullExpressionValue(var10001, "getAvatar(...)");
         String var10002 = var19.toString();
         String var10003 = IntercomPostActivity.access$getUserStatus(this.this$0);
         var1.startReplaceGroup(1506964192);
         ComposerKt.sourceInformation(var1, "CC(remember):IntercomPostActivity.kt#9igjgp");
         Composer var21 = var1;
         boolean var22 = var1.changedInstance(this.this$0);
         IntercomPostActivity var8 = this.this$0;
         int var9 = 0;
         Object var10 = var21.rememberedValue();
         int var11 = 0;
         Object var10004;
         if (!var22 && var10 != Composer.Companion.getEmpty()) {
            var10004 = var10;
         } else {
            String var17 = var10003;
            String var16 = var10002;
            Avatar var15 = var10001;
            Modifier var14 = var10000;
            int var12 = 0;
            final io.intercom.android.sdk.post.IntercomPostActivity recoveredCapture_1209_0 = var8;
            Function0 var18 = ((kotlin.jvm.functions.Function0) () -> IntercomPostActivity$onCreate$2$1$2.invoke$lambda$1$lambda$0(recoveredCapture_1209_0));
            var10000 = var14;
            var10001 = var15;
            var10002 = var16;
            var10003 = var17;
            Object var13 = var18;
            var21.updateRememberedValue(var13);
            var10004 = var13;
         }

         Function0 var20 = (Function0)var10004;
         var1.endReplaceGroup();
         IntercomPostActivityKt.access$TopBar(var10000, var10001, var10002, var10003, var20, var1, 6);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(IntercomPostActivity var0) {
      var0.finish();
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
