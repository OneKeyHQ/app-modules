package io.intercom.android.sdk.ui.theme;

import androidx.compose.runtime.Immutable;
import androidx.compose.ui.graphics.Color;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0017\n\u0002\u0010\u000b\n\u0002\bu\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B§\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u0010\u0012\u0006\u0010\u0011\u001a\u00020\u0012\u0012\u0006\u0010\u0013\u001a\u00020\u0014\u0012\u0006\u0010\u0015\u001a\u00020\u0016\u0012\u0006\u0010\u0017\u001a\u00020\u0003\u0012\u0006\u0010\u0018\u001a\u00020\u0003\u0012\u0006\u0010\u0019\u001a\u00020\u0003\u0012\u0006\u0010\u001a\u001a\u00020\u0003\u0012\u0006\u0010\u001b\u001a\u00020\u0003\u0012\u0006\u0010\u001c\u001a\u00020\u0003\u0012\u0006\u0010\u001d\u001a\u00020\u0003\u0012\u0006\u0010\u001e\u001a\u00020\u0003\u0012\u0006\u0010\u001f\u001a\u00020\u0003\u0012\u0006\u0010 \u001a\u00020\u0003\u0012\u0006\u0010!\u001a\u00020\u0003\u0012\u0006\u0010\"\u001a\u00020\u0003\u0012\u0006\u0010#\u001a\u00020\u0003\u0012\u0006\u0010$\u001a\u00020\u0003\u0012\u0006\u0010%\u001a\u00020\u0003\u0012\u0006\u0010&\u001a\u00020\u0003\u0012\u0006\u0010'\u001a\u00020\u0003\u0012\u0006\u0010(\u001a\u00020\u0003\u0012\u0006\u0010)\u001a\u00020\u0003\u0012\u0006\u0010*\u001a\u00020\u0003\u0012\u0006\u0010+\u001a\u00020\u0003\u0012\u0006\u0010,\u001a\u00020\u0003\u0012\u0006\u0010-\u001a\u00020.¢\u0006\u0004\b/\u00100J\u0010\u0010^\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b_\u00102J\u0010\u0010`\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\ba\u00102J\u0010\u0010b\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\bc\u00102J\u0010\u0010d\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\be\u00102J\u0010\u0010f\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\bg\u00102J\u0010\u0010h\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\bi\u00102J\t\u0010j\u001a\u00020\nHÆ\u0003J\t\u0010k\u001a\u00020\fHÆ\u0003J\t\u0010l\u001a\u00020\u000eHÆ\u0003J\t\u0010m\u001a\u00020\u0010HÆ\u0003J\t\u0010n\u001a\u00020\u0012HÆ\u0003J\t\u0010o\u001a\u00020\u0014HÆ\u0003J\t\u0010p\u001a\u00020\u0016HÆ\u0003J\u0010\u0010q\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\br\u00102J\u0010\u0010s\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\bt\u00102J\u0010\u0010u\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\bv\u00102J\u0010\u0010w\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\bx\u00102J\u0010\u0010y\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\bz\u00102J\u0010\u0010{\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b|\u00102J\u0010\u0010}\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b~\u00102J\u0011\u0010\u007f\u001a\u00020\u0003HÆ\u0003¢\u0006\u0005\b\u0080\u0001\u00102J\u0012\u0010\u0081\u0001\u001a\u00020\u0003HÆ\u0003¢\u0006\u0005\b\u0082\u0001\u00102J\u0012\u0010\u0083\u0001\u001a\u00020\u0003HÆ\u0003¢\u0006\u0005\b\u0084\u0001\u00102J\u0012\u0010\u0085\u0001\u001a\u00020\u0003HÆ\u0003¢\u0006\u0005\b\u0086\u0001\u00102J\u0012\u0010\u0087\u0001\u001a\u00020\u0003HÆ\u0003¢\u0006\u0005\b\u0088\u0001\u00102J\u0012\u0010\u0089\u0001\u001a\u00020\u0003HÆ\u0003¢\u0006\u0005\b\u008a\u0001\u00102J\u0012\u0010\u008b\u0001\u001a\u00020\u0003HÆ\u0003¢\u0006\u0005\b\u008c\u0001\u00102J\u0012\u0010\u008d\u0001\u001a\u00020\u0003HÆ\u0003¢\u0006\u0005\b\u008e\u0001\u00102J\u0012\u0010\u008f\u0001\u001a\u00020\u0003HÆ\u0003¢\u0006\u0005\b\u0090\u0001\u00102J\u0012\u0010\u0091\u0001\u001a\u00020\u0003HÆ\u0003¢\u0006\u0005\b\u0092\u0001\u00102J\u0012\u0010\u0093\u0001\u001a\u00020\u0003HÆ\u0003¢\u0006\u0005\b\u0094\u0001\u00102J\u0012\u0010\u0095\u0001\u001a\u00020\u0003HÆ\u0003¢\u0006\u0005\b\u0096\u0001\u00102J\u0012\u0010\u0097\u0001\u001a\u00020\u0003HÆ\u0003¢\u0006\u0005\b\u0098\u0001\u00102J\u0012\u0010\u0099\u0001\u001a\u00020\u0003HÆ\u0003¢\u0006\u0005\b\u009a\u0001\u00102J\u0012\u0010\u009b\u0001\u001a\u00020\u0003HÆ\u0003¢\u0006\u0005\b\u009c\u0001\u00102J\n\u0010\u009d\u0001\u001a\u00020.HÆ\u0003Jû\u0002\u0010\u009e\u0001\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u00122\b\b\u0002\u0010\u0013\u001a\u00020\u00142\b\b\u0002\u0010\u0015\u001a\u00020\u00162\b\b\u0002\u0010\u0017\u001a\u00020\u00032\b\b\u0002\u0010\u0018\u001a\u00020\u00032\b\b\u0002\u0010\u0019\u001a\u00020\u00032\b\b\u0002\u0010\u001a\u001a\u00020\u00032\b\b\u0002\u0010\u001b\u001a\u00020\u00032\b\b\u0002\u0010\u001c\u001a\u00020\u00032\b\b\u0002\u0010\u001d\u001a\u00020\u00032\b\b\u0002\u0010\u001e\u001a\u00020\u00032\b\b\u0002\u0010\u001f\u001a\u00020\u00032\b\b\u0002\u0010 \u001a\u00020\u00032\b\b\u0002\u0010!\u001a\u00020\u00032\b\b\u0002\u0010\"\u001a\u00020\u00032\b\b\u0002\u0010#\u001a\u00020\u00032\b\b\u0002\u0010$\u001a\u00020\u00032\b\b\u0002\u0010%\u001a\u00020\u00032\b\b\u0002\u0010&\u001a\u00020\u00032\b\b\u0002\u0010'\u001a\u00020\u00032\b\b\u0002\u0010(\u001a\u00020\u00032\b\b\u0002\u0010)\u001a\u00020\u00032\b\b\u0002\u0010*\u001a\u00020\u00032\b\b\u0002\u0010+\u001a\u00020\u00032\b\b\u0002\u0010,\u001a\u00020\u00032\b\b\u0002\u0010-\u001a\u00020.HÆ\u0001¢\u0006\u0006\b\u009f\u0001\u0010 \u0001J\u0015\u0010¡\u0001\u001a\u00020.2\t\u0010¢\u0001\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\u000b\u0010£\u0001\u001a\u00030¤\u0001HÖ\u0001J\u000b\u0010¥\u0001\u001a\u00030¦\u0001HÖ\u0001R\u0013\u0010\u0002\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\b1\u00102R\u0013\u0010\u0004\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\b4\u00102R\u0013\u0010\u0005\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\b5\u00102R\u0013\u0010\u0006\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\b6\u00102R\u0013\u0010\u0007\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\b7\u00102R\u0013\u0010\b\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\b8\u00102R\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b9\u0010:R\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b;\u0010<R\u0011\u0010\r\u001a\u00020\u000e¢\u0006\b\n\u0000\u001a\u0004\b=\u0010>R\u0011\u0010\u000f\u001a\u00020\u0010¢\u0006\b\n\u0000\u001a\u0004\b?\u0010@R\u0011\u0010\u0011\u001a\u00020\u0012¢\u0006\b\n\u0000\u001a\u0004\bA\u0010BR\u0011\u0010\u0013\u001a\u00020\u0014¢\u0006\b\n\u0000\u001a\u0004\bC\u0010DR\u0011\u0010\u0015\u001a\u00020\u0016¢\u0006\b\n\u0000\u001a\u0004\bE\u0010FR\u0013\u0010\u0017\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\bG\u00102R\u0013\u0010\u0018\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\bH\u00102R\u0013\u0010\u0019\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\bI\u00102R\u0013\u0010\u001a\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\bJ\u00102R\u0013\u0010\u001b\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\bK\u00102R\u0013\u0010\u001c\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\bL\u00102R\u0013\u0010\u001d\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\bM\u00102R\u0013\u0010\u001e\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\bN\u00102R\u0013\u0010\u001f\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\bO\u00102R\u0013\u0010 \u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\bP\u00102R\u0013\u0010!\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\bQ\u00102R\u0013\u0010\"\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\bR\u00102R\u0013\u0010#\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\bS\u00102R\u0013\u0010$\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\bT\u00102R\u0013\u0010%\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\bU\u00102R\u0013\u0010&\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\bV\u00102R\u0013\u0010'\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\bW\u00102R\u0013\u0010(\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\bX\u00102R\u0013\u0010)\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\bY\u00102R\u0013\u0010*\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\bZ\u00102R\u0013\u0010+\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\b[\u00102R\u0013\u0010,\u001a\u00020\u0003¢\u0006\n\n\u0002\u00103\u001a\u0004\b\\\u00102R\u0011\u0010-\u001a\u00020.¢\u0006\b\n\u0000\u001a\u0004\b-\u0010]¨\u0006§\u0001",
   d2 = {
         "Lio/intercom/android/sdk/ui/theme/IntercomColors;",
         "",
         "action",
         "Landroidx/compose/ui/graphics/Color;",
         "onAction",
         "actionContrastWhite",
         "onActionContrastWhite",
         "header",
         "onHeader",
         "base",
         "Lio/intercom/android/sdk/ui/theme/IntercomBaseColors;",
         "text",
         "Lio/intercom/android/sdk/ui/theme/IntercomTextColors;",
         "icon",
         "Lio/intercom/android/sdk/ui/theme/IntercomIconColors;",
         "container",
         "Lio/intercom/android/sdk/ui/theme/IntercomContainerColors;",
         "border",
         "Lio/intercom/android/sdk/ui/theme/IntercomBorderColors;",
         "syntax",
         "Lio/intercom/android/sdk/ui/theme/IntercomSyntaxColors;",
         "alpha",
         "Lio/intercom/android/sdk/ui/theme/IntercomAlphaColors;",
         "bubbleBackground",
         "adminBackground",
         "timestampBackground",
         "sourceBackground",
         "inputAlt",
         "poweredByBackgroundColor",
         "disabled",
         "composerMediaBackground",
         "composerMediaUploadingBackground",
         "composerMediaUploadingForeground",
         "composerMediaPlayBackground",
         "isTyping",
         "adminBorder",
         "collectorBorder",
         "specialNoticeBorder",
         "divider",
         "collectorSelected",
         "badge",
         "submitted",
         "active",
         "error",
         "shadow",
         "isLight",
         "",
         "<init>",
         "(JJJJJJLio/intercom/android/sdk/ui/theme/IntercomBaseColors;Lio/intercom/android/sdk/ui/theme/IntercomTextColors;Lio/intercom/android/sdk/ui/theme/IntercomIconColors;Lio/intercom/android/sdk/ui/theme/IntercomContainerColors;Lio/intercom/android/sdk/ui/theme/IntercomBorderColors;Lio/intercom/android/sdk/ui/theme/IntercomSyntaxColors;Lio/intercom/android/sdk/ui/theme/IntercomAlphaColors;JJJJJJJJJJJJJJJJJJJJJJZLkotlin/jvm/internal/DefaultConstructorMarker;)V",
         "getAction-0d7_KjU",
         "()J",
         "J",
         "getOnAction-0d7_KjU",
         "getActionContrastWhite-0d7_KjU",
         "getOnActionContrastWhite-0d7_KjU",
         "getHeader-0d7_KjU",
         "getOnHeader-0d7_KjU",
         "getBase",
         "()Lio/intercom/android/sdk/ui/theme/IntercomBaseColors;",
         "getText",
         "()Lio/intercom/android/sdk/ui/theme/IntercomTextColors;",
         "getIcon",
         "()Lio/intercom/android/sdk/ui/theme/IntercomIconColors;",
         "getContainer",
         "()Lio/intercom/android/sdk/ui/theme/IntercomContainerColors;",
         "getBorder",
         "()Lio/intercom/android/sdk/ui/theme/IntercomBorderColors;",
         "getSyntax",
         "()Lio/intercom/android/sdk/ui/theme/IntercomSyntaxColors;",
         "getAlpha",
         "()Lio/intercom/android/sdk/ui/theme/IntercomAlphaColors;",
         "getBubbleBackground-0d7_KjU",
         "getAdminBackground-0d7_KjU",
         "getTimestampBackground-0d7_KjU",
         "getSourceBackground-0d7_KjU",
         "getInputAlt-0d7_KjU",
         "getPoweredByBackgroundColor-0d7_KjU",
         "getDisabled-0d7_KjU",
         "getComposerMediaBackground-0d7_KjU",
         "getComposerMediaUploadingBackground-0d7_KjU",
         "getComposerMediaUploadingForeground-0d7_KjU",
         "getComposerMediaPlayBackground-0d7_KjU",
         "isTyping-0d7_KjU",
         "getAdminBorder-0d7_KjU",
         "getCollectorBorder-0d7_KjU",
         "getSpecialNoticeBorder-0d7_KjU",
         "getDivider-0d7_KjU",
         "getCollectorSelected-0d7_KjU",
         "getBadge-0d7_KjU",
         "getSubmitted-0d7_KjU",
         "getActive-0d7_KjU",
         "getError-0d7_KjU",
         "getShadow-0d7_KjU",
         "()Z",
         "component1",
         "component1-0d7_KjU",
         "component2",
         "component2-0d7_KjU",
         "component3",
         "component3-0d7_KjU",
         "component4",
         "component4-0d7_KjU",
         "component5",
         "component5-0d7_KjU",
         "component6",
         "component6-0d7_KjU",
         "component7",
         "component8",
         "component9",
         "component10",
         "component11",
         "component12",
         "component13",
         "component14",
         "component14-0d7_KjU",
         "component15",
         "component15-0d7_KjU",
         "component16",
         "component16-0d7_KjU",
         "component17",
         "component17-0d7_KjU",
         "component18",
         "component18-0d7_KjU",
         "component19",
         "component19-0d7_KjU",
         "component20",
         "component20-0d7_KjU",
         "component21",
         "component21-0d7_KjU",
         "component22",
         "component22-0d7_KjU",
         "component23",
         "component23-0d7_KjU",
         "component24",
         "component24-0d7_KjU",
         "component25",
         "component25-0d7_KjU",
         "component26",
         "component26-0d7_KjU",
         "component27",
         "component27-0d7_KjU",
         "component28",
         "component28-0d7_KjU",
         "component29",
         "component29-0d7_KjU",
         "component30",
         "component30-0d7_KjU",
         "component31",
         "component31-0d7_KjU",
         "component32",
         "component32-0d7_KjU",
         "component33",
         "component33-0d7_KjU",
         "component34",
         "component34-0d7_KjU",
         "component35",
         "component35-0d7_KjU",
         "component36",
         "copy",
         "copy-kz-0nr0",
         "(JJJJJJLio/intercom/android/sdk/ui/theme/IntercomBaseColors;Lio/intercom/android/sdk/ui/theme/IntercomTextColors;Lio/intercom/android/sdk/ui/theme/IntercomIconColors;Lio/intercom/android/sdk/ui/theme/IntercomContainerColors;Lio/intercom/android/sdk/ui/theme/IntercomBorderColors;Lio/intercom/android/sdk/ui/theme/IntercomSyntaxColors;Lio/intercom/android/sdk/ui/theme/IntercomAlphaColors;JJJJJJJJJJJJJJJJJJJJJJZ)Lio/intercom/android/sdk/ui/theme/IntercomColors;",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-ui_release"
   }
)
@Immutable
public final class IntercomColors {
   private final long action;
   private final long onAction;
   private final long actionContrastWhite;
   private final long onActionContrastWhite;
   private final long header;
   private final long onHeader;
   @NotNull
   private final IntercomBaseColors base;
   @NotNull
   private final IntercomTextColors text;
   @NotNull
   private final IntercomIconColors icon;
   @NotNull
   private final IntercomContainerColors container;
   @NotNull
   private final IntercomBorderColors border;
   @NotNull
   private final IntercomSyntaxColors syntax;
   @NotNull
   private final IntercomAlphaColors alpha;
   private final long bubbleBackground;
   private final long adminBackground;
   private final long timestampBackground;
   private final long sourceBackground;
   private final long inputAlt;
   private final long poweredByBackgroundColor;
   private final long disabled;
   private final long composerMediaBackground;
   private final long composerMediaUploadingBackground;
   private final long composerMediaUploadingForeground;
   private final long composerMediaPlayBackground;
   private final long isTyping;
   private final long adminBorder;
   private final long collectorBorder;
   private final long specialNoticeBorder;
   private final long divider;
   private final long collectorSelected;
   private final long badge;
   private final long submitted;
   private final long active;
   private final long error;
   private final long shadow;
   private final boolean isLight;
   public static int $stable;

