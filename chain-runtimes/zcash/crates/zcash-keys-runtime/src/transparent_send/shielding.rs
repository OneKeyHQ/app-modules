//! Stateless shielding from an explicit, host-owned transparent UTXO catalog.
use super::*;
use pczt::roles::{
    combiner::Combiner,
    creator::Creator,
    io_finalizer::IoFinalizer,
    signer::Signer,
    spend_finalizer::SpendFinalizer,
    updater::Updater,
    verifier::{OrchardError, TransparentError, Verifier},
};
use zcash_note_encryption::Domain;
use zcash_primitives::transaction::builder::{BuildConfig, Builder, BundlePadding};
use zcash_protocol::memo::MemoBytes;

/// 本模块实际提供的入口。定义在实现旁边，删改实现必然改到这里。
///
/// 宿主必须先查能力再调：这四个函数是后加的，装到旧 wasm 时缺的是导出本身，
/// 直接调只会得到 `undefined is not a function` —— 那看起来像 app 崩了，
/// 而不是一次「本版本不支持」。
pub const SHIELD_CREATE_WITH_SEED: bool = true;
pub const SHIELD_CREATE_WITH_ACCOUNT_XPRV: bool = true;
pub const SHIELD_SIGN_WITH_SEED: bool = true;
pub const SHIELD_SIGN_WITH_ACCOUNT_XPRV: bool = true;

fn failure(error: impl std::fmt::Debug) -> KeysError {
    KeysError::new(ErrorCode::TransactionBuildFailed).detail(format!("{error:?}"))
}

pub fn create(request_json: &str, seed: &[u8]) -> Result<Vec<u8>> {
    if seed.len() < 32 {
        return Err(KeysError::new(ErrorCode::InvalidSeed));
    }
    let plan = plan_transaction(parse_request(request_json)?)?;
    let key =
        AccountPrivKey::from_seed(&MAIN_NETWORK, seed, plan.account_index).map_err(failure)?;
    create_with_public_key(&plan, &key.to_account_pubkey())
}

pub fn create_with_account_xprv(request_json: &str, account_xprv: &str) -> Result<Vec<u8>> {
    let plan = plan_transaction(parse_request(request_json)?)?;
    let key = parse_account_xprv(account_xprv, u32::from(plan.account_index))?;
    create_with_public_key(&plan, &key.to_account_pubkey())
}

fn create_with_public_key(plan: &TransactionPlan, key: &AccountPubKey) -> Result<Vec<u8>> {
    let mut builder = Builder::new(
        MAIN_NETWORK,
        plan.target_height,
        BuildConfig::Standard {
            sapling_anchor: None,
            orchard_anchor: None,
            // No shielded spends: the empty anchor needs no scanned commitment tree.
            ironwood_anchor: plan
                .recipients
                .iter()
                .any(|output| matches!(output.address, RecipientAddress::Ironwood(_)))
                .then(orchard::Anchor::empty_tree),
            orchard_padding: BundlePadding::DEFAULT,
            ironwood_padding: BundlePadding::DEFAULT,
        },
    )
    .with_expiry_height(plan.expiry_height);
    for input in &plan.inputs {
        let pubkey = derive_public_key(key, input.path)?;
        let address = TransparentAddress::from_pubkey(&pubkey);
        if address.script().to_bytes() != input.script_pub_key {
            return Err(KeysError::new(ErrorCode::KeyMismatch));
        }
        builder
            .add_transparent_p2pkh_input(
                pubkey,
                OutPoint::new(*input.txid.as_ref(), input.request.vout),
                TxOut::new(input.value, address.script().into()),
            )
            .map_err(failure)?;
    }
    for output in &plan.recipients {
        match output.address {
            RecipientAddress::Transparent(address) => builder
                .add_transparent_output(&address, output.value)
                .map_err(failure)?,
            RecipientAddress::Ironwood(address) => builder
                .add_ironwood_output::<std::convert::Infallible>(
                    None,
                    address,
                    output.value,
                    MemoBytes::empty(),
                )
                .map_err(failure)?,
        }
    }
    if let Some((_, output, path)) = &plan.change {
        let pubkey = derive_public_key(key, *path)?;
        let address = TransparentAddress::from_pubkey(&pubkey);
        if address != require_transparent(output.address)? {
            return Err(KeysError::new(ErrorCode::KeyMismatch));
        }
        builder
            .add_transparent_output(&address, output.value)
            .map_err(failure)?;
    }
    let parts = builder
        .build_for_pczt(rand::rngs::OsRng, &Zip317FeeRule::standard())
        .map_err(failure)?;
    let pczt = Creator::build_from_parts(parts.pczt_parts)
        .ok_or_else(|| KeysError::new(ErrorCode::TransactionBuildFailed))?;
    let mut transparent_index = 0;
    let mut ironwood_index = 0;
    let mut transparent_addresses = Vec::new();
    let mut ironwood_addresses = Vec::new();
    for output in &plan.recipients {
        match output.address {
            RecipientAddress::Transparent(_) => {
                transparent_addresses.push((transparent_index, output.user_address.clone()));
                transparent_index += 1;
            }
            RecipientAddress::Ironwood(_) => {
                let action_index = parts
                    .ironwood_meta
                    .output_action_index(ironwood_index)
                    .ok_or_else(mismatch)?;
                ironwood_addresses.push((action_index, output.user_address.clone()));
                ironwood_index += 1;
            }
        }
    }
    let pczt = Updater::new(pczt)
        .update_transparent_with(|mut bundle| {
            for (index, address) in transparent_addresses {
                bundle.update_output_with(index, |mut output| {
                    output.set_user_address(address);
                    Ok(())
                })?;
            }
            Ok(())
        })
        .map_err(failure)?
        .update_ironwood_with(|mut bundle| {
            for (index, address) in ironwood_addresses {
                bundle.update_action_with(index, |mut action| {
                    action.set_output_user_address(address);
                    Ok(())
                })?;
            }
            Ok(())
        })
        .map_err(failure)?
        .finish();
    IoFinalizer::new(pczt)
        .finalize_io()
        .map_err(failure)?
        .serialize()
        .map_err(failure)
}

