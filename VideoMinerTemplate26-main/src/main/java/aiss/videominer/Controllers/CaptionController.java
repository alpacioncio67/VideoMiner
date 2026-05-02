package aiss.videominer.Controllers;

import aiss.videominer.Repositories.CaptionRepository;
import aiss.videominer.Repositories.VideoRepository;
import aiss.videominer.exception.CaptionNotFoundException;
import aiss.videominer.exception.VideoNotFoundException;
import aiss.videominer.model.Caption;
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

@Tag(name= "Caption",description = "Caption management API")
@RestController
@RequestMapping("/videominer/captions")
public class CaptionController {

    @Autowired
    CaptionRepository captionRepository;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    public CaptionController(CaptionRepository captionRepository){this.captionRepository=captionRepository;}

    //GET http://localhost:8080/api/captions

    @Operation(
            summary = "Retrieve all captions",
            description = "List all captions"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Listado de Subtítulos",
                    content = {@Content(schema = @Schema(implementation = Caption.class),mediaType = "application/json")})
    })

    @GetMapping
    public List<Caption> findAll(@RequestParam(defaultValue = "0")int page,
                                 @RequestParam(defaultValue = "10")int size,
                                 @RequestParam(required = false) String name,
                                 @RequestParam(required = false) String order){
        Pageable paging;

        if (order!=null){
            if(order.startsWith("-"))
                paging = PageRequest.of(page,size,Sort.by(order.substring(1)).descending());
            else
                paging = PageRequest.of(page,size,Sort.by(order).ascending());
        }
        else
            paging = PageRequest.of(page,size);

        Page<Caption> pageCaption;

        if (name==null){
            pageCaption = captionRepository.findAll(paging);
        }
        else
            // Este método está definido en nuestro repo por nosotros
            pageCaption = captionRepository.findByName(name,paging);

        return pageCaption.getContent();
    }

    //GET http://localhost:8080/api/captions/{id}

    @Operation(
            summary = "Retrieve one caption",
            description = "Obtain a caption based on an Id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Unos Subtítulos",
                    content = {@Content(schema = @Schema(implementation = Caption.class),mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",description = "Subtítulos no encontrados",
                    content = {@Content(schema = @Schema())})
    })

    @GetMapping("/{id}")
    public Caption findOne(@Parameter(description = "id de los Subtítulos a buscar") @PathVariable String id) throws CaptionNotFoundException {
        Optional<Caption> caption = captionRepository.findById(id);
        if(caption.isEmpty()){
            throw new CaptionNotFoundException();
        }
        return caption.get();
    }

    //POST http://localhost:8080/api/captions

    @Operation(
            summary = "Create a caption",
            description = "POST a caption"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",description = "Creación de unos Subtítulos",
                    content = {@Content(schema = @Schema(implementation = Caption.class),mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",description = "Subtítulos no creados",
                    content = {@Content(schema = @Schema())})
    })

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/videos/{videoId}/captions")
    public Caption create(@Parameter(description = "Id del Video al que pertenecen los Subtítulos") @PathVariable String videoId,
                          @Parameter(description = "Cuerpo de los Subtitulos a crear") @Valid @RequestBody Caption caption)
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

    @Operation(
            summary = "Update a caption",
            description = "Update a caption based on an Id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204",description = "Actualización de unos Subtítulos",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400",description = "Subtítulos no actualizados",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404",description = "Subtítulos no encontrados",
                    content = {@Content(schema = @Schema())})
    })

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void update(@Parameter(description = "Cuerpo de los subtítulos a actualizar") @Valid @RequestBody Caption updatedCaption,
                       @Parameter(description = "id de los Subtítulos a actualizar") @PathVariable String id)
            throws CaptionNotFoundException{
        Optional<Caption> captionData = captionRepository.findById(id);

        if(captionData.isEmpty()){
            throw new CaptionNotFoundException();
        }

        Caption _caption = captionData.get();
        _caption.setName(updatedCaption.getName());
        _caption.setLanguage(updatedCaption.getLanguage());
        captionRepository.save(_caption);
    }

    //DELETE http://localhost:8080/api/captions/{id}

    @Operation(
            summary = "Delete a caption",
            description = "Delete a caption based on an Id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204",description = "Borrado de unos Subtítulos",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400",description = "Subtítulos no borrados",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404",description = "Subtítulos no encontrados",
                    content = {@Content(schema = @Schema())})
    })

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@Parameter(description = "id de los Subtítulos a borrar") @PathVariable String id){
        if(captionRepository.existsById(id)){
            captionRepository.deleteById(id);
        }
    }
}