   static {}

   private IntercomColors(
      long var1,
      long var3,
      long var5,
      long var7,
      long var9,
      long var11,
      IntercomBaseColors var13,
      IntercomTextColors var14,
      IntercomIconColors var15,
      IntercomContainerColors var16,
      IntercomBorderColors var17,
      IntercomSyntaxColors var18,
      IntercomAlphaColors var19,
      long var20,
      long var22,
      long var24,
      long var26,
      long var28,
      long var30,
      long var32,
      long var34,
      long var36,
      long var38,
      long var40,
      long var42,
      long var44,
      long var46,
      long var48,
      long var50,
      long var52,
      long var54,
      long var56,
      long var58,
      long var60,
      long var62,
      boolean var64
   ) {
      super();
      Intrinsics.checkNotNullParameter(var13, "base");
      Intrinsics.checkNotNullParameter(var14, "text");
      Intrinsics.checkNotNullParameter(var15, "icon");
      Intrinsics.checkNotNullParameter(var16, "container");
      Intrinsics.checkNotNullParameter(var17, "border");
      Intrinsics.checkNotNullParameter(var18, "syntax");
      Intrinsics.checkNotNullParameter(var19, "alpha");
      this.action = var1;
      this.onAction = var3;
      this.actionContrastWhite = var5;
      this.onActionContrastWhite = var7;
      this.header = var9;
      this.onHeader = var11;
      this.base = var13;
      this.text = var14;
      this.icon = var15;
      this.container = var16;
      this.border = var17;
      this.syntax = var18;
      this.alpha = var19;
      this.bubbleBackground = var20;
      this.adminBackground = var22;
      this.timestampBackground = var24;
      this.sourceBackground = var26;
      this.inputAlt = var28;
      this.poweredByBackgroundColor = var30;
      this.disabled = var32;
      this.composerMediaBackground = var34;
      this.composerMediaUploadingBackground = var36;
      this.composerMediaUploadingForeground = var38;
      this.composerMediaPlayBackground = var40;
      this.isTyping = var42;
      this.adminBorder = var44;
      this.collectorBorder = var46;
      this.specialNoticeBorder = var48;
      this.divider = var50;
      this.collectorSelected = var52;
      this.badge = var54;
      this.submitted = var56;
      this.active = var58;
      this.error = var60;
      this.shadow = var62;
      this.isLight = var64;
   }

