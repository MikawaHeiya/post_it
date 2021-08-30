package qed.post_it.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import qed.post_it.sql.SqlConnector;
import qed.post_it.utility.tools.Utility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class BlockController
{
    public String blockName;
    public String blockDescribe;

    @GetMapping("/post_it/block")
    public String GoBlockPage(@RequestParam("name") String name)
    {
        blockName = name;
        return "Block";
    }

    @RequestMapping(value = "/post_it/block/get_block_info", method = RequestMethod.POST)
    @ResponseBody
    public String GetBlockInfo(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            var sql = new SqlConnector();
            blockDescribe = sql.BlockDescribeOf(blockName);
            sql.CloseConnection();

            return String.format("{\"success\": true, \"name\": \"%s\", \"des\": \"%s\"}", blockName, blockDescribe);
        }
        catch (RuntimeException ex)
        {
            ex.printStackTrace();
            return "{\"success\": false, \"msg\": \"未知错误。\"}";
        }
    }

    @RequestMapping(value = "/post_it/block/get_block_posts_num", method = RequestMethod.POST)
    @ResponseBody
    public String GetBlockPostNum(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            var sql = new SqlConnector();
            var num = sql.GetBlockPostNum(blockName);
            sql.CloseConnection();

            return "{\"success\": true, \"num\": " + num + "}";
        }
        catch (RuntimeException ex)
        {
            ex.printStackTrace();
            return "{\"success\": false, \"msg\": \"未知错误。\"}";
        }
    }

    @RequestMapping(value = "/post_it/block/get_block_posts", method = RequestMethod.POST)
    @ResponseBody
    public String GetBlockPosts(HttpServletRequest request, HttpServletResponse response)
    {
        var page = Integer.parseInt(request.getParameter("page"));

        try
        {
            var sql = new SqlConnector();
            var posts=sql.GetBlockPosts(blockName);
            return "{\"success\":true,\"blockname\":\""+ blockName +"\", \"posts\": " +
                    Utility.JoinJSON(posts.subList((page - 1) * 10, page * 10 > posts.size() ? (page - 1) * 10 + posts.size() % 10 : page * 10)) + "}";
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return  "{\"success\": false, \"msg\": \"未知错误。\"}";
        }
    }

    @RequestMapping(value = "/post_it/block/send_post", method = RequestMethod.POST)
    @ResponseBody
    public String SendPost(HttpServletRequest request, HttpServletResponse response)
    {
        var postName = request.getParameter("postName");
        var reply = request.getParameter("reply");
        var userName = request.getParameter("userName");

        try
        {
            var sql = new SqlConnector();

            var userId = sql.UserIdOf(userName);
            if (userId <= 0)
            {
                return "{\"success\": false, \"msg\": \"用户信息错误。\"}";
            }

            var postId = sql.NewPost(postName, userId, blockName);
            sql.NewReply(reply, postId, userId, true);
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
