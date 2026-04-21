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
@RequestMapping("/api/channel")
public class ChannelController {

    @Autowired
    ChannelRepository repository;

    //GET http://localhost:8080/api/channel
    @GetMapping
    public List<Channel> findAll(){
        return repository.findAll();
    }

    //GET http://localhost:8080/api/channel/{id}
    @GetMapping("/{id}")
    public Channel findOne(@PathVariable long id) throws ChannelNotFoundException {
        Optional<Channel> channel = repository.findById(id);
        if(channel.isEmpty()){
            throw new ChannelNotFoundException();
        }
        return channel.get();
    }

    //POST http://localhost:8080/api/channel
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Channel create(@Valid @RequestBody Channel channel){
        Channel _channel = repository.save(new Channel(channel.getName(),channel.getDescription(),channel.getCreatedTime()));
        return _channel;
    }

    //PUT http://localhost:8080/api/channel/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void update(@Valid @RequestBody Channel updatedChannel, @PathVariable long id){
        Optional<Channel> channelData = repository.findById(id);
        Channel _channel = channelData.get();
        _channel.setName(updatedChannel.getName());
        _channel.setDescription(updatedChannel.getDescription());
        _channel.setCreatedTime(updatedChannel.getCreatedTime());
        _channel.setVideos(updatedChannel.getVideos());
        repository.save(_channel);
    }

    //DELETE http://localhost:8080/api/channel/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id){
        if(repository.existsById(id)){
            repository.deleteById(id);
        }
    }
}