   public final long recovered$202acdf3dbb5c020() {
      return this.action;
   }

   public final long recovered$8a5d9ad2993bccce() {
      return this.onAction;
   }

   public final long recovered$d5d3a0ddb8f4248d() {
      return this.actionContrastWhite;
   }

   public final long recovered$b32e6d5b37f98566() {
      return this.onActionContrastWhite;
   }

   public final long recovered$a4be33b4cee7fcc4() {
      return this.header;
   }

   public final long recovered$156c30ff4ea0f1e5() {
      return this.onHeader;
   }

   @NotNull
   public final IntercomBaseColors getBase() {
      return this.base;
   }

   @NotNull
   public final IntercomTextColors getText() {
      return this.text;
   }

   @NotNull
   public final IntercomIconColors getIcon() {
      return this.icon;
   }

   @NotNull
   public final IntercomContainerColors getContainer() {
      return this.container;
   }

   @NotNull
   public final IntercomBorderColors getBorder() {
      return this.border;
   }

   @NotNull
   public final IntercomSyntaxColors getSyntax() {
      return this.syntax;
   }

   @NotNull
   public final IntercomAlphaColors getAlpha() {
      return this.alpha;
   }

   public final long recovered$f6e7a825ec69c94c() {
      return this.bubbleBackground;
   }

