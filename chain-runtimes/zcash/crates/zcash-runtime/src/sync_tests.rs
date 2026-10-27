//! Offline regression tests through the production scanner and wallet database.
//! Deterministic synthetic chain, generated in memory without network, files or
//! proving parameters. The layout and compact-note construction are adapted from
//! the upstream TestBuilder experiment that produced the former binary fixtures.
//! There are 501 blocks, three accounts, both Orchard and Ironwood receives and
//! spends, a completed shard in each pool (including Sapling), and a 35-block fork.

use super::*;
use std::collections::BTreeMap;
use std::sync::OnceLock;

use incrementalmerkletree::{frontier::Frontier, Hashable};
use orchard::{
    keys::FullViewingKey,
    note::{ExtractedNoteCommitment, NoteVersion, Nullifier, RandomSeed, Rho},
    note_encryption::{
        IronwoodDomain, IronwoodNoteEncryption, OrchardDomain, OrchardNoteEncryption,
    },
    tree::MerkleHashOrchard,
};
use rand::{RngCore, SeedableRng};
use rand_chacha::ChaCha20Rng;
use zcash_client_backend::data_api::{Account, AccountBirthday, AccountPurpose};
use zcash_client_backend::proto::compact_formats::{
    ChainMetadata, CompactBlock, CompactOrchardAction, CompactSaplingOutput, CompactTx,
};
use zcash_client_sqlite::error::SqliteClientError;
use zcash_keys::keys::UnifiedSpendingKey;
use zcash_note_encryption::Domain;
use zcash_primitives::block::BlockHash;
use zcash_primitives::merkle_tree::read_frontier_v1;
use zcash_protocol::consensus::Network;
use zip32::Scope;

fn height(h: u32) -> BlockHeight {
    BlockHeight::from_u32(h)
}

fn range(start: u32, end: u32, priority: ScanPriority) -> ScanRange {
    ScanRange::from_parts(height(start)..height(end), priority)
}

struct Fixture {
    birthday: u32,
    tip: u32,
    keys: Vec<String>,
    sapling: Vec<CommitmentTreeRoot<sapling::Node>>,
    orchard: Vec<CommitmentTreeRoot<orchard::tree::MerkleHashOrchard>>,
    ironwood: Vec<CommitmentTreeRoot<orchard::tree::MerkleHashOrchard>>,
    states: BTreeMap<u32, ChainState>,
    blocks: BTreeMap<u32, CompactBlock>,
}

const SHARD_SIZE: u32 = 1 << 16;

fn random_hash<H: HashSer>(rng: &mut ChaCha20Rng) -> H {
    loop {
        let mut bytes = [0; 32];
        rng.fill_bytes(&mut bytes);
        if let Ok(hash) = H::read(&bytes[..]) {
            return hash;
        }
    }
}

/// Like TestBuilder's random_with_prior_subtree_roots, construct a pruned
/// frontier with distinct completed roots and matching high-level ommers.
/// Only O(depth + completed shards) hashes are needed for the historical tree.
fn initial_tree<H: Hashable + HashSer + Clone>(
    rng: &mut ChaCha20Rng,
    size: u32,
    end: u32,
) -> (Frontier<H, 32>, Vec<CommitmentTreeRoot<H>>) {
    let roots: Vec<H> = (0..size / SHARD_SIZE).map(|_| random_hash(rng)).collect();
    let mut completed: Vec<(u8, H)> = Vec::new();
    for root in &roots {
        let (mut level, mut node) = (16, root.clone());
        while completed.last().is_some_and(|(l, _)| *l == level) {
            let (_, left) = completed.pop().unwrap();
            node = H::combine(level.into(), &left, &node);
            level += 1;
        }
        completed.push((level, node));
    }
    let position = size - 1;
    let tree = Frontier::from_parts(
        u64::from(position).into(),
        random_hash(rng),
        (0..32)
            .filter(|level| position & (1 << level) != 0)
            .map(|level| {
                if level < 16 {
                    random_hash(rng)
                } else {
                    completed
                        .iter()
                        .find(|(l, _)| *l == level)
                        .unwrap()
                        .1
                        .clone()
                }
            })
            .collect(),
    )
    .unwrap();
    (
        tree,
        roots
            .into_iter()
            .map(|r| CommitmentTreeRoot::from_parts(height(end), r))
            .collect(),
    )
}

fn append<H: Hashable + Clone>(
    tree: &mut Frontier<H, 32>,
    roots: &mut Vec<CommitmentTreeRoot<H>>,
    leaf: H,
    h: u32,
) {
    assert!(tree.append(leaf));
    if tree.tree_size() % u64::from(SHARD_SIZE) == 0 {
        roots.push(CommitmentTreeRoot::from_parts(
            height(h),
            tree.value().unwrap().root(Some(16.into())),
        ));
    }
}

#[derive(Clone, Copy)]
enum Pool {
    Orchard,
    Ironwood,
}

struct ChainBuilder {
    rng: ChaCha20Rng,
    f: Fixture,
    // A, B, C and an unowned filler recipient.
    fvks: Vec<FullViewingKey>,
}

impl ChainBuilder {
    fn new() -> Self {
        let birthday = 4_135_000;
        let mut rng = ChaCha20Rng::from_seed([0; 32]);
        let usks: Vec<_> = [(0, 0), (0, 1), (9, 0), (0xf5, 0)]
            .into_iter()
            .map(|(seed, account)| {
                UnifiedSpendingKey::from_seed(
                    &Network::TestNetwork,
                    &[seed; 32],
                    zip32::AccountId::try_from(account).unwrap(),
                )
                .unwrap()
            })
            .collect();
        let (sapling_tree, sapling) = initial_tree(&mut rng, 2 * SHARD_SIZE - 2, birthday - 500);
        let (orchard_tree, orchard) = initial_tree(&mut rng, 3 * SHARD_SIZE - 200, birthday - 500);
        let (ironwood_tree, ironwood) =
            initial_tree(&mut rng, 3 * SHARD_SIZE - 300, birthday - 300);
        let prior = ChainState::new(
            height(birthday - 1),
            BlockHash([5; 32]),
            sapling_tree,
            orchard_tree,
            ironwood_tree,
        );
        Self {
            rng,
            f: Fixture {
                birthday,
                tip: birthday - 1,
                keys: usks[..3]
                    .iter()
                    .map(|k| {
                        k.to_unified_full_viewing_key()
                            .encode(&Network::TestNetwork)
                    })
                    .collect(),
                sapling,
                orchard,
                ironwood,
                states: BTreeMap::from([(birthday - 1, prior)]),
                blocks: BTreeMap::new(),
            },
            fvks: usks
                .iter()
                .map(|k| FullViewingKey::from(k.orchard()))
                .collect(),
        }
    }

    fn random_bytes(&mut self) -> [u8; 32] {
        let mut bytes = [0; 32];
        self.rng.fill_bytes(&mut bytes);
        bytes
    }

    fn random_nf(&mut self) -> Nullifier {
        loop {
            if let Some(nf) = Option::from(Nullifier::from_bytes(&self.random_bytes())) {
                return nf;
            }
        }
    }

    /// Mirrors TestBuilder's compact Orchard / Ironwood action construction,
    /// using the matching V2 / V3 note-encryption domain and a real nullifier.
    fn action(
        &mut self,
        pool: Pool,
        key: usize,
        scope: Scope,
        value: u64,
        nf_old: Nullifier,
    ) -> (CompactOrchardAction, Nullifier) {
        let rho = Rho::from_bytes(&nf_old.to_bytes()).unwrap();
        let rseed = loop {
            if let Some(seed) = Option::from(RandomSeed::from_bytes(self.random_bytes(), &rho)) {
                break seed;
            }
        };
        let note = orchard::Note::from_parts(
            self.fvks[key].address_at(0u32, scope),
            orchard::value::NoteValue::from_raw(value),
            rho,
            rseed,
            match pool {
                Pool::Orchard => NoteVersion::V2,
                Pool::Ironwood => NoteVersion::V3,
            },
        )
        .unwrap();
        let (ephemeral_key, ciphertext) = match pool {
            Pool::Orchard => {
                let enc = OrchardNoteEncryption::new(None, note, [0; 512]);
                (
                    OrchardDomain::epk_bytes(enc.epk()),
                    enc.encrypt_note_plaintext(),
                )
            }
            Pool::Ironwood => {
                let enc = IronwoodNoteEncryption::new(None, note, [0; 512]);
                (
                    IronwoodDomain::epk_bytes(enc.epk()),
                    enc.encrypt_note_plaintext(),
                )
            }
        };
        (
            CompactOrchardAction {
                nullifier: nf_old.to_bytes().to_vec(),
                cmx: ExtractedNoteCommitment::from(note.commitment())
                    .to_bytes()
                    .to_vec(),
                ephemeral_key: ephemeral_key.0.to_vec(),
                ciphertext: ciphertext[..52].to_vec(),
            },
            note.nullifier(&self.fvks[key]),
        )
    }

