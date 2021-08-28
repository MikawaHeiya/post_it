package qed.post_it.modules;

import java.sql.Date;

public class Post
{
    public String name;
    public int postId;
    public int publisherId;
    public int highFloor;
    public String publisherName;
    public String publishTime;

    public Post(String n, int poId, int puId, int hf, String puName, String pDate)
    {
        name = n;
        postId = poId;
        publisherId = puId;
        highFloor = hf;
        publisherName = puName;
        publishTime = pDate;
    }

    @Override
    public String toString()
    {
        return String.format("{\"name\": \"%s\", \"postId\": %d, \"publisherId\": %d, \"highFloor\": %d, \"publisherName\": \"%s\", \"publishTime\": \"%s\"}",
                name, postId, publisherId, highFloor, publisherName, publishTime);
    }
}
