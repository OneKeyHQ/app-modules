import assert from "node:assert/strict";
import { spawnSync } from "node:child_process";
import { fileURLToPath } from "node:url";
import test from "node:test";
import { getIosNativePlan } from "./ios-native-plan.mjs";
import { checkIosNativeResults, requiredIosJobs } from "./check-ios-native-results.mjs";

const root = fileURLToPath(new URL("..", import.meta.url));
const parsed = spawnSync("ruby", ["-ryaml", "-rjson", "-e",
  "puts JSON.generate(YAML.safe_load(File.read(ARGV.fetch(0)), aliases: true))",
  ".github/workflows/native-tests.yml"], { cwd: root, encoding: "utf8" });
assert.equal(parsed.status, 0, parsed.stderr);
const workflow = JSON.parse(parsed.stdout);
const gate = workflow.jobs["ios-xctest"];
const gateStep = gate.steps.find((step) => step.id === "gate");
const groups = { "ios-native": "ui", "device-utils-store": "services" };

const originalSchemes = [
  "react-native-pager-view-Unit-Tests", "OneKeyTextInput-Unit-Tests", "OneKeyImage-Unit-Tests",
  "react-native-native-list-Unit-Tests", "PerpDepthBar-Unit-Tests", "ReactNativeBundleCrypto-Unit-Tests",
  "ReactNativeImageCropPicker-Unit-Tests", "ReactNativeCaptureProtection-Unit-Tests",
  "ReactNativePhotoLibrary-Unit-Tests", "ReactNativeJpush-Unit-Tests",
];

test("two independent macOS jobs preserve every original iOS suite and both pod builds", () => {
  assert.deepEqual(Object.entries(workflow.jobs).filter(([, job]) => job["runs-on"] === "macos-26").map(([name]) => name).sort(), ["device-utils-store", "ios-native"]);
  const plans = Object.entries(groups).map(([jobId, group]) => {
    const job = workflow.jobs[jobId];
    assert.equal(job.strategy, undefined);
    assert.equal(job.needs, undefined);
    assert.equal(job["timeout-minutes"], 50);
    assert.equal(job.env.IOS_NATIVE_GROUP, group);
    const build = job.steps.find((step) => step.name === "Build assigned pods and run assigned XCTest suites");
    assert.equal(build["timeout-minutes"], 30);
    assert.match(build.run, /No executed tests for \{name\}/);
    assert.match(build.run, /_GPChannelSDKCoreVersionString/);
    assert.match(build.run, /identity_phase.*show_env_vars_in_log/);
    return getIosNativePlan(group);
  });
  const schemes = plans.flatMap((plan) => plan.testSchemes);
  assert.deepEqual([...schemes].sort(), [...originalSchemes].sort());
  assert.equal(new Set(schemes).size, schemes.length);
  assert.deepEqual(plans.flatMap((plan) => plan.buildTargets), ["ReactNativeLiteCard", "NativeOverlay"]);
  assert.deepEqual(getIosNativePlan("ui").testSchemes, [
    "react-native-pager-view-Unit-Tests", "react-native-native-list-Unit-Tests", "OneKeyImage-Unit-Tests",
  ]);
});

test("workflow-selected plan CLI produces each group's plan and rejects unexpected arguments", () => {
  for (const [jobId, group] of Object.entries(groups)) {
    const build = workflow.jobs[jobId].steps.find((step) => step.name === "Build assigned pods and run assigned XCTest suites");
    assert.match(build.run, /node scripts\/ios-native-plan.mjs "\$IOS_NATIVE_GROUP"/);
    const actual = spawnSync(process.execPath, ["scripts/ios-native-plan.mjs", group], { cwd: root, encoding: "utf8" });
    assert.equal(actual.status, 0, actual.stderr);
    assert.deepEqual(JSON.parse(actual.stdout), getIosNativePlan(group));
  }
  for (const args of [[], ["PRIVATE-CI-INPUT"], ["ui", "PRIVATE-CI-INPUT"], ["toString"], ["__proto__"]]) {
    const result = spawnSync(process.execPath, ["scripts/ios-native-plan.mjs", ...args], { cwd: root, encoding: "utf8" });
    assert.equal(result.status, 1);
    assert.ok(["invalid_ios_native_arguments", "invalid_ios_native_group"].includes(result.stderr.trim()));
    assert.equal(result.stdout, "");
  }
});

test("existing required gate waits for both iOS groups", () => {
  assert.deepEqual(gate.needs, requiredIosJobs);
  assert.deepEqual([...requiredIosJobs].sort(), Object.keys(groups).sort());
  assert.equal(gate.if, "${{ always() }}");
  assert.equal(gateStep.env.IOS_NATIVE_RESULT, "${{ needs.ios-native.result }}");
  assert.equal(gateStep.env.DEVICE_UTILS_STORE_RESULT, "${{ needs.device-utils-store.result }}");
});

const results = ["success", "failure", "cancelled", "skipped", "unknown", "", "PRIVATE-STATE\n/path/user@host"];
for (const ui of results) {
  test(`actual workflow gate requires both groups to succeed (${results.indexOf(ui)})`, () => {
    for (const services of results) {
      const actual = spawnSync("bash", ["-e", "-o", "pipefail", "-c", gateStep.run], {
        cwd: root, encoding: "utf8", env: { ...process.env, IOS_NATIVE_RESULT: ui, DEVICE_UTILS_STORE_RESULT: services },
      });
      assert.equal(actual.status, ui === "success" && services === "success" ? 0 : 1);
      const output = (actual.stdout + actual.stderr).trim();
      const allowed = [
        "ios-native:success,device-utils-store:success", "invalid_ios_result",
        ...requiredIosJobs.flatMap((job) => ["failure", "cancelled", "skipped"].map((result) => `${job}:${result}`)),
      ];
      assert.ok(allowed.includes(output), "Only fixed job/state/error metadata may be emitted");
      assert.ok(!output.includes("PRIVATE") && !output.includes("/path/user@host"));
    }
  });
}

test("gate rejects missing dependencies, malformed objects and unexpected result types", () => {
  const success = { "ios-native": { result: "success" }, "device-utils-store": { result: "success" } };
  for (const input of [null, [], {}, { other: { result: "success" } },
    { "ios-native": { result: "success" } }, { "device-utils-store": { result: "success" } },
    { ...success, extra: {} }]) {
    assert.throws(() => checkIosNativeResults(input), /^Error: invalid_ios_dependencies$/);
  }
  for (const job of requiredIosJobs) {
    for (const result of [undefined, null, {}, 0, true]) {
      assert.throws(() => checkIosNativeResults({ ...success, [job]: { result } }), /^Error: invalid_ios_result$/);
    }
  }
  for (const missing of ["IOS_NATIVE_RESULT", "DEVICE_UTILS_STORE_RESULT"]) {
    const env = { ...process.env, IOS_NATIVE_RESULT: "success", DEVICE_UTILS_STORE_RESULT: "success" };
    delete env[missing];
    const actual = spawnSync("bash", ["-e", "-o", "pipefail", "-c", gateStep.run], { cwd: root, encoding: "utf8", env });
    assert.equal(actual.status, 1);
    assert.equal(actual.stderr.trim(), "invalid_ios_dependencies");
  }
});
