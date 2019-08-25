# ServerClient_Pi
A project in java with Server and Clients. Server uses his clients to calculate the 'Pi'.
 
At length, server wants as many clients as the cores of its pc is. (For example if the server runs from a pc with 4 cores, it will want 4 clients)
After that, every Client splits its job at some threads (with the same technique server does) and returns back to server its part for pi calculation.
The server after getting all the parts of 'Pi', it sum them all and gets the number of 'Pi'!!
