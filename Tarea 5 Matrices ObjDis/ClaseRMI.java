/*
Autor Mora Guzman Jose Antonio
Desarrollo de Sistemas Distribuidos 
Grupo 4CV13
Tarea 5: Multiplicacion de matrices utilizando objetos distribuidos
*/

//Bibliotecas
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class ClaseRMI extends UnicastRemoteObject implements InterfaceRMI {
  public static final int N = 9;
  public ClaseRMI() throws RemoteException {
    super();// se necesita que el contructor invoque la super-clase
  }

  public double[][] multiplica_matrices(double[][] A, double[][] B) throws RemoteException {
    double[][] C = new double[N / 3][N / 3];
    for (int i = 0; i < N / 3; i++)
        for (int j = 0; j < N / 3; j++)
            for (int k = 0; k < N; k++)
                C[i][j] += A[i][k] * B[j][k];
    return C;
  }
}