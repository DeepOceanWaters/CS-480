CC = groovyc
CCFLAGS = -j
NAMES = Node.groovy Token.groovy AtomToken.groovy GroupStartToken.groovy GroupEndToken.groovy SimpleRegex.groovy SimpleRegexTest.groovy
SRCDIR = cs480
TARGETS = $(addprefix $(SRCDIR)/, $(NAMES))
TCC = groovy
TCCFLAGS = -cp 
TCCPATH = "dependencies/junit-4.12.jar:dependencies/spock-core-0.7-groovy-2.0.jar"
TCCTARG = $(TCCPATH) cs480/LexerTest

all: 
	compiler stutest.out

compiler:
    # $(shell $(CC) $(CCFLAGS) $(TARGETS))

stutest.out:
	echo test > stutest.out

test:
	$(shell $(TCC) $(TCCFLAGS) $(TCCTARGS))

clean:
	rm -rf stutest.out