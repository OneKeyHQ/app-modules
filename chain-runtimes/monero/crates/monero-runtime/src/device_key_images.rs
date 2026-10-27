//! Key image sync for hardware accounts, whose spend key stays on the device.
//!
//! The device derives each output's key image from the exported keys and proves it
//! with a one-member ring signature by the output key (as wallet2 `import_key_images`);
//! only verified images are stored.

use std::collections::HashSet;

use curve25519_dalek::{constants::ED25519_BASEPOINT_POINT, EdwardsPoint};
use monero_wallet::{
    address::SubaddressIndex,
    ed25519::{CompressedPoint, Scalar},
    io::VarInt,
    ring_signatures::RingSignature,
};
use rusqlite::OptionalExtension;
use serde_json::{json, Value};
use zeroize::Zeroizing;

use crate::error::{db_err, ErrorCode, Result, RuntimeError};
use crate::spend::{apply_key_images, KeyImageImport};
use crate::wallet::{Db, Identity};

/// `(id, index in tx, output key, R keys, additional keys, subaddress major, minor)`.
type SyncRow = (
    i64,
    i64,
    Option<Vec<u8>>,
    Option<Vec<u8>>,
    Option<Vec<u8>>,
    Option<i64>,
    Option<i64>,
);

/// Outputs still lacking a key image, with the keys a device needs to derive them.
pub fn export_sync_data(db: &Db, identity_id: i64) -> Result<Value> {
    let identity = db.identity_by_id(identity_id)?;
    let view = view_scalar(db, identity_id)?;

    let rows: Vec<SyncRow> = {
        let mut stmt = db
            .conn()
            .prepare(
                "SELECT id, index_in_tx, output_key, tx_pub_keys, tx_additional_pub_keys,
                        subaddress_major, subaddress_minor
                 FROM outputs
                 WHERE identity_id = ?1 AND key_image IS NULL AND scan_valid = 1
                 ORDER BY id",
            )
            .map_err(|e| db_err("listKeyImageSyncOutputs", e))?;
        let rows = stmt
            .query_map([identity_id], |r| {
                Ok((
                    r.get(0)?,
                    r.get(1)?,
                    r.get(2)?,
                    r.get(3)?,
                    r.get(4)?,
                    r.get(5)?,
                    r.get(6)?,
                ))
            })
            .map_err(|e| db_err("listKeyImageSyncOutputs", e))?;
        rows.collect::<rusqlite::Result<_>>()
            .map_err(|e| db_err("listKeyImageSyncOutputs", e))?
    };

    let mut items = Vec::with_capacity(rows.len());
    for (id, index, output_key, tx_keys, additional, major, minor) in rows {
        let corrupt = |what: &str| db_err("exportKeyImageSyncData", format!("output {id}: {what}"));
        let output_key = output_key
            .and_then(|k| <[u8; 32]>::try_from(k).ok())
            .ok_or_else(|| corrupt("no output key"))?;
        let tx_keys = tx_keys
            .filter(|k| !k.is_empty() && k.len() % 32 == 0)
            .ok_or_else(|| corrupt("no transaction public key"))?;
        let index = u32::try_from(index).map_err(|_| corrupt("output index out of range"))?;
        let major = u32::try_from(major.unwrap_or(0)).map_err(|_| corrupt("bad subaddress"))?;
        let minor = u32::try_from(minor.unwrap_or(0)).map_err(|_| corrupt("bad subaddress"))?;

        let spend = receiving_spend_key(db, &identity, major, minor);
        let (tx_pub_key, additional) = real_tx_keys(
            &view,
            spend,
            &tx_keys,
            additional.as_deref(),
            index,
            &output_key,
        );

        items.push(json!({
            "outputId": id,
            "outKey": hex::encode(output_key),
            "txPubKey": hex::encode(tx_pub_key),
            "additionalTxPubKey": additional.map(hex::encode),
            "internalOutputIndex": index,
            "subAddrMajor": major,
            "subAddrMinor": minor,
        }));
    }
    Ok(json!({ "items": items }))
}

