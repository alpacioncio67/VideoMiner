package aiss.videominer.Controllers;

import aiss.videominer.Repositories.ChannelRepository;
import aiss.videominer.exception.ChannelNotFoundException;
import aiss.videominer.model.Caption;
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

@Tag(name= "Channel",description = "Channel management API")
@RestController
@RequestMapping("/videominer/channels")
public class ChannelController {

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    public ChannelController(ChannelRepository channelRepository) {this.channelRepository=channelRepository;}

    //GET http://localhost:8080/api/channel

    @Operation(
            summary = "Retrieve all channels",
            description = "List all channels"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Listado de Canales",
                    content = {@Content(schema = @Schema(implementation = Caption.class),mediaType = "application/json")})
    })

    @GetMapping
    public List<Channel> findAll(@RequestParam(defaultValue = "0")int page,
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

        Page<Channel> pageChannel;

        if (name==null){
            pageChannel = channelRepository.findAll(paging);
        }
        else
            // Este metodo está definido en nuestro repo por nosotros
            pageChannel = channelRepository.findByName(name,paging);

        return pageChannel.getContent();
    }

    //GET http://localhost:8080/api/channel/{id}

    @Operation(
            summary = "Retrieve one channel",
            description = "Obtain a channel based on an Id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Un Canal",
                    content = {@Content(schema = @Schema(implementation = Video.class),mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",description = "Canal no encontrado",
                    content = {@Content(schema = @Schema())})
    })

    @GetMapping("/{id}")
    public Channel findOne(@Parameter(description = "Id del Canal a buscar")@PathVariable String id) throws ChannelNotFoundException {
        Optional<Channel> channel = channelRepository.findById(id);
        if(channel.isEmpty()){
            throw new ChannelNotFoundException();
        }
        return channel.get();
    }

    //POST http://localhost:8080/api/channel

    @Operation(
            summary = "Create a channel",
            description = "POST a channel"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",description = "Creación de un Canal",
                    content = {@Content(schema = @Schema(implementation = Video.class),mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",description = "Canal no creado",
                    content = {@Content(schema = @Schema())})
    })

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Channel create(@Parameter(description = "Cuerpo del Canal a crear") @Valid @RequestBody Channel channel){
        return channelRepository.save(channel);
    }

    //PUT http://localhost:8080/api/channel/{id}

    @Operation(
            summary = "Update a channel",
            description = "Update a channel based on an Id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204",description = "Actualización de un Canal",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400",description = "Canal no actualizado",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404",description = "Canal no encontrado",
                    content = {@Content(schema = @Schema())})
    })

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void update(@Parameter(description = "Cuerpo del Canal a actualizar")@Valid @RequestBody Channel updatedChannel,
                       @Parameter(description = "id del Canal a actualizar")@PathVariable String id)
            throws ChannelNotFoundException{
        Optional<Channel> channelData = channelRepository.findById(id);

        if(channelData.isEmpty()){
            throw new ChannelNotFoundException();
        }

        Channel _channel = channelData.get();
        _channel.setName(updatedChannel.getName());
        _channel.setDescription(updatedChannel.getDescription());
        _channel.setCreatedTime(updatedChannel.getCreatedTime());
        _channel.setVideos(updatedChannel.getVideos());
        channelRepository.save(_channel);
    }

    //DELETE http://localhost:8080/api/channel/{id}

    @Operation(
            summary = "Delete a channel",
            description = "Delete a channel based on an Id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204",description = "Borrado de un Canal",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400",description = "Canal no borrado",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404",description = "Canal no encontrado",
                    content = {@Content(schema = @Schema())})
    })

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@Parameter(description = "id del Canal a borrar")@PathVariable String id){
        if(channelRepository.existsById(id)){
            channelRepository.deleteById(id);
        }
    }
}
