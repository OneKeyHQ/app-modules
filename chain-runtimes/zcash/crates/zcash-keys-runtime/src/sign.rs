//! PCZT signing with host-supplied account keys.

use pczt::roles::signer::Signer;
use pczt::roles::verifier::{TransparentError, Verifier};
use transparent::{keys::AccountPrivKey, sighash::SighashType};

use crate::{
    account_keys::AccountSigningKeys,
    error::{ErrorCode, KeysError, Result},
};

/// Whether a transparent input is `SIGHASH_ALL`; any other type lets a holder of the signed tx rewrite recipients.
pub(crate) fn is_sighash_all(input: &transparent::pczt::Input) -> bool {
    *input.sighash_type() == SighashType::ALL
}

pub const SIGN_ORCHARD: bool = true;
pub const SIGN_SAPLING: bool = false;
pub const SIGN_TRANSPARENT: bool = true;
pub const SIGN_IRONWOOD: bool = true;

/// Address indices tried per input, since `pczt` hides derivation paths; upstream rejects a key that does
/// not match `script_pubkey`. Also bounds change indices in `transparent_send`.
pub(crate) const TRANSPARENT_SCAN_GAP: u32 = 40;

/// Returns (signed count, last failure).
fn sign_transparent_inputs(
    signer: &mut Signer,
    account_key: &AccountPrivKey,
    input_count: usize,
) -> (usize, Option<String>) {
    use transparent::keys::{NonHardenedChildIndex, TransparentKeyScope};

    let mut signed = 0usize;
    let mut last_err = None;

    for index in 0..input_count {
        let mut done = false;
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

/// Signs Orchard/Ironwood spends and transparent inputs; Sapling is unsupported.
/// Callers verify the PCZT against the reviewed payment first (`account_keys::sign_reviewed_pczt`).
pub fn sign_pczt_with_account_keys(
    keys: &AccountSigningKeys,
    pczt_bytes: &[u8],
) -> Result<Vec<u8>> {
    let pczt = pczt::Pczt::parse(pczt_bytes)
        .map_err(|e| KeysError::new(ErrorCode::PcztParseFailed).detail(format!("{e:?}")))?;

    // Require `SIGHASH_ALL` on every transparent input; upstream does not check.
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

    let ask = orchard::keys::SpendAuthorizingKey::from(keys.orchard());

    // Shielded actions are signed best-effort (upstream rejects foreign actions); error only if
    // nothing is signed. Both pools are signed since one transaction can have Ironwood and Orchard.
    let (orchard_count, ironwood_count, transparent_count) = bundle_counts(pczt_bytes)?;
    let mut signed_any = false;
    let mut last_err: Option<String> = None;

    // Every transparent input must be signed; one missing signature invalidates the transaction.
    let (transparent_signed, transparent_err) =
        sign_transparent_inputs(&mut signer, keys.transparent(), transparent_count);
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

/// PCZT copy for a hardware signer: clears Sapling and Orchard/Ironwood `bsk`, proofs, spend
/// witnesses and dummy spend keys. The host keeps the original to combine the device result into.
pub fn redact_for_hardware_signer(pczt_bytes: &[u8]) -> Result<Vec<u8>> {
    fn redact_orchard_protocol(mut bundle: pczt::roles::redactor::orchard::OrchardRedactor<'_>) {
        bundle.clear_zkproof();
        bundle.clear_bsk();
        bundle.redact_actions(|mut action| {
            action.clear_spend_witness();
            action.clear_spend_dummy_sk();
        });
    }
    let pczt = pczt::Pczt::parse(pczt_bytes)
        .map_err(|e| KeysError::new(ErrorCode::PcztParseFailed).detail(format!("{e:?}")))?;
    pczt::roles::redactor::Redactor::new(pczt)
        .redact_sapling_with(|mut sapling| sapling.clear_bsk())
        .redact_orchard_with(redact_orchard_protocol)
        .redact_ironwood_with(redact_orchard_protocol)
        .finish()
        .serialize()
        .map_err(|e| KeysError::new(ErrorCode::PcztParseFailed).detail(format!("{e:?}")))
}

/// Signable items per bundle: (orchard, ironwood, transparent).
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
    use crate::account_keys::test_keys;

    #[test]
    fn rejects_garbage_pczt() {
        let keys = test_keys::signing_keys(&[7u8; 32], 0);
        let e = sign_pczt_with_account_keys(&keys, b"not a pczt").unwrap_err();
        assert_eq!(e.code, ErrorCode::PcztParseFailed);
    }
}
