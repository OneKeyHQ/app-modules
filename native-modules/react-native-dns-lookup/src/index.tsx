import NativeDnsLookup from './NativeDnsLookup';

export const DnsLookup = NativeDnsLookup;
export const getIpAddressesForHostname = (
  hostname: string,
): Promise<string[]> => NativeDnsLookup.getIpAddresses(hostname);
export type { Spec as DnsLookupSpec } from './NativeDnsLookup';
