mkdir -p bin
CLASSPATH=".:bin:lib/lucene-core-6.3.0.jar:lib/lucene-backward-codecs-6.3.0.jar:lib/lucene-analyzers-common-6.3.0.jar:lib/lucene-queryparser-6.3.0.jar:"

echo "Compiling Classes......"
javac -d bin src/Classes/*.java

echo "Compiling PreProcessData......"
javac -d bin -cp $CLASSPATH src/PreProcessData/*.java

echo "Compiling Indexing......"
javac -d bin -cp $CLASSPATH src/Indexing/*.java

echo "Compiling IndexingLucene......"
javac -d bin -cp $CLASSPATH src/IndexingLucene/*.java

echo "Compiling Search......"
javac -d bin -cp $CLASSPATH src/Search/*.java

echo "Compiling SearchLucene......"
javac -d bin -cp $CLASSPATH src/SearchLucene/*.java

echo "Compiling PseudoRFSearch......"
javac -d bin -cp $CLASSPATH src/PseudoRFSearch/*.java

echo "Compiling Main......"
javac -d bin -cp $CLASSPATH src/HW1Main.java
javac -d bin -cp $CLASSPATH src/HW2Main.java
javac -d bin -cp $CLASSPATH src/HW2MainLucene.java
javac -d bin -cp $CLASSPATH src/HW3Main.java
javac -d bin -cp $CLASSPATH src/HW3MainLucene.java
javac -d bin -cp $CLASSPATH src/HW4Main.java

echo "Compile completed!"
