package qed.post_it.utility.tools;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class Utility
{
    public static <T> String JoinJSON(List<T> list)
    {
        var builder = new StringBuilder("[");

        int i = 0;
        for (; i < list.size() - 1; ++i)
        {
            builder.append(list.get(i).toString() + ", ");
        }
        builder.append(list.get(i).toString() + "]");

        return builder.toString();
    }

    public static Pair<String, String> GetUserCookie(HttpServletRequest request)
    {
        var cookies = request.getCookies();
        if (cookies != null)
        {
            String name = null, password = null;
            for (var cookie : cookies)
            {
                if (cookie.getName().equals("userName"))
                {
                    name = cookie.getValue();
                }
                else if (cookie.getName().equals("userPassword"))
                {
                    password = cookie.getValue();
                }
            }

            if (name != null && password != null)
            {
                System.out.println("Detect User Cookie:\nName: " + name + " Password: " + password);
                return new Pair<>(name, password);
            }
        }
        else
        {
            System.out.println("Null Cookie");
        }

        System.out.println("No User Cookie!");
        return null;
    }

    public static void AddUserCookies(HttpServletResponse response, HttpServletRequest request, String name, String password)
    {
        var nameCookie = new Cookie("userName", name);
        nameCookie.setMaxAge(30 * 24 * 60 * 60);
        nameCookie.setPath("/");
        response.addCookie(nameCookie);

        var passwordCookie = new Cookie("userPassword", password);
        passwordCookie.setMaxAge(30 * 24 * 60 * 60);
        passwordCookie.setPath("/");
        response.addCookie(passwordCookie);
        System.out.println("Add User Cookie:\nName: " + name + " Password: " + password);
    }

    public static void RemoveUserCookie(HttpServletResponse response)
    {
        var nameCookie = new Cookie("userName", null);
        nameCookie.setMaxAge(0);
        nameCookie.setPath("/");

        var passwordCookie = new Cookie("userPassword", null);
        passwordCookie.setMaxAge(0);
        passwordCookie.setPath("/");

        response.addCookie(nameCookie);
        response.addCookie(passwordCookie);

        System.out.println("Remove User Cookie!");
    }
}
