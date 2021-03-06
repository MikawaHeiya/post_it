package qed.post_it.modules;

public class Reply
{
    public String reply;
    public int postId;
    public int publisherId;
    public String publisherName;
    public int floor;
    public int likes;
    public int dislikes;
    public String publishTime;

    public Reply(String rep, int poId, int puId, String puName, int f, int l, int dl, String pTime)
    {
        reply = rep;
        postId = poId;
        publisherId = puId;
        publisherName = puName;
        floor = f;
        likes = l;
        dislikes = dl;
        publishTime = pTime;
    }

    @Override
    public String toString()
    {
        return String.format("{\"reply\": \"%s\", \"postId\": %d, \"publisherId\": %d, \"publisherName\": \"%s\", \"floor\": %d, \"publishTime\": \"%s\"}",
                reply, postId, publisherId, publisherName, floor, publishTime);
    }
}
