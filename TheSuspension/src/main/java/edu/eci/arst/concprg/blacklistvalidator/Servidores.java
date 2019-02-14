/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.blacklistvalidator;
import edu.eci.arst.concprg.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author 2112076
 */

public class Servidores extends Thread{
    int checkedListsCount=0;
    int ocurrencesCount=0;
    int inicio;
    int fin;
    int contador=0;
    String ipAdd;
    LinkedList<Integer> blackListOcurrences=new LinkedList<>();
    private HostBlacklistsDataSourceFacade host;
    private static final int BLACK_LIST_ALARM_COUNT=5;

    public Servidores(int inicio, int fin, String ipAdd, HostBlacklistsDataSourceFacade host) {
        this.inicio = inicio;
        this.fin = fin;
        this.ipAdd = ipAdd;
        this.host = host;
    }
    @Override
    public void run(){
         for (int i=inicio;i<fin && ocurrencesCount<BLACK_LIST_ALARM_COUNT;i++){
            if (blackListOcurrences.size()==BLACK_LIST_ALARM_COUNT){
                synchronized(blackListOcurrences){
                    try {
                        blackListOcurrences.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Servidores.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            else{
                if (host.isInBlackListServer(i,ipAdd)){
                checkedListsCount++;
                blackListOcurrences.add(i);
                
                ocurrencesCount++;
            }
                    
                    }
        }
    }

    public LinkedList<Integer> getBlackListOcurrences() {
        return blackListOcurrences;
    }
    public int getBlackListOcurrencesSize(){
        return blackListOcurrences.size();
    }
}
