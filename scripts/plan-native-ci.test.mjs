import assert from "node:assert/strict";
import { execFileSync } from "node:child_process";
import { copyFileSync, mkdirSync, mkdtempSync, rmSync, writeFileSync } from "node:fs";
import { tmpdir } from "node:os";
import { dirname, join } from "node:path";
import test from "node:test";
import { fileURLToPath, pathToFileURL } from "node:url";
import { planNativeCI, readNativeDependents } from "./plan-native-ci.mjs";

const root = fileURLToPath(new URL("..", import.meta.url));
const graph = readNativeDependents();
const planFor = (...paths) => planNativeCI(paths, graph);
const scheduledJobs = (plan) => Object.keys(plan.jobs).filter((job) => plan.jobs[job]);

test("JPush changes select its XCTest, Gradle, and host runs without scheduling UI or packaging", () => {
  const plan = planFor(
    "native-modules/react-native-jpush/ios/jpush/JPUSHService.m",
    "native-modules/react-native-jpush/tests/android/test/SdkHostRegressionTest.java",
  );
  assert.equal(plan.mode, "affected");
  assert.deepEqual(plan.affectedModules, ["react-native-jpush"]);
  assert.deepEqual(scheduledJobs(plan), ["device-utils-store", "android-unit"]);
  assert.deepEqual(plan.ios.services, { buildTargets: [], testSchemes: ["ReactNativeJpush-Unit-Tests"] });
  assert.deepEqual(plan.ios.host, ["react-native-jpush"]);
  assert.deepEqual(plan.ios.swiftPackages, []);
  assert.deepEqual(plan.android, {
    gradleTasks: [":onekeyfe_react-native-jpush:testDebugUnitTest", ":onekey-jpush-fcm:testDebugUnitTest"],
    reportDirectories: [
      "native-modules/react-native-jpush/android/build",
      "native-modules/react-native-jpush/android/fcm/build",
    ],
    host: ["react-native-jpush"],
  });
});

test("either native platform or a module spec selects the complete registered JPush suite", () => {
  const ios = planFor("native-modules/react-native-jpush/ios/jpush/JPUSHService.m");
  const android = planFor("native-modules/react-native-jpush/tests/android/test/SdkHostRegressionTest.java");
  assert.deepEqual(ios, android);
  assert.deepEqual(ios, planFor("native-modules/react-native-jpush/docs/SPEC.md"));
  assert.deepEqual(scheduledJobs(ios), ["device-utils-store", "android-unit"]);
});

test("NativeList reaches Pager's test-specific dependency and BottomTabs' runtime caller", () => {
  assert.ok(graph.get("react-native-native-list").has("react-native-pager-view"));
  assert.ok(graph.get("react-native-native-list").has("react-native-bottom-tabs"));
  const plan = planFor("native-views/react-native-native-list/ios/tests/NativeListMediaVisibilityTests.swift");
  assert.deepEqual(plan.ios.ui.testSchemes, [
    "react-native-pager-view-Unit-Tests", "react-native-native-list-Unit-Tests",
    "react-native-bottom-tabs-Unit-Tests",
  ]);
  assert.deepEqual(plan.ios.swiftPackages, ["native-views/react-native-native-list/tests/swiftpm"]);
  assert.deepEqual(scheduledJobs(plan), ["ios-native", "device-utils-store", "android-unit"]);
});

test("Image schedules Overlay and transitive UI consumers of its runtime icon registry", () => {
  const ios = planFor("native-views/react-native-image/ios/OneKeyImageCoderBridge.m");
  const android = planFor("native-views/react-native-image/android/consumer-rules.pro");
  assert.deepEqual(ios, android);
  assert.deepEqual(ios.affectedModules, [
    "react-native-bottom-tabs", "react-native-image", "react-native-native-list",
    "react-native-native-overlay", "react-native-pager-view",
  ]);
  assert.equal(ios.ios.ui.testSchemes.length, 4);
  assert.deepEqual(ios.ios.services.buildTargets, ["NativeOverlay"]);
  assert.deepEqual(ios.android.gradleTasks, [
    ":onekeyfe_react-native-image:testDebugUnitTest",
    ":onekeyfe_react-native-native-list:testDebugUnitTest",
    ":onekeyfe_react-native-native-overlay:testDebugUnitTest",
    ":onekeyfe_react-native-bottom-tabs:compileReleaseKotlin",
    ":onekeyfe_react-native-pager-view:compileDebugKotlin",
  ]);
  assert.deepEqual(ios.android.host, ["react-native-pager-view"]);
  assert.equal(ios.jobs["native-overlay-package"], true);
});

