package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import android.content.res.Configuration;
import androidx.activity.compose.ActivityResultRegistryKt;
import androidx.activity.compose.ManagedActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.DrawableRes;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.BorderKt;
import androidx.compose.foundation.BorderStrokeKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.foundation.shape.RoundedCornerShape;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.foundation.text.BasicTextFieldKt;
import androidx.compose.material3.IconButtonColors;
import androidx.compose.material3.IconButtonDefaults;
import androidx.compose.material3.IconButtonKt;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.InteractiveComponentSizeKt;
import androidx.compose.material3.ProgressIndicatorKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.runtime.saveable.RememberSaveableKt;
import androidx.compose.runtime.saveable.Saver;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.draw.ShadowKt;
import androidx.compose.ui.focus.FocusChangedModifierKt;
import androidx.compose.ui.focus.FocusManager;
import androidx.compose.ui.focus.FocusState;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.SolidColor;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.platform.TestTagKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.TextRangeKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.input.TextFieldValue;
import androidx.compose.ui.unit.Density;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.conversation.metrics.MetricData;
import io.intercom.android.sdk.m5.conversation.metrics.MetricData$ComposerInputClicked;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton$GifInsert;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton$VoiceNotesInsert;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Error;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Idle;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Processing;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Recording;
import io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposerInputType$Text;
import io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposerInputType$VoiceNotes;
import io.intercom.android.sdk.m5.conversation.utils.KeyboardState;
import io.intercom.android.sdk.m5.conversation.utils.KeyboardStateKt;
import io.intercom.android.sdk.m5.conversation.utils.audio.AudioPermissionHandlerKt;
import io.intercom.android.sdk.m5.preview.data.DeleteType;
import io.intercom.android.sdk.m5.preview.data.DownloadState;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewArgs;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile;
import io.intercom.android.sdk.m5.preview.data.DeleteType$Delete;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$LocalFile;
import io.intercom.android.sdk.m5.preview.ui.PreviewMediaContract;
import io.intercom.android.sdk.m5.utils.TextFieldSaver;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.common.StringProvider;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import kotlinx.coroutines.flow.SharedFlow;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.flow.StateFlowKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0084\u0001\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\b\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\u001aÃ\u0002\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u001e\u0010\u0004\u001a\u001a\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\u0004\u0012\u00020\u00010\u00052\u0006\u0010\t\u001a\u00020\n2\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\u0014\b\u0002\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00010\u000f2\u0014\b\u0002\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u00010\u000f2\u000e\b\u0002\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\u0010\b\u0002\u0010\u0014\u001a\n\u0012\u0004\u0012\u00020\u0016\u0018\u00010\u00152\b\b\u0002\u0010\u0017\u001a\u00020\u00182\u000e\b\u0002\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001b0\u001a2\u000e\b\u0002\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\u000e\b\u0002\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\u000e\b\u0002\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\u000e\b\u0002\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\u0014\b\u0002\u0010 \u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u000f2\u0014\b\u0002\u0010!\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u000f2\u000e\b\u0002\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00010\fH\u0001¢\u0006\u0002\u0010#\u001a&\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020\u00062\f\u0010'\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010(\u001a\u00020%H\u0002\u001a9\u00101\u001a\u00020\u00012\f\u00102\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u00103\u001a\u00020%2\b\b\u0003\u00104\u001a\u000205H\u0003¢\u0006\u0002\u00106\u001a?\u00107\u001a\u00020\u00012\u0006\u00108\u001a\u00020\u00182\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001b0\u001a2\f\u00109\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\f\u0010:\u001a\b\u0012\u0004\u0012\u00020\u00010\fH\u0003¢\u0006\u0002\u0010;\u001a\r\u0010<\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010=\u001a\r\u0010>\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010=\u001a\r\u0010?\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010=\u001a\r\u0010@\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010=\u001a\r\u0010A\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010=\u001a\r\u0010B\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010=\"\u0010\u0010)\u001a\u00020*X\u0082\u0004¢\u0006\u0004\n\u0002\u0010+\"\u0016\u0010,\u001a\u00020*X\u0080\u0004¢\u0006\n\n\u0002\u0010+\u001a\u0004\b-\u0010.\"\u0016\u0010/\u001a\u00020*X\u0080\u0004¢\u0006\n\n\u0002\u0010+\u001a\u0004\b0\u0010.¨\u0006C²\u0006\n\u0010D\u001a\u00020EX\u008a\u008e\u0002²\u0006\n\u0010F\u001a\u00020GX\u008a\u008e\u0002²\u0006\n\u0010H\u001a\u00020IX\u008a\u0084\u0002",
   d2 = {
         "MessageComposer",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "onSendMessage",
         "Lkotlin/Function2;",
         "",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/MediaUploadItem;",
         "textInput",
         "Lio/intercom/android/sdk/m5/conversation/states/ComposerState$TextInput;",
         "onGifInputSelected",
         "Lkotlin/Function0;",
         "onMediaInputSelected",
         "onInputChange",
         "Lkotlin/Function1;",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposerInputType;",
         "trackMetric",
         "Lio/intercom/android/sdk/m5/conversation/metrics/MetricData;",
         "onTyping",
         "composerUiEffect",
         "Lkotlinx/coroutines/flow/SharedFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ComposerUiEffect;",
         "voiceTranscriptionState",
         "Lio/intercom/android/sdk/m5/conversation/states/VoiceTranscriptionState;",
         "amplitudeLevel",
         "Lkotlinx/coroutines/flow/StateFlow;",
         "",
         "onStartVoiceRecording",
         "onStopVoiceRecording",
         "onCancelVoiceRecording",
         "onTranscribedTextConsumed",
         "onDeleteMediaUploadItem",
         "onFailedMediaUploadItem",
         "onClearMediaUploadItems",
         "(Landroidx/compose/ui/Modifier;Lkotlin/jvm/functions/Function2;Lio/intercom/android/sdk/m5/conversation/states/ComposerState$TextInput;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlinx/coroutines/flow/SharedFlow;Lio/intercom/android/sdk/m5/conversation/states/VoiceTranscriptionState;Lkotlinx/coroutines/flow/StateFlow;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;III)V",
         "canSendMessage",
         "",
         "text",
         "mediaItems",
         "isTextInputDisabled",
         "ComposerIconSize",
         "Landroidx/compose/ui/unit/Dp;",
         "F",
         "ComposerMinSize",
         "getComposerMinSize",
         "()F",
         "ComposerHalfSize",
         "getComposerHalfSize",
         "ComposerActionButton",
         "onClick",
         "enabled",
         "iconRes",
         "",
         "(Lkotlin/jvm/functions/Function0;Landroidx/compose/ui/Modifier;ZILandroidx/compose/runtime/Composer;II)V",
         "VoiceNotesComposer",
         "state",
         "onCloseClick",
         "onStopRecordingClick",
         "(Lio/intercom/android/sdk/m5/conversation/states/VoiceTranscriptionState;Lkotlinx/coroutines/flow/StateFlow;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)V",
         "TextComposerPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "TextComposerWithButtonsPreview",
         "TextComposerWithInitialTextPreview",
         "TextComposerWithMediaItemsPreview",
         "TextComposerWithFileItemsPreview",
         "VoiceNotesComposerPreview",
         "intercom-sdk-base_release",
         "textFieldValue",
         "Landroidx/compose/ui/text/input/TextFieldValue;",
         "borderColor",
         "Landroidx/compose/ui/graphics/Color;",
         "keyboardAsState",
         "Lio/intercom/android/sdk/m5/conversation/utils/KeyboardState;"
   }
)
@SourceDebugExtension(
   "SMAP\nMessageComposer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MessageComposer.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/MessageComposerKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 5 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 6 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 7 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 8 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 9 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 10 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 11 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 12 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n+ 13 Dp.kt\nandroidx/compose/ui/unit/Dp\n*L\n1#1,677:1\n1047#2,6:678\n1047#2,6:684\n1047#2,6:690\n1047#2,6:696\n1047#2,6:702\n1047#2,6:708\n1047#2,6:714\n1047#2,6:720\n1047#2,6:726\n1047#2,6:732\n1047#2,6:738\n1047#2,6:744\n1047#2,6:750\n1047#2,6:760\n1047#2,6:767\n1047#2,6:775\n1047#2,6:781\n1047#2,6:787\n1047#2,6:793\n1047#2,6:799\n1047#2,6:839\n1047#2,6:845\n1047#2,6:851\n1047#2,6:923\n1047#2,6:930\n1047#2,6:941\n1047#2,6:980\n1047#2,6:986\n1047#2,6:992\n1047#2,6:998\n827#3:756\n855#3,2:757\n1740#3,3:1012\n774#3:1170\n865#3,2:1171\n1869#3,2:1173\n1617#3,9:1175\n1869#3:1184\n1870#3:1186\n1626#3:1187\n118#4:759\n118#4:805\n123#4:806\n118#4:889\n118#4:890\n118#4:940\n118#4:979\n118#4:1015\n118#4:1016\n118#4:1045\n118#4:1077\n118#4:1111\n118#4:1112\n118#4:1117\n118#4:1188\n118#4:1189\n75#5:766\n75#5:773\n75#5:774\n87#6:807\n84#6,9:808\n94#6:1011\n81#7,6:817\n88#7,6:832\n81#7,6:867\n88#7,6:882\n81#7,6:901\n88#7,6:916\n96#7:938\n96#7:949\n81#7,6:957\n88#7,6:972\n96#7:1006\n96#7:1010\n81#7,6:1023\n88#7,6:1038\n81#7,6:1055\n88#7,6:1070\n81#7,6:1089\n88#7,6:1104\n96#7:1115\n81#7,6:1129\n88#7,6:1144\n96#7:1153\n96#7:1157\n96#7:1161\n402#8,9:823\n411#8:838\n402#8,9:873\n411#8:888\n402#8,9:907\n411#8:922\n412#8,2:936\n412#8,2:947\n402#8,9:963\n411#8:978\n412#8,2:1004\n412#8,2:1008\n402#8,9:1029\n411#8:1044\n402#8,9:1061\n411#8:1076\n402#8,9:1095\n411#8:1110\n412#8,2:1113\n402#8,9:1135\n411#8,3:1150\n412#8,2:1155\n412#8,2:1159\n99#9:857\n96#9,9:858\n106#9:950\n99#9,6:951\n106#9:1007\n99#9,6:1017\n99#9:1078\n95#9,10:1079\n106#9:1116\n99#9:1118\n95#9,10:1119\n106#9:1154\n106#9:1162\n70#10:891\n67#10,9:892\n77#10:939\n70#10:1046\n68#10,8:1047\n77#10:1158\n1#11:929\n1#11:1185\n85#12:1163\n117#12,2:1164\n85#12:1166\n117#12,2:1167\n85#12:1169\n60#13:1190\n*S KotlinDebug\n*F\n+ 1 MessageComposer.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/MessageComposerKt\n*L\n84#1:678,6\n85#1:684,6\n86#1:690,6\n87#1:696,6\n88#1:702,6\n92#1:708,6\n93#1:714,6\n94#1:720,6\n95#1:726,6\n96#1:732,6\n97#1:738,6\n98#1:744,6\n104#1:750,6\n143#1:760,6\n147#1:767,6\n160#1:775,6\n175#1:781,6\n187#1:787,6\n190#1:793,6\n197#1:799,6\n240#1:839,6\n245#1:845,6\n254#1:851,6\n290#1:923,6\n295#1:930,6\n318#1:941,6\n338#1:980,6\n342#1:986,6\n345#1:992,6\n356#1:998,6\n121#1:756\n121#1:757,2\n382#1:1012,3\n201#1:1170\n201#1:1171,2\n204#1:1173,2\n257#1:1175,9\n257#1:1184\n257#1:1186\n257#1:1187\n133#1:759\n215#1:805\n225#1:806\n273#1:889\n277#1:890\n310#1:940\n335#1:979\n405#1:1015\n433#1:1016\n438#1:1045\n468#1:1077\n471#1:1111\n473#1:1112\n486#1:1117\n385#1:1188\n386#1:1189\n146#1:766\n154#1:773\n155#1:774\n210#1:807\n210#1:808,9\n210#1:1011\n210#1:817,6\n210#1:832,6\n269#1:867,6\n269#1:882,6\n274#1:901,6\n274#1:916,6\n274#1:938\n269#1:949\n329#1:957,6\n329#1:972,6\n329#1:1006\n210#1:1010\n430#1:1023,6\n430#1:1038,6\n453#1:1055,6\n453#1:1070,6\n466#1:1089,6\n466#1:1104,6\n466#1:1115\n484#1:1129,6\n484#1:1144,6\n484#1:1153\n453#1:1157\n430#1:1161\n210#1:823,9\n210#1:838\n269#1:873,9\n269#1:888\n274#1:907,9\n274#1:922\n274#1:936,2\n269#1:947,2\n329#1:963,9\n329#1:978\n329#1:1004,2\n210#1:1008,2\n430#1:1029,9\n430#1:1044\n453#1:1061,9\n453#1:1076\n466#1:1095,9\n466#1:1110\n466#1:1113,2\n484#1:1135,9\n484#1:1150,3\n453#1:1155,2\n430#1:1159,2\n269#1:857\n269#1:858,9\n269#1:950\n329#1:951,6\n329#1:1007\n430#1:1017,6\n466#1:1078\n466#1:1079,10\n466#1:1116\n484#1:1118\n484#1:1119,10\n484#1:1154\n430#1:1162\n274#1:891\n274#1:892,9\n274#1:939\n453#1:1046\n453#1:1047,8\n453#1:1158\n257#1:1185\n104#1:1163\n104#1:1164,2\n143#1:1166\n143#1:1167,2\n145#1:1169\n387#1:1190\n*E\n"
)
public final class MessageComposerKt {
   private static final float ComposerIconSize;
   private static final float ComposerMinSize;
   private static final float ComposerHalfSize;

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void MessageComposer(
      @Nullable Modifier var0,
      @NotNull Function2 var1,
      @NotNull io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput var2,
      @Nullable Function0 var3,
      @Nullable Function0 var4,
      @Nullable Function1 var5,
      @Nullable Function1 var6,
      @Nullable Function0 var7,
      @Nullable SharedFlow var8,
      @Nullable VoiceTranscriptionState var9,
      @Nullable StateFlow var10,
      @Nullable Function0 var11,
      @Nullable Function0 var12,
      @Nullable Function0 var13,
      @Nullable Function0 var14,
      @Nullable Function1 var15,
      @Nullable Function1 var16,
      @Nullable Function0 var17,
      @Nullable Composer var18,
      int var19,
      int var20,
      int var21
   ) {
      Intrinsics.checkNotNullParameter(var1, "onSendMessage");
      Intrinsics.checkNotNullParameter(var2, "textInput");
      Composer var150 = var18.startRestartGroup(-1711474858);
      ComposerKt.sourceInformation(
         var150,
         "C(MessageComposer)P(2,10,15,7,9,8,16,14,1,17!1,11,12!1,13,5,6)83@4225L2,84@4268L2,85@4321L2,86@4365L2,87@4396L2,91@4646L2,92@4689L2,93@4734L2,94@4782L2,95@4843L2,96@4904L2,97@4950L2,103@5182L224,103@5116L290,135@6574L6,136@6643L6,142@6799L47,144@6875L17,145@6934L7,146@6990L98,146@6946L142,158@7308L58,159@7392L356,159@7371L377,174@7989L311,174@7949L351,186@8395L47,189@8473L63,185@8333L210,196@8668L497,194@8571L601,216@9431L6,217@9508L6,220@9620L6,209@9178L6755:MessageComposer.kt#yb6g9"
      );
      int var22 = var19;
      int var23 = var20;
      if ((var21 & 1) != 0) {
         var22 |= 6;
      } else if ((var19 & 6) == 0) {
         var22 |= var150.changed(var0) ? 4 : 2;
      }

      if ((var21 & 2) != 0) {
         var22 |= 48;
      } else if ((var19 & 48) == 0) {
         var22 |= var150.changedInstance(var1) ? 32 : 16;
      }

      if ((var21 & 4) != 0) {
         var22 |= 384;
      } else if ((var19 & 384) == 0) {
         var22 |= var150.changedInstance(var2) ? 256 : 128;
      }

      if ((var21 & 8) != 0) {
         var22 |= 3072;
      } else if ((var19 & 3072) == 0) {
         var22 |= var150.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var21 & 16) != 0) {
         var22 |= 24576;
      } else if ((var19 & 24576) == 0) {
         var22 |= var150.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var21 & 32) != 0) {
         var22 |= 196608;
      } else if ((var19 & 196608) == 0) {
         var22 |= var150.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var21 & 64) != 0) {
         var22 |= 1572864;
      } else if ((var19 & 1572864) == 0) {
         var22 |= var150.changedInstance(var6) ? 1048576 : 524288;
      }

      if ((var21 & 128) != 0) {
         var22 |= 12582912;
      } else if ((var19 & 12582912) == 0) {
         var22 |= var150.changedInstance(var7) ? 8388608 : 4194304;
      }

      if ((var21 & 256) != 0) {
         var22 |= 100663296;
      } else if ((var19 & 100663296) == 0) {
         var22 |= var150.changedInstance(var8) ? 67108864 : 33554432;
      }

      if ((var21 & 512) != 0) {
         var22 |= 805306368;
      } else if ((var19 & 805306368) == 0) {
         var22 |= ((var19 & 1073741824) == 0 ? var150.changed(var9) : var150.changedInstance(var9)) ? 536870912 : 268435456;
      }

      if ((var20 & 6) == 0) {
         var23 |= (var21 & 1024) == 0 && var150.changedInstance(var10) ? 4 : 2;
      }

      if ((var21 & 2048) != 0) {
         var23 |= 48;
      } else if ((var20 & 48) == 0) {
         var23 |= var150.changedInstance(var11) ? 32 : 16;
      }

      if ((var21 & 4096) != 0) {
         var23 |= 384;
      } else if ((var20 & 384) == 0) {
         var23 |= var150.changedInstance(var12) ? 256 : 128;
      }

      if ((var21 & 8192) != 0) {
         var23 |= 3072;
      } else if ((var20 & 3072) == 0) {
         var23 |= var150.changedInstance(var13) ? 2048 : 1024;
      }

      if ((var21 & 16384) != 0) {
         var23 |= 24576;
      } else if ((var20 & 24576) == 0) {
         var23 |= var150.changedInstance(var14) ? 16384 : 8192;
      }

      if ((var21 & 32768) != 0) {
         var23 |= 196608;
      } else if ((var20 & 196608) == 0) {
         var23 |= var150.changedInstance(var15) ? 131072 : 65536;
      }

      if ((var21 & 65536) != 0) {
         var23 |= 1572864;
      } else if ((var20 & 1572864) == 0) {
         var23 |= var150.changedInstance(var16) ? 1048576 : 524288;
      }

      if ((var21 & 131072) != 0) {
         var23 |= 12582912;
      } else if ((var20 & 12582912) == 0) {
         var23 |= var150.changedInstance(var17) ? 8388608 : 4194304;
      }

      if ((var22 & 306783379) == 306783378 && (var23 & 4793491) == 4793490 && var150.getSkipping()) {
         var150.skipToGroupEnd();
      } else {
         var150.startDefaults();
         if ((var19 & 1) != 0 && !var150.getDefaultsInvalid()) {
            var150.skipToGroupEnd();
            if ((var21 & 1024) != 0) {
               var23 &= -15;
            }
         } else {
            if ((var21 & 1) != 0) {
               var0 = Modifier.Companion;
            }

            if ((var21 & 8) != 0) {
               var150.startReplaceGroup(319552242);
               ComposerKt.sourceInformation(var150, "CC(remember):MessageComposer.kt#9igjgp");
               Composer var25 = var150;
               boolean var26 = false;
               int var27 = 0;
               Object var28 = var25.rememberedValue();
               int var29 = 0;
               Object var10000;
               if (var28 == Composer.Companion.getEmpty()) {
                  int var30 = 0;
                  Object var225 = ((kotlin.jvm.functions.Function0) () -> MessageComposerKt.MessageComposer$lambda$1$lambda$0());
                  var25.updateRememberedValue(var225);
                  var10000 = var225;
               } else {
                  var10000 = var28;
               }

               Function0 var24 = (Function0)var10000;
               var150.endReplaceGroup();
               var3 = var24;
            }

            if ((var21 & 16) != 0) {
               var150.startReplaceGroup(319553618);
               ComposerKt.sourceInformation(var150, "CC(remember):MessageComposer.kt#9igjgp");
               Composer var164 = var150;
               boolean var176 = false;
               int var188 = 0;
               Object var200 = var164.rememberedValue();
               int var212 = 0;
               Object var487;
               if (var200 == Composer.Companion.getEmpty()) {
                  int var226 = 0;
                  Object var227 = ((kotlin.jvm.functions.Function0) () -> MessageComposerKt.MessageComposer$lambda$3$lambda$2());
                  var164.updateRememberedValue(var227);
                  var487 = var227;
               } else {
                  var487 = var200;
               }

               Function0 var151 = (Function0)var487;
               var150.endReplaceGroup();
               var4 = var151;
            }

            if ((var21 & 32) != 0) {
               var150.startReplaceGroup(319555314);
               ComposerKt.sourceInformation(var150, "CC(remember):MessageComposer.kt#9igjgp");
               Composer var165 = var150;
               boolean var177 = false;
               int var189 = 0;
               Object var201 = var165.rememberedValue();
               int var213 = 0;
               Object var488;
               if (var201 == Composer.Companion.getEmpty()) {
                  int var228 = 0;
                  Object var229 = ((kotlin.jvm.functions.Function1) (recoveredArgument_625_0) -> MessageComposerKt.MessageComposer$lambda$5$lambda$4((io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposerInputType)recoveredArgument_625_0));
                  var165.updateRememberedValue(var229);
                  var488 = var229;
               } else {
                  var488 = var201;
               }

               Function1 var152 = (Function1)var488;
               var150.endReplaceGroup();
               var5 = var152;
            }

            if ((var21 & 64) != 0) {
               var150.startReplaceGroup(319556722);
               ComposerKt.sourceInformation(var150, "CC(remember):MessageComposer.kt#9igjgp");
               Composer var166 = var150;
               boolean var178 = false;
               int var190 = 0;
               Object var202 = var166.rememberedValue();
               int var214 = 0;
               Object var489;
               if (var202 == Composer.Companion.getEmpty()) {
                  int var230 = 0;
                  Object var231 = ((kotlin.jvm.functions.Function1) (recoveredArgument_626_0) -> MessageComposerKt.MessageComposer$lambda$7$lambda$6((io.intercom.android.sdk.m5.conversation.metrics.MetricData)recoveredArgument_626_0));
                  var166.updateRememberedValue(var231);
                  var489 = var231;
               } else {
                  var489 = var202;
               }

               Function1 var153 = (Function1)var489;
               var150.endReplaceGroup();
               var6 = var153;
            }

            if ((var21 & 128) != 0) {
               var150.startReplaceGroup(319557714);
               ComposerKt.sourceInformation(var150, "CC(remember):MessageComposer.kt#9igjgp");
               Composer var167 = var150;
               boolean var179 = false;
               int var191 = 0;
               Object var203 = var167.rememberedValue();
               int var215 = 0;
               Object var490;
               if (var203 == Composer.Companion.getEmpty()) {
                  int var232 = 0;
                  Object var233 = ((kotlin.jvm.functions.Function0) () -> MessageComposerKt.MessageComposer$lambda$9$lambda$8());
                  var167.updateRememberedValue(var233);
                  var490 = var233;
               } else {
                  var490 = var203;
               }

               Function0 var154 = (Function0)var490;
               var150.endReplaceGroup();
               var7 = var154;
            }

            if ((var21 & 256) != 0) {
               var8 = null;
            }

            if ((var21 & 512) != 0) {
               var9 = io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Idle.INSTANCE;
            }

            if ((var21 & 1024) != 0) {
               var10 = StateFlowKt.MutableStateFlow(0.0F);
               var23 &= -15;
            }

            if ((var21 & 2048) != 0) {
               var150.startReplaceGroup(319565714);
               ComposerKt.sourceInformation(var150, "CC(remember):MessageComposer.kt#9igjgp");
               Composer var168 = var150;
               boolean var180 = false;
               int var192 = 0;
               Object var204 = var168.rememberedValue();
               int var216 = 0;
               Object var491;
               if (var204 == Composer.Companion.getEmpty()) {
                  int var234 = 0;
                  Object var235 = ((kotlin.jvm.functions.Function0) () -> MessageComposerKt.MessageComposer$lambda$11$lambda$10());
                  var168.updateRememberedValue(var235);
                  var491 = var235;
               } else {
                  var491 = var204;
               }

               Function0 var155 = (Function0)var491;
               var150.endReplaceGroup();
               var11 = var155;
            }

            if ((var21 & 4096) != 0) {
               var150.startReplaceGroup(319567090);
               ComposerKt.sourceInformation(var150, "CC(remember):MessageComposer.kt#9igjgp");
               Composer var169 = var150;
               boolean var181 = false;
               int var193 = 0;
               Object var205 = var169.rememberedValue();
               int var217 = 0;
               Object var492;
               if (var205 == Composer.Companion.getEmpty()) {
                  int var236 = 0;
                  Object var237 = ((kotlin.jvm.functions.Function0) () -> MessageComposerKt.MessageComposer$lambda$13$lambda$12());
                  var169.updateRememberedValue(var237);
                  var492 = var237;
               } else {
                  var492 = var205;
               }

               Function0 var156 = (Function0)var492;
               var150.endReplaceGroup();
               var12 = var156;
            }

            if ((var21 & 8192) != 0) {
               var150.startReplaceGroup(319568530);
               ComposerKt.sourceInformation(var150, "CC(remember):MessageComposer.kt#9igjgp");
               Composer var170 = var150;
               boolean var182 = false;
               int var194 = 0;
               Object var206 = var170.rememberedValue();
               int var218 = 0;
               Object var493;
               if (var206 == Composer.Companion.getEmpty()) {
                  int var238 = 0;
                  Object var239 = ((kotlin.jvm.functions.Function0) () -> MessageComposerKt.MessageComposer$lambda$15$lambda$14());
                  var170.updateRememberedValue(var239);
                  var493 = var239;
               } else {
                  var493 = var206;
               }

               Function0 var157 = (Function0)var493;
               var150.endReplaceGroup();
               var13 = var157;
            }

            if ((var21 & 16384) != 0) {
               var150.startReplaceGroup(319570066);
               ComposerKt.sourceInformation(var150, "CC(remember):MessageComposer.kt#9igjgp");
               Composer var171 = var150;
               boolean var183 = false;
               int var195 = 0;
               Object var207 = var171.rememberedValue();
               int var219 = 0;
               Object var494;
               if (var207 == Composer.Companion.getEmpty()) {
                  int var240 = 0;
                  Object var241 = ((kotlin.jvm.functions.Function0) () -> MessageComposerKt.MessageComposer$lambda$17$lambda$16());
                  var171.updateRememberedValue(var241);
                  var494 = var241;
               } else {
                  var494 = var207;
               }

               Function0 var158 = (Function0)var494;
               var150.endReplaceGroup();
               var14 = var158;
            }

            if ((var21 & 32768) != 0) {
               var150.startReplaceGroup(319572018);
               ComposerKt.sourceInformation(var150, "CC(remember):MessageComposer.kt#9igjgp");
               Composer var172 = var150;
               boolean var184 = false;
               int var196 = 0;
               Object var208 = var172.rememberedValue();
               int var220 = 0;
               Object var495;
               if (var208 == Composer.Companion.getEmpty()) {
                  int var242 = 0;
                  Object var243 = ((kotlin.jvm.functions.Function1) (recoveredArgument_605_0) -> MessageComposerKt.MessageComposer$lambda$19$lambda$18((io.intercom.android.sdk.m5.conversation.states.MediaUploadItem)recoveredArgument_605_0));
                  var172.updateRememberedValue(var243);
                  var495 = var243;
               } else {
                  var495 = var208;
               }

               Function1 var159 = (Function1)var495;
               var150.endReplaceGroup();
               var15 = var159;
            }

            if ((var21 & 65536) != 0) {
               var150.startReplaceGroup(319573970);
               ComposerKt.sourceInformation(var150, "CC(remember):MessageComposer.kt#9igjgp");
               Composer var173 = var150;
               boolean var185 = false;
               int var197 = 0;
               Object var209 = var173.rememberedValue();
               int var221 = 0;
               Object var496;
               if (var209 == Composer.Companion.getEmpty()) {
                  int var244 = 0;
                  Object var245 = ((kotlin.jvm.functions.Function1) (recoveredArgument_606_0) -> MessageComposerKt.MessageComposer$lambda$21$lambda$20((io.intercom.android.sdk.m5.conversation.states.MediaUploadItem)recoveredArgument_606_0));
                  var173.updateRememberedValue(var245);
                  var496 = var245;
               } else {
                  var496 = var209;
               }

               Function1 var160 = (Function1)var496;
               var150.endReplaceGroup();
               var16 = var160;
            }

            if ((var21 & 131072) != 0) {
               var150.startReplaceGroup(319575442);
               ComposerKt.sourceInformation(var150, "CC(remember):MessageComposer.kt#9igjgp");
               Composer var174 = var150;
               boolean var186 = false;
               int var198 = 0;
               Object var210 = var174.rememberedValue();
               int var222 = 0;
               Object var497;
               if (var210 == Composer.Companion.getEmpty()) {
                  int var246 = 0;
                  Object var247 = ((kotlin.jvm.functions.Function0) () -> MessageComposerKt.MessageComposer$lambda$23$lambda$22());
                  var174.updateRememberedValue(var247);
                  var497 = var247;
               } else {
                  var497 = var210;
               }

               Function0 var161 = (Function0)var497;
               var150.endReplaceGroup();
               var17 = var161;
            }
         }

         var150.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1711474858, var22, var23, "io.intercom.android.sdk.m5.conversation.ui.components.composer.MessageComposer (MessageComposer.kt:98)"
            );
         }

         Pair var162 = new Pair(var2.getInitialMessage(), var2.getHintText());
         String var175 = (String)var162.component1();
         StringProvider var187 = (StringProvider)var162.component2();
         boolean var199 = var2.isDisabled();
         Object[] var498 = new Object[0];
         Saver var10001 = TextFieldSaver.INSTANCE.getTextFieldValueSaver();
         var150.startReplaceGroup(319583088);
         ComposerKt.sourceInformation(var150, "CC(remember):MessageComposer.kt#9igjgp");
         Composer var248 = var150;
         boolean var31 = var150.changed(var175);
         int var32 = 0;
         Object var33 = var248.rememberedValue();
         int var34 = 0;
         Object var10002;
         if (!var31 && var33 != Composer.Companion.getEmpty()) {
            var10002 = var33;
         } else {
            Saver var148 = var10001;
            Object[] var147 = var498;
            int var35 = 0;
            final java.lang.String recoveredCapture_608_0 = var175;
            Function0 var149 = ((kotlin.jvm.functions.Function0) () -> MessageComposerKt.MessageComposer$lambda$25$lambda$24(recoveredCapture_608_0));
            var498 = var147;
            var10001 = var148;
            Object var36 = var149;
            var248.updateRememberedValue(var36);
            var10002 = var36;
         }

         Function0 var223 = (Function0)var10002;
         var150.endReplaceGroup();
         MutableState var211 = RememberSaveableKt.sourceRememberSaveableMutableState(var498, var10001, var223, var150, 0);
         boolean var224 = var2.getInputType() instanceof io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposerInputType$VoiceNotes || !(var9 instanceof io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Idle);
         List var499;
         if (StringsKt.isBlank(MessageComposer$lambda$26(var211).getText()) && var2.getMediaUploadItems().isEmpty()) {
            var499 = var2.getButtons();
         } else {
            Iterable var250 = var2.getButtons();
            int var252 = 0;
            Iterable var254 = var250;
            ArrayList var256 = new ArrayList();
            int var260 = 0;

            for (Object var37 : var254) {
               io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton var38 = (io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton)var37;
               int var39 = 0;
               if (!Intrinsics.areEqual(var38, io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton$VoiceNotesInsert.INSTANCE) && !Intrinsics.areEqual(var38, io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton$GifInsert.INSTANCE)) {
                  var256.add(var37);
               }
            }

            var499 = (List)var256;
         }

         List var249 = var499;
         boolean var251 = !var249.isEmpty();
         boolean var253 = var224 || var2.getMediaUploadItems().isEmpty() && !var251;
         float var500;
         if (var253) {
            int var257 = 24;
            int var261 = 0;
            var500 = Dp.recovered$95b3d103663bf060(var257);
         } else {
            int var258 = 20;
            int var262 = 0;
            var500 = Dp.recovered$95b3d103663bf060(var258);
         }

         float var255 = var500;
         RoundedCornerShape var259 = RoundedCornerShapeKt.recovered$8dfaa2739ee8e479(var255);
         long var263 = IntercomTheme.INSTANCE.getColors(var150, IntercomTheme.$stable).getBorder().recovered$e2b74d614aa09788();
         long var501;
         if (ColorExtensionsKt.recovered$4886b56a29c1c487(IntercomTheme.INSTANCE.getColors(var150, IntercomTheme.$stable).recovered$202acdf3dbb5c020())) {
            var150.startReplaceGroup(1318614878);
            ComposerKt.sourceInformation(var150, "137@6697L6");
            long var266 = ColorExtensionsKt.recovered$da172df9fccf1b21(
               IntercomTheme.INSTANCE.getColors(var150, IntercomTheme.$stable).recovered$202acdf3dbb5c020()
            );
            var150.endReplaceGroup();
            var501 = var266;
         } else {
            var150.startReplaceGroup(1318672135);
            ComposerKt.sourceInformation(var150, "139@6755L6");
            long var267 = IntercomTheme.INSTANCE.getColors(var150, IntercomTheme.$stable).recovered$202acdf3dbb5c020();
            var150.endReplaceGroup();
            var501 = var267;
         }

         long var265 = var501;
         var150.startReplaceGroup(319634655);
         ComposerKt.sourceInformation(var150, "CC(remember):MessageComposer.kt#9igjgp");
         Composer var41 = var150;
         boolean var42 = false;
         int $i$f$cachex = 0;
         Object it$ivx = var41.rememberedValue();
         int var45/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var502;
         if (it$ivx == Composer.Companion.getEmpty()) {
            int var46 = 0;
            Object var47 = SnapshotStateKt.mutableStateOf$default(Color.recovered$f46ef195ad4a2363(var263), null, 2, null);
            var41.updateRememberedValue(var47);
            var502 = var47;
         } else {
            var502 = it$ivx;
         }

         MutableState var40 = (MutableState)var502;
         var150.endReplaceGroup();
         MutableState var268 = var40;
         State var269 = KeyboardStateKt.keyboardAsState(var150, 0);
         CompositionLocal var271 = CompositionLocalsKt.getLocalFocusManager();
         int var274 = 0;
         int var278 = 0;
         ComposerKt.sourceInformationMarkerStart(var150, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var285 = var150.consume(var271);
         ComposerKt.sourceInformationMarkerEnd(var150);
         FocusManager var270 = (FocusManager)var285;
         Boolean var503 = MessageComposer$lambda$32(var269).isDismissed();
         var150.startReplaceGroup(319640818);
         ComposerKt.sourceInformation(var150, "CC(remember):MessageComposer.kt#9igjgp");
         Composer var275 = var150;
         boolean invalid$ivx = var150.changed(var269) | var150.changedInstance(var270);
         int $i$f$cachexx = 0;
         Object it$ivxx = var275.rememberedValue();
         int var305/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var523;
         if (!invalid$ivx && it$ivxx != Composer.Companion.getEmpty()) {
            var523 = it$ivxx;
         } else {
            Boolean var477 = var503;
            int var48 = 0;
            Function2 var482 = new MessageComposerKt$MessageComposer$13$1(var270, var269, null);
            var503 = var477;
            Object var49 = var482;
            var275.updateRememberedValue(var49);
            var523 = var49;
         }

         Function2 var272 = (Function2)var523;
         var150.endReplaceGroup();
         EffectsKt.LaunchedEffect(var503, var272, var150, 0);
         var150.startReplaceGroup(319644699);
         ComposerKt.sourceInformation(var150, "");
         var150.startReplaceGroup(319645085);
         ComposerKt.sourceInformation(var150, "153@7143L7");
         CompositionLocal var287 = AndroidCompositionLocals_androidKt.getLocalConfiguration();
         int $changed$ivx = 0;
         int $i$f$getCurrentx = 0;
         ComposerKt.sourceInformationMarkerStart(var150, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var315 = var150.consume(var287);
         ComposerKt.sourceInformationMarkerEnd(var150);
         boolean var280 = ((Configuration)var315).orientation == 2;
         var150.endReplaceGroup();
         int var504;
         if (var280) {
            var504 = 2;
         } else {
            var150.startReplaceGroup(319647956);
            ComposerKt.sourceInformation(var150, "154@7228L7");
            CompositionLocal var288 = CompositionLocalsKt.getLocalDensity();
            int $changed$ivxx = 0;
            int $i$f$getCurrentxx = 0;
            ComposerKt.sourceInformationMarkerStart(var150, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var316 = var150.consume(var288);
            ComposerKt.sourceInformationMarkerEnd(var150);
            boolean var281 = ((Density)var316).getFontScale() > 1.5;
            var150.endReplaceGroup();
            var504 = var281 ? 4 : 5;
         }

         int var276 = var504;
         var150.endReplaceGroup();
         int var273 = var276;
         String var277 = StringResources_androidKt.stringResource(string.intercom_report_ai_answer_message, var150, 0);
         Object var505 = null;
         var150.startReplaceGroup(319653940);
         ComposerKt.sourceInformation(var150, "CC(remember):MessageComposer.kt#9igjgp");
         Composer var289 = var150;
         boolean invalid$ivxx = var150.changedInstance(var8) | var150.changed(var211) | var150.changed(var277);
         int $i$f$cachexxx = 0;
         Object it$ivxxx = var289.rememberedValue();
         int var324/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
         Object var524;
         if (!invalid$ivxx && it$ivxxx != Composer.Companion.getEmpty()) {
            var524 = it$ivxxx;
         } else {
            Object var478 = null;
            int var50 = 0;
            Function2 var483 = new MessageComposerKt$MessageComposer$14$1(var8, var277, var211, null);
            var505 = var478;
            Object var51 = var483;
            var289.updateRememberedValue(var51);
            var524 = var51;
         }

         Function2 var282 = (Function2)var524;
         var150.endReplaceGroup();
         EffectsKt.LaunchedEffect(var505, var282, var150, 6);
         VoiceTranscriptionState var506 = var9;
         var150.startReplaceGroup(319672999);
         ComposerKt.sourceInformation(var150, "CC(remember):MessageComposer.kt#9igjgp");
         Composer var290 = var150;
         boolean invalid$ivxxx = ((var22 & 1879048192) == 536870912 || (var22 & 1073741824) != 0 && var150.changedInstance(var9))
            | var150.changed(var211)
            | (var23 & 57344) == 16384;
         int $i$f$cachexxxx = 0;
         Object it$ivxxxx = var290.rememberedValue();
         int var325/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
         Object var525;
         if (!invalid$ivxxx && it$ivxxxx != Composer.Companion.getEmpty()) {
            var525 = it$ivxxxx;
         } else {
            VoiceTranscriptionState var479 = var9;
            int var330 = 0;
            Function2 var484 = new MessageComposerKt$MessageComposer$15$1(var9, var14, var211, null);
            var506 = var479;
            Object var335 = var484;
            var290.updateRememberedValue(var335);
            var525 = var335;
         }

         Function2 var283 = (Function2)var525;
         var150.endReplaceGroup();
         EffectsKt.LaunchedEffect(var506, var283, var150, 14 & var22 >> 27);
         var150.startReplaceGroup(319685727);
         ComposerKt.sourceInformation(var150, "CC(remember):MessageComposer.kt#9igjgp");
         Composer var299 = var150;
         boolean var310 = (var23 & 112) == 32;
         int $i$f$cachexxxxx = 0;
         Object it$ivxxxxx = var299.rememberedValue();
         int var331/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxx */ = 0;
         Object var507;
         if (!var310 && it$ivxxxxx != Composer.Companion.getEmpty()) {
            var507 = it$ivxxxxx;
         } else {
            int var336 = 0;
            final kotlin.jvm.functions.Function0 recoveredCapture_609_0 = var11;
            Object var52 = ((kotlin.jvm.functions.Function0) () -> MessageComposerKt.MessageComposer$lambda$37$lambda$36(recoveredCapture_609_0));
            var299.updateRememberedValue(var52);
            var507 = var52;
         }

         Function0 var291 = (Function0)var507;
         var150.endReplaceGroup();
         Function0 var508 = var291;
         var150.startReplaceGroup(319688239);
         ComposerKt.sourceInformation(var150, "CC(remember):MessageComposer.kt#9igjgp");
         Composer var300 = var150;
         boolean var311 = false;
         int $i$f$cachexxxxxx = 0;
         Object it$ivxxxxxx = var300.rememberedValue();
         int var332/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxx */ = 0;
         Object var526;
         if (it$ivxxxxxx == Composer.Companion.getEmpty()) {
            Function0 var480 = var291;
            int var337 = 0;
            Function0 var485 = ((kotlin.jvm.functions.Function0) () -> MessageComposerKt.MessageComposer$lambda$39$lambda$38());
            var508 = var480;
            Object var338 = var485;
            var300.updateRememberedValue(var338);
            var526 = var338;
         } else {
            var526 = it$ivxxxxxx;
         }

         Function0 var292 = (Function0)var526;
         var150.endReplaceGroup();
         Function0 var284 = AudioPermissionHandlerKt.rememberAudioPermissionHandler(var508, var292, var150, 48);
         ActivityResultContract var509 = new PreviewMediaContract();
         var150.startReplaceGroup(319694913);
         ComposerKt.sourceInformation(var150, "CC(remember):MessageComposer.kt#9igjgp");
         Composer var312 = var150;
         boolean var321 = var150.changedInstance(var2) | (var23 & 458752) == 131072;
         int $i$f$cachexxxxxxx = 0;
         Object it$ivxxxxxxx = var312.rememberedValue();
         int var339/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxx */ = 0;
         Object var527;
         if (!var321 && it$ivxxxxxxx != Composer.Companion.getEmpty()) {
            var527 = it$ivxxxxxxx;
         } else {
            ActivityResultContract var481 = var509;
            int var341 = 0;
            final io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput recoveredCapture_611_0 = var2;
            final kotlin.jvm.functions.Function1 recoveredCapture_611_1 = var15;
            Function1 var486 = ((kotlin.jvm.functions.Function1) (recoveredArgument_611_0) -> MessageComposerKt.MessageComposer$lambda$43$lambda$42(recoveredCapture_611_0, recoveredCapture_611_1, (java.util.List)recoveredArgument_611_0));
            var509 = var481;
            Object var53 = var486;
            var312.updateRememberedValue(var53);
            var527 = var53;
         }

         Function1 var301 = (Function1)var527;
         var150.endReplaceGroup();
         ManagedActivityResultLauncher var293 = ActivityResultRegistryKt.rememberLauncherForActivityResult(var509, var301, var150, 0);
         Modifier var510 = SizeKt.recovered$da9aba2b92020277(SizeKt.fillMaxWidth$default(var0, 0.0F, 1, null), 0.0F, ComposerMinSize, 1, null);
         int var302 = 28;
         int var313 = 0;
         Modifier var511 = BackgroundKt.recovered$60bc47fb440df483(
            ShadowKt.recovered$04904c26f7ded95f(
               var510,
               Dp.recovered$95b3d103663bf060(var302),
               var259,
               false,
               Color.recovered$41aa0ee03c2282fb(
                  IntercomTheme.INSTANCE.getColors(var150, IntercomTheme.$stable).recovered$f6e9b4ba3d2c0c59(), 0.54F, 0.0F, 0.0F, 0.0F, 14, null
               ),
               Color.recovered$41aa0ee03c2282fb(
                  IntercomTheme.INSTANCE.getColors(var150, IntercomTheme.$stable).recovered$f6e9b4ba3d2c0c59(), 0.54F, 0.0F, 0.0F, 0.0F, 14, null
               ),
               4,
               null
            ),
            IntercomTheme.INSTANCE.getColors(var150, IntercomTheme.$stable).getBase().recovered$ad18c207ea721de5(),
            var259
         );
         double $this$dp$ivx = 1.5;
         int $i$f$getDpx = 0;
         Modifier var304 = BorderKt.border(
            var511, BorderStrokeKt.recovered$91879a1bef2fb536(Dp.recovered$95b3d103663bf060((float)$this$dp$ivx), MessageComposer$lambda$30(var268)), var259
         );
         byte var329 = 0;
         int var334 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var150, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var314 = Arrangement.INSTANCE.getTop();
         Horizontal var323 = Alignment.Companion.getStart();
         MeasurePolicy var340 = ColumnKt.columnMeasurePolicy(var314, var323, var150, 14 & var329 >> 3 | 112 & var329 >> 3);
         Modifier var342 = var304;
         MeasurePolicy var343 = var340;
         Composer var54 = var150;
         int var55 = 112 & var329 << 3;
         int var56 = 0;
         ComposerKt.sourceInformationMarkerStart(var54, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var57 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var54, 0));
         CompositionLocalMap var58 = var54.getCurrentCompositionLocalMap();
         Modifier var59 = ComposedModifierKt.materializeModifier(var54, var342);
         Function0 var60 = ComposeUiNode.Companion.getConstructor();
         Composer var61 = var54;
         int var62 = 6 | 896 & var55 << 6;
         int var63 = 0;
         ComposerKt.sourceInformationMarkerStart(var61, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var61.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var61.startReusableNode();
         if (var61.getInserting()) {
            var61.createNode(var60);
         } else {
            var61.useNode();
         }

         Composer var64 = Updater.recovered$676aa17b44fd6280(var61);
         int var65 = 0;
         Updater.recovered$89682b199a142880(var64, var343, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var64, var58, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var64, var57, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var64, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var64, var59, ComposeUiNode.Companion.getSetModifier());
         int var66 = 14 & var62 >> 6;
         Composer var67 = var61;
         int var68 = 0;
         ComposerKt.sourceInformationMarkerStart(var67, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var69 = 6 | 112 & var329 >> 6;
         Composer var70 = var67;
         ColumnScope var71 = ColumnScopeInstance.INSTANCE;
         int var72 = 0;
         ComposerKt.sourceInformationMarkerStart(var70, 2045570282, "C:MessageComposer.kt#yb6g9");
         if (var224) {
            var70.startReplaceGroup(2045440143);
            ComposerKt.sourceInformation(var70, "239@10362L185,244@10588L62,236@10224L441");
            VoiceTranscriptionState var346 = !(var9 instanceof io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Idle) ? var9 : io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Recording.INSTANCE;
            VoiceTranscriptionState var522 = var346;
            StateFlow var535 = var10;
            var70.startReplaceGroup(-1042381624);
            ComposerKt.sourceInformation(var70, "CC(remember):MessageComposer.kt#9igjgp");
            Composer var349 = var70;
            boolean var353 = (var23 & 7168) == 2048 | var150.changed(var211) | (var22 & 458752) == 131072;
            int $i$f$cachexxxxxxxx = 0;
            Object it$ivxxxxxxxx = var349.rememberedValue();
            int var365/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxx */ = 0;
            Object var538;
            if (!var353 && it$ivxxxxxxxx != Composer.Companion.getEmpty()) {
               var538 = it$ivxxxxxxxx;
            } else {
               StateFlow var79 = var10;
               VoiceTranscriptionState var368 = var346;
               int var372 = 0;
               final kotlin.jvm.functions.Function0 recoveredCapture_612_0 = var13;
               final kotlin.jvm.functions.Function1 recoveredCapture_612_1 = var5;
               final androidx.compose.runtime.MutableState recoveredCapture_612_2 = var211;
               Function0 var82 = ((kotlin.jvm.functions.Function0) () -> MessageComposerKt.MessageComposer$lambda$69$lambda$45$lambda$44(recoveredCapture_612_0, recoveredCapture_612_1, recoveredCapture_612_2));
               var522 = var368;
               var535 = var79;
               Object var376 = var82;
               var349.updateRememberedValue(var376);
               var538 = var376;
            }

            Function0 var380 = (Function0)var538;
            var70.endReplaceGroup();
            Function0 var539 = var380;
            var70.startReplaceGroup(-1042374515);
            ComposerKt.sourceInformation(var70, "CC(remember):MessageComposer.kt#9igjgp");
            Composer var350 = var70;
            boolean var354 = (var23 & 896) == 256;
            int $i$f$cachexxxxxxxxx = 0;
            Object it$ivxxxxxxxxx = var350.rememberedValue();
            int var366/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxx */ = 0;
            Object var541;
            if (!var354 && it$ivxxxxxxxxx != Composer.Companion.getEmpty()) {
               var541 = it$ivxxxxxxxxx;
            } else {
               Function0 var374 = var380;
               StateFlow var367 = var535;
               VoiceTranscriptionState var369 = var522;
               int var373 = 0;
               final kotlin.jvm.functions.Function0 recoveredCapture_613_0 = var12;
               Function0 var85 = ((kotlin.jvm.functions.Function0) () -> MessageComposerKt.MessageComposer$lambda$69$lambda$47$lambda$46(recoveredCapture_613_0));
               var522 = var369;
               var535 = var367;
               var539 = var374;
               Object var377 = var85;
               var350.updateRememberedValue(var377);
               var541 = var377;
            }

            Function0 var381 = (Function0)var541;
            var70.endReplaceGroup();
            VoiceNotesComposer(var522, var535, var539, var381, var70, 112 & var23 << 3);
            var70.endReplaceGroup();
         } else {
            var70.startReplaceGroup(2046381365);
            ComposerKt.sourceInformation(var70, "268@11558L2613,328@14272L1631");
            var70.startReplaceGroup(-1042369104);
            ComposerKt.sourceInformation(var70, "253@10911L430,251@10798L692");
            if (!var2.getMediaUploadItems().isEmpty()) {
               List var512 = var2.getMediaUploadItems();
               var70.startReplaceGroup(-1042363811);
               ComposerKt.sourceInformation(var70, "CC(remember):MessageComposer.kt#9igjgp");
               Composer var84 = var70;
               boolean var74 = var150.changedInstance(var293) | var150.changedInstance(var2);
               int $i$f$cachexxxxxxxx = 0;
               Object it$ivxxxxxxxx = var84.rememberedValue();
               int var77/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxx */ = 0;
               Object var529;
               if (!var74 && it$ivxxxxxxxx != Composer.Companion.getEmpty()) {
                  var529 = it$ivxxxxxxxx;
               } else {
                  List var80 = var512;
                  int var78 = 0;
                  final androidx.activity.compose.ManagedActivityResultLauncher recoveredCapture_614_0 = var293;
                  final io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput recoveredCapture_614_1 = var2;
                  Function1 var528 = ((kotlin.jvm.functions.Function1) (recoveredArgument_614_0) -> MessageComposerKt.MessageComposer$lambda$69$lambda$50$lambda$49(recoveredCapture_614_0, recoveredCapture_614_1, ((java.lang.Integer)recoveredArgument_614_0).intValue()));
                  var512 = var80;
                  Object var81 = var528;
                  var84.updateRememberedValue(var81);
                  var529 = var81;
               }

               Function1 var73 = (Function1)var529;
               var70.endReplaceGroup();
               MediaItemsKt.MediaItems(var512, var73, var15, var16, null, var70, 896 & var23 >> 9 | 7168 & var23 >> 9, 16);
            }

            var70.endReplaceGroup();
            Modifier var344 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
            androidx.compose.ui.Alignment.Vertical var347 = Alignment.Companion.getCenterVertically();
            short var351 = 390;
            int var355 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var70, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
            );
            androidx.compose.foundation.layout.Arrangement.Horizontal var378 = Arrangement.INSTANCE.getStart();
            MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var378, var347, var70, 14 & var351 >> 3 | 112 & var351 >> 3);
            Modifier var363 = var344;
            MeasurePolicy var370 = measurePolicy$ivx;
            Composer var83 = var70;
            int var86 = 112 & var351 << 3;
            int $i$f$Layoutx = 0;
            ComposerKt.sourceInformationMarkerStart(var83, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var83, 0));
            CompositionLocalMap localMap$iv$ivx = var83.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var83, var363);
            Function0 var91 = ComposeUiNode.Companion.getConstructor();
            Composer var92 = var83;
            int var93 = 6 | 896 & var86 << 6;
            int $i$f$ReusableComposeNodex = 0;
            ComposerKt.sourceInformationMarkerStart(var92, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var92.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var92.startReusableNode();
            if (var92.getInserting()) {
               var92.createNode(var91);
            } else {
               var92.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var92);
            int var96/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var370, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
            int var97 = 14 & var93 >> 6;
            Composer $composer$ivx = var92;
            int var99 = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            int var100 = 6 | 112 & var351 >> 6;
            Composer var101 = $composer$ivx;
            RowScope var102 = RowScopeInstance.INSTANCE;
            int var103 = 0;
            ComposerKt.sourceInformationMarkerStart(var101, 1080720839, "C272@11711L40,273@11768L1634:MessageComposer.kt#yb6g9");
            Modifier var513 = Modifier.Companion;
            int $this$dp$ivxx = 16;
            int $i$f$getDpxx = 0;
            SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var513, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var101, 6);
            Modifier var514 = RowScope.weight$default(var102, Modifier.Companion, 1.0F, false, 2, null);
            float var536;
            if (var251) {
               int $this$dp$ivxxx = 16;
               int $i$f$getDpxxx = 0;
               var536 = Dp.recovered$95b3d103663bf060($this$dp$ivxxx);
            } else {
               int $this$dp$ivxxx = 0;
               int $i$f$getDpxxx = 0;
               var536 = Dp.recovered$95b3d103663bf060($this$dp$ivxxx);
            }

            Modifier var402 = PaddingKt.recovered$03b481d31d62885a(var514, 0.0F, var536, 0.0F, 0.0F, 13, null);
            byte var106 = 0;
            int var107 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var101, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
            );
            Alignment var412 = Alignment.Companion.getTopStart();
            boolean var108 = false;
            MeasurePolicy measurePolicy$ivxx = BoxKt.maybeCachedBoxMeasurePolicy(var412, var108);
            Modifier var110 = var402;
            MeasurePolicy var111 = measurePolicy$ivxx;
            Composer var112 = var101;
            int var113 = 112 & var106 << 3;
            int $i$f$Layoutxx = 0;
            ComposerKt.sourceInformationMarkerStart(var112, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var112, 0));
            CompositionLocalMap localMap$iv$ivxx = var112.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var112, var110);
            Function0 var118 = ComposeUiNode.Companion.getConstructor();
            Composer var119 = var112;
            int var120 = 6 | 896 & var113 << 6;
            int $i$f$ReusableComposeNodexx = 0;
            ComposerKt.sourceInformationMarkerStart(var119, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var119.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var119.startReusableNode();
            if (var119.getInserting()) {
               var119.createNode(var118);
            } else {
               var119.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var119);
            int var123/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var111, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
            int var124 = 14 & var120 >> 6;
            Composer $composer$ivxx = var119;
            int var126 = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivxx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
            int var127 = 6 | 112 & var106 >> 6;
            Composer var128 = $composer$ivxx;
            BoxScope var129 = BoxScopeInstance.INSTANCE;
            int var130 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var128, -1384877799, "C289@12506L154,*298@12970L6,300@13109L10,301@13183L6,294@12802L114,285@12307L1077:MessageComposer.kt#yb6g9"
            );
            var128.startReplaceGroup(-598863904);
            ComposerKt.sourceInformation(var128, "280@12090L9,281@12151L6,282@12220L10,279@12040L224");
            if (MessageComposer$lambda$26(var211).getText().length() == 0) {
               TextKt.recovered$5b114ab32a4f4190(
                  var187.getText(var128, StringProvider.$stable),
                  null,
                  IntercomTheme.INSTANCE.getColors(var128, IntercomTheme.$stable).getText().recovered$0aef7413383f6122(),
                  null,
                  0L,
                  null,
                  null,
                  null,
                  0L,
                  null,
                  null,
                  0L,
                  0,
                  false,
                  0,
                  0,
                  null,
                  IntercomTheme.INSTANCE.getTypography(var128, IntercomTheme.$stable).getType04(),
                  var128,
                  0,
                  0,
                  131066
               );
            }

            var128.endReplaceGroup();
            Modifier var515 = SizeKt.fillMaxWidth$default(TestTagKt.testTag(Modifier.Companion, "text_input"), 0.0F, 1, null);
            var128.startReplaceGroup(-598847193);
            ComposerKt.sourceInformation(var128, "CC(remember):MessageComposer.kt#9igjgp");
            Composer var131 = var128;
            boolean var132 = var128.changed(var265) | var128.changed(var263);
            int $i$f$cachexxxxxxxx = 0;
            Object it$ivxxxxxxxx = var131.rememberedValue();
            int var135/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxx */ = 0;
            Object var531;
            if (!var132 && it$ivxxxxxxxx != Composer.Companion.getEmpty()) {
               var531 = it$ivxxxxxxxx;
            } else {
               Modifier var136 = var515;
               int var137 = 0;
               final long recoveredCapture_615_0 = var265;
               final long recoveredCapture_615_1 = var263;
               final androidx.compose.runtime.MutableState recoveredCapture_615_2 = var268;
               Function1 var530 = ((kotlin.jvm.functions.Function1) (recoveredArgument_615_0) -> MessageComposerKt.MessageComposer$lambda$69$lambda$59$lambda$56$lambda$52$lambda$51(recoveredCapture_615_0, recoveredCapture_615_1, recoveredCapture_615_2, (androidx.compose.ui.focus.FocusState)recoveredArgument_615_0));
               var515 = var136;
               Object var138 = var530;
               var131.updateRememberedValue(var138);
               var531 = var138;
            }

            Function1 var139 = (Function1)var531;
            var128.endReplaceGroup();
            Modifier var140 = FocusChangedModifierKt.onFocusChanged(var515, var139);
            TextFieldValue var476 = MessageComposer$lambda$26(var211);
            boolean var467 = !var199;
            long var141 = IntercomTheme.INSTANCE.getColors(var128, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
            int var473 = 0;
            SolidColor var468 = new SolidColor(var141, null);
            TextStyle var469 = TextStyle.recovered$cf01d8b71690b5c5(
               IntercomTheme.INSTANCE.getTypography(var128, IntercomTheme.$stable).getType04(),
               IntercomTheme.INSTANCE.getColors(var128, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(),
               0L,
               null,
               null,
               null,
               null,
               null,
               0L,
               null,
               null,
               null,
               0L,
               null,
               null,
               null,
               0,
               0,
               0L,
               null,
               null,
               null,
               0,
               0,
               null,
               16777214,
               null
            );
            TextFieldValue var516 = var476;
            var128.startReplaceGroup(-598837761);
            ComposerKt.sourceInformation(var128, "CC(remember):MessageComposer.kt#9igjgp");
            Composer var471 = var128;
            boolean var474 = (var22 & 29360128) == 8388608 | var150.changed(var211);
            int $i$f$cachexxxxxxxxx = 0;
            Object it$ivxxxxxxxxx = var471.rememberedValue();
            int var144/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxx */ = 0;
            Object var533;
            if (!var474 && it$ivxxxxxxxxx != Composer.Companion.getEmpty()) {
               var533 = it$ivxxxxxxxxx;
            } else {
               TextFieldValue var472 = var476;
               int var145 = 0;
               final kotlin.jvm.functions.Function0 recoveredCapture_616_0 = var7;
               final androidx.compose.runtime.MutableState recoveredCapture_616_1 = var211;
               Function1 var532 = ((kotlin.jvm.functions.Function1) (recoveredArgument_616_0) -> MessageComposerKt.MessageComposer$lambda$69$lambda$59$lambda$56$lambda$55$lambda$54(recoveredCapture_616_0, recoveredCapture_616_1, (androidx.compose.ui.text.input.TextFieldValue)recoveredArgument_616_0));
               var516 = var472;
               Object var146 = var532;
               var471.updateRememberedValue(var146);
               var533 = var146;
            }

            Function1 var470 = (Function1)var533;
            var128.endReplaceGroup();
            BasicTextFieldKt.BasicTextField(
               var516,
               var470,
               var140,
               var467,
               false,
               var469,
               null,
               null,
               false,
               var273,
               0,
               null,
               null,
               null,
               var468,
               io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$MessageComposerKt.INSTANCE.recovered$b26556cd5c386b55(),
               var128,
               0,
               196608,
               15824
            );
            ComposerKt.sourceInformationMarkerEnd(var128);
            ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
            var119.endNode();
            ComposerKt.sourceInformationMarkerEnd(var119);
            ComposerKt.sourceInformationMarkerEnd(var112);
            ComposerKt.sourceInformationMarkerEnd(var101);
            if (var251) {
               var101.startReplaceGroup(1082374564);
               ComposerKt.sourceInformation(var101, "309@13474L40");
               Modifier var517 = Modifier.Companion;
               int $this$dp$ivxxx = 16;
               int $i$f$getDpxxx = 0;
               SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var517, Dp.recovered$95b3d103663bf060($this$dp$ivxxx)), var101, 6);
               var101.endReplaceGroup();
            } else {
               var101.startReplaceGroup(1082476585);
               ComposerKt.sourceInformation(var101, "317@13880L236,311@13560L579");
               boolean var404 = canSendMessage(MessageComposer$lambda$26(var211).getText(), var2.getMediaUploadItems(), var199);
               var101.startReplaceGroup(1697497146);
               ComposerKt.sourceInformation(var101, "CC(remember):MessageComposer.kt#9igjgp");
               Composer var430 = var101;
               boolean var420 = (var22 & 112) == 32 | var150.changed(var211) | var150.changedInstance(var2) | (var23 & 29360128) == 8388608;
               int $i$f$cachexxxxxxxxxx = 0;
               Object it$ivxxxxxxxxxx = var430.rememberedValue();
               int var440/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxx */ = 0;
               Object var518;
               if (!var420 && it$ivxxxxxxxxxx != Composer.Companion.getEmpty()) {
                  var518 = it$ivxxxxxxxxxx;
               } else {
                  int var444 = 0;
                  final kotlin.jvm.functions.Function2 recoveredCapture_617_0 = var1;
                  final io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput recoveredCapture_617_1 = var2;
                  final kotlin.jvm.functions.Function0 recoveredCapture_617_2 = var17;
                  final androidx.compose.runtime.MutableState recoveredCapture_617_3 = var211;
                  Object var448 = ((kotlin.jvm.functions.Function0) () -> MessageComposerKt.MessageComposer$lambda$69$lambda$59$lambda$58$lambda$57(recoveredCapture_617_0, recoveredCapture_617_1, recoveredCapture_617_2, recoveredCapture_617_3));
                  var430.updateRememberedValue(var448);
                  var518 = var448;
               }

               Function0 var414 = (Function0)var518;
               var101.endReplaceGroup();
               ComposerActionButton(var414, null, var404, 0, var101, 0, 10);
               var101.endReplaceGroup();
            }

            ComposerKt.sourceInformationMarkerEnd(var101);
            ComposerKt.sourceInformationMarkerEnd($composer$ivx);
            var92.endNode();
            ComposerKt.sourceInformationMarkerEnd(var92);
            ComposerKt.sourceInformationMarkerEnd(var83);
            ComposerKt.sourceInformationMarkerEnd(var70);
            if (var251) {
               Modifier var345 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
               androidx.compose.foundation.layout.Arrangement.Horizontal var379 = Arrangement.INSTANCE.getSpaceBetween();
               androidx.compose.ui.Alignment.Vertical var348 = Alignment.Companion.getCenterVertically();
               short var352 = 438;
               int $i$f$Rowx = 0;
               ComposerKt.sourceInformationMarkerStart(
                  var70, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
               );
               MeasurePolicy measurePolicy$ivxxx = RowKt.rowMeasurePolicy(var379, var348, var70, 14 & var352 >> 3 | 112 & var352 >> 3);
               Modifier var364 = var345;
               MeasurePolicy var371 = measurePolicy$ivxxx;
               Composer var375 = var70;
               int var382 = 112 & var352 << 3;
               int $i$f$Layoutxxx = 0;
               ComposerKt.sourceInformationMarkerStart(
                  var375, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh"
               );
               int compositeKeyHash$iv$ivxxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var375, 0));
               CompositionLocalMap localMap$iv$ivxxx = var375.getCurrentCompositionLocalMap();
               Modifier materialized$iv$ivxxx = ComposedModifierKt.materializeModifier(var375, var364);
               Function0 var387 = ComposeUiNode.Companion.getConstructor();
               Composer var388 = var375;
               int var389 = 6 | 896 & var382 << 6;
               int $i$f$ReusableComposeNodexxx = 0;
               ComposerKt.sourceInformationMarkerStart(var388, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
               if (!(var388.getApplier() instanceof Applier)) {
                  ComposablesKt.invalidApplier();
               }

               var388.startReusableNode();
               if (var388.getInserting()) {
                  var388.createNode(var387);
               } else {
                  var388.useNode();
               }

               Composer $this$Layout_u24lambda_u240$iv$ivxxx = Updater.recovered$676aa17b44fd6280(var388);
               int var392/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxxx */ = 0;
               Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, var371, ComposeUiNode.Companion.getSetMeasurePolicy());
               Updater.recovered$89682b199a142880(
                  $this$Layout_u24lambda_u240$iv$ivxxx, localMap$iv$ivxxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals()
               );
               Updater.recovered$89682b199a142880(
                  $this$Layout_u24lambda_u240$iv$ivxxx, compositeKeyHash$iv$ivxxx, ComposeUiNode.Companion.getSetCompositeKeyHash()
               );
               Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
               Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, materialized$iv$ivxxx, ComposeUiNode.Companion.getSetModifier());
               int var393 = 14 & var389 >> 6;
               Composer $composer$ivxxx = var388;
               int var395/* $VF was: $i$a$-Layout-RowKt$Row$1$ivx */ = 0;
               ComposerKt.sourceInformationMarkerStart($composer$ivxxx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
               int var396 = 6 | 112 & var352 >> 6;
               Composer var397 = $composer$ivxxx;
               RowScope var398 = RowScopeInstance.INSTANCE;
               int var399 = 0;
               ComposerKt.sourceInformationMarkerStart(
                  var397, 1083465207, "C337@14744L181,341@14974L78,344@15106L156,333@14511L774,355@15626L236,349@15306L579:MessageComposer.kt#yb6g9"
               );
               Modifier var519 = Modifier.Companion;
               int $this$dp$ivxxx = 4;
               int $i$f$getDpxxx = 0;
               Modifier var520 = PaddingKt.recovered$eb6a744cd98cdbca(var519, Dp.recovered$95b3d103663bf060($this$dp$ivxxx));
               List var534 = var249;
               boolean var537 = !var199;
               var397.startReplaceGroup(1697524739);
               ComposerKt.sourceInformation(var397, "CC(remember):MessageComposer.kt#9igjgp");
               Composer var416 = var397;
               boolean var421 = (var22 & 3670016) == 1048576 | (var22 & 7168) == 2048;
               int $i$f$cachexxxxxxxxxx = 0;
               Object it$ivxxxxxxxxxx = var416.rememberedValue();
               int var436/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxx */ = 0;
               Object var10003;
               if (!var421 && it$ivxxxxxxxxxx != Composer.Companion.getEmpty()) {
                  var10003 = it$ivxxxxxxxxxx;
               } else {
                  boolean var441 = var537;
                  List var445 = var249;
                  Modifier var449 = var520;
                  int var452 = 0;
                  final kotlin.jvm.functions.Function1 recoveredCapture_618_0 = var6;
                  final kotlin.jvm.functions.Function0 recoveredCapture_618_1 = var3;
                  Function0 var456 = ((kotlin.jvm.functions.Function0) () -> MessageComposerKt.MessageComposer$lambda$69$lambda$68$lambda$61$lambda$60(recoveredCapture_618_0, recoveredCapture_618_1));
                  var520 = var449;
                  var534 = var445;
                  var537 = var441;
                  Object var459 = var456;
                  var416.updateRememberedValue(var459);
                  var10003 = var459;
               }

               Function0 var406 = (Function0)var10003;
               var397.endReplaceGroup();
               Function0 var540 = var406;
               var397.startReplaceGroup(1697531996);
               ComposerKt.sourceInformation(var397, "CC(remember):MessageComposer.kt#9igjgp");
               Composer var417 = var397;
               boolean var422 = (var22 & 57344) == 16384;
               int $i$f$cachexxxxxxxxxxx = 0;
               Object it$ivxxxxxxxxxxx = var417.rememberedValue();
               int var437/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxx */ = 0;
               Object var10004;
               if (!var422 && it$ivxxxxxxxxxxx != Composer.Companion.getEmpty()) {
                  var10004 = it$ivxxxxxxxxxxx;
               } else {
                  Function0 var457 = var406;
                  boolean var442 = var537;
                  List var446 = var534;
                  Modifier var450 = var520;
                  int var453 = 0;
                  final kotlin.jvm.functions.Function0 recoveredCapture_619_0 = var4;
                  Function0 var463 = ((kotlin.jvm.functions.Function0) () -> MessageComposerKt.MessageComposer$lambda$69$lambda$68$lambda$63$lambda$62(recoveredCapture_619_0));
                  var520 = var450;
                  var534 = var446;
                  var537 = var442;
                  var540 = var457;
                  Object var460 = var463;
                  var417.updateRememberedValue(var460);
                  var10004 = var460;
               }

               Function0 var407 = (Function0)var10004;
               var397.endReplaceGroup();
               Function0 var542 = var407;
               var397.startReplaceGroup(1697536298);
               ComposerKt.sourceInformation(var397, "CC(remember):MessageComposer.kt#9igjgp");
               Composer var418 = var397;
               boolean var423 = var397.changed(var284);
               int $i$f$cachexxxxxxxxxxxx = 0;
               Object it$ivxxxxxxxxxxxx = var418.rememberedValue();
               int var438/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxx */ = 0;
               Object var10005;
               if (!var423 && it$ivxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
                  var10005 = it$ivxxxxxxxxxxxx;
               } else {
                  Function0 var464 = var407;
                  Function0 var458 = var540;
                  boolean var443 = var537;
                  List var447 = var534;
                  Modifier var451 = var520;
                  int var454 = 0;
                  final kotlin.jvm.functions.Function0 recoveredCapture_620_0 = var284;
                  Function0 var465 = ((kotlin.jvm.functions.Function0) () -> MessageComposerKt.MessageComposer$lambda$69$lambda$68$lambda$65$lambda$64(recoveredCapture_620_0));
                  var520 = var451;
                  var534 = var447;
                  var537 = var443;
                  var540 = var458;
                  var542 = var464;
                  Object var461 = var465;
                  var418.updateRememberedValue(var461);
                  var10005 = var461;
               }

               Function0 var408 = (Function0)var10005;
               var397.endReplaceGroup();
               BottomBarButtonComponentKt.BottomBarButtonComponent(var520, var534, var537, var540, var542, var408, var397, 6, 0);
               boolean var409 = canSendMessage(MessageComposer$lambda$26(var211).getText(), var2.getMediaUploadItems(), var199);
               var397.startReplaceGroup(1697553018);
               ComposerKt.sourceInformation(var397, "CC(remember):MessageComposer.kt#9igjgp");
               Composer var424 = var397;
               boolean var429 = (var22 & 112) == 32 | var150.changed(var211) | var150.changedInstance(var2) | (var23 & 29360128) == 8388608;
               int $i$f$cachexxxxxxxxxxxxx = 0;
               Object it$ivxxxxxxxxxxxxx = var424.rememberedValue();
               int var455/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxxxxxxx */ = 0;
               Object var521;
               if (!var429 && it$ivxxxxxxxxxxxxx != Composer.Companion.getEmpty()) {
                  var521 = it$ivxxxxxxxxxxxxx;
               } else {
                  int var462 = 0;
                  final kotlin.jvm.functions.Function2 recoveredCapture_621_0 = var1;
                  final io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput recoveredCapture_621_1 = var2;
                  final kotlin.jvm.functions.Function0 recoveredCapture_621_2 = var17;
                  final androidx.compose.runtime.MutableState recoveredCapture_621_3 = var211;
                  Object var466 = ((kotlin.jvm.functions.Function0) () -> MessageComposerKt.MessageComposer$lambda$69$lambda$68$lambda$67$lambda$66(recoveredCapture_621_0, recoveredCapture_621_1, recoveredCapture_621_2, recoveredCapture_621_3));
                  var424.updateRememberedValue(var466);
                  var521 = var466;
               }

               Function0 var419 = (Function0)var521;
               var397.endReplaceGroup();
               ComposerActionButton(var419, null, var409, 0, var397, 0, 10);
               ComposerKt.sourceInformationMarkerEnd(var397);
               ComposerKt.sourceInformationMarkerEnd($composer$ivxxx);
               var388.endNode();
               ComposerKt.sourceInformationMarkerEnd(var388);
               ComposerKt.sourceInformationMarkerEnd(var375);
               ComposerKt.sourceInformationMarkerEnd(var70);
            }

            var70.endReplaceGroup();
         }

         ComposerKt.sourceInformationMarkerEnd(var70);
         ComposerKt.sourceInformationMarkerEnd(var67);
         var61.endNode();
         ComposerKt.sourceInformationMarkerEnd(var61);
         ComposerKt.sourceInformationMarkerEnd(var54);
         ComposerKt.sourceInformationMarkerEnd(var150);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var163 = var150.endRestartGroup();
      if (var163 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_622_0 = var0;
         final kotlin.jvm.functions.Function2 recoveredCapture_622_1 = var1;
         final io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput recoveredCapture_622_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredCapture_622_3 = var3;
         final kotlin.jvm.functions.Function0 recoveredCapture_622_4 = var4;
         final kotlin.jvm.functions.Function1 recoveredCapture_622_5 = var5;
         final kotlin.jvm.functions.Function1 recoveredCapture_622_6 = var6;
         final kotlin.jvm.functions.Function0 recoveredCapture_622_7 = var7;
         final kotlinx.coroutines.flow.SharedFlow recoveredCapture_622_8 = var8;
         final io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState recoveredCapture_622_9 = var9;
         final kotlinx.coroutines.flow.StateFlow recoveredCapture_622_10 = var10;
         final kotlin.jvm.functions.Function0 recoveredCapture_622_11 = var11;
         final kotlin.jvm.functions.Function0 recoveredCapture_622_12 = var12;
         final kotlin.jvm.functions.Function0 recoveredCapture_622_13 = var13;
         final kotlin.jvm.functions.Function0 recoveredCapture_622_14 = var14;
         final kotlin.jvm.functions.Function1 recoveredCapture_622_15 = var15;
         final kotlin.jvm.functions.Function1 recoveredCapture_622_16 = var16;
         final kotlin.jvm.functions.Function0 recoveredCapture_622_17 = var17;
         final int recoveredCapture_622_18 = var19;
         final int recoveredCapture_622_19 = var20;
         final int recoveredCapture_622_20 = var21;
         var163.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_622_0, recoveredArgument_622_1) -> MessageComposerKt.MessageComposer$lambda$70(recoveredCapture_622_0, recoveredCapture_622_1, recoveredCapture_622_2, recoveredCapture_622_3, recoveredCapture_622_4, recoveredCapture_622_5, recoveredCapture_622_6, recoveredCapture_622_7, recoveredCapture_622_8, recoveredCapture_622_9, recoveredCapture_622_10, recoveredCapture_622_11, recoveredCapture_622_12, recoveredCapture_622_13, recoveredCapture_622_14, recoveredCapture_622_15, recoveredCapture_622_16, recoveredCapture_622_17, recoveredCapture_622_18, recoveredCapture_622_19, recoveredCapture_622_20, (androidx.compose.runtime.Composer)recoveredArgument_622_0, ((java.lang.Integer)recoveredArgument_622_1).intValue())));
      }
   }

   private static final boolean canSendMessage(String var0, List var1, boolean var2) {
      if (var2) {
         return false;
      }

      boolean var3 = !StringsKt.isBlank(var0) || !var1.isEmpty();
      if (!var3) {
         return false;
      }

      var var4 = var1;
      int var5 = 0;
      boolean var10000;
      if (var4 instanceof Collection && ((Collection)var4).isEmpty()) {
         var10000 = true;
      } else {
         Iterator var6 = var4.iterator();

         while (true) {
            if (!var6.hasNext()) {
               var10000 = true;
               break;
            }

            Object var7 = var6.next();
            MediaUploadItem var8 = (MediaUploadItem)var7;
            int var9 = 0;
            if (!var8.isSuccess()) {
               var10000 = false;
               break;
            }
         }
      }

      return var10000;
   }

   public static final float getComposerMinSize() {
      return ComposerMinSize;
   }

   public static final float getComposerHalfSize() {
      return ComposerHalfSize;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ComposerActionButton(Function0 var0, Modifier var1, boolean var2, @DrawableRes int var3, Composer var4, int var5, int var6) {
      Composer var11 = var4.startRestartGroup(-960429944);
      ComposerKt.sourceInformation(
         var11,
         "C(ComposerActionButton)P(3,2)408@17212L6,409@17281L6,410@17362L6,411@17437L6,407@17151L317,413@17476L179,400@16906L749:MessageComposer.kt#yb6g9"
      );
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var11.changedInstance(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var11.changed(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var11.changed(var2) ? 256 : 128;
      }

      if ((var5 & 3072) == 0) {
         var7 |= (var6 & 8) == 0 && var11.changed(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var11.getSkipping()) {
         var11.skipToGroupEnd();
      } else {
         var11.startDefaults();
         if ((var5 & 1) != 0 && !var11.getDefaultsInvalid()) {
            var11.skipToGroupEnd();
            if ((var6 & 8) != 0) {
               var7 &= -7169;
            }
         } else {
            if ((var6 & 2) != 0) {
               var1 = Modifier.Companion;
            }

            if ((var6 & 4) != 0) {
               var2 = true;
            }

            if ((var6 & 8) != 0) {
               var3 = drawable.intercom_ic_arrow_up;
               var7 &= -7169;
            }
         }

         var11.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -960429944, var7, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposerActionButton (MessageComposer.kt:399)"
            );
         }

         Modifier var10000 = InteractiveComponentSizeKt.minimumInteractiveComponentSize(TestTagKt.testTag(var1, "send_button"));
         int var9 = 32;
         int var10 = 0;
         Modifier var8 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var9));
         IconButtonColors var12 = IconButtonDefaults.INSTANCE
            .recovered$d0285dfef4036e76(
               IntercomTheme.INSTANCE.getColors(var11, IntercomTheme.$stable).recovered$d5d3a0ddb8f4248d(),
               IntercomTheme.INSTANCE.getColors(var11, IntercomTheme.$stable).recovered$b32e6d5b37f98566(),
               IntercomTheme.INSTANCE.getColors(var11, IntercomTheme.$stable).getContainer().recovered$e2b74d614aa09788(),
               IntercomTheme.INSTANCE.getColors(var11, IntercomTheme.$stable).getIcon().recovered$090a6e753d82e539(),
               var11,
               IconButtonDefaults.$stable << 12,
               0
            );
         IconButtonKt.FilledIconButton(
            var0,
            var8,
            var2,
            null,
            var12,
            null,
            ComposableLambdaKt.rememberComposableLambda(-1864507480, true, new MessageComposerKt$ComposerActionButton$1(var3), var11, 54),
            var11,
            1572864 | 14 & var7 | 896 & var7,
            40
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var13 = var11.endRestartGroup();
      if (var13 != null) {
         final kotlin.jvm.functions.Function0 recoveredCapture_600_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_600_1 = var1;
         final boolean recoveredCapture_600_2 = var2;
         final int recoveredCapture_600_3 = var3;
         final int recoveredCapture_600_4 = var5;
         final int recoveredCapture_600_5 = var6;
         var13.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_600_0, recoveredArgument_600_1) -> MessageComposerKt.ComposerActionButton$lambda$72(recoveredCapture_600_0, recoveredCapture_600_1, recoveredCapture_600_2, recoveredCapture_600_3, recoveredCapture_600_4, recoveredCapture_600_5, (androidx.compose.runtime.Composer)recoveredArgument_600_0, ((java.lang.Integer)recoveredArgument_600_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void VoiceNotesComposer(VoiceTranscriptionState var0, StateFlow var1, Function0 var2, Function0 var3, Composer var4, int var5) {
      Composer var93 = var4.startRestartGroup(-592615267);
      ComposerKt.sourceInformation(var93, "C(VoiceNotesComposer)P(3)429@17853L3539:MessageComposer.kt#yb6g9");
      int var6 = var5;
      if ((var5 & 6) == 0) {
         var6 |= ((var5 & 8) == 0 ? var93.changed(var0) : var93.changedInstance(var0)) ? 4 : 2;
      }

      if ((var5 & 48) == 0) {
         var6 |= var93.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 384) == 0) {
         var6 |= var93.changedInstance(var2) ? 256 : 128;
      }

      if ((var5 & 3072) == 0) {
         var6 |= var93.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var6 & 1171) == 1170 && var93.getSkipping()) {
         var93.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -592615267, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.VoiceNotesComposer (MessageComposer.kt:428)"
            );
         }

         Modifier var7 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         androidx.compose.ui.Alignment.Vertical var8 = Alignment.Companion.getCenterVertically();
         int var10 = 8;
         int var11 = 0;
         HorizontalOrVertical var9 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var10));
         short var94 = 438;
         int var95 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var93, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var12 = RowKt.rowMeasurePolicy(var9, var8, var93, 14 & var94 >> 3 | 112 & var94 >> 3);
         Modifier var13 = var7;
         MeasurePolicy var14 = var12;
         Composer var15 = var93;
         int var16 = 112 & var94 << 3;
         int var17 = 0;
         ComposerKt.sourceInformationMarkerStart(var15, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var18 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var15, 0));
         CompositionLocalMap var19 = var15.getCurrentCompositionLocalMap();
         Modifier var20 = ComposedModifierKt.materializeModifier(var15, var13);
         Function0 var21 = ComposeUiNode.Companion.getConstructor();
         Composer var22 = var15;
         int var23 = 6 | 896 & var16 << 6;
         int var24 = 0;
         ComposerKt.sourceInformationMarkerStart(var22, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var22.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var22.startReusableNode();
         if (var22.getInserting()) {
            var22.createNode(var21);
         } else {
            var22.useNode();
         }

         Composer var25 = Updater.recovered$676aa17b44fd6280(var22);
         int var26 = 0;
         Updater.recovered$89682b199a142880(var25, var14, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var25, var19, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var25, var18, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var25, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var25, var20, ComposeUiNode.Companion.getSetModifier());
         int var27 = 14 & var23 >> 6;
         Composer var28 = var22;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var30 = 6 | 112 & var94 >> 6;
         Composer var31 = var28;
         RowScope var32 = RowScopeInstance.INSTANCE;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var31,
            701136646,
            "C440@18306L6,441@18373L6,442@18447L6,443@18526L6,439@18241L320,434@18034L765,452@18808L2213,510@21214L172:MessageComposer.kt#yb6g9"
         );
         Modifier var10000 = InteractiveComponentSizeKt.minimumInteractiveComponentSize(Modifier.Companion);
         int $this$dp$ivx = 32;
         int $i$f$getDpx = 0;
         Modifier var36 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx));
         IconButtonColors var96 = IconButtonDefaults.INSTANCE
            .recovered$d0285dfef4036e76(
               IntercomTheme.INSTANCE.getColors(var31, IntercomTheme.$stable).getAlpha().recovered$146d1d8b5c75e8db(),
               IntercomTheme.INSTANCE.getColors(var31, IntercomTheme.$stable).getIcon().recovered$0aef7413383f6122(),
               IntercomTheme.INSTANCE.getColors(var31, IntercomTheme.$stable).getContainer().recovered$e2b74d614aa09788(),
               IntercomTheme.INSTANCE.getColors(var31, IntercomTheme.$stable).getIcon().recovered$090a6e753d82e539(),
               var31,
               IconButtonDefaults.$stable << 12,
               0
            );
         IconButtonKt.FilledIconButton(
            var2,
            var36,
            false,
            null,
            var96,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$MessageComposerKt.INSTANCE.recovered$1400af5e34386102(),
            var31,
            1572912 | 14 & var6 >> 6,
            44
         );
         Modifier var100 = RowScope.weight$default(var32, Modifier.Companion, 1.0F, false, 2, null);
         Alignment var97 = Alignment.Companion.getCenter();
         byte var37 = 48;
         int var38 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var31, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var99 = false;
         MeasurePolicy measurePolicy$ivx = BoxKt.maybeCachedBoxMeasurePolicy(var97, var99);
         Modifier var40 = var100;
         MeasurePolicy var41 = measurePolicy$ivx;
         Composer var42 = var31;
         int var43 = 112 & var37 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var42, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var42, 0));
         CompositionLocalMap localMap$iv$ivx = var42.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var42, var40);
         Function0 var48 = ComposeUiNode.Companion.getConstructor();
         Composer var49 = var42;
         int var50 = 6 | 896 & var43 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var49, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var49.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var49.startReusableNode();
         if (var49.getInserting()) {
            var49.createNode(var48);
         } else {
            var49.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var49);
         int var53/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var41, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var54 = 14 & var50 >> 6;
         Composer $composer$ivx = var49;
         int var56 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var57 = 6 | 112 & var37 >> 6;
         Composer var58 = $composer$ivx;
         BoxScope var59 = BoxScopeInstance.INSTANCE;
         int var60 = 0;
         ComposerKt.sourceInformationMarkerStart(var58, 1111179902, "C:MessageComposer.kt#yb6g9");
         VoiceTranscriptionState var61 = var0;
         if (var61 instanceof io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Recording) {
            var58.startReplaceGroup(1111192239);
            ComposerKt.sourceInformation(var58, "460@19150L6,458@19023L167");
            SoundWaveVisualizationKt.recovered$92b4091f81df4daa(
               var1, null, IntercomTheme.INSTANCE.getColors(var58, IntercomTheme.$stable).getText().recovered$0aef7413383f6122(), var58, 14 & var6 >> 3, 2
            );
            var58.endReplaceGroup();
         } else if (var61 instanceof io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Processing) {
            var58.startReplaceGroup(1111473719);
            ComposerKt.sourceInformation(var58, "465@19289L735");
            androidx.compose.ui.Alignment.Vertical var62 = Alignment.Companion.getCenterVertically();
            int $this$dp$ivxx = 4;
            int $i$f$getDpxx = 0;
            HorizontalOrVertical var65 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060($this$dp$ivxx));
            short var106 = 432;
            int $i$f$Rowx = 0;
            ComposerKt.sourceInformationMarkerStart(
               var58, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
            );
            Modifier var103 = Modifier.Companion;
            MeasurePolicy measurePolicy$ivxx = RowKt.rowMeasurePolicy(var65, var62, var58, 14 & var106 >> 3 | 112 & var106 >> 3);
            Modifier var68 = var103;
            MeasurePolicy var69 = measurePolicy$ivxx;
            Composer var70 = var58;
            int var71 = 112 & var106 << 3;
            int $i$f$Layoutxx = 0;
            ComposerKt.sourceInformationMarkerStart(var70, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var70, 0));
            CompositionLocalMap localMap$iv$ivxx = var70.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var70, var68);
            Function0 var76 = ComposeUiNode.Companion.getConstructor();
            Composer var77 = var70;
            int var78 = 6 | 896 & var71 << 6;
            int $i$f$ReusableComposeNodexx = 0;
            ComposerKt.sourceInformationMarkerStart(var77, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var77.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var77.startReusableNode();
            if (var77.getInserting()) {
               var77.createNode(var76);
            } else {
               var77.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var77);
            int var81/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var69, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
            int var82 = 14 & var78 >> 6;
            Composer $composer$ivxx = var77;
            int var84/* $VF was: $i$a$-Layout-RowKt$Row$1$ivx */ = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivxx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            int var85 = 6 | 112 & var106 >> 6;
            Composer var86 = $composer$ivxx;
            RowScope var87 = RowScopeInstance.INSTANCE;
            int var88 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var86, 704692832, "C471@19628L6,469@19490L230,475@19786L51,476@19889L10,477@19958L6,474@19745L257:MessageComposer.kt#yb6g9"
            );
            Modifier var137 = Modifier.Companion;
            int $this$dp$ivxxx = 16;
            int $i$f$getDpxxx = 0;
            Modifier var138 = SizeKt.recovered$111ef94c93e78c04(var137, Dp.recovered$95b3d103663bf060($this$dp$ivxxx));
            long var10001 = IntercomTheme.INSTANCE.getColors(var86, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
            int $this$dp$ivxxxx = 2;
            int $i$f$getDpxxxx = 0;
            ProgressIndicatorKt.recovered$276fbc1f701bb35f(var138, var10001, Dp.recovered$95b3d103663bf060($this$dp$ivxxxx), 0L, 0, 0.0F, var86, 390, 56);
            String var134 = StringResources_androidKt.stringResource(string.intercom_transcribing, var86, 0);
            TextStyle var136 = IntercomTheme.INSTANCE.getTypography(var86, IntercomTheme.$stable).getType04();
            long var91 = IntercomTheme.INSTANCE.getColors(var86, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
            TextKt.recovered$5b114ab32a4f4190(
               var134, null, var91, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var136, var86, 0, 0, 131066
            );
            ComposerKt.sourceInformationMarkerEnd(var86);
            ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
            var77.endNode();
            ComposerKt.sourceInformationMarkerEnd(var77);
            ComposerKt.sourceInformationMarkerEnd(var70);
            ComposerKt.sourceInformationMarkerEnd(var58);
            var58.endReplaceGroup();
         } else if (var61 instanceof io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Error) {
            var58.startReplaceGroup(1112296025);
            ComposerKt.sourceInformation(var58, "483@20118L733");
            androidx.compose.ui.Alignment.Vertical var102 = Alignment.Companion.getCenterVertically();
            int $this$dp$ivxx = 4;
            int $i$f$getDpxx = 0;
            HorizontalOrVertical var109 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060($this$dp$ivxx));
            short var108 = 432;
            int $i$f$Rowx = 0;
            ComposerKt.sourceInformationMarkerStart(
               var58, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
            );
            Modifier var105 = Modifier.Companion;
            MeasurePolicy measurePolicy$ivxx = RowKt.rowMeasurePolicy(var109, var102, var58, 14 & var108 >> 3 | 112 & var108 >> 3);
            Modifier var112 = var105;
            MeasurePolicy var113 = measurePolicy$ivxx;
            Composer var114 = var58;
            int var115 = 112 & var108 << 3;
            int $i$f$Layoutxx = 0;
            ComposerKt.sourceInformationMarkerStart(var114, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var114, 0));
            CompositionLocalMap localMap$iv$ivxx = var114.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var114, var112);
            Function0 var120 = ComposeUiNode.Companion.getConstructor();
            Composer var121 = var114;
            int var122 = 6 | 896 & var115 << 6;
            int $i$f$ReusableComposeNodexx = 0;
            ComposerKt.sourceInformationMarkerStart(var121, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var121.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var121.startReusableNode();
            if (var121.getInserting()) {
               var121.createNode(var120);
            } else {
               var121.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var121);
            int var125/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var113, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
            int var126 = 14 & var122 >> 6;
            Composer $composer$ivxx = var121;
            int var128/* $VF was: $i$a$-Layout-RowKt$Row$1$ivx */ = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivxx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            int var129 = 6 | 112 & var108 >> 6;
            Composer var130 = $composer$ivxx;
            RowScope var131 = RowScopeInstance.INSTANCE;
            int var132 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var130, 705515138, "C488@20363L52,490@20521L6,487@20319L246,493@20655L9,494@20716L6,495@20785L10,492@20590L239:MessageComposer.kt#yb6g9"
            );
            IconKt.recovered$e00adffb7fe45b02(
               PainterResources_androidKt.painterResource(drawable.intercom_ic_error, var130, 0),
               null,
               null,
               IntercomTheme.INSTANCE.getColors(var130, IntercomTheme.$stable).getIcon().recovered$2bcfe565407cdfb8(),
               var130,
               48 | Painter.$stable,
               4
            );
            TextKt.recovered$5b114ab32a4f4190(
               ((io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Error)var0).getError().getUserMessage().getText(var130, StringProvider.$stable),
               null,
               IntercomTheme.INSTANCE.getColors(var130, IntercomTheme.$stable).getText().recovered$2bcfe565407cdfb8(),
               null,
               0L,
               null,
               null,
               null,
               0L,
               null,
               null,
               0L,
               0,
               false,
               0,
               0,
               null,
               IntercomTheme.INSTANCE.getTypography(var130, IntercomTheme.$stable).getType04(),
               var130,
               0,
               0,
               131066
            );
            ComposerKt.sourceInformationMarkerEnd(var130);
            ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
            var121.endNode();
            ComposerKt.sourceInformationMarkerEnd(var121);
            ComposerKt.sourceInformationMarkerEnd(var114);
            ComposerKt.sourceInformationMarkerEnd(var58);
            var58.endReplaceGroup();
         } else {
            var58.startReplaceGroup(1113067832);
            var58.endReplaceGroup();
         }

         ComposerKt.sourceInformationMarkerEnd(var58);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var49.endNode();
         ComposerKt.sourceInformationMarkerEnd(var49);
         ComposerKt.sourceInformationMarkerEnd(var42);
         ComposerKt.sourceInformationMarkerEnd(var31);
         int var101 = var0 instanceof io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Recording ? drawable.intercom_ic_stop : drawable.intercom_ic_arrow_up;
         boolean var98 = var0 instanceof io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Recording;
         ComposerActionButton(var3, null, var98, var101, var31, 14 & var6 >> 9, 2);
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var28);
         var22.endNode();
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var15);
         ComposerKt.sourceInformationMarkerEnd(var93);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var139 = var93.endRestartGroup();
      if (var139 != null) {
         final io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState recoveredCapture_633_0 = var0;
         final kotlinx.coroutines.flow.StateFlow recoveredCapture_633_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredCapture_633_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredCapture_633_3 = var3;
         final int recoveredCapture_633_4 = var5;
         var139.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_633_0, recoveredArgument_633_1) -> MessageComposerKt.VoiceNotesComposer$lambda$77(recoveredCapture_633_0, recoveredCapture_633_1, recoveredCapture_633_2, recoveredCapture_633_3, recoveredCapture_633_4, (androidx.compose.runtime.Composer)recoveredArgument_633_0, ((java.lang.Integer)recoveredArgument_633_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TextComposerPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-609144377);
      ComposerKt.sourceInformation(var2, "C(TextComposerPreview)521@21467L288:MessageComposer.kt#yb6g9");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -609144377, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.TextComposerPreview (MessageComposer.kt:520)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$MessageComposerKt.INSTANCE.recovered$fd838d44303763da(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_628_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_628_0, recoveredArgument_628_1) -> MessageComposerKt.TextComposerPreview$lambda$78(recoveredCapture_628_0, (androidx.compose.runtime.Composer)recoveredArgument_628_0, ((java.lang.Integer)recoveredArgument_628_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TextComposerWithButtonsPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1468421996);
      ComposerKt.sourceInformation(var2, "C(TextComposerWithButtonsPreview)535@21841L542:MessageComposer.kt#yb6g9");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1468421996, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.TextComposerWithButtonsPreview (MessageComposer.kt:534)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$MessageComposerKt.INSTANCE.recovered$54bdcf11cafa5012(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_629_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_629_0, recoveredArgument_629_1) -> MessageComposerKt.TextComposerWithButtonsPreview$lambda$79(recoveredCapture_629_0, (androidx.compose.runtime.Composer)recoveredArgument_629_0, ((java.lang.Integer)recoveredArgument_629_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TextComposerWithInitialTextPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-986390788);
      ComposerKt.sourceInformation(var2, "C(TextComposerWithInitialTextPreview)554@22473L604:MessageComposer.kt#yb6g9");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -986390788,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.TextComposerWithInitialTextPreview (MessageComposer.kt:553)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$MessageComposerKt.INSTANCE.recovered$265b6f46b79d310a(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_631_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_631_0, recoveredArgument_631_1) -> MessageComposerKt.TextComposerWithInitialTextPreview$lambda$80(recoveredCapture_631_0, (androidx.compose.runtime.Composer)recoveredArgument_631_0, ((java.lang.Integer)recoveredArgument_631_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TextComposerWithMediaItemsPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-71803831);
      ComposerKt.sourceInformation(var2, "C(TextComposerWithMediaItemsPreview)573@23166L1974:MessageComposer.kt#yb6g9");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -71803831, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.TextComposerWithMediaItemsPreview (MessageComposer.kt:572)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$MessageComposerKt.INSTANCE.recovered$5a1850a8ff371bfb(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_632_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_632_0, recoveredArgument_632_1) -> MessageComposerKt.TextComposerWithMediaItemsPreview$lambda$81(recoveredCapture_632_0, (androidx.compose.runtime.Composer)recoveredArgument_632_0, ((java.lang.Integer)recoveredArgument_632_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TextComposerWithFileItemsPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(233053391);
      ComposerKt.sourceInformation(var2, "C(TextComposerWithFileItemsPreview)614@25228L1143:MessageComposer.kt#yb6g9");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               233053391, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.TextComposerWithFileItemsPreview (MessageComposer.kt:613)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$MessageComposerKt.INSTANCE.recovered$3faf0e22fa74c6a7(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_630_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_630_0, recoveredArgument_630_1) -> MessageComposerKt.TextComposerWithFileItemsPreview$lambda$82(recoveredCapture_630_0, (androidx.compose.runtime.Composer)recoveredArgument_630_0, ((java.lang.Integer)recoveredArgument_630_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void VoiceNotesComposerPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1575191241);
      ComposerKt.sourceInformation(var2, "C(VoiceNotesComposerPreview)643@26452L1405:MessageComposer.kt#yb6g9");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1575191241, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.VoiceNotesComposerPreview (MessageComposer.kt:642)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$MessageComposerKt.INSTANCE.recovered$a427d7fccaa25a17(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_634_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_634_0, recoveredArgument_634_1) -> MessageComposerKt.VoiceNotesComposerPreview$lambda$83(recoveredCapture_634_0, (androidx.compose.runtime.Composer)recoveredArgument_634_0, ((java.lang.Integer)recoveredArgument_634_1).intValue())));
      }
   }

   private static final Unit MessageComposer$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$3$lambda$2() {
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$5$lambda$4(ComposerInputType var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$7$lambda$6(MetricData var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$9$lambda$8() {
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$11$lambda$10() {
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$13$lambda$12() {
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$15$lambda$14() {
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$17$lambda$16() {
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$19$lambda$18(MediaUploadItem var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$21$lambda$20(MediaUploadItem var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$23$lambda$22() {
      return Unit.INSTANCE;
   }

   private static final MutableState MessageComposer$lambda$25$lambda$24(String var0) {
      return SnapshotStateKt.mutableStateOf$default(new TextFieldValue(var0, TextRangeKt.TextRange(var0.length()), null, 4, null), null, 2, null);
   }

   private static final TextFieldValue MessageComposer$lambda$26(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (TextFieldValue)var1.getValue();
   }

   private static final void MessageComposer$lambda$27(MutableState var0, TextFieldValue var1) {
      MutableState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setValue(var1);
   }

   private static final long MessageComposer$lambda$30(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return ((Color)var1.getValue()).recovered$45f9de7b85e0e0b5();
   }

   private static final void MessageComposer$lambda$31(MutableState var0, long var1) {
      MutableState var3 = var0;
      Object var4 = null;
      Object var5 = null;
      Object var6 = Color.recovered$f46ef195ad4a2363(var1);
      int var7 = 0;
      var3.setValue(var6);
   }

   private static final KeyboardState MessageComposer$lambda$32(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (KeyboardState)var1.getValue();
   }

   private static final Unit MessageComposer$lambda$37$lambda$36(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$39$lambda$38() {
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$43$lambda$42(io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput var0, Function1 var1, List var2) {
      Intrinsics.checkNotNullParameter(var2, "remainingUris");
      List var3 = CollectionsKt.toMutableList(var2);
      Iterable var5 = var0.getMediaUploadItems();
      int var6 = 0;
      Iterable var7 = var5;
      ArrayList var8 = new ArrayList();
      int var9 = 0;

      for (Object var11 : var7) {
         MediaUploadItem var12 = (MediaUploadItem)var11;
         int var13 = 0;
         if (!var3.remove(var12.getMediaData().getUri())) {
            var8.add(var11);
         }
      }

      List var4 = (List)var8;
      Iterable var14 = var4;
      int var15 = 0;

      for (Object var17 : var14) {
         MediaUploadItem var18 = (MediaUploadItem)var17;
         int var19 = 0;
         var1.invoke(var18);
      }

      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$69$lambda$45$lambda$44(Function0 var0, Function1 var1, MutableState var2) {
      var0.invoke();
      MessageComposer$lambda$27(var2, new TextFieldValue("", 0L, null, 6, null));
      var1.invoke(io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposerInputType$Text.INSTANCE);
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$69$lambda$47$lambda$46(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$69$lambda$50$lambda$49(ManagedActivityResultLauncher var0, io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput var1, int var2) {
      Iterable var3 = var1.getMediaUploadItems();
      ManagedActivityResultLauncher var18 = var0;
      int var4 = 0;
      Iterable var5 = var3;
      ArrayList var6 = new ArrayList();
      int var7 = 0;
      Iterable var8 = var5;
      int var9 = 0;

      for (Object var11 : var8) {
         Object var12 = var11;
         int var13 = 0;
         MediaUploadItem var14 = (MediaUploadItem)var12;
         int var15 = 0;
         io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$LocalFile var10000 = IntercomPreviewFile.Companion.local(var14.getMediaData().getUri());
         if (var10000 != null) {
            Object var16 = var10000;
            int var17 = 0;
            var6.add(var16);
         }
      }

      List var10001 = (List)var6;
      DeleteType var10002 = io.intercom.android.sdk.m5.preview.data.DeleteType$Delete.INSTANCE;
      Object var19 = null;
      byte var20 = 44;
      Object var21 = null;
      int var22 = var2;
      boolean var23 = false;
      Object var24 = null;
      DeleteType var25 = var10002;
      List var26 = var10001;
      var18.launch(new IntercomPreviewArgs(var26, var25, (String)var24, var23, var22, (DownloadState)var21, var20, (DefaultConstructorMarker)var19));
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$69$lambda$59$lambda$56$lambda$52$lambda$51(long var0, long var2, MutableState var4, FocusState var5) {
      Intrinsics.checkNotNullParameter(var5, "focused");
      MessageComposer$lambda$31(var4, var5.isFocused() ? var0 : var2);
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$69$lambda$59$lambda$56$lambda$55$lambda$54(Function0 var0, MutableState var1, TextFieldValue var2) {
      Intrinsics.checkNotNullParameter(var2, "it");
      var0.invoke();
      MessageComposer$lambda$27(var1, var2);
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$69$lambda$59$lambda$58$lambda$57(Function2 var0, io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput var1, Function0 var2, MutableState var3) {
      var0.invoke(MessageComposer$lambda$26(var3).getText(), var1.getMediaUploadItems());
      MessageComposer$lambda$27(var3, new TextFieldValue("", 0L, null, 6, null));
      var2.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$69$lambda$68$lambda$61$lambda$60(Function1 var0, Function0 var1) {
      var0.invoke(new io.intercom.android.sdk.m5.conversation.metrics.MetricData$ComposerInputClicked("gif_input"));
      var1.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$69$lambda$68$lambda$63$lambda$62(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$69$lambda$68$lambda$65$lambda$64(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$69$lambda$68$lambda$67$lambda$66(Function2 var0, io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput var1, Function0 var2, MutableState var3) {
      var0.invoke(MessageComposer$lambda$26(var3).getText(), var1.getMediaUploadItems());
      MessageComposer$lambda$27(var3, new TextFieldValue("", 0L, null, 6, null));
      var2.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposer$lambda$70(
      Modifier var0,
      Function2 var1,
      io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput var2,
      Function0 var3,
      Function0 var4,
      Function1 var5,
      Function1 var6,
      Function0 var7,
      SharedFlow var8,
      VoiceTranscriptionState var9,
      StateFlow var10,
      Function0 var11,
      Function0 var12,
      Function0 var13,
      Function0 var14,
      Function1 var15,
      Function1 var16,
      Function0 var17,
      int var18,
      int var19,
      int var20,
      Composer var21,
      int var22
   ) {
      MessageComposer(
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
         var13,
         var14,
         var15,
         var16,
         var17,
         var21,
         RecomposeScopeImplKt.updateChangedFlags(var18 | 1),
         RecomposeScopeImplKt.updateChangedFlags(var19),
         var20
      );
      return Unit.INSTANCE;
   }

   private static final Unit ComposerActionButton$lambda$72(Function0 var0, Modifier var1, boolean var2, int var3, int var4, int var5, Composer var6, int var7) {
      ComposerActionButton(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit VoiceNotesComposer$lambda$77(
      VoiceTranscriptionState var0, StateFlow var1, Function0 var2, Function0 var3, int var4, Composer var5, int var6
   ) {
      VoiceNotesComposer(var0, var1, var2, var3, var5, RecomposeScopeImplKt.updateChangedFlags(var4 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TextComposerPreview$lambda$78(int var0, Composer var1, int var2) {
      TextComposerPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TextComposerWithButtonsPreview$lambda$79(int var0, Composer var1, int var2) {
      TextComposerWithButtonsPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TextComposerWithInitialTextPreview$lambda$80(int var0, Composer var1, int var2) {
      TextComposerWithInitialTextPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TextComposerWithMediaItemsPreview$lambda$81(int var0, Composer var1, int var2) {
      TextComposerWithMediaItemsPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TextComposerWithFileItemsPreview$lambda$82(int var0, Composer var1, int var2) {
      TextComposerWithFileItemsPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit VoiceNotesComposerPreview$lambda$83(int var0, Composer var1, int var2) {
      VoiceNotesComposerPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final float access$getComposerIconSize$p() {
      return ComposerIconSize;
   }

   // $VF: synthetic method
   public static final KeyboardState access$MessageComposer$lambda$32(State var0) {
      return MessageComposer$lambda$32(var0);
   }

   // $VF: synthetic method
   public static final void access$MessageComposer$lambda$27(MutableState var0, TextFieldValue var1) {
      MessageComposer$lambda$27(var0, var1);
   }

   static {
      int var0 = 16;
      int var1 = 0;
      ComposerIconSize = Dp.recovered$95b3d103663bf060(var0);
      int $this$dp$ivx = 48;
      int $i$f$getDpx = 0;
      ComposerMinSize = Dp.recovered$95b3d103663bf060($this$dp$ivx);
      float var4 = ComposerMinSize;
      int var6 = 2;
      int var2 = 0;
      ComposerHalfSize = Dp.recovered$95b3d103663bf060(var4 / var6);
   }
}
