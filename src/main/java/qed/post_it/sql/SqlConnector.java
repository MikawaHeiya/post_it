package qed.post_it.sql;

import org.springframework.expression.spel.ast.NullLiteral;
import qed.post_it.modules.*;
import qed.post_it.utility.exceptions.*;
import qed.post_it.utility.tools.*;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeMap;

public class SqlConnector
{
    private Connection connection;
    private Statement statement;
    private DateFormat dateFormat;

    private int CountResult(ResultSet result) throws SQLException
    {
        int count = 0;
        while (result.next())
        {
            ++count;
        }
        return count;
    }

    public SqlConnector()
    {
        try
        {
            connection = DriverManager.getConnection("jdbc:mysql://rm-bp1r163o6k3ge7c655o.mysql.rds.aliyuncs.com:3306/post_it?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&nullCatalogMeansCurrent=true",
                    "mikawa", "SQL@mikawa94740854");
            statement = connection.createStatement();
        }
        catch (SQLException ex)
        {
            CloseConnection();
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.SqlConnector: 创建SQL连接时异常。");
        }

        if (connection == null || statement == null)
        {
            throw new RuntimeException("SqlConnector.SqlConnector: 创建SQL连接时异常。");
        }

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public void CloseConnection()
    {
        try
        {
            if (connection != null)
            {
                connection.close();
            }
            if (statement != null)
            {
                statement.close();
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.CloseConnection: SQL连接关闭时异常。");
        }
    }

    public int Register(String name, String password)
    {
        int id = 0;

        try
        {
            var result = statement.executeQuery("select name from users");
            while (result.next())
            {
                if (result.getString("name").equals(name))
                {
                    throw new UserExistsException("用户'" + name + "'已存在。");
                }
            }

            result = statement.executeQuery("select user_num from infos where id = 0");
            while (result.next())
            {
                id = result.getInt("user_num") + 1;
            }

            if (id <= 0)
            {
                throw new RuntimeException("SqlConnector.Register: 查询数据时异常。");
            }

            result.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.Register: 查询数据时异常。");
        }

        try
        {
            statement.execute(String.format("insert into users values(%d, '%s', md5('%s'), NOW())", id, name, password));
            statement.execute(String.format("update infos set user_num = %d where id = 0", id));
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.Register: 插入数据时异常。");
        }

        return id;
    }

    public void Login(String name, String password)
    {
        try
        {
            var result =  statement.executeQuery(
                    String.format("select name, password from users where name='%s' and password=md5('%s')", name, password));
            if (CountResult(result) == 0)
            {
                throw new PasswordUncorrectException("用户名或密码错误。");
            }

            result.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.Login: 查询数据时异常。");
        }
    }

    public String UserNameOf(int id)
    {
        String name = null;

        try
        {
            var result = statement.executeQuery("select name from users where id=" + id);

            if (result.next())
            {
                name = result.getString("name");
            }

            result.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.UserNameOf: 查询数据时异常");
        }

        return name;
    }

    public int UserIdOf(String name)
    {
        int id = 0;

        try
        {
            var result = statement.executeQuery("select id from users where name='" + name + "'");

            if (result.next())
            {
                id = result.getInt("id");
            }

            result.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.UserIdOf: 查询数据时异常");
        }

        return id;
    }

    public ArrayList<Block> GetBlocks()
    {
        var blockInfo = new ArrayList<Block>();
        try
        {
            var result = statement.executeQuery("select * from blocks");
            while(result.next())
            {
                blockInfo.add(new Block(result.getString("block_name"),result.getString("block_describe")));
            }
            result.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.GetBlockInfo: 查询数据时异常");
        }

        return blockInfo;
    }

    public String BlockDescribeOf(String name)
    {
        var describe = "";

        try
        {
            var result = statement.executeQuery("select block_describe from blocks where block_name='" + name + "'");
            if (result.next())
            {
                describe = result.getString("block_describe");
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.BlockDescribeOf: 查询数据时异常");
        }

        return describe;
    }

    public Post GetPost(int id)
    {
        Post post = null;
        try
        {
            var result = statement.executeQuery("select * from posts, users where posts.post_id=" + id + " and posts.publisher_id=users.id");

            if (result.next())
            {
                post = new Post(result.getString("post_name"), result.getInt("post_id"),
                        result.getInt("publisher_id"), result.getInt("high_floor"),
                        result.getString("name"), dateFormat.format(result.getTimestamp("publish_time")));
            }

            result.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.GetPost: 查询数据时异常");
        }

        if (post == null)
        {
            throw new RuntimeException("SqlConnector.GetPost: 查询数据时异常");
        }

        return post;
    }

    public ArrayList<Post> GetBlockPosts(String blockName)
    {
        var posts = new ArrayList<Post>();
        try
        {
            var result = statement.executeQuery("select * from posts, users where posts.publisher_id=users.id and posts.block='" + blockName + "'");

            while (result.next())
            {
                posts.add(new Post(result.getString("post_name"), result.getInt("post_id"),
                        result.getInt("publisher_id"), result.getInt("high_floor"),
                        result.getString("name"), dateFormat.format(result.getTimestamp("publish_time"))));
            }

            result.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.GetBlockPosts: 查询数据时异常");
        }

        return posts;
    }

    public int GetBlockPostNum(String blockName)
    {
        int num = 0;

        try
        {
            var result = statement.executeQuery("select COUNT(*) from posts where block='" + blockName + "'");
            if (result.next())
            {
                num = result.getInt("COUNT(*)");
            }
            result.close();

            return num;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.GetBlockPostNum: 查询数据时异常");
        }
    }

    public ArrayList<Post> GetHotPosts()
    {
        var posts = new ArrayList<Post>();
        try
        {
            var result = statement.executeQuery("select * from posts, users where posts.publisher_id=users.id order by publish_time limit 5");

            while (result.next())
            {
                posts.add(new Post(result.getString("post_name"), result.getInt("post_id"),
                        result.getInt("publisher_id"), result.getInt("high_floor"),
                        result.getString("name"), dateFormat.format(result.getTimestamp("publish_time"))));
            }

            result.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.GetPosts: 查询数据时异常");
        }

        return posts;
    }

    //获取用户所发的全部帖子
    public ArrayList<Post> GetUserPosts(int id){
        var allPostInfo=new ArrayList<Post>();
        try{
            var result=statement.executeQuery("select * from posts, users where posts.publisher_id=users.id and publisher_id="+id+" order by publisher_time");
            while(result.next()){
                allPostInfo.add(new Post(result.getString("post_name"),
                        result.getInt("post_id"),
                        result.getInt("publisher_id"),
                        result.getInt("high_floor"),
                        result.getString("name"),
                        dateFormat.format(result.getTimestamp("publish_time"))));
            }
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("SqlConnector.GetPosts: 查询数据时异常");
        }
        return allPostInfo;
    }

    public ArrayList<Reply> GetReplies(int postId)
    {
        var replies = new ArrayList<Reply>();

        try
        {
            var result = statement.executeQuery("select * from replies, users where replies.publisher_id=users.id and replies.post_id=" + postId + " order by replies.floor");

            while (result.next())
            {
                replies.add(new Reply(result.getString("reply"), result.getInt("post_id"),
                        result.getInt("publisher_id"), result.getString("name"),
                        result.getInt("floor"), result.getInt("likes"),
                        result.getInt("dislikes"),
                        dateFormat.format(result.getTimestamp("reply_time"))));
            }

            result.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.GetReplies: 查询数据时异常");
        }

        return replies;
    }

    public ArrayList<Comment> GetComments(int postId, int floor)
    {
        var comments = new ArrayList<Comment>();

        try
        {
            var result = statement.executeQuery(String.format("select * from comments, users where comments.publisher_id=users.id and comments.post_id=%d and comments.floor=%d", postId, floor));
            while (result.next())
            {
                comments.add(new Comment(result.getString("comment"), result.getInt("post_id"),
                        result.getInt("owner_id"), result.getInt("publisher_id"),
                        result.getString("name"), dateFormat.format(result.getTimestamp("comment_time"))));
            }
            result.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.GetComments: 查询数据时异常");
        }

        return comments;
    }

    //获取用户收到的回复与评论
    public ArrayList<Message> GetMessages(int id)
    {
        var messages = new ArrayList<Message>();
        try
        {
            var result = statement.executeQuery("select replies.publisher_id, users.name, posts.post_id, replies.reply_time, replies.reply from replies, users, posts where replies.publisher_id=users.id and posts.publisher_id=" + id + " and posts.post_id=replies.post_id");
            while (result.next())
            {
                messages.add(new Message(result.getString("name"), result.getInt("publisher_id"),
                        result.getInt("post_id"), MessageType.Reply,
                        dateFormat.format(result.getTimestamp("reply_time")), result.getString("reply")));
            }
            result.close();

            var res = statement.executeQuery("select comments.publisher_id, users.name, comments.post_id, comments.comment_time, comments.comment from comments, users where comments.owner_id=" + id + " and comments.publisher_id=users.id");
            while (res.next())
            {
                messages.add(new Message(res.getString("name"), res.getInt("publisher_id"),
                        res.getInt("post_id"), MessageType.Comment,
                        dateFormat.format(res.getTimestamp("comment_time")), res.getString("comment")));
            }
            res.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.GetMessages: 查询数据时异常");
        }

        return messages;
    }

    public int GetReplyNum(int id)
    {
        int num = 0;

        try
        {
            var result = statement.executeQuery("select COUNT(*) from replies where post_id=" + id);
            if (result.next())
            {
                num = result.getInt("COUNT(*)");
            }
            result.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.GetReplyNum: 查询数据时异常。");
        }

        return num;
    }

    public int GetCommentNum(int postId, int floor)
    {
        int num = 0;

        try
        {
            var result = statement.executeQuery(String.format("select COUNT(*) from comments where post_id=%d and floor=%d", postId, floor));
            if (result.next())
            {
                num = result.getInt("COUNT(*)");
            }
            result.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.GetCommentNum: 查询数据时异常。");
        }

        return num;
    }

    public int NewPost(String postName, int publisherId, String block)
    {
        int id = 0;

        try
        {
            var result = statement.executeQuery("select post_num from infos where id = 0");
            while (result.next())
            {
                id = result.getInt("post_num") + 1;
            }

            if (id <= 0)
            {
                throw new RuntimeException("SqlConnector.Register: 查询数据时异常。");
            }

            result.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.NewPost: 查询数据时异常。");
        }

        try
        {
            statement.execute(String.format("insert into posts values('%s', NOW(), %d, %d, '%s', %d)",
                    postName, id, publisherId, block, 0));
            statement.execute(String.format("update infos set post_num=%d where id=0", id));
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.NewPost: 更新数据时异常。");
        }

        return id;
    }

    public void NewReply(String reply, int postId, int publisherId, boolean newPost)
    {
        try
        {
            int highFloor = 0;
            var result = statement.executeQuery("select high_floor from posts where post_id=" + postId);
            if (result.next())
            {
                highFloor = result.getInt("high_floor");
            }

            if (highFloor < 0)
            {
                throw new RuntimeException("SqlConnector.NewReply: 查询数据时异常。");
            }

            int floor = newPost ? 0 : highFloor + 1;
            statement.execute(String.format("insert into replies values(%d, %d, '%s', 0, 0, NOW(), %d)",
                    postId, floor, reply, publisherId));
            statement.execute(String.format("update posts set high_floor=%d where post_id=%d", floor, postId));
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.NewReply: 更新数据时异常。");
        }
    }

    public void NewComment(String comment, int postId, int floor, int publisherId, int ownerId)
    {
        try
        {
            statement.execute(String.format("insert into comments values(%d, '%s', NOW(), %d, %d, %d)",
                    postId, comment, floor, ownerId, publisherId));
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.NewComment: 更新数据时异常。");
        }
    }

    public void Like(int postId, int floor)
    {
        try
        {
            var result = statement.executeQuery(
                    String.format("select likes from replies where post_id=%d and floor=%d", postId, floor));

            if (result.next())
            {
                int like = result.getInt("likes");

                statement.execute(String.format("update replies set likes=%d where post_id=%d and floor=%d",
                        like + 1, postId, floor));
            }

            result.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.Like: 更新数据时异常。");
        }
    }

    public void UnLike(int postId, int floor)
    {
        try
        {
            var result = statement.executeQuery(
                    String.format("select likes from replies where post_id=%d and floor=%d", postId, floor));

            if (result.next())
            {
                int like = result.getInt("likes");

                if (like > 0)
                {
                    statement.execute(String.format("update replies set likes=%d where post_id=%d and floor=%d",
                            like - 1, postId, floor));
                }
            }

            result.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.Like: 更新数据时异常。");
        }
    }

    public void Dislike(int postId, int floor)
    {
        try
        {
            var result = statement.executeQuery(
                    String.format("select dislikes from replies where post_id=%d and floor=%d", postId, floor));

            if (result.next())
            {
                int dislike = result.getInt("dislikes");

                statement.execute(String.format("update replies set dislikes=%d where post_id=%d and floor=%d",
                        dislike + 1, postId, floor));
            }

            result.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.Like: 更新数据时异常。");
        }
    }

    public void UnDislike(int postId, int floor)
    {
        try
        {
            var result = statement.executeQuery(
                    String.format("select dislikes from replies where post_id=%d and floor=%d", postId, floor));

            if (result.next())
            {
                int dislike = result.getInt("dislikes");

                if (dislike > 0)
                {
                    statement.execute(String.format("update replies set dislikes=%d where post_id=%d and floor=%d",
                            dislike - 1, postId, floor));
                }
            }

            result.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.Like: 更新数据时异常。");
        }
    }
}
