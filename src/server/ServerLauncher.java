package server;

import interfaces.ControlesJuego;
import interfaces.Jugador;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import interfaces.InfoJuego;

public class ServerLauncher {
    
    public static void main(String[] args) {
//        String rutaServerPolicy = "file:C:/Users/lucio/Documents/NetBeansProjects/ProyectoAlpha/src/server/server.policy";
        String rutaServerPolicy = "file:D:/Documents/NetBeans/SD21-ProyectoAlpha/src/server/server.policy";
        System.setProperty("java.security.policy",rutaServerPolicy);
        if (System.getSecurityManager() == null) {
           System.setSecurityManager(new SecurityManager());
        }
        try {
            LocateRegistry.createRegistry(1099);   
            String nombre = "Información del juego";
            InfoJuegoServer motor = new InfoJuegoServer();
            InfoJuego motor2 = (InfoJuego) UnicastRemoteObject.exportObject(motor, 0);
            Registry registro = LocateRegistry.getRegistry();
            registro.rebind(nombre, motor2);
            System.out.println("Motor enlazado");
            ControlesJuego control = new ControlesJuego();
            TCPThread tcpThread = new TCPThread(control);
            tcpThread.start();
            MulticastConnection multicast = new MulticastConnection();
            control.setMulticast(multicast);
            TimeUnit.SECONDS.sleep(10);
            System.out.println("Inicio? (y)");
            Scanner sc = new Scanner(System.in);
            String s = sc.nextLine();

            if (s.equals("y")){
                boolean bandera = true;
                boolean banderaFin = false;
                while(true){
                    banderaFin = control.getFin();          
                    if(banderaFin){
                        ArrayList<Jugador> aux = control.getArregloJugadores();
                        control = new ControlesJuego();
                        control.setMulticast(multicast);
                        control.setArregloJugadores(aux);
                        tcpThread.setControlesJuego(control);
                        bandera = true;
                        TimeUnit.SECONDS.sleep(10);
                        System.out.println("REINICIANDO EL JUEGO...");
                    }      
                    else {
                        TimeUnit.MILLISECONDS.sleep(500);
                        control.start();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Motor con una excepción encontrada:");
            e.printStackTrace();
        }   
    }     
    
    public boolean cycle(){
        return true;
    }
    
}
