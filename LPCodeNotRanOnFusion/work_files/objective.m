function F = objective(i,u,lambdas);
E=expected_value(lambdas);
opt=opt_prime(i,u);
fn = @(S) opt(S)-E(S);
F = sfo_fn_wrapper(fn);