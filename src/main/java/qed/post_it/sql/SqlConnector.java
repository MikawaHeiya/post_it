package qed.post_it.sql;

import qed.post_it.modules.*;
import qed.post_it.utility.exceptions.*;
import qed.post_it.utility.tools.*;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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

    public void Register(String name, String password)
    {
        int id = 0;

        try
        {
            var result = statement.executeQuery("select name from users");
            while (result.next())
            {
                if (result.getString("name").equals(name))
                {
                    throw new UserExistsException("用户\"" + name + "\"已存在。");
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
            statement.execute(String.format("insert into users values(%d, '%s', md5('%s'))", id, name, password));
            statement.execute(String.format("update infos set user_num = %d where id = 0", id));
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("SqlConnector.Register: 插入数据时异常。");
        }
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

            result.next();
            if (result.isFirst())
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

    public ArrayList<Post> GetPosts(String blockName)
    {
        var posts = new ArrayList<Post>();
        try
        {
            var result = statement.executeQuery("select * from posts, users where posts.publisher_id=users.id and posts.block='" + blockName + "'");

            while (result.next())
            {
                posts.add(new Post(result.getString("post_name"), result.getInt("post_id"),
                        result.getInt("publisher_id"), result.getString("name"),
                        dateFormat.format(result.getTimestamp("publish_time"))));
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
                        result.getInt("floor"), result.getInt("like"),
                        result.getInt("dislike"),
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
}