/// The R a device derives output `index` from, and that output's additional key if any.
///
/// Among several R the one deriving `output_key` is chosen; when none does, the output
/// was received via its additional key and the first R is returned.
pub(crate) fn real_tx_keys<'a>(
    view: &curve25519_dalek::Scalar,
    spend: EdwardsPoint,
    tx_keys: &'a [u8],
    additional: Option<&'a [u8]>,
    index: u32,
    output_key: &[u8; 32],
) -> (&'a [u8], Option<&'a [u8]>) {
    let first = &tx_keys[..32];
    let tx_pub_key = if tx_keys.len() == 32 {
        first
    } else {
        tx_keys
            .chunks_exact(32)
            .find(|r| derives_output_key(view, r, index, spend, output_key))
            .unwrap_or(first)
    };
    let additional = additional.and_then(|a| a.chunks_exact(32).nth(index as usize));
    (tx_pub_key, additional)
}

pub(crate) fn view_scalar(
    db: &Db,
    identity_id: i64,
) -> Result<Zeroizing<curve25519_dalek::Scalar>> {
    let bytes: Zeroizing<Vec<u8>> = Zeroizing::new(
        db.conn()
            .query_row(
                "SELECT view_private_key FROM identities WHERE id = ?1",
                [identity_id],
                |r| r.get(0),
            )
            .map_err(|e| db_err("loadViewKey", e))?,
    );
    let scalar = Scalar::read(&mut bytes.as_slice()).map_err(|e| db_err("loadViewKey", e))?;
    Ok(Zeroizing::new(scalar.into()))
}

pub(crate) fn receiving_spend_key(
    db: &Db,
    identity: &Identity,
    major: u32,
    minor: u32,
) -> EdwardsPoint {
    match SubaddressIndex::new(major, minor) {
        Some(index) => identity
            .view_pair
            .subaddress(db.network(), index)
            .spend()
            .into(),
        None => identity.view_pair.spend().into(),
    }
}

/// Whether `Hs(8·a·R || index)·G + spend` is the output key.
pub(crate) fn derives_output_key(
    view: &curve25519_dalek::Scalar,
    r: &[u8],
    index: u32,
    spend: EdwardsPoint,
    output_key: &[u8; 32],
) -> bool {
    let Some(r) = <[u8; 32]>::try_from(r)
        .ok()
        .and_then(|r| CompressedPoint::from(r).decompress())
    else {
        return false;
    };
    let mut derivation = Zeroizing::new(
        (view * r.into())
            .mul_by_cofactor()
            .compress()
            .to_bytes()
            .to_vec(),
    );
    VarInt::write(&u64::from(index), &mut *derivation).expect("writing to a Vec cannot fail");
    let shared: curve25519_dalek::Scalar = Scalar::hash(&*derivation).into();
    (shared * ED25519_BASEPOINT_POINT + spend)
        .compress()
        .to_bytes()
        == *output_key
}

/// `(output key, key image, txid, index in tx, scan valid)`.
type ImportRow = (Option<Vec<u8>>, Option<Vec<u8>>, Vec<u8>, i64, bool);

#[derive(serde::Deserialize)]
#[serde(rename_all = "camelCase")]
struct DeviceKeyImage {
    output_id: i64,
    key_image: String,
    /// `c || r`, 64 bytes.
    signature: String,
}

