/*
Autor Mora Guzman Jose Antonio
Desarrollo de Sistemas Distribuidos 
Grupo 4CV13
Tarea 5: Multiplicacion de matrices utilizando objetos distribuidos
*/

//Bibliotecas
import java.rmi.Naming;

public class ServidorRMI {
  public static void main(String[] args) throws Exception {
    String url = "rmi://localhost/multmat";//URL
    ClaseRMI obj = new ClaseRMI();
    Naming.rebind(url,obj);// registra la instancia en el rmiregistry
  }
}