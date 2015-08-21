// -------------------------------------------------------------- -*- C++ -*-
// File: problem.cpp
// Version 12.6.2  
// --------------------------------------------------------------------------
//------------------------------------------------------------ */


#include <ilcplex/ilocplex.h>
#include <iostream>
#include <fstream>
#include <algorithm>    // std::random_shuffle
#include <stdio.h>
#include <string.h> /* For strcmp() */
#include <stdlib.h> /* For EXIT_FAILURE, EXIT_SUCCESS */
#include <cstdlib>
#include <sstream>
#include <vector> /* For STL */
#include "mat.h"
#include <ctime>
 
#define BUFSIZE 256
#define NUM_VSET -1

typedef IloArray<IloNumVarArray> NumVarMatrix;
typedef IloArray<NumVarMatrix>   NumVar3Matrix;
typedef IloArray<NumVar3Matrix>   NumVar4Matrix;
#define  TOTAL_ELEMENTS (FIRST_DIM * SECOND_DIM * THIRD_DIM)
typedef IloArray<IloNumArray> NumMatrix;

int offset(int x,int y,int z);
bool notin(int rand_i,int *used,int num_used);
double myrandom(int i);
int *round(IloCplex cplexL);
int read_in(IloModel model,IloEnv env);
int add_variables(IloModel model,IloEnv env);
int parse_data(IloEnv env,IloModel model,char *filename);
int output_lc(IloEnv env,IloModel model);
int calc_obj(int *f,IloCplex cplex);
int write_out(IloCplex cplex,IloEnv env); 
void exec(char* cmd);
bool violate_capacity(int *f,IloCplex cplex);
bool valid(int *f,IloCplex cplex);
mxArray *pa, *paMNK, *paLAMBDA;
MATFile *pmat;
 /*   NumVarMatrix a(env, n);  
    NumVarMatrix delta(env, n);    
    NumVar4Matrix t(env,n);
    NumVar3Matrix gamma(env,n); */


ILOSTLBEGIN

NumVarMatrix a;
NumVarMatrix delta;   
NumVar4Matrix t;
IloInt n, m, k;
IloNumArray c,l,k_array;
NumMatrix w,d;
IloEnv env;
IloModel model;
IloCplex cplex;

