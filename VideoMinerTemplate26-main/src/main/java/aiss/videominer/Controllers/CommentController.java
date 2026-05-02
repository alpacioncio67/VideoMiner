package aiss.videominer.Controllers;

import aiss.videominer.Repositories.ChannelRepository;
import aiss.videominer.Repositories.CommentRepository;
import aiss.videominer.Repositories.VideoRepository;
import aiss.videominer.exception.CommentNotFoundException;
import aiss.videominer.exception.VideoNotFoundException;
import aiss.videominer.model.Caption;
import aiss.videominer.model.Channel;
import aiss.videominer.model.Comment;
import aiss.videominer.model.Video;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name= "Comment",description = "Comment management API")
@RestController
@RequestMapping("/videominer/comments")
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    public CommentController(CommentRepository commentRepository) {this.commentRepository=commentRepository;}

    //GET http://localhost:8080/api/comment

    @Operation(
            summary = "Retrieve all comments",
            description = "List all comments"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Listado de Comentarios",
                    content = {@Content(schema = @Schema(implementation = Caption.class),mediaType = "application/json")})
    })

    @GetMapping
    public List<Comment> findAll(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10")int size,
            @RequestParam(required = false) String order){

        Pageable paging;

        if (order!=null){
            if(order.startsWith("-"))
                paging = PageRequest.of(page,size, Sort.by(order.substring(1)).descending());
            else
                paging = PageRequest.of(page,size,Sort.by(order).ascending());
        }
        else
            paging = PageRequest.of(page,size);

        Page<Comment> pageChannel;


            pageChannel = commentRepository.findAll(paging);

        return pageChannel.getContent();
    }

    //GET http://localhost:8080/api/comment/{id}

    @Operation(
            summary = "Retrieve one comment",
            description = "Obtain a comment based on an Id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Un Comentario",
                    content = {@Content(schema = @Schema(implementation = Video.class),mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",description = "Comentario no encontrado",
                    content = {@Content(schema = @Schema())})
    })

    @GetMapping("/{id}")
    public Comment findOne(@Parameter(description = "Id del Comentario a buscar")@PathVariable String id) throws CommentNotFoundException {
        Optional<Comment> comment = commentRepository.findById(id);
        if(comment.isEmpty()){
            throw new CommentNotFoundException();
        }
        return comment.get();
    }

    //POST http://localhost:8080/api/comment

    @Operation(
            summary = "Create a comment",
            description = "POST a comment"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",description = "Creación de un Comentario",
                    content = {@Content(schema = @Schema(implementation = Video.class),mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",description = "Comentario no creado",
                    content = {@Content(schema = @Schema())})
    })

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/videos/{videoId}/comments")
    public Comment create(@Parameter(description = "Id del Video al que pertenece el Comentario")@PathVariable String videoId,
                          @Parameter(description = "Cuerpo del Comentario a crear") @Valid @RequestBody Comment comment)
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

    @Operation(
            summary = "Update a comment",
            description = "Update a comment based on an Id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204",description = "Actualización de un Comentario",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400",description = "Comentario no actualizado",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404",description = "Comentario no encontrado",
                    content = {@Content(schema = @Schema())})
    })

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void update(@Parameter(description = "Cuerpo del Comentario a actualizar")@Valid @RequestBody Comment updatedComment,
                       @Parameter(description = "id del Canal a actualizar")@PathVariable String id)
            throws CommentNotFoundException{
        Optional<Comment> commentData = commentRepository.findById(id);

        if(commentData.isEmpty()){
            throw new CommentNotFoundException();
        }

        Comment _comment = commentData.get();
        _comment.setText(updatedComment.getText());
        _comment.setCreatedOn(updatedComment.getCreatedOn());
        commentRepository.save(_comment);
    }

    //DELETE http://localhost:8080/api/comment/{id}

    @Operation(
            summary = "Delete a comment",
            description = "Delete a comment based on an Id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204",description = "Borrado de un Commentario",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400",description = "Commentario no borrado",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404",description = "Commentario no encontrado",
                    content = {@Content(schema = @Schema())})
    })

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@Parameter(description = "id del Commentario a borrar")@PathVariable String id){
        if(commentRepository.existsById(id)){
            commentRepository.deleteById(id);
        }
    }
}
