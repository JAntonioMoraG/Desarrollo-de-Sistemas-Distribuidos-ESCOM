//Importamos las librerias a utilizar
import java.io.DataOutputStream;
import java.net.Socket; //libreria Socket(cliente)
import java.io.IOException; 
import java.lang.Thread;//libreria hilos
import java.net.ServerSocket;//libreria Socket(SERVER)
import java.io.DataInputStream;

public class PI{ //inicia CLASE PI 
	static Object obj= new Object();
	static float pi = 0;

	static class Worker extends Thread { //clase que maneja los hilos
		Socket conexion;
		Worker(Socket conexion){
			this.conexion = conexion;
		}
		public void run(){ //al iniciar un hilo entra a run
			//ALGORITMO 1
			try {
				//Paso 1 crear streams de entrada y salida
				DataOutputStream salida = new DataOutputStream(conexion.getOutputStream()); //escribir
		        DataInputStream entrada = new DataInputStream(conexion.getInputStream()); //leer
				float suma = 0; //Paso 2 declarar variable suma 
				suma = entrada.readFloat();//Paso 3 recibe en suma la que calculo el cliente
				synchronized(obj) //para que no lo usen dos hilos al mismo tiempo
				{ 
					pi =suma+pi;//Paso 4 asigna en suma+pi a pi
				} 

				//PASO 5 Y 6 cerramos streams y la conexion

				entrada.close();
				salida.close();
				conexion.close();
			} catch(Exception e){System.out.println(e.getMessage());}//"MANEJO" de la exception
		}
	} 

	public static void main(String[] args) throws Exception {
		if (args.length != 1) { //checa que pongamos el argumento 
			System.err.println("Para usar se debe ejecutar como : java Pi <nodo>");
			System.exit(0);
		}

		int nodo = Integer.valueOf(args[0]); //le pasamos a nodo el argumento 
		if (nodo == 0) {  //Si el argumento es 0 entonces es un servidor
			// Algoritmo 2
			ServerSocket servidor;//PASO ! creamos variable servidor 
			servidor= new ServerSocket(30000);// PASO 2 creamos server socket en puerto 30000 
			Worker v[] = new Worker[4];//Creamos vector tipo worker de con 4 elementos
			int i= 0;//PAso 4 declaramos i con valor 0
			for(i=0; i < 4; i++){ //PASO 5.1 si i es igual a 4 sale del ciclo 
				Socket conexion; //5.2 Declara variable conexion de tipo sockeyt
				conexion = servidor.accept();//5.3 invoca servidor.accept
				v[i] = new Worker(conexion);//5.4 Crea instancia de worker con parametro conexion
											//Y le asigna la instancia a v[i]
				v[i].start(); //invoca metofo start 

				//PASO 5.6 y 5.7 ya los hace el for 
			}
			for(i=0; i < 4; i++){//PASO 7
				v[i].join(); //7.2 invoca metodo join

				//PASO 7.1 7.3 y 7.4 los hace el for 
			}
			System.out.println("PI =" + pi);//PASO 8 muestra el valor de pi
		} else {
			//ALGORITMO 3
			Socket conexion = null; //PASO 1 declara socket conexion y se le asigna null

			//PASO 2
			for(;;) // HACE EL REINTENTO 
				try {
				conexion = new Socket("localhost",30000); //conexion cxon localhost puerto 30000 y se lo asigna 
														  // a conexion
				break;//si la conexion es exitosa sale del for 
			} catch (Exception e){ Thread.sleep(200);} // si no conecta hace sleep al hilo por 200 ms
			
			//PASO 3 Crea streams entrada y salida 
			DataOutputStream salida = new DataOutputStream(conexion.getOutputStream()); //escribir
		    DataInputStream entrada = new DataInputStream(conexion.getInputStream()); //leer
			
			float suma= 0; //Paso 4 declara variable suma con valor 0 
			int i=0;//PAso 5

			for(i=0;i<=1000000;i++){//PASO 6
				suma+=4.0/(8*i+2*(nodo-2)+3);//6.2
				//PASOS 6.1 6.3 6.4 los hace el for
			}
			suma=(nodo%2==0)?-suma:suma; //PASO 7
			salida.writeFloat(suma);//PASO 8 envia al servidor valor de suma
			
			//PASO 9 y 10 cerrar streams y conexion
			entrada.close();
			salida.close();
			conexion.close();
		}
	}
}