    fn push(&mut self, tx: CompactTx) {
        let h = self.f.tip + 1;
        let prior = &self.f.states[&self.f.tip];
        let mut sapling = prior.final_sapling_tree().clone();
        let mut orchard = prior.final_orchard_tree().clone();
        let mut ironwood = prior.final_ironwood_tree().clone();
        for output in &tx.outputs {
            append(
                &mut sapling,
                &mut self.f.sapling,
                sapling::Node::from_cmu(&output.cmu().unwrap()),
                h,
            );
        }
        for (actions, tree, roots) in [
            (&tx.actions, &mut orchard, &mut self.f.orchard),
            (&tx.ironwood_actions, &mut ironwood, &mut self.f.ironwood),
        ] {
            for action in actions {
                append(
                    tree,
                    roots,
                    MerkleHashOrchard::from_cmx(&action.cmx().unwrap()),
                    h,
                );
            }
        }
        let metadata = ChainMetadata {
            sapling_commitment_tree_size: sapling.tree_size() as u32,
            orchard_commitment_tree_size: orchard.tree_size() as u32,
            ironwood_commitment_tree_size: ironwood.tree_size() as u32,
        };
        let prev_hash = prior.block_hash().0.to_vec();
        let hash = self.random_bytes();
        self.f.blocks.insert(
            h,
            CompactBlock {
                height: u64::from(h),
                hash: hash.to_vec(),
                prev_hash,
                vtx: vec![tx],
                chain_metadata: Some(metadata),
                ..Default::default()
            },
        );
        self.f.states.insert(
            h,
            ChainState::new(height(h), BlockHash(hash), sapling, orchard, ironwood),
        );
        self.f.tip = h;
    }

    fn transaction(&mut self, pool: Pool, actions: Vec<CompactOrchardAction>) {
        let mut tx = CompactTx {
            txid: self.random_bytes().to_vec(),
            ..Default::default()
        };
        match pool {
            Pool::Orchard => tx.actions = actions,
            Pool::Ironwood => tx.ironwood_actions = actions,
        }
        self.push(tx);
    }

    fn receive(&mut self, pool: Pool, key: usize, value: u64) -> (Nullifier, u64) {
        let nf = self.random_nf();
        let (action, nf) = self.action(pool, key, Scope::External, value, nf);
        self.transaction(pool, vec![action]);
        (nf, value)
    }

    fn spend(&mut self, pool: Pool, key: usize, (nf, value): (Nullifier, u64), pay: u64) {
        let (change, _) = self.action(pool, key, Scope::Internal, value - pay, nf);
        let nf = self.random_nf();
        // TestBuilder's payment receiver is a UA with an Orchard receiver, even
        // when the spent note and its change belong to Ironwood.
        let (payment, _) = self.action(Pool::Orchard, 3, Scope::External, pay, nf);
        let mut tx = CompactTx {
            txid: self.random_bytes().to_vec(),
            actions: vec![payment],
            ..Default::default()
        };
        match pool {
            Pool::Orchard => tx.actions.insert(0, change),
            Pool::Ironwood => tx.ironwood_actions.push(change),
        }
        self.push(tx);
    }

    fn filler_to(&mut self, offset: u32) {
        while self.f.tip + 1 < self.f.birthday + offset {
            let pool = if (self.f.tip + 1) % 3 == 0 {
                Pool::Orchard
            } else {
                Pool::Ironwood
            };
            self.receive(pool, 3, 1000);
        }
    }

    /// Put owned notes immediately before and after the next shard boundary.
    fn fill(&mut self, pool: Pool, marks: &[(i32, usize, u64)]) -> Vec<(Nullifier, u64)> {
        let state = &self.f.states[&self.f.tip];
        let start = match pool {
            Pool::Orchard => state.final_orchard_tree().tree_size() as u32,
            Pool::Ironwood => state.final_ironwood_tree().tree_size() as u32,
        };
        let boundary = (start / SHARD_SIZE + 1) * SHARD_SIZE;
        let mut actions = Vec::new();
        let mut notes = Vec::new();
        for position in start..boundary + 5 {
            let mark = marks
                .iter()
                .find(|m| i64::from(boundary) + i64::from(m.0) == i64::from(position));
            let (_, key, value) = mark.copied().unwrap_or((0, 3, 1000));
            let nf = self.random_nf();
            let (action, nf) = self.action(pool, key, Scope::External, value, nf);
            actions.push(action);
            if mark.is_some() {
                notes.push((nf, value));
            }
        }
        self.transaction(pool, actions);
        notes
    }

    fn build(mut self) -> Self {
        use Pool::{Ironwood as I, Orchard as O};
        self.receive(O, 3, 1000); // birthday + 0
        self.receive(O, 3, 1000);
        let a_i110 = self.receive(I, 0, 110_000);
        let a_o100 = self.receive(O, 0, 100_000);
        self.filler_to(25); // B's birthday is +20.
        self.receive(I, 1, 60_000);
        self.receive(O, 1, 70_000);
        self.filler_to(33); // C's birthday is +30.
        self.receive(I, 2, 33_000);
        self.filler_to(40);
        // Complete Sapling's second shard with two unowned, valid outputs.
        let sk = sapling::zip32::ExtendedSpendingKey::master(&[0xf6; 32]);
        let outputs = (0..2)
            .map(|_| {
                let note = sapling::Note::from_parts(
                    sk.default_address().1,
                    sapling::value::NoteValue::from_raw(1000),
                    sapling::Rseed::AfterZip212(self.random_bytes()),
                );
                let enc = sapling::note_encryption::sapling_note_encryption(
                    None,
                    note.clone(),
                    [0; 512],
                    &mut self.rng,
                );
                CompactSaplingOutput {
                    cmu: note.cmu().to_bytes().to_vec(),
                    ephemeral_key: sapling::note_encryption::SaplingDomain::epk_bytes(enc.epk())
                        .0
                        .to_vec(),
                    ciphertext: enc.encrypt_note_plaintext()[..52].to_vec(),
                }
            })
            .collect();
        let txid = self.random_bytes().to_vec();
        self.push(CompactTx {
            txid,
            outputs,
            ..Default::default()
        });
        self.filler_to(50);
        self.spend(O, 0, a_o100, 30_000);
        self.filler_to(60);
        self.spend(I, 0, a_i110, 40_000);
        self.filler_to(69);
        let orchard = self.fill(
            O,
            &[
                (-1, 0, 200_000),
                (0, 0, 150_000),
                (1, 1, 80_000),
                (3, 0, 50_000),
                (4, 1, 90_000),
            ],
        );
        self.filler_to(99);
        let ironwood = self.fill(
            I,
            &[
                (-1, 0, 210_000),
                (0, 0, 220_000),
                (1, 1, 85_000),
                (2, 2, 44_000),
                (3, 0, 230_000),
                (4, 1, 95_000),
            ],
        );
        self.filler_to(140);
        let a_i300 = self.receive(I, 0, 300_000);
        self.filler_to(150);
        self.spend(I, 0, ironwood[1], 70_000); // received below the window
        self.filler_to(160);
        self.spend(O, 0, orchard[0], 20_000); // change in Orchard's tail
        self.filler_to(170);
        self.spend(I, 1, ironwood[5], 15_000);
        self.filler_to(180);
        self.spend(I, 0, a_i300, 120_000); // received and spent in the window
        self.filler_to(190);
        self.receive(I, 2, 66_000);
        self.filler_to(501);
        assert_eq!(self.f.blocks.len(), 501);
        assert_eq!(self.f.sapling.len(), 2);
        assert_eq!(self.f.orchard.len(), 3);
        assert_eq!(self.f.ironwood.len(), 3);
        self
    }
}

fn fixture() -> &'static Fixture {
    static FIXTURE: OnceLock<Fixture> = OnceLock::new();
    FIXTURE.get_or_init(|| ChainBuilder::new().build().f)
}

