"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.defaultConfig = defaultConfig;
function defaultConfig(options) {
  const {
    metadata,
    extraConnectors
  } = options;
  let providers = {
    metadata,
    extraConnectors
  };
  return providers;
}
//# sourceMappingURL=defaultConfig.js.map