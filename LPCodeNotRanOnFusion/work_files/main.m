function A = main()
V_iw = 1:100;
F_iw = sfo_fn_iwata(length(V_iw));
disp(' ');
disp('---------------------------------------------------------');
disp('Now let''s explore general submodular function minimization using the min-norm-point algorithm');
disp(' ');
disp('Example: Iwata''s test function');
F = F_iw; V = V_iw;
disp('A = sfo_min_norm_point(F,V)')
pause
A = sfo_min_norm_point(F,V)
disp(' ')
pause

