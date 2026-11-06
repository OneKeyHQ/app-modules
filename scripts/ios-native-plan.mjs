import { pathToFileURL } from "node:url";

const plan = {
  buildTargets: ["ReactNativeLiteCard", "NativeOverlay"],
  testSchemes: [
    "react-native-pager-view-Unit-Tests",
    "react-native-native-list-Unit-Tests",
    "OneKeyImage-Unit-Tests",
    "OneKeyTextInput-Unit-Tests",
    "PerpDepthBar-Unit-Tests",
    "ReactNativeBundleCrypto-Unit-Tests",
    "ReactNativeImageCropPicker-Unit-Tests",
    "ReactNativeCaptureProtection-Unit-Tests",
    "ReactNativePhotoLibrary-Unit-Tests",
    "ReactNativeJpush-Unit-Tests",
  ],
};

export function getIosNativePlan() {
  return structuredClone(plan);
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  if (process.argv.length !== 2) {
    console.error("invalid_ios_native_arguments");
    process.exitCode = 1;
  } else {
    console.log(JSON.stringify(getIosNativePlan()));
  }
}
