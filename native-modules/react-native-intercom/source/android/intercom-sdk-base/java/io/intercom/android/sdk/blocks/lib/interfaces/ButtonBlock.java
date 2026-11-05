package io.intercom.android.sdk.blocks.lib.interfaces;

import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import io.intercom.android.sdk.blocks.lib.BlockAlignment;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;

public interface ButtonBlock {
   View addButton(Spanned var1, String var2, BlockAlignment var3, BlockMetadata var4, ViewGroup var5);
}