int
main(int numargs, char** myargs)
{
      double time1=std::time(0);
      char *data_filename;
      if(numargs==2)
      {
              data_filename=myargs[1];
      }
      else
      {
              data_filename=(char *) "data2.txt";
      }

      const char *file = "/home/stuart006/MATBIN2/lambdas.mat";
      string df=(string) data_filename;
      string temp="LPOut" + df.substr(2);
      const char *result_filename=temp.c_str();
      cout << result_filename << '\n';
      char str[BUFSIZE];
      int status; 
      int i,j,x,y,u,v=0;
      string line("           "); 
      std:string s0;
      s0 = "/home/stuart006/MATBIN2/matlab -nodesktop -nodisplay -nosplash -r \"run('/home/stuart006/MATBIN2/sample.m');exit;\"";
      char *s1;
      s1 = (char *)malloc(s0.size() + 1);
      memcpy(s1, s0.c_str(), s0.size() + 1);

      model=IloModel(env);
   
      if(parse_data(env,model,data_filename)==-1)
      {    
            return -1;
      }

      if(output_lc(env,model)==-1)
      {
            return -1;
      }
     
      j=0;
      add_variables(model,env);

      int ii=0;
      int used[100] = {0};
      int num_used=0;
      std::srand(std::time(0)); // use current time as seed for random generator
      int cost_current=0;
      int cost_prev=-1;
      
      int *f;
      int *opt_f;
      int *old_f;
      int opt_val;
      int max_opt_val;
      int old_max_opt_val;
      //temp
      try{
      cplex=IloCplex(model);    
                  cplex.setOut(env.getNullStream());
            cplex.setWarning(env.getNullStream());
            //cplex.setParam(cplex);
      cplex.solve();
      }
      catch(...)
      {
        env.out() << "goshfrickendarnit\n";
      }
      if (cplex.getStatus() == IloAlgorithm::Infeasible)
      { env.out() << "no response" << endl;  return 1; }
             

      opt_f=round(cplex);

      //end temp
      
      while(ii<n)
      {
            cplex=IloCplex(model);
            cplex.setOut(env.getNullStream());
            cplex.setWarning(env.getNullStream());
      
            //solution at ii=1
            try{
            cplex.solve();  f=round(cplex); 
            }
            catch(...)
            {
              cout << "breaking while loop (1)";
              break;
            }
            if (cplex.getStatus() == IloAlgorithm::Infeasible)
                     { env.out() << "breaking while loop (2)" << endl;  break; }
             
             
           // cout << cplex.getValue(a[3][2]) << '\n';
            //cout << cplex.getValue(a[4][0]) << '\n';
           // cout << cplex.getValue(a[5][2]) << '\n';

            env.out() << "Solution status: " << cplex.getStatus() << endl;
            
            cost_prev=cost_current;
            cost_current=cplex.getObjValue();
            env.out() << "cost:" << cost_current << '\n';
                        
            //env.out() << "Cost:" << cost_current << endl;
            //write out results, execute, read in/add constraint
                       
            //cout << ii;
            opt_f=f; 
            opt_val=calc_obj(f,cplex);
            max_opt_val=opt_val;

            for(i=0;i<n;i++)
            {   
                f=round(cplex);
                opt_val=calc_obj(f,cplex);  
                if(!violate_capacity(f,cplex) && opt_val<=max_opt_val)
                {
                          opt_f=f;
                max_opt_val=opt_val;
                }
            }
            
            if(violate_capacity(f,cplex))
            {
              break;
            }
            
            if(write_out(cplex,env)==-1)
            { cout << "thing 1"; break; } 
            exec(s1);

            if(read_in(model,env)==-1)
            { cout << "thing 2"; break; }

            ii+=1; 
            if(cost_prev==cost_current)
            { cout << "thing 3"; break; }
  
            //cout << ii;
   }  
           
  /* if(ii==0)
   {
     cout << "sorry, can't do it";
     exit(1);
   }*/
   //end while
   //cout << ii << '\n';
   //Print into results file:  

   ofstream result_file;    
   result_file.open(result_filename);
   result_file << "Minimum Cost: " << calc_obj(opt_f,cplex) << '\n';      
   result_file << "Labels: ";

   for(i=0;i<n;i++)
     {
        result_file << i;
        result_file << "->";
        result_file << opt_f[i];
        result_file << "   ";
     }

    double time2=std::time(0);
    //env.out() << time2;

    result_file << "\nRuntime: " << time2-time1 << '\n';
    if(valid(opt_f,cplex))
    {
      result_file << "Violate capacities: Yes\n";
    }
    else{
      result_file << "Violate capacities: No\n";
    }


   //Cleanup
   if (matClose(pmat) != 0) {
          printf("Error closing file %s\n",file);
          return -1;
          //return(EXIT_FAILURE);
   }
   free(s1);
   
   env.end();
   return 0;
}

bool valid(int *f,IloCplex cplex)
{
  int i=0;
  if(violate_capacity(f,cplex))
  {
    return false;
  }
  for(i=0;i<n;i++)
    {
      if(f[i]==-1)
      {
        return false;
      }
    }
    return true;
}


int calc_obj(int *f,IloCplex cplex)
{
  int i=0,j=0,sum=0;
  for(i=0;i<n;i++)
    {
      for(j=i+1;j<n;j++)
        {

          if(f[i]==-1 || f[j]==-1)
          {         
            //cout << "two\n";
            continue;
          }         
          //cout << i << " " << f[i] << '\n';
          //cout << j << " " << f[j] << '\n';
          sum+=w[i][j]*d[f[i]][f[j]];
        }
    }
    return sum; //because of symmetry
}

int output_lc(IloEnv env,IloModel model)
{
   int i=0,j=0,x=0,y=0;
   ofstream loadfile;
    loadfile.open ("/home/stuart006/MATBIN2/l.m");     
      
    loadfile << "function ret = l(vm)\n";

    for(i=0;i<n;i++)
      {
        ostringstream convert;
        ostringstream convert2;
        
        convert << (i+1);
        string result1=convert.str();
        convert2 << l[i]; 
        string result2=convert2.str();
        if(i==0)
        {
        loadfile << "if vm==" << result1 << "\n";
        }
        else{
        loadfile << "elseif vm==" << result1 << "\n";
        }

        loadfile << "ret=" << result2 << ";\n";
      }
      loadfile << "end";
          loadfile.close();
          
    ofstream capfile;      
    capfile.open ("/home/stuart006/MATBIN2/c.m");     
      
    capfile << "function ret = c(pm)\n";

    for(x=0;x<m;x++)
      {
        ostringstream convert;
        ostringstream convert2;
        
        convert << (x+1);
        string result1=convert.str();
        
        convert2 << c[x];
        string result2=convert2.str();
        if(x==0)
        {
        capfile << "if pm==" << result1 << "\n";
        }
        else{
        capfile << "elseif pm==" << result1 << "\n";
        }

        capfile << "ret=" << result2 << ";\n";
      }
      capfile << "end";
          capfile.close();          
}

