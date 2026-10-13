//! PCZT signing with keys derived from caller-provided seed bytes.
//!
//! This crate also handles spending keys in `keys` and `transparent_send`.
//! See the crate documentation for the limits of derived-key zeroization.

use pczt::roles::signer::Signer;
use pczt::roles::verifier::{TransparentError, Verifier};
use transparent::sighash::SighashType;
use zcash_keys::keys::UnifiedSpendingKey;
use zcash_protocol::consensus::Network;
use zip32::AccountId;

use crate::error::{ErrorCode, KeysError, Result};

/// Whether a transparent input's signature commits to all outputs.
///
/// A non-`SIGHASH_ALL` signature lets anyone holding the signed transaction
/// rewrite the recipients. All signing and verification paths share this check.
pub(crate) fn is_sighash_all(input: &transparent::pczt::Input) -> bool {
    *input.sighash_type() == SighashType::ALL
}

/// Bundle types this crate can sign. Kept next to the implementation so they change together.
pub const SIGN_ORCHARD: bool = true;
pub const SIGN_SAPLING: bool = false;
pub const SIGN_TRANSPARENT: bool = true;
pub const SIGN_IRONWOOD: bool = true;

/// Address indices to try when signing transparent inputs.
///
/// `pczt` does not expose each input's BIP-32 derivation path, so indices are
/// tried in order. Upstream `sign` checks the derived pubkey against
/// `script_pubkey`, so a wrong key fails instead of producing a bad signature.
///
/// Covers twice the default gap limit (20). It also bounds change address
/// indices (see `transparent_send`): change beyond this range could not be signed.
pub(crate) const TRANSPARENT_SCAN_GAP: u32 = 40;

/// Sign all transparent inputs; returns (signed count, last failure).
fn sign_transparent_inputs(
    signer: &mut Signer,
    usk: &UnifiedSpendingKey,
    input_count: usize,
) -> (usize, Option<String>) {
    use transparent::keys::{NonHardenedChildIndex, TransparentKeyScope};

    let account_key = usk.transparent();
    let mut signed = 0usize;
    let mut last_err = None;

    for index in 0..input_count {
        let mut done = false;
        // Receiving uses the external scope and change the internal one; try both.
        for scope in [TransparentKeyScope::EXTERNAL, TransparentKeyScope::INTERNAL] {
            for i in 0..TRANSPARENT_SCAN_GAP {
                let Some(child) = NonHardenedChildIndex::from_index(i) else {
                    continue;
                };
                let Ok(sk) = account_key.derive_secret_key(scope, child) else {
                    continue;
                };
                match signer.sign_transparent(index, &sk) {
                    Ok(()) => {
                        signed += 1;
                        done = true;
                        break;
                    }
                    Err(e) => last_err = Some(format!("transparent[{index}]: {e:?}")),
                }
            }
            if done {
                break;
            }
        }
    }

    (signed, last_err)
}

