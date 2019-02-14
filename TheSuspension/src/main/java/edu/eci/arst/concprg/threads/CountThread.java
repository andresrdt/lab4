/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.threads;

/**
 *
 * @author hcadavid
 */
public class CountThread extends Thread{
    int a;
    int b;
    int mayor;
    int menor;
    public CountThread(int a, int b){
        this.a=a;
        this.b=b;
    }
    public void mayor(){
        if (a>b){
            mayor=a;
            menor=b;
        } else{
           mayor=b;
           menor=a; 
        }
    }
    public boolean aunMenor(){
        if (mayor>menor){
            return true;
        } else{
            return false;
        }
    }
    @Override
    public void run() {
        this.mayor();
        while(this.aunMenor()){
            menor+=1;
            System.out.println(menor);
            try{
                Thread.sleep(1);
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        } 
    }
    
}
