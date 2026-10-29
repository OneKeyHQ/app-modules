//! Shared transaction policy for the wallet runtime, stateless signer and host UI.

use std::cell::Cell;

use zcash_primitives::transaction::fees::zip317::{self, FeeRule};
use zcash_protocol::{
    consensus::{BlockHeight, BranchId, NetworkType, Parameters},
    value::Zatoshis,
};

pub const ZIP2009_MAINNET_HEIGHT: u32 = 3_590_000;
// Match Ledger's rollout margin; `target` is the next block, not the synced tip.
pub const ZIP2009_SWITCH_MARGIN: u32 = 100;
pub const EXPIRY_DELTA: u32 = 40;
pub const MIN_VALID_BLOCKS: u32 = 8;

/// The mainnet height sends move to the ZIP 2009 fee at, configured as Ledger Live
/// does: the host may delay or disable the switch, never advance it, since nodes
/// refuse the lower fee before the default height.
#[derive(Clone, Copy, Debug, PartialEq, Eq)]
pub enum Zip2009Switch {
    At(u32),
    Disabled,
}

impl Zip2009Switch {
    /// A host value: absent or malformed keeps the default, null disables.
    pub fn from_host(value: Option<Option<f64>>) -> Self {
        match value {
            Some(None) => Self::Disabled,
            Some(Some(height))
                if height.fract() == 0.0 && (0.0..=f64::from(u32::MAX)).contains(&height) =>
            {
                Self::At((height as u32).max(ZIP2009_MAINNET_HEIGHT))
            }
            _ => Self::default(),
        }
    }

    /// A host value as JSON: `null` disables, a height delays; absent or malformed
    /// keeps the default.
    pub fn from_host_json(json: Option<&str>) -> Self {
        Self::from_host(
            json.map(|json| serde_json::from_str::<Option<f64>>(json).unwrap_or(Some(f64::NAN))),
        )
    }

    fn applies_at(self, target: BlockHeight) -> bool {
        match self {
            Self::Disabled => false,
            Self::At(height) => {
                u32::from(target)
                    >= height
                        .max(ZIP2009_MAINNET_HEIGHT)
                        .saturating_add(ZIP2009_SWITCH_MARGIN)
            }
        }
    }
}

impl Default for Zip2009Switch {
    fn default() -> Self {
        Self::At(ZIP2009_MAINNET_HEIGHT)
    }
}

thread_local! {
    // Runtime-wide, like the node URL, so every fee use, vendored ones included,
    // agrees.
    static ZIP2009: Cell<Zip2009Switch> =
        const { Cell::new(Zip2009Switch::At(ZIP2009_MAINNET_HEIGHT)) };
}

/// Sets the switch every later fee in this runtime uses.
pub fn set_zip2009_switch(switch: Zip2009Switch) {
    ZIP2009.with(|cell| cell.set(switch));
}

pub fn zip2009_switch() -> Zip2009Switch {
    ZIP2009.with(Cell::get)
}

/// Test networks always price at the ZIP 2009 fee.
pub fn marginal_fee<P: Parameters>(params: &P, target: BlockHeight) -> Zatoshis {
    if params.network_type() != NetworkType::Main || zip2009_switch().applies_at(target) {
        Zatoshis::const_from_u64(1_000)
    } else {
        zip317::MARGINAL_FEE
    }
}

pub fn fee_rule<P: Parameters>(params: &P, target: BlockHeight) -> FeeRule {
    FeeRule::non_standard(
        marginal_fee(params, target),
        zip317::GRACE_ACTIONS,
        zip317::P2PKH_STANDARD_INPUT_SIZE,
        zip317::P2PKH_STANDARD_OUTPUT_SIZE,
    )
    .expect("standard ZIP-317 input and output sizes are nonzero")
}

pub fn expiry_height<P: Parameters>(params: &P, target: BlockHeight) -> Option<BlockHeight> {
    let target_u32 = u32::from(target);
    let expiry = target_u32.checked_add(EXPIRY_DELTA)?;
    let next_upgrade = BranchId::for_height(params, target)
        .height_bounds(params)
        .and_then(|(_, upper)| upper);
    let expiry = next_upgrade.map_or(expiry, |height| expiry.min(u32::from(height) - 1));
    (expiry.checked_sub(target_u32)? >= MIN_VALID_BLOCKS).then(|| BlockHeight::from_u32(expiry))
}

#[derive(serde::Serialize)]
#[serde(rename_all = "camelCase")]
pub struct TransactionPolicy {
    pub marginal_fee_zat: String,
    pub minimum_fee_zat: String,
    pub expiry_height: Option<u32>,
}

