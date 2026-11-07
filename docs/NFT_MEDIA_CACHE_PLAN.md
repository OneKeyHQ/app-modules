# NFT 原生媒体缓存与流畅度方案

状态：方案与源码已实现，已有分层编译、iOS 原生运行及 Android SVG 缓存运行证据；
完整跨平台/真机验收未完成。
基线：`origin/main@dec7687e0`。
本方案仅修改 `react-native-image` 和 `react-native-native-list` 的原生实现、
对应规范与聚焦测试；不升级依赖，不改变 JS API，不发布 SDK，不修改 App。
后续交付范围已获授权：Android 验证后提交并创建指向 `main` 的 PR；
新增的两端真实 App 滚动、帧率及内存比较已按用户最新指示取消，不属于 PR 通过声明。

## 现状与问题

- iOS、Android 的 NFT 列表由 NativeList 的 `mediaTile` 承载。上层传入 URI
  和 image/video 探测顺序；原生不得按 URI 或缓存记录擅自跳过 image 探测。
- 两端目前在可见时创建静音暂停的 AVPlayer/ExoPlayer，从已渲染画面复制首帧，
  成功后释放播放器。UIImage/Bitmap 只保留在 cell；recycle 清空，因此滚回和
  跨 cell 绑定同一视频仍会创建播放器。当前没有跨 cell 首帧缓存或同资源去重。
- iOS 当前在主线程把原视频像素缓冲转换成完整 CGImage，可能远大于 tile。
  Android 捕获 TextureView 尺寸，仍需要限制输出和编码，并保留显示比例。
- 普通图片和 SVG 已归 Image 所有。iOS 的 data URI 经 SDWebImage、OneKeySVGCoder
  和 SDImageCache；SVG/foreignObject 的安全限制与缓存版本已存在，不需要新建
  NFT SVG 缓存。Android 的 data URI 模型返回 ByteBuffer，但当前 SVG decoder
  只有 InputStream 路径；默认 AUTOMATIC + LOCAL + PictureDrawable 也不能作为
  持久化命中的保证。须补齐实际解码路径并指定 data URI 的 source DATA 缓存。
  Source DATA 命中仍需 SVG 解析/绘制，不等于 decoded-memory 命中。
- App 只读基线 `/Volumes/T7Shield/Project/app-monorepo-ok410093-nft-verify`
  的普通 NFT 详情使用 `CommonAssetImage`，通过 `@onekeyhq/components` 的 Image
  或带 controls 的 Video 展示；不是 NativeList 预览槽，也不是 WebView。
  Android 在这里将内嵌 SVG 规范化为有界 base64 URI，iOS 直接传给 Image。
  其 native Video 包装 `react-native-video` 的 VideoView/useVideoPlayer，默认
  可播放。本次不改变详情视频交互；Image 层的 SVG 修复可由正常 SDK 集成惠及详情。
  NativeNFTList 当前直接传原 URI，未运行详情的 Android base64 规范化；本次
  base64 路由修复不能据此声称 Android 非 base64 dataSVG 输入已支持。

## 平台、运行时与所有权

目标是 iOS/Android。App 的 main、bg 为独立 JS 堆；此缓存与调度位于进程共享
原生 Image 服务，既不在 JS 保留首帧，也不依赖 bg/main 初始化顺序。媒体描述
仍由 main 传入；native-only API 不新增 JS 桥和大型像素序列化。Web/Desktop
本次没有行为改动。缓存资源由 Image 包拥有，播放器和视图始终由 NativeList
slot 拥有；Image 不引入 AVPlayer/Media3 依赖。

## 实施合同

### 缓存 identity 与数据

1. Image 新增 `OneKeyMediaPreviewCache` native-only 服务，供 NativeList 使用。
   key 为版本前缀、原始 URI、规范化 headers、实际目标尺寸 tier 的 SHA-256。
   headers 名称大小写归一、排序并采用无歧义编码；值参与摘要，不明文写盘或日志。
   row key、主题、文字不参与资源 key。缓存 namespace 与正常图片严格分开。
2. tier 按实际 view 像素长边选择 128/256/512/1024；最大长边 1024、最大
   1,048,576 像素，保留源宽高比。零尺寸等待布局，不默认解码原始大图。
   缓存是方向已归一的静态图；contain/cover 继续由显示层实现。若 Android
   TextureView 捕获结果已受 fit 裁剪或变换影响，fit 必须进入 key，避免互换。
