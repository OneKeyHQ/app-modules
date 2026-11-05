package io.intercom.android.sdk.m5.preview.ui;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import androidx.activity.result.contract.ActivityResultContract;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.TuplesKt;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0011\n\u0002\b\r\b\u0002\u0018\u0000 #2\u0014\u0012\u0004\u0012\u00020\u0002\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00030\u0001:\u0001#B%\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n¢\u0006\u0004\b\u000b\u0010\fJ\u0018\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0002H\u0016J \u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0013\u001a\u00020\n2\b\u0010\u0014\u001a\u0004\u0018\u00010\u000eH\u0016J\u0010\u0010\u0015\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\u0002H\u0002J\u0013\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00020\u0017H\u0002¢\u0006\u0002\u0010\u0018J\u0013\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00020\u0017H\u0002¢\u0006\u0002\u0010\u0018J\u0013\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00020\u0017H\u0002¢\u0006\u0002\u0010\u0018J\u0013\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00020\u0017H\u0002¢\u0006\u0002\u0010\u0018J\u0016\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00020\u00032\u0006\u0010\u001d\u001a\u00020\u0002H\u0002J\u001b\u0010\u001e\u001a\u00020\u000e2\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00020\u0017H\u0002¢\u0006\u0002\u0010 J\u001b\u0010!\u001a\u00020\u000e2\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00020\u0017H\u0002¢\u0006\u0002\u0010 J\u0012\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003*\u00020\u000eH\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006$",
   d2 = {
         "Lio/intercom/android/sdk/m5/preview/ui/IntercomMediaPicker;",
         "Landroidx/activity/result/contract/ActivityResultContract;",
         "",
         "",
         "Landroid/net/Uri;",
         "mediaType",
         "Lio/intercom/android/sdk/m5/preview/ui/MediaType;",
         "trustedFileExtensions",
         "",
         "maxSelectionCount",
         "",
         "<init>",
         "(Lio/intercom/android/sdk/m5/preview/ui/MediaType;Ljava/util/Set;I)V",
         "createIntent",
         "Landroid/content/Intent;",
         "context",
         "Landroid/content/Context;",
         "input",
         "parseResult",
         "resultCode",
         "intent",
         "getIntent",
         "getVideoMimeTypes",
         "",
         "()[Ljava/lang/String;",
         "getImageMimeTypes",
         "getDocumentMimeTypes",
         "getAllMimeTypes",
         "getMimeTypesForExtension",
         "extension",
         "getPhotoPickerIntent",
         "supportedTypes",
         "([Ljava/lang/String;)Landroid/content/Intent;",
         "getNativePickerIntent",
         "getClipDataUris",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nMediaPickerButton.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MediaPickerButton.kt\nio/intercom/android/sdk/m5/preview/ui/IntercomMediaPicker\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,258:1\n3829#2:259\n4344#2,2:260\n3829#2:266\n4344#2,2:267\n3829#2:273\n4344#2,2:274\n37#3:262\n36#3,3:263\n37#3:269\n36#3,3:270\n37#3:276\n36#3,3:277\n37#3:286\n36#3,3:287\n1374#4:280\n1460#4,5:281\n*S KotlinDebug\n*F\n+ 1 MediaPickerButton.kt\nio/intercom/android/sdk/m5/preview/ui/IntercomMediaPicker\n*L\n161#1:259\n161#1:260,2\n164#1:266\n164#1:267,2\n167#1:273\n167#1:274,2\n161#1:262\n161#1:263,3\n164#1:269\n164#1:270,3\n168#1:276\n168#1:277,3\n172#1:286\n172#1:287,3\n170#1:280\n170#1:281,5\n*E\n"
)
final class IntercomMediaPicker extends ActivityResultContract {
   @NotNull
   private static final IntercomMediaPicker$Companion Companion = new IntercomMediaPicker$Companion(null);
   @NotNull
   private final MediaType mediaType;
   @NotNull
   private final Set trustedFileExtensions;
   private final int maxSelectionCount;
   @NotNull
   private static final Map ADDITIONAL_MIME_TYPES;

