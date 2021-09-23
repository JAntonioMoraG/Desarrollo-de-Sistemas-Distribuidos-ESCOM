/*Tarea 4 Chat Multicast
Autor Mora Guzman Jose Antonio
GRUPO 4CV13
fecha 23-09-2021
*/
//BIBLIOTECAS A USAR 
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

class Chat {
    //FUNCION PARA ENVIAR MENSAJES MULTICAST (OBTENIDA DEL MOODLE)
    static void envia_mensaje_multicast(byte[] buffer, String ip, int puerto) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), puerto));
        socket.close();
    }

    //FUNCION PARA RECIBIR MENSAJES MULTICAST (OBTENIDA DEL MOODLE)
    static byte[] recibe_mensaje_multicast(MulticastSocket socket, int longitud_mensaje) throws IOException {
        byte[] buffer = new byte[longitud_mensaje];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        return paquete.getData();
    }

    static class Worker extends Thread {
        public void run() {
            // En un ciclo infinito se recibirán los mensajes enviados al
            // grupo 230.0.0.0 a través del puerto 30000 y se desplegarán en la pantalla.
            for(;;) {
                try{
                    
                    InetAddress grupo230 = InetAddress.getByName("230.0.0.0");// grupo
                    MulticastSocket socket = new MulticastSocket(30000);//puerto
                    socket.joinGroup(grupo230);
                    byte[] buffer = recibe_mensaje_multicast(socket, 35);//recibe mensaje tamaño 35 bytes
                    System.out.println(new String(buffer,"windows-1252"));//Muestra el mensaje con codificion Windows-1252
                                                                          //para ver los acentos
                    System.out.println("Ingrese el mensaje a enviar:");//muestra prompt despues de recibir mensaje
                    //Cerramos conexion
                    socket.leaveGroup(grupo230);
                    socket.close();
                } catch (IOException e){
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new Worker().start(); 
        String nombre = args[0];//el parametro es el nombre usuario
        System.out.println("Ingrese el mensaje a enviar:");//prompt lanza al inicio
        Scanner entrada = new Scanner(System. in,"windows-1252");//recibe entrada por teclada 
        // En un ciclo infinito se leerá cada mensaje del teclado y se enviará al
        // grupo 230.0.0.0 a través del puerto 30000.
        for(;;){
            //lee y envia el mensaje con formato nombre:mensaje
            //al grupo 230.0.0.0 y puerto 30000
            String mensaje = entrada.nextLine();
            byte buffer[] = String.format("%s:%s", nombre, mensaje).getBytes();
            envia_mensaje_multicast(buffer, "230.0.0.0",30000);
        
        }
    }

}