3. 默认 `memory-disk`；`memory`、`disk`、`none` 分别限制可读写域，不能通过
   跨 policy flight 绕过。policy 不必进入存储 key，但应区分进行中请求。
   解码内存预算 16 MiB、64 条；持久化预算 64 MiB，单项编码不超过
   8 MiB。iOS NSCache/SDImageCache 配置是库淘汰目标，非瞬时硬上限；Android LruCache
   按实际 bitmap cost 淘汰。不是总进程内存上限，显示帧、原生解码器和临时源缓冲另计。
4. iOS 使用独立 namespace 的 SDImageCache（复用现有图片缓存技术）；Android
   使用当前 Glide 依赖提供的 DiskLruCacheWrapper 独立子目录与 LruCache。
   不访问 Glide 私有 Engine，不与 Glide 默认目录共享 journal。Android wrapper
   非稳定公开扩展合约，固定当前依赖并以真实读写测试覆盖。两端以容量淘汰为保证；
   不承诺统一 TTL：iOS 配置 30 天磁盘年龄清理，Android 使用容量 LRU 而不设 TTL；
   该平台差异已在 Image SPEC 中明确。

### 请求、提取与生命周期

1. 只有到达 video 候选时，才先查允许的内存缓存，再异步查磁盘；命中直接绑定
   frame，不创建播放器。磁盘读取、图像解码、编码和写入不得在 UI 线程执行。
2. 同 key/policy 的可见请求共享一个 flight。Image 服务只授予 `onExtract`
   许可；获许可的 slot 创建原有静音暂停播放器，提交首帧或失败结果。其它 slot
   等待同一结果。全进程最多两个正在提取的 flight，最多 130 个总 flight
   （包含缓存查询、FIFO 等待和提取）；超限返回普通候选失败，不静默丢失终态。
   等待中的重复 key 不重复排队。
3. 离屏、detach、后台、recycle 立即取消订阅；无订阅则取消 flight。owner 离开
   时必须撤销其播放器和提交权；其它仍可见订阅者可接任，旧 owner 迟到结果无效。
   回到可见区域先重新查缓存。每个 slot 的 epoch/candidate/player 代际仍保留。
4. 每次许可从 player 创建起最多 10 秒；取帧的六次短重试仍可沿用。不能复制
   首帧时释放资源、返回候选失败，不能让永久暂停的 player 独占两个许可并阻塞
   后续列表。image/video 顺序、错误占位、tap 归 row、静音和无自动播放不变。
   缓存写入失败不否定已经成功提取/显示的首帧；后续允许重新提取。
5. iOS 在 UI 线程仅取得 CVPixelBuffer 引用和必要方向/尺寸信息，在有界 worker
   中进行 CI 缩放和转换，避免先生成整幅 UIImage 再缩小。Android TextureView
   复制是 UI API，直接请求 bounded bitmap；编码和磁盘处理移至 worker。
   按实际视频比例恢复 `AspectRatioFrameLayout`，缓存命中与新首帧视觉一致。
6. 有效首帧立即交付 UI/允许的内存缓存，后台异步编码和落盘，不以慢盘阻塞显示。
   待持久化帧的像素成本另设 16 MiB 上限；超出时跳过该项磁盘写入，保留显示和
   内存命中。编码缓冲另计，受单 worker 与单项 8 MiB 限额约束。取消的 iOS
   待转换任务立即释放尚未开始转换的 CVPixelBuffer 引用。

### 清理、失效与损坏

Image 现有 `clearMemory/clearDisk/clearAll` 包含该服务，对应域完成清理才 resolve。
每次清理建立代际屏障；之前开始的 lookup、提取或写入不得在清理完成后回填
已清理域。序列化检查与写入/clear，不能只在异步写入开始前检查 token。已经
显示的帧可继续显示，clear 不负责将当前 UI 清空。新请求可按新代际重新缓存。
损坏、超限、解码失败的缓存项视为 miss，并安全删除或替换，重新走原视频候选；
同 key 并发失败读取不得删除后来已修复的文件。不得把普通图片 URI 的缓存图像
当成视频首帧。带相同 URL 不同 headers 的内容必须完全隔离。
磁盘读取先限制编码字节不超过 8 MiB，并先读取 metadata 验证长边 <=1024、
总像素 <=1,048,576，再分配像素/解码。不能先调用通用 UIImage/Bitmap 全量
decode 再检查大小；即使缓存正常由自身写入，也必须覆盖损坏/超大维度文件。

