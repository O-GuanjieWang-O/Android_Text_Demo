import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bank{
    private final double []accounts;
    private Lock bankLock=new ReentrantLock();
    private Condition sufficientFunds;

    public Bank(int n,double initialBalence){
        accounts=new double[n];
        sufficientFunds=bankLock.newCondition();
        Arrays.fill(accounts,initialBalence);
    }
    public synchronized void transfer(int from, int to, double amount){

        try{
            while(accounts[from]<amount){
                wait();//bankLock be released
            }
            System.out.println(Thread.currentThread());
            accounts[from]-=amount;
            System.out.printf("amount %10.2f from %d to %d\n" , amount , from , to );
            accounts[to]+=amount;
            System.out.printf("Total Balence: '%10.3f'%n" , this.getTotalBalance() );
            notifyAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally{

        }
    }
    public double getTotalBalance(){
        double sum=0;
        for(double a:accounts){
            sum+=a;
        }
        return sum;
    }
    public int size(){
        return accounts.length;
    }
}