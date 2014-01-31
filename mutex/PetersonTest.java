
package mutex;


public class PetersonTest {
  private final static int THREADS = 2;
  private final static int COUNT = 1024;
  private final static int PER_THREAD = COUNT / THREADS;
  Thread[] thread = new Thread[THREADS];
  int counter = 0;

  Peterson instance = new Peterson();
  
  public void testParallel() throws Exception {
      System.out.println("test parallel");
     // ThreadID.reset();
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
    }
  }

  public static void main(String[] args) {
      PetersonTest mpt = new PetersonTest();
      try {
	  mpt.testParallel();
      }
      catch (Exception e) {}   
  }
}
