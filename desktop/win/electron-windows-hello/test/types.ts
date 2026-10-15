import { Buffer } from "node:buffer";
import {
  Availability,
  VerificationResult,
  allowSetForegroundWindow,
  checkAvailability,
  requestVerification,
} from "../index";

const availability: Promise<Availability> = checkAvailability();
const verification: Promise<VerificationResult> = requestVerification({
  windowHandle: Buffer.alloc(8),
  message: "Unlock wallet",
});
const permission: boolean = allowSetForegroundWindow(123);
void availability;
void verification;
void permission;