fn fork_fixture() -> Fixture {
    let mut fork = ChainBuilder::new().build();
    let at = fork.f.birthday + 465;
    fork.f.blocks.retain(|h, _| *h <= at);
    fork.f.states.retain(|h, _| *h <= at);
    fork.f.tip = at;
    fork.receive(Pool::Ironwood, 3, 777);
    while fork.f.tip < fork.f.birthday + 501 {
        fork.receive(Pool::Ironwood, 3, 778);
    }
    fork.f
}

fn old_birthday_fixture(blocks: u32) -> Fixture {
    let mut f = ChainBuilder::new().build().f;
    f.tip = f.birthday + blocks - 1;
    f.blocks.retain(|h, _| *h <= f.tip);
    f.states.retain(|h, _| *h <= f.tip);
    f
}

fn scanned_a(f: &Fixture) -> Db {
    scanned_a_at(f, ":memory:")
}

fn scanned_a_at(f: &Fixture, name: &str) -> Db {
    let mut db = wallet_at(f, &[0], name);
    scan(&mut db, f, f.birthday, 200, &f.keys[..1]).unwrap();
    scan(
        &mut db,
        f,
        f.birthday + 200,
        f.tip - f.birthday - 199,
        &f.keys[..1],
    )
    .unwrap();
    db
}

fn import(db: &mut Db, f: &Fixture, key: usize, offset: u32) -> AccountUuid {
    try_import(db, f, key, offset).unwrap()
}

fn try_import(db: &mut Db, f: &Fixture, key: usize, offset: u32) -> Result<AccountUuid> {
    db.import_account_ufvk(
        &format!("account {key}"),
        &UnifiedFullViewingKey::decode(db.params(), &f.keys[key]).unwrap(),
        &AccountBirthday::from_parts(f.states[&(f.birthday + offset - 1)].clone(), None),
        AccountPurpose::Spending { derivation: None },
        None,
    )
    .map(|account| account.id())
    .map_err(crate::account::import_error)
}

fn wallet(f: &Fixture, offsets: &[u32]) -> Db {
    wallet_at(f, offsets, ":memory:")
}

fn wallet_at(f: &Fixture, offsets: &[u32], name: &str) -> Db {
    let mut db = crate::wallet::open_and_migrate("test", name).unwrap();
    for (i, offset) in offsets.iter().enumerate() {
        import(&mut db, f, i, *offset);
    }
    db.put_sapling_subtree_roots(0, &f.sapling).unwrap();
    db.put_orchard_subtree_roots(0, &f.orchard).unwrap();
    db.put_ironwood_subtree_roots(0, &f.ironwood).unwrap();
    db.update_chain_tip(height(f.tip)).unwrap();
    db
}

fn seed_unmined_sends(conn: &rusqlite::Connection, tip: u32, account_offset: usize) {
    seed_unmined_sends_at_positions(conn, tip, account_offset, None);
}

fn seed_unmined_sends_at_positions(
    conn: &rusqlite::Connection,
    tip: u32,
    account_offset: usize,
    positions: Option<&[u64]>,
) -> Vec<u64> {
    let (id, uuid): (i64, Vec<u8>) = conn
        .query_row(
            "SELECT id, uuid FROM accounts ORDER BY id LIMIT 1 OFFSET ?1",
            [account_offset as i64],
            |r| Ok((r.get(0)?, r.get(1)?)),
        )
        .unwrap();
    let notes: Vec<(i64, u64)> = conn.prepare("SELECT id, commitment_tree_position FROM orchard_received_notes n WHERE account_id = ?1 AND (?2 IS NULL OR commitment_tree_position IN (?2, ?3)) AND NOT EXISTS (SELECT 1 FROM orchard_received_note_spends s WHERE s.orchard_received_note_id = n.id) ORDER BY commitment_tree_position LIMIT 2").unwrap()
        .query_map(rusqlite::params![id, positions.and_then(|p| p.first()).copied(), positions.and_then(|p| p.get(1)).copied()], |r| Ok((r.get(0)?, r.get(1)?))).unwrap().collect::<rusqlite::Result<_>>().unwrap();
    assert_eq!(notes.len(), 2);
    for index in 0..2u8 {
        let note = notes[index as usize].0;
        // Inert raw markers exercise storage retention, not signing/broadcast.
        let txid = [0xe1 + index; 32];
        conn.execute("INSERT INTO transactions (txid, created, expiry_height, raw, min_observed_height) VALUES (?1, '2026-10-23T00:00:00Z', ?2, ?3, ?4)", rusqlite::params![txid.as_slice(), tip + 40, [index, 0x55].as_slice(), tip]).unwrap();
        conn.execute("INSERT INTO orchard_received_note_spends (orchard_received_note_id, transaction_id) VALUES (?1, ?2)", rusqlite::params![note, conn.last_insert_rowid()]).unwrap();
        if index == 1 {
            conn.execute("INSERT INTO ext_onekey_tx_state (txid, account_uuid, rejection_code, rejection_reason) VALUES (?1, ?2, 51, 'synthetic rejection')", rusqlite::params![txid.as_slice(), uuid]).unwrap();
        }
    }
    notes.into_iter().map(|(_, p)| p).collect()
}

fn assert_pending_input_witnesses_and_locks(db: &mut Db, tip: u32) {
    use zcash_client_backend::data_api::{wallet::input_selection::LockFilter, InputSource};
    use zcash_primitives::transaction::TxId;
    use zcash_protocol::ShieldedPool;
    let inputs: Vec<(Vec<u8>, u32, u64)> = read_rows(db, "SELECT source.txid, n.action_index, n.commitment_tree_position FROM orchard_received_note_spends s JOIN orchard_received_notes n ON n.id = s.orchard_received_note_id JOIN transactions source ON source.id_tx = n.transaction_id JOIN transactions pending ON pending.id_tx = s.transaction_id WHERE pending.raw IS NOT NULL AND pending.mined_height IS NULL", |r| Ok((r.get(0)?, r.get(1)?, r.get(2)?)));
    assert_eq!(inputs.len(), 2);
    for (txid, index, position) in inputs {
        assert!(db
            .with_orchard_tree_mut(|tree| tree
                .witness_at_checkpoint_depth(position.into(), 0)
                .map_err(SqliteClientError::from))
            .unwrap()
            .is_some());
        let txid = TxId::from_bytes(txid.try_into().unwrap());
        assert!(
            db.get_spendable_note(
                &txid,
                ShieldedPool::Orchard,
                index,
                (tip + 1).into(),
                LockFilter::Unfiltered
            )
            .unwrap()
            .is_none(),
            "a pending send input must not be selected again"
        );
    }
}

fn send_evidence(db: &mut Db) -> Vec<String> {
    let mut evidence = rows(db, "SELECT hex(txid), hex(raw), mined_height, expiry_height FROM transactions WHERE raw IS NOT NULL ORDER BY txid");
    evidence.extend(rows(
        db,
        "SELECT hex(txid), rejection_code, rejection_reason FROM ext_onekey_tx_state ORDER BY txid",
    ));
    evidence.extend(rows(db, "SELECT hex(t.txid), n.commitment_tree_position FROM orchard_received_note_spends s JOIN transactions t ON t.id_tx = s.transaction_id JOIN orchard_received_notes n ON n.id = s.orchard_received_note_id WHERE t.raw IS NOT NULL ORDER BY t.txid"));
    evidence
}

fn scan(db: &mut Db, f: &Fixture, start: u32, len: u32, active: &[String]) -> Result<()> {
    let cache = MemoryBlockCache::new();
    cache
        .insert(
            f.blocks
                .range(start..start + len)
                .map(|(_, b)| b.clone())
                .collect(),
        )
        .unwrap();
    let params = *db.params();
    let keys = ScanningKeys::from_account_ufvks(
        select_active_scanning_ufvks(&params, db.get_unified_full_viewing_keys().unwrap(), active)
            .unwrap(),
    );
    scan_blocks_inline(
        &params,
        &cache,
        db,
        &keys,
        height(start),
        &f.states[&(start - 1)],
        len as usize,
    )
}

#[derive(Clone, Copy)]
enum Order {
    Upstream,
    Ascending,
    Window,
    Any,
}

const LANES: [ScanPriority; 5] = [
    ScanPriority::Verify,
    ScanPriority::ChainTip,
    ScanPriority::FoundNote,
    ScanPriority::OpenAdjacent,
    ScanPriority::Historic,
];