/// Builds an unproved PCZT from an account xpub. Device derivation metadata
/// covers every selected input and the transparent change output.
pub fn create_with_account_xpub(
    request_json: &str,
    account_xpub: &str,
    seed_fingerprint_hex: &str,
) -> Result<Vec<u8>> {
    let plan = plan_transaction(parse_request(request_json)?)?;
    let key = parse_account_xpub(account_xpub, u32::from(plan.account_index))?;
    let fingerprint: [u8; 32] = hex::decode(seed_fingerprint_hex)
        .map_err(|_| KeysError::new(ErrorCode::InvalidTransactionRequest))?
        .try_into()
        .map_err(|_| KeysError::new(ErrorCode::InvalidTransactionRequest))?;
    let original = create_with_public_key(&plan, &key)?;
    let pczt = pczt::Pczt::parse(&original).map_err(failure)?;
    let derivation = |path: DerivationPath| {
        transparent::pczt::Bip32Derivation::parse(
            fingerprint,
            vec![
                44 | (1 << 31),
                133 | (1 << 31),
                path.account | (1 << 31),
                path.scope,
                path.index,
            ],
        )
        .map_err(failure)
    };
    let input_keys = plan
        .inputs
        .iter()
        .map(|input| {
            Ok((
                derive_public_key(&key, input.path)?.serialize(),
                derivation(input.path)?,
            ))
        })
        .collect::<Result<Vec<_>>>()?;
    let change_key = plan
        .change
        .as_ref()
        .map(|(_, _, path)| {
            Ok((
                derive_public_key(&key, *path)?.serialize(),
                derivation(*path)?,
            ))
        })
        .transpose()?;
    let change_index = plan
        .recipients
        .iter()
        .filter(|output| matches!(output.address, RecipientAddress::Transparent(_)))
        .count();
    let updated = Updater::new(pczt)
        .update_transparent_with(|mut updater| {
            for (index, (public, path)) in input_keys.into_iter().enumerate() {
                updater.update_input_with(index, |mut input| {
                    input.set_bip32_derivation(public, path);
                    Ok(())
                })?;
            }
            if let Some((public, path)) = change_key {
                updater.update_output_with(change_index, |mut output| {
                    output.set_bip32_derivation(public, path);
                    Ok(())
                })?;
            }
            Ok(())
        })
        .map_err(failure)?
        .finish();
    // Keep device RAM bounds explicit before asking the user to confirm.
    let serialized = updated.serialize().map_err(failure)?;
    if serialized.len() > 24 * 1024 {
        return Err(KeysError::with(
            ErrorCode::TransactionBuildFailed,
            serde_json::json!({ "reason": "hardwarePcztTooLarge", "maxBytes": 24 * 1024 }),
        ));
    }
    Ok(serialized)
}

