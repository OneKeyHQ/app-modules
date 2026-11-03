#pragma once
#import <Foundation/Foundation.h>
#import <WebKit/WebKit.h>
@interface JPushNotiInAppWeakScriptMessageDelegate : NSObject <WKScriptMessageHandler> {
  __weak id<WKScriptMessageHandler> _scriptDelegate;
}
@property(nonatomic, weak) id<WKScriptMessageHandler> scriptDelegate;
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly, copy) NSString *description;
@property(readonly, copy) NSString *debugDescription;
- (instancetype)initWithDelegate:(id<WKScriptMessageHandler>)delegate;
@end
