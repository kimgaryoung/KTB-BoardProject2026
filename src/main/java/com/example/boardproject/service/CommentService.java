package com.example.boardproject.service;

import com.example.boardproject.auth.PrincipalDetails;
import com.example.boardproject.dto.CommentRequestDto;
import com.example.boardproject.entity.Comment;
import com.example.boardproject.entity.Post;
import com.example.boardproject.entity.User;
import com.example.boardproject.entity.UserProfile;
import com.example.boardproject.repository.CommentRepository;
import com.example.boardproject.repository.PostRepository;
import com.example.boardproject.repository.UserProfileRepository;
import com.example.boardproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.boardproject.entity.QComment.comment;

@Service
@RequiredArgsConstructor
public class CommentService {

    //private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserProfileRepository userProfileRepository;
    private final CommentRepository commentRepository;

    //댓글 추가
    @Transactional
    public String createComment(final CommentRequestDto dto, final Long postId, final PrincipalDetails user) {

        //User findUser = findUserId(Long.parseLong(user.getUserId()));
        Post findPost = findPostId(postId);
        UserProfile userProfile = userProfileRepository.findByUserId(user.getId());

        Comment comment = new Comment(
                dto.getCommentContent(),
                userProfile,
                findPost
        );

        comment.updateCommentDate(LocalDateTime.now());// 시간추가

        Comment saved =commentRepository.save(comment);

        return "댓글 등록 완료 (id: " + saved.getCommentId() + ")";
    }




    //게시글 id찾
    private Post findPostId(final Long postId) {
        return postRepository.findByPostId(postId);
    }
}
