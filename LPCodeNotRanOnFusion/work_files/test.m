function F = expected_value(lambdas)
fn = @(S) sum_over(S,lambdas);
F = sfo_fn_wrapper(fn);

function ret = sum_over(indices,list)
ret=0;
for i=1:length(indices)
ret=ret+list(indices(i));
end