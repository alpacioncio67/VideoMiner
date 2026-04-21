package aiss.videominer.Controllers;

import aiss.videominer.Repositories.VideoRepository;
import aiss.videominer.exception.VideoNotFoundException;
import aiss.videominer.model.Video;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    public VideoController(VideoRepository videoRepository){
        this.videoRepository = videoRepository;
    }

    // Operaciones
    //GET http://localhost:8080/api/users
    @GetMapping
    public List<Video> findAll(){
        return videoRepository.findAll();
    }

    //GET http://localhost:8080/api/videos/{id}
    @GetMapping("/{id}")
    public Video findOneById(@PathVariable long id) throws VideoNotFoundException {
        Optional<Video> video = videoRepository.findById(id);

        if (video.isEmpty()){
            throw new VideoNotFoundException();
        }

        return video.get();
    }

    //POST http://localhost:8080/api/users
    @ResponseStatus(HttpStatus.CREATED) // 201
    @PostMapping
    public Video create(@Valid @RequestBody Video video){
        Video _video = videoRepository.save(new Video(video.getName(),
                video.getDescription(),
                video.getReleaseTime()));

        return _video;
    }

    //PUT http://localhost:8080/api/users/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    @PutMapping("/{id}")
    public void update(@Valid @RequestBody Video updatedVideo,@PathVariable long id) throws VideoNotFoundException{
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
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        if (videoRepository.existsById(id)){
            videoRepository.deleteById(id);
        }
    }
}
