// Source-only Foundation behavior tests; no original SDK is linked.
#import <Foundation/Foundation.h>
#import <objc/runtime.h>
#import "IntercomContent.h"
#import "ICMCompany.h"
#import "ICMUserAttributes.h"
#import "ICMCustomDataValidation.h"
#import "ICMHelpCenterArticle.h"
#import "ICMHelpCenterArticleAuthor.h"
#import "ICMHelpCenterArticleSearchResult.h"
#import "ICMHelpCenterCollection.h"
#import "ICMHelpCenterCollectionContent.h"
#import "ICMHelpCenterDataError.h"

static NSMutableArray<NSString *> *passed;
static void check(BOOL condition, NSString *name) {
    if (!condition) {
        fprintf(stderr, "FAIL: %s\n", name.UTF8String);
        exit(1);
    }
    [passed addObject:name];
}

int main(void) {
    @autoreleasepool {
        passed = [NSMutableArray new];
        NSArray<IntercomContent *> *contents = @[
            [IntercomContent articleWithId:@"a"], [IntercomContent surveyWithId:@"s"],
            [IntercomContent carouselWithId:@"c"], [IntercomContent helpCenterCollectionsWithIds:@[@"h"]],
            [IntercomContent conversationWithId:@"v"], [IntercomContent ticketWithId:@"t"]
        ];
        NSArray *names = @[@"Article", @"Survey", @"Carousel", @"HelpCenterCollections", @"Conversation", @"TicketContent"];
        for (NSUInteger i = 0; i < contents.count; i++) {
            IntercomContent *content = contents[i];
            check(content.type == (ContentType)i && [NSStringFromClass(content.class) isEqual:names[i]], [NSString stringWithFormat:@"content factory %lu", (unsigned long)i]);
            id identifier = content.contentId;
            content.type = (ContentType)123;
            content.contentId = @"ignored base ivar";
            check(content.type == (ContentType)i && content.contentId == identifier, [NSString stringWithFormat:@"content overridden getters %lu", (unsigned long)i]);
        }
        NSMutableString *mutableId = [NSMutableString stringWithString:@"source"];
        IntercomContent *copiedContent = [IntercomContent articleWithId:mutableId];
        [mutableId appendString:@" changed"];
        check([copiedContent.contentId isEqual:@"source"], @"content factory copies identifier");

        ICMCompany *company = [ICMCompany new];
        check(company.attributes.count == 0, @"company defaults omit attributes");
        company.companyId = mutableId;
        [mutableId appendString:@" again"];
        check(company.companyId == mutableId, @"company setter retains mutable string");
        company.companyId = @"";
        check(company.companyId == mutableId, @"empty setter preserves prior companyId");
        company.companyId = nil;
        check(company.companyId == mutableId, @"nil setter preserves prior companyId");
        company.name = @" ";
        check([company.name isEqual:@" "], @"whitespace is accepted without trimming");
        company.createdAt = [NSDate dateWithTimeIntervalSince1970:1.25];
        company.monthlySpend = @(-3.5);
        company.plan = @"plan";
        check([company.attributes[@"created_at"] isEqual:@1.25] && [company.attributes[@"monthly_spend"] isEqual:@(-3.5)], @"company timestamp preserves double and negative spend");
        company.name = [ICMCompany nullStringAttribute];
        company.createdAt = [NSDate dateWithTimeIntervalSinceReferenceDate:[NSDate distantFuture].timeIntervalSinceReferenceDate];
        company.monthlySpend = [ICMCompany nullNumberAttribute];
        check(company.attributes[@"name"] == [NSNull null] && company.attributes[@"created_at"] == [NSNull null] && company.attributes[@"monthly_spend"] == [NSNull null], @"company null sentinels serialize NSNull");
        NSMutableString *equalSentinel = [NSMutableString stringWithString:[ICMCompany nullStringAttribute]];
        company.name = equalSentinel;
        check(company.attributes[@"name"] == equalSentinel, @"equal nonidentical string sentinel stays string");
        company.customAttributes = @{@"empty": @"", @"number": @2, @"null": [NSNull null], @"sentinel": [ICMCompany nullStringAttribute]};
        check([company.customAttributes[@"empty"] isEqual:@""] && company.customAttributes[@"sentinel"] == [NSNull null], @"custom attributes empty value and sentinel");
        NSDictionary *priorCustom = company.customAttributes;
        company.customAttributes = @{};
        check(company.customAttributes == priorCustom, @"empty custom attributes rejected atomically");
        company.customAttributes = @{@"date": [NSDate dateWithTimeIntervalSince1970:0]};
        check(company.customAttributes == priorCustom, @"date custom value rejected");
        company.customAttributes = @{@"nested": @{@"inner": @1}};
        check(company.customAttributes == priorCustom, @"nested custom value rejected");
        check([[ICMCompany new] isEqual:[ICMCompany new]], @"company equality accepts nil fields");
        check([ICMCompany new].hash == 0, @"company empty hash xor");

        ICMUserAttributes *user = [ICMUserAttributes new];
        check(user.attributes.count == 0 && !user.hasNonIdentifyingAttributes && !user.unsubscribedFromEmails, @"user empty defaults");
        user.email = @"synthetic@example.invalid";
        user.userId = @"synthetic-id";
        check([user.registrationAttributes isEqual:@{@0: @"synthetic@example.invalid", @1: @"synthetic-id"}], @"registration uses numeric keys");
        check(!user.hasNonIdentifyingAttributes, @"identifiers excluded from nonidentifying attributes");
        user.unsubscribedFromEmails = YES;
        check([user.attributes[@"unsubscribed_from_emails"] isEqual:@YES] && !user.unsubscribedFromEmails && !user.hasNonIdentifyingAttributes, @"unsubscribe original split ivar behavior");
        user.unsubscribedFromEmails = NO;
        check([user.attributes[@"unsubscribed_from_emails"] isEqual:@NO], @"explicit false is serialized");
        user.companies = @[[ICMCompany new]];
        check(user.companies == nil, @"company without id rejected");
        NSMutableArray *companies = [NSMutableArray arrayWithObject:company];
        user.companies = companies;
        check(user.companies == companies && user.hasNonIdentifyingAttributes, @"valid companies retain input array");
        check([user.attributes[@"companies"] isEqual:@[company.attributes]], @"companies serialize nested attributes");
        user.companies = @[];
        check(user.companies == companies, @"empty companies preserve prior value");
        user.signedUpAt = [NSDate dateWithTimeIntervalSince1970:-0.25];
        check([user.attributes[@"signed_up_at"] isEqual:@(-0.25)], @"user signed up timestamp double");
        user.signedUpAt = [ICMUserAttributes nullDateAttribute];
        user.phone = [ICMUserAttributes nullStringAttribute];
        check(user.attributes[@"signed_up_at"] == [NSNull null] && user.attributes[@"phone"] == [NSNull null], @"user date and string null serialization");
        ICMUserAttributes *left = [ICMUserAttributes new], *right = [ICMUserAttributes new];
        left.unsubscribedFromEmails = YES;
        check([left isEqual:right] && left.hash == right.hash, @"user equality and hash use original BOOL getter");

        NSMutableString *title = [NSMutableString stringWithString:@"Title"];
        ICMHelpCenterArticle *article = [[ICMHelpCenterArticle alloc] initWithArticleId:@"article" title:title];
        check(article.title == title, @"article constructor retains mutable title");
        article.title = title;
        check(article.title != title, @"article setter copies mutable title");
        check([article isEqual:[[ICMHelpCenterArticle alloc] initWithArticleId:@"article" title:@"Title"]] && article.hash == @"article".hash, @"article equality and id hash");
        ICMHelpCenterArticleAuthor *authorA = [[ICMHelpCenterArticleAuthor alloc] initWithAuthorId:@"author" displayName:@"A" avatarURL:nil];
        ICMHelpCenterArticleAuthor *authorB = [[ICMHelpCenterArticleAuthor alloc] initWithAuthorId:@"author" displayName:@"A" avatarURL:nil];
        check(![authorA isEqual:authorB] && [authorA isEqual:authorA], @"author nil URL unequal across instances");
        ICMHelpCenterArticleSearchResult *searchA = [[ICMHelpCenterArticleSearchResult alloc] initWithArticleId:@"article" title:@"T" summary:nil matchingSnippet:nil];
        ICMHelpCenterArticleSearchResult *searchB = [[ICMHelpCenterArticleSearchResult alloc] initWithArticleId:@"article" title:@"T" summary:nil matchingSnippet:nil];
        check([searchA isEqual:searchB], @"search optional nil fields compare equal");
        ICMHelpCenterCollection *collectionA = [[ICMHelpCenterCollection alloc] initWithCollectionId:@"collection" title:@"T" summary:nil articleCount:2 collectionCount:3];
        ICMHelpCenterCollection *collectionB = [[ICMHelpCenterCollection alloc] initWithCollectionId:@"collection" title:@"T" summary:nil articleCount:2 collectionCount:3];
        check([collectionA isEqual:collectionB], @"collection equality includes nil summary");
        collectionB.collectionCount = 4;
        check(![collectionA isEqual:collectionB], @"collection equality includes nested collection count");
        NSMutableArray *authors = [NSMutableArray arrayWithObject:authorA];
        ICMHelpCenterCollectionContent *collectionContent = [[ICMHelpCenterCollectionContent alloc] initWithCollectionId:@"collection" title:@"T" summary:nil articles:@[article] articleCount:1 collections:@[collectionA] authors:authors];
        check(collectionContent.authors == authors, @"collection content constructor retains authors");
        collectionContent.authors = authors;
        check(collectionContent.authors != authors, @"collection content authors setter copies");
        check([collectionContent.description containsString:@"articleCount: 1, authors:"] && [article.description containsString:@"articleId: article, title: Title>"], @"help center descriptions preserve fields");
        check([ICMHelpCenterDataErrorDomain isEqual:@"ICMHelpCenterDataErrorDomain"] && contentNotAvailable == 1 && noAppRegistered == 5, @"help center error enum and domain");
        NSDictionary *receipt = @{@"status": @"passed", @"testCount": @(passed.count), @"tests": passed, @"scope": @"Recovered Foundation models only; original runtime differential pending"};
        NSData *data = [NSJSONSerialization dataWithJSONObject:receipt options:NSJSONWritingPrettyPrinted error:nil];
        fwrite(data.bytes, 1, data.length, stdout);
        fputc('\n', stdout);
    }
    return 0;
}
