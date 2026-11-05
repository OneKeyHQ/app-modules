package io.intercom.android.sdk.utilities;

import android.content.Context;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.R.string;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\r\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bÁ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003J \u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0007J \u0010\f\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0007¨\u0006\r",
   d2 = {
         "Lio/intercom/android/sdk/utilities/GroupConversationTextFormatter;",
         "",
         "<init>",
         "()V",
         "groupConversationTitle",
         "",
         "firstName",
         "",
         "otherParticipants",
         "",
         "context",
         "Landroid/content/Context;",
         "groupConversationSubtitle",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class GroupConversationTextFormatter {
   @NotNull
   public static final GroupConversationTextFormatter INSTANCE = new GroupConversationTextFormatter();
   public static int $stable;

   static {}

   private GroupConversationTextFormatter() {
   }

   @JvmStatic
   @NotNull
   public static final CharSequence groupConversationTitle(@NotNull String var0, int var1, @NotNull Context var2) {
      Intrinsics.checkNotNullParameter(var0, "firstName");
      Intrinsics.checkNotNullParameter(var2, "context");
      return var1 == 1
         ? var0 + var2.getString(string.intercom_group_conversation_1_other_participant_count_short)
         : (
            var1 > 1
               ? var0
                  + Phrase.from(var2, string.intercom_group_conversation_multiple_other_participant_count_short).put("other_participant_count", var1).format()
               : var0
         );
   }

   @JvmStatic
   @NotNull
   public static final CharSequence groupConversationSubtitle(@NotNull String var0, int var1, @NotNull Context var2) {
      Intrinsics.checkNotNullParameter(var0, "firstName");
      Intrinsics.checkNotNullParameter(var2, "context");
      CharSequence var10000;
      if (var1 == 1) {
         CharSequence var3 = Phrase.from(var2, string.intercom_name_and_1_other).put("name", var0).format();
         Intrinsics.checkNotNull(var3);
         var10000 = var3;
      } else if (var1 > 1) {
         CharSequence var4 = Phrase.from(var2, string.intercom_name_and_x_others).put("name", var0).put("count", var1).format();
         Intrinsics.checkNotNull(var4);
         var10000 = var4;
      } else {
         var10000 = var0;
      }

      return var10000;
   }
}
