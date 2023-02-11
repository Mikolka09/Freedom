package itstep.social_freedom.service;

import itstep.social_freedom.entity.Category;
import itstep.social_freedom.entity.Comment;
import itstep.social_freedom.entity.Status;
import itstep.social_freedom.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> allComments() {
        return commentRepository.findAll();
    }

    public Comment findCommentById(Long id) {
        return commentRepository.findById(id).orElse(new Comment());
    }

    public boolean save(Comment comment) {
        if (comment != null) {
            commentRepository.save(comment);
            return true;
        }
        return false;
    }

    public void delete(Long id) {
        if (commentRepository.findById(id).isPresent()) {
            Comment comment = commentRepository.findById(id).orElse(new Comment());
            comment.setStatus(Status.DELETED);
            commentRepository.save(comment);
        }
    }
}
