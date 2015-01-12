/* $Id: in.h,v 1.8 2011/05/02 08:48:58 matuzaki Exp $
 *
 *    Copyright (c) 1997-1998, Makino Takaki
 *
 *    You may distribute this file under the terms of the Artistic License.
 *
 */

#ifndef __in_h
#define __in_h

#include "lconfig.h"
#include "lstring.h"
#include "cell.h"
#include "structur.h"
#include <iostream>

#ifndef IN_BISON_FILE
#include "yacc.h"
#endif
#ifndef IN_LEX_LL
#include "FlexLexer.h"
#endif

namespace lilfes {

class lilfesFlexLexer : public yyFlexLexer
{
	class lilfesFlexLexer *next;

	std::istream *s;
	lstring infile;
	module *mod;		// このファイルを読み始めたときのモジュール
	lstring prefix;
	int proginput;
	YYSTYPE *yylvalp;
	int depth;
	int _line_no;

	int comment_start_line;
	int ref;

	bool ShouldDelete;
	bool skip;
	bool parsing;
	bool firstrun;
	bool progvars;
public:

	lilfesFlexLexer(std::istream *is, const char *ifile, int ipi=0) 
	 : yyFlexLexer(), next(NULL), s(is), infile(ifile), mod(module::CurrentModule()), prefix(""), proginput(ipi), yylvalp(NULL), _line_no(1), ShouldDelete(false), skip(false)
	{ 
		depth = -1;
		switch_streams(s, NULL);
		parsing = false;
		firstrun = true;
		progvars = false;
                  //		ref = 0;
		ref = 1;
	}
	lilfesFlexLexer(lilfesFlexLexer *inext, std::istream *is, bool iShouldDelete, const char *ifile, const char *iprefix="", int ipi=0) 
	 : yyFlexLexer(), next(inext), s(is), infile(ifile), mod(module::CurrentModule()), prefix(iprefix), proginput(ipi), yylvalp(NULL), _line_no(1), ShouldDelete(iShouldDelete), skip(false)
	{ 
		depth = -1;
		switch_streams(s, NULL);
		parsing = false;
		firstrun = true;
		progvars = false;
                //		ref = 0;
		ref = 1;
	}

	~lilfesFlexLexer() { if(ShouldDelete) delete s; }

	int Lineno() { return _line_no; } 

	const char *InFile() { return infile; }
	module *GetModule() { return mod; }
//	void SetModule(module *m) { mod = m; }
	lilfesFlexLexer *Next() { return next; }

	void Skip() { skip = true; }
	bool IsSkip() { return skip; }
	void Interrupt() { if ( ! Isatty() ) { Skip(); if( next ) next->Interrupt(); } }

	int incRef() { return ++ref; }
	int decRef() { return --ref; }

	bool Isatty();
	void Prompt();
	void SetParsing(bool b) { parsing = b; }
	void Flush();

	bool FirstRun() { bool ret = firstrun; firstrun = false; return ret; }
	void SetFirstRun(bool b = true) { firstrun = b;}

	int ProgInput() { if( IsSkip() && next ) return next->ProgInput(); else return proginput; }
	bool ProgVars() { if( IsSkip() && next ) return next->ProgVars(); else return progvars; }
	void SetProgVars(bool b) { progvars = b; }
	int Depth() { if( depth >= 0 ) return depth; else if( next == NULL ) return 1; else return next->Depth()+1; }
	void SetDepth(int i) { depth = i; }

	virtual int yylex();
	void set_yylvalp(YYSTYPE *s) { yylvalp = s; }
	YYSTYPE &get_yylval() { return *yylvalp; }
};

class parsevar
{
	lstring name;
	core_p  cp;
	int count;
public:
	parsevar(char *in, core_p ip = CORE_P_INVALID): name(in) { cp = ip; count = 0; }
	~parsevar() { }
	
