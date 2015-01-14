#--------------------------------------------
# INSTRUCTION
# Quoted strings are to be filled in by student
#
CC = gcc
CFLAGS = -c -Wall 
OBJS = compiler.out
SOURCE = compiler.in
RUNFLAGS = 

$(OBJS): $(SOURCE)
	$(CC) $(CFLAGS) -c $(SOURCE)

compiler: $(OBJS)
	$(CC) $(CFLAGS) -o compiler $(OBJS)

clean:
	rm -f compiler.out stutest.out core
	ls

stutest.out: 
	cat stutest1.in
	-compiler $(RUNFLAGS) stutest1.in > stutest1.out
	cat stutest1.out

proftest.out: compiler
	cat $(PROFTEST)
	compiler $(PROFTEST) > proftest.out
	cat proftest.out