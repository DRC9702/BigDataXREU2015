function result = test2()
fileID = fopen('test_out.txt','w');
fprintf(fileID,'testing\n');
fclose(fileID);
