# Native Module Development Requirements

## 1. Purpose and scope

This document defines the required workflow for packages under
`native-modules/**` and `native-views/**`. It applies to humans and coding
agents. A package's `docs/SPEC.md` is the authoritative behavioral contract for
that package; this document defines how that contract is created and maintained.

The requirements are enforced by repository instructions and review. Generated
bindings, tests, and builds verify parts of a contract, but none of them replace
the written cross-platform specification.

The words **MUST**, **MUST NOT**, **SHOULD**, and **MAY** are normative.

## 2. Definitions

- **Native module:** a non-visual Nitro package under `native-modules/**`.
- **Native view:** a Nitro-backed React Native view under `native-views/**`.
- **Public contract:** exported TypeScript types and functions, native bridge
  methods and props, callbacks, defaults, errors, and observable side effects.
- **Behavioral contract:** lifecycle, ordering, cancellation, threading,
  ownership, caching, persistence, resource limits, and platform semantics that
  callers can observe or must rely on.
- **Boundary:** where data, ownership, execution, or responsibility moves between
  JavaScript, generated bindings, native code, an operating system, or a third
  party library.
- **Divergence:** an intentional or existing difference between platforms.
- **Acceptance evidence:** a check that exercises the real layer named by the
  spec. Source inspection or a TypeScript test is not native runtime evidence.

## 3. Start from the repository template

New packages MUST be initialized from the checked-in template:

```sh
# Creates native-modules/<package-name>
yarn create:module <package-name>

# Creates native-views/react-native-<view-name>
yarn create:view <view-name>
```

The generated structure is the starting point. A developer MUST NOT construct a
new package by copying an existing package or assembling its build files by hand.
Existing packages contain historical and product-specific decisions that are not
general defaults.

If the template cannot support a required package, document the concrete gap in
the package spec. Update the shared template in a separate, reviewable change or
record an explicit exception in the spec; do not silently fork its conventions.

Before implementation begins, add `docs/SPEC.md` to the generated package and
link it from the package README.

## 4. Spec-first development

Every new package MUST have `docs/SPEC.md`. An existing package MUST add one when
work introduces or changes a behavioral contract. Write or update the relevant
contract before changing implementation so reviewers can assess the intended
behavior independently from the code.

The spec MUST distinguish requirements from implementation status. Use clear
labels such as `Proposed`, `Implemented`, and `Runtime verified`; do not describe
source-complete behavior as device-verified.

An implementation detail MAY stay out of the spec only when changing it cannot
affect callers, resource ownership, platform parity, safety, or compatibility.

## 5. Required contents of `docs/SPEC.md`

Use the sections below when applicable. A section may say `Not applicable` with
a reason; it MUST NOT be silently omitted when the concern exists.

### 5.1 Purpose, scope, and non-goals

- The problem the package owns.
- Supported callers and platforms.
- Explicit non-goals and work owned by consumers or another package.

### 5.2 Definitions and ownership boundaries

- Terms whose meaning affects behavior.
- Ownership of state, requests, threads, resources, and cleanup.
- JavaScript/native, module/application, and third party library boundaries.
- What the package MUST NOT infer from product-specific data.

### 5.3 Public API and defaults

- Exported types, props, methods, events, and return values.
- Default values, null/empty behavior, validation, and precedence.
- Serialization rules and stable identifiers crossing the native boundary.

### 5.4 Lifecycle and concurrency

- Initialization, update, cancellation, disposal, and reuse.
- Callback/event ordering and terminal-event ownership.
- Thread or actor requirements, concurrent work limits, and stale-result rules.

### 5.5 Data, cache, and identity

- Cache/request/persistence keys and every input that changes identity.
- Memory and disk ownership, invalidation, and stale-entry behavior.
- Migration or compatibility rules when formats or keys change.

### 5.6 Platform contract

- A shared semantic contract for iOS, Android, and Web where supported.
- A platform matrix mapping the shared behavior to each implementation.
- Known divergences, their reason, and whether they are accepted or pending.

### 5.7 Failure, fallback, and safety

- Invalid input, unavailable resources, retries, fallbacks, and error reporting.
- Hard limits for bytes, dimensions, counts, duration, or retained state.
- Cleanup behavior after failure, cancellation, recycling, and cache clearing.

### 5.8 Performance and resource budget

- Work allowed on the main/UI thread.
- Bounded memory, I/O, bridge calls, and background concurrency.
- The workload and metric used for any performance claim.

### 5.9 Conformance and acceptance

- Source locations that implement each major contract.
- Focused automated checks and the behavior they prove.
- Required runtime cases per platform, including reuse, rapid updates, failure,
  and cleanup when relevant.
- Known gaps and evidence still required.

## 6. Implementation rules

- Shared public semantics MUST be named once in the spec even when each platform
  has an independent implementation.
- Platform-specific library terms MUST be translated into the shared API rather
  than leaked as accidental public behavior.
- A bounded native structure MUST state its numeric bounds and eviction or
  rejection policy.
- Cancellation and recycling MUST define which owner may still write state or
  emit callbacks.
- New public behavior MUST include focused regression coverage at the lowest
  layer that can prove it. Do not use a source-only test to claim rendered or
  device behavior.
- Generated files MUST follow the repository's tracked/generated-file policy;
  do not hand-edit generated bindings.

## 7. Required post-change audit

After any native package change, the author or agent MUST perform this audit:

- [ ] Re-read the affected `docs/SPEC.md` after the code change.
- [ ] Map every changed public or observable behavior to a spec section.
- [ ] Check defaults, null behavior, event ordering, cancellation, reuse, and
      cleanup for unintended changes.
- [ ] Compare all supported platforms and record intentional divergence.
- [ ] Decide whether the spec changed. If yes, update it in the same change. If
      no, state that the implementation remains within the existing contract.
- [ ] Run the smallest relevant type, unit, native build, and runtime checks.
- [ ] Report what was verified and what remains unverified without upgrading
      source evidence into runtime evidence.

For a newly generated package, also compare its structure with the current
template and explain every intentional deviation.

## 8. Review gate

A native change is not ready for review when its observable behavior cannot be
located in a spec, when code and spec disagree, or when a platform divergence is
hidden. Reviewers SHOULD request a spec update before reviewing implementation
details in those cases.
