import NativeAesCrypto from './NativeAesCrypto';
export declare const encrypt: (data: string, key: string, iv: string, algorithm: string) => Promise<string>;
export declare const decrypt: (base64: string, key: string, iv: string, algorithm: string) => Promise<string>;
export declare const pbkdf2: (password: string, salt: string, cost: number, length: number, algorithm: string) => Promise<string>;
export declare const hmac256: (base64: string, key: string) => Promise<string>;
export declare const hmac512: (base64: string, key: string) => Promise<string>;
export declare const sha1: (text: string) => Promise<string>;
export declare const sha256: (text: string) => Promise<string>;
export declare const sha512: (text: string) => Promise<string>;
export declare const randomUuid: () => Promise<string>;
export declare const randomKey: (length: number) => Promise<string>;
export default NativeAesCrypto;
export type { Spec as AesCryptoSpec } from './NativeAesCrypto';
//# sourceMappingURL=index.d.ts.map