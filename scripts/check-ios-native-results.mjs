import { pathToFileURL } from "node:url";

export const requiredIosJobs = ["ios-native", "device-utils-store"];
const knownResults = new Set(["success", "failure", "cancelled", "skipped"]);

export function checkIosNativeResults(needs) {
  if (
    needs === null || typeof needs !== "object" || Array.isArray(needs) ||
    Object.keys(needs).length !== requiredIosJobs.length ||
    requiredIosJobs.some((job) => !Object.hasOwn(needs, job))
  ) throw new Error("invalid_ios_dependencies");

  for (const job of requiredIosJobs) {
    const result = needs[job]?.result;
    if (!knownResults.has(result)) throw new Error("invalid_ios_result");
    if (result !== "success") throw new Error(`${job}:${result}`);
  }
  return requiredIosJobs.map((job) => `${job}:success`).join(",");
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  try {
    const needs = {};
    if (process.env.IOS_NATIVE_RESULT !== undefined) {
      needs["ios-native"] = { result: process.env.IOS_NATIVE_RESULT };
    }
    if (process.env.DEVICE_UTILS_STORE_RESULT !== undefined) {
      needs["device-utils-store"] = { result: process.env.DEVICE_UTILS_STORE_RESULT };
    }
    console.log(checkIosNativeResults(needs));
  } catch (error) {
    console.error(error.message);
    process.exitCode = 1;
  }
}
