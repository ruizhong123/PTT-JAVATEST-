package social.controller;

import org.springframework.web.bind.annotation.RequestBody;

public abstract class CommentController {

    // 1.  新增留言
    public abstract void addComment(@RequestBody String comment){
        
    };



    
}