fn next(db: &mut Db, f: &Fixture, order: Order, lane: ScanPriority) -> Option<ScanRange> {
    let suggested = db.suggest_scan_ranges().unwrap();
    match order {
        Order::Upstream => suggested.into_iter().find(|r| r.priority() == lane),
        Order::Ascending => suggested.into_iter().min_by_key(|r| r.block_range().start),
        Order::Window | Order::Any => {
            let selected = pick_range(
                &suggested,
                if matches!(order, Order::Any) {
                    None
                } else {
                    Some(lane)
                },
                window_start(db).unwrap(),
            )?;
            let state = &f.states[&(u32::from(selected.block_range().start) - 1)];
            Some(guard_fast_range(db, &suggested, selected, state).unwrap())
        }
    }
}

fn finish(db: &mut Db, f: &Fixture, order: Order, batch: u32, active: &[String]) {
    for _ in 0..2000 {
        if db.suggest_scan_ranges().unwrap().is_empty() {
            return;
        }
        let mut advanced = false;
        for lane in LANES {
            if let Some(r) = next(db, f, order, lane) {
                let start = u32::from(r.block_range().start);
                scan(db, f, start, batch.min(r.len() as u32), active).unwrap();
                advanced = true;
            }
        }
        assert!(advanced, "pending ranges must be reachable");
    }
    panic!("scan order failed to converge");
}