	char *GetKey() { return name; } // for HashTable
	char *GetName() { return name; } 
	core_p addr() { return cp; }
	void SetAddr(core_p p) { cp = p; }
	void IncCount() { count++; }
	int GetCount() { return count; }
};

// exported from in.y
//void prompt();

extern FSP prog_types;
extern FSP prog_list;
extern FSP prog_list_top;
extern FSP prog_vars;
extern FSP prog_vars_top;

// exported from main.cpp

// exported from in.l
int pushfile(machine *m, const char *fname, int pi, const char *prefix = "");
//int readfile(const char *fname, int pi, const char *prefix = "");
int readfile(machine *m);
// int command(const char *cmd);
int eval(machine *m, const char *str, bool vars = false);
extern int nargc;
extern int ARGC;
extern char **ARGV;
void SetInteractive(int);
void free_dups(void);
extern int interactive;
extern void prompt(void);
extern void error_open_file(char *);
extern void success_open_file(char *);

extern std::istream *input_stream;
extern std::ostream *output_stream;
extern std::ostream *error_stream;

struct token_key {
	const char *str;
	int token_code;
	const char *GetKey() { return str; }
};

class search_path
{
	search_path *next;
	lstring name;

static search_path *default_search_path;
static search_path minimum_search_path;
static search_path relative_search_path;

public:
		search_path(const char *str, search_path *inext = NULL) : name(str) { next = inext; }

	const char* GetName() const { return name; }
	search_path* Next() { return next; }
	const search_path* Next() const { return next; }
	void SetNext(search_path *s ) { next = s;}

	static search_path * SearchPath() { RelativeSearchPath()->next = DefaultSearchPath(); return RelativeSearchPath()->name[0] == '\0' ? DefaultSearchPath() : RelativeSearchPath(); }
	static search_path * DefaultSearchPath() { return default_search_path; }
	static search_path * MinimumSearchPath() { return &minimum_search_path; }
	static search_path * RelativeSearchPath() { return &relative_search_path; }

	static void SetRelativePath(const char *s) { RelativeSearchPath()->name = s; }
	static void ExtractRelativePath(const char *);
	static void AddSearchPath(const char *);

	static void init();
        static void term();
};

// const char *infile();
// int lineno();
// int proginput();

struct parser_control		// argument of yyparse/yylex
{
public:
	machine *mach;

public:
  explicit parser_control( machine* m ) : mach( m ) {}
  ~parser_control() {}
};


} // namespace lilfes

// int yywrap(void);

#endif

/*
 * $Log: in.h,v $
 * Revision 1.8  2011/05/02 08:48:58  matuzaki
 *
 * windows
 *
 * Revision 1.7  2008/12/28 08:51:12  yusuke
 *
 * support for gcc 4.3
 *
 * Revision 1.6  2008/11/19 09:10:25  hkhana
 * used noyywrap option
 *
 * Revision 1.5  2005/02/11 01:17:46  n165
 * ブランチPIPE_STREAMを統合し、stringstreamの使用、標準出力、標準エラー出力のスイッチができるようになりました。
 *
 * Revision 1.4.4.1  2005/02/09 10:37:25  n165
 * output_streamとerror_streamの書き換えを実装。
 * 標準出力と標準エラー出力についてはポインタを書き換えたので、
 * cout/cerrでハードコードしてある部分はそのままにしてlillib/debugは書き換えた。
 *
 * Revision 1.4  2004/05/30 08:40:26  yusuke
 *
 * memory leak check のプログラムを実装。それにともない、memory leak を減
 * らした。
 *
 * Revision 1.3  2004/04/23 08:59:27  yusuke
 *
 * へっだふぁいるのばしょにいどう。
 *
 * Revision 1.4  2004/04/20 15:58:55  yusuke
 *
 * yacc.yy の save が初期化されていなかったので、メモりが破壊されていた。
 *
 * Revision 1.3  2004/04/08 06:23:29  tsuruoka
 * lexer_t: modified the initialization method of reference counting
 *
 */