### 内嵌 SVG

iOS 保持现有 loader/coder/安全检查及缓存所有权。现有 6.7.0-26 App 已实测唯一
nonce 的 data:image/svg+xml;utf8 首次成功加载，clearMemory 后 remount + disk
policy 返回 cacheType=disk/onDisplay；证据见外部验收目录
`/Volumes/T7Shield/Project/ok410093-nft-analysis-20261106/svg-cache-baseline.json`。
这证明已有路径落盘，不代表候选实现或 Android 验收。
Android 在现有 registry 中为 data-URI ByteBuffer 补齐 SVG 解码入口，继续使用
`OneKeySVG.parse` 及原 foreignObject、编码字节、像素预算；render 和 preload
统一 data URI `memory-disk` 下的 DATA 策略。磁盘中保存源数据，解码后对象仍由
Glide 管理。base64 数据内容不在日志中输出。不新增 URI 规范化/HTML 渲染规则，
不削弱 DTD/entity/external-resource 限制，不更改 animated image 时长等安全边界。

## 文件范围

- 新建两端 Image `OneKeyMediaPreviewCache.swift/.kt`：缓存、identity、clear
  屏障和不依赖播放器的 flight 协调。可在同文件放 native-only 辅助类型。
- 修改两端 `OneKeyImageCache.swift/.kt`：连接现有三个 clear 入口。
- 修改两端 `NativeListMediaPreviewSlot.swift/.kt`：缓存先查、可见订阅、许可、
  首帧缩放和代际提交；不改其它模板和 JS schema。
- 修改 Android `OneKeyImageGlideRegistry.kt` 与
  `OneKeyBlockieAvatarLoader.kt` 中现有共享策略：补齐 SVG/data URI 路径。
- 修改两包 `docs/SPEC.md`，并添加/扩展两包已有 iOS XCTest、Android 原生测试
  文件与必要的测试 runner 源文件清单。只允许为真实 native 测试调整 harness，
  不扩大到构建系统、依赖版本或业务功能。若须增加实现文件或改变以上合同，先说明。

## 聚焦验证与验收门槛

以下保留原设计的完整验证矩阵；本次交付按开头的最新范围执行，已取消的
两端 App 滚动和性能比较不会被追认为通过，也不要求在本次 PR 前补齐。

| 情况 | 必须观察的结果 |
| --- | --- |
| 冷加载一视频 | 一次 player 创建/一次成功提取；静音暂停，首帧后 player 数回零 |
| 同 cell 滚出滚回、跨 cell 同 key | 内存命中新增 player/提取均为 0 |
| 内存清除后、正常进程重启后 | 磁盘命中新增 player/提取均为 0；需等写入完成再重启 |
| 同 key 并发两 cell | 总计一次提取；取消 owner 后新 owner 可继续，无过时 UI 回写 |
| 三个以上独立视频 | 同时提取不超过 2，排队最终展示，不永久卡在首两项 |
| 同 URL 不同 headers/尺寸/必要 fit | 不串图；headers 顺序/名称大小写等价时可复用 |
| clear 与读/写/提取交错 | clear resolve 后旧 token 不回填；新 token 正常；已显示帧可保留 |
| 损坏缓存/无盘空间 | 不崩溃，可重建或展示成功提取帧，无无限重试 |
| A→B→A、布局变化、后台/离屏 | 无旧图闪现、无离屏 player、observer/任务能释放 |
| 图片先于视频、图片 URI | 原探测顺序、图片终态与占位保持，视频缓存不抢占 |
| SVG base64/data URI | 真正 Image/Glide 成功；重绑定命中memory；清内存后命中disk |
| SVG unsafe/overbudget、动画图 | 原安全回归保持；不得以新增路由绕过限制 |
| HTTP/file/HLS、横竖旋转及contain/cover | 首帧方向、比例、letterbox、tap 一致，无音频 |

性能验收采用同设备、同视频/SVG集合、同布局、同滑动脚本比较 baseline/candidate。
记录 cold/warm/disk 首帧延迟 p50/p95、player创建/提取/缓存命中计数、帧耗时p95、
掉帧比例、峰值/稳定内存以及完成后活跃 player。暖缓存关键硬指标是新增播放器
和提取次数为零；默认2并发和预算可由 native 测试直接断言。流畅度提升必须来自
真实 trace；没有 trace 时仅报告减少重复提取，不写百分比或宣称卡顿已解决。

