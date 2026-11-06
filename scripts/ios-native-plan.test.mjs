import assert from "node:assert/strict";
import { spawnSync } from "node:child_process";
import { fileURLToPath } from "node:url";
import test from "node:test";
import { getIosNativePlan, iosNativeGroups } from "./ios-native-plan.mjs";
import { checkIosNativeResults, requiredIosJobs } from "./check-ios-native-results.mjs";

const root = fileURLToPath(new URL("..", import.meta.url));
const parsed = spawnSync("ruby", ["-ryaml", "-rjson", "-e",
  "puts JSON.generate(YAML.safe_load(File.read(ARGV.fetch(0)), aliases: true))",
  ".github/workflows/native-tests.yml"], { cwd: root, encoding: "utf8" });
assert.equal(parsed.status, 0, parsed.stderr);
const workflow = JSON.parse(parsed.stdout);
const native = workflow.jobs["ios-native"];
const gate = workflow.jobs["ios-xctest"];
const gateStep = gate.steps.find((step) => step.id === "gate");

const originalSchemes = [
  "react-native-pager-view-Unit-Tests", "OneKeyTextInput-Unit-Tests", "OneKeyImage-Unit-Tests",
  "react-native-native-list-Unit-Tests", "PerpDepthBar-Unit-Tests", "ReactNativeBundleCrypto-Unit-Tests",
  "ReactNativeImageCropPicker-Unit-Tests", "ReactNativeCaptureProtection-Unit-Tests",
  "ReactNativePhotoLibrary-Unit-Tests", "ReactNativeJpush-Unit-Tests",
];

test("actual workflow matrix matches production plans and preserves every original suite once", () => {
  assert.deepEqual(native.strategy.matrix.group, iosNativeGroups);
  assert.equal(native.strategy["fail-fast"], false);
  assert.equal(native["timeout-minutes"], 50);
  const schemes = iosNativeGroups.flatMap((group) => getIosNativePlan(group).testSchemes);
  assert.deepEqual([...schemes].sort(), [...originalSchemes].sort());
  assert.equal(new Set(schemes).size, schemes.length);
  assert.deepEqual(getIosNativePlan("lite"), { action: "build", buildTargets: ["ReactNativeLiteCard"], testSchemes: [] });
  assert.deepEqual(getIosNativePlan("ui").buildTargets, ["NativeOverlay"]);
  assert.deepEqual(getIosNativePlan("modules").buildTargets, []);
  const build = native.steps.find((step) => step.name === "Build Lite Card pod or run assigned XCTest suites");
  assert.equal(build["timeout-minutes"], 20);
});

test("production plan CLI rejects unknown groups without echoing untrusted input", () => {
  const result = spawnSync(process.execPath, ["scripts/ios-native-plan.mjs", "PRIVATE-CI-INPUT"], { cwd: root, encoding: "utf8" });
  assert.equal(result.status, 1);
  assert.equal(result.stderr.trim(), "invalid_ios_native_group");
  for (const group of iosNativeGroups) {
    const actual = spawnSync(process.execPath, ["scripts/ios-native-plan.mjs", group], { cwd: root, encoding: "utf8" });
    assert.equal(actual.status, 0, actual.stderr);
    assert.deepEqual(JSON.parse(actual.stdout), getIosNativePlan(group));
  }
});

test("existing required gate runs after every matrix outcome and only depends on required jobs", () => {
  assert.deepEqual(gate.needs, requiredIosJobs);
  assert.equal(gate.if, "${{ always() }}");
  assert.equal(gateStep.env.IOS_NATIVE_RESULT, "${{ needs.ios-native.result }}");
});

for (const result of ["success", "failure", "cancelled", "skipped", "unknown", "", "PRIVATE-STATE\n/path/user@host"]) {
  test(`actual workflow gate command accepts only success (${result.startsWith("PRIVATE") ? "invalid" : result || "empty"})`, () => {
    const actual = spawnSync("bash", ["-e", "-o", "pipefail", "-c", gateStep.run], {
      cwd: root, encoding: "utf8", env: { ...process.env, IOS_NATIVE_RESULT: result },
    });
    assert.equal(actual.status, result === "success" ? 0 : 1);
    const output = (actual.stdout + actual.stderr).trim();
    assert.ok([
      "ios-native:success", "ios-native:failure", "ios-native:cancelled", "ios-native:skipped", "invalid_ios_result",
    ].includes(output), "Only fixed job/state/error metadata may be emitted");
    assert.ok(!output.includes("PRIVATE") && !output.includes("/path/user@host"));
  });
}

test("gate rejects missing dependencies, malformed objects and unexpected result types", () => {
  for (const input of [null, [], {}, { other: { result: "success" } }, { "ios-native": { result: "success" }, extra: {} }]) {
    assert.throws(() => checkIosNativeResults(input), /^Error: invalid_ios_dependencies$/);
  }
  for (const result of [undefined, null, {}, 0, true]) {
    assert.throws(() => checkIosNativeResults({ "ios-native": { result } }), /^Error: invalid_ios_result$/);
  }
  const env = { ...process.env };
  delete env.IOS_NATIVE_RESULT;
  const actual = spawnSync("bash", ["-e", "-o", "pipefail", "-c", gateStep.run], { cwd: root, encoding: "utf8", env });
  assert.equal(actual.status, 1);
  assert.equal(actual.stderr.trim(), "invalid_ios_dependencies");
});