/// Combines a device's redacted signatures with the approved original, verifies
/// every signature against the exact request, then finalizes transparent spends.
pub fn combine_hardware_signed(
    request_json: &str,
    account_xpub: &str,
    original: &[u8],
    signed: &[u8],
) -> Result<Vec<u8>> {
    let plan = plan_transaction(parse_request(request_json)?)?;
    let key = parse_account_xpub(account_xpub, u32::from(plan.account_index))?;
    let original = validate_original(&plan, &key, pczt::Pczt::parse(original).map_err(failure)?)?;
    let original_signer = Signer::new(original.clone()).map_err(failure)?;
    let combined = Combiner::new(vec![original, pczt::Pczt::parse(signed).map_err(failure)?])
        .combine()
        .map_err(failure)?;
    if combined.transparent().inputs().len() != plan.inputs.len() {
        return Err(mismatch());
    }
    let combined_signer = Signer::new(combined.clone()).map_err(failure)?;
    let mut sighashes = Vec::with_capacity(plan.inputs.len());
    for index in 0..plan.inputs.len() {
        let approved = original_signer
            .transparent_sighash(index)
            .map_err(failure)?;
        if combined_signer
            .transparent_sighash(index)
            .map_err(failure)?
            != approved
        {
            return Err(mismatch());
        }
        sighashes.push(approved);
    }
    let secp = secp256k1::Secp256k1::verification_only();
    let combined = Verifier::new(combined)
        .with_transparent(|bundle| {
            for (index, input) in bundle.inputs().iter().enumerate() {
                let public = derive_public_key(&key, plan.inputs[index].path)
                    .map_err(TransparentError::Custom)?;
                let signatures = input.partial_signatures();
                let signature = signatures
                    .get(&public.serialize())
                    .ok_or_else(|| TransparentError::Custom(mismatch()))?;
                if signatures.len() != 1
                    || signature.last() != Some(&1)
                    || input.script_sig().is_some()
                    || *input.sighash_type() != transparent::sighash::SighashType::ALL
                {
                    return Err(TransparentError::Custom(mismatch()));
                }
                let signature =
                    secp256k1::ecdsa::Signature::from_der(&signature[..signature.len() - 1])
                        .map_err(|_| TransparentError::Custom(mismatch()))?;
                secp.verify_ecdsa(
                    &secp256k1::Message::from_digest(sighashes[index]),
                    &signature,
                    &public,
                )
                .map_err(|_| TransparentError::Custom(mismatch()))?;
            }
            Ok(())
        })
        .map_err(failure)?
        .finish();
    SpendFinalizer::new(combined)
        .finalize_spends()
        .map_err(failure)?
        .serialize()
        .map_err(failure)
}

fn mismatch() -> KeysError {
    KeysError::new(ErrorCode::KeyMismatch)
}

fn validate_ironwood(bundle: &orchard::pczt::Bundle, plan: &TransactionPlan) -> Result<()> {
    let mut expected = plan
        .recipients
        .iter()
        .filter_map(|output| match output.address {
            RecipientAddress::Ironwood(address) => Some((address, u64::from(output.value))),
            RecipientAddress::Transparent(_) => None,
        })
        .collect::<Vec<_>>();
    let total = expected.iter().map(|(_, value)| value).sum::<u64>();
    let action_count = if expected.is_empty() {
        0
    } else {
        expected.len().max(2)
    };
    if bundle.actions().len() != action_count
        || *bundle.bundle_version() != orchard::bundle::BundleVersion::ironwood_v3()
        || *bundle.anchor() != orchard::Anchor::empty_tree()
        || bundle.value_sum().magnitude_sign().0 != total
        || (total != 0
            && !matches!(
                bundle.value_sum().magnitude_sign().1,
                orchard::value::Sign::Negative
            ))
    {
        return Err(mismatch());
    }
    bundle.verify_cross_address_restriction().map_err(failure)?;
    for action in bundle.actions() {
        let spend = action.spend();
        let output = action.output();
        if *spend.value() != Some(orchard::value::NoteValue::ZERO)
            || spend.dummy_sk().is_some()
            || spend.spend_auth_sig().is_none()
            || *spend.note_version() != orchard::note::NoteVersion::V3
            || *output.note_version() != orchard::note::NoteVersion::V3
        {
            return Err(mismatch());
        }
        action.verify_cv_net().map_err(failure)?;
        spend.verify_nullifier(None).map_err(failure)?;
        spend.verify_rk(None).map_err(failure)?;
        output.verify_note_commitment(spend).map_err(failure)?;
        let recipient = output.recipient().ok_or_else(mismatch)?;
        let value = output.value().ok_or_else(mismatch)?;
        if value.inner() != 0 {
            let index = expected
                .iter()
                .position(|entry| *entry == (recipient, value.inner()))
                .ok_or_else(mismatch)?;
            expected.remove(index);
        }
        // A matching commitment alone does not ensure the recipient can decrypt the note.
        // Dummy output ciphertexts may be randomized by the upstream builder.
        if value.inner() != 0 {
            let note = orchard::Note::from_parts(
                recipient,
                value,
                orchard::note::Rho::from_bytes(&spend.nullifier().to_bytes())
                    .into_option()
                    .ok_or_else(mismatch)?,
                output.rseed().ok_or_else(mismatch)?,
                *output.note_version(),
            )
            .into_option()
            .ok_or_else(mismatch)?;
            let encryptor = orchard::note_encryption::IronwoodNoteEncryption::new(
                None,
                note,
                *MemoBytes::empty().as_array(),
            );
            if encryptor.encrypt_note_plaintext() != output.encrypted_note().enc_ciphertext
                || orchard::note_encryption::IronwoodDomain::epk_bytes(encryptor.epk()).0
                    != output.encrypted_note().epk_bytes
            {
                return Err(mismatch());
            }
        }
    }
    if !expected.is_empty() {
        return Err(mismatch());
    }
    Ok(())
}

