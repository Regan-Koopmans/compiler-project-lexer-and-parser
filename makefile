make:
	@echo "Compiling package: lexer"
	javac -cp . compiler/lexer/*.java -Xlint:unchecked
	@echo "Done!"
	@echo "Compiling package: parser"
	javac -cp . compiler/parser/*.java -Xlint:unchecked
	@echo "Done!"
	@echo "Compiling Main"
	javac *.java -Xlint:unchecked
	@echo "Done!"


test:
	java Main test.txt


clean:
	rm *.class
