package io.intercom.android.sdk.ui.common;

import android.content.Context;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u00002\u00020\u0001:\u0002\u0007\bB\t\b\u0005¢\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0004\u001a\u00020\u0005H\u0007¢\u0006\u0002\u0010\u0006\u0082\u0001\u0002\t\n¨\u0006\u000b",
   d2 = {
         "Lio/intercom/android/sdk/ui/common/StringProvider;",
         "",
         "<init>",
         "()V",
         "getText",
         "",
         "(Landroidx/compose/runtime/Composer;I)Ljava/lang/String;",
         "StringRes",
         "ActualString",
         "Lio/intercom/android/sdk/ui/common/StringProvider$ActualString;",
         "Lio/intercom/android/sdk/ui/common/StringProvider$StringRes;",
         "intercom-sdk-ui_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nActualStringOrRes.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ActualStringOrRes.kt\nio/intercom/android/sdk/ui/common/StringProvider\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,41:1\n75#2:42\n*S KotlinDebug\n*F\n+ 1 ActualStringOrRes.kt\nio/intercom/android/sdk/ui/common/StringProvider\n*L\n23#1:42\n*E\n"
)
public abstract class StringProvider {
   public static int $stable;

   static {}

   private StringProvider() {
   }

   @Composable
   @NotNull
   public final String getText(@Nullable Composer var1, int var2) {
      var1.startReplaceGroup(-1160503525);
      ComposerKt.sourceInformation(var1, "C(getText)22@680L7:ActualStringOrRes.kt#ow2m35");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(-1160503525, var2, -1, "io.intercom.android.sdk.ui.common.StringProvider.getText (ActualStringOrRes.kt:19)");
      }

      StringProvider var4 = this;
      String var10000;
      if (var4 instanceof StringProvider$ActualString) {
         var10000 = ((StringProvider$ActualString)this).getString();
      } else {
         if (!(var4 instanceof StringProvider$StringRes)) {
            throw new NoWhenBranchMatchedException();
         }

         CompositionLocal var5 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var6 = 0;
         int var7 = 0;
         ComposerKt.sourceInformationMarkerStart(var1, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var8 = var1.consume(var5);
         ComposerKt.sourceInformationMarkerEnd(var1);
         var10000 = ActualStringOrResKt.parseString(
            (Context)var8, ((StringProvider$StringRes)this).getStringRes(), ((StringProvider$StringRes)this).getParams()
         );
      }

      String var3 = var10000;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var1.endReplaceGroup();
      return var3;
   }

   // $VF: synthetic method
   public StringProvider(DefaultConstructorMarker var1) {
      this();
   }
}
