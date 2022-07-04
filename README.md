# CHA: a verification tool supports SVA-like assertions in Chisel

CHA is an assertion language and verification tool for Chisel programs built on top of ChiselTest, where we extend the Chisel assertion language with SystemVerilog assertions (SVA)-like temporal operators. This enables formal verification of Chisel hardware designs against general temporal properties.

## Download

 The latest version of CHA can be found on GitHub: https://github.com/iscas-tis/CHA

## Prerequisites

Before you can use CHA, you need to install other dependencies as below.

1.[SBT (the Scala Build Tool)](http://www.scala-sbt.org)

You need to install sbt, which will automatically fetch the appropriate version of Scala and Chisel based on on your project configuration. Please install sbt according to the instructions from [sbt download](https://www.scala-sbt.org/download.html).

2.[BtorMC]([BTOR2, BtorMC and Boolector 3.0](https://link.springer.com/chapter/10.1007/978-3-319-96145-3_32))

For Linux and Unix-like OS:

```
# Download and build Boolector
git clone https://github.com/boolector/boolector
cd boolector

# Download and build Lingeling
./contrib/setup-lingeling.sh

# Download and build BTOR2Tools
./contrib/setup-btor2tools.sh

# Build Boolector
./configure.sh && cd build && make install
```

For building and usage of Boolector on Windows, please see [COMPILING_WINDOWS.md](https://github.com/Boolector/boolector/blob/master/COMPILING_WINDOWS.md).

3.[Spot](https://spot.lrde.epita.fr/)

The latest release of Spot is version 2.10.6:  [spot-2.10.6.tar.gz](http://www.lrde.epita.fr/dload/spot/spot-2.10.6.tar.gz).

Follow the instructions to install and build.

```
./configure
make
make install
```

You can visit [Spot installation](https://spot.lrde.epita.fr/install.html) for further detail.

4.[gtkwave](https://sourceforge.net/projects/gtkwave/) (optional)

   gtkwave is used to view counterexample written in VCD format if the assertion is violated.
   

## Download CHA, matched FIRRTL and ChiselTest

The stable version is at `testSVA` branch, from the root directory configure and build as follows:

```
# Download
git clone https://github.com/iscas-tis/CHA.git
cd chisel3
git checkout testSVA
git submodule init
git submodule update --remote
```

## Usage

1. Publish local Chiseltest, FIRRTL and Chisel

For local DUT Chisel project, you must publish local version of Chiseltest, FIRRTL and Chisel first.

```
# publish local version of Chiseltest, firrtl, Chisel

cd chiseltest
sbt
    %in sbt 
    publishLocal
    exit
cd ../firrtl
sbt
    publishLocal
    exit
cd ../
sbt
    publishLocal
    exit
```

2. Modify dependency of local DUT Chisel project

```
update libraryDependencies in build.sbt as
"edu.berkeley.cs" %% "chisel3" % "3.7-SNAPSHOT",
"edu.berkeley.cs" %% "chiseltest" % "0.7-SNAPSHOT" % "test",
"edu.berkeley.cs" %% "firrtl" % "1.6-SNAPSHOT",
```

3. Add necessary lib

   You need to add [javabdd](http://javabdd.sourceforge.net/) and  [jhoafparser](https://automata.tools/hoa/jhoafparser/) as external dependency in lib folder.

4. Run a test

   ```
   >sbt
   testOnly Yourtestclassname
   ```
5. Result
 CHA will show the property passes or provide a counterexample. If you have installed gtkwave, you could view VCD waveform witness in `test_run_dir` .
   

## An example

Here is an example test of GCD. For a 4-bit GCD project, we can verify that the program must end within 16 cycles. 

1. DUT project:

   ```scala
   class DecoupledGcd(val bitWidth: Int) extends Module {
     // module body
   }
   ```

2. Add the assertion:

   ```scala
   class DecoupledGcdProp1(width: Int) extends DecoupledGcd(width: Int){
     svaSeqAnno.makeSVAAnno(this.reset, ap(busy) |->  ###(1,15) ap(!busy))
   }
   ```

3. Create a test class:

   ```scala
   class DecoupledGcdSpec extends AnyFlatSpec with ChiselScalatestTester with Formal {
     behavior of "DecoupledGcd"
     it should "pass" in {
       verify(new DecoupledGcdProp1(4), Seq(BoundedCheck(150), BtormcEngineAnnotation))
     }
   }
   ```

4. Run a test

   ```
   >sbt
   testOnly DecoupledGcdSpec
   ```

5. When test case completed, you can find test results in `test_run_dir`.

   ![image-20220704102342059](https://tva1.sinaimg.cn/large/e6c9d24ely1h3uollfw8wj217o0420uw.jpg)

   

## The CHA Format

#### Sequence Format 
Note that "|-" and "-|" denotes the left parenthesis and right parenthesis.

```
s := ap(u) | s ###0(s) | s ###1(s)| s*(0)| s*(1.-1) | |-s-|
ap ::= boolean sequence
###(0) ::= sequence fusion
###(1) ::= sequence concatenation
*(0) ::= zero repetition
*(1,-1) ::= intervals
```

For example:

```
ap(busy) |->  ###(1,15) ap(!busy)
```

#### Property Format

```
p := s | s|->p | !p | Gp | Fp | Xp | p U p | p||p | p&&p | |-p-|
```

#### Sequence Operators                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             

| Name | boolean sequence | sequence fusion | sequence concatenation | sequence disjunction | zero repetition | intervals |
| :---: | :--------------: | :-------------: | :--------------------: | :------------------: | :-------------: | :-------: |
|  SVA  |        u         |       ##0       |          ##1           |          or          |      [*0]       |  [*1:$]   |
|  CHA  |      ap(u)       |     ###(0)      |         ###(1)         |          \|          |      *(0)       |  *(1:$)   |

#### Property Operators

| Name. | suffix implication | property negation | property conjunction | property disjunction | nexttime property | always property | s_eventually property | until property |
| :---: | :----------------: | :---------------: | :------------------: | :------------------: | :---------------: | :-------------: | :-------------------: | :------------: |
|  SVA  |        \|->        |        not        |         and          |          or          |     nexttime      |     always      |     s_eventually      |     until      |
|  CHA  |        \|->        |         !         |          &&          |         \|\|         |         X         |        G        |           F           |       U        |
