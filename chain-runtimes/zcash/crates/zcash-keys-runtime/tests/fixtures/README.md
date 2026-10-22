# Previous transaction fixtures

Raw public Zcash mainnet transactions:

- `69ff03c1e3d19df98087396f86783addbc9f8ad615022efdeb5de10fe78597ae`: V4, height 3490000, output 0 is 100000000 zatoshi.
- `70177881b2a62c93ed023725a24166362a5bd65cc93dbbd3b3a243ff5927d89c`: V6, height 3476066, output 0 is 1000 zatoshi.

These are public chain data, not spendable test credentials. Tests verify the
transaction IDs from the bytes before using the outputs. The coinbase test mutates
the V4 input into a synthetic coinbase outpoint and recomputes its ID; it does not
represent a mined transaction.