int parse_data(IloEnv env,IloModel model,char *filename)
{
   const char *file = "/home/stuart006/MATBIN2/lambdas.mat";
   const size_t ndim = 3, ddd[1]={3};
   int i=0,j=0,x=0,y=0,u=0,h=0;

  string line("           ");
  ifstream myfile (filename);

	  getline(myfile,line);
	  n=atoi(line.c_str());
	  l=IloNumArray(env,n);
	  w=NumMatrix(env,n);
        
    for(i=0; i<n; i++)
    {
      w[i]=IloNumArray(env,n);
    }
	  
	  getline (myfile,line);
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
   getline (myfile,line);
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
	  getline (myfile,line);
	  m=atoi( line.c_str() );

    c=IloNumArray(env,m);
	  d=NumMatrix(env,m);
    
    for(i=0; i<m; i++)
    {
      d[i]=IloNumArray(env,m);
    } 
		  
  i=0;
  j=0;    
	getline (myfile,line);
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
  getline (myfile,line);
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
    getline (myfile,line);
	  k=atoi( line.c_str() );
    
      //Open file

    pmat = matOpen(file, "w");
      int NMK[3]={n,m,k};
    if (pmat == NULL) {
    printf("Error creating file %s\n", file);
    printf("(Do you have write permission in this directory?)\n");
    return -1;//(EXIT_FAILURE);
  }
  paMNK = mxCreateNumericArray(1, ddd, mxINT32_CLASS, mxREAL);
  //Error check?
  
    //Error checking
    if (paMNK == NULL) {
       printf("%s : Out of memory on line %d\n", __FILE__, __LINE__); 
       printf("Unable to create mxArray.\n");
       return -1;//(EXIT_FAILURE);
    }

  //Copy and write data
  memcpy((void *)(mxGetPr(paMNK)), (void *)NMK, sizeof(NMK)); 

  int status = matPutVariable(pmat, "NMK", paMNK);
    //Error checking
    if (status != 0) {
       printf("%s :  Error using matPutVariable on line %d\n", __FILE__, __LINE__);
       return -1;//(EXIT_FAILURE);
    } 
  /* clean up and close*/


    getline (myfile,line);
    getline (myfile,line);
	  istringstream iss5(line);
    i=0;
    j=0;

    k_array=IloNumArray(env,n);
	  while (iss5 && j<=n) 
	  { 
		  string subs; 
		  iss5 >> subs; 
		  u=atoi(subs.c_str());

		  if(subs.compare("")==0)
		  {
			  continue;
		  }
              k_array[j]=u;      
      //cout << k_array[j] << '\n';


		     j++;
	  }
   
    
    myfile.close();  
  mxDestroyArray(paMNK); 
  return 0;
  
}

int write_out(IloCplex cplex,IloEnv env)
{
        int i=0,x=0,j=0;
        double aix=0;
        const size_t dims[3] = {n,n,m};
        size_t sizeL=sizeof(double)*n*n*m;
        double *LAMBDA=(double *)malloc(sizeL);
         
          for(i=0;i<n;i++)
            {
              for(x=0;x<m;x++)
                    {
                     aix=cplex.getValue(a[i][x]);
                    for(j=0;j<n;j++)
                    {
                      if(aix!=0)
                      {
                         LAMBDA[offset(i,j,x)]=(double) cplex.getValue(t[i][j][x][x])/aix;
                      }
                      else{
                         LAMBDA[offset(i,j,x)]=-1;
                      }
                    }
                }
            }       
          paLAMBDA = mxCreateNumericArray(3, dims, mxDOUBLE_CLASS, mxREAL);
        //Error check?

       //Error checking
       if (paLAMBDA == NULL) {
             printf("%s : Out of memory on line %d\n", __FILE__, __LINE__); 
             printf("Unable to create mxArray.\n");
             return -1;//(EXIT_FAILURE);
       }

       //Copy and write data
       memcpy((void *)(mxGetPr(paLAMBDA)), (void *)LAMBDA, sizeL); 

       int status = matPutVariable(pmat, "LAMBDAS", paLAMBDA);
       //Error checking
       if (status != 0) {
           printf("%s :  Error using matPutVariable on line %d\n", __FILE__, __LINE__);
           return -1;//(EXIT_FAILURE);
       } 
      /* clean up and close*/
      mxDestroyArray(paLAMBDA);
      return 0;
}




int offset(int x,int y,int z)
{
  return ( z * n * n ) + ( y * n ) + x;
}