fn read_rows<T>(
    db: &mut Db,
    sql: &str,
    mut read: impl FnMut(&rusqlite::Row<'_>) -> rusqlite::Result<T>,
) -> Vec<T> {
    use rusqlite::OptionalExtension;
    db.transactionally_with_extension(|_, ext| {
        let mut data = Vec::new();
        loop {
            let row = ext
                .query_row(
                    &format!("SELECT * FROM ({sql}) LIMIT 1 OFFSET {}", data.len()),
                    [],
                    &mut read,
                )
                .optional()?;
            let Some(row) = row else {
                break;
            };
            data.push(row);
        }
        Ok::<_, rusqlite::Error>(data)
    })
    .unwrap()
}

fn rows(db: &mut Db, sql: &str) -> Vec<String> {
    read_rows(db, sql, |r| {
        Ok((0..r.as_ref().column_count())
            .map(|i| format!("{:?}", r.get_ref(i).unwrap()))
            .collect::<Vec<_>>()
            .join("|"))
    })
}

fn all_table_rows(db: &mut Db) -> Vec<String> {
    let tables = read_rows(
        db,
        "SELECT name FROM sqlite_master WHERE type = 'table' ORDER BY name",
        |r| r.get::<_, String>(0),
    );
    let mut out = Vec::new();
    for table in tables {
        out.extend(
            rows(db, &format!("SELECT * FROM \"{table}\""))
                .into_iter()
                .map(|row| format!("{table}: {row}")),
        );
    }
    out.sort();
    out
}

fn assert_checkpoints_diverged(error: RuntimeError) {
    assert_eq!(error.code, ErrorCode::DatabaseError);
    assert_eq!(
        error.params,
        json!({ "operation": "importAccount", "reason": "checkpointsDiverged", "checkpointHeight": 4135105 })
    );
    assert!(error.detail.unwrap().contains(
        "the Orchard note commitment tree retains checkpoints both above and below height 4135105"
    ));
}

/// Compare values, spentness, nullifiers, transaction deltas, spendable balances,
/// final roots and every unspent note's witness. Compact-only is_change is an
/// upstream order-dependent hint; full-transaction enhancement corrects it.
fn snapshot(db: &mut Db, f: &Fixture) -> Vec<String> {
    let mut out = Vec::new();
    for pool in ["orchard", "ironwood"] {
        out.extend(rows(
            db,
            &format!(
                "SELECT rn.account_id, hex(t.txid), rn.value, t.mined_height,
                    rn.commitment_tree_position, hex(rn.nf), rn.recipient_key_scope,
                    rn.witness_stabilized, hex(spent.txid)
             FROM {pool}_received_notes rn JOIN transactions t ON t.id_tx = rn.transaction_id
             LEFT JOIN {pool}_received_note_spends s ON s.{pool}_received_note_id = rn.id
             LEFT JOIN transactions spent ON spent.id_tx = s.transaction_id
             ORDER BY rn.account_id, rn.commitment_tree_position"
            ),
        ));
        let positions: Vec<u64> = read_rows(db, &format!(
                "SELECT commitment_tree_position FROM {pool}_received_notes rn
                 WHERE rn.id NOT IN (SELECT {pool}_received_note_id FROM {pool}_received_note_spends)
                 ORDER BY commitment_tree_position"
            ), |r| r.get(0));
        for p in positions {
            let witness = if pool == "orchard" {
                db.with_orchard_tree_mut(|t| {
                    t.witness_at_checkpoint_depth(p.into(), 0)
                        .map_err(SqliteClientError::from)
                })
                .unwrap()
            } else {
                db.with_ironwood_tree_mut(|t| {
                    t.witness_at_checkpoint_depth(p.into(), 0)
                        .map_err(SqliteClientError::from)
                })
                .unwrap()
                .unwrap()
            }
            .expect("every unspent note has a usable witness");
            out.push(format!("{pool} {p} {:?}", witness.path_elems()));
        }
    }
    out.extend(rows(
        db,
        "SELECT a.id, hex(t.txid), t.mined_height, t.account_balance_delta,
        t.spent_note_count FROM v_transactions t
        JOIN accounts a ON a.uuid = t.account_uuid ORDER BY a.id, t.txid",
    ));
    let orchard = db
        .with_orchard_tree_mut(|t| {
            t.root_at_checkpoint_depth(Some(0))
                .map_err(SqliteClientError::from)
        })
        .unwrap()
        .unwrap();
    let ironwood = db
        .with_ironwood_tree_mut(|t| {
            t.root_at_checkpoint_depth(Some(0))
                .map_err(SqliteClientError::from)
        })
        .unwrap()
        .unwrap()
        .unwrap();
    out.push(format!("roots {orchard:?} {ironwood:?}"));
    let summary = db
        .get_wallet_summary(zcash_client_backend::data_api::wallet::ConfirmationsPolicy::MIN)
        .unwrap()
        .unwrap();
    for account in db.get_account_ids().unwrap() {
        let b = summary.account_balances().get(&account).unwrap();
        out.push(format!(
            "balance {} {} {} {} {}",
            db.get_account_birthday(account).unwrap(),
            b.orchard_balance().total().into_u64(),
            b.orchard_balance().spendable_value().into_u64(),
            b.ironwood_balance().total().into_u64(),
            b.ironwood_balance().spendable_value().into_u64()
        ));
    }
    out.sort();
    assert!(db.suggest_scan_ranges().unwrap().is_empty());
    assert_eq!(
        db.block_fully_scanned().unwrap().unwrap().block_height(),
        height(f.tip)
    );
    out
}

fn assert_same(actual: Vec<String>, expected: &[String]) {
    let actual: std::collections::BTreeSet<_> = actual.iter().collect();
    let expected: std::collections::BTreeSet<_> = expected.iter().collect();
    let differences: Vec<_> = actual.symmetric_difference(&expected).collect();
    assert!(
        differences.is_empty(),
        "{} different state rows: {:?}",
        differences.len(),
        differences.iter().take(3).collect::<Vec<_>>()
    );
}

#[test]
fn equivalent_orders_across_both_pools_and_shard_boundaries() {
    let f = fixture();
    let mut reference = wallet(f, &[0, 20, 30]);
    finish(&mut reference, f, Order::Ascending, 31, &f.keys);
    let expected = snapshot(&mut reference, f);
    assert!(expected.iter().any(|s| s.starts_with("orchard ")));
    assert!(expected.iter().any(|s| s.starts_with("ironwood ")));
    for (order, batch) in [
        (Order::Upstream, 13),
        (Order::Window, 7),
        (Order::Window, 53),
        (Order::Window, 131),
        (Order::Any, 23),
    ] {
        let mut db = wallet(f, &[0, 20, 30]);
        finish(&mut db, f, order, batch, &f.keys);
        assert_same(snapshot(&mut db, f), &expected);
    }
}

#[test]
fn window_note_extends_to_its_orchard_shard_and_becomes_spendable() {
    let f = fixture();
    let mut db = wallet(f, &[0, 20, 30]);
    let initial = window_start(&mut db).unwrap();
    assert_eq!(initial, height(f.birthday + 69));
    scan(&mut db, f, u32::from(initial), 132, &f.keys).unwrap();
    // Starting at the earlier Orchard boundary also discovers its completing-
    // block note. The cross-pool extension must include that older shard too.
    assert_eq!(window_start(&mut db).unwrap(), height(f.birthday - 500));
    // Finish fast work only: Historic remains pending, but both tail witnesses
    // must be complete and their notes selectable before the full restore.
    for _ in 0..100 {
        let Some(r) = next(&mut db, f, Order::Window, ScanPriority::ChainTip) else {
            break;
        };
        scan(
            &mut db,
            f,
            u32::from(r.block_range().start),
            31.min(r.len() as u32),
            &f.keys,
        )
        .unwrap();
    }
    assert!(!db.suggest_scan_ranges().unwrap().is_empty());
    let summary = db
        .get_wallet_summary(zcash_client_backend::data_api::wallet::ConfirmationsPolicy::MIN)
        .unwrap()
        .unwrap();
    let a = db
        .get_account_ids()
        .unwrap()
        .into_iter()
        .find(|a| db.get_account_birthday(*a).unwrap() == height(f.birthday))
        .unwrap();
    let balance = &summary.account_balances()[&a];
    assert!(balance.orchard_balance().spendable_value().into_u64() > 0);
    assert!(balance.ironwood_balance().spendable_value().into_u64() > 0);
}

#[test]
fn already_caught_up_and_birthday_inside_window_keep_upstream_ranges() {
    let f = fixture();
    let mut db = wallet(f, &[0]);
    finish(&mut db, f, Order::Window, 53, &f.keys[..1]);
    assert!(pick_range(&[], None, window_start(&mut db).unwrap()).is_none());
    db.update_chain_tip(height(f.tip + 1)).unwrap();
    let suggested = db.suggest_scan_ranges().unwrap();
    assert_eq!(
        pick_range(&suggested, None, window_start(&mut db).unwrap()),
        suggested.first().cloned()
    );

    let suggested = vec![range(f.birthday + 150, f.tip + 1, ScanPriority::ChainTip)];
    assert_eq!(
        pick_range(
            &suggested,
            Some(ScanPriority::ChainTip),
            height(f.birthday + 99)
        ),
        suggested.first().cloned()
    );
    assert!(pick_range(
        &suggested,
        Some(ScanPriority::Historic),
        height(f.birthday + 99)
    )
    .is_none());

    let mut reference = wallet(f, &[150]);
    finish(&mut reference, f, Order::Ascending, 53, &f.keys[..1]);
    let expected = snapshot(&mut reference, f);
    let mut db = wallet(f, &[150]);
    finish(&mut db, f, Order::Window, 23, &f.keys[..1]);
    assert_same(snapshot(&mut db, f), &expected);
}

#[test]
fn no_ironwood_activity_matches_ascending_scanning() {
    let mut f = ChainBuilder::new().build().f;
    f.ironwood.clear();
    for state in f.states.values_mut() {
        *state = ChainState::new(
            state.block_height(),
            state.block_hash(),
            state.final_sapling_tree().clone(),
            state.final_orchard_tree().clone(),
            ChainState::empty(state.block_height(), state.block_hash())
                .final_ironwood_tree()
                .clone(),
        );
    }
    for block in f.blocks.values_mut() {
        for tx in &mut block.vtx {
            tx.ironwood_actions.clear();
        }
        block
            .chain_metadata
            .as_mut()
            .unwrap()
            .ironwood_commitment_tree_size = 0;
    }
    let mut reference = wallet(&f, &[0, 20, 30]);
    finish(&mut reference, &f, Order::Ascending, 53, &f.keys);
    let expected = snapshot(&mut reference, &f);
    let mut db = wallet(&f, &[0, 20, 30]);
    finish(&mut db, &f, Order::Window, 23, &f.keys);
    assert_same(snapshot(&mut db, &f), &expected);
}

#[test]
fn progress_heights_do_not_depend_on_balance_queries() {
    let f = fixture();
    let uri = "file:onekey-progress-without-balances?mode=memory&cache=shared";
    let mut db = wallet_at(f, &[0, 20], uri);
    finish(&mut db, f, Order::Ascending, 53, &f.keys[..2]);
    let expected = scan_heights(&db).unwrap();
    let conn = rusqlite::Connection::open(uri).unwrap();
    conn.execute_batch("DROP VIEW v_orchard_shards_scan_state")
        .unwrap();
    assert!(db
        .get_wallet_summary(zcash_client_backend::data_api::wallet::ConfirmationsPolicy::default())
        .is_err());

    let peek: serde_json::Value = serde_json::from_str(&peek(&db).unwrap()).unwrap();
    assert_eq!(peek["chainTip"], json!(expected.0));
    assert_eq!(peek["fullyScannedHeight"], json!(expected.1));
    for id in db.get_account_ids().unwrap() {
        let status: serde_json::Value =
            serde_json::from_str(&account_status(&db, &id.expose_uuid().to_string()).unwrap())
                .unwrap();
        assert_eq!(status["chainTip"], peek["chainTip"]);
        assert_eq!(status["fullyScannedHeight"], peek["fullyScannedHeight"]);
        assert_eq!(status["remainingBlocks"], 0);
    }
}

#[test]
fn progress_heights_match_summary_across_scan_rescan_and_rewind() {
    fn check(db: &Db) {
        let summary = db
            .get_wallet_summary(
                zcash_client_backend::data_api::wallet::ConfirmationsPolicy::default(),
            )
            .unwrap()
            .unwrap();
        let expected = (
            Some(u32::from(summary.chain_tip_height())),
            Some(u32::from(summary.fully_scanned_height())),
        );
        assert_eq!(scan_heights(db).unwrap(), expected);
        let peek: serde_json::Value = serde_json::from_str(&peek(db).unwrap()).unwrap();
        assert_eq!(peek["chainTip"], json!(expected.0));
        assert_eq!(peek["fullyScannedHeight"], json!(expected.1));
    }

    let f = fixture();
    let empty = crate::wallet::open_and_migrate("test", ":memory:").unwrap();
    assert_eq!(scan_heights(&empty).unwrap(), (None, None));
    let no_accounts = wallet(f, &[]);
    assert_eq!(scan_heights(&no_accounts).unwrap(), (None, None));
    let mut db = wallet(f, &[0, 20]);
    check(&db);
    assert_eq!(scan_heights(&db).unwrap().1, Some(f.birthday - 1));
    scan(&mut db, f, f.tip - 19, 20, &f.keys[..2]).unwrap();
    check(&db);
    assert_eq!(scan_heights(&db).unwrap().1, Some(f.birthday - 1));
    scan(&mut db, f, f.birthday, 10, &f.keys[..2]).unwrap();
    check(&db);
    finish(&mut db, f, Order::Ascending, 53, &f.keys[..2]);
    check(&db);
    queue_rescan_from(&mut db, f.birthday + 30).unwrap();
    check(&db);
    rewind_to(&mut db, f.tip - 35).unwrap();
    check(&db);
}

#[test]
fn add_account_rewind_and_pause_resume_match_a_fresh_scan() {
    let f = fixture();
    let mut reference = wallet(f, &[0, 20, 30]);
    finish(&mut reference, f, Order::Ascending, 53, &f.keys);
    let expected = snapshot(&mut reference, f);
    let mut db = wallet(f, &[0]);
    // Isolate rewind/resume from upstream's separate sparse-checkpoint failure
    // on consecutive old-account imports (also reproduced with the original
    // order and 53-block batches); one-block checkpoints permit both imports.
    finish(&mut db, f, Order::Window, 1, &f.keys[..1]);
    import(&mut db, f, 1, 20);
    import(&mut db, f, 2, 30);
    assert!(!db.suggest_scan_ranges().unwrap().is_empty());
    finish(&mut db, f, Order::Window, 53, &f.keys[..2]);
    queue_rescan_from(&mut db, f.birthday + 30).unwrap();
    finish(&mut db, f, Order::Window, 23, &f.keys);
    assert_same(snapshot(&mut db, f), &expected);
}

#[test]
fn checkpoint_repair_imports_without_an_active_registered_identity() {
    let f = old_birthday_fixture(304);
    let uri = "file:onekey-checkpoint-repair?mode=memory&cache=shared";
    let mut db = scanned_a_at(&f, uri);
    import(&mut db, &f, 1, 20);
    let conn = rusqlite::Connection::open(uri).unwrap();
    seed_unmined_sends(&conn, f.tip, 0);
    let sends = send_evidence(&mut db);
    assert_checkpoints_diverged(try_import(&mut db, &f, 2, 30).unwrap_err());
    let accounts = db.get_account_ids().unwrap();
    let birthdays: Vec<_> = accounts
        .iter()
        .map(|id| db.get_account(*id).unwrap().unwrap().birthday_height())
        .collect();
    let ufvk = UnifiedFullViewingKey::decode(db.params(), &f.keys[2]).unwrap();
    crate::account::import_with_checkpoint(
        &mut db,
        "account 2",
        &ufvk,
        &AccountBirthday::from_parts(f.states[&(f.birthday + 29)].clone(), None),
        AccountPurpose::Spending { derivation: None },
        &f.states[&(f.birthday + 105)],
    )
    .unwrap();
    assert_eq!(send_evidence(&mut db), sends);
    assert_eq!(db.chain_height().unwrap(), Some(height(f.tip)));
    for (id, birthday) in accounts.iter().zip(birthdays) {
        assert_eq!(
            db.get_account(*id).unwrap().unwrap().birthday_height(),
            birthday
        );
    }
    // Only the newly imported identity is active during recovery.
    finish(&mut db, &f, Order::Ascending, 53, &f.keys[2..]);
    // Previously paused keys decrypt their retained span when resumed.
    queue_rescan_from(&mut db, f.birthday).unwrap();
    finish(&mut db, &f, Order::Ascending, 53, &f.keys);
    let reference_uri = "file:onekey-checkpoint-reference?mode=memory&cache=shared";
    let mut reference = wallet_at(&f, &[0, 20, 30], reference_uri);
    finish(&mut reference, &f, Order::Ascending, 53, &f.keys);
    seed_unmined_sends(
        &rusqlite::Connection::open(reference_uri).unwrap(),
        f.tip,
        0,
    );
    assert_same(snapshot(&mut db, &f), &snapshot(&mut reference, &f));
    assert_eq!(send_evidence(&mut db), sends);
}

#[test]
fn checkpoint_repair_rolls_back_when_account_import_fails() {
    let f = old_birthday_fixture(304);
    let mut db = scanned_a(&f);
    import(&mut db, &f, 1, 20);
    let before = all_table_rows(&mut db);
    let ufvk = UnifiedFullViewingKey::decode(db.params(), &f.keys[1]).unwrap();
    let error = crate::account::import_with_checkpoint(
        &mut db,
        "duplicate account",
        &ufvk,
        &AccountBirthday::from_parts(f.states[&(f.birthday + 19)].clone(), None),
        AccountPurpose::Spending { derivation: None },
        &f.states[&(f.birthday + 105)],
    )
    .unwrap_err();
    assert!(
        error
            .detail
            .as_deref()
            .unwrap_or_default()
            .contains("already exists"),
        "{error:?}"
    );
    assert_eq!(all_table_rows(&mut db), before);
}

#[test]
fn checkpoint_repair_rejects_a_conflicting_block_or_tree_position() {
    let f = fixture();
    let mut db = wallet(f, &[0]);
    finish(&mut db, f, Order::Ascending, 53, &f.keys[..1]);
    let ufvk = UnifiedFullViewingKey::decode(db.params(), &f.keys[1]).unwrap();
    let state = &f.states[&f.tip];
    let before = all_table_rows(&mut db);
    for checkpoint in [
        ChainState::new(
            state.block_height(),
            BlockHash([0xaa; 32]),
            state.final_sapling_tree().clone(),
            state.final_orchard_tree().clone(),
            state.final_ironwood_tree().clone(),
        ),
        ChainState::new(
            state.block_height(),
            state.block_hash(),
            state.final_sapling_tree().clone(),
            f.states[&f.birthday].final_orchard_tree().clone(),
            state.final_ironwood_tree().clone(),
        ),
    ] {
        assert!(crate::account::import_with_checkpoint(
            &mut db,
            "conflicting checkpoint",
            &ufvk,
            &AccountBirthday::from_parts(f.states[&(f.birthday + 19)].clone(), None),
            AccountPurpose::Spending { derivation: None },
            &checkpoint,
        )
        .is_err());
        assert_eq!(all_table_rows(&mut db), before);
    }
}

#[test]
fn safe_birthday_rescan_retains_pending_raw_rejections_and_other_identities() {
    let f = fixture();
    let uri = "file:onekey-safe-birthday-rescan?mode=memory&cache=shared";
    let mut db = wallet_at(f, &[0, 200], uri);
    finish(&mut db, f, Order::Ascending, 53, &f.keys[..2]);
    let conn = rusqlite::Connection::open(uri).unwrap();
    let positions = seed_unmined_sends_at_positions(&conn, f.tip, 1, None);
    let evidence = send_evidence(&mut db);
    let ids = db.get_account_ids().unwrap();
    let target = ids
        .iter()
        .copied()
        .find(|id| db.get_account_birthday(*id).unwrap() == height(f.birthday + 200))
        .unwrap();
    let other = ids.iter().copied().find(|id| *id != target).unwrap();
    let other_birthday = db.get_account_birthday(other).unwrap();
    crate::account::rescan_with_birthday(
        &mut db,
        target,
        &AccountBirthday::from_parts(f.states[&(f.birthday + 19)].clone(), None),
        None,
    )
    .unwrap();
    assert_eq!(send_evidence(&mut db), evidence);
    assert_eq!(db.get_account_ids().unwrap(), ids);
    assert_eq!(db.get_account_birthday(other).unwrap(), other_birthday);
    assert_eq!(
        db.get_account_birthday(target).unwrap(),
        height(f.birthday + 20)
    );
    finish(&mut db, f, Order::Ascending, 53, &f.keys[..2]);
    assert_pending_input_witnesses_and_locks(&mut db, f.tip);
    let reference_uri = "file:onekey-safe-rescan-reference?mode=memory&cache=shared";
    let mut reference = wallet_at(f, &[0, 20], reference_uri);
    finish(&mut reference, f, Order::Ascending, 53, &f.keys[..2]);
    seed_unmined_sends_at_positions(
        &rusqlite::Connection::open(reference_uri).unwrap(),
        f.tip,
        1,
        Some(&positions),
    );
    assert_same(snapshot(&mut db, f), &snapshot(&mut reference, f));
    assert_eq!(send_evidence(&mut db), evidence);
}

#[test]
fn a_later_birthday_starts_the_account_over_at_that_height() {
    // Notes of one account, independent of internal ids.
    fn notes_of(db: &mut Db, account: &str) -> Vec<String> {
        let uuid = account.replace('-', "");
        ["orchard", "ironwood"]
            .iter()
            .flat_map(|pool| {
                rows(
                    db,
                    &format!(
                        "SELECT '{pool}', hex(t.txid), rn.value, t.mined_height,
                            rn.commitment_tree_position, hex(rn.nf), hex(spent.txid)
                         FROM {pool}_received_notes rn
                         JOIN transactions t ON t.id_tx = rn.transaction_id
                         JOIN accounts a ON a.id = rn.account_id
                         LEFT JOIN {pool}_received_note_spends s ON s.{pool}_received_note_id = rn.id
                         LEFT JOIN transactions spent ON spent.id_tx = s.transaction_id
                         WHERE hex(a.uuid) = upper('{uuid}')
                         ORDER BY rn.commitment_tree_position"
                    ),
                )
            })
            .collect()
    }
    let f = fixture();
    let later = AccountBirthday::from_parts(f.states[&(f.birthday + 199)].clone(), None);
    let mut db = wallet(f, &[0, 200]);
    finish(&mut db, f, Order::Ascending, 53, &f.keys[..2]);
    let ids = db.get_account_ids().unwrap();
    let uuid_of = |id: &zcash_client_sqlite::AccountUuid| id.expose_uuid().to_string();
    let (target, other) = if db.get_account_birthday(ids[0]).unwrap() == height(f.birthday) {
        (ids[0], uuid_of(&ids[1]))
    } else {
        (ids[1], uuid_of(&ids[0]))
    };
    let before = notes_of(&mut db, &uuid_of(&target));
    let other_before = notes_of(&mut db, &other);

    let restarted = crate::account::restart_with_birthday(&mut db, target, &later, None).unwrap();
    finish(&mut db, f, Order::Ascending, 53, &f.keys[..2]);

    let mut reference = wallet(f, &[200]);
    finish(&mut reference, f, Order::Ascending, 53, &f.keys[..1]);
    let reference_account = uuid_of(&reference.get_account_ids().unwrap()[0]);
    let expected = notes_of(&mut reference, &reference_account);
    assert!(
        before.len() > expected.len(),
        "the old birthday found more notes"
    );
    assert_eq!(notes_of(&mut db, &restarted), expected);
    assert_eq!(notes_of(&mut db, &other), other_before);
}

#[test]
fn old_birthday_import_rolls_back_and_a_completed_rescan_heals() {
    let f = old_birthday_fixture(303);
    let mut db = scanned_a(&f);
    import(&mut db, &f, 1, 20);
    import(&mut db, &f, 2, 30);
    assert_eq!(db.get_account_ids().unwrap().len(), 3);

    let f = old_birthday_fixture(304);
    let mut reference = wallet(&f, &[0, 20]);
    finish(&mut reference, &f, Order::Ascending, 304, &f.keys[..2]);
    let expected_ab = snapshot(&mut reference, &f);
    let mut reference = wallet(&f, &[0, 20, 30]);
    finish(&mut reference, &f, Order::Ascending, 304, &f.keys);
    let expected = snapshot(&mut reference, &f);
    for order in [Order::Window, Order::Any] {
        for batch in [53, 200] {
            let mut db = scanned_a(&f);
            let b = import(&mut db, &f, 1, 20);
            let accounts = db.get_account_ids().unwrap();
            let before = all_table_rows(&mut db);
            for _ in 0..2 {
                assert_checkpoints_diverged(try_import(&mut db, &f, 2, 30).unwrap_err());
                assert_eq!(all_table_rows(&mut db), before);
                assert_eq!(db.get_account_ids().unwrap(), accounts);
                assert_eq!(
                    crate::account::account_by_ufvk(&db, &f.keys[2]).unwrap(),
                    "null"
                );
            }
            finish(&mut db, &f, order, batch, &f.keys[..2]);
            assert_same(snapshot(&mut db, &f), &expected_ab);
            let status: serde_json::Value =
                serde_json::from_str(&account_status(&db, &b.expose_uuid().to_string()).unwrap())
                    .unwrap();
            assert_eq!(status["isComplete"], true);
            import(&mut db, &f, 2, 30);
            finish(&mut db, &f, order, batch, &f.keys);
            assert_same(snapshot(&mut db, &f), &expected);
        }
    }
}

#[test]
fn old_birthday_import_heals_at_the_first_repaired_checkpoint() {
    let f = old_birthday_fixture(304);
    let mut reference = wallet(&f, &[0, 20, 30]);
    finish(&mut reference, &f, Order::Ascending, 304, &f.keys);
    let expected = snapshot(&mut reference, &f);
    for (order, batch, expected_steps, expected_blocks) in [
        (Order::Window, 1, 18, 18),
        (Order::Any, 1, 86, 86),
        (Order::Window, 53, 3, 133),
        (Order::Any, 53, 3, 133),
        (Order::Window, 200, 1, 200),
        (Order::Any, 200, 1, 200),
    ] {
        let mut db = scanned_a(&f);
        import(&mut db, &f, 1, 20);
        assert_checkpoints_diverged(try_import(&mut db, &f, 2, 30).unwrap_err());
        let initial = db.block_max_scanned().unwrap().unwrap().block_height();
        assert_eq!(initial, height(f.birthday + 204));
        let mut blocks = 0;
        let mut steps = 0;
        'scanning: for _ in 0..expected_steps {
            for lane in LANES {
                let Some(r) = next(&mut db, &f, order, lane) else {
                    continue;
                };
                let start = u32::from(r.block_range().start);
                let len = batch.min(r.len() as u32);
                let checkpoint_sql = format!(
                    "SELECT checkpoint_id FROM orchard_tree_checkpoints WHERE checkpoint_id = {}",
                    f.birthday + 105,
                );
                if batch == 1 && steps + 1 == expected_steps {
                    assert!(rows(&mut db, &checkpoint_sql).is_empty());
                }
                scan(&mut db, &f, start, len, &f.keys[..2]).unwrap();
                blocks += len;
                steps += 1;
                if steps == expected_steps {
                    if batch == 1 {
                        assert_eq!(start, f.birthday + 105);
                        assert_eq!(rows(&mut db, &checkpoint_sql).len(), 1);
                    }
                    if batch <= 53 {
                        assert_eq!(
                            db.block_max_scanned().unwrap().unwrap().block_height(),
                            initial
                        );
                    }
                    import(&mut db, &f, 2, 30);
                    break 'scanning;
                }
                assert_checkpoints_diverged(try_import(&mut db, &f, 2, 30).unwrap_err());
            }
        }
        assert_eq!((steps, blocks), (expected_steps, expected_blocks));
        assert_eq!(db.get_account_ids().unwrap().len(), 3);
        finish(&mut db, &f, order, 53, &f.keys);
        assert_same(snapshot(&mut db, &f), &expected);
    }
}

