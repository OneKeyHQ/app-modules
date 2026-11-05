package io.intercom.android.sdk.blocks.lib.interfaces;

import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import io.intercom.android.sdk.blocks.lib.BlockAlignment;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;

public interface LocalImageBlock {
   View addImage(Uri var1, int var2, int var3, BlockAlignment var4, BlockMetadata var5, ViewGroup var6);
}
