# Original remote card and sheet source paths

Status: full SDK not restored. These are observed source-body dependencies, not a working App or WebKit/backend acceptance. Original remote Web content and JavaScript transport remain SDK behavior; native Messenger UI is not replaced with a Web client.

## Mandatory startup

Original RN/public facade → ICMBridge → ICMPresentationManager → ICMWindow/ICMRootViewController. Root viewDidLoad unconditionally allocates ICMInAppsViewController, attaches its view and later invokes launcher/state operations. InApps and its preloader now have real bodies; Launcher and both single/stacked notification families now have their true source bodies and bounded three-target objects; full-screen Note/Post and their real FullScreen/Header/FakeComposer/Separator/Reactions bodies now have true bodies and bounded three-target source objects. Home, Help Center, Carousel and the complete default chat graph now have genuine source; the next whole-source/link gate must include these new bodies; a native source body does not prove ordinary consumer linkage. Actual native chat also requires the real ConversationSpaceViewController → ConversationViewController/ViewModel/Interactor. Real chat Controller/VM/Interactor and delegate bodies compile in the frozen whole snapshot; full destinations, strict linkage and runtime remain incomplete.

## Native chat to remote card

ConversationCollectionViewCell/PartViewFactory → BlockPartView → BlockViewFactory type18 → MessengerAppBlockView → MessengerAppConversationView. These factories/renderers have real source. The card loads its original fallback URL with appended theme into a WKWebView with a nonpersistent data store. Four original WKUserScripts and handleAction are present; parsing payload precedes name/type dispatch. HEIGHT, OPEN_URL, OPEN_SHEET, TEXT_INPUT and METRIC retain original routing, including the original HTTPS/nonnil-host OPEN_SHEET predicate.

Callbacks need the genuine ConversationViewModel implementation of ConversationCellBlockDelegate, including openAppSheet(with:params:delegate:), presentMessengerAppInputAlert(with:) and metrics. Existing Outbound behavior cannot substitute for these chat callbacks. Those true chat VM callbacks have source and whole-snapshot compilation; live remote content has not been exercised.

## Sheet presentation and remote execution

Outbound's real openAppSheet extracts article-link params or creates ICMMessengerSheetViewController → ICMPresentationManager.presentSheet → ICMRootViewController.presentSheet → real ICMNavigationController → actual sheet. This direct path bypasses ContentModal but still requires normal startup above. Chat VM must retain its own original delegate behavior.

ICMMessengerSheetViewController has true source for its own method inventory: loadView fetches before creating its default-store WKWebView; ICMHTTPBridge.fetchMessengerSheetData → real HTTPClient request → actual ICMSheet response; POST JSON keeps intercom_data, Content-Type and MobileClient. Original scripts/parser/actions, submit/timer, progress/error UI, navigation policy and close/reload callbacks are retained. Card nonpersistent storage and sheet default storage are intentionally distinct. Remote execution/backend delivery have not been exercised in this phase.

## Other native content paths

ICMRootViewController+Content allocates ICMContentModalViewController for Survey, Carousel and other content. ContentModal/ContentContainer and their observed native child graphs now have real source; full runtime and private-ABI acceptance remain separate. Home/Help Center/Carousel now have genuine controllers/services and their observed default children. Remaining method, private-protocol and consumer gaps stay listed separately; class presence does not establish all-method closure.

## Evidence boundary

250-Swift/17-ObjC rendering,255-Swift native-header,260-Swift Launcher,263-Swift notification and264-Swift/14-ObjC combined native full-screen snapshots are bounded with explicit unresolved exclusions. Selector/type matches and finite source reviews do not prove WMO-deleted private ABI, whole no-exclusion strict link, normal static/dynamic Pod/RN consumption or actual WebKit/backend runtime. IOS_FUNCTIONAL_GAPS.json separately records class presence and actual missing HTTP bodies; a header/class match is not method closure.

## Home remote app path

All eight genuine Home renderers and their real rows/tables/Help Center children now have source. MessengerAppHomescreenCardViewItem builds its distinct MessengerAppHomescreenView with original scripts/nonpersistent WK store/input alert/metrics and actual sheet delegates. Main Home VM/Controller and the full default Help Center graph now have genuine source; no generic controller or conversation/ICP substitute was used. The earlier bounded333Swift+oneObjC child snapshot matches18 classes90 own entries; the later frozen whole259ObjC/377Swift snapshot compiles these true graphs on three targets but fails strict linking at four then-missing classes. New Carousel source is separately compiled; no normal consumer or WebJS runtime acceptance is claimed.


## Normal consumer update

Actual normal local static and dynamic CocoaPods/RN Hosts now build and link with real Codegen. `.build/ios-normal-consumer/receipt.json` records selected public API compilation/private rejection and real Host launches checking all63 original resource hashes, two Host namesake image collisions, default AssetManager getter/nil behavior and native RN class loading. An SDK-owned bundle Info.plist makes the relocated catalog available without changing original assets/getters. These checks do not execute account/session/native chat/cards/sheets/remote WebJS/backend flows or the five complete UI icon callsites.