pub fn transaction_policy<P: Parameters>(params: &P, target: BlockHeight) -> TransactionPolicy {
    let marginal = u64::from(marginal_fee(params, target));
    TransactionPolicy {
        marginal_fee_zat: marginal.to_string(),
        minimum_fee_zat: (marginal * zip317::GRACE_ACTIONS as u64).to_string(),
        expiry_height: expiry_height(params, target).map(u32::from),
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use zcash_protocol::consensus::{NetworkUpgrade, MAIN_NETWORK, TEST_NETWORK};

    // Each test runs on its own thread, so the setting stays local to it.
    fn mainnet_fee(height: u32, zip2009: Zip2009Switch) -> u64 {
        set_zip2009_switch(zip2009);
        u64::from(fee_rule(&MAIN_NETWORK, BlockHeight::from_u32(height)).marginal_fee())
    }

    #[test]
    fn fees_switch_at_the_target_block() {
        for (height, expected) in [
            (3_589_999, 5_000),
            (3_590_000, 5_000),
            (3_590_099, 5_000),
            (3_590_100, 1_000),
        ] {
            assert_eq!(mainnet_fee(height, Zip2009Switch::default()), expected);
            set_zip2009_switch(Zip2009Switch::Disabled);
            assert_eq!(
                u64::from(marginal_fee(&TEST_NETWORK, BlockHeight::from_u32(height))),
                1_000
            );
        }
    }

    #[test]
    fn the_host_can_delay_or_disable_the_switch_but_not_advance_it() {
        let delayed = Zip2009Switch::from_host(Some(Some(3_700_000.0)));
        assert_eq!(mainnet_fee(3_700_099, delayed), 5_000);
        assert_eq!(mainnet_fee(3_700_100, delayed), 1_000);

        let advanced = Zip2009Switch::from_host(Some(Some(3_000_000.0)));
        assert_eq!(advanced, Zip2009Switch::default());
        assert_eq!(mainnet_fee(3_590_099, advanced), 5_000);
        assert_eq!(mainnet_fee(3_590_099, Zip2009Switch::At(1)), 5_000);

        let disabled = Zip2009Switch::from_host(Some(None));
        assert_eq!(mainnet_fee(u32::MAX, disabled), 5_000);
        // The margin saturates instead of wrapping to an early height.
        assert_eq!(mainnet_fee(3_590_100, Zip2009Switch::At(u32::MAX)), 5_000);
    }

    #[test]
    fn a_json_host_value_follows_the_same_rule() {
        assert_eq!(
            Zip2009Switch::from_host_json(None),
            Zip2009Switch::default()
        );
        assert_eq!(
            Zip2009Switch::from_host_json(Some("null")),
            Zip2009Switch::Disabled
        );
        assert_eq!(
            Zip2009Switch::from_host_json(Some("3700000")),
            Zip2009Switch::At(3_700_000)
        );
        assert_eq!(
            Zip2009Switch::from_host_json(Some("\"3700000\"")),
            Zip2009Switch::default()
        );
        assert_eq!(
            Zip2009Switch::from_host_json(Some("{")),
            Zip2009Switch::default()
        );
    }

    #[test]
    fn an_absent_or_malformed_height_keeps_the_default_switch() {
        for value in [
            None,
            Some(Some(f64::NAN)),
            Some(Some(-1.0)),
            Some(Some(3_600_000.5)),
        ] {
            assert_eq!(Zip2009Switch::from_host(value), Zip2009Switch::default());
        }
        assert_eq!(
            Zip2009Switch::from_host(Some(Some(f64::from(u32::MAX) + 1.0))),
            Zip2009Switch::default()
        );
    }

    #[test]
    fn the_runtime_starts_on_the_default_switch() {
        assert_eq!(zip2009_switch(), Zip2009Switch::default());
    }

    #[test]
    fn expiry_stays_in_the_current_consensus_epoch() {
        let upgrade = MAIN_NETWORK
            .activation_height(NetworkUpgrade::Nu6_3)
            .unwrap();
        let target = upgrade - 20;
        assert_eq!(expiry_height(&MAIN_NETWORK, target), Some(upgrade - 1));
        assert_eq!(expiry_height(&MAIN_NETWORK, upgrade - 9), Some(upgrade - 1));
        assert_eq!(expiry_height(&MAIN_NETWORK, upgrade - 8), None);
        assert_eq!(
            expiry_height(&MAIN_NETWORK, upgrade),
            Some(upgrade + EXPIRY_DELTA)
        );
    }

    #[test]
    fn overflow_cannot_create_a_nonexpiring_transaction() {
        assert_eq!(
            expiry_height(&MAIN_NETWORK, BlockHeight::from_u32(u32::MAX)),
            None
        );
        let policy = transaction_policy(&MAIN_NETWORK, BlockHeight::from_u32(3_590_100));
        assert_eq!(policy.minimum_fee_zat, "2000");
        assert_eq!(policy.expiry_height, Some(3_590_140));
    }
}
