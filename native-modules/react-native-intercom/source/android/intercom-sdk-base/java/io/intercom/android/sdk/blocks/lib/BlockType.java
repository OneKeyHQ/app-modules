package io.intercom.android.sdk.blocks.lib;

import android.view.View;
import android.view.ViewGroup;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;
import io.intercom.android.sdk.blocks.lib.models.ConversationRating;
import io.intercom.android.sdk.blocks.lib.models.Link;
import io.intercom.android.sdk.blocks.lib.models.LinkList;
import io.intercom.android.sdk.models.carousel.ActionType;
import io.intercom.android.sdk.models.carousel.AppearanceType;
import io.intercom.android.sdk.utilities.commons.HtmlCompat;
import java.util.Locale;

/* JADX INFO: loaded from: intercom-sdk-base-18.10.0.aar:classes.jar:io/intercom/android/sdk/blocks/lib/BlockType.class */
public enum BlockType {
    PARAGRAPH("paragraph") { // from class: io.intercom.android.sdk.blocks.lib.BlockType.1
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            return viewHolder.getParagraph().addParagraph(HtmlCompat.fromHtml(block.getText()), block.getAlign(), blockMetadata, layout);
        }
    },
    HEADING("heading") { // from class: io.intercom.android.sdk.blocks.lib.BlockType.2
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            return viewHolder.getHeading().addHeading(HtmlCompat.fromHtml(block.getText()), block.getAlign(), blockMetadata, layout);
        }
    },
    SUBHEADING("subheading") { // from class: io.intercom.android.sdk.blocks.lib.BlockType.3
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            return viewHolder.getSubheading().addSubheading(HtmlCompat.fromHtml(block.getText()), block.getAlign(), blockMetadata, layout);
        }
    },
    CODE("code") { // from class: io.intercom.android.sdk.blocks.lib.BlockType.4
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            return viewHolder.getCode().addCode(HtmlCompat.fromHtml(block.getText()), blockMetadata, layout);
        }
    },
    LINK(ActionType.LINK) { // from class: io.intercom.android.sdk.blocks.lib.BlockType.5
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            return viewHolder.getLink().addLinkBlock(Link.fromBlock(block), blockMetadata, layout);
        }
    },
    CONVERSATIONRATING("conversationRating") { // from class: io.intercom.android.sdk.blocks.lib.BlockType.6
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            return viewHolder.getConversationRating().addConversationRatingBlock(ConversationRating.fromBlock(block), blockMetadata, layout);
        }
    },
    LINKLIST("linkList") { // from class: io.intercom.android.sdk.blocks.lib.BlockType.7
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            return viewHolder.getLinkList().addLinkListBlock(LinkList.fromBlock(block), blockMetadata, layout);
        }
    },
    UNORDEREDLIST("unorderedList") { // from class: io.intercom.android.sdk.blocks.lib.BlockType.8
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            return viewHolder.getUnorderedList().addUnorderedList(block.getItems(), blockMetadata, layout);
        }
    },
    ORDEREDLIST("orderedList") { // from class: io.intercom.android.sdk.blocks.lib.BlockType.9
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            return viewHolder.getOrderedList().addOrderedList(block.getItems(), blockMetadata, layout);
        }
    },
    ATTACHMENTLIST("attachmentList") { // from class: io.intercom.android.sdk.blocks.lib.BlockType.10
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            return viewHolder.getAttachmentList().addAttachmentList(block.getAttachments(), blockMetadata, layout);
        }
    },
    IMAGE(AppearanceType.IMAGE) { // from class: io.intercom.android.sdk.blocks.lib.BlockType.11
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            return viewHolder.getImage().addImage(block.getUrl(), getLinkUrl(block), block.getDescription(), block.getWidth(), block.getHeight(), block.getAlign(), blockMetadata, layout);
        }
    },
    IMAGETEXT("imageText") { // from class: io.intercom.android.sdk.blocks.lib.BlockType.12
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            block.getImage();
            return viewHolder.getImageText().addImageText(HtmlCompat.fromHtml(block.getTitle()), HtmlCompat.fromHtml(block.getText()), block.getImageUrl(), block.getDescription(), block.getImageWidth(), block.getImageHeight(), blockMetadata, layout);
        }
    },
    BUTTON("button") { // from class: io.intercom.android.sdk.blocks.lib.BlockType.13
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            return viewHolder.getButton().addButton(HtmlCompat.fromHtml(block.getText()), getLinkUrl(block), block.getAlign(), blockMetadata, layout);
        }
    },
    FACEBOOKLIKEBUTTON("facebookLikeButton") { // from class: io.intercom.android.sdk.blocks.lib.BlockType.14
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            return viewHolder.getFacebookButton().addFacebookButton(block.getUrl(), block.getAlign(), blockMetadata, layout);
        }
    },
    TWITTERFOLLOWBUTTON("twitterFollowButton") { // from class: io.intercom.android.sdk.blocks.lib.BlockType.15
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            return viewHolder.getTwitterButton().addTwitterButton("https://twitter.com/" + block.getUsername(), block.getAlign(), blockMetadata, layout);
        }
    },
    VIDEO("video") { // from class: io.intercom.android.sdk.blocks.lib.BlockType.16
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            return viewHolder.getVideo().addVideo(block.getText(), VideoProvider.videoValueOf(block.getProvider()), block.getId(), blockMetadata, layout);
        }
    },
    VIDEOFILE("videoFile") { // from class: io.intercom.android.sdk.blocks.lib.BlockType.17
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            return viewHolder.getVideoFile().addVideoFile(block.getText(), block.getUrl(), block.getThumbnailUrl(), blockMetadata, layout);
        }
    },
    MESSENGERCARD("messengerCard") { // from class: io.intercom.android.sdk.blocks.lib.BlockType.18
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            return viewHolder.getMessengerCardBlock().addCard(block.getText(), block.getFallbackUrl(), blockMetadata, layout);
        }
    },
    LOCALIMAGE("localImage") { // from class: io.intercom.android.sdk.blocks.lib.BlockType.19
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            return viewHolder.getLocalImage().addImage(block.getLocalUri(), block.getWidth(), block.getHeight(), block.getAlign(), blockMetadata, layout);
        }
    },
    LOCAL_ATTACHMENT("local_attachment") { // from class: io.intercom.android.sdk.blocks.lib.BlockType.20
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            return viewHolder.getLocalAttachment().addAttachment((io.intercom.android.sdk.blocks.lib.models.BlockAttachment)block.getAttachments().get(0), blockMetadata, layout);
        }
    },
    CREATETICKETCARD("createTicketCard") { // from class: io.intercom.android.sdk.blocks.lib.BlockType.21
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            return viewHolder.getParagraph().addParagraph(HtmlCompat.fromHtml(block.getText()), block.getAlign(), blockMetadata, layout);
        }
    },
    TABLE("table") { // from class: io.intercom.android.sdk.blocks.lib.BlockType.22
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            throw new BlockTypeNotImplementedException();
        }
    },
    UNKNOWN("unknown") { // from class: io.intercom.android.sdk.blocks.lib.BlockType.23
        @Override // io.intercom.android.sdk.blocks.lib.BlockType
        public View generateViewFromBlockAndLayout(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) {
            if (!block.getText().isEmpty() && viewHolder.getParagraph() != null) {
                return PARAGRAPH.generateViewFromBlockAndLayout(viewHolder, block, layout, blockMetadata);
            }
            throw new BlockTypeNotImplementedException();
        }
    };

    private final String serializedName;

    abstract View generateViewFromBlockAndLayout(BlocksViewHolder blocksViewHolder, Block block, ViewGroup viewGroup, BlockMetadata blockMetadata);

    BlockType(String serializedName) {
        this.serializedName = serializedName;
    }

    public String getSerializedName() {
        return this.serializedName;
    }

    public View getView(BlocksViewHolder viewHolder, Block block, ViewGroup layout, BlockMetadata blockMetadata) throws BlockTypeNotImplementedException {
        try {
            return generateViewFromBlockAndLayout(viewHolder, block, layout, blockMetadata);
        } catch (NullPointerException e) {
            try {
                return UNKNOWN.generateViewFromBlockAndLayout(viewHolder, block, layout, blockMetadata);
            } catch (BlockTypeNotImplementedException | NullPointerException e2) {
                throw new BlockTypeNotImplementedException();
            }
        }
    }

    protected static String getLinkUrl(Block block) {
        return block.getTrackingUrl().isEmpty() ? block.getLinkUrl() : block.getTrackingUrl();
    }

    public static BlockType typeValueOf(String type) {
        BlockType blockType = UNKNOWN;
        try {
            blockType = valueOf(type.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException | NullPointerException e) {
        }
        return blockType;
    }
}
