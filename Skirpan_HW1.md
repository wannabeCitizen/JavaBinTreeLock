#Concurrent Programming HW1

###Michael Skirpan


**Problem 2**:

1) The basic reasoning behind discussing a 'doorway section' is to specify the events pertinent to each thread attempting to get into the critical section of code that is being handled by some concurrency method.  If we simply left out the details of the doorway, i.e., shared objects and variables being used as overhead for a concurrent solution, the process would be viewed as a black box - leaving out the details where a particular solution could go wrong.  In the case of FCFS for a lock, merely entering the lock() method first is not enough to be guaranteed first passage through to the critical section.  Take the example of a Peterson Lock:

```
lock(){
	i = thread.getID() % 2;
	j = 1 - i;
	flag[i] = true;
	victim = i;
	while (flag[j]==true && victim == i){}

```

When thread A enters the lock it does not immediately obtain the lock or start waiting; instead, it first must write to variable i and j, set it's flag to true and write to the victim variable.  Only *after* these writes does it reach the while loop where it goes back to read flag[j] and the victim variable and try to enter the critical section of the code.  Thus even if Thread A gets to the doorway first (i.e., into the lock() method) Thread B could overtake Thread A and set itself as the victim first, gaining access to the critical section of the code first.  This order events would explain this:

A(calls lock) --> B(calls lock) --> A(writes flag[0]=true) --> B(writes flag[1]=true) --> B(writes victim=1) --> A(writes victim=2)

The state of the shared variables flag and victim will now allow B first access into the critical section despite A calling the lock method first, violating the FCFS principle and validating the need to analyze a doorway section.  

2)  Theoretically a filter lock could allow some threads to overtake others an arbitrary number of times because of how the doorway section is structured.  Any given thread that is stuck at a particular level must loop through all threads' levels checking to see if others are at a higher level while also checking if you are still the victim.  It is because in this read process it is possible to be passed up, despite it being your turn in a FCFS model, that some threads can continue getting passed up.  I'll use the following code -paraphrased from our homework- to elaborate:

```
filterLock(){
	me = threadID.get();
	for (all levels){
		level[me] = i
		victim[i] = me
		while(exists(me,i)){}

exists(me, i){
	for (all threads)
		if (thread ==me)
			continue
		if (level[other thread] >= i && victim[i] == me)
			return true
	return false
```
Using this example let's imagine thread 2 (out of 10) is waiting at level 1.  This would mean it's stuck at the while loop calling exist to see if it's still the victim and if others are at a higher level.  Let's suppose the thread 5, who is at level 9, finishes in the critical section and releases its level so all threads can move up, allowing thread 4 to make it to the top level.  Now imagine this order of events (the numbers in front correspond to threads):

4(read level[9] == 0) --> 4 enters CS --> 2(read victim[1] == 2]) --> 5(write victim [1] = 5) --> 4 finishes CS --> 4(write victim[1]==5) --> 5(read victim[1] != me) --> 5(write victim[2] = me) --> 2(read victim[1] != me) --> 2(write victim[2] = me)

This series of events shows thread 5 coming back from the critical section, starting at level 1, changing the victim variable, and then thread 4 doing the same thing before thread 2 ever realized it was not the victim.  This allowed thread 5 to overtake thread 2 in accessing level 2.  This overtake can happen an arbitrary number of times pending the system's scheduler, which could allow any number of threads to pass thread 2 before it reads it is not the victim, and then, whenever thread 2 does make it to the next level, this process can happen again where some number of threads get to overtake between the period of the victim variable being written by a new thread and some stuck thread reading victim.

**Problem 3**

For a three-threaded system using a T3 timestamp system, there are two states that are unordered:

1. Where all three threads occupy different nodes of the same subgraph. 
2. Each thread is on a different subgraph.
 
These two states bring about the same unordered condition since within a subgraph 0<1, 1<2, 2<0, and this same ordering also holds between subgraphs.  When deciding whether there is someone before them in order, each thread will continually find they are behind someone else.

In a single-threaded system, this can be prevented by employing a basic compare method; however, this breaks down in the concurrent case. Thus, my counterexample will be to prove that case X is possible in a concurrent system.

Suppose we have 3 threads: A, B, and C.  I'll identify thread location by using the following notation: A[00] = Thread A on Node 00.  I'll assume threads are using a system similar to the Bakery Algorithm, where each thread must set their flag to true and take the next highest token given the current order.  Then wait until they have the lowest number in the order.

We begin at the valid state: A[00], B[01], C[22].  The current ordering is C < A < B.  When C finishes and reads A and B's positions, it prepares to enter the lock again, obtaining a token at the back of the order.  This is where we'll begin our chain of events that leads to the unordered system:

C(writes flag[C] = true) --> C(reads max=[10]) -->  A(writes flag[A]=false) --> C(writes C[10]) -->A(writes flag[A]=true) --> A(reads max=[11]) --> A(writes A[11]) --> B(writes flag[B]=false) --> B(writes flag[B]=true) --> B(reads max=[20]) --> A(writes flag[A]=false) --> A(writes flag[A]=true) --> A(reads max=[02]) --> B(writes B[20]) --> A(writes A[02])

Though convoluted, this series of possible reads/writes would lead to a state where A is on subgraph 0, B is on subgraph 2, and C is on subgraph 1. Now as they loop over each other's positions, they each find that they have someone ahead of them in line because there is not a well-defined ordering.

	