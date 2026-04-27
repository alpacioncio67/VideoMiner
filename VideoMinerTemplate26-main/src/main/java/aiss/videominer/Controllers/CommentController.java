package aiss.videominer.Controllers;

import aiss.videominer.Repositories.CommentRepository;
import aiss.videominer.exception.CommentNotFoundException;
import aiss.videominer.model.Comment;
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
    CommentRepository repository;

    //GET http://localhost:8080/api/comment
    @GetMapping
    public List<Comment> findAll(){
        return repository.findAll();
    }

    //GET http://localhost:8080/api/comment/{id}
    @GetMapping("/{id}")
    public Comment findOne(@PathVariable long id) throws CommentNotFoundException {
        Optional<Comment> comment = repository.findById(id);
        if(comment.isEmpty()){
            throw new CommentNotFoundException();
        }
        return comment.get();
    }

    //POST http://localhost:8080/api/comment
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Comment create(@Valid @RequestBody Comment comment){
        Comment _comment = repository.save(new Comment(comment.getId(), comment.getText(), comment.getCreatedOn()));
        return _comment;
    }

    //PUT http://localhost:8080/api/comment/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void update(@Valid @RequestBody Comment updatedComment, @PathVariable long id){
        Optional<Comment> commentData = repository.findById(id);
        Comment _comment = commentData.get();
        _comment.setText(updatedComment.getText());
        _comment.setCreatedOn(updatedComment.getCreatedOn());
        repository.save(_comment);
    }

    //DELETE http://localhost:8080/api/comment/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id){
        if(repository.existsById(id)){
            repository.deleteById(id);
        }
    }
}
