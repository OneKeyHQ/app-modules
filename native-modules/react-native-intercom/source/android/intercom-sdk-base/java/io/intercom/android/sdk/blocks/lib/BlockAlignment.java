package io.intercom.android.sdk.blocks.lib;

import java.util.Locale;

/* JADX INFO: loaded from: intercom-sdk-base-18.10.0.aar:classes.jar:io/intercom/android/sdk/blocks/lib/BlockAlignment.class */
public enum BlockAlignment {
    LEFT { // from class: io.intercom.android.sdk.blocks.lib.BlockAlignment.1
        @Override // io.intercom.android.sdk.blocks.lib.BlockAlignment
        public int getGravity() {
            return 8388611;
        }
    },
    CENTER { // from class: io.intercom.android.sdk.blocks.lib.BlockAlignment.2
        @Override // io.intercom.android.sdk.blocks.lib.BlockAlignment
        public int getGravity() {
            return 1;
        }
    },
    RIGHT { // from class: io.intercom.android.sdk.blocks.lib.BlockAlignment.3
        @Override // io.intercom.android.sdk.blocks.lib.BlockAlignment
        public int getGravity() {
            return 8388613;
        }
    };

    public abstract int getGravity();

    public static BlockAlignment alignValueOf(String align) {
        BlockAlignment blockAlignment = LEFT;
        try {
            blockAlignment = valueOf(align.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException | NullPointerException e) {
        }
        return blockAlignment;
    }
}
