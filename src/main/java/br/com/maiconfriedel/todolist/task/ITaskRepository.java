package br.com.maiconfriedel.todolist.task;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ITaskRepository extends JpaRepository<TaksModel, UUID> {
  List<TaksModel> findByUserId(UUID userId);
}