   public final long recovered$61a1caf6b6549f74() {
      return this.adminBackground;
   }

   public final long recovered$4f0eb35b16b89a87() {
      return this.timestampBackground;
   }

   public final long recovered$a9608762563e9caa() {
      return this.sourceBackground;
   }

   public final long recovered$ad18c207ea721de5() {
      return this.inputAlt;
   }

   public final long recovered$c65d7201b2ad76fa() {
      return this.poweredByBackgroundColor;
   }

   public final long recovered$090a6e753d82e539() {
      return this.disabled;
   }

   public final long recovered$baf23fcaed2b2227() {
      return this.composerMediaBackground;
   }

   public final long recovered$1aad99ea8dc7d116() {
      return this.composerMediaUploadingBackground;
   }

   public final long recovered$2a20e1a7e3ec768f() {
      return this.composerMediaUploadingForeground;
   }

   public final long recovered$03091cedb7ddd0e0() {
      return this.composerMediaPlayBackground;
   }

   public final long recovered$4983b1a7f0ae0645() {
      return this.isTyping;
   }

   public final long recovered$8c831c6299defa27() {
      return this.adminBorder;
   }

   public final long recovered$a35e0f0c0a5b2483() {
      return this.collectorBorder;
   }

   public final long recovered$e610a85b5b67ed0e() {
      return this.specialNoticeBorder;
   }

