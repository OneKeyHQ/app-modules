//! Immutable public circuit parameters, shared by proving and extraction.

use std::sync::OnceLock;

use orchard::circuit::{OrchardCircuitVersion, ProvingKey, VerifyingKey};
use zcash_primitives::transaction::builder::cached_orchard_proving_key;

use crate::error::{ErrorCode, Result, RuntimeError};

// Each WASM instance owns its cache. Native callers share these thread-safe
// public parameters; wallet data, witnesses, and spending keys never enter it.
static HISTORICAL_VERIFYING: OnceLock<VerifyingKey> = OnceLock::new();
static FIXED_VERIFYING: OnceLock<VerifyingKey> = OnceLock::new();
static CURRENT_VERIFYING: OnceLock<VerifyingKey> = OnceLock::new();

pub fn proving_key(version: OrchardCircuitVersion) -> Result<&'static ProvingKey> {
    match version {
        OrchardCircuitVersion::FixedPostNu6_2 | OrchardCircuitVersion::PostNu6_3 => {}
        OrchardCircuitVersion::InsecurePreNu6_2 => {
            return Err(RuntimeError::new(ErrorCode::Unimplemented)
                .detail("Historical insecure circuits are verification-only"));
        }
    }
    Ok(cached_orchard_proving_key(version))
}

pub fn verifying_key(version: OrchardCircuitVersion) -> &'static VerifyingKey {
    let slot = match version {
        OrchardCircuitVersion::InsecurePreNu6_2 => &HISTORICAL_VERIFYING,
        OrchardCircuitVersion::FixedPostNu6_2 => &FIXED_VERIFYING,
        OrchardCircuitVersion::PostNu6_3 => &CURRENT_VERIFYING,
    };
    slot.get_or_init(|| VerifyingKey::build(version))
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn public_parameters_are_shared_and_isolated_by_circuit_version() {
        for version in [
            OrchardCircuitVersion::FixedPostNu6_2,
            OrchardCircuitVersion::PostNu6_3,
        ] {
            let proving = proving_key(version).unwrap();
            let verifying = verifying_key(version);
            assert!(std::ptr::eq(proving, cached_orchard_proving_key(version)));
            assert_eq!(proving.circuit_version(), version);
            assert_eq!(verifying.circuit_version(), version);
            std::thread::scope(|scope| {
                for _ in 0..4 {
                    scope.spawn(move || {
                        assert!(std::ptr::eq(proving, proving_key(version).unwrap()));
                        assert!(std::ptr::eq(verifying, verifying_key(version)));
                    });
                }
            });
        }
        assert!(!std::ptr::eq(
            proving_key(OrchardCircuitVersion::FixedPostNu6_2).unwrap(),
            proving_key(OrchardCircuitVersion::PostNu6_3).unwrap(),
        ));
        assert!(!std::ptr::eq(
            verifying_key(OrchardCircuitVersion::FixedPostNu6_2),
            verifying_key(OrchardCircuitVersion::PostNu6_3),
        ));
    }

    #[test]
    fn insecure_historical_circuit_cannot_be_used_for_proving() {
        assert_eq!(
            proving_key(OrchardCircuitVersion::InsecurePreNu6_2)
                .unwrap_err()
                .code,
            ErrorCode::Unimplemented,
        );
    }
}