bool notin(int rand_i,int *used,int num_used)
{

  int i=0;
  for(i;i<num_used;i++)
    {
      if(used[i]==rand_i)
      {
        
        return false;
      }
    }
    return true;
}

double myrandom(int i)
{
  return rand()/(double) RAND_MAX;
}

int *round(IloCplex cplexL)
{
        double alpha=0;
        int i,j,u,v=0;
        std::vector<int> terminals;
        
        for(int i=0;i<m;i++)
          {
            terminals.push_back(i);
          }
        
        std::random_shuffle(terminals.begin(),terminals.end());
        
             
        alpha=myrandom(2)+1;

        int *f = (int *) malloc(n*sizeof(int));
        double *A = (double *) malloc(n*sizeof(double));
        int temp,max_terminal=0;
        double max_val=0;
        
        //Initialize A
        for(u=0;u<n;u++)
          {
            max_val=cplexL.getValue(a[u][0]);
            max_terminal=0;
            for(i=1;i<m;i++)
              {
                temp=cplexL.getValue(a[u][i]);
                if(temp>=max_val)
                {
                  max_val=temp;
                  max_terminal=i;
                }
              }
             A[u]=max_val;
          }
           
        //Initialize f
        for(i=0;i<n;i++)
          {
            f[i]=-1;  
          }

        //Generate assignment
        for(j=0;j<m;j++)
          {
            for(u=0;u<=n;u++)
            { 
              if(f[u]==-1)
              {
                //cout << cplexL.getValue(a[u][terminals[j]]) << " " << A[u] << " " << A[u]/alpha << '\n';
                if(cplexL.getValue(a[u][terminals[j]])>=A[u]/alpha)
                {
                  f[u]=terminals[j];
                  if(violate_capacity(f,cplex))
                  {
                    f[u]=-1;//-1;
                    alpha=(2-alpha)/2+alpha;
                  }
                }
              }
            }
          }
          free(A);

          return f;
}

bool violate_capacity(int *f,IloCplex cplex)
{
  int j=0,temp=0,x=0;
      for(x=0;x<m;x++)
        {
             temp=0;
             for(j=0;j<n;j++)
               {
                 if(f[j]==x)
                 {
                 temp+=l[j];
                 }
               }
               if(temp>c[x])
               {
                 return true;
               }
        }
   return false;  
}

int read_in(IloModel model,IloEnv env)
{
           string subs,tempii,delimiter;
           delimiter="]";
           int num_S = 100;
           int lenS=0,si=0,sx=0,i=0,j=0,optS=0;
           int tempiii=0;
           string line("           ");
           ifstream file2 ("violating_sets.txt");

           while( getline (file2,line) && tempiii < n)
            {
              tempiii+=1;
                    lenS=0;
                    int *S=(int *) calloc(num_S,sizeof(int));

                    tempii = line.substr(1, line.find(delimiter));
	                  istringstream iss11(tempii);
                       
                    iss11 >> subs;
                    si=atoi(subs.c_str())-1;

                    iss11 >> subs;
                    sx=atoi(subs.c_str())-1;
                   
                    iss11 >> subs;
                    optS=atoi(subs.c_str());

                  tempii = line.substr(line.find(delimiter)+2, sizeof(line)+1);
                  istringstream iss12(tempii);
                  i=0;
		              while (iss12) 
		               { 
			             string subs; 
			             iss12 >> subs; 
			             string temp=subs;
			             if(temp.compare("")==0)
                   {
                     continue;
			             }
			             else
			             {
                        S[i]=atoi(subs.c_str())-1;
                        lenS=lenS+1;
                        i=i+1;
			             }
                   if(i>=num_S-5)
                   {
                       num_S+=100;
                       S=(int *) realloc(S,num_S * sizeof(int));
                   }
	                 }
                  //ADD CONSTRAINT
                  IloExpr james(env);
                  for(j=0;j<lenS;j++)
                    {
                       //cout << "si:" << si << "s[j]:" << S[j] << "sx:" << sx << '\n';
                       james-=t[si][S[j]][sx][sx];
                    }
                    model.add(james>=0);                                
                    free(S);
                    //cout << tempiii << '\n'; 
            }
            file2.close(); 
            return 0;
            
}

