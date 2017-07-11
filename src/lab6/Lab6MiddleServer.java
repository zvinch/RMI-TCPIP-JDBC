/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab6;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

/**
 *
 * @author mzvin
 */
public class Lab6MiddleServer implements RemoteHistory{

    private static String DBHOST = "localhost";
    private static int DBPORT = 8888;
    private static int RMIPORT = 1099;
    private static String Login_CMND = "login";
    
    
    public static void main(String[] args) {
        try {
        Lab6MiddleServer obj = new Lab6MiddleServer();
        RemoteHistory stub = (RemoteHistory) UnicastRemoteObject.exportObject(obj, 0);
        
        LocateRegistry.createRegistry(RMIPORT);
        Registry registry = LocateRegistry.getRegistry();
        registry.bind("RemoteHistory", stub);
        
        System.out.format("Добрый день,  это Сервер Аутентификации lab6:%n");
        System.out.println("Server is accepting clients");
        } catch(Exception e) {
            System.err.println("Server exception: " + e.toString());
        }
        
    }

    @Override
    public synchronized  RemoteSession login(String user, String password) {
        
//        if((user.equals("mike"))&&(password.equals("cluck"))){
//            try{
//                
//            HistoryQuerer res = new HistoryQuerer(DBHOST);
//            return res;
//            } catch(Exception e){
//                System.out.println("Exception " + e.getCause());
//            }
//        }
        
        
        try {
            
            Socket s = new Socket(DBHOST, DBPORT);
            BufferedReader InputBuffer = new BufferedReader(new InputStreamReader(s.getInputStream()));
            DataOutputStream OutStream = new DataOutputStream(s.getOutputStream());
            System.out.println("Logging in ...");
            
            OutStream.writeBytes(Login_CMND+","+user+","+password+'\n');
            String result = InputBuffer.readLine();
            HistoryQuerer res = null;
            if(result.equals("loggedin")){
                res = new HistoryQuerer(DBHOST);
            }else if(result.equals("DENIED")){
                res = null;
            }
            
            if(res != null){
                System.out.println("Logged in "+user);
            }else{
                System.out.println("DENIED");
            }
            s.close();
            return res;
        } catch (IOException ioe) {
            System.out.println("Exception " + ioe.getCause());
            return null;
        }
        
        
    }


    
}
