import assert from "node:assert/strict";
import { execFileSync } from "node:child_process";
import test from "node:test";
import { planNativeCI, readNativeDependents } from "./plan-native-ci.mjs";

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
  assert.deepEqual(pager.affectedModules, ["react-native-bottom-tabs", "react-native-pager-view"]);
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

test("full CI retains every existing XCTest, Gradle and host suite", () => {
  const full = JSON.parse(execFileSync(process.execPath, ["scripts/plan-native-ci.mjs", "--full"], { encoding: "utf8" }));
  assert.equal(full.ios.ui.testSchemes.length + full.ios.services.testSchemes.length, 13);
  assert.deepEqual(full.ios.services.buildTargets, ["ReactNativeLiteCard", "NativeOverlay", "AutoSizeInput"]);
  assert.equal(full.ios.host.length, 4);
  assert.equal(full.ios.swiftPackages.length, 4);
  assert.equal(full.android.gradleTasks.length, 20);
  assert.equal(full.android.reportDirectories.length, 13);
  assert.equal(full.android.host.length, 7);
});