test("Overlay and Pager expand runtime callers in the provider-to-consumer direction", () => {
  const overlay = planFor("native-views/react-native-native-overlay/ios/NativeOverlayWindowManager.swift");
  assert.equal(overlay.jobs["native-overlay-package"], true);
  assert.deepEqual(overlay.affectedModules, [
    "react-native-bottom-tabs", "react-native-native-list", "react-native-native-overlay", "react-native-pager-view",
  ]);
  const pager = planFor("native-views/react-native-pager-view/ios/RNCCollapsiblePagerViewComponentView.mm");
  assert.deepEqual(pager.affectedModules, [
    "react-native-bottom-tabs", "react-native-native-list", "react-native-pager-view",
  ]);
  assert.ok(pager.ios.ui.testSchemes.includes("react-native-native-list-Unit-Tests"));
  assert.ok(pager.ios.swiftPackages.includes("native-views/react-native-native-list/tests/swiftpm"));
  assert.ok(pager.android.gradleTasks.includes(":onekeyfe_react-native-native-list:testDebugUnitTest"));
  assert.ok(pager.android.host.includes("react-native-pager-view"));
  assert.ok(pager.android.gradleTasks.includes(":onekeyfe_react-native-pager-view:compileDebugKotlin"));
});

test("SNI diagnostics run in both SNI host lanes regardless of the modified platform", () => {
  const swift = planFor("native-modules/react-native-sni-connect/ios/SniConnectLog.swift");
  const kotlin = planFor("native-modules/react-native-sni-connect/android/src/main/java/com/sniconnect/SniConnectLogger.kt");
  assert.deepEqual(swift, kotlin);
  assert.deepEqual(swift.ios.host, ["react-native-sni-connect"]);
  assert.deepEqual(swift.android.host, ["react-native-sni-connect"]);
  assert.deepEqual(swift.affectedModules, ["react-native-sni-connect"]);
});

test("shared Nitro interfaces have both native targets in the plan", () => {
  const autoSize = planFor("native-views/react-native-auto-size-input/src/AutoSizeInput.nitro.ts");
  assert.deepEqual(autoSize.ios.services.buildTargets, ["AutoSizeInput"]);
  assert.deepEqual(autoSize.android.gradleTasks, [":onekeyfe_react-native-auto-size-input:testDebugUnitTest"]);
  const depthBar = planFor("native-views/react-native-perp-depth-bar/src/PerpDepthBars.nitro.ts");
  assert.deepEqual(depthBar.ios.services.testSchemes, ["PerpDepthBar-Unit-Tests"]);
  assert.deepEqual(depthBar.android.gradleTasks, [":onekeyfe_react-native-perp-depth-bar:compileDebugKotlin"]);
});

test("OTA consumers without direct suite mappings force the full fallback", () => {
  const consumers = graph.get("react-native-bundle-update");
  assert.ok(consumers.has("react-native-split-bundle-loader"));
  assert.ok(consumers.has("react-native-background-thread"));
  assert.equal(planFor("native-modules/react-native-bundle-update/ios/ReactNativeBundleUpdate.swift").reason,
    "unregistered-dependent");
});

test("Docs skip native jobs, while shared, unknown, and unregistered inputs run full coverage", () => {
  assert.deepEqual(scheduledJobs(planFor(
    "README.md", "docs/README.md",
  )), []);
  const fullJobs = ["native-overlay-package", "ios-native", "device-utils-store", "android-unit"];
  for (const path of [
    "example/react-native/ios/Podfile.lock",
    ".github/workflows/native-tests.yml",
    "native-views/react-native-image/react-native.config.js",
    "native-modules/react-native-jpush/android/build.gradle",
    "native-modules/native-logger/ios/NativeLogger.swift",
    "native-modules/unregistered/ios/example.swift",
  ]) {
    const plan = planFor(path);
    assert.equal(plan.mode, "full", path);
    assert.deepEqual(scheduledJobs(plan), fullJobs, path);
  }
  const incomplete = new Map(graph);
  incomplete.set("react-native-jpush", new Set(["missing-test-coverage"]));
  assert.equal(planNativeCI(["native-modules/react-native-jpush/ios/example.m"], incomplete).mode, "full");
});

test("iOS changes to Android-only registered modules fall back to the full suite", () => {
  const plan = planFor("native-modules/react-native-app-update/ios/ReactNativeAppUpdate.swift");
  assert.equal(plan.mode, "full");
  assert.equal(plan.reason, "unregistered-platform");
  assert.deepEqual(plan.changedModules, ["react-native-app-update"]);
  assert.deepEqual(scheduledJobs(plan), ["native-overlay-package", "ios-native", "device-utils-store", "android-unit"]);
});

