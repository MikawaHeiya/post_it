package qed.post_it.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import qed.post_it.sql.SqlConnector;
import qed.post_it.utility.tools.Utility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class MessagesController
{
    @GetMapping("/post_it/messages")
    public String GoMessagesPage()
    {
        return "Messages";
    }

    @RequestMapping(value = "/post_it/messages/get_message_num", method = RequestMethod.POST)
    @ResponseBody
    public String GetMessageNum(HttpServletRequest request, HttpServletResponse response)
    {
        var id = Integer.parseInt(request.getParameter("userId"));

        try
        {
            var sql = new SqlConnector();
            var messages = sql.GetMessages(id);
            sql.CloseConnection();

            return "{\"success\": true, \"num\": " + messages.size() + "}";
        }
        catch (RuntimeException ex)
        {
            ex.printStackTrace();
            return "{\"success\": false, \"msg\": \"未知错误。\"}";
        }
    }

    @RequestMapping(value = "/post_it/messages/get_messages", method = RequestMethod.POST)
    @ResponseBody
    public String GetMessages(HttpServletRequest request, HttpServletResponse response)
    {
        var id = Integer.parseInt(request.getParameter("userId"));
        var page = Integer.parseInt(request.getParameter("page"));

        try
        {
            var sql = new SqlConnector();
            var messages = sql.GetMessages(id);
            sql.CloseConnection();

            return "{\"success\": true, \"messages\": " + Utility.JoinJSON(
                messages.subList((page - 1) * 10, page * 10 > messages.size() ? (page - 1) * 10 + messages.size() % 10 : page * 10)) + "}";
        }
        catch (RuntimeException ex)
        {
            ex.printStackTrace();
            return "{\"success\": false, \"msg\": \"未知错误。\"}";
        }
    }
}
