( Print functions )
: dprint <<# #s #> TYPE #>> ;
: print 0 dprint

( Exercise 1 )
CR ." Exercise 1"
CR ." Hello World"

( Exercise 2 )
CR ." Exercise 2"
CR 10 7 3 5 * 12 / - + .s

( Exercise 3 )
CR ." Exercise 3"
CR 10e0 7e0 3e0 5e0 f* 12e0 f/ f- f+ f.

( Exercise 4 )
CR ." Exercise 4"
CR 10e0 7e0 3e0 5e0 f* 12e0 f/ f- f+ f.

( Exercise 5 )
CR ." Exercise 5"
CR 10 7e0 3e0 5 s>d d>f f* 12 s>d d>f f/ f- s>d d>f f+ f.

( Exercise 6 )
CR ." Exercise 6"
variable y 
10 y !
fvariable x 
7e0 x f!
CR y @ x f@ 3e0 5 s>d d>f f* 12 s>d d>f f/ f- s>d d>f f+ f.

( Exercise 7 )
CR ." Exercise 7"
: Ex7 CR 5 3 < if 7 else 2 endif .
Ex7

( Exercise 8 )
CR ." Exercise 8"
: Ex8 CR 5 3 > if 7 else 2 endif .
Ex8

( Exercise 9 )
CR ." Exercise 9"
: Ex9 CR 5 0 do i . loop
Ex9

( Exercise 10 ) 
CR ." Exercise 10"
: convertint { a } \ convert int to double
	a 0 ;

: TEST_10 { a } \ Test convertint using a as the int
	CR ."     TEST: convertint( " a print ." ): "
	a convertint 
	0<> swap dup <> and if ." Failure: expected" a print ." .0 got " d. else ." Success" endif ;

10 TEST_10
4 TEST_10

( Exercise 11 )
CR ." Exercise 11"
: fact { a }
	a 0<= if 1 else a a 1 - recurse * endif ;

: TEST_FACT { a b } ( a = input, b = excepted output )
	CR ."     TEST: fact( " a print ." ): "
	a fact
	b = if ." Failure: expected " b . ." got " a fact . else ." Success" endif ;

\ 1 1 TEST_FACT
\ 2 2 TEST_FACT
\ 3 6 TEST_FACT
\ 4 24 TEST_FACT
\ 5 120 TEST_FACT

( Exercise 12 )
CR ." Exercise 12"
: fib { a }
	a 0= if 0 else a 1 = if 1 else a 1 - recurse a 2 - recurse + endif endif ;

: TEST_FIB { a b } ( a = input, b = excepted output )
	CR ."     TEST: fact( " a print ." ): "
	a fib
	b = if ." Failure: expected " b . ." got " a fib . else ." Success" endif ;


\ 0 0 TEST_FIB
\ 1 1 TEST_FIB
\ 2 1 TEST_FIB
\ 3 2 TEST_FIB
\ 4 3 TEST_FIB
\ 5 5 TEST_FIB
\ 6 8 TEST_FIB
\ 7 13 TEST_FIB
\ 8 21 TEST_FIB
\ 9 34 TEST_FIB
\ 10 55 TEST_FIB

bye


	