/// Stores device-computed key images once every item verifies; one bad item imports nothing.
///
/// Items whose output already has the same key image are skipped.
pub fn import_verified(db: &mut Db, identity_id: i64, items_json: &str) -> Result<KeyImageImport> {
    db.identity_by_id(identity_id)?;
    let items: Vec<DeviceKeyImage> = serde_json::from_str(items_json).map_err(|e| {
        RuntimeError::with(ErrorCode::InvalidRequest, json!({ "field": "itemsJson" })).detail(e)
    })?;

    let mut verified = Vec::with_capacity(items.len());
    let mut seen = HashSet::new();
    for item in &items {
        let id = item.output_id;
        let key_image: [u8; 32] =
            decode(&item.key_image).ok_or_else(|| rejected(id, "malformedKeyImage"))?;
        let signature: [u8; 64] =
            decode(&item.signature).ok_or_else(|| rejected(id, "malformedSignature"))?;

        let row: Option<ImportRow> = db
            .conn()
            .query_row(
                "SELECT output_key, key_image, txid, index_in_tx, scan_valid FROM outputs
                 WHERE id = ?1 AND identity_id = ?2",
                [id, identity_id],
                |r| Ok((r.get(0)?, r.get(1)?, r.get(2)?, r.get(3)?, r.get(4)?)),
            )
            .optional()
            .map_err(|e| db_err("loadKeyImageOutput", e))?;
        let Some((output_key, existing, txid, index, scan_valid)) = row else {
            return Err(rejected(id, "outputNotFound"));
        };
        if !scan_valid {
            return Err(rejected(id, "outputNotScanValid"));
        }
        let output_key = output_key
            .and_then(|k| <[u8; 32]>::try_from(k).ok())
            .ok_or_else(|| {
                db_err(
                    "importDeviceKeyImages",
                    format!("output {id}: no output key"),
                )
            })?;
        if !signature_verifies(&key_image, &output_key, &signature) {
            return Err(rejected(id, "badSignature"));
        }
        match existing {
            Some(stored) if stored == key_image => continue,
            Some(_) => return Err(rejected(id, "keyImageConflict")),
            None => {}
        }
        // A valid signature fixes the image, so a repeated id carries the same one.
        if seen.insert(id) {
            verified.push((id, key_image, txid, index));
        }
    }

    apply_key_images(db, identity_id, &verified)
}

/// One-member ring signature by `output_key` over the key image itself (wallet2's message).
fn signature_verifies(key_image: &[u8; 32], output_key: &[u8; 32], signature: &[u8; 64]) -> bool {
    // Non-canonical scalars fail to read.
    let Ok(signature) = RingSignature::read(1, &mut signature.as_slice()) else {
        return false;
    };
    signature.verify(
        key_image,
        &[CompressedPoint::from(*output_key)],
        &CompressedPoint::from(*key_image),
    )
}

fn decode<const N: usize>(value: &str) -> Option<[u8; N]> {
    let mut out = [0u8; N];
    hex::decode_to_slice(value, &mut out).ok()?;
    Some(out)
}