fn validate_original(
    plan: &TransactionPlan,
    key: &AccountPubKey,
    pczt: pczt::Pczt,
) -> Result<pczt::Pczt> {
    let global = pczt.global();
    let has_ironwood = plan
        .recipients
        .iter()
        .any(|output| matches!(output.address, RecipientAddress::Ironwood(_)));
    let expected_anchor = has_ironwood.then(|| orchard::Anchor::empty_tree().to_bytes());
    if *global.tx_version() != zcash_protocol::constants::V6_TX_VERSION
        || *global.version_group_id() != zcash_protocol::constants::V6_VERSION_GROUP_ID
        || *global.consensus_branch_id() != u32::from(BranchId::Nu6_3)
        || *global.expiry_height() != u32::from(plan.expiry_height)
        || pczt::common::determine_lock_time(global, pczt.transparent().inputs()) != Some(0)
        || !pczt.sapling().spends().is_empty()
        || !pczt.sapling().outputs().is_empty()
        || !pczt.orchard().actions().is_empty()
        || *pczt.ironwood().anchor() != expected_anchor
        || (!has_ironwood
            && (!pczt.ironwood().actions().is_empty()
                || *pczt.ironwood().value_sum() != (0, false)))
        || pczt.transparent().inputs().len() != plan.inputs.len()
    {
        return Err(mismatch());
    }
    for (input, planned) in pczt.transparent().inputs().iter().zip(&plan.inputs) {
        let public = derive_public_key(key, planned.path)?;
        if input.prevout_txid() != planned.txid.as_ref()
            || *input.prevout_index() != planned.request.vout
            || *input.value() != u64::from(planned.value)
            || input.script_pubkey() != &planned.script_pub_key
            || TransparentAddress::from_pubkey(&public).script().to_bytes()
                != planned.script_pub_key
            || input
                .sequence()
                .is_some_and(|sequence| sequence != u32::MAX)
        {
            return Err(mismatch());
        }
    }
    let mut outputs = plan
        .recipients
        .iter()
        .filter_map(|output| match output.address {
            RecipientAddress::Transparent(address) => Some((address, output.value)),
            RecipientAddress::Ironwood(_) => None,
        })
        .collect::<Vec<_>>();
    if let Some((_, change, path)) = &plan.change {
        let address = require_transparent(change.address)?;
        let public = derive_public_key(key, *path)?;
        if TransparentAddress::from_pubkey(&public) != address {
            return Err(mismatch());
        }
        outputs.push((address, change.value));
    }
    if pczt.transparent().outputs().len() != outputs.len() {
        return Err(mismatch());
    }
    for (output, (address, value)) in pczt.transparent().outputs().iter().zip(outputs) {
        if *output.value() != u64::from(value)
            || *output.script_pubkey() != address.script().to_bytes()
        {
            return Err(mismatch());
        }
    }
    let verified = Verifier::new(pczt)
        .with_transparent(|bundle| {
            if bundle.inputs().iter().any(|input| {
                *input.sighash_type() != transparent::sighash::SighashType::ALL
                    || input.redeem_script().is_some()
                    || input.script_sig().is_some()
                    || !input.partial_signatures().is_empty()
                    || input.required_time_lock_time().is_some()
                    || input.required_height_lock_time().is_some()
            }) {
                return Err(TransparentError::Custom(mismatch()));
            }
            Ok(())
        })
        .map_err(failure)?;
    let verified = if has_ironwood {
        verified
            .with_ironwood(|bundle| validate_ironwood(bundle, plan).map_err(OrchardError::Custom))
            .map_err(failure)?
    } else {
        verified
    };
    Ok(verified.finish())
}

