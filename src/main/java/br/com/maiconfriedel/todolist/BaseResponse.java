package br.com.maiconfriedel.todolist;

import lombok.Data;

@Data
public class BaseResponse {
  private String message;

  public BaseResponse(String message) {
    this.message = message;
  }

}
