/* $Id: hash.h,v 1.5 2011/05/02 08:48:58 matuzaki Exp $
 *
 *    Copyright (c) 1997-1998, Makino Takaki
 *
 *    You may distribute this file under the terms of the Artistic License.
 *
 */

//
///  <ID>$Id: hash.h,v 1.5 2011/05/02 08:48:58 matuzaki Exp $</ID>
//
///  <COLLECTION> lilfes-bytecode </COLLECTION>
//
///  <name>hash.h</name>
//
///  <overview>
///  <jpn>ハッシュ操作</jpn>
///  <eng>Hash routines </eng>
///  </overview>

// Usage:
//
// class HashTable<T> : provide hash table of class T's pointer
// class T must have following functions:
//  char *GetKey(void)
//
// T *HashTable<T>::Add(T *)
//      Adds datum to the hash table. Return NULL if successful.
// T *HashTable<T>::Find(char *)
//      Finds datum on the hash table. return NULL if the data is not found.
// unsigned GetNData()
//      Returns how many data are on the table.
// unsigned GetTableSize()
//      Returns the size of the hash table.


/// <notiondef>
///
/// <name> hash_overview </name>
/// <title> ハッシュ操作ルーチン </title>
/// 
/// <desc>
/// 
/// <p> <ref>hash.h</ref> には、ハッシュを操作するためのライブラリが入っています。</p>
/// 
/// 
///
/// </desc>
/// <see>  </see>
/// </notiondef>
//////////////////////////////////////////////////////////////////////////////
#ifndef __hash_h
#define __hash_h

#include "lconfig.h"
#include "errors.h"
#include <iostream>

namespace lilfes {

class FSP;

#ifdef PROFILE
#if PROFILE >= 3
#define PROFILE_HASH
#endif
#endif



//////////////////////////////////////////////////////////////////////////////
//
// class Pair
//
/// <classdef>
/// <name> Pair</name>

/// <overview>キーと値を一組にしたクラス</overview>
/// <desc>
/// <p>
/// キーと値の組を作ります
/// </p>
/// </desc>
/// <see></see>

/// <body>

template <class T1, class T2>
struct Pair
{
	T1 a;
	T2 b;

	Pair() { }
	Pair(const T1 & ia, const T2 & ib) : a(ia), b(ib) { }


};
/// </body></classdef>


//////////////////////////////////////////////////////////////////////////////
//
// class HashTable
//

#define INITIALHASHSIZE 1024
	// Initial hash size must be value of 2^n form */

#define INTTABLEUSAGEMAXDEF   3
	// Expand hash table if (TABLEUSAGEMAX/16*100)% of the table is used
#define STRTABLEUSAGEMAXDEF   4
	// Expand hash table if (TABLEUSAGEMAX/16*100)% of the table is used
#define FSPTABLEUSAGEMAXDEF   4
	// Expand hash table if (TABLEUSAGEMAX/16*100)% of the table is used


/////////////////////////////////////////////////////////////////////////

/// <classdef>
/// <name> IntegerHash </name>

/// <overview>キーがInteger型であるハッシュを操作するクラス</overview>
/// <desc>
/// <p>
/// 
/// 
/// </p>
/// </desc>
/// <see></see>
/// <body>

template <class Tobj>
class IntegerHash
{
	Tobj **table;
	unsigned tablesize;
  /// ハッシュテーブルのサイズ
	unsigned ndata;
  /// 格納されているデータ数（キーと値のペア数）
	unsigned usagemax;
  /// ハッシュテーブルの使用上限
#ifdef PROFILE_HASH
	const char *name;
	int add, col;
#endif

public:
		IntegerHash(const char *iname = "", unsigned inisize = INITIALHASHSIZE, unsigned max = INTTABLEUSAGEMAXDEF);
  /// ハッシュを作成します。<br><var>iname</var>:ハッシュの名前。<br>
  /// <var>inisize</var>:テーブルの初期サイズ<br>
  /// <var>max</var>:テーブルの使用限度。(max/16)*100% 
		IntegerHash(IntegerHash<Tobj> &);
		~IntegerHash();

	unsigned GetNData()     const { return ndata; }
  /// ハッシュテーブルに存在するデータ数（キーと値のペア数）を取得します
  
