function F = opt_prime(i,u)
T = @(S_sorted) fractional_knapsack(S_sorted,u,i);
F = sfo_fn_wrapper(T);

function value = fractional_knapsack(S,u,i)
S_sorted=sort(arrayfun(@(x) l(x),S));
remaining=c(u)-l(i);
value=0;
for i=1:length(S_sorted)
if S_sorted(i)<=remaining
    remaining=remaining-S_sorted(i);
    value=value+1;
else
    value=value+remaining/S_sorted(i);
    remaining=0;
end
if remaining==0
    break
end
end