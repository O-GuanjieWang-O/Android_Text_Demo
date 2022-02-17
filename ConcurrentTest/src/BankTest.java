import java.util.*;
import java.util.concurrent.locks.*;
public class BankTest {
    public static final int NACCOUNTS=10;
    public static final double INITIAL_BALANCE=1000;
    public static final double MAX_AMOUNT=2*1000;
    public static final int DELAY=10;
    public static void  main (String []args){
        Bank bank=new Bank(NACCOUNTS,INITIAL_BALANCE);
        for(int i=0;i<NACCOUNTS;i++){
            int fromAccount=i;
            Runnable r=new Runnable(){
                @Override
                public void run(){
                    try{
                        while(true){
                            int toAccount=(int)(bank.size()*Math.random());
                            double amount=MAX_AMOUNT*Math.random();
                            bank.transfer(fromAccount,toAccount,2000);
                            Thread.sleep((int)(DELAY*Math.random()));

                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            };
            Thread t=new Thread(r);
            t.start();
        }

    }

}
