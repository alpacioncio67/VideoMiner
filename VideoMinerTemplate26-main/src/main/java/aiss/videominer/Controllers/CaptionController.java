package aiss.videominer.Controllers;

import aiss.videominer.Repositories.CaptionRepository;
import aiss.videominer.Repositories.UserRepository;
import aiss.videominer.exception.CaptionNotFoundException;
import aiss.videominer.model.Caption;
import aiss.videominer.model.User;
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
    CaptionRepository repository;

    @Autowired
    public CaptionController(CaptionRepository captionRepository){
        this.repository = captionRepository;
    }

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
        return repository.findAll();
    }

    //GET http://localhost:8080/api/captions/{id}
    @GetMapping("/{id}")
    public Caption findOne(@PathVariable long id) throws CaptionNotFoundException {
        Optional<Caption> caption = repository.findById(id);
        if(caption.isEmpty()){
            throw new CaptionNotFoundException();
        }
        return caption.get();
    }

    //POST http://localhost:8080/api/captions
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Caption create(@Valid @RequestBody Caption caption){
        Caption _caption = repository.save(new Caption(caption.getName(),caption.getLanguage()));
        return _caption;
    }

    //PUT http://localhost:8080/api/captions/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void update(@Valid @RequestBody Caption updatedCaption, @PathVariable long id){
        Optional<Caption> captionData = repository.findById(id);
        Caption _caption = captionData.get();
        _caption.setName(updatedCaption.getName());
        _caption.setLanguage(updatedCaption.getLanguage());
        repository.save(_caption);
    }

    //DELETE http://localhost:8080/api/captions/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id){
        if(repository.existsById(id)){
            repository.deleteById(id);
        }
    }
}
