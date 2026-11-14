package io.intercom.android.sdk.m5.preview.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.util.Log;
import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.activity.compose.ComponentActivityKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.core.os.BundleCompat;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.LifecycleOwnerKt;
import io.intercom.android.sdk.activities.IntercomActivity;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewArgs;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile;
import io.intercom.android.sdk.m5.preview.viewmodel.PreviewViewModel;
import java.util.ArrayList;
import java.util.List;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0007\u0018\u0000 \u001c2\u00020\u00012\u00020\u0002:\u0001\u001cB\t\b\u0007¢\u0006\u0004\b\u0003\u0010\u0004J\u0012\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0014J\b\u0010\u000f\u001a\u00020\u0010H\u0002J\u001e\u0010\u0011\u001a\u00020\f2\u0006\u0010\u0012\u001a\u00020\u00132\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00160\u0015H\u0002J\u0010\u0010\u0017\u001a\u00020\f2\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\u0010\u0010\u001a\u001a\u00020\f2\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\b\u0010\u001b\u001a\u00020\fH\u0002R\u001b\u0010\u0005\u001a\u00020\u00068BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\t\u0010\n\u001a\u0004\b\u0007\u0010\b¨\u0006\u001d",
   d2 = {
         "Lio/intercom/android/sdk/m5/preview/ui/IntercomPreviewActivity;",
         "Landroidx/activity/ComponentActivity;",
         "Lio/intercom/android/sdk/activities/IntercomActivity;",
         "<init>",
         "()V",
         "viewModel",
         "Lio/intercom/android/sdk/m5/preview/viewmodel/PreviewViewModel;",
         "getViewModel",
         "()Lio/intercom/android/sdk/m5/preview/viewmodel/PreviewViewModel;",
         "viewModel$delegate",
         "Lkotlin/Lazy;",
         "onCreate",
         "",
         "savedInstanceState",
         "Landroid/os/Bundle;",
         "getPreviewData",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewArgs;",
         "finishWithResult",
         "resultCode",
         "",
         "uris",
         "",
         "Landroid/net/Uri;",
         "checkAndHandlePdfBrowserFallback",
         "file",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile;",
         "openPdfInBrowser",
         "setupPreviewUI",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class IntercomPreviewActivity extends ComponentActivity implements IntercomActivity {
   @NotNull
   public static final IntercomPreviewActivity$Companion Companion = new IntercomPreviewActivity$Companion(null);
   @NotNull
   private final Lazy viewModel$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.preview.ui.IntercomPreviewActivity.viewModel_delegate$lambda$0(this)));
   public static final int $stable;

   static { $stable = 8; }

   private final PreviewViewModel getViewModel() {
      Lazy var1 = this.viewModel$delegate;
      return (PreviewViewModel)var1.getValue();
   }

   @Override
   protected void onCreate(@Nullable Bundle var1) {
      super.onCreate(var1);
      IntercomPreviewArgs var2 = this.getPreviewData();
      if (var2.getUris().size() == 1) {
         IntercomPreviewFile var3 = (IntercomPreviewFile)CollectionsKt.first(var2.getUris());
         String var4 = var3.getMimeType(this);
         if (StringsKt.contains(var4, "pdf", true)) {
            this.checkAndHandlePdfBrowserFallback(var3);
            return;
         }
      }

      this.setupPreviewUI();
   }

   private final IntercomPreviewArgs getPreviewData() {
      Bundle var10000 = this.getIntent().getExtras();
      if (var10000 != null) {
         Bundle var1 = var10000;
         int var2 = 0;
         IntercomPreviewArgs var3 = (IntercomPreviewArgs)BundleCompat.getParcelable(var1, "INTERCOM_PREVIEW_ARGS", IntercomPreviewArgs.class);
         if (var3 != null) {
            return var3;
         }
      }

      return new IntercomPreviewArgs(null, null, null, false, 0, null, 63, null);
   }

   private final void finishWithResult(int var1, List var2) {
      Intent var4 = new Intent();
      Intent var5 = var4;
      int var6 = 0;
      var5.putParcelableArrayListExtra("MEDIA_RESULT_URIS", new ArrayList(var2));
      Intent var3 = var4;
      this.setResult(var1, var3);
      this.finish();
   }

   private final void checkAndHandlePdfBrowserFallback(IntercomPreviewFile var1) {
      BuildersKt.launch$default(
         LifecycleOwnerKt.getLifecycleScope(this), null, null, new IntercomPreviewActivity$checkAndHandlePdfBrowserFallback$1(var1, this, null), 3, null
      );
   }

   private final void openPdfInBrowser(IntercomPreviewFile var1) {
      Uri var2 = var1.getUri();

      try {
         Intent var4 = new Intent("android.intent.action.VIEW", var2);
         Intent var5 = var4;
         int var6 = 0;
         var5.setFlags(268435457);
         if (VERSION.SDK_INT >= 30) {
            var5.addCategory("android.intent.category.BROWSABLE");
         }

         Intent var3 = var4;
         this.startActivity(var3);
      } catch (ActivityNotFoundException var7) {
         Log.e("IntercomPreview", "No Activity found to handle the PDF file: " + var2, var7);
      } catch (SecurityException var8) {
         Log.e("IntercomPreview", "Couldn't open PDF file because of security error: " + var8.getMessage(), var8);
      }
   }

   private final void setupPreviewUI() {
      EdgeToEdge.enable$default(this, null, null, 3, null);
      WindowCompat.getInsetsController(this.getWindow(), this.getWindow().getDecorView()).setAppearanceLightStatusBars(false);
      ComponentActivityKt.setContent$default(
         this, null, ComposableLambdaKt.composableLambdaInstance(-2127503266, true, new IntercomPreviewActivity$setupPreviewUI$1(this)), 1, null
      );
   }

   private static final PreviewViewModel viewModel_delegate$lambda$0(IntercomPreviewActivity var0) {
      return PreviewViewModel.Companion.create$intercom_sdk_base_release(var0, var0.getPreviewData());
   }

   // $VF: synthetic method
   public static final void access$openPdfInBrowser(IntercomPreviewActivity var0, IntercomPreviewFile var1) {
      var0.openPdfInBrowser(var1);
   }

   // $VF: synthetic method
   public static final void access$finishWithResult(IntercomPreviewActivity var0, int var1, List var2) {
      var0.finishWithResult(var1, var2);
   }

   // $VF: synthetic method
   public static final void access$setupPreviewUI(IntercomPreviewActivity var0) {
      var0.setupPreviewUI();
   }

   // $VF: synthetic method
   public static final PreviewViewModel access$getViewModel(IntercomPreviewActivity var0) {
      return var0.getViewModel();
   }

   // $VF: synthetic method
   public static final IntercomPreviewArgs access$getPreviewData(IntercomPreviewActivity var0) {
      return var0.getPreviewData();
   }
}
