package br.com.maiconfriedel.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.maiconfriedel.todolist.BaseResponse;

@RestController
@RequestMapping("/users")
public class UserController {
  @Autowired
  private IUserRepository userRepository;

  @PostMapping("/")
  public ResponseEntity<BaseResponse<UserModel>> create(@RequestBody UserModel usermodel) {
    var user = this.userRepository.findByUsername(usermodel.getUsername());

    if (user != null) {

      return ResponseEntity.status(400).body(new BaseResponse<UserModel>("User already exists"));
    } else {
      var hashed = BCrypt.withDefaults().hashToString(12, usermodel.getPassword().toCharArray());
      usermodel.setPassword(hashed);

      var userCreated = this.userRepository.save(usermodel);

      return ResponseEntity.status(201).body(new BaseResponse<UserModel>(userCreated));
    }
  }
}