	unsigned GetTableSize() const { return tablesize; }
  /// ハッシュテーブルの大きさを取得します
	Tobj *Add(Tobj *data);
  /// ハッシュにデータを追加します。既に同じキーをもつものが存在すれば追加されません。
  /// <br>返り値
  /// <br>同じキーをもつものが存在していた場合 -> そのオブジェクトへのポインタ
  /// <br>同じキーをもつものが存在しなかった場合 -> NULL
	Tobj *AddOrReplace(Tobj *data);
  /// ハッシュにデータを追加します。既に同じキーをもつものが存在すれば置き換えます。
  /// <br>返り値
  /// <br>同じキーをもつものが存在していた場合 -> そのオブジェクトへのポインタ
  /// <br>同じキーをもつものが存在しなかった場合 -> NULL

        Tobj *Find(ulong Key) const;
  /// 与えられたキーに対応するデータを取得します。消去が成功すれば消去したオブジェクトへのポインタを、消去すべきデータが見つからなかった場合NULLを返します。
	Tobj *Delete(ulong Key);
  /// ハッシュからデータを消去します。引数にはキーを渡します。消去が成功すれば消去したオブジェクトへのポインタを、消去すべきデータが見つからなかった場合NULLを返します。
	Tobj *Delete(Tobj *data);
  /// ハッシュからデータを消去します。引数にはデータへのポインタを渡します。
  
//	unsigned GetIndex(Tobj *data);

	Tobj *Pick(int index) const { return table[index]; }
  /// ハッシュテーブルからデータを取得します。キーではなくテーブルのインデックスを引数に取ります
	void ClearTable() { 
		for( uint i=0; i<GetTableSize(); i++ )
		{
			delete Pick(i);
		}
	ndata = 0; memset(table, 0, tablesize*sizeof(Tobj *)); }
  /// ハッシュに格納されているデータを全て消去します

protected:
	static int hash(ulong key);
  /// ハッシュ関数です。<var>key</var>をもとに整数を生成します
	void ExpandTable();
  /// テーブルサイズを拡張します。サイズが2倍になります。
};
/// </body></classdef>




#define HashTable StringHash /* obsolete form */
/////////////////////////////////////////////////////////////////////////

/// <classdef>
/// <name> StringHash </name>

/// <overview>キーがString型であるハッシュを操作するクラス</overview>
/// <desc>
/// <p>
/// 
/// 
/// </p>
/// </desc>
/// <see></see>

/// <body>
template <class Tobj> 
class StringHash
{
	Tobj **table;
	unsigned tablesize;
	unsigned ndata;
	unsigned usagemax;
#ifdef PROFILE_HASH
	const char *name;
	int add, col;
#endif

public:
		StringHash(const char *iname = "", unsigned inisize = INITIALHASHSIZE, unsigned max = STRTABLEUSAGEMAXDEF);
		StringHash(StringHash<Tobj> &);
		~StringHash();

	unsigned GetNData()     const { return ndata; }
	unsigned GetTableSize() const { return tablesize; }

	Tobj *Add(Tobj *data);
	Tobj *AddOrReplace(Tobj *data);
	Tobj *Find(const char *Key) const;
	Tobj *Delete(const char *Key);
	Tobj *Delete(Tobj *data);

//	unsigned GetIndex(Tobj *data);

	Tobj *Pick(int index) const { return table[index]; }

	void ClearTable() { 
		for( uint i=0; i<GetTableSize(); i++ )
		{
			delete Pick(i);
		}
	ndata = 0; memset(table, 0, tablesize*sizeof(Tobj *)); }

protected:
	static int hash(const char *key);
	void ExpandTable();
};
/// </body></classdef>





/////////////////////////////////////////////////////////////////////////

/// <classdef>
/// <name> HSPHash </name>

/// <overview>キーがHSP型であるハッシュを操作するクラス</overview>
/// <desc>
/// <p>
/// 
/// 
/// </p>
/// </desc>
/// <see></see>

/// <body>
template <class Tobj> 
class FSPHash
{
	Tobj **table;
	unsigned tablesize;
	unsigned ndata;
	unsigned usagemax;
#ifdef PROFILE_HASH
	const char *name;
	int add, col;
#endif

public:
		FSPHash(const char *iname, unsigned inisize = INITIALHASHSIZE, unsigned max = FSPTABLEUSAGEMAXDEF);
		FSPHash(FSPHash<Tobj> &);
		~FSPHash();

	unsigned GetNData()     const { return ndata; }
	unsigned GetTableSize() const { return tablesize; }

	Tobj *Add(Tobj *data);
	Tobj *Find(FSP Key) const;

//	unsigned GetIndex(Tobj *data);

	Tobj *Pick(int index) const { return table[index]; }

