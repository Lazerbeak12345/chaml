/**
 * This file is the temperary lexer for my proto language, written in cpp.
 * 
 * I don't plan on supporting syntax extentions in this implementation of the language
 */
#include <iostream>
#include <fstream>
#include <string>
#include <vector>

enum PossibleTokens {
	WHITESPACE,

	SINGLELINE_COMMENT,
	OPEN_MULTILINE_COMMENT,CLOSE_MULTILINE_COMMENT,

	OPEN_PAREN,CLOSE_PAREN,
	OPEN_CURLY,CLOSE_CURLY,
	OPEN_SQUARE,CLOSE_SQUARE,

	LAMDA,
	SET,
	SEMICOLON,
	COMMA,
	SUB_ITEM,
	STRING,
	CHAR,
	NUMBER,

	IDENTIFIER,
};

std::string getTokenAsStr(PossibleTokens token) {
	switch(token) {
		case WHITESPACE: return "WHITESPACE";
		case SINGLELINE_COMMENT: return "SINGLELINE_COMMENT";
		case OPEN_MULTILINE_COMMENT: return "OPEN_MULTILINE_COMMENT";
		case CLOSE_MULTILINE_COMMENT: return "CLOSE_MULTILINE_COMMENT";
		case OPEN_PAREN: return "OPEN_PAREN";
		case CLOSE_PAREN: return "CLOSE_PAREN";
		case OPEN_CURLY: return "OPEN_CURLY";
		case CLOSE_CURLY: return "CLOSE_CURLY";
		case OPEN_SQUARE: return "OPEN_SQUARE";
		case CLOSE_SQUARE: return "CLOSE_SQUARE";
		case LAMDA: return "LAMDA";
		case SET: return "SET";
		case SEMICOLON: return "SEMICOLON";
		case COMMA: return "COMMA";
		case SUB_ITEM: return "SUB_ITEM";
		case STRING: return "STRING";
		case CHAR: return "CHAR";
		case IDENTIFIER: return "IDENTIFIER";
		default: return "!tokenMissing!";
	}
};

class Token{
	private:

	PossibleTokens type;

	std::string value;

	public:

	PossibleTokens getType() {
		return type;
	};

	std::string getTypeAsStr() {
		return getTokenAsStr(type);
	};

	std::string getValueAsStr() {
		return value;
	};
	
	Token(PossibleTokens tok,std::string v) {
		type=tok;
		value=v;
	};

	std::string getAsCStr() {
		return getTypeAsStr()+"\t"+getValueAsStr();
	};
};

int main(int arg_num,char** arg_value) {
	std::string mainFileLocation;
	mainFileLocation=arg_value[1];
	
	std::ifstream mainFile;
	mainFile.open(mainFileLocation.c_str());

	if (!mainFile) {
		std::cerr <<"Unable to open file \""<<mainFileLocation<<"\"."<<std::endl
			<<"\tMake sure that the filename is correct, and points to a vali"<<
			"d location."<<std::endl;
		return 1;
	}

	std::string buffer,
		untokened="";
	int lineNum=0;//There's a possiblity that this isn't long enough
	while(std::getline(mainFile,buffer)) {
		untokened+=buffer+"\n";
		std::printf("%5d\t%s\n",++lineNum,buffer.c_str());
	}
};
