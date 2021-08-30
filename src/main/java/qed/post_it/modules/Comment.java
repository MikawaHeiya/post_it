package qed.post_it.modules;

public class Comment
{
    public String comment;
    public int postId;
    public int ownerId;
    public int publisherId;
    public String publisherName;
    public String commentTime;

    public Comment(String comm, int poId, int oId, int puId, String puName, String commTime)
    {
        comment = comm;
        postId = poId;
        ownerId = oId;
        publisherId = puId;
        publisherName = puName;
        commentTime = commTime;
    }

    @Override
    public String toString()
    {
        return String.format("{\"comment\": \"%s\", \"postId\": %d, \"ownerId\": %d, " +
                        "\"publisherId\": %d, \"publisherName\": \"%s\", \"commentTime\": \"%s\"}",
                comment, postId, ownerId, publisherId, publisherName, commentTime);
    }
}
