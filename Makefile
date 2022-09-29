JFLAGS = -g
JC = javac

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
		  AllocMain.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class $(RM) */*.class

build: \
    classes

run:
	AllocMain main