pub fn sign(request_json: &str, seed: &[u8], original: &[u8], pczt: &[u8]) -> Result<Vec<u8>> {
    if seed.len() < 32 {
        return Err(KeysError::new(ErrorCode::InvalidSeed));
    }
    let plan = plan_transaction(parse_request(request_json)?)?;
    let key =
        AccountPrivKey::from_seed(&MAIN_NETWORK, seed, plan.account_index).map_err(failure)?;
    sign_with_key(&plan, &key, original, pczt)
}

pub fn sign_with_account_xprv(
    request_json: &str,
    account_xprv: &str,
    original: &[u8],
    pczt: &[u8],
) -> Result<Vec<u8>> {
    let plan = plan_transaction(parse_request(request_json)?)?;
    let key = parse_account_xprv(account_xprv, u32::from(plan.account_index))?;
    sign_with_key(&plan, &key, original, pczt)
}

fn sign_with_key(
    plan: &TransactionPlan,
    key: &AccountPrivKey,
    original: &[u8],
    pczt: &[u8],
) -> Result<Vec<u8>> {
    let original = validate_original(
        plan,
        &key.to_account_pubkey(),
        pczt::Pczt::parse(original).map_err(failure)?,
    )?;
    let pczt = pczt::Pczt::parse(pczt).map_err(failure)?;
    // V6 deliberately excludes anchors from the effects hash (ZIP 374).
    if pczt.ironwood().anchor() != original.ironwood().anchor()
        || pczt.transparent().inputs().len() != plan.inputs.len()
    {
        return Err(mismatch());
    }
    let pczt = Verifier::new(pczt)
        .with_transparent(|bundle| {
            if bundle
                .inputs()
                .iter()
                .any(|input| *input.sighash_type() != transparent::sighash::SighashType::ALL)
            {
                return Err(TransparentError::Custom(mismatch()));
            }
            Ok(())
        })
        .map_err(failure)?
        .finish();
    let original_signer = Signer::new(original).map_err(failure)?;
    let mut signer = Signer::new(pczt).map_err(failure)?;
    // After request validation, SIGHASH_ALL pins the prover to the approved effects.
    for index in 0..plan.inputs.len() {
        if original_signer
            .transparent_sighash(index)
            .map_err(failure)?
            != signer.transparent_sighash(index).map_err(failure)?
        {
            return Err(mismatch());
        }
    }
    for (index, input) in plan.inputs.iter().enumerate() {
        signer
            .sign_transparent(index, &derive_secret_key(key, input.path)?)
            .map_err(failure)?;
    }
    SpendFinalizer::new(signer.finish())
        .finalize_spends()
        .map_err(failure)?
        .serialize()
        .map_err(failure)
}

#[cfg(test)]
mod tests {
    use super::*;
    use serde_json::{json, Value};

    const SEED: [u8; 64] = [7; 64];

    fn transparent_address(scope: u32, index: u32) -> TransparentAddress {
        let key = AccountPrivKey::from_seed(&MAIN_NETWORK, &SEED, AccountId::ZERO).unwrap();
        let secret = derive_secret_key(
            &key,
            DerivationPath {
                account: 0,
                scope,
                index,
            },
        )
        .unwrap();
        TransparentAddress::from_pubkey(&secp256k1::PublicKey::from_secret_key(
            &secp256k1::Secp256k1::new(),
            &secret,
        ))
    }

    fn recipient(index: u32) -> String {
        let sk = orchard::keys::SpendingKey::from_bytes([7; 32]).unwrap();
        let fvk = orchard::keys::FullViewingKey::from(&sk);
        zcash_keys::address::UnifiedAddress::from_receivers(
            Some(fvk.address_at(index, zip32::Scope::External)),
            None,
            None,
        )
        .unwrap()
        .encode(&MAIN_NETWORK)
    }

    fn request(send_max: bool) -> Value {
        let txid = format!("{:064x}", 1);
        json!({
            "network": "main", "accountIndex": 0,
            "targetHeight": 3_500_000, "expiryHeight": 3_500_020,
            "utxos": [{ "txid": txid, "vout": 0, "valueZat": "100000",
                "scriptPubKey": hex::encode(transparent_address(0, 0).script().to_bytes()),
                "isCoinbase": false, "confirmations": 1, "derivationPath": "m/44'/133'/0'/0/0" }],
            "selectedOutpoints": [{ "txid": txid, "vout": 0 }],
            "recipients": [{ "address": recipient(0), "amountZat": if send_max { Value::Null } else { "50000".into() } }],
            "sendMax": send_max,
            "change": if send_max { Value::Null } else { json!({
                "address": transparent_address(1, 0).to_zcash_address(NetworkType::Main).to_string(),
                "derivationPath": "m/44'/133'/0'/1/0" }) },
        })
    }

