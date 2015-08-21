// -------------------------------------------------------------- -*- C++ -*-
// File: problem.cpp
// Version 12.6.2  
// --------------------------------------------------------------------------
//------------------------------------------------------------ */


#include <ilcplex/ilocplex.h>
#include <iostream>
#include <fstream>
#include <string>
#include <cstdlib>
#include <sstream>

typedef IloArray<IloNumVarArray> NumVarMatrix;
typedef IloArray<NumVarMatrix>   NumVar3Matrix;
typedef IloArray<NumVar3Matrix>   NumVar4Matrix;

typedef IloArray<IloNumArray> NumMatrix;

ILOSTLBEGIN

IloInt n, m;
IloNumArray c,l;
NumMatrix w,d;

void define_data(IloEnv env) {
  string line("           ");
  ifstream myfile ("/home/DavidRC/data.txt");
  int i=0;
  int j=0;
  int x=0;
  int y=0;

  if (myfile.is_open())
  {
    i=0;
	  j=0;
    
	  getline(myfile,line);
	  n=atoi(line.c_str());
	  IloNumArray l(env,n);
	  NumMatrix w(env,n);
    
    for(i=0; i<n; i++)
    {
      w[i]=IloNumArray(env,n);
    }
	  
	  getline (myfile,line);
    
	  istringstream iss1(line);  
	  	while (iss1 && j<n) 
		{ 
			string subs; 
			iss1 >> subs; 
			string temp=subs;
			if(temp.compare("")==0)
			{
				continue;
			}
			else
			{

            l[j]=atoi(subs.c_str());
			}
			j++;
	    }
     exit(1);

     i=0;
	 j=0;
	 while ( getline (myfile,line) && i<n)
     {
	    istringstream iss2(line);
          j=0;

		while (iss2 && j<n) 
		{ 
			string subs; 
			iss2 >> subs; 
			string temp=subs;

			if(temp.compare("")==0)
			{
				continue;
                        j++;
			}
			else
			{

            w[i][j]=atoi(subs.c_str());
            			j++;
			}
	    }
		i++;
    }

	  m=atoi( line.c_str() );

    IloNumArray c(env,m);
	  NumMatrix d(env,m);
    
    for(i=0; i<m; i++)
    {
      d[i]=IloNumArray(env,m);
    } 
		  
  i=0;
  j=0;    
	getline (myfile,line);
	istringstream iss3(line);
	while (iss3 && j<m) 
	{ 
		string subs; 
		iss3 >> subs; 
		string temp=subs;
		if(temp.compare("")==0)
		{
			continue;
		}
		else
		{
           c[j]=atoi(subs.c_str());
		}
		j++;
	}
		
	i=0;
	j=0;
	while ( getline (myfile,line) && i<m)
    {
	    istringstream iss4(line);
          j=0;
		while (iss4 && j<m) 
		{ 
			string subs; 
			iss4 >> subs; 
			string temp=subs;
			if(temp.compare("")==0)
			{
				continue;
			}
			else
			{
            d[i][j]=atoi(subs.c_str());
			}
			j++;
	    }
		i++;
    }
    myfile.close();
  }

  else cout << "Unable to open file"; 

}

