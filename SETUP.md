# Chisel Local Setup
Instructions for setting up your environment to run Chisel locally.

For a minimal setup, you only need to install [SBT (the Scala Build Tool)](http://www.scala-sbt.org), which will automatically fetch the appropriate version of Scala and Chisel based on on your project configuration.

[Verilator](https://www.veripool.org/wiki/verilator) is installation is required to simulate your Verilog designs.

## Ubuntu Linux	

1.  Install Java	
    ```	
    sudo apt-get install default-jdk	
    ```
   	
1.  Install sbt according to the instructions from [sbt download](https://www.scala-sbt.org/download.html).
    
1.  Install Verilator.	
    We currently recommend Verilator version 4.016.	
    Follow these instructions to compile it from source.	

    1.  Install prerequisites (if not installed already):	
        ```	
        sudo apt-get install git make autoconf g++ flex bison	
        ```

    2.  Clone the Verilator repository:	
        ```	
        git clone http://git.veripool.org/git/verilator	
        ```

    3.  In the Verilator repository directory, check out a known good version:	
        ```	
        git pull	
        git checkout v4.016	
        ```

    4.  In the Verilator repository directory, build and install:	
        ```	
        unset VERILATOR_ROOT # For bash, unsetenv for csh	
        autoconf # Create ./configure script	
        ./configure	
        make	
        sudo make install	
        ```

## Arch Linux
1.  Install Verilator and SBT
    ```
    yaourt -S sbt verilator
    ```
 
## Windows
1.  [Download and install sbt for Windows](https://www.scala-sbt.org/download.html).

Verilator does not appear to have native Windows support.
However, Verilator works in [WSL](https://docs.microsoft.com/en-us/windows/wsl/install-win10) or in other Linux-compatible environments like Cygwin.

There are no issues with generating Verilog from Chisel, which can be pushed to FPGA or ASIC tools.

## Mac OS X
1.  Install Verilator and SBT
    ```
    brew install sbt verilator
    ```
