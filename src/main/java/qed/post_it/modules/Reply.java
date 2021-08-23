package qed.post_it.modules;

public class Reply
{
    public String reply;
    public int postId;
    public int publisherId;
    public String publisherName;
    public int floor;
    public int like;
    public int dislike;
    public String publishTime;

    public Reply(String rep, int poId, int puId, String puName, int f, int l, int dl, String pTime)
    {
        reply = rep;
        postId = poId;
        publisherId = puId;
        publisherName = puName;
        floor = f;
        like = l;
        dislike = dl;
        publishTime = pTime;
    }

    @Override
    public String toString()
    {
        return "Reply{" +
                "reply='" + reply + '\'' +
                ", postId=" + postId +
                ", publisherId=" + publisherId +
                ", publisherName='" + publisherName + '\'' +
                ", floor=" + floor +
                ", like=" + like +
                ", dislike=" + dislike +
                ", publishTime='" + publishTime + '\'' +
                '}';
    }
}
