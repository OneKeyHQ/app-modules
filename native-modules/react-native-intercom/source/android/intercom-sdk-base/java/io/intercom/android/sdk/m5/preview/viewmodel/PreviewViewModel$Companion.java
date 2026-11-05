package io.intercom.android.sdk.m5.preview.viewmodel;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.lifecycle.ViewModelProvider.Factory;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewArgs;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0080\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001d\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0000¢\u0006\u0002\b\nJ\u0015\u0010\u000b\u001a\u00020\f2\u0006\u0010\b\u001a\u00020\tH\u0000¢\u0006\u0002\b\r¨\u0006\u000e",
   d2 = {
         "Lio/intercom/android/sdk/m5/preview/viewmodel/PreviewViewModel$Companion;",
         "",
         "<init>",
         "()V",
         "create",
         "Lio/intercom/android/sdk/m5/preview/viewmodel/PreviewViewModel;",
         "owner",
         "Landroidx/lifecycle/ViewModelStoreOwner;",
         "previewArgs",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewArgs;",
         "create$intercom_sdk_base_release",
         "factory",
         "Landroidx/lifecycle/ViewModelProvider$Factory;",
         "factory$intercom_sdk_base_release",
         "intercom-sdk-base_release"
   }
)
public final class PreviewViewModel$Companion {
   private PreviewViewModel$Companion() {
   }

   @NotNull
   public final PreviewViewModel create$intercom_sdk_base_release(@NotNull ViewModelStoreOwner var1, @NotNull IntercomPreviewArgs var2) {
      Intrinsics.checkNotNullParameter(var1, "owner");
      Intrinsics.checkNotNullParameter(var2, "previewArgs");
      return (PreviewViewModel)new ViewModelProvider(var1, this.factory$intercom_sdk_base_release(var2)).get(PreviewViewModel.class);
   }

   @NotNull
   public final Factory factory$intercom_sdk_base_release(@NotNull IntercomPreviewArgs var1) {
      Intrinsics.checkNotNullParameter(var1, "previewArgs");
      return new PreviewViewModel$Companion$factory$1(var1);
   }

   // $VF: synthetic method
   public PreviewViewModel$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
