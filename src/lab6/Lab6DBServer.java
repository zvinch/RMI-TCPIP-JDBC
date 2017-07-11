/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab6;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.Console;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mzvin
 */
public class Lab6DBServer {

    private static String dbURL = "jdbc:derby://localhost:1527/lab6DB;create=true;user=Mylab6;password=lab6";
    private static String employess = "Employees";
    private static String employeehistory = "EmployeeHistory";
    private static String drivername = "org.apache.derby.jdbc.ClientDriver";
    
    private static Connection conn = null;
    private static Statement stmt = null;
    private static DatabaseMetaData meta = null;
    private static SimpleDateFormat NormalDatetimeFormatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
    private static SimpleDateFormat DBdatetimeFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    
    private static Lab6DBServer instance;
    
    private Lab6DBServer(){}
    
    public static synchronized Lab6DBServer getInstance(){
        if(instance == null){
            instance = new Lab6DBServer();
        }
        return instance;
    }
    
    
    
    private static void shutdown() {
        try
        {
            if (stmt != null)
            {
                stmt.close();
            }
            if (conn != null)
            {
                DriverManager.getConnection(dbURL + ";shutdown=true");
                conn.close();
            }           
        }
        catch (SQLException sqlExcept)
        {
            
        }
    }

    private static void createConnection() {
        try
        {
            Class.forName(drivername).newInstance();
            //Get a connection
            conn = DriverManager.getConnection(dbURL);
            meta = conn.getMetaData();
        }
        catch (Exception except)
        {
            except.printStackTrace();
        }
    }

    
    
    public static void main(String[] args) {
        System.out.format("Добрый день,  это Сервер базы данных lab6:%n"); 
        
        createConnection();
         //createTables();
         
         try {
            ServerSocket ss = new ServerSocket(8888);
            System.out.println("started" + '\n');

            Socket s = ss.accept();
            while (true) {
                Date trialTime = new Date();
                System.out.println("listening");
                BufferedReader InputBuffer = new BufferedReader(new InputStreamReader(s.getInputStream()));
                DataOutputStream OutStream = new DataOutputStream(s.getOutputStream());
                String comand = InputBuffer.readLine();
                System.out.println("Received: " + comand);
                if (comand != null) {
                    String response = "";
                    List<String> comandList = Arrays.asList(comand.split(","));
                        
                    if (comandList.get(0).equals("login")) {
                        if(login(comandList.get(1), comandList.get(2))){
                            response="loggedin"+"\n";
                        }else{
                            response="DENIED"+"\n";
                        }
                    }  else if (comandList.get(0).equals("getHistoryint")) {
                        String[] strA =getHistory(Integer.parseInt(comandList.get(1)));
                        
                        if(strA.length == 0){
                            response="null"+"\n";
                        } else{
                            for(int i = 0; i<strA.length; i++){
                                System.out.println(strA[i]+"\n");
                                OutStream.writeBytes(strA[i]+"\n");
                            }
                            response ="*"+"\n";
                        }
                        
                    } else if (comandList.get(0).equals("getHistorystring")) {
                        String[] strA =getHistory(comandList.get(1));
                        
                        if(strA.length == 0){
                            response="null"+"\n";
                        } else{
                            for(int i = 0; i<strA.length; i++){
                                System.out.println(strA[i]+"\n");
                                OutStream.writeBytes(strA[i]+"\n");
                            }
                            response ="*"+"\n";
                        }
                        
                    } else{
                        response="not recognised"+"\n";
                    }

                    OutStream.writeBytes(response);
                    System.out.println("finished cycle");
                } else{
                    System.out.println("restarting");
                    s = ss.accept();
                }
                
            }

        } catch (IOException ioe) {
           ioe.printStackTrace();
        }
         
         
         
         shutdown();
    }
    
    

    private static void createTables() {
         try
        {
            ResultSet res = meta.getTables(null, null, "%", null);
            ArrayList<String> tablenames = new ArrayList<String>();
            while(res.next()){
                tablenames.add(res.getString("TABLE_NAME"));
            }
            if(!tablenames.contains(employess)){
                stmt = conn.createStatement();
                stmt.execute("CREATE TABLE "+ employess+
                            " ( code INT,"
                               + " name VARCHAR(24) NOT NULL ,"
                               + " last_name VARCHAR(32) NOT NULL ,"
                               + " login VARCHAR(16) NOT NULL ,"
                               + " psw VARCHAR(16)  ,"
                               +" PRIMARY KEY (code) ) "
                );
                stmt.close();
            }
            if(!tablenames.contains(employeehistory)){
                         stmt = conn.createStatement();
            stmt.execute("CREATE TABLE "+ employeehistory+
                        " ( id INT,"
                           +"position VARCHAR(24) NOT NULL,"
                           +"manager INT  CONSTRAINT MANAGER_CK CHECK (manager > 0) ,"
                           + " hire TIMESTAMP  NOT NULL ,"
                           + " dismiss TIMESTAMP NOT NULL,"
                           +"code INT,"
                           +" PRIMARY KEY (id), "
                           +" FOREIGN KEY (code) REFERENCES "+employess+"(code), CHECK (dismiss >= hire))"
        );
            stmt.close();
            }
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
        

    }
    
    
    private static String[] getHistory(String name) {
         
        try
            {
                StringBuilder strb= new StringBuilder();
                
                strb.append("SELECT * FROM "+employeehistory+""
                     + " WHERE (code =  ( SELECT code FROM "+employess+" WHERE ( last_name = '"+name+"')) )"
                    );
            
                
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(strb.toString());
                
                 ResultSetMetaData rsmd = rs.getMetaData();
                List<String> ret = new ArrayList<>();
                while (rs.next()) {
                    String buf = "";
                 for (int i = 1; i < rsmd.getColumnCount()+1; i++) {
                    buf+=rs.getString(i) + " ";
                 }
                 ret.add(buf);
                }
                stmt.close();
                
                String[] retArr = new String[ret.size()];
                retArr = ret.toArray(retArr);
                return retArr;
                
            }catch(Exception ex){
                ex.printStackTrace();
            }
        
        return new String[0] ;
    }

    
    private static String[] getHistory(int code) {
        
       
        try
            {
                StringBuilder strb= new StringBuilder();
                
                strb.append("SELECT * FROM "+employeehistory+""
                     + " WHERE (code = "+code+" )"
                    );
            
                
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(strb.toString());
                
                 ResultSetMetaData rsmd = rs.getMetaData();
                List<String> ret = new ArrayList<>();
                while (rs.next()) {
                    String buf = "";
                 for (int i = 1; i < rsmd.getColumnCount()+1; i++) {
                    buf+=rs.getString(i) + " ";
                 }
                 ret.add(buf);
                }
                stmt.close();
                
                String[] retArr = new String[ret.size()];
                retArr = ret.toArray(retArr);
                return retArr;
                
            }catch(Exception ex){
                ex.printStackTrace();
            }
        
        return new String[0] ;
    }

    
    private static boolean login(String user, String password) {
        boolean res = false;
        try
            {
                StringBuilder strb= new StringBuilder();
                
                strb.append("SELECT code FROM "+employess+""
                     + " WHERE ((login = '"+user+"') AND ('"+password+"' = psw))"
                    );
            
                
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(strb.toString());
                if(rs.next()){
                    res = true;
                }
                stmt.close();
                return res;
                
            }catch(Exception ex){
                ex.printStackTrace();
            }
        res = false;
        return res ;
        
    }

   
    public boolean logout() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
