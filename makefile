make:
	@echo "Compiling package: lexer"
	javac -cp . compiler/lexer/*.java
	@echo "Done!"
	@echo "Compiling package: parser"
	javac -cp . compiler/parser/*.java
	@echo "Done!"
	@echo "Compiling Main"
	javac *.java
	@echo "Done!"


test:
	java Main test.txt


clean:
	rm *.class
