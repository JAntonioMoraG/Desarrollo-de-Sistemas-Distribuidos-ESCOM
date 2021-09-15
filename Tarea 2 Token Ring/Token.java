//Tarea 2 Sistemas Distribuidos 
//Autor: Mora Guzman Jose Antonio 4CV13
//LIBRERIAS A USAR
import java.net.Socket;
import java.net.ServerSocket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
class Token {
	static DataInputStream entrada;
	static DataOutputStream salida;
	static boolean inicio = true;
	static String ip;
	static int nodo;
	static long token;

	static class Worker extends Thread { //CLASE MANEJA HILOS
		public void run() { //AL INICIAR HILO ENTRA AL RUN
			//ALGORITMO 1 (TIENE LAS INSTRUCCIONES DEL MOODLE) 
			try { //1 en un vloque try 
				System.setProperty("javax.net.ssl.keyStore", "keystore_servidor.jks");
                System.setProperty("javax.net.ssl.keyStorePassword", "1234567");         //propiedades sockets seguros Servidor
				ServerSocket servidor; //1.1 Declarar la variable servidor de tipo ServerSocket
				//servidor= new ServerSocket(20000+nodo); //1.2 Asignar a la variable servidor el objeto: new ServerSocket(20000+nodo)
				//PUNTO 1.2 PERO CON SOCKETS SEGUROS
				SSLServerSocketFactory socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
				servidor = socket_factory.createServerSocket(20000+nodo);
				Socket conexion;//1.3 Declarar la variable conexion de tipo socket
				conexion = servidor.accept();// 1.4 Asignar a la variable conexion el objeto servidor.accept().
				entrada = new DataInputStream(conexion.getInputStream());//1.5 Asignar a la variable entrada el objeto 														 //new DataInputStream(conexion.getInputStream())
			   

			} catch (Exception e) {
				e.printStackTrace();//2 en el catch mostrar la excepcion
			}
		}
	}

	public static void main(String[] args) throws Exception {

		System.setProperty("javax.net.ssl.trustStore", "keystore_cliente.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456"); //PRopiedades sockets seguros CLIENTE

		if (args.length != 2) {
			System.err.println("Se debe pasar como parametros el numero del nodo y la IP del siguiente nodo en el anillo");
			System.exit(1);
		}
		nodo = Integer.valueOf(args[0]);
		ip = args[1];
		//ALGORITMO 2 (TIENE INSTRUCCIONES DEL MOODLE)
		Worker w;//1 Declarar la variable w de tipo Worker.
		w=new Worker();//2 Asignar a la variable w el objeto new Worker().
		w.start();//3 Invocar el metodo w.start();
		Socket conexion = null;//4 Declarar la variable conexion de tipo Socket y asignar null a la variable conexion.
		while(true) {//5 en un ciclo
			try {//5.1 en un bloque try
				//conexion = new Socket(ip,20000+(nodo+1)%4);//5.1.1 Asignar a conexion el objeto socket(ip,20000+(nodo+1)%4)
				//PASO 5.1.1 PEro con sockets seguros
				SSLSocketFactory cliente = (SSLSocketFactory) SSLSocketFactory.getDefault();
				conexion = (SSLSocket) cliente.createSocket(ip, 20000+(nodo+1)%4);
				break;//5.1.2 ejecutar break para salir del ciclo 
			// 5.2 En el bloque catch:
			}catch (Exception e) {//5.2 En el bloque catch
				Thread.sleep(500); // Invocar el metodo Thread.sleep(500)
			}
		}
		salida = new DataOutputStream(conexion.getOutputStream());//6 Asignar a la variable salida
																  //el objeto new DataOutputStream(conexion.getOutputStream()).
		w.join();//7 Invocar w.join()
		while(true) {//8 En un ciclo 
			if(nodo == 0) {//8.1 si nodo es 0
				if(inicio == true) {//8.1.1 si inicio es true
					inicio = false;//8.1.1.1 Asignar false a la variable inicio
					token = 1;//8.1.1.2 Asignar 1 a la variable token
				}else { //8.1.2 De otra manera(variable inicio es false)
					token = entrada.readLong();//8.1.2.1 Asignar a token el resultado de entrada.readLong()
					token++;//8.1.2.2 incrementar variable token 
					System.out.println("nodo: "+nodo+" token: "+token+"\n");//8.1.2.3 Desplegar variable nodo y token
				}
			} else {//8.2 De otra manera (si nodo no es 0)
				token = entrada.readLong();	// 8.2.1 Asignar a la variable token el resultado del metodo entrada.readLong().
				token++; //8.2.2 Incrementar variable token
				System.out.println("nodo: "+nodo+" token: "+token+"\n");//8.2.3 Desplegar variable nodo y token
			}
			if(nodo == 0 && token >= 1000) { //8.3 Si la variavle nodo es 0 y token es mayor o igual a 1000
				break;//8.3.1 Salir del ciclo 
			}
			salida.writeLong(token);//Invocar el metodo salida.writeLong(token)
			
		}
		 conexion.close();
	     entrada.close();
		salida.close();
	}
}