"use strict";

import NativeDnsLookup from "./NativeDnsLookup.js";
export const DnsLookup = NativeDnsLookup;
export const getIpAddressesForHostname = hostname => NativeDnsLookup.getIpAddresses(hostname);
//# sourceMappingURL=index.js.map