/// Sign a PCZT with spending keys derived from the seed. The caller wipes its seed copy.
///
/// Signs Orchard/Ironwood spends and transparent inputs only. Sapling spends are
/// unsupported (the ~50 MB Groth16 parameters are not shipped).
pub fn sign_pczt_with_seed(
    params: Network,
    seed: &[u8],
    account_index: u32,
    pczt_bytes: &[u8],
) -> Result<Vec<u8>> {
    if seed.len() < 32 {
        return Err(KeysError::with(
            ErrorCode::InvalidSeed,
            serde_json::json!({ "byteLen": seed.len(), "min": 32 }),
        ));
    }

    let account = AccountId::try_from(account_index).map_err(|_| {
        KeysError::with(
            ErrorCode::InvalidAccountIndex,
            serde_json::json!({ "value": account_index }),
        )
    })?;

    let usk = UnifiedSpendingKey::from_seed(&params, seed, account)
        .map_err(|e| KeysError::new(ErrorCode::DeriveFailed).detail(format!("{e:?}")))?;

    let pczt = pczt::Pczt::parse(pczt_bytes)
        .map_err(|e| KeysError::new(ErrorCode::PcztParseFailed).detail(format!("{e:?}")))?;

    // Upstream `Signer::sign_*` leaves semantic checks to the caller. Require
    // `SIGHASH_ALL` on every transparent input so signatures commit to all outputs.
    let pczt = Verifier::new(pczt)
        .with_transparent(|bundle| {
            match bundle
                .inputs()
                .iter()
                .position(|input| !is_sighash_all(input))
            {
                Some(index) => Err(TransparentError::Custom(KeysError::with(
                    ErrorCode::PcztSignFailed,
                    serde_json::json!({
                        "reason": "transparentInputSighashNotAll",
                        "inputIndex": index,
                    }),
                ))),
                None => Ok(()),
            }
        })
        .map_err(|e| KeysError::new(ErrorCode::PcztSignFailed).detail(format!("{e:?}")))?
        .finish();

    let mut signer = Signer::new(pczt)
        .map_err(|e| KeysError::new(ErrorCode::PcztSignFailed).detail(format!("{e:?}")))?;

    // Orchard spend authorizing key, derived from the spending key.
    let ask = orchard::keys::SpendAuthorizingKey::from(usk.orchard());

    // Shielded actions are signed best-effort: upstream rejects actions not owned
    // by this account, and it is an error only if nothing is signed. Both pools are
    // signed: post-NU6.3 spends use Ironwood, while migrations out of Orchard use
    // Orchard, and one transaction can have both.
    let (orchard_count, ironwood_count, transparent_count) = bundle_counts(pczt_bytes)?;
    let mut signed_any = false;
    let mut last_err: Option<String> = None;

    // Every transparent input must be signed; one missing signature invalidates the transaction.
    let (transparent_signed, transparent_err) =
        sign_transparent_inputs(&mut signer, &usk, transparent_count);
    if transparent_signed < transparent_count {
        return Err(KeysError::with(
            ErrorCode::PcztSignFailed,
            serde_json::json!({
                "transparentInputs": transparent_count,
                "transparentSigned": transparent_signed,
            }),
        )
        .detail(transparent_err.unwrap_or_else(|| {
            "a transparent input did not match any key in the scan range".into()
        })));
    }
    signed_any |= transparent_signed > 0;

    for index in 0..orchard_count {
        match signer.sign_orchard(index, &ask) {
            Ok(()) => signed_any = true,
            Err(e) => last_err = Some(format!("orchard[{index}]: {e:?}")),
        }
    }
    for index in 0..ironwood_count {
        match signer.sign_ironwood(index, &ask) {
            Ok(()) => signed_any = true,
            Err(e) => last_err = Some(format!("ironwood[{index}]: {e:?}")),
        }
    }

    if !signed_any {
        return Err(KeysError::with(
            ErrorCode::PcztSignFailed,
            serde_json::json!({
                "orchardActions": orchard_count,
                "ironwoodActions": ironwood_count,
                "transparentInputs": transparent_count,
            }),
        )
        .detail(last_err.unwrap_or_else(|| "nothing in this PCZT was signable".into())));
    }

    let signed = signer.finish();
    signed
        .serialize()
        .map_err(|e| KeysError::new(ErrorCode::PcztSignFailed).detail(format!("{e:?}")))
}

/// Count signable items per bundle: (orchard, ironwood, transparent).
///
/// Ironwood and Orchard share Rust types but are separate PCZT bundles.
fn bundle_counts(pczt_bytes: &[u8]) -> Result<(usize, usize, usize)> {
    let pczt = pczt::Pczt::parse(pczt_bytes)
        .map_err(|e| KeysError::new(ErrorCode::PcztParseFailed).detail(format!("{e:?}")))?;
    Ok((
        pczt.orchard().actions().len(),
        pczt.ironwood().actions().len(),
        pczt.transparent().inputs().len(),
    ))
}

#[cfg(test)]
mod tests {
    use super::*;

    const SEED: [u8; 32] = [7u8; 32];

    #[test]
    fn rejects_short_seed() {
        let e = sign_pczt_with_seed(Network::TestNetwork, &[0u8; 8], 0, &[]).unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidSeed);
        assert_eq!(e.params["byteLen"], 8);
    }

    #[test]
    fn rejects_garbage_pczt() {
        let e = sign_pczt_with_seed(Network::TestNetwork, &SEED, 0, b"not a pczt").unwrap_err();
        assert_eq!(e.code, ErrorCode::PcztParseFailed);
    }
}
