package qed.post_it.utility.exceptions;

public class UserUnexistsException extends RuntimeException
{
    public String what;

    public UserUnexistsException(String msg)
    {
        super(msg);
        what = msg;
    }
}