验收分层：源码/SPEC一致性 → native单测与缓存真实I/O → simulator/emulator
真实播放器和SVG → App集成滑动 → 真机性能。缺任一层均明确记录，不以前一层
替代后一层。SDK 发布与 consumer 依赖升级未授权；提交与 PR 按本文开头的最新
交付范围执行，未运行的性能层保持明确缺口。


## 独立验收记录（2026-11-06）

独立审查按 Image/NativeList SPEC 逐项核对了资源 identity、clear 分域屏障、
owner 转移、失败/超时、尺寸变更、取消后的原始像素引用以及 UI/后台线程边界。
首轮发现的旧 owner 完成竞态、有效首帧等待落盘、磁盘先全量解码、尺寸变化无法
重启、取消队列保留原始 buffer 等问题已修复。直接文件读取引入的 SDWebImage
异步清理顺序，以及 pending persistence 编码后过早扣减预算，也已修复。
最终另修复了超限订阅的延迟 failure 取消，以及已有暖帧重挂时多余的 viewport
观察器。服务级 130-flight 饱和/取消已测，不等于真实 130 个 cell 的 UI 压力验收。
Android 最后恢复了既有 Media3 错误回调约束：`onPlayerError` 在下一 UI turn
复核 player generation 后才使缓存 flight 失败并释放播放器，避免在监听器
发射栈内重入 release；该增量已独立源码检查，并纳入最终 App 增量构建。
现有 iOS SDWebImage 的 `storeImageDataToDisk` 是内部 IO 队列同步操作，clear
则为异步；最终实现仅在自身后台 IO worker 等待 clear 完成，不阻塞 UI。

独立审查已读取实际日志并核对运行证据中的 Swift 源文件 SHA-256 与工作树一致。
测试目录统一为外接盘兄弟目录：
`/Volumes/T7Shield/Project/app-modules-nft-media-cache-performance-20261106-tests`。

| 层次 | 实际证据 | 可得结论 |
| --- | --- | --- |
| iOS 缓存服务 | `ios-cache/cache-final.log`：38 checks；真实生产 Swift + SDWebImage，非缓存 mock | 同 key 去重、memory/disk 命中、2 permits、owner 取消/转移、clear 旧任务不回填、损坏/超字节/超维度拒绝、headers identity、130 total flight 超限及取消后不投递失败通过 |
| iOS 跨进程 | `ios-cache/restart-final.log`：2 checks | 另一个进程可从同 namespace 读磁盘，无提取 |
| iOS video slot | `ios-slot/runtime-final.log`：14 checks；真实生产 slot + AVPlayer + H.264 MP4 | 冷 player=1；首帧后释放；跨 cell/recycle/clearMemory 后 disk 命中不新增 player；跨尺寸 tier 后仍有帧 |
| iOS 正式测试源码 | `ios-slot/xctest-typecheck.log`，真实 XCTest Swift overlay，退出码 0 | 新 XCTest 可编译；不是完整 Pod XCTest suite 已运行 |
| Android 原生源码 | `AndroidCompile/nft-kotlin-check` 的 core/bridge/slot 编译日志和命令清单 | 使用真实 Android/Glide/Media3/Nitro 依赖编译新服务、Image bridge 和两个生产 slot；不是完整 Gradle 构建 |
| Android 正式测试源码 | 同目录 `instrumentation-compile.log` | 仪器测试源码可编译；随后选择其中 SVG case 执行，未声称整个 instrumentation suite 全过 |
| Android 正常 SDK 构建 | `AndroidCompile/normal-image-apk-build.log`：BUILD SUCCESSFUL，122 tasks | 正常 Gradle assembleDebugAndroidTest，包含生产 Kotlin/JNI 和正式测试 APK，不再仅有隔离 kotlinc 证据 |
| Android SVG 缓存 runtime | `AndroidCompile/svg-instrumentation.log`：OK (1 test)，0.27 s，API 35 arm64 emulator-5586 | 生产 Glide 路径实际绘制 foreignObject 内嵌 PNG 并验证红像素；LOCAL→MEMORY_CACHE→清内存后 DATA_DISK_CACHE 通过 |
| Android 消费 App 正常构建 | `AndroidApp/dev-shell-local.log`：21m41s SUCCESS；`dev-shell-final.log`：最终增量 5m15s SUCCESS | 五项原生源码 overlay 与 SDK 最终源码一致，最终 APK 包含新的缓存类；包版本、锁文件、配置、JS 均不变 |
| Android App 启动 smoke | 最终 DevSession run-result 为 running，`AndroidApp/app-started.png` 已独立目视核对 | OneKey onboarding 及 Create new wallet 按钮正常可见；截图含开发警告/RevenueCat 提示，不宣称 clean console 或完整 Home NFT 路由通过 |
| 原 iOS SVG 基线 | `ok410093-nft-analysis-20261106/svg-cache-baseline.json` | 原 SDK 6.7.0-26 的内嵌 SVG 已真实落盘，本次保留其路径 |

