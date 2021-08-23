package qed.post_it.sql;

import qed.post_it.utility.exceptions.*;

import java.sql.*;

public class SqlConnector
{
    private Connection connection;
    private Statement statement;

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
}
