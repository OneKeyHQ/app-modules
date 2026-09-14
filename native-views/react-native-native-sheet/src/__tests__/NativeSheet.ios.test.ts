import fs from 'node:fs';
import path from 'node:path';

describe('NativeSheet iOS dimming', () => {
  const source = fs.readFileSync(
    path.join(__dirname, '../../ios/NativeSheetContainerView.swift'),
    'utf8'
  );

  it('applies a clamped non-default dimAmount to the presentation container', () => {
    expect(source).toContain('self.dimAmountValue = min(max(dimAmount, 0), 1)');
    expect(source).toContain(
      'sheet.largestUndimmedDetentIdentifier = detentIdentifier'
    );
    expect(source).toContain('let targetAlpha = visible ? dimAmountValue : 0');
    expect(source).toContain('transitionCoordinator.animate');
    expect(source).toContain('UIViewControllerTransitioningDelegate');
    expect(source).toContain('NativeSheetTransitionAnimator(presenting: true)');
    expect(source).toContain(
      'NativeSheetTransitionAnimator(presenting: false)'
    );
    expect(source).toContain('mass: 0.1');
    expect(source).toContain('stiffness: 100');
    expect(source).toContain('damping: 20');
    expect(source).toContain('UIViewPropertyAnimator(duration: 0');
    expect(source).toContain('override func viewWillAppear');
    expect(source).toContain('override func viewWillDisappear');
    expect(source).toContain('dimmingView.isUserInteractionEnabled = true');
    expect(source).toContain('dimmingView.addGestureRecognizer(recognizer)');
    expect(source).toContain('host.finishFailedPresentation(reason: "system")');
    expect(source).toContain(
      'reason: host.securityBlocked ? "security" : (host.open ? "system" : "programmatic")'
    );
    expect(source).toContain('cancelDeferredProgrammaticDismissal(self)');
    expect(source).toContain('presentedController?.isBeingDismissed == true');
    expect(source).toContain(
      'let reopenedAfterProgrammaticDismiss = reason == "programmatic" && committedOpen'
    );
    expect(source).toContain(
      'let shouldNotify = hadController || (reason == "security" && committedOpen)'
    );
    expect(source).toMatch(
      /dismissOnBackdropPress: dismissOnBackdropPress,\s+dimAmount: dimAmount/
    );
  });

  it('animates height changes as a bottom-anchored custom detent update', () => {
    expect(source).toContain('private var targetHeight: CGFloat');
    expect(source).toContain(
      'func updateHeight(_ height: CGFloat, animated: Bool)'
    );
    expect(source).toContain('sheet.invalidateDetents()');
    expect(source).toContain(
      'let animator = NativeSheetQuickAnimation.makeAnimator()'
    );
    expect(source).toContain('animator.addAnimations(changes)');
    expect(source).toContain('heightAnimator.finishAnimation(at: .current)');
    expect(source).toContain(
      'controller.updateHeight(sheetHeight, animated: shouldAnimate)'
    );
    expect(source).toContain(
      'min(max(self?.targetHeight ?? 1, 1), context.maximumDetentValue)'
    );
    expect(source).toContain('rootView.clipsToBounds = true');
  });

  it('gates the new presentation behavior to iOS 26', () => {
    expect(source).toMatch(
      /if #available\(iOS 26\.0, \*\) \{\s+backgroundView\.frame = rootView\.bounds/
    );
    expect(source).toMatch(
      /private func stageContent\(\) \{\s+if #available\(iOS 26\.0, \*\) \{[\s\S]*?\} else \{\s+moveContent\(to: self\)/
    );
    expect(source).toContain(
      'private func compensateForPresentationScale(in shadowView: UIView)'
    );
    expect(source).toContain('scaleX: 1 / transform.a');
    expect(source).toContain('y: 1 / transform.d');
  });

  it('removes only the UIKit presentation wrapper shadow', () => {
    expect(source).toContain('private func removePresentationShadow()');
    expect(source).toContain(
      'while let current = ancestor, current !== container'
    );
    expect(source).toContain('removeDropShadowViews(in: container)');
    expect(source).toContain('className.contains("DropShadowView")');
    expect(source).toContain('guard candidate !== view else { return }');
    expect(source).toContain(
      'guard className.hasPrefix("UI") || className.hasPrefix("_UI") else { return }'
    );
    expect(source).toContain('shadowView.layer.shadowOpacity = 0');
    expect(source).toContain(
      'shadowView.layer.shadowColor = UIColor.clear.cgColor'
    );
    expect(source).toContain('CATransaction.setDisableActions(true)');
    expect(source).toContain(
      'private var shadowSuppressionDisplayLink: CADisplayLink?'
    );
    expect(source).toContain('startSuppressingPresentationShadow()');
    expect(source).toContain('displayLink.add(to: .main, forMode: .common)');
    expect(source).toContain('stopSuppressingPresentationShadow()');
    expect(source).toContain('override func viewDidAppear');
    expect(source).toContain('override func viewDidLayoutSubviews');
  });
});

describe('NativeSheet iOS podspec', () => {
  const podspec = fs.readFileSync(
    path.join(__dirname, '../../NativeSheet.podspec'),
    'utf8'
  );

  it('includes the Fabric component header', () => {
    expect(podspec).toContain('"ios/**/*.h"');
  });
});