提交准备检查已通过：`yarn lint:test-integrity` 覆盖 77 个文件；NativeList 的
nativeMediaGrid/validation 两个 Jest suites 共 235 tests，Image index 一个
suite 共 21 tests，总计 256 tests。Image 首次缺少生成 JSON 的检查失败已通过
执行正常 Nitrogen 生成纠正，随后复跑通过；未将首次失败写成通过。

首轮正常 Android Gradle 在 RN settings-plugin 的 Kotlin metadata 不兼容处
失败，尚未进入本改动模块，日志为 `AndroidCompile/nft-android-compile.log`。
后续查明当时复用的 node_modules 指向 RN 0.86.2，未匹配当前 SDK 锁文件的
RN 0.88；按当前锁文件执行 `yarn install --immutable` 后依赖已恢复，SDK 的
Nitrogen 生成也已完成。没有为绕过它升级依赖或修改生产构建配置。隔离编译
仍只是更窄的源码验证，不代替正常构建与运行。
正常 AndroidTest 打包所需的唯一新增测试配置调整是删除 Image 测试 manifest
重复的 `uses-sdk minSdkVersion=24`（AGP 9 拒绝该测试 manifest 声明）；生产
defaultConfig 和既有 minSdk 24 均保留。该改动已独立检查，属于测试入口适配。

首轮“无 Android system image”的判断亦已纠正：外置 SDK 符号链接内实际有
API 35 arm64 镜像，当时的目录查询未跟随链接。任务专属 emulator 5586 已启动；
正常 SDK 构建和所选 SVG runtime 已通过，不能继续以缺镜像作为当前阻塞。
独立审查已读取该运行日志和 `normal-apk-receipt.json`，并复算测试 APK SHA-256：
`ea06a87bbfc5720a5a343457f1c8b1c4f5a53d6a1a1fe5d6a6cdd2fdbab561ed`。
测试 manifest 生成物仍保留 minSdk 24。Android 消费 App 的本地完整构建和最后
错误回调修复后的增量构建均成功，最终 DevSession 报告 `status=running`。
独立复算 `AndroidApp/OneKeyWallet-candidate-final.apk` 的 SHA-256 为
`d36d779428b48f24d70f215cbefb8b9ce559648f10f2b1a2e3e45b0ffae8bbec`；
五项 SDK/consumer 源码逐一与 `candidate-final-receipt.json` 一致，最终 Android
slot SHA-256 为 `55d1b49e034f42283e492615cd13a97d0fd73e67c6f4c29e81498614a15fdd02`，
新缓存类实际位于 `classes2.dex`。此前隔离 slot 编译是最后错误回调修复前的
历史证据，最终 slot 以消费 App 的正常增量构建绑定，不更新旧产物摘要冒充。
最终 DevSession 记录的安装产物路径复算为相同 APK 摘要；启动截图已独立核对，
确认 App 实际呈现 onboarding。此为启动 smoke，不是完整钱包业务验收。

额外组件探测的限制也保留：第一次挂载 Home NFTList 在 fresh onboarding 中
缺账户 provider context，出现 JS RenderError；该失败探测不算业务路由通过。
重新加载后的裸 SDK NativeList/C.Image 临时探测收到原 QA URL 的
onLoadStart→onLoad→onLoadEnd→onDisplay，无 onError，但对应 Dialog 截图为黑屏，
当时的 window dump 也没有 FLAG_SECURE。因此不能把该图当媒体像素正确的证明，
也不将黑屏擅自归因于安全截图限制。这个额外 Dialog 探测未升级为视觉通过；
Android SVG 像素的有效证明来自前述正式 instrumentation case。

