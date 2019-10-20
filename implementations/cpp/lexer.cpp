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

	std::string buffer;
	std::string sweepedContent;
	bool insideMultiLineComment=false;
	while(getline(mainFile,buffer)){
		bool hasImportantDataInThisLine=false;

		if (buffer[0]=='#'&&
			1<buffer.size()&&
			buffer[1]=='!') continue;//shabang

		std::string line;

		for (int i=0;i<buffer.size();++i) {

			if (buffer[i]=='/'&&
				i+1<buffer.size()&&
				buffer[i+1]=='/') {//single-line comments
				break;
			}
			
			if(!insideMultiLineComment&&//multi line comments
				buffer[i]=='/'&&
				i+1<buffer.size()&&
				buffer[i+1]=='*') {
				insideMultiLineComment=true;
				i+=2;//Skip both chars
				std::cout<<"COMMENT:\""<<buffer[i]<<"\"\n";
			}
			if (insideMultiLineComment&&
				buffer[i]=='*'&&
				i+1<buffer.size()&&
				buffer[i+1]=='/') {
				insideMultiLineComment=false;
				i++;
				std::cout<<"COMMENT:\""<<buffer[i]<<"\"\n";
			}
			if (insideMultiLineComment) continue;

			hasImportantDataInThisLine=buffer[i]!=' '||
				buffer[i]!='\t'||
				hasImportantDataInThisLine;//If it only has whitespace, it's removed
			
			line+=buffer[i];
		}

		if (hasImportantDataInThisLine)
			sweepedContent+=line+"\n";
	}
	std::cout<<sweepedContent;
}
