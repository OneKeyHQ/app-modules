// Shared demo constants for the capability-extraction green-field demos.

// A real OneKey PGP cleartext-signed message. `verifyGpgCleartext` runs the full
// GPG path against bundle-crypto's embedded public key and returns valid=true.
// NOTE: the signed payload's sha256 (bf3734…) is for a *different* artifact (an
// electron metadata.json), so it will NOT match the iOS bundle these demos
// download. That is expected — it proves the signature-verification primitive
// works. Swap in the matching signed manifest to verify one artifact end-to-end.
export const SAMPLE_SIGNED_MESSAGE = `-----BEGIN PGP SIGNED MESSAGE-----
Hash: SHA256

{
  "fileName": "metadata.json",
  "sha256": "bf3734ac6e59388fe23c40ce2960b6fd197c596af05dd08b3ccc8b201b78c52b",
  "size": 167265,
  "generatedAt": "2026-03-31T03:25:05.000Z",
  "appVersion": "6.1.0",
  "buildNumber": "2026032032",
  "bundleVersion": "7701116",
  "appType": "electron"
}
-----BEGIN PGP SIGNATURE-----

iQJCBAEBCAAsFiEE62iuVE8f3YzSZGJPs2mmepC/OHsFAmnLXs0OHGRldkBvbmVr
ZXkuc28ACgkQs2mmepC/OHtUkhAAoMZQc/Z1slPudePNjgO33XZwhWJNQkLeyPRL
Evz6JowioGdQjk1yJ+2jleSDDHRCceh6BzeqZqCFP58oRqug3MS4x1/7Egvza3l8
5vW+NeX9Ai8l4PniUDcC9IwBITsVz/wzjQdhOuVbtYcP4y/48JvctBNBj5cG7cG7
pMvOiXffUWjrBHToAKJec6V1N5L2b/2K3dutp10o3+tkfOznsHaD1vCpwxaeWcMx
W2I2SsH3uBDRYisY5W5mb5mDPbEuyqL+M+TLxHAGPwRe3+ExeipakPIJFfYsf5zi
6AnlllUv/QBH+1VZ7KauadPLD1HfMCPSbqQuTsgay56H7fvUe9khp2ysftgQ2tpc
NzTtQyZqIUeiUwBSTGqUvuLMCRChfGo7OBJE7Ec/VRzUIwGmN4Je+nY1JTYW+iR5
cRQ9j+aNAhLYLPkdUr9hMXaDjpSdGCBM0YpEoqSOzbuZEVCD92tzdfMUI+bdC6a/
I5cI5w1KTRKJ8irMfzm/TDcIenoUTvhzwqm+v69vFSR1LqWQMXnRvhONNTa9haov
+s+6KSUKPMH4Pa5AgRu5dkoj3UrbZUwt3tOIao97PXVXaFuSBLNhFEjS5yV+uOgK
Wfi3u5D2NWfhq0ZaV25yC16xDIe7SOXgHjNnR1vtt5L9ThZ2deidyiBJA6BFHZK6
RNAOJKE=
=JKzr
-----END PGP SIGNATURE-----`;

// Known-good sha256 of the demo OTA artifact
// (https://uni.onekey-asset.com/dashboard/version-update/12652642/ios-bundle.zip),
// verified on disk. Used as the "expected" value for the sha256 match step so
// the pipeline performs a real comparison (corrupt it to see the step fail).
export const EXPECTED_BUNDLE_SHA256 =
  'f4e6418a4abfe01f01325f90bcab366efa2dcfc3b36a03becf26420f69ab2007';
