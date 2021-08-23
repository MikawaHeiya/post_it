package qed.post_it.utility.exceptions;

public class PasswordUncorrectException extends RuntimeException
{
    public String what;

    public PasswordUncorrectException(String msg)
    {
        super(msg);
        what = msg;
    }
}
