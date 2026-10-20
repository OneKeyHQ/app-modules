package com.margelo.nitro.onekeyimage

import kotlin.math.max
import kotlin.math.min

internal data class OneKeyImageMemoryFamilyKey(
  val rawUrl: String,
  val headersDigest: String?,
) {
  companion object {
    fun from(rawUrl: String, headersJson: String?): OneKeyImageMemoryFamilyKey =
      OneKeyImageMemoryFamilyKey(
        rawUrl = rawUrl,
        headersDigest = OneKeyImageModel.remoteHeadersDigest(
          OneKeyImageModel.headers(headersJson),
        ),
      )
  }
}

internal data class OneKeyImageMemoryVariant(
  val requestUrl: String,
  val width: Int,
  val height: Int,
  val round: Boolean,
) {
  val area: Long
    get() = width.toLong() * height.toLong()
}

/**
 * A bounded index of size-specific Glide resources that may still be in memory.
 *
 * Glide remains the source of truth. Entries are only hints used to reproduce a
 * candidate EngineKey; a synchronous cache miss removes the stale hint.
 */
internal object OneKeyImageMemoryVariantRegistry {
  private const val MAX_FAMILIES = 1024
  private const val MAX_VARIANTS_PER_FAMILY = 8
  private const val MAX_ASPECT_RATIO_DRIFT = 1.1

  private val variantsByFamily =
    LinkedHashMap<OneKeyImageMemoryFamilyKey, MutableList<OneKeyImageMemoryVariant>>(
      16,
      0.75f,
      true,
    )

  @Synchronized
  fun record(family: OneKeyImageMemoryFamilyKey, variant: OneKeyImageMemoryVariant) {
    if (variant.width <= 0 || variant.height <= 0) return
    val variants = variantsByFamily.getOrPut(family) { mutableListOf() }
    variants.remove(variant)
    variants.add(variant)
    while (variants.size > MAX_VARIANTS_PER_FAMILY) variants.removeAt(0)
    while (variantsByFamily.size > MAX_FAMILIES) {
      val eldest = variantsByFamily.entries.firstOrNull()?.key ?: break
      variantsByFamily.remove(eldest)
    }
  }

  @Synchronized
  fun remove(family: OneKeyImageMemoryFamilyKey, variant: OneKeyImageMemoryVariant) {
    val variants = variantsByFamily[family] ?: return
    variants.remove(variant)
    if (variants.isEmpty()) variantsByFamily.remove(family)
  }

  @Synchronized
  fun candidates(
    family: OneKeyImageMemoryFamilyKey,
    target: OneKeyImageMemoryVariant,
  ): List<OneKeyImageMemoryVariant> = orderedCandidates(
    variants = variantsByFamily[family].orEmpty(),
    target = target,
  )

  @Synchronized
  fun clear() {
    variantsByFamily.clear()
  }

  internal fun orderedCandidates(
    variants: List<OneKeyImageMemoryVariant>,
    target: OneKeyImageMemoryVariant,
  ): List<OneKeyImageMemoryVariant> {
    val compatible = variants
      .asSequence()
      .filter { it != target }
      .filter { candidate -> target.round || !candidate.round }
      .filter { candidate -> aspectRatioIsCompatible(candidate, target) }
      .distinct()
      .toList()

    val larger = compatible
      .filter { it.width >= target.width && it.height >= target.height }
      .minWithOrNull(
        compareBy<OneKeyImageMemoryVariant> { it.area }
          .thenBy { if (it.round == target.round) 0 else 1 },
      )
    val smaller = compatible
      .filter { it.width <= target.width && it.height <= target.height }
      .maxWithOrNull(
        compareBy<OneKeyImageMemoryVariant> { it.area }
          .thenByDescending { if (it.round == target.round) 0 else 1 },
      )
    return listOfNotNull(larger, smaller).distinct()
  }

  private fun aspectRatioIsCompatible(
    candidate: OneKeyImageMemoryVariant,
    target: OneKeyImageMemoryVariant,
  ): Boolean {
    val candidateRatio = candidate.width.toDouble() / candidate.height.toDouble()
    val targetRatio = target.width.toDouble() / target.height.toDouble()
    return max(candidateRatio, targetRatio) / min(candidateRatio, targetRatio) <=
      MAX_ASPECT_RATIO_DRIFT
  }
}