   public final long recovered$9e30ba45ae499588() {
      return this.divider;
   }

   public final long recovered$e5be759ff86c80c5() {
      return this.collectorSelected;
   }

   public final long recovered$ff4dc34320f68c71() {
      return this.badge;
   }

   public final long recovered$f94228f772064095() {
      return this.submitted;
   }

   public final long recovered$b549188d2697e772() {
      return this.active;
   }

   public final long recovered$2bcfe565407cdfb8() {
      return this.error;
   }

   public final long recovered$f6e9b4ba3d2c0c59() {
      return this.shadow;
   }

   public final boolean isLight() {
      return this.isLight;
   }

   public final long recovered$2b43d556f0094889() {
      return this.action;
   }

   public final long recovered$306ddc2ccc425e26() {
      return this.onAction;
   }

   public final long recovered$46bfddf50387e286() {
      return this.actionContrastWhite;
   }

   public final long recovered$0e8bb1255e180941() {
      return this.onActionContrastWhite;
   }

   public final long recovered$6420fbad4c51c3dc() {
      return this.header;
   }

   public final long recovered$d686ca7219936040() {
      return this.onHeader;
   }

   @NotNull
   public final IntercomBaseColors component7() {
      return this.base;
   }

   @NotNull
   public final IntercomTextColors component8() {
      return this.text;
   }

   @NotNull
   public final IntercomIconColors component9() {
      return this.icon;
   }

   @NotNull
   public final IntercomContainerColors component10() {
      return this.container;
   }

   @NotNull
   public final IntercomBorderColors component11() {
      return this.border;
   }

   @NotNull
   public final IntercomSyntaxColors component12() {
      return this.syntax;
   }

   @NotNull
   public final IntercomAlphaColors component13() {
      return this.alpha;
   }

   public final long recovered$52323945801cdb3c() {
      return this.bubbleBackground;
   }

   public final long recovered$d2373b040a344642() {
      return this.adminBackground;
   }

