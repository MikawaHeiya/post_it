package qed.post_it.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import qed.post_it.sql.SqlConnector;
import qed.post_it.utility.exceptions.PasswordUncorrectException;
import qed.post_it.utility.exceptions.UserExistsException;
import qed.post_it.utility.tools.Utility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class BlocksController
{
    @GetMapping("/post_it")
    public String GoBlocksPage()
    {
        return "Blocks";
    }
    @GetMapping("/post_it/Posts")
    public String GoPostsPage(){return "Posts";}

    @RequestMapping(value = "/post_it/login", method = RequestMethod.POST)
    @ResponseBody
    public String Login(HttpServletRequest request, HttpServletResponse response)
    {
        var name = request.getParameter("name");
        var password = request.getParameter("password");
        int id = 0;

        try
        {
            var sql = new SqlConnector();
            sql.Login(name, password);
            id = sql.UserIdOf(name);
            sql.CloseConnection();
        }
        catch (PasswordUncorrectException ex)
        {
            return "{\"success\": false, \"isLogin\": false, \"msg\": \"" + ex.what + "\"}";
        }
        catch (RuntimeException ex)
        {
            return "{\"success\": false, \"isLogin\": false, \"msg\": \"未知错误。\"}";
        }

        if (id > 0)
        {
            Utility.AddUserCookies(response, request, name, password);
            return String.format("{\"success\": true, \"isLogin\": true, \"name\": \"%s\", \"id\": %d}", name, id);
        }

        return "{\"success\": false, \"isLogin\": false, \"msg\": \"未知错误。\"}";
    }

    @RequestMapping(value = "/post_it/register", method = RequestMethod.POST)
    @ResponseBody
    public String Register(HttpServletRequest request, HttpServletResponse response)
    {
        var name = request.getParameter("name");
        var password = request.getParameter("password");
        var id = 0;

        try
        {
            var sql = new SqlConnector();
            id = sql.Register(name, password);
            sql.CloseConnection();
        }
        catch (UserExistsException ex)
        {
            System.out.println("{\"success\": false, \"isLogin\": false, \"msg\": \"" + ex.what + "\"}");
            return "{\"success\": false, \"isLogin\": false, \"msg\": \"" + ex.what + "\"}";
        }
        catch (RuntimeException ex)
        {
            return "{\"success\": false, \"isLogin\": false, \"msg\": \"未知错误。\"}";
        }

        if (id > 0)
        {
            Utility.AddUserCookies(response, request, name, password);
            return String.format("{\"success\": true, \"isLogin\": true, \"name\": \"%s\", \"id\": %d}", name, id);
        }

        return "{\"success\": false, \"isLogin\": false, \"msg\": \"未知错误。\"}";
    }

    @RequestMapping(value = "/post_it/logout", method = RequestMethod.POST)
    @ResponseBody
    public void Logout(HttpServletRequest request, HttpServletResponse response)
    {
        Utility.RemoveUserCookie(response);
    }

    @RequestMapping(value = "/post_it/get_account_info", method = RequestMethod.POST)
    @ResponseBody
    public String GetAccountInfo(HttpServletRequest request, HttpServletResponse response)
    {
        var pair = Utility.GetUserCookie(request);

        if (pair == null)
        {
            return "{\"success\": true, \"isLogin\": false}";
        }
        else
        {
            int id = 0;
            try
            {
                var sql = new SqlConnector();
                sql.Login(pair.first, pair.second);
                id = sql.UserIdOf(pair.first);
                sql.CloseConnection();
            }
            catch (PasswordUncorrectException ex)
            {
                return "{\"success\": false, \"msg\":\"登录已过期，请重新登录。\", \"isLogin\": false}";
            }
            catch (RuntimeException ex)
            {
                return "{\"success\": false, \"msg\": \"未知错误。\", \"isLogin\": false}}";
            }

            if (id > 0)
            {
                return String.format("{\"success\": true, \"isLogin\": true, \"name\": \"%s\", \"id\": %d}", pair.first, id);
            }

            return "{\"success\": false, \"msg\": \"未知错误。\", \"isLogin\": false}}";
        }
    }

    @RequestMapping(value = "/post_it/get_blocks", method = RequestMethod.POST)
    @ResponseBody
    public String GetBlocks(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            var sql = new SqlConnector();
            var blocks = sql.GetBlocks();
            sql.CloseConnection();

            return "{\"success\": true, \"blocks\": " + Utility.JoinJSON(blocks) + "}";
        }
        catch (RuntimeException ex)
        {
            ex.printStackTrace();
            return "{\"success\": false, \"msg\":\"未知错误。\"}";
        }
    }

    @RequestMapping(value = "/post_it/get_hot_posts", method = RequestMethod.POST)
    @ResponseBody
    public String GetHotPosts(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            var sql = new SqlConnector();
            var posts = sql.GetHotPosts();
            sql.CloseConnection();

            return "{\"success\": true, \"hotPosts\": " + Utility.JoinJSON(posts) + "}";
        }
        catch (RuntimeException ex)
        {
            ex.printStackTrace();
            return "{\"success\": false, \"msg\": \"未知错误。\"}";
        }
    }
}
