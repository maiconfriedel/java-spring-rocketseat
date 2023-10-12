package br.com.maiconfriedel.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.maiconfriedel.todolist.BaseResponse;
import br.com.maiconfriedel.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping("/")
  public ResponseEntity<BaseResponse<TaksModel>> create(@RequestBody TaksModel taksModel, HttpServletRequest request) {
    taksModel.setUserId((UUID) request.getAttribute("userId"));
    var task = taskRepository.save(taksModel);

    var currentDate = LocalDateTime.now();
    if (currentDate.isAfter(taksModel.getStartAt()) || currentDate.isAfter(taksModel.getEndAt())) {
      return ResponseEntity.status(400)
          .body(new BaseResponse<TaksModel>("Start or end date must be greater than today"));
    }

    if (taksModel.getStartAt().isAfter(taksModel.getEndAt())) {
      return ResponseEntity.status(400).body(new BaseResponse<TaksModel>("End date must be greater than start date"));
    }

    return ResponseEntity.status(201).body(new BaseResponse<TaksModel>((task)));
  }

  @GetMapping("/")
  public List<TaksModel> list(HttpServletRequest request) {
    return this.taskRepository.findByUserId((UUID) request.getAttribute("userId"));
  }

  @PutMapping("/{taskId}")
  public ResponseEntity<BaseResponse<TaksModel>> update(@PathVariable UUID taskId, @RequestBody TaksModel taskModel,
      HttpServletRequest request) {
    var task = this.taskRepository.findById(taskId).orElse(null);

    if (task == null) {
      return ResponseEntity.badRequest()
          .body(new BaseResponse<TaksModel>("Task with id" + taskId.toString() + " not found"));
    }

    Utils.copyNonNullProperties(taskModel, task);

    var response = this.taskRepository.save(task);

    return ResponseEntity.ok().body(new BaseResponse<TaksModel>(response));
  }
}