test("Network Throttle changes build the iOS pod and run the Android tests", () => {
  const plan = planFor("native-modules/react-native-network-throttle/ios/OneKeyNetworkThrottle.m");
  assert.equal(plan.mode, "affected");
  assert.deepEqual(plan.affectedModules, ["react-native-network-throttle"]);
  assert.deepEqual(scheduledJobs(plan), ["device-utils-store", "android-unit"]);
  assert.deepEqual(plan.ios.services, { buildTargets: ["ReactNativeNetworkThrottle"], testSchemes: [] });
  assert.deepEqual(plan.android.gradleTasks, [":onekeyfe_react-native-network-throttle:testDebugUnitTest"]);
  assert.deepEqual(planFor("native-modules/react-native-network-throttle/android/src/main/AndroidManifest.xml"), plan);
});

test("shared changes trigger full coverage and report every changed native module regardless of path order", () => {
  const paths = [
    ".github/workflows/native-tests.yml",
    "native-modules/react-native-app-update/ios/ReactNativeAppUpdate.swift",
    "native-modules/react-native-jpush/package.json",
    "native-modules/react-native-sni-connect/ios/SniConnectLog.swift",
    "native-views/react-native-pager-view/docs/SPEC.md",
  ];
  const plan = planFor(...paths);
  assert.equal(plan.mode, "full");
  assert.equal(plan.reason, "shared-or-unclassified-path");
  assert.deepEqual(plan.changedModules, [
    "react-native-app-update", "react-native-jpush", "react-native-pager-view", "react-native-sni-connect",
  ]);
  assert.deepEqual(planFor(...paths.toReversed()), plan);
});

test("full CI retains the registered native suites and Android test reports", () => {
  const full = JSON.parse(execFileSync(process.execPath, ["scripts/plan-native-ci.mjs", "--full"], {
    cwd: root, encoding: "utf8",
  }));
  assert.equal(full.mode, "full");
  assert.deepEqual(scheduledJobs(full), ["native-overlay-package", "ios-native", "device-utils-store", "android-unit"]);
  assert.equal(full.ios.ui.testSchemes.length + full.ios.services.testSchemes.length, 13);
  assert.deepEqual(full.ios.services.buildTargets, [
    "ReactNativeLiteCard", "NativeOverlay", "AutoSizeInput", "ReactNativeNetworkThrottle",
  ]);
  assert.deepEqual(full.ios.host, [
    "react-native-device-utils", "react-native-lite-card", "react-native-jpush", "react-native-sni-connect",
  ]);
  assert.deepEqual(full.ios.swiftPackages, [
    "native-modules/react-native-sni-connect",
    "native-modules/react-native-bundle-update",
    "native-modules/react-native-range-downloader/tests/swiftpm",
    "native-views/react-native-native-list/tests/swiftpm",
  ]);
  assert.deepEqual(full.android, {
    gradleTasks: [
      ":onekeyfe_react-native-app-update:testDebugUnitTest",
      ":onekeyfe_react-native-bundle-crypto:testDebugUnitTest",
      ":onekeyfe_react-native-jpush:testDebugUnitTest",
      ":onekey-jpush-fcm:testDebugUnitTest",
      ":onekeyfe_react-native-network-throttle:testDebugUnitTest",
      ":onekeyfe_react-native-range-downloader:testDebugUnitTest",
      ":onekeyfe_react-native-sni-connect:testDebugUnitTest",
      ":onekeyfe_react-native-auto-size-input:testDebugUnitTest",
      ":onekeyfe_react-native-image:testDebugUnitTest",
      ":onekeyfe_react-native-native-list:testDebugUnitTest",
      ":onekeyfe_react-native-native-overlay:testDebugUnitTest",
      ":onekeyfe_react-native-text-input:testDebugUnitTest",
      ":onekeyfe_react-native-qr-scanner:testDebugUnitTest",
      ":onekeyfe_react-native-bundle-update:compileDebugKotlin",
      ":onekeyfe_react-native-bottom-tabs:compileReleaseKotlin",
      ":onekeyfe_react-native-pager-view:compileDebugKotlin",
      ":onekeyfe_react-native-perp-depth-bar:compileDebugKotlin",
      ":onekeyfe_react-native-capture-protection:assembleDebug",
      ":onekeyfe_react-native-device-utils:assembleDebug",
      ":onekeyfe_react-native-photo-library:assembleDebug",
    ],
    reportDirectories: [
      "native-modules/react-native-app-update/android/build",
      "native-modules/react-native-bundle-crypto/android/build",
      "native-modules/react-native-jpush/android/build",
      "native-modules/react-native-jpush/android/fcm/build",
      "native-modules/react-native-network-throttle/android/build",
      "native-modules/react-native-range-downloader/android/build",
      "native-modules/react-native-sni-connect/android/build",
      "native-views/react-native-auto-size-input/android/build",
      "native-views/react-native-image/android/build",
      "native-views/react-native-native-list/android/build",
      "native-views/react-native-native-overlay/android/build",
      "native-views/react-native-text-input/android/build",
      "native-views/react-native-qr-scanner/android/build",
    ],
    host: [
      "react-native-device-utils", "react-native-bundle-update", "react-native-pager-view",
      "react-native-image-crop-picker", "react-native-lite-card", "react-native-jpush", "react-native-sni-connect",
    ],
  });
});

