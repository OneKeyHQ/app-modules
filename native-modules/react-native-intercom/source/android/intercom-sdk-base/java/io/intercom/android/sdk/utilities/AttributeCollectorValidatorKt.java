package io.intercom.android.sdk.utilities;

import android.content.res.Resources;
import io.intercom.android.sdk.R.string;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0012\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\u001a\u0014\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0000¨\u0006\u0005",
   d2 = {"getErrorStringFromCode", "", "Landroid/content/res/Resources;", "errorCode", "", "intercom-sdk-base_release"}
)
public final class AttributeCollectorValidatorKt {
   @NotNull
   public static final String getErrorStringFromCode(@NotNull Resources var0, int var1) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      String var10000;
      switch (var1) {
         case 1:
            var10000 = var0.getString(string.intercom_string_is_incorrect);
            Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
            break;
         case 2:
            var10000 = var0.getString(string.intercom_that_email_address_doesnt_look_quite_right);
            Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
            break;
         case 3:
            var10000 = var0.getString(string.intercom_that_number_doesnt_look_quite_right);
            Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
            break;
         case 4:
            var10000 = var0.getString(string.intercom_that_number_needs_a_prefix);
            Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
            break;
         case 5:
            var10000 = var0.getString(string.intercom_that_number_has_too_many_digits);
            Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
            break;
         case 6:
            var10000 = var0.getString(string.intercom_that_country_code_doesnt_look_quite_right);
            Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
            break;
         case 7:
            var10000 = var0.getString(string.intercom_that_number_is_missing_a_few_digits);
            Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
            break;
         default:
            var10000 = "";
      }

      return var10000;
   }
}
