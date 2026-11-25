import { pathToFileURL } from "node:url";

export const requiredIosJobs = ["ios-native", "device-utils-store"];
const knownResults = new Set(["success", "failure", "cancelled", "skipped"]);

export function checkIosNativeResults(needs, jobs = { "ios-native": true, "device-utils-store": true }) {
  if (
    needs === null || typeof needs !== "object" || Array.isArray(needs) ||
    Object.keys(needs).length !== requiredIosJobs.length ||
    requiredIosJobs.some((job) => !Object.hasOwn(needs, job))
  ) throw new Error("invalid_ios_dependencies");

  if (requiredIosJobs.some((job) => typeof jobs?.[job] !== "boolean")) throw new Error("invalid_ios_plan");

  const checks = [];
  for (const job of requiredIosJobs) {
    const result = needs[job]?.result;
    if (!knownResults.has(result)) throw new Error("invalid_ios_result");
    if (result !== (jobs[job] ? "success" : "skipped")) throw new Error(`${job}:${result}`);
    checks.push(`${job}:${result}`);
  }
  return checks.join(",");
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  try {
    if (process.env.NATIVE_PLAN_RESULT !== "success") throw new Error("native_plan_failed");
    let plan;
    try {
      plan = JSON.parse(process.env.NATIVE_CI_PLAN);
    } catch {
      throw new Error("invalid_ios_plan");
    }
    if (!plan?.jobs) throw new Error("invalid_ios_plan");
    const needs = {};
    if (process.env.IOS_NATIVE_RESULT !== undefined) {
      needs["ios-native"] = { result: process.env.IOS_NATIVE_RESULT };
    }
    if (process.env.DEVICE_UTILS_STORE_RESULT !== undefined) {
      needs["device-utils-store"] = { result: process.env.DEVICE_UTILS_STORE_RESULT };
    }
    console.log(checkIosNativeResults(needs, plan.jobs));
  } catch (error) {
    console.error(error.message);
    process.exitCode = 1;
  }
}