int add_variables(IloModel model,IloEnv env)
{
   int i,j,x,y,u,v,h=0;
   
    a=NumVarMatrix(env, n);  
    delta=NumVarMatrix(env, n);    
    t=NumVar4Matrix(env,n);

    /* Initilize a*/

    for(i=0; i<n; i++)
    {
           a[i]=IloNumVarArray(env,m);
           for(x=0; x<m; x++)
           {
               a[i][x]=IloNumVar(env,0.0,1.0);
           }
     }
    

    /* Initialize t*/
    for(i=0; i< n; i++) {
        t[i] = NumVar3Matrix(env, n);
         for(j=0; j< n; j++) {
            t[i][j] = NumVarMatrix(env, m);
            for(x=0; x<m; x++) {
                t[i][j][x] = IloNumVarArray(env, m);
                for(y=0;y<m;y++){
                     t[i][j][x][y] = IloNumVar(env, 0.0, 1.0);
                     model.add(t[i][j][x][y]);
                 }
             }
         }
     }
     
    /* Initialize delta*/
    for(i=0; i< n; i++) {
        delta[i] = IloNumVarArray(env, n);
         for(j=0; j< n; j++) {
               delta[i][j] = IloNumVar(env, 0.0);
         }
    }
    

  /* Initialize gamma*/
         /*for(i=0; i< n; i++) {
            gamma[i] = NumVarMatrix(env, n);
            for(j=0; j<n; j++) {
                gamma[i][j] = IloNumVarArray(env, m);
                for(x=0;x<m;x++){
                     gamma[i][j][x] = IloIntVar(env, 0.0, 1.0);
                 }
             }
         }
     */
     

 /* make objective function */

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
          //Done during initialization
      //constraint 2
          //Done during initialization
          
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
      /*qqq
      for(i=0;i<n;i++)
        {
          for(j=i+1;j<n;j++)
            {
                 IloExpr temp(env);
                 for(x=0;x<m;x++)
                   {
                     for(y=0;y<m;y++)
                       {
                         temp+=d[x][y]*t[i][j][x][y];
                       }
                   }
                 model.add(delta[i][j]==delta[j][i]);
                 model.add(delta[i][j]==temp);
            }
        }*/
      
      
      //constraint 5
      /*qqq
      for(i=0;i<n;i++)
        {
          for(j=0;j<n;j++)
            {
              if(i==j)
              {
                continue;
              }
              for(h=0;h<n;h++)
                {
                  if(i==h || j==h)
                  {
                    continue;
                  }
                  model.add(delta[i][j]+delta[j][h]>=delta[i][h]);
                }
            }
        }*/

      //constraint 6
      
      //SKIP, IMPLIED BY OTHER CONSTRAINTS
      
      
      //constraint 7
      
      for(x=0;x<m;x++)
        {
          for(i=0;i<n;i++)
            {
              for(j=i+1;j<n;j++)
                {
                  model.add(t[i][j][x][x]==t[j][i][x][x]);
                }
            }
        }
      

      //constraint 8
        for(i=0;i<n;i++)
        {
          for(j=i+1;j<n;j++)
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
      

      //constraint 9 
        for(i=0;i<n;i++)
        {
          for(j=i+1;j<n;j++)
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
        
      //constraint 10
      
      for(x=0;x<m;x++)
        {

             IloExpr temp(env);
             for(i=0;i<n;i++)
               {
                 temp+=l[i]*a[i][x];

               }
               model.add(temp<=c[x]);
        }     

      //constraint 11
         // OMIT, ALONG WITH OTHER GAMMA STUFF
         
      //constraint 12
         // OMIT, ALONG WITH OTHER GAMMA STUFF
         
         
      //constraint 13
      /*qqq
      for(x=0;x<m;x++)
      {
      for(i=0;i<n;i++)
        {
          for(j=0;j<n;j++)
            {
              if(i==j)
              {
                continue;
              }
              for(h=0;h<n;h++)
                {
                  if(i==h || j==h)
                  {
                    continue;
                  }
                  model.add(t[i][j][x][x]-t[j][h][x][x]<=a[i][x]-t[i][h][x][x]);
                }
            }
        }
        }*/
        
        
     //constraint 14
     for(x=0;x<m;x++)
       {
         for(i=0;i<n;i++)
           {
             for(j=0;j<n;j++)
               {
                 if(l[i]+l[j]<=c[x])
                 {
                   continue;
                 }
                 else
                 {
                   model.add(a[i][x]-t[i][j][x][x]>=a[i][u]);
                 }
               }
           }
       }


       // constraint 15

       for(i=0;i<n;i++)
         {
           if(i>=k)
           {

             model.add(a[i][k_array[i]]==1);
           }
         }
                            return 0;

}

void exec(char* cmd) {
    FILE* pipe = popen(cmd, "r");
    if (!pipe) return;
    char buffer[128];
    std::string result = "";
    while(!feof(pipe)) {
    	if(fgets(buffer, 128, pipe) != NULL)
    		result += buffer;
    }
    pclose(pipe);
    return;
}
