import fs from 'node:fs';
import path from 'node:path';

describe('NativeSheet public API', () => {
  const source = fs.readFileSync(path.join(__dirname, '../index.tsx'), 'utf8');

  it('aligns controlled dismissal props with the existing JS Sheet API', () => {
    expect(source).toContain('onOpenChange?: (open: boolean) => void');
    expect(source).toContain('dismissOnOverlayPress?: boolean');
    expect(source).toContain('dismissOnSnapToBottom?: boolean');
    expect(source).toContain('disableDrag?: boolean');
    expect(source).toContain(
      'dismissOnOverlayPress ?? dismissOnBackdropPress ?? false'
    );
    expect(source).toContain(
      'dismissOnSnapToBottom ?? dismissOnPanDown ?? true'
    );
    expect(source).toContain('onOpenChange?.(false)');
    expect(source).toContain('export function NativeSheetHost()');
    expect(source).toContain('show: showNativeSheet');
    expect(source).toContain('measurement?.openCycle === openCycleRef.current');
    expect(source).toContain('key={`open-cycle-${openCycleRef.current}`}');
    expect(source).toContain('resolveNativeSheetHeight({');
    expect(source).toContain(
      'explicitHeightForOpenRef.current = height !== undefined'
    );
    expect(source).toContain('const shouldAutoMeasure');
    expect(source).toContain('style={styles.autoMeasureContent}');
    expect(source).toContain('onLayout={handleContentLayout}');
    expect(source).toContain('flexShrink: 0');
    expect(source).toMatch(
      /const handlePresented[\s\S]*onPresentationRequested\?\.\(\);[\s\S]*onPresented\?\.\(event\.nativeEvent\.height\)/
    );
    expect(source).toContain(
      'const effectiveBlocked = parentBlocked || blocked'
    );
    expect(source).toContain('value={effectiveBlocked}');
  });
});

