# CHA: a verification tool supports SVA-like assertions in Chisel

CHA makes it possible to describe SVA-like assertions in Chisel, and provides a verification tool for Chisel programs.

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

## Build

The stable version is at `testSVA` branch, from the  root directory configure and build as follows:

```
# Download
git clone https://github.com/iscas-tis/CHA.git

# Build
cd chisel3
git checkout testSVA
git submodule init
git submodule update --remote
sbt compile
```

## Usage

You can write your own assertions using `makeSVAAnno` function in  [svaSeq](chiseltest/src/main/scala/chiseltest/formal/svaAnno.scala). Please add the necessary imports as below.

```scala
import chisel3._
import chiseltest._
import chiseltest.formal.svaSeq._
```

Assuming a Chisel project `MyModule` defined in `src/main/scala/MyModule.scala`:

```scala
class MyModule extend Module {
    val io = IO(new Bundle {
        val in = Input(UInt(16.W))
        val out = Output(UInt(16.W))
    })

    // module class body here
  
    // assertion
    // svaSeqAnno.makeSVAAnno(******)
}
```

Write a test class in `src/test/scala/` , for example:

```scala
class Mytest extends AnyFlatSpec with ChiselScalatestTester with Formal {
  behavior of "MyModule"
  // test class body here
}
```

After install [gtkwave](https://sourceforge.net/projects/gtkwave/) , you can find VCD waveform witness in `test_run_dir` .

![GCDWitness](https://tva1.sinaimg.cn/large/e6c9d24ely1h3iaa6kca2j217o042di0.jpg)

 

## The CHA Format

#### Sequence Format

```
s := ap(u) | s ###0(s) | s ###1(s)| s*(0)| s*(1.-1) | |-s-|
```

#### Property Format

```
p := s | s|->p | !p | Gp | Fp | Xp | p U p | p||p | p&&p | |-p-|
```

#### Sequence Operators                                                                                                                                                 
| Operator | Description | Signature  |
| :------: | :---------: | :--------: |
|   ###    | time delay  | s ###(1) s |
|   \|->   | Implication |  s \|-> p  |
|   *(n)   |   repeat    |   s *(1)   |