	void ClearTable() { 
		for( uint i=0; i<GetTableSize(); i++ )
		{
			delete Pick(i);
		}
	ndata = 0; memset(table, 0, tablesize*sizeof(Tobj *)); }

protected:
	static int hash(FSP key);
	void ExpandTable();
};
/// </body></classdef>


//////////////////////////////////////////////////////////////////////////////
//
// class VarArray
//
/// <classdef>
/// <name> VarArray </name>

/// <overview>可変長配列</overview>
/// <desc>
/// <p>
/// 要素数が可変の配列です。領域が足りなくなったら自動的に確保します。その際、ある一定の量を一度に確保します。
/// 一定の量というのは２のべき乗（何乗かは配列毎に一定）の整数倍です。
/// </p>
/// </desc>
/// <see></see>

/// <body>
#define VARINDEX_GROWSTEP	128

template <class T>
class VarArray
{
	T **arrs;
	T *nextp;
	uint size;
	static uint growstep;		// 1) Must be declared for each type
								// 2) Must be 2^n form (2, 4, 8, 16 ... )
	static uint growbits;		// 3) Must be 2^growbits = growstep = growmask+1
	static uint growmask;

public:
	VarArray();
  /// VarArray型のオブジェクトを作成します。要素数は0です。
	VarArray(const VarArray<T> &);
  
	~VarArray();

	uint GetSize() const { return size; }
  /// 現在の配列の要素数を取得します。
	T &operator[](uint i) { ASSERT( i < size ); return arrs[i>>growbits][i&growmask]; }
	const T &operator[](uint i) const { ASSERT( i < size ); return arrs[i>>growbits][i&growmask]; }
	T &Next() { if( (size & (growstep-1)) == 0 ) Resize(size); size++; return *(nextp++); }
	void Push(const T &c) { Next() = c; }
  /// 要素を追加します。
	T &Pop() { T &ret = operator[](size-1); size--; return ret; }
  /// 要素を取り出します。
	uint GetLimit(uint i) const { return (i|growmask)+1; }
  /// i番目の要素を使用する時に、確保されているべき要素数を返します。（例：32個ずつ要素が確保されるときに、１００番目の要素をつかうには、128の要素数が確保されていないといけない。上の説明を参照）
	void Resize(uint i);
};
/// </body></classdef>


template <class T>
VarArray<T>::VarArray() { arrs = NULL; nextp = NULL; size = 0; }


template <class T>
VarArray<T>::VarArray(const VarArray<T> &v) 
{ 
	if( v.arrs == NULL ) 
	{ 
		arrs = NULL; 
		nextp = NULL; 
		size = 0; 
	} 
	else {
		size = v.size;
#if 0
	std::cout << "VarArray copy constructor" << std::endl;
	std::cout << "growmask = " << growmask << std::endl;
	std::cout << "size = " << size << std::endl;
	std::cout << "((size+growmask)>>growbits) = " << ((size+growmask)>>growbits) << std::endl;
	std::cout << "(((size+growmask)>>growbits)+VARINDEX_GROWSTEP-1)&~(VARINDEX_GROWSTEP-1) = " << ((((size+growmask)>>growbits)+VARINDEX_GROWSTEP-1)&~(VARINDEX_GROWSTEP-1)) << std::endl;
#endif
		uint arrssize = (((size+growmask)>>growbits)+VARINDEX_GROWSTEP-1)&~(VARINDEX_GROWSTEP-1);
		arrs = new T *[arrssize];
		for( uint i=0; i<((size+growmask)>>growbits); i++ )
		{
			arrs[i] = new T[growstep];
			memcpy(arrs[i], v.arrs[i], sizeof(T)*growstep);
		}
		nextp = arrs[((size+growmask)>>growbits)-1] + (size&growmask);
	}
}

template <class T>
VarArray<T>::~VarArray() 
{ 
	ASSERT(size >= 0);
//	std::cout << "size = " << size << "  ss = " << ((size+growmask)>>growbits) << "growmask = " << growmask << std::endl;
	for( uint j=0; j<((size+growmask)>>growbits); j++) 
		delete[] arrs[j]; 
	delete[] arrs; 
}

template <class T>
void VarArray<T>::Resize(uint i) 
{ 
	uint ii = i >> growbits;
	if( (ii & (VARINDEX_GROWSTEP - 1)) == 0 )
	{
		T **newarrs = new T *[ii+VARINDEX_GROWSTEP];
		if( arrs != NULL )
		{
			memcpy(newarrs, arrs, (size >> growbits) * sizeof(T *));
			delete[] arrs;
		}
		arrs = newarrs;
	}
	arrs[ii] = nextp = new T[growstep];
}

//////////////////////////////////////////////////////////////////////////////
//
// class VarArrayIterator
//
/// <classdef>
/// <name> VarArrayIterator </name>

/// <overview></overview>
/// <desc>
/// <p>
/// 
/// 
/// </p>
/// </desc>
/// <see></see>

/// <body>
template <class T>
class VarArrayIterator
{
	VarArray<T> &arr;
	T *p;
	T *limit;
	int limitptr;
public:
	VarArrayIterator(VarArray<T> &iarr) : arr(iarr) { Jump(0); }
	VarArrayIterator(VarArray<T> &iarr, int idx) : arr(iarr) { Jump(idx); }
	~VarArrayIterator() { }

