function ret=sample()
addpath('/home/stuart006/Desktop/sfo');
load('lambdas.mat');
disp('displaying');
n=NMK(1);
m=NMK(2);
k=NMK(3);
counter=0;
set_stuff=[];
C={};
D={};
for i=1:n
    disp('i:'); disp(i);
    for u=1:m
        disp('u:'); disp(u);
        Biu=construct_Biu(i,u,n);
        if LAMBDAS(i,1,u)==-1
            continue;
        end
        obj=objective(i,u,LAMBDAS(i,:,u));
        vset=sfo_min_norm_point(obj,Biu);
        temp=size(vset);
        if(temp(2)>1)
            set_stuff=[set_stuff,temp(2)];
            counter=counter+1;
            C{counter}=mat2str(vset);
            opt=opt_prime(i,u);
            D{counter}=mat2str([i,u,opt(vset)]);
        end
    end
end
%newC=unique(C);
%ind = true(1,numel(C)); %// true indicates non-duplicate. Initiallization
%for ii = 1:numel(C)-1
%    for jj = ii+1:numel(C)
%        if isequal(C{ii}, C{jj})
%            ind(jj) = false; %// mark as duplicate
%        end
%   end
%end
%newC = C(ind);
fileID = fopen('/home/stuart006/Desktop/cpp_hello_world/violating_sets.txt','w');
temp=size(C);
for i=1:temp(2)
    fprintf(fileID,D{i});
    fprintf(fileID,C{i});
    fprintf(fileID,'\n');
end

fclose(fileID);

ret=0;

function Biu=construct_Biu(i,u,n)
Biu=[];
for j=1:n
    if and(l(j)<=c(u)-l(i),i~=j)
        Biu=[Biu,j];
    end
end