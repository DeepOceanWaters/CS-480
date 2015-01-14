( Print functions )
: dprint <<# #s #> TYPE #>> ;
: print 0 dprint ;

( Exercise 1 )
CR ." Ex  1: "
." Hello World"

( Exercise 2 )
CR ." Ex  2: "
10 7 3 5 * 12 / - + .

( Exercise 3 )
CR ." Ex  3: "
10e0 7e0 3e0 5e0 f* 12e0 f/ f- f+ f.

( Exercise 4 )
CR ." Ex  4: "
10e0 7e0 3e0 5e0 f* 12e0 f/ f- f+ f.

( Exercise 5 )
CR ." Ex  5: "
10 7e0 3e0 5 s>d d>f f* 12 s>d d>f f/ f- s>d d>f f+ f.

( Exercise 6 )
CR ." Ex  6: "
variable y 10 y !
fvariable x 7e0 x f!
y @ x f@ 3e0 5 s>d d>f f* 12 s>d d>f f/ f- s>d d>f f+ f.

( Exercise 7 )
CR ." Ex  7: " 
: Ex7 5 3 < if 7 else 2 endif . ; Ex7

( Exercise 8 )
CR ." Ex  8: "
: Ex8 5 3 > if 7 else 2 endif . ; Ex8

( Exercise 9 )
CR ." Ex  9: "
: Ex9 5 0 do i . loop ; Ex9

( Exercise 10 ) 
CR ." Ex 10: "
: convertint { a } \ convert int to double
	a 0 ;

: TEST_10 { a } \ Test convertint using a as the int
	CR ."     TEST: convertint( " a print ." ): "
	a convertint 
	0<> swap dup <> and if ." Failure: expected" a print ." .0 got " d. else ." Success" endif ;

10 TEST_10
4 TEST_10

( Exercise 11 )
CR ." Ex 11: "
: fact { a }
	a 0<= if 1 else a a 1 - recurse * endif ;

: TEST_FACT { a b } ( a = input, b = excepted output )
	CR ."     TEST: fact( " a print ." ): "
	a fact
	b = if ." Failure: expected " b . ." got " a fact . else ." Success" endif ;

1 1 TEST_FACT
2 2 TEST_FACT
3 6 TEST_FACT
4 24 TEST_FACT
5 120 TEST_FACT

( Exercise 12 )
CR ." Ex 12: "
: fib { a }
	a 0= if 0 else a 1 = if 1 else a 1 - recurse a 2 - recurse + endif endif ;

: TEST_FIB { a b } ( a = input, b = excepted output )
	CR ."     TEST: fact( " a print ." ): "
	a fib
	b = if ." Failure: expected " b . ." got " a fib . else ." Success" endif ;

0 0 TEST_FIB
1 1 TEST_FIB
2 1 TEST_FIB
3 2 TEST_FIB
4 3 TEST_FIB
5 5 TEST_FIB
6 8 TEST_FIB
7 13 TEST_FIB
8 21 TEST_FIB
9 34 TEST_FIB
10 55 TEST_FIB

bye


	