   public final long recovered$8bdcafd355d283ce() {
      return this.timestampBackground;
   }

   public final long recovered$db93f6a9656f0f1a() {
      return this.sourceBackground;
   }

   public final long recovered$53c7297a64e7619a() {
      return this.inputAlt;
   }

   public final long recovered$f8e07295264d499c() {
      return this.poweredByBackgroundColor;
   }

   public final long recovered$b37baf675859f3ad() {
      return this.disabled;
   }

   public final long recovered$45cef7fd9eb0035a() {
      return this.composerMediaBackground;
   }

   public final long recovered$bc52edead62cf6cc() {
      return this.composerMediaUploadingBackground;
   }

   public final long recovered$a545b974dcb6ec24() {
      return this.composerMediaUploadingForeground;
   }

   public final long recovered$ea35eb188f9c3d26() {
      return this.composerMediaPlayBackground;
   }

   public final long recovered$41e2993051889176() {
      return this.isTyping;
   }

   public final long recovered$a81878abc63d1471() {
      return this.adminBorder;
   }

   public final long recovered$78d8a595299def1b() {
      return this.collectorBorder;
   }

   public final long recovered$fe3cddfd46f55ec1() {
      return this.specialNoticeBorder;
   }

   public final long recovered$ff08425fbf95bb2a() {
      return this.divider;
   }

   public final long recovered$764f1a4805e30ccb() {
      return this.collectorSelected;
   }

   public final long recovered$6dd1e897a3325700() {
      return this.badge;
   }

   public final long recovered$3c2d610a0cdc287d() {
      return this.submitted;
   }

   public final long recovered$d49bda44cb9584a5() {
      return this.active;
   }

   public final long recovered$4475a314a190cc3c() {
      return this.error;
   }

   public final long recovered$2536455c2b6eaa30() {
      return this.shadow;
   }

   public final boolean component36() {
      return this.isLight;
   }

   @NotNull
   public final IntercomColors recovered$6301be4c4b6e918d(
      long var1,
      long var3,
      long var5,
      long var7,
      long var9,
      long var11,
      @NotNull IntercomBaseColors var13,
      @NotNull IntercomTextColors var14,
      @NotNull IntercomIconColors var15,
      @NotNull IntercomContainerColors var16,
      @NotNull IntercomBorderColors var17,
      @NotNull IntercomSyntaxColors var18,
      @NotNull IntercomAlphaColors var19,
      long var20,
      long var22,
      long var24,
      long var26,
      long var28,
      long var30,
      long var32,
      long var34,
      long var36,
      long var38,
      long var40,
      long var42,
      long var44,
      long var46,
      long var48,
      long var50,
      long var52,
      long var54,
      long var56,
      long var58,
      long var60,
      long var62,
      boolean var64
   ) {
      Intrinsics.checkNotNullParameter(var13, "base");
      Intrinsics.checkNotNullParameter(var14, "text");
      Intrinsics.checkNotNullParameter(var15, "icon");
      Intrinsics.checkNotNullParameter(var16, "container");
      Intrinsics.checkNotNullParameter(var17, "border");
      Intrinsics.checkNotNullParameter(var18, "syntax");
      Intrinsics.checkNotNullParameter(var19, "alpha");
      return new IntercomColors(
         var1,
         var3,
         var5,
         var7,
         var9,
         var11,
         var13,
         var14,
         var15,
         var16,
         var17,
         var18,
         var19,
         var20,
         var22,
         var24,
         var26,
         var28,
         var30,
         var32,
         var34,
         var36,
         var38,
         var40,
         var42,
         var44,
         var46,
         var48,
         var50,
         var52,
         var54,
         var56,
         var58,
         var60,
         var62,
         var64,
         null
      );
   }

