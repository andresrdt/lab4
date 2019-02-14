/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.blacklistvalidator;

import edu.eci.arst.concprg.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT = 5;
    private ArrayList<Integer> blackListOcurrences = new ArrayList<>();
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    /**
     * Check the given host's IP address in all the available black lists, and
     * report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case. The
     * search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as NOT
     * Trustworthy, and the list of the five blacklists returned.
     *
     * @param ipaddress suspicious host's IP address.
     * @return Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, int a) {

        LinkedList<Integer> blackListOcurrences = new LinkedList<>();
        ArrayList<Servidores> thread = new ArrayList<Servidores>();
        int ocurrencesCount = 0;

        HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();
        int inicio = 0;
        if (a % 2 == 0) {
            int count = skds.getRegisteredServersCount() / a;
            for (int i = 0; i < a; i++) {
                inicio += count;
                Servidores hilo = new Servidores(inicio - count, inicio, ipaddress, skds);
                thread.add(hilo);
                hilo.start();
            }
        } else {
            int count = (int) skds.getRegisteredServersCount() / a;
            for (int i = 0; i < a; i++) {
                inicio += count;
                if (inicio > skds.getRegisteredServersCount()) {
                    inicio = skds.getRegisteredServersCount();
                }
                Servidores hilo = new Servidores(inicio - count, inicio, ipaddress, skds);
                thread.add(hilo);
                hilo.start();
            }
        }
        for (Servidores i : thread) {
            try {
                i.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(HostBlackListsValidator.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        for (Servidores i : thread) {
            ocurrencesCount += i.getBlackListOcurrencesSize();
        }
        for (Servidores i : thread) {
            i.stop();
        }
        if (ocurrencesCount >= BLACK_LIST_ALARM_COUNT) {
            skds.reportAsNotTrustworthy(ipaddress);
        } else {
            skds.reportAsTrustworthy(ipaddress);
        }

        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{ocurrencesCount, skds.getRegisteredServersCount()});

        return blackListOcurrences;
    }

}
