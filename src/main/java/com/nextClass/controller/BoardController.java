package com.nextClass.controller;

import com.nextClass.dto.*;
import com.nextClass.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class BoardController {
    private final BoardService boardService;


    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping(value = "/post/{postSequence}")
    public ResponseEntity<ResponseDto<?>> getPost(@PathVariable Integer postSequence){
        return ResponseEntity.ok(boardService.getPost(postSequence));
    }

    @PostMapping(value = "/post_save")
    public ResponseEntity<ResponseDto<?>> savePost(@RequestBody PostSaveRequestDto requestBody){
        return ResponseEntity.ok(boardService.savePost(requestBody));
    }

    @PostMapping(value = "/post_change")
    public ResponseEntity<ResponseDto<?>> changePost(@RequestBody PostChangeRequestDto requestBody){
        return ResponseEntity.ok(boardService.changePost(requestBody));
    }
    @PostMapping(value = "/post_delete")
    public ResponseEntity<ResponseDto<?>> deletePost(@RequestBody PostDeleteRequestDto requestBody){
        return ResponseEntity.ok(boardService.deletePost(requestBody));
    }


    @PostMapping(value = "/post_list")
    public ResponseEntity<ResponseDto<?>> deletePost(@RequestBody PostListSelectRequestDto requestBody){
        return ResponseEntity.ok(boardService.getPostList(requestBody));
    }

    @PostMapping(value = "/comment_save")
    public ResponseEntity<ResponseDto<?>> saveComment(@RequestBody CommentSaveRequestDto requestBody){
        return ResponseEntity.ok(boardService.saveComment(requestBody));
    }
    @PostMapping(value = "/comment_change")
    public ResponseEntity<ResponseDto<?>> changeComment(@RequestBody CommentChangeRequestDto requestBody){
        return ResponseEntity.ok(boardService.changeComment(requestBody));
    }
}