    fn account_xpub() -> String {
        let key = AccountPrivKey::from_seed(&MAIN_NETWORK, &SEED, AccountId::ZERO).unwrap();
        let mut payload = vec![0x04, 0x88, 0xb2, 0x1e, 3, 0, 0, 0, 0];
        payload.extend_from_slice(&(1_u32 << 31).to_be_bytes());
        payload.extend_from_slice(&key.to_account_pubkey().serialize());
        bs58::encode(payload).with_check().into_string()
    }

    fn simulated_device_signature(original: &[u8]) -> Vec<u8> {
        simulated_device_signatures(original, 1)
    }

    fn simulated_device_signatures(original: &[u8], input_count: usize) -> Vec<u8> {
        let key = AccountPrivKey::from_seed(&MAIN_NETWORK, &SEED, AccountId::ZERO).unwrap();
        let mut signer = Signer::new(pczt::Pczt::parse(original).unwrap()).unwrap();
        for index in 0..input_count {
            signer
                .sign_transparent(
                    index,
                    &derive_secret_key(
                        &key,
                        DerivationPath {
                            account: 0,
                            scope: 0,
                            index: 0,
                        },
                    )
                    .unwrap(),
                )
                .unwrap();
        }
        pczt::roles::redactor::Redactor::new(signer.finish())
            .redact_transparent_with(|mut bundle| {
                bundle.redact_inputs(|mut input| input.clear_bip32_derivation());
                bundle.redact_outputs(|mut output| output.clear_bip32_derivation());
            })
            .finish()
            .serialize()
            .unwrap()
    }

    #[test]
    fn hardware_metadata_matches_each_external_output_after_action_randomization() {
        let mut request = request(false);
        request["recipients"] = json!([
            { "address": recipient(0), "amountZat": "10000" },
            { "address": recipient(1), "amountZat": "10000" },
            { "address": recipient(0), "amountZat": "10000" },
            { "address": transparent_address(0, 9).to_zcash_address(NetworkType::Main).to_string(), "amountZat": "10000" },
        ]);
        for _ in 0..4 {
            let original =
                create_with_account_xpub(&request.to_string(), &account_xpub(), &"01".repeat(32))
                    .unwrap();
            let pczt = pczt::Pczt::parse(&original).unwrap();
            Verifier::new(pczt)
                .with_transparent::<KeysError, _>(|bundle| {
                    assert_eq!(bundle.inputs()[0].bip32_derivation().len(), 1);
                    let external = &bundle.outputs()[0];
                    let address = require_transparent(
                        parse_recipient_address(external.user_address().as_ref().unwrap()).unwrap(),
                    )
                    .unwrap();
                    assert_eq!(
                        address.script().to_bytes(),
                        external.script_pubkey().to_bytes()
                    );
                    assert!(external.bip32_derivation().is_empty());
                    assert_eq!(bundle.outputs()[1].bip32_derivation().len(), 1);
                    Ok(())
                })
                .unwrap()
                .with_ironwood::<KeysError, _>(|bundle| {
                    let mut displayed = Vec::new();
                    for action in bundle.actions() {
                        let output = action.output();
                        if output.value().unwrap().inner() == 0 {
                            continue;
                        }
                        let address = output.user_address().as_ref().unwrap();
                        let RecipientAddress::Ironwood(receiver) =
                            parse_recipient_address(address).unwrap()
                        else {
                            panic!("expected Ironwood recipient");
                        };
                        assert_eq!(Some(receiver), *output.recipient());
                        displayed.push(address.clone());
                    }
                    displayed.sort();
                    let mut expected = vec![recipient(0), recipient(1), recipient(0)];
                    expected.sort();
                    assert_eq!(displayed, expected);
                    Ok(())
                })
                .unwrap();
        }
    }

