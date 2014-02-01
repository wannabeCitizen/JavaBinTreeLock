package mutex;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class PetersonTree{

	//Need to track local threads
	final private ThreadLocal<Integer> THREAD_ID = new ThreadLocal<Integer>(){
        final private AtomicInteger id = new AtomicInteger(0);
        //Increments 
        protected Integer initialValue(){
            return id.getAndIncrement();
        }
    };

	private int MAX_DEPTH;
	private Peterson[][] lockLevel;   

    //Constructor
	public PetersonTree(int threads){

		//Determing Max Depth of the Tree
		this.MAX_DEPTH =  (int) Math.ceil(((Math.log(threads))/Math.log(2))) ;
		//Initialize Tree of Peterson Locks
		this.lockLevel = new Peterson[(this.MAX_DEPTH) + 1][threads + 1];
		for(int i = 0; i <= this.MAX_DEPTH; i++){
			for (int j = 0; j <= threads; j++){
				lockLevel[i][j] = new Peterson();
			}
		}
		
	}

	//For trying to obtain lock
	public void PTlock(){
		//Get ID and starting point
		int i = THREAD_ID.get();
		int start = this.MAX_DEPTH;

		//Keep trying to obtain the next level lock until you've achieved root lock
		while (start>=0){
			int me = i;
			System.out.println("Attempting lock: " + start + ", " + me);
			this.lockLevel[start][me].lock();
			start--;
			me = (int) Math.ceil(i/2);
		}

	}

	//For unlocking your path
	public void PTunlock(){

		int i = THREAD_ID.get();
		ArrayList<Integer> path = new ArrayList(this.MAX_DEPTH);
		int start = 0;

		//Need to figure out what locks we have to unlock
		for(int j=0 ; j < path.size() ; j++){
			path.add(i);
			i = (int) Math.ceil(i/2);
		}

		//Using path through tree, I go back and unlock
		for(int j=(path.size() - 1); j > 0; j--){
			this.lockLevel[start][path.get(j)].unlock();
			start++;
		}
			
	}
}