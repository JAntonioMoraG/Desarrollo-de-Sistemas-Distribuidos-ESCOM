/*
Autor Mora Guzman Jose Antonio
Desarrollo de Sistemas Distribuidos 
Grupo 4CV13
Tarea 5: Multiplicacion de matrices utilizando objetos distribuidos
*/

//Bibliotecas
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceRMI extends Remote {
  public double[][] multiplica_matrices(double[][] A, double[][] B) throws RemoteException;
}