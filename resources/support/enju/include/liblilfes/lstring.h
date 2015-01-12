/* $Id: lstring.h,v 1.4 2011/05/02 08:48:59 matuzaki Exp $
 *
 *    Copyright (c) 1997-1998, Makino Takaki
 *
 *    You may distribute this file under the terms of the Artistic License.
 *
 */

///  <ID>$Id: lstring.h,v 1.4 2011/05/02 08:48:59 matuzaki Exp $</ID>
//
///  <COLLECTION> lilfes-bytecode </COLLECTION>
//
///  <name>lstring.h</name>
//
///  <overview>
///  <jpn>文字列操作ルーチン</jpn>
///  <eng>string operation routine </eng>
///  </overview>

/// <notiondef>
/// <name> lstring_overview </name>
/// <title> 文字列操作ルーチン </title>
/// 
/// <desc>
/// 
/// <p> <ref>lstring.h</ref>はstring クラスの自作版です。文字列操作に関するさまざまなメソッドが用意されています。lstringとはlight stringの略で、軽い動作を目指して作られています。<br>
/// </p>
/// </desc>
/// <see> </see>
/// </notiondef>

#ifndef __lstring_h
#define __lstring_h

#include "lconfig.h"

#include <string.h>
#include <iostream>

namespace lilfes {

/////////////////////////////////////////////////////////////////////////
/// <classdef>
/// <name> lstring </name>

/// <overview>文字列 クラス</overview>
/// <desc>
/// <p>
/// 文字列クラスです。文字列操作に関するメソッドが用意されています。
/// </p>
/// </desc>
/// <see></see>
/// <body>
class lstring
{
	char *str;
	
public:
	lstring() { str = NULL; }
	lstring(const char *s);
	lstring(const lstring &s);
	explicit lstring(char c, int repeat = 1);
	~lstring();

	bool IsNull() const { return str == NULL; }
  /// 中身がNULLかどうかを調べます。
	operator char *() const { return str; }
  /// Ｃ形式の文字列に変換します。
  
	lstring &operator =(const lstring &l);
	lstring &operator =(const char *s);
  /// 文字列を代入します。文字列がコピーされます。
	char &operator[](int i) { return str[i]; }
  /// i番目の文字を取得します。
	friend lstring operator+(const lstring &a, const lstring &b);
	friend lstring operator+(const char *a, const lstring &b);
	friend lstring operator+(const lstring &a, const char *b);
  /// 文字列を結合します。
  
	char *GetKey() { return str; } // for use in HashTable
  /// 文字列を返します。HashTableで使うために用意されています。
  
#ifndef NO_IOSTREAM
	friend std::ostream& operator<<(std::ostream& os, const lstring &l) { return os << (char *)(l.operator char *()); }
  /// 文字列を出力します。
#endif
};
/// </body></classdef>


// gokan overload function
/////////////////////////////////////////////////////////////////////////
/// <funcdef>
/// <name> strlen </name>
/// <overview>文字列長取得</overview>
/// <desc>
/// <p>
/// 文字列の長さを取得します。
/// </p>
/// </desc>
/// <args> 文字列を渡します。</args>
/// <retval> 文字列の長さを返します。 </retval>
/// <remark></remark>
/// <see></see>
/// <body>
inline int strlen(const lstring &a) 
{ return ::strlen(a.operator char *()); }
/// </body></funcdef>

/////////////////////////////////////////////////////////////////////////
/// <funcdef>
/// <name> strcpy </name>
/// <overview>文字列コピー</overview>
/// <desc>
/// <p>
/// <var>a</var>の中身を<var>s</var>にコピーします。
/// </p>
/// </desc>
/// <args> コピー元とコピー先の文字列を渡します。</args>
/// <retval> コピー先の文字列を返します。 </retval>
/// <remark></remark>
/// <see></see>
/// <body>
inline char *strcpy(char *s, const lstring &a) 
{ return ::strcpy(s, a.operator char *()); }
/// </body></funcdef>

/////////////////////////////////////////////////////////////////////////
/// <funcdef>
/// <name> strcmp </name>
/// <overview>文字列比較</overview>
/// <desc>
/// <p>
/// 文字列のを比較します。
/// </p>
/// </desc>
/// <args> 比較する文字列を渡します。</args>
/// <retval> 文字列が同じなら０を、a&lt;bなら-1を、a&gt;bなら1を返します。 </retval>
/// <remark></remark>
/// <see></see>
/// <body>
inline int strcmp(const lstring &a, const lstring &b)
{ return ::strcmp(a.operator char *(), b.operator char *()); }
/// </body></funcdef>

/////////////////////////////////////////////////////////////////////////
/// <funcdef>
/// <name> strcmp </name>
/// <overview>文字列比較</overview>
/// <desc>
/// <p>
/// 文字列を比較します。
/// </p>
/// </desc>
/// <args> 比較する文字列を渡します。</args>
/// <retval> 文字列が同じなら０を、a&lt;bなら-1を、a&gt;bなら1を返します。 </retval>
/// <remark></remark>
/// <see></see>
/// <body>
inline int strcmp(const char *a, const lstring &b) 
{ return ::strcmp(a, b.operator char *()); }
/// </body></funcdef>

/////////////////////////////////////////////////////////////////////////
/// <funcdef>
/// <name> strcmp </name>
/// <overview>文字列比較</overview>
/// <desc>
/// <p>
/// 文字列を比較します。
/// </p>
/// </desc>
/// <args> 比較する文字列を渡します。</args>
/// <retval>  文字列が同じなら０を、a&lt;bなら-1を、a&gt;bなら1を返します。 </retval>
/// <remark></remark>
/// <see></see>
/// <body>
inline int strcmp(const lstring &a, const char *b) 
{ return ::strcmp(a.operator char *(), b); }
/// </body></funcdef>

inline int strlen(const char *a) { return ::strlen(a); }
inline int strcmp(const char *a, const char *b) { return ::strcmp(a,b); }
inline char * strcpy(char *a, const char *b) { return ::strcpy((char *)a,b); }

/////////////////////////////////////////////////////////////////////////
/// <funcdef>
/// <name> int2lstr </name>
/// <overview>整数を文字列に変換</overview>
/// <desc>
/// <p>
/// 整数値をlstringオブジェクトに変換します。
/// </p>
/// </desc>
/// <args> int値を渡します。</args>
/// <retval> lstringオブジェクトを返します。 </retval>
/// <remark></remark>
/// <see></see>
/// <body>
lstring int2lstr(int i);
/// </body></funcdef>

} // namespace lilfes

#endif // __lstring_h
