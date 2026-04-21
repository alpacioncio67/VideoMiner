package aiss.videominer.Controllers;

import aiss.videominer.Repositories.UserRepository;
import aiss.videominer.exception.UserNotFoundException;
import aiss.videominer.model.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    // Operaciones
    //GET http://localhost:8080/api/users
    @GetMapping
    public List<User> findAll(){
        return userRepository.findAll();
    }

    //GET http://localhost:8080/api/users/{id}
    @GetMapping("/{id}")
    public User findOneById(@PathVariable long id) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(id);

        if(user.isEmpty()){
            throw  new UserNotFoundException();
        }

        return user.get();
    }

    //POST http://localhost:8080/api/users
    @ResponseStatus(HttpStatus.CREATED) // 201
    @PostMapping
    public User create(@Valid @RequestBody User user){
        User _user = userRepository.save(new User(user.getName(),user.getUser_link(),
                user.getPicture_link()));
        return user;
    }

    //PUT http://localhost:8080/api/users/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    @PutMapping("/{id}")
    public void update(@Valid @RequestBody User updatedUser,@PathVariable long id) throws
            UserNotFoundException{
        Optional<User> userData = userRepository.findById(id);

        if (userData.isEmpty()){
            throw new UserNotFoundException();
        }

        User _user = userData.get();
        // Actualizamos las propiedades, exceptuando la id
        _user.setName(updatedUser.getName());
        _user.setUser_link(updatedUser.getUser_link());
        _user.setPicture_link(updatedUser.getPicture_link());
        userRepository.save(_user);
    }

    //DELETE http://localhost:8080/api/users/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id){
        if (userRepository.existsById(id)){
            userRepository.deleteById(id);
        }
    }
}