fn rejected(output_id: i64, reason: &str) -> RuntimeError {
    RuntimeError::with(
        ErrorCode::InvalidKeyImage,
        json!({ "outputId": output_id, "reason": reason }),
    )
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::wallet::testing::{db_with_identity, key_bytes};
    use curve25519_dalek::Scalar as Dalek;
    use monero_wallet::ed25519::Point;
    use rusqlite::params;

    const G: EdwardsPoint = ED25519_BASEPOINT_POINT;

    /// The test identity's private view scalar and public spend key.
    fn identity_keys() -> (Dalek, EdwardsPoint) {
        let (spend, view) = key_bytes();
        let spend = CompressedPoint::from(spend).decompress().unwrap().into();
        (Dalek::from_canonical_bytes(view).unwrap(), spend)
    }

    /// Sender side: the key of output `index` paid with secret `r` to (view, spend) public keys.
    fn sent_output_key(r: Dalek, view: EdwardsPoint, spend: EdwardsPoint, index: u64) -> [u8; 32] {
        let mut derivation = (r * view).mul_by_cofactor().compress().to_bytes().to_vec();
        VarInt::write(&index, &mut derivation).unwrap();
        let shared: Dalek = Scalar::hash(&derivation).into();
        (shared * G + spend).compress().to_bytes()
    }

    fn concat(points: &[EdwardsPoint]) -> Vec<u8> {
        points
            .iter()
            .flat_map(|p| p.compress().to_bytes())
            .collect()
    }

    fn insert_output(
        db: &Db,
        identity_id: i64,
        txid: u8,
        index: i64,
        output_key: [u8; 32],
        tx_keys: &[EdwardsPoint],
        additional: Option<&[EdwardsPoint]>,
    ) -> i64 {
        db.conn()
            .execute(
                "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                   amount_atomic, height, tx_pub_keys, tx_additional_pub_keys, output_key)
                 VALUES (?1, x'00', ?2, 7, ?3, 1000, 10, ?4, ?5, ?6)",
                params![
                    identity_id,
                    vec![txid; 32],
                    index,
                    concat(tx_keys),
                    additional.map(concat),
                    output_key.to_vec()
                ],
            )
            .unwrap();
        db.conn().last_insert_rowid()
    }

    fn set_subaddress(db: &Db, id: i64, major: u32, minor: u32) {
        db.conn()
            .execute(
                "UPDATE outputs SET subaddress_major = ?1, subaddress_minor = ?2 WHERE id = ?3",
                params![major, minor, id],
            )
            .unwrap();
    }

    fn subaddress_spend(db: &Db, identity_id: i64, minor: u32) -> EdwardsPoint {
        let identity = db.identity_by_id(identity_id).unwrap();
        receiving_spend_key(db, &identity, 0, minor)
    }

    fn hex_of(point: EdwardsPoint) -> String {
        hex::encode(point.compress().to_bytes())
    }

    fn export(db: &Db, id: i64) -> Vec<Value> {
        export_sync_data(db, id).unwrap()["items"]
            .as_array()
            .unwrap()
            .clone()
    }

    /// Among several R, the one that derives the output key is exported, not the first or last.
    #[test]
    fn export_picks_the_tx_public_key_that_derives_the_output() {
        let (db, id) = db_with_identity();
        let (view, spend) = identity_keys();
        let r: Vec<Dalek> = [11u64, 22, 33].map(Dalek::from).to_vec();

        let primary_keys: Vec<EdwardsPoint> = r.iter().map(|r| r * G).collect();
        let key = sent_output_key(r[1], view * G, spend, 1);
        let primary = insert_output(&db, id, 1, 1, key, &primary_keys, None);

        // To a subaddress D the sender uses R = r·D and pays the view key a·D.
        let d = subaddress_spend(&db, id, 3);
        let sub_keys: Vec<EdwardsPoint> = r.iter().map(|r| r * d).collect();
        let key = sent_output_key(r[1], view * d, d, 0);
        let sub = insert_output(&db, id, 2, 0, key, &sub_keys, None);
        set_subaddress(&db, sub, 0, 3);

        let items = export(&db, id);
        assert_eq!(items.len(), 2);
        assert_eq!(items[0]["outputId"], primary);
        assert_eq!(items[0]["txPubKey"], hex_of(primary_keys[1]));
        assert_eq!(items[0]["internalOutputIndex"], 1);
        assert_eq!(items[0]["additionalTxPubKey"], Value::Null);
        assert_eq!(items[0]["subAddrMajor"], 0);
        assert_eq!(items[0]["subAddrMinor"], 0);
        assert_eq!(items[1]["outputId"], sub);
        assert_eq!(items[1]["txPubKey"], hex_of(sub_keys[1]));
        assert_eq!(items[1]["subAddrMinor"], 3);
    }

    /// An output paid via its additional key exports that key, with the first R as `txPubKey`.
    #[test]
    fn export_includes_the_additional_key_and_skips_resolved_outputs() {
        let (db, id) = db_with_identity();
        let (view, _) = identity_keys();
        let d = subaddress_spend(&db, id, 2);
        let tx_keys = [Dalek::from(5u64) * G, Dalek::from(6u64) * G];
        let k: Vec<Dalek> = [41u64, 42, 43].map(Dalek::from).to_vec();
        let additional: Vec<EdwardsPoint> = k.iter().map(|k| k * d).collect();
        let key = sent_output_key(k[2], view * d, d, 2);
        let out = insert_output(&db, id, 1, 2, key, &tx_keys, Some(&additional));
        set_subaddress(&db, out, 0, 2);

        let imaged = insert_output(&db, id, 2, 0, [9; 32], &tx_keys[..1], None);
        db.conn()
            .execute(
                "UPDATE outputs SET key_image = ?1 WHERE id = ?2",
                params![vec![1u8; 32], imaged],
            )
            .unwrap();
        let invalid = insert_output(&db, id, 3, 0, [8; 32], &tx_keys[..1], None);
        db.conn()
            .execute("UPDATE outputs SET scan_valid = 0 WHERE id = ?1", [invalid])
            .unwrap();

        let items = export(&db, id);
        assert_eq!(
            items.len(),
            1,
            "imaged and unverified outputs are not exported"
        );
        assert_eq!(items[0]["outputId"], out);
        assert_eq!(items[0]["outKey"], hex::encode(key));
        assert_eq!(items[0]["txPubKey"], hex_of(tx_keys[0]));
        assert_eq!(items[0]["additionalTxPubKey"], hex_of(additional[2]));
        assert_eq!(items[0]["subAddrMinor"], 2);
    }

    /// `(output key, key image, c || r)`: a key image and its one-member ring signature
    /// for the output with secret `x`, following Monero's `generate_ring_signature`.
    fn device_sign(x: Dalek, nonce: Dalek) -> ([u8; 32], [u8; 32], [u8; 64]) {
        let output_key = (x * G).compress().to_bytes();
        let hp: EdwardsPoint = Point::biased_hash(output_key).into();
        let image = (x * hp).compress().to_bytes();
        let mut transcript = image.to_vec();
        transcript.extend_from_slice((nonce * G).compress().as_bytes());
        transcript.extend_from_slice((nonce * hp).compress().as_bytes());
        let c: Dalek = Scalar::hash(&transcript).into();
        let r = nonce - c * x;
        let mut signature = [0u8; 64];
        signature[..32].copy_from_slice(c.as_bytes());
        signature[32..].copy_from_slice(r.as_bytes());
        (output_key, image, signature)
    }

    fn item(id: i64, image: &[u8], signature: &[u8]) -> Value {
        json!({ "outputId": id, "keyImage": hex::encode(image), "signature": hex::encode(signature) })
    }

    fn key_images(db: &Db) -> Vec<Option<Vec<u8>>> {
        let mut stmt = db
            .conn()
            .prepare("SELECT key_image FROM outputs ORDER BY id")
            .unwrap();
        stmt.query_map([], |r| r.get(0))
            .unwrap()
            .collect::<rusqlite::Result<_>>()
            .unwrap()
    }

    fn rejection(db: &mut Db, id: i64, items: Value) -> RuntimeError {
        match import_verified(db, id, &items.to_string()) {
            Ok(_) => panic!("import must be rejected"),
            Err(e) => e,
        }
    }

    /// `(output id, key image, signature)`.
    type SignedOutput = (i64, [u8; 32], [u8; 64]);

    /// Two device-signed outputs; the first has a possible spend with its real key image.
    fn db_with_signed_outputs() -> (Db, i64, [SignedOutput; 2]) {
        let (db, id) = db_with_identity();
        let signed = [(101u64, 7u64, 1u8), (202, 8, 2)].map(|(x, nonce, txid)| {
            let (key, image, signature) = device_sign(Dalek::from(x), Dalek::from(nonce));
            let out = insert_output(&db, id, txid, 0, key, &[G], None);
            (out, image, signature)
        });
        db.conn()
            .execute(
                "INSERT INTO possible_spends
                   (identity_id, out_txid, out_index, key_image, spend_txid, height, timestamp)
                 VALUES (?1, ?2, 0, ?3, ?4, 40, 1700000000), (?1, ?2, 0, ?5, ?6, 41, 1700000000)",
                params![
                    id,
                    vec![1u8; 32],
                    signed[0].1.to_vec(),
                    vec![0x51u8; 32],
                    vec![0xEEu8; 32],
                    vec![0x52u8; 32]
                ],
            )
            .unwrap();
        (db, id, signed)
    }

    #[test]
    fn a_device_key_image_with_a_bad_signature_rejects_the_whole_import() {
        let (mut db, id, [(a, a_image, a_sig), (b, b_image, mut b_sig)]) = db_with_signed_outputs();
        b_sig[0] ^= 1;

        let e = rejection(
            &mut db,
            id,
            json!([item(a, &a_image, &a_sig), item(b, &b_image, &b_sig)]),
        );
        assert_eq!(e.code, ErrorCode::InvalidKeyImage);
        assert_eq!(e.params, json!({ "outputId": b, "reason": "badSignature" }));
        assert_eq!(key_images(&db), vec![None, None], "nothing is written");
        let pending: i64 = db
            .conn()
            .query_row("SELECT count(*) FROM possible_spends", [], |r| r.get(0))
            .unwrap();
        assert_eq!(pending, 2);

        // A valid signature for another output's key does not prove this output's image.
        let e = rejection(&mut db, id, json!([item(b, &a_image, &a_sig)]));
        assert_eq!(e.params["reason"], "badSignature");
        let e = rejection(&mut db, id, json!([item(b, &b_image, &a_sig[..63])]));
        assert_eq!(e.params["reason"], "malformedSignature");
    }

    #[test]
    fn verified_device_key_images_are_stored_and_confirm_an_earlier_spend() {
        let (mut db, id, [(a, a_image, a_sig), (b, b_image, b_sig)]) = db_with_signed_outputs();
        let items = json!([item(a, &a_image, &a_sig), item(b, &b_image, &b_sig)]).to_string();

        let result = import_verified(&mut db, id, &items).unwrap();
        assert_eq!((result.imported, result.spends_found), (2, 1));
        assert_eq!(
            key_images(&db),
            vec![Some(a_image.to_vec()), Some(b_image.to_vec())]
        );
        let (height, txid): (Option<i64>, Option<Vec<u8>>) = db
            .conn()
            .query_row(
                "SELECT spent_at_height, spent_in_txid FROM outputs WHERE id = ?1",
                [a],
                |r| Ok((r.get(0)?, r.get(1)?)),
            )
            .unwrap();
        assert_eq!((height, txid), (Some(40), Some(vec![0x51; 32])));
        let direction: String = db
            .conn()
            .query_row(
                "SELECT direction FROM transactions WHERE txid = ?1",
                [vec![0x51u8; 32]],
                |r| r.get(0),
            )
            .unwrap();
        assert_eq!(direction, "out");
        assert!(export(&db, id).is_empty());

        let again = import_verified(&mut db, id, &items).unwrap();
        assert_eq!(
            (again.imported, again.spends_found),
            (0, 0),
            "already stored"
        );
    }

    #[test]
    fn device_key_images_for_foreign_unverified_or_conflicting_outputs_are_refused() {
        let (mut db, id, [(a, a_image, a_sig), (b, b_image, b_sig)]) = db_with_signed_outputs();

        let derived = crate::keys::derive_from_bip32_key(db.network(), &[5; 32]).unwrap();
        let other = db
            .add_identity(
                "acct-2",
                &derived.spend_public_key,
                &derived.view_private_key,
                100,
            )
            .unwrap()
            .id;
        let e = rejection(&mut db, other, json!([item(a, &a_image, &a_sig)]));
        assert_eq!(
            e.params,
            json!({ "outputId": a, "reason": "outputNotFound" })
        );

        db.conn()
            .execute("UPDATE outputs SET scan_valid = 0 WHERE id = ?1", [a])
            .unwrap();
        let e = rejection(&mut db, id, json!([item(a, &a_image, &a_sig)]));
        assert_eq!(e.params["reason"], "outputNotScanValid");

        db.conn()
            .execute(
                "UPDATE outputs SET key_image = ?1 WHERE id = ?2",
                params![vec![3u8; 32], b],
            )
            .unwrap();
        let e = rejection(&mut db, id, json!([item(b, &b_image, &b_sig)]));
        assert_eq!(e.params["reason"], "keyImageConflict");

        let e = rejection(&mut db, id, json!({ "outputId": a }));
        assert_eq!(e.code, ErrorCode::InvalidRequest);
    }
}
