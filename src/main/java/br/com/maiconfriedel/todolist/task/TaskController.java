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
  public ResponseEntity<BaseResponse<TaskModel>> create(@RequestBody TaskModel taksModel, HttpServletRequest request) {
    taksModel.setUserId((UUID) request.getAttribute("userId"));

    var currentDate = LocalDateTime.now();
    if (currentDate.isAfter(taksModel.getStartAt()) || currentDate.isAfter(taksModel.getEndAt())) {
      return ResponseEntity.status(400)
          .body(new BaseResponse<TaskModel>("Start or end date must be greater than today"));
    }

    if (taksModel.getStartAt().isAfter(taksModel.getEndAt())) {
      return ResponseEntity.status(400).body(new BaseResponse<TaskModel>("End date must be greater than start date"));
    }

    var task = taskRepository.save(taksModel);

    return ResponseEntity.status(201).body(new BaseResponse<TaskModel>((task)));
  }

  @GetMapping("/")
  public List<TaskModel> list(HttpServletRequest request) {
    return this.taskRepository.findByUserId((UUID) request.getAttribute("userId"));
  }

  @PutMapping("/{taskId}")
  public ResponseEntity<BaseResponse<TaskModel>> update(@PathVariable UUID taskId, @RequestBody TaskModel taskModel,
      HttpServletRequest request) {
    var task = this.taskRepository.findById(taskId).orElse(null);

    if (task == null) {
      return ResponseEntity.badRequest()
          .body(new BaseResponse<TaskModel>("Task with id" + taskId.toString() + " not found"));
    }

    if (!task.getUserId().equals(request.getAttribute("userId"))) {
      return ResponseEntity.badRequest()
          .body(new BaseResponse<TaskModel>("You do not have permission to change this task"));
    }

    Utils.copyNonNullProperties(taskModel, task);

    var response = this.taskRepository.save(task);

    return ResponseEntity.ok().body(new BaseResponse<TaskModel>(response));
  }
}
