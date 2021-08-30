package qed.post_it.controllers;

import org.apache.coyote.http11.filters.SavedRequestInputFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import qed.post_it.modules.Post;
import qed.post_it.sql.SqlConnector;
import qed.post_it.utility.tools.Utility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class PostController
{
    public Post post;

    @GetMapping("/post_it/post")
    public String GoPostPage(@RequestParam("id") int id)
    {
        try
        {
            var sql = new SqlConnector();
            post = sql.GetPost(id);
            sql.CloseConnection();
        }
        catch (RuntimeException ex)
        {
            ex.printStackTrace();
        }

        return "Post";
    }

    @RequestMapping(value = "/post_it/post/get_post", method = RequestMethod.POST)
    @ResponseBody
    public String GetPost(HttpServletRequest request, HttpServletResponse response)
    {
        return post.toString();
    }

    @RequestMapping(value = "/post_it/post/get_reply_num", method = RequestMethod.POST)
    @ResponseBody
    public String GetReplyNum(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            var sql = new SqlConnector();
            int num = sql.GetReplyNum(post.postId);
            sql.CloseConnection();

            return "{\"success\": true, \"num\": " + num + "}";
        }
        catch (RuntimeException ex)
        {
            ex.printStackTrace();
            return "{\"success\": false, \"msg\": \"未知错误。\"}";
        }
    }

    @RequestMapping(value = "/post_it/post/get_comment_num", method = RequestMethod.POST)
    @ResponseBody
    public String GetCommentNum(HttpServletRequest request, HttpServletResponse response)
    {
        int floor = Integer.parseInt(request.getParameter("floor"));

        try
        {
            var sql = new SqlConnector();
            int num = sql.GetCommentNum(post.postId, floor);
            sql.CloseConnection();

            return "{\"success\": true, \"num\": " + num + "}";
        }
        catch (RuntimeException ex)
        {
            ex.printStackTrace();
            return "{\"success\": false, \"msg\": \"未知错误。\"}";
        }
    }

    @RequestMapping(value = "/post_it/post/get_replies", method = RequestMethod.POST)
    @ResponseBody
    public String GetReplies(HttpServletRequest request, HttpServletResponse response)
    {
        int page = Integer.parseInt(request.getParameter("page"));

        try
        {
            var sql = new SqlConnector();
            var replies = sql.GetReplies(post.postId);
            sql.CloseConnection();

            return "{\"success\": true, \"replies\": " + Utility.JoinJSON(
                    replies.subList((page - 1) * 10, page * 10 > replies.size() ? (page - 1) * 10 + replies.size() % 10 : page * 10)) + "}";
        }
        catch (RuntimeException ex)
        {
            ex.printStackTrace();
            return "{\"success\": false, \"msg\": \"未知错误。\"}";
        }
    }

    @RequestMapping(value = "/post_it/post/get_comments", method = RequestMethod.POST)
    @ResponseBody
    public String GetComments(HttpServletRequest request, HttpServletResponse response)
    {
        int floor = Integer.parseInt(request.getParameter("floor"));

        try
        {
            var sql = new SqlConnector();
            var comments = sql.GetComments(post.postId, floor);
            sql.CloseConnection();

            return "{\"success\": true, \"comments\": " + Utility.JoinJSON(comments) + "}";
        }
        catch (RuntimeException ex)
        {
            ex.printStackTrace();
            return "{\"success\": false, \"msg\": \"未知错误。\"}";
        }
    }

    @RequestMapping(value = "/post_it/post/send_reply", method = RequestMethod.POST)
    @ResponseBody
    public String SendReply(HttpServletRequest request, HttpServletResponse response)
    {
        var userName = request.getParameter("userName");
        var reply = request.getParameter("reply");

        try
        {
            var sql = new SqlConnector();
            int userId = sql.UserIdOf(userName);

            if (userId <= 0)
            {
                return "{\"success\": false, \"msg\": \"用户信息错误。\"}";
            }

            sql.NewReply(reply, post.postId, userId, false);
            sql.CloseConnection();

            return "{\"success\": true, \"msg\": \"发送成功~\"}";
        }
        catch (RuntimeException ex)
        {
            ex.printStackTrace();
            return "{\"success\": false, \"msg\": \"未知错误。\"}";
        }
    }

    @RequestMapping(value = "/post_it/post/send_comment", method = RequestMethod.POST)
    @ResponseBody
    public String SendComment(HttpServletRequest request, HttpServletResponse response)
    {
        var userName = request.getParameter("userName");
        var comment = request.getParameter("comment");
        var floor = Integer.parseInt(request.getParameter("floor"));
        var ownerId = Integer.parseInt(request.getParameter("ownerId"));

        try
        {
            var sql = new SqlConnector();
            int userId = sql.UserIdOf(userName);

            if (userId <= 0)
            {
                return "{\"success\": false, \"msg\": \"用户信息错误。\"}";
            }

            sql.NewComment(comment, post.postId, floor, userId, ownerId);
            sql.CloseConnection();

            return "{\"success\": true, \"msg\": \"发送成功~\"}";
        }
        catch (RuntimeException ex)
        {
            ex.printStackTrace();
            return "{\"success\": false, \"msg\": \"未知错误。\"}";
        }
    }
}