#[test]
fn deep_reorg_and_verify_converge_to_the_new_chain() {
    let f = fixture();
    let fork = fork_fixture();
    let mut reference = wallet(&fork, &[0, 20, 30]);
    finish(&mut reference, &fork, Order::Ascending, 31, &fork.keys);
    let expected = snapshot(&mut reference, &fork);
    for order in [Order::Upstream, Order::Ascending, Order::Window] {
        let mut db = wallet(f, &[0, 20, 30]);
        finish(&mut db, f, order, 23, &f.keys);
        db.update_chain_tip(height(fork.tip)).unwrap();
        let error = scan(&mut db, &fork, f.tip + 1, 1, &f.keys).unwrap_err();
        assert_eq!(error.code, ErrorCode::ReorgDetected);
        // The fork is 35 blocks below the old tip: repeated host rewinds must
        // reach it without the old shardtree Insert(Conflict) failure.
        let mut at = error.params["suggestedRewindHeight"].as_u64().unwrap() as u32;
        loop {
            let actual = rewind_to(&mut db, at).unwrap();
            match scan(&mut db, &fork, actual + 1, 7, &f.keys) {
                Ok(()) => break,
                Err(e) => {
                    assert_eq!(e.code, ErrorCode::ReorgDetected);
                    at = e.params["suggestedRewindHeight"].as_u64().unwrap() as u32;
                }
            }
        }
        // A rewind truncates the known tip as well. The next host pass sees
        // liveTip > storedTip and calls syncTip before it resumes scanning.
        db.update_chain_tip(height(fork.tip)).unwrap();
        finish(&mut db, &fork, order, 23, &fork.keys);
        assert_same(snapshot(&mut db, &fork), &expected);
        db.update_chain_tip(height(fork.tip + 250)).unwrap();
        let suggested = db.suggest_scan_ranges().unwrap();
        assert_eq!(
            pick_range(&suggested, None, window_start(&mut db).unwrap())
                .unwrap()
                .priority(),
            ScanPriority::Verify
        );
    }
}