    #[test]
    fn hardware_requires_all_selected_inputs_and_rejects_invalid_public_material() {
        let mut request = request(false);
        let mut second = request["utxos"][0].clone();
        second["vout"] = 1.into();
        request["utxos"].as_array_mut().unwrap().push(second);
        let mut outpoint = request["selectedOutpoints"][0].clone();
        outpoint["vout"] = 1.into();
        request["selectedOutpoints"]
            .as_array_mut()
            .unwrap()
            .push(outpoint);
        let request = request.to_string();
        let original =
            create_with_account_xpub(&request, &account_xpub(), &"01".repeat(32)).unwrap();
        assert!(combine_hardware_signed(
            &request,
            &account_xpub(),
            &original,
            &simulated_device_signatures(&original, 1)
        )
        .is_err());
        let signed = simulated_device_signatures(&original, 2);
        let finalized =
            combine_hardware_signed(&request, &account_xpub(), &original, &signed).unwrap();
        assert!(combine_hardware_signed(&request, &account_xpub(), &original, &finalized).is_err());
        let parsed = pczt::Pczt::parse(&signed).unwrap();
        let mut signature = Vec::new();
        Verifier::new(parsed)
            .with_transparent::<KeysError, _>(|bundle| {
                signature = bundle.inputs()[0]
                    .partial_signatures()
                    .values()
                    .next()
                    .unwrap()
                    .clone();
                Ok(())
            })
            .unwrap();
        let position = signed
            .windows(signature.len())
            .position(|bytes| bytes == signature.as_slice())
            .unwrap();
        let mut corrupted = signed.clone();
        corrupted[position + signature.len() - 3] ^= 1;
        assert!(combine_hardware_signed(&request, &account_xpub(), &original, &corrupted).is_err());
        assert!(create_with_account_xpub(&request, "not-an-xpub", &"01".repeat(32)).is_err());
        assert!(create_with_account_xpub(&request, &account_xpub(), "01").is_err());
    }

    #[test]
    fn hardware_public_construction_and_signature_validation_cover_both_destinations() {
        for transparent_destination in [false, true] {
            for send_max in [false, true] {
                let mut request = request(send_max);
                if transparent_destination {
                    request["recipients"][0]["address"] = transparent_address(0, 9)
                        .to_zcash_address(NetworkType::Main)
                        .to_string()
                        .into();
                }
                let request_json = request.to_string();
                let original =
                    create_with_account_xpub(&request_json, &account_xpub(), &"01".repeat(32))
                        .unwrap();
                let signed = simulated_device_signature(&original);
                let finalized =
                    combine_hardware_signed(&request_json, &account_xpub(), &original, &signed)
                        .unwrap();
                Verifier::new(pczt::Pczt::parse(&finalized).unwrap())
                    .with_transparent::<KeysError, _>(|bundle| {
                        assert!(bundle.inputs()[0].script_sig().is_some());
                        Ok(())
                    })
                    .unwrap();
                let mut changed_request = request.clone();
                changed_request["expiryHeight"] = 3_500_021.into();
                let changed = simulated_device_signature(
                    &create(&changed_request.to_string(), &SEED).unwrap(),
                );
                assert!(combine_hardware_signed(
                    &request_json,
                    &account_xpub(),
                    &original,
                    &changed
                )
                .is_err());
                assert!(combine_hardware_signed(
                    &request_json,
                    &account_xpub(),
                    &original,
                    &original
                )
                .is_err());
            }
        }
    }

    fn mutate(bytes: &[u8], change: impl FnOnce(&mut Value)) -> Vec<u8> {
        let wire = pczt::v2::Pczt::try_from(pczt::Pczt::parse(bytes).unwrap()).unwrap();
        let mut value = serde_json::to_value(wire).unwrap();
        change(&mut value);
        serde_json::from_value::<pczt::v2::Pczt>(value)
            .unwrap()
            .serialize()
    }

    #[test]
    fn signs_fixed_and_max_shielding_with_matching_request() {
        for send_max in [false, true] {
            let request = request(send_max).to_string();
            let original = create(&request, &SEED).unwrap();
            // Proof generation is independent of the transparent signing authorization.
            sign(&request, &SEED, &original, &original).unwrap();
        }
    }

    #[test]
    fn rejects_substitution_of_a_different_approved_request() {
        let request = request(false);
        let original = create(&request.to_string(), &SEED).unwrap();
        for field in ["recipient", "amount", "input", "value", "change", "expiry"] {
            let mut changed = request.clone();
            match field {
                "recipient" => changed["recipients"][0]["address"] = recipient(1).into(),
                "amount" => changed["recipients"][0]["amountZat"] = "40000".into(),
                "input" => {
                    changed["utxos"][0]["vout"] = 1.into();
                    changed["selectedOutpoints"][0]["vout"] = 1.into();
                }
                "value" => changed["utxos"][0]["valueZat"] = "110000".into(),
                "change" => {
                    changed["change"]["address"] = transparent_address(1, 1)
                        .to_zcash_address(NetworkType::Main)
                        .to_string()
                        .into();
                    changed["change"]["derivationPath"] = "m/44'/133'/0'/1/1".into();
                }
                "expiry" => changed["expiryHeight"] = 3_500_021.into(),
                _ => unreachable!(),
            }
            assert!(
                sign(&changed.to_string(), &SEED, &original, &original).is_err(),
                "{field}"
            );
        }
    }

