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

    @GetMapping("/post_it/block")
    public String GoBlockPage(@RequestParam("name") String name)
    {
        blockName = name;
        System.out.println(blockName);
        return "Block";
    }

    @RequestMapping(value = "/post_it/block/get_block_name", method = RequestMethod.POST)
    @ResponseBody
    public String GetBlockName(HttpServletRequest request, HttpServletResponse response)
    {
        return blockName;
    }

    @RequestMapping(value = "/post_it/block/get_block_posts", method = RequestMethod.POST)
    @ResponseBody
    public String GetBlockPosts(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            var sql = new SqlConnector();
            var posts=sql.GetBlockPosts(blockName);
            return "{\"success\":true,\"blockname\":\""+ blockName +"\", \"posts\": "+ Utility.JoinJSON(posts) + "}";
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return  "{\"success\": false, \"msg\": \"未知错误。\"}";
        }
    }
}
