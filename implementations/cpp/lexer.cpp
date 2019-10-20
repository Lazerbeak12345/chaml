/**
 * This file is the temperary lexer for my proto language, written in cpp.
 * 
 * I don't plan on supporting syntax extentions in this implementation of the language
 */
#include <iostream>
#include <fstream>
#include <string>
int main(int arg_num,char** arg_value) {
	std::string mainFileLocation;
	mainFileLocation=arg_value[1];

	std::ifstream mainFile;
	mainFile.open(mainFileLocation.c_str());

	if (!mainFile) {
		std::cerr <<"Unable to open file \""<<mainFileLocation<<"\"."<<std::endl
			<<"\tMake sure that the filename is both correct, and points to a "
			<<"valid location."<<std::endl;
		return 1;
	}
}
