package io.intercom.android.sdk.models;

import java.util.Set;
import kotlin.collections.SetsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\b\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u000e\u0010\u0000\u001a\u00020\u0001*\u0004\u0018\u00010\u0001H\u0000¨\u0006\u0002",
   d2 = {"sanitized", "Lio/intercom/android/sdk/models/AttachmentSettings;", "intercom-sdk-base_release"}
)
public final class AttachmentSettingsKt {
   @NotNull
   public static final AttachmentSettings sanitized(@Nullable AttachmentSettings var0) {
      if (var0 != null) {
         Set var10006 = var0.getTrustedFileExtensions();
         if (var10006 == null) {
            var10006 = SetsKt.emptySet();
         }

         AttachmentSettings var10000 = AttachmentSettings.copy$default(var0, false, false, false, false, false, var10006, 0L, 95, null);
         if (var10000 != null) {
            return var10000;
         }
      }

      return AttachmentSettings.Companion.getDEFAULT();
   }
}
