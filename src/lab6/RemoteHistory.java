/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab6;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author mzvin
 */
public interface RemoteHistory extends Remote {
     RemoteSession login (String user, String password) throws RemoteException;
}
