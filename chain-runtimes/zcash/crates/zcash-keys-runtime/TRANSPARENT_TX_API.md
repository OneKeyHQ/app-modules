# Stateless transparent transaction API

`onekey-zcash-keys` can quote and sign a mainnet transaction that spends transparent
funds without a wallet database, scanner or network client. The host owns UTXO
discovery, outpoint reservation, revalidation at signing time, broadcast and status
reconciliation. Recipients find shielded outputs by scanning; the sender does not scan.

## Exports

- `transparentTxQuote(requestJson)`
- `transparentTxBuildWithAccountXprv(requestJson, accountXprv)`: transparent recipients
- `transparentShieldCreateWithAccountXprv(requestJson, accountXprv)` and
  `transparentShieldSignWithAccountXprv(requestJson, accountXprv, original, proved)`:
  Unified Address recipients through a proved PCZT
- `transparentTxCreateWithAccountXpub(requestJson, accountXpub, seedFingerprintHex)` and
  `transparentTxCombineHardwareSigned(requestJson, accountXpub, original, signed)`:
  hardware signing
- `parseTransparentTransactions(requestJson)`: verifies raw previous transactions
  against their txids and returns output values, scripts and coinbase status; the
  host checks each UTXO's index and value against it

All entry points take the same request. No entry point takes a seed or mnemonic.
`seedFingerprintHex` is the 32-byte ZIP-32 seed fingerprint written into the PCZT's
derivation metadata for the device; it is an identifier, not key material.

The xprv entry points take the account-level BIP-44 xprv at `m/44'/133'/account'`.
An account xprv cannot attest its ancestors, so the crate checks its depth and
hardened account child; deriving it from the right purpose and coin type is the
host's job.

## Call sequences

Unified Address recipient, software key:

1. `keys.transparentShieldCreateWithAccountXprv` builds a PCZT from the quoted request.
2. `runtime.pcztProveAtHeight` proves it.
3. `keys.transparentShieldSignWithAccountXprv` checks the request, the original PCZT
   and the proved PCZT, then signs.
4. `runtime.pcztExtractStateless` verifies and extracts the final transaction.

Hardware: `keys.transparentTxCreateWithAccountXpub` → device signs →
`keys.transparentTxCombineHardwareSigned` (verifies every signature against the
request) → `runtime.pcztProveAtHeight` → `runtime.pcztExtractStateless`.

## Request

Amounts are decimal strings because JavaScript numbers cannot represent every valid
zatoshi value.

```json
{
  "network": "main",
  "accountIndex": 0,
  "targetHeight": 3500000,
  "expiryHeight": 3500020,
  "utxos": [
    {
      "txid": "0000000000000000000000000000000000000000000000000000000000000001",
      "vout": 0,
      "valueZat": "100000",
      "scriptPubKey": "76a914000000000000000000000000000000000000000088ac",
      "isCoinbase": false,
      "confirmations": 1,
      "derivationPath": "m/44'/133'/0'/0/0"
    }
  ],
  "selectedOutpoints": [
    {
      "txid": "0000000000000000000000000000000000000000000000000000000000000001",
      "vout": 0
    }
  ],
  "recipients": [{ "address": "t1...", "amountZat": "50000" }],
  "sendMax": false,
  "change": {
    "address": "t1...",
    "derivationPath": "m/44'/133'/0'/1/0"
  }
}
```

- Only selected outpoints are spent; each must resolve to exactly one supplied UTXO.
- Inputs must be confirmed, non-coinbase P2PKH coins controlled by their declared
  external or internal BIP-44 path.
- Recipients may be `t1` (P2PKH), `t3` (P2SH), ZIP-320 `tex1` (paid as P2PKH), or a
  Unified Address with an Orchard receiver (paid as an Ironwood note). A Unified
  Address without one is rejected rather than paid at a public receiver.
  Sapling/Sprout destinations are unsupported.
- Change must be an owned P2PKH address on internal scope `1` with an address index
  below 40.
- Send-max: exactly one recipient, no `amountZat` and no `change`; the recipient gets
  the selected total minus the ZIP-317 fee.
- Fixed amount: a surplus too small to fund a change output (below the 5,000 zatoshi
  marginal fee, or not enough to pay for the extra output) is added to the fee and shown in the quote, so the overpayment stays
  below about 0.0001 ZEC. A larger surplus requires `change`.
- `targetHeight` must be on the mainnet NU6.3 branch. `expiryHeight` is mandatory,
  comes from the host's policy, and must be after the target height and at most 100
  blocks later. The result is a V6 transaction.

## Results

Quote:

```json
{
  "feeZat": "10000",
  "inputTotalZat": "100000",
  "sendAmountZat": "50000",
  "changeZat": "40000",
  "expiryHeight": 3500020,
  "spentOutpoints": [{ "txid": "...", "vout": 0 }]
}
```

Build:

```json
{
  "rawTx": "...",
  "txid": "...",
  "feeZat": "10000",
  "expiryHeight": 3500020,
  "spentOutpoints": [{ "txid": "...", "vout": 0 }]
}
```

Build entry points recompute the quote and fail closed if any input script or change
address does not match the derived key. Reserve every returned outpoint before
broadcast and keep the exact raw transaction for ambiguous broadcast recovery.
