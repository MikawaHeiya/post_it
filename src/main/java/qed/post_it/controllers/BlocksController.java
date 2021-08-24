package qed.post_it.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BlocksController
{
    @GetMapping("/post_it")
    public String GoBlocksPage()
    {
        return "Blocks";
    }
}