describe('NativeSheet Android window behavior', () => {
  const source = fs.readFileSync(
    path.join(
      __dirname,
      '../../android/src/main/java/com/onekey/nativesheet/NativeSheetView.kt'
    ),
    'utf8'
  );
  it('configures dimming before presentation and owns keyboard avoidance natively', () => {
    const beforeShow = source.slice(0, source.indexOf('nextDialog.show()'));

    expect(beforeShow).toContain('setDimAmount(0f)');
    expect(beforeShow).toContain('setCanceledOnTouchOutside(false)');
    expect(beforeShow).toContain(
      'setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)'
    );
    expect(source).not.toContain('SOFT_INPUT_ADJUST_RESIZE');
    expect(source).toContain('ValueAnimator.ofFloat');
    expect(source).toContain('if (open && !securityBlocked)');
    expect(source).toContain('view.finishFailedPresentation()');
    expect(source).toContain('it.second == "programmatic"');
    expect(source).toContain(
      'if (reason == "security" && committedOpen && !dismissNotified)'
    );
    expect(source).toContain(
      'val reopenedAfterProgrammaticDismiss = pendingDismissReason == "programmatic" && committedOpen'
    );
    expect(source).toContain('animateDimAmount(currentDialog, 0f, animated)');
    expect(source).toContain('com.google.android.material.R.id.touch_outside');
    expect(source).toContain('if (dismissOnBackdropPress)');
    expect(source).toContain(
      'NativeSheetStack.dismiss(this@NativeSheetView, "back", true)'
    );
    expect(source).toContain(
      'NativeSheetStack.dismiss(this, "security", false)'
    );
    expect(source).toContain('navigationBarColor = Color.TRANSPARENT');
    expect(source).toContain(
      'WindowCompat.setDecorFitsSystemWindows(window, false)'
    );
    expect(source).toContain('setFitInsetsTypes(0)');
    expect(source).toContain('setFitInsetsSides(0)');
    expect(source).toContain(
      'WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN'
    );
    expect(source).toContain('WindowInsetsCompat.Type.navigationBars()');
    expect(source).toContain('getInsetsIgnoringVisibility');
    expect(source).toContain('updateNavigationBarBackground(sheetDialog)');
    expect(source).toContain('navigationBarBackgroundColor');
    expect(source).toContain('backgroundView.bringToFront()');
    expect(source).toContain('getInsets(WindowInsetsCompat.Type.ime())');
    expect(source).toContain(
      'val insetHost = sheetDialog.window?.decorView ?: container ?: bottomSheet'
    );
    expect(source).toContain('getWindowVisibleDisplayFrame(visibleFrame)');
    expect(source).toContain('visibleFrameImeInsetPx');
    expect(source).toContain(
      'maxOf(reportedImeInsetPx, visibleFrameImeInsetPx)'
    );
    expect(source).toContain('NativeSheetStack.isTop(this)');
    expect(source).toContain('removeOnGlobalLayoutListener');
    expect(source).toContain(
      'scheduleImeInsetFallback(insetHost, bottomSheet)'
    );
    expect(source).toContain('cancelImeInsetFallback()');
    expect(source).toContain('insetHost.postDelayed(fallback, 48)');
    expect(source).toContain('insets.isVisible(WindowInsetsCompat.Type.ime())');
    expect(source).toContain('if (hideImeIfVisible())');
    expect(source).toContain('applyImeOffset(bottomSheet)');
    expect(source).toContain('val isTop = NativeSheetStack.isTop(this)');
    expect(source).toContain('decorView == null || !isTop || imeInsetPx <= 0');
    expect(source).toContain('fun isTop(view: NativeSheetView): Boolean');
    expect(source).toContain(
      '(presenting?.get() ?: active.lastOrNull()?.get()) === view'
    );
    expect(source).toContain(
      'val restingTop = (originalBottomPx - layoutHeight).coerceAtLeast(0)'
    );
    expect(source).toContain('?: (bottomSheet.top + layoutHeight).also {');
    expect(source).toContain(
      '(imeInsetPx - navigationBarInsetPx).coerceAtLeast(0)'
    );
    expect(source).toContain(
      'val desiredTop = (restingTop - keyboardOcclusionPx).coerceAtLeast(0)'
    );
    expect(source).toContain('if (isTop && imeAnimationRunning)');
    expect(source).toContain('pendingImeTarget = child.findFocus()');
    expect(source).toContain('restorePendingImeFocus(sheetDialog)');
    expect(source).toContain('decorView.hasWindowFocus()');
    expect(source).toContain(
      'inputMethodManager?.showSoftInput(target, InputMethodManager.SHOW_IMPLICIT)'
    );
    expect(source).toContain(
      'behavior.expandedOffset = restoredTop.coerceAtLeast(0)'
    );
    expect(source).toContain('behavior.isFitToContents = false');
    expect(source).toContain('behavior.expandedOffset = desiredTop');
    expect(source).toContain(
      'ViewCompat.offsetTopAndBottom(bottomSheet, desiredTop - bottomSheet.top)'
    );
    expect(source).toContain('preImeBottomSheetBottomPx');
    expect(source).toContain('WindowInsetsAnimationCompat.Callback');
    expect(source).toContain(
      'bottomSheet.background = createSheetBackground(resolvedSheetBackgroundColor)'
    );
    expect(source).toContain('bottomSheet.clipChildren = true');
    expect(source).toContain('bottomSheet.clipToPadding = true');
    expect(source).toContain('bottomSheet.setPadding(0, 0, 0, 0)');
    expect(source).toContain(
      'ViewCompat.setOnApplyWindowInsetsListener(bottomSheet)'
    );
    expect(source).toContain(
      'ValueAnimator.ofInt(currentHeightPx, targetHeightPx)'
    );
    expect(source).toContain('interpolator = FastOutSlowInInterpolator()');
    expect(source).toContain('duration = 280');
    expect(source).toContain(
      'applyPresentedHeight(sheetDialog, bottomSheet, animator.animatedValue as Int)'
    );
    expect(source).toContain(
      'content.layoutParams = content.layoutParams.apply'
    );
    expect(source).toContain('bottomSheet.layoutParams');
    expect(source).toContain('if (extendsIntoNavigationBar)');
    expect(source).toContain('Color.alpha(resolvedSheetBackgroundColor) > 0');
    expect(source).toContain(
      'height = clampedHeightPx + navigationBarExtensionPx'
    );
    expect(source).toContain(
      'sheetDialog.behavior.peekHeight = clampedHeightPx + navigationBarExtensionPx'
    );
    expect(source).toContain(
      'BottomSheetBehavior.STATE_DRAGGING -> userDrivenSheetSlide = true'
    );
    expect(source).toContain('if (userDrivenSheetSlide && slideOffset < 1f)');
    expect(source).toContain('userDrivenSheetSlide = false');
  });

  it('dismisses stacked dialogs sequentially from the top', () => {
    expect(source).toContain('dismissNext(targets, view, reason, animated)');
    expect(source).toContain('dismissNext(targets.drop(1)');
    expect(source).toContain('val completion = dismissalCompletion');
    expect(source).not.toContain('targets.forEach');
  });
});
