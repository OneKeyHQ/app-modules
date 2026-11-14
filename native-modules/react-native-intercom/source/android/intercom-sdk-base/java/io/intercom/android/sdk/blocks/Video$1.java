package io.intercom.android.sdk.blocks;

import android.widget.ImageView;
import io.intercom.android.sdk.blocks.views.VideoPreviewView;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

class Video$1 implements Callback {
   // $VF: synthetic field
   final VideoPreviewView val$previewView;
   // $VF: synthetic field
   final String val$id;
   // $VF: synthetic field
   final ImageView val$videoImageView;
   // $VF: synthetic field
   final Video this$0;

   Video$1(Video var1, VideoPreviewView var2, String var3, ImageView var4) {
      this.this$0 = var1;
      this.val$previewView = var2;
      this.val$id = var3;
      this.val$videoImageView = var4;
   }

   @Override
   public void onFailure(Call var1, IOException var2) {
      this.val$previewView.showFailedImage();
   }

   @Override
   public void onResponse(Call var1, Response var2) {
      if (var2.isSuccessful()) {
         String var3 = "https://fast.wistia.net/embed/iframe/" + this.val$id;
         String var4 = Video.access$000(this.this$0, var2);
         this.val$videoImageView.post(new Video$1$1(this, var3, var4));
      } else {
         this.val$previewView.showFailedImage();
      }
   }
}
