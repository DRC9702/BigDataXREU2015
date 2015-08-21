In the processed Data, only look at sheet 6. This computer doesn't have excel on it so I can't delete all the other sheets.
Also, runtime for LP isn't actually 0, that code just didn't report it correctly.

Also, all the code assumes Cplex was installed in a folder called "CPLEXSTUFF". You must install the unrestricted version of cplex to run any big instances (more than 1000 constraints which is very easy to get to)