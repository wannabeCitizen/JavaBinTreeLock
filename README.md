#Peterson Lock Using Binary Tree

A Peterson Lock alone is only useful for two threads, so
this project implements a binary tree to hold a series of
Peterson locks in order to allow the Peterson lock to be useful
for *n* threads.  It will be compared with the speed of a Filter
lock.
