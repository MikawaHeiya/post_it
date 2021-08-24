package qed.post_it.modules;

public class Message
{
    public String publisherName;
    public int publisherId;
    public MessageType messageType;
    public String messageTime;

    public Message(String puName, int puId, MessageType mType, String mTime)
    {
        publisherName = puName;
        publisherId = puId;
        messageType = mType;
        messageTime = mTime;
    }

    @Override
    public String toString()
    {
        return "Message{" +
                "publisherName='" + publisherName + '\'' +
                ", publisherId=" + publisherId +
                ", messageType=" + messageType +
                ", messageTime='" + messageTime + '\'' +
                '}';
    }
}
