/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab6;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/**
 *
 * @author mzvin
 */
public class Lab6Client {

    private static boolean logedIN = false;
    private static String INPUT_login = "";
    private static RemoteSession response = null;
    private static RemoteHistory stub = null;
    private static BufferedReader br = null;
    
    
    private static String NumberRegex = "[0-9]+";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Lab6Client client = new Lab6Client();
        client.run();
    }

    private boolean login(){
        try{
        int fails = 3;
        while(!logedIN){
         
          System.out.format("Введите login:%n");
          INPUT_login =  br.readLine();
          
          if ((INPUT_login.length()==0)||(INPUT_login.length()>16)) { 
               System.out.format("Не верные данные%n");
               continue;
          }
          System.out.format("Введите password:%n");
          String INPUT_pass =  br.readLine();
          if ((INPUT_pass.length()==0)||(INPUT_pass.length()>16)) { 
               System.out.format("Не верные данные%n");
               continue;
          }
          
          
          response = stub.login(INPUT_login, INPUT_pass);
             if(response == null){
                 if(fails>0){
                     fails--;
                     System.out.format("Не верные данные%n");
                 }else{
                     System.out.format("Отказать! Хорошего дня!%n");
                     return false;
                 }
                        
                continue;
             }else{
                 System.out.println("Вы вошли");
                 logedIN = true;
                 return true;
             }
             
          }
        } 
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
         return false;
    }
    
    private void run() {
        String host = "localhost";
        
        try {
          
          Registry registry = LocateRegistry.getRegistry(host);
          stub = (RemoteHistory) registry.lookup("RemoteHistory");
          br = new BufferedReader(new InputStreamReader(System.in));
          
          boolean working =true;
          
          System.out.format("Добрый день,  это клиент lab6:%n");
          
          if(!login()){
              return;
          }
          
         String comand;
         
         System.out.format("Доступные команды:%n");
         System.out.format(" getHistorybyCode - выбрать записи по коду%n");
         System.out.format(" getHistorybyName - выбрать записи по имени%n");
         System.out.format(" logout - выйти из учетной записи %n");
         System.out.format(" exit - выход%n");
         System.out.format(" help - список команд%n");
         
         String[] res;
         
         while(working){
             try{
         System.out.format("Введите команду: %n");
         comand = br.readLine();
         if(comand.equals("getHistorybyCode")){
              System.out.format("Введите code в формате не отрицательного целого числа: %n");
              comand = br.readLine();
              
              res = response.getHistory(Integer.parseInt(comand));
             System.out.println("response: " );
             for(int i=0; i<res.length; i++){
                 System.out.println(res[i]);
             }
             
         }else if(comand.equals("getHistorybyName")){
             System.out.format("Введите имя: %n");
             comand = br.readLine();
             
             
             res = response.getHistory(comand);
             System.out.println("response: " );
             for(int i=0; i<res.length; i++){
                 System.out.println(res[i]);
             }
             
         }else if(comand.equals("logout")){
             System.out.format("Выход из учетной записи %n");
             response.logout();
             logedIN =false;
             if(!login()){
              return;
             }
             
         }
         else if(comand.equals("exit")){
             System.out.format("Хорошего дня! %n");
             working = false;
         }
         else if(comand.equals("help")){
            System.out.format("Доступные команды:%n");
            System.out.format(" getHistorybyCode - выбрать записи по коду%n");
            System.out.format(" getHistorybyName - выбрать записи по имени%n");
            System.out.format(" logout - выйти из учетной записи %n");
            System.out.format(" exit - выход%n");
            System.out.format(" help - список команд%n");
         }else{
             System.out.println("Я таких слов не знаю: "+comand);
         }
         comand="";
             }catch(Exception ex){
                 ex.printStackTrace();
             }
         }
             
             
             
             
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
        System.out.println("Доброго дня!");
    }
    
    
    
}
