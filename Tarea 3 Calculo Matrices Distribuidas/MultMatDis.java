//BIBLIOTECAS A UTILIZAR 
import java.net.Socket;
import java.net.ServerSocket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.lang.Thread;
class MultMatDis{
    
    static Object obj = new Object(); //OBJ SERA USADO EN EL LOCK PARA QUE ENTRE UN HILO A LA VEZ A LA ZONA CRITICA
    static int N = 10; //N que debe ser N=10 para desplegar A B C y CHECKSUM
                         //N=1500 Imprime el checksum
    //Variables que se van a usar para los ciclos 
    static int i=0;
    static int j=0;
    static int k=0; 
    static long checksum = 0;
    static String ip ="10.0.0.4";
    static int port=50000; 
    //MATRICES A USAR: A,B,C DE TIPO LONG de NxN
    static long[][] A = new long[N][N];
    static long[][] B = new long[N][N];
    static long[][] C = new long[N][N];
    

    static class Worker extends Thread{//Clase PAra los Hilos
        Socket conexion;
        Worker(Socket conexion){
            this.conexion = conexion;
        }
        public void run(){ //Cuando Inicia un hilo entra al run
            try {
                //Dividimos A1 y A2 en (N/2)xN y B1 y B2 en Nx(N/2)
                long[][] A1 = new long[N/2][N];
                long[][] A2 = new long[N/2][N];
                //B1 y B2 en este momento son de [N/2][N] debido que con la transpuesta queda [N][N/2]
                long[][] B1 = new long[N/2][N];
                long[][] B2 = new long[N/2][N];

                DataInputStream entrada = new DataInputStream(conexion.getInputStream());//Leer                
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream()); //escribir
                //Recibimos el numero de nodo
                int nodos = entrada.readInt();
                // es parte del NODO 0
                //Vamos a mandar las matrices a cada nodo 
                if (nodos == 1){
                    for(i = 0; i < (N/2); i++){
                        for(j = 0; j < N; j++){
                            A1 [i][j] = A [i][j];
                            B1 [i][j] = B [i][j];
                            salida.writeLong(A1[i][j]); // 3 Enviar la matriz A1 al nodo 1
                            salida.writeLong(B1[i][j]); // 4 Enviar la matriz B1 al nodo 1
                        }
                    }
       
                } else if (nodos == 2){
                    for(i = 0; i < (N/2); i++){
                        for(j = 0; j < N; j++){
                            A1 [i][j] = A [i][j];
                            salida.writeLong(A1[i][j]); // 5 Enviar la matriz A1 al nodo 2.
                        }
                    }
                    for(i = (N/2); i < N; i++){
                        for(j = 0; j < N; j++){
                            B2 [i - (N/2)][j] = B [i][j];
                            salida.writeLong(B2[i - (N/2)][j]); // 6 Enviar la matriz B2 al nodo 2.
                        }
                    }
                } else if (nodos == 3){
                    for(i = (N/2); i < N; i++){
                        for(j = 0; j < N;j++){
                            A2 [i - (N/2)][j] = A [i][j];
                            salida.writeLong(A2[i - (N/2)][j]); //7 Enviar la matriz A2 al nodo 3.
                        }
                    }       
                     
                    for(i = 0; i < (N/2); i++){
                        for(j = 0; j < N; j++){
                            B1 [i][j] = B [i][j];
                            salida.writeLong(B1[i][j]); // 8 Enviar la matriz B1 al nodo 3.
                        }
                    }

                } else if (nodos == 4){                    
                    for(i = (N/2); i < N; i++){
                        for(j = 0; j < N;j++){
                            A2 [i - (N/2)][j] = A [i][j];
                            B2 [i - (N/2)][j] = B [i][j];
                            salida.writeLong(A2[i - (N/2)][j]); // 9 Enviar la matriz A2 al nodo 4.
                            salida.writeLong(B2[i - (N/2)][j]); // 10 Enviar la matriz B2 al nodo 4.
                        }
                    }    
                }                
                synchronized(obj){//Vamos a recibir las matrices en cada nodo todo esto 
                                    //en synchronized para que no entre mas de un hilo a la vez
                    if(nodos == 1){// 11 Recibe la matriz C1 del nodo 1
                        long[][] C1 = new long[N/2][N/2];
                        for(i = 0; i < (N/2); i++){
                            for(j = 0; j < (N/2); j++){
                                C1[i][j] = entrada.readLong(); //asigna a C1 lo que recibe en entrada.readLong()
                                C[i][j] = C1[i][j]; //a C le asigna lo que hay en C1
                            }
                        }
                    }else if (nodos == 2){// 12 Recibe la matriz C2 del nodo 2
                        long[][] C2 = new long[N/2][N];
                        for(i = 0; i < (N/2); i++){
                            for(j = (N/2); j < N; j++){
                                C2[i][j] = entrada.readLong();
                                C[i][j] = C2[i][j];
                            }
                        }    
                    }else if (nodos == 3){// 13 Recibe la matriz C3 del nodo 3
                        long[][] C3 = new long[N][N/2];
                        for(i = (N/2); i < N; i++){
                            for(j = 0; j < (N/2); j++){
                                C3[i - (N/2)][j] = entrada.readLong();
                                C[i][j] = C3[i - (N/2)][j];
                            }
                        }                                 
                    }else if (nodos == 4){// 14 Recibe la matriz C4 del nodo 4
                        long[][] C4 = new long[N][N];
                        for(i = (N/2); i < N; i++){
                            for(j = (N/2); j < N; j++){
                                C4[i - (N/2)][j - (N/2)] = entrada.readLong();
                                C[i][j] = C4[i - (N/2)][j - (N/2)];
                            }
                        }

                    }

                }            
                // Cerramos la conexion, la entrada y la salida
                entrada.close();
                salida.close();
                conexion.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
  
    public static void main(String[] args) throws Exception {
        if (args.length != 1){ //Si no introduces parametros al ejecutar
        System.err.println("Debes poner como argumento el numero de nodo por ejemplo: java MultMatDis <nodo>");
        System.exit(0);
        }
        int nodo = Integer.valueOf(args[0]);
       
       //************ NODO 0 *******************

        if (nodo == 0){ 
            //MATRICES ORIGINALES DE NxN
            A = new long[N][N];
            B = new long[N][N];
            C = new long[N][N];
            //1 inicializamos nuestras matrices A y B
            for (i = 0; i < N; i++){
                for (j = 0; j < N; j++){
                    A[i][j] = 3*i+j;
                    B[i][j] = 3*i-j;
                }
            }                
            //2 transponer la matriz B
            for (i = 0; i < N; i++){
                for (j = 0; j < i; j++){
                    //intercambio de indices de B como en MultiplicaMatriz_2
                    long x = B[i][j];
                    B[i][j] = B[j][i];
                    B[j][i] = x;
                }
            }
            ServerSocket servidor = new ServerSocket(50000); //creamos servidor puerto 50000
            // vamos a aceptar 4 clientes (nodo 1,2,3,4)
            Worker[] w = new Worker[4];
            
            int numcliente = 0;
            while (numcliente != 4){
                Socket conexion = servidor.accept();
                w[numcliente] = new Worker(conexion);
                w[numcliente].start();
                numcliente++;
            }
            // Esperamos a que se ejecute el hilo
            int y = 0;
            while (y != 4){
                w[y].join();
                y++;
            }
             // cerramos el servidor
            servidor.close();
            // 14. Calcular el checksum de la matriz C.
            for( i = 0; i < N; i++){
                for( j = 0; j < N; j++){
                    checksum += C[i][j];
                }
            }// 16 Desplegar el checksum de la matriz C.
            System.out.println("checksum= " + checksum);
            // 17 Si N=10 entonces desplegar las matrices A,B yC
            if (N == 10){
                System.out.println("Matriz A ");
                imprimematriz(A,N,N);  
                System.out.println("Matriz B ");
                imprimematriz(B,N,N);  
                System.out.println("Matriz C ");
                imprimematriz(C,N,N);           
            }
        }else{ //Si es algun otro nodo 
            // matrices que usaremos para recibir 
            long[][] tempA = new long[N/2][N];
            long[][] tempB = new long[N/2][N];
            // matriz que usaremos para  A1 x B1
            long[][] resC = new long[N/2][N/2];
            
            //************ NODO 1 ****************

            if(nodo == 1){
                Socket conexion = new Socket(ip, port);
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                // Enviamos al nodo 0 (Servidor) el nodo
                salida.writeInt(nodo);
                          
                for (i = 0; i < (N/2); i++){
                    for (j = 0; j < N; j++){
                        tempA[i][j] = entrada.readLong(); //1 Recibir del nodo 0 la matriz A1.
                        tempB[i][j] = entrada.readLong(); //2 Recibir del nodo 0 la matriz B1.
                    }
                }
                for ( i = 0; i < (N/2); i++){
                    for (j = 0; j < (N/2); j++){
                        for (k = 0; k < N; k++){
                            resC[i][j] += tempA[i][k] * tempB[j][k];//3 Realizar el producto C1=A1xB1 (Renglon por renglon)
                        }
                    }
                }    
                //4 Enviar la matriz C1 al nodo 0.
                for( i = 0; i < (N / 2); i++){
                    for( j = 0; j < (N / 2); j++){  
                        salida.writeLong(resC[i][j]);
                    }
                }
                //cerramos entrada, salida y la conexión
                entrada.close();
                salida.close();
                conexion.close();
            }

            //******** NODO 2 **************

            else if (nodo == 2){
                Socket conexion = new Socket(ip, port);
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                // Enviamos al nodo 0 (servidor )el nodo
                salida.writeInt(nodo);
                for(i = 0; i < (N/2); i++){
                    for(j = 0; j < N; j++){
                        tempA[i][j] = entrada.readLong();//1 Recibir del nodo 0 la matriz A1.
                    }    
                }
                for(i = (N/2); i < N; i++){
                    for(j = 0; j < N; j++){
                        tempB[i-(N/2)][j] = entrada.readLong();//2 Recibir del nodo 0 la matriz B2.

                    }    
                } 
                
                for ( i = 0; i < (N/2); i++){
                    for (j = 0; j < (N/2); j++){
                        for (k = 0; k < N; k++){
                            resC[i][j] += tempA[i][k] * tempB[j][k];//3 Realizar el producto C2=A1xB2 (Renglon por renglon)
                        }
                    }
                }
               
                for( i = 0; i < (N / 2); i++){
                    for( j = 0; j < (N / 2); j++){
                        salida.writeLong(resC[i][j]);//4 Enviar la matriz C2 al nodo 0.              
                    }
                }
                entrada.close();
                salida.close();
                conexion.close();
            }

            //******** NODO 3 ************

            else if (nodo == 3){
                Socket conexion = new Socket(ip, port);
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                salida.writeInt(nodo);
                 
                for( i = (N/2); i < N; i++){
                    for( j = 0; j < N; j++){
                        tempA[i-(N/2)][j] = entrada.readLong();//1 Recibir del nodo 0 la matriz A2
                    }
                }
                for( i = 0; i < (N/2); i++){
                    for( j = 0; j < N; j++){
                        tempB[i][j] = entrada.readLong();//2 Recibir del nodo 0 la matriz B1.
                    }
                }
                
                for ( i = 0; i < (N/2); i++){
                    for (j = 0; j < (N/2); j++){
                        for (k = 0; k < N; k++){
                            resC[i][j] += tempA[i][k] * tempB[j][k];//3 Realizar el producto C3=A2xB1 (renglon por renglon)
                        }
                    }
                }
                for(i = 0; i < (N/2); i++){
                    for(j = 0; j < (N/2); j++){
                        salida.writeLong(resC[i][j]);//4 Enviar la matriz C3 al nodo 0.
                    }
                }
                entrada.close();
                salida.close();
                conexion.close();
            }

            //*********** NODO 4 ******************

            else if (nodo == 4){
                Socket conexion = new Socket(ip, port);
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                salida.writeInt(nodo);
                 
                for(i = (N/2); i < N; i++){
                    for(j = 0; j < N; j++){
                        tempA[i-(N/2)][j] = entrada.readLong();//1 Recibir del nodo 0 la matriz A2
                        tempB[i-(N/2)][j] = entrada.readLong();//2 Recibir del nodo 0 la matriz B2.
                    }
                }
                
                for ( i = 0; i < (N/2); i++){
                    for (j = 0; j < (N/2); j++){
                        for (k = 0; k < N; k++){
                            resC[i][j] += tempA[i][k] * tempB[j][k];//3 Realizar el producto C4=A2xB2(renglon por renglon)
                        }
                    }
                }
                
                for( i = 0; i < (N/2); i++){
                    for( j = 0; j < (N/2); j++){
                        salida.writeLong(resC[i][j]);//4 Enviar la matriz C4 al nodo 0.
                    }
                }
                entrada.close();
                salida.close();
                conexion.close();
            }
                     
        }
    }
    
    // Clase para Imprimir Las matrices
    private static void imprimematriz(long[][] mat, long filas, long columnas) {
        for (i = 0; i< filas; i++){
            for (j = 0; j < columnas; j++){
                System.out.print(mat[i][j] + " ");
            }
            System.out.println("");
        }
    }
}