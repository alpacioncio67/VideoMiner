package aiss.videominer.Controllers;

import aiss.videominer.Repositories.UserRepository;
import aiss.videominer.exception.UserNotFoundException;
import aiss.videominer.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Tag(name= "User",description = "User management API")
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
    @Operation(
            summary = "Retrieve all users",
            description = "List all users",
            tags = {"users","get"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Listado de usuarios",
            content = {@Content(schema = @Schema(implementation = User.class),mediaType = "application/json")})
    })
    @GetMapping
    public List<User> findAll(){
        return userRepository.findAll();
    }

    //GET http://localhost:8080/api/users/{id}
    @Operation(
            summary = "Retrieve one user",
            description = "Find one user based on an Id",
            tags = {"users","get"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Listado de un usuario",
                    content = {@Content(schema = @Schema(implementation = User.class),mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",description = "Usuario no encontrado",
                    content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{id}")
        public User findOneById(@Parameter(description = "id del usuario a buscar") @PathVariable long id) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(id);

        if(user.isEmpty()){
            throw  new UserNotFoundException();
        }

        return user.get();
    }

    //POST http://localhost:8080/api/users
    @Operation(
            summary = "Create a user",
            description = "Post a user",
            tags = {"users","post"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",description = "Creación de un usuario",
                    content = {@Content(schema = @Schema(implementation = User.class),mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",description = "Usuario no creado",
                    content = {@Content(schema = @Schema())})
    })
    @ResponseStatus(HttpStatus.CREATED) // 201
    @PostMapping
    public User create(@Parameter(description = "Cuerpo del usuario a crear") @Valid @RequestBody User user){
        User _user = userRepository.save(new User(user.getName(),user.getUser_link(),
                user.getPicture_link()));
        return _user;
    }

    //PUT http://localhost:8080/api/users/{id}
    @Operation(
            summary = "Update a user",
            description = "Update a used based on an Id",
            tags = {"users","put"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204",description = "Actualización de un usuario",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400",description = "Usuario no actualizado",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404",description = "Usuario no encontrado",
                    content = {@Content(schema = @Schema())})
    })
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    @PutMapping("/{id}")
    public void update(@Parameter(description = "Cuerpo del usuario a actualizar") @Valid @RequestBody User updatedUser,
                       @Parameter(description = "id del usuario a actualizar") @PathVariable long id) throws
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
    @Operation(
            summary = "Delete a user",
            description = "Delete a user based on an Id",
            tags = {"users","delete"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204",description = "Borrado de un usuario",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400",description = "Usuario no borrado",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404",description = "Usuario no encontrado",
                    content = {@Content(schema = @Schema())})
    })
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    @DeleteMapping("/{id}")
    public void delete(@Parameter(description = "id del usuario a borrar") @PathVariable long id){
        if (userRepository.existsById(id)){
            userRepository.deleteById(id);
        }
    }
}
