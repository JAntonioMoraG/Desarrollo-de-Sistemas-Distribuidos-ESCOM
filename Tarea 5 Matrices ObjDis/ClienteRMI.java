/*
Autor Mora Guzman Jose Antonio
Desarrollo de Sistemas Distribuidos 
Grupo 4CV13
Tarea 5: Multiplicacion de matrices utilizando objetos distribuidos
*/

//Bibliotecas
import java.rmi.Naming;

public class ClienteRMI {

  private static final int N = 9;

  static double[][] separa_matriz(double[][] A, int inicio) {
    double[][] M = new double[N / 3][N];
    for (int i = 0; i < N / 3; i++)
      for (int j = 0; j < N; j++)
        M[i][j] = A[i + inicio][j];
    return M;
  }

  static void acomoda_matriz(double[][] C, double[][] A, int renglon, int columna) {
    for (int i = 0; i < N / 3; i++)
      for (int j = 0; j < N / 3; j++)
        C[i + renglon][j + columna] = A[i][j];
  }

  static void imprime_matriz(double matriz[][], int renglones, int columnas) {
    for (int i = 0; i < renglones; i++) {
      for (int j = 0; j < columnas; j++) {
        System.out.printf("%8.1f", matriz[i][j]);
      }
      System.out.println("");
    }
  }

  public static void main(String args[]) throws Exception {
    double[][] A = new double[N][N];
    double[][] B = new double[N][N];
    double[][] C = new double[N][N];
    double checksum = 0;

    // Inicializar las matrices A, B y C
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        A[i][j] = 4*i+j;
        B[i][j] = i-3*j;
        C[i][j] = 0;
      }
    }


    // transpone la matriz B, la matriz traspuesta queda en B
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < i; j++) {
        double x = B[i][j];
        B[i][j] = B[j][i];
        B[j][i] = x;
      }
    }


    // Separar las matrices
    double[][] A1 = separa_matriz(A, 0);
    double[][] A2 = separa_matriz(A, N/3);
    double[][] A3 = separa_matriz(A,2*N/3);
    double[][] B1 = separa_matriz(B, 0);
    double[][] B2 = separa_matriz(B, N/3);
    double[][] B3 = separa_matriz(B,2*N/3);


    InterfaceRMI nodo1 = (InterfaceRMI) Naming.lookup("rmi://localhost/multmat");
    InterfaceRMI nodo2 = (InterfaceRMI) Naming.lookup("rmi://localhost/multmat");
    InterfaceRMI nodo3 = (InterfaceRMI) Naming.lookup("rmi://localhost/multmat");


    // Multiplica las matrices
    double[][] C1 = nodo1.multiplica_matrices(A1, B1);
    double[][] C2 = nodo1.multiplica_matrices(A1, B2);
    double[][] C3 = nodo1.multiplica_matrices(A1, B3);
    double[][] C4 = nodo2.multiplica_matrices(A2, B1);
    double[][] C5 = nodo2.multiplica_matrices(A2, B2);
    double[][] C6 = nodo2.multiplica_matrices(A2, B3);
    double[][] C7 = nodo3.multiplica_matrices(A3, B1);
    double[][] C8 = nodo3.multiplica_matrices(A3, B2);
    double[][] C9 = nodo3.multiplica_matrices(A3, B3);

    // Une la matriz resultante
    acomoda_matriz(C, C1, 0, 0);
    acomoda_matriz(C, C2, 0, N/3);
    acomoda_matriz(C, C3, 0, 2*N/3);
    acomoda_matriz(C, C4, N/3, 0);
    acomoda_matriz(C, C5, N/3 , N/3);
    acomoda_matriz(C, C6, N/3, 2*N/3);
    acomoda_matriz(C, C7, 2*N/3, 0);
    acomoda_matriz(C, C8, 2*N/3, N/3);
    acomoda_matriz(C, C9, 2*N/3, 2*N/3);


    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        checksum += C[i][j];
      }
    }

    if(N==9){
      System.out.println("Matriz A:");
      imprime_matriz(A, N, N);
      System.out.println("Matriz B traspuesta: ");
      imprime_matriz(B, N, N);
      System.out.println("Matriz C: ");
      imprime_matriz(C, N, N);
      System.out.println("Checksum: "+checksum);

    }
    if(N==3000){
      System.out.println("Checksum: " + checksum);

    }
    
  }
}