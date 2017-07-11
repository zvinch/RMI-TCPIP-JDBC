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
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import static java.rmi.server.UnicastRemoteObject.unexportObject;
import java.rmi.server.Unreferenced;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mzvin
 */
public class HistoryQuerer extends UnicastRemoteObject implements RemoteSession, Unreferenced{

    private String dbhost = null;
    private static String DBHOST = "localhost";
    private static int DBPORT = 8888;
    private static int RMIPORT = 1099;
    private static String Login_CMND = "login";
    private static String getHistoryint_CMND = "getHistoryint";
    private static String getHistorystring_CMND = "getHistorystring";
    
    public HistoryQuerer(String host) throws RemoteException {
        super();
        dbhost = host;
        
    }
 
    @Override
    public String[] getHistory(String name) throws RemoteException {
        try {
            
            Socket s = new Socket(DBHOST, DBPORT);
            BufferedReader InputBuffer = new BufferedReader(new InputStreamReader(s.getInputStream()));
            DataOutputStream OutStream = new DataOutputStream(s.getOutputStream());
            System.out.println("Getting history by name ...");
            
            OutStream.writeBytes(getHistorystring_CMND+","+name+'\n');
            String result = InputBuffer.readLine();
            
              
            if(result.equals("null")){
               s.close();
               return new String[0];
            }
            List<String> ret = new ArrayList<>();
            while(!result.equals("*")){
                ret.add(result);
                System.out.println(result);
                result = InputBuffer.readLine();
                
            }
            s.close();
            
            String[] retArr = new String[ret.size()];
            retArr = ret.toArray(retArr);
               
            return retArr;
        } catch (IOException ioe) {
            System.out.println("Exception " + ioe.getCause());
            return null;
        }
    }

    @Override
    public String[] getHistory(int code) throws RemoteException {
          try {
            
            Socket s = new Socket(DBHOST, DBPORT);
            BufferedReader InputBuffer = new BufferedReader(new InputStreamReader(s.getInputStream()));
            DataOutputStream OutStream = new DataOutputStream(s.getOutputStream());
            System.out.println("Getting history by code ...");
            
            OutStream.writeBytes(getHistoryint_CMND+","+Integer.toString(code)+'\n');
            String result = InputBuffer.readLine();
            
              
            if(result.equals("null")){
               s.close();
               return new String[0];
            }
            List<String> ret = new ArrayList<>();
            while(!result.equals("*")){
                ret.add(result);
                System.out.println(result);
                result = InputBuffer.readLine();
                
            }
            s.close();
            
            String[] retArr = new String[ret.size()];
            retArr = ret.toArray(retArr);
               
            return retArr;
        } catch (IOException ioe) {
            System.out.println("Exception " + ioe.getCause());
            return null;
        }
    }

    @Override
    public boolean logout() throws RemoteException {
       
        try{
             unexportObject(this, true);
        }catch (Exception e){
            System.err.println("Client exception: " + e.toString());
        }
        return true;
    }

    @Override
    public void unreferenced() {
        try{
             unexportObject(this, true);
        }catch (Exception e){
            System.err.println("Client exception: " + e.toString());
        }
    }
   
}