本次首次 vendor 远端 OCI 请求 HTTP 404，DevSession 发出 `ONEKEY_USER_NOTICE`
并自动本地生成；该通知已向用户报告。最终启动复用的是随后形成的本地 vendor
缓存，不能将整个运行描述为全程干净的远端缓存命中。

iOS slot harness 仅替代未使用的 image-candidate host，视频路径使用生产代码；
它不证明 image→video fallback、完整 NFT 页、HTTP/HLS、不同方向视频、全部
cachePolicy、真实 130 可见 cell 压力、10 秒超时、真实前后台交互或设备 FPS。缓存层的
合法帧不会因 clear 被禁止显示，但旧代际不得回填被清理域，这是特意保留的 UI
语义。流畅度没有测量百分比，当前只可主张已实证消除相同视频暖缓存场景的重复
player 创建/提取。完整 App 集成、Android 视频预览 runtime、真机性能、SDK 发布和消费端
升级均未完成，也未将这些状态写成通过。


## PR #228 独立审计与最终范围（2026-11-07）

用户明确本功能当前没有 Cookie 会话管理需求，不提供通过 Cookie 管理媒体
缓存的能力。已复核 Image Nitro props/preload/cache API、List ImageSource 与
App NativeNFTList 调用：接口没有 Cookie/session 管理字段；现有 NFT 调用只传
metadata.image URI、160×160 尺寸提示和 probeOrder。此结论仅针对本功能调用
边界，不是对整个 App 所有 Cookie 用途的调查。

最终身份合同为 schema + 原始 URI + 调用方显式 headers + 实际尺寸 tier + fit；
policy 控制 memory/disk 读写域并区分进行中 flight。环境 Cookie 不进入 key，
不提供 Cookie 会话分区或 Cookie 变化自动失效。依赖环境 Cookie 改变身份或
授权的媒体不在首帧缓存支持范围内。显式 headers 仍作为不透明值参与摘要。
Android 原 RN CookieJar 与 iOS AVFoundation 默认联网行为保持，不据此宣称
底层请求不携带 Cookie，也不宣称完成了跨 Cookie 会话安全隔离。

据此撤回本轮 Android Cookie tracker、全局资格检查、private-flight/auth
admission 与 key v2，恢复原服务合同和 key schema。Cookie 专属测试随功能撤回；
独立的 queued lookup 取消测试迁回已有 cache 测试文件。此前等待的 iOS HTTP
降级/transport 选择已取消，Cookie 范围不再作为本 PR 阻塞。完整审计历史保留
在外部 tests/PrAudit20261107/audit-report.md，不能用历史 Cookie 测试成绩
代表最终撤回后的实现通过。

继续保留三项独立修复：

- iOS detach 只有真实 window 存在才比较 pixel tier，保留同一 still，避免以
  scale1 误清帧；仍允许真实尺寸变化重新请求。
- Android data URI 策略对应单测改为 DATA，并覆盖 PNG/SVG；HTTP 与 avatar
  原断言保留。
- 两端排队 disk lookup 持有取消句柄；最后订阅取消后未开始的读取/解码不再
  执行，已有 peer 则保留；运行中的单个 I/O 可结束但旧结果不得投递。

这三项已有修复前后/聚焦证据：Android 原第49行失败后 JUnit15通过；iOS 原
f881 slot 真实 MP4 detach 失败后13检查通过；iOS cache38与queued取消4检查
分别通过。Android 最终回退后正常 Gradle SDK APK 与 List Kotlin 构建52秒
通过，专属 emulator-5586 上既有cache/SVG/迁移取消三项 instrumentation 全过
（0.207秒）。首次增量构建出现 NativeList 内部符号解析失败，保留原失败日志；
源码不变，仅本次命令加 -Pkotlin.incremental=false 后通过，没有修改构建配置
或依赖，也不将过程描述为干净缓存命中。独立复核最终 source receipt 全部匹配，
Cookie 专属测试文件确已删除，保留的取消测试实测通过。最终提交的托管 CI
仍需绑定新 HEAD，不能复用已撤回 Cookie 版本的11项成绩或较早 App APK作为
新版本完整验收。

运行时范围：slot 属 main UI，Image 服务为进程共享 native 单例，清理协调状态
回主线程。没有 JS 帧对象或新增逐 runtime 反序列化副本，main/bg 双堆不等于
Bitmap 各复制一份；未验证 bg 调用 clear 的完整跨运行时路径。App 图片像素、
完整 Home NFT 路由以及已取消的滚动/帧率/内存测量边界维持，不扩大验证范围。
