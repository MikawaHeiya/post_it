package qed.post_it.modules;

public class Message
{
    public String publisherName;
    public int publisherId;
    public int postId;
    public MessageType messageType;
    public String messageTime;
    public String content;

    public Message(String puName, int puId, int poId, MessageType mType, String mTime, String con)
    {
        publisherName = puName;
        publisherId = puId;
        postId = poId;
        messageType = mType;
        messageTime = mTime;
        content = con;
    }

    @Override
    public String toString()
    {
        return String.format("{\"publisherName\": \"%s\", \"publisherId\": %d, \"postId\": %d, \"messageType\": \"%s\", \"messageTime\": \"%s\", \"content\": \"%s\"}",
                publisherName, publisherId, postId, messageType == MessageType.Reply ? "回复" : "评论", messageTime, content);
    }
}
