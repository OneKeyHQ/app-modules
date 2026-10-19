export declare const Availability: {
  readonly Available: "available";
  readonly DeviceNotPresent: "device_not_present";
  readonly NotConfiguredForUser: "not_configured_for_user";
  readonly DisabledByPolicy: "disabled_by_policy";
  readonly DeviceBusy: "device_busy";
};
export type Availability = (typeof Availability)[keyof typeof Availability];

export declare const VerificationResult: {
  readonly Verified: "verified";
  readonly DeviceNotPresent: "device_not_present";
  readonly NotConfiguredForUser: "not_configured_for_user";
  readonly DisabledByPolicy: "disabled_by_policy";
  readonly DeviceBusy: "device_busy";
  readonly RetriesExhausted: "retries_exhausted";
  readonly Canceled: "canceled";
};
export type VerificationResult =
  (typeof VerificationResult)[keyof typeof VerificationResult];

export interface VerificationOptions {
  windowHandle: import("node:buffer").Buffer;
  message: string;
}

export declare function checkAvailability(): Promise<Availability>;
export declare function requestVerification(
  options: VerificationOptions,
): Promise<VerificationResult>;
export declare function allowSetForegroundWindow(processId: number): boolean;