   public IntercomMediaPicker(@NotNull MediaType var1, @NotNull Set var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "mediaType");
      Intrinsics.checkNotNullParameter(var2, "trustedFileExtensions");
      super();
      this.mediaType = var1;
      this.trustedFileExtensions = var2;
      this.maxSelectionCount = var3;
   }

   @NotNull
   public Intent createIntent(@NotNull Context var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "input");
      return this.getIntent(var2);
   }

   @NotNull
   public List parseResult(int var1, @Nullable Intent var2) {
      Intent var4 = var2;
      int var6 = 0;
      Intent var3 = var1 == -1 ? var4 : null;
      if (var3 != null) {
         List var7 = this.getClipDataUris(var3);
         if (var7 != null) {
            return var7;
         }
      }

      return CollectionsKt.emptyList();
   }

   private final Intent getIntent(String var1) {
      Intent var10000;
      switch (this.mediaType) {
         case All:
            Intent var2 = new Intent("android.intent.action.GET_CONTENT")
               .addCategory("android.intent.category.OPENABLE")
               .putExtra("android.intent.extra.MIME_TYPES", this.getAllMimeTypes())
               .setType(var1)
               .putExtra("android.intent.extra.ALLOW_MULTIPLE", this.maxSelectionCount > 1);
            Intrinsics.checkNotNull(var2);
            var10000 = var2;
            break;
         case DocumentOnly:
            var10000 = this.getNativePickerIntent(this.getDocumentMimeTypes());
            break;
         case ImageAndVideo:
            var10000 = MediaPickerButtonKt.isPhotoPickerAvailable()
               ? this.getPhotoPickerIntent((String[])ArraysKt.plus(this.getImageMimeTypes(), this.getVideoMimeTypes()))
               : this.getNativePickerIntent((String[])ArraysKt.plus(this.getImageMimeTypes(), this.getVideoMimeTypes()));
            break;
         case ImageOnly:
            var10000 = MediaPickerButtonKt.isPhotoPickerAvailable()
               ? this.getPhotoPickerIntent(this.getImageMimeTypes())
               : this.getNativePickerIntent(this.getImageMimeTypes());
            break;
         case VideOnly:
            var10000 = MediaPickerButtonKt.isPhotoPickerAvailable()
               ? this.getPhotoPickerIntent(this.getVideoMimeTypes())
               : this.getNativePickerIntent(this.getVideoMimeTypes());
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return var10000;
   }

   private final String[] getVideoMimeTypes() {
      Object[] var1 = this.getAllMimeTypes();
      int var2 = 0;
      Object[] var3 = var1;
      ArrayList var4 = new ArrayList();
      int var5 = 0;
      int var6 = 0;

      for (int var7 = var3.length; var6 < var7; var6++) {
         Object var8 = var3[var6];
         String var9 = (String)var8;
         int var10 = 0;
         if (StringsKt.contains$default(var9, "video", false, 2, null)) {
            var4.add(var8);
         }
      }

      var var11 = (Collection & List)var4;
      var2 = 0;
      Collection var13 = var11;
      return (String[])var13.toArray(new String[0]);
   }

   private final String[] getImageMimeTypes() {
      Object[] var1 = this.getAllMimeTypes();
      int var2 = 0;
      Object[] var3 = var1;
      ArrayList var4 = new ArrayList();
      int var5 = 0;
      int var6 = 0;

      for (int var7 = var3.length; var6 < var7; var6++) {
         Object var8 = var3[var6];
         String var9 = (String)var8;
         int var10 = 0;
         if (StringsKt.contains$default(var9, "image", false, 2, null)) {
            var4.add(var8);
         }
      }

      var var11 = (Collection & List)var4;
      var2 = 0;
      Collection var13 = var11;
      return (String[])var13.toArray(new String[0]);
   }

   private final String[] getDocumentMimeTypes() {
      Object[] var1 = this.getAllMimeTypes();
      int var2 = 0;
      Object[] var3 = var1;
      ArrayList var4 = new ArrayList();
      int var5 = 0;
      int var6 = 0;

      for (int var7 = var3.length; var6 < var7; var6++) {
         Object var8 = var3[var6];
         String var9 = (String)var8;
         int var10 = 0;
         if (StringsKt.contains$default(var9, "application", false, 2, null) || StringsKt.contains$default(var9, "text", false, 2, null)) {
            var4.add(var8);
         }
      }

      var var11 = (Collection & List)var4;
      var2 = 0;
      Collection var13 = var11;
      return (String[])var13.toArray(new String[0]);
   }

   private final String[] getAllMimeTypes() {
      Iterable var1 = this.trustedFileExtensions;
      int var2 = 0;
      Iterable var3 = var1;
      ArrayList var4 = new ArrayList();
      int var5 = 0;

      for (Object var7 : var3) {
         String var8 = (String)var7;
         int var9 = 0;
         Iterable var13 = this.getMimeTypesForExtension(var8);
         CollectionsKt.addAll(var4, var13);
      }

      List recoveredMimeList = (List)var4;
      Collection recoveredMimeTypes = (Collection)recoveredMimeList;
      var2 = 0;
      return (String[])recoveredMimeTypes.toArray(new String[0]);
   }

   private final List getMimeTypesForExtension(String var1) {
      Map var10000 = ADDITIONAL_MIME_TYPES;
      String var10001 = var1.toLowerCase(Locale.ROOT);
      Intrinsics.checkNotNullExpressionValue(var10001, "toLowerCase(...)");
      List var4 = (List)var10000.get(var10001);
      if (var4 == null) {
         var4 = CollectionsKt.emptyList();
      }

      List var2 = var4;
      String var3 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var1);
      return CollectionsKt.distinct(CollectionsKt.plus(var2, CollectionsKt.listOfNotNull(var3)));
   }

   private final Intent getPhotoPickerIntent(String[] var1) {
      Intent var2 = new Intent("android.provider.action.PICK_IMAGES");
      Intent var3 = var2;
      int var4 = 0;
      var3.putExtra("android.intent.extra.MIME_TYPES", var1);
      if (this.maxSelectionCount > 1) {
         if (this.maxSelectionCount > MediaStore.getPickImagesMaxLimit()) {
            int var5 = 0;
            String var6 = "Max items must be less or equals MediaStore.getPickImagesMaxLimit()";
            throw new IllegalArgumentException(var6.toString());
         }

         var3.putExtra("android.provider.extra.PICK_IMAGES_MAX", this.maxSelectionCount);
      }

      return var2;
   }

   private final Intent getNativePickerIntent(String[] var1) {
      Intent var10000 = new Intent("android.intent.action.GET_CONTENT")
         .addCategory("android.intent.category.OPENABLE")
         .setType((String)ArraysKt.first(var1))
         .putExtra("android.intent.extra.MIME_TYPES", var1)
         .addFlags(1)
         .putExtra("android.intent.extra.ALLOW_MULTIPLE", this.maxSelectionCount > 1);
      Intrinsics.checkNotNullExpressionValue(var10000, "putExtra(...)");
      return var10000;
   }

   private final List getClipDataUris(Intent var1) {
      LinkedHashSet var2 = new LinkedHashSet();
      Uri var10000 = var1.getData();
      if (var10000 != null) {
         Uri var5 = var10000;
         int var6 = 0;
         var2.add(var5);
      }

      ClipData var3 = var1.getClipData();
      if (var3 == null && var2.isEmpty()) {
         return CollectionsKt.emptyList();
      }

      if (var3 != null) {
         int var4 = 0;

         for (int var7 = var3.getItemCount(); var4 < var7; var4++) {
            Uri var8 = var3.getItemAt(var4).getUri();
            if (var8 != null) {
               var2.add(var8);
            }
         }
      }

      return new ArrayList(var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Intent createIntent(Context var1, Object var2) {
      return this.createIntent(var1, (String)var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   public static final Map access$getADDITIONAL_MIME_TYPES$cp() {
      return ADDITIONAL_MIME_TYPES;
   }

   static {
      String[] var0 = new String[]{"text/csv", "application/csv", "text/x-csv", "application/x-csv"};
      ADDITIONAL_MIME_TYPES = MapsKt.mapOf(TuplesKt.to("csv", CollectionsKt.listOf(var0)));
   }
}
