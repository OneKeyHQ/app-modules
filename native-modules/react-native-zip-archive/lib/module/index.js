"use strict";

import NativeZipArchive from "./NativeZipArchive.js";
export const ZipArchive = NativeZipArchive;
export const zip = (source, target) => NativeZipArchive.zipFolder(source, target);
//# sourceMappingURL=index.js.map