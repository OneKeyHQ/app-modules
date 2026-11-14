package io.intercom.android.sdk.m5.conversation.ui.components.row;

import android.content.Context;
import androidx.compose.foundation.BorderStrokeKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.foundation.shape.CornerBasedShape;
import androidx.compose.foundation.shape.CornerSizeKt;
import androidx.compose.foundation.shape.RoundedCornerShape;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material3.ButtonDefaults;
import androidx.compose.material3.ButtonKt;
import androidx.compose.material3.ColorSchemeKt;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.SurfaceKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.ProvidedValue;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.text.AnnotatedString;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.AnnotatedString.Builder;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.LayoutDirection;
import androidx.core.text.HtmlCompat;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.blocks.lib.models.BlockAttachment;
import io.intercom.android.sdk.blocks.lib.models.BlockAttachment$Companion;
import io.intercom.android.sdk.blocks.lib.models.TicketType;
import io.intercom.android.sdk.m5.components.avatar.AvatarShape;
import io.intercom.android.sdk.m5.conversation.states.AttributeData;
import io.intercom.android.sdk.m5.conversation.states.GroupingPosition;
import io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData;
import io.intercom.android.sdk.m5.conversation.states.SenderMetadata;
import io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt;
import io.intercom.android.sdk.m5.preview.data.DownloadState;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewArgs;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$NetworkFile;
import io.intercom.android.sdk.m5.preview.ui.IntercomPreviewActivity;
import io.intercom.android.sdk.m5.preview.ui.IntercomPreviewActivity$Companion;
import io.intercom.android.sdk.models.Attachments;
import io.intercom.android.sdk.models.Avatar$Builder;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.Part$MessageState;
import io.intercom.android.sdk.survey.block.BlockRenderData;
import io.intercom.android.sdk.survey.block.BlockRenderTextStyle;
import io.intercom.android.sdk.survey.block.BlockViewKt;
import io.intercom.android.sdk.survey.block.ImageRenderType;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.extension.ContentTypeExtensionKt;
import io.intercom.android.sdk.ui.theme.IntercomColors;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.views.compose.AttributeCollectorCardKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000â\u0001\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u001d\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0018\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u000b\u001aÁ\u0001\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\r2\u0014\b\u0002\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00010\u000f2\u000e\b\u0002\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00122\u000e\b\u0002\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00122\u0014\b\u0002\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u00010\u000f2\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u00152\n\b\u0002\u0010\u0017\u001a\u0004\u0018\u00010\u00182\u0014\b\u0002\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u001a\u0012\u0004\u0012\u00020\u00010\u000fH\u0001¢\u0006\u0002\u0010\u001b\u001a\u0097\u0001\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001e0\u00122\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\u00072\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\"\u001a\u00020#2\u0012\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u001a\u0012\u0004\u0012\u00020\u00010\u000f2\b\u0010\u0016\u001a\u0004\u0018\u00010\u00152\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u00010\u000f2\f\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00010%2\b\b\u0002\u0010&\u001a\u00020\u0007H\u0003¢\u0006\u0004\b'\u0010(\u001a?\u0010)\u001a\u00020 2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010*\u001a\u00020\u00072\u0006\u0010+\u001a\u00020#2\u0006\u0010,\u001a\u00020#2\u0006\u0010-\u001a\u00020#H\u0002¢\u0006\u0004\b.\u0010/\u001a\f\u00105\u001a\u00020\u0007*\u00020\u0005H\u0002\u001a\f\u00106\u001a\u00020\u0007*\u00020\u0005H\u0002\u001a3\u00107\u001a\u0002082\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u00109\u001a\u0002012\b\b\u0002\u0010:\u001a\u000201H\u0002¢\u0006\u0004\b;\u0010<\u001a+\u0010=\u001a\u0002082\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u00109\u001a\u0002012\b\b\u0002\u0010:\u001a\u000201H\u0000¢\u0006\u0004\b>\u0010?\u001a+\u0010@\u001a\u0002082\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u00109\u001a\u0002012\b\b\u0002\u0010:\u001a\u000201H\u0002¢\u0006\u0004\bA\u0010?\u001a\u0017\u0010B\u001a\u00020\u00012\b\b\u0002\u0010\b\u001a\u00020\tH\u0003¢\u0006\u0002\u0010C\u001a\u0093\u0001\u0010D\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\"\u001a\u00020#2\u0006\u0010E\u001a\u00020\u00072\u0006\u0010F\u001a\u00020G2\f\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00010%2\u0012\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u001a\u0012\u0004\u0012\u00020\u00010\u000f2\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\u0016\u001a\u0004\u0018\u00010\u00152\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u00010\u000f2\b\b\u0002\u0010H\u001a\u0002012\u0010\b\u0002\u0010I\u001a\n\u0012\u0004\u0012\u00020\u001e\u0018\u00010\u0012H\u0001¢\u0006\u0004\bJ\u0010K\u001a\u0099\u0001\u0010L\u001a\u00020\u00012\f\u0010M\u001a\b\u0012\u0004\u0012\u00020\u001e0\u00122\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\"\u001a\u00020#2\u0006\u0010F\u001a\u00020G2\u0006\u0010E\u001a\u00020\u00072\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\u0016\u001a\u0004\u0018\u00010\u00152\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u00010\u000f2\f\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00010%2\u0012\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u001a\u0012\u0004\u0012\u00020\u00010\u000f2\b\b\u0002\u0010H\u001a\u0002012\b\b\u0002\u0010\b\u001a\u00020\tH\u0003¢\u0006\u0004\bN\u0010O\u001a\u0081\u0001\u0010P\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\u00072\u0006\u0010Q\u001a\u00020\u00072\u0006\u0010\u0006\u001a\u00020\u00072\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00122\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00010\u000f2\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010R\u001a\u00020\u0007H\u0003¢\u0006\u0002\u0010S\u001aO\u0010T\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00122\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00010\u000f2\b\b\u0002\u0010\b\u001a\u00020\tH\u0003¢\u0006\u0002\u0010U\u001a\r\u0010V\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010W\u001a\r\u0010X\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010W\u001a\r\u0010Y\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010W\u001a\r\u0010Z\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010W\u001a\r\u0010[\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010W\u001a\r\u0010\\\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010W\u001a\r\u0010]\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010W\u001a\r\u0010^\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010W\u001a\r\u0010_\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010W\u001a\r\u0010`\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010W\u001a\r\u0010a\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010W\u001a\r\u0010b\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010W\u001a\r\u0010c\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010W\u001a/\u0010g\u001a\u00020\u00012\b\b\u0002\u0010\b\u001a\u00020\t2\u0006\u0010h\u001a\u00020\u000b2\u0006\u0010i\u001a\u00020\u000b2\u0006\u0010\u0006\u001a\u00020\u0007H\u0001¢\u0006\u0002\u0010j\u001a\f\u0010k\u001a\u00020l*\u00020\u0003H\u0000\u001a\u0010\u0010m\u001a\u00020\u00072\u0006\u0010\u0002\u001a\u00020\u0003H\u0002\u001a\u001a\u0010n\u001a\u00020o*\b\u0012\u0004\u0012\u00020\u001e0\u00122\u0006\u0010\u0006\u001a\u00020\u0007H\u0002\u001a\u0010\u0010p\u001a\u00020\u001e2\u0006\u0010q\u001a\u00020\u000bH\u0002\u001a\u0016\u0010r\u001a\u00020\u001e2\f\u0010s\u001a\b\u0012\u0004\u0012\u00020t0\u0012H\u0002\u001a \u0010u\u001a\u0004\u0018\u00010\u001e2\u0006\u0010v\u001a\u00020\u001e2\f\u0010w\u001a\b\u0012\u0004\u0012\u00020\u001e0xH\u0002\u001a\u001a\u0010y\u001a\b\u0012\u0004\u0012\u00020\u001e0\u0012*\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u0002\u001a\u0014\u0010z\u001a\u00020{*\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u0002\u001a\u0015\u0010|\u001a\u00020}2\u0006\u0010E\u001a\u00020\u0007H\u0001¢\u0006\u0002\u0010~\u001a*\u0010\u009d\u0001\u001a\u0002082\u0006\u0010!\u001a\u00020\u00072\u0006\u0010Q\u001a\u00020\u00072\u0006\u0010\u0006\u001a\u00020\u00072\u0007\u0010\u009e\u0001\u001a\u000208H\u0000\u001aO\u0010\u009f\u0001\u001a\u00020\u0001*\u00030 \u00012\u0006\u0010q\u001a\u00020\u000b2\u0007\u0010¡\u0001\u001a\u00020\u000b2\n\b\u0002\u0010¢\u0001\u001a\u00030£\u00012\n\b\u0002\u0010¤\u0001\u001a\u00030£\u00012\n\b\u0002\u0010¥\u0001\u001a\u00030£\u00012\n\b\u0002\u0010¦\u0001\u001a\u00030£\u0001H\u0002\u001a\r\u0010§\u0001\u001a\u00020\u0007*\u00020\u0003H\u0000\u001a9\u0010¨\u0001\u001a\u0002082\u0007\u0010©\u0001\u001a\u0002082\b\u0010ª\u0001\u001a\u00030£\u00012\b\u0010«\u0001\u001a\u00030£\u00012\b\b\u0002\u0010:\u001a\u000201H\u0000¢\u0006\u0006\b¬\u0001\u0010\u00ad\u0001\"\u0010\u00100\u001a\u000201X\u0082\u0004¢\u0006\u0004\n\u0002\u00102\"\u0010\u00103\u001a\u000201X\u0082\u0004¢\u0006\u0004\n\u0002\u00102\"\u0010\u00104\u001a\u000201X\u0082\u0004¢\u0006\u0004\n\u0002\u00102\"\u000e\u0010d\u001a\u00020eX\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010f\u001a\u00020eX\u0082\u0004¢\u0006\u0002\n\u0000\"$\u0010\u007f\u001a\r \u0081\u0001*\u0005\u0018\u00010\u0080\u00010\u0080\u0001X\u0080\u0004¢\u0006\r\n\u0003\u0010\u0084\u0001\u001a\u0006\b\u0082\u0001\u0010\u0083\u0001\"%\u0010\u0085\u0001\u001a\r \u0081\u0001*\u0005\u0018\u00010\u0080\u00010\u0080\u0001X\u0080\u0004¢\u0006\r\n\u0003\u0010\u0084\u0001\u001a\u0006\b\u0086\u0001\u0010\u0083\u0001\"%\u0010\u0087\u0001\u001a\r \u0081\u0001*\u0005\u0018\u00010\u0080\u00010\u0080\u0001X\u0080\u0004¢\u0006\r\n\u0003\u0010\u0084\u0001\u001a\u0006\b\u0088\u0001\u0010\u0083\u0001\"%\u0010\u0089\u0001\u001a\r \u0081\u0001*\u0005\u0018\u00010\u0080\u00010\u0080\u0001X\u0080\u0004¢\u0006\r\n\u0003\u0010\u0084\u0001\u001a\u0006\b\u008a\u0001\u0010\u0083\u0001\"%\u0010\u008b\u0001\u001a\r \u0081\u0001*\u0005\u0018\u00010\u0080\u00010\u0080\u0001X\u0080\u0004¢\u0006\r\n\u0003\u0010\u0084\u0001\u001a\u0006\b\u008c\u0001\u0010\u0083\u0001\"\u0017\u0010\u008d\u0001\u001a\u00020eX\u0080\u0004¢\u0006\n\n\u0000\u001a\u0006\b\u008e\u0001\u0010\u008f\u0001\"%\u0010\u0090\u0001\u001a\r \u0081\u0001*\u0005\u0018\u00010\u0080\u00010\u0080\u0001X\u0080\u0004¢\u0006\r\n\u0003\u0010\u0084\u0001\u001a\u0006\b\u0091\u0001\u0010\u0083\u0001\"%\u0010\u0092\u0001\u001a\r \u0081\u0001*\u0005\u0018\u00010\u0080\u00010\u0080\u0001X\u0080\u0004¢\u0006\r\n\u0003\u0010\u0084\u0001\u001a\u0006\b\u0093\u0001\u0010\u0083\u0001\"%\u0010\u0094\u0001\u001a\r \u0081\u0001*\u0005\u0018\u00010\u0080\u00010\u0080\u0001X\u0080\u0004¢\u0006\r\n\u0003\u0010\u0084\u0001\u001a\u0006\b\u0095\u0001\u0010\u0083\u0001\"%\u0010\u0096\u0001\u001a\r \u0081\u0001*\u0005\u0018\u00010\u0080\u00010\u0080\u0001X\u0080\u0004¢\u0006\r\n\u0003\u0010\u0084\u0001\u001a\u0006\b\u0097\u0001\u0010\u0083\u0001\"\u0017\u0010\u0098\u0001\u001a\n\u0012\u0005\u0012\u00030\u009a\u00010\u0099\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u0017\u0010\u009b\u0001\u001a\n\u0012\u0005\u0012\u00030\u009a\u00010\u0099\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u0017\u0010\u009c\u0001\u001a\n\u0012\u0005\u0012\u00030\u009a\u00010\u0099\u0001X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006®\u0001",
   d2 = {
         "BubbleMessageRow",
         "",
         "conversationPart",
         "Lio/intercom/android/sdk/models/Part;",
         "groupingPosition",
         "Lio/intercom/android/sdk/m5/conversation/states/GroupingPosition;",
         "isAdminOrAltParticipant",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "timestamp",
         "",
         "senderMetadata",
         "Lio/intercom/android/sdk/m5/conversation/states/SenderMetadata;",
         "onSubmitAttribute",
         "Lkotlin/Function1;",
         "Lio/intercom/android/sdk/m5/conversation/states/AttributeData;",
         "failedAttributeIdentifiers",
         "",
         "loadingAttributeIdentifiers",
         "onRetryMediaClicked",
         "Lio/intercom/android/sdk/m5/conversation/states/PendingMessage$FailedMediaUploadData;",
         "failedMediaUploadData",
         "failedMessage",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/FailedMessage;",
         "onCreateTicket",
         "Lio/intercom/android/sdk/blocks/lib/models/TicketType;",
         "(Lio/intercom/android/sdk/models/Part;Lio/intercom/android/sdk/m5/conversation/states/GroupingPosition;ZLandroidx/compose/ui/Modifier;Ljava/lang/String;Lio/intercom/android/sdk/m5/conversation/states/SenderMetadata;Lkotlin/jvm/functions/Function1;Ljava/util/List;Ljava/util/List;Lkotlin/jvm/functions/Function1;Lio/intercom/android/sdk/m5/conversation/states/PendingMessage$FailedMediaUploadData;Lio/intercom/android/sdk/m5/conversation/ui/components/row/FailedMessage;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;III)V",
         "SegmentedBubbleContent",
         "contentBlocks",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "messageStyle",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/MessageStyle;",
         "hasContentAbove",
         "contentColor",
         "Landroidx/compose/ui/graphics/Color;",
         "onClick",
         "Lkotlin/Function0;",
         "showMetadata",
         "SegmentedBubbleContent-E1AOrGg",
         "(Lio/intercom/android/sdk/models/Part;Lio/intercom/android/sdk/m5/conversation/states/GroupingPosition;Ljava/util/List;Lio/intercom/android/sdk/m5/conversation/ui/components/row/MessageStyle;ZZJLkotlin/jvm/functions/Function1;Lio/intercom/android/sdk/m5/conversation/states/PendingMessage$FailedMediaUploadData;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;ZLandroidx/compose/runtime/Composer;III)V",
         "getMessageStyle",
         "isFailed",
         "adminBackground",
         "adminBorder",
         "actionColor",
         "getMessageStyle-15-ZiRo",
         "(ZLio/intercom/android/sdk/m5/conversation/states/GroupingPosition;ZJJJ)Lio/intercom/android/sdk/m5/conversation/ui/components/row/MessageStyle;",
         "DEFAULT_CORNER_RADIUS",
         "Landroidx/compose/ui/unit/Dp;",
         "F",
         "GROUPED_CORNER_RADIUS",
         "ADMIN_BUBBLE_END_GUTTER",
         "shouldGroupTop",
         "shouldGroupBottom",
         "getCornerShape",
         "Landroidx/compose/foundation/shape/RoundedCornerShape;",
         "defaultCornerRadius",
         "groupedCornerRadius",
         "getCornerShape-4j6BHR0",
         "(Lio/intercom/android/sdk/m5/conversation/states/GroupingPosition;ZFF)Landroidx/compose/foundation/shape/RoundedCornerShape;",
         "getEndUserCornerShape",
         "getEndUserCornerShape-VpY3zN4",
         "(Lio/intercom/android/sdk/m5/conversation/states/GroupingPosition;FF)Landroidx/compose/foundation/shape/RoundedCornerShape;",
         "getAdminCornerShape",
         "getAdminCornerShape-VpY3zN4",
         "FailedMessageIcon",
         "(Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "MessageContent",
         "enabled",
         "contentShape",
         "Landroidx/compose/ui/graphics/Shape;",
         "spaceBetweenBlocks",
         "blocksToRender",
         "MessageContent-hcemMNk",
         "(Lio/intercom/android/sdk/models/Part;JZLandroidx/compose/ui/graphics/Shape;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;ZLio/intercom/android/sdk/m5/conversation/states/PendingMessage$FailedMediaUploadData;Lkotlin/jvm/functions/Function1;FLjava/util/List;Landroidx/compose/runtime/Composer;III)V",
         "MessageBlocks",
         "blocks",
         "MessageBlocks-5WxuK6Y",
         "(Ljava/util/List;Lio/intercom/android/sdk/models/Part;JLandroidx/compose/ui/graphics/Shape;ZZLio/intercom/android/sdk/m5/conversation/states/PendingMessage$FailedMediaUploadData;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;FLandroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;III)V",
         "AttributeCollectorBubble",
         "hasContentBelow",
         "suppressAvatarHeader",
         "(Lio/intercom/android/sdk/models/Part;Lio/intercom/android/sdk/m5/conversation/states/GroupingPosition;Lio/intercom/android/sdk/m5/conversation/ui/components/row/MessageStyle;ZZZLjava/util/List;Ljava/util/List;Lkotlin/jvm/functions/Function1;Landroidx/compose/ui/Modifier;ZLandroidx/compose/runtime/Composer;III)V",
         "PartAttributeCollectorCard",
         "(Lio/intercom/android/sdk/models/Part;Ljava/util/List;Ljava/util/List;Lkotlin/jvm/functions/Function1;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "EndUserMessageRowPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "EndUserMessageRowWithAttachmentsPreview",
         "AdminMessageRowPreview",
         "StreamingSenderTimestampBubbleMessageRowRtlPreview",
         "StreamingSenderTimestampBubbleMessageRowPreview",
         "SegmentedAdminMessageRowPreview",
         "SegmentedAdminMessageRowCodeLeadingPreview",
         "SegmentedAdminMessageRowCodeNoMetadataPreview",
         "SegmentedAdminMessageRowTextOnlyPreview",
         "SegmentedAttributeCollectorPreview",
         "SegmentedAttributeCollectorWithCodePreview",
         "AttributeCollectorNoBlocksPreview",
         "SegmentedAttributeCollectorNoBlocksPreview",
         "botPreviewMetadata",
         "Lio/intercom/android/sdk/models/Metadata;",
         "humanPreviewMetadata",
         "MessageMeta",
         "metaString",
         "attributeString",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;Ljava/lang/String;ZLandroidx/compose/runtime/Composer;II)V",
         "getCopyText",
         "Landroidx/compose/ui/text/AnnotatedString;",
         "isCreateTicketEnabled",
         "categorizeBlocks",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/CategorizedBlocks;",
         "createVideoBlock",
         "url",
         "createAttachmentListBlock",
         "attachments",
         "Lio/intercom/android/sdk/models/Attachments;",
         "removeVideoAttachmentsFromBlock",
         "block",
         "videoBlocks",
         "",
         "convertAttachmentsToBlocks",
         "extractAndCleanAttachmentBlocks",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/ProcessedBlocks;",
         "contentAlpha",
         "",
         "(ZLandroidx/compose/runtime/Composer;I)F",
         "paragraphBlock",
         "Lio/intercom/android/sdk/blocks/lib/models/Block$Builder;",
         "kotlin.jvm.PlatformType",
         "getParagraphBlock",
         "()Lio/intercom/android/sdk/blocks/lib/models/Block$Builder;",
         "Lio/intercom/android/sdk/blocks/lib/models/Block$Builder;",
         "longParagraphBlock",
         "getLongParagraphBlock",
         "createTicketBlock",
         "getCreateTicketBlock",
         "answerBlock",
         "getAnswerBlock",
         "articleBlock",
         "getArticleBlock",
         "humanMetadata",
         "getHumanMetadata",
         "()Lio/intercom/android/sdk/models/Metadata;",
         "codeBlock",
         "getCodeBlock",
         "imageBlock",
         "getImageBlock",
         "attachmentBlock",
         "getAttachmentBlock",
         "videoBlock",
         "getVideoBlock",
         "imageBlockTypes",
         "",
         "Lio/intercom/android/sdk/blocks/lib/BlockType;",
         "attachmentBlockTypes",
         "videoBlockTypes",
         "calculateGroupedCornerShape",
         "baseShape",
         "launchPreviewActivity",
         "Landroid/content/Context;",
         "mimeType",
         "fileSavedTextRes",
         "",
         "fileSavingTextRes",
         "saveFailedTextRes",
         "permissionDeniedTextRes",
         "shouldShowAttribution",
         "getGroupedItemCornerShape",
         "cornerShape",
         "index",
         "totalItems",
         "getGroupedItemCornerShape-eqLRuRQ",
         "(Landroidx/compose/foundation/shape/RoundedCornerShape;IIF)Landroidx/compose/foundation/shape/RoundedCornerShape;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nBubbleMessageRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BubbleMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/BubbleMessageRowKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 5 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 6 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 7 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 8 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 9 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 10 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 11 AnnotatedString.kt\nandroidx/compose/ui/text/AnnotatedStringKt\n+ 12 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1611:1\n1047#2,6:1612\n1047#2,6:1618\n1047#2,6:1624\n1047#2,6:1631\n1047#2,6:1637\n1047#2,3:1643\n1050#2,3:1654\n1047#2,6:1658\n1047#2,6:1815\n75#3:1630\n75#3:1657\n1563#4:1646\n1634#4,3:1647\n1563#4:1650\n1634#4,3:1651\n1878#4,3:1697\n1869#4:1781\n1870#4:1825\n1761#4,3:1869\n774#4:1872\n865#4,2:1873\n774#4:1875\n865#4,2:1876\n774#4:1878\n865#4,2:1879\n827#4:1881\n855#4,2:1882\n1563#4:1884\n1634#4,3:1885\n3307#4,10:1888\n1869#4,2:1898\n1563#4:1900\n1634#4,3:1901\n1617#4,9:1904\n1869#4:1913\n1870#4:1915\n1626#4:1916\n87#5:1664\n83#5,10:1665\n94#5:1703\n87#5:1712\n83#5,10:1713\n94#5:1748\n87#5:1750\n85#5,8:1751\n94#5:1829\n81#6,6:1675\n88#6,6:1690\n96#6:1702\n81#6,6:1723\n88#6,6:1738\n96#6:1747\n81#6,6:1759\n88#6,6:1774\n81#6,6:1793\n88#6,6:1808\n96#6:1823\n96#6:1828\n81#6,6:1839\n88#6,6:1854\n96#6:1865\n402#7,9:1681\n411#7:1696\n412#7,2:1700\n402#7,9:1729\n411#7,3:1744\n402#7,9:1765\n411#7:1780\n402#7,9:1799\n411#7:1814\n412#7,2:1821\n412#7,2:1826\n402#7,9:1845\n411#7:1860\n412#7,2:1863\n118#8:1704\n118#8:1705\n118#8:1706\n118#8:1707\n118#8:1708\n118#8:1709\n118#8:1710\n118#8:1711\n118#8:1749\n118#8:1861\n118#8:1862\n118#8:1917\n118#8:1918\n118#8:1919\n70#9:1782\n66#9,10:1783\n77#9:1824\n99#10:1830\n97#10,8:1831\n106#10:1866\n1580#11:1867\n1#12:1868\n1#12:1914\n*S KotlinDebug\n*F\n+ 1 BubbleMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/BubbleMessageRowKt\n*L\n91#1:1612,6\n94#1:1618,6\n97#1:1624,6\n101#1:1631,6\n114#1:1637,6\n123#1:1643,3\n123#1:1654,3\n281#1:1658,6\n608#1:1815,6\n99#1:1630\n147#1:1657\n124#1:1646\n124#1:1647,3\n125#1:1650\n125#1:1651,3\n327#1:1697,3\n585#1:1781\n585#1:1825\n1307#1:1869,3\n1325#1:1872\n1325#1:1873,2\n1326#1:1875\n1326#1:1876,2\n1327#1:1878\n1327#1:1879,2\n1328#1:1881\n1328#1:1882,2\n1346#1:1884\n1346#1:1885,3\n1363#1:1888,10\n1365#1:1898,2\n1382#1:1900\n1382#1:1901,3\n1407#1:1904,9\n1407#1:1913\n1407#1:1915\n1407#1:1916\n302#1:1664\n302#1:1665,10\n302#1:1703\n541#1:1712\n541#1:1713,10\n541#1:1748\n581#1:1750\n581#1:1751,8\n581#1:1829\n302#1:1675,6\n302#1:1690,6\n302#1:1702\n541#1:1723,6\n541#1:1738,6\n541#1:1747\n581#1:1759,6\n581#1:1774,6\n586#1:1793,6\n586#1:1808,6\n586#1:1823\n581#1:1828\n1238#1:1839,6\n1238#1:1854,6\n1238#1:1865\n302#1:1681,9\n302#1:1696\n302#1:1700,2\n541#1:1729,9\n541#1:1744,3\n581#1:1765,9\n581#1:1780\n586#1:1799,9\n586#1:1814\n586#1:1821,2\n581#1:1826,2\n1238#1:1845,9\n1238#1:1860\n1238#1:1863,2\n417#1:1704\n419#1:1705\n424#1:1706\n431#1:1707\n437#1:1708\n439#1:1709\n522#1:1710\n538#1:1711\n578#1:1749\n1244#1:1861\n1257#1:1862\n444#1:1917\n445#1:1918\n449#1:1919\n586#1:1782\n586#1:1783,10\n586#1:1824\n1238#1:1830\n1238#1:1831,8\n1238#1:1866\n1268#1:1867\n1407#1:1914\n*E\n"
)
public final class BubbleMessageRowKt {
   private static final float DEFAULT_CORNER_RADIUS;
   private static final float GROUPED_CORNER_RADIUS;
   private static final float ADMIN_BUBBLE_END_GUTTER;
   @NotNull
   private static final io.intercom.android.sdk.models.Metadata botPreviewMetadata = new io.intercom.android.sdk.models.Metadata(
      "Bot", "AI Agent", 1726738186L, CollectionsKt.listOf(new Avatar$Builder().withInitials("BB").withShape(AvatarShape.SQUIRCLE))
   );
   @NotNull
   private static final io.intercom.android.sdk.models.Metadata humanPreviewMetadata = new io.intercom.android.sdk.models.Metadata(
      "Bob", null, 1726738186L, CollectionsKt.listOf(new Avatar$Builder().withInitials("SK")), 2, null
   );
   private static final Block$Builder paragraphBlock = new Block$Builder().withText("Hey").withType(BlockType.PARAGRAPH.getSerializedName());
   private static final Block$Builder longParagraphBlock = new Block$Builder()
      .withText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
      .withType(BlockType.PARAGRAPH.getSerializedName());
   private static final Block$Builder createTicketBlock = new Block$Builder()
      .withTitle("Create ticket")
      .withTicketType(new TicketType(-1L, "Bug", "", CollectionsKt.emptyList(), false))
      .withType(BlockType.CREATETICKETCARD.getSerializedName());
   private static final Block$Builder answerBlock = new Block$Builder()
      .withText(
         "Yes, you can change the date of your stay for up to seven days before it is due to begin. To do this, first go to your stays and click the relevant one. Then, go to change details and enter a new date. <a class=\"inline-citation\" data-entity-id=\"1\" data-entity-type=\"other\" data-source-index=\"1\" href=\"http://external.source.com\" title=\"Changing the date of your stay using our mobile app\"></a> <a class=\"inline-citation\" data-entity-id=\"2\" data-entity-type=\"article\" data-source-index=\"2\" href=\"http://intercom.com\" title=\"Cancel your booking\"></a>"
      )
      .withType(BlockType.PARAGRAPH.getSerializedName());
   private static final Block$Builder articleBlock = new Block$Builder()
      .withText(
         "I can’t find exactly what you need, but here is an article that could help:<br><br><a href=\"http://www.intercom.com\"> Making a group reservation</a><br>Explains how to make a group reservation with multiple guests."
      )
      .withType(BlockType.PARAGRAPH.getSerializedName());
   @NotNull
   private static final io.intercom.android.sdk.models.Metadata humanMetadata;
   private static final Block$Builder codeBlock = new Block$Builder()
      .withText("fun main() {<br>    val items = listOf(1, 2, 3)<br>    for (item in items) {<br>        println(item)<br>    }<br>}")
      .withType(BlockType.CODE.getSerializedName());
   private static final Block$Builder imageBlock = new Block$Builder().withUrl("https://via.placeholder.com/150").withType(BlockType.IMAGE.getSerializedName());
   private static final Block$Builder attachmentBlock = new Block$Builder()
      .withAttachments(
         CollectionsKt.listOf(
            BlockAttachment$Companion.create$default(
               BlockAttachment.Companion, "document.pdf", "https://example.com/document.pdf", "application/pdf", null, 0L, 0L, 56, null
            )
         )
      )
      .withType(BlockType.ATTACHMENTLIST.getSerializedName());
   private static final Block$Builder videoBlock = new Block$Builder()
      .withUrl("https://via.placeholder.com/150")
      .withType(BlockType.VIDEOFILE.getSerializedName());
   @NotNull
   private static final Set imageBlockTypes;
   @NotNull
   private static final Set attachmentBlockTypes;
   @NotNull
   private static final Set videoBlockTypes;

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void BubbleMessageRow(
      @NotNull Part var0,
      @NotNull GroupingPosition var1,
      boolean var2,
      @Nullable Modifier var3,
      @Nullable String var4,
      @Nullable SenderMetadata var5,
      @Nullable Function1 var6,
      @Nullable List var7,
      @Nullable List var8,
      @Nullable Function1 var9,
      @Nullable PendingMessage$FailedMediaUploadData var10,
      @Nullable FailedMessage var11,
      @Nullable Function1 var12,
      @Nullable Composer var13,
      int var14,
      int var15,
      int var16
   ) {
      Intrinsics.checkNotNullParameter(var0, "conversationPart");
      Intrinsics.checkNotNullParameter(var1, "groupingPosition");
      var13 = var13.startRestartGroup(-1731846255);
      ComposerKt.sourceInformation(
         var13,
         "C(BubbleMessageRow)P(!1,4,5,7,12,11,10!1,6,9)90@4478L2,93@4677L2,96@4839L2,98@4878L7,99@4917L6,100@4947L416,111@5388L47,113@5463L299,122@5972L241,154@7631L5109,137@6756L5984:BubbleMessageRow.kt#hr3bi5"
      );
      int var17 = var14;
      int var18 = var15;
      if ((var16 & 1) != 0) {
         var17 |= 6;
      } else if ((var14 & 6) == 0) {
         var17 |= var13.changedInstance(var0) ? 4 : 2;
      }

      if ((var16 & 2) != 0) {
         var17 |= 48;
      } else if ((var14 & 48) == 0) {
         var17 |= var13.changed(var1) ? 32 : 16;
      }

      if ((var16 & 4) != 0) {
         var17 |= 384;
      } else if ((var14 & 384) == 0) {
         var17 |= var13.changed(var2) ? 256 : 128;
      }

      if ((var16 & 8) != 0) {
         var17 |= 3072;
      } else if ((var14 & 3072) == 0) {
         var17 |= var13.changed(var3) ? 2048 : 1024;
      }

      if ((var16 & 16) != 0) {
         var17 |= 24576;
      } else if ((var14 & 24576) == 0) {
         var17 |= var13.changed(var4) ? 16384 : 8192;
      }

      if ((var16 & 32) != 0) {
         var17 |= 196608;
      } else if ((var14 & 196608) == 0) {
         var17 |= var13.changed(var5) ? 131072 : 65536;
      }

      if ((var16 & 64) != 0) {
         var17 |= 1572864;
      } else if ((var14 & 1572864) == 0) {
         var17 |= var13.changedInstance(var6) ? 1048576 : 524288;
      }

      if ((var16 & 128) != 0) {
         var17 |= 12582912;
      } else if ((var14 & 12582912) == 0) {
         var17 |= var13.changedInstance(var7) ? 8388608 : 4194304;
      }

      if ((var16 & 256) != 0) {
         var17 |= 100663296;
      } else if ((var14 & 100663296) == 0) {
         var17 |= var13.changedInstance(var8) ? 67108864 : 33554432;
      }

      if ((var16 & 512) != 0) {
         var17 |= 805306368;
      } else if ((var14 & 805306368) == 0) {
         var17 |= var13.changedInstance(var9) ? 536870912 : 268435456;
      }

      if ((var16 & 1024) != 0) {
         var18 |= 6;
      } else if ((var15 & 6) == 0) {
         var18 |= ((var15 & 8) == 0 ? var13.changed(var10) : var13.changedInstance(var10)) ? 4 : 2;
      }

      if ((var16 & 2048) != 0) {
         var18 |= 48;
      } else if ((var15 & 48) == 0) {
         var18 |= var13.changed(var11) ? 32 : 16;
      }

      if ((var16 & 4096) != 0) {
         var18 |= 384;
      } else if ((var15 & 384) == 0) {
         var18 |= var13.changedInstance(var12) ? 256 : 128;
      }

      if ((var17 & 306783379) == 306783378 && (var18 & 147) == 146 && var13.getSkipping()) {
         var13.skipToGroupEnd();
      } else {
         if ((var16 & 8) != 0) {
            var3 = Modifier.Companion;
         }

         if ((var16 & 16) != 0) {
            var4 = null;
         }

         if ((var16 & 32) != 0) {
            var5 = null;
         }

         if ((var16 & 64) != 0) {
            var13.startReplaceGroup(-1746732564);
            ComposerKt.sourceInformation(var13, "CC(remember):BubbleMessageRow.kt#9igjgp");
            Composer var20 = var13;
            boolean var21 = false;
            int var22 = 0;
            Object var23 = var20.rememberedValue();
            int var24 = 0;
            Object var10000;
            if (var23 == Composer.Companion.getEmpty()) {
               int var25 = 0;
               Object var75 = ((kotlin.jvm.functions.Function1) (recoveredArgument_658_0) -> BubbleMessageRowKt.BubbleMessageRow$lambda$1$lambda$0((io.intercom.android.sdk.m5.conversation.states.AttributeData)recoveredArgument_658_0));
               var20.updateRememberedValue(var75);
               var10000 = var75;
            } else {
               var10000 = var23;
            }

            Function1 var19 = (Function1)var10000;
            var13.endReplaceGroup();
            var6 = var19;
         }

         if ((var16 & 128) != 0) {
            var7 = CollectionsKt.emptyList();
         }

         if ((var16 & 256) != 0) {
            var8 = CollectionsKt.emptyList();
         }

         if ((var16 & 512) != 0) {
            var13.startReplaceGroup(-1746726196);
            ComposerKt.sourceInformation(var13, "CC(remember):BubbleMessageRow.kt#9igjgp");
            Composer var54 = var13;
            boolean var58 = false;
            int var62 = 0;
            Object var67 = var54.rememberedValue();
            int var71 = 0;
            Object var113;
            if (var67 == Composer.Companion.getEmpty()) {
               int var76 = 0;
               Object var77 = ((kotlin.jvm.functions.Function1) (recoveredArgument_659_0) -> BubbleMessageRowKt.BubbleMessageRow$lambda$3$lambda$2((io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData)recoveredArgument_659_0));
               var54.updateRememberedValue(var77);
               var113 = var77;
            } else {
               var113 = var67;
            }

            Function1 var51 = (Function1)var113;
            var13.endReplaceGroup();
            var9 = var51;
         }

         if ((var16 & 1024) != 0) {
            var10 = null;
         }

         if ((var16 & 2048) != 0) {
            var11 = null;
         }

         if ((var16 & 4096) != 0) {
            var13.startReplaceGroup(-1746721012);
            ComposerKt.sourceInformation(var13, "CC(remember):BubbleMessageRow.kt#9igjgp");
            Composer var55 = var13;
            boolean var59 = false;
            int var63 = 0;
            Object var68 = var55.rememberedValue();
            int var72 = 0;
            Object var114;
            if (var68 == Composer.Companion.getEmpty()) {
               int var78 = 0;
               Object var79 = ((kotlin.jvm.functions.Function1) (recoveredArgument_660_0) -> BubbleMessageRowKt.BubbleMessageRow$lambda$5$lambda$4((io.intercom.android.sdk.blocks.lib.models.TicketType)recoveredArgument_660_0));
               var55.updateRememberedValue(var79);
               var114 = var79;
            } else {
               var114 = var68;
            }

            Function1 var52 = (Function1)var114;
            var13.endReplaceGroup();
            var12 = var52;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1731846255, var17, var18, "io.intercom.android.sdk.m5.conversation.ui.components.row.BubbleMessageRow (BubbleMessageRow.kt:97)"
            );
         }

         CompositionLocal var56 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var60 = 0;
         int var64 = 0;
         ComposerKt.sourceInformationMarkerStart(var13, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var69 = var13.consume(var56);
         ComposerKt.sourceInformationMarkerEnd(var13);
         Context var53 = (Context)var69;
         IntercomColors var57 = IntercomTheme.INSTANCE.getColors(var13, IntercomTheme.$stable);
         var13.startReplaceGroup(-1746717142);
         ComposerKt.sourceInformation(var13, "CC(remember):BubbleMessageRow.kt#9igjgp");
         Composer var70 = var13;
         boolean var73 = (var17 & 896) == 256 | (var17 & 112) == 32 | (var18 & 112) == 32 | var13.changed(var57);
         int var80 = 0;
         Object var26 = var70.rememberedValue();
         int var27 = 0;
         Object var115;
         if (!var73 && var26 != Composer.Companion.getEmpty()) {
            var115 = var26;
         } else {
            int var28 = 0;
            Object var29 = recovered$9102ce0d86ae511c(
               var2, var1, var11 != null, var57.recovered$61a1caf6b6549f74(), var57.recovered$8c831c6299defa27(), var57.recovered$202acdf3dbb5c020()
            );
            var70.updateRememberedValue(var29);
            var115 = var29;
         }

         MessageStyle var65 = (MessageStyle)var115;
         var13.endReplaceGroup();
         MessageStyle var61 = var65;
         long var66 = ColorSchemeKt.recovered$e73d735ab97f03b1(var61.getBubbleStyle().recovered$6f15fcf6c761ad06(), var13, 0);
         var13.startReplaceGroup(-1746700747);
         ComposerKt.sourceInformation(var13, "CC(remember):BubbleMessageRow.kt#9igjgp");
         Composer recoveredCategorizedComposer = var13;
         boolean invalid$ivx = var13.changed(var0) | (var17 & 896) == 256;
         int $i$f$cachex = 0;
         Object it$ivx = recoveredCategorizedComposer.rememberedValue();
         int var30/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         if (!invalid$ivx && it$ivx != Composer.Companion.getEmpty()) {
            var115 = it$ivx;
         } else {
            int var31 = 0;
            ProcessedBlocks var32 = extractAndCleanAttachmentBlocks(var0, var2);
            List var33 = CollectionsKt.plus(var32.getCleaned(), var32.getExtracted());
            Object var34 = categorizeBlocks(var33, var2);
            recoveredCategorizedComposer.updateRememberedValue(var34);
            var115 = var34;
         }

         CategorizedBlocks var81 = (CategorizedBlocks)var115;
         var13.endReplaceGroup();
         CategorizedBlocks var74 = var81;
         List var82 = var74.component1();
         List var84 = var74.component2();
         List var86 = var74.component3();
         List var88 = var74.component4();
         var13.startReplaceGroup(-1746684517);
         ComposerKt.sourceInformation(var13, "CC(remember):BubbleMessageRow.kt#9igjgp");
         Composer var93 = var13;
         boolean invalid$ivxx = var13.changed(var82) | var13.changed(var84);
         int $i$f$cachexx = 0;
         Object it$ivxx = var93.rememberedValue();
         int var35/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         if (!invalid$ivxx && it$ivxx != Composer.Companion.getEmpty()) {
            var115 = it$ivxx;
         } else {
            int var36 = 0;
            Iterable var37 = var82;
            int var38 = 0;
            Iterable var39 = var37;
            ArrayList var40 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var37, 10));
            int var41 = 0;

            for (Object var43 : var39) {
               Block var44 = (Block)var43;
               Collection var45 = var40;
               int var46 = 0;
               var45.add(new MediaItem(var44, false));
            }

            List var47 = (List)var40;
            Iterable $this$map$ivx = var84;
            int $i$f$mapx = 0;
            Iterable var106 = $this$map$ivx;
            var destination$iv$ivx = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$ivx, 10));
            int $i$f$mapTox = 0;

            for (Object var110 : var106) {
               Block var112 = (Block)var110;
               Collection var111 = destination$iv$ivx;
               int var48 = 0;
               var111.add(new MediaItem(var112, true));
            }

            var37 = (List)destination$iv$ivx;
            Object var49 = CollectionsKt.plus(var47, var37);
            var93.updateRememberedValue(var49);
            var115 = var49;
         }

         List var91 = (List)var115;
         var13.endReplaceGroup();
         it$ivx = var91;
         boolean var92 = !((Collection)it$ivx).isEmpty() || !var86.isEmpty();
         boolean var94 = var0.getMessageStyle() == io.intercom.android.sdk.models.MessageStyle.ATTRIBUTE_COLLECTOR && !var0.getForm().getAttributes().isEmpty();
         var13.startReplaceGroup(-1746655259);
         ComposerKt.sourceInformation(var13, "146@7323L7");
         BottomMetadata var10002;
         if (var11 != null) {
            var10002 = new BottomMetadata(var11.getMessage(), 0.0F, true, 0.0F, 10, null);
         } else if (var5 != null && var4 != null) {
            String var10004 = ClickableMessageRowKt.joinSenderLabelAndTimestamp(var5.getLabel(), var4);
            boolean var10006 = var5.getPinned();
            PaddingValues var10007 = var61.getBubbleStyle().getPadding();
            CompositionLocal var98 = CompositionLocalsKt.getLocalLayoutDirection();
            it$ivxx = 0;
            var35/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
            ComposerKt.sourceInformationMarkerStart(var13, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var102 = var13.consume(var98);
            ComposerKt.sourceInformationMarkerEnd(var13);
            var10002 = new BottomMetadata(var10004, 0.0F, var10006, PaddingKt.calculateStartPadding(var10007, (LayoutDirection)var102), 2, null);
         } else {
            var10002 = var4 != null ? new BottomMetadata(var4, 0.0F, false, 0.0F, 14, null) : null;
         }

         BottomMetadata var96 = var10002;
         var13.endReplaceGroup();
         ClickableMessageRowKt.ClickableMessageRow(
            var0,
            var3,
            var96,
            var61.getRowAlignment(),
            var61.getRowPadding(),
            var11 != null ? var11.getOnRetryMessageClicked() : null,
            ComposableLambdaKt.rememberComposableLambda(
               912974020,
               true,
               new BubbleMessageRowKt$BubbleMessageRow$4(
                  var11, var61, (List)it$ivx, var86, var94, var88, var2, var53, var1, var92, var7, var8, var6, var5, var66, var12, var10, var9
               ),
               var13,
               54
            ),
            var13,
            1572864 | 14 & var17 | 112 & var17 >> 6,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var118 = var13.endRestartGroup();
      if (var118 != null) {
         final io.intercom.android.sdk.models.Part recoveredCapture_657_0 = var0;
         final io.intercom.android.sdk.m5.conversation.states.GroupingPosition recoveredCapture_657_1 = var1;
         final boolean recoveredCapture_657_2 = var2;
         final androidx.compose.ui.Modifier recoveredCapture_657_3 = var3;
         final java.lang.String recoveredCapture_657_4 = var4;
         final io.intercom.android.sdk.m5.conversation.states.SenderMetadata recoveredCapture_657_5 = var5;
         final kotlin.jvm.functions.Function1 recoveredCapture_657_6 = var6;
         final java.util.List recoveredCapture_657_7 = var7;
         final java.util.List recoveredCapture_657_8 = var8;
         final kotlin.jvm.functions.Function1 recoveredCapture_657_9 = var9;
         final io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData recoveredCapture_657_10 = var10;
         final io.intercom.android.sdk.m5.conversation.ui.components.row.FailedMessage recoveredCapture_657_11 = var11;
         final kotlin.jvm.functions.Function1 recoveredCapture_657_12 = var12;
         final int recoveredCapture_657_13 = var14;
         final int recoveredCapture_657_14 = var15;
         final int recoveredCapture_657_15 = var16;
         var118.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_657_0, recoveredArgument_657_1) -> BubbleMessageRowKt.BubbleMessageRow$lambda$11(recoveredCapture_657_0, recoveredCapture_657_1, recoveredCapture_657_2, recoveredCapture_657_3, recoveredCapture_657_4, recoveredCapture_657_5, recoveredCapture_657_6, recoveredCapture_657_7, recoveredCapture_657_8, recoveredCapture_657_9, recoveredCapture_657_10, recoveredCapture_657_11, recoveredCapture_657_12, recoveredCapture_657_13, recoveredCapture_657_14, recoveredCapture_657_15, (androidx.compose.runtime.Composer)recoveredArgument_657_0, ((java.lang.Integer)recoveredArgument_657_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void recovered$5dd6a8902c85f195(
      Part var0,
      GroupingPosition var1,
      List var2,
      MessageStyle var3,
      boolean var4,
      boolean var5,
      long var6,
      Function1 var8,
      PendingMessage$FailedMediaUploadData var9,
      Function1 var10,
      Function0 var11,
      boolean var12,
      Composer var13,
      int var14,
      int var15,
      int var16
   ) {
      var13 = var13.startRestartGroup(1707313759);
      ComposerKt.sourceInformation(
         var13, "C(SegmentedBubbleContent)P(2,4!1,7,5,6,1:c#ui.graphics.Color,9!1,10)280@13936L67,290@14623L313,301@14942L5217:BubbleMessageRow.kt#hr3bi5"
      );
      int var17 = var14;
      int var18 = var15;
      if ((var16 & 1) != 0) {
         var17 |= 6;
      } else if ((var14 & 6) == 0) {
         var17 |= var13.changedInstance(var0) ? 4 : 2;
      }

      if ((var16 & 2) != 0) {
         var17 |= 48;
      } else if ((var14 & 48) == 0) {
         var17 |= var13.changed(var1) ? 32 : 16;
      }

      if ((var16 & 4) != 0) {
         var17 |= 384;
      } else if ((var14 & 384) == 0) {
         var17 |= var13.changedInstance(var2) ? 256 : 128;
      }

      if ((var16 & 8) != 0) {
         var17 |= 3072;
      } else if ((var14 & 3072) == 0) {
         var17 |= var13.changed(var3) ? 2048 : 1024;
      }

      if ((var16 & 16) != 0) {
         var17 |= 24576;
      } else if ((var14 & 24576) == 0) {
         var17 |= var13.changed(var4) ? 16384 : 8192;
      }

      if ((var16 & 32) != 0) {
         var17 |= 196608;
      } else if ((var14 & 196608) == 0) {
         var17 |= var13.changed(var5) ? 131072 : 65536;
      }

      if ((var16 & 64) != 0) {
         var17 |= 1572864;
      } else if ((var14 & 1572864) == 0) {
         var17 |= var13.changed(var6) ? 1048576 : 524288;
      }

      if ((var16 & 128) != 0) {
         var17 |= 12582912;
      } else if ((var14 & 12582912) == 0) {
         var17 |= var13.changedInstance(var8) ? 8388608 : 4194304;
      }

      if ((var16 & 256) != 0) {
         var17 |= 100663296;
      } else if ((var14 & 100663296) == 0) {
         var17 |= ((var14 & 134217728) == 0 ? var13.changed(var9) : var13.changedInstance(var9)) ? 67108864 : 33554432;
      }

      if ((var16 & 512) != 0) {
         var17 |= 805306368;
      } else if ((var14 & 805306368) == 0) {
         var17 |= var13.changedInstance(var10) ? 536870912 : 268435456;
      }

      if ((var16 & 1024) != 0) {
         var18 |= 6;
      } else if ((var15 & 6) == 0) {
         var18 |= var13.changedInstance(var11) ? 4 : 2;
      }

      if ((var16 & 2048) != 0) {
         var18 |= 48;
      } else if ((var15 & 48) == 0) {
         var18 |= var13.changed(var12) ? 32 : 16;
      }

      if ((var17 & 306783379) == 306783378 && (var18 & 19) == 18 && var13.getSkipping()) {
         var13.skipToGroupEnd();
      } else {
         if ((var16 & 2048) != 0) {
            var12 = true;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1707313759, var17, var18, "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedBubbleContent (BubbleMessageRow.kt:279)"
            );
         }

         var13.startReplaceGroup(658933641);
         ComposerKt.sourceInformation(var13, "CC(remember):BubbleMessageRow.kt#9igjgp");
         Composer var21 = var13;
         boolean var22 = var13.changed(var2);
         int var23 = 0;
         Object var24 = var21.rememberedValue();
         int var25 = 0;
         Object var10000;
         if (!var22 && var24 != Composer.Companion.getEmpty()) {
            var10000 = var24;
         } else {
            int var26 = 0;
            Object var27 = ContentSegmentKt.segmentStandaloneBlocks(var2);
            var21.updateRememberedValue(var27);
            var10000 = var27;
         }

         List var20 = (List)var10000;
         var13.endReplaceGroup();
         List var19 = var20;
         boolean var65 = isCreateTicketEnabled(var0);
         boolean var66 = var12 && (var1 == GroupingPosition.STANDALONE || var1 == GroupingPosition.TOP);
         var22 = CollectionsKt.firstOrNull(var19) instanceof ContentSegment$TextRun;
         boolean var68 = var66 && !var22 && var0.getMetadata() != null;
         var24 = ComposableLambdaKt.rememberComposableLambda(75783638, true, new BubbleMessageRowKt$SegmentedBubbleContent$metadata$1(var0), var13, 54);
         Vertical var71 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(StreamingMessageListKt.getGroupedMessageGap());
         int var28 = 48;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var13, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var70 = Modifier.Companion;
         Horizontal var72 = Alignment.Companion.getStart();
         MeasurePolicy var30 = ColumnKt.columnMeasurePolicy(var71, var72, var13, 14 & var28 >> 3 | 112 & var28 >> 3);
         Modifier var31 = var70;
         MeasurePolicy var32 = var30;
         Composer var33 = var13;
         int var34 = 112 & var28 << 3;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var36 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var33, 0));
         CompositionLocalMap var37 = var33.getCurrentCompositionLocalMap();
         Modifier var38 = ComposedModifierKt.materializeModifier(var33, var31);
         Function0 var39 = ComposeUiNode.Companion.getConstructor();
         Composer var40 = var33;
         int var41 = 6 | 896 & var34 << 6;
         int var42 = 0;
         ComposerKt.sourceInformationMarkerStart(var40, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var40.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var40.startReusableNode();
         if (var40.getInserting()) {
            var40.createNode(var39);
         } else {
            var40.useNode();
         }

         Composer var43 = Updater.recovered$676aa17b44fd6280(var40);
         int var44 = 0;
         Updater.recovered$89682b199a142880(var43, var32, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var43, var37, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var43, var36, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var43, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var43, var38, ComposeUiNode.Companion.getSetModifier());
         int var45 = 14 & var41 >> 6;
         Composer var46 = var40;
         int var47 = 0;
         ComposerKt.sourceInformationMarkerStart(var46, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var48 = 6 | 112 & var28 >> 6;
         Composer var49 = var46;
         ColumnScope var50 = ColumnScopeInstance.INSTANCE;
         int var51 = 0;
         ComposerKt.sourceInformationMarkerStart(var49, 950305462, "C:BubbleMessageRow.kt#hr3bi5");
         var49.startReplaceGroup(-1909011608);
         ComposerKt.sourceInformation(var49, "*318@16013L143,308@15502L654");
         if (var68) {
            MessageStyle$BubbleStyle var52 = var3.getBubbleStyle();
            int var53 = 0;
            SurfaceKt.recovered$ecd79a99e04a9534(
               PaddingKt.padding(Modifier.Companion, var3.getBubbleGutter()),
               calculateGroupedCornerShape(var4, true, var5, var3.getBubbleStyle().getShape()),
               var52.recovered$6f15fcf6c761ad06(),
               0L,
               0.0F,
               0.0F,
               var52.getBorderStroke(),
               ComposableLambdaKt.rememberComposableLambda(
                  585322216, true, new BubbleMessageRowKt$SegmentedBubbleContent$1$1$1(var52, (Function2)var24), var49, 54
               ),
               var49,
               12582912,
               56
            );
         }

         var49.endReplaceGroup();
         var49.startReplaceGroup(-1908983145);
         ComposerKt.sourceInformation(var49, "");
         Iterable var54 = var19;
         int var73 = 0;
         int var74 = 0;

         for (Object var56 : var54) {
            int var57 = var74++;
            if (var57 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            ContentSegment var58 = (ContentSegment)var56;
            int var59 = var57;
            int var60 = 0;
            ContentSegment var61 = var58;
            if (var61 instanceof ContentSegment$TextRun) {
               var49.startReplaceGroup(-1658183020);
               ComposerKt.sourceInformation(var49, "*340@17013L1281,330@16387L1907");
               MessageStyle$BubbleStyle var62 = var3.getBubbleStyle();
               int var63 = 0;
               SurfaceKt.recovered$ecd79a99e04a9534(
                  PaddingKt.padding(Modifier.Companion, var3.getBubbleGutter()),
                  calculateGroupedCornerShape(var4 || var59 > 0, var59 < CollectionsKt.getLastIndex(var19), var5, var3.getBubbleStyle().getShape()),
                  var62.recovered$6f15fcf6c761ad06(),
                  0L,
                  0.0F,
                  0.0F,
                  var62.getBorderStroke(),
                  ComposableLambdaKt.rememberComposableLambda(
                     686874094,
                     true,
                     new BubbleMessageRowKt$SegmentedBubbleContent$1$2$1$1(
                        var62, var59, var66, (Function2)var24, var58, var0, var6, var3, var65, var5, var9, var10, var11, var8
                     ),
                     var49,
                     54
                  ),
                  var49,
                  12582912,
                  56
               );
               var49.endReplaceGroup();
            } else {
               if (!(var61 instanceof ContentSegment$Standalone)) {
                  var49.startReplaceGroup(916341437);
                  var49.endReplaceGroup();
                  throw new NoWhenBranchMatchedException();
               }

               var49.startReplaceGroup(-1656132339);
               ComposerKt.sourceInformation(var49, "375@18973L10,376@19060L10,378@19206L10,370@18670L1441");
               BlockRenderData var10001;
               Block var10003 = ((ContentSegment$Standalone)var58).getBlock();
               Color var10004 = Color.recovered$f46ef195ad4a2363(var6);
               BlockRenderTextStyle var10007;
               long var10009 = IntercomTheme.INSTANCE.getTypography(var49, IntercomTheme.$stable).getType04().recovered$8f8bc2178e9073c3();
               FontWeight var10010 = IntercomTheme.INSTANCE.getTypography(var49, IntercomTheme.$stable).getType04().getFontWeight();
               if (var10010 == null) {
                  var10010 = FontWeight.Companion.getNormal();
               }

               var10007 = new BlockRenderTextStyle(
                  var10009,
                  var10010,
                  IntercomTheme.INSTANCE.getTypography(var49, IntercomTheme.$stable).getType04().recovered$17d1bb629b307c0f(),
                  null,
                  null,
                  null,
                  56,
                  null
               );
               var10001 = new BlockRenderData(var10003, var10004, null, null, var10007, 12, null);
               BlockViewKt.recovered$5709084683a88fd9(
                  null,
                  var10001,
                  var5,
                  null,
                  var65,
                  var0.getParentConversation().getId(),
                  ImageRenderType.WITH_MAX_SIZE,
                  0L,
                  var11,
                  var8,
                  false,
                  null,
                  calculateGroupedCornerShape(var4 || var59 > 0 || var68, var59 < CollectionsKt.getLastIndex(var19), var5, var3.getBubbleStyle().getShape()),
                  false,
                  null,
                  var49,
                  1572864 | 896 & var17 >> 9 | 234881024 & var18 << 24 | 1879048192 & var17 << 6,
                  0,
                  27785
               );
               var49.endReplaceGroup();
            }
         }

         var49.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var49);
         ComposerKt.sourceInformationMarkerEnd(var46);
         var40.endNode();
         ComposerKt.sourceInformationMarkerEnd(var40);
         ComposerKt.sourceInformationMarkerEnd(var33);
         ComposerKt.sourceInformationMarkerEnd(var13);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var76 = var13.endRestartGroup();
      if (var76 != null) {
         final io.intercom.android.sdk.models.Part recoveredCapture_675_0 = var0;
         final io.intercom.android.sdk.m5.conversation.states.GroupingPosition recoveredCapture_675_1 = var1;
         final java.util.List recoveredCapture_675_2 = var2;
         final io.intercom.android.sdk.m5.conversation.ui.components.row.MessageStyle recoveredCapture_675_3 = var3;
         final boolean recoveredCapture_675_4 = var4;
         final boolean recoveredCapture_675_5 = var5;
         final long recoveredCapture_675_6 = var6;
         final kotlin.jvm.functions.Function1 recoveredCapture_675_7 = var8;
         final io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData recoveredCapture_675_8 = var9;
         final kotlin.jvm.functions.Function1 recoveredCapture_675_9 = var10;
         final kotlin.jvm.functions.Function0 recoveredCapture_675_10 = var11;
         final boolean recoveredCapture_675_11 = var12;
         final int recoveredCapture_675_12 = var14;
         final int recoveredCapture_675_13 = var15;
         final int recoveredCapture_675_14 = var16;
         var76.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_675_0, recoveredArgument_675_1) -> BubbleMessageRowKt.SegmentedBubbleContent_E1AOrGg$lambda$17(recoveredCapture_675_0, recoveredCapture_675_1, recoveredCapture_675_2, recoveredCapture_675_3, recoveredCapture_675_4, recoveredCapture_675_5, recoveredCapture_675_6, recoveredCapture_675_7, recoveredCapture_675_8, recoveredCapture_675_9, recoveredCapture_675_10, recoveredCapture_675_11, recoveredCapture_675_12, recoveredCapture_675_13, recoveredCapture_675_14, (androidx.compose.runtime.Composer)recoveredArgument_675_0, ((java.lang.Integer)recoveredArgument_675_1).intValue())));
      }
   }

   private static final MessageStyle recovered$9102ce0d86ae511c(boolean var0, GroupingPosition var1, boolean var2, long var3, long var5, long var7) {
      MessageStyle var10000;
      if (var0) {
         int var9 = 16;
         int var10 = 0;
         float var10005 = Dp.recovered$95b3d103663bf060(var9);
         var9 = 12;
         var10 = 0;
         PaddingValues var36 = PaddingKt.recovered$4ebb4826aa57abc6(var10005, Dp.recovered$95b3d103663bf060(var9));
         RoundedCornerShape var10006 = recovered$0adb9bd0db3d52f7(var1, 0.0F, 0.0F, 6, null);
         var9 = 1;
         var10 = 0;
         MessageStyle$BubbleStyle var10002 = new MessageStyle$BubbleStyle(
            var3, var36, var10006, BorderStrokeKt.recovered$91879a1bef2fb536(Dp.recovered$95b3d103663bf060(var9), var5), null
         );
         Horizontal var10003 = Alignment.Companion.getStart();
         var9 = 16;
         var10 = 0;
         float var10004 = Dp.recovered$95b3d103663bf060(var9);
         var9 = 16;
         var10 = 0;
         var10000 = new MessageStyle(
            var10002,
            var10003,
            PaddingKt.recovered$0229d0d9fcc63c3d(var10004, 0.0F, Dp.recovered$95b3d103663bf060(var9), 0.0F, 10, null),
            PaddingKt.recovered$0229d0d9fcc63c3d(0.0F, 0.0F, ADMIN_BUBBLE_END_GUTTER, 0.0F, 11, null)
         );
      } else {
         ;
         int var15 = 16;
         int var25 = 0;
         float var37 = Dp.recovered$95b3d103663bf060(var15);
         var15 = 12;
         var25 = 0;
         MessageStyle$BubbleStyle var31 = new MessageStyle$BubbleStyle(
            var7,
            PaddingKt.recovered$4ebb4826aa57abc6(var37, Dp.recovered$95b3d103663bf060(var15)),
            recovered$f595cb41dc6eb0a1(var1, 0.0F, 0.0F, 6, null),
            null,
            null
         );
         Horizontal var32 = Alignment.Companion.getEnd();
         PaddingValues var34;
         if (var2) {
            var15 = 36;
            var25 = 0;
            float var33 = Dp.recovered$95b3d103663bf060(var15);
            var15 = 16;
            var25 = 0;
            var34 = PaddingKt.recovered$0229d0d9fcc63c3d(var33, 0.0F, Dp.recovered$95b3d103663bf060(var15), 0.0F, 10, null);
         } else {
            var15 = 60;
            var25 = 0;
            float var35 = Dp.recovered$95b3d103663bf060(var15);
            var15 = 16;
            var25 = 0;
            var34 = PaddingKt.recovered$0229d0d9fcc63c3d(var35, 0.0F, Dp.recovered$95b3d103663bf060(var15), 0.0F, 10, null);
         }

         var10000 = new MessageStyle(var31, var32, var34, null, 8, null);
      }

      return var10000;
   }

   private static final boolean shouldGroupTop(GroupingPosition var0) {
      return var0 == GroupingPosition.BOTTOM || var0 == GroupingPosition.MIDDLE;
   }

   private static final boolean shouldGroupBottom(GroupingPosition var0) {
      return var0 == GroupingPosition.TOP || var0 == GroupingPosition.MIDDLE;
   }

   private static final RoundedCornerShape recovered$d4df99f6a2aa9fb5(GroupingPosition var0, boolean var1, float var2, float var3) {
      boolean var4 = shouldGroupTop(var0);
      boolean var5 = shouldGroupBottom(var0);
      RoundedCornerShape var10000;
      if (var1) {
         float var6 = var4 ? var3 : var2;
         float var7 = var5 ? var3 : var2;
         var10000 = RoundedCornerShapeKt.recovered$e05142e8a28d423f(var6, var2, var2, var7);
      } else {
         float var8 = var4 ? var3 : var2;
         float var9 = var5 ? var3 : var2;
         var10000 = RoundedCornerShapeKt.recovered$e05142e8a28d423f(var2, var8, var9, var2);
      }

      return var10000;
   }

   // $VF: synthetic method
   static RoundedCornerShape recovered$ce55a70e748d1da8(GroupingPosition var0, boolean var1, float var2, float var3, int var4, Object var5) {
      if ((var4 & 4) != 0) {
         var2 = DEFAULT_CORNER_RADIUS;
      }

      if ((var4 & 8) != 0) {
         var3 = GROUPED_CORNER_RADIUS;
      }

      return recovered$d4df99f6a2aa9fb5(var0, var1, var2, var3);
   }

   @NotNull
   public static final RoundedCornerShape recovered$77008b4928d6b5a0(@NotNull GroupingPosition var0, float var1, float var2) {
      Intrinsics.checkNotNullParameter(var0, "groupingPosition");
      return recovered$d4df99f6a2aa9fb5(var0, false, var1, var2);
   }

   // $VF: synthetic method
   public static RoundedCornerShape recovered$f595cb41dc6eb0a1(GroupingPosition var0, float var1, float var2, int var3, Object var4) {
      if ((var3 & 2) != 0) {
         var1 = DEFAULT_CORNER_RADIUS;
      }

      if ((var3 & 4) != 0) {
         var2 = GROUPED_CORNER_RADIUS;
      }

      return recovered$77008b4928d6b5a0(var0, var1, var2);
   }

   private static final RoundedCornerShape recovered$ebd86916e4a7e654(GroupingPosition var0, float var1, float var2) {
      return recovered$d4df99f6a2aa9fb5(var0, true, var1, var2);
   }

   // $VF: synthetic method
   static RoundedCornerShape recovered$0adb9bd0db3d52f7(GroupingPosition var0, float var1, float var2, int var3, Object var4) {
      if ((var3 & 2) != 0) {
         var1 = DEFAULT_CORNER_RADIUS;
      }

      if ((var3 & 4) != 0) {
         var2 = GROUPED_CORNER_RADIUS;
      }

      return recovered$ebd86916e4a7e654(var0, var1, var2);
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void FailedMessageIcon(Modifier var0, Composer var1, int var2, int var3) {
      var1 = var1.startRestartGroup(-1829301504);
      ComposerKt.sourceInformation(var1, "C(FailedMessageIcon)519@24722L55,522@24884L6,518@24698L205:BubbleMessageRow.kt#hr3bi5");
      int var4 = var2;
      if ((var3 & 1) != 0) {
         var4 |= 6;
      } else if ((var2 & 6) == 0) {
         var4 |= var1.changed(var0) ? 4 : 2;
      }

      if ((var4 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if ((var3 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1829301504, var4, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.FailedMessageIcon (BubbleMessageRow.kt:517)"
            );
         }

         Painter var10000 = PainterResources_androidKt.painterResource(drawable.intercom_message_error, var1, 0);
         int var5 = 16;
         int var6 = 0;
         IconKt.recovered$e00adffb7fe45b02(
            var10000,
            null,
            SizeKt.recovered$111ef94c93e78c04(var0, Dp.recovered$95b3d103663bf060(var5)),
            IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).recovered$2bcfe565407cdfb8(),
            var1,
            48 | Painter.$stable,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var8 = var1.endRestartGroup();
      if (var8 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_663_0 = var0;
         final int recoveredCapture_663_1 = var2;
         final int recoveredCapture_663_2 = var3;
         var8.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_663_0, recoveredArgument_663_1) -> BubbleMessageRowKt.FailedMessageIcon$lambda$18(recoveredCapture_663_0, recoveredCapture_663_1, recoveredCapture_663_2, (androidx.compose.runtime.Composer)recoveredArgument_663_0, ((java.lang.Integer)recoveredArgument_663_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$e9eb3be55737358d(
      @NotNull Part var0,
      long var1,
      boolean var3,
      @NotNull Shape var4,
      @NotNull Function0 var5,
      @NotNull Function1 var6,
      boolean var7,
      @Nullable PendingMessage$FailedMediaUploadData var8,
      @NotNull Function1 var9,
      float var10,
      @Nullable List var11,
      @Nullable Composer var12,
      int var13,
      int var14,
      int var15
   ) {
      Intrinsics.checkNotNullParameter(var0, "conversationPart");
      Intrinsics.checkNotNullParameter(var4, "contentShape");
      Intrinsics.checkNotNullParameter(var5, "onClick");
      Intrinsics.checkNotNullParameter(var6, "onCreateTicket");
      Intrinsics.checkNotNullParameter(var9, "onRetryMediaClicked");
      var12 = var12.startRestartGroup(1959146675);
      ComposerKt.sourceInformation(var12, "C(MessageContent)P(3,1:c#ui.graphics.Color,4,2,7,8,6,5,9,10:c#ui.unit.Dp)540@25376L658:BubbleMessageRow.kt#hr3bi5");
      int var16 = var13;
      int var17 = var14;
      if ((var15 & 1) != 0) {
         var16 |= 6;
      } else if ((var13 & 6) == 0) {
         var16 |= var12.changedInstance(var0) ? 4 : 2;
      }

      if ((var15 & 2) != 0) {
         var16 |= 48;
      } else if ((var13 & 48) == 0) {
         var16 |= var12.changed(var1) ? 32 : 16;
      }

      if ((var15 & 4) != 0) {
         var16 |= 384;
      } else if ((var13 & 384) == 0) {
         var16 |= var12.changed(var3) ? 256 : 128;
      }

      if ((var15 & 8) != 0) {
         var16 |= 3072;
      } else if ((var13 & 3072) == 0) {
         var16 |= var12.changed(var4) ? 2048 : 1024;
      }

      if ((var15 & 16) != 0) {
         var16 |= 24576;
      } else if ((var13 & 24576) == 0) {
         var16 |= var12.changedInstance(var5) ? 16384 : 8192;
      }

      if ((var15 & 32) != 0) {
         var16 |= 196608;
      } else if ((var13 & 196608) == 0) {
         var16 |= var12.changedInstance(var6) ? 131072 : 65536;
      }

      if ((var15 & 64) != 0) {
         var16 |= 1572864;
      } else if ((var13 & 1572864) == 0) {
         var16 |= var12.changed(var7) ? 1048576 : 524288;
      }

      if ((var15 & 128) != 0) {
         var16 |= 12582912;
      } else if ((var13 & 12582912) == 0) {
         var16 |= ((var13 & 16777216) == 0 ? var12.changed(var8) : var12.changedInstance(var8)) ? 8388608 : 4194304;
      }

      if ((var15 & 256) != 0) {
         var16 |= 100663296;
      } else if ((var13 & 100663296) == 0) {
         var16 |= var12.changedInstance(var9) ? 67108864 : 33554432;
      }

      if ((var15 & 512) != 0) {
         var16 |= 805306368;
      } else if ((var13 & 805306368) == 0) {
         var16 |= var12.changed(var10) ? 536870912 : 268435456;
      }

      if ((var15 & 1024) != 0) {
         var17 |= 6;
      } else if ((var14 & 6) == 0) {
         var17 |= var12.changedInstance(var11) ? 4 : 2;
      }

      if ((var16 & 306783379) == 306783378 && (var17 & 3) == 2 && var12.getSkipping()) {
         var12.skipToGroupEnd();
      } else {
         if ((var15 & 512) != 0) {
            int var18 = 0;
            int var19 = 0;
            var10 = Dp.recovered$95b3d103663bf060(var18);
         }

         if ((var15 & 1024) != 0) {
            var11 = null;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1959146675, var16, var17, "io.intercom.android.sdk.m5.conversation.ui.components.row.MessageContent (BubbleMessageRow.kt:539)"
            );
         }

         Vertical var48 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(var10);
         byte var21 = 0;
         int var22 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var12, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var47 = Modifier.Companion;
         Horizontal var20 = Alignment.Companion.getStart();
         MeasurePolicy var23 = ColumnKt.columnMeasurePolicy(var48, var20, var12, 14 & var21 >> 3 | 112 & var21 >> 3);
         Modifier var24 = var47;
         MeasurePolicy var25 = var23;
         Composer var26 = var12;
         int var27 = 112 & var21 << 3;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var26, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var29 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var26, 0));
         CompositionLocalMap var30 = var26.getCurrentCompositionLocalMap();
         Modifier var31 = ComposedModifierKt.materializeModifier(var26, var24);
         Function0 var32 = ComposeUiNode.Companion.getConstructor();
         Composer var33 = var26;
         int var34 = 6 | 896 & var27 << 6;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var33.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var33.startReusableNode();
         if (var33.getInserting()) {
            var33.createNode(var32);
         } else {
            var33.useNode();
         }

         Composer var36 = Updater.recovered$676aa17b44fd6280(var33);
         int var37 = 0;
         Updater.recovered$89682b199a142880(var36, var25, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var36, var30, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var36, var29, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var36, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var36, var31, ComposeUiNode.Companion.getSetModifier());
         int var38 = 14 & var34 >> 6;
         Composer var39 = var33;
         int var40 = 0;
         ComposerKt.sourceInformationMarkerStart(var39, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var41 = 6 | 112 & var21 >> 6;
         Composer var42 = var39;
         ColumnScope var43 = ColumnScopeInstance.INSTANCE;
         int var44 = 0;
         ComposerKt.sourceInformationMarkerStart(var42, 1704344507, "C543@25472L556:BubbleMessageRow.kt#hr3bi5");
         List var10000 = var11;
         if (var11 == null) {
            var10000 = var0.getBlocks();
         }

         List var45 = var10000;
         Intrinsics.checkNotNull(var45);
         recovered$5fb12a358761c608(
            var45,
            var0,
            var1,
            var4,
            var3,
            var7,
            var8,
            var9,
            var5,
            var6,
            var10,
            null,
            var42,
            112 & var16 << 3
               | 896 & var16 << 3
               | 7168 & var16
               | 57344 & var16 << 6
               | 458752 & var16 >> 3
               | 3670016 & var16 >> 3
               | 29360128 & var16 >> 3
               | 234881024 & var16 << 12
               | 1879048192 & var16 << 12,
            14 & var16 >> 27,
            2048
         );
         ComposerKt.sourceInformationMarkerEnd(var42);
         ComposerKt.sourceInformationMarkerEnd(var39);
         var33.endNode();
         ComposerKt.sourceInformationMarkerEnd(var33);
         ComposerKt.sourceInformationMarkerEnd(var26);
         ComposerKt.sourceInformationMarkerEnd(var12);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var49 = var12.endRestartGroup();
      if (var49 != null) {
         final io.intercom.android.sdk.models.Part recoveredCapture_678_0 = var0;
         final long recoveredCapture_678_1 = var1;
         final boolean recoveredCapture_678_2 = var3;
         final androidx.compose.ui.graphics.Shape recoveredCapture_678_3 = var4;
         final kotlin.jvm.functions.Function0 recoveredCapture_678_4 = var5;
         final kotlin.jvm.functions.Function1 recoveredCapture_678_5 = var6;
         final boolean recoveredCapture_678_6 = var7;
         final io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData recoveredCapture_678_7 = var8;
         final kotlin.jvm.functions.Function1 recoveredCapture_678_8 = var9;
         final float recoveredCapture_678_9 = var10;
         final java.util.List recoveredCapture_678_10 = var11;
         final int recoveredCapture_678_11 = var13;
         final int recoveredCapture_678_12 = var14;
         final int recoveredCapture_678_13 = var15;
         var49.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_678_0, recoveredArgument_678_1) -> BubbleMessageRowKt.MessageContent_hcemMNk$lambda$20(recoveredCapture_678_0, recoveredCapture_678_1, recoveredCapture_678_2, recoveredCapture_678_3, recoveredCapture_678_4, recoveredCapture_678_5, recoveredCapture_678_6, recoveredCapture_678_7, recoveredCapture_678_8, recoveredCapture_678_9, recoveredCapture_678_10, recoveredCapture_678_11, recoveredCapture_678_12, recoveredCapture_678_13, (androidx.compose.runtime.Composer)recoveredArgument_678_0, ((java.lang.Integer)recoveredArgument_678_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void recovered$5fb12a358761c608(
      List var0,
      Part var1,
      long var2,
      Shape var4,
      boolean var5,
      boolean var6,
      PendingMessage$FailedMediaUploadData var7,
      Function1 var8,
      Function0 var9,
      Function1 var10,
      float var11,
      Modifier var12,
      Composer var13,
      int var14,
      int var15,
      int var16
   ) {
      var13 = var13.startRestartGroup(1388262313);
      ComposerKt.sourceInformation(var13, "C(MessageBlocks)P(!1,3,1:c#ui.graphics.Color!2,6!1,10,8,9,11:c#ui.unit.Dp)580@26846L1862:BubbleMessageRow.kt#hr3bi5");
      int var17 = var14;
      int var18 = var15;
      if ((var16 & 1) != 0) {
         var17 |= 6;
      } else if ((var14 & 6) == 0) {
         var17 |= var13.changedInstance(var0) ? 4 : 2;
      }

      if ((var16 & 2) != 0) {
         var17 |= 48;
      } else if ((var14 & 48) == 0) {
         var17 |= var13.changedInstance(var1) ? 32 : 16;
      }

      if ((var16 & 4) != 0) {
         var17 |= 384;
      } else if ((var14 & 384) == 0) {
         var17 |= var13.changed(var2) ? 256 : 128;
      }

      if ((var16 & 8) != 0) {
         var17 |= 3072;
      } else if ((var14 & 3072) == 0) {
         var17 |= var13.changed(var4) ? 2048 : 1024;
      }

      if ((var16 & 16) != 0) {
         var17 |= 24576;
      } else if ((var14 & 24576) == 0) {
         var17 |= var13.changed(var5) ? 16384 : 8192;
      }

      if ((var16 & 32) != 0) {
         var17 |= 196608;
      } else if ((var14 & 196608) == 0) {
         var17 |= var13.changed(var6) ? 131072 : 65536;
      }

      if ((var16 & 64) != 0) {
         var17 |= 1572864;
      } else if ((var14 & 1572864) == 0) {
         var17 |= ((var14 & 2097152) == 0 ? var13.changed(var7) : var13.changedInstance(var7)) ? 1048576 : 524288;
      }

      if ((var16 & 128) != 0) {
         var17 |= 12582912;
      } else if ((var14 & 12582912) == 0) {
         var17 |= var13.changedInstance(var8) ? 8388608 : 4194304;
      }

      if ((var16 & 256) != 0) {
         var17 |= 100663296;
      } else if ((var14 & 100663296) == 0) {
         var17 |= var13.changedInstance(var9) ? 67108864 : 33554432;
      }

      if ((var16 & 512) != 0) {
         var17 |= 805306368;
      } else if ((var14 & 805306368) == 0) {
         var17 |= var13.changedInstance(var10) ? 536870912 : 268435456;
      }

      if ((var16 & 1024) != 0) {
         var18 |= 6;
      } else if ((var15 & 6) == 0) {
         var18 |= var13.changed(var11) ? 4 : 2;
      }

      if ((var16 & 2048) != 0) {
         var18 |= 48;
      } else if ((var15 & 48) == 0) {
         var18 |= var13.changed(var12) ? 32 : 16;
      }

      if ((var17 & 306783379) == 306783378 && (var18 & 19) == 18 && var13.getSkipping()) {
         var13.skipToGroupEnd();
      } else {
         if ((var16 & 1024) != 0) {
            int var19 = 0;
            int var20 = 0;
            var11 = Dp.recovered$95b3d103663bf060(var19);
         }

         if ((var16 & 2048) != 0) {
            var12 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1388262313, var17, var18, "io.intercom.android.sdk.m5.conversation.ui.components.row.MessageBlocks (BubbleMessageRow.kt:579)"
            );
         }

         Vertical var87 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(var11);
         int var21 = 14 & var18 >> 3;
         int var22 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var13, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Horizontal var88 = Alignment.Companion.getStart();
         MeasurePolicy var23 = ColumnKt.columnMeasurePolicy(var87, var88, var13, 14 & var21 >> 3 | 112 & var21 >> 3);
         Modifier var24 = var12;
         MeasurePolicy var25 = var23;
         Composer var26 = var13;
         int var27 = 112 & var21 << 3;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var26, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var29 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var26, 0));
         CompositionLocalMap var30 = var26.getCurrentCompositionLocalMap();
         Modifier var31 = ComposedModifierKt.materializeModifier(var26, var24);
         Function0 var32 = ComposeUiNode.Companion.getConstructor();
         Composer var33 = var26;
         int var34 = 6 | 896 & var27 << 6;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var33.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var33.startReusableNode();
         if (var33.getInserting()) {
            var33.createNode(var32);
         } else {
            var33.useNode();
         }

         Composer var36 = Updater.recovered$676aa17b44fd6280(var33);
         int var37 = 0;
         Updater.recovered$89682b199a142880(var36, var25, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var36, var30, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var36, var29, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var36, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var36, var31, ComposeUiNode.Companion.getSetModifier());
         int var38 = 14 & var34 >> 6;
         Composer var39 = var33;
         int var40 = 0;
         ComposerKt.sourceInformationMarkerStart(var39, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var41 = 6 | 112 & var21 >> 6;
         Composer var42 = var39;
         ColumnScope var43 = ColumnScopeInstance.INSTANCE;
         int var44 = 0;
         ComposerKt.sourceInformationMarkerStart(var42, -920776129, "C:BubbleMessageRow.kt#hr3bi5");
         var42.startReplaceGroup(-168249571);
         ComposerKt.sourceInformation(var42, "*585@27009L1683");
         Iterable var45 = var0;
         int var46 = 0;

         for (Object var48 : var45) {
            Block var49 = (Block)var48;
            int var50 = 0;
            byte var51 = 0;
            int var52 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var42, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
            );
            Modifier var53 = Modifier.Companion;
            Alignment var54 = Alignment.Companion.getTopStart();
            boolean var55 = false;
            MeasurePolicy measurePolicy$ivx = BoxKt.maybeCachedBoxMeasurePolicy(var54, var55);
            Modifier var57 = var53;
            MeasurePolicy var58 = measurePolicy$ivx;
            Composer var59 = var42;
            int var60 = 112 & var51 << 3;
            int $i$f$Layoutx = 0;
            ComposerKt.sourceInformationMarkerStart(var59, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var59, 0));
            CompositionLocalMap localMap$iv$ivx = var59.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var59, var57);
            Function0 var65 = ComposeUiNode.Companion.getConstructor();
            Composer var66 = var59;
            int var67 = 6 | 896 & var60 << 6;
            int $i$f$ReusableComposeNodex = 0;
            ComposerKt.sourceInformationMarkerStart(var66, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var66.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var66.startReusableNode();
            if (var66.getInserting()) {
               var66.createNode(var65);
            } else {
               var66.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var66);
            int var70/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var58, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
            int var71 = 14 & var67 >> 6;
            Composer $composer$ivx = var66;
            int var73 = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
            int var74 = 6 | 112 & var51 >> 6;
            Composer var75 = $composer$ivx;
            BoxScope var76 = BoxScopeInstance.INSTANCE;
            int var77 = 0;
            ComposerKt.sourceInformationMarkerStart(var75, 637654485, "C592@27366L10,593@27449L10,595@27587L10,586@27031L999:BubbleMessageRow.kt#hr3bi5");
            Modifier var10000 = ClipKt.clip(Modifier.Companion, var4);
            BlockRenderData var10001;
            Color var10004 = Color.recovered$f46ef195ad4a2363(var2);
            BlockRenderTextStyle var10007;
            long var10009 = IntercomTheme.INSTANCE.getTypography(var75, IntercomTheme.$stable).getType04().recovered$8f8bc2178e9073c3();
            FontWeight var10010 = IntercomTheme.INSTANCE.getTypography(var75, IntercomTheme.$stable).getType04().getFontWeight();
            if (var10010 == null) {
               var10010 = FontWeight.Companion.getNormal();
            }

            var10007 = new BlockRenderTextStyle(
               var10009,
               var10010,
               IntercomTheme.INSTANCE.getTypography(var75, IntercomTheme.$stable).getType04().recovered$17d1bb629b307c0f(),
               null,
               null,
               null,
               56,
               null
            );
            var10001 = new BlockRenderData(var49, var10004, null, null, var10007, 12, null);
            BlockViewKt.recovered$5709084683a88fd9(
               var10000,
               var10001,
               var6,
               null,
               var5,
               var1.getParentConversation().getId(),
               ImageRenderType.WITH_MAX_SIZE,
               0L,
               var9,
               var10,
               false,
               null,
               null,
               false,
               null,
               var75,
               1572864 | 896 & var17 >> 9 | 57344 & var17 | 234881024 & var17 | 1879048192 & var17,
               0,
               31880
            );
            var75.startReplaceGroup(-1503419658);
            ComposerKt.sourceInformation(var75, "607@28146L46,610@28384L6,611@28490L6,611@28442L62,609@28311L220,606@28104L556");
            if (var7 != null) {
               var75.startReplaceGroup(-1503417075);
               ComposerKt.sourceInformation(var75, "CC(remember):BubbleMessageRow.kt#9igjgp");
               Composer var78 = var75;
               boolean var79 = (var17 & 29360128) == 8388608 | ((var17 & 3670016) == 1048576 || (var17 & 2097152) != 0 && var13.changedInstance(var7));
               int var80 = 0;
               Object var81 = var78.rememberedValue();
               int var82 = 0;
               Object var89;
               if (!var79 && var81 != Composer.Companion.getEmpty()) {
                  var89 = var81;
               } else {
                  int var83 = 0;
                  final kotlin.jvm.functions.Function1 recoveredCapture_676_0 = var8;
                  final io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData recoveredCapture_676_1 = var7;
                  Object var84 = ((kotlin.jvm.functions.Function0) () -> BubbleMessageRowKt.MessageBlocks_5WxuK6Y$lambda$25$lambda$24$lambda$23$lambda$22$lambda$21(recoveredCapture_676_0, recoveredCapture_676_1));
                  var78.updateRememberedValue(var84);
                  var89 = var84;
               }

               Function0 var85 = (Function0)var89;
               var75.endReplaceGroup();
               ButtonKt.Button(
                  var85,
                  var76.align(Modifier.Companion, Alignment.Companion.getCenter()),
                  false,
                  null,
                  ButtonDefaults.INSTANCE
                     .recovered$6a8c28fb0b42a2fc(
                        IntercomTheme.INSTANCE.getColors(var75, IntercomTheme.$stable).recovered$202acdf3dbb5c020(),
                        ColorSchemeKt.recovered$e73d735ab97f03b1(
                           IntercomTheme.INSTANCE.getColors(var75, IntercomTheme.$stable).recovered$202acdf3dbb5c020(), var75, 0
                        ),
                        0L,
                        0L,
                        var75,
                        ButtonDefaults.$stable << 12,
                        12
                     ),
                  null,
                  null,
                  null,
                  null,
                  ComposableSingletons$BubbleMessageRowKt.INSTANCE.recovered$b26556cd5c386b55(),
                  var75,
                  805306368,
                  492
               );
            }

            var75.endReplaceGroup();
            ComposerKt.sourceInformationMarkerEnd(var75);
            ComposerKt.sourceInformationMarkerEnd($composer$ivx);
            var66.endNode();
            ComposerKt.sourceInformationMarkerEnd(var66);
            ComposerKt.sourceInformationMarkerEnd(var59);
            ComposerKt.sourceInformationMarkerEnd(var42);
         }

         var42.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var42);
         ComposerKt.sourceInformationMarkerEnd(var39);
         var33.endNode();
         ComposerKt.sourceInformationMarkerEnd(var33);
         ComposerKt.sourceInformationMarkerEnd(var26);
         ComposerKt.sourceInformationMarkerEnd(var13);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var90 = var13.endRestartGroup();
      if (var90 != null) {
         final java.util.List recoveredCapture_677_0 = var0;
         final io.intercom.android.sdk.models.Part recoveredCapture_677_1 = var1;
         final long recoveredCapture_677_2 = var2;
         final androidx.compose.ui.graphics.Shape recoveredCapture_677_3 = var4;
         final boolean recoveredCapture_677_4 = var5;
         final boolean recoveredCapture_677_5 = var6;
         final io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData recoveredCapture_677_6 = var7;
         final kotlin.jvm.functions.Function1 recoveredCapture_677_7 = var8;
         final kotlin.jvm.functions.Function0 recoveredCapture_677_8 = var9;
         final kotlin.jvm.functions.Function1 recoveredCapture_677_9 = var10;
         final float recoveredCapture_677_10 = var11;
         final androidx.compose.ui.Modifier recoveredCapture_677_11 = var12;
         final int recoveredCapture_677_12 = var14;
         final int recoveredCapture_677_13 = var15;
         final int recoveredCapture_677_14 = var16;
         var90.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_677_0, recoveredArgument_677_1) -> BubbleMessageRowKt.MessageBlocks_5WxuK6Y$lambda$26(recoveredCapture_677_0, recoveredCapture_677_1, recoveredCapture_677_2, recoveredCapture_677_3, recoveredCapture_677_4, recoveredCapture_677_5, recoveredCapture_677_6, recoveredCapture_677_7, recoveredCapture_677_8, recoveredCapture_677_9, recoveredCapture_677_10, recoveredCapture_677_11, recoveredCapture_677_12, recoveredCapture_677_13, recoveredCapture_677_14, (androidx.compose.runtime.Composer)recoveredArgument_677_0, ((java.lang.Integer)recoveredArgument_677_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void AttributeCollectorBubble(
      Part var0,
      GroupingPosition var1,
      MessageStyle var2,
      boolean var3,
      boolean var4,
      boolean var5,
      List var6,
      List var7,
      Function1 var8,
      Modifier var9,
      boolean var10,
      Composer var11,
      int var12,
      int var13,
      int var14
   ) {
      var11 = var11.startRestartGroup(96551334);
      ComposerKt.sourceInformation(var11, "C(AttributeCollectorBubble)P(!1,2,7,3,4,5!2,9)*654@30133L1114,644@29726L1521:BubbleMessageRow.kt#hr3bi5");
      int var15 = var12;
      int var16 = var13;
      if ((var14 & 1) != 0) {
         var15 |= 6;
      } else if ((var12 & 6) == 0) {
         var15 |= var11.changedInstance(var0) ? 4 : 2;
      }

      if ((var14 & 2) != 0) {
         var15 |= 48;
      } else if ((var12 & 48) == 0) {
         var15 |= var11.changed(var1) ? 32 : 16;
      }

      if ((var14 & 4) != 0) {
         var15 |= 384;
      } else if ((var12 & 384) == 0) {
         var15 |= var11.changed(var2) ? 256 : 128;
      }

      if ((var14 & 8) != 0) {
         var15 |= 3072;
      } else if ((var12 & 3072) == 0) {
         var15 |= var11.changed(var3) ? 2048 : 1024;
      }

      if ((var14 & 16) != 0) {
         var15 |= 24576;
      } else if ((var12 & 24576) == 0) {
         var15 |= var11.changed(var4) ? 16384 : 8192;
      }

      if ((var14 & 32) != 0) {
         var15 |= 196608;
      } else if ((var12 & 196608) == 0) {
         var15 |= var11.changed(var5) ? 131072 : 65536;
      }

      if ((var14 & 64) != 0) {
         var15 |= 1572864;
      } else if ((var12 & 1572864) == 0) {
         var15 |= var11.changedInstance(var6) ? 1048576 : 524288;
      }

      if ((var14 & 128) != 0) {
         var15 |= 12582912;
      } else if ((var12 & 12582912) == 0) {
         var15 |= var11.changedInstance(var7) ? 8388608 : 4194304;
      }

      if ((var14 & 256) != 0) {
         var15 |= 100663296;
      } else if ((var12 & 100663296) == 0) {
         var15 |= var11.changedInstance(var8) ? 67108864 : 33554432;
      }

      if ((var14 & 512) != 0) {
         var15 |= 805306368;
      } else if ((var12 & 805306368) == 0) {
         var15 |= var11.changed(var9) ? 536870912 : 268435456;
      }

      if ((var14 & 1024) != 0) {
         var16 |= 6;
      } else if ((var13 & 6) == 0) {
         var16 |= var11.changed(var10) ? 4 : 2;
      }

      if ((var15 & 306783379) == 306783378 && (var16 & 3) == 2 && var11.getSkipping()) {
         var11.skipToGroupEnd();
      } else {
         if ((var14 & 512) != 0) {
            var9 = Modifier.Companion;
         }

         if ((var14 & 1024) != 0) {
            var10 = false;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               96551334, var15, var16, "io.intercom.android.sdk.m5.conversation.ui.components.row.AttributeCollectorBubble (BubbleMessageRow.kt:642)"
            );
         }

         MessageStyle$BubbleStyle var17 = var2.getBubbleStyle();
         int var18 = 0;
         SurfaceKt.recovered$ecd79a99e04a9534(
            var9,
            calculateGroupedCornerShape(var3, var4, var5, var2.getBubbleStyle().getShape()),
            var17.recovered$6f15fcf6c761ad06(),
            0L,
            0.0F,
            0.0F,
            var17.getBorderStroke(),
            ComposableLambdaKt.rememberComposableLambda(
               -1048297056, true, new BubbleMessageRowKt$AttributeCollectorBubble$1$1(var17, var1, var10, var0, var6, var7, var8), var11, 54
            ),
            var11,
            12582912 | 14 & var15 >> 27,
            56
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var11.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.models.Part recoveredCapture_655_0 = var0;
         final io.intercom.android.sdk.m5.conversation.states.GroupingPosition recoveredCapture_655_1 = var1;
         final io.intercom.android.sdk.m5.conversation.ui.components.row.MessageStyle recoveredCapture_655_2 = var2;
         final boolean recoveredCapture_655_3 = var3;
         final boolean recoveredCapture_655_4 = var4;
         final boolean recoveredCapture_655_5 = var5;
         final java.util.List recoveredCapture_655_6 = var6;
         final java.util.List recoveredCapture_655_7 = var7;
         final kotlin.jvm.functions.Function1 recoveredCapture_655_8 = var8;
         final androidx.compose.ui.Modifier recoveredCapture_655_9 = var9;
         final boolean recoveredCapture_655_10 = var10;
         final int recoveredCapture_655_11 = var12;
         final int recoveredCapture_655_12 = var13;
         final int recoveredCapture_655_13 = var14;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_655_0, recoveredArgument_655_1) -> BubbleMessageRowKt.AttributeCollectorBubble$lambda$28(recoveredCapture_655_0, recoveredCapture_655_1, recoveredCapture_655_2, recoveredCapture_655_3, recoveredCapture_655_4, recoveredCapture_655_5, recoveredCapture_655_6, recoveredCapture_655_7, recoveredCapture_655_8, recoveredCapture_655_9, recoveredCapture_655_10, recoveredCapture_655_11, recoveredCapture_655_12, recoveredCapture_655_13, (androidx.compose.runtime.Composer)recoveredArgument_655_0, ((java.lang.Integer)recoveredArgument_655_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void PartAttributeCollectorCard(Part var0, List var1, List var2, Function1 var3, Modifier var4, Composer var5, int var6, int var7) {
      var5 = var5.startRestartGroup(1863819923);
      ComposerKt.sourceInformation(var5, "C(PartAttributeCollectorCard)P(!3,4)691@31610L402:BubbleMessageRow.kt#hr3bi5");
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var5.changedInstance(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var5.changedInstance(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var5.changedInstance(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var5.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 16) != 0) {
         var8 |= 24576;
      } else if ((var6 & 24576) == 0) {
         var8 |= var5.changed(var4) ? 16384 : 8192;
      }

      if ((var8 & 9363) == 9362 && var5.getSkipping()) {
         var5.skipToGroupEnd();
      } else {
         if ((var7 & 16) != 0) {
            var4 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1863819923, var8, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.PartAttributeCollectorCard (BubbleMessageRow.kt:690)"
            );
         }

         Modifier var9 = SizeKt.fillMaxWidth$default(var4, 0.0F, 1, null);
         List var10 = var0.getForm().getAttributes();
         String var10000 = var0.getId();
         Intrinsics.checkNotNullExpressionValue(var10000, "getId(...)");
         String var11 = var10000;
         boolean var12 = var0.getForm().getDisabled();
         AttributeCollectorCardKt.AttributeCollectorCard(
            var9, var10, var1, var2, var11, var12, var3, var5, 896 & var8 << 3 | 7168 & var8 << 3 | 3670016 & var8 << 9, 0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var14 = var5.endRestartGroup();
      if (var14 != null) {
         final io.intercom.android.sdk.models.Part recoveredCapture_665_0 = var0;
         final java.util.List recoveredCapture_665_1 = var1;
         final java.util.List recoveredCapture_665_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredCapture_665_3 = var3;
         final androidx.compose.ui.Modifier recoveredCapture_665_4 = var4;
         final int recoveredCapture_665_5 = var6;
         final int recoveredCapture_665_6 = var7;
         var14.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_665_0, recoveredArgument_665_1) -> BubbleMessageRowKt.PartAttributeCollectorCard$lambda$29(recoveredCapture_665_0, recoveredCapture_665_1, recoveredCapture_665_2, recoveredCapture_665_3, recoveredCapture_665_4, recoveredCapture_665_5, recoveredCapture_665_6, (androidx.compose.runtime.Composer)recoveredArgument_665_0, ((java.lang.Integer)recoveredArgument_665_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void EndUserMessageRowPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1439135167);
      ComposerKt.sourceInformation(var0, "C(EndUserMessageRowPreview)705@32091L2509:BubbleMessageRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1439135167, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.EndUserMessageRowPreview (BubbleMessageRow.kt:704)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$BubbleMessageRowKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_661_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_661_0, recoveredArgument_661_1) -> BubbleMessageRowKt.EndUserMessageRowPreview$lambda$30(recoveredCapture_661_0, (androidx.compose.runtime.Composer)recoveredArgument_661_0, ((java.lang.Integer)recoveredArgument_661_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void EndUserMessageRowWithAttachmentsPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(801443125);
      ComposerKt.sourceInformation(var0, "C(EndUserMessageRowWithAttachmentsPreview)771@34694L1352:BubbleMessageRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               801443125,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.EndUserMessageRowWithAttachmentsPreview (BubbleMessageRow.kt:770)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$BubbleMessageRowKt.INSTANCE.recovered$fd838d44303763da(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_662_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_662_0, recoveredArgument_662_1) -> BubbleMessageRowKt.EndUserMessageRowWithAttachmentsPreview$lambda$31(recoveredCapture_662_0, (androidx.compose.runtime.Composer)recoveredArgument_662_0, ((java.lang.Integer)recoveredArgument_662_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void AdminMessageRowPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1215138038);
      ComposerKt.sourceInformation(var0, "C(AdminMessageRowPreview)809@36123L2113:BubbleMessageRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1215138038, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.AdminMessageRowPreview (BubbleMessageRow.kt:808)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$BubbleMessageRowKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_654_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_654_0, recoveredArgument_654_1) -> BubbleMessageRowKt.AdminMessageRowPreview$lambda$32(recoveredCapture_654_0, (androidx.compose.runtime.Composer)recoveredArgument_654_0, ((java.lang.Integer)recoveredArgument_654_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void StreamingSenderTimestampBubbleMessageRowRtlPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(585978324);
      ComposerKt.sourceInformation(var0, "C(StreamingSenderTimestampBubbleMessageRowRtlPreview)866@38342L141:BubbleMessageRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               585978324,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.StreamingSenderTimestampBubbleMessageRowRtlPreview (BubbleMessageRow.kt:865)"
            );
         }

         CompositionLocalKt.CompositionLocalProvider(
            CompositionLocalsKt.getLocalLayoutDirection().provides(LayoutDirection.Rtl),
            ComposableSingletons$BubbleMessageRowKt.INSTANCE.recovered$265b6f46b79d310a(),
            var0,
            48 | ProvidedValue.$stable
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_674_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_674_0, recoveredArgument_674_1) -> BubbleMessageRowKt.StreamingSenderTimestampBubbleMessageRowRtlPreview$lambda$33(recoveredCapture_674_0, (androidx.compose.runtime.Composer)recoveredArgument_674_0, ((java.lang.Integer)recoveredArgument_674_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void StreamingSenderTimestampBubbleMessageRowPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1094075166);
      ComposerKt.sourceInformation(var0, "C(StreamingSenderTimestampBubbleMessageRowPreview)874@38586L3690:BubbleMessageRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1094075166,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.StreamingSenderTimestampBubbleMessageRowPreview (BubbleMessageRow.kt:873)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$BubbleMessageRowKt.INSTANCE.recovered$5a1850a8ff371bfb(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_673_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_673_0, recoveredArgument_673_1) -> BubbleMessageRowKt.StreamingSenderTimestampBubbleMessageRowPreview$lambda$34(recoveredCapture_673_0, (androidx.compose.runtime.Composer)recoveredArgument_673_0, ((java.lang.Integer)recoveredArgument_673_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SegmentedAdminMessageRowPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1675097524);
      ComposerKt.sourceInformation(var0, "C(SegmentedAdminMessageRowPreview)964@42442L594:BubbleMessageRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1675097524, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedAdminMessageRowPreview (BubbleMessageRow.kt:963)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$BubbleMessageRowKt.INSTANCE.recovered$3faf0e22fa74c6a7(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_668_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_668_0, recoveredArgument_668_1) -> BubbleMessageRowKt.SegmentedAdminMessageRowPreview$lambda$35(recoveredCapture_668_0, (androidx.compose.runtime.Composer)recoveredArgument_668_0, ((java.lang.Integer)recoveredArgument_668_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SegmentedAdminMessageRowCodeLeadingPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-859566031);
      ComposerKt.sourceInformation(var0, "C(SegmentedAdminMessageRowCodeLeadingPreview)989@43373L578:BubbleMessageRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -859566031,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedAdminMessageRowCodeLeadingPreview (BubbleMessageRow.kt:988)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$BubbleMessageRowKt.INSTANCE.recovered$a427d7fccaa25a17(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_666_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_666_0, recoveredArgument_666_1) -> BubbleMessageRowKt.SegmentedAdminMessageRowCodeLeadingPreview$lambda$36(recoveredCapture_666_0, (androidx.compose.runtime.Composer)recoveredArgument_666_0, ((java.lang.Integer)recoveredArgument_666_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SegmentedAdminMessageRowCodeNoMetadataPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(224214193);
      ComposerKt.sourceInformation(var0, "C(SegmentedAdminMessageRowCodeNoMetadataPreview)1014@44355L502:BubbleMessageRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               224214193,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedAdminMessageRowCodeNoMetadataPreview (BubbleMessageRow.kt:1013)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$BubbleMessageRowKt.INSTANCE.recovered$db063fabb945fca0(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_667_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_667_0, recoveredArgument_667_1) -> BubbleMessageRowKt.SegmentedAdminMessageRowCodeNoMetadataPreview$lambda$37(recoveredCapture_667_0, (androidx.compose.runtime.Composer)recoveredArgument_667_0, ((java.lang.Integer)recoveredArgument_667_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SegmentedAdminMessageRowTextOnlyPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-166406707);
      ComposerKt.sourceInformation(var0, "C(SegmentedAdminMessageRowTextOnlyPreview)1037@45085L583:BubbleMessageRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -166406707,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedAdminMessageRowTextOnlyPreview (BubbleMessageRow.kt:1036)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$BubbleMessageRowKt.INSTANCE.recovered$abc66e26750555d7(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_669_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_669_0, recoveredArgument_669_1) -> BubbleMessageRowKt.SegmentedAdminMessageRowTextOnlyPreview$lambda$38(recoveredCapture_669_0, (androidx.compose.runtime.Composer)recoveredArgument_669_0, ((java.lang.Integer)recoveredArgument_669_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SegmentedAttributeCollectorPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(6305419);
      ComposerKt.sourceInformation(var0, "C(SegmentedAttributeCollectorPreview)1061@45960L1310:BubbleMessageRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               6305419, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedAttributeCollectorPreview (BubbleMessageRow.kt:1060)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$BubbleMessageRowKt.INSTANCE.recovered$308c563a5ff7ee52(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_671_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_671_0, recoveredArgument_671_1) -> BubbleMessageRowKt.SegmentedAttributeCollectorPreview$lambda$39(recoveredCapture_671_0, (androidx.compose.runtime.Composer)recoveredArgument_671_0, ((java.lang.Integer)recoveredArgument_671_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SegmentedAttributeCollectorWithCodePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1228742658);
      ComposerKt.sourceInformation(var0, "C(SegmentedAttributeCollectorWithCodePreview)1101@47551L1341:BubbleMessageRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1228742658,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedAttributeCollectorWithCodePreview (BubbleMessageRow.kt:1100)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$BubbleMessageRowKt.INSTANCE.recovered$ce33619ac1c358ef(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_672_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_672_0, recoveredArgument_672_1) -> BubbleMessageRowKt.SegmentedAttributeCollectorWithCodePreview$lambda$40(recoveredCapture_672_0, (androidx.compose.runtime.Composer)recoveredArgument_672_0, ((java.lang.Integer)recoveredArgument_672_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void AttributeCollectorNoBlocksPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1410958780);
      ComposerKt.sourceInformation(var0, "C(AttributeCollectorNoBlocksPreview)1142@49285L1255:BubbleMessageRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1410958780, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.AttributeCollectorNoBlocksPreview (BubbleMessageRow.kt:1141)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$BubbleMessageRowKt.INSTANCE.recovered$5994e78f13d09dca(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_656_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_656_0, recoveredArgument_656_1) -> BubbleMessageRowKt.AttributeCollectorNoBlocksPreview$lambda$41(recoveredCapture_656_0, (androidx.compose.runtime.Composer)recoveredArgument_656_0, ((java.lang.Integer)recoveredArgument_656_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SegmentedAttributeCollectorNoBlocksPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1175525586);
      ComposerKt.sourceInformation(var0, "C(SegmentedAttributeCollectorNoBlocksPreview)1181@50795L1255:BubbleMessageRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1175525586,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedAttributeCollectorNoBlocksPreview (BubbleMessageRow.kt:1180)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$BubbleMessageRowKt.INSTANCE.recovered$46a4deae0c40d2ce(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_670_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_670_0, recoveredArgument_670_1) -> BubbleMessageRowKt.SegmentedAttributeCollectorNoBlocksPreview$lambda$42(recoveredCapture_670_0, (androidx.compose.runtime.Composer)recoveredArgument_670_0, ((java.lang.Integer)recoveredArgument_670_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void MessageMeta(
      @Nullable Modifier var0, @NotNull String var1, @NotNull String var2, boolean var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "metaString");
      Intrinsics.checkNotNullParameter(var2, "attributeString");
      var4 = var4.startRestartGroup(302477331);
      ComposerKt.sourceInformation(var4, "C(MessageMeta)P(3,2)1236@52728L6,1237@52750L808:BubbleMessageRow.kt#hr3bi5");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var4.changed(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var4.changed(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var4.changed(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if ((var6 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(302477331, var7, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.MessageMeta (BubbleMessageRow.kt:1235)");
         }

         long var8 = IntercomTheme.INSTANCE.getColors(var4, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
         androidx.compose.foundation.layout.Arrangement.Horizontal var10 = Arrangement.INSTANCE.getSpaceBetween();
         int var12 = 48 | 14 & var7;
         int var13 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var4, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         androidx.compose.ui.Alignment.Vertical var11 = Alignment.Companion.getTop();
         MeasurePolicy var14 = RowKt.rowMeasurePolicy(var10, var11, var4, 14 & var12 >> 3 | 112 & var12 >> 3);
         Modifier var15 = var0;
         MeasurePolicy var16 = var14;
         Composer var17 = var4;
         int var18 = 112 & var12 << 3;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(var17, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var20 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var17, 0));
         CompositionLocalMap var21 = var17.getCurrentCompositionLocalMap();
         Modifier var22 = ComposedModifierKt.materializeModifier(var17, var15);
         Function0 var23 = ComposeUiNode.Companion.getConstructor();
         Composer var24 = var17;
         int var25 = 6 | 896 & var18 << 6;
         int var26 = 0;
         ComposerKt.sourceInformationMarkerStart(var24, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var24.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var24.startReusableNode();
         if (var24.getInserting()) {
            var24.createNode(var23);
         } else {
            var24.useNode();
         }

         Composer var27 = Updater.recovered$676aa17b44fd6280(var24);
         int var28 = 0;
         Updater.recovered$89682b199a142880(var27, var16, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var27, var21, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var27, var20, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var27, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var27, var22, ComposeUiNode.Companion.getSetModifier());
         int var29 = 14 & var25 >> 6;
         Composer var30 = var24;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var32 = 6 | 112 & var12 >> 6;
         Composer var33 = var30;
         RowScope var34 = RowScopeInstance.INSTANCE;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, -83427475, "C1251@53207L10,1249@53136L137:BubbleMessageRow.kt#hr3bi5");
         var33.startReplaceGroup(-1111070291);
         ComposerKt.sourceInformation(var33, "1245@53043L10,1242@52902L215");
         if (!var3) {
            Modifier var10000 = Modifier.Companion;
            int var36 = 8;
            int var37 = 0;
            Modifier var38 = PaddingKt.recovered$03b481d31d62885a(var10000, 0.0F, 0.0F, Dp.recovered$95b3d103663bf060(var36), 0.0F, 11, null);
            TextStyle var40 = IntercomTheme.INSTANCE.getTypography(var33, IntercomTheme.$stable).getType05();
            TextKt.recovered$5b114ab32a4f4190(
               var2, var38, var8, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var40, var33, 48 | 14 & var7 >> 6, 0, 131064
            );
         }

         var33.endReplaceGroup();
         TextStyle var44 = IntercomTheme.INSTANCE.getTypography(var33, IntercomTheme.$stable).getType05();
         TextKt.recovered$5b114ab32a4f4190(
            var1, null, var8, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var44, var33, 14 & var7 >> 3, 0, 131066
         );
         var33.startReplaceGroup(-1111056722);
         ComposerKt.sourceInformation(var33, "1258@53468L10,1255@53325L217");
         if (var3) {
            Modifier var46 = Modifier.Companion;
            int var41 = 8;
            int var43 = 0;
            Modifier var45 = PaddingKt.recovered$03b481d31d62885a(var46, Dp.recovered$95b3d103663bf060(var41), 0.0F, 0.0F, 0.0F, 14, null);
            TextStyle var42 = IntercomTheme.INSTANCE.getTypography(var33, IntercomTheme.$stable).getType05();
            TextKt.recovered$5b114ab32a4f4190(
               var2, var45, var8, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var42, var33, 48 | 14 & var7 >> 6, 0, 131064
            );
         }

         var33.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var33);
         ComposerKt.sourceInformationMarkerEnd(var30);
         var24.endNode();
         ComposerKt.sourceInformationMarkerEnd(var24);
         ComposerKt.sourceInformationMarkerEnd(var17);
         ComposerKt.sourceInformationMarkerEnd(var4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var47 = var4.endRestartGroup();
      if (var47 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_664_0 = var0;
         final java.lang.String recoveredCapture_664_1 = var1;
         final java.lang.String recoveredCapture_664_2 = var2;
         final boolean recoveredCapture_664_3 = var3;
         final int recoveredCapture_664_4 = var5;
         final int recoveredCapture_664_5 = var6;
         var47.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_664_0, recoveredArgument_664_1) -> BubbleMessageRowKt.MessageMeta$lambda$44(recoveredCapture_664_0, recoveredCapture_664_1, recoveredCapture_664_2, recoveredCapture_664_3, recoveredCapture_664_4, recoveredCapture_664_5, (androidx.compose.runtime.Composer)recoveredArgument_664_0, ((java.lang.Integer)recoveredArgument_664_1).intValue())));
      }
   }

   @NotNull
   public static final AnnotatedString getCopyText(@NotNull Part var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      int var2 = 0;
      Builder var3 = new Builder(0, 1, null);
      Builder var4 = var3;
      int var5 = 0;

      for (java.lang.Object recoveredEntryElement_0_2634 : var0.getBlocks()) {
         Block var7 = (Block) recoveredEntryElement_0_2634;
         BlockType var10000 = var7.getType();
         switch (var10000 == null ? -1 : BubbleMessageRowKt$WhenMappings.$EnumSwitchMapping$0[var10000.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
               var4.append(HtmlCompat.fromHtml(var7.getText(), 0).toString());
               break;
            case 5:
               String var10001 = var7.getUrl();
               Intrinsics.checkNotNullExpressionValue(var10001, "getUrl(...)");
               var4.append(var10001);
               break;
            case 6:
            case 7:
               for (java.lang.Object recoveredEntryElement_0_2650 : var7.getItems()) {
                  String var9 = (String) recoveredEntryElement_0_2650;
                  Intrinsics.checkNotNull(var9);
                  var4.append(var9);
               }
         }
      }

      AnnotatedString var1 = var3.toAnnotatedString();
      CharSequence var10 = var1;
      CharSequence var12;
      if (var10.length() == 0) {
         int var11 = 0;
         String var10002 = var0.getSummary();
         Intrinsics.checkNotNullExpressionValue(var10002, "getSummary(...)");
         var12 = new AnnotatedString(var10002, null, 2, null);
      } else {
         var12 = var10;
      }

      return (AnnotatedString)var12;
   }

   private static final boolean isCreateTicketEnabled(Part var0) {
      List var10000 = var0.getBlocks();
      Intrinsics.checkNotNullExpressionValue(var10000, "getBlocks(...)");
      var var2 = var10000;
      int var3 = 0;
      boolean var9;
      if (var2 instanceof Collection && ((Collection)var2).isEmpty()) {
         var9 = false;
      } else {
         label50: {
            for (Object var5 : var2) {
               Block var6 = (Block)var5;
               int var7 = 0;
               if (var6.getType() == BlockType.CREATETICKETCARD) {
                  var9 = true;
                  break label50;
               }
            }

            var9 = false;
         }
      }

      boolean var1 = var9;
      boolean var8 = var0.getParentConversation().getTicket() != null;
      return !var1 || !var8;
   }

   private static final CategorizedBlocks categorizeBlocks(List var0, boolean var1) {
      if (var1) {
         return new CategorizedBlocks(null, null, null, var0, 7, null);
      }

      Iterable var2 = var0;
      int var3 = 0;
      Iterable var4 = var2;
      ArrayList var5 = new ArrayList();
      int var6 = 0;

      for (Object var8 : var4) {
         boolean var50;
         label80: {
            Block var9 = (Block)var8;
            int var10 = 0;
            if (imageBlockTypes.contains(var9.getType())) {
               String var10000 = var9.getAttribution();
               Intrinsics.checkNotNullExpressionValue(var10000, "getAttribution(...)");
               if (var10000.length() == 0) {
                  var50 = true;
                  break label80;
               }
            }

            var50 = false;
         }

         if (var50) {
            var5.add(var8);
         }
      }

      List var51 = (List)var5;
      var2 = var0;
      List var11 = var51;
      var3 = 0;
      var4 = var2;
      var5 = new ArrayList();
      var6 = 0;

      for (Object var37 : var4) {
         Block var40 = (Block)var37;
         int var43 = 0;
         if (videoBlockTypes.contains(var40.getType())) {
            var5.add(var37);
         }
      }

      List var10001 = (List)var5;
      var2 = var0;
      List var12 = var10001;
      var11 = var11;
      var3 = 0;
      var4 = var2;
      var5 = new ArrayList();
      var6 = 0;

      for (Object var38 : var4) {
         Block var41 = (Block)var38;
         int var44 = 0;
         if (attachmentBlockTypes.contains(var41.getType())) {
            var5.add(var38);
         }
      }

      List var13 = (List)var5;
      var2 = var0;
      var13 = var13;
      var12 = var12;
      var11 = var11;
      var3 = 0;
      var4 = var2;
      var5 = new ArrayList();
      var6 = 0;

      for (Object var39 : var4) {
         boolean var53;
         label57: {
            label56: {
               Block var42 = (Block)var39;
               int var45 = 0;
               if (imageBlockTypes.contains(var42.getType())) {
                  String var52 = var42.getAttribution();
                  Intrinsics.checkNotNullExpressionValue(var52, "getAttribution(...)");
                  if (var52.length() == 0) {
                     break label56;
                  }
               }

               if (!videoBlockTypes.contains(var42.getType()) && !attachmentBlockTypes.contains(var42.getType())) {
                  var53 = false;
                  break label57;
               }
            }

            var53 = true;
         }

         if (!var53) {
            var5.add(var39);
         }
      }

      List var14 = (List)var5;
      List var15 = var14;
      List var16 = var13;
      List var17 = var12;
      List var18 = var11;
      return new CategorizedBlocks(var18, var17, var16, var15);
   }

   private static final Block createVideoBlock(String var0) {
      Block var10000 = new Block$Builder().withUrl(var0).withType("VIDEOFILE").build();
      Intrinsics.checkNotNullExpressionValue(var10000, "build(...)");
      return var10000;
   }

   private static final Block createAttachmentListBlock(List var0) {
      Block$Builder var10000 = new Block$Builder().withType("ATTACHMENTLIST");
      Iterable var1 = var0;
      Block$Builder var10 = var10000;
      int var2 = 0;
      Iterable var3 = var1;
      ArrayList var4 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var1, 10));
      int var5 = 0;

      for (Object var7 : var3) {
         Attachments var8 = (Attachments)var7;
         Collection var11 = var4;
         int var9 = 0;
         var11.add(
            BlockAttachment$Companion.create$default(
               BlockAttachment.Companion, var8.getName(), var8.getUrl(), var8.getContentType(), var8.getHumanFileSize(), 0L, 0L, 48, null
            )
         );
      }

      Block var12 = var10.withAttachments((List)var4).build();
      Intrinsics.checkNotNullExpressionValue(var12, "build(...)");
      return var12;
   }

   private static final Block removeVideoAttachmentsFromBlock(Block var0, List var1) {
      if (var0.getAttachments().isEmpty()) {
         return var0;
      }

      List var10000 = var0.getAttachments();
      Intrinsics.checkNotNullExpressionValue(var10000, "getAttachments(...)");
      Iterable var3 = var10000;
      int var4 = 0;
      ArrayList var5 = new ArrayList();
      ArrayList var6 = new ArrayList();

      for (Object var8 : var3) {
         BlockAttachment var9 = (BlockAttachment)var8;
         int var10 = 0;
         if (ContentTypeExtensionKt.isVideo(var9.getContentType())) {
            var5.add(var8);
         } else {
            var6.add(var8);
         }
      }

      Pair var2 = new Pair(var5, var6);
      List recoveredVideoAttachments = (List)var2.component1();
      List var12 = (List)var2.component2();
      Iterable var13 = recoveredVideoAttachments;
      int var14 = 0;

      for (Object var16 : var13) {
         BlockAttachment var17 = (BlockAttachment)var16;
         int var18 = 0;
         var1.add(createVideoBlock(var17.getUrl()));
      }

      return recoveredVideoAttachments.isEmpty()
         ? var0
         : (var12.isEmpty() && attachmentBlockTypes.contains(var0.getType()) ? null : var0.toBuilder().withAttachments(var12).build());
   }

   private static final List convertAttachmentsToBlocks(Part var0, boolean var1) {
      if (var0.getAttachments().isEmpty()) {
         return CollectionsKt.emptyList();
      }

      List var12;
      if (var1) {
         var12 = var0.getAttachments();
         Intrinsics.checkNotNullExpressionValue(var12, "getAttachments(...)");
         var12 = CollectionsKt.listOf(createAttachmentListBlock(var12));
      } else {
         var12 = var0.getAttachments();
         Intrinsics.checkNotNullExpressionValue(var12, "getAttachments(...)");
         Iterable var2 = var12;
         int var3 = 0;
         Iterable var4 = var2;
         ArrayList var5 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var2, 10));
         int var6 = 0;

         for (Object var8 : var4) {
            Attachments var9 = (Attachments)var8;
            Collection var11 = var5;
            int var10 = 0;
            var11.add(
               ContentTypeExtensionKt.isVideo(var9.getContentType()) ? createVideoBlock(var9.getUrl()) : createAttachmentListBlock(CollectionsKt.listOf(var9))
            );
         }

         var12 = (List)var5;
      }

      return var12;
   }

   private static final ProcessedBlocks extractAndCleanAttachmentBlocks(Part var0, boolean var1) {
      List var2 = convertAttachmentsToBlocks(var0, var1);
      if (var1) {
         List var10002 = var0.getBlocks();
         Intrinsics.checkNotNullExpressionValue(var10002, "getBlocks(...)");
         return new ProcessedBlocks(var10002, var2);
      }

      List var3 = new ArrayList();
      List var10000 = var0.getBlocks();
      Intrinsics.checkNotNullExpressionValue(var10000, "getBlocks(...)");
      Iterable var5 = var10000;
      int var6 = 0;
      Iterable var7 = var5;
      ArrayList var8 = new ArrayList();
      int var9 = 0;
      Iterable var10 = var7;
      int var11 = 0;

      for (Object var13 : var10) {
         Object var14 = var13;
         int var15 = 0;
         Block var16 = (Block)var14;
         int var17 = 0;
         Intrinsics.checkNotNull(var16);
         Block var20 = removeVideoAttachmentsFromBlock(var16, var3);
         if (var20 != null) {
            Object var18 = var20;
            int var19 = 0;
            var8.add(var18);
         }
      }

      List var4 = (List)var8;
      return new ProcessedBlocks(var4, CollectionsKt.plus(var2, var3));
   }

   @Composable
   public static final float contentAlpha(boolean var0, @Nullable Composer var1, int var2) {
      var1.startReplaceGroup(1168284893);
      ComposerKt.sourceInformation(var1, "C(contentAlpha):BubbleMessageRow.kt#hr3bi5");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(1168284893, var2, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.contentAlpha (BubbleMessageRow.kt:1415)");
      }

      float var3 = var0 ? 1.0F : 0.38F;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var1.endReplaceGroup();
      return var3;
   }

   public static final Block$Builder getParagraphBlock() {
      return paragraphBlock;
   }

   public static final Block$Builder getLongParagraphBlock() {
      return longParagraphBlock;
   }

   public static final Block$Builder getCreateTicketBlock() {
      return createTicketBlock;
   }

   public static final Block$Builder getAnswerBlock() {
      return answerBlock;
   }

   public static final Block$Builder getArticleBlock() {
      return articleBlock;
   }

   @NotNull
   public static final io.intercom.android.sdk.models.Metadata getHumanMetadata() {
      return humanMetadata;
   }

   public static final Block$Builder getCodeBlock() {
      return codeBlock;
   }

   public static final Block$Builder getImageBlock() {
      return imageBlock;
   }

   public static final Block$Builder getAttachmentBlock() {
      return attachmentBlock;
   }

   public static final Block$Builder getVideoBlock() {
      return videoBlock;
   }

   @NotNull
   public static final RoundedCornerShape calculateGroupedCornerShape(boolean var0, boolean var1, boolean var2, @NotNull RoundedCornerShape var3) {
      Intrinsics.checkNotNullParameter(var3, "baseShape");
      return !var0 && !var1
         ? var3
         : (
            var0 && !var1
               ? (
                  var2
                     ? (RoundedCornerShape)CornerBasedShape.copy$default(
                        var3, CornerSizeKt.recovered$ea3a4e3d7147c535(GROUPED_CORNER_RADIUS), null, null, null, 14, null
                     )
                     : (RoundedCornerShape)CornerBasedShape.copy$default(
                        var3, null, CornerSizeKt.recovered$ea3a4e3d7147c535(GROUPED_CORNER_RADIUS), null, null, 13, null
                     )
               )
               : (
                  !var0 && var1
                     ? (
                        var2
                           ? (RoundedCornerShape)CornerBasedShape.copy$default(
                              var3, null, null, null, CornerSizeKt.recovered$ea3a4e3d7147c535(GROUPED_CORNER_RADIUS), 7, null
                           )
                           : (RoundedCornerShape)CornerBasedShape.copy$default(
                              var3, null, null, CornerSizeKt.recovered$ea3a4e3d7147c535(GROUPED_CORNER_RADIUS), null, 11, null
                           )
                     )
                     : (
                        var2
                           ? (RoundedCornerShape)CornerBasedShape.copy$default(
                              var3,
                              CornerSizeKt.recovered$ea3a4e3d7147c535(GROUPED_CORNER_RADIUS),
                              null,
                              null,
                              CornerSizeKt.recovered$ea3a4e3d7147c535(GROUPED_CORNER_RADIUS),
                              6,
                              null
                           )
                           : (RoundedCornerShape)CornerBasedShape.copy$default(
                              var3,
                              null,
                              CornerSizeKt.recovered$ea3a4e3d7147c535(GROUPED_CORNER_RADIUS),
                              CornerSizeKt.recovered$ea3a4e3d7147c535(GROUPED_CORNER_RADIUS),
                              null,
                              9,
                              null
                           )
                     )
               )
         );
   }

   private static final void launchPreviewActivity(Context var0, String var1, String var2, int var3, int var4, int var5, int var6) {
      IntercomPreviewFile$NetworkFile var10000 = IntercomPreviewFile.Companion.network(var1, var2);
      if (var10000 != null) {
         IntercomPreviewFile$NetworkFile var7 = var10000;
         IntercomPreviewActivity$Companion var10001 = IntercomPreviewActivity.Companion;
         List var10005 = CollectionsKt.listOf(var7);
         boolean var8 = var1.length() > 0;
         String var10010 = var0.getString(var3);
         Intrinsics.checkNotNullExpressionValue(var10010, "getString(...)");
         String var9 = var10010;
         var10010 = var0.getString(var4);
         Intrinsics.checkNotNullExpressionValue(var10010, "getString(...)");
         String var10 = var10010;
         var10010 = var0.getString(var5);
         Intrinsics.checkNotNullExpressionValue(var10010, "getString(...)");
         String var11 = var10010;
         var10010 = var0.getString(var6);
         Intrinsics.checkNotNullExpressionValue(var10010, "getString(...)");
         String var12 = var10010;
         var0.startActivity(
            var10001.createIntent(var0, new IntercomPreviewArgs(var10005, null, null, false, 0, new DownloadState(var8, var10, var9, var11, var12), 30, null))
         );
      }
   }

   // $VF: synthetic method
   static void launchPreviewActivity$default(Context var0, String var1, String var2, int var3, int var4, int var5, int var6, int var7, Object var8) {
      if ((var7 & 4) != 0) {
         var3 = string.intercom_file_saved;
      }

      if ((var7 & 8) != 0) {
         var4 = string.intercom_saving;
      }

      if ((var7 & 16) != 0) {
         var5 = string.intercom_something_went_wrong_try_again;
      }

      if ((var7 & 32) != 0) {
         var6 = string.intercom_permission_denied;
      }

      launchPreviewActivity(var0, var1, var2, var3, var4, var5, var6);
   }

   public static final boolean shouldShowAttribution(@NotNull Part var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      if (var0.getBlocks().size() == 1) {
         Set var10000 = imageBlockTypes;
         List var10001 = var0.getBlocks();
         Intrinsics.checkNotNullExpressionValue(var10001, "getBlocks(...)");
         if (var10000.contains(((Block)CollectionsKt.first(var10001)).getType())) {
            List var1 = var0.getBlocks();
            Intrinsics.checkNotNullExpressionValue(var1, "getBlocks(...)");
            String var2 = ((Block)CollectionsKt.first(var1)).getAttribution();
            Intrinsics.checkNotNullExpressionValue(var2, "getAttribution(...)");
            if (var2.length() > 0 && var0.getMessageState() == Part$MessageState.NORMAL) {
               return true;
            }
         }
      }

      return false;
   }

   @NotNull
   public static final RoundedCornerShape recovered$2105a72475a7f489(@NotNull RoundedCornerShape var0, int var1, int var2, float var3) {
      Intrinsics.checkNotNullParameter(var0, "cornerShape");
      boolean var4 = var1 == 0;
      boolean var5 = var1 == var2 - 1;
      boolean var6 = var2 == 1;
      return var6
         ? var0
         : (
            var4
               ? (RoundedCornerShape)CornerBasedShape.copy$default(var0, null, null, CornerSizeKt.recovered$ea3a4e3d7147c535(var3), null, 11, null)
               : (
                  var5
                     ? (RoundedCornerShape)CornerBasedShape.copy$default(var0, null, CornerSizeKt.recovered$ea3a4e3d7147c535(var3), null, null, 13, null)
                     : (RoundedCornerShape)CornerBasedShape.copy$default(
                        var0, null, CornerSizeKt.recovered$ea3a4e3d7147c535(var3), CornerSizeKt.recovered$ea3a4e3d7147c535(var3), null, 9, null
                     )
               )
         );
   }

   // $VF: synthetic method
   public static RoundedCornerShape recovered$64fcaea88d5ad8c0(RoundedCornerShape var0, int var1, int var2, float var3, int var4, Object var5) {
      if ((var4 & 8) != 0) {
         var3 = GROUPED_CORNER_RADIUS;
      }

      return recovered$2105a72475a7f489(var0, var1, var2, var3);
   }

   private static final Unit BubbleMessageRow$lambda$1$lambda$0(AttributeData var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit BubbleMessageRow$lambda$3$lambda$2(PendingMessage$FailedMediaUploadData var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit BubbleMessageRow$lambda$5$lambda$4(TicketType var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit BubbleMessageRow$lambda$11(
      Part var0,
      GroupingPosition var1,
      boolean var2,
      Modifier var3,
      String var4,
      SenderMetadata var5,
      Function1 var6,
      List var7,
      List var8,
      Function1 var9,
      PendingMessage$FailedMediaUploadData var10,
      FailedMessage var11,
      Function1 var12,
      int var13,
      int var14,
      int var15,
      Composer var16,
      int var17
   ) {
      BubbleMessageRow(
         var0,
         var1,
         var2,
         var3,
         var4,
         var5,
         var6,
         var7,
         var8,
         var9,
         var10,
         var11,
         var12,
         var16,
         RecomposeScopeImplKt.updateChangedFlags(var13 | 1),
         RecomposeScopeImplKt.updateChangedFlags(var14),
         var15
      );
      return Unit.INSTANCE;
   }

   private static final Unit SegmentedBubbleContent_E1AOrGg$lambda$17(
      Part var0,
      GroupingPosition var1,
      List var2,
      MessageStyle var3,
      boolean var4,
      boolean var5,
      long var6,
      Function1 var8,
      PendingMessage$FailedMediaUploadData var9,
      Function1 var10,
      Function0 var11,
      boolean var12,
      int var13,
      int var14,
      int var15,
      Composer var16,
      int var17
   ) {
      recovered$5dd6a8902c85f195(
         var0,
         var1,
         var2,
         var3,
         var4,
         var5,
         var6,
         var8,
         var9,
         var10,
         var11,
         var12,
         var16,
         RecomposeScopeImplKt.updateChangedFlags(var13 | 1),
         RecomposeScopeImplKt.updateChangedFlags(var14),
         var15
      );
      return Unit.INSTANCE;
   }

   private static final Unit FailedMessageIcon$lambda$18(Modifier var0, int var1, int var2, Composer var3, int var4) {
      FailedMessageIcon(var0, var3, RecomposeScopeImplKt.updateChangedFlags(var1 | 1), var2);
      return Unit.INSTANCE;
   }

   private static final Unit MessageContent_hcemMNk$lambda$20(
      Part var0,
      long var1,
      boolean var3,
      Shape var4,
      Function0 var5,
      Function1 var6,
      boolean var7,
      PendingMessage$FailedMediaUploadData var8,
      Function1 var9,
      float var10,
      List var11,
      int var12,
      int var13,
      int var14,
      Composer var15,
      int var16
   ) {
      recovered$e9eb3be55737358d(
         var0,
         var1,
         var3,
         var4,
         var5,
         var6,
         var7,
         var8,
         var9,
         var10,
         var11,
         var15,
         RecomposeScopeImplKt.updateChangedFlags(var12 | 1),
         RecomposeScopeImplKt.updateChangedFlags(var13),
         var14
      );
      return Unit.INSTANCE;
   }

   private static final Unit MessageBlocks_5WxuK6Y$lambda$25$lambda$24$lambda$23$lambda$22$lambda$21(Function1 var0, PendingMessage$FailedMediaUploadData var1) {
      var0.invoke(var1);
      return Unit.INSTANCE;
   }

   private static final Unit MessageBlocks_5WxuK6Y$lambda$26(
      List var0,
      Part var1,
      long var2,
      Shape var4,
      boolean var5,
      boolean var6,
      PendingMessage$FailedMediaUploadData var7,
      Function1 var8,
      Function0 var9,
      Function1 var10,
      float var11,
      Modifier var12,
      int var13,
      int var14,
      int var15,
      Composer var16,
      int var17
   ) {
      recovered$5fb12a358761c608(
         var0,
         var1,
         var2,
         var4,
         var5,
         var6,
         var7,
         var8,
         var9,
         var10,
         var11,
         var12,
         var16,
         RecomposeScopeImplKt.updateChangedFlags(var13 | 1),
         RecomposeScopeImplKt.updateChangedFlags(var14),
         var15
      );
      return Unit.INSTANCE;
   }

   private static final Unit AttributeCollectorBubble$lambda$28(
      Part var0,
      GroupingPosition var1,
      MessageStyle var2,
      boolean var3,
      boolean var4,
      boolean var5,
      List var6,
      List var7,
      Function1 var8,
      Modifier var9,
      boolean var10,
      int var11,
      int var12,
      int var13,
      Composer var14,
      int var15
   ) {
      AttributeCollectorBubble(
         var0,
         var1,
         var2,
         var3,
         var4,
         var5,
         var6,
         var7,
         var8,
         var9,
         var10,
         var14,
         RecomposeScopeImplKt.updateChangedFlags(var11 | 1),
         RecomposeScopeImplKt.updateChangedFlags(var12),
         var13
      );
      return Unit.INSTANCE;
   }

   private static final Unit PartAttributeCollectorCard$lambda$29(
      Part var0, List var1, List var2, Function1 var3, Modifier var4, int var5, int var6, Composer var7, int var8
   ) {
      PartAttributeCollectorCard(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit EndUserMessageRowPreview$lambda$30(int var0, Composer var1, int var2) {
      EndUserMessageRowPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit EndUserMessageRowWithAttachmentsPreview$lambda$31(int var0, Composer var1, int var2) {
      EndUserMessageRowWithAttachmentsPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit AdminMessageRowPreview$lambda$32(int var0, Composer var1, int var2) {
      AdminMessageRowPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit StreamingSenderTimestampBubbleMessageRowRtlPreview$lambda$33(int var0, Composer var1, int var2) {
      StreamingSenderTimestampBubbleMessageRowRtlPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit StreamingSenderTimestampBubbleMessageRowPreview$lambda$34(int var0, Composer var1, int var2) {
      StreamingSenderTimestampBubbleMessageRowPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SegmentedAdminMessageRowPreview$lambda$35(int var0, Composer var1, int var2) {
      SegmentedAdminMessageRowPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SegmentedAdminMessageRowCodeLeadingPreview$lambda$36(int var0, Composer var1, int var2) {
      SegmentedAdminMessageRowCodeLeadingPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SegmentedAdminMessageRowCodeNoMetadataPreview$lambda$37(int var0, Composer var1, int var2) {
      SegmentedAdminMessageRowCodeNoMetadataPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SegmentedAdminMessageRowTextOnlyPreview$lambda$38(int var0, Composer var1, int var2) {
      SegmentedAdminMessageRowTextOnlyPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SegmentedAttributeCollectorPreview$lambda$39(int var0, Composer var1, int var2) {
      SegmentedAttributeCollectorPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SegmentedAttributeCollectorWithCodePreview$lambda$40(int var0, Composer var1, int var2) {
      SegmentedAttributeCollectorWithCodePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit AttributeCollectorNoBlocksPreview$lambda$41(int var0, Composer var1, int var2) {
      AttributeCollectorNoBlocksPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SegmentedAttributeCollectorNoBlocksPreview$lambda$42(int var0, Composer var1, int var2) {
      SegmentedAttributeCollectorNoBlocksPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit MessageMeta$lambda$44(Modifier var0, String var1, String var2, boolean var3, int var4, int var5, Composer var6, int var7) {
      MessageMeta(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final io.intercom.android.sdk.models.Metadata access$getBotPreviewMetadata$p() {
      return botPreviewMetadata;
   }

   // $VF: synthetic method
   public static final io.intercom.android.sdk.models.Metadata access$getHumanPreviewMetadata$p() {
      return humanPreviewMetadata;
   }

   // $VF: synthetic method
   public static final void access$AttributeCollectorBubble(
      Part var0,
      GroupingPosition var1,
      MessageStyle var2,
      boolean var3,
      boolean var4,
      boolean var5,
      List var6,
      List var7,
      Function1 var8,
      Modifier var9,
      boolean var10,
      Composer var11,
      int var12,
      int var13,
      int var14
   ) {
      AttributeCollectorBubble(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
   }

   // $VF: synthetic method
   public static final void recovered$21a7c0d1d5f487b2(
      Part var0,
      GroupingPosition var1,
      List var2,
      MessageStyle var3,
      boolean var4,
      boolean var5,
      long var6,
      Function1 var8,
      PendingMessage$FailedMediaUploadData var9,
      Function1 var10,
      Function0 var11,
      boolean var12,
      Composer var13,
      int var14,
      int var15,
      int var16
   ) {
      recovered$5dd6a8902c85f195(var0, var1, var2, var3, var4, var5, var6, var8, var9, var10, var11, var12, var13, var14, var15, var16);
   }

   // $VF: synthetic method
   public static final void access$FailedMessageIcon(Modifier var0, Composer var1, int var2, int var3) {
      FailedMessageIcon(var0, var1, var2, var3);
   }

   // $VF: synthetic method
   public static final void recovered$19fe6bd444e58e9d(
      List var0,
      Part var1,
      long var2,
      Shape var4,
      boolean var5,
      boolean var6,
      PendingMessage$FailedMediaUploadData var7,
      Function1 var8,
      Function0 var9,
      Function1 var10,
      float var11,
      Modifier var12,
      Composer var13,
      int var14,
      int var15,
      int var16
   ) {
      recovered$5fb12a358761c608(var0, var1, var2, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
   }

   // $VF: synthetic method
   public static final void access$PartAttributeCollectorCard(Part var0, List var1, List var2, Function1 var3, Modifier var4, Composer var5, int var6, int var7) {
      PartAttributeCollectorCard(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   static {
      int var0 = 20;
      int var1 = 0;
      DEFAULT_CORNER_RADIUS = Dp.recovered$95b3d103663bf060(var0);
      var0 = 4;
      var1 = 0;
      GROUPED_CORNER_RADIUS = Dp.recovered$95b3d103663bf060(var0);
      var0 = 44;
      var1 = 0;
      ADMIN_BUBBLE_END_GUTTER = Dp.recovered$95b3d103663bf060(var0);
      List var4 = CollectionsKt.listOf(new Avatar$Builder().withInitials("L"));
      humanMetadata = new io.intercom.android.sdk.models.Metadata("Lisa", null, null, var4, 6, null);
      BlockType[] var5 = new BlockType[]{BlockType.IMAGE, BlockType.LOCALIMAGE};
      imageBlockTypes = SetsKt.setOf(var5);
      BlockType[] var6 = new BlockType[]{BlockType.ATTACHMENTLIST, BlockType.LOCAL_ATTACHMENT};
      attachmentBlockTypes = SetsKt.setOf(var6);
      BlockType[] var7 = new BlockType[]{BlockType.VIDEO, BlockType.VIDEOFILE};
      videoBlockTypes = SetsKt.setOf(var7);
   }
}
