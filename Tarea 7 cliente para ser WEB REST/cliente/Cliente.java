/*
Autor Mora Guzman Jose Antonio
Desarrollo de Sistemas Distribuidos 
Grupo 4CV13
Tarea 7: Desarrollo de un cliente para un servicio WEB estilo REST
*/
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

public class Cliente {
   static String memail,mnnombre,map_p,map_m,mfna,mtel,mgen;

    public static void main(String[] args) throws Exception {

        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("a. Alta usuario");
            System.out.println("b. Consulta usuario");
            System.out.println("c. Borra usuario");
            System.out.println("d. Salir");
            System.out.print("Opcion: ");

            char opc = br.readLine().charAt(0);
            Usuario usuario = new Usuario();
            switch (opc) {
                case 'a':
                    System.out.println("Alta usuario");
                    

                    System.out.print("Email: ");
                    usuario.email = br.readLine();

                    System.out.print("Nombre: ");
                    usuario.nombre = br.readLine();

                    System.out.print("Apellido Paterno: ");
                    usuario.apellido_paterno = br.readLine();

                    System.out.print("Apellido Materno: ");
                    usuario.apellido_materno = br.readLine();
                    
                    System.out.print("Fecha de nacimiento: ");
                    usuario.fecha_nacimiento = br.readLine();

                    System.out.print("Telefono: ");
                    usuario.telefono = br.readLine();

                    System.out.print("Genero (M/F): ");
                    usuario.genero = br.readLine();
                    alta_usuario(usuario);
                    break;
                case 'b':
                    System.out.println("Consulta usuario");
                    System.out.print("Ingresa el ID de usuario: ");
                    String id=br.readLine();
                    consultar_usuario(Integer.parseInt(id));
                    System.out.println("Desea modificar los datos del usuario ? s/n");
                    char opc2= br.readLine().charAt(0);
                    if(opc2=='s'){
                    
                   
                    usuario.id_usuario=Integer.parseInt(id);
                    System.out.print("Email: ");
                    usuario.email = br.readLine();
                    if(usuario.email==null || usuario.email.equals("")){
                        usuario.email=memail;
                    }
                    System.out.print("Nombre: ");
                    usuario.nombre = br.readLine();
                    if(usuario.nombre==null || usuario.nombre.equals("")){
                        usuario.nombre=mnnombre;
                    }

                    System.out.print("Apellido Paterno: ");
                    usuario.apellido_paterno = br.readLine();
                    if(usuario.apellido_paterno==null || usuario.apellido_paterno.equals("")){
                        usuario.apellido_paterno=map_p;
                    }

                    System.out.print("Apellido Materno: ");
                    usuario.apellido_materno = br.readLine();
                    if(usuario.apellido_materno==null || usuario.apellido_materno.equals("")){
                        usuario.apellido_materno=map_m;
                    }
                    
                    System.out.print("Fecha de nacimiento: ");
                    usuario.fecha_nacimiento = br.readLine();
                    if(usuario.fecha_nacimiento==null || usuario.fecha_nacimiento.equals("")){
                        usuario.fecha_nacimiento=mfna;
                    }

                    System.out.print("Telefono: ");
                    usuario.telefono = br.readLine();
                    if(usuario.telefono==null || usuario.telefono.equals("")){
                        usuario.telefono=mtel;
                    }

                    System.out.print("Genero (M/F): ");
                    usuario.genero = br.readLine(); 
                    if(usuario.genero==null || usuario.genero.equals("")){
                        usuario.genero=mgen;
                    }       
                    modifica_usuario(usuario);
                    }
                    else if(opc2=='n'){
                        System.out.println("CONSULTA FINALIZADA :)");
                    }
                    else{
                        System.out.println("Opcion no valida, Saliendo a menu"); 
                    }

                    break;
                case 'c':
                    System.out.println("Borrar usuario");
                    System.out.print("Ingresa el ID de usuario: ");
                    borrar_usuario(Integer.parseInt(br.readLine()));
                    break;
                case 'd':
                    br.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opcion no valida");
                    break;
            }
        }
    }

    public static void alta_usuario(Usuario usuario) throws IOException {
        URL url = new URL("http://20.114.1.4:8080/Servicio/rest/ws/alta_usuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);

        // en este caso utilizamos el metodo POST de HTTP
        conexion.setRequestMethod("POST");

        // indica que la peticion estara codificada como URL
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();

        Gson gson = builder.create();
        String body = gson.toJson(usuario);

        String parametros = "usuario=" + URLEncoder.encode(body, "UTF-8");

        OutputStream os = conexion.getOutputStream();
        os.write(parametros.getBytes());

        os.flush();

        // se debe verificar si hubo error
        if (conexion.getResponseCode() == 200) { // no hubo error
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));
            String respuesta;
            // el metodo web regresa una string en formato JSON
            while ((respuesta = br.readLine()) != null)
                System.out.println("Se agrego el usuario con ID " + respuesta);
        } else { // hubo error
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
            String respuesta; // el metodo web regresa una instancia de la clase Error en formato JSON
            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);
            // dispara una excepcion para terminar el programa
            throw new RuntimeException("Codigo de error HTTP: " + conexion.getResponseCode());
        }

        conexion.disconnect();

    }

    public static void consultar_usuario(int id_usuario) throws IOException {

        URL url = new URL("http://20.114.1.4:8080/Servicio/rest/ws/consulta_usuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);

        // en este caso utilizamos el metodo POST de HTTP
        conexion.setRequestMethod("POST");

        // indica que la peticion estara codificada como URL
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String parametros = "id_usuario=" + URLEncoder.encode(String.valueOf(id_usuario), "UTF-8");

        OutputStream os = conexion.getOutputStream();
        os.write(parametros.getBytes());

        os.flush();

        // se debe verificar si hubo error
        if (conexion.getResponseCode() == 200) { // no hubo error
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));
            String respuesta;

            Gson j = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            while ((respuesta = br.readLine()) != null){
                Usuario user = (Usuario) j.fromJson(respuesta, Usuario.class);
                //System.out.println("ID: " + user.id_usuario);
               
               //VARIABLES PARA USAR POR SI NO SE INGRESA ALGUN CAMPO EN MODIFICAR 
                memail=user.email;
                mtel=user.telefono;
                mnnombre=user.nombre;
                map_p=user.apellido_paterno;
                map_m=user.apellido_materno;
                mfna=user.fecha_nacimiento;
                mgen=user.genero;
                //System.out.println("Email: " + user.email);
                System.out.println("Nombre: " + user.nombre);
                System.out.println("Apellido Paterno: " + user.apellido_paterno);
                System.out.println("Apellido Materno: " + user.apellido_materno);
                System.out.println("Fecha: " + user.fecha_nacimiento);
                System.out.println("Telefono: " + user.telefono);
                System.out.println("Genero: " + user.genero);
            }

            // el metodo web regresa una string en formato JSON
            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);
        } else { // hubo error
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
            String respuesta; // el metodo web regresa una instancia de la clase Error en formato JSON
            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);
            // dispara una excepcion para terminar el programa
            throw new RuntimeException("Codigo de error HTTP: " + conexion.getResponseCode());
        }

        conexion.disconnect();

    }

    public static void borrar_usuario(int id_usuario) throws IOException {

        URL url = new URL("http://20.114.1.4:8080/Servicio/rest/ws/borra_usuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);

        // en este caso utilizamos el metodo POST de HTTP
        conexion.setRequestMethod("POST");

        // indica que la peticion estara codificada como URL
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // el metodo web "consulta_usuario" recibe como parametro el id de un usuario,
        // en este caso el id es 10
        String parametros = "id_usuario=" + URLEncoder.encode(String.valueOf(id_usuario), "UTF-8");

        OutputStream os = conexion.getOutputStream();
        os.write(parametros.getBytes());

        os.flush();

        // se debe verificar si hubo error
        if (conexion.getResponseCode() == 200) { // no hubo error
            //BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));
            //String respuesta;

            // el metodo web regresa una string en formato JSON
            //while ((respuesta = br.readLine()) != null)
                //System.out.println(respuesta);
            System.out.println("El usuario ha sido borrado");
        } else { // hubo error
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
            String respuesta; // el metodo web regresa una instancia de la clase Error en formato JSON
            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);
                // dispara una excepcion para terminar el programa
            throw new RuntimeException("Codigo de error HTTP: " + conexion.getResponseCode());
        }

        conexion.disconnect();

    }



    public static void modifica_usuario(Usuario usuario) throws IOException {

        URL url = new URL("http://20.114.1.4:8080/Servicio/rest/ws/modifica_usuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);

        // en este caso utilizamos el metodo POST de HTTP
        conexion.setRequestMethod("POST");

        // indica que la peticion estara codificada como URL
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // el metodo web "consulta_usuario" recibe como parametro el id de un usuario,
        // en este caso el id es 10
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();

        Gson gson = builder.create();
        String body = gson.toJson(usuario);

        String parametros = "usuario=" + URLEncoder.encode(body, "UTF-8");

        OutputStream os = conexion.getOutputStream();
        os.write(parametros.getBytes());

        os.flush();

        // se debe verificar si hubo error
        if (conexion.getResponseCode() == 200) { // no hubo error
                System.out.println("El usuario se ha modificado :)");
        } else { // hubo error
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
            String respuesta; // el metodo web regresa una instancia de la clase Error en formato JSON
            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);
            // dispara una excepcion para terminar el programa
            throw new RuntimeException("Codigo de error HTTP: " + conexion.getResponseCode());
        }

        conexion.disconnect();

    }
}

class Usuario {
    int id_usuario;
    String email;
    String nombre;
    String apellido_paterno;
    String apellido_materno;
    String fecha_nacimiento;
    String telefono;
    String genero;
    byte[] foto;
}

