package aiss.videominer.Controllers;

import aiss.videominer.Repositories.ChannelRepository;
import aiss.videominer.Repositories.VideoRepository;
import aiss.videominer.exception.ChannelNotFoundException;
import aiss.videominer.exception.VideoNotFoundException;
import aiss.videominer.model.Channel;
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

@Tag(name = "Video",description = "Video management API")
@RestController
@RequestMapping("/videominer/videos")
public class VideoController {

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    public VideoController(VideoRepository videoRepository){
        this.videoRepository = videoRepository;
    }

    // Operaciones
    //GET http://localhost:8080/api/users
    @Operation(
            summary = "Retrieve all videos",
            description = "List all videos"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Listado de Videos",
                    content = {@Content(schema = @Schema(implementation = Video.class),mediaType = "application/json")})
    })
    @GetMapping()
    public List<Video> findAll(@RequestParam(defaultValue = "0")int page,
                               @RequestParam(defaultValue = "10")int size,
                               @RequestParam(required = false) String name,
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

        Page<Video> pageVideo;

        if (name==null){
            pageVideo = videoRepository.findAll(paging);
        }
        else
            // Este método está definido en nuestro repo por nosotros
            pageVideo = videoRepository.findByName(name,paging);

        return pageVideo.getContent();
    }

    //GET http://localhost:8080/api/videos/{id}
    @Operation(
            summary = "Retrieve one video",
            description = "Obtain a video based on an Id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Un Video",
                    content = {@Content(schema = @Schema(implementation = Video.class),mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",description = "Video no encontrado",
                    content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{id}")
    public Video findOneById(@Parameter(description = "Id del Video a buscar") @PathVariable String id) throws VideoNotFoundException {
        Optional<Video> video = videoRepository.findById(id);

        if (video.isEmpty()){
            throw new VideoNotFoundException();
        }

        return video.get();
    }

    //POST http://localhost:8080/api/users
    @Operation(
            summary = "Create a video",
            description = "POST a video"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",description = "Creación de un Video",
                    content = {@Content(schema = @Schema(implementation = Video.class),mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",description = "Video no creado",
                    content = {@Content(schema = @Schema())})
    })
    @ResponseStatus(HttpStatus.CREATED) // 201
    @PostMapping("/channels/{channelId}/videos")
    public Video create(@Parameter(description = "Id del Canal al que pertenece el Video") @PathVariable String channelId ,
                        @Parameter(description = "Cuerpo del Video a crear") @Valid @RequestBody Video video)
    throws ChannelNotFoundException {
        Optional<Channel> canal = channelRepository.findById(channelId);

        if(canal.isEmpty()){
            throw new ChannelNotFoundException();
        }

        Channel channel = canal.get();
        channel.getVideos().add(video);
        channelRepository.save(channel);
        return video;
    }

    //PUT http://localhost:8080/api/users/{id}
    @Operation(
            summary = "Update a video",
            description = "Update a video based on an Id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204",description = "Actualización de un Video",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400",description = "Video no actualizado",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404",description = "Video no encontrado",
                    content = {@Content(schema = @Schema())})
    })
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    @PutMapping("/{id}")
    public void update(@Parameter(description = "Cuerpo del Video a actualizar") @Valid @RequestBody Video updatedVideo,
                       @Parameter(description = "id del Video a actualizar") @PathVariable String id)
            throws VideoNotFoundException{
        Optional<Video> videoData = videoRepository.findById(id);

        if (videoData.isEmpty()){
            throw new VideoNotFoundException();
        }

        Video _video = videoData.get();
        _video.setName(updatedVideo.getName());
        _video.setDescription(updatedVideo.getDescription());
        _video.setReleaseTime(updatedVideo.getReleaseTime());
        videoRepository.save(_video);
    }

    //DELETE http://localhost:8080/api/users/{id}
    @Operation(
            summary = "Delete a video",
            description = "Delete a video based on an Id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204",description = "Borrado de un Video",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400",description = "Video no borrado",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404",description = "Video no encontrado",
                    content = {@Content(schema = @Schema())})
    })
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    @DeleteMapping("/{id}")
    public void delete(@Parameter(description = "id del Video a borrar") @PathVariable String id) {
        if (videoRepository.existsById(id)){
            videoRepository.deleteById(id);
        }
    }
}