   // $VF: synthetic method
   public static IntercomColors recovered$2c5c5937d8c458df(
      IntercomColors var0,
      long var1,
      long var3,
      long var5,
      long var7,
      long var9,
      long var11,
      IntercomBaseColors var13,
      IntercomTextColors var14,
      IntercomIconColors var15,
      IntercomContainerColors var16,
      IntercomBorderColors var17,
      IntercomSyntaxColors var18,
      IntercomAlphaColors var19,
      long var20,
      long var22,
      long var24,
      long var26,
      long var28,
      long var30,
      long var32,
      long var34,
      long var36,
      long var38,
      long var40,
      long var42,
      long var44,
      long var46,
      long var48,
      long var50,
      long var52,
      long var54,
      long var56,
      long var58,
      long var60,
      long var62,
      boolean var64,
      int var65,
      int var66,
      Object var67
   ) {
      if ((var65 & 1) != 0) {
         var1 = var0.action;
      }

      if ((var65 & 2) != 0) {
         var3 = var0.onAction;
      }

      if ((var65 & 4) != 0) {
         var5 = var0.actionContrastWhite;
      }

      if ((var65 & 8) != 0) {
         var7 = var0.onActionContrastWhite;
      }

      if ((var65 & 16) != 0) {
         var9 = var0.header;
      }

      if ((var65 & 32) != 0) {
         var11 = var0.onHeader;
      }

      if ((var65 & 64) != 0) {
         var13 = var0.base;
      }

      if ((var65 & 128) != 0) {
         var14 = var0.text;
      }

      if ((var65 & 256) != 0) {
         var15 = var0.icon;
      }

      if ((var65 & 512) != 0) {
         var16 = var0.container;
      }

      if ((var65 & 1024) != 0) {
         var17 = var0.border;
      }

      if ((var65 & 2048) != 0) {
         var18 = var0.syntax;
      }

      if ((var65 & 4096) != 0) {
         var19 = var0.alpha;
      }

      if ((var65 & 8192) != 0) {
         var20 = var0.bubbleBackground;
      }

      if ((var65 & 16384) != 0) {
         var22 = var0.adminBackground;
      }

      if ((var65 & 32768) != 0) {
         var24 = var0.timestampBackground;
      }

      if ((var65 & 65536) != 0) {
         var26 = var0.sourceBackground;
      }

      if ((var65 & 131072) != 0) {
         var28 = var0.inputAlt;
      }

      if ((var65 & 262144) != 0) {
         var30 = var0.poweredByBackgroundColor;
      }

      if ((var65 & 524288) != 0) {
         var32 = var0.disabled;
      }

      if ((var65 & 1048576) != 0) {
         var34 = var0.composerMediaBackground;
      }

      if ((var65 & 2097152) != 0) {
         var36 = var0.composerMediaUploadingBackground;
      }

      if ((var65 & 4194304) != 0) {
         var38 = var0.composerMediaUploadingForeground;
      }

      if ((var65 & 8388608) != 0) {
         var40 = var0.composerMediaPlayBackground;
      }

      if ((var65 & 16777216) != 0) {
         var42 = var0.isTyping;
      }

      if ((var65 & 33554432) != 0) {
         var44 = var0.adminBorder;
      }

      if ((var65 & 67108864) != 0) {
         var46 = var0.collectorBorder;
      }

      if ((var65 & 134217728) != 0) {
         var48 = var0.specialNoticeBorder;
      }

      if ((var65 & 268435456) != 0) {
         var50 = var0.divider;
      }

      if ((var65 & 536870912) != 0) {
         var52 = var0.collectorSelected;
      }

      if ((var65 & 1073741824) != 0) {
         var54 = var0.badge;
      }

      if ((var65 & -2147483648) != 0) {
         var56 = var0.submitted;
      }

      if ((var66 & 1) != 0) {
         var58 = var0.active;
      }

      if ((var66 & 2) != 0) {
         var60 = var0.error;
      }

      if ((var66 & 4) != 0) {
         var62 = var0.shadow;
      }

      if ((var66 & 8) != 0) {
         var64 = var0.isLight;
      }

      return var0.recovered$6301be4c4b6e918d(
         var1,
         var3,
         var5,
         var7,
         var9,
         var11,
         var13,
         var14,
         var15,
         var16,
         var17,
         var18,
         var19,
         var20,
         var22,
         var24,
         var26,
         var28,
         var30,
         var32,
         var34,
         var36,
         var38,
         var40,
         var42,
         var44,
         var46,
         var48,
         var50,
         var52,
         var54,
         var56,
         var58,
         var60,
         var62,
         var64
      );
   }