test("a depth-two PR merge checkout selects every PR commit and both sides of a cross-module rename", (t) => {
  const source = mkdtempSync(join(tmpdir(), "native-ci-source-"));
  const checkout = mkdtempSync(join(tmpdir(), "native-ci-checkout-"));
  t.after(() => {
    rmSync(source, { recursive: true, force: true });
    rmSync(checkout, { recursive: true, force: true });
  });
  const git = (cwd, ...args) => execFileSync("git", args, { cwd, encoding: "utf8" }).trim();
  const script = "scripts/plan-native-ci.mjs";
  const metadata = git(root, "ls-files", "--", ":(glob)native-*/*/package.json",
    ":(glob)native-*/*/*.podspec", ":(glob)native-*/*/android/build.gradle*").split("\n");
  for (const path of [script, "scripts/ios-native-plan.mjs", ...metadata]) {
    const destination = join(source, path);
    mkdirSync(dirname(destination), { recursive: true });
    copyFileSync(join(root, path), destination);
  }
  const jpush = "native-modules/react-native-jpush/ci-fixture.txt";
  const networkThrottle = "native-modules/react-native-network-throttle/ci-fixture.txt";
  const sni = "native-modules/react-native-sni-connect/ci-fixture.txt";
  const image = "native-views/react-native-image/ci-fixture.txt";
  const write = (path, content) => writeFileSync(join(source, path), content);
  const commit = (message) => {
    git(source, "add", "--all");
    git(source, "commit", "-qm", message);
  };
  git(source, "init", "-q", "--initial-branch=base", "--object-format=sha1");
  git(source, "config", "user.name", "Native CI fixture");
  git(source, "config", "user.email", "native-ci@example.invalid");
  git(source, "config", "commit.gpgsign", "false");
  write(jpush, "Renamed fixture content\n");
  commit("Common ancestor");
  git(source, "switch", "-qc", "pr");
  write(sni, "Earlier PR commit\n");
  commit("Modify SNI");
  git(source, "mv", jpush, networkThrottle);
  commit("Move fixture out of JPush");
  const head = git(source, "rev-parse", "HEAD");
  git(source, "switch", "-q", "base");
  write(image, "Base-only change\n");
  commit("Modify Image on base");
  git(source, "merge", "-q", "--no-ff", "--no-edit", "pr");
  const origin = pathToFileURL(source).href;
  git(checkout, "clone", "-q", "--depth=2", origin, ".");
  assert.equal(git(checkout, "rev-parse", "--is-shallow-repository"), "true");
  const plan = (...args) => JSON.parse(execFileSync(process.execPath, [script, ...args], {
    cwd: checkout, encoding: "utf8",
  }));
  const selected = plan("--pull-request", head);
  assert.equal(selected.mode, "affected", selected.reason);
  assert.deepEqual(selected.changedModules, [
    "react-native-jpush", "react-native-network-throttle", "react-native-sni-connect",
  ]);
  assert.deepEqual(scheduledJobs(selected), ["device-utils-store", "android-unit"]);
  assert.deepEqual(selected.ios.host, ["react-native-jpush", "react-native-sni-connect"]);
  assert.deepEqual(selected.android.host, ["react-native-jpush", "react-native-sni-connect"]);
  assert.deepEqual(selected.android.gradleTasks, [
    ":onekeyfe_react-native-jpush:testDebugUnitTest", ":onekey-jpush-fcm:testDebugUnitTest",
    ":onekeyfe_react-native-network-throttle:testDebugUnitTest", ":onekeyfe_react-native-sni-connect:testDebugUnitTest",
  ]);

  const full = plan("--full");
  const unverified = plan("--pull-request", git(checkout, "rev-parse", "HEAD^1"));
  assert.deepEqual(unverified, { ...full, reason: "unverified-merge-checkout" });
});
