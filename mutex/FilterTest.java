
package mutex;


public class FilterTest  {
  private final static int THREADS = 8;
  private final static int COUNT = 8*1000;
  private final static int PER_THREAD = COUNT / THREADS;
  Thread[] thread = new Thread[THREADS];
  volatile int counter = 0;
  
  Filter instance = new Filter(THREADS);
  java.util.concurrent.locks.Lock lock = new java.util.concurrent.locks.ReentrantLock();   
  
  public void testParallel() throws Exception {
    System.out.println("test parallel");
    //ThreadID.reset();
    for (int i = 0; i < THREADS; i++) {
      thread[i] = new MyThread();
    }
    for (int i = 0; i < THREADS; i++) {
      thread[i].start();
    }
    for (int i = 0; i < THREADS; i++) {
      thread[i].join();
    }
    if (counter != COUNT) {
	System.out.println("Wrong! " + counter + " " + COUNT);
    }
  }
  
  class MyThread extends Thread {
    public void run() {
      for (int i = 0; i < PER_THREAD; i++) {
	  instance.lock();
        try {
          counter = counter + 1;
        } finally {
	    instance.unlock();
        }
      }
      //System.out.println("ThreadID: "+ThreadID.get());
    }
  }

  public static void main(String[] args) {
    FilterTest mft = new FilterTest();
    try {
      mft.testParallel();
    }
    catch (Exception e) {}   
  }

}
