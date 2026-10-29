//! Shared transaction policy for the wallet runtime, stateless signer and host UI.

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

pub fn marginal_fee<P: Parameters>(params: &P, target: BlockHeight) -> Zatoshis {
    if params.network_type() != NetworkType::Main
        || u32::from(target) >= ZIP2009_MAINNET_HEIGHT + ZIP2009_SWITCH_MARGIN
    {
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

    #[test]
    fn fees_switch_at_the_target_block() {
        for (height, expected) in [
            (3_589_999, 5_000),
            (3_590_000, 5_000),
            (3_590_099, 5_000),
            (3_590_100, 1_000),
        ] {
            let target = BlockHeight::from_u32(height);
            assert_eq!(
                u64::from(fee_rule(&MAIN_NETWORK, target).marginal_fee()),
                expected
            );
            assert_eq!(u64::from(marginal_fee(&TEST_NETWORK, target)), 1_000);
        }
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
