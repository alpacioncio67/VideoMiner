package aiss.videominer.Controllers;

import aiss.videominer.Repositories.ChannelRepository;
import aiss.videominer.exception.ChannelNotFoundException;
import aiss.videominer.model.Channel;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videominer/channels")
public class ChannelController {

    @Autowired
    ChannelRepository channelRepository;

    //GET http://localhost:8080/api/channel
    @GetMapping
    public List<Channel> findAll(){
        return channelRepository.findAll();
    }

    //GET http://localhost:8080/api/channel/{id}
    @GetMapping("/{id}")
    public Channel findOne(@PathVariable String id) throws ChannelNotFoundException {
        Optional<Channel> channel = channelRepository.findById(id);
        if(channel.isEmpty()){
            throw new ChannelNotFoundException();
        }
        return channel.get();
    }

    //POST http://localhost:8080/api/channel
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Channel create(@Valid @RequestBody Channel channel){
        return channelRepository.save(channel);
    }

    //PUT http://localhost:8080/api/channel/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void update(@Valid @RequestBody Channel updatedChannel, @PathVariable String id){
        Optional<Channel> channelData = channelRepository.findById(id);
        Channel _channel = channelData.get();
        _channel.setName(updatedChannel.getName());
        _channel.setDescription(updatedChannel.getDescription());
        _channel.setCreatedTime(updatedChannel.getCreatedTime());
        _channel.setVideos(updatedChannel.getVideos());
        channelRepository.save(_channel);
    }

    //DELETE http://localhost:8080/api/channel/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id){
        if(channelRepository.existsById(id)){
            channelRepository.deleteById(id);
        }
    }
}
