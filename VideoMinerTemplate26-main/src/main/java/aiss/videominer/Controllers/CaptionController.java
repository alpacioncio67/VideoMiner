package aiss.videominer.Controllers;

import aiss.videominer.Repositories.CaptionRepository;
import aiss.videominer.Repositories.VideoRepository;
import aiss.videominer.exception.CaptionNotFoundException;
import aiss.videominer.exception.VideoNotFoundException;
import aiss.videominer.model.Caption;
import aiss.videominer.model.User;
import aiss.videominer.model.Video;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name= "Caption",description = "Caption management API")
@RestController
@RequestMapping("/videominer/captions")
public class CaptionController {

    @Autowired
    CaptionRepository captionRepository;

    @Autowired
    VideoRepository videoRepository;

    @Operation(
            summary = "Retrieve all captions",
            description = "List all captions"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Listado de captions",
                    content = {@Content(schema = @Schema(implementation = User.class),mediaType = "application/json")})
    })

    //GET http://localhost:8080/api/captions
    @GetMapping
    public List<Caption> findAll(){
        return captionRepository.findAll();
    }

    //GET http://localhost:8080/api/captions/{id}
    @GetMapping("/{id}")
    public Caption findOne(@PathVariable String id) throws CaptionNotFoundException {
        Optional<Caption> caption = captionRepository.findById(id);
        if(caption.isEmpty()){
            throw new CaptionNotFoundException();
        }
        return caption.get();
    }

    //POST http://localhost:8080/api/captions
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/videos/{videoId}/captions")
    public Caption create(@PathVariable String videoId,@Valid @RequestBody Caption caption)
    throws VideoNotFoundException{
        Optional<Video> video = videoRepository.findById(videoId);
        if(video.isEmpty()){
            throw new VideoNotFoundException();
        }

        Video persistedVideo = video.get();
        persistedVideo.getCaptions().add(caption);
        videoRepository.save(persistedVideo);
        return caption;
    }

    //PUT http://localhost:8080/api/captions/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void update(@Valid @RequestBody Caption updatedCaption, @PathVariable String id){
        Optional<Caption> captionData = captionRepository.findById(id);
        Caption _caption = captionData.get();
        _caption.setName(updatedCaption.getName());
        _caption.setLanguage(updatedCaption.getLanguage());
        captionRepository.save(_caption);
    }

    //DELETE http://localhost:8080/api/captions/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id){
        if(captionRepository.existsById(id)){
            captionRepository.deleteById(id);
        }
    }
}
