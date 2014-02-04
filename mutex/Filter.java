package mutex;


import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.TimeUnit;


public class Filter implements Lock
{
    final private ThreadLocal<Integer> THREAD_ID = new ThreadLocal<Integer>(){
        final private AtomicInteger id = new AtomicInteger(0);

        protected Integer initialValue(){
            return id.getAndIncrement();
        }
    };


    //We use AtomicInteger here.
    //Using int would not work because of memory model issues ---
    // Java memory model is not sequentially consistent. 
    //Using volatile int would not work, because there is no way of declaring
    //an array element volatile.  
    final private AtomicInteger[] level ;
    final private AtomicInteger[] victim ;
    final private int n;
    
    public Filter (int n) {
        this.n = n;
        level = new AtomicInteger[n];
        victim = new AtomicInteger[n]; // use 1.. n-1
        for (int i = 0; i < n; i++) {
            level[i] = new AtomicInteger();
            victim[i] = new AtomicInteger();
        }
    }
    
    public void lock() {
        int me = THREAD_ID.get();
        for (int i=1 ; i<this.n ; i++) { //attempt level 1
            System.out.println("Attempting Level " + i);
            level[me].set(i);
            victim[i].set(me);
            // spin while conflicts exist
            while (exists(me,i)){};
        }
    }
    
    private boolean exists(int me, int i){
        for(int k=0 ; k<this.n ; k++){
            if(k==me)
                continue;
            
            if(level[k].get()>=i && victim[i].get()==me)
                return true;
        }
        return false;
    }
    
    public void unlock() {
        int me = THREAD_ID.get();
        level[me].set(0);
    }

      // Any class implementing Lock must provide these methods
    public Condition newCondition() {
	throw new java.lang.UnsupportedOperationException();
    }
    public boolean tryLock(long time,
			   TimeUnit unit)
	throws InterruptedException {
	throw new java.lang.UnsupportedOperationException();
    }
    public boolean tryLock() {
	throw new java.lang.UnsupportedOperationException();
    }
    public void lockInterruptibly() throws InterruptedException {
	throw new java.lang.UnsupportedOperationException();
    }


}