#[test]
fn historic_lane_is_strictly_ascending_and_preserves_special_priorities() {
    let suggested = vec![
        range(70, 110, ScanPriority::ChainTip),
        range(10, 40, ScanPriority::Historic),
        range(40, 70, ScanPriority::ChainTip),
        range(1, 5, ScanPriority::FoundNote),
        range(5, 10, ScanPriority::Verify),
    ];
    assert_eq!(
        pick_range(&suggested, Some(ScanPriority::Historic), height(90)),
        Some(suggested[1].clone())
    );
    assert_eq!(
        pick_range(&suggested[2..3], Some(ScanPriority::Historic), height(90)),
        Some(suggested[2].clone())
    );
    assert_eq!(
        pick_range(&suggested, Some(ScanPriority::ChainTip), height(90)),
        Some(range(90, 110, ScanPriority::ChainTip))
    );
    for lane in [ScanPriority::Verify, ScanPriority::FoundNote] {
        assert_eq!(
            pick_range(&suggested, Some(lane), height(90)),
            suggested.iter().find(|r| r.priority() == lane).cloned()
        );
    }
    assert_eq!(
        pick_range(&suggested, None, height(90)),
        Some(suggested[4].clone())
    );
}

/// Minimal SQL stores let boundary tests exercise local planning without writing
/// wallet-owned tables via the production extension API.
fn planning_db(tip: Option<u32>, extra: &str) -> Db {
    let conn = rusqlite::Connection::open_in_memory().unwrap();
    conn.execute_batch("CREATE TABLE scan_queue(block_range_end INTEGER);")
        .unwrap();
    if let Some(tip) = tip {
        conn.execute("INSERT INTO scan_queue VALUES (?)", [tip + 1])
            .unwrap();
    }
    for pool in ["sapling", "orchard", "ironwood"] {
        conn.execute_batch(&format!(
            "CREATE TABLE {pool}_tree_shards(shard_index INTEGER PRIMARY KEY, subtree_end_height INTEGER, root_hash BLOB);
             CREATE TABLE {pool}_received_notes(commitment_tree_position INTEGER);
             CREATE TABLE v_{pool}_shards_scan_state(subtree_start_height INTEGER, subtree_end_height INTEGER,
                 start_position INTEGER, end_position_exclusive INTEGER, max_priority INTEGER);"
        )).unwrap();
    }
    conn.execute_batch(extra).unwrap();
    zcash_client_sqlite::WalletDb::from_connection(
        conn,
        Network::TestNetwork,
        crate::clock::HostClock,
        rand_chacha::ChaCha20Rng::from_seed([0; 32]),
    )
}

