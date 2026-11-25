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

test("NativeList includes the Pager XCTest through its test-specific pod dependency", () => {
  assert.ok(graph.get("react-native-native-list").has("react-native-pager-view"));
  const plan = planFor("native-views/react-native-native-list/ios/tests/NativeListMediaVisibilityTests.swift");
  assert.deepEqual(plan.ios.ui.testSchemes, [
    "react-native-pager-view-Unit-Tests", "react-native-native-list-Unit-Tests",
  ]);
  assert.deepEqual(plan.ios.swiftPackages, ["native-views/react-native-native-list/tests/swiftpm"]);
  assert.deepEqual(scheduledJobs(plan), ["ios-native", "device-utils-store"]);
});

test("Image selects dependent modules on the changed platform", () => {
  const ios = planFor("native-views/react-native-image/ios/example.swift");
  assert.deepEqual(ios.affectedModules, [
    "react-native-bottom-tabs", "react-native-image", "react-native-native-list", "react-native-pager-view",
  ]);
  assert.equal(ios.ios.ui.testSchemes.length, 4);
  assert.equal(ios.jobs["android-unit"], false);

  const android = planFor("native-views/react-native-image/android/example.kt");
  assert.deepEqual(scheduledJobs(android), ["android-unit"]);
  assert.deepEqual(android.android.gradleTasks, [
    ":onekeyfe_react-native-image:testDebugUnitTest",
    ":onekeyfe_react-native-native-list:testDebugUnitTest",
    ":onekeyfe_react-native-bottom-tabs:compileReleaseKotlin",
  ]);
  assert.deepEqual(android.android.host, ["react-native-pager-view"]);
});

test("Overlay native changes exercise the package and its platform build", () => {
  const plan = planFor("native-views/react-native-native-overlay/ios/NativeOverlay.mm");
  assert.deepEqual(scheduledJobs(plan), ["native-overlay-package", "device-utils-store"]);
  assert.deepEqual(plan.ios.services, { buildTargets: ["NativeOverlay"], testSchemes: [] });
});

test("Docs skip native jobs, while shared, unknown, and unregistered inputs run full coverage", () => {
  assert.deepEqual(scheduledJobs(planFor(
    "README.md", "docs/README.md", "native-modules/react-native-jpush/docs/SPEC.md",
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
  assert.deepEqual(full.ios.services.buildTargets, ["ReactNativeLiteCard", "NativeOverlay"]);
  assert.equal(full.ios.host.length, 3);
  assert.equal(full.ios.swiftPackages.length, 4);
  assert.equal(full.android.gradleTasks.length, 18);
  assert.equal(full.android.reportDirectories.length, 13);
  assert.equal(full.android.host.length, 6);
});
