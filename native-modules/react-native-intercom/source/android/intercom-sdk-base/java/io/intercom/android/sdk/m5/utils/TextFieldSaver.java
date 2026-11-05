package io.intercom.android.sdk.m5.utils;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.runtime.saveable.ListSaverKt;
import androidx.compose.runtime.saveable.Saver;
import androidx.compose.runtime.saveable.SaverScope;
import androidx.compose.ui.text.TextRange;
import androidx.compose.ui.text.TextRangeKt;
import androidx.compose.ui.text.input.TextFieldValue;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bÁ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003R\u001b\u0010\u0004\u001a\f\u0012\u0004\u0012\u00020\u0006\u0012\u0002\b\u00030\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\t",
   d2 = {
         "Lio/intercom/android/sdk/m5/utils/TextFieldSaver;",
         "",
         "<init>",
         "()V",
         "textFieldValueSaver",
         "Landroidx/compose/runtime/saveable/Saver;",
         "Landroidx/compose/ui/text/input/TextFieldValue;",
         "getTextFieldValueSaver",
         "()Landroidx/compose/runtime/saveable/Saver;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class TextFieldSaver {
   @NotNull
   public static final TextFieldSaver INSTANCE = new TextFieldSaver();
   @NotNull
   private static final Saver textFieldValueSaver = ListSaverKt.listSaver(
      ((kotlin.jvm.functions.Function2) (recoveredArgument_1190_0, recoveredArgument_1190_1) -> TextFieldSaver.textFieldValueSaver$lambda$0((androidx.compose.runtime.saveable.SaverScope)recoveredArgument_1190_0, (androidx.compose.ui.text.input.TextFieldValue)recoveredArgument_1190_1)), ((kotlin.jvm.functions.Function1) (recoveredArgument_1191_0) -> TextFieldSaver.textFieldValueSaver$lambda$1((java.util.List)recoveredArgument_1191_0))
   );
   public static final int $stable;

   static { $stable = 8; }

   private TextFieldSaver() {
   }

   @NotNull
   public final Saver getTextFieldValueSaver() {
      return textFieldValueSaver;
   }

   private static final List textFieldValueSaver$lambda$0(SaverScope var0, TextFieldValue var1) {
      Intrinsics.checkNotNullParameter(var0, "$this$listSaver");
      Intrinsics.checkNotNullParameter(var1, "it");
      Object[] var2 = new Object[]{
         var1.getText(),
         TextRange.recovered$28b4722c85ffc757(var1.recovered$59ba9c6163f81a8d()),
         TextRange.recovered$447e5818a361e3ad(var1.recovered$59ba9c6163f81a8d()),
         null,
         null
      };
      TextRange var10002 = var1.recovered$2ca1a6aaaaa1b68f();
      var2[3] = var10002 != null ? TextRange.recovered$28b4722c85ffc757(var10002.recovered$45f9de7b85e0e0b5()) : -1;
      var10002 = var1.recovered$2ca1a6aaaaa1b68f();
      var2[4] = var10002 != null ? TextRange.recovered$447e5818a361e3ad(var10002.recovered$45f9de7b85e0e0b5()) : -1;
      return CollectionsKt.listOf(var2);
   }

   private static final TextFieldValue textFieldValueSaver$lambda$1(List var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      TextFieldValue var10000;
      String var10002 = (String)var0.get(0);
      Intrinsics.checkNotNull(var10002, "null cannot be cast to non-null type kotlin.String");
      var10002 = var10002;
      Object var10003 = var0.get(1);
      Intrinsics.checkNotNull(var10003, "null cannot be cast to non-null type kotlin.Int");
      int var2 = (Integer)var10003;
      Object var10004 = var0.get(2);
      Intrinsics.checkNotNull(var10004, "null cannot be cast to non-null type kotlin.Int");
      long var3 = TextRangeKt.TextRange(var2, (Integer)var10004);
      var10004 = var0.get(3);
      Intrinsics.checkNotNull(var10004, "null cannot be cast to non-null type kotlin.Int");
      TextRange var7;
      if ((Integer)var10004 >= 0) {
         Object var5 = var0.get(3);
         Intrinsics.checkNotNull(var5, "null cannot be cast to non-null type kotlin.Int");
         int var6 = (Integer)var5;
         Object var10005 = var0.get(4);
         Intrinsics.checkNotNull(var10005, "null cannot be cast to non-null type kotlin.Int");
         var7 = TextRange.recovered$58f3be759041da90(TextRangeKt.TextRange(var6, (Integer)var10005));
      } else {
         var7 = null;
      }

      var10000 = new TextFieldValue(var10002, var3, var7, null);
      return var10000;
   }
}