#[test]
fn earlier_tail_from_either_pool_defines_the_capped_fast_window() {
    let tip = 100_000;
    for (ironwood, orchard, expected) in [
        (99_000, 98_000, 98_000),
        (98_000, 99_000, 98_000),
        (60_000, 99_000, tip - FAST_WINDOW_MAX_BLOCKS),
        (99_000, 60_000, tip - FAST_WINDOW_MAX_BLOCKS),
    ] {
        let mut db = planning_db(
            Some(tip),
            &format!(
                "INSERT INTO ironwood_tree_shards VALUES (0, {ironwood}, X'01');
                 INSERT INTO orchard_tree_shards VALUES (0, {orchard}, X'01');"
            ),
        );
        assert_eq!(window_start(&mut db).unwrap(), height(expected));
        // The actual fast range is also bounded by upstream's ChainTip start.
        let upstream = range(expected + 100, tip + 1, ScanPriority::ChainTip);
        assert_eq!(
            pick_range(
                &[upstream.clone()],
                Some(ScanPriority::ChainTip),
                height(expected)
            ),
            Some(upstream)
        );
    }
}

#[test]
fn missing_tail_uses_each_pools_activation_height() {
    let orchard_activation = u32::from(
        Network::TestNetwork
            .activation_height(NetworkUpgrade::Nu5)
            .unwrap(),
    );
    let ironwood_activation = u32::from(
        Network::TestNetwork
            .activation_height(NetworkUpgrade::Nu6_3)
            .unwrap(),
    );
    assert!(ironwood_activation > orchard_activation + 500);
    let tip = orchard_activation + 500;
    for (extra, expected) in [
        (String::new(), orchard_activation),
        (
            format!(
                "INSERT INTO ironwood_tree_shards VALUES (0, {}, X'01');",
                tip - 400
            ),
            orchard_activation,
        ),
        (
            format!(
                "INSERT INTO orchard_tree_shards VALUES (0, {}, X'01');",
                tip - 400
            ),
            tip - 400,
        ),
    ] {
        assert_eq!(
            window_start(&mut planning_db(Some(tip), &extra)).unwrap(),
            height(expected)
        );
    }
    let tip = ironwood_activation + 100_000;
    assert_eq!(
        window_start(&mut planning_db(Some(tip), "")).unwrap(),
        height(tip - FAST_WINDOW_MAX_BLOCKS)
    );
}

#[test]
fn clamp_keeps_two_hundred_blocks_even_when_both_tails_are_new() {
    let tip = 100_000;
    let mut db = planning_db(
        Some(tip),
        "INSERT INTO ironwood_tree_shards VALUES (0, 99999, X'01');
         INSERT INTO orchard_tree_shards VALUES (0, 99998, X'01');",
    );
    assert_eq!(
        window_start(&mut db).unwrap(),
        height(tip - FAST_WINDOW_REORG_MARGIN)
    );
    assert_eq!(
        window_start(&mut planning_db(None, "")).unwrap(),
        height(u32::MAX)
    );
}

#[test]
fn note_extension_reaches_a_fixed_point_across_both_pools() {
    let mut db = planning_db(
        Some(100_000),
        "INSERT INTO ironwood_tree_shards VALUES (0, 99000, X'01');
         INSERT INTO orchard_received_notes VALUES (0);
         INSERT INTO ironwood_received_notes VALUES (0);
         INSERT INTO v_orchard_shards_scan_state VALUES (98000, NULL, 0, 65536, 50);
         INSERT INTO v_ironwood_shards_scan_state VALUES (97000, 98500, 0, 65536, 50);",
    );
    assert_eq!(window_start(&mut db).unwrap(), height(97000));
}

#[test]
fn missing_completed_roots_fall_back_in_each_pool_including_sapling() {
    let f = fixture();
    let original = range(f.birthday, f.tip + 1, ScanPriority::ChainTip);
    let fast = range(f.birthday + 100, f.tip + 1, ScanPriority::ChainTip);
    let state = &f.states[&(f.birthday + 99)];
    for missing in ["sapling", "orchard", "ironwood"] {
        let mut sql = String::new();
        for (pool, size) in [
            ("sapling", state.final_sapling_tree().tree_size()),
            ("orchard", state.final_orchard_tree().tree_size()),
            ("ironwood", state.final_ironwood_tree().tree_size()),
        ] {
            for i in 0..(size >> 16) {
                if pool != missing || i != 0 {
                    sql.push_str(&format!(
                        "INSERT INTO {pool}_tree_shards VALUES ({i}, 1, X'01');"
                    ));
                }
            }
        }
        let mut db = planning_db(Some(f.tip), &sql);
        assert!(!roots_cover(&mut db, state).unwrap());
        assert_eq!(
            guard_fast_range(&mut db, &[original.clone()], fast.clone(), state).unwrap(),
            original
        );
    }
    let mut db = wallet(f, &[0]);
    assert!(roots_cover(&mut db, state).unwrap());
    assert_eq!(
        guard_fast_range(&mut db, &[original], fast.clone(), state).unwrap(),
        fast
    );
}

#[test]
fn missing_root_guard_prevents_a_real_subtree_discontinuity_retry_loop() {
    use zcash_primitives::merkle_tree::write_frontier_v1;
    let f = fixture();
    let start = f.birthday + 201;
    let prior = &f.states[&(start - 1)];
    // Model two completed Ironwood shards that the server has not supplied.
    // Shard indices 3 and 5 have the same frontier shape, so relocating this
    // synthetic frontier preserves its encoding while leaving a gap in SQL.
    let mut encoded = Vec::new();
    write_frontier_v1(&mut encoded, prior.final_ironwood_tree()).unwrap();
    let position = u64::from_le_bytes(encoded[1..9].try_into().unwrap()) + (2 << 16);
    encoded[1..9].copy_from_slice(&position.to_le_bytes());
    let state = ChainState::new(
        prior.block_height(),
        prior.block_hash(),
        prior.final_sapling_tree().clone(),
        prior.final_orchard_tree().clone(),
        read_frontier_v1(&encoded[..]).unwrap(),
    );
    let mut block = f.blocks[&start].clone();
    block
        .chain_metadata
        .as_mut()
        .unwrap()
        .ironwood_commitment_tree_size += 2 << 16;
    let cache = MemoryBlockCache::new();
    cache.insert(vec![block]).unwrap();
    let mut db = wallet(f, &[0]);
    let params = *db.params();
    let keys = ScanningKeys::from_account_ufvks(
        select_active_scanning_ufvks(
            &params,
            db.get_unified_full_viewing_keys().unwrap(),
            &f.keys[..1],
        )
        .unwrap(),
    );
    let error =
        scan_blocks_inline(&params, &cache, &mut db, &keys, height(start), &state, 1).unwrap_err();
    assert_eq!(error.params["operation"], "putBlocks");
    assert!(
        error
            .detail
            .as_ref()
            .is_some_and(|s| s.contains("discontinuous with existing subtree range")),
        "{error:?}"
    );
    let original = range(f.birthday, f.tip + 1, ScanPriority::ChainTip);
    let guarded = guard_fast_range(
        &mut db,
        &[original.clone()],
        range(start, f.tip + 1, ScanPriority::ChainTip),
        &state,
    )
    .unwrap();
    assert_eq!(guarded, original);
    scan(
        &mut db,
        f,
        u32::from(guarded.block_range().start),
        7,
        &f.keys[..1],
    )
    .unwrap();
    assert_eq!(
        db.block_fully_scanned().unwrap().unwrap().block_height(),
        height(f.birthday + 6)
    );
}
