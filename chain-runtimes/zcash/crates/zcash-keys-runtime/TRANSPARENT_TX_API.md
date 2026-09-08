# Stateless transparent transaction API

This crate can quote and sign a mainnet transparent-to-transparent transaction without a
`WalletDb`, scanner, or network client. The host owns UTXO discovery, reservation, broadcast, and
status reconciliation.

## Exports

- `transparentTxQuote(requestJson)`
- `transparentTxBuildWithSeed(requestJson, seed)`
- `transparentTxBuildWithAccountXprv(requestJson, accountXprv)`

The xprv entry point accepts an account-level BIP-44 xprv. Its required derivation path is
`m/44'/133'/account'`. An account xprv cannot attest its ancestors, so the crate verifies its depth
and hardened account child metadata while the host remains responsible for deriving the account
xprv from the exact purpose and coin-type path.

## Request

Amounts are decimal strings because JavaScript numbers cannot represent every valid zatoshi value.

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

Only selected outpoints are spent. Every selection must resolve to exactly one supplied UTXO.
Inputs must be confirmed, non-coinbase P2PKH coins controlled by their declared external or internal
BIP-44 path. Recipient outputs may be mainnet P2PKH (`t1`) or P2SH (`t3`). Change must be an owned
P2PKH address on internal scope `1`.

For send-max, supply exactly one recipient and omit `amountZat` and `change`. The recipient receives
the selected input total minus the ZIP-317 fee. For a fixed amount, change below the standard 5,000
zatoshi marginal fee is rejected instead of silently adding it to the fee.

The target height must be on the mainnet NU6.3 branch. `expiryHeight` is mandatory and is supplied
by the host's transaction policy. The crate validates that it is after the target height and no more
than 100 blocks later; it does not choose a product expiry policy. The resulting transaction is V6.

## Results

Quote result:

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

Build result:

```json
{
  "rawTx": "...",
  "txid": "...",
  "feeZat": "10000",
  "expiryHeight": 3500020,
  "spentOutpoints": [{ "txid": "...", "vout": 0 }]
}
```

The build entry points recompute the quote and fail closed if any input script or change address does
not match the derived key. The host must reserve every returned outpoint before broadcast and retain
the exact raw transaction for ambiguous broadcast recovery.