   @NotNull
   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("IntercomColors(action=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.action))
         .append(", onAction=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.onAction))
         .append(", actionContrastWhite=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.actionContrastWhite))
         .append(", onActionContrastWhite=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.onActionContrastWhite))
         .append(", header=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.header))
         .append(", onHeader=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.onHeader))
         .append(", base=")
         .append(this.base)
         .append(", text=")
         .append(this.text)
         .append(", icon=")
         .append(this.icon)
         .append(", container=")
         .append(this.container)
         .append(", border=")
         .append(this.border)
         .append(", syntax=");
      var1.append(this.syntax)
         .append(", alpha=")
         .append(this.alpha)
         .append(", bubbleBackground=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.bubbleBackground))
         .append(", adminBackground=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.adminBackground))
         .append(", timestampBackground=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.timestampBackground))
         .append(", sourceBackground=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.sourceBackground))
         .append(", inputAlt=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.inputAlt))
         .append(", poweredByBackgroundColor=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.poweredByBackgroundColor))
         .append(", disabled=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.disabled))
         .append(", composerMediaBackground=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.composerMediaBackground))
         .append(", composerMediaUploadingBackground=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.composerMediaUploadingBackground))
         .append(", composerMediaUploadingForeground=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.composerMediaUploadingForeground));
      var1.append(", composerMediaPlayBackground=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.composerMediaPlayBackground))
         .append(", isTyping=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.isTyping))
         .append(", adminBorder=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.adminBorder))
         .append(", collectorBorder=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.collectorBorder))
         .append(", specialNoticeBorder=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.specialNoticeBorder))
         .append(", divider=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.divider))
         .append(", collectorSelected=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.collectorSelected))
         .append(", badge=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.badge))
         .append(", submitted=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.submitted))
         .append(", active=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.active))
         .append(", error=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.error))
         .append(", shadow=");
      var1.append((Object)Color.recovered$cfef63bb1d111f9f(this.shadow)).append(", isLight=").append(this.isLight).append(')');
      return var1.toString();
   }

   @Override
   public int hashCode() {
      int var1 = Color.recovered$87e060624084b26b(this.action);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.onAction);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.actionContrastWhite);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.onActionContrastWhite);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.header);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.onHeader);
      var1 = var1 * 31 + this.base.hashCode();
      var1 = var1 * 31 + this.text.hashCode();
      var1 = var1 * 31 + this.icon.hashCode();
      var1 = var1 * 31 + this.container.hashCode();
      var1 = var1 * 31 + this.border.hashCode();
      var1 = var1 * 31 + this.syntax.hashCode();
      var1 = var1 * 31 + this.alpha.hashCode();
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.bubbleBackground);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.adminBackground);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.timestampBackground);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.sourceBackground);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.inputAlt);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.poweredByBackgroundColor);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.disabled);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.composerMediaBackground);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.composerMediaUploadingBackground);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.composerMediaUploadingForeground);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.composerMediaPlayBackground);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.isTyping);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.adminBorder);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.collectorBorder);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.specialNoticeBorder);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.divider);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.collectorSelected);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.badge);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.submitted);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.active);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.error);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.shadow);
      return var1 * 31 + Boolean.hashCode(this.isLight);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof IntercomColors)) {
         return false;
      } else {
         IntercomColors var2 = (IntercomColors)var1;
         if (!Color.recovered$9eab66ea8c46e11c(this.action, var2.action)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.onAction, var2.onAction)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.actionContrastWhite, var2.actionContrastWhite)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.onActionContrastWhite, var2.onActionContrastWhite)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.header, var2.header)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.onHeader, var2.onHeader)) {
            return false;
         } else if (!Intrinsics.areEqual(this.base, var2.base)) {
            return false;
         } else if (!Intrinsics.areEqual(this.text, var2.text)) {
            return false;
         } else if (!Intrinsics.areEqual(this.icon, var2.icon)) {
            return false;
         } else if (!Intrinsics.areEqual(this.container, var2.container)) {
            return false;
         } else if (!Intrinsics.areEqual(this.border, var2.border)) {
            return false;
         } else if (!Intrinsics.areEqual(this.syntax, var2.syntax)) {
            return false;
         } else if (!Intrinsics.areEqual(this.alpha, var2.alpha)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.bubbleBackground, var2.bubbleBackground)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.adminBackground, var2.adminBackground)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.timestampBackground, var2.timestampBackground)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.sourceBackground, var2.sourceBackground)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.inputAlt, var2.inputAlt)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.poweredByBackgroundColor, var2.poweredByBackgroundColor)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.disabled, var2.disabled)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.composerMediaBackground, var2.composerMediaBackground)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.composerMediaUploadingBackground, var2.composerMediaUploadingBackground)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.composerMediaUploadingForeground, var2.composerMediaUploadingForeground)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.composerMediaPlayBackground, var2.composerMediaPlayBackground)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.isTyping, var2.isTyping)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.adminBorder, var2.adminBorder)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.collectorBorder, var2.collectorBorder)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.specialNoticeBorder, var2.specialNoticeBorder)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.divider, var2.divider)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.collectorSelected, var2.collectorSelected)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.badge, var2.badge)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.submitted, var2.submitted)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.active, var2.active)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.error, var2.error)) {
            return false;
         } else {
            return !Color.recovered$9eab66ea8c46e11c(this.shadow, var2.shadow) ? false : this.isLight == var2.isLight;
         }
      }
   }

   // $VF: synthetic method
   public IntercomColors(
      long var1,
      long var3,
      long var5,
      long var7,
      long var9,
      long var11,
      IntercomBaseColors var13,
      IntercomTextColors var14,
      IntercomIconColors var15,
      IntercomContainerColors var16,
      IntercomBorderColors var17,
      IntercomSyntaxColors var18,
      IntercomAlphaColors var19,
      long var20,
      long var22,
      long var24,
      long var26,
      long var28,
      long var30,
      long var32,
      long var34,
      long var36,
      long var38,
      long var40,
      long var42,
      long var44,
      long var46,
      long var48,
      long var50,
      long var52,
      long var54,
      long var56,
      long var58,
      long var60,
      long var62,
      boolean var64,
      DefaultConstructorMarker var65
   ) {
      this(
         var1,
         var3,
         var5,
         var7,
         var9,
         var11,
         var13,
         var14,
         var15,
         var16,
         var17,
         var18,
         var19,
         var20,
         var22,
         var24,
         var26,
         var28,
         var30,
         var32,
         var34,
         var36,
         var38,
         var40,
         var42,
         var44,
         var46,
         var48,
         var50,
         var52,
         var54,
         var56,
         var58,
         var60,
         var62,
         var64
      );
   }
}