	operator T *() { return p; }
	T *operator ->() { return p; }
	T *operator++()    { if( ++p >= limit ) Jump(limitptr); return p; }
	T *operator++(int) { T *ret = p; if( ++p >= limit ) Jump(limitptr); return ret; }
	VarArrayIterator<T>& operator+=(int i) { p+=i; if( p >= limit ) Jump(limitptr + (p-limit)); return *this; }
	T &operator[](int i) { if( p+i < limit ) return p[i]; else return arr[limitptr + ((p+i)-limit)]; }

	void Jump(int addr);
	int GetIndex() { return limitptr - (limit-p); }
};
/// </body></classdef>


template <class T>
void VarArrayIterator<T>::Jump(int addr)
{
	p = &(arr[addr]);
	limitptr = arr.GetLimit(addr);
	limit = p + (limitptr - addr);
}


//////////////////////////////////////////////////////////////////////////////
//
// class Queue
/// <classdef>
/// <name> Queue </name>

/// <overview>キュー（待ち行列）を作成します</overview>
/// <desc>
/// <p>
/// 
/// 
/// </p>
/// </desc>
/// <see></see>

/// <body>
#define QUEUE_INISIZE	1024
template <class T>
class Queue
{
	T *list;
	int read, write, size;
	static T invalid;
	
public:
	Queue(int isize = QUEUE_INISIZE);
  /// サイズが<var>isize</var>であるキューを作成します
	~Queue();

	void Enqueue(T data);
  /// キューにデータを追加します
	T Dequeue();
  /// キューからデータを取り出します
	void FlushAll();
  /// キューにたまっているデータを消去します
	bool IsEmpty() { return read==write; }
  /// キューが空かどうか調べます　空ならtrueを、そうでないならfalseを返します。
};
/// </body></classdef>



//////////////////////////////////////////////////////////////////////////////
//
// class Bitmap
//
/// <classdef>
/// <name> Bitmap </name>

/// <overview></overview>
/// <desc>
/// <p>
/// 
/// 
/// </p>
/// </desc>
/// <see></see>

/// <body>
class Bitmap
{
	unsigned size;
	int32 *ar;
	
public:
			 Bitmap(unsigned is);
			 Bitmap(const Bitmap &p);
			~Bitmap() { delete[] ar; }
	Bitmap & operator =(const Bitmap &);


	void Set(unsigned b)   { ASSERT( size > b ); ar[b/32] |=   (int32)1<<(b%32); }
	void Reset(unsigned b) { ASSERT( size > b ); ar[b/32] &= ~((int32)1<<(b%32)); }
	bool Test(unsigned b) const { ASSERT( size > b ); return ar[b/32] & ((int32)1<<(b%32)) ? true : false; }
	void Merge(const Bitmap &b);

	bool IsAllZero();

	bool Ident(const Bitmap &b);
  /// 引数に指定されたBitmap オブジェクト <var>b</var> の中身が自分と同じか調べます
  /// <br>同じなら<var>true</var>をそうでなければ<var>false</var>を返します

	void Output(std::ostream &o);
};
/// </body></classdef>


//////////////////////////////////////////////////////////////////////////////
//
// class Int
//
/// <classdef>
/// <name> Int </name>

/// <overview></overview>
/// <desc>
/// <p>
/// 整数型のクラスです。
/// 
/// </p>
/// </desc>
/// <see></see>

/// <body>
class Int
{
	int i;

public:
	Int() { }
	Int(int ii) : i(ii) { }
  /// オブジェクトを作り、整数<var>ii</var>を格納します。
	operator int &() { return i;}
	operator const int &() const { return i;}
	int GetKey() { return i;}
  /// 自分自身の整数値を返します
};
/// </body></classdef>

} // namespace lilfes

#endif // __hash_h
