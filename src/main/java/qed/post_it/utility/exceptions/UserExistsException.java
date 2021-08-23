package qed.post_it.utility.exceptions;

public class UserExistsException extends RuntimeException
{
    public String what;

    public UserExistsException(String msg)
    {
        super(msg);
        what = msg;
    }
}
