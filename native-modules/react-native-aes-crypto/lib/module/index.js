"use strict";

import NativeAesCrypto from "./NativeAesCrypto.js";
export const encrypt = NativeAesCrypto.encrypt.bind(NativeAesCrypto);
export const decrypt = NativeAesCrypto.decrypt.bind(NativeAesCrypto);
export const pbkdf2 = NativeAesCrypto.pbkdf2.bind(NativeAesCrypto);
export const hmac256 = NativeAesCrypto.hmac256.bind(NativeAesCrypto);
export const hmac512 = NativeAesCrypto.hmac512.bind(NativeAesCrypto);
export const sha1 = NativeAesCrypto.sha1.bind(NativeAesCrypto);
export const sha256 = NativeAesCrypto.sha256.bind(NativeAesCrypto);
export const sha512 = NativeAesCrypto.sha512.bind(NativeAesCrypto);
export const randomUuid = NativeAesCrypto.randomUuid.bind(NativeAesCrypto);
export const randomKey = NativeAesCrypto.randomKey.bind(NativeAesCrypto);
export default NativeAesCrypto;
//# sourceMappingURL=index.js.map