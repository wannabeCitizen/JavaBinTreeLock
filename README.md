#Peterson Lock Using Binary Tree

This is a project attempting to implement a 
Peterson Lock for concurrency using a Binary
Tree.  

##Peterson Lock
Peterson Locks are objects used for 2 threaded
systems to ensure one thread always waits while the
other is writing to a shared object/variable.  We
call this thread the 'victim.'  Peterson Locks, however,
are only good for 2 threaded systems, not *n*-threaded 
system.

As a basis to this project, you'll find a Peterson.java class
that is the standard implementation of a Peterson lock.
This will be used to create the Tree implementation.

##Peterson Tree Lock

The Peterson Tree Lock uses a "tree-type" heuristic
for solving the problem of *n*-threads, by making providing
levels of locks that the threads must pass through.  The lock
is only obtained by a thread by making it to level {0,1} 
(i.e., the root of the tree) and obtaining the that level's lock.

You'll find the Peterson Tree Lock in the PetersonTree.java
class file.  This implementation has been tested so far on 8 cores
and works fast (statistics to come).  

##Filter Lock

A Filter Lock is another implementation of a lock for an *n*-threaded
system.  It uses a similar locking system, except instead of using
the Peterson Lock as a backbone, it employs a multi-dimensional system
for keeping track of "victims" and must waste time on a for-loop to check
the progress of other threads.

This implementation is much slower, and can be used for comparison.
A Filter Lock implementation is found in the Filter.java class file.
