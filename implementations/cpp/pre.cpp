/**
 * This file is the lexer for my proto language, written in cpp.
 * 
 * (yes, yes, I know, I should have done it in my language, but I needed a
 * working demo)
 */
#include <iostream>
#include <fstream>
#include <string>
int main(int arg_num,char** arg_value) {
	//std::cout <<num<<std::endl<<value[num-1];
	std::string mainFileLocation;
	mainFileLocation=arg_value[1];

	std::ifstream mainFile;
	mainFile.open(mainFileLocation);

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
				hasImportantDataInThisLine=false;
				i+=2;
			}else if (insideMultiLineComment&&
				buffer[i]=='*'&&
				i+1<buffer.size()&&
				buffer[i+1]=='/') {
				insideMultiLineComment=false;
				i++;
				continue;
			}else if (insideMultiLineComment) continue;

			hasImportantDataInThisLine=buffer[i]!=' '||
				buffer[i]!='\t'||
				hasImportantDataInThisLine;
			
			line+=buffer[i];
		}

		if (hasImportantDataInThisLine)
			sweepedContent+=line+"\n";
	}
	std::cout<<sweepedContent;
}
