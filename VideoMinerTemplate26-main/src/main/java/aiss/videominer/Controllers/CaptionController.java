package aiss.videominer.Controllers;

import aiss.videominer.Repositories.CaptionRepository;
import aiss.videominer.exception.CaptionNotFoundException;
import aiss.videominer.model.Caption;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videominer/captions")
public class CaptionController {

    @Autowired
    CaptionRepository repository;

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
