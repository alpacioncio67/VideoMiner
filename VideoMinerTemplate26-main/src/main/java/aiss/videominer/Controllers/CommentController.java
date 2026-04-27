package aiss.videominer.Controllers;

import aiss.videominer.Repositories.CommentRepository;
import aiss.videominer.Repositories.VideoRepository;
import aiss.videominer.exception.CommentNotFoundException;
import aiss.videominer.exception.VideoNotFoundException;
import aiss.videominer.model.Comment;
import aiss.videominer.model.Video;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videominer/comments")
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    VideoRepository videoRepository;

    //GET http://localhost:8080/api/comment
    @GetMapping
    public List<Comment> findAll(){
        return commentRepository.findAll();
    }

    //GET http://localhost:8080/api/comment/{id}
    @GetMapping("/{id}")
    public Comment findOne(@PathVariable String id) throws CommentNotFoundException {
        Optional<Comment> comment = commentRepository.findById(id);
        if(comment.isEmpty()){
            throw new CommentNotFoundException();
        }
        return comment.get();
    }

    //POST http://localhost:8080/api/comment
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/videos/{videoId}/comments")
    public Comment create(@PathVariable String videoId,@Valid @RequestBody Comment comment)
    throws VideoNotFoundException {
        Optional<Video> video = videoRepository.findById(videoId);
        if(video.isEmpty()){
            throw new VideoNotFoundException();
        }

        Video persistedVideo = video.get();
        persistedVideo.getComments().add(comment);
        videoRepository.save(persistedVideo);
        return comment;

    }

    //PUT http://localhost:8080/api/comment/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void update(@Valid @RequestBody Comment updatedComment, @PathVariable String id){
        Optional<Comment> commentData = commentRepository.findById(id);
        Comment _comment = commentData.get();
        _comment.setText(updatedComment.getText());
        _comment.setCreatedOn(updatedComment.getCreatedOn());
        commentRepository.save(_comment);
    }

    //DELETE http://localhost:8080/api/comment/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id){
        if(commentRepository.existsById(id)){
            commentRepository.deleteById(id);
        }
    }
}
