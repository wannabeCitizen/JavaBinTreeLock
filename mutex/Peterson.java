package mutex;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.TimeUnit;


public class Peterson implements Lock
{
    final private ThreadLocal<Integer> THREAD_ID = new ThreadLocal<Integer>(){
        final private AtomicInteger id = new AtomicInteger(0);

        protected Integer initialValue(){
            return id.getAndIncrement();
        }
    };


    //We use AtomicBoolean here.
    //Using boolean would not work because of memory model issues --- 
    // Java memory model is not sequentially consistent. 
    //Using volatile boolean would not work, because there is no way of declaring
    //an array element volatile.  
    final private AtomicBoolean[] flag = new AtomicBoolean[2];
    private volatile int victim;
    
    public Peterson(){
        for(int i=0 ; i<flag.length ; ++i)
            flag[i] = new AtomicBoolean();
    }
    
    public void lock() {
        int i = THREAD_ID.get() % 2;
        int j = 1 - i;
        flag [i].set(true); // I am interested
        victim = i ; // you go first
        while ( flag[j].get() && victim == i) {}; // wait
    }
    
    public void unlock() {
        int i = THREAD_ID.get() % 2;
        flag[i].set(false); // I am not interested
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


