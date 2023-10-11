package br.com.maiconfriedel.todolist;

import lombok.Data;

@Data
public class BaseResponse<T> {
  private boolean sucess;
  private T response;
  private String message;

  /**
   * Construtor em caso de erro
   * 
   * @param message Mensagem de erro
   */
  public BaseResponse(String message) {
    this.message = message;
    this.sucess = false;
  }

  /**
   * Construtor em caso de sucesso
   * 
   * @param response Dados da resposta
   */
  public BaseResponse(T response) {
    this.response = response;
    this.sucess = true;
  }

}
