# OneKey app modules Socket 评分审计

目标是全部 43 个发布库的五项官方分类分数分别严格大于 90；90 不算达标。当前已发布版本全部为 3.0.165，0/43 满足该目标。本地改动与打包不产生新的官方分数，复测栏仍为未发布、未复测。算术平均值仅供对照，不是 Socket 官方 Overall 或 Deep。

## 基线与范围

- SDK 基线：`f11ac08bf1278a31345829d9e245015d52035df4`，包含已合并 [SDK PR 139](https://github.com/OneKeyHQ/app-modules/pull/139)。本地分支 `codex/socket-all-package-scores`，外置 worktree `/Volumes/T7Shield/Project/app-modules-socket-all-package-scores`。工作区枚举为 30 个 native modules 和 13 个 native views，全部非 private；与 43 个 npm 审计包名称逐一匹配。
- [App PR 13852](https://github.com/OneKeyHQ/app-monorepo/pull/13852) 最终核对 head 为 `3d7b0d596de2217e193a51962ff15a0bcd56baae`，当前 [Socket SBOM](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9) 为 4,778 dependencies、552 direct dependencies、70 manifest files。最新扫描的43 SDK全部为3.0.165。
- 历史评分快照为head `3a9cf6865106956728a093028b5ee124947b2c50` / scan `d478eb5c-9504-42f6-a0a3-ecfd133b607c`，以及head `5b2d600922d1c114006fad7b6904c4dd077a797b` / scan `5fc53fd0-85cd-4752-b0a6-f4e8b056368a`。前次仅Setting页面一行变化；最终5b2d600→3d7b0d5仅ScanQrCodeModal.tsx的4行日志变化，没有manifest/lock或字体配置差异。每次head更新后均重新读取全部43包分类和Overall列，结果一致；本表包详情URL来自最终scan，历史快照独立保留。没有将本地源码修改当作新官方分析。
- 用户最初提供的 diff scan 对应旧 head `441028184b0e5e69e11f835638b5530440f8ebaa`，不是当前基线。PR 评论中的分数变化量没有用于本表。
- 官方网页分数采集时间：`2026-10-10T15:48:56.792Z`。GitHub Socket Project Report check 完成于 `2026-10-10T15:31:34Z`；这不是已证实的 Socket 服务端扫描完成时间，后者未知。
- 每包五项分数来自基线 SBOM 的包详情，Overall 来自同一扫描的 Overall Score 列；没有以平均值或最小值补 Overall。具体包详情 URL、npm tarball URL、发布日期、尺寸与完整性检查见 [基线 JSON](socket-package-baseline.json)。
- Deep 未取得：登录网页没有相应字段，用户没有 API token。Socket 官方 `package score` / `shallow` 需要 `packages:list` 权限，不能离线生成官方结果。取得权限后，应固定完整包名与版本并使用 `--json`，避免终端截断。[CLI 说明](https://docs.socket.dev/docs/socket-package)

原始网页观察、全部 active alert groups 和 npm 查询保存在本 worktree 被 Git 忽略的 `.tmp/socket-baseline/` 中。`final-sdk-official.json` 保留最终head逐包详情，`final-active-alerts.json` 保留最终扫描全部加载后的告警组，`final-sdk-alert-filter.json`保留名称筛选；latest-/current-prefixed文件保留历史快照；它们不随 npm 发布。

## 逐库对照

表中 SC、Q、M、V、L 分别为供应链、质量、维护、漏洞、许可证。每项均为官方网页已发布版本的绝对分数。“未过线项”只列数值 ≤90 的项，不等同于已确认的扣分原因。本轮本地改动对应的新 SDK 版本尚未发布，因而没有该版本的官方复测结果。

| 包名 | 版本 | SC/Q/M/V/L | 算术均值 | 官方 Overall | 官方 Deep | 自身/传递告警 | 本地改动 | 构建/pack | 新官方复测 | 未过线项 | 达标 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| [@onekeyfe/react-native-native-logger](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528556) | 3.0.165 | 77/83/96/100/100 | 91.2 | 77 | 未知 | 网页无 / 未知 | G+D | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-aes-crypto](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528543) | 3.0.165 | 78/90/96/100/100 | 92.8 | 78 | 未知 | 未知 / 未知 | G+D+L | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-app-update](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528564) | 3.0.165 | 78/86/96/100/100 | 92.0 | 78 | 未知 | 未知 / 未知 | G+D | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-async-storage](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528574) | 3.0.165 | 81/93/96/100/100 | 94.0 | 81 | 未知 | 未知 / 未知 | G+L | 通过/通过 | 未发布；未复测 | SC | 否，基线 |
| [@onekeyfe/react-native-background-thread](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528547) | 3.0.165 | 79/86/96/100/100 | 92.2 | 79 | 未知 | 未知 / 未知 | G+D | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-bundle-crypto](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528565) | 3.0.165 | 77/96/96/100/100 | 93.8 | 77 | 未知 | 未知 / 未知 | G+D+M+L | 通过/通过 | 未发布；未复测 | SC | 否，基线 |
| [@onekeyfe/react-native-bundle-update](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528562) | 3.0.165 | 79/86/96/100/100 | 92.2 | 79 | 未知 | 未知 / 未知 | G+D | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-capture-protection](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528884) | 3.0.165 | 72/76/88/100/100 | 87.2 | 72 | 未知 | 网页无 / 未知 | G+D | 通过/通过 | 未发布；未复测 | SC,Q,M | 否，基线 |
| [@onekeyfe/react-native-check-biometric-auth-changed](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528550) | 3.0.165 | 78/83/96/100/100 | 91.4 | 78 | 未知 | 未知 / 未知 | G+D | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-cloud-fs](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528720) | 3.0.165 | 79/89/96/100/100 | 92.8 | 79 | 未知 | 未知 / 未知 | G+D+L | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-cloud-kit-module](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528561) | 3.0.165 | 79/86/96/100/100 | 92.2 | 79 | 未知 | 未知 / 未知 | G+D | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-device-utils](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960529502) | 3.0.165 | 79/85/96/100/100 | 92.0 | 79 | 未知 | 未知 / 未知 | G+D | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-dns-lookup](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960529250) | 3.0.165 | 74/85/96/100/100 | 91.0 | 74 | 未知 | 未知 / 未知 | G+D+L | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-get-random-values](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960529550) | 3.0.165 | 79/93/96/100/100 | 93.6 | 79 | 未知 | 未知 / 未知 | G | 通过/通过 | 未发布；未复测 | SC | 否，基线 |
| [@onekeyfe/react-native-image-crop-picker](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528873) | 3.0.165 | 78/100/96/100/100 | 94.8 | 78 | 未知 | 未知 / 未知 | G+D | 通过/通过 | 未发布；未复测 | SC | 否，基线 |
| [@onekeyfe/react-native-keychain-module](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528722) | 3.0.165 | 78/84/96/100/100 | 91.6 | 78 | 未知 | 未知 / 未知 | G+D | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-lite-card](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528549) | 3.0.165 | 83/88/96/100/100 | 93.4 | 83 | 未知 | 未知 / 未知 | G+D | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-network-info](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528560) | 3.0.165 | 74/88/96/100/100 | 91.6 | 74 | 未知 | 未知 / 未知 | G+D+L | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-network-throttle](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960529515) | 3.0.165 | 77/85/96/100/100 | 91.6 | 77 | 未知 | 未知 / 未知 | G+D | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-pbkdf2](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960529503) | 3.0.165 | 74/86/96/100/100 | 91.2 | 74 | 未知 | 未知 / 未知 | G+D+L | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-perf-memory](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960529499) | 3.0.165 | 77/82/96/100/100 | 91.0 | 77 | 未知 | 未知 / 未知 | G+D | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-perf-stats](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528559) | 3.0.165 | 80/93/96/100/100 | 93.8 | 80 | 未知 | 未知 / 未知 | G | 通过/通过 | 未发布；未复测 | SC | 否，基线 |
| [@onekeyfe/react-native-photo-library](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960529061) | 3.0.165 | 72/76/88/100/100 | 87.2 | 72 | 未知 | 网页无 / 未知 | G+D | 通过/通过 | 未发布；未复测 | SC,Q,M | 否，基线 |
| [@onekeyfe/react-native-ping](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528536) | 3.0.165 | 74/85/96/100/100 | 91.0 | 74 | 未知 | 未知 / 未知 | G+D+M+L | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-range-downloader](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528726) | 3.0.165 | 79/91/96/100/100 | 93.2 | 79 | 未知 | 未知 / 未知 | G | 通过/通过 | 未发布；未复测 | SC | 否，基线 |
| [@onekeyfe/react-native-sni-connect](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528737) | 3.0.165 | 79/100/96/100/100 | 95.0 | 79 | 未知 | 未知 / 未知 | G+L | 通过/通过 | 未发布；未复测 | SC | 否，基线 |
| [@onekeyfe/react-native-splash-screen](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528724) | 3.0.165 | 77/90/96/100/100 | 92.6 | 77 | 未知 | 未知 / 未知 | G+D | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-split-bundle-loader](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960529501) | 3.0.165 | 75/82/96/100/100 | 90.6 | 75 | 未知 | 未知 / 未知 | G+D+L | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-tcp-socket](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528987) | 3.0.165 | 77/89/96/100/100 | 92.4 | 77 | 未知 | 未知 / 未知 | G+D+L | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-zip-archive](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528735) | 3.0.165 | 78/90/96/100/100 | 92.8 | 78 | 未知 | 未知 / 未知 | G+D+L | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-auto-size-input](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528730) | 3.0.165 | 79/87/96/100/100 | 92.4 | 79 | 未知 | 未知 / 未知 | G+D+L | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-chart-webview](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528732) | 3.0.165 | 80/92/96/100/100 | 93.6 | 80 | 未知 | 未知 / 未知 | G | 通过/通过 | 未发布；未复测 | SC | 否，基线 |
| [@onekeyfe/react-native-image](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960529267) | 3.0.165 | 80/100/96/100/100 | 95.2 | 80 | 未知 | 未知 / 未知 | G | 通过/通过 | 未发布；未复测 | SC | 否，基线 |
| [@onekeyfe/react-native-native-list](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960530251) | 3.0.165 | 84/100/96/100/100 | 96.0 | 84 | 未知 | 未知 / 未知 | G+D+M+L+F | 通过/通过 | 未发布；未复测 | SC | 否，基线 |
| [@onekeyfe/react-native-native-sheet](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960528710) | 3.0.165 | 80/97/96/100/100 | 94.6 | 80 | 未知 | 未知 / 未知 | G | 通过/通过 | 未发布；未复测 | SC | 否，基线 |
| [@onekeyfe/react-native-pager-view](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960529297) | 3.0.165 | 84/100/96/100/100 | 96.0 | 84 | 未知 | 未知 / 未知 | G | 通过/通过 | 未发布；未复测 | SC | 否，基线 |
| [@onekeyfe/react-native-perp-depth-bar](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960529251) | 3.0.165 | 80/97/96/100/100 | 94.6 | 80 | 未知 | 未知 / 未知 | G | 通过/通过 | 未发布；未复测 | SC | 否，基线 |
| [@onekeyfe/react-native-scroll-guard](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960529498) | 3.0.165 | 79/86/96/100/100 | 92.2 | 79 | 未知 | 未知 / 未知 | G+D+L | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-segment-slider](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960529524) | 3.0.165 | 79/93/96/100/100 | 93.6 | 79 | 未知 | 未知 / 未知 | G | 通过/通过 | 未发布；未复测 | SC | 否，基线 |
| [@onekeyfe/react-native-skeleton](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960529252) | 3.0.165 | 79/84/96/100/100 | 91.8 | 79 | 未知 | 未知 / 未知 | G+D | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-tab-view](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960529508) | 3.0.165 | 82/95/96/100/100 | 94.6 | 82 | 未知 | 未知 / 未知 | G+L | 通过/通过 | 未发布；未复测 | SC | 否，基线 |
| [@onekeyfe/react-native-text](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960529531) | 3.0.165 | 80/87/96/100/100 | 92.6 | 80 | 未知 | 未知 / 未知 | G+D+L | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |
| [@onekeyfe/react-native-text-input](https://socket.dev/dashboard/org/OneKeyHQ/sbom/037b1960-d024-4b7b-9fb4-63b9a6590cc9?tab=dependencies&dependency_item_key=101960529519) | 3.0.165 | 83/87/96/100/100 | 93.2 | 83 | 未知 | 未知 / 未知 | G+D+L | 通过/通过 | 未发布；未复测 | SC,Q | 否，基线 |

G=发布 workflow provenance 准备（全部43包）；D=有源码依据的 README 补充/纠错；L=新许可证/署名；M=准确许可元数据；F=移除SDK付费字体资源，保留宿主字体读取。构建指 Nitrogen、JS 和声明文件，不是 iOS/Android 编译。pack 为完成发布构建步骤后的真实 tarball；缺少任何平台/设备验收都没有按通过填充。

## 扣分依据与改动边界

43 个包的 SC 为 72–84，28 个包 Q≤90，photo-library 与 capture-protection 的 M 为 88。V/L 原始基线均为 100。这些是官方观测，不能证明包自身或整个 App 没有风险。

Socket 公开说明包含 README、包体积、依赖数量、下载量、项目热度及发布/维护历史等输入，也说明算法会调整。因此具体每包扣分权重与根因仍待确认：文档缺口、缺少物理 LICENSE 或 provenance 不能直接解释为已证实的官方扣分项，更不能量化预测修复后的分数。[评分说明](https://docs.socket.dev/docs/package-scores)

官方 npm downloads API 查询 43 包 last-month：38 个成功，2 个 HTTP404（photo/capture），3 个 HTTP429（tab-view/text/text-input），失败不按零填充。成功值为 11,428–25,609，API 返回日期窗为 2026-09-09 至 2026-10-08。它们是所有版本的包级下载量，未证实 Socket 使用相同时间窗或数据；不能统一归因于“低下载量”。[npm API 定义](https://raw.githubusercontent.com/npm/registry/main/docs/download-counts.md)

改动按实际证据处理三个方面：README 说明真实安装、公开 API、失败与平台边界；缺失许可证恢复原声明并保留第三方署名；发布 workflow 准备 provenance。README 对应官方公开的质量输入，但涨分待复测。原 L100 包补许可证是修复分发缺口，不宣称一定提升许可证分数。provenance 提供来源链接，不能证明无恶意代码或保证 SC>90。

原发布产物的 README、repository.directory、homepage、bugs 已包含 PR141 的改动，未重复修改这些元数据。43 包 SHA512 tarball integrity 均匹配 registry；均没有 preinstall/install/postinstall，均有 registry signatures，但未见 dist.attestations。signature 与 provenance 不相同。

## 版权来源与发布条件

本轮恢复14份根LICENSE和1份GBPing Apache声明，另在bundle-crypto、text、text-input补齐已确认的第三方原文。43份实际tarball均检查manifest、实体notice、字体/图像/framework成员及源码归属头；完整二进制链接闭包、所有历史作者与消费端Pod/Gradle/npm传递依赖未获完整授权审计。不能据此宣布43包全部可再次分发。

| 包目录 | 文件 | 已核验来源与限制 |
| --- | --- | --- |
| native-modules/react-native-aes-crypto | LICENSE | exact upstream notice restored; [source](https://github.com/tectiv3/react-native-aes/blob/master/LICENSE) |
| native-modules/react-native-dns-lookup | LICENSE | exact upstream notice restored; [source](https://github.com/tableau/react-native-dns-lookup/blob/master/LICENSE) |
| native-modules/react-native-network-info | LICENSE | exact upstream notice restored; [source](https://github.com/cwilsn/react-native-network-info/blob/master/LICENSE) |
| native-modules/react-native-pbkdf2 | LICENSE | exact upstream notice restored; [source](https://github.com/PublicaIO/react-native-pbkdf2/blob/master/LICENSE) |
| native-modules/react-native-tcp-socket | LICENSE | exact upstream notice restored; [source](https://github.com/Rapsssito/react-native-tcp-socket/blob/master/LICENSE) |
| native-modules/react-native-zip-archive | LICENSE | exact upstream notice restored; [source](https://github.com/mockingbot/react-native-zip-archive/blob/master/LICENSE) |
| native-modules/react-native-async-storage | LICENSE | exact upstream notice restored; [source](https://raw.githubusercontent.com/react-native-async-storage/async-storage/v1.23.1/LICENSE), [source](https://raw.githubusercontent.com/necolas/react-native-web/0.19.13/LICENSE) |
| native-modules/react-native-cloud-fs | LICENSE | exact upstream notice retains unresolved year/fullname placeholders; [source](https://github.com/npomfret/react-native-cloud-fs/blob/master/licesnse.txt) |
| native-views/react-native-tab-view | LICENSE | exact upstream notice restored; [source](https://github.com/callstack/react-native-bottom-tabs/blob/main/LICENSE), [source](https://raw.githubusercontent.com/expo/expo/d516432a626ef37de1ae750ffc3b47a142a63f17/LICENSE) |
| native-modules/react-native-sni-connect | LICENSE | repository template MIT notice restored for OneKey-owned implementation; [source](../scripts/nitro-view/template/docs/LICENSE) |
| native-modules/react-native-split-bundle-loader | LICENSE | repository template MIT notice restored for OneKey-owned implementation; [source](../scripts/nitro-view/template/docs/LICENSE) |
| native-views/react-native-auto-size-input | LICENSE | repository template MIT notice restored for OneKey-owned implementation; [source](../scripts/nitro-view/template/docs/LICENSE) |
| native-views/react-native-scroll-guard | LICENSE | repository template MIT notice restored for OneKey-owned implementation; [source](../scripts/nitro-view/template/docs/LICENSE) |
| native-views/react-native-native-list | LICENSE | repository MIT notice and accurately scoped MyCrypto attribution; [source](../scripts/nitro-view/template/docs/LICENSE), [source](https://github.com/MyCryptoHQ/ethereum-blockies-base64/tree/1290187d9c099335a06a2f867d5240a6034a6631) |
| native-modules/react-native-ping | LICENSE.GBPing | exact vendored Apache-2.0 notice restored; upstream RoJoHub MIT grant file remains unavailable; [source](https://raw.githubusercontent.com/lmirosevic/GBPing/bb1156f1c4425981f4cf495952623935d6e124d2/LICENSE) |

已发布 native-list@3.0.165 包含4个Roobert字体，字体内嵌声明识别 Displaay Type Foundry 与 Martin Vácha，并限制再分发。现已取得[厂商官方价格](https://displaay.net/typeface/roobert)与[许可条款](https://displaay.net/help/licenses)：Roobert 属于付费零售字体，单样式标价 €55 起；App/Game 许可仅允许在许可主体的应用中嵌入，不能作为公开 npm 原始字体再分发的授权证据。免费字体类别未包含 Roobert，trial 限内部评估。仓库未找到单独覆盖 npm 再分发的授权，不能用 OneKey 的 MIT 声明替代厂商许可。按用户明确授权，已移除 SDK 的4个TTF、Pod/Android打包资源声明与SDK/示例的字体注册、font-face文件引用；保留宿主App的字体查找能力。最终NativeList tarball 为530,553字节（移除前1,137,509字节），字体文件数量为0，图标保留。历史已发布 tarball 与 Git 历史不会因本地删除自动更新；本轮不做 npm unpublish 或历史改写。这里不作历史侵权结论。原始name-table记录及4个SHA256见本地 `.tmp/socket-execution/font-license-audit.json`，官方条款观察见 `roobert-official-license-audit.json`。

ping 保存实际GBPing的Apache-2.0许可与署名，manifest 改为 `MIT AND Apache-2.0` 并显式包含 LICENSE.GBPing；RoJoHub 的 MIT 主体声明仅找到元数据，未恢复到物理许可证原文。cloud-fs 原上游 MIT 原文保留 `[year]` / `[fullname]` 占位符，未伪填版权作者。这两项需要向相应上游取得完整授权/署名证据。异构许可更正可能改变新 Socket License 分数，复扫前未知；不能为了保留旧100分误标。

async-storage保留历史Facebook与NicolasGallagher的原文；tab-view保存Expo SDK52的650 Industries声明；native-list的MyCrypto算法出处与未包含的pnglib区分说明。逐文件来源URL、blob SHA和内容SHA256保存在 `.tmp/socket-execution/license-primary/restored-notices.json`；NOTICE不存在时没有编造。

已补齐的后续许可缺口：bundle-crypto 根LICENSE限定OneKey自有源码范围，并附Gopenpgp v3.4.1的Proton MIT与x/mobile 4776eadac327的Go BSD原文；manifest改为 `SEE LICENSE IN LICENSE`。只确认Catalyst的记录版本，其他slice版本与完整链接依赖闭包仍未知，三个二进制SHA256保持不变。[Proton许可](https://raw.githubusercontent.com/ProtonMail/gopenpgp/v3.4.1/LICENSE)、[Go绑定许可](https://raw.githubusercontent.com/golang/mobile/4776eadac327/LICENSE)。text与text-input保留原OneKey声明，追加React Native衍生代码的完整Meta MIT；来源核对v0.86.2，不把适配文件说成完全复制该tag。[Meta许可](https://raw.githubusercontent.com/facebook/react-native/v0.86.2/LICENSE)。准确原文、来源SHA、实际tgz与Pod验证见 `.tmp/socket-execution/vendor-notices-validation.json`。

公开再次分发仍需补证的五组项目：

| 项目 | 已观察到的证据/缺口 | 可执行下一步 |
| --- | --- | --- |
| bundle-crypto | 已补已识别MIT/BSD；3个static framework无法用go version -m读出完整模块闭包 | 提供各slice构建工程、go.mod/go.sum、准确版本与SBOM，逐依赖保留许可/NOTICE；必要时按已确认来源重建，而非删加密校验 |
| lite-card | GPChannelSDKCore.h声明2020 sherlockirene All rights reserved，包只有OneKey MIT；OKNFCBridge还有Apple归属头待追溯 | 内部权利人/供应商确认源码、模板出处及覆盖公开npm二次分发的授权范围，不擅自删除钱包硬件功能 |
| native-list图标 | Apple/Google品牌图标缺独立来源与权利范围记录 | 设计负责人提供资产出处、适用许可和品牌使用条件；不要将OneKey MIT自动套到第三方品牌 |
| ping | 已附GBPing Apache-2.0；RoJoHub主体MIT只找到元数据 | 取得准确主体授权原文/版权署名并进入产物 |
| cloud-fs | 上游MIT原文含year/fullname占位符 | 向上游取得准确署名/许可来源，不伪填作者 |

每包实际产物integrity、notice SHA256、条件性状态、限制与下一步见基线JSON中的 `redistributionAudit`；全部43包源记录位于 `.tmp/socket-execution/redistribution-audit.json`。MIT允许再分发以保留版权/许可为条件，BSD要求保留声明并满足其条件，Apache-2.0也有notice等义务；有这些根LICENSE不代表作者权属、商标和完整native闭包均已核清。

## Roobert其他库与App调用

SDK中已无字体二进制。字体家族名称、接收调用方字体名的API与实际再分发字体文件分开审计。以下六个其他库已有宿主字体通路，本轮未删API或改其实现：

| 库 | 调用字段/宿主字体通路 |
| --- | --- |
| auto-size-input | `fontFamily`；Android ReactFontManager，iOS UIFont(name:) |
| pager-view | `nativeTabBarStyle.fontFamily`；Android ReactFontManager，iOS fontWithName |
| image-crop-picker | `titleFontFamily` / `buttonFontFamily`；Android ReactFontManager，iOS UIFont(name:)；README保留Roobert调用示例并注明字体由宿主提供/授权 |
| tab-view | `fontFamily`；Android ReactFontManager，iOS RCTFont |
| text | RN TextProps字体字段，经React Native字体通路 |
| text-input | 保留RN TextInputProps字体字段，经React Native字体通路 |

NativeList最终仍优先使用宿主的Roobert：iOS查已注册的weight-specific PostScript名，不自行注册；Android逐次调用ReactFontManager，覆盖宿主注册（含Expo setTypeface）与assets，移除本地fallback缓存；Web使用宿主定义的Roobert font face，缺失再回退系统字体。没有增加public `fontFamily`配置。Android缺宿主字体时采用RN标准system fallback，原medium/semibold显式fallback会变化，已同步SPEC/STYLE_SPEC；现有行不会仅因动态注册自动重绘，宿主应在绘制前提供字体。

已只读核对App head `5b2d600922d1c114006fad7b6904c4dd077a797b`，并通过最终head比较确认下列字体文件、调用与依赖配置未变：

- Android `apps/mobile/android/app/src/main/assets/fonts/`有4个Roobert文件，Provider还有同样4个；8份文件的Git blob逐一匹配删除前SDK的4个原字体。App的说明记录它们用于同步首帧字体测量。[App资产说明](https://github.com/OneKeyHQ/app-monorepo/blob/5b2d600922d1c114006fad7b6904c4dd077a797b/apps/mobile/android/app/src/main/assets/fonts/README.md)
- Provider通过expo-font 57.0.1的useFonts注册4个Roobert-Weight别名；iOS UIAppFonts列4字体，Xcode Resources已有引用。[Provider](https://github.com/OneKeyHQ/app-monorepo/blob/5b2d600922d1c114006fad7b6904c4dd077a797b/packages/components/src/hocs/Provider/hooks/useLoadCustomFonts.ts)、[iOS配置](https://github.com/OneKeyHQ/app-monorepo/blob/5b2d600922d1c114006fad7b6904c4dd077a797b/apps/mobile/ios/OneKeyWallet/Info.plist)
- Web/desktop/ext各自App入口import `web-fonts.css`，其中4个Roobert weight定义已存在。[Web字体定义](https://github.com/OneKeyHQ/app-monorepo/blob/5b2d600922d1c114006fad7b6904c4dd077a797b/packages/components/src/hocs/Provider/web-fonts.css)
- App ImageCrop仍传Roobert-SemiBold/Medium，AutoSizeInput继续透传fontFamily，Market NativeList调用无需增加参数。[ImageCrop调用](https://github.com/OneKeyHQ/app-monorepo/blob/5b2d600922d1c114006fad7b6904c4dd077a797b/packages/components/src/composite/ImageCrop/index.native.tsx)

App文件未修改。上述源码/配置支持升级后继续使用App提供的Roobert，实际渲染待验证；不等于已完成新App原生构建/设备视觉验收，也不证明公司已经取得App字体使用或源文件公开分发授权。SDK不附TTF不代替App自身的授权核对。原始调用/配置证据见 `.tmp/socket-execution/app-host-font-audit.json`、`app-host-font-usage.json`与 `other-library-host-font-audit.json`。

## 告警与原生能力

最新 App 扫描于 `2026-10-10T15:46:29.873Z` 全部加载得到 397 个 active alert groups；UI优先级计数为9 Critical、134 High、231 Medium、23 Low。优先级与告警自身severity不是同一字段。DOM 中的重复嵌套行不另计。于 `2026-10-10T15:53:22.932Z` 按 `@onekeyfe/react-native-` 名称过滤观察 active/resolved 均为 0；这不是 43 包自身告警的完整 API 枚举，也不是每包传递依赖闭包。

仅 logger、photo-library、capture-protection 的公开包页已观察到包自身无告警，其余包自身告警与所有完整 Deep 告警均仍未知。没有把未知写成“无告警”，也没有压制、忽略或 resolve 告警。

正常能力的源码证据与风险判断：photo-library 仅接收绝对本地路径/file URI，64MiB 输入限制，iOS addOnly 与 Android MediaStore/旧系统权限路径分离，源文件仍由调用者持有；这些写文件和权限能力服务于用户保存动作。capture-protection 的 Android FLAG_SECURE 按 owner 管理；iOS 使用 secure text field 技巧仍需真机验收。sni-connect 保留 hostname verification、IP/path/header/body 大小校验；网络能力本身不是漏洞结论。这些说明解释能力用途，不表示 Socket 实际报告了对应能力告警，更不代替设备或完整安全验收。源码和 contract 可从各包 README/SPEC 链接核对。

最新 App 扫描也显示范围之外的 `@onekeyfe/inpage-providers-hub@2.2.76` Critical malware/Block 告警，路径为 root→cross-inpage-provider-injected→inpage-providers-hub。已见 useContext/Object.keys interception 源码；该告警未处理或认定误报。本轮 app-modules 分数与平均值不能掩盖该风险；需在对应仓库另行审计 builder 来源、费用同意和签名链。

## 依赖与发布产物

仅三个 SDK 包声明 regular runtime dependencies：async-storage→merge-options，get-random-values→expo-crypto/fast-base64-decode，tab-view→react-freeze/sf-symbols-typescript/use-latest-callback。实际 App lock 解析包括 merge-options3.0.4→is-plain-obj2.1.0、expo-crypto57.0.1、fast-base64-decode2.0.0、react-freeze1.0.3、sf-symbols-typescript2.2.0、use-latest-callback0.2.6。

上述七个节点的 registry metadata 确认 regular dependency 闭包；2026-10-10T14:56:40.055980Z npm 官方 bulk advisory 查询返回 `{}`。范围不含 React/RN/Expo 等 peer、dev 或 bundled native 依赖，不是 Socket Deep，也不是整个 App 无漏洞的证明。[npm audit endpoint](https://docs.npmjs.com/cli/v11/commands/npm-audit/)

公开 Socket peer 解析树中 RN1000.0.0/React19.3 与实际 App RN0.86.2/React19.2.3 分开记录。此前 peer 树中观察到 image-size、ip、braces、node-forge 告警，不能未经闭包映射把全部归到每个 SDK；App lock 的对应问题也不能通过删除必要 peer 或在 SDK README 中改变分数解决。image-size 的 major 升级需要 Metro 调用兼容测试；其余缺少已确认修复版本的问题需要上游修复或经过审核的替代方案。

包体积检查保留 bundle-crypto 的平台 xcframework，以及 9 包 podspec test_spec 用到的 native tests。按用户明确授权，移除 native-list 携带的付费 Roobert 字体及关联加载配置，保留宿主Roobert查找和缺字体回退；未为分数删 source maps、安全校验或必要原生功能。系统字体的字宽和截断效果需要设备视觉验收。

## 本地验证与合约审计

- Immutable Yarn安装完成，依赖及yarn.lock未修改。已有peer提示（eslint/prettier与example缺Expo）记录在安装日志，未通过删peer或改锁消除。
- 全43包运行各自release脚本中的Nitrogen（需要时）与prepare：43/43成功。随后逐包实际npm pack（pack阶段ignore-scripts，因为release-build前缀已运行）检查导出/main/types入口、README逐字一致、LICENSE/原生源码/生成文件、SHA512及安装生命周期。
- photo-library 3项、capture-protection 4项JS测试通过；release scripts 33项通过；lint:test-integrity核对43个测试文件通过（1条原有提示）。README里的28个TS/TSX示例只做语法诊断，不冒充完整类型、运行或设备验证。
- NPM_CONFIG_PROVENANCE=true 经Yarn workspace执行npm config后返回true，使用--workspaces=false避免ENOWORKSPACES；workflow的三个发布入口及job-local OIDC权限独立复核。没有实际发布或取得新attestation。
- 第一阶段只改说明/版权/发布设置，SPEC无需行为变更；字体阶段按新要求更新NativeList SPEC/STYLE_SPEC/README再实现宿主字体与fallback契约。biometric README为“attempts to refresh”，匹配未检查Keychain写结果的实现，没有改安全校验。
- CocoaPods使用标准example/react-native/ios工作目录及bundledGemfile；许可元数据的Podspec checksum必须精确更新，其他主机Hermes checksum按CI现有规则归一化，不弱化检查。最终Podfile.lock仅更新Ping、NativeList、BundleCrypto三个checksum：许可元数据与NativeList字体resource glob改变均用实际序列化spec重现旧值，其余依赖与lock字段保持原基线；分阶段证据记录在validation-summary、font-removal-validation与vendor-notices-validation。
- 字体相关最终检查：NativeList 236项JS测试、Android 6 suites/19项JVM测试及Kotlin编译、iOS字体/图标单文件UIKit typecheck、release-build与example Web build通过；宿主兼容后重新检查并打包。未执行完整C++/全App原生构建或模拟器/真机视觉验收。字体fallback、固定行高、截断和数字对齐需要对应平台验收。

本地证据位于 `.tmp/socket-execution/`，包括逐包build/pack JSON、真实tarball、许可证来源和validation-summary；原始失败及后续修正均保留在验证摘要中，未将失败命令记为通过。

## 发布与官方复测方案

本地分支完成 review 后，发布前先获用户授权；本轮未 push、合并、运行发布 workflow、发布 npm 或改组织策略。版本暂保留 3.0.165，不能覆盖已发布内容。

1. 审阅本地提交和逐包构建/pack 证据，解决未确认许可证来源，尤其 native 二进制闭包、GPChannelSDKCore、品牌图标和缺失上游声明；确认 repo URL 的大小写与 GitHub 仓库一致。
2. 在 npm 查询全部 43 包，选择尚未占用的新版本。通过现有版本工具同步 workspace versions 和 lock，不手工冒充已发布版本；候选如 3.0.166 仅作方案示例，未核验可用性。
3. 用户确认候选版本、渠道（先 next）、完整包清单、提交 SHA 与发布 workflow 后，在 GitHub-hosted runner 发布。job-local id-token:write 与三个发布入口的 NPM_CONFIG_PROVENANCE=true 配合现有 NPM_TOKEN。Yarn4.1 只运行脚本，实际发布命令是 npm publish。[npm provenance 要求](https://docs.npmjs.com/generating-provenance-statements/)
4. Registry 检查全部精确版本、tarball integrity、README、许可证、导出/native 文件、dist.attestations 与 Sigstore source/workflow/commit；原历史版本的恢复/retag 不强制要求 attestations。确认来源证明实际生成，不能将本地 YAML 检查当作已发布 provenance。
5. 使用 packages:list token 对每包 exact version 取得官方浅层五项、Overall、Deep 与所有告警；同时在 App 对新依赖锁/PR head 创建新 Socket scan。两个 dependency graphs 分开记录。无 token 时继续使用已授权网页的实际字段，其余保持未知。
6. 记录新扫描 source/time、完整依赖闭包与告警，对照本表。仅五项均 >90 且没有未处置高危风险才满足完整验收；任何未知字段或本地预测不能算达标。旧3.0.165分数不会被源码修改更新。

外部条件包括新版本发布授权、可用的 npm 发布认证与 OIDC 环境、Socket API packages:list 权限或能提供完整结果的官方界面，以及真实使用/维护历史和上游修复。若复扫仍低于阈值，应向 Socket 请求具体评分因素与误报证据复核，逐项提出有依据的改动；不能伪造下载/发布历史或改评分展示。