    #[test]
    fn rejects_modified_transparent_effects_and_sighash_policy() {
        let request = request(false).to_string();
        let original = create(&request, &SEED).unwrap();
        for field in ["prevout_index", "value", "sighash_type", "sequence"] {
            let modified = mutate(&original, |value| {
                value["transparent"]["inputs"][0][field] = match field {
                    "value" => 110_000.into(),
                    "sighash_type" => 2.into(),
                    "sequence" => 1.into(),
                    _ => 1.into(),
                };
            });
            assert!(
                sign(&request, &SEED, &modified, &modified).is_err(),
                "{field}"
            );
            assert!(
                sign(&request, &SEED, &original, &modified).is_err(),
                "prover {field}"
            );
        }
        let modified = mutate(&original, |value| {
            value["transparent"]["outputs"][0]["value"] = 1.into()
        });
        assert!(sign(&request, &SEED, &modified, &modified).is_err());
        assert!(sign(&request, &SEED, &original, &modified).is_err());
    }

    #[test]
    fn rejects_unreviewed_ironwood_actions_and_ciphertexts() {
        let request = request(false).to_string();
        let original = create(&request, &SEED).unwrap();
        for field in [
            "anchor",
            "value_sum",
            "extra_action",
            "real_spend",
            "output_value",
            "ciphertext",
            "ephemeral_key",
        ] {
            let modified = mutate(&original, |value| {
                let bundle = &mut value["ironwood"];
                match field {
                    "anchor" => bundle["anchor"] = json!([0; 32].to_vec()),
                    "value_sum" => bundle["value_sum"][0] = 1.into(),
                    "extra_action" => {
                        let action = bundle["actions"][0].clone();
                        bundle["actions"].as_array_mut().unwrap().push(action);
                    }
                    "real_spend" => bundle["actions"][0]["spend"]["value"] = 1.into(),
                    _ => {
                        let output = &mut bundle["actions"]
                            .as_array_mut()
                            .unwrap()
                            .iter_mut()
                            .find(|action| action["output"]["value"].as_u64().unwrap() != 0)
                            .unwrap()["output"];
                        match field {
                            "output_value" => output["value"] = 1.into(),
                            "ciphertext" => {
                                output["enc_ciphertext"] = json!({ "Encrypted": vec![0u8; 580] })
                            }
                            "ephemeral_key" => output["ephemeral_key"] = json!([0; 32].to_vec()),
                            _ => unreachable!(),
                        }
                    }
                }
            });
            assert!(
                sign(&request, &SEED, &modified, &modified).is_err(),
                "{field}"
            );
            if field != "real_spend" && field != "output_value" {
                assert!(
                    sign(&request, &SEED, &original, &modified).is_err(),
                    "prover {field}"
                );
            }
        }
    }

    #[test]
    fn account_xprv_has_the_same_authorization_checks() {
        let request = request(false).to_string();
        let key = AccountPrivKey::from_seed(&MAIN_NETWORK, &SEED, AccountId::ZERO).unwrap();
        let mut encoded = XPRV_VERSION.to_vec();
        encoded.extend_from_slice(&key.to_bytes());
        let xprv = bs58::encode(encoded).with_check().into_string();
        let original = create_with_account_xprv(&request, &xprv).unwrap();
        sign_with_account_xprv(&request, &xprv, &original, &original).unwrap();
        let mut changed: Value = serde_json::from_str(&request).unwrap();
        changed["recipients"][0]["address"] = recipient(1).into();
        assert!(sign_with_account_xprv(&changed.to_string(), &xprv, &original, &original).is_err());
    }

    #[test]
    fn validates_every_output_in_a_mixed_recipient_transaction() {
        let mut request = request(false);
        request["recipients"] = json!([
            { "address": recipient(0), "amountZat": "10000" },
            { "address": recipient(0), "amountZat": "10000" },
            { "address": recipient(1), "amountZat": "10000" },
            { "address": transparent_address(0, 2).to_zcash_address(NetworkType::Main).to_string(), "amountZat": "10000" },
        ]);
        let request = request.to_string();
        let original = create(&request, &SEED).unwrap();
        sign(&request, &SEED, &original, &original).unwrap();
        let modified = mutate(&original, |value| {
            let output = value["transparent"]["outputs"][0].clone();
            value["transparent"]["outputs"]
                .as_array_mut()
                .unwrap()
                .push(output);
        });
        assert!(sign(&request, &SEED, &modified, &modified).is_err());
    }
}
