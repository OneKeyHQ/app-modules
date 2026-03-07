package com.rcttabview

data class TabInfo(
  val key: String,
  val title: String,
  val badge: String? = null,
  val badgeBackgroundColor: Int? = null,
  val badgeTextColor: Int? = null,
  val activeTintColor: Int? = null,
  val hidden: Boolean = false,
  val testID: String? = null
)
