package qed.post_it.modules;

import java.sql.Date;

public class Post
{
    public String name;
    public int postId;
    public int publisherId;
    public String publisherName;
    public String publishTime;

    public Post(String n, int poId, int puId, String puName, String pDate)
    {
        name = n;
        postId = poId;
        publisherId = puId;
        publisherName = puName;
        publishTime = pDate;
    }

    @Override
    public String toString()
    {
        return String.format("{\"name\": \"%s\", \"postId\": %d, \"publisherId\": %d, \"publisherName\": \"%s\", \"publishTime\": \"%s\"}",
                name, postId, publisherId, publisherName, publishTime);
    }
}
