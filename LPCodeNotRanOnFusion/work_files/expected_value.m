function F = expected_value(lambdas)
fn = @(S) sum(lambdas(S));
F = sfo_fn_wrapper(fn);