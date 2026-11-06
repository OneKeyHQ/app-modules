import { pathToFileURL } from "node:url";

export const requiredIosJobs = ["ios-native"];
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
  return "ios-native:success";
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  try {
    const result = process.env.IOS_NATIVE_RESULT;
    const needs = result === undefined ? {} : { "ios-native": { result } };
    console.log(checkIosNativeResults(needs));
  } catch (error) {
    console.error(error.message);
    process.exitCode = 1;
  }
}
