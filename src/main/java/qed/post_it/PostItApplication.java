package qed.post_it;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import qed.post_it.sql.SqlConnector;
import qed.post_it.utility.exceptions.PasswordUncorrectException;

@SpringBootApplication
public class PostItApplication
{
    public static void main(String[] args)
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }

        SpringApplication.run(PostItApplication.class, args);
    }
}
