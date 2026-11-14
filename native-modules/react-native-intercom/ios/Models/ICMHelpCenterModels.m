#import "ICMHelpCenterArticle.h"
#import "ICMHelpCenterArticleAuthor.h"
#import "ICMHelpCenterArticleSearchResult.h"
#import "ICMHelpCenterCollection.h"
#import "ICMHelpCenterCollectionContent.h"
#import "ICMHelpCenterDataError.h"

NSErrorDomain const ICMHelpCenterDataErrorDomain = @"ICMHelpCenterDataErrorDomain";

@implementation ICMHelpCenterArticle
- (instancetype)initWithArticleId:(NSString *)articleId title:(NSString *)title {
    if ((self = [super init])) {
        _articleId = articleId;
        _title = title;
    }
    return self;
}
- (BOOL)isEqual:(id)object {
    if (object == self) return YES;
    if (![object isKindOfClass:[ICMHelpCenterArticle class]]) return NO;
    ICMHelpCenterArticle *other = object;
    return [self.articleId isEqualToString:other.articleId] && [self.title isEqualToString:other.title];
}
- (NSUInteger)hash { return self.articleId.hash; }
- (NSString *)description {
    return [NSString stringWithFormat:@"<%@: %p, articleId: %@, title: %@>", NSStringFromClass(self.class), self, self.articleId, self.title];
}
@end

@implementation ICMHelpCenterArticleAuthor
- (instancetype)initWithAuthorId:(NSString *)authorId displayName:(NSString *)displayName avatarURL:(NSURL *)avatarURL {
    if ((self = [super init])) {
        _authorId = authorId;
        _displayName = displayName;
        _avatarURL = avatarURL;
    }
    return self;
}
- (BOOL)isEqual:(id)object {
    if (object == self) return YES;
    if (![object isKindOfClass:[ICMHelpCenterArticleAuthor class]]) return NO;
    ICMHelpCenterArticleAuthor *other = object;
    return [self.authorId isEqualToString:other.authorId] && [self.displayName isEqualToString:other.displayName] && [self.avatarURL isEqual:other.avatarURL];
}
- (NSUInteger)hash { return self.authorId.hash; }
- (NSString *)description {
    return [NSString stringWithFormat:@"<%@: %p, authorId: %@, displayName: %@, avatarURL: %@>", NSStringFromClass(self.class), self, self.authorId, self.displayName, self.avatarURL];
}
@end

@implementation ICMHelpCenterArticleSearchResult
- (instancetype)initWithArticleId:(NSString *)articleId title:(NSString *)title summary:(NSString *)summary matchingSnippet:(NSString *)matchingSnippet {
    if ((self = [super init])) {
        _articleId = articleId;
        _title = title;
        _summary = summary;
        _matchingSnippet = matchingSnippet;
    }
    return self;
}
- (BOOL)isEqual:(id)object {
    if (object == self) return YES;
    if (![object isKindOfClass:[ICMHelpCenterArticleSearchResult class]]) return NO;
    ICMHelpCenterArticleSearchResult *other = object;
    return [self.articleId isEqualToString:other.articleId] && [self.title isEqualToString:other.title]
        && ([self.summary isEqualToString:other.summary] || (!self.summary && !other.summary))
        && ([self.matchingSnippet isEqualToString:other.matchingSnippet] || (!self.matchingSnippet && !other.matchingSnippet));
}
- (NSUInteger)hash { return self.articleId.hash; }
- (NSString *)description {
    return [NSString stringWithFormat:@"<%@: %p, articleId: %@, title: %@, summary: %@, matchingSnippet: %@>", NSStringFromClass(self.class), self, self.articleId, self.title, self.summary, self.matchingSnippet];
}
@end

@implementation ICMHelpCenterCollection
- (instancetype)initWithCollectionId:(NSString *)collectionId title:(NSString *)title summary:(NSString *)summary articleCount:(NSInteger)articleCount collectionCount:(NSInteger)collectionCount {
    if ((self = [super init])) {
        _collectionId = collectionId;
        _title = title;
        _summary = summary;
        _articleCount = articleCount;
        _collectionCount = collectionCount;
    }
    return self;
}
- (instancetype)initWithCollectionId:(NSString *)collectionId title:(NSString *)title summary:(NSString *)summary articleCount:(NSInteger)articleCount {
    return [self initWithCollectionId:collectionId title:title summary:summary articleCount:articleCount collectionCount:0];
}
- (BOOL)isEqual:(id)object {
    if (object == self) return YES;
    if (![object isKindOfClass:[ICMHelpCenterCollection class]]) return NO;
    ICMHelpCenterCollection *other = object;
    return [self.collectionId isEqualToString:other.collectionId] && [self.title isEqualToString:other.title]
        && ([self.summary isEqualToString:other.summary] || (!self.summary && !other.summary))
        && self.articleCount == other.articleCount && self.collectionCount == other.collectionCount;
}
- (NSUInteger)hash { return self.collectionId.hash; }
- (NSString *)description {
    return [NSString stringWithFormat:@"<%@: %p, collectionId: %@, title: %@, summary: %@, articleCount: %ld, collectionCount: %ld>", NSStringFromClass(self.class), self, self.collectionId, self.title, self.summary, (long)self.articleCount, (long)self.collectionCount];
}
@end

@implementation ICMHelpCenterCollectionContent
- (instancetype)initWithCollectionId:(NSString *)collectionId title:(NSString *)title summary:(NSString *)summary articles:(NSArray<ICMHelpCenterArticle *> *)articles articleCount:(NSInteger)articleCount collections:(NSArray<ICMHelpCenterCollection *> *)collections authors:(NSArray<ICMHelpCenterArticleAuthor *> *)authors {
    if ((self = [super init])) {
        _collectionId = collectionId;
        _title = title;
        _summary = summary;
        _articles = articles;
        _articleCount = articleCount;
        _collections = collections;
        _authors = authors;
    }
    return self;
}
- (BOOL)isEqual:(id)object {
    if (object == self) return YES;
    if (![object isKindOfClass:[ICMHelpCenterCollectionContent class]]) return NO;
    ICMHelpCenterCollectionContent *other = object;
    return [self.collectionId isEqualToString:other.collectionId] && [self.title isEqualToString:other.title]
        && ([self.summary isEqualToString:other.summary] || (!self.summary && !other.summary))
        && [self.articles isEqualToArray:other.articles] && self.articleCount == other.articleCount
        && [self.authors isEqualToArray:other.authors] && [self.collections isEqualToArray:other.collections];
}
- (NSUInteger)hash { return self.collectionId.hash; }
- (NSString *)description {
    return [NSString stringWithFormat:@"<%@: %p, collectionId: %@, title: %@, summary: %@, articles: %@, articleCount: %ld, authors: %@, collections: %@>", NSStringFromClass(self.class), self, self.collectionId, self.title, self.summary, self.articles, (long)self.articleCount, self.authors, self.collections];
}
@end
