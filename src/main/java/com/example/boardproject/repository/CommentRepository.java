package com.example.boardproject.repository;

import com.example.boardproject.dto.PostGetDetailResponseDto;
import com.example.boardproject.entity.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    //postId로 comment 테이블을 직접 조회해 해당 게시글의 댓글 목록을 반환함.게시글 상세조회에서에 댓글 전체가 보이기 위해서 추가.
    //N+!문제 주의하기.
    @Query("select new com.example.boardproject.dto.PostGetDetailResponseDto$CommentInfo(c.commentId, c.commentContent, c.commentDate, c.userProfile.nickname, c.userProfile.profileImage) from Comment c where c.post.postId = :postId")
    List<PostGetDetailResponseDto.CommentInfo> findCommentInfoByPostId(Long postId);
}