int
main(int, char**)
{

   IloEnv env;
   int i=0;
   int j=0;
   int x=0;
   int y=0;
   try {

  string line("           ");
  ifstream myfile ("data.txt");


    i=0;
	  j=0;
    
	  getline(myfile,line);
	  n=atoi(line.c_str());
	  IloNumArray l(env,n);
	  NumMatrix w(env,n);
    
    for(i=0; i<n; i++)
    {
      w[i]=IloNumArray(env,n);
    }
	  
	  getline (myfile,line);
    
	  istringstream iss1(line);  
	  	while (iss1 && j<n) 
		{ 
			string subs; 
			iss1 >> subs; 
			string temp=subs;
			if(temp.compare("")==0)
			{
				continue;
			}
			else
			{

            l[j]=atoi(subs.c_str());
			}
			j++;
	    }


     i=0;
	 j=0;
	 while ( getline (myfile,line) && i<n)
     {
	    istringstream iss2(line);
          j=0;

		while (iss2 && j<n) 
		{ 
			string subs; 
			iss2 >> subs; 
			string temp=subs;

			if(temp.compare("")==0)
			{
				continue;
                        j++;
			}
			else
			{

            w[i][j]=atoi(subs.c_str());
            			j++;
			}
	    }
		i++;
    }

	  m=atoi( line.c_str() );

    IloNumArray c(env,m);
	  NumMatrix d(env,m);
    
    for(i=0; i<m; i++)
    {
      d[i]=IloNumArray(env,m);
    } 
		  
  i=0;
  j=0;    
	getline (myfile,line);
	istringstream iss3(line);
	while (iss3 && j<m) 
	{ 
		string subs; 
		iss3 >> subs; 
		string temp=subs;
		if(temp.compare("")==0)
		{
			continue;
		}
		else
		{
           c[j]=atoi(subs.c_str());
		}
		j++;
	}
		
	i=0;
	j=0;
	while ( getline (myfile,line) && i<m)
    {
	    istringstream iss4(line);
          j=0;
		while (iss4 && j<m) 
		{ 
			string subs; 
			iss4 >> subs; 
			string temp=subs;
			if(temp.compare("")==0)
			{
				continue;
			}
			else
			{
            d[i][j]=atoi(subs.c_str());
			}
			j++;
	    }
		i++;
    }
    myfile.close();

      IloModel model(env);
      NumVarMatrix a(env, n);
      NumVar4Matrix t(env,n);
      IloNumVar tempi;
      
      for(i=0; i<n; i++)
        {
          a[i]=IloNumVarArray(env,m);
           for(j=0; j<m; j++)
           {
             a[i][j]=IloIntVar(env,0.0,1.0);
          }
        }

    for(i=0; i< n; i++) {
        t[i] = NumVar3Matrix(env, n);
         for(j=0; j< n; j++) {
            t[i][j] = NumVarMatrix(env, m);
            for(x=0; x<m; x++) {
                t[i][j][x] = IloNumVarArray(env, m);
                for(y=0;y<m;y++){
                     t[i][j][x][y] = IloIntVar(env, 0.0, 1.0);
                 }
             }
         }
     }

 /* add a constraint */

      IloExpr sc(env);
      for(i=0;i<n;i++)
        {
          for(j=i+1;j<n;j++)
            {
              for(x=0;x<m;x++)
                {
                  for(y=0;y<m;y++)
                    {
                      
                     sc+=w[i][j]*d[x][y]*t[i][j][x][y];
                    }
                }
            }
        }

      // Objective Function: Minimize Cost   
      model.add(IloMinimize(env,sc));

      //constraint 1
      
      //constraint 2

      //constraint 3

      for(i=0;i<n;i++)
        {
          IloExpr temp(env);
          for(x=0;x<m;x++)
            {
                temp+=a[i][x];
            }
            model.add(temp==1);
        }

      
      //constraint 4
      for(i=0;i<n;i++)
        {
          for(j=0;j<n;j++)
            {
              IloExpr temp(env);
              for(x=0;x<m;x++)
                {
                  for(y=0;y<m;y++)
                    {
                      temp+=t[i][j][x][y];
                    }
                }
                model.add(temp==1);
            }
        }

      //constraint 5 
        for(i=0;i<n;i++)
        {
          for(j=0;j<n;j++)
            {
              for(x=0;x<m;x++)
                {
                  IloExpr temp(env);
                  for(y=0;y<m;y++)
                    {
                      temp+=t[i][j][x][y];
                    }
                  model.add(temp>=a[i][x]);
                }
            }
        }
      

      //constraint 6  
        for(i=0;i<n;i++)
        {
          for(j=0;j<n;j++)
            {
              for(y=0;y<m;y++)
                {
                  IloExpr temp(env);
                  for(x=0;x<m;x++)
                    {
                      temp+=t[i][j][x][y];
                    }
                  model.add(temp<=a[j][y]);
                }
            }
        }
        
      //constraint 7
      
      for(x=0;x<m;x++)
        {

             IloExpr temp(env);
             for(i=0;i<n;i++)
               {
                 temp+=l[i]*a[i][x];

               }
               model.add(temp<=c[x]);
        }     
  


      // Optimize

      IloCplex cplex(model);
      cplex.setOut(env.getNullStream());
      cplex.setWarning(env.getNullStream());
      cplex.solve();

      if (cplex.getStatus() == IloAlgorithm::Infeasible)
         env.out() << "No Solution" << endl;

      env.out() << "Solution status: " << cplex.getStatus() << endl;
      // Print results
      env.out() << "Cost:" << cplex.getObjValue() << endl;
      env.out() << "test\n";
      for(i=0;i<n;i++)
        {
          for(x=0;x<m;x++)
            {
              if(cplex.getValue(a[i][x])!=0)
              {
                cout <<  i << ":" << x  << '\n';
              }
            }
            cout << '\n';
        }
   
   }
   catch (IloException& ex) {
      cerr << "Error: " << ex << endl;
   }
   catch (...) {
      cerr << "Error" << endl;
   }
   env.end();
   return 0;
}
