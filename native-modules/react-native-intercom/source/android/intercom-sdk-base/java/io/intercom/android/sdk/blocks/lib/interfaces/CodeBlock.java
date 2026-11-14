package io.intercom.android.sdk.blocks.lib.interfaces;

import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;

public interface CodeBlock {
   View addCode(Spanned var1, BlockMetadata var2, ViewGroup var3);
}
